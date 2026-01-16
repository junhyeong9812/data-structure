# 서킷 브레이커 구현에 유용한 Java API

## 📦 시간 관련

### java.time 패키지
```java
import java.time.Duration;
import java.time.Instant;

// 현재 시간
Instant now = Instant.now();

// 시간 간격
Duration waitTime = Duration.ofSeconds(30);
Duration.ofMinutes(5);
Duration.ofMillis(500);

// 경과 시간 확인
Instant lastTransition = Instant.now();
// ...
Duration elapsed = Duration.between(lastTransition, Instant.now());

if (elapsed.compareTo(waitTime) >= 0) {
    // 대기 시간 경과
}

// 또는
if (elapsed.toMillis() >= waitTime.toMillis()) {
    // 대기 시간 경과
}

// 시간 더하기/빼기
Instant future = now.plus(Duration.ofMinutes(5));
Instant past = now.minus(Duration.ofSeconds(30));
```

### 시간 측정
```java
// 나노초 정밀도
long startNano = System.nanoTime();
// ... 작업 수행
long endNano = System.nanoTime();
long elapsedNano = endNano - startNano;

// 밀리초
long startMillis = System.currentTimeMillis();
```

---

## 🔐 동시성 관련

### Atomic 클래스
```java
import java.util.concurrent.atomic.*;

// 원자적 정수
AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();  // ++counter
counter.getAndIncrement();  // counter++
counter.get();
counter.set(10);
counter.compareAndSet(expected, newValue);

// 원자적 참조
AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
state.get();
state.set(State.OPEN);
state.compareAndSet(State.OPEN, State.HALF_OPEN);

// 원자적 롱
AtomicLong totalCalls = new AtomicLong(0);

// 원자적 불린
AtomicBoolean isOpen = new AtomicBoolean(false);
```

### ReentrantLock
```java
import java.util.concurrent.locks.ReentrantLock;

ReentrantLock lock = new ReentrantLock();

// 락 획득 및 해제
lock.lock();
try {
    // 임계 영역
} finally {
    lock.unlock();
}

// tryLock (타임아웃)
if (lock.tryLock(100, TimeUnit.MILLISECONDS)) {
    try {
        // 임계 영역
    } finally {
        lock.unlock();
    }
} else {
    // 락 획득 실패
}
```

### ReadWriteLock
```java
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

ReadWriteLock rwLock = new ReentrantReadWriteLock();

// 읽기 락 (여러 스레드 동시 가능)
rwLock.readLock().lock();
try {
    return state;
} finally {
    rwLock.readLock().unlock();
}

// 쓰기 락 (배타적)
rwLock.writeLock().lock();
try {
    state = newState;
} finally {
    rwLock.writeLock().unlock();
}
```

### volatile
```java
// 가시성 보장
private volatile State state = State.CLOSED;
private volatile Instant lastTransition = Instant.now();

// volatile은 복합 연산에는 부족!
// count++는 read-modify-write → AtomicInteger 사용
```

---

## 🎯 함수형 인터페이스

### Supplier
```java
import java.util.function.Supplier;

// 값을 공급하는 함수
Supplier<String> supplier = () -> callExternalService();

// 사용
public <T> T execute(Supplier<T> supplier) {
    return supplier.get();
}

// 호출
String result = execute(() -> httpClient.get("/api/data"));
```

### Function
```java
import java.util.function.Function;

// 입력 → 출력 변환
Function<Exception, String> fallback = ex -> "default";

// Fallback 처리
public <T> T executeWithFallback(
        Supplier<T> supplier, 
        Function<Exception, T> fallback) {
    try {
        return execute(supplier);
    } catch (Exception e) {
        return fallback.apply(e);
    }
}
```

### Consumer
```java
import java.util.function.Consumer;

// 상태 변경 리스너
Consumer<State> stateChangeListener = newState -> {
    System.out.println("State changed to: " + newState);
};

// 사용
private void transitionTo(State newState) {
    state = newState;
    if (stateChangeListener != null) {
        stateChangeListener.accept(newState);
    }
}
```

### Predicate
```java
import java.util.function.Predicate;

// 조건 검사
Predicate<Exception> recordFailurePredicate = ex -> 
    !(ex instanceof BusinessException);

// 특정 예외만 실패로 기록
private void recordFailure(Exception e) {
    if (recordFailurePredicate.test(e)) {
        failureCount.incrementAndGet();
    }
}
```

---

## 📊 컬렉션

### 슬라이딩 윈도우용
```java
import java.util.Deque;
import java.util.LinkedList;
import java.util.ArrayDeque;

// 시간 기반 슬라이딩 윈도우
Deque<CallRecord> records = new LinkedList<>();

record CallRecord(Instant timestamp, boolean success) {}

void addRecord(boolean success) {
    records.addLast(new CallRecord(Instant.now(), success));
    removeOld();
}

void removeOld() {
    Instant cutoff = Instant.now().minus(windowDuration);
    while (!records.isEmpty() && 
           records.peekFirst().timestamp().isBefore(cutoff)) {
        records.pollFirst();
    }
}
```

### 카운트 기반 윈도우
```java
// 원형 버퍼
boolean[] results = new boolean[windowSize];
int index = 0;

void record(boolean success) {
    results[index] = success;
    index = (index + 1) % windowSize;
}

double getFailureRate() {
    int failures = 0;
    for (boolean r : results) {
        if (!r) failures++;
    }
    return (double) failures / results.length * 100;
}
```

---

## 🧪 테스트 관련

### AssertJ 사용
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldTransitionToOpen() {
    CircuitBreaker cb = new CircuitBreaker(config);
    
    // 실패 발생시키기
    for (int i = 0; i < 5; i++) {
        try {
            cb.execute(() -> { throw new RuntimeException(); });
        } catch (Exception ignored) {}
    }
    
    assertThat(cb.getState()).isEqualTo(State.OPEN);
}

@Test
void shouldBlockCallsWhenOpen() {
    CircuitBreaker cb = createOpenCircuitBreaker();
    
    assertThatThrownBy(() -> cb.execute(() -> "test"))
        .isInstanceOf(CircuitBreakerOpenException.class);
}
```

### 시간 제어 테스트
```java
// Clock 주입으로 시간 제어
public class CircuitBreaker {
    private final Clock clock;
    
    public CircuitBreaker(CircuitBreakerConfig config, Clock clock) {
        this.clock = clock;
    }
    
    private Instant now() {
        return clock.instant();
    }
}

// 테스트
@Test
void shouldTransitionToHalfOpenAfterWait() {
    Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
    CircuitBreaker cb = new CircuitBreaker(config, fixedClock);
    
    // OPEN 상태로 만들기
    // ...
    
    // 시간 이동
    Clock advancedClock = Clock.fixed(
        Instant.now().plus(Duration.ofSeconds(31)), 
        ZoneId.systemDefault()
    );
    cb.setClock(advancedClock);
    
    assertThat(cb.getState()).isEqualTo(State.HALF_OPEN);
}
```

---

## 📚 Java 21 관련

### Record
```java
// 설정
public record CircuitBreakerConfig(
    int failureThreshold,
    double failureRateThreshold,
    Duration waitDurationInOpenState,
    int permittedCallsInHalfOpen,
    int slidingWindowSize
) {
    public CircuitBreakerConfig {
        if (failureThreshold < 1) 
            throw new IllegalArgumentException();
    }
    
    public static CircuitBreakerConfig defaults() {
        return new CircuitBreakerConfig(5, 50.0, 
            Duration.ofSeconds(60), 3, 10);
    }
}

// 메트릭스
public record Metrics(
    long totalCalls,
    long successCount,
    long failureCount,
    double failureRate,
    State currentState
) {}

// 호출 기록
public record CallRecord(Instant timestamp, boolean success) {}
```

### Sealed Classes
```java
public sealed interface CircuitBreakerState 
    permits ClosedState, OpenState, HalfOpenState {
    
    boolean allowsRequest();
    CircuitBreakerState onSuccess();
    CircuitBreakerState onFailure();
}

public final class ClosedState implements CircuitBreakerState {
    @Override public boolean allowsRequest() { return true; }
    // ...
}

public final class OpenState implements CircuitBreakerState {
    private final Instant openedAt;
    @Override public boolean allowsRequest() { return false; }
    // ...
}

public final class HalfOpenState implements CircuitBreakerState {
    private final int attemptCount;
    @Override public boolean allowsRequest() { 
        return attemptCount < maxAttempts; 
    }
    // ...
}
```

### Pattern Matching
```java
public void handleState(CircuitBreakerState state) {
    switch (state) {
        case ClosedState s -> System.out.println("Closed");
        case OpenState s -> System.out.println("Open since: " + s.openedAt());
        case HalfOpenState s -> System.out.println("Testing...");
    }
}
```

---

## ⚡ 성능 팁

### 1. volatile vs Atomic
```java
// 단순 읽기/쓰기 → volatile
private volatile State state;

// 복합 연산 → Atomic
private final AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();  // atomic
```

### 2. 락 최소화
```java
// 잘못됨: 전체 메서드에 락
public synchronized <T> T execute(Supplier<T> supplier) {
    // 외부 호출까지 락 유지 (나쁨!)
    return supplier.get();
}

// 올바름: 상태 변경에만 락
public <T> T execute(Supplier<T> supplier) {
    synchronized (this) {
        checkAndTransitionState();
    }
    // 락 해제 후 외부 호출
    return supplier.get();
}
```

### 3. 불변 메트릭스
```java
// 스냅샷으로 반환 (스레드 안전)
public Metrics getMetrics() {
    return new Metrics(
        totalCalls.get(),
        successCount.get(),
        failureCount.get(),
        getFailureRate(),
        state
    );
}
```

---

## 🔀 Builder 패턴
```java
public class CircuitBreaker {
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private int failureThreshold = 5;
        private double failureRateThreshold = 50.0;
        private Duration waitDuration = Duration.ofSeconds(60);
        private int permittedCallsInHalfOpen = 3;
        private int slidingWindowSize = 10;
        
        public Builder failureThreshold(int threshold) {
            this.failureThreshold = threshold;
            return this;
        }
        
        public Builder failureRateThreshold(double rate) {
            this.failureRateThreshold = rate;
            return this;
        }
        
        public Builder waitDurationInOpenState(Duration duration) {
            this.waitDuration = duration;
            return this;
        }
        
        public Builder permittedCallsInHalfOpen(int calls) {
            this.permittedCallsInHalfOpen = calls;
            return this;
        }
        
        public Builder slidingWindowSize(int size) {
            this.slidingWindowSize = size;
            return this;
        }
        
        public CircuitBreaker build() {
            return new CircuitBreaker(new CircuitBreakerConfig(
                failureThreshold,
                failureRateThreshold,
                waitDuration,
                permittedCallsInHalfOpen,
                slidingWindowSize
            ));
        }
    }
}

// 사용
CircuitBreaker cb = CircuitBreaker.builder()
    .failureThreshold(3)
    .waitDurationInOpenState(Duration.ofSeconds(30))
    .build();
```
