# 27. 의존성 해결기 (Dependency Resolver)

## 📋 문제 정의

**위상 정렬(Topological Sort)**을 기반으로 한 
의존성 해결기를 구현하세요.

의존성 해결기는 패키지 매니저, 빌드 시스템, 작업 스케줄러 등에서
의존 관계를 분석하고 올바른 실행/설치 순서를 결정합니다.

---

## 🎯 학습 목표

- 방향 그래프 (Directed Graph)
- 위상 정렬 알고리즘 (Kahn's, DFS)
- 순환 의존성 탐지
- 의존성 그래프 구축
- 버전 충돌 해결

---

## 📝 요구사항

### 핵심 개념

| 개념 | 설명 |
|------|------|
| **Dependency** | A가 B에 의존 = B가 먼저 처리되어야 함 |
| **Topological Sort** | 의존 순서를 만족하는 선형 순서 |
| **Cycle** | 순환 의존 = A→B→C→A (해결 불가) |
| **DAG** | 순환이 없는 방향 그래프 |

### 기본 연산

| 메서드 | 설명 |
|--------|------|
| `addNode(name)` | 노드 추가 |
| `addDependency(from, to)` | 의존성 추가 (from → to) |
| `resolve()` | 의존성 해결 순서 반환 |
| `hasCycle()` | 순환 의존성 존재 여부 |
| `getDependencies(name)` | 특정 노드의 의존성 목록 |

### 고급 기능

| 기능 | 설명 |
|------|------|
| 순환 탐지 | 순환 경로 식별 및 보고 |
| 버전 관리 | 버전 제약 조건 처리 |
| 병렬 실행 | 병렬 가능한 그룹 식별 |
| 역의존성 | 특정 노드에 의존하는 노드 찾기 |

---

## 📊 입출력 예시

### 예제 1: 기본 사용
```java
DependencyResolver resolver = new DependencyResolver();

// 패키지 추가
resolver.addNode("app");
resolver.addNode("web");
resolver.addNode("database");
resolver.addNode("logging");

// 의존성 추가: app → web → database
//                 ↘ logging
resolver.addDependency("app", "web");
resolver.addDependency("app", "logging");
resolver.addDependency("web", "database");

// 의존성 해결 (설치 순서)
List<String> order = resolver.resolve();
// ["database", "logging", "web", "app"]
// 또는 ["logging", "database", "web", "app"]
```

### 예제 2: 의존성 그래프
```
app ──→ web ──→ database
  │              ↑
  └──→ logging ──┘

해결 순서:
1. database (의존성 없음)
2. logging (database 의존성 해결됨)
3. web (database 의존성 해결됨)
4. app (web, logging 의존성 해결됨)
```

### 예제 3: 순환 의존성 탐지
```java
DependencyResolver resolver = new DependencyResolver();

resolver.addNode("A");
resolver.addNode("B");
resolver.addNode("C");

resolver.addDependency("A", "B");
resolver.addDependency("B", "C");
resolver.addDependency("C", "A");  // 순환!

boolean hasCycle = resolver.hasCycle();  // true

List<String> cycle = resolver.findCycle();
// ["A", "B", "C", "A"]
```

### 예제 4: 병렬 실행 그룹
```java
// 동시에 실행 가능한 그룹 식별
List<List<String>> parallelGroups = resolver.getParallelGroups();

// 예: [["database", "logging"], ["web"], ["app"]]
// 그룹 1: database, logging 동시 실행 가능
// 그룹 2: web (그룹 1 완료 후)
// 그룹 3: app (그룹 2 완료 후)
```

### 예제 5: npm/Maven 스타일
```java
DependencyResolver resolver = new DependencyResolver();

// package.json 스타일
resolver.addPackage("react", "18.2.0");
resolver.addPackage("react-dom", "18.2.0", List.of("react@>=17.0.0"));
resolver.addPackage("next", "14.0.0", List.of("react@>=18.0.0", "react-dom@>=18.0.0"));

// 설치 순서 결정
List<Package> installOrder = resolver.resolveWithVersions();
// [react@18.2.0, react-dom@18.2.0, next@14.0.0]
```

---

## 🔍 핵심 개념

### 위상 정렬 (Kahn's Algorithm)
```java
// 진입 차수 기반 알고리즘
1. 모든 노드의 진입 차수(incoming edges) 계산
2. 진입 차수가 0인 노드를 큐에 추가
3. 큐에서 노드를 꺼내 결과에 추가
4. 해당 노드의 모든 나가는 간선 제거 (연결된 노드의 진입 차수 감소)
5. 진입 차수가 0이 된 노드를 큐에 추가
6. 큐가 빌 때까지 반복
7. 모든 노드가 처리되지 않으면 순환 존재
```

### 위상 정렬 (DFS 기반)
```java
// 후위 순회 역순
1. 방문하지 않은 노드에서 DFS 시작
2. 모든 인접 노드 방문 후 스택에 push
3. 방문 중 이미 스택에 있는 노드 만나면 순환
4. 모든 노드 처리 후 스택을 역순으로 출력
```

### 그래프 표현
```java
// 인접 리스트
Map<String, List<String>> graph;
// "app" → ["web", "logging"]
// "web" → ["database"]

// 역방향 그래프 (역의존성 조회용)
Map<String, List<String>> reverseGraph;
// "database" → ["web", "logging"]
// "web" → ["app"]
```

---

## 💡 힌트

### 기본 구조
```java
public class DependencyResolver {
    private final Map<String, Set<String>> dependencies = new HashMap<>();
    private final Set<String> nodes = new HashSet<>();
    
    public void addNode(String name) {
        nodes.add(name);
        dependencies.putIfAbsent(name, new HashSet<>());
    }
    
    public void addDependency(String from, String to) {
        addNode(from);
        addNode(to);
        dependencies.get(from).add(to);
    }
}
```

### Kahn's Algorithm
```java
public List<String> resolve() {
    // 진입 차수 계산
    Map<String, Integer> inDegree = new HashMap<>();
    for (String node : nodes) {
        inDegree.put(node, 0);
    }
    
    for (String node : nodes) {
        for (String dep : dependencies.get(node)) {
            inDegree.merge(dep, 1, Integer::sum);
        }
    }
    
    // 진입 차수 0인 노드부터 시작
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
        
        for (String dependent : getDependents(node)) {
            inDegree.merge(dependent, -1, Integer::sum);
            if (inDegree.get(dependent) == 0) {
                queue.offer(dependent);
            }
        }
    }
    
    if (result.size() != nodes.size()) {
        throw new CyclicDependencyException("Cycle detected");
    }
    
    return result;
}
```

---

## ✅ 체크리스트

- [ ] 노드/의존성 추가
- [ ] 위상 정렬 (Kahn's)
- [ ] 순환 탐지
- [ ] 순환 경로 찾기
- [ ] DFS 기반 위상 정렬
- [ ] 역의존성 조회
- [ ] 병렬 실행 그룹
- [ ] 버전 제약 (선택)

---

## 📚 참고

- npm, Maven, Gradle 의존성 관리
- DAG (Directed Acyclic Graph)
- Kahn's Algorithm
- 강연결 요소 (SCC)
