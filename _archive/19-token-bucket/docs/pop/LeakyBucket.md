# pop/LeakyBucket.java

Leaky Bucket. 일정 속도로 "새는" 큐 기반 평탄화. offer는 큐에 적재, processing은 일정 간격으로 drain.

```java
package com.datastructure.tokenbucket.pop;

import java.time.Duration;
import java.time.Instant;

public class LeakyBucket {
    private final long capacity;
    private final long leakIntervalMillis;
    private long currentSize;
    private Instant lastLeakTime;

    public LeakyBucket(long capacity, Duration leakInterval) {
        this.capacity = capacity;
        this.leakIntervalMillis = leakInterval.toMillis();
        this.currentSize = 0;
        this.lastLeakTime = Instant.now();
    }

    public synchronized boolean offer() {
        leak();
        if (currentSize < capacity) {
            currentSize++;
            return true;
        }
        return false;
    }

    public synchronized long getCurrentSize() {
        leak();
        return currentSize;
    }

    private void leak() {
        Instant now = Instant.now();
        long elapsed = Duration.between(lastLeakTime, now).toMillis();
        if (elapsed < leakIntervalMillis) return;
        long leakedCount = elapsed / leakIntervalMillis;
        currentSize = Math.max(0, currentSize - leakedCount);
        lastLeakTime = lastLeakTime.plusMillis(leakedCount * leakIntervalMillis);
    }
}
```
