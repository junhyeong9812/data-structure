# oop/ReadWriteDistributedLock.java

분산 Read-Write 락. 다중 reader 동시 허용, writer는 단독.

```java
package com.datastructure.distributedlock.oop;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ReadWriteDistributedLock {

    private static class State {
        Map<String, Instant> readers = new ConcurrentHashMap<>();
        String writerOwner;
        Instant writerExpires;
        long writerToken;
    }

    private final Map<String, State> resources = new ConcurrentHashMap<>();
    private final AtomicLong tokenGen = new AtomicLong();

    public boolean readLock(String resource, String clientId, Duration ttl) {
        Instant now = Instant.now();
        Instant expires = now.plus(ttl);
        State s = resources.computeIfAbsent(resource, k -> new State());
        synchronized (s) {
            if (s.writerOwner != null && s.writerExpires.isAfter(now)
                    && !s.writerOwner.equals(clientId)) {
                return false;
            }
            // writer expired? clear
            if (s.writerOwner != null && !s.writerExpires.isAfter(now)) {
                s.writerOwner = null;
            }
            s.readers.put(clientId, expires);
            return true;
        }
    }

    public long writeLock(String resource, String clientId, Duration ttl) {
        Instant now = Instant.now();
        Instant expires = now.plus(ttl);
        State s = resources.computeIfAbsent(resource, k -> new State());
        synchronized (s) {
            // 활성 reader 정리
            s.readers.entrySet().removeIf(e -> !e.getValue().isAfter(now));

            boolean otherReaders = s.readers.keySet().stream()
                    .anyMatch(c -> !c.equals(clientId));
            if (otherReaders) return -1;

            if (s.writerOwner != null && s.writerExpires.isAfter(now)
                    && !s.writerOwner.equals(clientId)) {
                return -1;
            }

            if (s.writerOwner == null || !s.writerOwner.equals(clientId)) {
                s.writerToken = tokenGen.incrementAndGet();
            }
            s.writerOwner = clientId;
            s.writerExpires = expires;
            return s.writerToken;
        }
    }

    public boolean releaseRead(String resource, String clientId) {
        State s = resources.get(resource);
        if (s == null) return false;
        synchronized (s) {
            return s.readers.remove(clientId) != null;
        }
    }

    public boolean releaseWrite(String resource, String clientId) {
        State s = resources.get(resource);
        if (s == null) return false;
        synchronized (s) {
            if (clientId.equals(s.writerOwner)) {
                s.writerOwner = null;
                s.writerExpires = null;
                return true;
            }
            return false;
        }
    }

    public Set<String> getActiveReaders(String resource) {
        State s = resources.get(resource);
        if (s == null) return Set.of();
        Instant now = Instant.now();
        synchronized (s) {
            Set<String> result = new HashSet<>();
            for (Map.Entry<String, Instant> e : s.readers.entrySet()) {
                if (e.getValue().isAfter(now)) result.add(e.getKey());
            }
            return result;
        }
    }

    public String getWriter(String resource) {
        State s = resources.get(resource);
        if (s == null) return null;
        Instant now = Instant.now();
        synchronized (s) {
            return (s.writerOwner != null && s.writerExpires.isAfter(now)) ? s.writerOwner : null;
        }
    }
}
```
