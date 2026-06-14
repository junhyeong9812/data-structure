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

    public void addVertex(int v) {}

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