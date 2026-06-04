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

    public void addEdge(int u, int v, int weight) {
        if (!adjList.containsKey(u)) throw new IllegalArgumentException("존재하지 않는 정점입니다: "+u);
        if (!adjList.containsKey(v)) throw new IllegalArgumentException("존재하지 않는 정점입니다: "+v);
        if (u == v) throw new IllegalArgumentException("자기 루프는 허용하지 않습니다: " + u);
        if (adjList.get(u).containsKey(v)) throw new IllegalArgumentException("이미 존재하는 간선입니다: " + u + "-" + v);
        adjList.get(u).put(v, weight);
    }

    public void removeVertex(int v) {
        if (!adjList.containsKey(v)) throw new IllegalArgumentException("존재하지 않는 정점입니다: " + v);
        adjList.remove(v);
        for(Map<Integer, Integer> values: adjList.values()) {
            values.remove(v);
        }
    }

    public void removeEdge(int u, int v) {
        if (!adjList.containsKey(u)) throw new IllegalArgumentException("존재하지 않는 정점입니다: " + u);
        if (!adjList.containsKey(v)) throw new IllegalArgumentException("존재하지 않는 정점입니다: " + v);
        if (!adjList.get(u).containsKey(v)) throw new IllegalArgumentException("존재하지 않는 간선입니다: " + u + "-" + v);
        adjList.get(u).remove(v);
    }

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
