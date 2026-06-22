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

    public void removeVertex(int v) {
        if (!vertexToIndex.containsKey(v)) throw new IllegalArgumentException("존재하지 않는 정점입니다: " + v);
        int removeIndex = vertexToIndex.get(v);
        int lastIndex = size - 1;

        if (removeIndex != lastIndex) {
            int lastVertex = -1;
            for (Map.Entry<Integer, Integer> entry : vertexToIndex.entrySet()) {
                if (entry.getValue() == lastIndex) {
                    lastVertex = entry.getKey();
                    break;
                }
            }
            // 행렬에서 마지막 행/열을 삭제된 위치로 복사
            for (int i = 0; i < size; i++) {
                matrix[removeIndex][i] = matrix[lastIndex][i];
                matrix[i][removeIndex] = matrix[i][lastIndex];
            }
            vertexToIndex.put(lastVertex, removeIndex);
        }

        for (int i =0; i < size; i++) {
            matrix[lastIndex][i] = false;
            matrix[i][lastIndex] = false;
        }

        vertexToIndex.remove(v);
        size--;
    }

    public void removeEdge(int u, int v) {
        if (!vertexToIndex.containsKey(u)) throw new IllegalArgumentException("존재하지 않는 정점입니다: " + u);
        if (!vertexToIndex.containsKey(v)) throw new IllegalArgumentException("존재하지 않는 정점입니다: " + v);
        int i = vertexToIndex.get(u);
        int j = vertexToIndex.get(v);
        if (!matrix[i][j]) throw new IllegalArgumentException("존재하지 않는 간선입니다: " + u + "-" + v);
        matrix[i][j] = false;
        matrix[j][i] = false;
    }

    public boolean hasVertex(int v) { return false; }

    public boolean hasEdge(int u, int v) { return false; }

    public List<Integer> getNeighbors(int v) { return null; }

    public int vertexCount() { return 0; }

    public int edgeCount() { return 0; }

    public boolean isEmpty() { return false; }
}