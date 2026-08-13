package com.datastructure.linkedlist;

import java.util.function.Predicate;

/**
 * 연결 리스트로 푸는 응용 문제들.
 *
 * 전부 **List 인터페이스**만 받는다. 그래서 단일이든 이중이든 같은 코드가 돌아간다.
 * 노드를 직접 만질 수 없다는 뜻이기도 하다. 그 제약이 이 문제들의 핵심이다.
 *
 * 인덱스로 접근하면 매번 앞에서부터 세므로 O(n) 이다.
 * 루프 안에서 그걸 부르면 전체가 O(n^2) 이 된다.
 * **인덱스를 안 쓰고 순회하는 유일한 방법이 Iterator 다.**
 *
 * 계약: 정렬을 다루는 문제(3)의 입력에는 null 이 들어오지 않는다고 가정한다.
 */
public final class ListProblems {

    private ListProblems() {
    }

    /**
     * 문제 1. 조건에 맞는 원소를 모두 제거 (이 문제집의 함정)
     *
     * predicate 가 true 인 원소를 전부 없애고 제거한 개수를 반환한다.
     *
     * 함정
     *   `for (int i = size-1; i >= 0; i--) if (test(get(i))) remove(i)` 가 가장 먼저 떠오른다.
     *   답은 맞다. 그런데 get(i) 도 remove(i) 도 매번 앞에서부터 세므로 각각 O(n) 이고,
     *   그걸 n 번 부르면 O(n^2) 이다. 테스트에 10만 건 시간 제한이 있다.
     *
     *   01번 배열에서는 remove 의 **시프트** 때문에 O(n^2) 이었다.
     *   여기서는 **탐색** 때문이다. 증상은 같은데 원인이 다르다.
     *
     * 생각할 것
     *   - 인덱스를 안 쓰고 훑을 방법이 있는가?
     *   - 훑으면서 지우려면 반복자에 무엇이 있어야 하는가?
     *
     * TODO(18): 구현하라. O(n) 이어야 한다.
     */
    public static <E> int removeAllIf(List<E> list, Predicate<? super E> predicate) {
        throw new UnsupportedOperationException("TODO(18): removeAllIf");
    }

    /**
     * 문제 2. 가운데 값 찾기
     *
     * 원소가 짝수 개면 뒤쪽 것을 반환한다.
     *   [1, 2, 3]     -> 2
     *   [1, 2, 3, 4]  -> 3
     *
     * 조건: size() 를 쓰지 말고 리스트를 **한 번만** 훑어서 찾아라.
     *      길이를 모르는 스트림에서도 통하는 기법이다.
     *
     * 생각할 것
     *   - 한 칸씩 가는 것과 두 칸씩 가는 것을 동시에 움직이면?
     *   - 인덱스가 없으니 반복자를 두 개 쓰면 된다.
     *
     * TODO(19): 구현하라. 비어 있으면 NoSuchElementException.
     */
    public static <E> E findMiddle(List<E> list) {
        throw new UnsupportedOperationException("TODO(19): findMiddle");
    }

    /**
     * 문제 3. 정렬된 두 리스트 병합
     *
     * 오름차순 리스트 둘을 합쳐 result 에 오름차순으로 담는다. a 와 b 는 건드리지 않는다.
     * result 는 비어 있는 상태로 들어온다.
     *
     * 결과를 담을 리스트를 인자로 받는 이유가 있다.
     * 여기서 `new DoublyLinkedList<>()` 라고 쓰면 이 코드가 한 구현에 묶인다.
     * **어떤 구현을 쓸지는 호출자가 정한다.**
     *
     * 생각할 것
     *   - 양쪽을 동시에 훑어야 한다. 반복자 두 개를 어떻게 나란히 움직이는가?
     *   - Iterator 는 "다음 값을 미리 보기"가 없다. 어떻게 우회하는가?
     *
     * TODO(20): 구현하라. O(n+m) 이어야 한다.
     */
    public static void mergeSorted(List<Integer> a, List<Integer> b, List<Integer> result) {
        throw new UnsupportedOperationException("TODO(20): mergeSorted");
    }
}
