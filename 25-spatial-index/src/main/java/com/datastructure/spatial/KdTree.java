package com.datastructure.spatial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * KD-트리. 깊이마다 축을 번갈아 가며 공간을 가른다.
 *
 * <h2>06번과 무엇이 다른가</h2>
 *
 * 06번 이진 탐색 트리는 노드마다 "왼쪽은 작고 오른쪽은 크다"였다.
 * 여기서는 그 "작다"를 깊이마다 다른 축으로 잰다.
 *
 *   깊이 0 (짝수) : x 로 가른다
 *   깊이 1 (홀수) : y 로 가른다
 *   깊이 2        : 다시 x
 *
 * 그래서 노드 하나가 평면을 반으로 자르는 선이 되고, 트리 전체가 직사각형 칸들의 계층이 된다.
 *
 * <h2>불변식 (이게 계약이다)</h2>
 *
 * 깊이 d 의 축을 a 라 하면
 *   왼쪽 서브트리의 모든 점 : coordinate(a) 가 노드 이하
 *   오른쪽 서브트리의 모든 점 : coordinate(a) 가 노드보다 큼
 *
 * 같은 값을 왼쪽으로 몰아야 한다. 양쪽에 흩어지면 탐색이 두 갈래로 내려가야 하고,
 * 미리 채워둔 contains 가 한 길로만 내려가므로 못 찾는다.
 * 좌표 범위가 좁으면 같은 x, 같은 y 가 수없이 나온다. 여기가 제일 많이 깨지는 자리다.
 *
 * <h2>삭제가 없는 이유</h2>
 *
 * 06번에서 삭제가 제일 어려웠다. 자식이 둘인 노드를 지울 때 후속자를 끌어올렸다.
 * KD-트리에서는 그게 안 된다. 후속자는 "그 축에서 다음으로 큰 것"인데,
 * 끌어올린 뒤 그 노드의 깊이가 바뀌면 축이 바뀌고 아래 전체의 불변식이 깨진다.
 * 제대로 하려면 그 서브트리를 통째로 다시 지어야 한다.
 * 그래서 실무의 KD-트리는 대개 정적으로 쓰고, 지울 일이 있으면 무덤돌(tombstone)을 세우거나
 * 주기적으로 재구축한다. 이 문제집도 삽입만 지원한다.
 *
 * 테스트가 root 와 Node 의 point, left, right 를 직접 읽는다. 이름이 계약이다.
 */
public class KdTree implements SpatialIndex, VisitCounting {

    static final class Node {
        final Point2D point;
        Node left;
        Node right;

        Node(Point2D point) {
            this.point = point;
        }
    }

    Node root;
    int size;
    private long visits;

    @Override
    public boolean insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("점이 null 이다");
        int before = size;
        root = insertInto(root, p, 0);
        return size != before;
    }

    private Node insertInto(Node node, Point2D p, int depth) {
        if (node == null) {
            size++;
            return new Node(p);
        }
        visits++;
        // TODO 9: 축을 골라 왼쪽이나 오른쪽으로 내려보내라.
        //
        //   1. 이미 있는 점이면(equals) 아무것도 하지 말고 node 를 그대로 반환한다.
        //      **여기를 빼먹으면 size 만 안 늘고 같은 점이 트리에 계속 쌓인다.**
        //   2. 축은 depth & 1 이다. 0 이면 x, 1 이면 y. coordinate(axis) 로 꺼낸다.
        //   3. p 의 좌표가 노드 이하면 왼쪽, 크면 오른쪽. **같으면 왼쪽이다**(위 불변식).
        //   4. 내려간 쪽의 결과를 그 자식에 도로 붙이고(node.left = ...) node 를 반환한다.
        //
        // 06번 put 과 같은 모양이다. 다른 것은 축이 깊이마다 바뀐다는 것 하나뿐이다.
        // 자식에 도로 붙이는 것을 잊으면 새 노드가 허공에 만들어졌다 사라진다.
        throw new UnsupportedOperationException("TODO 9: insertInto");
    }

    /**
     * 점 목록으로 균형 잡힌 트리를 한 번에 짓는다. 중복은 하나로 친다.
     *
     * 삽입은 넣는 순서가 모양을 정한다. 정렬된 순서로 넣으면 한 줄이 되어 06번 꼴이 난다.
     * 미리 전부 알고 있다면 중앙값을 뿌리로 삼아 지을 수 있고, 그러면 높이가 log n 이다.
     */
    public static KdTree build(List<Point2D> points) {
        if (points == null) throw new IllegalArgumentException("목록이 null 이다");
        List<Point2D> distinct = new ArrayList<>(new LinkedHashSet<>(points));
        if (distinct.contains(null)) throw new IllegalArgumentException("점이 null 이다");
        KdTree tree = new KdTree();
        tree.root = buildRange(distinct, 0, distinct.size(), 0);
        tree.size = distinct.size();
        return tree;
    }

    private static Node buildRange(List<Point2D> points, int from, int to, int depth) {
        if (from >= to) return null;
        // TODO 10: [from, to) 구간을 그 깊이의 축으로 정렬하고 중앙값을 뿌리로 삼아라.
        //
        //   1. axis = depth & 1
        //   2. points.subList(from, to).sort(...) 로 그 구간만 정렬한다.
        //      subList 는 원본을 들여다보는 창이라 여기를 정렬하면 원본의 그 구간이 정렬된다.
        //      Comparator.comparingInt(p -> p.coordinate(axis)) 를 쓴다.
        //      (axis 를 람다 안에서 쓰려면 사실상 final 이어야 한다. 재대입하지 마라)
        //   3. mid = (from + to) >>> 1 을 고른다.
        //   4. **같은 좌표를 오른쪽으로 넘기지 않도록 mid 를 밀어라.**
        //         while (mid + 1 < to && 다음 점의 그 축 좌표가 지금과 같으면) mid++
        //      이게 없으면 중앙값과 같은 좌표가 오른쪽에 남는다. 그러면 위의 불변식이 깨져서
        //      contains 와 rangeSearch 가 그 점들을 못 찾는다. **좌표가 몰린 데이터에서 터진다.**
        //   5. mid 의 점으로 노드를 만들고, 왼쪽은 [from, mid), 오른쪽은 [mid+1, to) 로 재귀한다.
        //      깊이는 둘 다 depth + 1 이다.
        //
        // build 가 받은 목록을 흔들면 안 되므로 위에서 이미 복사해뒀다. 그 복사본을 정렬한다.
        throw new UnsupportedOperationException("TODO 10: buildRange");
    }

    /**
     * 그 점이 있나. 미리 채워뒀다.
     *
     * 불변식대로 한 길로만 내려간다. 같은 좌표를 왼쪽에 몰아뒀기 때문에 이게 성립한다.
     * insert 에서 같은 값을 오른쪽으로 보내면 여기가 못 찾는다.
     */
    @Override
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("점이 null 이다");
        Node node = root;
        int depth = 0;
        while (node != null) {
            visits++;
            if (p.equals(node.point)) return true;
            int axis = depth & 1;
            node = p.coordinate(axis) <= node.point.coordinate(axis) ? node.left : node.right;
            depth++;
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = null;        // 06번과 같다. 부모를 끊으면 아래가 통째로 GC 대상이 된다
        size = 0;
    }

    @Override
    public List<Point2D> rangeSearch(Rectangle area) {
        if (area == null) throw new IllegalArgumentException("사각형이 null 이다");
        List<Point2D> out = new ArrayList<>();
        rangeFrom(root, area, 0, out);
        return out;
    }

    private void rangeFrom(Node node, Rectangle area, int depth, List<Point2D> out) {
        if (node == null) return;
        visits++;
        // TODO 11: 이 노드를 담을지 보고, **볼 값어치가 있는 쪽으로만** 내려가라.
        //
        //   1. area.contains(node.point) 면 out 에 담는다.
        //   2. axis = depth & 1, split = node.point.coordinate(axis)
        //   3. 왼쪽에는 split 이하의 좌표만 있다.
        //      질의 사각형의 그 축 최솟값이 split 보다 크면 **왼쪽에는 답이 하나도 없다.**
        //      즉 area.min(axis) <= split 일 때만 내려간다.
        //   4. 오른쪽에는 split 보다 큰 좌표만 있다. area.max(axis) > split 일 때만 내려간다.
        //
        // **여기가 이 자료구조의 전부다.** 3번과 4번의 조건을 빼고 양쪽에 다 내려가도 답은 맞는다.
        // 다만 그러면 노드를 전부 방문하므로 NaiveSpatialIndex 와 정확히 같은 일을 한다.
        // (PruningTest 가 방문 수를 세서 그걸 확인한다)
        //
        // 부등호를 조심하라. 3번을 area.min(axis) < split 으로 쓰면
        // 분할선 위에 딱 걸친 점들이 통째로 사라진다. 왼쪽은 split 을 **포함**한다.
        // 06번 keysInRange 의 가지치기와 같은 이야기이고, 축이 하나 늘었을 뿐이다.
        throw new UnsupportedOperationException("TODO 11: rangeFrom");
    }

    @Override
    public Point2D nearest(Point2D target) {
        if (target == null) throw new IllegalArgumentException("target 이 null 이다");
        if (root == null) return null;
        return nearestFrom(root, target, 0, root.point);
    }

    private Point2D nearestFrom(Node node, Point2D target, int depth, Point2D best) {
        if (node == null) return best;
        visits++;
        // TODO 12: 유망한 쪽을 먼저 내려가고, 돌아와서 반대쪽을 볼지 정하라.
        //
        //   1. 이 노드가 best 보다 가까우면 best 를 갈아끼운다(제곱거리로 비교).
        //   2. axis = depth & 1
        //      gap = target.coordinate(axis) - node.point.coordinate(axis)   <- long 으로 받아라
        //      gap 이 0 이하면 target 은 분할선의 왼쪽이다. 가까운 쪽이 left, 먼 쪽이 right.
        //      gap 이 양수면 반대다.
        //   3. 가까운 쪽으로 먼저 내려간다. 그 결과를 best 에 받는다.
        //   4. **먼 쪽은 조건부다.** gap * gap 이 지금 best 까지의 제곱거리보다 작을 때만 내려간다.
        //
        // 4번이 이 알고리즘의 심장이다. gap 은 target 에서 분할선까지의 거리이고,
        // 먼 쪽의 점들은 전부 그 선 너머에 있으므로 **최소한 gap 만큼은 떨어져 있다.**
        // 그 gap 이 이미 찾은 best 보다 멀면 저쪽에는 더 나은 답이 있을 수 없다.
        //
        // 3번을 먼저 하는 순서도 중요하다. 유망한 쪽을 먼저 내려가야 best 가 빨리 작아지고,
        // best 가 작아야 4번이 자주 참이 아니게 된다. 순서를 뒤집으면 답은 맞고 방문 수만 는다.
        //
        // gap 을 int 로 받으면 gap * gap 이 넘칠 수 있다. TODO 1 과 같은 함정이다.
        // 4번을 통째로 빼면(늘 양쪽에 내려가면) 답은 맞고 전수 조사가 된다.
        throw new UnsupportedOperationException("TODO 12: nearestFrom");
    }

    @Override
    public List<Point2D> nearestK(Point2D target, int k) {
        if (target == null) throw new IllegalArgumentException("target 이 null 이다");
        if (k < 0) throw new IllegalArgumentException("k 가 음수다: " + k);
        if (k == 0 || root == null) return new ArrayList<>();
        KNearest best = new KNearest(target, k);
        nearestKFrom(root, target, 0, best);
        return best.drain();
    }

    private void nearestKFrom(Node node, Point2D target, int depth, KNearest best) {
        if (node == null) return;
        visits++;
        // TODO 13: TODO 12 와 같은 모양이다. 다른 것은 둘뿐이다.
        //
        //   - "best 보다 가까우면 갈아끼운다" 대신 best.offer(node.point) 를 부른다.
        //     들어갈 자리가 있는지는 KNearest 가 판단한다.
        //   - 먼 쪽으로 갈지 정하는 기준이 best.radius() 다.
        //     아직 k 개가 안 찼으면 무한대라 반드시 내려가고, 다 찼으면 k 번째까지의 거리가 반경이다.
        //
        // 반경이 탐색 도중에 계속 줄어든다는 것이 요점이다.
        // 가까운 쪽을 먼저 훑어 k 개를 빨리 채우면 반경이 빨리 작아지고, 그만큼 덜 본다.
        throw new UnsupportedOperationException("TODO 13: nearestKFrom");
    }

    /** 트리의 높이. 비었으면 0. 삽입 순서가 이 값을 정한다. */
    public int height() {
        return heightOf(root);
    }

    private int heightOf(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(heightOf(node.left), heightOf(node.right));
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
