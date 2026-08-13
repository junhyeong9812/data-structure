package com.datastructure.hashmap;

import java.util.ArrayList;

/**
 * 체이닝 방식 해시맵.
 *
 * 버킷 배열을 두고, 같은 자리에 여러 키가 오면 그 자리에 사슬(연결 리스트)로 매단다.
 * 02번에서 만든 연결 리스트가 여기서 쓰인다.
 *
 * 성질
 *   - 부하율(원소 수 / 버킷 수)이 낮으면 사슬이 짧아 평균 O(1) 이다.
 *   - 부하율이 높아지면 사슬이 길어져 O(n) 에 가까워진다. 그래서 리사이즈가 필요하다.
 *   - 지우기가 단순하다. 사슬에서 노드 하나 빼면 끝이다.
 *     (개방 주소법은 이게 훨씬 까다롭다. LinearProbingHashMap 에서 보게 된다.)
 *
 * 참고: 필드 이름 buckets, size 는 테스트가 직접 들여다본다.
 */
public class ChainingHashMap<K, V> implements Map<K, V> {

    /** 버킷 하나에 매달리는 노드. 같은 자리에 온 키들이 next 로 이어진다. */
    static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;

        Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    static final int DEFAULT_CAPACITY = 8;
    /** 이 비율을 넘으면 버킷을 두 배로 늘린다. 0.75 는 시간과 공간의 흔한 절충점이다. */
    static final double LOAD_FACTOR = 0.75;

    Node<K, V>[] buckets;
    int size;

    @SuppressWarnings("unchecked")
    public ChainingHashMap() {
        this.buckets = new Node[DEFAULT_CAPACITY];
        this.size = 0;
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
        return buckets.length;
    }

    @Override
    public boolean containsKey(Object key) {
        return findNode(key) != null;
    }

    @Override
    public V get(Object key) {
        Node<K, V> node = findNode(key);
        return node == null ? null : node.value;
    }

    /**
     * 키가 들어갈 버킷 번호.
     *
     * hashCode() 는 음수일 수 있다. 그대로 % 하면 인덱스가 음수가 되어 터진다.
     *
     * `Math.abs(h) % n` 도 완전하지 않다. `Math.abs(Integer.MIN_VALUE)` 는 오버플로해서
     * 여전히 Integer.MIN_VALUE 다. 다만 이 구현은 용량이 늘 2의 거듭제곱이라
     * 그 값의 나머지가 0 이 되어 우연히 사고가 안 난다. **우연에 기대는 코드다.**
     *
     * 최상위 비트를 지우는 방식은 용량이 무엇이든 항상 0 이상을 준다.
     */
    int bucketOf(Object key, int capacity) {
        return (key.hashCode() & 0x7fffffff) % capacity;
    }

    /** 삽입/삭제 훅. LinkedHashMap 이 삽입 순서를 관리하려고 재정의한다. */
    protected void afterPut(K key, boolean isNewKey) {
    }

    protected void afterRemove(Object key) {
    }

    protected void afterClear() {
    }

    @Override
    public Iterable<K> keys() {
        java.util.List<K> result = new ArrayList<>(size);
        for (Node<K, V> bucket : buckets) {
            for (Node<K, V> n = bucket; n != null; n = n.next) {
                result.add(n.key);
            }
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
     * 키에 해당하는 노드를 찾는다. 없으면 null.
     *
     * 생각할 것
     *   - 버킷을 찾은 다음에는 사슬을 훑어야 한다. 왜 버킷만으로는 부족한가?
     *   - 같은 버킷에 있다고 같은 키가 아니다. 무엇으로 비교해야 하는가?
     *
     * TODO(01): 구현하라. key 가 null 이면 null 을 반환한다(예외 아님).
     */
    Node<K, V> findNode(Object key) {
        throw new UnsupportedOperationException("TODO(01): findNode");
    }

    /**
     * 키에 값을 넣고 이전 값을 반환한다.
     *
     * 생각할 것
     *   - 이미 있는 키면 사슬에 새로 매달면 안 된다. 같은 키가 두 개가 된다.
     *   - 새 키를 매달 때 사슬의 앞과 뒤 중 어디가 싼가?
     *   - 새 키를 넣은 뒤 부하율이 넘으면 리사이즈한다. afterPut 훅도 잊지 마라.
     *
     * TODO(02): 구현하라. key 가 null 이면 IllegalArgumentException.
     */
    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException("TODO(02): put");
    }

    /**
     * 버킷을 두 배로 늘리고 전부 다시 배치한다.
     *
     * 생각할 것
     *   - 버킷 수가 바뀌면 같은 키의 버킷 번호도 바뀐다. 그래서 옮기는 게 아니라 **다시 계산**해야 한다.
     *   - 노드를 그대로 재사용할 수 있는가, 새로 만들어야 하는가?
     *
     * TODO(03): 구현하라.
     */
    @SuppressWarnings("unchecked")
    void resize() {
        throw new UnsupportedOperationException("TODO(03): resize");
    }

    /**
     * 키를 지우고 그 값을 반환한다. 없었으면 null.
     *
     * 생각할 것
     *   - 사슬에서 노드를 빼려면 그 **앞** 노드를 알아야 한다. 첫 노드일 때는?
     *   - afterRemove 훅을 잊지 마라.
     *
     * TODO(04): 구현하라.
     */
    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException("TODO(04): remove");
    }

    /**
     * 전부 비운다. 버킷 배열 길이는 유지한다.
     *
     * TODO(05): 구현하라. afterClear 훅도 부른다.
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException("TODO(05): clear");
    }
}
