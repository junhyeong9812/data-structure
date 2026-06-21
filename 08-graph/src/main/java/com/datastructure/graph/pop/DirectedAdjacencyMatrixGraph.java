package com.datastructure.graph.pop;

import java.util.*;

public class DirectedAdjacencyMatrixGraph {

    private boolean[][] matrix;
    private Map<Integer, Integer> vertexToIndex;
    private int size;

    public DirectedAdjacencyMatrixGraph(int capacity) {
        this.matrix = new boolean[capacity][capacity];
        this.vertexToIndex = new HashMap<>();
        this.size = 0;
    }

    public void addVertex(int v) {
        if (vertexToIndex.containsKey(v)) throw new IllegalArgumentException("이미 존재하는 정점입니다: " + v);
        if (size >= matrix.length) throw new IllegalArgumentException("용량을 초과했습니다");
        vertexToIndex.put(v, size);
        size++;
    }

    public void addEdge(int u, int v) {
        if (!vertexToIndex.containsKey(u)) throw new IllegalArgumentException("존재하지 않는 정점입니다: " + u);
        if (!vertexToIndex.containsKey(v)) throw new IllegalArgumentException("존재하지 않는 정점입니다: " + v);
        if (u == v) throw new IllegalArgumentException("자기 루프는 허용하지 않습니다: " + u);
        int i = vertexToIndex.get(u);
        int j = vertexToIndex.get(v);
        if (matrix[i][j]) throw new IllegalArgumentException("이미 존재하는 간선입니다: " + u + "-" + v);
        matrix[i][j] = true;
    }

    public void removeVertex(int v) {}

    public void removeEdge(int u, int v) {}

    public boolean hasVertex(int v) { return false; }

    public boolean hasEdge(int u, int v) { return false; }

    public List<Integer> getNeighbors(int v) { return null; }

    public int vertexCount() { return 0; }

    public int edgeCount() { return 0; }

    public boolean isEmpty() { return false; }
}