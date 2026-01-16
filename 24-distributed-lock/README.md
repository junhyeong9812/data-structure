# 24. 분산 락 (Distributed Lock)

## 📋 문제 정의

**분산 시스템에서 상호 배제(Mutual Exclusion)**를 보장하는 
분산 락 매니저를 구현하세요.

분산 락은 여러 프로세스/서버가 공유 자원에 동시 접근할 때
데이터 일관성을 보장하는 핵심 메커니즘입니다.

---

## 🎯 학습 목표

- 분산 시스템의 동시성 제어
- 상호 배제(Mutual Exclusion) 원리
- Fencing Token을 통한 안전성 보장
- 락 타임아웃과 자동 해제
- 데드락 방지 전략

---

## 📝 요구사항

### 핵심 개념

| 개념 | 설명 |
|------|------|
| **Mutual Exclusion** | 한 번에 하나의 클라이언트만 락 보유 |
| **Fencing Token** | 락 획득 순서를 보장하는 단조 증가 토큰 |
| **TTL (Time-To-Live)** | 락 자동 만료 시간 |
| **Lock Owner** | 락을 보유한 클라이언트 식별자 |

### 기본 연산

| 메서드 | 설명 |
|--------|------|
| `tryLock(resource, clientId, ttl)` | 락 획득 시도 |
| `unlock(resource, clientId)` | 락 해제 |
| `extend(resource, clientId, ttl)` | 락 TTL 연장 |
| `isLocked(resource)` | 락 상태 확인 |
| `getOwner(resource)` | 락 소유자 조회 |

### 고급 기능

| 기능 | 설명 |
|------|------|
| **Reentrant Lock** | 같은 클라이언트가 중복 획득 가능 |
| **Read-Write Lock** | 읽기/쓰기 분리 락 |
| **Fair Lock** | 요청 순서대로 락 부여 |
| **Wait Queue** | 락 대기 큐 |

---

## 📊 입출력 예시

### 예제 1: 기본 사용
```java
DistributedLock lockManager = new DistributedLock();

// 락 획득 시도 (10초 TTL)
LockResult result = lockManager.tryLock("resource-1", "client-A", 
    Duration.ofSeconds(10));

if (result.isSuccess()) {
    try {
        // 임계 영역 작업
        processResource();
    } finally {
        // 락 해제
        lockManager.unlock("resource-1", "client-A");
    }
}
```

### 예제 2: Fencing Token
```java
// 락 획득 시 Fencing Token 반환
LockResult result = lockManager.tryLock("orders", "server-1", ttl);

if (result.isSuccess()) {
    long fencingToken = result.getFencingToken();
    
    // 토큰을 사용하여 작업 (순서 보장)
    database.update(data, fencingToken);  // DB가 토큰 검증
}
```

### 예제 3: Fencing Token 시나리오
```
시간 →
Client A: [락 획득 token=1]-----[GC pause]----[작업 시도 token=1]
Client B:                 [락 획득 token=2][작업 완료]
                                              ↓
                         DB는 token=1 < token=2 이므로 거부!

Fencing Token이 없으면:
Client A: [락 획득]-----[GC pause]----[작업 성공] ← 위험!
Client B:          [락 획득][작업 성공]
                          ↓
                   데이터 불일치 발생!
```

### 예제 4: 락 연장
```java
// 작업이 오래 걸릴 때 락 연장
LockResult result = lockManager.tryLock("long-task", "worker-1", 
    Duration.ofSeconds(30));

if (result.isSuccess()) {
    // 별도 스레드에서 주기적으로 연장
    scheduler.scheduleAtFixedRate(() -> {
        lockManager.extend("long-task", "worker-1", Duration.ofSeconds(30));
    }, 10, 10, TimeUnit.SECONDS);
    
    try {
        doLongRunningTask();
    } finally {
        lockManager.unlock("long-task", "worker-1");
    }
}
```

### 예제 5: Read-Write Lock
```java
ReadWriteDistributedLock rwLock = new ReadWriteDistributedLock();

// 읽기 락 - 여러 클라이언트가 동시에 획득 가능
rwLock.readLock("config", "reader-1", ttl);
rwLock.readLock("config", "reader-2", ttl);  // OK

// 쓰기 락 - 단독 점유
rwLock.writeLock("config", "writer-1", ttl);  // 읽기 락 해제 대기
```

---

## 🔍 핵심 개념

### 분산 락 동작 원리
```
┌─────────────────────────────────────────────────┐
│              Lock Manager                        │
├─────────────────────────────────────────────────┤
│                                                  │
│  Resource      Owner       Token    Expiry      │
│  ─────────────────────────────────────────────  │
│  "orders"      client-A    42       10:30:05    │
│  "inventory"   client-B    43       10:30:10    │
│  "users"       (unlocked)  -        -           │
│                                                  │
└─────────────────────────────────────────────────┘

tryLock("orders", "client-C"):
  1. "orders" 확인 → client-A 소유
  2. 만료 시간 확인 → 아직 유효
  3. 실패 반환

tryLock("users", "client-C"):
  1. "users" 확인 → 미소유
  2. token++ → 44
  3. 락 등록 (client-C, 44, now+TTL)
  4. 성공 반환 (token=44)
```

### 안전한 락 해제
```java
// 잘못된 방법: 다른 클라이언트의 락을 해제할 수 있음
public void unlock(String resource) {
    locks.remove(resource);  // 위험!
}

// 올바른 방법: 소유자 확인
public boolean unlock(String resource, String clientId) {
    LockInfo lock = locks.get(resource);
    if (lock != null && lock.owner().equals(clientId)) {
        locks.remove(resource);
        return true;
    }
    return false;
}
```

---

## 💡 힌트

### 기본 구조
```java
public class DistributedLock {
    private final Map<String, LockInfo> locks = new ConcurrentHashMap<>();
    private final AtomicLong tokenGenerator = new AtomicLong(0);
    
    public record LockInfo(
        String owner,
        long fencingToken,
        Instant expiresAt
    ) {}
    
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
}
```

### tryLock 구현
```java
public LockResult tryLock(String resource, String clientId, Duration ttl) {
    Instant now = Instant.now();
    Instant expiresAt = now.plus(ttl);
    
    // 원자적 연산
    LockInfo newLock = new LockInfo(
        clientId, 
        tokenGenerator.incrementAndGet(), 
        expiresAt
    );
    
    LockInfo existing = locks.compute(resource, (key, current) -> {
        // 락이 없거나 만료됨
        if (current == null || current.expiresAt().isBefore(now)) {
            return newLock;
        }
        // 재진입 (같은 클라이언트)
        if (current.owner().equals(clientId)) {
            return new LockInfo(clientId, current.fencingToken(), expiresAt);
        }
        // 다른 클라이언트가 보유 중
        return current;
    });
    
    if (existing.owner().equals(clientId)) {
        return LockResult.success(existing.fencingToken());
    }
    return LockResult.failure("Lock held by " + existing.owner());
}
```

### unlock 구현
```java
public boolean unlock(String resource, String clientId) {
    AtomicBoolean success = new AtomicBoolean(false);
    
    locks.computeIfPresent(resource, (key, current) -> {
        if (current.owner().equals(clientId)) {
            success.set(true);
            return null;  // 제거
        }
        return current;  // 유지
    });
    
    return success.get();
}
```

---

## ✅ 체크리스트

- [ ] tryLock (락 획득)
- [ ] unlock (락 해제)
- [ ] TTL 자동 만료
- [ ] Fencing Token
- [ ] 소유자 검증
- [ ] 락 연장 (extend)
- [ ] 재진입 락 (선택)
- [ ] Read-Write 락 (선택)
- [ ] 대기 큐 (선택)

---

## 📚 참고

- Redis Redlock 알고리즘
- ZooKeeper 분산 락
- etcd 분산 락
- Martin Kleppmann의 분산 락 분석
