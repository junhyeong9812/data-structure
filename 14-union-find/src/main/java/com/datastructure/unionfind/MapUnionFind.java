package com.datastructure.unionfind;

import java.util.HashMap;
import java.util.Map;

/**
 * 맵으로 만든 유니온 파인드. **원소 수를 미리 몰라도 된다.**
 *
 * ArrayUnionFind 는 생성할 때 n 을 알아야 한다. 아이디가 0..n-1 로 촘촘해야 한다.
 * 그런데 실제 자료는 그렇지 않은 경우가 많다. 사용자 ID 가 10만~99만 사이에 흩어져 있다면
 * 배열로는 100만 칸을 잡아야 한다.
 *
 * | | ArrayUnionFind | MapUnionFind |
 * |---|---|---|
 * | 원소 수 | **미리 알아야 한다** | 필요 없다 |
 * | 아이디 | 0..n-1 촘촘하게 | 아무 정수나 |
 * | 메모리 | int 배열 두 개 | **맵 오버헤드** |
 * | 속도 | 배열 접근 | 해시 조회 |
 *
 * 05번에서 본 거래가 다시 나온다. **배열은 빠르고 조밀하고, 맵은 유연하고 헐겁다.**
 * 09번 ArrayTrie 대 MapTrie 의 대비와도 같다.
 *
 * 처음 보는 원소는 **자동으로 혼자짜리 묶음**이 된다. 배열판에는 없던 편의다.
 */
public class MapUnionFind implements UnionFind {

    private final Map<Integer, Integer> parent = new HashMap<>();
    private final Map<Integer, Integer> treeSize = new HashMap<>();
    private int components;

    public MapUnionFind() {
    }

    /** 없으면 혼자짜리 묶음으로 만든다. 넣었으면 true. */
    public boolean add(int x) {
        if (parent.containsKey(x)) {
            return false;
        }
        parent.put(x, x);
        treeSize.put(x, 1);
        components++;
        return true;
    }

    public boolean contains(int x) {
        return parent.containsKey(x);
    }

    @Override
    public int find(int x) {
        // TODO 1: ArrayUnionFind.find 와 같은 알고리즘이다. 배열 대신 맵을 쓴다.
        //
        // 처음 보는 원소면 먼저 만들어준다.
        //
        // **주의 하나.** parent.get(root) 는 Integer 다.
        // int 지역 변수에 담아 비교하라(자동 언박싱이 값 비교를 해준다).
        //
        // Integer 끼리 `!=` 로 비교하면 캐시 범위(-128..127) 밖에서 참조 비교가 된다.
        // 다만 **여기서는 우연히 자기 교정된다** - 고정점(parent 가 자기 자신)까지 걸어가는
        // 구조라 한 바퀴 더 돌고 같은 답에 도달한다(변종으로 확인했다. 52개가 다 통과했다).
        // 그래도 int 로 쓰라. 우연에 기대는 코드는 구조를 조금만 바꿔도 물린다.
        // (05번 Math.abs 주석과 같은 이야기다)
        throw new UnsupportedOperationException("TODO 1: find");
    }

    @Override
    public boolean union(int x, int y) {
        // TODO 2: ArrayUnionFind.union 과 같다. 크기로 붙인다.
        throw new UnsupportedOperationException("TODO 2: union");
    }

    @Override
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    @Override
    public int componentCount() {
        return components;
    }

    @Override
    public int size() {
        return parent.size();
    }

    @Override
    public int sizeOf(int x) {
        return treeSize.get(find(x));
    }
}
