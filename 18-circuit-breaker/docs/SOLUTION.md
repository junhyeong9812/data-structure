# 서킷 브레이커 풀이 해설

## 📌 핵심 아이디어

서킷 브레이커는 **장애 전파 방지** 패턴입니다.
외부 서비스 장애 시 빠른 실패(Fail Fast)로 시스템을 보호합니다.

**핵심 원리**:
- 연속 실패 감지 → 회로 차단
- 차단 상태에서 즉시 실패 반환
- 주기적으로 복구 테스트

---

## 🔑 핵심 개념

### 1. 상태 다이어그램
```
         ┌─────────────────────┐
         │                     │
         ▼                     │
    ┌─────────┐           ┌─────────┐
    │ CLOSED  │──실패율──▶│  OPEN   │
    │ (정상)   │  초과     │ (차단)   │
    └─────────┘           └─────────┘
         ▲                     │
         │                     │ 대기시간
    성공  │                     │ 경과
         │    ┌──────────┐     │
         └────│HALF_OPEN │◀────┘
              │ (테스트)  │
              └──────────┘
                   │
              실패 │
                   ▼
              ┌─────────┐
              │  OPEN   │
              └─────────┘
```

### 2. 슬라이딩 윈도우
```java
// 카운트 기반 슬라이딩 윈도우
class CountBasedWindow {
    private boolean[] results;  // 최근 N개 결과
    private int index = 0;
    
    void record(boolean success) {
        results[index] = success;
        index = (index + 1) % results.length;
    }
    
    double getFailureRate() {
        int failures = 0;
        for (boolean r : results) {
            if (!r) failures++;
        }
        return (double) failures / results.length * 100;
    }
}

// 시간 기반 슬라이딩 윈도우
class TimeBasedWindow {
    private Deque<CallResult> results = new LinkedList<>();
    private Duration windowDuration;
    
    void record(boolean success) {
        results.addLast(new CallResult(Instant.now(), success));
        removeOldEntries();
    }
    
    private void removeOldEntries() {
        Instant cutoff = Instant.now().minus(windowDuration);
        while (!results.isEmpty() && 
               results.peekFirst().timestamp.isBefore(cutoff)) {
            results.pollFirst();
        }
    }
}
```

### 3. 실패율 계산
```
윈도우 크기: 10
최소 호출 수: 5 (이하면 실패율 계산 안 함)

호출 기록: [S, F, S, F, F, S, F, S, F, F]
실패 수: 6
실패율: 60%

임계값 50% 초과 → OPEN 전환
```

---

## 📝 POP 구현 해설

### 기본 구현
```java
public class CircuitBreaker {
    
    public enum State {
        CLOSED, OPEN, HALF_OPEN
    }
    
    private volatile State state = State.CLOSED;
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger halfOpenCalls = new AtomicInteger(0);
    private volatile Instant lastStateTransition = Instant.now();
    
    private final int failureThreshold;
    private final double failureRateThreshold;
    private final Duration waitDurationInOpenState;
    private final int permittedCallsInHalfOpen;
    private final int slidingWindowSize;
    
    // 슬라이딩 윈도우
    private final boolean[] callResults;
    private final AtomicInteger callIndex = new AtomicInteger(0);
    private final AtomicInteger totalCalls = new AtomicInteger(0);
    
    public CircuitBreaker(CircuitBreakerConfig config) {
        this.failureThreshold = config.failureThreshold;
        this.failureRateThreshold = config.failureRateThreshold;
        this.waitDurationInOpenState = config.waitDurationInOpenState;
        this.permittedCallsInHalfOpen = config.permittedCallsInHalfOpen;
        this.slidingWindowSize = config.slidingWindowSize;
        this.callResults = new boolean[slidingWindowSize];
        Arrays.fill(callResults, true);  // 초기값은 성공
    }
    
    public <T> T execute(Supplier<T> supplier) {
        // 상태 확인 및 전이
        State currentState = checkState();
        
        if (currentState == State.OPEN) {
            throw new CircuitBreakerOpenException("Circuit breaker is OPEN");
        }
        
        if (currentState == State.HALF_OPEN) {
            if (halfOpenCalls.incrementAndGet() > permittedCallsInHalfOpen) {
                throw new CircuitBreakerOpenException(
                    "Too many calls in HALF_OPEN state");
            }
        }
        
        try {
            T result = supplier.get();
            recordSuccess();
            return result;
        } catch (Exception e) {
            recordFailure();
            throw e;
        }
    }
    
    private State checkState() {
        if (state == State.OPEN) {
            if (shouldTransitionToHalfOpen()) {
                transitionTo(State.HALF_OPEN);
            }
        }
        return state;
    }
    
    private boolean shouldTransitionToHalfOpen() {
        return Duration.between(lastStateTransition, Instant.now())
            .compareTo(waitDurationInOpenState) >= 0;
    }
    
    private synchronized void transitionTo(State newState) {
        if (state != newState) {
            state = newState;
            lastStateTransition = Instant.now();
            
            if (newState == State.CLOSED) {
                reset();
            } else if (newState == State.HALF_OPEN) {
                halfOpenCalls.set(0);
                successCount.set(0);
            }
        }
    }
    
    private void recordSuccess() {
        recordCall(true);
        successCount.incrementAndGet();
        
        if (state == State.HALF_OPEN) {
            if (successCount.get() >= permittedCallsInHalfOpen) {
                transitionTo(State.CLOSED);
            }
        }
    }
    
    private void recordFailure() {
        recordCall(false);
        failureCount.incrementAndGet();
        
        if (state == State.HALF_OPEN) {
            transitionTo(State.OPEN);
        } else if (state == State.CLOSED) {
            if (shouldOpen()) {
                transitionTo(State.OPEN);
            }
        }
    }
    
    private void recordCall(boolean success) {
        int idx = callIndex.getAndUpdate(i -> (i + 1) % slidingWindowSize);
        callResults[idx] = success;
        totalCalls.incrementAndGet();
    }
    
    private boolean shouldOpen() {
        // 최소 호출 수 이상일 때만 실패율 검사
        if (totalCalls.get() < slidingWindowSize) {
            return failureCount.get() >= failureThreshold;
        }
        return getFailureRate() >= failureRateThreshold;
    }
    
    public double getFailureRate() {
        int failures = 0;
        int count = Math.min(totalCalls.get(), slidingWindowSize);
        for (int i = 0; i < count; i++) {
            if (!callResults[i]) failures++;
        }
        return count > 0 ? (double) failures / count * 100 : 0;
    }
    
    private void reset() {
        failureCount.set(0);
        successCount.set(0);
        totalCalls.set(0);
        Arrays.fill(callResults, true);
    }
    
    public State getState() {
        checkState();  // 상태 전이 확인
        return state;
    }
}
```

### Fallback 지원
```java
public <T> T executeWithFallback(
        Supplier<T> supplier, 
        Function<Exception, T> fallback) {
    try {
        return execute(supplier);
    } catch (CircuitBreakerOpenException e) {
        return fallback.apply(e);
    } catch (Exception e) {
        return fallback.apply(e);
    }
}
```

### 메트릭스
```java
public record Metrics(
    long totalCalls,
    long successCount,
    long failureCount,
    double failureRate,
    State currentState,
    Instant lastStateTransition
) {
    public static Metrics from(CircuitBreaker cb) {
        return new Metrics(
            cb.getTotalCalls(),
            cb.getSuccessCount(),
            cb.getFailureCount(),
            cb.getFailureRate(),
            cb.getState(),
            cb.getLastStateTransition()
        );
    }
}
```

---

## 📝 스레드 안전 구현
```java
public class ThreadSafeCircuitBreaker {
    private final ReentrantLock lock = new ReentrantLock();
    private volatile State state = State.CLOSED;
    
    public <T> T execute(Supplier<T> supplier) {
        // 상태 확인 (락 없이)
        if (state == State.OPEN && !shouldTransitionToHalfOpen()) {
            throw new CircuitBreakerOpenException();
        }
        
        // 상태 전이 (락 필요)
        lock.lock();
        try {
            if (state == State.OPEN && shouldTransitionToHalfOpen()) {
                transitionTo(State.HALF_OPEN);
            }
            
            if (state == State.HALF_OPEN && 
                halfOpenCalls.get() >= permittedCallsInHalfOpen) {
                throw new CircuitBreakerOpenException();
            }
            
            halfOpenCalls.incrementAndGet();
        } finally {
            lock.unlock();
        }
        
        // 실제 호출 (락 해제 후)
        try {
            T result = supplier.get();
            recordSuccess();
            return result;
        } catch (Exception e) {
            recordFailure();
            throw e;
        }
    }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 | 공간복잡도 |
|------|-----------|-----------|
| execute | O(1) | O(1) |
| getFailureRate | O(w) | O(1) |
| transitionTo | O(w) | O(1) |

w = 슬라이딩 윈도우 크기

---

## ❌ 흔한 실수

### 1. 동시성 문제
```java
// 잘못됨: 상태 체크와 전이 사이 경쟁 조건
if (state == State.OPEN && shouldTransitionToHalfOpen()) {
    state = State.HALF_OPEN;  // 다른 스레드도 동시에 실행 가능
}

// 올바름: 동기화
synchronized (this) {
    if (state == State.OPEN && shouldTransitionToHalfOpen()) {
        transitionTo(State.HALF_OPEN);
    }
}
```

### 2. 타임스탬프 갱신 누락
```java
// 잘못됨: 상태 전이 시 시간 기록 누락
private void transitionTo(State newState) {
    state = newState;
    // lastStateTransition 갱신 안 함!
}

// 올바름
private void transitionTo(State newState) {
    state = newState;
    lastStateTransition = Instant.now();
}
```

### 3. HALF_OPEN 호출 제한
```java
// 잘못됨: 제한 없이 모든 호출 허용
if (state == State.HALF_OPEN) {
    // 그냥 진행
}

// 올바름: 호출 수 제한
if (state == State.HALF_OPEN) {
    if (halfOpenCalls.incrementAndGet() > permittedCallsInHalfOpen) {
        throw new CircuitBreakerOpenException();
    }
}
```

---

## 🔗 관련 문제

- 분산 시스템 설계
- API Gateway 패턴
- Bulkhead 패턴
- Retry 패턴
- Timeout 패턴
