package com.datastructure.hashmap;

/**
 * 키로 값을 찾는 자료구조.
 *
 * 01~04 는 전부 순서가 있는 구조였다. 인덱스든 앞뒤든, 어디에 있는지로 찾았다.
 * 여기서는 다르다. 값 자체로부터 위치를 계산한다. 그게 해시다.
 *
 * 잘 되면 넣기/찾기/지우기가 전부 평균 O(1) 이다. 배열의 O(1) 접근을 임의의 키로 확장한 셈이다.
 * 대신 세 가지 대가를 치른다.
 *   1. 순서가 없다. 넣은 순서도, 정렬 순서도 보장되지 않는다.
 *   2. 최악은 O(n) 이다. 키들이 한곳에 몰리면 그냥 리스트가 된다.
 *   3. 공간을 여유 있게 잡아야 한다. 꽉 채우면 충돌이 급증한다.
 *
 * 구현이 셋이다. 충돌을 처리하는 방식이 다르다.
 *   ChainingHashMap        같은 자리에 여러 개가 오면 그 자리에 사슬로 매단다
 *   LinearProbingHashMap   자리가 차 있으면 옆칸으로 밀어 넣는다
 *   LinkedHashMap          체이닝에 삽입 순서를 얹는다 (순서 없음이라는 대가 1번을 되사온다)
 *
 * 이 인터페이스에는 TODO 가 없다. 계약은 주어지는 것이다.
 */
public interface Map<K, V> {

    /**
     * 키에 값을 넣는다. 이미 있던 키면 값을 바꾸고 이전 값을 반환한다. 없었으면 null.
     *
     * null 키는 허용하지 않는다(IllegalArgumentException). null 값은 허용한다.
     * "값이 null 인 것"과 "키가 없는 것"을 get 만으로는 구분할 수 없으니 containsKey 가 따로 있다.
     */
    V put(K key, V value);

    /** 없으면 null. */
    V get(Object key);

    boolean containsKey(Object key);

    /** 지우고 그 값을 반환한다. 없었으면 null. */
    V remove(Object key);

    int size();

    boolean isEmpty();

    void clear();

    /**
     * 담긴 키 전부. 순서는 구현이 정한다.
     *
     * ChainingHashMap 과 LinearProbingHashMap 은 아무 순서나 준다(버킷 배치에 따라 달라진다).
     * LinkedHashMap 만 넣은 순서를 지킨다.
     * 계약 테스트가 "집합으로서 같은지"만 검사하고 순서는 LinkedHashMap 테스트에서만 보는 이유다.
     */
    Iterable<K> keys();
}
