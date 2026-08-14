package com.datastructure.spatial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class QuadTree implements SpatialIndex, VisitCounting {

    static final class Node {
        final Rectangle bounds;
        final List<Point2D> points = new ArrayList<>();
        Node[] children;

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
        if (!bounds.contains(p)) return false;
        if (!insertInto(root, p)) return false;
        size++;
        return true;
    }

    private boolean insertInto(Node node, Point2D p) {
        visits++;
        if (node.isLeaf()) {
            if (node.points.contains(p)) return false;
            node.points.add(p);
            if (node.points.size() > capacity && node.bounds.canSubdivide()) subdivide(node);
            return true;
        }
        return insertInto(node.children[childIndex(node, p)], p);
    }

    private void subdivide(Node node) {
        Rectangle[] quads = node.bounds.subdivide();
        node.children = new Node[quads.length];
        for (int i = 0; i < quads.length; i++) {
            node.children[i] = new Node(quads[i]);
        }
        List<Point2D> moved = new ArrayList<>(node.points);
        node.points.clear();
        for (Point2D p : moved) {
            insertInto(node.children[childIndex(node, p)], p);
        }
    }

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
        root = new Node(bounds);
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
        if (!node.bounds.intersects(area)) return;
        if (node.isLeaf()) {
            for (Point2D p : node.points) {
                if (area.contains(p)) out.add(p);
            }
            return;
        }
        for (Node child : node.children) {
            rangeFrom(child, area, out);
        }
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
        if (node.bounds.squaredDistanceTo(target) >= best.radius()) return;
        if (node.isLeaf()) {
            for (Point2D p : node.points) {
                best.offer(p);
            }
            return;
        }
        Node[] order = node.children.clone();
        Arrays.sort(order, Comparator.comparingLong(c -> c.bounds.squaredDistanceTo(target)));
        for (Node child : order) {
            searchNearest(child, target, best);
        }
    }

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
