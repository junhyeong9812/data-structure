# 동적 배열 풀이 해설

## 📌 핵심 아이디어

동적 배열은 **고정 크기 배열의 한계를 극복**하기 위한 자료구조입니다.
내부적으로 배열을 사용하면서, 필요에 따라 크기를 자동으로 조절합니다.

---

## 🔑 핵심 개념

### 1. Amortized Time Complexity (분할 상환 시간복잡도)

배열 확장은 O(n)이지만, 매번 발생하지 않습니다.
n번의 삽입 중 확장은 log(n)번만 발생하므로:
```
총 비용 = n + n/2 + n/4 + ... + 1 ≈ 2n
평균 비용 = 2n / n = O(1)
```

### 2. 확장 전략 (Growth Factor)

| 전략 | 장점 | 단점 |
|------|------|------|
| 1.5배 (Java) | 메모리 효율적 | 확장 빈도 높음 |
| 2배 (일반적) | 확장 빈도 낮음 | 메모리 낭비 가능 |

### 3. 축소 전략

- 너무 빈 배열은 메모리 낭비
- 1/4 이하일 때 1/2로 축소 (Hysteresis 방지)

---

## 📝 POP 구현 해설
```java
public class DynamicArray {
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] data;
    private int size;
    
    public DynamicArray() {
        this.data = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }
    
    // 핵심: 확장 로직
    private void grow() {
        int newCapacity = data.length + (data.length >> 1); // 1.5배
        Object[] newData = new Object[newCapacity];
        System.arraycopy(data, 0, newData, 0, size);
        data = newData;
    }
    
    // 핵심: 축소 로직
    private void shrink() {
        if (data.length > DEFAULT_CAPACITY && size <= data.length / 4) {
            int newCapacity = Math.max(DEFAULT_CAPACITY, data.length / 2);
            Object[] newData = new Object[newCapacity];
            System.arraycopy(data, 0, newData, 0, size);
            data = newData;
        }
    }
    
    public void add(Object element) {
        if (size == data.length) {
            grow();
        }
        data[size++] = element;
    }
    
    public Object remove(int index) {
        // 범위 검사 생략
        Object removed = data[index];
        // 요소 이동
        System.arraycopy(data, index + 1, data, index, size - index - 1);
        data[--size] = null; // GC 도움
        shrink();
        return removed;
    }
}
```

---

## 📝 OOP 구현 해설
```java
public class DynamicArray<E> implements List<E> {
    private Object[] elements;
    private int size;
    
    // 캡슐화: 용량 관리를 별도 메서드로
    private void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            grow(minCapacity);
        }
    }
    
    // SRP: 확장 책임 분리
    private void grow(int minCapacity) {
        int oldCapacity = elements.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity < minCapacity) {
            newCapacity = minCapacity;
        }
        elements = Arrays.copyOf(elements, newCapacity);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        Objects.checkIndex(index, size);
        return (E) elements[index];
    }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 | 공간복잡도 | 설명 |
|------|-----------|-----------|------|
| add(E) | O(1)* | O(1) | 확장 시 O(n) |
| add(index, E) | O(n) | O(1) | 요소 이동 필요 |
| get(index) | O(1) | O(1) | 직접 접근 |
| set(index, E) | O(1) | O(1) | 직접 접근 |
| remove(index) | O(n) | O(1) | 요소 이동 필요 |
| contains(E) | O(n) | O(1) | 순차 탐색 |
| size() | O(1) | O(1) | 필드 반환 |

*Amortized

---

## 🎯 최적화 포인트

### 1. System.arraycopy vs 반복문
```java
// 권장: 네이티브 메서드로 빠름
System.arraycopy(src, srcPos, dest, destPos, length);

// 비권장: 느림
for (int i = 0; i < length; i++) {
    dest[destPos + i] = src[srcPos + i];
}
```

### 2. null 처리
```java
// 삭제 시 명시적 null 할당 (GC 도움)
data[--size] = null;
```

### 3. 용량 예측
```java
// 대량 삽입 전 미리 확장
public void ensureCapacity(int minCapacity) {
    // 한 번에 확장하여 여러 번 확장 방지
}
```

---

## ❌ 흔한 실수

1. **인덱스 검증 누락**
```java
// 잘못됨
public Object get(int index) {
    return data[index]; // ArrayIndexOutOfBoundsException 가능
}

// 올바름
public Object get(int index) {
    if (index < 0 || index >= size) {
        throw new IndexOutOfBoundsException();
    }
    return data[index];
}
```

2. **size vs length 혼동**
```java
// size: 실제 요소 개수
// data.length: 배열 용량 (capacity)
```

3. **축소 시 무한 루프**
```java
// 잘못됨: 1/2 이하일 때 축소하면 진동 발생
// 올바름: 1/4 이하일 때 1/2로 축소 (Hysteresis)
```

---

## 🔗 관련 문제

- LeetCode 1929: Concatenation of Array
- LeetCode 1480: Running Sum of 1d Array
- 프로그래머스: 배열 문제들
