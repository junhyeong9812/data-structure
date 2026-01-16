# 09. 트라이 (Trie / Prefix Tree)

## 📋 문제 정의

**접두사 트리(Prefix Tree)**라고도 불리는 트라이를 구현하세요.

트라이는 문자열을 효율적으로 저장하고 검색하는 트리 자료구조로,
자동완성, 맞춤법 검사, IP 라우팅 등에 활용됩니다.

---

## 🎯 학습 목표

- 트라이의 구조와 원리 이해
- 접두사 기반 검색의 효율성
- 문자열 집합의 효율적 저장
- 자동완성 기능 구현
- 와일드카드 검색 패턴

---

## 📝 요구사항

### 기본 연산

| 메서드 | 설명 | 시간복잡도 |
|--------|------|-----------|
| `insert(word)` | 단어 삽입 | O(m) |
| `search(word)` | 정확한 단어 검색 | O(m) |
| `startsWith(prefix)` | 접두사로 시작하는 단어 존재 여부 | O(m) |
| `delete(word)` | 단어 삭제 | O(m) |
| `countWordsEqualTo(word)` | 정확히 일치하는 단어 개수 | O(m) |
| `countWordsStartingWith(prefix)` | 접두사로 시작하는 단어 개수 | O(m) |

*m = 단어/접두사 길이

### 자동완성 기능

| 메서드 | 설명 |
|--------|------|
| `autocomplete(prefix)` | 접두사로 시작하는 모든 단어 반환 |
| `autocomplete(prefix, limit)` | 접두사로 시작하는 단어 최대 limit개 반환 |
| `getSuggestions(prefix)` | 빈도/우선순위 기반 추천 |

### 고급 기능

| 메서드 | 설명 |
|--------|------|
| `searchWithWildcard(pattern)` | `.`을 와일드카드로 사용한 검색 |
| `longestCommonPrefix()` | 모든 단어의 최장 공통 접두사 |
| `getAllWords()` | 트라이에 저장된 모든 단어 반환 |
| `size()` | 저장된 단어 개수 |

---

## 📊 입출력 예시

### 예제 1: 기본 사용
```java
Trie trie = new Trie();
trie.insert("apple");
trie.insert("app");
trie.insert("apricot");
trie.insert("banana");

System.out.println(trie.search("apple"));     // true
System.out.println(trie.search("app"));       // true
System.out.println(trie.search("ap"));        // false (접두사만 있음)
System.out.println(trie.startsWith("ap"));    // true
System.out.println(trie.startsWith("ban"));   // true
System.out.println(trie.startsWith("cat"));   // false
```

### 예제 2: 자동완성
```java
Trie trie = new Trie();
trie.insert("car");
trie.insert("card");
trie.insert("care");
trie.insert("careful");
trie.insert("careless");

List<String> suggestions = trie.autocomplete("car");
// ["car", "card", "care", "careful", "careless"]

List<String> limited = trie.autocomplete("car", 3);
// ["car", "card", "care"]
```

### 예제 3: 와일드카드 검색
```java
Trie trie = new Trie();
trie.insert("bad");
trie.insert("dad");
trie.insert("mad");
trie.insert("pad");
trie.insert("bat");

List<String> matches = trie.searchWithWildcard(".ad");
// ["bad", "dad", "mad", "pad"]

List<String> matches2 = trie.searchWithWildcard("b..");
// ["bad", "bat"]
```

### 예제 4: 단어 개수 세기
```java
Trie trie = new Trie();
trie.insert("apple");
trie.insert("apple");
trie.insert("app");
trie.insert("application");

System.out.println(trie.countWordsEqualTo("apple"));        // 2
System.out.println(trie.countWordsStartingWith("app"));     // 4
```

### 예제 5: 최장 공통 접두사
```java
Trie trie = new Trie();
trie.insert("flower");
trie.insert("flow");
trie.insert("flight");

System.out.println(trie.longestCommonPrefix()); // "fl"
```

---

## 🔍 제약 조건

- 단어는 소문자 알파벳 (a-z)으로만 구성
- 빈 문자열 삽입 허용하지 않음
- null 허용하지 않음
- 중복 단어 삽입 시 카운트 증가 (구현에 따라)

---

## 💡 힌트

### 트라이 노드 구조
```java
class TrieNode {
    TrieNode[] children = new TrieNode[26];  // a-z
    boolean isEndOfWord = false;
    int count = 0;  // 해당 단어 개수 (선택)
    int prefixCount = 0;  // 이 접두사를 가진 단어 개수 (선택)
}
```

### Map 기반 노드 (유니코드 지원)
```java
class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEndOfWord = false;
}
```

### 트라이 시각화
```
        root
       / | \
      a  b  c
     /   |
    p    a
   / \   |
  p   r  n
  |   |  |
 [l]  i [a]  <- []: isEndOfWord=true
  |   |  |
 [e]  c  n
      |  |
     [o] [a]
      |
     [t]

저장된 단어: apple, apricot, ban, banana
```

---

## ✅ 체크리스트

- [ ] 기본 insert, search, startsWith 구현
- [ ] delete 구현
- [ ] 자동완성 기능 구현
- [ ] 와일드카드 검색 구현
- [ ] 단어 카운팅 기능
- [ ] 최장 공통 접두사
- [ ] Iterator 구현

---

## 📚 참고

- [LeetCode 208. Implement Trie](https://leetcode.com/problems/implement-trie-prefix-tree/)
- [LeetCode 211. Design Add and Search Words Data Structure](https://leetcode.com/problems/design-add-and-search-words-data-structure/)
- 트라이 vs 해시맵 비교
- 압축 트라이 (Radix Tree)
