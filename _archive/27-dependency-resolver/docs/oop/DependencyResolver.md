# oop/DependencyResolver.java

제네릭 OOP 의존성 해결기. 노드 타입 임의 (`Comparable` 불필요).

```java
package com.datastructure.dependencyresolver.oop;

import java.util.List;
import java.util.Set;

public interface DependencyResolver<T> {
    void addNode(T node);
    void addDependency(T from, T to);

    List<T> resolve();
    boolean hasCycle();
    List<T> findCycle();
    List<List<T>> getParallelGroups();

    Set<T> getDependencies(T node);
    Set<T> getDependents(T node);
}
```

---

# oop/GraphDependencyResolver.java

```java
package com.datastructure.dependencyresolver.oop;

import java.util.*;

public class GraphDependencyResolver<T> implements DependencyResolver<T> {

    public static class CyclicDependencyException extends RuntimeException {
        public final List<?> cycle;
        public CyclicDependencyException(List<?> cycle) {
            super("Cycle: " + cycle);
            this.cycle = cycle;
        }
    }

    private final Set<T> nodes = new LinkedHashSet<>();
    private final Map<T, Set<T>> deps = new HashMap<>();
    private final Map<T, Set<T>> reverse = new HashMap<>();

    @Override
    public void addNode(T node) {
        if (nodes.add(node)) {
            deps.put(node, new LinkedHashSet<>());
            reverse.put(node, new LinkedHashSet<>());
        }
    }

    @Override
    public void addDependency(T from, T to) {
        addNode(from);
        addNode(to);
        deps.get(from).add(to);
        reverse.get(to).add(from);
    }

    @Override
    public Set<T> getDependencies(T node) {
        return Collections.unmodifiableSet(deps.getOrDefault(node, Set.of()));
    }

    @Override
    public Set<T> getDependents(T node) {
        return Collections.unmodifiableSet(reverse.getOrDefault(node, Set.of()));
    }

    @Override
    public List<T> resolve() {
        Map<T, Integer> inDegree = new HashMap<>();
        for (T n : nodes) inDegree.put(n, deps.get(n).size());
        Queue<T> ready = new ArrayDeque<>();
        for (T n : nodes) if (inDegree.get(n) == 0) ready.offer(n);

        List<T> result = new ArrayList<>();
        while (!ready.isEmpty()) {
            T n = ready.poll();
            result.add(n);
            for (T dependent : reverse.get(n)) {
                inDegree.merge(dependent, -1, Integer::sum);
                if (inDegree.get(dependent) == 0) ready.offer(dependent);
            }
        }
        if (result.size() != nodes.size()) {
            throw new CyclicDependencyException(findCycle());
        }
        return result;
    }

    @Override
    public boolean hasCycle() {
        try { resolve(); return false; }
        catch (CyclicDependencyException e) { return true; }
    }

    @Override
    public List<T> findCycle() {
        Set<T> visited = new HashSet<>();
        Set<T> onStack = new HashSet<>();
        Deque<T> path = new ArrayDeque<>();
        for (T n : nodes) {
            if (!visited.contains(n)) {
                List<T> r = dfs(n, visited, onStack, path);
                if (r != null) return r;
            }
        }
        return Collections.emptyList();
    }

    private List<T> dfs(T n, Set<T> visited, Set<T> onStack, Deque<T> path) {
        visited.add(n);
        onStack.add(n);
        path.push(n);
        for (T d : deps.get(n)) {
            if (!visited.contains(d)) {
                List<T> r = dfs(d, visited, onStack, path);
                if (r != null) return r;
            } else if (onStack.contains(d)) {
                List<T> cycle = new ArrayList<>();
                for (T s : path) {
                    cycle.add(s);
                    if (Objects.equals(s, d)) break;
                }
                Collections.reverse(cycle);
                cycle.add(d);
                return cycle;
            }
        }
        onStack.remove(n);
        path.pop();
        return null;
    }

    @Override
    public List<List<T>> getParallelGroups() {
        Map<T, Integer> inDegree = new HashMap<>();
        for (T n : nodes) inDegree.put(n, deps.get(n).size());
        List<List<T>> groups = new ArrayList<>();
        Set<T> processed = new HashSet<>();

        while (processed.size() < nodes.size()) {
            List<T> group = new ArrayList<>();
            for (T n : nodes) {
                if (!processed.contains(n) && inDegree.get(n) == 0) group.add(n);
            }
            if (group.isEmpty()) throw new CyclicDependencyException(findCycle());
            for (T n : group) {
                processed.add(n);
                for (T dependent : reverse.get(n)) inDegree.merge(dependent, -1, Integer::sum);
                inDegree.put(n, -1);
            }
            groups.add(group);
        }
        return groups;
    }
}
```
