# oop/DynamicArray.java

동적 배열 인터페이스. 제네릭 요소 타입 `E` 지원. `Iterable<E>`를 확장하여 향상된 for문 사용 가능.

```java
package com.datastructure.dynamicarray.oop;

import java.util.Iterator;

public interface DynamicArray<E> extends Iterable<E> {

    public void add(E element);

    public void add(int index, E element);

    public E get(int index);

    public E set(int index, E element);

    public E remove(int index);

    public int size();

    public boolean isEmpty();

    public boolean contains(E element);

    public int indexOf(E element);

    public void clear();
}
```
