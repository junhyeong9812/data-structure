package com.datastructure.unionfind;

/**
 * 배열 두 개로 만든 유니온 파인드. 원소 수를 미리 안다면 이게 가장 빠르다.
 *
 * parent[i] 는 i 의 부모다. 자기 자신을 가리키면 그게 뿌리(= 묶음의 대표)다.
 * 노드 객체도 링크도 없다. 트리를 배열 하나로 표현한다.
 * 07번 힙에서 배열로 트리를 표현했던 것과 같은 발상인데, 여기서는 부모를 직접 적는다.
 *
 * 최적화 둘을 켜고 끌 수 있게 해뒀다. 끄고 돌려보면 왜 필요한지 숫자로 보인다.
 * (테스트 전용이다. 실제로 쓸 때는 둘 다 켠 생성자를 쓴다)
 */
public class ArrayUnionFind implements UnionFind {

    private final int[] parent;
    private final int[] treeSize;
    private final boolean unionBySize;
    private final boolean pathCompression;
    private int components;

    public ArrayUnionFind(int n) {
        this(n, true, true);
    }

    ArrayUnionFind(int n, boolean unionBySize, boolean pathCompression) {
        if (n < 1) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다: " + n);
        }
        this.parent = new int[n];
        this.treeSize = new int[n];
        this.unionBySize = unionBySize;
        this.pathCompression = pathCompression;
        this.components = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            treeSize[i] = 1;
        }
    }

    @Override
    public int find(int x) {
        requireIndex(x);
        // TODO 1: 뿌리를 찾고, **지나온 노드를 전부 뿌리에 직접 붙인다.**
        //
        //   1. parent[root] == root 가 될 때까지 올라간다
        //   2. pathCompression 이면 x 부터 다시 올라가며 parent 를 root 로 바꾼다
        //
        // 2번이 경로 압축이다. 다음 번 find 는 한 걸음이면 끝난다.
        // **조회가 자료구조를 바꾼다.** 10번 LRU 의 get 과 같은 성질이고,
        // 바로 그 때문에 한 번 합친 것을 쪼갤 수 없다. 원래 부모가 지워지기 때문이다.
        //
        // 재귀로 짜도 되지만 여기서는 반복으로 한다.
        // 압축 없이 10만 개를 한 줄로 이으면 재귀는 스택이 넘친다(08번 DFS 와 같은 이유).
        //
        // 두 번째 루프에서 **다음 부모를 먼저 붙잡아야 한다.** parent[cur] 를 먼저 바꾸면
        // 어디로 갈지 잃는다. 02번 reverse 와 같은 함정이다.
        throw new UnsupportedOperationException("TODO 1: find");
    }

    @Override
    public boolean union(int x, int y) {
        // TODO 2: 두 뿌리를 찾아 **작은 나무를 큰 나무 밑에** 붙인다.
        //
        //   같은 뿌리면 이미 같은 묶음이다. false.
        //   아니면 붙이고, 크기를 합치고, 묶음 수를 하나 줄인다.
        //
        // 크기를 안 보고 늘 같은 쪽에 붙이면 어떻게 되는가.
        // 0-1, 0-2, 0-3 ... 순서로 합치면 한 줄짜리 나무가 된다. find 가 O(n) 이다.
        // **작은 쪽을 밑에 넣으면 깊이가 log n 을 절대 안 넘는다.**
        // (깊이가 1 늘어나려면 나무 크기가 두 배가 돼야 하기 때문이다)
        //
        // unionBySize 가 false 면 늘 ry 를 rx 밑에 붙인다. 테스트가 그 차이를 잰다.
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
        return parent.length;
    }

    @Override
    public int sizeOf(int x) {
        // TODO 3: 이 묶음의 크기.
        //
        // treeSize[x] 를 그대로 주면 안 된다. **뿌리의 것만 정확하다.**
        // 자식들의 treeSize 는 합쳐지기 전의 옛 값이라 갱신하지 않는다.
        // (전부 갱신하려면 O(n) 이니 안 한다. 대신 뿌리에서만 읽는다)
        throw new UnsupportedOperationException("TODO 3: sizeOf");
    }

    int parentOf(int x) {
        requireIndex(x);
        return parent[x];
    }

    /** 뿌리까지 몇 걸음인가. 최적화의 효과를 재려고 둔 것이다. */
    int depthOf(int x) {
        requireIndex(x);
        int d = 0;
        int cur = x;
        while (parent[cur] != cur) {
            cur = parent[cur];
            d++;
        }
        return d;
    }

    private void requireIndex(int x) {
        if (x < 0 || x >= parent.length) {
            throw new IndexOutOfBoundsException("원소 " + x + " 가 범위를 벗어났다 (크기 " + parent.length + ")");
        }
    }
}
