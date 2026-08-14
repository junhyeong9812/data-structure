package com.datastructure.openaddr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * [구현] tombstone 을 쓰는 세 구현의 공통 뼈대.
 *
 * 선형, 이차, 이중 해싱은 넣고 찾고 지우는 절차가 글자 그대로 같다.
 * 다른 것은 probe 하나뿐이다. 그래서 여기에 절차를 두고 수열만 자식이 정한다.
 *
 * 필드 이름 keys, values, states 는 테스트가 직접 들여다본다.
 */
public abstract class ProbeSequenceMap<K, V> implements ProbeMap<K, V> {

    static final byte EMPTY = 0;
    static final byte OCCUPIED = 1;
    static final byte TOMBSTONE = 2;

    static final int DEFAULT_CAPACITY = 8;
    static final double DEFAULT_MAX_LOAD = 0.5;

    Object[] keys;
    Object[] values;
    byte[] states;
    int size;          // OCCUPIED 개수
    int used;          // OCCUPIED + TOMBSTONE 개수. 리사이즈 판단은 이걸로 한다
    int mask;          // capacity - 1. 용량이 2의 거듭제곱이라 & 로 나머지를 구한다
    final double maxLoad;
    int lastProbes;

    protected ProbeSequenceMap() {
        this(DEFAULT_CAPACITY, DEFAULT_MAX_LOAD);
    }

    /**
     * 용량과 최대 부하율을 정해서 만든다. 한계 측정이 이 생성자를 쓴다.
     *
     * 부하율 0.9 에서 무슨 일이 일어나는지 보려면 그 부하율까지 리사이즈가 일어나면 안 된다.
     * 용량은 2의 거듭제곱으로 올림한다. 이차 탐사와 이중 해싱이 그 성질에 기대고 있다.
     */
    protected ProbeSequenceMap(int capacity, double maxLoad) {
        if (maxLoad <= 0 || maxLoad > 1) {
            throw new IllegalArgumentException("부하율은 0 초과 1 이하여야 한다: " + maxLoad);
        }
        this.maxLoad = maxLoad;
        allocate(Hashing.tableSizeFor(capacity));
    }

    private void allocate(int capacity) {
        this.keys = new Object[capacity];
        this.values = new Object[capacity];
        this.states = new byte[capacity];
        this.mask = capacity - 1;
        this.size = 0;
        this.used = 0;
    }

    /**
     * i 번째 탐사가 볼 칸. i = 0 이 홈 버킷이다.
     *
     * 이 메서드 하나가 세 구현의 차이 전부다.
     */
    abstract int probe(int hash, int i);

    // ------------------------------------------------------------------
    // 채워져 있는 부분
    // ------------------------------------------------------------------

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int capacity() {
        return keys.length;
    }

    @Override
    public int lastProbeCount() {
        return lastProbes;
    }

    /** 지워진 자리 수. 넣고 지우기를 반복하면 이게 쌓이고, 쌓인 만큼 탐사가 길어진다. */
    public int tombstones() {
        return used - size;
    }

    @Override
    public boolean containsKey(Object key) {
        return indexOf(key) >= 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        int i = indexOf(key);
        return i < 0 ? null : (V) values[i];
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterable<K> keys() {
        List<K> result = new ArrayList<>(size);
        for (int i = 0; i < keys.length; i++) {
            if (states[i] == OCCUPIED) result.add((K) keys[i]);
        }
        return result;
    }

    /**
     * 키를 지우고 그 값을 반환한다. 없었으면 null.
     *
     * 05번과 같다. 그냥 비우면 그 자리를 지나 뒤쪽에 들어간 키를 못 찾는다.
     * 그래서 tombstone 을 남긴다. size 는 줄지만 used 는 줄지 않는다.
     * 자리는 여전히 탐사를 길게 만들기 때문이다. 로빈후드는 이걸 다르게 푼다.
     */
    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        int i = indexOf(key);
        if (i < 0) {
            return null;
        }
        V old = (V) values[i];
        keys[i] = null;
        values[i] = null;
        states[i] = TOMBSTONE;
        size--;
        return old;
    }

    @Override
    public void clear() {
        Arrays.fill(keys, null);
        Arrays.fill(values, null);
        Arrays.fill(states, EMPTY);
        size = 0;
        used = 0;
        lastProbes = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (K key : keys()) {
            if (!first) sb.append(", ");
            sb.append(key).append('=').append(get(key));
            first = false;
        }
        return sb.append('}').toString();
    }

    // ------------------------------------------------------------------
    // 여기부터가 본체
    // ------------------------------------------------------------------

    /**
     * 키가 있는 칸의 인덱스. 없으면 -1. 들여다본 칸 수를 lastProbes 에 남긴다.
     */
    int indexOf(Object key) {
        lastProbes = 0;
        if (key == null) {
            return -1;
        }
        int hash = Hashing.hash(key);
        int capacity = keys.length;
        for (int i = 0; i < capacity; i++) {
            int slot = probe(hash, i);
            lastProbes++;
            if (states[slot] == EMPTY) {
                return -1;                                   // 여기서 멈춘다
            }
            if (states[slot] == OCCUPIED && keys[slot].equals(key)) {
                return slot;
            }
        }
        return -1;                                           // 한 바퀴가 상한이다
    }

    /**
     * 키에 값을 넣고 이전 값을 반환한다.
     */
    @Override
    @SuppressWarnings("unchecked")
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("null 키는 담을 수 없다");
        }
        if (used + 1 > keys.length * maxLoad) {
            resize();
        }
        lastProbes = 0;
        int hash = Hashing.hash(key);
        int capacity = keys.length;
        int firstTombstone = -1;

        for (int i = 0; i < capacity; i++) {
            int slot = probe(hash, i);
            lastProbes++;
            if (states[slot] == EMPTY) {
                // 여기까지 오는 동안 같은 키는 없었다. 이제 넣어도 안전하다.
                if (firstTombstone >= 0) {
                    insertAt(firstTombstone, key, value);    // 재사용이므로 used 는 그대로
                } else {
                    insertAt(slot, key, value);
                    used++;
                }
                size++;
                return null;
            }
            if (states[slot] == TOMBSTONE) {
                if (firstTombstone < 0) {
                    firstTombstone = slot;                   // 자리는 기억하되 탐사는 계속한다
                }
            } else if (keys[slot].equals(key)) {
                V old = (V) values[slot];
                values[slot] = value;
                return old;
            }
        }

        if (firstTombstone >= 0) {          // 한 바퀴 돌았는데 EMPTY 가 없었던 경우
            insertAt(firstTombstone, key, value);
            size++;
            return null;
        }
        // 선형 탐사와 이중 해싱은 여기 오지 않는다. 수열이 모든 칸을 방문하기 때문이다.
        // 이차 탐사는 온다. 그게 이 박스의 측정 하나다.
        throw new IllegalStateException(
                "탐사 수열이 빈칸을 못 찾았다. capacity=" + capacity + " size=" + size);
    }

    private void insertAt(int index, K key, V value) {
        keys[index] = key;
        values[index] = value;
        states[index] = OCCUPIED;
    }

    /**
     * 다시 배치한다. 05번과 같은 정책이다.
     *
     * 실제 원소가 적으면 늘리지 않고 같은 크기로 다시 배치한다. tombstone 청소만 하는 것이다.
     * 넣고 지우기만 반복할 때 배열이 무한정 커지는 것을 막는다.
     */
    @SuppressWarnings("unchecked")
    void resize() {
        Object[] oldKeys = keys;
        Object[] oldValues = values;
        byte[] oldStates = states;

        int capacity = (size > oldKeys.length * maxLoad / 2) ? oldKeys.length * 2 : oldKeys.length;
        allocate(capacity);

        for (int i = 0; i < oldKeys.length; i++) {
            if (oldStates[i] == OCCUPIED) {
                insertFresh((K) oldKeys[i], (V) oldValues[i]);
            }
        }
    }

    /** 전부 EMPTY 인 새 배열에 넣는다. 중복도 tombstone 도 없으므로 검사가 필요 없다. */
    private void insertFresh(K key, V value) {
        int hash = Hashing.hash(key);
        int capacity = keys.length;
        for (int i = 0; i < capacity; i++) {
            int slot = probe(hash, i);
            if (states[slot] != OCCUPIED) {
                insertAt(slot, key, value);
                size++;
                used++;
                return;
            }
        }
        throw new IllegalStateException("재배치 중 빈칸을 못 찾았다. capacity=" + capacity);
    }
}
