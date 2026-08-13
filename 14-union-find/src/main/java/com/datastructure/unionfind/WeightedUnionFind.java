package com.datastructure.unionfind;

/**
 * 원소 사이의 **차이**까지 같이 관리하는 유니온 파인드.
 *
 * 보통의 유니온 파인드는 "x 와 y 가 같은 묶음인가"만 답한다.
 * 여기서는 한 걸음 더 간다. **"y 가 x 보다 얼마나 큰가"** 까지 답한다.
 *
 * 쓰이는 곳이 분명하다. 이런 제약이 하나씩 들어올 때다.
 *
 *   "b 는 a 보다 3 크다"     union(a, b, 3)
 *   "c 는 b 보다 5 크다"     union(b, c, 5)
 *   "c 는 a 보다 얼마나 크나" diff(a, c) -> 8      <- 직접 말한 적 없는데 안다
 *   "c 는 a 보다 2 크다"     union(a, c, 2) -> false   <- 모순을 잡아낸다
 *
 * 실무 대응물: 통화 환율의 일관성 검사, 물리 시뮬레이션의 상대 좌표,
 * 분산 시스템의 논리 시계 오프셋, SAT 풀이의 등식 제약.
 *
 * **어떻게 되는가.** weight[x] 를 "x 의 값 - 부모의 값"으로 둔다.
 * 뿌리까지의 weight 를 다 더하면 "x 의 값 - 뿌리의 값"이 된다.
 * 두 원소가 같은 뿌리를 가지면 뿌리 값이 소거되므로
 *
 *   value(y) - value(x) = weight[y] - weight[x]
 *
 * 가 된다. **뿌리의 값이 무엇인지는 끝까지 몰라도 된다.** 차이만 알면 되기 때문이다.
 *
 * 경로 압축을 할 때 weight 도 같이 고쳐야 한다는 것이 이 구조의 어려운 지점이다.
 */
public class WeightedUnionFind {

    private final int[] parent;
    private final int[] treeSize;
    private final long[] weight;
    private int components;

    public WeightedUnionFind(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("원소가 하나 이상 있어야 한다: " + n);
        }
        this.parent = new int[n];
        this.treeSize = new int[n];
        this.weight = new long[n];
        this.components = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            treeSize[i] = 1;
        }
    }

    public int find(int x) {
        requireIndex(x);
        // TODO 1: 뿌리를 찾으면서 **weight 도 뿌리 기준으로 다시 쓴다.**
        //
        // 재귀로 하면 자연스럽다. 부모를 먼저 뿌리에 붙인 **뒤에**
        //   weight[x] += weight[부모]
        // 를 하면, 그 시점의 weight[부모] 는 이미 "부모 - 뿌리"라서
        // 더한 결과가 정확히 "x - 뿌리"가 된다.
        //
        // **순서가 전부다.** 재귀 호출 전에 더하면 부모의 옛 값(= 부모 - 할아버지)을 더하게 되어
        // 조용히 틀린다. 작은 예제에서는 안 걸린다.
        //
        // 부모를 붙잡아두고 시작하라. parent[x] 를 먼저 바꾸면 어느 weight 를 더할지 잃는다.
        throw new UnsupportedOperationException("TODO 1: find");
    }

    /** value(y) - value(x) = w 라고 선언한다. 이미 아는 것과 모순이면 false. */
    public boolean union(int x, int y, long w) {
        // TODO 2: 합치면서 두 나무의 좌표계를 맞춘다.
        //
        //   이미 같은 묶음이면 **모순인지 검사만 한다.** 아는 차이와 w 가 같으면 true.
        //
        //   다른 묶음이면 한쪽 뿌리를 다른 쪽 밑에 붙이는데, 그 뿌리의 weight 를 정해야 한다.
        //   ry 를 rx 밑에 붙인다면 weight[ry] = value(ry) - value(rx) 여야 한다.
        //   find 를 부른 뒤라 weight[x], weight[y] 는 각자 뿌리 기준이므로
        //
        //     w = value(y) - value(x) = (weight[y] + value(ry)) - (weight[x] + value(rx))
        //     => value(ry) - value(rx) = w + weight[x] - weight[y]
        //
        //   반대로 rx 를 ry 밑에 붙이면 **부호가 뒤집힌다.**
        //   크기로 붙이기를 하려면 두 경우를 다 써야 한다. 부호를 빠뜨리는 것이 흔한 실수다.
        throw new UnsupportedOperationException("TODO 2: union");
    }

    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }

    /** value(y) - value(x). 연결돼 있지 않으면 예외. */
    /** value(y) - value(x). 연결돼 있지 않으면 예외. */
    public long diff(int x, int y) {
        // TODO 3: 연결돼 있어야 답할 수 있다. 아니면 IllegalStateException.
        //
        // 연결돼 있다면 **뺄셈 하나**다. 뿌리 값이 소거되기 때문이다.
        // connected 를 먼저 부르면 find 가 일어나 weight 가 뿌리 기준으로 정리된다.
        // **그 순서에 의존한다는 점을 알고 있어야 한다.**
        throw new UnsupportedOperationException("TODO 3: diff");
    }

    public int componentCount() {
        return components;
    }

    public int size() {
        return parent.length;
    }

    long weightOf(int x) {
        return weight[x];
    }

    private void requireIndex(int x) {
        if (x < 0 || x >= parent.length) {
            throw new IndexOutOfBoundsException("원소 " + x + " 가 범위를 벗어났다 (크기 " + parent.length + ")");
        }
    }
}
