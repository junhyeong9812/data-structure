package com.datastructure.linkedlist;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * [구현] 연결 리스트 응용 문제.
 *
 * 세 문제 전부 인덱스를 한 번도 쓰지 않는다. 전부 Iterator 로 푼다.
 * 인덱스를 쓰는 순간 탐색이 O(n) 이 되고, 루프 안에서 부르면 O(n^2) 이 된다.
 */
public final class ListProblems {

    private ListProblems() {
    }

    /**
     * 문제 1. 조건부 일괄 삭제.
     *
     * 훑으면서 그 자리에서 지운다. Iterator.remove() 는 "방금 돌려준 노드"를 알고 있으므로
     * 다시 찾을 필요가 없다. 전체 O(n).
     *
     * get(i)/remove(i) 로 풀면 매번 앞에서부터 세느라 O(n^2) 이 된다.
     * **Iterator 가 문법 설탕이 아니라 복잡도를 바꾸는 장치인 이유가 이것이다.**
     */
    public static <E> int removeAllIf(List<E> list, Predicate<? super E> predicate) {
        int removed = 0;
        Iterator<E> it = list.iterator();
        while (it.hasNext()) {
            if (predicate.test(it.next())) {
                it.remove();
                removed++;
            }
        }
        return removed;
    }

    /**
     * 문제 2. 가운데 값 (두 포인터).
     *
     * 반복자를 두 개 만들어 하나는 한 칸씩, 하나는 두 칸씩 나아간다.
     * 빠른 쪽이 끝에 닿으면 느린 쪽이 절반 지점이다. 길이를 몰라도 되고 한 번만 훑는다.
     *
     * 짝수 개일 때 뒤쪽을 고르는 것은 이 루프 모양에서 자연스럽게 나온다.
     */
    public static <E> E findMiddle(List<E> list) {
        Iterator<E> slow = list.iterator();
        Iterator<E> fast = list.iterator();
        if (!slow.hasNext()) {
            throw new NoSuchElementException("비어 있다");
        }
        E middle = slow.next();
        while (fast.hasNext()) {
            fast.next();
            if (!fast.hasNext()) {
                break;
            }
            fast.next();
            middle = slow.next();
        }
        return middle;
    }

    /**
     * 문제 3. 정렬된 두 리스트 병합.
     *
     * Iterator 에는 "다음 값 미리 보기"가 없다. 그래서 각 쪽에서 꺼낸 값을 하나씩 손에 들고 비교한다.
     * 들고 있던 값을 쓰면 그때 다음 값을 꺼낸다.
     *
     * null 을 "더 이상 없음" 표시로 쓸 수 있는 것은 이 문제의 입력에 null 이 없다고 계약했기 때문이다.
     */
    public static void mergeSorted(List<Integer> a, List<Integer> b, List<Integer> result) {
        Iterator<Integer> ia = a.iterator();
        Iterator<Integer> ib = b.iterator();
        Integer x = ia.hasNext() ? ia.next() : null;
        Integer y = ib.hasNext() ? ib.next() : null;

        while (x != null && y != null) {
            if (x <= y) {
                result.add(x);
                x = ia.hasNext() ? ia.next() : null;
            } else {
                result.add(y);
                y = ib.hasNext() ? ib.next() : null;
            }
        }
        while (x != null) {
            result.add(x);
            x = ia.hasNext() ? ia.next() : null;
        }
        while (y != null) {
            result.add(y);
            y = ib.hasNext() ? ib.next() : null;
        }
    }
}
