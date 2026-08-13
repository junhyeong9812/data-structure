package com.datastructure.hashmap;

/**
 * 삽입 순서를 지키는 해시맵.
 *
 * ChainingHashMap 을 상속하고, 키가 들어온 순서를 따로 기록한다.
 * 해시맵이 포기했던 "순서"를 되사오는 것이고, 그 대가로 링크 관리 비용이 붙는다.
 *
 * 왜 상속인가
 *   해시 계산, 충돌 처리, 리사이즈는 부모와 완전히 같다. 다시 쓸 이유가 없다.
 *   달라지는 것은 "넣고 지울 때 순서 기록을 갱신한다"뿐이라, 부모가 그 지점에 훅을 열어뒀다.
 *   이런 걸 template method 패턴이라고 부른다.
 *
 * 실무에서는 이 구조 위에 접근 순서(access order)를 얹어 LRU 캐시를 만든다. 10번에서 다시 만난다.
 */
public class LinkedHashMap<K, V> extends ChainingHashMap<K, V> {

    /** 삽입 순서대로 이어지는 사슬. 02번에서 만든 연결 리스트와 같은 모양이다. */
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
    /** 키에서 순서 노드로 바로 가기 위한 색인. 없으면 지울 때 사슬을 훑어야 해서 O(n) 이 된다. */
    private final ChainingHashMap<K, Entry<K>> order = new ChainingHashMap<>();

    // ------------------------------------------------------------------

    /**
     * 새 키가 들어왔을 때만 순서 사슬 맨 뒤에 붙인다.
     *
     * 생각할 것
     *   - 이미 있던 키의 값만 바꾼 경우에는 순서를 건드리면 안 된다. 그래서 isNewKey 를 받는다.
     *   - 붙일 때 order 색인에도 넣어야 나중에 O(1) 로 지울 수 있다.
     *
     * TODO(11): 구현하라.
     */
    @Override
    protected void afterPut(K key, boolean isNewKey) {
        throw new UnsupportedOperationException("TODO(11): afterPut");
    }

    /**
     * 지워진 키를 순서 사슬에서도 뺀다.
     *
     * 생각할 것
     *   - 색인에서 노드를 바로 찾을 수 있다. 그 노드를 사슬에서 떼는 것은 02번과 같다.
     *   - 양 끝이었다면 first/last 도 바뀐다.
     *
     * TODO(12): 구현하라.
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void afterRemove(Object key) {
        throw new UnsupportedOperationException("TODO(12): afterRemove");
    }

    /**
     * 순서 사슬도 비운다.
     *
     * TODO(13): 구현하라.
     */
    @Override
    protected void afterClear() {
        throw new UnsupportedOperationException("TODO(13): afterClear");
    }

    /**
     * 넣은 순서대로 키를 준다. 부모는 버킷 순서로 주므로 재정의해야 한다.
     *
     * TODO(14): 구현하라.
     */
    @Override
    public Iterable<K> keys() {
        throw new UnsupportedOperationException("TODO(14): keys");
    }
}
