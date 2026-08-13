# oop/CircuitBreaker.java

서킷 브레이커 인터페이스 + Builder 기반 OOP 구현.

```java
package com.datastructure.circuitbreaker.oop;

import java.util.function.Function;
import java.util.function.Supplier;

public interface CircuitBreaker {
    enum State { CLOSED, OPEN, HALF_OPEN }

    <T> T execute(Supplier<T> supplier);
    <T> T executeWithFallback(Supplier<T> supplier, Function<Throwable, T> fallback);

    State getState();
    Metrics getMetrics();
    void reset();

    interface Metrics {
        long totalCalls();
        long successCount();
        long failureCount();
        double failureRate();
    }
}
```

---

# oop/StandardCircuitBreaker.java

```java
package com.datastructure.circuitbreaker.oop;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;
import java.util.function.Supplier;

public class StandardCircuitBreaker implements CircuitBreaker {
    public static class CircuitBreakerOpenException extends RuntimeException {
        public CircuitBreakerOpenException() { super("Circuit breaker is OPEN"); }
    }

    public static class Config {
        public int failureThreshold = 5;
        public double failureRateThreshold = 50.0;
        public Duration waitDurationInOpen = Duration.ofSeconds(60);
        public int permittedCallsInHalfOpen = 3;
        public int slidingWindowSize = 10;
    }

    public static class Builder {
        private final Config c = new Config();
        public Builder failureThreshold(int v) { c.failureThreshold = v; return this; }
        public Builder failureRateThreshold(double v) { c.failureRateThreshold = v; return this; }
        public Builder waitDurationInOpen(Duration v) { c.waitDurationInOpen = v; return this; }
        public Builder permittedCallsInHalfOpen(int v) { c.permittedCallsInHalfOpen = v; return this; }
        public Builder slidingWindowSize(int v) { c.slidingWindowSize = v; return this; }
        public StandardCircuitBreaker build() { return new StandardCircuitBreaker(c); }
    }

    public static Builder builder() { return new Builder(); }

    private final Config cfg;
    private final boolean[] win;
    private final boolean[] has;
    private int idx;

    private volatile State state = State.CLOSED;
    private Instant openedAt;
    private int halfOpenSuccess;

    private long totalCalls, successCount, failureCount;

    public StandardCircuitBreaker(Config cfg) {
        this.cfg = cfg;
        this.win = new boolean[cfg.slidingWindowSize];
        this.has = new boolean[cfg.slidingWindowSize];
    }

    @Override
    public synchronized <T> T execute(Supplier<T> supplier) {
        guard();
        try {
            T result = supplier.get();
            onSuccess();
            return result;
        } catch (RuntimeException e) {
            onFailure();
            throw e;
        }
    }

    @Override
    public synchronized <T> T executeWithFallback(Supplier<T> supplier,
                                                  Function<Throwable, T> fallback) {
        try {
            return execute(supplier);
        } catch (Throwable t) {
            return fallback.apply(t);
        }
    }

    private void guard() {
        if (state == State.OPEN) {
            if (Duration.between(openedAt, Instant.now()).compareTo(cfg.waitDurationInOpen) >= 0) {
                transitionTo(State.HALF_OPEN);
            } else {
                throw new CircuitBreakerOpenException();
            }
        }
    }

    private void onSuccess() {
        totalCalls++;
        successCount++;
        record(true);
        if (state == State.HALF_OPEN) {
            halfOpenSuccess++;
            if (halfOpenSuccess >= cfg.permittedCallsInHalfOpen) transitionTo(State.CLOSED);
        }
    }

    private void onFailure() {
        totalCalls++;
        failureCount++;
        record(false);
        if (state == State.HALF_OPEN) {
            transitionTo(State.OPEN);
        } else if (state == State.CLOSED && shouldOpen()) {
            transitionTo(State.OPEN);
        }
    }

    private void record(boolean success) {
        win[idx] = success;
        has[idx] = true;
        idx = (idx + 1) % cfg.slidingWindowSize;
    }

    private boolean shouldOpen() {
        int filled = 0, fails = 0;
        for (int i = 0; i < cfg.slidingWindowSize; i++) {
            if (has[i]) {
                filled++;
                if (!win[i]) fails++;
            }
        }
        if (fails >= cfg.failureThreshold) return true;
        return filled >= cfg.slidingWindowSize
                && 100.0 * fails / filled >= cfg.failureRateThreshold;
    }

    private void transitionTo(State next) {
        state = next;
        if (next == State.OPEN) {
            openedAt = Instant.now();
            halfOpenSuccess = 0;
        } else if (next == State.HALF_OPEN) {
            halfOpenSuccess = 0;
        } else {
            for (int i = 0; i < cfg.slidingWindowSize; i++) has[i] = false;
            idx = 0;
        }
    }

    @Override
    public synchronized State getState() {
        return state;
    }

    @Override
    public synchronized Metrics getMetrics() {
        long t = totalCalls, s = successCount, f = failureCount;
        return new Metrics() {
            @Override public long totalCalls() { return t; }
            @Override public long successCount() { return s; }
            @Override public long failureCount() { return f; }
            @Override public double failureRate() { return t == 0 ? 0.0 : 100.0 * f / t; }
        };
    }

    @Override
    public synchronized void reset() {
        transitionTo(State.CLOSED);
        totalCalls = successCount = failureCount = 0;
    }
}
```
