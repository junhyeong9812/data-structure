package com.datastructure.redblack;

import java.util.List;

/**
 * 정렬을 유지하는 트리. 06번, 12번, 15번과 같은 계약이다.
 *
 * 같은 문제에 네 번째 답이다. 06번 이진 탐색 트리가 정렬 입력에서 무너진 뒤로
 *
 *   12번 스킵 리스트   동전을 던져 확률로 균형을 맞춘다. 회전이 없다
 *   15번 B-트리        노드를 뚱뚱하게 만들고 위로만 자란다. 회전이 없다
 *   16번 여기      회전으로 강제로 맞춘다. 보장이다
 *
 * 왜 또 필요한가. 앞의 둘에는 각각 대가가 있었다.
 *
 *   스킵 리스트: 최악을 막지 못한다. 확률이 낮을 뿐이다
 *   B-트리: 노드가 커서 메모리 안에서는 낭비다. 캐시 한 줄에 안 들어간다
 *
 * 레드블랙 트리는 메모리 안에서, 보장으로 균형을 잡는다.
 * 노드는 이진 트리 그대로이고 색 비트 하나만 더 든다.
 * 자바의 TreeMap/TreeSet, C++ 의 std::map, 리눅스 커널의 스케줄러가 이것이다.
 *
 * 레드블랙 트리는 B-트리를 이진 트리로 흉내낸 것이다.
 * 여기서 만드는 좌편향(left-leaning) 판은 2-3 트리와 정확히 대응한다.
 *
 *   빨간 링크로 이어진 두 노드  =  2-3 트리의 키 2개짜리 노드
 *   검은 링크                    =  2-3 트리의 진짜 간선
 *
 * 그래서 검은 높이가 곧 2-3 트리의 높이다. 회전과 색 바꾸기는
 * 2-3 트리의 분할을 이진 트리 위에서 흉내내는 동작이다.
 * 이 대응이 보이면 회전이 마법이 아니게 된다.
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
