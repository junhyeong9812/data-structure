package com.datastructure.stack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LinkedStack 이 Stack 계약을 지키는지.
 *
 * 계약 테스트는 물려받고, 여기에는 **연결 구현에만 있는 성질**만 적는다.
 */
class LinkedStackTest extends StackContractTest {

    @Override
    protected <E> Stack<E> create() {
        return new LinkedStack<>();
    }

    @Test
    @DisplayName("push 는 새 노드를 top 에 얹고 이전 top 을 가리킨다")
    void pushLinksToPreviousTop() {
        LinkedStack<String> stack = new LinkedStack<>();
        stack.push("a");
        LinkedStack.Node<String> first = stack.top;
        stack.push("b");

        assertEquals("b", stack.top.item);
        assertSame(first, stack.top.next, "새 top 이 이전 top 을 가리켜야 한다");
        assertNull(first.next, "맨 아래 노드의 next 는 null 이다");
    }

    @Test
    @DisplayName("pop 한 노드는 스택을 붙잡지 않는다")
    void poppedNodeIsDetached() {
        LinkedStack<String> stack = new LinkedStack<>();
        stack.push("a");
        stack.push("b");
        LinkedStack.Node<String> popped = stack.top;

        stack.pop();

        // 떼어낸 노드가 아래를 계속 가리키면 그 노드 하나가 스택 전체를 살려둔다.
        assertNull(popped.next, "떼어낸 노드의 next 를 끊어야 한다");
    }

    @Test
    @DisplayName("전부 꺼내면 top 이 null 이 된다")
    void topBecomesNullWhenDrained() {
        LinkedStack<Integer> stack = new LinkedStack<>();
        stack.push(1);
        stack.pop();

        assertNull(stack.top);
        assertEquals(0, stack.size());
    }

    @Test
    @DisplayName("clear 는 노드 사슬을 끊는다")
    void clearDetachesChain() {
        LinkedStack<Integer> stack = new LinkedStack<>();
        stack.push(1);
        stack.push(2);
        LinkedStack.Node<Integer> head = stack.top;

        stack.clear();

        assertNull(stack.top);
        assertNull(head.next, "top 만 null 로 만들면 노드끼리 계속 서로를 붙잡는다");
    }

    @Test
    @DisplayName("확장 복사가 없어 push 비용이 일정하다")
    void noResizeCost() {
        // 배열 구현과 달리 "가끔 느려지는" 순간이 없다는 것을 구조로 확인한다.
        // 노드를 세어 보면 push 횟수와 정확히 같다.
        LinkedStack<Integer> stack = new LinkedStack<>();
        for (int i = 0; i < 100; i++) stack.push(i);

        int nodes = 0;
        for (LinkedStack.Node<Integer> n = stack.top; n != null; n = n.next) nodes++;
        assertEquals(100, nodes);
    }
}
