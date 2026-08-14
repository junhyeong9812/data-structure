package com.datastructure.spatial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 쿼드트리의 내부를 직접 본다.
 *
 * 규칙: 잎에만 점이 있고, 용량을 넘으면 네 칸으로 쪼개 아래로 내려보낸다.
 * 쪼갠 네 칸은 부모를 빈틈없이, 겹치지 않게 덮어야 한다.
 *
 * 테스트가 root 와 Node 의 bounds, points, children 을 직접 읽는다. 이름이 계약이다.
 */
@DisplayName("QuadTree 구조")
class QuadTreeStructureTest {

    private static List<Point2D> pointsIn(QuadTree.Node node) {
        List<Point2D> out = new ArrayList<>();
        collect(node, out);
        return out;
    }

    private static void collect(QuadTree.Node node, List<Point2D> out) {
        if (node.isLeaf()) {
            out.addAll(node.points);
            return;
        }
        for (QuadTree.Node child : node.children) collect(child, out);
    }

    /** 내부 노드는 점을 들고 있으면 안 되고, 모든 점은 자기 칸 안에 있어야 한다. */
    private static void assertWellFormed(QuadTree.Node node) {
        if (node.isLeaf()) {
            for (Point2D p : node.points) {
                assertTrue(node.bounds.contains(p), p + " 가 자기 칸 " + node.bounds + " 밖에 있다");
            }
            return;
        }
        assertTrue(node.points.isEmpty(), "쪼갠 노드가 점을 그대로 들고 있다. 아래로 내려보내야 한다");
        assertEquals(4, node.children.length);
        for (QuadTree.Node child : node.children) {
            assertTrue(node.bounds.intersects(child.bounds));
            assertWellFormed(child);
        }
    }

    @Test
    @DisplayName("용량을 넘을 때만 쪼갠다")
    void splitsOnlyWhenFull() {
        QuadTree quad = new QuadTree(new Rectangle(0, 0, 15, 15), 4);
        for (int i = 0; i < 4; i++) quad.insert(new Point2D(i, i));
        assertEquals(1, quad.depth(), "용량까지는 잎 하나다");
        assertEquals(1, quad.leafCount());

        quad.insert(new Point2D(9, 9));
        assertEquals(2, quad.depth(), "다섯 번째에서 쪼개진다");
        assertEquals(4, quad.leafCount(), "네 칸이 생긴다");
        assertWellFormed(quad.root);
        assertEquals(5, pointsIn(quad.root).size(), "쪼개면서 점을 잃으면 안 된다");
    }

    @Test
    @DisplayName("고정 데이터셋의 깊이")
    void fixedDatasetDepth() {
        // 파이썬 참조 구현으로 검산한 값이다.
        QuadTree quad = new QuadTree(new Rectangle(0, 0, 15, 15), 2);
        for (Point2D p : SpatialIndexContractTest.FIXED) quad.insert(p);
        assertEquals(3, quad.depth());
        assertEquals(7, quad.leafCount());
        assertWellFormed(quad.root);
        assertEquals(6, pointsIn(quad.root).size());
    }

    @Test
    @DisplayName("가까이 몰린 점은 여러 번 쪼개진다")
    void clusteredPointsForceRepeatedSplits() {
        // 다섯 점이 4x4 안에 있는데 경계가 16x16 이라 다섯 층까지 내려간다.
        // 한 번 쪼개도 같은 칸에 남으면 또 쪼갠다. 재귀가 여기서 필요하다.
        QuadTree quad = new QuadTree(new Rectangle(0, 0, 15, 15), 1);
        for (Point2D p : List.of(new Point2D(1, 1), new Point2D(2, 2), new Point2D(1, 2),
                new Point2D(2, 1), new Point2D(0, 0))) {
            assertTrue(quad.insert(p));
        }
        assertEquals(5, quad.depth());
        assertWellFormed(quad.root);
        assertEquals(5, quad.size());
    }

    @Test
    @DisplayName("1x1 칸에서는 더 못 쪼갠다")
    void unitCellsStopSplitting() {
        // 정수 좌표라 여기가 바닥이다. 무한 재귀를 막는 것이 이 조건이다.
        QuadTree quad = new QuadTree(new Rectangle(0, 0, 1, 1), 1);
        for (Point2D p : List.of(new Point2D(0, 0), new Point2D(1, 0),
                new Point2D(0, 1), new Point2D(1, 1))) {
            assertTrue(quad.insert(p));
        }
        assertEquals(4, quad.size());
        assertEquals(2, quad.depth(), "1x1 칸 네 개로 끝난다");
        assertWellFormed(quad.root);
    }

    @Test
    @DisplayName("폭이 1 인 경계는 용량을 넘어도 못 쪼갠다")
    void degenerateBoundsOverflowTheLeaf() {
        // 정직하게 적어둔다. 네 칸으로 쪼개려면 가로도 세로도 쪼개져야 한다.
        // 폭이 1 인 칸은 가로로 못 쪼개므로 아예 안 쪼갠다.
        // 그러면 잎 하나에 점이 용량보다 많이 쌓인다. 답은 맞고 느려질 뿐이다.
        // 경계를 정사각형으로 잡으면(권장) 이런 칸이 안 나온다.
        QuadTree column = new QuadTree(new Rectangle(3, 0, 3, 100), 2);
        for (int y = 0; y <= 20; y++) assertTrue(column.insert(new Point2D(3, y)));
        assertEquals(21, column.size());
        assertEquals(1, column.depth(), "폭이 1 이면 한 번도 못 쪼갠다. 잎 하나에 21개가 쌓인다");
        assertEquals(21, column.rangeSearch(new Rectangle(3, 0, 3, 100)).size(), "그래도 답은 맞는다");
        assertEquals(0L, new Point2D(3, 5).squaredDistanceTo(column.nearest(new Point2D(3, 5))));

        QuadTree flat = new QuadTree(new Rectangle(0, 7, 100, 7), 2);
        for (int x = 0; x <= 20; x++) assertTrue(flat.insert(new Point2D(x, 7)));
        assertEquals(1, flat.depth(), "높이가 1 인 칸도 마찬가지다");
        assertEquals(21, flat.rangeSearch(new Rectangle(0, 7, 100, 7)).size());
    }

    @Test
    @DisplayName("경계 밖은 거절한다")
    void outsideBoundsIsRejected() {
        QuadTree quad = new QuadTree(new Rectangle(-8, -8, 7, 7), 2);
        assertTrue(quad.insert(new Point2D(-8, -8)), "경계 위는 안쪽이다");
        assertTrue(quad.insert(new Point2D(7, 7)));
        assertFalse(quad.insert(new Point2D(8, 0)));
        assertFalse(quad.insert(new Point2D(0, -9)));
        assertEquals(2, quad.size());
        assertFalse(quad.contains(new Point2D(8, 0)), "경계 밖을 물어도 터지지 않는다");
    }

    @Test
    @DisplayName("잘못된 생성자 인자")
    void badConstructorArgs() {
        assertThrows(IllegalArgumentException.class, () -> new QuadTree(null, 4));
        assertThrows(IllegalArgumentException.class,
                () -> new QuadTree(new Rectangle(0, 0, 7, 7), 0));
        assertThrows(IllegalArgumentException.class,
                () -> new QuadTree(new Rectangle(0, 0, 7, 7), -3));
    }

    @Test
    @DisplayName("점을 잃지 않는다")
    void keepsEveryPoint() {
        QuadTree quad = new QuadTree(new Rectangle(0, 0, 255, 255), 3);
        List<Point2D> points = new TestSupport.Dice(64L).points(800, 256);
        for (Point2D p : points) quad.insert(p);
        assertWellFormed(quad.root);
        assertEquals(quad.size(), pointsIn(quad.root).size(), "잎에 담긴 점의 수가 size 와 다르다");
        assertEquals(quad.size(), pointsIn(quad.root).stream().distinct().count(),
                "같은 점이 두 칸에 들어갔다");
    }

    @Test
    @DisplayName("clear 하면 잎 하나로 돌아간다")
    void clearResetsToOneLeaf() {
        QuadTree quad = new QuadTree(new Rectangle(0, 0, 255, 255), 3);
        for (Point2D p : new TestSupport.Dice(3L).points(200, 256)) quad.insert(p);
        assertTrue(quad.depth() > 1);
        quad.clear();
        assertEquals(0, quad.size());
        assertEquals(1, quad.depth(), "쪼갠 칸이 남아 있으면 안 된다");
        assertEquals(quad.bounds(), quad.root.bounds);
    }
}
