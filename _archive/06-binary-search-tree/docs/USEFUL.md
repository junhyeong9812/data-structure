# 이진 탐색 트리 구현에 유용한 Java API

## 📦 비교 관련

### Comparable<T> 인터페이스
```java
public class Person implements Comparable<Person> {
    private String name;
    private int age;
    
    @Override
    public int compareTo(Person other) {
        // 음수: this < other
        // 0: this == other
        // 양수: this > other
        return Integer.compare(this.age, other.age);
    }
}

// 사용
int cmp = a.compareTo(b);
if (cmp < 0) { /* a < b */ }
else if (cmp > 0) { /* a > b */ }
else { /* a == b */ }
```

### Comparator<T> 인터페이스
```java
import java.util.Comparator;

// 람다로 생성
Comparator<String> byLength = (a, b) -> Integer.compare(a.length(), b.length());

// 메서드 참조
Comparator<String> natural = Comparator.naturalOrder();
Comparator<String> reverse = Comparator.reverseOrder();

// 체이닝
Comparator<Person> byAgeAndName = Comparator
    .comparingInt(Person::getAge)
    .thenComparing(Person::getName);

// null 처리
Comparator<String> nullsFirst = Comparator.nullsFirst(Comparator.naturalOrder());
```

### Integer.compare() / Double.compare()
```java
// 오버플로우 안전한 비교
int cmp = Integer.compare(a, b);  // a - b는 오버플로우 위험!

// 다양한 타입
Long.compare(a, b);
Double.compare(a, b);
Boolean.compare(a, b);
```

---

## 🔄 재귀 관련

### 꼬리 재귀 최적화
```java
// 꼬리 재귀 (Java는 최적화 안 함, 하지만 반복으로 변환 쉬움)
int factorial(int n, int acc) {
    if (n <= 1) return acc;
    return factorial(n - 1, n * acc);  // 꼬리 위치
}

// 반복으로 변환
int factorial(int n) {
    int acc = 1;
    while (n > 1) {
        acc *= n--;
    }
    return acc;
}
```

### 재귀 깊이 제한
```java
// Java 기본 스택 크기: 약 512KB ~ 1MB
// 약 10,000 ~ 20,000 호출 가능

// 깊은 재귀가 필요하면 반복으로 변환
// 또는 JVM 옵션: -Xss4m (스택 4MB)
```

---

## 📐 트리 순회용 컬렉션

### Stack (반복 순회용)
```java
import java.util.Deque;
import java.util.ArrayDeque;

// 전위 순회 (반복)
public List<Integer> preorder(Node root) {
    List<Integer> result = new ArrayList<>();
    Deque<Node> stack = new ArrayDeque<>();
    
    if (root != null) stack.push(root);
    
    while (!stack.isEmpty()) {
        Node node = stack.pop();
        result.add(node.value);
        
        // 오른쪽 먼저 push (왼쪽이 먼저 pop되도록)
        if (node.right != null) stack.push(node.right);
        if (node.left != null) stack.push(node.left);
    }
    
    return result;
}
```

### Queue (레벨 순회용)
```java
import java.util.Queue;
import java.util.LinkedList;

// 레벨 순회 (BFS)
public List<List<Integer>> levelOrder(Node root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    
    Queue<Node> queue = new LinkedList<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        List<Integer> level = new ArrayList<>();
        
        for (int i = 0; i < levelSize; i++) {
            Node node = queue.poll();
            level.add(node.value);
            
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        
        result.add(level);
    }
    
    return result;
}
```

---

## 🧮 수학 관련

### Math.max() / Math.min()
```java
// 높이 계산
int height = 1 + Math.max(height(left), height(right));

// 균형 확인
int diff = Math.abs(height(left) - height(right));
boolean balanced = diff <= 1;
```

### 로그 계산 (높이 예측)
```java
// 이상적인 BST 높이 = log2(n)
int expectedHeight = (int) (Math.log(n) / Math.log(2));

// 또는
int expectedHeight = 32 - Integer.numberOfLeadingZeros(n);
```

---

## 🛠️ 유틸리티

### Objects.requireNonNull()
```java
import java.util.Objects;

public void insert(T value) {
    Objects.requireNonNull(value, "Value cannot be null");
    root = insert(root, value);
}
```

### Optional (null 안전)
```java
import java.util.Optional;

public Optional<T> find(T value) {
    Node<T> node = findNode(root, value);
    return Optional.ofNullable(node).map(n -> n.value);
}

// 사용
bst.find(5).ifPresent(System.out::println);
T value = bst.find(5).orElse(defaultValue);
T value = bst.find(5).orElseThrow();
```

---

## 🔁 Iterator 구현

### Morris Traversal (O(1) 공간)
```java
// 스택 없이 중위 순회 (스레드 트리 활용)
public List<T> morrisInorder() {
    List<T> result = new ArrayList<>();
    Node<T> curr = root;
    
    while (curr != null) {
        if (curr.left == null) {
            result.add(curr.value);
            curr = curr.right;
        } else {
            // 왼쪽 서브트리의 가장 오른쪽 노드 찾기
            Node<T> pred = curr.left;
            while (pred.right != null && pred.right != curr) {
                pred = pred.right;
            }
            
            if (pred.right == null) {
                // 스레드 생성
                pred.right = curr;
                curr = curr.left;
            } else {
                // 스레드 제거 (복원)
                pred.right = null;
                result.add(curr.value);
                curr = curr.right;
            }
        }
    }
    
    return result;
}
```

### 스택 기반 Iterator
```java
private class InOrderIterator implements Iterator<T> {
    private Deque<Node<T>> stack = new ArrayDeque<>();
    
    InOrderIterator() {
        pushLeftPath(root);
    }
    
    private void pushLeftPath(Node<T> node) {
        while (node != null) {
            stack.push(node);
            node = node.left;
        }
    }
    
    @Override
    public boolean hasNext() {
        return !stack.isEmpty();
    }
    
    @Override
    public T next() {
        if (!hasNext()) throw new NoSuchElementException();
        
        Node<T> node = stack.pop();
        pushLeftPath(node.right);
        return node.value;
    }
}
```

---

## 🧪 테스트 관련

### 트리 검증
```java
// BST 속성 검증
public boolean isValidBST(Node<T> node, T min, T max) {
    if (node == null) return true;
    
    if (min != null && node.value.compareTo(min) <= 0) return false;
    if (max != null && node.value.compareTo(max) >= 0) return false;
    
    return isValidBST(node.left, min, node.value) &&
           isValidBST(node.right, node.value, max);
}
```

### AssertJ 사용
```java
import static org.assertj.core.api.Assertions.*;

@Test
void inorderShouldReturnSortedValues() {
    BST<Integer> bst = new BST<>();
    bst.insert(5);
    bst.insert(3);
    bst.insert(7);
    bst.insert(1);
    bst.insert(9);
    
    assertThat(bst.inorder())
        .containsExactly(1, 3, 5, 7, 9)
        .isSorted();
}

@Test
void shouldThrowOnEmptyMin() {
    BST<Integer> bst = new BST<>();
    
    assertThatThrownBy(() -> bst.min())
        .isInstanceOf(NoSuchElementException.class);
}
```

---

## 📚 Java 21 관련

### Record로 노드 정의 (불변)
```java
// 불변 트리 (함수형 스타일)
public record ImmutableNode<T>(
    T value,
    ImmutableNode<T> left,
    ImmutableNode<T> right
) {
    public ImmutableNode<T> withLeft(ImmutableNode<T> newLeft) {
        return new ImmutableNode<>(value, newLeft, right);
    }
}
```

### Pattern Matching
```java
public void printNode(Object obj) {
    if (obj instanceof Node<?> node) {
        System.out.println("Value: " + node.value);
        if (node.left != null) printNode(node.left);
        if (node.right != null) printNode(node.right);
    }
}
```

### Sealed Classes (노드 타입 제한)
```java
public sealed interface TreeNode<T>
    permits LeafNode, InternalNode {
}

public record LeafNode<T>(T value) implements TreeNode<T> {}

public record InternalNode<T>(
    T value,
    TreeNode<T> left,
    TreeNode<T> right
) implements TreeNode<T> {}
```

---

## ⚡ 성능 팁

### 1. 중복 비교 피하기
```java
// 비효율적: compareTo 여러 번 호출
if (value.compareTo(node.value) < 0) { ... }
else if (value.compareTo(node.value) > 0) { ... }

// 효율적: 한 번만 호출
int cmp = value.compareTo(node.value);
if (cmp < 0) { ... }
else if (cmp > 0) { ... }
```

### 2. 균형 유지 (실무)
```java
// 랜덤 삽입으로 균형 개선
List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);
Collections.shuffle(data);
data.forEach(bst::insert);

// 또는 정렬된 배열에서 균형 트리 생성
Node<T> sortedArrayToBST(T[] arr, int start, int end) {
    if (start > end) return null;
    int mid = (start + end) / 2;
    Node<T> node = new Node<>(arr[mid]);
    node.left = sortedArrayToBST(arr, start, mid - 1);
    node.right = sortedArrayToBST(arr, mid + 1, end);
    return node;
}
```
