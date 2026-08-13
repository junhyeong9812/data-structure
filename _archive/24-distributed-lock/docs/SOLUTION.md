# 분산 락 풀이 해설

## 📌 핵심 아이디어

분산 락은 **상호 배제**를 보장하면서 **안전성**을 위해 
Fencing Token을 사용합니다.

**핵심 원칙**:
- 한 번에 하나의 클라이언트만 락 보유
- TTL로 데드락 방지
- Fencing Token으로 순서 보장

---

## 🔑 핵심 개념

### 1. Fencing Token의 필요성
```
문제 시나리오 (Fencing Token 없이):

1. Client A가 락 획득
2. Client A가 GC pause로 멈춤
3. 락 TTL 만료
4. Client B가 락 획득
5. Client B가 데이터 수정
6. Client A가 깨어나서 데이터 수정 ← 문제!

해결책 (Fencing Token):

1. Client A가 락 획득 (token=33)
2. Client A가 GC pause
3. 락 TTL 만료
4. Client B가 락 획득 (token=34)
5. Client B가 데이터 수정 (token=34 저장)
6. Client A가 깨어나서 수정 시도 (token=33)
7. 저장소가 token=33 < 34 확인 → 거부!
```

### 2. TTL과 데드락 방지
```java
// 클라이언트가 크래시하면?
// TTL이 없으면 락이 영원히 잠김 (데드락)

tryLock("resource", "client-A", Duration.ofSeconds(30));
// 30초 후 자동 해제 → 데드락 방지

// 주의: TTL이 작업 시간보다 짧으면?
// → 작업 완료 전에 락 만료 → 다른 클라이언트 진입
// → 해결: 락 연장 (heartbeat)
```

### 3. 원자적 연산
```java
// ConcurrentHashMap의 compute는 원자적
locks.compute(resource, (key, current) -> {
    if (current == null || isExpired(current)) {
        return newLock;
    }
    return current;
});

// 여러 스레드가 동시에 호출해도 안전
```

---

## 📝 POP 구현 해설

### 완전한 구현
```java
public class DistributedLock {
    private final Map<String, LockInfo> locks = new ConcurrentHashMap<>();
    private final AtomicLong tokenGenerator = new AtomicLong(0);
    
    // 락 정보
    public record LockInfo(
        String owner,
        long fencingToken,
        Instant expiresAt,
        int reentrantCount
    ) {
        public LockInfo withExtendedTtl(Instant newExpiry) {
            return new LockInfo(owner, fencingToken, newExpiry, reentrantCount);
        }
        
        public LockInfo withIncrementedCount() {
            return new LockInfo(owner, fencingToken, expiresAt, reentrantCount + 1);
        }
        
        public LockInfo withDecrementedCount() {
            return new LockInfo(owner, fencingToken, expiresAt, reentrantCount - 1);
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
        
        public boolean isSuccess() { return success; }
        public long getFencingToken() { return fencingToken; }
    }
    
    // 락 획득
    public LockResult tryLock(String resource, String clientId, Duration ttl) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(ttl);
        
        long newToken = tokenGenerator.incrementAndGet();
        
        LockInfo[] result = new LockInfo[1];
        
        locks.compute(resource, (key, current) -> {
            // 락 없음 또는 만료됨
            if (current == null || current.expiresAt().isBefore(now)) {
                result[0] = new LockInfo(clientId, newToken, expiresAt, 1);
                return result[0];
            }
            
            // 재진입 (같은 클라이언트)
            if (current.owner().equals(clientId)) {
                result[0] = new LockInfo(
                    clientId, 
                    current.fencingToken(), 
                    expiresAt, 
                    current.reentrantCount() + 1
                );
                return result[0];
            }
            
            // 다른 클라이언트가 보유
            result[0] = null;
            return current;
        });
        
        if (result[0] != null && result[0].owner().equals(clientId)) {
            return LockResult.success(result[0].fencingToken());
        }
        
        LockInfo holder = locks.get(resource);
        return LockResult.failure("Lock held by " + 
            (holder != null ? holder.owner() : "unknown"));
    }
    
    // 락 해제
    public boolean unlock(String resource, String clientId) {
        AtomicBoolean success = new AtomicBoolean(false);
        
        locks.computeIfPresent(resource, (key, current) -> {
            if (!current.owner().equals(clientId)) {
                return current;  // 소유자 아님
            }
            
            // 재진입 카운트 감소
            if (current.reentrantCount() > 1) {
                success.set(true);
                return current.withDecrementedCount();
            }
            
            // 완전 해제
            success.set(true);
            return null;
        });
        
        return success.get();
    }
    
    // 락 연장
    public boolean extend(String resource, String clientId, Duration ttl) {
        Instant newExpiry = Instant.now().plus(ttl);
        AtomicBoolean success = new AtomicBoolean(false);
        
        locks.computeIfPresent(resource, (key, current) -> {
            if (current.owner().equals(clientId)) {
                success.set(true);
                return current.withExtendedTtl(newExpiry);
            }
            return current;
        });
        
        return success.get();
    }
    
    // 락 상태 확인
    public boolean isLocked(String resource) {
        LockInfo lock = locks.get(resource);
        return lock != null && lock.expiresAt().isAfter(Instant.now());
    }
    
    // 소유자 조회
    public Optional<String> getOwner(String resource) {
        LockInfo lock = locks.get(resource);
        if (lock != null && lock.expiresAt().isAfter(Instant.now())) {
            return Optional.of(lock.owner());
        }
        return Optional.empty();
    }
    
    // 만료된 락 정리
    public int cleanupExpired() {
        Instant now = Instant.now();
        AtomicInteger count = new AtomicInteger(0);
        
        locks.entrySet().removeIf(entry -> {
            if (entry.getValue().expiresAt().isBefore(now)) {
                count.incrementAndGet();
                return true;
            }
            return false;
        });
        
        return count.get();
    }
}
```

### 블로킹 락 (대기 포함)
```java
public LockResult lock(String resource, String clientId, 
                       Duration ttl, Duration waitTimeout) 
        throws InterruptedException {
    
    Instant deadline = Instant.now().plus(waitTimeout);
    
    while (Instant.now().isBefore(deadline)) {
        LockResult result = tryLock(resource, clientId, ttl);
        if (result.isSuccess()) {
            return result;
        }
        
        // 백오프 후 재시도
        Thread.sleep(50 + ThreadLocalRandom.current().nextInt(50));
    }
    
    return LockResult.failure("Timeout waiting for lock");
}
```

### Read-Write 락
```java
public class ReadWriteDistributedLock {
    private final Map<String, RWLockInfo> locks = new ConcurrentHashMap<>();
    
    public record RWLockInfo(
        Set<String> readers,
        String writer,
        Instant expiresAt
    ) {}
    
    // 읽기 락 (공유)
    public boolean readLock(String resource, String clientId, Duration ttl) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(ttl);
        
        AtomicBoolean success = new AtomicBoolean(false);
        
        locks.compute(resource, (key, current) -> {
            // 새 락
            if (current == null || current.expiresAt().isBefore(now)) {
                success.set(true);
                return new RWLockInfo(
                    new HashSet<>(Set.of(clientId)), 
                    null, 
                    expiresAt
                );
            }
            
            // 쓰기 락이 있으면 실패
            if (current.writer() != null) {
                return current;
            }
            
            // 읽기 락 추가
            Set<String> newReaders = new HashSet<>(current.readers());
            newReaders.add(clientId);
            success.set(true);
            
            Instant newExpiry = expiresAt.isAfter(current.expiresAt()) 
                ? expiresAt : current.expiresAt();
            
            return new RWLockInfo(newReaders, null, newExpiry);
        });
        
        return success.get();
    }
    
    // 쓰기 락 (배타적)
    public boolean writeLock(String resource, String clientId, Duration ttl) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(ttl);
        
        AtomicBoolean success = new AtomicBoolean(false);
        
        locks.compute(resource, (key, current) -> {
            // 새 락
            if (current == null || current.expiresAt().isBefore(now)) {
                success.set(true);
                return new RWLockInfo(Set.of(), clientId, expiresAt);
            }
            
            // 이미 락이 있으면 실패
            if (!current.readers().isEmpty() || current.writer() != null) {
                return current;
            }
            
            success.set(true);
            return new RWLockInfo(Set.of(), clientId, expiresAt);
        });
        
        return success.get();
    }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 |
|------|-----------|
| tryLock | O(1) |
| unlock | O(1) |
| extend | O(1) |
| isLocked | O(1) |
| cleanupExpired | O(n) |

---

## ❌ 흔한 실수

### 1. 소유자 확인 없이 해제
```java
// 잘못됨: 누구나 해제 가능
public void unlock(String resource) {
    locks.remove(resource);
}

// 올바름: 소유자만 해제
public boolean unlock(String resource, String clientId) {
    LockInfo lock = locks.get(resource);
    if (lock != null && lock.owner().equals(clientId)) {
        locks.remove(resource);
        return true;
    }
    return false;
}
```

### 2. 비원자적 검사-후-행동
```java
// 잘못됨: race condition
if (!locks.containsKey(resource)) {
    locks.put(resource, newLock);  // 다른 스레드가 먼저 넣을 수 있음!
}

// 올바름: 원자적 연산
locks.compute(resource, (k, v) -> {
    if (v == null) return newLock;
    return v;
});
```

### 3. TTL 만료 체크 누락
```java
// 잘못됨: 만료 체크 없음
public boolean isLocked(String resource) {
    return locks.containsKey(resource);
}

// 올바름: 만료 확인
public boolean isLocked(String resource) {
    LockInfo lock = locks.get(resource);
    return lock != null && lock.expiresAt().isAfter(Instant.now());
}
```

---

## 🔗 관련 문제

- 분산 트랜잭션
- 리더 선출 (Leader Election)
- 분산 세마포어
- 분산 배리어
