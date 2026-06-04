package com.datastructure.graph.pop;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class WeightedGraph {
    private Map<Integer, Map<Integer, Integer>> adjList;

    public WeightedGraph() {
        this.adjList = new HashMap<>();
    }

    public void addVertex(int v) {
        if (adjList.containsKey(v)) throw new IllegalArgumentException("이미 존재하는 정점입니다: " + v);
        adjList.put(v, new HashMap<>());
    }

    public void addEdge(int u, int v, int weight) {}

    public void removeVertex(int v) {}

    public void removeEdge(int u, int v) {}

    public boolean basVertex(int v) {
        return false;
    }

    public boolean hasEdge(int u, int v) {
        return false;
    }

    public int getWeight(int u, int v) {
        return 0;
    }

    public List<Integer> getNeighbors(int v) {return null;}

    public int vertexCount() { return 0;}

    public int edgeCount() {return 0;}

    public boolean isEmpty() {return false;}
}
