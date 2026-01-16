# 블룸 필터 풀이 해설

## 📌 핵심 아이디어

블룸 필터는 **비트 배열**과 **여러 개의 해시 함수**를 사용하여
원소의 존재 여부를 확률적으로 판단합니다.

**핵심 특성**:
- 거짓 음성 없음: "없다"고 하면 **확실히** 없음
- 거짓 양성 가능: "있다"고 해도 **없을 수** 있음
- 삭제 불가 (기본 블룸 필터)

---

## 🔑 핵심 개념

### 1. 구조
```
비트 배열 (m bits):
[0][0][1][0][0][1][0][0][1][0][0][0][1][0][0][0]
       ↑        ↑        ↑           ↑
       h1       h2       h3          h1 (다른 원소)

k개의 해시 함수: h1, h2, h3, ...
```

### 2. 추가 연산
```
add("apple"):
    h1("apple") mod m → index 2  → bits[2] = 1
    h2("apple") mod m → index 5  → bits[5] = 1
    h3("apple") mod m → index 8  → bits[8] = 1

비트 배열:
[0][0][1][0][0][1][0][0][1][0][0][0][0][0][0][0]
       ↑        ↑        ↑
```

### 3. 검색 연산
```
mightContain("apple"):
    bits[2] == 1? ✓
    bits[5] == 1? ✓
    bits[8] == 1? ✓
    → true (모두 1이면 있을 수도 있음)

mightContain("grape"):
    h1("grape") → index 3
    bits[3] == 0? ✗
    → false (하나라도 0이면 확실히 없음)
```

### 4. 거짓 양성 발생 원인
```
add("apple"):  h1=2, h2=5, h3=8
add("banana"): h1=5, h2=8, h3=12

mightContain("cherry"): h1=2, h2=8, h3=12
    bits[2] = 1 (apple이 설정)
    bits[8] = 1 (banana가 설정)
    bits[12] = 1 (banana가 설정)
    → true (거짓 양성! cherry는 추가된 적 없음)
```

---

## 📝 POP 구현 해설
```java
public class BloomFilter {
    private final BitSet bits;
    private final int numBits;        // m
    private final int numHashFunctions;  // k
    private int insertedCount;
    
    // 예상 원소 수와 거짓 양성 확률로 생성
    public BloomFilter(int expectedElements, double falsePositiveProb) {
        this.numBits = optimalNumBits(expectedElements, falsePositiveProb);
        this.numHashFunctions = optimalNumHashFunctions(expectedElements, numBits);
        this.bits = new BitSet(numBits);
        this.insertedCount = 0;
    }
    
    // 직접 크기 지정
    public BloomFilter(int numBits, int numHashFunctions) {
        this.numBits = numBits;
        this.numHashFunctions = numHashFunctions;
        this.bits = new BitSet(numBits);
        this.insertedCount = 0;
    }
    
    // 최적 비트 수 계산
    // m = -n * ln(p) / (ln2)^2
    private static int optimalNumBits(int n, double p) {
        return (int) Math.ceil(-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }
    
    // 최적 해시 함수 수 계산
    // k = (m/n) * ln2
    private static int optimalNumHashFunctions(int n, int m) {
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }
    
    // 원소 추가
    public void add(String element) {
        int[] hashes = getHashes(element);
        for (int hash : hashes) {
            bits.set(hash);
        }
        insertedCount++;
    }
    
    // 원소 존재 가능성 확인
    public boolean mightContain(String element) {
        int[] hashes = getHashes(element);
        for (int hash : hashes) {
            if (!bits.get(hash)) {
                return false;  // 하나라도 0이면 확실히 없음
            }
        }
        return true;  // 모두 1이면 있을 수도 있음
    }
    
    // 해시 값들 계산 (Kirsch-Mitzenmacher 최적화)
    private int[] getHashes(String element) {
        int[] result = new int[numHashFunctions];
        
        int hash1 = murmurHash(element, 0);
        int hash2 = murmurHash(element, hash1);
        
        for (int i = 0; i < numHashFunctions; i++) {
            int combinedHash = hash1 + i * hash2;
            // 음수 처리 및 범위 조정
            result[i] = Math.abs(combinedHash % numBits);
        }
        
        return result;
    }
    
    // 간단한 MurmurHash 구현
    private int murmurHash(String data, int seed) {
        int h = seed;
        for (char c : data.toCharArray()) {
            h ^= c;
            h *= 0x5bd1e995;
            h ^= h >>> 15;
        }
        return h;
    }
    
    // 예상 거짓 양성 확률
    // FPP = (1 - e^(-kn/m))^k
    public double expectedFpp() {
        double exponent = -numHashFunctions * insertedCount / (double) numBits;
        return Math.pow(1 - Math.exp(exponent), numHashFunctions);
    }
    
    // 설정된 비트 개수
    public int bitCount() {
        return bits.cardinality();
    }
    
    // 대략적인 원소 개수 추정
    // n* = -(m/k) * ln(1 - X/m) where X = 설정된 비트 수
    public int approximateCount() {
        int setBits = bits.cardinality();
        if (setBits == 0) return 0;
        if (setBits >= numBits) return Integer.MAX_VALUE;
        
        double ratio = (double) setBits / numBits;
        return (int) Math.round(-(double) numBits / numHashFunctions 
                               * Math.log(1 - ratio));
    }
    
    public void clear() {
        bits.clear();
        insertedCount = 0;
    }
    
    public int size() {
        return numBits;
    }
    
    public int hashFunctionCount() {
        return numHashFunctions;
    }
}
```

---

## 📝 OOP 구현 (제네릭)
```java
public interface ProbabilisticSet<E> {
    void add(E element);
    boolean mightContain(E element);
    double expectedFpp();
    void clear();
}

public class BloomFilter<E> implements ProbabilisticSet<E> {
    private final BitSet bits;
    private final int numBits;
    private final int numHashFunctions;
    private final HashStrategy<E> hashStrategy;
    private int count;
    
    @FunctionalInterface
    public interface HashStrategy<E> {
        int hash(E element, int seed);
    }
    
    // 기본 해시 전략 (Object.hashCode 기반)
    public static <E> HashStrategy<E> defaultStrategy() {
        return (element, seed) -> {
            int h = seed ^ element.hashCode();
            h *= 0x5bd1e995;
            h ^= h >>> 15;
            return h;
        };
    }
    
    public BloomFilter(int expectedElements, double fpp) {
        this(expectedElements, fpp, defaultStrategy());
    }
    
    public BloomFilter(int expectedElements, double fpp, HashStrategy<E> strategy) {
        this.numBits = optimalBits(expectedElements, fpp);
        this.numHashFunctions = optimalHashFunctions(expectedElements, numBits);
        this.bits = new BitSet(numBits);
        this.hashStrategy = strategy;
        this.count = 0;
    }
    
    @Override
    public void add(E element) {
        Objects.requireNonNull(element);
        for (int index : computeIndices(element)) {
            bits.set(index);
        }
        count++;
    }
    
    @Override
    public boolean mightContain(E element) {
        Objects.requireNonNull(element);
        for (int index : computeIndices(element)) {
            if (!bits.get(index)) {
                return false;
            }
        }
        return true;
    }
    
    private int[] computeIndices(E element) {
        int[] indices = new int[numHashFunctions];
        int h1 = hashStrategy.hash(element, 0);
        int h2 = hashStrategy.hash(element, h1);
        
        for (int i = 0; i < numHashFunctions; i++) {
            indices[i] = Math.abs((h1 + i * h2) % numBits);
        }
        return indices;
    }
    
    private static int optimalBits(int n, double p) {
        return (int) Math.ceil(-n * Math.log(p) / (Math.log(2) * Math.log(2)));
    }
    
    private static int optimalHashFunctions(int n, int m) {
        return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
    }
    
    @Override
    public double expectedFpp() {
        double exp = -(double) numHashFunctions * count / numBits;
        return Math.pow(1 - Math.exp(exp), numHashFunctions);
    }
    
    @Override
    public void clear() {
        bits.clear();
        count = 0;
    }
}
```

---

## 📝 Counting Bloom Filter
```java
// 삭제를 지원하는 카운팅 블룸 필터
public class CountingBloomFilter<E> {
    private final int[] counters;  // 비트 대신 카운터 사용
    private final int numBits;
    private final int numHashFunctions;
    
    public void add(E element) {
        for (int index : computeIndices(element)) {
            counters[index]++;  // 증가
        }
    }
    
    public boolean mightContain(E element) {
        for (int index : computeIndices(element)) {
            if (counters[index] == 0) {
                return false;
            }
        }
        return true;
    }
    
    // 삭제 지원!
    public void remove(E element) {
        if (!mightContain(element)) return;
        
        for (int index : computeIndices(element)) {
            if (counters[index] > 0) {
                counters[index]--;  // 감소
            }
        }
    }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 | 공간복잡도 |
|------|-----------|-----------|
| add | O(k) | - |
| mightContain | O(k) | - |
| clear | O(m) | - |
| 전체 | - | O(m) bits |

*k = 해시 함수 개수 (보통 작은 상수)
*m = 비트 배열 크기

### 공간 효율성
```
HashSet vs BloomFilter (100만 원소, 1% FPP):

HashSet: ~40MB (객체 오버헤드 포함)
BloomFilter: ~1.2MB (9.6M bits ≈ 1.2MB)

약 30배 공간 절약!
```

---

## ❌ 흔한 실수

### 1. 음수 해시 값 처리
```java
// 잘못됨: 음수 모듈러
int index = hash % numBits;  // hash가 음수면 index도 음수!

// 올바름: 절대값 사용
int index = Math.abs(hash % numBits);

// 또는: 비트 마스킹
int index = (hash & 0x7FFFFFFF) % numBits;
```

### 2. 해시 함수 품질
```java
// 잘못됨: 단순 해시
int hash = element.hashCode() + i;  // 상관관계 높음

// 올바름: 독립적인 해시
int h1 = murmurHash(element, 0);
int h2 = murmurHash(element, h1);
int hash = h1 + i * h2;  // Kirsch-Mitzenmacher
```

### 3. 파라미터 계산 오류
```java
// 잘못됨: 정수 나눗셈
int k = m / n * Math.log(2);  // m/n이 정수 나눗셈됨

// 올바름: 부동소수점 사용
int k = (int) Math.round((double) m / n * Math.log(2));
```

---

## 🔗 관련 문제 및 활용

### 실무 활용 사례
- **Redis**: 캐시 미스 방지
- **Cassandra/HBase**: SSTable 조회 최적화
- **Chrome**: 악성 URL 탐지
- **Medium**: 추천 시스템 (이미 본 글 필터링)
- **Akamai**: CDN 캐시 결정

### 관련 자료구조
- Counting Bloom Filter (삭제 지원)
- Cuckoo Filter (더 나은 삭제 지원)
- Quotient Filter (캐시 친화적)
- HyperLogLog (카디널리티 추정)
