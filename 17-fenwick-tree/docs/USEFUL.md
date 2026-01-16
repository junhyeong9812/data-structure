# 펜윅 트리 구현에 유용한 Java API

## 📦 비트 연산

### 기본 비트 연산자
```java
// AND, OR, XOR, NOT
int and = a & b;
int or = a | b;
int xor = a ^ b;
int not = ~a;

// 시프트
int left = a << n;   // a * 2^n
int right = a >> n;  // a / 2^n (부호 유지)
int uright = a >>> n; // 부호 무시

// LSB (Lowest Set Bit) - 핵심!
int lsb = i & (-i);

// 예시:
// 12 = 1100
// -12 = 0100 (2의 보수)
// 12 & (-12) = 0100 = 4
```

### Integer 비트 메서드
```java
// 1인 비트 개수
Integer.bitCount(n);

// 선행 0 개수
Integer.numberOfLeadingZeros(n);

// 후행 0 개수 (= log2(LSB))
Integer.numberOfTrailingZeros(n);

// 최상위 1비트
Integer.highestOneBit(n);

// 최하위 1비트 (= LSB)
Integer.lowestOneBit(n);  // = n & (-n)

// 비트 반전
Integer.reverse(n);

// 바이트 순서 반전
Integer.reverseBytes(n);

// 이진 문자열
Integer.toBinaryString(n);
```

### Long 비트 메서드
```java
// 동일한 메서드 제공
Long.bitCount(n);
Long.numberOfTrailingZeros(n);
Long.lowestOneBit(n);
Long.highestOneBit(n);
```

---

## 🔢 수학 함수

### Math 클래스
```java
// 로그 (인덱스 범위 계산용)
int logN = (int) (Math.log(n) / Math.log(2));
// 또는
int logN = 31 - Integer.numberOfLeadingZeros(n);

// 2의 거듭제곱
int pow2 = 1 << k;  // 2^k
int pow2 = (int) Math.pow(2, k);

// 올림/내림
Math.ceil(x);
Math.floor(x);
```

---

## 📊 배열 초기화

### Arrays 클래스
```java
import java.util.Arrays;

// 배열 생성
long[] tree = new long[n + 1];

// 초기화
Arrays.fill(tree, 0);

// 복사
long[] copy = Arrays.copyOf(tree, tree.length);

// 출력 (디버깅)
System.out.println(Arrays.toString(tree));
```

### 1-indexed 변환
```java
// 0-indexed 배열을 1-indexed로
int[] arr0 = {1, 2, 3, 4, 5};  // 0-indexed
int[] arr1 = new int[arr0.length + 1];  // 1-indexed
System.arraycopy(arr0, 0, arr1, 1, arr0.length);
// arr1 = {0, 1, 2, 3, 4, 5}

// 또는 사용 시 변환
public void update(int i, long delta) {  // i는 0-indexed
    i++;  // 1-indexed로 변환
    while (i <= n) {
        tree[i] += delta;
        i += i & (-i);
    }
}
```

---

## 🧪 테스트 관련

### AssertJ 사용
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldQueryPrefixSum() {
    int[] arr = {0, 1, 2, 3, 4, 5};  // 1-indexed
    FenwickTree tree = new FenwickTree(arr);
    
    assertThat(tree.query(3)).isEqualTo(6);   // 1+2+3
    assertThat(tree.query(5)).isEqualTo(15);  // 1+2+3+4+5
}

@Test
void shouldUpdateAndQuery() {
    FenwickTree tree = new FenwickTree(5);
    
    tree.update(1, 1);
    tree.update(2, 2);
    tree.update(3, 3);
    
    assertThat(tree.query(3)).isEqualTo(6);
    
    tree.update(2, 5);  // arr[2] += 5
    assertThat(tree.query(3)).isEqualTo(11);
}

@Test
void shouldComputeRangeSum() {
    int[] arr = {0, 1, 2, 3, 4, 5};
    FenwickTree tree = new FenwickTree(arr);
    
    assertThat(tree.rangeQuery(2, 4)).isEqualTo(9);  // 2+3+4
    assertThat(tree.rangeQuery(1, 5)).isEqualTo(15); // 전체
    assertThat(tree.rangeQuery(3, 3)).isEqualTo(3);  // 단일
}
```

### 대용량 테스트
```java
@Test
void shouldHandleLargeInput() {
    int n = 100000;
    FenwickTree tree = new FenwickTree(n);
    
    // 모든 위치에 1 더하기
    for (int i = 1; i <= n; i++) {
        tree.update(i, 1);
    }
    
    assertThat(tree.query(n)).isEqualTo(n);
    assertThat(tree.rangeQuery(1, n)).isEqualTo(n);
}

@Test
void shouldBeEfficient() {
    int n = 1000000;
    FenwickTree tree = new FenwickTree(n);
    
    long start = System.nanoTime();
    
    // 100만 번 업데이트
    for (int i = 1; i <= n; i++) {
        tree.update(i, i);
    }
    
    // 100만 번 쿼리
    for (int i = 1; i <= n; i++) {
        tree.query(i);
    }
    
    long end = System.nanoTime();
    
    // 1초 이내 완료 확인
    assertThat(end - start).isLessThan(1_000_000_000L);
}
```

---

## 📚 Java 21 관련

### Record 활용
```java
// 쿼리 결과
public record QueryResult(int left, int right, long sum) {}

public QueryResult queryWithInfo(int l, int r) {
    return new QueryResult(l, r, rangeQuery(l, r));
}

// 업데이트 기록
public record UpdateLog(int index, long delta, long newValue) {}
```

### 제네릭 펜윅 트리 (합 연산)
```java
import java.util.function.BinaryOperator;

public class GenericFenwickTree<T> {
    private Object[] tree;
    private BinaryOperator<T> add;
    private BinaryOperator<T> subtract;
    private T identity;
    
    public GenericFenwickTree(int n, BinaryOperator<T> add, 
                               BinaryOperator<T> subtract, T identity) {
        this.tree = new Object[n + 1];
        this.add = add;
        this.subtract = subtract;
        this.identity = identity;
        Arrays.fill(tree, identity);
    }
    
    @SuppressWarnings("unchecked")
    public T query(int i) {
        T sum = identity;
        while (i > 0) {
            sum = add.apply(sum, (T) tree[i]);
            i -= i & (-i);
        }
        return sum;
    }
    
    // ...
}
```

---

## ⚡ 성능 팁

### 1. 비트 연산 직접 사용
```java
// 메서드 호출 대신 직접 연산
// 느림
i += lsb(i);

// 빠름
i += i & (-i);
```

### 2. 배열 접근 최소화
```java
// 캐싱 활용
public void update(int i, long delta) {
    while (i <= n) {
        tree[i] += delta;
        i += i & (-i);
    }
}
```

### 3. O(n) 빌드 사용
```java
// O(n log n) - 느림
for (int i = 1; i <= n; i++) {
    update(i, arr[i]);
}

// O(n) - 빠름
for (int i = 1; i <= n; i++) {
    tree[i] = arr[i];
}
for (int i = 1; i <= n; i++) {
    int parent = i + (i & (-i));
    if (parent <= n) {
        tree[parent] += tree[i];
    }
}
```

---

## 🔀 응용 패턴

### 역수 카운팅 (Inversion Count)
```java
// 배열에서 i < j이고 arr[i] > arr[j]인 쌍의 개수
public long countInversions(int[] arr) {
    int max = Arrays.stream(arr).max().getAsInt();
    FenwickTree tree = new FenwickTree(max);
    long inversions = 0;
    
    for (int i = arr.length - 1; i >= 0; i--) {
        // arr[i]보다 작은 수의 개수 (오른쪽에서)
        inversions += tree.query(arr[i] - 1);
        tree.update(arr[i], 1);
    }
    
    return inversions;
}
```

### 좌표 압축과 함께
```java
// 값의 범위가 클 때
public long countInversionsCompressed(int[] arr) {
    // 좌표 압축
    int[] sorted = arr.clone();
    Arrays.sort(sorted);
    Map<Integer, Integer> rank = new HashMap<>();
    int r = 1;
    for (int v : sorted) {
        if (!rank.containsKey(v)) {
            rank.put(v, r++);
        }
    }
    
    // 압축된 값으로 카운팅
    FenwickTree tree = new FenwickTree(r);
    long inversions = 0;
    
    for (int i = arr.length - 1; i >= 0; i--) {
        int compressed = rank.get(arr[i]);
        inversions += tree.query(compressed - 1);
        tree.update(compressed, 1);
    }
    
    return inversions;
}
```

### 구간 업데이트 (차분 배열)
```java
// 구간 [l, r]에 delta 더하기
// 차분 배열과 함께 사용

public class RangeUpdateFenwickTree {
    private FenwickTree diff;  // 차분 배열
    
    public void rangeUpdate(int l, int r, long delta) {
        diff.update(l, delta);
        diff.update(r + 1, -delta);
    }
    
    public long get(int i) {
        return diff.query(i);
    }
}
```

---

## 🎯 디버깅

### 트리 내용 출력
```java
public void printTree() {
    System.out.println("Fenwick Tree:");
    for (int i = 1; i <= n; i++) {
        int range = i & (-i);
        System.out.printf("tree[%d] = %d (범위 [%d, %d])%n", 
            i, tree[i], i - range + 1, i);
    }
}

// 출력 예:
// tree[1] = 1 (범위 [1, 1])
// tree[2] = 3 (범위 [1, 2])
// tree[3] = 3 (범위 [3, 3])
// tree[4] = 10 (범위 [1, 4])
```

### 원본 배열 복원
```java
public long[] toArray() {
    long[] arr = new long[n + 1];
    for (int i = 1; i <= n; i++) {
        arr[i] = get(i);
    }
    return arr;
}
```
