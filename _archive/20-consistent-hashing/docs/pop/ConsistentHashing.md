# pop/ConsistentHashing.java

TreeMap 해시 링 + 가상 노드. MD5 해시. addNode/removeNode/getNode/getNodes.

```java
package com.datastructure.consistenthashing.pop;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ConsistentHashing {
    private final TreeMap<Long, String> ring = new TreeMap<>();
    private final Map<String, Set<Long>> nodePositions = new HashMap<>();
    private final int virtualNodeCount;

    public ConsistentHashing() {
        this(150);
    }

    public ConsistentHashing(int virtualNodeCount) {
        if (virtualNodeCount < 1) throw new IllegalArgumentException();
        this.virtualNodeCount = virtualNodeCount;
    }

    public synchronized void addNode(String node) {
        if (nodePositions.containsKey(node)) return;
        Set<Long> positions = new HashSet<>();
        for (int i = 0; i < virtualNodeCount; i++) {
            long h = hash(node + "#" + i);
            ring.put(h, node);
            positions.add(h);
        }
        nodePositions.put(node, positions);
    }

    public synchronized void removeNode(String node) {
        Set<Long> positions = nodePositions.remove(node);
        if (positions == null) return;
        for (long pos : positions) ring.remove(pos);
    }

    public synchronized String getNode(String key) {
        if (ring.isEmpty()) return null;
        long h = hash(key);
        Map.Entry<Long, String> e = ring.ceilingEntry(h);
        if (e == null) e = ring.firstEntry();
        return e.getValue();
    }

    public synchronized List<String> getNodes(String key, int count) {
        if (ring.isEmpty() || count <= 0) return Collections.emptyList();
        Set<String> seen = new LinkedHashSet<>();
        long h = hash(key);

        SortedMap<Long, String> tail = ring.tailMap(h);
        Iterator<String> it = tail.values().iterator();
        while (seen.size() < count && it.hasNext()) seen.add(it.next());
        if (seen.size() < count) {
            it = ring.headMap(h).values().iterator();
            while (seen.size() < count && it.hasNext()) seen.add(it.next());
        }
        return new ArrayList<>(seen);
    }

    public synchronized Set<String> getAllNodes() {
        return new HashSet<>(nodePositions.keySet());
    }

    public synchronized int ringSize() {
        return ring.size();
    }

    private long hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(key.getBytes(StandardCharsets.UTF_8));
            // 상위 8바이트를 long으로
            long h = 0;
            for (int i = 0; i < 8; i++) {
                h = (h << 8) | (digest[i] & 0xff);
            }
            return h & 0x7fffffffffffffffL; // 양수
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
```
