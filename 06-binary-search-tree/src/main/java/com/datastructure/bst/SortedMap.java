package com.datastructure.bst;

/**
 * 키 순서를 유지하는 맵.
 *
 * 05번 해시맵은 O(1) 을 얻는 대가로 순서를 버렸다. 키를 일부러 흩뿌리기 때문이다.
 * 흩뿌리는 순간 "5 옆에 뭐가 있나"라는 정보가 사라진다. 5 와 6 이 완전히 다른 버킷으로 간다.
 *
 * 여기서는 반대 거래를 한다. **왼쪽은 작고 오른쪽은 크다**를 구조에 새긴다.
 * 조회가 O(1) 에서 O(log n) 으로 나빠지는 대신, 순서를 묻는 질문들에 답할 수 있게 된다.
 *
 * | 질문 | 해시맵 | 이 구조 |
 * |------|--------|---------|
 * | get(k) | O(1) | O(log n) |
 * | 가장 작은 키 | O(n) | O(log n) |
 * | k 보다 큰 것 중 가장 작은 키 | O(n) | O(log n) |
 * | 정렬 순회 | O(n log n) (정렬 필요) | O(n) |
 *
 * 어느 쪽이 낫냐는 질문이 잘못됐다. **무엇을 물을 것인지가 자료구조를 정한다.**
 *
 * 이 인터페이스에는 TODO 가 없다. 계약은 주어지는 것이다.
 */
public interface SortedMap<K extends Comparable<K>, V> {

    /** 이미 있는 키면 값을 바꾸고 이전 값을 반환한다. 없었으면 null. */
    V put(K key, V value);

    V get(K key);

    boolean containsKey(K key);

    /** 지우고 그 값을 반환한다. 없었으면 null. */
    V remove(K key);

    int size();

    boolean isEmpty();

    void clear();

    // ------------------------------------------------------------------
    // 여기부터가 해시맵이 못 하는 것들
    // ------------------------------------------------------------------

    /** 가장 작은 키. 비었으면 NoSuchElementException. */
    K firstKey();

    /** 가장 큰 키. 비었으면 NoSuchElementException. */
    K lastKey();

    /** key 이하인 키 중 가장 큰 것. 없으면 null. */
    K floorKey(K key);

    /** key 이상인 키 중 가장 작은 것. 없으면 null. */
    K ceilingKey(K key);

    /** 오름차순으로 정렬된 전체 키. */
    Iterable<K> keys();

    /** from 이상 to 이하인 키를 오름차순으로. 범위가 뒤집혀 있으면 빈 결과. */
    Iterable<K> keysInRange(K from, K to);
}
