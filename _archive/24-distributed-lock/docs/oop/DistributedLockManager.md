# oop/DistributedLockManager.java

OOP 인터페이스 + 구현. 재진입 락 카운트 + Fencing Token.

```java
package com.datastructure.distributedlock.oop;

import java.time.Duration;

public interface DistributedLockManager {
    LockResult tryLock(String resource, String clientId, Duration ttl);
    boolean unlock(String resource, String clientId);
    boolean extend(String resource, String clientId, Duration ttl);
    boolean isLocked(String resource);
    String getOwner(String resource);

    class LockResult {
        public final boolean success;
        public final long fencingToken;
        public final String message;

        private LockResult(boolean success, long token, String message) {
            this.success = success;
            this.fencingToken = token;
            this.message = message;
        }

        public static LockResult success(long token) {
            return new LockResult(true, token, "ok");
        }

        public static LockResult failure(String reason) {
            return new LockResult(false, -1, reason);
        }
    }
}
```

---

# oop/InMemoryLockManager.java

```java
package com.datastructure.distributedlock.oop;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryLockManager implements DistributedLockManager {

    static class LockInfo {
        final String owner;
        final long fencingToken;
        Instant expiresAt;
        int reentrantCount;

        LockInfo(String owner, long token, Instant expires) {
            this.owner = owner;
            this.fencingToken = token;
            this.expiresAt = expires;
            this.reentrantCount = 1;
        }
    }

    private final Map<String, LockInfo> locks = new ConcurrentHashMap<>();
    private final AtomicLong tokenGen = new AtomicLong();

    @Override
    public LockResult tryLock(String resource, String clientId, Duration ttl) {
        Instant now = Instant.now();
        Instant expires = now.plus(ttl);
        long candidate = tokenGen.incrementAndGet();

        LockInfo result = locks.compute(resource, (k, cur) -> {
            if (cur == null || cur.expiresAt.isBefore(now)) {
                return new LockInfo(clientId, candidate, expires);
            }
            if (cur.owner.equals(clientId)) {
                cur.expiresAt = expires;
                cur.reentrantCount++;
                return cur;
            }
            return cur;
        });

        if (result.owner.equals(clientId)) {
            return LockResult.success(result.fencingToken);
        }
        return LockResult.failure("Lock held by " + result.owner);
    }

    @Override
    public boolean unlock(String resource, String clientId) {
        AtomicBoolean ok = new AtomicBoolean(false);
        locks.computeIfPresent(resource, (k, cur) -> {
            if (!cur.owner.equals(clientId)) return cur;
            ok.set(true);
            cur.reentrantCount--;
            return cur.reentrantCount <= 0 ? null : cur;
        });
        return ok.get();
    }

    @Override
    public boolean extend(String resource, String clientId, Duration ttl) {
        Instant now = Instant.now();
        AtomicBoolean ok = new AtomicBoolean(false);
        locks.computeIfPresent(resource, (k, cur) -> {
            if (cur.owner.equals(clientId) && cur.expiresAt.isAfter(now)) {
                cur.expiresAt = now.plus(ttl);
                ok.set(true);
            }
            return cur;
        });
        return ok.get();
    }

    @Override
    public boolean isLocked(String resource) {
        Instant now = Instant.now();
        LockInfo l = locks.get(resource);
        return l != null && l.expiresAt.isAfter(now);
    }

    @Override
    public String getOwner(String resource) {
        Instant now = Instant.now();
        LockInfo l = locks.get(resource);
        return (l != null && l.expiresAt.isAfter(now)) ? l.owner : null;
    }
}
```
