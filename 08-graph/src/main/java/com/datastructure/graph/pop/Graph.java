package com.datastructure.graph.pop;

import java.util.*;

public class Graph {

    private Map<Integer, List<Integer>> adjList;

    public Graph() {
        this.adjList = new HashMap<>();
    }

    public void addVertex(int v) {
        if (adjList.containsKey(v)) throw new IllegalArgumentException("이미 존재하는 정점입니다: " + v);
        adjList.put(v,new ArrayList<>());
    }

    public void addEdge(int u, int v) {}

    public void removeVertex(int v) {}

    public void removeEdge(int u, int v) {}

    public boolean hasVertex(int v) { return false; }

    public boolean hasEdge(int u, int v) { return false; }

    public List<Integer> getNeighbors(int v) { return null; }

    public int vertexCount() { return 0; }

    public int edgeCount() { return 0; }

    public boolean isEmpty() { return false; }
}