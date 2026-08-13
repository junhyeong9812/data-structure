package com.datastructure.unionfind;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 아무 타입이나 묶을 수 있는 유니온 파인드. **번호를 붙여 맵판에 넘긴다.**
 *
 * UnionFind 는 정수만 다룬다. 문자열, 좌표, 객체를 묶고 싶으면
 * "이 원소는 3번"이라고 번호를 매겨 안쪽에 넘기면 된다.
 *
 * 08번에서 정점을 0..V-1 정수로 고정하고 "이름표가 필요하면 바깥에서 매핑하라"고 했던
 * 바로 그 매핑을 여기서 한다. **한 번 정한 단순화가 계속 값을 한다.**
 *
 * 12번 SkipListSet 이 SkipListMap 을 포함으로 재사용한 것과 같은 방식이다.
 */
public class DisjointSet<T> {

    private final Map<T, Integer> ids = new LinkedHashMap<>();
    private final List<T> items = new ArrayList<>();
    private final MapUnionFind uf = new MapUnionFind();

    public boolean add(T item) {
        requireItem(item);
        // TODO 1: 처음 보는 원소면 번호를 하나 새로 붙이고 안쪽 유니온 파인드에도 알린다.
        //
        // 번호는 items.size() 를 쓰면 0 부터 차례로 붙는다.
        // ids 는 원소 -> 번호, items 는 번호 -> 원소다. **양방향이 다 필요하다.**
        // find 가 번호를 돌려주는데 호출자에게는 원소를 줘야 하기 때문이다.
        throw new UnsupportedOperationException("TODO 1: add");
    }

    public boolean contains(T item) {
        return ids.containsKey(item);
    }

    public T find(T item) {
        requireItem(item);
        add(item);
        return items.get(uf.find(ids.get(item)));
    }

    public boolean union(T a, T b) {
        requireItem(a);
        requireItem(b);
        add(a);
        add(b);
        return uf.union(ids.get(a), ids.get(b));
    }

    public boolean connected(T a, T b) {
        requireItem(a);
        requireItem(b);
        if (!ids.containsKey(a) || !ids.containsKey(b)) {
            return false;
        }
        return uf.connected(ids.get(a), ids.get(b));
    }

    public int componentCount() {
        return uf.componentCount();
    }

    public int size() {
        return items.size();
    }

    public int sizeOf(T item) {
        requireItem(item);
        add(item);
        return uf.sizeOf(ids.get(item));
    }

    /** 묶음마다 원소 목록. 대표를 키로 한다. */
    /** 묶음마다 원소 목록. 대표를 키로 한다. */
    public Map<T, List<T>> groups() {
        // TODO 2: 원소마다 대표를 찾아 그 아래에 모은다.
        //
        // 유니온 파인드는 "누가 같은 묶음인가"를 **직접 알려주지 않는다.**
        // 대표만 알려주므로 전부 훑어 모아야 한다. O(n) 이다.
        // 그게 이 자료구조가 포기한 것 중 하나다. 빠른 것은 "둘이 같은가"뿐이다.
        //
        // 순서를 유지하려면 LinkedHashMap 을 쓴다(05번에서 만든 그것이다).
        throw new UnsupportedOperationException("TODO 2: groups");
    }

    private static void requireItem(Object item) {
        if (item == null) {
            throw new IllegalArgumentException("원소는 null 일 수 없다");
        }
    }
}
