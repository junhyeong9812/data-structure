# 18. 서킷 브레이커 (Circuit Breaker)

## 📋 문제 정의

**분산 시스템에서 장애 전파를 방지**하는 서킷 브레이커 패턴을 구현하세요.

서킷 브레이커는 전기 회로의 차단기처럼 동작하여, 외부 서비스 호출 실패가 
연속으로 발생하면 회로를 열어(Open) 추가 호출을 차단하고, 
시스템이 복구되면 다시 닫습니다(Closed).

---

## 🎯 학습 목표

- 서킷 브레이커의 3가지 상태 이해
- 상태 전이(State Transition) 로직
- 실패율 기반 회로 개폐
- 타임아웃과 재시도 처리
- 분산 시스템의 장애 격리 개념

---

## 📝 요구사항

### 3가지 상태

| 상태 | 설명 |
|------|------|
| **CLOSED** | 정상 상태, 요청 통과 |
| **OPEN** | 차단 상태, 요청 즉시 실패 |
| **HALF_OPEN** | 테스트 상태, 일부 요청만 허용 |

### 상태 전이
```
CLOSED → OPEN: 실패율이 임계값 초과
OPEN → HALF_OPEN: 대기 시간 경과 후
HALF_OPEN → CLOSED: 테스트 요청 성공
HALF_OPEN → OPEN: 테스트 요청 실패
```

### 기본 연산

| 메서드 | 설명 |
|--------|------|
| `execute(Supplier<T>)` | 보호된 작업 실행 |
| `getState()` | 현재 상태 반환 |
| `getFailureRate()` | 현재 실패율 |
| `getMetrics()` | 성공/실패 통계 |

### 설정 옵션

| 설정 | 설명 | 기본값 |
|------|------|--------|
| `failureThreshold` | OPEN 전환 실패 횟수 | 5 |
| `failureRateThreshold` | OPEN 전환 실패율(%) | 50 |
| `waitDurationInOpenState` | OPEN 유지 시간 | 60초 |
| `permittedCallsInHalfOpen` | HALF_OPEN에서 허용 호출 수 | 3 |
| `slidingWindowSize` | 실패율 계산 윈도우 크기 | 10 |

---

## 📊 입출력 예시

### 예제 1: 기본 사용
```java
CircuitBreaker cb = CircuitBreaker.builder()
    .failureThreshold(3)
    .waitDurationInOpenState(Duration.ofSeconds(30))
    .build();

// 정상 호출
String result = cb.execute(() -> callExternalService());

// 실패 발생 시
try {
    cb.execute(() -> failingService());
} catch (Exception e) {
    // 예외 처리
}
```

### 예제 2: 상태 전이 시나리오
```
초기 상태: CLOSED
  │
  │ 실패 1회
  │ 실패 2회  
  │ 실패 3회 (임계값 도달)
  ↓
상태: OPEN (30초간)
  │
  │ 30초 경과
  ↓
상태: HALF_OPEN
  │
  │ 테스트 호출 성공
  ↓
상태: CLOSED (정상 복귀)
```

### 예제 3: 메트릭스
```java
CircuitBreaker cb = new CircuitBreaker(config);

// 여러 호출 후
Metrics metrics = cb.getMetrics();

System.out.println("총 호출: " + metrics.totalCalls());
System.out.println("성공: " + metrics.successCount());
System.out.println("실패: " + metrics.failureCount());
System.out.println("실패율: " + metrics.failureRate() + "%");
System.out.println("현재 상태: " + cb.getState());
```

### 예제 4: Fallback 처리
```java
CircuitBreaker cb = new CircuitBreaker(config);

String result = cb.executeWithFallback(
    () -> callExternalService(),
    ex -> "Fallback 응답"  // 회로 열림 또는 실패 시
);
```

---

## 🔍 핵심 개념

### 슬라이딩 윈도우
```
최근 N개 요청의 실패율 계산:

호출 기록: [S, S, F, S, F, F, S, F, S, F]
           (S=성공, F=실패)
           
윈도우 크기: 10
실패 수: 5
실패율: 50%

임계값 50% → OPEN 전환!
```

### 상태별 동작
```
CLOSED:
  - 모든 요청 통과
  - 실패 기록
  - 실패율 모니터링

OPEN:
  - 모든 요청 즉시 거부 (CircuitBreakerOpenException)
  - 대기 시간 타이머 시작
  - 시스템 부하 감소

HALF_OPEN:
  - 제한된 수의 요청만 허용
  - 성공 → CLOSED
  - 실패 → OPEN
```

---

## 💡 힌트

### 기본 구조
```java
public class CircuitBreaker {
    private State state = State.CLOSED;
    private int failureCount = 0;
    private int successCount = 0;
    private Instant lastFailureTime;
    private final CircuitBreakerConfig config;
    
    public enum State {
        CLOSED, OPEN, HALF_OPEN
    }
    
    public <T> T execute(Supplier<T> supplier) {
        // 상태 확인
        if (state == State.OPEN) {
            if (shouldTransitionToHalfOpen()) {
                transitionTo(State.HALF_OPEN);
            } else {
                throw new CircuitBreakerOpenException();
            }
        }
        
        try {
            T result = supplier.get();
            onSuccess();
            return result;
        } catch (Exception e) {
            onFailure();
            throw e;
        }
    }
}
```

### 상태 전이
```java
private void onSuccess() {
    successCount++;
    if (state == State.HALF_OPEN) {
        if (successCount >= config.permittedCallsInHalfOpen) {
            transitionTo(State.CLOSED);
        }
    }
    // CLOSED에서는 실패 카운트 리셋 가능
}

private void onFailure() {
    failureCount++;
    lastFailureTime = Instant.now();
    
    if (state == State.HALF_OPEN) {
        transitionTo(State.OPEN);
    } else if (state == State.CLOSED) {
        if (shouldOpen()) {
            transitionTo(State.OPEN);
        }
    }
}

private boolean shouldOpen() {
    return failureCount >= config.failureThreshold
        || getFailureRate() >= config.failureRateThreshold;
}
```

---

## ✅ 체크리스트

- [ ] 3가지 상태 구현
- [ ] 상태 전이 로직
- [ ] 실패 횟수 기반 OPEN 전환
- [ ] 실패율 기반 OPEN 전환
- [ ] 슬라이딩 윈도우
- [ ] 타임아웃 처리
- [ ] Fallback 지원
- [ ] 메트릭스 수집
- [ ] 스레드 안전성

---

## 📚 참고

- Netflix Hystrix (현재 deprecated)
- Resilience4j (현재 표준)
- 마이크로서비스 아키텍처 패턴
- [Martin Fowler - Circuit Breaker](https://martinfowler.com/bliki/CircuitBreaker.html)
