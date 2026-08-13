# 토큰 버킷 구현에 유용한 Java API

## 📦 시간 관련

### java.time 패키지
```java
import java.time.Duration;
import java.time.Instant;

// Duration 생성
Duration oneSecond = Duration.ofSeconds(1);
Duration halfSecond = Duration.ofMillis(500);
Duration tenMinutes = Duration.ofMinutes(10);

// Duration 연산
Duration doubled = oneSecond.multipliedBy(2);
Duration halved = oneSecond.dividedBy(2);

// Duration 변환
long millis = duration.toMillis();
long nanos = duration.toNanos();
long seconds = duration.getSeconds();

// Instant (시점)
Instant now = Instant.now();
Instant later = now.plus(Duration.ofSeconds(5));
Duration elapsed = Duration.between(start, end);
```

### System 시간
```java
// 나노초 (고정밀)
long startNano = System.nanoTime();
// ... 작업
long elapsedNano = System.nanoTime() - startNano;

// 밀리초 (epoch 기준)
long currentMillis = System.currentTimeMillis();

// 나노초가 더 정밀하고 오버플로우 걱정 적음
// (약 292년 후 오버플로우)
```

### 시간 단위 변환
```java
import java.util.concurrent.TimeUnit;

// 변환
long millis = TimeUnit.SECONDS.toMillis(5);      // 5000
long nanos = TimeUnit.MILLISECONDS.toNanos(100); // 100_000_000
long seconds = TimeUnit.MINUTES.toSeconds(2);    // 120

// sleep
TimeUnit.SECONDS.sleep(1);
TimeUnit.MILLISECONDS.sleep(500);
```

---

## 🔐 동시성

### synchronized
```java
// 메서드 레벨
public synchronized boolean tryConsume(long tokens) {
    // 한 번에 하나의 스레드만
}

// 블록 레벨
public boolean tryConsume(long tokens) {
    synchronized (this) {
        // 임계 영역
    }
}

// 객체 락
private final Object lock = new Object();
public boolean tryConsume(long tokens) {
    synchronized (lock) {
        // ...
    }
}
```

### ReentrantLock
```java
import java.util.concurrent.locks.ReentrantLock;

private final ReentrantLock lock = new ReentrantLock();

public boolean tryConsume(long tokens) {
    lock.lock();
    try {
        refill();
        if (availableTokens >= tokens) {
            availableTokens -= tokens;
            return true;
        }
        return false;
    } finally {
        lock.unlock();
    }
}

// tryLock (비블로킹)
if (lock.tryLock()) {
    try {
        // ...
    } finally {
        lock.unlock();
    }
}

// tryLock with timeout
if (lock.tryLock(100, TimeUnit.MILLISECONDS)) {
    // ...
}
```

### Atomic 클래스
```java
import java.util.concurrent.atomic.*;

// 단순 카운터에는 적합하지만,
// 토큰 버킷은 여러 필드를 동시에 업데이트해야 해서
// synchronized가 더 적합

AtomicLong tokens = new AtomicLong(100);

// CAS (Compare-And-Swap) 패턴
long current;
do {
    current = tokens.get();
    if (current < required) {
        return false;
    }
} while (!tokens.compareAndSet(current, current - required));
return true;
```

### wait/notify
```java
// 블로킹 consume 구현
public void consume(long tokens) throws InterruptedException {
    synchronized (this) {
        while (availableTokens < tokens) {
            long waitTime = calculateWaitTime(tokens);
            wait(waitTime);  // 락 해제하고 대기
            refill();
        }
        availableTokens -= tokens;
    }
}

// notify로 대기 스레드 깨우기
public void addTokens(long tokens) {
    synchronized (this) {
        availableTokens = Math.min(capacity, availableTokens + tokens);
        notifyAll();  // 대기 중인 스레드 깨움
    }
}
```

---

## 📊 수학 연산

### Math 클래스
```java
// 최소/최대
long capped = Math.min(capacity, availableTokens + tokensToAdd);
long nonNegative = Math.max(0, tokens);

// 올림 나눗셈
long periodsNeeded = (tokensNeeded + refillTokens - 1) / refillTokens;
// 또는
long periodsNeeded = (long) Math.ceil((double) tokensNeeded / refillTokens);

// 반올림
long rounded = Math.round(value);
```

### 오버플로우 방지
```java
// 안전한 더하기 (오버플로우 시 예외)
long result = Math.addExact(a, b);

// 안전한 곱하기
long result = Math.multiplyExact(a, b);

// 또는 BigInteger 사용
import java.math.BigInteger;
BigInteger big = BigInteger.valueOf(a).multiply(BigInteger.valueOf(b));
```

---

## 🧪 테스트 관련

### AssertJ 사용
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldConsumeTokens() {
    TokenBucket bucket = new TokenBucket(100, 10, Duration.ofSeconds(1));
    
    assertThat(bucket.tryConsume(50)).isTrue();
    assertThat(bucket.getAvailableTokens()).isEqualTo(50);
}

@Test
void shouldRejectWhenInsufficient() {
    TokenBucket bucket = new TokenBucket(10, 1, Duration.ofSeconds(1));
    
    bucket.tryConsume(10);  // 모두 소비
    
    assertThat(bucket.tryConsume(1)).isFalse();
    assertThat(bucket.getAvailableTokens()).isEqualTo(0);
}

@Test
void shouldRefillOverTime() throws InterruptedException {
    TokenBucket bucket = new TokenBucket(10, 2, Duration.ofSeconds(1));
    bucket.tryConsume(10);
    
    Thread.sleep(1500);  // 1.5초 대기
    
    assertThat(bucket.getAvailableTokens()).isEqualTo(2);
}
```

### 시간 제어 테스트
```java
// Clock 주입
public class TokenBucket {
    private final Clock clock;
    
    public TokenBucket(Config config, Clock clock) {
        this.clock = clock;
        this.lastRefillNanos = clock.instant().toEpochMilli() * 1_000_000;
    }
    
    // 테스트에서 시간 제어
}

@Test
void shouldRefillWithMockedClock() {
    Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.systemDefault());
    TokenBucket bucket = new TokenBucket(config, fixedClock);
    
    bucket.tryConsume(100);
    
    // 시간 이동
    Clock advanced = Clock.fixed(
        Instant.now().plus(Duration.ofSeconds(5)), 
        ZoneId.systemDefault()
    );
    bucket.setClock(advanced);
    
    assertThat(bucket.getAvailableTokens()).isEqualTo(50);
}
```

### 동시성 테스트
```java
@Test
void shouldBeThreadSafe() throws InterruptedException {
    TokenBucket bucket = new TokenBucket(1000, 100, Duration.ofSeconds(1));
    int threadCount = 10;
    int consumePerThread = 50;
    
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);
    AtomicInteger successCount = new AtomicInteger(0);
    
    for (int i = 0; i < threadCount; i++) {
        executor.submit(() -> {
            for (int j = 0; j < consumePerThread; j++) {
                if (bucket.tryConsume(1)) {
                    successCount.incrementAndGet();
                }
            }
            latch.countDown();
        });
    }
    
    latch.await();
    executor.shutdown();
    
    // 1000개 토큰에서 시작, 최대 500개 소비 시도
    assertThat(successCount.get()).isLessThanOrEqualTo(1000);
}
```

---

## 📚 Java 21 관련

### Record
```java
// 설정
public record TokenBucketConfig(
    long capacity,
    long refillTokens,
    Duration refillPeriod
) {
    public TokenBucketConfig {
        if (capacity <= 0) throw new IllegalArgumentException();
        if (refillTokens <= 0) throw new IllegalArgumentException();
        if (refillPeriod.isNegative() || refillPeriod.isZero()) 
            throw new IllegalArgumentException();
    }
    
    public static TokenBucketConfig perSecond(long tokensPerSecond) {
        return new TokenBucketConfig(
            tokensPerSecond, 
            tokensPerSecond, 
            Duration.ofSeconds(1)
        );
    }
}

// 소비 결과
public record ConsumeResult(
    boolean allowed,
    long remainingTokens,
    Duration waitTime
) {
    public static ConsumeResult allowed(long remaining) {
        return new ConsumeResult(true, remaining, Duration.ZERO);
    }
    
    public static ConsumeResult denied(long remaining, Duration wait) {
        return new ConsumeResult(false, remaining, wait);
    }
}
```

### Virtual Threads
```java
// 블로킹 consume을 가상 스레드에서 사용
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 10000; i++) {
        executor.submit(() -> {
            bucket.consume(1);  // 블로킹 OK (가상 스레드)
            processRequest();
        });
    }
}
```

---

## ⚡ 성능 팁

### 1. 나노초 사용
```java
// 밀리초는 정밀도 부족할 수 있음
private long lastRefillNanos = System.nanoTime();
private final long refillPeriodNanos = refillPeriod.toNanos();
```

### 2. 불필요한 객체 생성 피하기
```java
// 매번 Instant 생성 피하기
// 느림
Instant now = Instant.now();
Duration elapsed = Duration.between(lastRefill, now);

// 빠름
long now = System.nanoTime();
long elapsed = now - lastRefillNanos;
```

### 3. volatile vs synchronized
```java
// 읽기만 하는 필드는 volatile로 충분
private volatile long availableTokens;

// 하지만 토큰 버킷은 읽기+쓰기가 원자적이어야 해서
// synchronized 필요
```

---

## 🔀 Builder 패턴
```java
public class TokenBucket {
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private long capacity = 100;
        private long refillTokens = 10;
        private Duration refillPeriod = Duration.ofSeconds(1);
        private long initialTokens = -1;  // -1이면 capacity
        
        public Builder capacity(long capacity) {
            this.capacity = capacity;
            return this;
        }
        
        public Builder refillTokens(long tokens) {
            this.refillTokens = tokens;
            return this;
        }
        
        public Builder refillPeriod(Duration period) {
            this.refillPeriod = period;
            return this;
        }
        
        public Builder initialTokens(long tokens) {
            this.initialTokens = tokens;
            return this;
        }
        
        // 편의 메서드
        public Builder tokensPerSecond(long tps) {
            this.refillTokens = tps;
            this.refillPeriod = Duration.ofSeconds(1);
            return this;
        }
        
        public Builder tokensPerMinute(long tpm) {
            this.refillTokens = tpm;
            this.refillPeriod = Duration.ofMinutes(1);
            return this;
        }
        
        public TokenBucket build() {
            long initial = initialTokens < 0 ? capacity : initialTokens;
            return new TokenBucket(capacity, refillTokens, refillPeriod, initial);
        }
    }
}

// 사용
TokenBucket bucket = TokenBucket.builder()
    .capacity(100)
    .tokensPerSecond(10)
    .initialTokens(50)
    .build();
```
