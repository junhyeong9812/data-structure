# 트라이 구현에 유용한 Java API

## 📦 문자 처리

### 문자열 순회
```java
String word = "apple";

// 1. toCharArray() - 새 배열 생성
for (char c : word.toCharArray()) {
    // O(n) 추가 공간
}

// 2. charAt(i) - 인덱스 접근
for (int i = 0; i < word.length(); i++) {
    char c = word.charAt(i);
}

// 3. chars() - IntStream
word.chars().forEach(c -> {
    char ch = (char) c;
});
```

### 문자 → 인덱스 변환
```java
// 소문자 알파벳 (a-z)
int index = c - 'a';  // 0-25
char c = (char) ('a' + index);

// 대문자 알파벳 (A-Z)
int index = c - 'A';  // 0-25

// 숫자 (0-9)
int index = c - '0';  // 0-9

// 모든 ASCII
int index = (int) c;  // 0-127
```

### Character 유틸리티
```java
import java.lang.Character;

Character.isLetter(c);      // 문자인지
Character.isDigit(c);       // 숫자인지
Character.isLetterOrDigit(c);
Character.isLowerCase(c);   // 소문자인지
Character.isUpperCase(c);   // 대문자인지
Character.toLowerCase(c);   // 소문자로 변환
Character.toUpperCase(c);   // 대문자로 변환
```

---

## 🗺️ Map 관련

### HashMap (노드 자식 저장)
```java
import java.util.Map;
import java.util.HashMap;

Map<Character, TrieNode> children = new HashMap<>();

// 자식 추가
children.put('a', new TrieNode());

// 자식 조회
TrieNode child = children.get('a');  // 없으면 null

// 자식 존재 확인
if (children.containsKey('a')) { ... }

// 없으면 생성
children.computeIfAbsent('a', k -> new TrieNode());

// 자식 순회
for (Map.Entry<Character, TrieNode> entry : children.entrySet()) {
    char c = entry.getKey();
    TrieNode node = entry.getValue();
}

// 키만 순회
for (char c : children.keySet()) { ... }

// 값만 순회
for (TrieNode node : children.values()) { ... }
```

### TreeMap (정렬된 자식)
```java
import java.util.TreeMap;

// 알파벳 순서 유지 (자동완성 시 정렬된 결과)
Map<Character, TrieNode> children = new TreeMap<>();
```

---

## 📝 StringBuilder (경로 추적)

### 기본 사용
```java
StringBuilder sb = new StringBuilder();

sb.append('a');           // 문자 추가
sb.append("bc");          // 문자열 추가
sb.deleteCharAt(sb.length() - 1);  // 마지막 문자 삭제
sb.setLength(sb.length() - 1);     // 더 빠른 삭제

String result = sb.toString();     // String으로 변환
sb.setLength(0);                   // 초기화 (재사용)
```

### 백트래킹 패턴
```java
void dfs(TrieNode node, StringBuilder path, List<String> results) {
    if (node.isWord) {
        results.add(path.toString());
    }
    
    for (var entry : node.children.entrySet()) {
        path.append(entry.getKey());      // 선택
        dfs(entry.getValue(), path, results);
        path.deleteCharAt(path.length() - 1);  // 복원 (백트래킹)
    }
}
```

### String vs StringBuilder
```java
// String (불변) - 매번 새 객체 생성
String s = "";
s += "a";  // O(n) per operation

// StringBuilder (가변) - 같은 객체 수정
StringBuilder sb = new StringBuilder();
sb.append("a");  // O(1) amortized
```

---

## 📋 List (결과 저장)

### ArrayList
```java
import java.util.ArrayList;
import java.util.List;

List<String> results = new ArrayList<>();

results.add("word");
results.addAll(otherList);
results.size();
results.isEmpty();
results.get(0);
results.contains("word");

// 불변 리스트로 반환
return Collections.unmodifiableList(results);
return List.copyOf(results);
```

### 제한된 결과 수집
```java
public List<String> autocomplete(String prefix, int limit) {
    List<String> results = new ArrayList<>();
    TrieNode node = findNode(prefix);
    if (node != null) {
        collectLimited(node, new StringBuilder(prefix), results, limit);
    }
    return results;
}

void collectLimited(TrieNode node, StringBuilder sb, List<String> results, int limit) {
    if (results.size() >= limit) return;  // 조기 종료
    
    if (node.isWord) {
        results.add(sb.toString());
        if (results.size() >= limit) return;
    }
    
    for (var entry : node.children.entrySet()) {
        sb.append(entry.getKey());
        collectLimited(entry.getValue(), sb, results, limit);
        sb.deleteCharAt(sb.length() - 1);
        
        if (results.size() >= limit) return;  // 조기 종료
    }
}
```

---

## 🔁 Stack/Deque (반복 구현)

### ArrayDeque
```java
import java.util.Deque;
import java.util.ArrayDeque;

// DFS 반복 구현
Deque<Object[]> stack = new ArrayDeque<>();
stack.push(new Object[]{root, new StringBuilder()});

while (!stack.isEmpty()) {
    Object[] state = stack.pop();
    TrieNode node = (TrieNode) state[0];
    StringBuilder path = (StringBuilder) state[1];
    
    if (node.isWord) {
        results.add(path.toString());
    }
    
    for (var entry : node.children.entrySet()) {
        StringBuilder newPath = new StringBuilder(path).append(entry.getKey());
        stack.push(new Object[]{entry.getValue(), newPath});
    }
}
```

### Record로 상태 표현 (Java 14+)
```java
record State(TrieNode node, String path) {}

Deque<State> stack = new ArrayDeque<>();
stack.push(new State(root, ""));

while (!stack.isEmpty()) {
    State s = stack.pop();
    if (s.node().isWord) {
        results.add(s.path());
    }
    
    for (var entry : s.node().children.entrySet()) {
        stack.push(new State(entry.getValue(), s.path() + entry.getKey()));
    }
}
```

---

## 🧪 테스트 관련

### AssertJ 사용
```java
import static org.assertj.core.api.Assertions.*;

@Test
void searchShouldFindInsertedWord() {
    Trie trie = new Trie();
    trie.insert("apple");
    
    assertThat(trie.search("apple")).isTrue();
    assertThat(trie.search("app")).isFalse();
    assertThat(trie.startsWith("app")).isTrue();
}

@Test
void autocompleteShouldReturnMatchingWords() {
    Trie trie = new Trie();
    trie.insert("car");
    trie.insert("card");
    trie.insert("care");
    
    List<String> results = trie.autocomplete("car");
    
    assertThat(results)
        .hasSize(3)
        .contains("car", "card", "care");
}

@Test
void wildcardSearchShouldMatchPattern() {
    Trie trie = new Trie();
    trie.insert("bad");
    trie.insert("dad");
    trie.insert("mad");
    
    List<String> results = trie.searchWithWildcard(".ad");
    
    assertThat(results)
        .containsExactlyInAnyOrder("bad", "dad", "mad");
}
```

---

## 📚 Java 21 관련

### Record로 노드 정의 (불변 트라이)
```java
// 불변 트라이 노드 (함수형 스타일)
public record ImmutableTrieNode(
    Map<Character, ImmutableTrieNode> children,
    boolean isWord
) {
    public ImmutableTrieNode() {
        this(Map.of(), false);
    }
    
    public ImmutableTrieNode withChild(char c, ImmutableTrieNode child) {
        Map<Character, ImmutableTrieNode> newChildren = new HashMap<>(children);
        newChildren.put(c, child);
        return new ImmutableTrieNode(Map.copyOf(newChildren), isWord);
    }
    
    public ImmutableTrieNode asWord() {
        return new ImmutableTrieNode(children, true);
    }
}
```

### Pattern Matching
```java
public void process(Object result) {
    switch (result) {
        case String word -> System.out.println("Found: " + word);
        case List<?> list -> System.out.println("Multiple: " + list.size());
        case null -> System.out.println("Not found");
        default -> throw new IllegalArgumentException();
    }
}
```

### Stream으로 단어 수집
```java
// 트라이의 모든 단어를 Stream으로
public Stream<String> wordStream() {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED),
        false
    );
}

// 사용
long count = trie.wordStream().count();
List<String> sorted = trie.wordStream().sorted().toList();
```

---

## ⚡ 성능 팁

### 1. 배열 vs Map
```java
// 배열: 알파벳만 (빠름, 메모리 고정)
TrieNode[] children = new TrieNode[26];
// 접근: O(1), 공간: 26 포인터/노드

// Map: 모든 문자 (느림, 메모리 유연)
Map<Character, TrieNode> children = new HashMap<>();
// 접근: O(1) 평균, 공간: 실제 자식 수만큼
```

### 2. StringBuilder 재사용
```java
// 비효율: 매번 새로 생성
String path = prefix + c;  // 새 String 생성

// 효율: StringBuilder 재사용
sb.append(c);
// ... 작업 ...
sb.deleteCharAt(sb.length() - 1);
```

### 3. 조기 종료
```java
// 자동완성 결과 제한
if (results.size() >= limit) return;

// 와일드카드 검색 최적화
if (pattern.indexOf('.') == -1) {
    // 와일드카드 없으면 직접 검색
    return search(pattern) ? List.of(pattern) : List.of();
}
```

### 4. 압축 트라이 (Radix Tree)
```java
// 일반 트라이: 각 문자가 노드
// a -> p -> p -> l -> e

// 압축 트라이: 분기점만 노드
// "apple" (단일 노드)

// 공통 접두사가 많을 때 공간 절약
class RadixNode {
    String edge;  // 간선에 문자열 저장
    Map<Character, RadixNode> children;
}
```
