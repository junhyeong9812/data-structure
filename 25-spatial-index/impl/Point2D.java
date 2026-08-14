package com.datastructure.spatial;

public final class Point2D {

    public final int x;
    public final int y;

    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int coordinate(int axis) {
        return axis == 0 ? x : y;
    }

    public long squaredDistanceTo(Point2D other) {
        long dx = (long) x - other.x;
        long dy = (long) y - other.y;
        return dx * dx + dy * dy;
    }

    public double distanceTo(Point2D other) {
        return Math.sqrt(squaredDistanceTo(other));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point2D other)) return false;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
