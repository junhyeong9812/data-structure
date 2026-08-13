package com.datastructure.hashmap;

import java.util.ArrayList;

/**
 * [구현] 삽입 순서를 지키는 해시맵.
 *
 * 해시, 충돌, 리사이즈는 부모가 다 한다. 여기서는 순서 사슬만 관리한다.
 * 부모가 열어둔 훅 세 개를 재정의하는 것이 전부다(template method).
 */
public class LinkedHashMap<K, V> extends ChainingHashMap<K, V> {

    static class Entry<K> {
        final K key;
        Entry<K> prev;
        Entry<K> next;

        Entry(K key) {
            this.key = key;
        }
    }

    Entry<K> first;
    Entry<K> last;

    /**
     * 키에서 순서 노드로 바로 가는 색인.
     *
     * 이게 없으면 지울 때 사슬을 앞에서부터 훑어야 해서 remove 가 O(n) 이 된다.
     * 해시맵 위에 해시맵을 얹는 셈인데, 이 order 는 평범한 ChainingHashMap 이라
     * afterPut 훅이 비어 있어 무한 재귀가 생기지 않는다.
     */
    private final ChainingHashMap<K, Entry<K>> order = new ChainingHashMap<>();

    @Override
    protected void afterPut(K key, boolean isNewKey) {
        if (!isNewKey) {
            return;                    // 값만 바뀐 것은 순서를 건드리지 않는다
        }
        Entry<K> entry = new Entry<>(key);
        if (last == null) {
            first = entry;
        } else {
            entry.prev = last;
            last.next = entry;
        }
        last = entry;
        order.put(key, entry);
    }

    @Override
    protected void afterRemove(Object key) {
        Entry<K> entry = order.remove(key);
        if (entry == null) {
            return;
        }
        if (entry.prev == null) {
            first = entry.next;
        } else {
            entry.prev.next = entry.next;
        }
        if (entry.next == null) {
            last = entry.prev;
        } else {
            entry.next.prev = entry.prev;
        }
        entry.prev = null;
        entry.next = null;
    }

    @Override
    protected void afterClear() {
        first = null;
        last = null;
        order.clear();
    }

    /** 부모는 버킷 순서로 준다. 여기서는 넣은 순서로 준다. */
    @Override
    public Iterable<K> keys() {
        java.util.List<K> result = new ArrayList<>(size());
        for (Entry<K> e = first; e != null; e = e.next) {
            result.add(e.key);
        }
        return result;
    }
}
