# 분산 락 구현에 유용한 Java API

## 📦 java.util.concurrent

### ConcurrentHashMap
```java
import java.util.concurrent.ConcurrentHashMap;

ConcurrentHashMap<String, LockInfo> locks = new ConcurrentHashMap<>();

// 원자적 compute (핵심!)
locks.compute(key, (k, current) -> {
    if (current == null) {
        return newLock;  // 새로 생성
    }
    return current;  // 기존 유지 또는 수정
});

// 존재할 때만 수정
locks.computeIfPresent(key, (k, current) -> {
    if (shouldRemove(current)) {
        return null;  // null 반환 시 제거
    }
    return modifiedLock;
});

// 없을 때만 생성
locks.computeIfAbsent(key, k -> createNewLock());

// 조건부 제거
locks.entrySet().removeIf(e -> isExpired(e.getValue()));
```

### AtomicLong
```java
import java.util.concurrent.atomic.AtomicLong;

// Fencing Token 생성
AtomicLong tokenGenerator = new AtomicLong(0);

// 증가 후 반환
long token = tokenGenerator.incrementAndGet();

// 현재 값 조회
long current = tokenGenerator.get();

// 비교 후 교환 (CAS)
boolean success = tokenGenerator.compareAndSet(expected, newValue);
```

### AtomicBoolean
```java
import java.util.concurrent.atomic.AtomicBoolean;

AtomicBoolean success = new AtomicBoolean(false);

// lambda 내에서 결과 전달
locks.computeIfPresent(key, (k, v) -> {
    success.set(true);
    return null;
});

if (success.get()) {
    // 성공 처리
}
```

### AtomicReference
```java
import java.util.concurrent.atomic.AtomicReference;

AtomicReference<LockInfo> result = new AtomicReference<>();

locks.compute(key, (k, v) -> {
    LockInfo newLock = createLock();
    result.set(newLock);
    return newLock;
});

LockInfo acquired = result.get();
```

---

## 📊 시간 관련

### java.time.Instant
```java
import java.time.Instant;
import java.time.Duration;

// 현재 시간
Instant now = Instant.now();

// 만료 시간 계산
Instant expiresAt = now.plus(Duration.ofSeconds(30));

// 만료 확인
boolean expired = expiresAt.isBefore(Instant.now());
boolean valid = expiresAt.isAfter(Instant.now());

// 비교
int cmp = instant1.compareTo(instant2);
```

### java.time.Duration
```java
import java.time.Duration;

// 생성
Duration ttl = Duration.ofSeconds(30);
Duration timeout = Duration.ofMillis(100);

// 연산
Duration doubled = ttl.multipliedBy(2);
Duration remaining = Duration.between(now, expiresAt);

// 변환
long millis = ttl.toMillis();
long seconds = ttl.getSeconds();
```

---

## 🔐 동기화

### synchronized
```java
// 메서드 레벨
public synchronized boolean tryLock(String resource) {
    // ...
}

// 블록 레벨
public boolean tryLock(String resource) {
    synchronized (this) {
        // ...
    }
}

// 특정 객체
private final Object lock = new Object();
public void method() {
    synchronized (lock) {
        // ...
    }
}
```

### ReentrantLock
```java
import java.util.concurrent.locks.ReentrantLock;

ReentrantLock lock = new ReentrantLock();

// 기본 사용
lock.lock();
try {
    // 임계 영역
} finally {
    lock.unlock();
}

// 타임아웃 시도
if (lock.tryLock(1, TimeUnit.SECONDS)) {
    try {
        // 임계 영역
    } finally {
        lock.unlock();
    }
}

// 인터럽트 가능
lock.lockInterruptibly();
```

### ReadWriteLock
```java
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

ReadWriteLock rwLock = new ReentrantReadWriteLock();

// 읽기 락
rwLock.readLock().lock();
try {
    // 읽기 작업 (여러 스레드 동시 가능)
} finally {
    rwLock.readLock().unlock();
}

// 쓰기 락
rwLock.writeLock().lock();
try {
    // 쓰기 작업 (단독 점유)
} finally {
    rwLock.writeLock().unlock();
}
```

---

## 🧪 테스트

### AssertJ
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldAcquireLock() {
    DistributedLock lockManager = new DistributedLock();
    
    LockResult result = lockManager.tryLock("resource", "client-A", 
        Duration.ofSeconds(10));
    
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.getFencingToken()).isPositive();
}

@Test
void shouldRejectSecondClient() {
    DistributedLock lockManager = new DistributedLock();
    
    lockManager.tryLock("resource", "client-A", Duration.ofSeconds(10));
    LockResult result = lockManager.tryLock("resource", "client-B", 
        Duration.ofSeconds(10));
    
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getMessage()).contains("client-A");
}

@Test
void shouldReleaseAfterTtl() throws InterruptedException {
    DistributedLock lockManager = new DistributedLock();
    
    lockManager.tryLock("resource", "client-A", Duration.ofMillis(100));
    
    Thread.sleep(150);
    
    LockResult result = lockManager.tryLock("resource", "client-B", 
        Duration.ofSeconds(10));
    assertThat(result.isSuccess()).isTrue();
}
```

### 동시성 테스트
```java
@Test
void shouldHandleConcurrentRequests() throws InterruptedException {
    DistributedLock lockManager = new DistributedLock();
    int threadCount = 100;
    
    CountDownLatch latch = new CountDownLatch(threadCount);
    AtomicInteger successCount = new AtomicInteger(0);
    
    for (int i = 0; i < threadCount; i++) {
        final String clientId = "client-" + i;
        new Thread(() -> {
            LockResult result = lockManager.tryLock("resource", clientId, 
                Duration.ofSeconds(10));
            if (result.isSuccess()) {
                successCount.incrementAndGet();
            }
            latch.countDown();
        }).start();
    }
    
    latch.await();
    
    // 정확히 1개만 성공해야 함
    assertThat(successCount.get()).isEqualTo(1);
}

@Test
void shouldIncreaseFencingToken() {
    DistributedLock lockManager = new DistributedLock();
    
    LockResult r1 = lockManager.tryLock("r1", "c1", Duration.ofSeconds(10));
    LockResult r2 = lockManager.tryLock("r2", "c2", Duration.ofSeconds(10));
    
    assertThat(r2.getFencingToken()).isGreaterThan(r1.getFencingToken());
}
```

---

## 📚 Java 21 관련

### Record
```java
// 락 정보
public record LockInfo(
    String owner,
    long fencingToken,
    Instant expiresAt,
    int reentrantCount
) {
    public boolean isExpired() {
        return expiresAt.isBefore(Instant.now());
    }
    
    public LockInfo withExtendedTtl(Duration extension) {
        return new LockInfo(owner, fencingToken, 
            Instant.now().plus(extension), reentrantCount);
    }
}

// 락 결과
public record LockResult(
    boolean success,
    long fencingToken,
    String message
) {
    public static LockResult success(long token) {
        return new LockResult(true, token, "Lock acquired");
    }
    
    public static LockResult failure(String reason) {
        return new LockResult(false, -1, reason);
    }
}
```

### Sealed Classes
```java
public sealed interface LockType permits ExclusiveLock, SharedLock {
    String resource();
    String owner();
    Instant expiresAt();
}

public record ExclusiveLock(
    String resource, String owner, Instant expiresAt
) implements LockType {}

public record SharedLock(
    String resource, Set<String> owners, Instant expiresAt
) implements LockType {
    @Override
    public String owner() {
        return owners.toString();
    }
}
```

### Pattern Matching
```java
public void handleLock(LockType lock) {
    switch (lock) {
        case ExclusiveLock e -> 
            System.out.println("Exclusive: " + e.owner());
        case SharedLock s -> 
            System.out.println("Shared: " + s.owners().size() + " readers");
    }
}
```

### Optional
```java
import java.util.Optional;

public Optional<String> getOwner(String resource) {
    LockInfo lock = locks.get(resource);
    
    return Optional.ofNullable(lock)
        .filter(l -> !l.isExpired())
        .map(LockInfo::owner);
}

// 사용
lockManager.getOwner("resource")
    .ifPresent(owner -> System.out.println("Owned by: " + owner));
```

---

## ⚡ 성능 팁

### 1. 락 경합 줄이기
```java
// 리소스별로 다른 락 사용
// (전체를 하나의 락으로 보호하지 않음)
ConcurrentHashMap<String, LockInfo> locks;  // 자동으로 분할 락 사용
```

### 2. 스핀 락 vs 블로킹
```java
// 짧은 대기: 스핀
public LockResult tryLockSpin(String resource, String clientId, 
                              Duration ttl, int maxSpins) {
    for (int i = 0; i < maxSpins; i++) {
        LockResult result = tryLock(resource, clientId, ttl);
        if (result.isSuccess()) return result;
        Thread.onSpinWait();  // CPU 힌트
    }
    return LockResult.failure("Spin limit exceeded");
}

// 긴 대기: 블로킹
public LockResult lockBlocking(String resource, String clientId,
                               Duration ttl, Duration timeout) 
        throws InterruptedException {
    long deadline = System.currentTimeMillis() + timeout.toMillis();
    
    while (System.currentTimeMillis() < deadline) {
        LockResult result = tryLock(resource, clientId, ttl);
        if (result.isSuccess()) return result;
        Thread.sleep(50);  // 백오프
    }
    return LockResult.failure("Timeout");
}
```

### 3. 락 풀링
```java
// 미리 생성된 락 객체 재사용
private final ObjectPool<LockInfo> lockPool;

public LockResult tryLock(...) {
    LockInfo lock = lockPool.borrow();
    // ... 사용
    lockPool.release(lock);
}
```
