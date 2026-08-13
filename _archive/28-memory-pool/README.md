# 28. 메모리 풀 (Memory Pool)

## 📋 문제 정의

**고정 크기 블록 할당**과 **버디 시스템(Buddy System)**을 기반으로 한
메모리 풀 할당자를 구현하세요.

메모리 풀은 빈번한 메모리 할당/해제의 오버헤드를 줄이고,
메모리 단편화를 방지하는 핵심 시스템 기법입니다.

---

## 🎯 학습 목표

- 메모리 할당 알고리즘
- 버디 시스템 (Buddy System)
- 메모리 단편화 (내부/외부)
- Free List 관리
- 비트맵 기반 할당

---

## 📝 요구사항

### 핵심 개념

| 개념 | 설명 |
|------|------|
| **Memory Pool** | 미리 할당된 메모리 영역 |
| **Block** | 고정 크기의 할당 단위 |
| **Free List** | 사용 가능한 블록 목록 |
| **Buddy System** | 2의 거듭제곱 크기로 분할/병합 |
| **Fragmentation** | 내부(블록 내) / 외부(블록 간) 단편화 |

### 기본 연산

| 메서드 | 설명 |
|--------|------|
| `allocate(size)` | 메모리 할당 |
| `free(address)` | 메모리 해제 |
| `getUsedMemory()` | 사용 중인 메모리 |
| `getFreeMemory()` | 사용 가능한 메모리 |
| `defragment()` | 조각 모음 (선택) |

### 할당 전략

| 전략 | 설명 |
|------|------|
| **First Fit** | 첫 번째 적합한 블록 |
| **Best Fit** | 가장 작은 적합한 블록 |
| **Worst Fit** | 가장 큰 블록 |
| **Buddy System** | 2의 거듭제곱 분할 |

---

## 📊 입출력 예시

### 예제 1: 고정 크기 풀
```java
// 1024 바이트 풀, 64 바이트 블록
FixedSizePool pool = new FixedSizePool(1024, 64);
// 총 16개 블록 사용 가능

// 블록 할당
int addr1 = pool.allocate();  // 0
int addr2 = pool.allocate();  // 64
int addr3 = pool.allocate();  // 128

// 블록 해제
pool.free(addr2);  // 64번 주소 해제

// 재할당 (해제된 블록 재사용)
int addr4 = pool.allocate();  // 64 (재사용)
```

### 예제 2: 버디 시스템
```java
// 1024 바이트 버디 시스템
BuddyAllocator buddy = new BuddyAllocator(1024);

// 200 바이트 요청 → 256 바이트 블록 할당
int addr1 = buddy.allocate(200);  // 0 (256 바이트)

// 100 바이트 요청 → 128 바이트 블록 할당
int addr2 = buddy.allocate(100);  // 256 (128 바이트)

// 해제 시 버디와 병합
buddy.free(addr2);  // 해제
buddy.free(addr1);  // 해제 + 버디 병합 → 512 바이트 블록
```

### 예제 3: 버디 시스템 분할 과정
```
초기 상태: [1024]

allocate(200) → 256 바이트 필요
  1. [1024] → [512][512]
  2. [512][512] → [256][256][512]
  3. [256*][256][512]  (* = 할당됨)

allocate(100) → 128 바이트 필요
  4. [256*][256][512] → [256*][128][128][512]
  5. [256*][128*][128][512]

상태: [256*][128*][128][512]
      addr=0  addr=256  free  free
```

### 예제 4: 버디 병합 과정
```
상태: [256*][128*][128][512]

free(256) - 128 바이트 블록 해제
  1. [256*][128][128][512]
  2. 버디(128,128) 병합 → [256*][256][512]

free(0) - 256 바이트 블록 해제
  1. [256][256][512]
  2. 버디(256,256) 병합 → [512][512]
  3. 버디(512,512) 병합 → [1024]

최종: [1024] - 완전 병합됨
```

### 예제 5: 메모리 상태 시각화
```
┌────────────────────────────────────────────────┐
│                  Memory Pool (1024 bytes)       │
├────────────────────────────────────────────────┤
│                                                 │
│  [####][####][    ][    ][########][          ]│
│   64    64    64    64     128        512      │
│  used  used  free  free   used       free     │
│                                                 │
│  Used: 256 bytes (25%)                         │
│  Free: 768 bytes (75%)                         │
│  Fragmentation: 3 free blocks                  │
│                                                 │
└────────────────────────────────────────────────┘
```

---

## 🔍 핵심 개념

### 고정 크기 풀 구조
```java
// 비트맵 기반
class FixedSizePool {
    byte[] memory;        // 실제 메모리
    boolean[] allocated;  // 각 블록의 할당 상태
    int blockSize;
    int blockCount;
}

// Free List 기반
class FixedSizePool {
    byte[] memory;
    Deque<Integer> freeList;  // 사용 가능한 블록 주소
    int blockSize;
}
```

### 버디 시스템 구조
```java
class BuddyAllocator {
    byte[] memory;
    int totalSize;
    
    // 각 크기별 free list
    // freeLists[k] = 크기 2^k인 free 블록들의 주소
    Map<Integer, Set<Integer>> freeLists;
    
    // 할당된 블록의 크기 기록
    Map<Integer, Integer> allocatedBlocks;
}
```

### 버디 찾기
```java
// 주소 addr의 버디 주소 (크기 size인 경우)
int getBuddyAddress(int addr, int size) {
    return addr ^ size;  // XOR 연산
}

// 예: 주소 0, 크기 256의 버디 = 0 ^ 256 = 256
// 예: 주소 256, 크기 256의 버디 = 256 ^ 256 = 0
// 예: 주소 512, 크기 512의 버디 = 512 ^ 512 = 0
```

---

## 💡 힌트

### 고정 크기 풀
```java
public class FixedSizePool {
    private final byte[] memory;
    private final int blockSize;
    private final int blockCount;
    private final Deque<Integer> freeList;
    
    public FixedSizePool(int totalSize, int blockSize) {
        this.memory = new byte[totalSize];
        this.blockSize = blockSize;
        this.blockCount = totalSize / blockSize;
        this.freeList = new ArrayDeque<>();
        
        // 모든 블록을 free list에 추가
        for (int i = 0; i < blockCount; i++) {
            freeList.offer(i * blockSize);
        }
    }
    
    public int allocate() {
        if (freeList.isEmpty()) {
            throw new OutOfMemoryException();
        }
        return freeList.poll();
    }
    
    public void free(int address) {
        if (address % blockSize != 0 || address < 0 || 
            address >= memory.length) {
            throw new InvalidAddressException();
        }
        freeList.offer(address);
    }
}
```

### 버디 시스템
```java
public class BuddyAllocator {
    private final int totalSize;
    private final int minBlockSize;
    private final Map<Integer, Set<Integer>> freeLists;
    private final Map<Integer, Integer> allocatedBlocks;
    
    public BuddyAllocator(int totalSize, int minBlockSize) {
        this.totalSize = totalSize;
        this.minBlockSize = minBlockSize;
        this.freeLists = new HashMap<>();
        this.allocatedBlocks = new HashMap<>();
        
        // 초기: 전체 메모리가 하나의 free 블록
        freeLists.computeIfAbsent(totalSize, k -> new HashSet<>()).add(0);
    }
    
    public int allocate(int size) {
        int blockSize = nextPowerOfTwo(Math.max(size, minBlockSize));
        
        // 적합한 블록 찾기 또는 큰 블록 분할
        int addr = findOrSplitBlock(blockSize);
        
        if (addr == -1) {
            throw new OutOfMemoryException();
        }
        
        allocatedBlocks.put(addr, blockSize);
        return addr;
    }
    
    private int findOrSplitBlock(int size) {
        // 정확한 크기의 블록이 있으면 사용
        if (hasFreeBlock(size)) {
            return removeFreeBlock(size);
        }
        
        // 더 큰 블록을 분할
        for (int s = size * 2; s <= totalSize; s *= 2) {
            if (hasFreeBlock(s)) {
                int addr = removeFreeBlock(s);
                // 분할하여 하나는 free list에, 하나는 반환
                while (s > size) {
                    s /= 2;
                    addFreeBlock(s, addr + s);  // 버디를 free list에
                }
                return addr;
            }
        }
        
        return -1;  // 할당 실패
    }
    
    public void free(int address) {
        if (!allocatedBlocks.containsKey(address)) {
            throw new InvalidAddressException();
        }
        
        int size = allocatedBlocks.remove(address);
        mergeWithBuddy(address, size);
    }
    
    private void mergeWithBuddy(int addr, int size) {
        while (size < totalSize) {
            int buddyAddr = addr ^ size;  // 버디 주소
            
            // 버디가 free이면 병합
            if (freeLists.getOrDefault(size, Set.of()).contains(buddyAddr)) {
                removeFreeBlock(size, buddyAddr);
                addr = Math.min(addr, buddyAddr);
                size *= 2;
            } else {
                break;  // 버디가 사용 중이면 병합 중단
            }
        }
        
        addFreeBlock(size, addr);
    }
}
```

---

## ✅ 체크리스트

- [ ] 고정 크기 풀 구현
- [ ] Free List 관리
- [ ] 버디 시스템 구현
- [ ] 블록 분할 (Split)
- [ ] 블록 병합 (Merge)
- [ ] 메모리 사용량 추적
- [ ] 단편화 측정
- [ ] First/Best/Worst Fit (선택)

---

## 📚 참고

- Linux Kernel Buddy Allocator
- SLAB/SLUB Allocator
- jemalloc, tcmalloc
- Memory Management in Operating Systems
