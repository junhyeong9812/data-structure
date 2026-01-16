# 30. 분산 ID 생성기 (Distributed ID Generator)

## 📋 문제 정의

**분산 환경에서 고유한 ID를 생성**하는 여러 알고리즘을 구현하세요.

분산 ID 생성기는 여러 서버/노드에서 동시에 충돌 없이
정렬 가능하고 고유한 ID를 생성합니다.

---

## 🎯 학습 목표

- Snowflake 알고리즘
- UUID (v1, v4, v7)
- ULID (Universally Unique Lexicographically Sortable Identifier)
- 시간 기반 ID 생성
- 비트 조작과 인코딩

---

## 📝 요구사항

### 핵심 개념

| 개념 | 설명 |
|------|------|
| **Uniqueness** | 전역적으로 고유한 ID |
| **Sortability** | 시간순 정렬 가능 |
| **Distributed** | 중앙 조정 없이 생성 |
| **Compact** | 저장 공간 효율적 |

### ID 유형

| 유형 | 크기 | 특징 |
|------|------|------|
| **Snowflake** | 64 bits | 시간순 정렬, 고성능 |
| **UUID v4** | 128 bits | 완전 랜덤, 표준화 |
| **UUID v7** | 128 bits | 시간 기반, 정렬 가능 |
| **ULID** | 128 bits | 시간+랜덤, 문자열 친화 |

### 기본 연산

| 메서드 | 설명 |
|--------|------|
| `generate()` | 새 ID 생성 |
| `parse(id)` | ID에서 정보 추출 |
| `getTimestamp(id)` | 생성 시간 추출 |
| `compare(id1, id2)` | ID 비교 |

---

## 📊 입출력 예시

### 예제 1: Snowflake
```java
SnowflakeGenerator generator = new SnowflakeGenerator(1, 1);
// workerId=1, datacenterId=1

long id1 = generator.generate();  // 7196912405048999936
long id2 = generator.generate();  // 7196912405048999937
long id3 = generator.generate();  // 7196912405048999938

// ID 파싱
SnowflakeId parsed = generator.parse(id1);
System.out.println(parsed.timestamp());    // 1704067200000
System.out.println(parsed.datacenterId()); // 1
System.out.println(parsed.workerId());     // 1
System.out.println(parsed.sequence());     // 0
```

### 예제 2: Snowflake 비트 구조
```
64 bits Snowflake ID:
┌─────────────────────────────────────────────────────────────────┐
│ 0 │      41 bits timestamp      │ 5 bits │ 5 bits │  12 bits   │
│   │   (milliseconds since epoch) │ DC ID  │ Worker │  sequence  │
└─────────────────────────────────────────────────────────────────┘

예: 7196912405048999936
  - timestamp: 1704067200000 (2024-01-01 00:00:00 UTC)
  - datacenter: 1
  - worker: 1  
  - sequence: 0
```

### 예제 3: UUID
```java
// UUID v4 (랜덤)
UUIDGenerator uuidGen = new UUIDGenerator();
String uuid4 = uuidGen.generateV4();
// "550e8400-e29b-41d4-a716-446655440000"

// UUID v7 (시간 기반)
String uuid7 = uuidGen.generateV7();
// "018d5e8c-4d6a-7000-8000-000000000001"

// UUID 비교
boolean isNewer = uuidGen.compare(uuid7_1, uuid7_2) < 0;
```

### 예제 4: ULID
```java
ULIDGenerator ulidGen = new ULIDGenerator();

String ulid1 = ulidGen.generate();  // "01ARZ3NDEKTSV4RRFFQ69G5FAV"
String ulid2 = ulidGen.generate();  // "01ARZ3NDEKTSV4RRFFQ69G5FAW"

// ULID 파싱
ULIDComponents components = ulidGen.parse(ulid1);
System.out.println(components.timestamp());  // 1704067200000
System.out.println(components.randomness()); // [random bytes]

// 문자열 비교로 정렬 가능
boolean ordered = ulid1.compareTo(ulid2) < 0;  // true
```

### 예제 5: ULID 구조
```
ULID (26 characters, Crockford Base32):
01ARZ3NDEKTSV4RRFFQ69G5FAV

┌──────────────────┬──────────────────────────────────┐
│   10 chars       │        16 chars                  │
│   Timestamp      │        Randomness                │
│   (48 bits)      │        (80 bits)                 │
└──────────────────┴──────────────────────────────────┘

장점:
- 문자열 정렬 = 시간순 정렬
- URL-safe
- 대소문자 무관
```

---

## 🔍 핵심 개념

### Snowflake 구조
```java
// 64 bits 분배
// 1 bit: 부호 (항상 0)
// 41 bits: 타임스탬프 (69년)
// 5 bits: 데이터센터 ID (0-31)
// 5 bits: 워커 ID (0-31)
// 12 bits: 시퀀스 (0-4095)

public class SnowflakeGenerator {
    private static final long EPOCH = 1704067200000L; // 2024-01-01
    
    private static final int WORKER_ID_BITS = 5;
    private static final int DATACENTER_ID_BITS = 5;
    private static final int SEQUENCE_BITS = 12;
    
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    
    private static final int WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final int DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final int TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
}
```

### UUID v7 구조
```java
// 128 bits UUID v7
// 48 bits: Unix timestamp (milliseconds)
// 4 bits: version (7)
// 12 bits: random
// 2 bits: variant (10)
// 62 bits: random

public class UUIDv7Generator {
    public UUID generate() {
        long timestamp = System.currentTimeMillis();
        
        // Most significant bits: timestamp + version
        long msb = (timestamp << 16) | (7L << 12) | (random12bits());
        
        // Least significant bits: variant + random
        long lsb = (0b10L << 62) | random62bits();
        
        return new UUID(msb, lsb);
    }
}
```

### ULID 인코딩
```java
// Crockford Base32
// 0123456789ABCDEFGHJKMNPQRSTVWXYZ (I, L, O, U 제외)

public class ULIDGenerator {
    private static final char[] ENCODING = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();
    
    public String generate() {
        long timestamp = System.currentTimeMillis();
        byte[] randomness = new byte[10];
        secureRandom.nextBytes(randomness);
        
        StringBuilder sb = new StringBuilder(26);
        // 타임스탬프 인코딩 (10자)
        encodeTimestamp(sb, timestamp);
        // 랜덤 인코딩 (16자)
        encodeRandomness(sb, randomness);
        
        return sb.toString();
    }
}
```

---

## 💡 힌트

### Snowflake 기본 구현
```java
public class SnowflakeGenerator {
    private final long workerId;
    private final long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    
    public synchronized long generate() {
        long timestamp = System.currentTimeMillis();
        
        if (timestamp < lastTimestamp) {
            throw new ClockMovedBackwardsException();
        }
        
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // 다음 밀리초까지 대기
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        
        lastTimestamp = timestamp;
        
        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
             | (datacenterId << DATACENTER_ID_SHIFT)
             | (workerId << WORKER_ID_SHIFT)
             | sequence;
    }
}
```

### ULID 기본 구현
```java
public class ULIDGenerator {
    private final SecureRandom random = new SecureRandom();
    private long lastTimestamp = -1L;
    private byte[] lastRandomness = new byte[10];
    
    public synchronized String generate() {
        long timestamp = System.currentTimeMillis();
        
        if (timestamp == lastTimestamp) {
            // 같은 밀리초 내에서 랜덤 부분 증가
            incrementRandomness();
        } else {
            // 새 랜덤값
            random.nextBytes(lastRandomness);
            lastTimestamp = timestamp;
        }
        
        return encode(timestamp, lastRandomness);
    }
}
```

---

## ✅ 체크리스트

- [ ] Snowflake ID 생성
- [ ] Snowflake ID 파싱
- [ ] UUID v4 생성
- [ ] UUID v7 생성
- [ ] ULID 생성
- [ ] ULID 파싱
- [ ] 시계 역행 처리
- [ ] 스레드 안전성

---

## 📚 참고

- Twitter Snowflake
- RFC 4122 (UUID)
- RFC 9562 (UUID v7)
- ULID Specification
