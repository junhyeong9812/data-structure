package com.datastructure.spatial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

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
        if (p.equals(node.point)) return node;
        int axis = depth & 1;
        if (p.coordinate(axis) <= node.point.coordinate(axis)) {
            node.left = insertInto(node.left, p, depth + 1);
        } else {
            node.right = insertInto(node.right, p, depth + 1);
        }
        return node;
    }

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
        int axis = depth & 1;
        points.subList(from, to).sort(Comparator.comparingInt(p -> p.coordinate(axis)));
        int mid = (from + to) >>> 1;
        int split = points.get(mid).coordinate(axis);
        while (mid + 1 < to && points.get(mid + 1).coordinate(axis) == split) mid++;
        Node node = new Node(points.get(mid));
        node.left = buildRange(points, from, mid, depth + 1);
        node.right = buildRange(points, mid + 1, to, depth + 1);
        return node;
    }

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
        root = null;
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
        if (area.contains(node.point)) out.add(node.point);
        int axis = depth & 1;
        int split = node.point.coordinate(axis);
        if (area.min(axis) <= split) rangeFrom(node.left, area, depth + 1, out);
        if (area.max(axis) > split) rangeFrom(node.right, area, depth + 1, out);
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
        if (target.squaredDistanceTo(node.point) < target.squaredDistanceTo(best)) {
            best = node.point;
        }
        int axis = depth & 1;
        long gap = (long) target.coordinate(axis) - node.point.coordinate(axis);
        Node near = gap <= 0 ? node.left : node.right;
        Node far = gap <= 0 ? node.right : node.left;
        best = nearestFrom(near, target, depth + 1, best);
        if (gap * gap < target.squaredDistanceTo(best)) {
            best = nearestFrom(far, target, depth + 1, best);
        }
        return best;
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
        best.offer(node.point);
        int axis = depth & 1;
        long gap = (long) target.coordinate(axis) - node.point.coordinate(axis);
        Node near = gap <= 0 ? node.left : node.right;
        Node far = gap <= 0 ? node.right : node.left;
        nearestKFrom(near, target, depth + 1, best);
        if (gap * gap < best.radius()) {
            nearestKFrom(far, target, depth + 1, best);
        }
    }

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
