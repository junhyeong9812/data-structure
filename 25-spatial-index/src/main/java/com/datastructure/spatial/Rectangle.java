package com.datastructure.spatial;

/**
 * 축에 나란한 사각형. 질의 영역이자 쿼드트리의 칸이다.
 *
 * 네 변을 전부 포함한다. 즉 [minX, maxX] x [minY, maxY] 이고 양 끝이 들어간다.
 * 정수 좌표라 이렇게 두는 편이 헷갈리지 않는다.
 * 폭이 1 인 사각형(minX == maxX)도 정상이고, 그 안에 점이 들어갈 수 있다.
 */
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

    /** 축 번호로 최솟값. 0 이면 x, 1 이면 y. KD-트리의 가지치기가 쓴다. */
    public int min(int axis) {
        return axis == 0 ? minX : minY;
    }

    /** 축 번호로 최댓값. */
    public int max(int axis) {
        return axis == 0 ? maxX : maxY;
    }

    /** 점이 이 사각형 안(경계 포함)에 있나. */
    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("점이 null 이다");
        // TODO 2: x 와 y 가 각각 범위 안에 있는지 보라.
        //
        // **경계를 포함한다.** < 로 쓰면 테두리에 있는 점이 통째로 사라진다.
        // 격자 5x5 에서 [0,0]x[0,0] 을 물으면 (0,0) 하나가 나와야 한다.
        //
        // 자바에서는 minX <= p.x && p.x <= maxX 처럼 두 번 나눠 써야 한다.
        // 파이썬의 a <= x <= b 같은 연쇄 비교가 없다.
        throw new UnsupportedOperationException("TODO 2: contains");
    }

    /** 다른 사각형과 조금이라도 겹치나. 한 점만 닿아도 겹친 것이다. */
    public boolean intersects(Rectangle other) {
        if (other == null) throw new IllegalArgumentException("사각형이 null 이다");
        // TODO 3: 겹치는지 보라.
        //
        // **겹치는 조건을 직접 쓰려고 하지 마라.** 경우가 많아서 십중팔구 틀린다.
        // 안 겹치는 조건은 네 가지뿐이고 서로 배타적이라 훨씬 쉽다.
        //
        //   상대가 완전히 왼쪽 : other.maxX < minX
        //   상대가 완전히 오른쪽 : other.minX > maxX
        //   완전히 아래, 완전히 위도 같은 모양
        //
        // 넷 중 하나라도 참이면 안 겹친다. 그 전체를 부정하면 겹친다.
        //
        // 부등호에 = 를 넣으면 안 된다. other.maxX == minX 는 **한 줄이 닿은 것**이고
        // 그 줄 위에 점이 있을 수 있다. 여기서 놓치면 쿼드트리가 답을 빠뜨린다.
        throw new UnsupportedOperationException("TODO 3: intersects");
    }

    /**
     * 이 사각형에서 점까지의 제곱거리. 점이 안에 있으면 0.
     *
     * 쿼드트리의 가지치기가 이 값 하나로 돌아간다.
     * 칸까지의 거리가 지금까지 찾은 최단 거리보다 멀면 그 칸 안은 볼 필요가 없다.
     */
    public long squaredDistanceTo(Point2D p) {
        if (p == null) throw new IllegalArgumentException("점이 null 이다");
        // TODO 4: 축마다 "범위를 얼마나 벗어났는지"를 구해 제곱해서 더하라.
        //
        //   p.x 가 minX 보다 작으면      -> minX - p.x
        //   p.x 가 maxX 보다 크면        -> p.x - maxX
        //   범위 안이면                  -> 0
        //
        // y 도 똑같이 하고 둘을 제곱해서 더한다. 이것이 사각형 위의 가장 가까운 점까지의 거리다.
        // 벗어난 양을 구하는 것을 클램프(clamp)라고 부른다.
        //
        // **0 을 빠뜨리기 쉽다.** 안에 있는 축을 0 으로 안 두면 변 위의 점이 멀어 보이고,
        // 그러면 볼 필요가 있는 칸을 건너뛰어 답이 조용히 틀린다.
        // TODO 1 과 같은 이유로 여기도 long 이다.
        throw new UnsupportedOperationException("TODO 4: squaredDistanceTo");
    }

    /** 네 칸으로 쪼갤 수 있나. 가로도 세로도 2 이상이어야 한다. */
    public boolean canSubdivide() {
        return maxX > minX && maxY > minY;
    }

    /**
     * 네 칸으로 쪼갠다. 순서는 SW, SE, NW, NE.
     *
     * 미리 채워뒀다. 다만 왜 이렇게 나뉘는지는 보라.
     * midX 까지가 왼쪽, midX + 1 부터가 오른쪽이다. 그래서 두 칸이 겹치지 않고 빈틈도 없다.
     * 겹치면 한 점이 두 칸에 들어가 범위 조회가 같은 점을 두 번 주고,
     * 빈틈이 있으면 그 자리의 점을 넣을 곳이 없어 삽입이 터진다.
     *
     * minX + (maxX - minX) / 2 로 쓴 것은 (minX + maxX) / 2 가 넘칠 수 있어서다.
     * 좌표가 20억 근처면 두 수를 더하는 순간 int 를 넘는다. 15번 B-트리에서 본 것과 같은 함정이다.
     */
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
