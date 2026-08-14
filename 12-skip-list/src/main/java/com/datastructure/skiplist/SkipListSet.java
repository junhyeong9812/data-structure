package com.datastructure.skiplist;

import java.util.List;

/**
 * SkipListMap 을 집합으로 쓴 것. 새 자료구조가 아니다.
 *
 * 값 자리에 상수 하나를 넣고 맵을 그대로 쓴다.
 * 자바의 TreeSet 이 TreeMap 을, HashSet 이 HashMap 을 그렇게 쓴다.
 *
 * add 가 "새로 들어갔는지"를 어떻게 아는가. put 이 옛 값을 돌려주기 때문이다.
 * null 이면 새로 들어간 것이다. 계약을 그렇게 설계해두면 이런 재사용이 공짜로 된다.
 */
public class SkipListSet<K extends Comparable<K>> implements OrderedSet<K> {

    private static final Object PRESENT = new Object();

    private final SkipListMap<K, Object> map;

    public SkipListSet() {
        this.map = new SkipListMap<>();
    }

    public SkipListSet(long seed) {
        this.map = new SkipListMap<>(seed);
    }

    @Override
    public boolean add(K key) {
        // TODO 1: 맵에 넣고, **새로 들어갔는지**를 반환한다.
        //
        // 별도의 조회가 필요 없다. put 이 이미 그 정보를 준다.
        // containsKey 로 먼저 확인하면 같은 길을 두 번 걷는 셈이다.
        throw new UnsupportedOperationException("TODO 1: add");
    }

    @Override
    public boolean contains(K key) {
        return map.containsKey(key);
    }

    @Override
    public boolean remove(K key) {
        // TODO 2: 같은 방식으로.
        throw new UnsupportedOperationException("TODO 2: remove");
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public K first() {
        return map.firstKey();
    }

    @Override
    public K last() {
        return map.lastKey();
    }

    @Override
    public K floor(K key) {
        return map.floorKey(key);
    }

    @Override
    public K ceiling(K key) {
        return map.ceilingKey(key);
    }

    @Override
    public List<K> toList() {
        return map.keys();
    }

    @Override
    public List<K> range(K from, K to) {
        return map.keysInRange(from, to);
    }
}
