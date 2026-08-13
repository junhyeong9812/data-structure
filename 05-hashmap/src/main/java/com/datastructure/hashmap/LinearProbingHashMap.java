package com.datastructure.hashmap;

import java.util.ArrayList;

/**
 * 개방 주소법(선형 탐사) 해시맵.
 *
 * 사슬을 매달지 않는다. 자리가 차 있으면 **옆칸으로 한 칸씩 밀어 가며** 빈자리를 찾는다.
 * 그래서 모든 원소가 배열 안에 산다. 노드 객체가 없어 메모리가 적고 캐시 지역성이 좋다.
 *
 * 그 대신 **지우기가 까다롭다.** 이게 이 구현의 핵심이고, 이 문제의 함정이다.
 *
 *   A 를 넣었더니 3번 칸, B 를 넣었더니 3번이 차 있어 4번 칸에 들어갔다고 하자.
 *   이제 A 를 지우고 3번을 그냥 비우면, B 를 찾을 때 3번이 비었으니 "없다"고 판단하고 멈춘다.
 *   **B 는 4번에 멀쩡히 있는데 못 찾는다.** 탐사 사슬이 끊긴 것이다.
 *
 *   그래서 지운 자리에 "여기 예전에 뭔가 있었다"는 표시를 남긴다. 이걸 tombstone 이라고 한다.
 *   찾을 때는 그 표시를 지나쳐 계속 가고, 넣을 때는 그 자리를 재사용한다.
 *
 * 참고: 필드 이름 keys, values, states 는 테스트가 직접 들여다본다.
 */
public class LinearProbingHashMap<K, V> implements Map<K, V> {

    /** 칸의 상태. tombstone 이 왜 EMPTY 와 달라야 하는지가 이 구현의 전부다. */
    static final byte EMPTY = 0;
    static final byte OCCUPIED = 1;
    static final byte TOMBSTONE = 2;

    static final int DEFAULT_CAPACITY = 8;
    /** 개방 주소법은 꽉 찰수록 급격히 느려지므로 체이닝보다 낮게 잡는다. */
    static final double LOAD_FACTOR = 0.5;

    Object[] keys;
    Object[] values;
    byte[] states;
    int size;          // OCCUPIED 개수
    int used;          // OCCUPIED + TOMBSTONE 개수. 리사이즈 판단은 이걸로 한다

    public LinearProbingHashMap() {
        this.keys = new Object[DEFAULT_CAPACITY];
        this.values = new Object[DEFAULT_CAPACITY];
        this.states = new byte[DEFAULT_CAPACITY];
        this.size = 0;
        this.used = 0;
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

    public int capacity() {
        return keys.length;
    }

    int bucketOf(Object key, int capacity) {
        return (key.hashCode() & 0x7fffffff) % capacity;
    }

    /** 다음 탐사 위치. 끝에 닿으면 처음으로 되감는다. 04번 원형 배열과 같은 계산이다. */
    int nextProbe(int index, int capacity) {
        return (index + 1) % capacity;
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
        java.util.List<K> result = new ArrayList<>(size);
        for (int i = 0; i < keys.length; i++) {
            if (states[i] == OCCUPIED) result.add((K) keys[i]);
        }
        return result;
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
     * 키가 있는 칸의 인덱스. 없으면 -1.
     *
     * 생각할 것
     *   - 어디서 멈춰야 하는가? EMPTY 를 만나면 멈춘다. 그럼 TOMBSTONE 을 만나면?
     *   - 한 바퀴를 다 돌면 어떻게 되는가? 무한 루프에 빠지지 않으려면?
     *
     * TODO(06): 구현하라. key 가 null 이면 -1.
     */
    int indexOf(Object key) {
        throw new UnsupportedOperationException("TODO(06): indexOf");
    }

    /**
     * 키에 값을 넣고 이전 값을 반환한다.
     *
     * 생각할 것
     *   - 이미 있는 키면 그 자리의 값만 바꾼다. 새 자리를 잡으면 같은 키가 두 개가 된다.
     *   - 새 키를 넣을 때 TOMBSTONE 자리를 재사용할 수 있다. 다만 재사용하기 전에
     *     **그 키가 뒤쪽에 이미 있는지** 확인해야 한다. 안 그러면 중복이 생긴다.
     *   - 리사이즈 판단에 size 가 아니라 used 를 쓰는 이유는 무엇인가?
     *     (tombstone 도 탐사를 길게 만든다. 지우기만 반복해도 느려질 수 있다.)
     *
     * TODO(07): 구현하라. key 가 null 이면 IllegalArgumentException.
     */
    @Override
    @SuppressWarnings("unchecked")
    public V put(K key, V value) {
        throw new UnsupportedOperationException("TODO(07): put");
    }

    /**
     * 배열을 두 배로 늘리고 전부 다시 배치한다.
     *
     * 생각할 것
     *   - tombstone 은 옮길 필요가 있는가? 리사이즈가 tombstone 청소를 겸한다.
     *   - 다시 넣을 때 put 을 재사용할 수 있는가? 재귀 리사이즈에 빠지지 않는가?
     *   - **항상 두 배로 늘려야 하는가?** 넣고 지우기만 반복하면 실제 원소는 적은데
     *     tombstone 때문에 리사이즈가 계속 불린다. 그때마다 두 배로 키우면 배열만 커진다.
     *     실제 원소가 적으면 같은 크기로 다시 배치해 tombstone 만 치우는 것으로 충분하다.
     *
     * TODO(08): 구현하라.
     */
    void resize() {
        throw new UnsupportedOperationException("TODO(08): resize");
    }

    /**
     * 키를 지우고 그 값을 반환한다. 없었으면 null.
     *
     * 생각할 것
     *   - **그냥 EMPTY 로 만들면 안 된다.** 그 자리를 지나 뒤쪽에 들어간 키들을 못 찾게 된다.
     *   - size 와 used 중 무엇이 줄어드는가?
     *
     * TODO(09): 구현하라.
     */
    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        throw new UnsupportedOperationException("TODO(09): remove");
    }

    /**
     * 전부 비운다. 배열 길이는 유지한다.
     *
     * TODO(10): 구현하라. 값 참조도 남기지 않는다.
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException("TODO(10): clear");
    }
}
