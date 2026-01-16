# 이진 탐색 트리 풀이 해설

## 📌 핵심 아이디어

BST는 **왼쪽 < 루트 < 오른쪽** 속성을 만족하는 이진 트리입니다.
이 속성 덕분에 이진 탐색처럼 평균 O(log n) 시간에 검색이 가능합니다.

---

## 🔑 핵심 개념

### 1. BST 속성
```
        8
       / \
      3   10
     / \    \
    1   6    14
       / \   /
      4   7 13

모든 노드에서:
- 왼쪽 서브트리의 모든 값 < 현재 노드 값
- 오른쪽 서브트리의 모든 값 > 현재 노드 값
```

### 2. 순회 방식
```
       5
      / \
     3   7
    / \
   1   4

전위(Preorder):  5 → 3 → 1 → 4 → 7  (루트 먼저)
중위(Inorder):   1 → 3 → 4 → 5 → 7  (정렬된 순서!)
후위(Postorder): 1 → 4 → 3 → 7 → 5  (루트 마지막)
레벨(Level):     5 → 3 → 7 → 1 → 4  (위에서 아래로)
```

### 3. 삭제의 3가지 케이스
```
Case 1: 리프 노드 삭제
    5           5
   / \    →   / \
  3   7      3   7
 /
1 ← 삭제

Case 2: 자식 하나인 노드 삭제
    5           5
   / \    →   / \
  3   7      1   7
 /    
1 ← 3 삭제, 1이 승계

Case 3: 자식 둘인 노드 삭제
    5           6
   / \    →   / \
  3   7      3   7
     /
    6 ← 5 삭제, 후계자 6이 대체
```

---

## 📝 POP 구현 해설
```java
public class BinarySearchTree {
    private Node root;
    private int size;
    
    static class Node {
        int value;
        Node left, right;
        
        Node(int value) {
            this.value = value;
        }
    }
    
    // 삽입 (재귀)
    public void insert(int value) {
        root = insertRec(root, value);
    }
    
    private Node insertRec(Node node, int value) {
        if (node == null) {
            size++;
            return new Node(value);
        }
        
        if (value < node.value) {
            node.left = insertRec(node.left, value);
        } else if (value > node.value) {
            node.right = insertRec(node.right, value);
        }
        // value == node.value: 중복 무시
        
        return node;
    }
    
    // 검색
    public boolean search(int value) {
        return searchRec(root, value);
    }
    
    private boolean searchRec(Node node, int value) {
        if (node == null) return false;
        if (value == node.value) return true;
        if (value < node.value) return searchRec(node.left, value);
        return searchRec(node.right, value);
    }
    
    // 최솟값
    public int min() {
        if (root == null) throw new NoSuchElementException();
        return minNode(root).value;
    }
    
    private Node minNode(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }
    
    // 삭제
    public void delete(int value) {
        root = deleteRec(root, value);
    }
    
    private Node deleteRec(Node node, int value) {
        if (node == null) return null;
        
        if (value < node.value) {
            node.left = deleteRec(node.left, value);
        } else if (value > node.value) {
            node.right = deleteRec(node.right, value);
        } else {
            // 삭제할 노드 찾음
            size--;
            
            // Case 1 & 2: 자식 0개 또는 1개
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            
            // Case 3: 자식 2개 → 후계자로 대체
            Node successor = minNode(node.right);
            node.value = successor.value;
            node.right = deleteRec(node.right, successor.value);
            size++;  // deleteRec에서 감소되므로 복구
        }
        
        return node;
    }
    
    // 중위 순회 (정렬된 순서)
    public List<Integer> inorder() {
        List<Integer> result = new ArrayList<>();
        inorderRec(root, result);
        return result;
    }
    
    private void inorderRec(Node node, List<Integer> result) {
        if (node == null) return;
        inorderRec(node.left, result);
        result.add(node.value);
        inorderRec(node.right, result);
    }
    
    // 높이
    public int height() {
        return heightRec(root);
    }
    
    private int heightRec(Node node) {
        if (node == null) return -1;  // 빈 트리는 -1
        return 1 + Math.max(heightRec(node.left), heightRec(node.right));
    }
    
    // Floor: value 이하의 최댓값
    public Integer floor(int value) {
        Node result = floorRec(root, value);
        return result == null ? null : result.value;
    }
    
    private Node floorRec(Node node, int value) {
        if (node == null) return null;
        
        if (value == node.value) return node;
        if (value < node.value) return floorRec(node.left, value);
        
        // value > node.value: 오른쪽에서 더 가까운 값 찾기
        Node right = floorRec(node.right, value);
        return right != null ? right : node;
    }
    
    // 레벨 순회 (BFS)
    public List<Integer> levelorder() {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;
        
        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            result.add(node.value);
            
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        
        return result;
    }
}
```

---

## 📝 OOP 구현 해설
```java
public class BinarySearchTree<T extends Comparable<T>> implements Iterable<T> {
    private Node<T> root;
    private int size;
    
    private static class Node<T> {
        T value;
        Node<T> left, right;
        int subtreeSize;  // 서브트리 크기 (rank/select용)
        
        Node(T value) {
            this.value = value;
            this.subtreeSize = 1;
        }
    }
    
    public void insert(T value) {
        Objects.requireNonNull(value);
        root = insert(root, value);
    }
    
    private Node<T> insert(Node<T> node, T value) {
        if (node == null) {
            size++;
            return new Node<>(value);
        }
        
        int cmp = value.compareTo(node.value);
        if (cmp < 0) {
            node.left = insert(node.left, value);
        } else if (cmp > 0) {
            node.right = insert(node.right, value);
        }
        
        node.subtreeSize = 1 + subtreeSize(node.left) + subtreeSize(node.right);
        return node;
    }
    
    private int subtreeSize(Node<T> node) {
        return node == null ? 0 : node.subtreeSize;
    }
    
    // rank: value보다 작은 키의 개수
    public int rank(T value) {
        return rank(root, value);
    }
    
    private int rank(Node<T> node, T value) {
        if (node == null) return 0;
        
        int cmp = value.compareTo(node.value);
        if (cmp < 0) {
            return rank(node.left, value);
        } else if (cmp > 0) {
            return 1 + subtreeSize(node.left) + rank(node.right, value);
        } else {
            return subtreeSize(node.left);
        }
    }
    
    // select: k번째로 작은 값 (0-indexed)
    public T select(int k) {
        if (k < 0 || k >= size) throw new IllegalArgumentException();
        return select(root, k).value;
    }
    
    private Node<T> select(Node<T> node, int k) {
        int leftSize = subtreeSize(node.left);
        
        if (k < leftSize) {
            return select(node.left, k);
        } else if (k > leftSize) {
            return select(node.right, k - leftSize - 1);
        } else {
            return node;
        }
    }
    
    // Iterator (중위 순회)
    @Override
    public Iterator<T> iterator() {
        return new BSTIterator();
    }
    
    private class BSTIterator implements Iterator<T> {
        private Deque<Node<T>> stack = new ArrayDeque<>();
        
        BSTIterator() {
            pushLeft(root);
        }
        
        private void pushLeft(Node<T> node) {
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
            pushLeft(node.right);
            return node.value;
        }
    }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 평균 | 최악 (편향) | 설명 |
|------|------|------------|------|
| insert | O(log n) | O(n) | 높이만큼 내려감 |
| search | O(log n) | O(n) | 높이만큼 탐색 |
| delete | O(log n) | O(n) | 높이만큼 + 후계자 찾기 |
| min/max | O(log n) | O(n) | 끝까지 내려감 |
| floor/ceiling | O(log n) | O(n) | 높이만큼 |
| rank/select | O(log n) | O(n) | 서브트리 크기 활용 |
| inorder | O(n) | O(n) | 모든 노드 방문 |

### 공간 복잡도
- 트리 자체: O(n)
- 재귀 호출 스택: O(h) where h = 높이
- 최악의 경우 h = n (편향 트리)

---

## ❌ 흔한 실수

### 1. 삭제 시 후계자 처리
```java
// 잘못됨: 후계자 값만 복사하고 후계자 노드 삭제 안 함
node.value = successor.value;
// successor 노드를 삭제해야 함!

// 올바름
node.value = successor.value;
node.right = deleteRec(node.right, successor.value);
```

### 2. null 체크 누락
```java
// 잘못됨
public int min() {
    return minNode(root).value;  // root가 null이면 NPE!
}

// 올바름
public int min() {
    if (root == null) throw new NoSuchElementException();
    return minNode(root).value;
}
```

### 3. 중복 처리
```java
// 중복 허용 안 함 (Set 시맨틱)
if (cmp == 0) return node;  // 그냥 반환

// 중복 허용 (Multiset 시맨틱)
if (cmp == 0) {
    node.count++;  // 또는 오른쪽에 삽입
}
```

### 4. 편향 트리 성능
```java
// 정렬된 데이터 삽입 시 편향 발생
for (int i = 1; i <= 10000; i++) {
    bst.insert(i);  // O(n^2) 총 시간!
}
// 해결: 셔플 후 삽입 또는 균형 트리 사용
```

---

## 🔗 관련 문제

- LeetCode 98: Validate Binary Search Tree
- LeetCode 700: Search in a BST
- LeetCode 701: Insert into a BST
- LeetCode 450: Delete Node in a BST
- LeetCode 230: Kth Smallest Element in a BST
- LeetCode 235: Lowest Common Ancestor of a BST
- LeetCode 108: Convert Sorted Array to BST
