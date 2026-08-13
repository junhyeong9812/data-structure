# pop/DistributedLock.java

인메모리 분산 락 시뮬레이터. ConcurrentHashMap + AtomicLong fencing token. tryLock/unlock/extend.

```java
package com.datastructure.distributedlock.pop;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class DistributedLock {

    public static class LockInfo {
        public final String owner;
        public final long fencingToken;
        public final Instant expiresAt;

        public LockInfo(String owner, long fencingToken, Instant expiresAt) {
            this.owner = owner;
            this.fencingToken = fencingToken;
            this.expiresAt = expiresAt;
        }

        public boolean isExpired(Instant now) {
            return expiresAt.isBefore(now);
        }
    }

    public static class LockResult {
        public final boolean success;
        public final long fencingToken;
        public final String message;

        private LockResult(boolean success, long fencingToken, String message) {
            this.success = success;
            this.fencingToken = fencingToken;
            this.message = message;
        }

        public static LockResult success(long token) {
            return new LockResult(true, token, "Lock acquired");
        }

        public static LockResult failure(String reason) {
            return new LockResult(false, -1, reason);
        }
    }

    private final Map<String, LockInfo> locks = new ConcurrentHashMap<>();
    private final AtomicLong tokenGen = new AtomicLong();

    public LockResult tryLock(String resource, String clientId, Duration ttl) {
        Instant now = Instant.now();
        Instant expires = now.plus(ttl);
        long candidateToken = tokenGen.incrementAndGet();

        LockInfo result = locks.compute(resource, (k, cur) -> {
            if (cur == null || cur.isExpired(now)) {
                return new LockInfo(clientId, candidateToken, expires);
            }
            if (cur.owner.equals(clientId)) {
                // 재진입: token 유지하고 만료 갱신
                return new LockInfo(clientId, cur.fencingToken, expires);
            }
            return cur;
        });

        if (result.owner.equals(clientId)) {
            return LockResult.success(result.fencingToken);
        }
        return LockResult.failure("Lock held by " + result.owner);
    }

    public boolean unlock(String resource, String clientId) {
        AtomicBoolean ok = new AtomicBoolean(false);
        locks.computeIfPresent(resource, (k, cur) -> {
            if (cur.owner.equals(clientId)) {
                ok.set(true);
                return null;
            }
            return cur;
        });
        return ok.get();
    }

    public boolean extend(String resource, String clientId, Duration ttl) {
        Instant now = Instant.now();
        Instant newExpires = now.plus(ttl);
        AtomicBoolean ok = new AtomicBoolean(false);
        locks.computeIfPresent(resource, (k, cur) -> {
            if (cur.owner.equals(clientId) && !cur.isExpired(now)) {
                ok.set(true);
                return new LockInfo(clientId, cur.fencingToken, newExpires);
            }
            return cur;
        });
        return ok.get();
    }

    public boolean isLocked(String resource) {
        Instant now = Instant.now();
        LockInfo info = locks.get(resource);
        return info != null && !info.isExpired(now);
    }

    public String getOwner(String resource) {
        Instant now = Instant.now();
        LockInfo info = locks.get(resource);
        return (info != null && !info.isExpired(now)) ? info.owner : null;
    }

    public int activeLockCount() {
        Instant now = Instant.now();
        int count = 0;
        for (LockInfo info : locks.values()) {
            if (!info.isExpired(now)) count++;
        }
        return count;
    }
}
```
