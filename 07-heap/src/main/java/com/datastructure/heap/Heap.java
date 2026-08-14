package com.datastructure.heap;

/**
 * 우선순위가 가장 높은 것을 빨리 꺼내는 자료구조.
 *
 * 06번 이진 탐색 트리는 전부 정렬 상태를 유지했다. 그래서 순서에 관한 무엇이든 물을 수 있었다.
 * 그런데 실무에서는 그렇게까지 필요 없는 경우가 훨씬 많다.
 * 작업 큐, 다익스트라, 이벤트 스케줄러는 전부 "지금 가장 급한 것 하나" 만 알면 된다.
 *
 * 힙은 딱 그만큼만 한다. 부분 순서만 지킨다.
 *   부모가 자식보다 앞선다는 것만 보장하고, 형제끼리는 아무 관계가 없다.
 *   덜 지키니까 더 싸다. 그게 이 자료구조의 거래다.
 *
 * | 질문 | BST | 힙 |
 * |------|-----|-----|
 * | 가장 앞선 것 | O(log n) | O(1) |
 * | 넣기 | O(log n) | O(log n) |
 * | 가장 앞선 것 빼기 | O(log n) | O(log n) |
 * | 임의의 키 찾기 | O(log n) | O(n) |
 * | 정렬 순회 | O(n) | O(n log n) |
 *
 * 우선순위 큐(priority queue)라고도 부른다. 큐라는 이름이 붙었지만
 * 04번 큐와 달리 나가는 순서가 들어온 순서가 아니라 우선순위다.
 *
 * 구현이 둘이다.
 *   SortedListHeap  정렬된 리스트에 끼워 넣는다. 가장 먼저 떠오르는 방법이고, 동작은 맞다
 *   BinaryHeap      배열로 트리를 표현한다. 그 방법의 문제를 고친 것
 *
 * 이 인터페이스에는 TODO 가 없다. 계약은 주어지는 것이다.
 */
public interface Heap<E> {

    /** 넣는다. null 은 허용하지 않는다(IllegalArgumentException). */
    void insert(E element);

    /**
     * 가장 앞선 것을 보기만 한다.
     *
     * @throws java.util.NoSuchElementException 비어 있을 때
     */
    E peek();

    /**
     * 가장 앞선 것을 꺼낸다.
     *
     * @throws java.util.NoSuchElementException 비어 있을 때
     */
    E poll();

    int size();

    boolean isEmpty();

    void clear();
}
