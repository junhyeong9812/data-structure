package com.datastructure.practice0222.map;

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
}
