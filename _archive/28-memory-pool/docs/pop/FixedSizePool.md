# pop/FixedSizePool.java

고정 크기 블록 풀. Free List 기반 O(1) allocate/free.

```java
package com.datastructure.memorypool.pop;

import java.util.ArrayDeque;
import java.util.Deque;

public class FixedSizePool {

    public static class OutOfMemoryException extends RuntimeException {
        public OutOfMemoryException() { super("Pool exhausted"); }
    }

    public static class InvalidAddressException extends RuntimeException {
        public InvalidAddressException(int addr) { super("Invalid: " + addr); }
    }

    private final byte[] memory;
    private final int blockSize;
    private final int blockCount;
    private final Deque<Integer> freeList;
    private final boolean[] allocated;

    public FixedSizePool(int totalSize, int blockSize) {
        if (totalSize <= 0 || blockSize <= 0 || totalSize % blockSize != 0) {
            throw new IllegalArgumentException();
        }
        this.memory = new byte[totalSize];
        this.blockSize = blockSize;
        this.blockCount = totalSize / blockSize;
        this.freeList = new ArrayDeque<>();
        this.allocated = new boolean[blockCount];
        for (int i = 0; i < blockCount; i++) freeList.offer(i * blockSize);
    }

    public int allocate() {
        Integer addr = freeList.poll();
        if (addr == null) throw new OutOfMemoryException();
        allocated[addr / blockSize] = true;
        return addr;
    }

    public void free(int address) {
        if (address < 0 || address >= memory.length || address % blockSize != 0) {
            throw new InvalidAddressException(address);
        }
        int idx = address / blockSize;
        if (!allocated[idx]) throw new InvalidAddressException(address);
        allocated[idx] = false;
        freeList.offer(address);
    }

    public byte[] getMemory() { return memory; }
    public int getBlockSize() { return blockSize; }
    public int getBlockCount() { return blockCount; }

    public int getUsedMemory() {
        return (blockCount - freeList.size()) * blockSize;
    }

    public int getFreeMemory() {
        return freeList.size() * blockSize;
    }
}
```
