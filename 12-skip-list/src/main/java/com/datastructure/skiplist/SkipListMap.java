package com.datastructure.skiplist;

import java.util.List;

/**
 * SkipList 를 OrderedMap 계약에 맞춘 것. **거의 전부 위임이다.**
 *
 * 왜 둘로 나누는가. SkipList 는 자료구조이고 OrderedMap 은 계약이다.
 * 06번 BinarySearchTree, 15번 BTree, 16번 RedBlackTree 가 같은 계약을 구현할 수 있고,
 * 쓰는 쪽은 무엇이 안에 있는지 몰라도 된다.
 *
 * 값이 null 이면 안 되는 이유는 10번 캐시와 같다. get 의 null 이 "없다"를 뜻하기 때문이다.
 */
public class SkipListMap<K extends Comparable<K>, V> implements OrderedMap<K, V> {

    private final SkipList<K, V> list;

    public SkipListMap() {
        this.list = new SkipList<>();
    }

    public SkipListMap(long seed) {
        this.list = new SkipList<>(seed);
    }

    SkipList<K, V> list() {
        return list;
    }

    @Override
    public V put(K key, V value) {
        if (value == null) {
            throw new IllegalArgumentException("값은 null 일 수 없다 - get 의 null 이 '없다'를 뜻하기 때문이다");
        }
        return list.put(key, value);
    }

    @Override
    public V get(K key) {
        return list.get(key);
    }

    @Override
    public boolean containsKey(K key) {
        return list.containsKey(key);
    }

    @Override
    public V remove(K key) {
        return list.remove(key);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public K firstKey() {
        return list.firstKey();
    }

    @Override
    public K lastKey() {
        return list.lastKey();
    }

    @Override
    public K floorKey(K key) {
        return list.floorKey(key);
    }

    @Override
    public K ceilingKey(K key) {
        return list.ceilingKey(key);
    }

    @Override
    public List<K> keys() {
        return list.keys();
    }

    @Override
    public List<K> keysInRange(K from, K to) {
        return list.keysInRange(from, to);
    }
}
