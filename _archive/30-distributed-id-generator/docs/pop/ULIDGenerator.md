# pop/ULIDGenerator.java

ULID. 26-char Crockford Base32: 10 chars timestamp(48 bits) + 16 chars randomness(80 bits).

```java
package com.datastructure.idgenerator.pop;

import java.security.SecureRandom;

public class ULIDGenerator {
    private static final char[] ENCODING =
            "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();
    private static final byte[] DECODING = new byte[128];
    static {
        java.util.Arrays.fill(DECODING, (byte) -1);
        for (int i = 0; i < ENCODING.length; i++) DECODING[ENCODING[i]] = (byte) i;
    }

    public static class ULIDComponents {
        public final long timestamp;
        public final byte[] randomness;
        public ULIDComponents(long ts, byte[] r) {
            this.timestamp = ts;
            this.randomness = r;
        }
    }

    private final SecureRandom random = new SecureRandom();
    private long lastTimestamp = -1L;
    private final byte[] lastRandomness = new byte[10];

    public synchronized String generate() {
        long ts = System.currentTimeMillis();
        if (ts == lastTimestamp) {
            incrementRandomness();
        } else {
            random.nextBytes(lastRandomness);
            lastTimestamp = ts;
        }
        return encode(ts, lastRandomness);
    }

    private void incrementRandomness() {
        for (int i = 9; i >= 0; i--) {
            int v = (lastRandomness[i] & 0xff) + 1;
            lastRandomness[i] = (byte) (v & 0xff);
            if (v < 256) return;
        }
        throw new RuntimeException("randomness overflow");
    }

    public static String encode(long ts, byte[] randomness) {
        if (randomness.length != 10) throw new IllegalArgumentException();
        StringBuilder sb = new StringBuilder(26);
        // timestamp 10 chars (48 bits → 10 base32 chars)
        for (int i = 9; i >= 0; i--) {
            sb.append(ENCODING[(int) ((ts >>> (i * 5)) & 0x1f)]);
            // 50 bits used; only low 48 bits relevant — top 2 bits will be 0 for ms timestamps
        }
        // 위 방식으로 11번 인코딩이 아닌 10번. (i=9..0 → 10번) ✓
        sb.setLength(0);
        // 정확한 인코딩: 48 비트 → 10 char (5비트 단위, MSB 2비트는 패딩)
        long t = ts & 0xffffffffffffL;
        sb.append(ENCODING[(int) ((t >>> 45) & 0x07)]); // top 3 bits + padding
        for (int i = 8; i >= 0; i--) {
            sb.append(ENCODING[(int) ((t >>> (i * 5)) & 0x1f)]);
        }
        // randomness 16 chars (80 bits)
        long hi = 0;
        for (int i = 0; i < 5; i++) hi = (hi << 8) | (randomness[i] & 0xff);
        long lo = 0;
        for (int i = 5; i < 10; i++) lo = (lo << 8) | (randomness[i] & 0xff);
        for (int i = 7; i >= 0; i--) sb.append(ENCODING[(int) ((hi >>> (i * 5)) & 0x1f)]);
        for (int i = 7; i >= 0; i--) sb.append(ENCODING[(int) ((lo >>> (i * 5)) & 0x1f)]);
        return sb.toString();
    }

    public static ULIDComponents parse(String ulid) {
        if (ulid.length() != 26) throw new IllegalArgumentException();
        long ts = 0;
        for (int i = 0; i < 10; i++) {
            int v = DECODING[ulid.charAt(i)];
            if (v < 0) throw new IllegalArgumentException("invalid char");
            ts = (ts << 5) | v;
        }
        ts &= 0xffffffffffffL;

        byte[] randomness = new byte[10];
        long hi = 0;
        for (int i = 10; i < 18; i++) hi = (hi << 5) | DECODING[ulid.charAt(i)];
        long lo = 0;
        for (int i = 18; i < 26; i++) lo = (lo << 5) | DECODING[ulid.charAt(i)];
        for (int i = 4; i >= 0; i--) {
            randomness[i] = (byte) (hi & 0xff);
            hi >>>= 8;
        }
        for (int i = 9; i >= 5; i--) {
            randomness[i] = (byte) (lo & 0xff);
            lo >>>= 8;
        }
        return new ULIDComponents(ts, randomness);
    }
}
```
