# pop/CircuitBreaker.java

기본 서킷 브레이커. CLOSED/OPEN/HALF_OPEN 상태 관리. 슬라이딩 윈도우 기반 실패율 계산.

```java
package com.datastructure.circuitbreaker.pop;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;
import java.util.function.Supplier;

public class CircuitBreaker {
    public enum State { CLOSED, OPEN, HALF_OPEN }

    public static class CircuitBreakerOpenException extends RuntimeException {
        public CircuitBreakerOpenException() { super("Circuit breaker is OPEN"); }
    }

    private final int failureThreshold;
    private final double failureRateThreshold; // 0~100
    private final Duration waitDurationInOpen;
    private final int permittedCallsInHalfOpen;
    private final int slidingWindowSize;

    private final boolean[] window; // true=success, false=failure
    private final boolean[] hasValue;
    private int windowIndex;

    private volatile State state;
    private Instant openedAt;
    private int halfOpenSuccess;
    private int halfOpenFailure;

    private long totalCalls;
    private long successCount;
    private long failureCount;

    public CircuitBreaker(int failureThreshold, double failureRateThreshold,
                          Duration waitDurationInOpen, int permittedCallsInHalfOpen,
                          int slidingWindowSize) {
        this.failureThreshold = failureThreshold;
        this.failureRateThreshold = failureRateThreshold;
        this.waitDurationInOpen = waitDurationInOpen;
        this.permittedCallsInHalfOpen = permittedCallsInHalfOpen;
        this.slidingWindowSize = slidingWindowSize;
        this.window = new boolean[slidingWindowSize];
        this.hasValue = new boolean[slidingWindowSize];
        this.state = State.CLOSED;
    }

    public synchronized <T> T execute(Supplier<T> supplier) {
        guardState();
        try {
            T result = supplier.get();
            onSuccess();
            return result;
        } catch (RuntimeException e) {
            onFailure();
            throw e;
        }
    }

    public synchronized <T> T executeWithFallback(Supplier<T> supplier,
                                                  Function<Throwable, T> fallback) {
        try {
            return execute(supplier);
        } catch (Throwable t) {
            return fallback.apply(t);
        }
    }

    private void guardState() {
        if (state == State.OPEN) {
            if (Duration.between(openedAt, Instant.now()).compareTo(waitDurationInOpen) >= 0) {
                transitionTo(State.HALF_OPEN);
            } else {
                throw new CircuitBreakerOpenException();
            }
        }
    }

    private void onSuccess() {
        totalCalls++;
        successCount++;
        recordWindow(true);

        if (state == State.HALF_OPEN) {
            halfOpenSuccess++;
            if (halfOpenSuccess >= permittedCallsInHalfOpen) {
                transitionTo(State.CLOSED);
            }
        }
    }

    private void onFailure() {
        totalCalls++;
        failureCount++;
        recordWindow(false);

        if (state == State.HALF_OPEN) {
            halfOpenFailure++;
            transitionTo(State.OPEN);
        } else if (state == State.CLOSED && shouldOpen()) {
            transitionTo(State.OPEN);
        }
    }

    private void recordWindow(boolean success) {
        window[windowIndex] = success;
        hasValue[windowIndex] = true;
        windowIndex = (windowIndex + 1) % slidingWindowSize;
    }

    private boolean shouldOpen() {
        int filled = 0, fails = 0;
        for (int i = 0; i < slidingWindowSize; i++) {
            if (hasValue[i]) {
                filled++;
                if (!window[i]) fails++;
            }
        }
        if (fails >= failureThreshold) return true;
        if (filled >= slidingWindowSize) {
            double rate = 100.0 * fails / filled;
            return rate >= failureRateThreshold;
        }
        return false;
    }

    private void transitionTo(State next) {
        this.state = next;
        if (next == State.OPEN) {
            openedAt = Instant.now();
            halfOpenSuccess = 0;
            halfOpenFailure = 0;
        } else if (next == State.HALF_OPEN) {
            halfOpenSuccess = 0;
            halfOpenFailure = 0;
        } else if (next == State.CLOSED) {
            for (int i = 0; i < slidingWindowSize; i++) {
                hasValue[i] = false;
            }
            windowIndex = 0;
        }
    }

    public synchronized State getState() {
        return state;
    }

    public synchronized double getFailureRate() {
        int filled = 0, fails = 0;
        for (int i = 0; i < slidingWindowSize; i++) {
            if (hasValue[i]) {
                filled++;
                if (!window[i]) fails++;
            }
        }
        return filled == 0 ? 0.0 : 100.0 * fails / filled;
    }

    public synchronized long getTotalCalls() { return totalCalls; }
    public synchronized long getSuccessCount() { return successCount; }
    public synchronized long getFailureCount() { return failureCount; }

    public synchronized void reset() {
        transitionTo(State.CLOSED);
        totalCalls = successCount = failureCount = 0;
    }
}
```
