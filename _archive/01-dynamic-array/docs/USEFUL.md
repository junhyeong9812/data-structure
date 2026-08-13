# 동적 배열 구현에 유용한 Java API

## 📦 배열 관련

### System.arraycopy()
배열 간 고속 복사 (네이티브 메서드)
```java
// 원본 배열의 srcPos부터 length개를 dest 배열의 destPos로 복사
System.arraycopy(Object src, int srcPos, Object dest, int destPos, int length);

// 예시
int[] src = {1, 2, 3, 4, 5};
int[] dest = new int[10];
System.arraycopy(src, 0, dest, 0, src.length);
// dest = [1, 2, 3, 4, 5, 0, 0, 0, 0, 0]
```

### Arrays.copyOf()
새 배열을 생성하며 복사
```java
// 새로운 길이의 배열로 복사 (확장/축소 가능)
int[] newArray = Arrays.copyOf(int[] original, int newLength);

// 예시
int[] arr = {1, 2, 3};
int[] expanded = Arrays.copyOf(arr, 10);  // [1, 2, 3, 0, 0, 0, 0, 0, 0, 0]
int[] shrunk = Arrays.copyOf(arr, 2);     // [1, 2]
```

### Arrays.copyOfRange()
범위 지정 복사
```java
int[] slice = Arrays.copyOfRange(int[] original, int from, int to);

// 예시
int[] arr = {0, 1, 2, 3, 4, 5};
int[] slice = Arrays.copyOfRange(arr, 2, 5);  // [2, 3, 4]
```

### Arrays.fill()
배열을 특정 값으로 채우기
```java
Arrays.fill(int[] a, int val);
Arrays.fill(int[] a, int fromIndex, int toIndex, int val);

// 예시
int[] arr = new int[5];
Arrays.fill(arr, -1);  // [-1, -1, -1, -1, -1]
```

---

## 🔢 수학 관련

### Math 클래스
```java
// 최대/최소
Math.max(int a, int b);
Math.min(int a, int b);

// 절대값
Math.abs(int a);

// 거듭제곱
Math.pow(double a, double b);  // a^b

// 제곱근
Math.sqrt(double a);

// 올림/내림/반올림
Math.ceil(double a);   // 올림
Math.floor(double a);  // 내림
Math.round(double a);  // 반올림
```

### 비트 연산 (용량 계산에 유용)
```java
// 1.5배 계산 (곱셈 대신 비트 연산)
int newCapacity = oldCapacity + (oldCapacity >> 1);  // oldCapacity * 1.5

// 2배 계산
int doubled = capacity << 1;  // capacity * 2

// 절반 계산
int half = capacity >> 1;  // capacity / 2

// 2의 거듭제곱 확인
boolean isPowerOfTwo = (n & (n - 1)) == 0;

// 다음 2의 거듭제곱 찾기
int nextPowerOfTwo = Integer.highestOneBit(n - 1) << 1;
```

---

## ✅ 검증 관련

### Objects 클래스 (Java 7+)
```java
// null 체크
Objects.requireNonNull(Object obj);
Objects.requireNonNull(Object obj, String message);

// 인덱스 범위 체크 (Java 9+)
Objects.checkIndex(int index, int length);  // 0 <= index < length
Objects.checkFromToIndex(int fromIndex, int toIndex, int length);
Objects.checkFromIndexSize(int fromIndex, int size, int length);

// 예시
public E get(int index) {
    Objects.checkIndex(index, size);  // 자동으로 IndexOutOfBoundsException
    return (E) elements[index];
}
```

### 수동 범위 검사
```java
// 전통적인 방식
if (index < 0 || index >= size) {
    throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
}
```

---

## 🔄 제네릭 관련

### 제네릭 배열 생성
```java
// 방법 1: Object 배열 사용 (권장)
private Object[] elements = new Object[capacity];

@SuppressWarnings("unchecked")
public E get(int index) {
    return (E) elements[index];
}

// 방법 2: 리플렉션 사용
@SuppressWarnings("unchecked")
E[] elements = (E[]) Array.newInstance(componentType, capacity);
```

### 타입 토큰 패턴
```java
public class DynamicArray<E> {
    private final Class<E> type;
    private E[] elements;
    
    @SuppressWarnings("unchecked")
    public DynamicArray(Class<E> type, int capacity) {
        this.type = type;
        this.elements = (E[]) Array.newInstance(type, capacity);
    }
}
```

---

## 📐 배열 유틸리티

### Arrays.toString()
배열을 문자열로 변환 (디버깅용)
```java
int[] arr = {1, 2, 3};
System.out.println(Arrays.toString(arr));  // [1, 2, 3]
```

### Arrays.equals()
배열 내용 비교
```java
int[] a = {1, 2, 3};
int[] b = {1, 2, 3};
System.out.println(a == b);              // false (참조 비교)
System.out.println(Arrays.equals(a, b)); // true (내용 비교)
```

### Arrays.hashCode()
배열의 해시코드 계산
```java
int[] arr = {1, 2, 3};
int hash = Arrays.hashCode(arr);
```

---

## ⚡ 성능 팁

### 1. 초기 용량 지정
```java
// 대량 데이터 예상 시 미리 용량 지정
DynamicArray<String> arr = new DynamicArray<>(1000);
```

### 2. addAll 최적화
```java
// 여러 요소 추가 시 한 번에 확장
public void addAll(Collection<? extends E> c) {
    ensureCapacity(size + c.size());
    // 요소 추가
}
```

### 3. 불필요한 박싱 피하기
```java
// 기본형 전용 배열 사용 고려
int[] intArray;        // 권장 (기본형)
Integer[] boxedArray;  // 비권장 (박싱 오버헤드)
```

---

## 📚 Java 21 새로운 기능

### SequencedCollection (Java 21)
```java
// 첫/마지막 요소 접근
E getFirst();
E getLast();
void addFirst(E e);
void addLast(E e);
E removeFirst();
E removeLast();

// 역순 뷰
SequencedCollection<E> reversed();
```

### Record 패턴 (Java 21)
```java
// 불변 데이터 홀더로 사용 가능
record ArrayMetadata(int size, int capacity) {}
```

---

## 🧪 테스트 관련

### JUnit 5 Assertions
```java
import static org.junit.jupiter.api.Assertions.*;

assertEquals(expected, actual);
assertThrows(IndexOutOfBoundsException.class, () -> arr.get(-1));
assertDoesNotThrow(() -> arr.add(1));
assertTrue(arr.isEmpty());
assertFalse(arr.contains(999));
```

### AssertJ (권장)
```java
import static org.assertj.core.api.Assertions.*;

assertThat(arr.size()).isEqualTo(3);
assertThat(arr.get(0)).isEqualTo(1);
assertThatThrownBy(() -> arr.get(-1))
    .isInstanceOf(IndexOutOfBoundsException.class);
```
