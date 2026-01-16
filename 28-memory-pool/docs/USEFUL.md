# 메모리 풀 구현에 유용한 Java API

## 📦 비트 연산

### 2의 거듭제곱 확인
```java
// n이 2의 거듭제곱인지 확인
public static boolean isPowerOfTwo(int n) {
    return n > 0 && (n & (n - 1)) == 0;
}

// 예: 8 = 1000, 8-1 = 0111
// 8 & 7 = 0 → true

// 예: 6 = 0110, 6-1 = 0101
// 6 & 5 = 0100 ≠ 0 → false
```

### 다음 2의 거듭제곱
```java
// 방법 1: 반복
public static int nextPowerOfTwo(int n) {
    if (n <= 0) return 1;
    if (isPowerOfTwo(n)) return n;
    
    int power = 1;
    while (power < n) {
        power <<= 1;  // power *= 2
    }
    return power;
}

// 방법 2: Integer.highestOneBit
public static int nextPowerOfTwo(int n) {
    if (n <= 0) return 1;
    int highest = Integer.highestOneBit(n);
    return (highest == n) ? n : highest << 1;
}

// 방법 3: 비트 조작
public static int nextPowerOfTwo(int n) {
    n--;
    n |= n >> 1;
    n |= n >> 2;
    n |= n >> 4;
    n |= n >> 8;
    n |= n >> 16;
    n++;
    return n;
}
```

### log2 계산
```java
// 방법 1: Math.log
public static int log2(int n) {
    return (int) (Math.log(n) / Math.log(2));
}

// 방법 2: numberOfLeadingZeros
public static int log2(int n) {
    return 31 - Integer.numberOfLeadingZeros(n);
}

// 방법 3: numberOfTrailingZeros (2의 거듭제곱일 때)
public static int log2PowerOfTwo(int n) {
    return Integer.numberOfTrailingZeros(n);
}
```

### XOR 버디 계산
```java
// 버디 주소 계산
public static int getBuddyAddress(int address, int size) {
    return address ^ size;
}

// 예시:
// getBuddyAddress(0, 512) = 512
// getBuddyAddress(512, 512) = 0
// getBuddyAddress(0, 256) = 256
// getBuddyAddress(256, 256) = 0
```

---

## 📊 컬렉션

### Deque (Free List용)
```java
import java.util.ArrayDeque;
import java.util.Deque;

Deque<Integer> freeList = new ArrayDeque<>();

// 추가
freeList.offer(address);     // 뒤에 추가
freeList.offerFirst(address);  // 앞에 추가 (최근 해제 블록 우선)

// 제거
Integer addr = freeList.poll();  // 앞에서 제거
Integer addr = freeList.pollLast();  // 뒤에서 제거

// 확인
boolean empty = freeList.isEmpty();
int size = freeList.size();
```

### Map (할당 기록)
```java
import java.util.HashMap;
import java.util.Map;

// 할당된 블록: 주소 → 크기
Map<Integer, Integer> allocatedBlocks = new HashMap<>();

// 할당 기록
allocatedBlocks.put(address, blockSize);

// 해제 시 크기 조회 및 제거
int size = allocatedBlocks.remove(address);

// 존재 여부
boolean allocated = allocatedBlocks.containsKey(address);
```

### Set (Free 블록 관리)
```java
import java.util.HashSet;
import java.util.Set;

// 각 크기별 free 블록
Map<Integer, Set<Integer>> freeLists = new HashMap<>();

// 추가
freeLists.computeIfAbsent(size, k -> new HashSet<>()).add(address);

// 제거
Set<Integer> freeSet = freeLists.get(size);
if (freeSet != null) {
    freeSet.remove(address);
    if (freeSet.isEmpty()) {
        freeLists.remove(size);
    }
}

// 존재 여부
boolean hasFreeBlock = freeLists.getOrDefault(size, Set.of()).contains(address);
```

---

## 🔢 배열 복사

### System.arraycopy
```java
byte[] memory = new byte[1024];
byte[] data = "Hello".getBytes();

// 메모리에 쓰기
System.arraycopy(data, 0, memory, address, data.length);
// arraycopy(src, srcPos, dest, destPos, length)

// 메모리에서 읽기
byte[] result = new byte[length];
System.arraycopy(memory, address, result, 0, length);
```

### Arrays.copyOfRange
```java
import java.util.Arrays;

byte[] memory = new byte[1024];

// 범위 복사
byte[] block = Arrays.copyOfRange(memory, start, end);

// 배열 채우기
Arrays.fill(memory, address, address + size, (byte) 0);
```

---

## 🧪 테스트

### AssertJ
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldAllocateAndFreeBlock() {
    FixedSizePool pool = new FixedSizePool(1024, 64);
    
    int addr = pool.allocate();
    
    assertThat(addr).isEqualTo(0);
    assertThat(pool.getAllocatedBlockCount()).isEqualTo(1);
    
    pool.free(addr);
    
    assertThat(pool.getAllocatedBlockCount()).isEqualTo(0);
}

@Test
void shouldReuseFreedBlock() {
    FixedSizePool pool = new FixedSizePool(1024, 64);
    
    int addr1 = pool.allocate();
    int addr2 = pool.allocate();
    
    pool.free(addr1);
    
    int addr3 = pool.allocate();
    
    assertThat(addr3).isEqualTo(addr1);  // 재사용
}

@Test
void shouldMergeBuddies() {
    BuddyAllocator buddy = new BuddyAllocator(1024, 64);
    
    int addr1 = buddy.allocate(200);  // 256 바이트 블록
    int addr2 = buddy.allocate(100);  // 128 바이트 블록
    
    buddy.free(addr2);
    buddy.free(addr1);
    
    // 완전히 병합되어 1024 블록 하나
    assertThat(buddy.getFreeBlockCount()).isEqualTo(1);
}

@Test
void shouldThrowWhenOutOfMemory() {
    FixedSizePool pool = new FixedSizePool(128, 64);
    
    pool.allocate();  // 0
    pool.allocate();  // 64
    
    assertThatThrownBy(() -> pool.allocate())
        .isInstanceOf(OutOfMemoryException.class);
}
```

---

## 📚 Java 21 관련

### Record
```java
// 블록 정보
public record Block(int address, int size, boolean allocated) {}

// 메모리 상태
public record MemoryStats(
    int totalMemory,
    int usedMemory,
    int freeMemory,
    int allocatedBlocks,
    int freeBlocks,
    double utilization,
    double fragmentation
) {}
```

### Pattern Matching
```java
public void processBlock(Object block) {
    switch (block) {
        case FreeBlock f -> addToFreeList(f);
        case AllocatedBlock a -> trackAllocation(a);
        default -> throw new IllegalArgumentException();
    }
}
```

### Sealed Classes
```java
public sealed interface MemoryBlock permits FreeBlock, AllocatedBlock {
    int address();
    int size();
}

public record FreeBlock(int address, int size) implements MemoryBlock {}
public record AllocatedBlock(int address, int size, long allocatedAt) implements MemoryBlock {}
```

---

## ⚡ 성능 팁

### 1. 비트맵 기반 할당 (고정 크기)
```java
// boolean 배열 대신 비트맵 사용
long[] bitmap;  // 64 블록당 1 long

// 블록 할당 상태 확인
boolean isAllocated(int blockIndex) {
    int wordIndex = blockIndex / 64;
    int bitIndex = blockIndex % 64;
    return (bitmap[wordIndex] & (1L << bitIndex)) != 0;
}

// 블록 할당
void setAllocated(int blockIndex) {
    int wordIndex = blockIndex / 64;
    int bitIndex = blockIndex % 64;
    bitmap[wordIndex] |= (1L << bitIndex);
}

// 첫 번째 free 블록 찾기
int findFirstFree() {
    for (int i = 0; i < bitmap.length; i++) {
        if (bitmap[i] != -1L) {  // not all 1s
            return i * 64 + Long.numberOfTrailingZeros(~bitmap[i]);
        }
    }
    return -1;
}
```

### 2. LIFO Free List (캐시 친화적)
```java
// 최근 해제된 블록을 먼저 재사용 (캐시에 있을 가능성 높음)
Deque<Integer> freeList = new ArrayDeque<>();

void free(int address) {
    freeList.addFirst(address);  // 앞에 추가
}

int allocate() {
    return freeList.removeFirst();  // 앞에서 제거
}
```

### 3. 크기별 풀 분리
```java
// Slab Allocator 스타일
Map<Integer, FixedSizePool> pools = new HashMap<>();

// 16, 32, 64, 128, ... 바이트 풀
for (int size = 16; size <= 4096; size *= 2) {
    pools.put(size, new FixedSizePool(1024 * 1024, size));
}

int allocate(int size) {
    int poolSize = nextPowerOfTwo(size);
    return pools.get(poolSize).allocate();
}
```

---

## 🔀 예외 클래스
```java
public class OutOfMemoryException extends RuntimeException {
    public OutOfMemoryException(String message) {
        super(message);
    }
}

public class InvalidAddressException extends RuntimeException {
    public InvalidAddressException(String message) {
        super(message);
    }
}

public class DoubleFreeException extends RuntimeException {
    public DoubleFreeException(String message) {
        super(message);
    }
}
```
