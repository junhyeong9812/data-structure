package com.datastructure.skiplist;

import java.util.List;

/**
 * 정렬을 유지하는 집합.
 *
 * OrderedMap 에서 값을 뺀 것이다. 그래서 새로 만들 것이 없다.
 * 값 자리에 아무거나(여기서는 상수 하나) 넣고 맵을 그대로 쓰면 된다.
 *
 * 자바의 HashSet 이 HashMap 을, TreeSet 이 TreeMap 을 그렇게 쓴다.
 * 05번 LinkedHashMap 을 상속으로 얹었던 것과는 다른 방식의 재사용이다.
 * 거기서는 상속으로, 여기서는 포함으로 한다.
 */
public interface OrderedSet<K extends Comparable<K>> {

    /** 넣는다. 새로 들어갔으면 true, 이미 있었으면 false. */
    boolean add(K key);

    boolean contains(K key);

    /** 지웠으면 true. */
    boolean remove(K key);

    int size();

    boolean isEmpty();

    void clear();

    K first();

    K last();

    K floor(K key);

    K ceiling(K key);

    List<K> toList();

    List<K> range(K from, K to);
}
