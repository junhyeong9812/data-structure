# pop/DynamicArray.java

인터페이스 분리 없이 단일 클래스로 구현한 동적 배열. OOP 버전과 동일한 자동 확장/축소 정책을 가지며, 추가로 `toString()` 포맷팅을 제공한다.

```java
package com.datastructure.dynamicarray.pop;

import java.util.Arrays;
import java.util.Objects;

public class DynamicArray<T> {

    private int capacity = 10;
    private T[] elements;
    private int index = 0;

    @SuppressWarnings("unchecked")
    public DynamicArray() {
        this.elements = (T[]) new Object[capacity];
    }

    @SuppressWarnings("unchecked")
    public DynamicArray(int capacity) {
        this.capacity = capacity;
        this.elements = (T[]) new Object[capacity];
    }

    public void add(T element) {
        extendCapacity();
        elements[index] = element;
        index++;
    }

    public void add(int index, T element) {
        checkIndex(index, this.index + 1);
        extendCapacity();
        System.arraycopy(elements, index, elements, index+1, this.index - index);
        elements[index] = element;
        this.index++;
    }

    public T get(int index) {
        checkIndex(index, this.index);
        return elements[index];
    }

    public T set(int index, T element) {
        checkIndex(index, this.index);
        T old = elements[index];
        elements[index] = element;
        return old;
    }

    public T remove(int index) {
        checkIndex(index, this.index);
        T old= elements[index];
        System.arraycopy(elements, index + 1, elements, index, this.index - index - 1);
        this.index--;
        elements[this.index] = null;
        collapseCapacity();
        return old;
    }

    public int size() {
        return index;
    }

    public boolean isEmpty() {
        return index == 0;
    }

    public boolean contains(T element) {
        for (int index = 0; index < this.index; index++) {
            if (Objects.equals(elements[index], element)) {
                return true;
            }
        }
        return false;
    }

    public int indexOf(T element) {
        for (int index = 0; index < this.index; index++) {
            if (Objects.equals(elements[index], element)) {
                return index;
            }
        }
        return -1;
    }

    public void clear() {
        Arrays.fill(elements, 0, this.index, null);
        this.index = 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int index = 0; index < this.index; index++) {
            sb.append(elements[index]);
            if (index != this.index - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private void extendCapacity() {
        if (this.index + 1 > capacity) {
            capacity = (int)(capacity * 1.5);
            elements = Arrays.copyOf(elements, capacity);
        }
    }

    private void collapseCapacity() {
        if ( this.index <= capacity/4 && capacity > 10) {
            capacity = Math.max(capacity/2, 10);
            elements = Arrays.copyOf(elements, capacity);
        }
    }

    private void checkIndex(int index, int bound) {
        if (index < 0 || index >= bound) {
            throw new IndexOutOfBoundsException();
        }
    }
}
```
