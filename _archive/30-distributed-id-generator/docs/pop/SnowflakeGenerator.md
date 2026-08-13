# pop/SnowflakeGenerator.java

Twitter Snowflake. 64-bit: timestamp(41) + datacenter(5) + worker(5) + sequence(12).

```java
package com.datastructure.idgenerator.pop;

public class SnowflakeGenerator {

    public static class ClockMovedBackwardsException extends RuntimeException {
        public ClockMovedBackwardsException(long delta) {
            super("Clock moved backwards by " + delta + "ms");
        }
    }

    public static class SnowflakeId {
        public final long timestamp;
        public final long datacenterId;
        public final long workerId;
        public final long sequence;

        public SnowflakeId(long t, long dc, long w, long s) {
            this.timestamp = t;
            this.datacenterId = dc;
            this.workerId = w;
            this.sequence = s;
        }

        @Override
        public String toString() {
            return "SnowflakeId{ts=" + timestamp + ",dc=" + datacenterId
                    + ",w=" + workerId + ",seq=" + sequence + "}";
        }
    }

    private static final long EPOCH = 1704067200000L; // 2024-01-01 UTC

    private static final int WORKER_BITS = 5;
    private static final int DATACENTER_BITS = 5;
    private static final int SEQUENCE_BITS = 12;

    private static final long MAX_WORKER = ~(-1L << WORKER_BITS);
    private static final long MAX_DATACENTER = ~(-1L << DATACENTER_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    private static final int WORKER_SHIFT = SEQUENCE_BITS;
    private static final int DATACENTER_SHIFT = SEQUENCE_BITS + WORKER_BITS;
    private static final int TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_BITS + DATACENTER_BITS;

    private final long datacenterId;
    private final long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeGenerator(long datacenterId, long workerId) {
        if (datacenterId < 0 || datacenterId > MAX_DATACENTER) {
            throw new IllegalArgumentException("dc out of range");
        }
        if (workerId < 0 || workerId > MAX_WORKER) {
            throw new IllegalArgumentException("worker out of range");
        }
        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    public synchronized long generate() {
        long ts = System.currentTimeMillis();
        if (ts < lastTimestamp) {
            throw new ClockMovedBackwardsException(lastTimestamp - ts);
        }
        if (ts == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) ts = waitNextMillis(lastTimestamp);
        } else {
            sequence = 0L;
        }
        lastTimestamp = ts;
        return ((ts - EPOCH) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_SHIFT)
                | (workerId << WORKER_SHIFT)
                | sequence;
    }

    private long waitNextMillis(long lastTs) {
        long ts = System.currentTimeMillis();
        while (ts <= lastTs) ts = System.currentTimeMillis();
        return ts;
    }

    public SnowflakeId parse(long id) {
        long sequence = id & MAX_SEQUENCE;
        long worker = (id >> WORKER_SHIFT) & MAX_WORKER;
        long dc = (id >> DATACENTER_SHIFT) & MAX_DATACENTER;
        long ts = (id >> TIMESTAMP_SHIFT) + EPOCH;
        return new SnowflakeId(ts, dc, worker, sequence);
    }

    public long getTimestamp(long id) {
        return (id >> TIMESTAMP_SHIFT) + EPOCH;
    }
}
```
