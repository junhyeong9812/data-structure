# pop/DependencyResolver.java

위상 정렬 기반 의존성 해결기. resolve / hasCycle / findCycle / getParallelGroups / getDependents.

`addDependency(A, B)`는 "A는 B에 의존" → B가 먼저 처리됨.

```java
package com.datastructure.dependencyresolver.pop;

import java.util.*;

public class DependencyResolver {

    public static class CyclicDependencyException extends RuntimeException {
        public final List<String> cycle;
        public CyclicDependencyException(List<String> cycle) {
            super("Cycle: " + cycle);
            this.cycle = cycle;
        }
    }

    private final Set<String> nodes = new LinkedHashSet<>();
    private final Map<String, Set<String>> deps = new HashMap<>();      // node → [dependencies]
    private final Map<String, Set<String>> reverse = new HashMap<>();   // node → [dependents]

    public void addNode(String name) {
        if (nodes.add(name)) {
            deps.put(name, new LinkedHashSet<>());
            reverse.put(name, new LinkedHashSet<>());
        }
    }

    public void addDependency(String from, String to) {
        addNode(from);
        addNode(to);
        deps.get(from).add(to);
        reverse.get(to).add(from);
    }

    public Set<String> getDependencies(String name) {
        return Collections.unmodifiableSet(deps.getOrDefault(name, Set.of()));
    }

    public Set<String> getDependents(String name) {
        return Collections.unmodifiableSet(reverse.getOrDefault(name, Set.of()));
    }

    /** 의존성이 먼저 오는 순서. */
    public List<String> resolve() {
        Map<String, Integer> inDegree = new HashMap<>();
        for (String n : nodes) inDegree.put(n, deps.get(n).size());

        Queue<String> ready = new ArrayDeque<>();
        for (String n : nodes) if (inDegree.get(n) == 0) ready.offer(n);

        List<String> result = new ArrayList<>();
        while (!ready.isEmpty()) {
            String n = ready.poll();
            result.add(n);
            for (String dependent : reverse.get(n)) {
                inDegree.merge(dependent, -1, Integer::sum);
                if (inDegree.get(dependent) == 0) ready.offer(dependent);
            }
        }

        if (result.size() != nodes.size()) {
            List<String> cycle = findCycle();
            throw new CyclicDependencyException(cycle);
        }
        return result;
    }

    public boolean hasCycle() {
        try {
            resolve();
            return false;
        } catch (CyclicDependencyException e) {
            return true;
        }
    }

    /** 임의의 순환 경로 찾기 (DFS) */
    public List<String> findCycle() {
        Set<String> visited = new HashSet<>();
        Set<String> onStack = new HashSet<>();
        Deque<String> path = new ArrayDeque<>();

        for (String n : nodes) {
            if (!visited.contains(n)) {
                List<String> cycle = dfsCycle(n, visited, onStack, path);
                if (cycle != null) return cycle;
            }
        }
        return Collections.emptyList();
    }

    private List<String> dfsCycle(String n, Set<String> visited,
                                  Set<String> onStack, Deque<String> path) {
        visited.add(n);
        onStack.add(n);
        path.push(n);

        for (String d : deps.get(n)) {
            if (!visited.contains(d)) {
                List<String> r = dfsCycle(d, visited, onStack, path);
                if (r != null) return r;
            } else if (onStack.contains(d)) {
                List<String> cycle = new ArrayList<>();
                Iterator<String> it = path.iterator();
                while (it.hasNext()) {
                    String s = it.next();
                    cycle.add(s);
                    if (s.equals(d)) break;
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

    /** 병렬 실행 가능 그룹. group N의 모든 노드는 group 0..N-1 완료 후 동시에 실행 가능. */
    public List<List<String>> getParallelGroups() {
        Map<String, Integer> inDegree = new HashMap<>();
        for (String n : nodes) inDegree.put(n, deps.get(n).size());

        List<List<String>> groups = new ArrayList<>();
        Set<String> processed = new HashSet<>();

        while (processed.size() < nodes.size()) {
            List<String> currentGroup = new ArrayList<>();
            for (String n : nodes) {
                if (!processed.contains(n) && inDegree.get(n) == 0) {
                    currentGroup.add(n);
                }
            }
            if (currentGroup.isEmpty()) {
                throw new CyclicDependencyException(findCycle());
            }
            for (String n : currentGroup) {
                processed.add(n);
                for (String dependent : reverse.get(n)) {
                    inDegree.merge(dependent, -1, Integer::sum);
                }
            }
            // 처리된 노드의 inDegree를 효과적으로 무효화하기 위해
            for (String n : currentGroup) inDegree.put(n, -1);
            groups.add(currentGroup);
        }
        return groups;
    }

    public Set<String> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }
}
```
