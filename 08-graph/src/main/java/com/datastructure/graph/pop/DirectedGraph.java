package com.datastructure.graph.pop;

import java.util.*;

public class DirectedGraph {

    private Map<Integer, List<Integer>> adjList;

    public DirectedGraph() {
        this.adjList = new HashMap<>();
    }

    public void addVertex(int v) {
        if (adjList.containsKey(v)) throw new IllegalArgumentException("이미 존재하는 정점입니다: " + v);
        adjList.put(v,new ArrayList<>());
    }

    public void addEdge(int u, int v) {
        if (!adjList.containsKey(u)) throw new IllegalArgumentException("존재하지 않는 정점입니다: "+u);
        if (!adjList.containsKey(v)) throw new IllegalArgumentException("존재하지 않는 정점입니다: "+v);
        if (u == v) throw new IllegalArgumentException("자기 루프는 허용하지 않습니다: " + u);
        if (adjList.get(u).contains(v)) throw new IllegalArgumentException("이미 존재하는 간선입니다: " + u + "-" + v);
        adjList.get(u).add(v);
    }

    public void removeVertex(int v) {
        if (!adjList.containsKey(v)) throw new IllegalArgumentException("존재하지 않는 정점입니다: " + v);
        adjList.remove(v);
        for (List<Integer> neighbors: adjList.values()) {
            neighbors.remove(Integer.valueOf(v));
        }
    }

    public void removeEdge(int u, int v) {
        if (!adjList.containsKey(u)) throw new IllegalArgumentException("존재하지 않는 정점입니다: "+ u);
        if (!adjList.containsKey(v)) throw new IllegalArgumentException("존재하지 않는 정점입니다: "+ v);
        if (!adjList.get(u).contains(v)) throw new IllegalArgumentException("존재하지 않는 간선입니다: " + u + "-" + v);
        adjList.get(u).remove(Integer.valueOf(v));
    }

    public boolean hasVertex(int v) {
        return adjList.containsKey(v);
    }

    public boolean hasEdge(int u, int v) {
        if (!adjList.containsKey(u)) throw new IllegalArgumentException("존재하지 않는 정점입니다: " + u);
        if (!adjList.containsKey(v)) throw new IllegalArgumentException("존재하지 않는 정점입니다: " + v);
        return adjList.get(u).contains(v);
    }

    public List<Integer> getNeighbors(int v) {
        if (!adjList.containsKey(v)) throw new IllegalArgumentException("존재하지 않는 정점입니다: " + v);
        return adjList.get(v);
    }

    public int vertexCount() { return adjList.size(); }

    public int edgeCount() {
        int count = 0;
        for (List<Integer> neighbors : adjList.values()) {
            count += neighbors.size();
        }
        return count;
    }

    public boolean isEmpty() { return false; }
}
