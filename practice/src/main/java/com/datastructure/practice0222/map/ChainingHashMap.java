package com.datastructure.practice0222.map;

import java.util.Objects;

/**
 * 체이닝(Separate Chaining) 방식 해시맵
 *
 * 각 버킷이 연결 리스트를 가지며, 해시 충돌 시 같은 버킷의 리스트에 추가한다.
 *
 * 시간 복잡도:
 *  - 평균: put O(1), get O(1), remove O(1)
 *  - 최악 (모든 키가 같은 버킷): O(n)
 *
 *  적재율(load factor)이 임계값을 넘으면 버킷 수를 2배로 확장(rehash)한다.
 * */
public class ChainingHashMap<K, V> implements Map<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private Node<K, V>[] buckets;
    private int size;
    private final float loadFactor;

    // 노드 (연결 리스트)

    private static class Node<K, V> implements Map.Entry<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public K getKey() { return key; }
        @Override
        public V getValue() { return value; }
        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public String toString() {
            return key+ "=" + value;
        }
    }

    // -- 생성자 --
    @SuppressWarnings("unchecked")
    public ChainingHashMap() {
        this.buckets = new Node[DEFAULT_CAPACITY];
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }

    @SuppressWarnings("unchecked")
    public ChainingHashMap(int initialCapacity, float loadFactor) {
        if(initialCapacity < 1) throw new IllegalArgumentException("용량은 1 이상이어야 합니다.");
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) throw new IllegalArgumentException("적재율이 올바르지 않습니다.");
        this.buckets = new Node[initialCapacity];
        this.loadFactor = loadFactor;
    }

    // 핵심 연산

    @Override
    public void put(K key, V value) {
        int hash = hash(key);
        int idx = index(hash);

        // 기존 키가 있으면 값 덮어쓰기
        for (Node<K, V> node = buckets[idx]; node != null; node = node.next) {
            if(node.hash == hash && Objects.equals(node.key, key)) {
                node.value = value;
                return;
            }
        }

        // 새 노드를 버킷 앞에 삽입
        buckets[idx] = new Node<>(hash, key, value, buckets[idx]);
        size++;

        if (size > buckets.length * loadFactor) {
            resize();
        }
    }

    @Override
    public V get(K key) {
        Node<K, V> node = findNode(key);
        return node != null ? node.value : null;
    }

    @Override
    public V getOrDefault(K key, V defaultValue) {
        Node<K, V> node = findNode(key);
        return node != null ? node.value : defaultValue;
    }

    @Override
    public V remove(K key) {
        int hash = hash(key);
        int idx = index(hash);

        Node<K, V> prev = null;
        for (Node<K, V> node = buckets[idx]; node != null;
             prev = node, node = node.next ) {
            if (node.hash == hash && Objects.equals(node.key, key)) {
                if (prev == null) {
                    buckets[idx] = node.next;
                } else {
                    prev.next = node.next;
                }
                size--;
                return node.value;
            }
        }
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return findNode(key) != null;
    }

    @Override
    public boolean containsValue(V value) {
        for (Node<K, V> bucket : buckets) {
            for (Node<K, V> node = bucket; node != null; node = node.next) {
                if (Objects.equals(node.value, value)) return true;
            }
        }
        return false;
    }

    // 내부 유틸리티
    private int hash(K key) {
        if (key == null) return 0;
        int h = key.hashCode();
        // 상위 비트를 하위 비트에 섞어서 분포를 개선
        return h ^ (h >>> 16);
    }

    private int index(int hash) {
        return hash & (buckets.length - 1);
    }

    private Node<K, V> findNode(K key) {
        int hash = hash(key);
        int idx = index(hash);
        for (Node<K, V> node = buckets[idx]; node != null; node = node.next) {
            if (node.hash == hash && Objects.equals(node.key, key)) {
                return node;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldBuckets = buckets;
        buckets = new Node[oldBuckets.length * 2];

        for (Node<K, V> bucket : oldBuckets) {
            for (Node<K, V> node = bucket; node != null;) {
                Node<K, V> next = node.next;
                int idx = index(node.hash);
                node.next = buckets[idx];
                buckets[idx] = node;
                node = next;
            }
        }
    }
}
