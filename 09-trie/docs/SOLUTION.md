# 트라이 풀이 해설

## 📌 핵심 아이디어

트라이는 문자열을 문자 단위로 트리에 저장하여
**접두사 기반 검색**을 O(m) 시간에 수행합니다. (m = 문자열 길이)

---

## 🔑 핵심 개념

### 1. 트라이 구조
```
"app", "apple", "apt", "bat" 저장:

        root
        / \
       a   b
       |   |
       p   a
      / \  |
     p   t t
     |   ↓ ↓
     l  END END
     |
     e
     ↓
    END

각 경로가 하나의 접두사를 나타냄
END 표시가 있는 노드에서 단어가 끝남
```

### 2. 노드 표현
```java
// 배열 기반 (알파벳만)
class TrieNode {
    TrieNode[] children = new TrieNode[26];
    boolean isEnd = false;
}

// Map 기반 (모든 문자)
class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEnd = false;
}
```

### 3. 삽입 과정
```
"apple" 삽입:

root → 'a' 생성
     → 'p' 생성
     → 'p' 생성
     → 'l' 생성
     → 'e' 생성 (isEnd = true)
```

### 4. 검색 vs 접두사 확인
```java
// search("app"): 'a'→'p'→'p' 찾고, isEnd 확인
// startsWith("app"): 'a'→'p'→'p' 찾기만 하면 됨
```

---

## 📝 POP 구현 해설
```java
public class Trie {
    private TrieNode root;
    private int wordCount;
    
    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEndOfWord = false;
        int prefixCount = 0;  // 이 노드를 거치는 단어 수
    }
    
    public Trie() {
        root = new TrieNode();
        wordCount = 0;
    }
    
    // 삽입: O(m)
    public void insert(String word) {
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }
            current = current.children[index];
            current.prefixCount++;
        }
        if (!current.isEndOfWord) {
            wordCount++;
        }
        current.isEndOfWord = true;
    }
    
    // 검색: O(m)
    public boolean search(String word) {
        TrieNode node = findNode(word);
        return node != null && node.isEndOfWord;
    }
    
    // 접두사 확인: O(m)
    public boolean startsWith(String prefix) {
        return findNode(prefix) != null;
    }
    
    // 노드 찾기 헬퍼
    private TrieNode findNode(String s) {
        TrieNode current = root;
        for (char c : s.toCharArray()) {
            int index = c - 'a';
            if (current.children[index] == null) {
                return null;
            }
            current = current.children[index];
        }
        return current;
    }
    
    // 삭제: O(m)
    public boolean delete(String word) {
        return deleteHelper(root, word, 0);
    }
    
    private boolean deleteHelper(TrieNode node, String word, int depth) {
        if (node == null) return false;
        
        if (depth == word.length()) {
            if (!node.isEndOfWord) return false;
            node.isEndOfWord = false;
            wordCount--;
            return isEmpty(node);  // 자식이 없으면 노드 삭제 가능
        }
        
        int index = word.charAt(depth) - 'a';
        if (deleteHelper(node.children[index], word, depth + 1)) {
            node.children[index] = null;
            node.prefixCount--;
            return !node.isEndOfWord && isEmpty(node);
        }
        
        node.prefixCount--;
        return false;
    }
    
    private boolean isEmpty(TrieNode node) {
        for (TrieNode child : node.children) {
            if (child != null) return false;
        }
        return true;
    }
    
    // 자동완성: O(m + k) where k = 결과 단어들의 총 길이
    public List<String> autocomplete(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode node = findNode(prefix);
        if (node != null) {
            collectWords(node, new StringBuilder(prefix), results);
        }
        return results;
    }
    
    private void collectWords(TrieNode node, StringBuilder sb, List<String> results) {
        if (node.isEndOfWord) {
            results.add(sb.toString());
        }
        
        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null) {
                sb.append((char) ('a' + i));
                collectWords(node.children[i], sb, results);
                sb.deleteCharAt(sb.length() - 1);  // 백트래킹
            }
        }
    }
    
    // 자동완성 (제한): O(m + limit)
    public List<String> autocomplete(String prefix, int limit) {
        List<String> results = new ArrayList<>();
        TrieNode node = findNode(prefix);
        if (node != null) {
            collectWordsLimited(node, new StringBuilder(prefix), results, limit);
        }
        return results;
    }
    
    private void collectWordsLimited(TrieNode node, StringBuilder sb, 
                                     List<String> results, int limit) {
        if (results.size() >= limit) return;
        
        if (node.isEndOfWord) {
            results.add(sb.toString());
        }
        
        for (int i = 0; i < 26 && results.size() < limit; i++) {
            if (node.children[i] != null) {
                sb.append((char) ('a' + i));
                collectWordsLimited(node.children[i], sb, results, limit);
                sb.deleteCharAt(sb.length() - 1);
            }
        }
    }
    
    // 와일드카드 검색 ('.'은 아무 문자나 매칭)
    public List<String> searchWithWildcard(String pattern) {
        List<String> results = new ArrayList<>();
        searchWildcardHelper(root, pattern, 0, new StringBuilder(), results);
        return results;
    }
    
    private void searchWildcardHelper(TrieNode node, String pattern, int index,
                                      StringBuilder sb, List<String> results) {
        if (node == null) return;
        
        if (index == pattern.length()) {
            if (node.isEndOfWord) {
                results.add(sb.toString());
            }
            return;
        }
        
        char c = pattern.charAt(index);
        
        if (c == '.') {
            // 모든 자식 탐색
            for (int i = 0; i < 26; i++) {
                if (node.children[i] != null) {
                    sb.append((char) ('a' + i));
                    searchWildcardHelper(node.children[i], pattern, index + 1, sb, results);
                    sb.deleteCharAt(sb.length() - 1);
                }
            }
        } else {
            int idx = c - 'a';
            if (node.children[idx] != null) {
                sb.append(c);
                searchWildcardHelper(node.children[idx], pattern, index + 1, sb, results);
                sb.deleteCharAt(sb.length() - 1);
            }
        }
    }
    
    // 최장 공통 접두사
    public String longestCommonPrefix() {
        StringBuilder sb = new StringBuilder();
        TrieNode current = root;
        
        while (current != null) {
            int childCount = 0;
            int nextIndex = -1;
            
            for (int i = 0; i < 26; i++) {
                if (current.children[i] != null) {
                    childCount++;
                    nextIndex = i;
                }
            }
            
            // 자식이 하나이고 현재 노드가 단어 끝이 아닐 때만 계속
            if (childCount == 1 && !current.isEndOfWord) {
                sb.append((char) ('a' + nextIndex));
                current = current.children[nextIndex];
            } else {
                break;
            }
        }
        
        return sb.toString();
    }
    
    // 접두사로 시작하는 단어 개수: O(m)
    public int countWordsStartingWith(String prefix) {
        TrieNode node = findNode(prefix);
        return node == null ? 0 : node.prefixCount;
    }
    
    public int size() {
        return wordCount;
    }
    
    public boolean isEmpty() {
        return wordCount == 0;
    }
}
```

---

## 📝 OOP 구현 해설
```java
public interface Trie<T> {
    void insert(T word);
    boolean search(T word);
    boolean startsWith(T prefix);
    boolean delete(T word);
    List<T> autocomplete(T prefix);
    int size();
}

public class StringTrie implements Trie<String>, Iterable<String> {
    private final TrieNode root;
    private int size;
    
    private static class TrieNode {
        private final Map<Character, TrieNode> children;
        private boolean isWord;
        private int wordCount;  // 이 단어의 중복 횟수
        
        TrieNode() {
            this.children = new HashMap<>();
            this.isWord = false;
            this.wordCount = 0;
        }
        
        TrieNode getChild(char c) {
            return children.get(c);
        }
        
        TrieNode getOrCreateChild(char c) {
            return children.computeIfAbsent(c, k -> new TrieNode());
        }
        
        boolean hasChild(char c) {
            return children.containsKey(c);
        }
        
        void removeChild(char c) {
            children.remove(c);
        }
        
        boolean hasChildren() {
            return !children.isEmpty();
        }
        
        Set<Map.Entry<Character, TrieNode>> childEntries() {
            return children.entrySet();
        }
    }
    
    public StringTrie() {
        this.root = new TrieNode();
        this.size = 0;
    }
    
    @Override
    public void insert(String word) {
        Objects.requireNonNull(word);
        if (word.isEmpty()) return;
        
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            current = current.getOrCreateChild(c);
        }
        
        if (!current.isWord) {
            size++;
        }
        current.isWord = true;
        current.wordCount++;
    }
    
    @Override
    public boolean search(String word) {
        TrieNode node = traverse(word);
        return node != null && node.isWord;
    }
    
    @Override
    public boolean startsWith(String prefix) {
        return traverse(prefix) != null;
    }
    
    private TrieNode traverse(String s) {
        TrieNode current = root;
        for (char c : s.toCharArray()) {
            current = current.getChild(c);
            if (current == null) return null;
        }
        return current;
    }
    
    @Override
    public List<String> autocomplete(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode node = traverse(prefix);
        if (node != null) {
            dfs(node, new StringBuilder(prefix), results);
        }
        return results;
    }
    
    private void dfs(TrieNode node, StringBuilder current, List<String> results) {
        if (node.isWord) {
            results.add(current.toString());
        }
        
        for (var entry : node.childEntries()) {
            current.append(entry.getKey());
            dfs(entry.getValue(), current, results);
            current.deleteCharAt(current.length() - 1);
        }
    }
    
    @Override
    public Iterator<String> iterator() {
        return new TrieIterator();
    }
    
    private class TrieIterator implements Iterator<String> {
        private final Deque<IteratorState> stack;
        private String next;
        
        private record IteratorState(
            TrieNode node,
            StringBuilder prefix,
            Iterator<Map.Entry<Character, TrieNode>> childIterator
        ) {}
        
        TrieIterator() {
            stack = new ArrayDeque<>();
            stack.push(new IteratorState(root, new StringBuilder(), 
                                        root.childEntries().iterator()));
            advance();
        }
        
        private void advance() {
            next = null;
            
            while (!stack.isEmpty() && next == null) {
                IteratorState state = stack.peek();
                
                if (state.childIterator.hasNext()) {
                    var entry = state.childIterator.next();
                    StringBuilder newPrefix = new StringBuilder(state.prefix)
                        .append(entry.getKey());
                    TrieNode child = entry.getValue();
                    
                    stack.push(new IteratorState(child, newPrefix, 
                                                child.childEntries().iterator()));
                    
                    if (child.isWord) {
                        next = newPrefix.toString();
                    }
                } else {
                    stack.pop();
                }
            }
        }
        
        @Override
        public boolean hasNext() {
            return next != null;
        }
        
        @Override
        public String next() {
            if (!hasNext()) throw new NoSuchElementException();
            String result = next;
            advance();
            return result;
        }
    }
    
    @Override
    public int size() {
        return size;
    }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 | 공간복잡도 |
|------|-----------|-----------|
| insert | O(m) | O(m) |
| search | O(m) | O(1) |
| startsWith | O(m) | O(1) |
| delete | O(m) | O(1) |
| autocomplete | O(m + k) | O(k) |
| searchWithWildcard | O(26^w × m) | O(m) |

*m = 단어 길이, k = 결과 총 길이, w = 와일드카드 개수

### 공간 복잡도
- 배열 기반: O(26 × N × m) where N = 단어 수
- Map 기반: O(총 문자 수)

---

## ❌ 흔한 실수

### 1. search vs startsWith 혼동
```java
// search: 정확한 단어 매칭 (isEndOfWord 확인)
// startsWith: 접두사만 확인 (isEndOfWord 무관)

trie.insert("apple");
trie.search("app");      // false! "app"은 단어가 아님
trie.startsWith("app");  // true
```

### 2. 삭제 시 노드 관리
```java
// 잘못됨: 단순히 isEndOfWord만 false로
node.isEndOfWord = false;  // "app" 삭제 시 "apple" 깨짐

// 올바름: 다른 단어에 영향 없을 때만 노드 삭제
// "apple"이 있으면 "app" 경로의 노드는 유지
```

### 3. 백트래킹 누락
```java
// 자동완성 시 StringBuilder 복원 필수
sb.append(c);
collectWords(child, sb, results);
sb.deleteCharAt(sb.length() - 1);  // 이거 빠뜨리면 버그!
```

---

## 🔗 관련 문제

- LeetCode 208: Implement Trie (Prefix Tree)
- LeetCode 211: Design Add and Search Words Data Structure
- LeetCode 212: Word Search II
- LeetCode 14: Longest Common Prefix
- LeetCode 648: Replace Words
- LeetCode 677: Map Sum Pairs
- LeetCode 720: Longest Word in Dictionary
