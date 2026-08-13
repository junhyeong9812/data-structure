# oop/IdGenerator.java

OOP 인터페이스 + Snowflake/UUID/ULID 구현체.

```java
package com.datastructure.idgenerator.oop;

public interface IdGenerator<T> {
    T generate();
    long getTimestamp(T id);
}
```

---

# oop/SnowflakeIdGenerator.java

```java
package com.datastructure.idgenerator.oop;

public class SnowflakeIdGenerator implements IdGenerator<Long> {
    private static final long EPOCH = 1704067200000L;
    private static final int WORKER_BITS = 5;
    private static final int DC_BITS = 5;
    private static final int SEQ_BITS = 12;
    private static final long MAX_WORKER = ~(-1L << WORKER_BITS);
    private static final long MAX_DC = ~(-1L << DC_BITS);
    private static final long MAX_SEQ = ~(-1L << SEQ_BITS);
    private static final int WORKER_SHIFT = SEQ_BITS;
    private static final int DC_SHIFT = SEQ_BITS + WORKER_BITS;
    private static final int TS_SHIFT = SEQ_BITS + WORKER_BITS + DC_BITS;

    private final long datacenterId;
    private final long workerId;
    private long sequence;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long datacenterId, long workerId) {
        if (datacenterId < 0 || datacenterId > MAX_DC) throw new IllegalArgumentException();
        if (workerId < 0 || workerId > MAX_WORKER) throw new IllegalArgumentException();
        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    @Override
    public synchronized Long generate() {
        long ts = System.currentTimeMillis();
        if (ts < lastTimestamp) throw new RuntimeException("clock backward");
        if (ts == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQ;
            if (sequence == 0) {
                while (ts <= lastTimestamp) ts = System.currentTimeMillis();
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = ts;
        return ((ts - EPOCH) << TS_SHIFT) | (datacenterId << DC_SHIFT)
                | (workerId << WORKER_SHIFT) | sequence;
    }

    @Override
    public long getTimestamp(Long id) {
        return (id >> TS_SHIFT) + EPOCH;
    }
}
```

---

# oop/UUIDv7Generator.java

```java
package com.datastructure.idgenerator.oop;

import java.security.SecureRandom;
import java.util.UUID;

public class UUIDv7Generator implements IdGenerator<UUID> {
    private final SecureRandom random = new SecureRandom();

    @Override
    public UUID generate() {
        long ts = System.currentTimeMillis() & 0xffffffffffffL;
        long randHi = random.nextInt() & 0x0fff;
        long randLo = random.nextLong() & 0x3fffffffffffffffL;
        long msb = (ts << 16) | (0x7L << 12) | randHi;
        long lsb = (0x2L << 62) | randLo;
        return new UUID(msb, lsb);
    }

    @Override
    public long getTimestamp(UUID id) {
        return id.getMostSignificantBits() >>> 16;
    }
}
```

---

# oop/ULIDIdGenerator.java

```java
package com.datastructure.idgenerator.oop;

import com.datastructure.idgenerator.pop.ULIDGenerator;

public class ULIDIdGenerator implements IdGenerator<String> {
    private final ULIDGenerator inner = new ULIDGenerator();

    @Override
    public String generate() {
        return inner.generate();
    }

    @Override
    public long getTimestamp(String id) {
        return ULIDGenerator.parse(id).timestamp;
    }
}
```
