# B-트리 구현에 유용한 Java API

## 📦 배열 조작

### Arrays 클래스
```java
import java.util.Arrays;

// 배열 복사
int[] keys = new int[2 * t - 1];
int[] newKeys = Arrays.copyOf(keys, keys.length);
int[] range = Arrays.copyOfRange(keys, start, end);

// 배열 이동 (삽입/삭제 시)
System.arraycopy(src, srcPos, dest, destPos, length);

// 예: 키 삽입을 위해 오른쪽으로 이동
System.arraycopy(keys, i, keys, i + 1, n - i);
keys[i] = newKey;

// 배열 출력 (디버깅)
System.out.println(Arrays.toString(keys));

// 배열 정렬 (필요시)
Arrays.sort(keys, 0, n);

// 이진 탐색
int idx = Arrays.binarySearch(keys, 0, n, target);
// idx >= 0: 발견, 인덱스 반환
// idx < 0: -(insertion point) - 1
```

### 제네릭 배열
```java
// 제네릭 배열 생성 (캐스팅 필요)
@SuppressWarnings("unchecked")
K[] keys = (K[]) new Comparable[2 * t - 1];

@SuppressWarnings("unchecked")
BTreeNode<K, V>[] children = new BTreeNode[2 * t];
```

---

## 🔢 정수/비교 연산

### Integer 클래스
```java
// 비교
Integer.compare(a, b);  // a < b: -1, a == b: 0, a > b: 1

// 최대/최소
Integer.MAX_VALUE;
Integer.MIN_VALUE;
```

### Comparable 인터페이스
```java
// 제네릭 B-트리용
public class BTree<K extends Comparable<K>, V> {
    
    private int compare(K k1, K k2) {
        return k1.compareTo(k2);
    }
    
    // 키 위치 찾기
    private int findKeyIndex(BTreeNode<K, V> node, K key) {
        int i = 0;
        while (i < node.n && key.compareTo(node.keys[i]) > 0) {
            i++;
        }
        return i;
    }
}
```

### Math 클래스
```java
// 트리 높이 계산
int height = (int) Math.ceil(Math.log(n + 1) / Math.log(t)) - 1;

// 최대 키 개수
int maxKeys = (int) Math.pow(2 * t, height + 1) - 1;
```

---

## 📊 컬렉션

### List (순회 결과)
```java
import java.util.ArrayList;
import java.util.List;

public List<K> toList() {
    List<K> result = new ArrayList<>();
    inorderTraverse(root, result);
    return result;
}

private void inorderTraverse(BTreeNode<K, V> node, List<K> result) {
    if (node == null) return;
    
    for (int i = 0; i < node.n; i++) {
        if (!node.leaf) {
            inorderTraverse(node.children[i], result);
        }
        result.add(node.keys[i]);
    }
    if (!node.leaf) {
        inorderTraverse(node.children[node.n], result);
    }
}
```

### Queue (레벨 순회)
```java
import java.util.LinkedList;
import java.util.Queue;

public void levelOrderTraverse() {
    if (root == null) return;
    
    Queue<BTreeNode<K, V>> queue = new LinkedList<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        int levelSize = queue.size();
        for (int i = 0; i < levelSize; i++) {
            BTreeNode<K, V> node = queue.poll();
            
            // 키 출력
            for (int j = 0; j < node.n; j++) {
                System.out.print(node.keys[j] + " ");
            }
            System.out.print("| ");
            
            // 자식 추가
            if (!node.leaf) {
                for (int j = 0; j <= node.n; j++) {
                    queue.offer(node.children[j]);
                }
            }
        }
        System.out.println();
    }
}
```

### Optional (검색 결과)
```java
import java.util.Optional;

public Optional<V> get(K key) {
    SearchResult<K, V> result = search(root, key);
    if (result == null) {
        return Optional.empty();
    }
    return Optional.of(result.value());
}

record SearchResult<K, V>(BTreeNode<K, V> node, int index, V value) {}
```

---

## 🧪 테스트 관련

### AssertJ 사용
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldInsertAndSearch() {
    BTree<Integer, String> tree = new BTree<>(2);
    
    tree.insert(10, "ten");
    tree.insert(20, "twenty");
    tree.insert(5, "five");
    
    assertThat(tree.search(10)).isTrue();
    assertThat(tree.search(15)).isFalse();
}

@Test
void shouldMaintainSortedOrder() {
    BTree<Integer, Integer> tree = new BTree<>(3);
    int[] values = {30, 10, 20, 50, 40, 60, 5, 15};
    
    for (int v : values) {
        tree.insert(v, v);
    }
    
    List<Integer> keys = tree.toList();
    assertThat(keys).isSorted();
    assertThat(keys).containsExactly(5, 10, 15, 20, 30, 40, 50, 60);
}

@Test
void shouldHandleSplit() {
    BTree<Integer, Integer> tree = new BTree<>(2);  // 최대 3키
    
    tree.insert(1, 1);
    tree.insert(2, 2);
    tree.insert(3, 3);
    assertThat(tree.getHeight()).isEqualTo(0);
    
    tree.insert(4, 4);  // 분할 발생
    assertThat(tree.getHeight()).isEqualTo(1);
}
```

### 속성 기반 테스트
```java
@Test
void shouldSatisfyBTreeProperties() {
    BTree<Integer, Integer> tree = new BTree<>(3);
    Random rand = new Random(42);
    
    for (int i = 0; i < 1000; i++) {
        tree.insert(rand.nextInt(10000), i);
    }
    
    // 속성 1: 모든 리프는 같은 깊이
    assertThat(tree.allLeavesAtSameLevel()).isTrue();
    
    // 속성 2: 모든 노드는 키 개수 제한 만족
    assertThat(tree.allNodesValidKeyCount()).isTrue();
    
    // 속성 3: 정렬 순서 유지
    assertThat(tree.toList()).isSorted();
}
```

---

## 📚 Java 21 관련

### Record로 노드/결과 표현
```java
// 검색 결과
public record SearchResult<K, V>(
    BTreeNode<K, V> node,
    int keyIndex,
    V value
) {}

// 분할 결과
public record SplitResult<K, V>(
    K promotedKey,
    BTreeNode<K, V> leftChild,
    BTreeNode<K, V> rightChild
) {}

// 삭제 컨텍스트
public record DeleteContext<K>(
    K replacementKey,
    boolean needRebalance
) {}
```

### Pattern Matching
```java
public void processNode(Object node) {
    switch (node) {
        case LeafNode<?, ?> leaf -> processLeaf(leaf);
        case InternalNode<?, ?> internal -> processInternal(internal);
        case null -> {}
        default -> throw new IllegalStateException();
    }
}
```

### Sealed Classes
```java
sealed interface BTreeNode<K extends Comparable<K>, V> 
    permits LeafNode, InternalNode {
    
    int keyCount();
    K keyAt(int index);
}

final class LeafNode<K extends Comparable<K>, V> 
    implements BTreeNode<K, V> {
    // ...
}

final class InternalNode<K extends Comparable<K>, V> 
    implements BTreeNode<K, V> {
    // ...
}
```

---

## ⚡ 성능 팁

### 1. 배열 복사 최적화
```java
// 느림: 루프
for (int j = 0; j < t - 1; j++) {
    newNode.keys[j] = fullChild.keys[j + t];
}

// 빠름: System.arraycopy
System.arraycopy(fullChild.keys, t, newNode.keys, 0, t - 1);
```

### 2. 이진 탐색 (큰 t 값에서)
```java
// 선형 탐색 (작은 t)
int i = 0;
while (i < n && key > keys[i]) i++;

// 이진 탐색 (큰 t)
int i = Arrays.binarySearch(keys, 0, n, key);
if (i < 0) i = -(i + 1);
```

### 3. 객체 풀링 (빈번한 노드 생성 시)
```java
class NodePool<K, V> {
    private final Queue<BTreeNode<K, V>> pool = new LinkedList<>();
    private final int t;
    
    BTreeNode<K, V> acquire(boolean leaf) {
        BTreeNode<K, V> node = pool.poll();
        if (node == null) {
            return new BTreeNode<>(t, leaf);
        }
        node.reset(leaf);
        return node;
    }
    
    void release(BTreeNode<K, V> node) {
        pool.offer(node);
    }
}
```

---

## 🔀 변형 구현

### B+ 트리 구조
```java
// 리프 노드만 데이터 보유
class BPlusLeafNode<K extends Comparable<K>, V> {
    K[] keys;
    V[] values;
    BPlusLeafNode<K, V> next;  // 리프 연결
}

// 내부 노드는 라우팅만
class BPlusInternalNode<K extends Comparable<K>, V> {
    K[] keys;
    BPlusNode<K, V>[] children;
}

// 범위 쿼리
public List<V> rangeQuery(K from, K to) {
    List<V> result = new ArrayList<>();
    BPlusLeafNode<K, V> leaf = findLeaf(from);
    
    while (leaf != null) {
        for (int i = 0; i < leaf.n; i++) {
            if (leaf.keys[i].compareTo(to) > 0) {
                return result;
            }
            if (leaf.keys[i].compareTo(from) >= 0) {
                result.add(leaf.values[i]);
            }
        }
        leaf = leaf.next;
    }
    return result;
}
```

### 디스크 기반 B-트리
```java
// 노드 직렬화
class DiskBTreeNode {
    long pageId;  // 디스크 페이지 번호
    
    void writeToDisk(RandomAccessFile file) throws IOException {
        file.seek(pageId * PAGE_SIZE);
        file.writeInt(n);
        file.writeBoolean(leaf);
        for (int i = 0; i < n; i++) {
            file.writeInt(keys[i]);
        }
        // ...
    }
    
    static DiskBTreeNode readFromDisk(RandomAccessFile file, long pageId) 
            throws IOException {
        file.seek(pageId * PAGE_SIZE);
        // ...
    }
}
```

---

## 🎯 시각화/디버깅

### 트리 출력
```java
public void printTree() {
    printTree(root, "", true);
}

private void printTree(BTreeNode<K, V> node, String prefix, boolean isTail) {
    StringBuilder sb = new StringBuilder();
    sb.append(prefix);
    sb.append(isTail ? "└── " : "├── ");
    sb.append("[");
    for (int i = 0; i < node.n; i++) {
        sb.append(node.keys[i]);
        if (i < node.n - 1) sb.append(", ");
    }
    sb.append("]");
    System.out.println(sb);
    
    if (!node.leaf) {
        for (int i = 0; i <= node.n; i++) {
            printTree(node.children[i], 
                prefix + (isTail ? "    " : "│   "),
                i == node.n);
        }
    }
}
```
