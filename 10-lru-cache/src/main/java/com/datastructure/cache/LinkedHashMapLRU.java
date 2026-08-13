package com.datastructure.cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 표준 라이브러리로 만든 LRU 캐시.
 *
 * `LinkedHashMap` 은 **이미 LRU 캐시다.** 두 가지 스위치만 켜면 된다.
 *
 *   accessOrder = true        get 이 순서를 갱신하게 한다 (기본값은 삽입 순서)
 *   removeEldestEntry(...)    put 뒤에 불려서 "가장 오래된 것을 버릴까"를 묻는다
 *
 * 05번에서 만든 `LinkedHashMap` 이 정확히 이것이다.
 * 거기서 "이 구조 위에 접근 순서를 얹으면 LRU 캐시가 된다"고 했던 지점이다.
 *
 * **그러면 왜 LRUCache 를 직접 만드는가.** 실제로 실무에서는 대개 이쪽을 쓴다.
 * 직접 만드는 이유는 셋 정도다.
 *
 *   1. 통계(hits/misses/evictions)를 원하는 대로 붙일 수 있다 - 여기서는 바깥에서 세야 한다
 *   2. 축출 정책을 바꿀 수 있다 (LFU, TTL, 크기 기반)
 *   3. **안에서 무슨 일이 벌어지는지 알아야 밖에서 고를 수 있다**
 *
 * 3번이 이 문제집의 이유다. LRUCache 를 먼저 채우고 여기로 오라.
 * 40줄이 4줄이 되는 것을 보면 표준 라이브러리가 무엇을 대신해주는지 정확히 보인다.
 */
public class LinkedHashMapLRU<K, V> implements Cache<K, V> {

    private final int capacity;
    private final LinkedHashMap<K, V> map;

    private long hits;
    private long misses;
    private long evictions;

    public LinkedHashMapLRU(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("용량은 1 이상이어야 한다: " + capacity);
        }
        this.capacity = capacity;
        // TODO 1: LinkedHashMap 을 LRU 로 설정해 만든다.
        //
        //   new LinkedHashMap<>(초기용량, 로드팩터, accessOrder) 의 **세 번째 인자**가 열쇠다.
        //   그리고 removeEldestEntry 를 재정의해 언제 버릴지 답한다.
        //
        // removeEldestEntry 는 put 이 끝난 **뒤에** 불린다.
        // 그래서 그 시점의 size() 는 이미 용량을 넘어선 상태다. 비교 부호를 조심하라.
        // (>= 로 쓰면 아직 자리가 있는데도 버린다)
        //
        // 익명 클래스 안에서 바깥 capacity 를 쓰려면 LinkedHashMapLRU.this.capacity 다.
        // 그냥 size() > capacity 라고 쓰면 LinkedHashMap 자신의 필드를 볼 위험이 있다.
        throw new UnsupportedOperationException("TODO 1: LinkedHashMap 설정");
    }

    @Override
    public V get(K key) {
        // TODO 2: map.get 을 부르고 통계를 센다.
        //
        // 순서 갱신은 accessOrder=true 가 알아서 한다. **여기서 할 일은 세는 것뿐이다.**
        // 값이 null 일 수 없다는 계약(put 이 막는다) 덕분에 null 이면 없는 것이다.
        throw new UnsupportedOperationException("TODO 2: get");
    }

    @Override
    public void put(K key, V value) {
        requirePair(key, value);
        // TODO 3: map.put 을 부르고 축출이 일어났는지 센다.
        //
        // 축출은 **새 키를 넣는데 이미 꽉 차 있었을 때**만 일어난다.
        // 기존 키를 갱신하는 것은 크기를 안 바꾸므로 축출이 아니다.
        //
        // map.containsKey 는 순서를 바꾸지 않으니 판단에 써도 안전하다.
        // (map.get 을 쓰면 그 자체로 순서가 바뀌어 결과가 달라진다)
        throw new UnsupportedOperationException("TODO 3: put");
    }

    @Override
    public List<K> keysInOrder() {
        // accessOrder=true 인 LinkedHashMap 은 순회 자체가 "오래된 것부터"다.
        return new ArrayList<>(map.keySet());
    }

    @Override
    public V remove(K key) {
        return map.remove(key);
    }

    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public long hits() {
        return hits;
    }

    @Override
    public long misses() {
        return misses;
    }

    @Override
    public long evictions() {
        return evictions;
    }

    private static void requirePair(Object key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("키는 null 일 수 없다");
        }
        if (value == null) {
            throw new IllegalArgumentException("값은 null 일 수 없다 - get 의 null 이 '없다'를 뜻하기 때문이다");
        }
    }
}
