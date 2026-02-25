package com.datastructure.practice0222.map;

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
}
