# Red-Black 트리 구현에 유용한 Java API

## 📦 기본 타입/상수

### 색상 표현
```java
// 방법 1: boolean
private static final boolean RED = true;
private static final boolean BLACK = false;

// 방법 2: enum
enum Color { RED, BLACK }

// 방법 3: int
private static final int RED = 0;
private static final int BLACK = 1;
```

### Comparable 인터페이스
```java
public class RedBlackTree<K extends Comparable<K>> {
    
    private int compare(K k1, K k2) {
        return k1.compareTo(k2);
    }
}

// 또는 Comparator 지원
public class RedBlackTree<K> {
    private final Comparator<K> comparator;
    
    private int compare(K k1, K k2) {
        return comparator != null 
            ? comparator.compare(k1, k2)
            : ((Comparable<K>) k1).compareTo(k2);
    }
}
```

---

## 🔧 NIL 센티넬 패턴

### 센티넬 노드
```java
// NIL 노드: 모든 리프를 대표하는 단일 검정 노드
private final Node<K> NIL;

public RedBlackTree() {
    NIL = new Node<>(null, BLACK, null);
    NIL.left = NIL;
    NIL.right = NIL;
    NIL.parent = NIL;
    root = NIL;
}

// 장점: null 체크 불필요
// node.left가 NIL인지만 체크

// vs null 사용 시:
// if (node != null && node.left != null) ...
```

### null 대신 NIL 검사
```java
// NIL 사용
if (x.left != NIL) {
    x.left.parent = y;
}

// null 사용 (더 많은 검사 필요)
if (x != null && x.left != null) {
    x.left.parent = y;
}
```

---

## 📊 컬렉션 활용

### List (순회 결과)
```java
import java.util.ArrayList;
import java.util.List;

public List<K> inorder() {
    List<K> result = new ArrayList<>();
    inorderTraverse(root, result);
    return result;
}

private void inorderTraverse(Node<K> node, List<K> result) {
    if (node == NIL) return;
    inorderTraverse(node.left, result);
    result.add(node.key);
    inorderTraverse(node.right, result);
}
```

### Queue (레벨 순회)
```java
import java.util.LinkedList;
import java.util.Queue;

public void levelOrder() {
    if (root == NIL) return;
    
    Queue<Node<K>> queue = new LinkedList<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        Node<K> node = queue.poll();
        String color = node.color == RED ? "R" : "B";
        System.out.print(node.key + "(" + color + ") ");
        
        if (node.left != NIL) queue.offer(node.left);
        if (node.right != NIL) queue.offer(node.right);
    }
}
```

### Optional (검색 결과)
```java
import java.util.Optional;

public Optional<K> get(K key) {
    Node<K> node = searchNode(key);
    return node == NIL ? Optional.empty() : Optional.of(node.key);
}
```

---

## 🧪 테스트 관련

### AssertJ 사용
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldInsertAndSearch() {
    RedBlackTree<Integer> tree = new RedBlackTree<>();
    
    tree.insert(10);
    tree.insert(20);
    tree.insert(30);
    
    assertThat(tree.contains(10)).isTrue();
    assertThat(tree.contains(25)).isFalse();
}

@Test
void shouldMaintainRedBlackProperties() {
    RedBlackTree<Integer> tree = new RedBlackTree<>();
    
    for (int i = 1; i <= 100; i++) {
        tree.insert(i);
    }
    
    // 속성 검증
    assertThat(tree.isValidRedBlackTree()).isTrue();
    assertThat(tree.isRootBlack()).isTrue();
    assertThat(tree.noConsecutiveRed()).isTrue();
    assertThat(tree.blackHeightConsistent()).isTrue();
}

@Test
void shouldMaintainSortedOrder() {
    RedBlackTree<Integer> tree = new RedBlackTree<>();
    int[] values = {30, 10, 20, 50, 40, 60, 5, 15};
    
    for (int v : values) {
        tree.insert(v);
    }
    
    assertThat(tree.inorder()).isSorted();
}
```

### 속성 검증 메서드
```java
public boolean isValidRedBlackTree() {
    // 속성 2: 루트는 검정
    if (root.color != BLACK) return false;
    
    // 속성 4 & 5 검사
    return checkProperties(root, 0, -1);
}

private boolean checkProperties(Node<K> node, int blackCount, int expectedBlackHeight) {
    if (node == NIL) {
        // 속성 5: 모든 경로 검정 높이 동일
        if (expectedBlackHeight == -1) {
            return true;  // 첫 번째 리프, 기준 설정
        }
        return blackCount == expectedBlackHeight;
    }
    
    // 속성 4: 빨강 노드의 자식은 검정
    if (node.color == RED) {
        if (node.left.color == RED || node.right.color == RED) {
            return false;
        }
    }
    
    int newBlackCount = blackCount + (node.color == BLACK ? 1 : 0);
    
    // 양쪽 서브트리 검사
    // 첫 번째 리프에서 expectedBlackHeight 결정
    // (구현 복잡성으로 간략화)
    return checkProperties(node.left, newBlackCount, expectedBlackHeight) &&
           checkProperties(node.right, newBlackCount, expectedBlackHeight);
}

// 간단한 검정 높이 계산
public int getBlackHeight() {
    int height = 0;
    Node<K> node = root;
    while (node != NIL) {
        if (node.color == BLACK) height++;
        node = node.left;
    }
    return height;
}
```

---

## 📚 Java 21 관련

### Record로 결과 표현
```java
public record NodeInfo<K>(K key, boolean isRed, int depth) {}

public List<NodeInfo<K>> getNodesWithInfo() {
    List<NodeInfo<K>> result = new ArrayList<>();
    collectNodeInfo(root, 0, result);
    return result;
}

private void collectNodeInfo(Node<K> node, int depth, List<NodeInfo<K>> result) {
    if (node == NIL) return;
    result.add(new NodeInfo<>(node.key, node.color == RED, depth));
    collectNodeInfo(node.left, depth + 1, result);
    collectNodeInfo(node.right, depth + 1, result);
}
```

### Pattern Matching
```java
sealed interface TreeResult<K> permits Found, NotFound {}
record Found<K>(K key, Node<K> node) implements TreeResult<K> {}
record NotFound<K>(K searchedKey) implements TreeResult<K> {}

public TreeResult<K> searchWithResult(K key) {
    Node<K> node = searchNode(key);
    return node == NIL 
        ? new NotFound<>(key) 
        : new Found<>(key, node);
}

// 사용
switch (tree.searchWithResult(key)) {
    case Found(var k, var n) -> System.out.println("Found: " + k);
    case NotFound(var k) -> System.out.println("Not found: " + k);
}
```

### Sealed Classes (노드 타입)
```java
sealed interface RBNode<K> permits RedNode, BlackNode, NilNode {
    K key();
    RBNode<K> left();
    RBNode<K> right();
}

record RedNode<K>(K key, RBNode<K> left, RBNode<K> right) 
    implements RBNode<K> {}

record BlackNode<K>(K key, RBNode<K> left, RBNode<K> right) 
    implements RBNode<K> {}

record NilNode<K>() implements RBNode<K> {
    public K key() { return null; }
    public RBNode<K> left() { return this; }
    public RBNode<K> right() { return this; }
}
```

---

## ⚡ 성능 팁

### 1. 재귀 vs 반복
```java
// 재귀 버전 (간결)
private Node<K> searchNode(K key) {
    return searchNode(root, key);
}

private Node<K> searchNode(Node<K> node, K key) {
    if (node == NIL) return NIL;
    int cmp = key.compareTo(node.key);
    if (cmp == 0) return node;
    return cmp < 0 
        ? searchNode(node.left, key) 
        : searchNode(node.right, key);
}

// 반복 버전 (스택 오버플로우 방지)
private Node<K> searchNode(K key) {
    Node<K> node = root;
    while (node != NIL) {
        int cmp = key.compareTo(node.key);
        if (cmp == 0) return node;
        node = cmp < 0 ? node.left : node.right;
    }
    return NIL;
}
```

### 2. 비교 캐싱
```java
// 불필요한 비교 방지
int cmp = key.compareTo(node.key);
if (cmp < 0) {
    // ...
} else if (cmp > 0) {
    // ...
} else {
    // cmp == 0
}
```

---

## 🎨 시각화/디버깅

### 트리 출력
```java
public void printTree() {
    printTree(root, "", true);
}

private void printTree(Node<K> node, String prefix, boolean isTail) {
    if (node == NIL) return;
    
    String color = node.color == RED ? "R" : "B";
    System.out.println(prefix + (isTail ? "└── " : "├── ") + 
                       node.key + "(" + color + ")");
    
    String newPrefix = prefix + (isTail ? "    " : "│   ");
    
    if (node.right != NIL && node.left != NIL) {
        printTree(node.right, newPrefix, false);
        printTree(node.left, newPrefix, true);
    } else if (node.right != NIL) {
        printTree(node.right, newPrefix, true);
    } else if (node.left != NIL) {
        printTree(node.left, newPrefix, true);
    }
}

// 출력 예:
// └── 20(B)
//     ├── 30(R)
//     │   └── 25(B)
//     └── 10(R)
//         ├── 15(B)
//         └── 5(B)
```

### DOT 형식 (Graphviz)
```java
public String toDot() {
    StringBuilder sb = new StringBuilder();
    sb.append("digraph RBTree {\n");
    sb.append("  node [style=filled];\n");
    toDot(root, sb);
    sb.append("}\n");
    return sb.toString();
}

private void toDot(Node<K> node, StringBuilder sb) {
    if (node == NIL) return;
    
    String color = node.color == RED ? "red" : "black";
    String fontColor = node.color == RED ? "white" : "white";
    
    sb.append(String.format("  \"%s\" [fillcolor=%s, fontcolor=%s];\n",
        node.key, color, fontColor));
    
    if (node.left != NIL) {
        sb.append(String.format("  \"%s\" -> \"%s\";\n", node.key, node.left.key));
        toDot(node.left, sb);
    }
    if (node.right != NIL) {
        sb.append(String.format("  \"%s\" -> \"%s\";\n", node.key, node.right.key));
        toDot(node.right, sb);
    }
}
```

---

## 🔀 Java TreeMap 참고
```java
import java.util.TreeMap;
import java.util.TreeSet;

// TreeMap은 내부적으로 Red-Black 트리
TreeMap<String, Integer> map = new TreeMap<>();
map.put("apple", 1);
map.put("banana", 2);

// NavigableMap 메서드들
map.floorKey("b");      // "banana" 이하 최대
map.ceilingKey("b");    // "banana" 이상 최소
map.lowerKey("banana"); // "banana" 미만 최대
map.higherKey("apple"); // "apple" 초과 최소

// 범위 뷰
map.subMap("a", "c");   // [a, c)
map.headMap("banana");  // [처음, banana)
map.tailMap("apple");   // [apple, 끝]

// TreeSet도 동일
TreeSet<Integer> set = new TreeSet<>();
set.floor(5);
set.ceiling(5);
```
