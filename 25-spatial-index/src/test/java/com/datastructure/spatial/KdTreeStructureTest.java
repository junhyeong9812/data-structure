package com.datastructure.spatial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * KD-트리의 내부를 직접 본다.
 *
 * 계약 테스트는 "답이 맞는가"만 본다. 그건 전부 훑어도 통과한다.
 * 여기서는 노드를 재귀로 내려가며 분할 규칙이 실제로 지켜지는지 본다.
 * 그 규칙이 깨지면 가지치기가 답을 지운다.
 *
 * 규칙: 깊이가 짝수면 x 로, 홀수면 y 로 가른다.
 * 왼쪽은 그 축의 좌표가 노드 이하, 오른쪽은 노드보다 크다.
 * 같은 값을 왼쪽으로 몰아야 탐색이 한 길로 내려간다.
 *
 * 테스트가 root 와 Node 의 point, left, right 를 직접 읽는다. 이름이 계약이다.
 */
@DisplayName("KdTree 구조")
class KdTreeStructureTest {

    /** 분할 규칙을 재귀로 확인한다. 이것이 이 자료구조의 불변식이다. */
    private static void assertSplitProperty(KdTree.Node node, int depth) {
        if (node == null) return;
        int axis = depth & 1;
        int split = node.point.coordinate(axis);
        for (Point2D p : subtree(node.left)) {
            assertTrue(p.coordinate(axis) <= split,
                    "깊이 " + depth + " 축 " + axis + ": 왼쪽의 " + p + " 가 " + node.point + " 보다 크다");
        }
        for (Point2D p : subtree(node.right)) {
            assertTrue(p.coordinate(axis) > split,
                    "깊이 " + depth + " 축 " + axis + ": 오른쪽의 " + p + " 가 " + node.point + " 이하다");
        }
        assertSplitProperty(node.left, depth + 1);
        assertSplitProperty(node.right, depth + 1);
    }

    private static List<Point2D> subtree(KdTree.Node node) {
        List<Point2D> out = new ArrayList<>();
        collect(node, out);
        return out;
    }

    private static void collect(KdTree.Node node, List<Point2D> out) {
        if (node == null) return;
        out.add(node.point);
        collect(node.left, out);
        collect(node.right, out);
    }

    @Test
    @DisplayName("깊이마다 축을 번갈아 가른다")
    void alternatesAxes() {
        KdTree kd = new KdTree();
        for (Point2D p : new TestSupport.Dice(2024L).points(500, 200)) kd.insert(p);
        assertSplitProperty(kd.root, 0);
        assertEquals(kd.size(), subtree(kd.root).size(), "노드 수와 size 가 다르다");
    }

    @Test
    @DisplayName("같은 좌표가 잔뜩 있어도 규칙이 지켜진다")
    void tiesGoLeft() {
        // 좌표 범위가 8 이라 같은 x, 같은 y 가 계속 나온다.
        // 같은 값을 오른쪽으로 보내면 contains 가 못 찾고 rangeSearch 가 빠뜨린다.
        KdTree kd = new KdTree();
        List<Point2D> points = new TestSupport.Dice(9L).points(300, 8);
        for (Point2D p : points) kd.insert(p);
        assertSplitProperty(kd.root, 0);
        for (Point2D p : points) assertTrue(kd.contains(p), p + " 를 못 찾는다");
    }

    @Test
    @DisplayName("일괄 구축도 같은 규칙을 지킨다")
    void buildKeepsTheSameRule() {
        List<Point2D> points = new TestSupport.Dice(5L).points(400, 50);
        KdTree built = KdTree.build(points);
        assertSplitProperty(built.root, 0);
        for (Point2D p : points) assertTrue(built.contains(p), p + " 를 못 찾는다");
    }

    @Test
    @DisplayName("고정 데이터셋의 높이")
    void fixedDatasetHeight() {
        // 파이썬 참조 구현으로 검산한 값이다.
        KdTree kd = new KdTree();
        for (Point2D p : SpatialIndexContractTest.FIXED) kd.insert(p);
        assertEquals(4, kd.height(), "여섯 점을 순서대로 넣으면 4층");
        assertEquals(new Point2D(2, 3), kd.root.point, "먼저 넣은 점이 뿌리다");
        assertEquals(3, KdTree.build(List.of(SpatialIndexContractTest.FIXED)).height(),
                "중앙값으로 지으면 3층");
    }

    @Test
    @DisplayName("일괄 구축은 균형이 잡힌다")
    void buildIsBalanced() {
        List<Point2D> points = new TestSupport.Dice(77L).points(1000, 100_000);
        KdTree inserted = new KdTree();
        for (Point2D p : points) inserted.insert(p);
        KdTree built = KdTree.build(points);

        assertEquals(inserted.size(), built.size());
        assertTrue(built.height() <= 12,
                "1000개면 이상적으로 10층이다. 지금 " + built.height() + "층");
        assertTrue(built.height() < inserted.height(),
                "일괄 구축 " + built.height() + " 대 삽입 " + inserted.height());
    }

    @Test
    @DisplayName("빈 목록으로도 지을 수 있다")
    void buildEdgeCases() {
        assertEquals(0, KdTree.build(List.of()).size());
        assertEquals(1, KdTree.build(List.of(new Point2D(3, 3))).size());
        assertEquals(1, KdTree.build(List.of(new Point2D(3, 3), new Point2D(3, 3))).size(),
                "같은 점이 두 번 들어오면 하나로 친다");
        assertThrows(IllegalArgumentException.class, () -> KdTree.build(null));
    }

    @Test
    @DisplayName("일괄 구축이 남의 목록을 흔들지 않는다")
    void buildDoesNotDisturbTheCallersList() {
        // 중앙값을 찾으려면 정렬해야 하는데, 받은 목록을 그 자리에서 정렬하면
        // 부른 쪽의 순서가 조용히 바뀐다. 그런 부수효과는 나중에 아주 찾기 어렵다.
        List<Point2D> given = new ArrayList<>(List.of(
                new Point2D(9, 1), new Point2D(2, 8), new Point2D(5, 5), new Point2D(1, 9)));
        List<Point2D> before = new ArrayList<>(given);
        KdTree.build(given);
        assertEquals(before, given, "받은 목록을 그대로 두어야 한다");
    }

    @Test
    @DisplayName("clear 는 뿌리만 끊는다")
    void clearDropsTheRoot() {
        // 06번에서 본 것과 같다. 부모를 끊으면 아래가 통째로 GC 대상이 된다.
        KdTree kd = new KdTree();
        for (Point2D p : new TestSupport.Dice(1L).points(100, 100)) kd.insert(p);
        assertNotNull(kd.root);
        kd.clear();
        assertNull(kd.root, "뿌리를 안 끊으면 트리 전체가 살아 있다");
        assertEquals(0, kd.size());
    }
}
