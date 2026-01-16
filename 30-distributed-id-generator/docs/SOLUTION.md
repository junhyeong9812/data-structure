# 분산 ID 생성기 풀이 해설

## 📌 핵심 아이디어

분산 ID 생성기는 **중앙 조정 없이** 여러 노드에서
**충돌 없는 고유 ID**를 생성합니다.

**핵심 전략**:
- 시간 + 노드 ID + 시퀀스 조합 (Snowflake)
- 완전 랜덤 (UUID v4)
- 시간 + 랜덤 (UUID v7, ULID)

---

## 🔑 핵심 개념

### 1. ID 유형 비교
```
         정렬 가능?  크기   생성 속도  충돌 확률
Snowflake   ✓       64b    매우 빠름   없음*
UUID v4     ✗      128b    빠름        극히 낮음
UUID v7     ✓      128b    빠름        극히 낮음
ULID        ✓      128b    빠름        극히 낮음

* 올바른 설정 시
```

### 2. Snowflake 상세
```
64 bits:
┌─┬─────────────────────────────────────────┬─────┬─────┬────────────┐
│0│           41 bits timestamp             │5 DC │5 WK │ 12 seq     │
└─┴─────────────────────────────────────────┴─────┴─────┴────────────┘

- 부호 비트: 항상 0 (양수 보장)
- 타임스탬프: 에포크 이후 밀리초 (69년)
- 데이터센터 ID: 0-31 (32개)
- 워커 ID: 0-31 (노드당 32개)
- 시퀀스: 0-4095 (밀리초당 4096개)

초당 생성 가능: 32 × 32 × 4096 × 1000 = 4,194,304,000개
```

### 3. ULID 상세
```
128 bits (26 characters):
┌────────────────────────────────────────────────────────────────┐
│   48 bits timestamp   │           80 bits randomness          │
│   (10 characters)     │           (16 characters)             │
└────────────────────────────────────────────────────────────────┘

Crockford Base32 인코딩:
- 대소문자 구분 없음
- 혼동 문자 제외 (I, L, O, U)
- URL-safe
```

---

## 📝 POP 구현 해설

### Snowflake 완전 구현
```java
public class SnowflakeGenerator {
    // 에포크: 2024-01-01 00:00:00 UTC
    private static final long EPOCH = 1704067200000L;
    
    // 비트 할당
    private static final int WORKER_ID_BITS = 5;
    private static final int DATACENTER_ID_BITS = 5;
    private static final int SEQUENCE_BITS = 12;
    
    // 최대값
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);         // 31
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS); // 31
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);           // 4095
    
    // 시프트 양
    private static final int WORKER_ID_SHIFT = SEQUENCE_BITS;                   // 12
    private static final int DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS; // 17
    private static final int TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS; // 22
    
    private final long workerId;
    private final long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    
    public SnowflakeGenerator(long workerId, long datacenterId) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException("Worker ID must be 0-" + MAX_WORKER_ID);
        }
        if (datacenterId < 0 || datacenterId > MAX_DATACENTER_ID) {
            throw new IllegalArgumentException("Datacenter ID must be 0-" + MAX_DATACENTER_ID);
        }
        
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }
    
    public synchronized long generate() {
        long timestamp = currentTimeMillis();
        
        // 시계 역행 체크
        if (timestamp < lastTimestamp) {
            throw new ClockMovedBackwardsException(
                "Clock moved backwards. Refusing to generate id for " + 
                (lastTimestamp - timestamp) + " milliseconds"
            );
        }
        
        if (timestamp == lastTimestamp) {
            // 같은 밀리초 내에서 시퀀스 증가
            sequence = (sequence + 1) & MAX_SEQUENCE;
            
            if (sequence == 0) {
                // 시퀀스 오버플로우 - 다음 밀리초까지 대기
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 새 밀리초 - 시퀀스 리셋
            sequence = 0L;
        }
        
        lastTimestamp = timestamp;
        
        // ID 조립
        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
             | (datacenterId << DATACENTER_ID_SHIFT)
             | (workerId << WORKER_ID_SHIFT)
             | sequence;
    }
    
    public SnowflakeId parse(long id) {
        long timestamp = ((id >> TIMESTAMP_SHIFT) & 0x1FFFFFFFFFFL) + EPOCH;
        long datacenterId = (id >> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID;
        long workerId = (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
        long sequence = id & MAX_SEQUENCE;
        
        return new SnowflakeId(timestamp, datacenterId, workerId, sequence);
    }
    
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimeMillis();
        }
        return timestamp;
    }
    
    protected long currentTimeMillis() {
        return System.currentTimeMillis();
    }
    
    public long getWorkerId() { return workerId; }
    public long getDatacenterId() { return datacenterId; }
}

public record SnowflakeId(
    long timestamp,
    long datacenterId,
    long workerId,
    long sequence
) {
    public Instant toInstant() {
        return Instant.ofEpochMilli(timestamp);
    }
}
```

### UUID 구현
```java
public class UUIDGenerator {
    private final SecureRandom random = new SecureRandom();
    
    // UUID v4 (랜덤)
    public UUID generateV4() {
        byte[] data = new byte[16];
        random.nextBytes(data);
        
        // version 4
        data[6] = (byte) ((data[6] & 0x0f) | 0x40);
        // variant 10xx
        data[8] = (byte) ((data[8] & 0x3f) | 0x80);
        
        return bytesToUUID(data);
    }
    
    // UUID v7 (시간 기반)
    public UUID generateV7() {
        long timestamp = System.currentTimeMillis();
        
        byte[] data = new byte[16];
        random.nextBytes(data);
        
        // 48-bit timestamp (big-endian)
        data[0] = (byte) (timestamp >> 40);
        data[1] = (byte) (timestamp >> 32);
        data[2] = (byte) (timestamp >> 24);
        data[3] = (byte) (timestamp >> 16);
        data[4] = (byte) (timestamp >> 8);
        data[5] = (byte) timestamp;
        
        // version 7
        data[6] = (byte) ((data[6] & 0x0f) | 0x70);
        // variant 10xx
        data[8] = (byte) ((data[8] & 0x3f) | 0x80);
        
        return bytesToUUID(data);
    }
    
    public UUIDInfo parse(UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        
        int version = (int) ((msb >> 12) & 0xf);
        int variant = (int) ((lsb >> 62) & 0x3);
        
        Long timestamp = null;
        if (version == 7) {
            timestamp = (msb >> 16) & 0xFFFFFFFFFFFFL;
        }
        
        return new UUIDInfo(version, variant, timestamp);
    }
    
    private UUID bytesToUUID(byte[] data) {
        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (data[i] & 0xff);
        }
        for (int i = 8; i < 16; i++) {
            lsb = (lsb << 8) | (data[i] & 0xff);
        }
        return new UUID(msb, lsb);
    }
}

public record UUIDInfo(int version, int variant, Long timestamp) {
    public Optional<Instant> toInstant() {
        return Optional.ofNullable(timestamp)
            .map(Instant::ofEpochMilli);
    }
}
```

### ULID 구현
```java
public class ULIDGenerator {
    private static final char[] ENCODING = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();
    private static final byte[] DECODING = new byte[128];
    
    static {
        Arrays.fill(DECODING, (byte) -1);
        for (int i = 0; i < ENCODING.length; i++) {
            DECODING[ENCODING[i]] = (byte) i;
            DECODING[Character.toLowerCase(ENCODING[i])] = (byte) i;
        }
    }
    
    private final SecureRandom random = new SecureRandom();
    private long lastTimestamp = -1L;
    private final byte[] lastRandomness = new byte[10];
    
    public synchronized String generate() {
        long timestamp = System.currentTimeMillis();
        
        if (timestamp == lastTimestamp) {
            // 같은 밀리초 - 랜덤 부분 증가
            incrementRandomness();
        } else {
            // 새 밀리초 - 새 랜덤값
            random.nextBytes(lastRandomness);
            lastTimestamp = timestamp;
        }
        
        return encode(timestamp, lastRandomness);
    }
    
    private void incrementRandomness() {
        for (int i = lastRandomness.length - 1; i >= 0; i--) {
            if (++lastRandomness[i] != 0) {
                return;
            }
        }
        // 오버플로우 - 모든 바이트가 0xFF였음
        throw new IllegalStateException("ULID randomness overflow");
    }
    
    private String encode(long timestamp, byte[] randomness) {
        char[] result = new char[26];
        
        // 타임스탬프 인코딩 (48 bits → 10 chars)
        result[0] = ENCODING[(int) ((timestamp >> 45) & 0x1f)];
        result[1] = ENCODING[(int) ((timestamp >> 40) & 0x1f)];
        result[2] = ENCODING[(int) ((timestamp >> 35) & 0x1f)];
        result[3] = ENCODING[(int) ((timestamp >> 30) & 0x1f)];
        result[4] = ENCODING[(int) ((timestamp >> 25) & 0x1f)];
        result[5] = ENCODING[(int) ((timestamp >> 20) & 0x1f)];
        result[6] = ENCODING[(int) ((timestamp >> 15) & 0x1f)];
        result[7] = ENCODING[(int) ((timestamp >> 10) & 0x1f)];
        result[8] = ENCODING[(int) ((timestamp >> 5) & 0x1f)];
        result[9] = ENCODING[(int) (timestamp & 0x1f)];
        
        // 랜덤 인코딩 (80 bits → 16 chars)
        result[10] = ENCODING[(randomness[0] >> 3) & 0x1f];
        result[11] = ENCODING[((randomness[0] << 2) | ((randomness[1] >> 6) & 0x03)) & 0x1f];
        result[12] = ENCODING[(randomness[1] >> 1) & 0x1f];
        result[13] = ENCODING[((randomness[1] << 4) | ((randomness[2] >> 4) & 0x0f)) & 0x1f];
        result[14] = ENCODING[((randomness[2] << 1) | ((randomness[3] >> 7) & 0x01)) & 0x1f];
        result[15] = ENCODING[(randomness[3] >> 2) & 0x1f];
        result[16] = ENCODING[((randomness[3] << 3) | ((randomness[4] >> 5) & 0x07)) & 0x1f];
        result[17] = ENCODING[randomness[4] & 0x1f];
        result[18] = ENCODING[(randomness[5] >> 3) & 0x1f];
        result[19] = ENCODING[((randomness[5] << 2) | ((randomness[6] >> 6) & 0x03)) & 0x1f];
        result[20] = ENCODING[(randomness[6] >> 1) & 0x1f];
        result[21] = ENCODING[((randomness[6] << 4) | ((randomness[7] >> 4) & 0x0f)) & 0x1f];
        result[22] = ENCODING[((randomness[7] << 1) | ((randomness[8] >> 7) & 0x01)) & 0x1f];
        result[23] = ENCODING[(randomness[8] >> 2) & 0x1f];
        result[24] = ENCODING[((randomness[8] << 3) | ((randomness[9] >> 5) & 0x07)) & 0x1f];
        result[25] = ENCODING[randomness[9] & 0x1f];
        
        return new String(result);
    }
    
    public ULIDComponents parse(String ulid) {
        if (ulid.length() != 26) {
            throw new IllegalArgumentException("ULID must be 26 characters");
        }
        
        String upper = ulid.toUpperCase();
        
        // 타임스탬프 디코딩
        long timestamp = 0;
        for (int i = 0; i < 10; i++) {
            byte val = DECODING[upper.charAt(i)];
            if (val < 0) throw new IllegalArgumentException("Invalid character: " + upper.charAt(i));
            timestamp = (timestamp << 5) | val;
        }
        
        // 랜덤 디코딩
        byte[] randomness = new byte[10];
        // ... 디코딩 로직
        
        return new ULIDComponents(timestamp, randomness);
    }
    
    public Instant getTimestamp(String ulid) {
        return Instant.ofEpochMilli(parse(ulid).timestamp());
    }
}

public record ULIDComponents(long timestamp, byte[] randomness) {
    public Instant toInstant() {
        return Instant.ofEpochMilli(timestamp);
    }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | Snowflake | UUID | ULID |
|------|-----------|------|------|
| generate | O(1) | O(1) | O(1) |
| parse | O(1) | O(1) | O(1) |
| compare | O(1) | O(1) | O(1)* |

* ULID는 문자열 비교로 O(26)이지만 상수 취급

---

## ❌ 흔한 실수

### 1. 시계 역행 무시
```java
// 잘못됨: 시계 역행 무시
public long generate() {
    long timestamp = System.currentTimeMillis();
    // 과거로 돌아가도 그냥 생성?
}

// 올바름: 예외 또는 대기
if (timestamp < lastTimestamp) {
    throw new ClockMovedBackwardsException();
}
```

### 2. 동기화 누락
```java
// 잘못됨: 동기화 없음
public long generate() {
    // 여러 스레드가 동시에 시퀀스 증가?
}

// 올바름: synchronized
public synchronized long generate() {
    // 안전
}
```

### 3. 에포크 미설정
```java
// 잘못됨: Unix 에포크 사용
long timestamp = System.currentTimeMillis(); // 1970년부터
// 41비트로 69년 → 2039년에 오버플로우!

// 올바름: 커스텀 에포크
long timestamp = System.currentTimeMillis() - EPOCH; // 2024년부터
// 69년 더 사용 가능
```

---

## 🔗 관련 문제

- 분산 시스템 설계
- 데이터베이스 기본키
- 이벤트 소싱
- 메시지 큐
