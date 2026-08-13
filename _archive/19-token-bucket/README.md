# 19. 토큰 버킷 (Token Bucket)

## 📋 문제 정의

**API 속도 제한(Rate Limiting)**을 위한 토큰 버킷 알고리즘을 구현하세요.

토큰 버킷은 일정한 속도로 토큰이 버킷에 추가되고, 요청마다 토큰을 소비하는 방식으로
트래픽을 제어합니다. 버스트(순간적 급증) 트래픽도 버킷 용량 내에서 허용합니다.

---

## 🎯 학습 목표

- 토큰 버킷 알고리즘 원리
- 속도 제한(Rate Limiting) 개념
- 버스트 트래픽 처리
- 시간 기반 토큰 리필
- Leaky Bucket과의 비교

---

## 📝 요구사항

### 핵심 개념

| 개념 | 설명 |
|------|------|
| **Bucket Capacity** | 버킷이 담을 수 있는 최대 토큰 수 |
| **Refill Rate** | 토큰이 추가되는 속도 (tokens/sec) |
| **Token** | 요청 처리를 위해 소비되는 단위 |

### 기본 연산

| 메서드 | 설명 |
|--------|------|
| `tryConsume(tokens)` | 토큰 소비 시도, 성공/실패 반환 |
| `consume(tokens)` | 토큰 소비, 부족하면 대기 |
| `getAvailableTokens()` | 현재 사용 가능한 토큰 수 |
| `tryConsumeAndReturnDelay(tokens)` | 실패 시 대기 시간 반환 |

### 설정 옵션

| 설정 | 설명 | 예시 |
|------|------|------|
| `capacity` | 버킷 최대 용량 | 100 |
| `refillTokens` | 리필당 추가 토큰 수 | 10 |
| `refillPeriod` | 리필 주기 | 1초 |

---

## 📊 입출력 예시

### 예제 1: 기본 사용
```java
// 초당 10개 토큰, 최대 100개
TokenBucket bucket = TokenBucket.builder()
    .capacity(100)
    .refillTokens(10)
    .refillPeriod(Duration.ofSeconds(1))
    .build();

// 토큰 소비
boolean allowed = bucket.tryConsume(1);  // true
System.out.println(bucket.getAvailableTokens());  // 99

// 대량 소비
allowed = bucket.tryConsume(50);  // true
System.out.println(bucket.getAvailableTokens());  // 49

// 초과 시도
allowed = bucket.tryConsume(100);  // false (토큰 부족)
```

### 예제 2: 버스트 트래픽
```
시나리오: capacity=100, refill=10/sec

초기: 100 토큰

t=0s: 80 요청 → 허용 (남은 토큰: 20)
t=0s: 30 요청 → 거부 (토큰 부족)
t=1s: 리필 → 30 토큰 (20 + 10)
t=1s: 25 요청 → 허용 (남은 토큰: 5)
t=2s: 리필 → 15 토큰 (5 + 10)
...
```

### 예제 3: 시간 경과에 따른 리필
```java
TokenBucket bucket = new TokenBucket(10, 1, Duration.ofSeconds(1));
// 10 capacity, 1 token/sec

bucket.tryConsume(10);  // 모든 토큰 소비
System.out.println(bucket.getAvailableTokens());  // 0

Thread.sleep(3000);  // 3초 대기

System.out.println(bucket.getAvailableTokens());  // 3 (3초 동안 리필)
```

### 예제 4: 대기 시간 계산
```java
TokenBucket bucket = new TokenBucket(10, 2, Duration.ofSeconds(1));
bucket.tryConsume(10);  // 모두 소비

// 5개 토큰 필요, 현재 0개
long waitMs = bucket.tryConsumeAndReturnDelay(5);
// 2.5초 대기 필요 (5 tokens / 2 tokens per sec)
System.out.println(waitMs);  // 2500
```

---

## 🔍 핵심 개념

### 토큰 버킷 vs Leaky Bucket
```
Token Bucket:
┌─────────────┐
│ ○ ○ ○ ○ ○   │  ← 토큰이 일정 속도로 추가
│ ○ ○ ○       │
│ ○           │
└─────┬───────┘
      │ 요청마다 토큰 소비
      ▼
   [요청 처리]

→ 버스트 허용 (버킷 용량만큼)
→ 순간적 트래픽 급증 처리 가능


Leaky Bucket:
┌─────────────┐
│ 요청 요청   │  ← 요청이 버킷에 쌓임
│ 요청 요청   │
│ 요청        │
└─────┬───────┘
      │ 일정 속도로 처리
      ▼
   [요청 처리]

→ 출력 속도 일정
→ 버스트 흡수, 평탄화
```

### 토큰 리필 방식
```
방식 1: 지연 리필 (Lazy Refill)
- 요청 시점에 경과 시간 계산
- 경과 시간에 비례해 토큰 추가
- 타이머 불필요, 효율적

방식 2: 주기적 리필 (Periodic Refill)
- 별도 스레드/타이머로 주기적 추가
- 실시간성 높음
- 리소스 더 사용
```

---

## 💡 힌트

### 지연 리필 구현
```java
public class TokenBucket {
    private final long capacity;
    private final long refillTokens;
    private final Duration refillPeriod;
    
    private long availableTokens;
    private Instant lastRefillTime;
    
    public TokenBucket(long capacity, long refillTokens, Duration refillPeriod) {
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillPeriod = refillPeriod;
        this.availableTokens = capacity;
        this.lastRefillTime = Instant.now();
    }
    
    public synchronized boolean tryConsume(long tokens) {
        refill();  // 먼저 리필
        
        if (availableTokens >= tokens) {
            availableTokens -= tokens;
            return true;
        }
        return false;
    }
    
    private void refill() {
        Instant now = Instant.now();
        Duration elapsed = Duration.between(lastRefillTime, now);
        
        // 경과 시간에 비례한 토큰 계산
        long periods = elapsed.toMillis() / refillPeriod.toMillis();
        long tokensToAdd = periods * refillTokens;
        
        if (tokensToAdd > 0) {
            availableTokens = Math.min(capacity, availableTokens + tokensToAdd);
            lastRefillTime = lastRefillTime.plus(
                refillPeriod.multipliedBy(periods));
        }
    }
}
```

### 대기 시간 계산
```java
public long tryConsumeAndReturnDelay(long tokens) {
    refill();
    
    if (availableTokens >= tokens) {
        availableTokens -= tokens;
        return 0;  // 즉시 가능
    }
    
    // 필요한 토큰 수
    long tokensNeeded = tokens - availableTokens;
    
    // 필요한 리필 주기 수
    long periodsNeeded = (tokensNeeded + refillTokens - 1) / refillTokens;
    
    // 대기 시간 (밀리초)
    return periodsNeeded * refillPeriod.toMillis();
}
```

---

## ✅ 체크리스트

- [ ] 기본 tryConsume 구현
- [ ] 지연 리필 구현
- [ ] 대기 시간 계산
- [ ] 블로킹 consume 구현
- [ ] 스레드 안전성
- [ ] Builder 패턴
- [ ] Leaky Bucket 변형 (선택)

---

## 📚 참고

- API Gateway Rate Limiting
- AWS API Gateway, Stripe API 등에서 사용
- Guava RateLimiter
- Bucket4j 라이브러리
