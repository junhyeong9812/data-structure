package com.datastructure.spatial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 전수 조사. 점을 목록에 담고 질의마다 전부 훑는다.
 *
 * <h2>이게 왜 첫 번째 구현인가</h2>
 *
 * 첫째, 이것이 정답 판정 기준이다. 두 트리는 이것과 답이 같아야 한다.
 * 가지치기를 잘못 쓰면 예외도 안 나고 조용히 몇 개가 빠지는데,
 * 그걸 잡는 방법은 느리고 명백한 구현과 대조하는 것뿐이다.
 *
 * 둘째, 이것이 비교 기준이다. 질의 한 번에 정확히 n 걸음이다.
 * 트리가 그 n 을 몇 분의 일로 줄이는지가 이 문제의 전부다.
 *
 * 셋째, 점이 몇백 개뿐이면 이게 제일 빠르다. 트리는 만드는 비용이 있고 노드마다 분기가 있다.
 * "작으면 그냥 훑어라"는 01번 동적 배열부터 되풀이된 이야기다.
 */
public class NaiveSpatialIndex implements SpatialIndex, VisitCounting {

    private final List<Point2D> points = new ArrayList<>();
    private long visits;

    /**
     * 훑을 목록을 준다. 부를 때마다 방문 수가 n 만큼 늘어난다.
     *
     * 전수 조사는 무엇을 묻든 전부 보므로 여기서 한 번에 센다.
     * points 를 직접 쓰지 말고 이걸 써라. 안 그러면 측정이 0 으로 나온다.
     */
    private List<Point2D> scan() {
        visits += points.size();
        return points;
    }

    @Override
    public boolean insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException("점이 null 이다");
        if (contains(p)) return false;      // 중복은 넣지 않는다. 그래서 삽입도 O(n) 이다
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
        // TODO 6: scan() 을 훑으면서 area 안에 있는 점을 모아라.
        //
        // 세 줄이면 된다. 어려울 게 없다는 것이 요점이다.
        // 사각형이 아무리 작아도 n 개를 전부 본다. 점 100만 개면 100만 번이다.
        // 그 100만을 줄이려고 나머지 두 구현이 있다.
        throw new UnsupportedOperationException("TODO 6: rangeSearch");
    }

    @Override
    public Point2D nearest(Point2D target) {
        if (target == null) throw new IllegalArgumentException("target 이 null 이다");
        // TODO 7: 제곱거리가 가장 작은 점을 찾아라. 비었으면 null.
        //
        // **제곱근을 쓰지 마라.** squaredDistanceTo 로 비교하면 된다(TODO 1 참고).
        // 최솟값 초기화는 Long.MAX_VALUE 로 두면 첫 점에서 자연히 갱신된다.
        //
        // 동점이면 무엇을 골라도 맞는 답이다. 다만 어느 쪽을 고르든 **거리는 같아야** 한다.
        throw new UnsupportedOperationException("TODO 7: nearest");
    }

    @Override
    public List<Point2D> nearestK(Point2D target, int k) {
        if (target == null) throw new IllegalArgumentException("target 이 null 이다");
        if (k < 0) throw new IllegalArgumentException("k 가 음수다: " + k);
        if (k == 0) return new ArrayList<>();
        // TODO 8: 제곱거리로 정렬해서 앞의 k 개를 반환하라. k 가 size 보다 크면 전부.
        //
        // 여기서는 **정렬이 맞는 선택이다.** 어차피 n 개를 다 볼 것이라 O(n log n) 이고,
        // 힙으로 O(n log k) 를 해도 같은 n 을 다 훑는다.
        // 트리는 다르다. 힙을 쓰면 그 머리가 곧 가지치기 반경이 되어 **볼 노드 자체가 줄어든다.**
        // 그것이 KNearest 를 두 트리에서만 쓰는 이유다.
        //
        // scan() 이 준 목록을 그 자리에서 정렬하면 **인덱스의 내용이 뒤섞인다.** 복사해서 정렬하라.
        // Comparator.comparingLong(target::squaredDistanceTo) 를 쓰면 한 줄이다.
        // subList 는 원본을 들여다보는 창이라 그대로 반환하면 밖에서 인덱스를 흔들 수 있다.
        throw new UnsupportedOperationException("TODO 8: nearestK");
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
