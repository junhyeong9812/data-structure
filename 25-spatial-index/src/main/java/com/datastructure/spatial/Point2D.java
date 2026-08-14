package com.datastructure.spatial;

/**
 * 2차원 점. 값 객체다. 만든 뒤로 바뀌지 않는다.
 *
 * 좌표를 int 로 둔 것이 이 문제집의 결정이다.
 * double 을 쓰면 "같은 점인가"가 흔들리고, 그러면 세 구현의 답을 대조할 수 없다.
 * 실무의 위경도도 보통 정수 마이크로도(1e-6 도 단위)로 저장한다.
 *
 * 좌표는 절댓값 10억 이내를 전제로 한다. 그 안에서는 제곱거리가 long 에 들어간다.
 */
public final class Point2D {

    public final int x;
    public final int y;

    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 축 번호로 좌표를 꺼낸다. 0 이면 x, 1 이면 y.
     *
     * KD-트리가 깊이마다 축을 바꾼다. 이게 없으면 if (depth % 2 == 0) 이 사방에 퍼진다.
     */
    public int coordinate(int axis) {
        return axis == 0 ? x : y;
    }

    /**
     * 두 점 사이의 제곱거리.
     *
     * TODO 1 을 보라. 이 문제집에서 거리를 비교할 때는 언제나 이 값을 쓴다.
     */
    public long squaredDistanceTo(Point2D other) {
        // TODO 1: (x 차이)^2 + (y 차이)^2 을 반환하라. 제곱근을 씌우지 않는다.
        //
        // **반환형이 long 인 이유부터 보라.** 좌표가 10억쯤 떨어지면 차이가 20억이고
        // 그 제곱은 4 * 10^18 이다. int 최대는 21억이라 어림도 없다.
        //   int  dx = x - other.x;  long d = dx * dx;   <- 이미 넘쳤다. long 으로 올려도 늦었다
        //   long dx = (long) x - other.x;               <- 곱하기 전에 올려야 한다
        // 넘치면 예외가 나는 게 아니라 **음수가 나온다.** 그러면 최근접이 조용히 틀린다.
        //
        // **왜 제곱근을 안 쓰는가.** 두 가지다.
        //   1. 비싸다. 가지치기는 노드마다 거리를 비교한다. sqrt 를 매번 부르면 그 비교가 본전이다.
        //   2. 정보가 사라진다. double 은 가수가 53비트라 큰 값에서 이웃한 수들이 같은 값으로 뭉친다.
        //      제곱거리 4*10^18 과 4*10^18+1 은 sqrt 를 씌우면 **완전히 같은 double** 이 된다.
        //      (GeometryTest 의 sqrtLosesInformation 이 그걸 보여준다)
        //
        // 제곱은 음이 아닌 수에서 단조증가라 **비교만 할 것이면 제곱근이 필요 없다.**
        // 거리 자체를 사람에게 보여줄 때만 distanceTo 를 쓴다.
        throw new UnsupportedOperationException("TODO 1: squaredDistanceTo");
    }

    /** 실제 거리. 사람에게 보여줄 때만 쓴다. 비교에는 제곱거리를 쓴다. */
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
