# oop/MemoryAllocator.java

메모리 할당자 인터페이스 + 객체 풀(`ObjectPool<T>`) 구현.

```java
package com.datastructure.memorypool.oop;

public interface MemoryAllocator {
    int allocate(int size);
    void free(int address);

    int getUsedMemory();
    int getFreeMemory();
    int getTotalSize();
}
```

---

# oop/ObjectPool.java

```java
package com.datastructure.memorypool.oop;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ObjectPool<T> {
    private final Deque<T> pool = new ArrayDeque<>();
    private final Supplier<T> factory;
    private final Consumer<T> resetter;
    private final int maxSize;
    private int created;

    public ObjectPool(Supplier<T> factory) {
        this(factory, t -> {}, Integer.MAX_VALUE);
    }

    public ObjectPool(Supplier<T> factory, Consumer<T> resetter, int maxSize) {
        this.factory = factory;
        this.resetter = resetter;
        this.maxSize = maxSize;
    }

    public synchronized T acquire() {
        T obj = pool.poll();
        if (obj != null) return obj;
        created++;
        return factory.get();
    }

    public synchronized void release(T obj) {
        if (obj == null) return;
        if (pool.size() >= maxSize) return;
        resetter.accept(obj);
        pool.offer(obj);
    }

    public synchronized int availableCount() {
        return pool.size();
    }

    public synchronized int totalCreated() {
        return created;
    }
}
```

---

# oop/BuddyMemoryAllocator.java

```java
package com.datastructure.memorypool.oop;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class BuddyMemoryAllocator implements MemoryAllocator {

    private final int totalSize;
    private final int minBlockSize;
    private final Map<Integer, TreeSet<Integer>> freeLists = new HashMap<>();
    private final Map<Integer, Integer> allocated = new HashMap<>();

    public BuddyMemoryAllocator(int totalSize, int minBlockSize) {
        if (Integer.bitCount(totalSize) != 1 || Integer.bitCount(minBlockSize) != 1) {
            throw new IllegalArgumentException("must be power of two");
        }
        this.totalSize = totalSize;
        this.minBlockSize = minBlockSize;
        freeLists.computeIfAbsent(totalSize, k -> new TreeSet<>()).add(0);
    }

    @Override
    public int allocate(int size) {
        int needed = next2(Math.max(size, minBlockSize));
        if (needed > totalSize) throw new RuntimeException("OOM");

        TreeSet<Integer> exact = freeLists.get(needed);
        if (exact != null && !exact.isEmpty()) {
            int addr = exact.pollFirst();
            allocated.put(addr, needed);
            return addr;
        }
        for (int s = needed * 2; s <= totalSize; s *= 2) {
            TreeSet<Integer> set = freeLists.get(s);
            if (set != null && !set.isEmpty()) {
                int addr = set.pollFirst();
                while (s > needed) {
                    s /= 2;
                    freeLists.computeIfAbsent(s, k -> new TreeSet<>()).add(addr + s);
                }
                allocated.put(addr, needed);
                return addr;
            }
        }
        throw new RuntimeException("OOM");
    }

    @Override
    public void free(int address) {
        Integer size = allocated.remove(address);
        if (size == null) throw new IllegalArgumentException();
        int addr = address;
        int sz = size;
        while (sz < totalSize) {
            int buddy = addr ^ sz;
            TreeSet<Integer> peers = freeLists.get(sz);
            if (peers == null || !peers.remove(buddy)) break;
            addr = Math.min(addr, buddy);
            sz *= 2;
        }
        freeLists.computeIfAbsent(sz, k -> new TreeSet<>()).add(addr);
    }

    @Override public int getUsedMemory() {
        return allocated.values().stream().mapToInt(Integer::intValue).sum();
    }
    @Override public int getFreeMemory() { return totalSize - getUsedMemory(); }
    @Override public int getTotalSize() { return totalSize; }

    private static int next2(int n) {
        if (n <= 1) return 1;
        int p = Integer.highestOneBit(n);
        return p == n ? n : p << 1;
    }
}
```
