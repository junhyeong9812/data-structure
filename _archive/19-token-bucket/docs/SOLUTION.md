# 토큰 버킷 풀이 해설

## 📌 핵심 아이디어

토큰 버킷은 **속도 제한**을 구현하는 가장 널리 사용되는 알고리즘입니다.
일정 속도로 토큰이 채워지고, 요청마다 토큰을 소비합니다.

**핵심 특징**:
- 버스트 트래픽 허용 (버킷 용량만큼)
- 평균 속도 제한 보장
- 지연 리필로 효율적 구현

---

## 🔑 핵심 개념

### 1. 토큰 리필 계산
```
경과 시간 기반 리필:

마지막 리필: t0
현재 시간: t1
경과 시간: Δt = t1 - t0

리필할 토큰 = floor(Δt / refillPeriod) * refillTokens

예:
- refillPeriod = 1초
- refillTokens = 10
- Δt = 2.5초

리필 = floor(2.5) * 10 = 20 토큰
```

### 2. 소비 결정
```
요청: n개 토큰 필요

if (availableTokens >= n) {
    availableTokens -= n;
    return ALLOWED;
} else {
    return DENIED;
}
```

### 3. 대기 시간 계산
```
필요 토큰: n
현재 토큰: available
부족 토큰: needed = n - available

대기 시간 = ceil(needed / refillTokens) * refillPeriod

예:
- needed = 15 토큰
- refillTokens = 10/초

대기 시간 = ceil(15/10) * 1초 = 2초
```

---

## 📝 POP 구현 해설

### 기본 구현
```java
public class TokenBucket {
    private final long capacity;
    private final long refillTokens;
    private final long refillPeriodNanos;
    
    private long availableTokens;
    private long lastRefillNanos;
    
    public TokenBucket(long capacity, long refillTokens, Duration refillPeriod) {
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillPeriodNanos = refillPeriod.toNanos();
        this.availableTokens = capacity;
        this.lastRefillNanos = System.nanoTime();
    }
    
    public synchronized boolean tryConsume(long tokens) {
        if (tokens <= 0) {
            throw new IllegalArgumentException("Tokens must be positive");
        }
        
        refill();
        
        if (availableTokens >= tokens) {
            availableTokens -= tokens;
            return true;
        }
        return false;
    }
    
    private void refill() {
        long now = System.nanoTime();
        long elapsed = now - lastRefillNanos;
        
        if (elapsed < refillPeriodNanos) {
            return;  // 아직 리필 시간 안 됨
        }
        
        long periods = elapsed / refillPeriodNanos;
        long tokensToAdd = periods * refillTokens;
        
        availableTokens = Math.min(capacity, availableTokens + tokensToAdd);
        lastRefillNanos += periods * refillPeriodNanos;
    }
    
    public synchronized long getAvailableTokens() {
        refill();
        return availableTokens;
    }
}
```

### 대기 시간 반환
```java
public synchronized long tryConsumeAndReturnDelay(long tokens) {
    refill();
    
    if (availableTokens >= tokens) {
        availableTokens -= tokens;
        return 0;
    }
    
    long tokensNeeded = tokens - availableTokens;
    long periodsNeeded = (tokensNeeded + refillTokens - 1) / refillTokens;
    
    return periodsNeeded * refillPeriodNanos / 1_000_000;  // 밀리초로 반환
}

// Duration으로 반환
public synchronized Duration tryConsumeAndReturnWait(long tokens) {
    long delayMs = tryConsumeAndReturnDelay(tokens);
    return Duration.ofMillis(delayMs);
}
```

### 블로킹 소비
```java
public void consume(long tokens) throws InterruptedException {
    while (true) {
        synchronized (this) {
            refill();
            
            if (availableTokens >= tokens) {
                availableTokens -= tokens;
                return;
            }
            
            // 대기 시간 계산
            long tokensNeeded = tokens - availableTokens;
            long periodsNeeded = (tokensNeeded + refillTokens - 1) / refillTokens;
            long waitNanos = periodsNeeded * refillPeriodNanos;
            long waitMillis = waitNanos / 1_000_000;
            
            // 대기
            wait(waitMillis);
        }
    }
}

// 타임아웃 버전
public boolean tryConsume(long tokens, Duration timeout) 
        throws InterruptedException {
    long deadline = System.nanoTime() + timeout.toNanos();
    
    while (true) {
        synchronized (this) {
            refill();
            
            if (availableTokens >= tokens) {
                availableTokens -= tokens;
                return true;
            }
            
            long remaining = deadline - System.nanoTime();
            if (remaining <= 0) {
                return false;  // 타임아웃
            }
            
            long waitMillis = Math.min(
                remaining / 1_000_000,
                refillPeriodNanos / 1_000_000
            );
            
            wait(Math.max(1, waitMillis));
        }
    }
}
```

### 다중 버킷 (계층적 제한)
```java
public class MultiBucketRateLimiter {
    private final TokenBucket[] buckets;
    
    public MultiBucketRateLimiter(TokenBucket... buckets) {
        this.buckets = buckets;
    }
    
    public boolean tryConsume(long tokens) {
        // 모든 버킷에서 소비 가능해야 함
        for (TokenBucket bucket : buckets) {
            if (bucket.getAvailableTokens() < tokens) {
                return false;
            }
        }
        
        // 모두 소비
        for (TokenBucket bucket : buckets) {
            bucket.tryConsume(tokens);
        }
        return true;
    }
}

// 사용 예: 초당 10개 + 분당 100개 제한
MultiBucketRateLimiter limiter = new MultiBucketRateLimiter(
    new TokenBucket(10, 10, Duration.ofSeconds(1)),   // 초당
    new TokenBucket(100, 100, Duration.ofMinutes(1))  // 분당
);
```

---

## 📝 Leaky Bucket 구현
```java
public class LeakyBucket {
    private final long capacity;
    private final long leakRatePerSecond;
    
    private long water = 0;
    private long lastLeakNanos;
    
    public LeakyBucket(long capacity, long leakRatePerSecond) {
        this.capacity = capacity;
        this.leakRatePerSecond = leakRatePerSecond;
        this.lastLeakNanos = System.nanoTime();
    }
    
    public synchronized boolean tryAdd(long amount) {
        leak();
        
        if (water + amount <= capacity) {
            water += amount;
            return true;
        }
        return false;  // 버킷 넘침
    }
    
    private void leak() {
        long now = System.nanoTime();
        long elapsed = now - lastLeakNanos;
        
        long leaked = elapsed * leakRatePerSecond / 1_000_000_000L;
        
        if (leaked > 0) {
            water = Math.max(0, water - leaked);
            lastLeakNanos = now;
        }
    }
    
    public synchronized long getCurrentWater() {
        leak();
        return water;
    }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 | 공간복잡도 |
|------|-----------|-----------|
| tryConsume | O(1) | O(1) |
| refill | O(1) | O(1) |
| getAvailableTokens | O(1) | O(1) |

### Token Bucket vs Leaky Bucket

| 특성 | Token Bucket | Leaky Bucket |
|------|-------------|--------------|
| 버스트 허용 | ○ (용량만큼) | × |
| 출력 속도 | 가변 | 일정 |
| 구현 복잡도 | 낮음 | 낮음 |
| 사용 사례 | API 제한 | 네트워크 정형화 |

---

## ❌ 흔한 실수

### 1. 시간 단위 혼동
```java
// 잘못됨: 밀리초와 나노초 혼동
long elapsed = now - lastRefillNanos;  // 나노초
long periods = elapsed / refillPeriodMillis;  // 밀리초!

// 올바름: 단위 통일
long periodNanos = refillPeriod.toNanos();
long periods = elapsed / periodNanos;
```

### 2. 오버플로우
```java
// 잘못됨: 큰 경과 시간에서 오버플로우
long tokensToAdd = elapsed * refillTokens / refillPeriodNanos;

// 올바름: 먼저 나누기
long periods = elapsed / refillPeriodNanos;
long tokensToAdd = periods * refillTokens;
```

### 3. 동시성 문제
```java
// 잘못됨: 원자성 없음
if (availableTokens >= tokens) {  // 체크
    // 다른 스레드가 끼어들 수 있음!
    availableTokens -= tokens;    // 소비
}

// 올바름: synchronized
public synchronized boolean tryConsume(long tokens) {
    refill();
    if (availableTokens >= tokens) {
        availableTokens -= tokens;
        return true;
    }
    return false;
}
```

### 4. 리필 시간 업데이트
```java
// 잘못됨: 현재 시간으로 업데이트
lastRefillNanos = now;  // 누적 오차 발생!

// 올바름: 정확한 주기만큼 증가
lastRefillNanos += periods * refillPeriodNanos;
```

---

## 🔗 관련 문제

- LeetCode 359: Logger Rate Limiter
- API Gateway 설계
- 분산 Rate Limiter (Redis 기반)
- 슬라이딩 윈도우 Rate Limiter
