# 분산 ID 생성기 구현에 유용한 Java API

## 🔢 비트 연산

### 시프트 연산
```java
// 왼쪽 시프트
long shifted = value << bits;
// 예: 1L << 22 = 4194304

// 오른쪽 시프트
long shifted = value >> bits;
// 예: 4194304 >> 22 = 1

// 부호 없는 오른쪽 시프트
long shifted = value >>> bits;
```

### 마스킹
```java
// N 비트 마스크 생성
long mask = ~(-1L << n);  // n개의 1비트
// 예: ~(-1L << 5) = 0b11111 = 31

// 특정 비트 추출
long bits = (value >> shift) & mask;

// 예: Snowflake에서 워커 ID 추출
long workerId = (id >> 12) & 31;
```

### 비트 조합
```java
// 여러 값을 하나의 long으로
long id = (timestamp << 22)
        | (datacenterId << 17)
        | (workerId << 12)
        | sequence;
```

---

## ⏱️ 시간 관련

### System.currentTimeMillis()
```java
// 현재 시간 (밀리초)
long now = System.currentTimeMillis();

// 커스텀 에포크 적용
long EPOCH = 1704067200000L; // 2024-01-01
long relativeTime = now - EPOCH;
```

### Instant
```java
import java.time.Instant;

// 타임스탬프 → Instant
Instant instant = Instant.ofEpochMilli(timestamp);

// Instant → 타임스탬프
long millis = instant.toEpochMilli();

// 현재 시간
Instant now = Instant.now();
```

### 시계 역행 대기
```java
private long waitNextMillis(long lastTimestamp) {
    long timestamp = System.currentTimeMillis();
    while (timestamp <= lastTimestamp) {
        Thread.onSpinWait();  // CPU 힌트 (Java 9+)
        timestamp = System.currentTimeMillis();
    }
    return timestamp;
}
```

---

## 🔐 랜덤 생성

### SecureRandom
```java
import java.security.SecureRandom;

SecureRandom random = new SecureRandom();

// 바이트 배열
byte[] bytes = new byte[16];
random.nextBytes(bytes);

// long 값
long randomLong = random.nextLong();

// 범위 내 값
int randomInt = random.nextInt(100);  // 0-99
```

### ThreadLocalRandom
```java
import java.util.concurrent.ThreadLocalRandom;

// 스레드별 랜덤 (더 빠름, 보안 낮음)
long random = ThreadLocalRandom.current().nextLong();
```

---

## 📝 문자열/바이트 변환

### 바이트 → long
```java
// Big-endian
public static long bytesToLong(byte[] bytes) {
    long value = 0;
    for (int i = 0; i < 8; i++) {
        value = (value << 8) | (bytes[i] & 0xff);
    }
    return value;
}
```

### long → 바이트
```java
// Big-endian
public static byte[] longToBytes(long value) {
    byte[] bytes = new byte[8];
    for (int i = 7; i >= 0; i--) {
        bytes[i] = (byte) (value & 0xff);
        value >>= 8;
    }
    return bytes;
}
```

### Base32 인코딩
```java
// Crockford Base32
private static final char[] ENCODING = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();

public static char encodeBase32(int value) {
    return ENCODING[value & 0x1f];
}

public static int decodeBase32(char c) {
    // I, L → 1, O → 0 변환 포함
    c = Character.toUpperCase(c);
    if (c == 'I' || c == 'L') return 1;
    if (c == 'O') return 0;
    // ... 테이블 조회
}
```

### 16진수 변환
```java
// 바이트 → 16진수 문자열
public static String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
        sb.append(String.format("%02x", b));
    }
    return sb.toString();
}

// UUID 형식
public static String formatUUID(byte[] bytes) {
    return String.format(
        "%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x",
        bytes[0], bytes[1], bytes[2], bytes[3],
        bytes[4], bytes[5],
        bytes[6], bytes[7],
        bytes[8], bytes[9],
        bytes[10], bytes[11], bytes[12], bytes[13], bytes[14], bytes[15]
    );
}
```

---

## 🧪 테스트

### AssertJ
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldGenerateUniqueIds() {
    SnowflakeGenerator generator = new SnowflakeGenerator(1, 1);
    
    Set<Long> ids = new HashSet<>();
    for (int i = 0; i < 10000; i++) {
        ids.add(generator.generate());
    }
    
    assertThat(ids).hasSize(10000);
}

@Test
void shouldParseSnowflakeId() {
    SnowflakeGenerator generator = new SnowflakeGenerator(5, 10);
    
    long id = generator.generate();
    SnowflakeId parsed = generator.parse(id);
    
    assertThat(parsed.workerId()).isEqualTo(5);
    assertThat(parsed.datacenterId()).isEqualTo(10);
}

@Test
void shouldGenerateSortableULIDs() throws InterruptedException {
    ULIDGenerator generator = new ULIDGenerator();
    
    String ulid1 = generator.generate();
    Thread.sleep(1);
    String ulid2 = generator.generate();
    
    assertThat(ulid1).isLessThan(ulid2);
}

@Test
void shouldGenerateMonotonicIds() {
    SnowflakeGenerator generator = new SnowflakeGenerator(1, 1);
    
    long prev = 0;
    for (int i = 0; i < 10000; i++) {
        long id = generator.generate();
        assertThat(id).isGreaterThan(prev);
        prev = id;
    }
}
```

### 동시성 테스트
```java
@Test
void shouldBeThreadSafe() throws Exception {
    SnowflakeGenerator generator = new SnowflakeGenerator(1, 1);
    Set<Long> ids = ConcurrentHashMap.newKeySet();
    int threads = 10;
    int idsPerThread = 10000;
    
    ExecutorService executor = Executors.newFixedThreadPool(threads);
    CountDownLatch latch = new CountDownLatch(threads);
    
    for (int i = 0; i < threads; i++) {
        executor.submit(() -> {
            try {
                for (int j = 0; j < idsPerThread; j++) {
                    ids.add(generator.generate());
                }
            } finally {
                latch.countDown();
            }
        });
    }
    
    latch.await();
    assertThat(ids).hasSize(threads * idsPerThread);
}
```

---

## 📚 Java 21 관련

### Record
```java
// Snowflake 파싱 결과
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

// ULID 컴포넌트
public record ULIDComponents(
    long timestamp,
    byte[] randomness
) {}

// UUID 정보
public record UUIDInfo(
    int version,
    int variant,
    Long timestamp
) {}
```

### Pattern Matching
```java
public String describe(Object id) {
    return switch (id) {
        case Long l -> "Snowflake: " + parseSnowflake(l);
        case UUID u -> "UUID v" + ((u.version()) + ": " + u);
        case String s when s.length() == 26 -> "ULID: " + s;
        default -> "Unknown ID type";
    };
}
```

### Virtual Threads (테스트용)
```java
@Test
void testWithVirtualThreads() throws Exception {
    SnowflakeGenerator generator = new SnowflakeGenerator(1, 1);
    Set<Long> ids = ConcurrentHashMap.newKeySet();
    
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
        for (int i = 0; i < 100_000; i++) {
            executor.submit(() -> ids.add(generator.generate()));
        }
    }
    
    assertThat(ids).hasSize(100_000);
}
```

---

## ⚡ 성능 팁

### 1. 동기화 최소화
```java
// AtomicLong으로 시퀀스 관리
private final AtomicLong sequence = new AtomicLong(0);

public long generate() {
    long timestamp = System.currentTimeMillis();
    long seq = sequence.getAndIncrement() & MAX_SEQUENCE;
    
    return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
         | (datacenterId << DATACENTER_ID_SHIFT)
         | (workerId << WORKER_ID_SHIFT)
         | seq;
}
```

### 2. 배치 생성
```java
public long[] generateBatch(int count) {
    long[] ids = new long[count];
    synchronized (this) {
        for (int i = 0; i < count; i++) {
            ids[i] = generateInternal();
        }
    }
    return ids;
}
```

### 3. 시퀀스 프리페치
```java
// 미리 시퀀스 블록 할당
private static final int BATCH_SIZE = 1000;
private long[] preAllocated;
private int index;

public synchronized long generate() {
    if (index >= preAllocated.length) {
        preAllocated = generateBatch(BATCH_SIZE);
        index = 0;
    }
    return preAllocated[index++];
}
```

---

## 🔀 예외 클래스
```java
public class ClockMovedBackwardsException extends RuntimeException {
    public ClockMovedBackwardsException(String message) {
        super(message);
    }
}

public class SequenceOverflowException extends RuntimeException {
    public SequenceOverflowException(String message) {
        super(message);
    }
}

public class InvalidIdException extends RuntimeException {
    public InvalidIdException(String message) {
        super(message);
    }
}
```
