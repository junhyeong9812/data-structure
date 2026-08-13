# pop/BuddyAllocator.java

버디 시스템. 2^k 단위 분할/병합. allocate/free.

```java
package com.datastructure.memorypool.pop;

import java.util.*;

public class BuddyAllocator {

    public static class OutOfMemoryException extends RuntimeException {
        public OutOfMemoryException() { super("OOM"); }
    }

    private final int totalSize;
    private final int minBlockSize;
    private final Map<Integer, TreeSet<Integer>> freeLists = new HashMap<>();
    private final Map<Integer, Integer> allocated = new HashMap<>();

    public BuddyAllocator(int totalSize) {
        this(totalSize, 16);
    }

    public BuddyAllocator(int totalSize, int minBlockSize) {
        if (Integer.bitCount(totalSize) != 1) {
            throw new IllegalArgumentException("totalSize must be power of two");
        }
        if (Integer.bitCount(minBlockSize) != 1) {
            throw new IllegalArgumentException("minBlockSize must be power of two");
        }
        this.totalSize = totalSize;
        this.minBlockSize = minBlockSize;
        addFree(totalSize, 0);
    }

    public int allocate(int size) {
        int needed = nextPowerOfTwo(Math.max(size, minBlockSize));
        if (needed > totalSize) throw new OutOfMemoryException();

        // 정확한 사이즈 free 블록 있으면 사용
        TreeSet<Integer> exact = freeLists.get(needed);
        if (exact != null && !exact.isEmpty()) {
            int addr = exact.pollFirst();
            allocated.put(addr, needed);
            return addr;
        }

        // 더 큰 블록을 찾아 분할
        for (int s = needed * 2; s <= totalSize; s *= 2) {
            TreeSet<Integer> set = freeLists.get(s);
            if (set != null && !set.isEmpty()) {
                int addr = set.pollFirst();
                while (s > needed) {
                    s /= 2;
                    addFree(s, addr + s); // 오른쪽 절반을 free 등록
                }
                allocated.put(addr, needed);
                return addr;
            }
        }
        throw new OutOfMemoryException();
    }

    public void free(int address) {
        Integer size = allocated.remove(address);
        if (size == null) throw new IllegalArgumentException("Not allocated: " + address);

        // 버디 병합
        int curAddr = address;
        int curSize = size;
        while (curSize < totalSize) {
            int buddy = curAddr ^ curSize;
            TreeSet<Integer> peers = freeLists.get(curSize);
            if (peers == null || !peers.remove(buddy)) {
                break;
            }
            curAddr = Math.min(curAddr, buddy);
            curSize *= 2;
        }
        addFree(curSize, curAddr);
    }

    public int getUsedMemory() {
        return allocated.values().stream().mapToInt(Integer::intValue).sum();
    }

    public int getFreeMemory() {
        return totalSize - getUsedMemory();
    }

    public int getTotalSize() {
        return totalSize;
    }

    private void addFree(int size, int addr) {
        freeLists.computeIfAbsent(size, k -> new TreeSet<>()).add(addr);
    }

    private static int nextPowerOfTwo(int n) {
        if (n <= 1) return 1;
        int p = Integer.highestOneBit(n);
        return p == n ? n : p << 1;
    }
}
```
