# 11. 블룸 필터 (Bloom Filter)

## 📋 문제 정의

**확률적 자료구조**인 블룸 필터를 구현하세요.

블룸 필터는 원소가 집합에 **속하는지 아닌지**를 검사하는 공간 효율적인 자료구조입니다.
**거짓 양성(False Positive)**은 가능하지만, **거짓 음성(False Negative)**은 절대 발생하지 않습니다.

---

## 🎯 학습 목표

- 확률적 자료구조의 개념
- 해시 함수의 역할과 설계
- 공간-정확도 트레이드오프
- 비트 배열 조작
- 실무에서의 블룸 필터 활용

---

## 📝 요구사항

### 기본 연산

| 메서드 | 설명 | 시간복잡도 |
|--------|------|-----------|
| `add(element)` | 원소 추가 | O(k) |
| `mightContain(element)` | 원소 존재 가능성 확인 | O(k) |
| `clear()` | 필터 초기화 | O(m) |

*k = 해시 함수 개수, m = 비트 배열 크기

### 결과 해석

| 반환값 | 의미 |
|--------|------|
| `true` | 원소가 **있을 수도** 있음 (확실하지 않음) |
| `false` | 원소가 **확실히 없음** |

### 추가 기능

| 메서드 | 설명 |
|--------|------|
| `expectedFpp()` | 예상 거짓 양성 확률 |
| `approximateCount()` | 대략적인 원소 개수 |
| `bitCount()` | 설정된 비트 개수 |
| `size()` | 비트 배열 크기 |

---

## 📊 입출력 예시

### 예제 1: 기본 사용
```java
BloomFilter filter = new BloomFilter(1000, 0.01);  // 1000개 원소, 1% FPP

filter.add("apple");
filter.add("banana");
filter.add("cherry");

System.out.println(filter.mightContain("apple"));   // true (확실히 있음)
System.out.println(filter.mightContain("banana"));  // true (확실히 있음)
System.out.println(filter.mightContain("grape"));   // false (확실히 없음)
System.out.println(filter.mightContain("melon"));   // 대부분 false, 가끔 true (FP)
```

### 예제 2: 대용량 데이터
```java
// 100만 개 원소, 0.1% 거짓 양성률
BloomFilter filter = new BloomFilter(1_000_000, 0.001);

// URL 중복 체크
for (String url : visitedUrls) {
    filter.add(url);
}

// 새 URL이 이미 방문했는지 확인
if (!filter.mightContain(newUrl)) {
    // 확실히 처음 방문
    processUrl(newUrl);
} else {
    // 이미 방문했을 수도 있음 (추가 확인 필요)
}
```

### 예제 3: 거짓 양성 확인
```java
BloomFilter filter = new BloomFilter(1000, 0.05);  // 5% FPP

// 1~1000 추가
for (int i = 1; i <= 1000; i++) {
    filter.add(String.valueOf(i));
}

// 1001~2000 테스트 (없는 원소들)
int falsePositives = 0;
for (int i = 1001; i <= 2000; i++) {
    if (filter.mightContain(String.valueOf(i))) {
        falsePositives++;  // 거짓 양성!
    }
}

System.out.println("FP count: " + falsePositives);  // 약 50개 (5%)
```

---

## 🔍 핵심 개념

### 거짓 양성 확률 (FPP)
```
FPP ≈ (1 - e^(-kn/m))^k

m = 비트 배열 크기
n = 예상 원소 개수
k = 해시 함수 개수
```

### 최적 설정
```
최적 비트 수: m = -n × ln(p) / (ln2)²
최적 해시 함수 수: k = (m/n) × ln2

n = 1000, p = 0.01 (1%) → m ≈ 9585 bits, k ≈ 7
```

### 동작 원리
```
add("hello"):
    h1("hello") = 3   → bits[3] = 1
    h2("hello") = 17  → bits[17] = 1
    h3("hello") = 42  → bits[42] = 1

mightContain("hello"):
    bits[3] == 1? ✓
    bits[17] == 1? ✓
    bits[42] == 1? ✓
    → true (있을 수도 있음)

mightContain("world"):
    h1("world") = 5   → bits[5] == 0? ✗
    → false (확실히 없음)
```

---

## 💡 힌트

### 비트 배열
```java
// BitSet 사용
BitSet bits = new BitSet(size);
bits.set(index);       // 비트 설정
bits.get(index);       // 비트 확인
bits.cardinality();    // 설정된 비트 개수

// long 배열 직접 사용
long[] bits = new long[(size + 63) / 64];
bits[index / 64] |= (1L << (index % 64));   // 설정
(bits[index / 64] & (1L << (index % 64))) != 0  // 확인
```

### 해시 함수 생성
```java
// 두 개의 해시로 k개 해시 시뮬레이션 (Kirsch-Mitzenmacher)
int h1 = hash1(element);
int h2 = hash2(element);

for (int i = 0; i < k; i++) {
    int combinedHash = h1 + i * h2;
    int index = Math.abs(combinedHash % m);
    // index 사용
}
```

### MurmurHash
```java
// 일반적으로 사용되는 해시 함수
int hash = MurmurHash3.hash32(data, seed);
```

---

## ✅ 체크리스트

- [ ] 기본 add, mightContain 구현
- [ ] 최적 비트 배열 크기 계산
- [ ] 최적 해시 함수 개수 계산
- [ ] 다중 해시 함수 구현
- [ ] 거짓 양성률 계산
- [ ] Counting Bloom Filter (선택)
- [ ] Scalable Bloom Filter (선택)

---

## 📚 참고

- [Bloom Filter Calculator](https://hur.st/bloomfilter/)
- Redis의 BF.ADD, BF.EXISTS
- Cassandra의 블룸 필터
- Google Guava의 BloomFilter
