# oop/ConsistentHashing.java

인터페이스 + 가중치 지원 OOP 구현. 해시 함수 추상화.

```java
package com.datastructure.consistenthashing.oop;

import java.util.List;
import java.util.Set;

public interface ConsistentHashing<N> {
    void addNode(N node);
    void addNode(N node, int weight);
    void removeNode(N node);
    N getNode(Object key);
    List<N> getNodes(Object key, int count);
    Set<N> getAllNodes();
    int ringSize();
}
```

---

# oop/HashFunction.java

```java
package com.datastructure.consistenthashing.oop;

@FunctionalInterface
public interface HashFunction {
    long hash(String key);
}
```

---

# oop/Md5HashFunction.java

```java
package com.datastructure.consistenthashing.oop;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5HashFunction implements HashFunction {
    @Override
    public long hash(String key) {
        try {
            byte[] d = MessageDigest.getInstance("MD5")
                    .digest(key.getBytes(StandardCharsets.UTF_8));
            long h = 0;
            for (int i = 0; i < 8; i++) h = (h << 8) | (d[i] & 0xff);
            return h & 0x7fffffffffffffffL;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
```

---

# oop/ConsistentHashRouter.java

```java
package com.datastructure.consistenthashing.oop;

import java.util.*;

public class ConsistentHashRouter<N> implements ConsistentHashing<N> {
    private final TreeMap<Long, N> ring = new TreeMap<>();
    private final Map<N, Set<Long>> nodePositions = new HashMap<>();
    private final int defaultVirtualNodes;
    private final HashFunction hash;

    public ConsistentHashRouter() {
        this(150, new Md5HashFunction());
    }

    public ConsistentHashRouter(int defaultVirtualNodes, HashFunction hash) {
        this.defaultVirtualNodes = defaultVirtualNodes;
        this.hash = hash;
    }

    @Override
    public void addNode(N node) {
        addNode(node, 1);
    }

    @Override
    public synchronized void addNode(N node, int weight) {
        if (nodePositions.containsKey(node)) return;
        if (weight < 1) throw new IllegalArgumentException();
        int virtual = defaultVirtualNodes * weight;
        Set<Long> positions = new HashSet<>();
        for (int i = 0; i < virtual; i++) {
            long h = hash.hash(node.toString() + "#" + i);
            ring.put(h, node);
            positions.add(h);
        }
        nodePositions.put(node, positions);
    }

    @Override
    public synchronized void removeNode(N node) {
        Set<Long> positions = nodePositions.remove(node);
        if (positions == null) return;
        for (long p : positions) ring.remove(p);
    }

    @Override
    public synchronized N getNode(Object key) {
        if (ring.isEmpty()) return null;
        long h = hash.hash(key.toString());
        Map.Entry<Long, N> e = ring.ceilingEntry(h);
        return (e == null ? ring.firstEntry() : e).getValue();
    }

    @Override
    public synchronized List<N> getNodes(Object key, int count) {
        if (ring.isEmpty() || count <= 0) return Collections.emptyList();
        long h = hash.hash(key.toString());
        Set<N> seen = new LinkedHashSet<>();

        SortedMap<Long, N> tail = ring.tailMap(h);
        for (N n : tail.values()) {
            if (seen.size() >= count) break;
            seen.add(n);
        }
        if (seen.size() < count) {
            for (N n : ring.headMap(h).values()) {
                if (seen.size() >= count) break;
                seen.add(n);
            }
        }
        return new ArrayList<>(seen);
    }

    @Override
    public synchronized Set<N> getAllNodes() {
        return new HashSet<>(nodePositions.keySet());
    }

    @Override
    public synchronized int ringSize() {
        return ring.size();
    }
}
```
