package com.datastructure.splay;

import java.util.List;

/**
 * 정렬을 유지하는 트리. 06번, 12번, 15번, 16번과 같은 계약이다.
 *
 * <h2>같은 문제에 다섯 번째 답이다</h2>
 *
 * 06번 이진 탐색 트리가 정렬 입력에서 무너진 뒤로 균형을 잡는 방법을 셋 봤다.
 *
 *   12번 스킵 리스트   동전을 던져 확률로 맞춘다
 *   15번 B-트리        노드를 뚱뚱하게 만들고 위로만 자란다
 *   16번 레드블랙      회전으로 강제로 맞춘다. 보장이다
 *
 * 스플레이 트리는 균형을 아예 목표로 삼지 않는다. 대신 접근한 노드를 뿌리로 끌어올린다.
 * 그것뿐이다. 색 비트도, 높이 필드도, 균형 조건도 없다.
 * 노드에 여분 데이터가 하나도 없다.
 *
 * 그런데도 m 번의 연산 전체가 O(m log n) 이다. 상환(amortized) 이라고 부른다.
 * 연산 하나하나는 O(n) 일 수 있다. 보장이 아니라 총량의 약속이다.
 *
 * <h2>대가</h2>
 *
 *   1. 한 연산의 최악이 O(n) 이다. 응답 시간 상한이 필요한 곳에는 못 쓴다
 *   2. 조회가 트리를 바꾼다. get 도, floorKey 도 쓰기다
 *   3. 그래서 읽기 잠금을 쓸 수 없다. 10번 LRU 캐시와 같은 이유다
 *
 * 이 인터페이스에는 TODO 가 없다. 계약은 주어지는 것이다.
 */
public interface SortedTree<K extends Comparable<K>, V> {

    V put(K key, V value);

    V get(K key);

    boolean containsKey(K key);

    V remove(K key);

    int size();

    boolean isEmpty();

    void clear();

    List<K> keys();

    K firstKey();

    K lastKey();

    /** key 이하인 것 중 가장 큰 키. 없으면 null. */
    K floorKey(K key);

    /** key 이상인 것 중 가장 작은 키. 없으면 null. */
    K ceilingKey(K key);

    /** 가장 긴 뿌리-잎 경로의 길이. 비었으면 0. */
    int height();
}
