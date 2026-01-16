# 의존성 해결기 풀이 해설

## 📌 핵심 아이디어

의존성 해결은 **위상 정렬**로 해결합니다.
DAG(방향 비순환 그래프)에서 의존 순서를 만족하는 선형 순서를 찾습니다.

**핵심 알고리즘**:
- Kahn's Algorithm (BFS 기반, 진입 차수 활용)
- DFS 기반 위상 정렬 (후위 순회 역순)

---

## 🔑 핵심 개념

### 1. 그래프 모델링
```
의존성 관계를 그래프로:

A depends on B  →  A ──→ B (A에서 B로 간선)

예: npm install
  next → react-dom → react
  
설치 순서 (역순):
  react → react-dom → next
```

### 2. Kahn's Algorithm 상세
```java
// 단계별 실행
초기 상태:
  app → web → database
    ↘ logging

진입 차수:
  app: 0, web: 1, logging: 1, database: 2

단계 1: app 처리 (진입 차수 0)
  result: [app]
  web: 0, logging: 0, database: 2

단계 2: web, logging 처리 (진입 차수 0)
  result: [app, web, logging] 또는 [app, logging, web]
  database: 0

단계 3: database 처리
  result: [app, web, logging, database]

역순 → 설치 순서: [database, logging, web, app]
```

### 3. 순환 탐지
```java
// DFS 기반 순환 탐지
상태: WHITE (미방문), GRAY (방문 중), BLACK (완료)

방문 중(GRAY) 노드를 다시 만나면 순환!

A → B → C → A
     ↑     │
     └─────┘  (C에서 A 방문 시 A는 GRAY → 순환!)
```

---

## 📝 POP 구현 해설

### 완전한 구현
```java
public class DependencyResolver {
    // from → [to1, to2, ...] : from이 to들에 의존
    private final Map<String, Set<String>> dependencies = new HashMap<>();
    // to → [from1, from2, ...] : to에 의존하는 노드들
    private final Map<String, Set<String>> dependents = new HashMap<>();
    private final Set<String> nodes = new HashSet<>();
    
    // 노드 추가
    public void addNode(String name) {
        if (nodes.add(name)) {
            dependencies.put(name, new HashSet<>());
            dependents.put(name, new HashSet<>());
        }
    }
    
    // 의존성 추가: from이 to에 의존
    public void addDependency(String from, String to) {
        addNode(from);
        addNode(to);
        dependencies.get(from).add(to);
        dependents.get(to).add(from);
    }
    
    // 의존성 제거
    public void removeDependency(String from, String to) {
        if (dependencies.containsKey(from)) {
            dependencies.get(from).remove(to);
        }
        if (dependents.containsKey(to)) {
            dependents.get(to).remove(from);
        }
    }
    
    // 노드 제거
    public void removeNode(String name) {
        if (!nodes.contains(name)) return;
        
        // 이 노드의 의존성 제거
        for (String dep : new HashSet<>(dependencies.get(name))) {
            dependents.get(dep).remove(name);
        }
        
        // 이 노드에 의존하는 노드들에서 제거
        for (String dependent : new HashSet<>(dependents.get(name))) {
            dependencies.get(dependent).remove(name);
        }
        
        dependencies.remove(name);
        dependents.remove(name);
        nodes.remove(name);
    }
    
    // 특정 노드의 직접 의존성
    public Set<String> getDependencies(String name) {
        return dependencies.getOrDefault(name, Set.of());
    }
    
    // 특정 노드에 의존하는 노드들
    public Set<String> getDependents(String name) {
        return dependents.getOrDefault(name, Set.of());
    }
    
    // 모든 의존성 (전이적 폐쇄)
    public Set<String> getAllDependencies(String name) {
        Set<String> result = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(name);
        
        while (!queue.isEmpty()) {
            String current = queue.poll();
            for (String dep : dependencies.getOrDefault(current, Set.of())) {
                if (result.add(dep)) {
                    queue.add(dep);
                }
            }
        }
        
        return result;
    }
    
    // Kahn's Algorithm으로 위상 정렬
    public List<String> resolve() {
        // 진입 차수 계산 (이 노드에 의존하는 노드 수)
        Map<String, Integer> inDegree = new HashMap<>();
        for (String node : nodes) {
            inDegree.put(node, dependents.get(node).size());
        }
        
        // 진입 차수 0인 노드 (아무도 의존하지 않는 노드)
        Queue<String> queue = new LinkedList<>();
        for (String node : nodes) {
            if (inDegree.get(node) == 0) {
                queue.offer(node);
            }
        }
        
        List<String> result = new ArrayList<>();
        
        while (!queue.isEmpty()) {
            String node = queue.poll();
            result.add(node);
            
            // 이 노드의 의존성들의 진입 차수 감소
            for (String dep : dependencies.get(node)) {
                inDegree.merge(dep, -1, Integer::sum);
                if (inDegree.get(dep) == 0) {
                    queue.offer(dep);
                }
            }
        }
        
        if (result.size() != nodes.size()) {
            throw new CyclicDependencyException("Cyclic dependency detected");
        }
        
        // 역순으로 반환 (의존성이 먼저 처리되도록)
        Collections.reverse(result);
        return result;
    }
    
    // 순환 존재 여부
    public boolean hasCycle() {
        try {
            resolve();
            return false;
        } catch (CyclicDependencyException e) {
            return true;
        }
    }
    
    // DFS로 순환 탐지 및 경로 찾기
    public List<String> findCycle() {
        Map<String, Integer> state = new HashMap<>();  // 0: white, 1: gray, 2: black
        Map<String, String> parent = new HashMap<>();
        
        for (String node : nodes) {
            state.put(node, 0);
        }
        
        for (String node : nodes) {
            if (state.get(node) == 0) {
                List<String> cycle = dfsForCycle(node, state, parent);
                if (cycle != null) {
                    return cycle;
                }
            }
        }
        
        return null;
    }
    
    private List<String> dfsForCycle(String node, Map<String, Integer> state, 
                                     Map<String, String> parent) {
        state.put(node, 1);  // GRAY
        
        for (String dep : dependencies.get(node)) {
            if (state.get(dep) == 1) {
                // 순환 발견! 경로 구성
                List<String> cycle = new ArrayList<>();
                String current = node;
                cycle.add(dep);
                
                while (current != null && !current.equals(dep)) {
                    cycle.add(current);
                    current = parent.get(current);
                }
                cycle.add(dep);
                Collections.reverse(cycle);
                return cycle;
            }
            
            if (state.get(dep) == 0) {
                parent.put(dep, node);
                List<String> cycle = dfsForCycle(dep, state, parent);
                if (cycle != null) return cycle;
            }
        }
        
        state.put(node, 2);  // BLACK
        return null;
    }
    
    // 병렬 실행 가능한 그룹
    public List<List<String>> getParallelGroups() {
        Map<String, Integer> inDegree = new HashMap<>();
        for (String node : nodes) {
            inDegree.put(node, dependents.get(node).size());
        }
        
        List<List<String>> groups = new ArrayList<>();
        Set<String> processed = new HashSet<>();
        
        while (processed.size() < nodes.size()) {
            List<String> currentGroup = new ArrayList<>();
            
            for (String node : nodes) {
                if (!processed.contains(node) && inDegree.get(node) == 0) {
                    currentGroup.add(node);
                }
            }
            
            if (currentGroup.isEmpty()) {
                throw new CyclicDependencyException("Cyclic dependency detected");
            }
            
            // 그룹 처리
            for (String node : currentGroup) {
                processed.add(node);
                for (String dep : dependencies.get(node)) {
                    inDegree.merge(dep, -1, Integer::sum);
                }
            }
            
            Collections.reverse(currentGroup);  // 의존성 먼저
            groups.add(currentGroup);
        }
        
        Collections.reverse(groups);
        return groups;
    }
    
    // DFS 기반 위상 정렬
    public List<String> resolveWithDFS() {
        Set<String> visited = new HashSet<>();
        Set<String> visiting = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();
        
        for (String node : nodes) {
            if (!visited.contains(node)) {
                if (!dfsTopSort(node, visited, visiting, stack)) {
                    throw new CyclicDependencyException("Cyclic dependency detected");
                }
            }
        }
        
        return new ArrayList<>(stack);
    }
    
    private boolean dfsTopSort(String node, Set<String> visited, 
                               Set<String> visiting, Deque<String> stack) {
        visiting.add(node);
        
        for (String dep : dependencies.get(node)) {
            if (visiting.contains(dep)) {
                return false;  // 순환
            }
            
            if (!visited.contains(dep)) {
                if (!dfsTopSort(dep, visited, visiting, stack)) {
                    return false;
                }
            }
        }
        
        visiting.remove(node);
        visited.add(node);
        stack.addLast(node);
        return true;
    }
    
    // 노드 수
    public int size() {
        return nodes.size();
    }
    
    // 모든 노드
    public Set<String> getNodes() {
        return Collections.unmodifiableSet(nodes);
    }
}
```

### 예외 클래스
```java
public class CyclicDependencyException extends RuntimeException {
    private List<String> cycle;
    
    public CyclicDependencyException(String message) {
        super(message);
    }
    
    public CyclicDependencyException(String message, List<String> cycle) {
        super(message);
        this.cycle = cycle;
    }
    
    public List<String> getCycle() {
        return cycle;
    }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 |
|------|-----------|
| addNode | O(1) |
| addDependency | O(1) |
| resolve (Kahn) | O(V + E) |
| resolve (DFS) | O(V + E) |
| hasCycle | O(V + E) |
| findCycle | O(V + E) |
| getParallelGroups | O(V + E) |
| getAllDependencies | O(V + E) |

V = 노드 수, E = 간선(의존성) 수

---

## ❌ 흔한 실수

### 1. 의존성 방향 혼동
```java
// 잘못됨: A가 B에 의존 = B→A 간선?
addEdge(to, from);  // 반대!

// 올바름: A가 B에 의존 = A→B 간선
// A가 먼저 처리되려면 B가 먼저 처리되어야 함
// 위상 정렬 결과: [..., B, ..., A, ...]
```

### 2. 진입 차수 vs 출력 차수
```java
// 패키지 설치 순서: 의존성 없는 것 먼저

// A → B → C 에서
// 진입 차수: A=0, B=1, C=1
// 진입 차수 0인 A 먼저? → 설치 순서가 A, B, C?
// 틀림! 설치 순서는 C, B, A (역순)

// 또는 역방향 그래프에서 출력 차수 0인 노드 먼저
```

### 3. 결과 역순 처리 누락
```java
// 잘못됨: Kahn 결과 그대로 반환
return result;  // 의존하는 노드가 먼저 나옴

// 올바름: 역순으로 반환
Collections.reverse(result);
return result;  // 의존성이 먼저 나옴
```

---

## 🔗 관련 문제

- 그래프 탐색 (BFS, DFS)
- 강연결 요소 (SCC)
- 최단 경로
- 작업 스케줄링
