# pop/UUIDGenerator.java

UUID v4 (랜덤) + UUID v7 (시간 기반) 생성.

```java
package com.datastructure.idgenerator.pop;

import java.security.SecureRandom;
import java.util.UUID;

public class UUIDGenerator {
    private final SecureRandom random = new SecureRandom();

    /** RFC 4122 v4: 122 random bits */
    public UUID generateV4() {
        return UUID.randomUUID();
    }

    /** RFC 9562 v7: 48-bit ms timestamp + version + random */
    public UUID generateV7() {
        long ts = System.currentTimeMillis() & 0xffffffffffffL;
        long randHi = random.nextInt() & 0x0fff; // 12 bits
        long randLo = random.nextLong() & 0x3fffffffffffffffL; // 62 bits

        // MSB: timestamp(48) | version 7 (4) | rand(12)
        long msb = (ts << 16) | (0x7L << 12) | randHi;
        // LSB: variant 10 (2) | rand(62)
        long lsb = (0x2L << 62) | randLo;

        return new UUID(msb, lsb);
    }

    public long getTimestampFromV7(UUID uuid) {
        return uuid.getMostSignificantBits() >>> 16;
    }
}
```
