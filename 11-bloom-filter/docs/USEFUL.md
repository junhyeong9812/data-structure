# 블룸 필터 구현에 유용한 Java API

## 📦 BitSet

### 기본 사용
```java
import java.util.BitSet;

// 생성
BitSet bits = new BitSet();        // 기본 크기
BitSet bits = new BitSet(1000);    // 초기 용량 지정

// 비트 설정/해제
bits.set(index);           // 비트를 1로
bits.set(index, true);     // 비트를 1로
bits.set(index, false);    // 비트를 0으로
bits.clear(index);         // 비트를 0으로
bits.flip(index);          // 비트 반전

// 비트 확인
boolean value = bits.get(index);   // true/false

// 범위 설정
bits.set(fromIndex, toIndex);      // [from, to) 범위 1로
bits.clear(fromIndex, toIndex);    // [from, to) 범위 0으로

// 정보 조회
int cardinality = bits.cardinality();  // 1인 비트 개수
int size = bits.size();                 // 실제 크기 (64의 배수)
int length = bits.length();             // 마지막 1 비트의 인덱스 + 1
boolean isEmpty = bits.isEmpty();       // 모든 비트가 0인지

// 전체 초기화
bits.clear();

// 비트 연산
bits.and(other);      // AND
bits.or(other);       // OR
bits.xor(other);      // XOR
bits.andNot(other);   // AND NOT
```

### 순회
```java
// 설정된 비트 순회
for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
    System.out.println("Bit " + i + " is set");
}

// 클리어된 비트 순회
for (int i = bits.nextClearBit(0); i < bits.length(); i = bits.nextClearBit(i + 1)) {
    System.out.println("Bit " + i + " is clear");
}

// Stream으로 설정된 비트 인덱스
IntStream setBitIndices = bits.stream();
```

---

## 🔢 비트 연산 (long[] 직접 사용)

### 비트 배열 직접 관리
```java
// long 배열로 비트 관리
int numBits = 1000;
long[] bits = new long[(numBits + 63) / 64];  // 올림 나눗셈

// 비트 설정
void set(int index) {
    bits[index >> 6] |= (1L << index);  // index / 64, index % 64
}

// 비트 확인
boolean get(int index) {
    return (bits[index >> 6] & (1L << index)) != 0;
}

// 비트 해제
void clear(int index) {
    bits[index >> 6] &= ~(1L << index);
}

// 비트 반전
void flip(int index) {
    bits[index >> 6] ^= (1L << index);
}
```

### 비트 연산자
```java
// 기본 연산자
&   // AND
|   // OR
^   // XOR
~   // NOT
<<  // 왼쪽 시프트
>>  // 오른쪽 시프트 (부호 유지)
>>> // 오른쪽 시프트 (부호 없음)

// 비트 카운팅
Long.bitCount(x);        // 1인 비트 개수
Long.numberOfLeadingZeros(x);   // 선행 0 개수
Long.numberOfTrailingZeros(x);  // 후행 0 개수
Long.highestOneBit(x);   // 최상위 1 비트만
Long.lowestOneBit(x);    // 최하위 1 비트만
```

---

## 🔐 해시 함수

### Object.hashCode()
```java
String s = "hello";
int hash = s.hashCode();

// Objects.hash (여러 값 결합)
int hash = Objects.hash(a, b, c);

// Arrays.hashCode (배열용)
int hash = Arrays.hashCode(array);
```

### 간단한 MurmurHash 구현
```java
public static int murmurHash32(byte[] data, int seed) {
    int h = seed;
    int length = data.length;
    int i = 0;
    
    // 4바이트씩 처리
    while (i + 4 <= length) {
        int k = (data[i] & 0xFF) 
              | ((data[i + 1] & 0xFF) << 8)
              | ((data[i + 2] & 0xFF) << 16)
              | ((data[i + 3] & 0xFF) << 24);
        
        k *= 0xcc9e2d51;
        k = Integer.rotateLeft(k, 15);
        k *= 0x1b873593;
        
        h ^= k;
        h = Integer.rotateLeft(h, 13);
        h = h * 5 + 0xe6546b64;
        
        i += 4;
    }
    
    // 남은 바이트 처리
    int k = 0;
    switch (length & 3) {
        case 3: k ^= (data[i + 2] & 0xFF) << 16;
        case 2: k ^= (data[i + 1] & 0xFF) << 8;
        case 1: k ^= (data[i] & 0xFF);
                k *= 0xcc9e2d51;
                k = Integer.rotateLeft(k, 15);
                k *= 0x1b873593;
                h ^= k;
    }
    
    // 최종 믹싱
    h ^= length;
    h ^= h >>> 16;
    h *= 0x85ebca6b;
    h ^= h >>> 13;
    h *= 0xc2b2ae35;
    h ^= h >>> 16;
    
    return h;
}

// 문자열용 래퍼
public static int murmurHash32(String s, int seed) {
    return murmurHash32(s.getBytes(StandardCharsets.UTF_8), seed);
}
```

### 간단한 해시 함수 (학습용)
```java
// FNV-1a 해시
public static int fnv1aHash(String data) {
    int hash = 0x811c9dc5;  // FNV offset basis
    for (char c : data.toCharArray()) {
        hash ^= c;
        hash *= 0x01000193;  // FNV prime
    }
    return hash;
}

// DJB2 해시
public static int djb2Hash(String data) {
    int hash = 5381;
    for (char c : data.toCharArray()) {
        hash = ((hash << 5) + hash) + c;  // hash * 33 + c
    }
    return hash;
}
```

### Kirsch-Mitzenmacher 최적화
```java
// 두 개의 해시로 k개의 해시 생성
public int[] getKHashes(String element, int k, int m) {
    int[] hashes = new int[k];
    
    int h1 = murmurHash32(element, 0);
    int h2 = murmurHash32(element, h1);
    
    for (int i = 0; i < k; i++) {
        // gi(x) = h1(x) + i × h2(x)
        int combinedHash = h1 + i * h2;
        hashes[i] = Math.abs(combinedHash % m);
    }
    
    return hashes;
}
```

---

## 📊 수학 함수

### Math 클래스
```java
// 로그
Math.log(x);       // 자연로그 (ln)
Math.log10(x);     // 상용로그
Math.log(x) / Math.log(2);  // 로그 밑 2

// 지수
Math.exp(x);       // e^x
Math.pow(base, exp);  // base^exp

// 올림/내림/반올림
Math.ceil(x);      // 올림
Math.floor(x);     // 내림
Math.round(x);     // 반올림

// 절대값
Math.abs(x);

// 최대/최소
Math.max(a, b);
Math.min(a, b);
```

### 블룸 필터 공식
```java
// 최적 비트 수: m = -n × ln(p) / (ln2)²
public static int optimalBits(int n, double p) {
    double ln2Squared = Math.log(2) * Math.log(2);
    return (int) Math.ceil(-n * Math.log(p) / ln2Squared);
}

// 최적 해시 함수 수: k = (m/n) × ln2
public static int optimalHashFunctions(int n, int m) {
    return Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
}

// 거짓 양성 확률: (1 - e^(-kn/m))^k
public static double falsePosProb(int n, int m, int k) {
    double exponent = -(double) k * n / m;
    return Math.pow(1 - Math.exp(exponent), k);
}

// 대략적인 원소 수 추정: n* = -(m/k) × ln(1 - X/m)
public static int estimateCount(int setBits, int m, int k) {
    if (setBits == 0) return 0;
    if (setBits >= m) return Integer.MAX_VALUE;
    double ratio = (double) setBits / m;
    return (int) Math.round(-(double) m / k * Math.log(1 - ratio));
}
```

---

## 🧪 테스트 관련

### AssertJ 사용
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldNotHaveFalseNegatives() {
    BloomFilter filter = new BloomFilter(1000, 0.01);
    
    // 원소 추가
    for (int i = 0; i < 1000; i++) {
        filter.add("item" + i);
    }
    
    // 거짓 음성 없음 확인
    for (int i = 0; i < 1000; i++) {
        assertThat(filter.mightContain("item" + i)).isTrue();
    }
}

@Test
void falsePositiveRateShouldBeWithinExpected() {
    BloomFilter filter = new BloomFilter(1000, 0.05);  // 5% FPP
    
    for (int i = 0; i < 1000; i++) {
        filter.add("existing" + i);
    }
    
    // 없는 원소 테스트
    int falsePositives = 0;
    int tests = 10000;
    for (int i = 0; i < tests; i++) {
        if (filter.mightContain("nonexistent" + i)) {
            falsePositives++;
        }
    }
    
    double actualFpp = (double) falsePositives / tests;
    assertThat(actualFpp).isLessThan(0.08);  // 약간의 여유
}
```

---

## 📚 Java 21 관련

### Record로 설정 표현
```java
public record BloomFilterConfig(
    int expectedElements,
    double falsePositiveProb,
    int numBits,
    int numHashFunctions
) {
    public static BloomFilterConfig optimal(int n, double p) {
        int m = (int) Math.ceil(-n * Math.log(p) / (Math.log(2) * Math.log(2)));
        int k = Math.max(1, (int) Math.round((double) m / n * Math.log(2)));
        return new BloomFilterConfig(n, p, m, k);
    }
}
```

### 함수형 해시 전략
```java
@FunctionalInterface
public interface HashFunction<T> {
    int hash(T element, int seed);
}

// 사용
HashFunction<String> murmur = (s, seed) -> murmurHash32(s, seed);
HashFunction<String> fnv = (s, seed) -> fnv1aHash(s) ^ seed;
```

### Stream으로 해시 계산
```java
int[] hashes = IntStream.range(0, k)
    .map(i -> Math.abs((h1 + i * h2) % m))
    .toArray();
```

---

## ⚡ 성능 팁

### 1. BitSet vs long[]
```java
// BitSet: 편리하지만 약간의 오버헤드
BitSet bits = new BitSet(1_000_000);

// long[]: 더 빠름, 직접 제어
long[] bits = new long[(1_000_000 + 63) / 64];
```

### 2. 해시 재계산 피하기
```java
// 비효율: 매번 해시 계산
for (int i = 0; i < k; i++) {
    int hash = computeHash(element, i);  // 매번 새로 계산
}

// 효율: Kirsch-Mitzenmacher
int h1 = hash1(element);
int h2 = hash2(element);
for (int i = 0; i < k; i++) {
    int hash = h1 + i * h2;  // 간단한 덧셈만
}
```

### 3. 문자열 바이트 변환 캐싱
```java
// 비효율: 매번 변환
byte[] bytes = element.getBytes(StandardCharsets.UTF_8);

// 효율: 캐싱 또는 직접 문자 처리
int hash = seed;
for (int i = 0; i < element.length(); i++) {
    hash ^= element.charAt(i);
    hash *= PRIME;
}
```

### 4. 블룸 필터 병합
```java
// 두 블룸 필터 OR 연산
public BloomFilter merge(BloomFilter other) {
    if (this.numBits != other.numBits) {
        throw new IllegalArgumentException("Size mismatch");
    }
    BloomFilter merged = new BloomFilter(numBits, numHashFunctions);
    merged.bits.or(this.bits);
    merged.bits.or(other.bits);
    return merged;
}
```
