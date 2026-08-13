# oop/Graph.java

그래프 인터페이스. 제네릭 정점 타입 `V` 지원.

```java
package com.datastructure.graph.oop;

import java.util.Collection;
import java.util.List;

public interface Graph<V> {
    void addVertex(V v);
    void addEdge(V u, V v);
    void removeVertex(V v);
    void removeEdge(V u, V v);

    boolean hasVertex(V v);
    boolean hasEdge(V u, V v);
    Collection<V> getNeighbors(V v);
    Collection<V> vertices();

    int vertexCount();
    int edgeCount();
    boolean isDirected();

    List<V> bfs(V start);
    List<V> dfs(V start);
    List<V> shortestPath(V start, V end);
    boolean hasCycle();
    List<List<V>> connectedComponents();
}
```
