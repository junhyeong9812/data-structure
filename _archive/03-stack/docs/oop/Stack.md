# oop/Stack.java

스택 인터페이스. push/pop/peek/top 외에 `search` (1-based 위치 반환)와 `toArray`를 포함한다.

```java
package com.datastructure.stack.oop;

public interface Stack<E> {

    public void push(E element);

    public E pop();

    public E peek();

    public E top();

    public boolean isEmpty();

    public int size();

    public void clear();

    public int search(E element);

    public Object[] toArray();
}
```
