package com.datastructure.spatial;

import java.util.List;

/**
 * 2차원 점 집합에 "어디에 있나"를 묻는 자료구조.
 *
 * <h2>06번 이진 탐색 트리가 여기서 멈춘다</h2>
 *
 * 06번은 "왼쪽은 작고 오른쪽은 크다" 하나로 1차원을 갈랐다.
 * 그런데 점 (3, 7) 과 (5, 2) 중 무엇이 더 큰가. 정할 수 없다.
 * x 로 보면 (3,7) 이 작고 y 로 보면 (5,2) 가 작다. 2차원에는 전순서가 없다.
 *
 * 전순서가 없으면 05번 해시맵도 06번 BST 도 못 쓴다.
 * 해시맵은 "5 근처에 뭐가 있나"를 애초에 버렸고, BST 는 그 질문에 답하려고
 * 순서를 새겼는데 그 순서를 2차원에서 정의할 수 없기 때문이다.
 *
 * 남는 것은 전수 조사뿐이다. 점 100만 개에서 "가장 가까운 것"을 물으면 100만 번을 센다.
 * 그것이 NaiveSpatialIndex 이고, 이 문제의 기준선이다.
 *
 * <h2>그래서 무엇을 하는가</h2>
 *
 * 전순서를 포기하는 대신 공간을 조각내고, 질의와 안 겹치는 조각을 통째로 건너뛴다.
 * 가지치기(pruning) 라고 부른다. 조각내는 방법이 두 가지다.
 *
 * | | KdTree | QuadTree |
 * |---|---|---|
 * | 나누는 기준 | 데이터의 중앙값 | 공간의 절반 |
 * | 점이 몰려 있으면 | 잘 버틴다 | 깊어진다 |
 * | 삽입 후 균형 | 깨진다 | 상관없다 |
 * | 깊이를 정하는 것 | 넣은 순서 | 좌표 범위 |
 *
 * <h2>계약</h2>
 *
 * 이 문제집에서는 중복을 허용하지 않는다. 같은 점을 두 번 넣으면 두 번째는 false 다.
 * 그래서 세 구현이 전부 집합(set) 이고, 서로 답을 대조할 수 있다.
 * 좌표는 정수다. 부동소수점 좌표를 쓰면 같은 점인지 아닌지가 흔들려서
 * 세 구현의 답을 비교하는 이 문제집의 주력 검증이 성립하지 않는다.
 *
 * 이 인터페이스에는 TODO 가 없다. 계약은 주어지는 것이다.
 */
public interface SpatialIndex {

    /**
     * 점을 넣는다. 실제로 늘어났으면 true.
     *
     * 이미 있는 점이면 false. QuadTree 는 경계 밖의 점도 false 다.
     * p 가 null 이면 IllegalArgumentException.
     */
    boolean insert(Point2D p);

    /** 그 점이 들어 있나. p 가 null 이면 IllegalArgumentException. */
    boolean contains(Point2D p);

    /** 들어 있는 점의 개수. */
    int size();

    /** 전부 비운다. */
    void clear();

    default boolean isEmpty() {
        return size() == 0;
    }

    /**
     * 사각형 안(경계 포함)에 있는 점 전부.
     *
     * 순서는 정하지 않는다. 구현마다 다르게 나오는 것이 정상이고,
     * 그것을 계약으로 만들면 트리가 방문 순서를 못 바꾼다.
     * 테스트는 정렬해서 비교한다.
     *
     * area 가 null 이면 IllegalArgumentException.
     */
    List<Point2D> rangeSearch(Rectangle area);

    /**
     * target 에서 가장 가까운 점. 비어 있으면 null.
     *
     * 같은 거리의 점이 여럿이면 그중 무엇이 나오든 맞는 답이다.
     * 그래서 테스트는 거리를 비교하지 어느 점인지를 비교하지 않는다.
     *
     * target 이 null 이면 IllegalArgumentException.
     */
    Point2D nearest(Point2D target);

    /**
     * target 에서 가까운 순서로 k 개. 가까운 것이 앞이다.
     *
     * k 가 size 보다 크면 전부 준다. k 가 0 이면 빈 목록, 음수면 IllegalArgumentException.
     * k 번째와 k+1 번째의 거리가 같으면 둘 중 무엇이 뽑히든 맞는 답이다.
     * 그래서 테스트는 거리 수열을 비교한다.
     */
    List<Point2D> nearestK(Point2D target, int k);
}
