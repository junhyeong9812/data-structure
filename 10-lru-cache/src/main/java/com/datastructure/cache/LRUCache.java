package com.datastructure.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 해시맵과 이중 연결 리스트를 겹쳐 만든 LRU 캐시.
 *
 * 05번과 02번이 여기서 만난다.
 *
 *   해시맵    키 -> 노드. "이 키가 어디 있나"를 O(1) 로 답한다
 *   연결 리스트  오래된 것부터 최근 것까지의 줄. "무엇을 버릴까"를 O(1) 로 답한다
 *
 * 둘 중 하나만으로는 안 된다. 맵만 쓰면 가장 오래된 것을 찾느라 전부 봐야 하고,
 * 리스트만 쓰면 키를 찾느라 전부 봐야 한다. 서로의 약점을 정확히 메운다.
 *
 * 맵의 값이 값(V)이 아니라 노드라는 점이 핵심이다.
 * 노드를 손에 쥐고 있어야 리스트에서 그 자리를 O(1) 로 뗄 수 있다.
 * 02번에서 "노드를 알면 O(1), 인덱스로 찾으면 O(n)"이라고 했던 그 지점이다.
 *
 * 줄의 방향을 이렇게 정한다.
 *
 *   head <-> (가장 오래된 것) <-> ... <-> (가장 최근 것) <-> tail
 *
 * head 와 tail 은 센티넬이다. 값을 담지 않고 자리만 지킨다.
 * 그 덕분에 "리스트가 비었나", "이게 첫 노드인가"를 따로 검사할 필요가 없다.
 * 모든 노드가 앞뒤 이웃을 반드시 갖는다.
 */
public class LRUCache<K, V> implements Cache<K, V> {

    static final class Node<K, V> {
        K key;
        V value;
        Node<K, V> prev;
        Node<K, V> next;
    }

    private final int capacity;

    /** 키 -> 노드. 값이 아니라 노드를 담는 이유를 위 설명에서 확인하라. */
    private final Map<K, Node<K, V>> index = new HashMap<>();

    final Node<K, V> head = new Node<>();
    final Node<K, V> tail = new Node<>();

    private long hits;
    private long misses;
    private long evictions;

    public LRUCache(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("용량은 1 이상이어야 한다: " + capacity);
        }
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    /** 노드를 줄에서 뗀다. 이웃끼리 이어주면 된다. */
    private void unlink(Node<K, V> node) {
        // TODO 1: 앞뒤 이웃을 서로 잇는다.
        //
        // 센티넬 덕분에 node.prev 와 node.next 는 **절대 null 이 아니다.**
        // null 검사가 필요 없다는 것이 센티넬을 두는 이유다.
        //
        // 뗀 노드의 prev/next 도 끊어두라. 02번에서 본 것과 같은 이유다.
        // 그 노드 하나가 남아 있으면 줄 전체가 GC 되지 않는다.
        throw new UnsupportedOperationException("TODO 1: unlink");
    }

    /** 노드를 줄의 맨 뒤(= 가장 최근)에 붙인다. */
    private void linkLast(Node<K, V> node) {
        // TODO 2: tail 바로 앞에 끼워 넣는다.
        //
        // 링크를 고치는 순서가 있다. 바꾸기 전에 필요한 참조를 먼저 붙잡아야 한다.
        // tail.prev 를 먼저 갈아치우면 원래 마지막 노드를 잃는다.
        throw new UnsupportedOperationException("TODO 2: linkLast");
    }

    @Override
    public V get(K key) {
        // TODO 3: 꺼내면서 **순서를 갱신한다.**
        //
        //   없으면 misses 를 올리고 null.
        //   있으면 hits 를 올리고, 그 노드를 줄 맨 뒤로 옮긴 뒤 값을 준다.
        //
        // **이 메서드가 이 자료구조에서 제일 중요한 지점이다.**
        // 이름은 get 인데 상태를 바꾼다. 조회가 쓰기다.
        // ThreadSafeLRUCache 가 읽기 잠금이 아니라 배타 잠금을 쓰는 이유가 바로 이것이다.
        throw new UnsupportedOperationException("TODO 3: get");
    }

    @Override
    public void put(K key, V value) {
        requirePair(key, value);
        // TODO 4: 넣는다. 경우가 셋이다.
        //
        //   1. 이미 있는 키   -> 값을 갈고 맨 뒤로 옮긴다. **크기는 안 변하고 축출도 없다.**
        //   2. 새 키인데 자리가 있다 -> 노드를 만들어 맨 뒤에 붙이고 맵에 넣는다.
        //   3. 새 키인데 꽉 찼다 -> **먼저 버리고 넣는다.** head 바로 뒤가 가장 오래된 것이다.
        //
        // 3번에서 맵과 리스트 **양쪽에서** 지워야 한다. 한쪽만 지우면 조용히 어긋난다.
        // 그리고 버릴 때 evictions 를 올린다(remove 나 clear 는 축출이 아니다).
        throw new UnsupportedOperationException("TODO 4: put");
    }

    @Override
    public V remove(K key) {
        // TODO 5: 맵에서 빼고 줄에서도 뗀다. 없었으면 null.
        //
        // 축출이 아니라 명시적 삭제다. evictions 를 올리면 안 된다.
        throw new UnsupportedOperationException("TODO 5: remove");
    }

    @Override
    public List<K> keysInOrder() {
        // TODO 6: head 다음부터 tail 전까지 훑으며 키를 모은다.
        //
        // 센티넬은 값이 없으니 결과에 넣으면 안 된다.
        // 이 순서가 곧 버릴 순서다. 맨 앞이 다음 희생자다.
        throw new UnsupportedOperationException("TODO 6: keysInOrder");
    }

    @Override
    public boolean containsKey(K key) {
        // get 과 달리 순서를 바꾸지 않는다. 통계에도 안 잡힌다.
        return index.containsKey(key);
    }

    @Override
    public int size() {
        return index.size();
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public boolean isEmpty() {
        return index.isEmpty();
    }

    @Override
    public void clear() {
        index.clear();
        head.next = tail;
        tail.prev = head;
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
