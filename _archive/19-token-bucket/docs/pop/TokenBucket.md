# pop/TokenBucket.java

지연 리필(Lazy Refill) 토큰 버킷. tryConsume / consume(블로킹) / tryConsumeAndReturnDelay.

```java
package com.datastructure.tokenbucket.pop;

import java.time.Duration;
import java.time.Instant;

public class TokenBucket {
    private final long capacity;
    private final long refillTokens;
    private final long refillPeriodMillis;

    private long availableTokens;
    private Instant lastRefillTime;

    public TokenBucket(long capacity, long refillTokens, Duration refillPeriod) {
        if (capacity <= 0 || refillTokens <= 0) {
            throw new IllegalArgumentException("capacity, refillTokens > 0");
        }
        if (refillPeriod.toMillis() <= 0) {
            throw new IllegalArgumentException("refillPeriod > 0");
        }
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillPeriodMillis = refillPeriod.toMillis();
        this.availableTokens = capacity;
        this.lastRefillTime = Instant.now();
    }

    public synchronized boolean tryConsume(long tokens) {
        if (tokens <= 0) throw new IllegalArgumentException();
        refill();
        if (availableTokens >= tokens) {
            availableTokens -= tokens;
            return true;
        }
        return false;
    }

    /** 토큰이 충분해질 때까지 블로킹 후 소비. */
    public void consume(long tokens) throws InterruptedException {
        while (true) {
            long waitMs;
            synchronized (this) {
                refill();
                if (availableTokens >= tokens) {
                    availableTokens -= tokens;
                    return;
                }
                long needed = tokens - availableTokens;
                long periodsNeeded = (needed + refillTokens - 1) / refillTokens;
                waitMs = periodsNeeded * refillPeriodMillis;
            }
            Thread.sleep(waitMs);
        }
    }

    public synchronized long tryConsumeAndReturnDelay(long tokens) {
        refill();
        if (availableTokens >= tokens) {
            availableTokens -= tokens;
            return 0;
        }
        long needed = tokens - availableTokens;
        long periodsNeeded = (needed + refillTokens - 1) / refillTokens;
        return periodsNeeded * refillPeriodMillis;
    }

    public synchronized long getAvailableTokens() {
        refill();
        return availableTokens;
    }

    public long getCapacity() {
        return capacity;
    }

    private void refill() {
        Instant now = Instant.now();
        long elapsedMs = Duration.between(lastRefillTime, now).toMillis();
        if (elapsedMs < refillPeriodMillis) return;

        long periods = elapsedMs / refillPeriodMillis;
        long add = periods * refillTokens;
        availableTokens = Math.min(capacity, availableTokens + add);
        lastRefillTime = lastRefillTime.plusMillis(periods * refillPeriodMillis);
    }
}
```
