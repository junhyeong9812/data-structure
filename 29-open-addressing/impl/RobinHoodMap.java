package com.datastructure.openaddr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * [구현] 로빈후드 해싱. 부자에게서 뺏어 가난한 자에게 준다.
 *
 * 수열은 선형 탐사와 똑같다. 옆칸으로 한 칸씩 간다. 다른 것은 자리 다툼의 규칙뿐이다.
 *
 *   넣으려는 키가 걸어온 거리 > 지금 그 칸에 있는 키가 걸어온 거리 이면 자리를 뺏는다.
 *   뺏긴 키를 들고 계속 걷는다.
 *
 * 그래서 오래 걸은 놈이 앞자리를 갖는다. 평균 탐사 거리는 선형 탐사와 같다.
 * 자리 배치가 바뀔 뿐 총합은 보존되기 때문이다. 줄어드는 것은 분산, 즉 최댓값이다.
 * 이 구별이 이 구현의 전부다. 평균이 아니라 꼬리를 자른다.
 *
 * 조회에서 이득이 하나 더 나온다. 걸어온 거리가 지금 칸의 거리보다 커지는 순간
 * 그 뒤에는 이 키가 있을 수 없다. 뺏겼어야 하기 때문이다. 그래서 빈칸까지 안 가고 멈춘다.
 * 군집 한가운데로 떨어진 없는 키 조회가 선형 탐사에서는 덩어리 끝까지 갔는데 여기서는 몇 칸이면 끝난다.
 *
 * 삭제도 다르다. tombstone 이 필요 없다. 뒤에서 당겨오면(backward shift) 되기 때문이다.
 * 자기 홈에 있는 키(거리 0)를 만나면 멈춘다. 그 앞의 키들은 전부 홈이 뒤에 있어서 당겨도 된다.
 *
 * 필드 이름 keys, values, hashes 는 테스트가 직접 들여다본다. 빈칸은 keys[i] == null 이다.
 */
public class RobinHoodMap<K, V> implements ProbeMap<K, V> {

    static final int DEFAULT_CAPACITY = 8;
    static final double DEFAULT_MAX_LOAD = 0.5;

    Object[] keys;
    Object[] values;
    int[] hashes;      // 홈 버킷을 다시 계산하지 않으려고 들고 있는다
    int size;
    int mask;
    final double maxLoad;
    int lastProbes;

    public RobinHoodMap() {
        this(DEFAULT_CAPACITY, DEFAULT_MAX_LOAD);
    }

    public RobinHoodMap(int capacity, double maxLoad) {
        if (maxLoad <= 0 || maxLoad > 1) {
            throw new IllegalArgumentException("부하율은 0 초과 1 이하여야 한다: " + maxLoad);
        }
        this.maxLoad = maxLoad;
        allocate(Hashing.tableSizeFor(capacity));
    }

    private void allocate(int capacity) {
        this.keys = new Object[capacity];
        this.values = new Object[capacity];
        this.hashes = new int[capacity];
        this.mask = capacity - 1;
        this.size = 0;
    }

    /** 그 칸에 있는 키가 홈에서 몇 칸 밀려났나. 되감김을 고려해 마스크로 잰다. */
    int distanceOf(int slot) {
        return (slot - (hashes[slot] & mask)) & mask;
    }

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
            if (keys[i] != null) result.add((K) keys[i]);
        }
        return result;
    }

    @Override
    public void clear() {
        Arrays.fill(keys, null);
        Arrays.fill(values, null);
        Arrays.fill(hashes, 0);
        size = 0;
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

    private void place(int slot, Object key, Object value, int hash) {
        keys[slot] = key;
        values[slot] = value;
        hashes[slot] = hash;
    }

    @SuppressWarnings("unchecked")
    void resize() {
        Object[] oldKeys = keys;
        Object[] oldValues = values;
        allocate(oldKeys.length * 2);
        for (int i = 0; i < oldKeys.length; i++) {
            if (oldKeys[i] != null) {
                put((K) oldKeys[i], (V) oldValues[i]);   // 크기가 두 배라 여기서 또 리사이즈되지 않는다
            }
        }
    }

    // ------------------------------------------------------------------
    // 여기부터가 본체
    // ------------------------------------------------------------------

    /**
     * 키가 있는 칸. 없으면 -1.
     */
    int indexOf(Object key) {
        lastProbes = 0;
        if (key == null) {
            return -1;
        }
        int hash = Hashing.hash(key);
        int slot = hash & mask;
        for (int dist = 0; dist < keys.length; dist++) {
            lastProbes++;
            if (keys[slot] == null) {
                return -1;
            }
            if (distanceOf(slot) < dist) {
                return -1;      // 여기 있었다면 이 칸을 뺏었어야 한다. 더 볼 필요가 없다
            }
            if (hashes[slot] == hash && keys[slot].equals(key)) {
                return slot;
            }
            slot = (slot + 1) & mask;
        }
        return -1;
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
        if (size + 1 > keys.length * maxLoad) {
            resize();
        }
        lastProbes = 0;
        int hash = Hashing.hash(key);
        Object carriedKey = key;
        Object carriedValue = value;
        int carriedHash = hash;
        boolean original = true;    // 아직 원래 키를 들고 있나
        int slot = hash & mask;

        for (int dist = 0; dist < keys.length; dist++) {
            lastProbes++;
            if (keys[slot] == null) {
                place(slot, carriedKey, carriedValue, carriedHash);
                size++;
                return null;
            }
            if (original && hashes[slot] == hash && keys[slot].equals(key)) {
                V old = (V) values[slot];
                values[slot] = value;
                return old;                     // 자리는 그대로 두고 값만 바꾼다
            }
            int residentDist = distanceOf(slot);
            if (residentDist < dist) {
                // 이 칸의 주인이 나보다 덜 걸었다. 자리를 뺏고 그를 들고 간다.
                Object tmpKey = keys[slot];
                Object tmpValue = values[slot];
                int tmpHash = hashes[slot];
                place(slot, carriedKey, carriedValue, carriedHash);
                carriedKey = tmpKey;
                carriedValue = tmpValue;
                carriedHash = tmpHash;
                dist = residentDist;            // 이제 뺏긴 놈의 거리로 계속 걷는다
                original = false;               // 뺏은 시점에서 원래 키는 없던 키로 확정됐다
            }
            slot = (slot + 1) & mask;
        }
        throw new IllegalStateException("빈칸이 없다. capacity=" + keys.length);
    }

    /**
     * 키를 지우고 그 값을 반환한다. tombstone 없이 뒤에서 당겨온다.
     */
    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        int hole = indexOf(key);
        if (hole < 0) {
            return null;
        }
        V old = (V) values[hole];
        int next = (hole + 1) & mask;
        // 자기 홈에 앉아 있는 키(거리 0)나 빈칸을 만나면 멈춘다.
        // 거리가 0 인 키를 당기면 그 키가 자기 홈보다 앞으로 가버려서 영영 못 찾는다.
        while (keys[next] != null && distanceOf(next) != 0) {
            place(hole, keys[next], values[next], hashes[next]);
            hole = next;
            next = (next + 1) & mask;
        }
        keys[hole] = null;
        values[hole] = null;
        hashes[hole] = 0;
        size--;
        return old;
    }
}
