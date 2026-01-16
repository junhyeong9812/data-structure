# 구간 트리 구현에 유용한 Java API

## 📦 배열 유틸리티

### Arrays 클래스
```java
import java.util.Arrays;

// 배열 초기화
int[] tree = new int[4 * n];
Arrays.fill(tree, 0);
Arrays.fill(tree, Integer.MAX_VALUE);  // 최소값용

// 배열 복사
int[] copy = Arrays.copyOf(arr, arr.length);
int[] range = Arrays.copyOfRange(arr, from, to);

// 배열 출력 (디버깅)
System.out.println(Arrays.toString(tree));
```

### 배열 크기 계산
```java
// 구간 트리 크기 (안전하게)
int treeSize = 4 * n;

// 더 정확한 계산: 2의 거듭제곱
int height = (int) Math.ceil(Math.log(n) / Math.log(2));
int treeSize = 2 * (int) Math.pow(2, height);

// 비트 연산으로 다음 2의 거듭제곱
int nextPow2 = Integer.highestOneBit(n - 1) << 1;
int treeSize = 2 * nextPow2;
```

---

## 🔢 수학 함수

### Math 클래스
```java
// 최대/최소
Math.max(a, b);
Math.min(a, b);

// 로그 (높이 계산용)
Math.log(n);                    // 자연로그
Math.log(n) / Math.log(2);      // 로그 밑 2
Math.log10(n);                  // 상용로그

// 거듭제곱
Math.pow(2, height);            // 2^height

// 올림/내림
Math.ceil(x);
Math.floor(x);

// GCD (최대공약수) - 구간 GCD용
int gcd(int a, int b) {
    return b == 0 ? a : gcd(b, a % b);
}

// Java 17+
// Math.gcd() 없음 → 직접 구현 필요
```

### 비트 연산
```java
// 다음 2의 거듭제곱
Integer.highestOneBit(n);           // n 이하 최대 2의 거듭제곱
Integer.highestOneBit(n - 1) << 1;  // n 이상 최소 2의 거듭제곱

// 2의 거듭제곱 확인
(n & (n - 1)) == 0;  // n이 2의 거듭제곱이면 true

// 비트 수
Integer.bitCount(n);

// 선행/후행 0
Integer.numberOfLeadingZeros(n);
Integer.numberOfTrailingZeros(n);
```

---

## 📊 항등원 (Identity Element)
```java
// 각 연산별 항등원

// 합 (Sum)
int SUM_IDENTITY = 0;
// a + 0 = a

// 곱 (Product)
int PRODUCT_IDENTITY = 1;
// a * 1 = a

// 최소값 (Min)
int MIN_IDENTITY = Integer.MAX_VALUE;
// min(a, INF) = a

// 최대값 (Max)
int MAX_IDENTITY = Integer.MIN_VALUE;
// max(a, -INF) = a

// GCD
int GCD_IDENTITY = 0;
// gcd(a, 0) = a

// AND
int AND_IDENTITY = -1;  // 모든 비트 1
// a & (-1) = a

// OR
int OR_IDENTITY = 0;
// a | 0 = a

// XOR
int XOR_IDENTITY = 0;
// a ^ 0 = a
```

---

## 🎯 제네릭 연산

### Function/BinaryOperator
```java
import java.util.function.BinaryOperator;
import java.util.function.IntBinaryOperator;

// 정수 연산
IntBinaryOperator sumOp = Integer::sum;
IntBinaryOperator minOp = Math::min;
IntBinaryOperator maxOp = Math::max;

// 제네릭 연산
BinaryOperator<Integer> merge = (a, b) -> a + b;
BinaryOperator<Integer> merge = Integer::sum;

// 사용
int result = merge.apply(left, right);
```

### 연산별 Segment Tree
```java
public class GenericSegmentTree<T> {
    private Object[] tree;
    private BinaryOperator<T> merge;
    private T identity;
    
    public GenericSegmentTree(T[] arr, BinaryOperator<T> merge, T identity) {
        this.merge = merge;
        this.identity = identity;
        // build...
    }
    
    @SuppressWarnings("unchecked")
    private T get(int idx) {
        return (T) tree[idx];
    }
}
```

---

## 🧪 테스트 관련

### AssertJ 사용
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldQueryRangeSum() {
    int[] arr = {1, 3, 5, 7, 9, 11};
    SegmentTree tree = new SegmentTree(arr);
    
    assertThat(tree.query(1, 3)).isEqualTo(15);  // 3+5+7
    assertThat(tree.query(0, 5)).isEqualTo(36);  // 전체 합
}

@Test
void shouldUpdateAndQuery() {
    int[] arr = {1, 3, 5, 7, 9, 11};
    SegmentTree tree = new SegmentTree(arr);
    
    tree.update(1, 10);  // arr[1] = 10
    
    assertThat(tree.query(1, 3)).isEqualTo(22);  // 10+5+7
}

@Test
void shouldHandleRangeUpdate() {
    int[] arr = {1, 2, 3, 4, 5};
    LazySegmentTree tree = new LazySegmentTree(arr);
    
    tree.updateRange(1, 3, 10);  // [1,3]에 10 더하기
    
    assertThat(tree.query(0, 4)).isEqualTo(45);
    assertThat(tree.query(1, 3)).isEqualTo(39);
}
```

### 엣지 케이스 테스트
```java
@Test
void shouldHandleSingleElement() {
    int[] arr = {42};
    SegmentTree tree = new SegmentTree(arr);
    
    assertThat(tree.query(0, 0)).isEqualTo(42);
}

@Test
void shouldHandleFullRange() {
    int[] arr = {1, 2, 3, 4, 5};
    SegmentTree tree = new SegmentTree(arr);
    
    assertThat(tree.query(0, arr.length - 1)).isEqualTo(15);
}

@Test
void shouldHandleOutOfRange() {
    int[] arr = {1, 2, 3};
    MinSegmentTree tree = new MinSegmentTree(arr);
    
    // 구현에 따라 예외 또는 항등원 반환
}
```

---

## 📚 Java 21 관련

### Record로 노드 표현
```java
public record SegmentNode(int sum, int min, int max) {
    public static SegmentNode merge(SegmentNode left, SegmentNode right) {
        return new SegmentNode(
            left.sum + right.sum,
            Math.min(left.min, right.min),
            Math.max(left.max, right.max)
        );
    }
    
    public static SegmentNode leaf(int value) {
        return new SegmentNode(value, value, value);
    }
    
    public static SegmentNode identity() {
        return new SegmentNode(0, Integer.MAX_VALUE, Integer.MIN_VALUE);
    }
}
```

### Sealed Interface (연산 타입)
```java
sealed interface MergeOperation permits Sum, Min, Max, Gcd {
    int apply(int a, int b);
    int identity();
}

record Sum() implements MergeOperation {
    public int apply(int a, int b) { return a + b; }
    public int identity() { return 0; }
}

record Min() implements MergeOperation {
    public int apply(int a, int b) { return Math.min(a, b); }
    public int identity() { return Integer.MAX_VALUE; }
}
```

### Pattern Matching
```java
public int getIdentity(String operation) {
    return switch (operation) {
        case "sum" -> 0;
        case "min" -> Integer.MAX_VALUE;
        case "max" -> Integer.MIN_VALUE;
        case "gcd" -> 0;
        default -> throw new IllegalArgumentException();
    };
}
```

---

## ⚡ 성능 팁

### 1. 반복문 버전 (Bottom-Up)
```java
// 재귀 오버헤드 제거
public class IterativeSegmentTree {
    private int[] tree;
    private int n;
    
    public IterativeSegmentTree(int[] arr) {
        this.n = arr.length;
        this.tree = new int[2 * n];
        
        // 리프 노드
        System.arraycopy(arr, 0, tree, n, n);
        
        // 내부 노드
        for (int i = n - 1; i > 0; i--) {
            tree[i] = tree[2 * i] + tree[2 * i + 1];
        }
    }
    
    public void update(int idx, int val) {
        idx += n;
        tree[idx] = val;
        while (idx > 1) {
            idx /= 2;
            tree[idx] = tree[2 * idx] + tree[2 * idx + 1];
        }
    }
    
    public int query(int left, int right) {
        int sum = 0;
        left += n;
        right += n + 1;
        
        while (left < right) {
            if ((left & 1) == 1) {
                sum += tree[left++];
            }
            if ((right & 1) == 1) {
                sum += tree[--right];
            }
            left /= 2;
            right /= 2;
        }
        
        return sum;
    }
}
```

### 2. long 타입 사용
```java
// 큰 합을 다룰 때 오버플로우 방지
private long[] tree;
private long[] lazy;
```

### 3. 좌표 압축
```java
// 값의 범위가 크지만 개수가 적을 때
// 1. 모든 좌표 수집
// 2. 정렬 후 인덱스 매핑
// 3. 압축된 인덱스로 세그먼트 트리 구성
```

---

## 🔀 변형 구조

### 동적 세그먼트 트리
```java
// 희소 데이터용 (좌표 범위는 크지만 실제 데이터는 적음)
class Node {
    long value;
    Node left, right;
}
// 필요할 때만 노드 생성
```

### 영속 세그먼트 트리 (Persistent)
```java
// 이전 버전 유지
// 업데이트 시 경로만 새로 생성
class PersistentSegmentTree {
    Node[] roots;  // 각 버전의 루트
    
    Node update(Node prev, int idx, int val) {
        // 새 노드 생성하며 업데이트
    }
}
```

### 2D 세그먼트 트리
```java
// 2차원 배열의 구간 쿼리
class SegmentTree2D {
    int[][] tree;
    
    int query(int x1, int y1, int x2, int y2) {
        // 행 세그먼트 트리의 각 노드가 열 세그먼트 트리
    }
}
```
