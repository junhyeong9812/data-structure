package com.datastructure.graph;

/**
 * 그래프로 푸는 문제들.
 *
 * 전부 Graph 인터페이스만 받는다. 인접 리스트든 행렬이든 같은 코드가 돈다.
 * **결과는 같고 비용만 다르다.** 그게 인터페이스를 나눈 이유다.
 */
public final class GraphProblems {

    private GraphProblems() {
    }

    /**
     * 문제 1. 최단 거리 (간선 수 기준)
     *
     * start 에서 각 정점까지 **몇 개의 간선**을 거쳐야 하는지. 못 가면 -1.
     * 가중치는 무시한다.
     *
     *   0-1, 1-2, 0-3 인 그래프에서 start=0  ->  [0, 1, 2, 1]
     *
     * 생각할 것
     *   - 왜 너비 우선(BFS)인가? 깊이 우선으로 하면 왜 최단이 아닌가?
     *   - 큐가 필요하다. 04번에서 만든 것이 이것이다.
     *   - 방문 표시를 넣을 때 하는가, 꺼낼 때 하는가?
     *     둘 다 답은 맞다. 다만 꺼낼 때 표시하면 같은 정점이 큐에 여러 번 들어가 메모리를 더 쓴다.
     *     정확성이 아니라 비용의 문제다.
     *
     * TODO(09): 구현하라.
     */
    public static int[] bfsDistances(Graph graph, int start) {
        throw new UnsupportedOperationException("TODO(09): bfsDistances");
    }

    /**
     * 문제 2. 깊이 우선 방문 순서
     *
     * start 에서 시작해 갈 수 있는 데까지 들어갔다가 되돌아 나오는 순서.
     * 이웃은 `neighbors` 가 주는 순서대로 본다.
     *
     * 생각할 것
     *   - 스택이 필요하다. 03번에서 만든 것이 이것이다. 재귀를 쓰면 호출 스택이 그 역할을 한다.
     *   - 재귀로 하면 깊은 그래프에서 StackOverflow 가 난다. 반복으로 하면 그 문제가 없다.
     *   - 반복으로 할 때 방문 순서를 재귀와 똑같이 맞추려면 이웃을 어떤 순서로 쌓아야 하는가?
     *
     * TODO(10): 구현하라. 반복으로 구현하라(테스트에 깊은 그래프가 있다).
     */
    public static int[] dfsOrder(Graph graph, int start) {
        throw new UnsupportedOperationException("TODO(10): dfsOrder");
    }

    /**
     * 문제 3. 위상 정렬
     *
     * 방향 그래프에서 "모든 간선이 앞에서 뒤로 가도록" 정점을 늘어놓는다.
     * 빌드 의존성, 강의 선수과목, 작업 순서가 전부 이 문제다.
     *
     *   0->1, 0->2, 1->3, 2->3  ->  [0, 1, 2, 3] (또는 [0, 2, 1, 3])
     *
     * 순환이 있으면 순서를 정할 수 없다. IllegalStateException 을 던진다.
     * 무방향 그래프면 IllegalArgumentException.
     *
     * 생각할 것
     *   - 들어오는 간선이 하나도 없는 정점은 지금 당장 처리할 수 있다. 그 정점을 빼면 어떻게 되는가?
     *   - 큐를 쓰면 자연스럽다(Kahn 알고리즘). 답이 여러 개일 수 있으므로
     *     테스트는 "순서가 유효한가"만 검사한다.
     *   - 다 처리했는데 정점이 남아 있으면 그건 무슨 뜻인가?
     *
     * TODO(11): 구현하라.
     */
    public static int[] topologicalSort(Graph graph) {
        throw new UnsupportedOperationException("TODO(11): topologicalSort");
    }

    /**
     * 문제 4. 가중치 최단 거리 (다익스트라)
     *
     * start 에서 각 정점까지의 **가중치 합**이 최소인 경로 비용. 못 가면 -1.
     *
     * 생각할 것
     *   - BFS 는 왜 안 되는가? 간선 수가 적어도 가중치 합이 클 수 있다.
     *   - "지금까지 알아낸 것 중 가장 가까운 정점"을 반복해서 꺼내야 한다.
     *     **그게 07번 힙이다.** (여기서는 java.util.PriorityQueue 를 쓴다.
     *     모듈이 분리되어 있어서일 뿐, 07번에서 만든 것이 정확히 이 물건이다.)
     *   - 이미 확정된 정점을 다시 꺼내면 어떻게 되는가? 걸러내야 하는가?
     *   - 음수 간선이 있으면 왜 깨지는가? (그래서 addEdge 가 음수를 거부한다.)
     *
     * TODO(12): 구현하라.
     */
    public static long[] shortestPaths(Graph graph, int start) {
        throw new UnsupportedOperationException("TODO(12): shortestPaths");
    }
}
