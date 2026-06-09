package com.datastructure.graph.pop;

import java.util.*;

public class AdjacencyMatrixGraph {

    private boolean[][] matrix;
    private Map<Integer, Integer> vertexToIndex;
    private int size;

    public AdjacencyMatrixGraph(int capacity) {
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
        matrix[j][i] = true;
    }

    public void removeVertex(int v) {
        if (!vertexToIndex.containsKey(v)) throw new IllegalArgumentException("존재하지 않는 정점입니다: " + v);
        int removeIndex = vertexToIndex.get(v);
        int lastIndex = size - 1;

        if (removeIndex != lastIndex) {
            // 마지막 인덱스를 가진 정점 찾기
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
            //매핑 업데이트
            vertexToIndex.put(lastVertex, removeIndex);
        }

        // 마지막 행/열 초기화
        for (int i = 0; i < size; i++) {
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

    public boolean hasVertex(int v) { return vertexToIndex.containsKey(v); }

    public boolean hasEdge(int u, int v) {
        if (!vertexToIndex.containsKey(u)) throw new IllegalArgumentException("존재하지 않는 정점입니다: " + u);
        if (!vertexToIndex.containsKey(v)) throw new IllegalArgumentException("존재하지 않는 정점입니다: " + v);
        int i = vertexToIndex.get(u);
        int j = vertexToIndex.get(v);
        return matrix[i][j];
    }

    public List<Integer> getNeighbors(int v) {
        if (!vertexToIndex.containsKey(v)) throw new IllegalArgumentException("존재하지 않는 정점입니다: " + v);
        int idx = vertexToIndex.get(v);
        List<Integer> result = new ArrayList<>();
        for (Map.Entry<Integer,Integer> entry : vertexToIndex.entrySet()) {
            if (matrix[idx][entry.getValue()]) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    public int vertexCount() { return size; }

    public int edgeCount() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (matrix[i][j]) count++;
            }
        }
        return count / 2;
    }

    public boolean isEmpty() { return size==0; }
}