# 의존성 해결기 구현에 유용한 Java API

## 📦 컬렉션

### Map
```java
import java.util.HashMap;
import java.util.Map;

// 그래프 표현 (인접 리스트)
Map<String, Set<String>> graph = new HashMap<>();

// 노드 추가
graph.put(name, new HashSet<>());

// 간선 추가
graph.get(from).add(to);

// 없으면 기본값 반환
Set<String> deps = graph.getOrDefault(name, Set.of());

// 없으면 생성
graph.computeIfAbsent(name, k -> new HashSet<>()).add(dep);

// 값 변경
graph.merge(name, 1, Integer::sum);  // 진입 차수 증가
graph.merge(name, -1, Integer::sum);  // 진입 차수 감소
```

### Set
```java
import java.util.HashSet;
import java.util.Set;

Set<String> visited = new HashSet<>();

// 추가 (이미 존재하면 false)
if (visited.add(node)) {
    // 새로 추가됨
}

// 존재 여부
visited.contains(node);

// 복사 (수정해도 원본 영향 없음)
for (String dep : new HashSet<>(dependencies.get(node))) {
    // ...
}

// 불변 Set
return Collections.unmodifiableSet(nodes);
```

### Queue
```java
import java.util.LinkedList;
import java.util.Queue;

// BFS용 큐
Queue<String> queue = new LinkedList<>();

queue.offer(node);  // 추가
String node = queue.poll();  // 제거 및 반환
boolean isEmpty = queue.isEmpty();
```

### Deque (스택 대용)
```java
import java.util.ArrayDeque;
import java.util.Deque;

// DFS용 스택
Deque<String> stack = new ArrayDeque<>();

stack.push(node);  // 스택 push
stack.addLast(node);  // 큐처럼 뒤에 추가

String node = stack.pop();  // 스택 pop
String node = stack.pollFirst();  // 큐처럼 앞에서 제거
```

### List 역순
```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

List<String> result = new ArrayList<>();
// ... 채우기

// 제자리 역순
Collections.reverse(result);

// 새 리스트로 역순 (원본 유지)
List<String> reversed = new ArrayList<>(result);
Collections.reverse(reversed);

// Stream으로 (Java 21)
List<String> reversed = result.reversed();
```

---

## 🔄 그래프 알고리즘

### BFS 템플릿
```java
public List<String> bfs(String start) {
    List<String> result = new ArrayList<>();
    Set<String> visited = new HashSet<>();
    Queue<String> queue = new LinkedList<>();
    
    queue.offer(start);
    visited.add(start);
    
    while (!queue.isEmpty()) {
        String node = queue.poll();
        result.add(node);
        
        for (String neighbor : graph.get(node)) {
            if (visited.add(neighbor)) {
                queue.offer(neighbor);
            }
        }
    }
    
    return result;
}
```

### DFS 템플릿
```java
// 재귀 버전
public void dfs(String node, Set<String> visited) {
    if (!visited.add(node)) return;
    
    // 전처리
    System.out.println("방문: " + node);
    
    for (String neighbor : graph.get(node)) {
        dfs(neighbor, visited);
    }
    
    // 후처리
    System.out.println("완료: " + node);
}

// 반복 버전
public List<String> dfsIterative(String start) {
    List<String> result = new ArrayList<>();
    Set<String> visited = new HashSet<>();
    Deque<String> stack = new ArrayDeque<>();
    
    stack.push(start);
    
    while (!stack.isEmpty()) {
        String node = stack.pop();
        
        if (visited.add(node)) {
            result.add(node);
            
            for (String neighbor : graph.get(node)) {
                if (!visited.contains(neighbor)) {
                    stack.push(neighbor);
                }
            }
        }
    }
    
    return result;
}
```

### 순환 탐지 (3-색 DFS)
```java
enum Color { WHITE, GRAY, BLACK }

public boolean hasCycle() {
    Map<String, Color> color = new HashMap<>();
    for (String node : nodes) {
        color.put(node, Color.WHITE);
    }
    
    for (String node : nodes) {
        if (color.get(node) == Color.WHITE) {
            if (dfsHasCycle(node, color)) {
                return true;
            }
        }
    }
    
    return false;
}

private boolean dfsHasCycle(String node, Map<String, Color> color) {
    color.put(node, Color.GRAY);
    
    for (String neighbor : graph.get(node)) {
        if (color.get(neighbor) == Color.GRAY) {
            return true;  // 순환!
        }
        
        if (color.get(neighbor) == Color.WHITE) {
            if (dfsHasCycle(neighbor, color)) {
                return true;
            }
        }
    }
    
    color.put(node, Color.BLACK);
    return false;
}
```

---

## 🧪 테스트

### AssertJ
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldResolveSimpleDependency() {
    DependencyResolver resolver = new DependencyResolver();
    
    resolver.addNode("A");
    resolver.addNode("B");
    resolver.addDependency("A", "B");  // A depends on B
    
    List<String> order = resolver.resolve();
    
    // B가 A보다 먼저
    assertThat(order.indexOf("B")).isLessThan(order.indexOf("A"));
}

@Test
void shouldDetectCycle() {
    DependencyResolver resolver = new DependencyResolver();
    
    resolver.addNode("A");
    resolver.addNode("B");
    resolver.addNode("C");
    resolver.addDependency("A", "B");
    resolver.addDependency("B", "C");
    resolver.addDependency("C", "A");
    
    assertThat(resolver.hasCycle()).isTrue();
    
    List<String> cycle = resolver.findCycle();
    assertThat(cycle).containsAll(List.of("A", "B", "C"));
}

@Test
void shouldHandleComplexDependencies() {
    DependencyResolver resolver = new DependencyResolver();
    
    // Diamond dependency: D depends on B,C; B,C depend on A
    resolver.addDependency("D", "B");
    resolver.addDependency("D", "C");
    resolver.addDependency("B", "A");
    resolver.addDependency("C", "A");
    
    List<String> order = resolver.resolve();
    
    // A가 B,C보다 먼저, B,C가 D보다 먼저
    int aIdx = order.indexOf("A");
    int bIdx = order.indexOf("B");
    int cIdx = order.indexOf("C");
    int dIdx = order.indexOf("D");
    
    assertThat(aIdx).isLessThan(bIdx).isLessThan(dIdx);
    assertThat(aIdx).isLessThan(cIdx).isLessThan(dIdx);
}

@Test
void shouldGetParallelGroups() {
    DependencyResolver resolver = new DependencyResolver();
    
    resolver.addDependency("app", "web");
    resolver.addDependency("app", "logging");
    resolver.addDependency("web", "database");
    resolver.addDependency("logging", "database");
    
    List<List<String>> groups = resolver.getParallelGroups();
    
    // [database], [web, logging], [app]
    assertThat(groups).hasSize(3);
    assertThat(groups.get(0)).containsExactly("database");
    assertThat(groups.get(1)).containsExactlyInAnyOrder("web", "logging");
    assertThat(groups.get(2)).containsExactly("app");
}
```

---

## 📚 Java 21 관련

### Record
```java
// 의존성 정보
public record Dependency(String from, String to) {}

// 노드 정보
public record Node(String name, Set<String> dependencies) {}

// 해결 결과
public record Resolution(
    List<String> order,
    List<List<String>> parallelGroups,
    boolean hasCycle
) {}
```

### Pattern Matching
```java
public void process(Object input) {
    switch (input) {
        case String s -> addNode(s);
        case Dependency d -> addDependency(d.from(), d.to());
        case List<?> list -> list.forEach(this::process);
        default -> throw new IllegalArgumentException();
    }
}
```

### Sequenced Collections
```java
// 역순 리스트 (Java 21)
List<String> reversed = result.reversed();

// 첫 번째/마지막 요소
String first = result.getFirst();
String last = result.getLast();
```

---

## ⚡ 성능 팁

### 1. Set 사용
```java
// 느림: List로 중복 체크
List<String> visited = new ArrayList<>();
if (!visited.contains(node)) ...  // O(n)

// 빠름: Set으로 중복 체크
Set<String> visited = new HashSet<>();
if (visited.add(node)) ...  // O(1)
```

### 2. 진입 차수 미리 계산
```java
// 느림: 매번 계산
for (String node : nodes) {
    int degree = 0;
    for (String other : nodes) {
        if (dependencies.get(other).contains(node)) {
            degree++;
        }
    }
}

// 빠름: 역방향 그래프 유지
Map<String, Set<String>> dependents;  // 역방향
int degree = dependents.get(node).size();  // O(1)
```

### 3. 불필요한 복사 방지
```java
// 느림: 매번 복사
for (String dep : new ArrayList<>(dependencies.get(node))) {
    // 수정 없으면 불필요
}

// 빠름: 직접 순회 (수정 없을 때)
for (String dep : dependencies.get(node)) {
    // ...
}

// 수정 필요할 때만 복사
Set<String> deps = new HashSet<>(dependencies.get(node));
for (String dep : deps) {
    removeDependency(node, dep);
}
```

---

## 🔀 예외 처리
```java
// 순환 의존성 예외
public class CyclicDependencyException extends RuntimeException {
    private final List<String> cycle;
    
    public CyclicDependencyException(String message, List<String> cycle) {
        super(message + ": " + String.join(" → ", cycle));
        this.cycle = cycle;
    }
    
    public List<String> getCycle() {
        return Collections.unmodifiableList(cycle);
    }
}

// 노드 없음 예외
public class NodeNotFoundException extends RuntimeException {
    public NodeNotFoundException(String name) {
        super("Node not found: " + name);
    }
}

// 사용
public List<String> resolve() {
    // ...
    if (result.size() != nodes.size()) {
        List<String> cycle = findCycle();
        throw new CyclicDependencyException("Cyclic dependency", cycle);
    }
    return result;
}
```

---

## 📊 시각화 (디버깅용)
```java
// Graphviz DOT 형식 출력
public String toDot() {
    StringBuilder sb = new StringBuilder();
    sb.append("digraph Dependencies {\n");
    
    for (String node : nodes) {
        sb.append("  \"").append(node).append("\";\n");
        
        for (String dep : dependencies.get(node)) {
            sb.append("  \"").append(node)
              .append("\" -> \"").append(dep).append("\";\n");
        }
    }
    
    sb.append("}\n");
    return sb.toString();
}

// 출력 예:
// digraph Dependencies {
//   "app";
//   "app" -> "web";
//   "app" -> "logging";
//   "web";
//   "web" -> "database";
// }
```
