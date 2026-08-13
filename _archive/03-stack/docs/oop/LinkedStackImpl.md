# oop/LinkedStackImpl.java

연결 리스트 기반 스택. `top` 노드만 보유하며 push/pop은 O(1). `size`는 노드를 순회하므로 O(n). `toArray`는 top→bottom 순회 결과를 거꾸로 채워 bottom→top 순서로 반환.

```java
package com.datastructure.stack.oop;

import java.util.*;

public class LinkedStackImpl<E> implements Stack<E> {

    private Node<E> top;

    private class Node<E> {
        E element;
        Node<E> prev;

        public Node(E element) {
            this.element = element;
        }
    }
    public void push(E element) {
        Node<E> addStack = new Node<>(element);
        addStack.prev = top;
        top = addStack;
    }

    public E pop() {
        if (top == null) {
            throw new EmptyStackException();
        }
        E popElement = top.element;
        top = top.prev;
        return popElement;
    }

    public E peek() {
        if (top == null) {
            throw new EmptyStackException();
        }
        return top.element;
    }

    public E top() {
        if (top == null) {
            throw new EmptyStackException();
        }
        return top.element;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size(){
        int count = 0;
        Node<E> checkNode = top;
        while(checkNode != null) {
            checkNode = checkNode.prev;
            count++;
        }
        return count;
    }

    public void clear() {
        top = null;
    }

    public int search(E element) {
        int count = 0;
        int size = size();
        Node<E> checkNode = top;
        while(checkNode != null) {
            if (Objects.equals(checkNode.element,element)) {
                return size - count;
            }
            checkNode = checkNode.prev;
            count++;
        }
        return -1;
    }

    public Object[] toArray(){
        int size = size();
        Object[] result = new Object[size];
        Node<E>  checkNode = top;
        for (int i = size; i > 0; i--) {
            result[i - 1] = checkNode.element;
            checkNode = checkNode.prev;
        }
        return result;
    }
}
```
