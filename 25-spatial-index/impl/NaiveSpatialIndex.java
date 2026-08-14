package com.datastructure.spatial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NaiveSpatialIndex implements SpatialIndex, VisitCounting {

    private final List<Point2D> points = new ArrayList<>();
    private long visits;

    private List<Point2D> scan() {
        visits += points.size();
        return points;
    }

    @Override
    public boolean insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("점이 null 이다");
        if (contains(p)) return false;
        points.add(p);
        return true;
    }

    @Override
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("점이 null 이다");
        return scan().contains(p);
    }

    @Override
    public int size() {
        return points.size();
    }

    @Override
    public void clear() {
        points.clear();
    }

    @Override
    public List<Point2D> rangeSearch(Rectangle area) {
        if (area == null) throw new IllegalArgumentException("사각형이 null 이다");
        List<Point2D> out = new ArrayList<>();
        for (Point2D p : scan()) {
            if (area.contains(p)) out.add(p);
        }
        return out;
    }

    @Override
    public Point2D nearest(Point2D target) {
        if (target == null) throw new IllegalArgumentException("target 이 null 이다");
        Point2D best = null;
        long bestDistance = Long.MAX_VALUE;
        for (Point2D p : scan()) {
            long d = target.squaredDistanceTo(p);
            if (d < bestDistance) {
                bestDistance = d;
                best = p;
            }
        }
        return best;
    }

    @Override
    public List<Point2D> nearestK(Point2D target, int k) {
        if (target == null) throw new IllegalArgumentException("target 이 null 이다");
        if (k < 0) throw new IllegalArgumentException("k 가 음수다: " + k);
        if (k == 0) return new ArrayList<>();
        List<Point2D> sorted = new ArrayList<>(scan());
        sorted.sort(Comparator.comparingLong(target::squaredDistanceTo));
        return new ArrayList<>(sorted.subList(0, Math.min(k, sorted.size())));
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
