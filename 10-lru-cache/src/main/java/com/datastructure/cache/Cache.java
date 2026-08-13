package com.datastructure.cache;

import java.util.List;

/**
 * 용량이 정해진 캐시. 꽉 차면 무언가를 버려야 한다.
 *
 * 지금까지 만든 것들은 전부 **넣으면 남아 있었다.** 여기서는 다르다.
 * 용량이 자료구조의 일부이고, **무엇을 버릴지가 곧 이 자료구조의 정책이다.**
 *
 * LRU(Least Recently Used)는 "가장 오래 안 쓴 것을 버린다"를 고른다.
 * 최근에 쓴 것을 또 쓸 가능성이 높다는 가정(시간 지역성) 위에 서 있다.
 * **가정이지 정리가 아니다.** 순차 스캔에서는 이 가정이 정확히 반대로 틀린다.
 *
 * | | get | put | 가장 오래된 것 찾기 |
 * |---|---|---|---|
 * | 해시맵만 | O(1) | O(1) | **O(n)** 전부 봐야 안다 |
 * | 연결 리스트만 | **O(n)** | O(1) | O(1) |
 * | 둘 다 | O(1) | O(1) | O(1) |
 *
 * **그래서 둘을 겹쳐 쓴다.** 05번 해시맵과 02번 이중 연결 리스트가 여기서 만난다.
 *
 * 구현이 셋이다.
 *
 *   LRUCache            해시맵 + 이중 연결 리스트를 직접 엮는다
 *   LinkedHashMapLRU    표준 라이브러리가 이미 해준다
 *   ThreadSafeLRUCache  위의 것을 감싸 잠근다
 *
 * 이 인터페이스에는 TODO 가 없다. 계약은 주어지는 것이다.
 */
public interface Cache<K, V> {

    /**
     * 값을 꺼낸다. 없으면 null.
     *
     * **이 메서드는 읽기가 아니다.** 꺼내는 순간 그 키가 "가장 최근"으로 올라간다.
     * 상태를 바꾸므로 여러 스레드가 동시에 부르면 깨진다. ThreadSafeLRUCache 의 존재 이유다.
     */
    V get(K key);

    /** 넣는다. 이미 있는 키면 값을 갈고 최근으로 올린다. 꽉 찼으면 가장 오래된 것을 버린다. */
    void put(K key, V value);

    /** 있는지만 본다. **순서를 바꾸지 않고 통계에도 안 잡힌다.** get 과 다르다. */
    boolean containsKey(K key);

    /** 지운다. 없었으면 null. 축출이 아니므로 evictions 에 안 잡힌다. */
    V remove(K key);

    int size();

    /** 이 캐시가 담을 수 있는 최대 개수. 생성 후 바뀌지 않는다. */
    int capacity();

    boolean isEmpty();

    void clear();

    /** 오래된 것부터 최근 것 순서로. 이 순서가 곧 버릴 순서다. */
    List<K> keysInOrder();

    /** get 이 값을 찾은 횟수. */
    long hits();

    /** get 이 못 찾은 횟수. */
    long misses();

    /** 용량이 차서 밀려난 횟수. remove 나 clear 는 포함하지 않는다. */
    long evictions();
}
