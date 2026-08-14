package com.datastructure.spatial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 쿼드트리. 공간을 네 칸으로 재귀 분할한다.
 *
 * <h2>KD-트리와 무엇이 다른가</h2>
 *
 * KD-트리는 데이터를 보고 가른다. 노드가 곧 점이고, 그 점의 좌표가 분할선이다.
 * 쿼드트리는 데이터를 안 보고 가른다. 칸을 그냥 절반씩 네 조각으로 나눈다.
 *
 * 그래서 성질이 반대다.
 *   - 삽입이 단순하다. 어느 칸에 들어갈지가 좌표만으로 정해진다. 균형을 걱정할 일이 없다.
 *   - 대신 깊이를 데이터가 아니라 좌표 범위가 정한다. 점 천 개가 구석에 몰려 있으면
 *     그 구석까지 내려가는 데만 열 층을 쓴다. 그 열 층에는 자식이 하나뿐이다.
 *   - 지도 타일과 같은 모양이다. 실무의 지도 서비스가 이 구조를 쓰는 이유이기도 하다.
 *
 * <h2>규칙</h2>
 *
 * 점은 잎에만 있다. 잎에 점이 capacity 를 넘으면 네 칸으로 쪼개고 아래로 내려보낸다.
 * 칸이 1x1 이 되면 더는 못 쪼갠다. 그래서 정수 좌표에서는 깊이에 상한이 있다.
 * 경계 밖의 점은 insert 가 false 를 반환한다. 같은 점을 또 넣어도 false 다.
 *
 * 경계를 2의 거듭제곱 크기 정사각형으로 잡기를 권한다. 그러면 칸이 계속 정사각형으로 쪼개진다.
 * 폭이 1 인 칸은 네 조각으로 못 나뉘어서 그 잎에 점이 용량보다 많이 쌓인다.
 *
 * 테스트가 root 와 Node 의 bounds, points, children 을 직접 읽는다. 이름이 계약이다.
 */
public class QuadTree implements SpatialIndex, VisitCounting {

    static final class Node {
        final Rectangle bounds;
        final List<Point2D> points = new ArrayList<>();
        Node[] children;        // null 이면 잎이다

        Node(Rectangle bounds) {
            this.bounds = bounds;
        }

        boolean isLeaf() {
            return children == null;
        }
    }

    private final Rectangle bounds;
    private final int capacity;
    Node root;
    private int size;
    private long visits;

    public QuadTree(Rectangle bounds, int capacity) {
        if (bounds == null) throw new IllegalArgumentException("경계가 null 이다");
        if (capacity < 1) throw new IllegalArgumentException("capacity 는 1 이상이어야 한다: " + capacity);
        this.bounds = bounds;
        this.capacity = capacity;
        this.root = new Node(bounds);
    }

    public Rectangle bounds() {
        return bounds;
    }

    public int capacity() {
        return capacity;
    }

    @Override
    public boolean insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("점이 null 이다");
        if (!bounds.contains(p)) return false;      // 경계 밖은 담을 칸이 없다
        if (!insertInto(root, p)) return false;
        size++;
        return true;
    }

    private boolean insertInto(Node node, Point2D p) {
        visits++;
        // TODO 14: 잎이면 담고, 아니면 해당 자식으로 내려가라.
        //
        //   잎일 때
        //     1. 이미 그 점이 있으면 false. **중복은 안 받는다**(이 문제집의 계약이다).
        //     2. 담는다.
        //     3. 담고 나서 점 수가 capacity 를 **넘으면** 쪼갠다.
        //        단 node.bounds.canSubdivide() 일 때만. 1x1 칸은 더 못 쪼갠다.
        //        이 조건을 빼면 못 쪼개는 칸에서 subdivide 가 IllegalStateException 을 던진다.
        //        경계가 정사각형이면 그런 칸에 점이 둘 이상 들어갈 일이 없어서 잘 안 드러난다.
        //        폭이 1 인 칸(정사각형이 아닌 경계)에서 드러난다.
        //     4. true 를 반환한다.
        //
        //   잎이 아닐 때
        //     childIndex(node, p) 가 골라주는 자식으로 그대로 내려간다.
        //
        // "넘으면"에 주의하라. capacity 가 4 인데 4 개째에서 쪼개면 용량의 뜻이 달라진다.
        // 5 개째에 쪼개는 것이 계약이다(QuadTreeStructureTest 의 splitsOnlyWhenFull).
        throw new UnsupportedOperationException("TODO 14: insertInto");
    }

    private void subdivide(Node node) {
        // TODO 15: 네 칸으로 쪼개고 가지고 있던 점을 전부 아래로 내려보내라.
        //
        //   1. node.bounds.subdivide() 가 네 칸을 준다(SW, SE, NW, NE 순).
        //   2. 그 넷으로 node.children 을 채운다.
        //   3. **가지고 있던 점을 전부 자식으로 옮기고 node.points 를 비운다.**
        //      옮기는 것은 insertInto(자식, 점) 을 부르면 된다.
        //      한 자식에 또 용량이 넘치면 거기서 또 쪼개진다. 재귀가 알아서 한다.
        //
        // 3번에서 두 가지를 조심하라.
        //   - 비우기 전에 목록을 복사해두지 않으면 순회 중에 원본이 바뀐다.
        //     insertInto 가 같은 리스트를 건드리므로 ConcurrentModificationException 이 난다.
        //   - **비우는 것을 잊으면 같은 점이 부모와 자식에 동시에 있게 된다.**
        //     조회는 잎만 읽으므로 답은 안 틀린다. 대신 지운 적도 없는 점이 계속 살아 있고
        //     "점은 잎에만 있다"는 불변식이 깨진다. 그건 계약 테스트로는 안 잡히고
        //     QuadTreeStructureTest 가 노드를 직접 들여다봐야 잡힌다.
        throw new UnsupportedOperationException("TODO 15: subdivide");
    }

    /** p 가 들어갈 자식의 번호. 네 칸이 부모를 빈틈없이 덮으므로 반드시 하나 나온다. */
    private static int childIndex(Node node, Point2D p) {
        for (int i = 0; i < node.children.length; i++) {
            if (node.children[i].bounds.contains(p)) return i;
        }
        throw new IllegalStateException("네 칸이 부모를 덮지 못한다: " + p + " in " + node.bounds);
    }

    @Override
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("점이 null 이다");
        if (!bounds.contains(p)) return false;
        Node node = root;
        while (true) {
            visits++;
            if (node.isLeaf()) return node.points.contains(p);
            node = node.children[childIndex(node, p)];
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = new Node(bounds);        // 쪼개둔 칸까지 통째로 버린다
        size = 0;
    }

    @Override
    public List<Point2D> rangeSearch(Rectangle area) {
        if (area == null) throw new IllegalArgumentException("사각형이 null 이다");
        List<Point2D> out = new ArrayList<>();
        rangeFrom(root, area, out);
        return out;
    }

    private void rangeFrom(Node node, Rectangle area, List<Point2D> out) {
        visits++;
        // TODO 16: 안 겹치는 칸은 통째로 버리고, 잎이면 걸러 담고, 아니면 네 자식에 다 내려가라.
        //
        //   1. node.bounds 가 area 와 안 겹치면 그냥 돌아온다. **여기가 가지치기다.**
        //      이 한 줄이 없으면 잎마다 점을 전부 훑는다. 즉 전수 조사와 같아진다.
        //   2. 잎이면 점을 하나씩 보고 area.contains 인 것만 담는다.
        //      **칸이 겹친다고 그 안의 점이 다 들어가는 것은 아니다.** 점마다 다시 봐야 한다.
        //   3. 잎이 아니면 네 자식 전부에 재귀한다. 어차피 1번이 걸러낸다.
        //
        // node 는 null 이 아니다. 자식 배열에는 null 이 안 들어간다(subdivide 가 넷 다 만든다).
        throw new UnsupportedOperationException("TODO 16: rangeFrom");
    }

    @Override
    public Point2D nearest(Point2D target) {
        List<Point2D> one = nearestK(target, 1);
        return one.isEmpty() ? null : one.get(0);
    }

    @Override
    public List<Point2D> nearestK(Point2D target, int k) {
        if (target == null) throw new IllegalArgumentException("target 이 null 이다");
        if (k < 0) throw new IllegalArgumentException("k 가 음수다: " + k);
        if (k == 0 || size == 0) return new ArrayList<>();
        KNearest best = new KNearest(target, k);
        searchNearest(root, target, best);
        return best.drain();
    }

    private void searchNearest(Node node, Point2D target, KNearest best) {
        visits++;
        // TODO 17: 반경 밖의 칸은 버리고, 가까운 자식부터 내려가라.
        //
        //   1. node.bounds.squaredDistanceTo(target) 이 best.radius() 이상이면 돌아온다.
        //      칸 전체가 이미 찾은 k 개보다 멀다는 뜻이다. 그 안은 볼 것이 없다.
        //      아직 k 개가 안 찼으면 radius() 가 무한대라 이 조건은 거짓이다.
        //   2. 잎이면 점을 전부 best.offer 한다.
        //   3. 잎이 아니면 **자식을 target 에 가까운 순으로 정렬해서** 하나씩 내려간다.
        //
        // 3번의 정렬이 없어도 답은 맞는다. 그런데 가까운 칸을 먼저 봐야 반경이 빨리 줄고,
        // 반경이 줄어야 1번이 자주 참이 된다. 순서가 곧 가지치기의 효율이다.
        // (12번 스킵 리스트에서 "먼저 크게 건너뛴다"가 이롭던 것과 같은 이야기다)
        //
        // 정렬은 node.children.clone() 을 떠서 하라. 원본 배열의 순서를 바꾸면
        // childIndex 가 골라주는 번호와 실제 칸이 어긋나 삽입이 엉뚱한 곳으로 간다.
        // Arrays.sort 에 Comparator.comparingLong(c -> c.bounds.squaredDistanceTo(target)) 을 준다.
        visits++;
        throw new UnsupportedOperationException("TODO 17: searchNearest");
    }

    /** 트리의 깊이. 잎 하나면 1. 좌표 범위와 점이 몰린 정도가 이 값을 정한다. */
    public int depth() {
        return depthOf(root);
    }

    private static int depthOf(Node node) {
        if (node.isLeaf()) return 1;
        int deepest = 0;
        for (Node child : node.children) {
            deepest = Math.max(deepest, depthOf(child));
        }
        return 1 + deepest;
    }

    /** 잎의 개수. 공간을 몇 칸으로 나눴는지를 본다. */
    public int leafCount() {
        return leavesOf(root);
    }

    private static int leavesOf(Node node) {
        if (node.isLeaf()) return 1;
        int total = 0;
        for (Node child : node.children) {
            total += leavesOf(child);
        }
        return total;
    }

    @Override
    public long visits() {
        return visits;
    }

    @Override
    public void resetVisits() {
        visits = 0;
    }
}
