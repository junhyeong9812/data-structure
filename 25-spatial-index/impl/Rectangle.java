package com.datastructure.spatial;

public final class Rectangle {

    public final int minX;
    public final int minY;
    public final int maxX;
    public final int maxY;

    public Rectangle(int minX, int minY, int maxX, int maxY) {
        if (minX > maxX || minY > maxY) {
            throw new IllegalArgumentException(
                    "min 이 max 보다 클 수 없다: x [" + minX + ", " + maxX + "] y [" + minY + ", " + maxY + "]");
        }
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public int min(int axis) {
        return axis == 0 ? minX : minY;
    }

    public int max(int axis) {
        return axis == 0 ? maxX : maxY;
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("점이 null 이다");
        return minX <= p.x && p.x <= maxX && minY <= p.y && p.y <= maxY;
    }

    public boolean intersects(Rectangle other) {
        if (other == null) throw new IllegalArgumentException("사각형이 null 이다");
        return !(other.maxX < minX || other.minX > maxX || other.maxY < minY || other.minY > maxY);
    }

    public long squaredDistanceTo(Point2D p) {
        if (p == null) throw new IllegalArgumentException("점이 null 이다");
        long dx = p.x < minX ? (long) minX - p.x : (p.x > maxX ? (long) p.x - maxX : 0L);
        long dy = p.y < minY ? (long) minY - p.y : (p.y > maxY ? (long) p.y - maxY : 0L);
        return dx * dx + dy * dy;
    }

    public boolean canSubdivide() {
        return maxX > minX && maxY > minY;
    }

    public Rectangle[] subdivide() {
        if (!canSubdivide()) {
            throw new IllegalStateException("더는 쪼갤 수 없는 칸이다: " + this);
        }
        int midX = minX + (maxX - minX) / 2;
        int midY = minY + (maxY - minY) / 2;
        return new Rectangle[] {
                new Rectangle(minX, minY, midX, midY),
                new Rectangle(midX + 1, minY, maxX, midY),
                new Rectangle(minX, midY + 1, midX, maxY),
                new Rectangle(midX + 1, midY + 1, maxX, maxY)
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rectangle r)) return false;
        return minX == r.minX && minY == r.minY && maxX == r.maxX && maxY == r.maxY;
    }

    @Override
    public int hashCode() {
        int h = minX;
        h = 31 * h + minY;
        h = 31 * h + maxX;
        h = 31 * h + maxY;
        return h;
    }

    @Override
    public String toString() {
        return "[" + minX + ", " + maxX + "] x [" + minY + ", " + maxY + "]";
    }
}
