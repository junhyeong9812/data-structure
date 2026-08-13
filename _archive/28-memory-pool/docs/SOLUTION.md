# 메모리 풀 풀이 해설

## 📌 핵심 아이디어

메모리 풀은 **미리 할당된 메모리**에서 블록을 관리합니다.
버디 시스템은 **2의 거듭제곱 크기**로 분할/병합하여 외부 단편화를 줄입니다.

**핵심 특징**:
- 할당/해제 오버헤드 최소화
- 메모리 단편화 관리
- 예측 가능한 성능

---

## 🔑 핵심 개념

### 1. 고정 크기 vs 가변 크기
```
고정 크기 풀:
┌──┬──┬──┬──┬──┬──┬──┬──┐
│64│64│64│64│64│64│64│64│  모든 블록 동일 크기
└──┴──┴──┴──┴──┴──┴──┴──┘

가변 크기 (버디 시스템):
┌────────────┬──────┬───┬───┐
│    512     │ 256  │128│128│  다양한 크기
└────────────┴──────┴───┴───┘
```

### 2. 버디 시스템의 분할/병합
```
분할 (Split):
[1024] 에서 200 바이트 요청

1. 1024 > 256 → 분할 필요
   [512][512]

2. 512 > 256 → 분할 필요
   [256][256][512]

3. 256 == 256 → 할당!
   [256*][256][512]

병합 (Merge):
[256*][256][512] 에서 256* 해제

1. 256* 해제 → [256][256][512]
2. 버디(256, 256) 확인 → 둘 다 free!
3. 병합 → [512][512]
4. 버디(512, 512) 확인 → 둘 다 free!
5. 병합 → [1024]
```

### 3. 버디 주소 계산
```java
// XOR로 버디 주소 계산
// 주소 addr, 크기 size의 버디 = addr XOR size

예시 (1024 바이트 메모리):
- 주소 0, 크기 512 → 버디 = 0 ^ 512 = 512
- 주소 512, 크기 512 → 버디 = 512 ^ 512 = 0
- 주소 0, 크기 256 → 버디 = 0 ^ 256 = 256
- 주소 256, 크기 256 → 버디 = 256 ^ 256 = 0
- 주소 512, 크기 256 → 버디 = 512 ^ 256 = 768

버디 쌍:
  크기 512: (0, 512)
  크기 256: (0, 256), (512, 768)
  크기 128: (0, 128), (256, 384), (512, 640), (768, 896)
```

---

## 📝 POP 구현 해설

### 고정 크기 풀 완전 구현
```java
public class FixedSizePool {
    private final byte[] memory;
    private final int blockSize;
    private final int blockCount;
    private final Deque<Integer> freeList;
    private final Set<Integer> allocatedAddresses;
    
    public FixedSizePool(int totalSize, int blockSize) {
        if (totalSize <= 0 || blockSize <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
        if (totalSize % blockSize != 0) {
            throw new IllegalArgumentException("Total size must be divisible by block size");
        }
        
        this.memory = new byte[totalSize];
        this.blockSize = blockSize;
        this.blockCount = totalSize / blockSize;
        this.freeList = new ArrayDeque<>();
        this.allocatedAddresses = new HashSet<>();
        
        // 모든 블록을 free list에 추가
        for (int i = 0; i < blockCount; i++) {
            freeList.offer(i * blockSize);
        }
    }
    
    public int allocate() {
        if (freeList.isEmpty()) {
            throw new OutOfMemoryException("No free blocks available");
        }
        
        int address = freeList.poll();
        allocatedAddresses.add(address);
        return address;
    }
    
    public void free(int address) {
        validateAddress(address);
        
        if (!allocatedAddresses.contains(address)) {
            throw new InvalidAddressException("Address not allocated: " + address);
        }
        
        allocatedAddresses.remove(address);
        freeList.offer(address);
    }
    
    public void write(int address, byte[] data) {
        validateAddress(address);
        
        if (!allocatedAddresses.contains(address)) {
            throw new InvalidAddressException("Address not allocated");
        }
        
        if (data.length > blockSize) {
            throw new IllegalArgumentException("Data exceeds block size");
        }
        
        System.arraycopy(data, 0, memory, address, data.length);
    }
    
    public byte[] read(int address, int length) {
        validateAddress(address);
        
        if (!allocatedAddresses.contains(address)) {
            throw new InvalidAddressException("Address not allocated");
        }
        
        if (length > blockSize) {
            throw new IllegalArgumentException("Length exceeds block size");
        }
        
        byte[] data = new byte[length];
        System.arraycopy(memory, address, data, 0, length);
        return data;
    }
    
    private void validateAddress(int address) {
        if (address < 0 || address >= memory.length) {
            throw new InvalidAddressException("Address out of bounds: " + address);
        }
        if (address % blockSize != 0) {
            throw new InvalidAddressException("Address not aligned: " + address);
        }
    }
    
    public int getUsedMemory() {
        return allocatedAddresses.size() * blockSize;
    }
    
    public int getFreeMemory() {
        return freeList.size() * blockSize;
    }
    
    public int getTotalMemory() {
        return memory.length;
    }
    
    public int getBlockSize() {
        return blockSize;
    }
    
    public int getFreeBlockCount() {
        return freeList.size();
    }
    
    public int getAllocatedBlockCount() {
        return allocatedAddresses.size();
    }
    
    public double getUtilization() {
        return (double) getUsedMemory() / getTotalMemory();
    }
}
```

### 버디 시스템 완전 구현
```java
public class BuddyAllocator {
    private final int totalSize;
    private final int minBlockSize;
    private final int maxOrder;  // log2(totalSize / minBlockSize)
    
    // 각 크기(order)별 free 블록들
    private final Map<Integer, Set<Integer>> freeLists;
    
    // 할당된 블록: 주소 → 크기
    private final Map<Integer, Integer> allocatedBlocks;
    
    public BuddyAllocator(int totalSize) {
        this(totalSize, 16);  // 기본 최소 블록 16 바이트
    }
    
    public BuddyAllocator(int totalSize, int minBlockSize) {
        if (!isPowerOfTwo(totalSize) || !isPowerOfTwo(minBlockSize)) {
            throw new IllegalArgumentException("Sizes must be power of 2");
        }
        
        this.totalSize = totalSize;
        this.minBlockSize = minBlockSize;
        this.maxOrder = log2(totalSize / minBlockSize);
        this.freeLists = new HashMap<>();
        this.allocatedBlocks = new HashMap<>();
        
        // 초기: 전체가 하나의 free 블록
        addFreeBlock(totalSize, 0);
    }
    
    public int allocate(int requestedSize) {
        if (requestedSize <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
        
        // 필요한 블록 크기 계산 (2의 거듭제곱으로 올림)
        int blockSize = nextPowerOfTwo(Math.max(requestedSize, minBlockSize));
        
        if (blockSize > totalSize) {
            throw new OutOfMemoryException("Requested size too large");
        }
        
        // 블록 찾기 또는 분할
        int address = findOrSplitBlock(blockSize);
        
        if (address == -1) {
            throw new OutOfMemoryException("Not enough memory");
        }
        
        allocatedBlocks.put(address, blockSize);
        return address;
    }
    
    private int findOrSplitBlock(int targetSize) {
        // 정확한 크기의 블록이 있으면 사용
        if (hasFreeBlock(targetSize)) {
            return removeFreeBlock(targetSize);
        }
        
        // 더 큰 블록을 분할
        for (int size = targetSize * 2; size <= totalSize; size *= 2) {
            if (hasFreeBlock(size)) {
                int address = removeFreeBlock(size);
                
                // 분할하여 버디는 free list에 추가
                while (size > targetSize) {
                    size /= 2;
                    int buddyAddress = address + size;
                    addFreeBlock(size, buddyAddress);
                }
                
                return address;
            }
        }
        
        return -1;  // 할당 실패
    }
    
    public void free(int address) {
        if (!allocatedBlocks.containsKey(address)) {
            throw new InvalidAddressException("Address not allocated: " + address);
        }
        
        int size = allocatedBlocks.remove(address);
        mergeWithBuddy(address, size);
    }
    
    private void mergeWithBuddy(int address, int size) {
        while (size < totalSize) {
            int buddyAddress = getBuddyAddress(address, size);
            
            // 버디가 같은 크기의 free 블록이면 병합
            Set<Integer> freeSet = freeLists.get(size);
            if (freeSet != null && freeSet.contains(buddyAddress)) {
                freeSet.remove(buddyAddress);
                if (freeSet.isEmpty()) {
                    freeLists.remove(size);
                }
                
                // 병합: 더 작은 주소가 새 블록의 시작
                address = Math.min(address, buddyAddress);
                size *= 2;
            } else {
                break;  // 버디가 사용 중이거나 다른 크기면 중단
            }
        }
        
        addFreeBlock(size, address);
    }
    
    private int getBuddyAddress(int address, int size) {
        return address ^ size;
    }
    
    private boolean hasFreeBlock(int size) {
        Set<Integer> freeSet = freeLists.get(size);
        return freeSet != null && !freeSet.isEmpty();
    }
    
    private void addFreeBlock(int size, int address) {
        freeLists.computeIfAbsent(size, k -> new HashSet<>()).add(address);
    }
    
    private int removeFreeBlock(int size) {
        Set<Integer> freeSet = freeLists.get(size);
        if (freeSet == null || freeSet.isEmpty()) {
            return -1;
        }
        
        int address = freeSet.iterator().next();
        freeSet.remove(address);
        
        if (freeSet.isEmpty()) {
            freeLists.remove(size);
        }
        
        return address;
    }
    
    // 유틸리티 메서드
    private static boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }
    
    private static int nextPowerOfTwo(int n) {
        if (isPowerOfTwo(n)) return n;
        int power = 1;
        while (power < n) {
            power *= 2;
        }
        return power;
    }
    
    private static int log2(int n) {
        return (int) (Math.log(n) / Math.log(2));
    }
    
    // 상태 조회
    public int getUsedMemory() {
        return allocatedBlocks.values().stream()
            .mapToInt(Integer::intValue)
            .sum();
    }
    
    public int getFreeMemory() {
        return totalSize - getUsedMemory();
    }
    
    public int getTotalMemory() {
        return totalSize;
    }
    
    public int getAllocatedBlockCount() {
        return allocatedBlocks.size();
    }
    
    public int getFreeBlockCount() {
        return freeLists.values().stream()
            .mapToInt(Set::size)
            .sum();
    }
    
    // 외부 단편화 측정: 가장 큰 free 블록 / 전체 free 메모리
    public double getExternalFragmentation() {
        int freeMemory = getFreeMemory();
        if (freeMemory == 0) return 0;
        
        int largestFreeBlock = freeLists.keySet().stream()
            .filter(this::hasFreeBlock)
            .max(Integer::compare)
            .orElse(0);
        
        return 1.0 - ((double) largestFreeBlock / freeMemory);
    }
    
    // 디버그 출력
    public void printState() {
        System.out.println("=== Buddy Allocator State ===");
        System.out.println("Total: " + totalSize + " bytes");
        System.out.println("Used: " + getUsedMemory() + " bytes");
        System.out.println("Free: " + getFreeMemory() + " bytes");
        System.out.println("\nAllocated blocks:");
        allocatedBlocks.forEach((addr, size) -> 
            System.out.println("  " + addr + ": " + size + " bytes"));
        System.out.println("\nFree lists:");
        freeLists.forEach((size, addrs) -> 
            System.out.println("  " + size + ": " + addrs));
    }
}
```

---

## ⏱️ 복잡도 분석

### 고정 크기 풀

| 연산 | 시간복잡도 |
|------|-----------|
| allocate | O(1) |
| free | O(1) |
| getUsedMemory | O(1) |

### 버디 시스템

| 연산 | 시간복잡도 |
|------|-----------|
| allocate | O(log n) |
| free | O(log n) |
| getUsedMemory | O(k) |

n = 총 메모리 / 최소 블록
k = 할당된 블록 수

---

## ❌ 흔한 실수

### 1. 버디 주소 계산 오류
```java
// 잘못됨: 단순 덧셈/뺄셈
int buddy = address + size;  // 항상 맞지 않음

// 올바름: XOR 연산
int buddy = address ^ size;

// 예: 주소 256, 크기 256
// 잘못: 256 + 256 = 512 (실제 버디는 0)
// 올바: 256 ^ 256 = 0 ✓
```

### 2. 병합 조건 누락
```java
// 잘못됨: 무조건 병합 시도
while (size < totalSize) {
    int buddy = address ^ size;
    // 버디가 할당 중이면?
    merge();  // 오류!
}

// 올바름: free인지 확인
while (size < totalSize) {
    int buddy = address ^ size;
    if (!isFree(buddy, size)) break;  // 확인!
    merge();
}
```

### 3. 2의 거듭제곱 올림 오류
```java
// 잘못됨: 정확한 2의 거듭제곱 확인 안 함
int blockSize = requestedSize;

// 올바름: 다음 2의 거듭제곱으로 올림
int blockSize = nextPowerOfTwo(requestedSize);
// 200 → 256, 100 → 128, 64 → 64
```

---

## 🔗 관련 문제

- 운영체제 메모리 관리
- 가비지 컬렉션
- 메모리 매핑
- 캐시 최적화
