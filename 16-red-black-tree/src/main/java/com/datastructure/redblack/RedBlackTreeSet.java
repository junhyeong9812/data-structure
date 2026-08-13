package com.datastructure.redblack;

import java.util.List;

/**
 * RedBlackTreeMap 을 집합으로 쓴 것. 12번 SkipListSet 과 똑같은 방식이다.
 *
 * 자바의 TreeSet 이 정확히 이렇게 만들어져 있다. TreeMap 을 안에 두고 값 자리에 상수를 넣는다.
 */
public class RedBlackTreeSet<K extends Comparable<K>> {

    private static final Object PRESENT = new Object();

    private final RedBlackTreeMap<K, Object> map = new RedBlackTreeMap<>();

    public boolean add(K key) {
        // TODO 1: 맵에 넣고 **새로 들어갔는지**를 반환한다. 별도 조회 없이.
        throw new UnsupportedOperationException("TODO 1: add");
    }

    public boolean contains(K key) {
        return map.containsKey(key);
    }

    public boolean remove(K key) {
        // TODO 2: 같은 방식으로.
        throw new UnsupportedOperationException("TODO 2: remove");
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public void clear() {
        map.clear();
    }

    public List<K> toList() {
        return map.keys();
    }

    public K first() {
        return map.firstKey();
    }

    public K last() {
        return map.lastKey();
    }

    public K floor(K key) {
        return map.floorKey(key);
    }

    public K ceiling(K key) {
        return map.ceilingKey(key);
    }
}
