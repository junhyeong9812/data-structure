package com.datastructure.splay;

import java.util.List;

public class SplayTreeMap<K extends Comparable<K>, V> implements SortedTree<K, V> {

    private final SplayTree<K, V> tree = new SplayTree<>();

    SplayTree<K, V> tree() {
        return tree;
    }

    @Override
    public V put(K key, V value) {
        return tree.put(key, value);
    }

    @Override
    public V get(K key) {
        return tree.get(key);
    }

    @Override
    public boolean containsKey(K key) {
        return tree.containsKey(key);
    }

    @Override
    public V remove(K key) {
        return tree.remove(key);
    }

    @Override
    public int size() {
        return tree.size();
    }

    @Override
    public boolean isEmpty() {
        return tree.isEmpty();
    }

    @Override
    public void clear() {
        tree.clear();
    }

    @Override
    public List<K> keys() {
        return tree.keys();
    }

    @Override
    public K firstKey() {
        return tree.firstKey();
    }

    @Override
    public K lastKey() {
        return tree.lastKey();
    }

    @Override
    public K floorKey(K key) {
        return tree.floorKey(key);
    }

    @Override
    public K ceilingKey(K key) {
        return tree.ceilingKey(key);
    }

    @Override
    public int height() {
        return tree.height();
    }
}
