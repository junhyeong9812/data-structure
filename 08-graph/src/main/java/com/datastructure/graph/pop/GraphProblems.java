package com.datastructure.graph.pop;

import java.util.List;
import java.util.Map;

public class GraphProblems {

    // 순회
    public List<Integer> bfs(Graph graph, int start) {return null;}
    public List<Integer> dfs(Graph graph, int start) {return null;}

    // 최단 경로
    public List<Integer> shortestPath(Graph graph, int start, int end) {return null;}
    public Map<Integer, Integer> dijkstra(WeightedGraph graph, int start) {return null;}
    public Map<Integer, Integer> bellmanFord(WeightedGraph graph, int start) {return null;}
    public int[][] floydWarshall(WeightedGraph graph) {return null;}

    // 사이클/위상 정렬
    public boolean hasCycle(DirectedGraph graph) {return false;}
    public List<Integer> topologicalSort(DirectedGraph graph) {return null;}

    // 연결/판별
    public List<List<Integer>> connectedComponents(Graph graph) {return null;}
    public boolean isBipartite(Graph graph) {return false;}

    // 최소 신장 트리
    public List<int[]> prim(WeightedGraph graph) {return null;}
    public List<int[]> kruskal(WeightedGraph graph) {return null;}
}
