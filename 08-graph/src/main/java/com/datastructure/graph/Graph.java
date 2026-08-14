package com.datastructure.graph;

/**
 * 정점과 간선으로 이루어진 그래프.
 *
 * 01~07은 전부 "원소를 어떻게 담을까"였다. 여기서는 다르다.
 * 원소 사이의 관계 자체가 자료구조다. 담는 것이 아니라 잇는 것이다.
 *
 * 정점은 0부터 vertexCount-1 까지의 정수다. 이름표를 붙이고 싶으면 바깥에서 매핑하면 된다.
 * 그 단순화 덕분에 인접 행렬이 그대로 배열이 된다.
 *
 * 구현이 둘이고, 어느 쪽이 나은지는 그래프의 밀도가 정한다.
 *
 *   AdjacencyListGraph    정점마다 "이웃 목록"을 들고 있는다
 *   AdjacencyMatrixGraph  V x V 표를 만들어 놓고 칸을 채운다
 *
 * | | 인접 리스트 | 인접 행렬 |
 * |---|---|---|
 * | 메모리 | O(V + E) | O(V^2) 간선이 없어도 |
 * | hasEdge(u, v) | O(deg(u)) | O(1) |
 * | neighbors(v) 순회 | O(deg(v)) | O(V) 이웃이 없어도 |
 * | 간선 추가 | O(1) | O(1) |
 *
 * 현실의 그래프는 대개 희소하다(간선이 V^2 보다 훨씬 적다).
 * 도로망, 소셜 그래프, 의존성 그래프가 전부 그렇다. 그래서 보통 인접 리스트를 쓴다.
 * 행렬은 밀집 그래프이거나 hasEdge 를 아주 자주 물을 때 쓴다.
 *
 * 이 인터페이스에는 TODO 가 없다. 계약은 주어지는 것이다.
 */
public interface Graph {

    int vertexCount();

    /** 간선 개수. 무방향 그래프에서 u-v 는 한 개로 센다. */
    int edgeCount();

    boolean isDirected();

    /** 가중치 1 짜리 간선을 잇는다. */
    void addEdge(int from, int to);

    /**
     * 가중치가 있는 간선을 잇는다. 같은 간선을 다시 이으면 가중치를 덮어쓴다.
     *
     * @throws IndexOutOfBoundsException 정점 번호가 범위 밖일 때
     * @throws IllegalArgumentException  가중치가 음수일 때 (다익스트라가 음수 간선을 못 다룬다)
     */
    void addEdge(int from, int to, int weight);

    boolean hasEdge(int from, int to);

    /**
     * 간선의 가중치. 간선이 없으면 NO_EDGE.
     */
    int weight(int from, int to);

    /** from 에서 갈 수 있는 정점들. 순서는 구현이 정한다. */
    Iterable<Integer> neighbors(int from);

    /** 간선이 없음을 나타내는 값. */
    int NO_EDGE = Integer.MAX_VALUE;
}
