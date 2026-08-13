# oop/LinkedList.java

연결 리스트 인터페이스. 단일/이중 구현체가 공통으로 따르는 계약. 양 끝/임의 위치 추가·삭제, 순회, 검색, `reverse` 포함.

```java
package com.datastructure.linkedlist.oop;

public interface LinkedList<E> {

    public void addFirst(E element);

    public void addLast(E element);

    public void add(int index, E element);

    public E removeFirst();

    public E removeLast();

    public E remove(int index);

    public E get(int index);

    public E set(int index, E element);

    public int size();

    public boolean isEmpty();

    public boolean contains(E element);

    public int indexOf(E element);

    public void clear();

    public void reverse();
}
```
