# oop/RateLimiter.java

레이트 리미터 인터페이스 + 토큰 버킷 구현 (Builder 패턴).

```java
package com.datastructure.tokenbucket.oop;

public interface RateLimiter {
    boolean tryAcquire();
    boolean tryAcquire(long permits);
    void acquire(long permits) throws InterruptedException;
    long tryAcquireAndReturnDelayMillis(long permits);
    long getAvailablePermits();
    long getCapacity();
}
```

---

# oop/TokenBucketRateLimiter.java

```java
package com.datastructure.tokenbucket.oop;

import java.time.Duration;
import java.time.Instant;

public class TokenBucketRateLimiter implements RateLimiter {
    private final long capacity;
    private final long refillTokens;
    private final long refillPeriodMillis;

    private long available;
    private Instant lastRefill;

    public static class Builder {
        private long capacity;
        private long refillTokens;
        private Duration refillPeriod = Duration.ofSeconds(1);

        public Builder capacity(long c) { this.capacity = c; return this; }
        public Builder refillTokens(long t) { this.refillTokens = t; return this; }
        public Builder refillPeriod(Duration d) { this.refillPeriod = d; return this; }
        public TokenBucketRateLimiter build() {
            return new TokenBucketRateLimiter(capacity, refillTokens, refillPeriod);
        }
    }

    public static Builder builder() { return new Builder(); }

    public TokenBucketRateLimiter(long capacity, long refillTokens, Duration refillPeriod) {
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillPeriodMillis = refillPeriod.toMillis();
        this.available = capacity;
        this.lastRefill = Instant.now();
    }

    @Override
    public boolean tryAcquire() {
        return tryAcquire(1);
    }

    @Override
    public synchronized boolean tryAcquire(long permits) {
        refill();
        if (available >= permits) {
            available -= permits;
            return true;
        }
        return false;
    }

    @Override
    public void acquire(long permits) throws InterruptedException {
        while (true) {
            long wait;
            synchronized (this) {
                refill();
                if (available >= permits) {
                    available -= permits;
                    return;
                }
                long needed = permits - available;
                long periods = (needed + refillTokens - 1) / refillTokens;
                wait = periods * refillPeriodMillis;
            }
            Thread.sleep(wait);
        }
    }

    @Override
    public synchronized long tryAcquireAndReturnDelayMillis(long permits) {
        refill();
        if (available >= permits) {
            available -= permits;
            return 0L;
        }
        long needed = permits - available;
        long periods = (needed + refillTokens - 1) / refillTokens;
        return periods * refillPeriodMillis;
    }

    @Override
    public synchronized long getAvailablePermits() {
        refill();
        return available;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    private void refill() {
        long elapsed = Duration.between(lastRefill, Instant.now()).toMillis();
        if (elapsed < refillPeriodMillis) return;
        long periods = elapsed / refillPeriodMillis;
        available = Math.min(capacity, available + periods * refillTokens);
        lastRefill = lastRefill.plusMillis(periods * refillPeriodMillis);
    }
}
```
