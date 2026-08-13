package com.datastructure.practice0222.map;

import java.util.*;

/**
 * 개방 주소법(Open Addressing) - 선형 탐사(Linear Probing) 방식 해시맵
 *
 * 충돌 시 다음 빈 슬롯을 순차적으로 탐색하여 저장한다.
 * 배열 하나만 사용하므로 캐시 친화적이며, 적재율이 낮을 때 체이닝보다 빠를 수 있다.
 *
 * 시간 복잡도:
 *  - 평균: put O(1), get O(1), remove O(1)
 *  - 최악 (클러스터링 심화): O(n)
 *
 *  삭제 시 tombstone(DELETED 마커)을 사용하여 탐사 체인이 끊기지 않도록 한다.
 * */
public class LinearProbingHashMap<K, V> implements Map<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.5f; // 개방 주소법은 낮은 적재율 권장

    private K[] keys;
    private V[] values;
    private boolean[] occupied; // 슬롯에 값이 있는 지 (null 키 지원용)
    private boolean[] deleted; // tombstone 배열
    private int size;
    private int capacity;
    private final float loadFactor;

    // -- 생성자 --

    @SuppressWarnings("unchecked")
    public LinearProbingHashMap() {
        this.capacity = DEFAULT_CAPACITY;
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.keys = (K[]) new Object[capacity];
        this.values = (V[]) new Object[capacity];
        this. occupied = new boolean[capacity];
        this.deleted = new boolean[capacity];
    }

    @SuppressWarnings("unchecked")
    public LinearProbingHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 1) throw new IllegalArgumentException("용량은 1 이상이어야 합니다.");
        if (loadFactor <= 0 || loadFactor >= 1 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("적재율은 0 초과 1 미만이어야 합니다.");
        this.capacity = initialCapacity;
        this.loadFactor = loadFactor;
        this.keys = (K[]) new Object[capacity];
        this.values = (V[]) new Object[capacity];
        this.occupied = new boolean[capacity];
        this.deleted = new boolean[capacity];
    }

    // -- 핵심 연산 --

    @Override
    public void put(K key, V value) {
        if (size >= capacity * loadFactor) {
            resize(capacity * 2);
        }

        int idx = findSlot(key);

        // 기본 키 -> 값 덮어쓰기
        if (occupied[idx] && !deleted[idx]) {
            values[idx] = value;
            return;
        }

        // 새 삽입
        keys[idx] = key;
        values[idx] = value;
        occupied[idx] = true;
        deleted[idx] = false;
        size++;
    }

    @Override
    public V get(K key) {
        int idx = findExisting(key);
        return idx == -1 ? null : values[idx];
    }

    @Override
    public V getOrDefault(K key, V defaultValue) {
        int idx = findExisting(key);
        return idx == -1 ? defaultValue : values[idx];
    }

    @Override
    public V remove(K key) {
        int idx = findExisting(key);
        if (idx == -1) return null;

        V old = values[idx];
        // tombstone 처리: 키/값은 남겨두고 deleted 마킹
        deleted[idx] = true;
        size--;

        // 적재율이 너무 낮아지면 축소
        if (capacity > DEFAULT_CAPACITY && size <= capacity * loadFactor/4) {
            resize(Math.max(capacity / 2, DEFAULT_CAPACITY));
        }

        return old;
    }

    @Override
    public boolean containsKey(K key) {
        return findExisting(key) != -1;
    }

    @Override
    public boolean containsValue(V value) {
        for (int i = 0; i < capacity; i++) {
            if (occupied[i] && !deleted[i] && Objects.equals(values[i], value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        capacity = DEFAULT_CAPACITY;
        keys = (K[]) new Object[capacity];
        values = (V[]) new Object[capacity];
        occupied = new boolean[capacity];
        deleted = new boolean[capacity];
        size = 0;
    }

    // -- 뷰 --

    @Override
    public Set<K> keySet() {
        Set<K> set = new LinkedHashSet<>();
        for (int i = 0; i < capacity; i++) {
            if (occupied[i] && !deleted[i]) set.add(keys[i]);
        }
        return set;
    }

    @Override
    public Collection<V> values() {
        List<V> list = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            if (occupied[i] && !deleted[i]) list.add(values[i]);
        }
        return list;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> set = new LinkedHashSet<>();
        for (int i = 0; i < capacity; i++) {
            if (occupied[i] && !deleted[i]) {
                final int idx = i;
                set.add(new Entry<K, V>() {
                    @Override public K getKey() { return keys[idx]; }
                    @Override public V getValue() { return values[idx]; }
                    @Override public V setValue(V value) {
                        V old = values[idx];
                        values[idx] = value;
                        return old;
                    }
                    @Override public String toString() { return keys[idx] + "=" + values[idx]; }
                });
            }
        }
        return set;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return entrySet().iterator();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{");
        boolean first = true;
        for (int i = 0; i < capacity; i++) {
            if (occupied[i] && !deleted[i]) {
                if (!first) result.append(", ");
                result.append(keys[i]).append("=").append(values[i]);
                first = false;
            }
        }
        return result.append("}").toString();
    }

    // -- 내부 유틸리티 --
    private int hash(K key) {
        if (key == null) return 0;
        int h = key.hashCode();
        return h ^ (h>>>16);
    }

    private int index(int hash) {
        return (hash & 0x7FFFFFFF) % capacity;
    }

    /**
     * put용: 키가 있으면 그 위치, 없으면 삽입할 빈 슬롯 반환.
     * tombstone 위치를 우선적으로 재활용한다.
     * */
    private int findSlot(K key) {
        int hash = hash(key);
        int idx = index(hash);
        int firstDeleted = -1;
        for (int i = 0; i < capacity; i++) {
            int probe = (idx + i) % capacity;

            if (!occupied[probe] && !deleted[probe]) {
                // 완전히 빈 슬롯
                return firstDeleted != -1 ? firstDeleted : probe;
            }
            if (deleted[probe]) {
                if (firstDeleted == -1) firstDeleted = probe;
                continue;
            }
            if (Objects.equals(keys[probe], key)) {
                return probe; // 기본 키 발견
            }
        }
        return firstDeleted;
    }

    /**
     * get/remove/containsKey용: 키가 존재하면 인덱스, 없으면 -1
     * */
    public int findExisting(K key) {
        int hash = hash(key);
        int idx = index(hash);

        for (int i = 0; i < capacity; i++) {
            int probe = (idx + i) % capacity;

            if (!occupied[probe] && !deleted[probe]) {
                return -1;
            }
            if (!deleted[probe] && occupied[probe] && Objects.equals(keys[probe], key)) {
                return probe;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        K[] oldKeys = keys;
        V[] oldValues = values;
        boolean[] oldOccupied = occupied;
        boolean[] oldDeleted = deleted;
        int oldCapacity = capacity;

        capacity = newCapacity;
        keys = (K[]) new Object[capacity];
        values = (V[]) new Object[capacity];
        occupied = new boolean[capacity];
        deleted = new boolean[capacity];
        size = 0;

        for (int i = 0; i < oldCapacity; i++) {
            if (oldOccupied[i] && oldDeleted[i]) {
                put(oldKeys[i], oldValues[i]);
            }
        }
    }
}
