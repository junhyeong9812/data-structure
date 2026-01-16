# 12. 스킵 리스트 (Skip List)

## 📋 문제 정의

**확률적 균형**을 사용하는 스킵 리스트를 구현하세요.

스킵 리스트는 다층 연결 리스트로, 이진 탐색 트리와 유사한 O(log n) 평균 시간복잡도를 제공하면서도
구현이 상대적으로 간단하고 동시성 처리가 용이합니다.

---

## 🎯 학습 목표

- 확률적 자료구조의 개념
- 다층 리스트 구조 이해
- 확률적 레벨 결정
- 탐색/삽입/삭제 알고리즘
- 균형 트리의 대안으로서의 활용

---

## 📝 요구사항

### 기본 연산

| 메서드 | 설명 | 평균 시간복잡도 |
|--------|------|----------------|
| `add(key)` 또는 `add(key, value)` | 원소 추가 | O(log n) |
| `search(key)` 또는 `contains(key)` | 원소 검색 | O(log n) |
| `remove(key)` | 원소 삭제 | O(log n) |
| `size()` | 원소 개수 | O(1) |
| `isEmpty()` | 비어있는지 확인 | O(1) |

### 범위 연산

| 메서드 | 설명 |
|--------|------|
| `floor(key)` | key 이하의 최대 원소 |
| `ceiling(key)` | key 이상의 최소 원소 |
| `range(from, to)` | [from, to] 범위의 모든 원소 |
| `getMin()` | 최소 원소 |
| `getMax()` | 최대 원소 |

### 추가 기능

| 메서드 | 설명 |
|--------|------|
| `rank(key)` | key보다 작은 원소 개수 |
| `select(k)` | k번째로 작은 원소 |
| `clear()` | 모든 원소 삭제 |
| `getLevel()` | 현재 최대 레벨 |

---

## 📊 입출력 예시

### 예제 1: 기본 사용
```java
SkipList<Integer> list = new SkipList<>();

list.add(3);
list.add(6);
list.add(7);
list.add(9);
list.add(12);
list.add(19);

System.out.println(list.search(6));   // true
System.out.println(list.search(8));   // false

list.remove(6);
System.out.println(list.search(6));   // false
```

### 예제 2: 시각화
```
Level 3:  HEAD ─────────────────────────→ 9 ───────────────→ NIL
Level 2:  HEAD ───────→ 6 ───────────────→ 9 ──────→ 19 ──→ NIL
Level 1:  HEAD → 3 → 6 → 7 → 9 → 12 → 19 → NIL

탐색 경로 (search 9):
Level 3: HEAD → 9 (발견!)
```

### 예제 3: Key-Value 버전
```java
SkipList<String, Integer> scores = new SkipList<>();

scores.put("Alice", 95);
scores.put("Bob", 87);
scores.put("Charlie", 92);

System.out.println(scores.get("Bob"));    // 87
scores.put("Bob", 90);                    // 업데이트
System.out.println(scores.get("Bob"));    // 90
```

### 예제 4: 범위 쿼리
```java
SkipList<Integer> list = new SkipList<>();
// 1, 3, 5, 7, 9, 11, 13 추가

System.out.println(list.floor(6));     // 5
System.out.println(list.ceiling(6));   // 7
System.out.println(list.range(4, 10)); // [5, 7, 9]
```

---

## 🔍 핵심 개념

### 레벨 결정 (확률적)
```
각 노드의 레벨은 동전 던지기로 결정:
- 50% 확률로 레벨 1
- 25% 확률로 레벨 2
- 12.5% 확률로 레벨 3
- ...

평균적으로 log₂(n) 레벨이 생성됨
```

### 기대 구조
```
n = 16일 때:
- Level 1: 16개 노드
- Level 2: 8개 노드 (평균)
- Level 3: 4개 노드 (평균)
- Level 4: 2개 노드 (평균)
- Level 5: 1개 노드 (평균)
```

---

## 💡 힌트

### 노드 구조
```java
class Node<K extends Comparable<K>> {
    K key;
    Node<K>[] forward;  // 각 레벨의 다음 노드
    
    @SuppressWarnings("unchecked")
    Node(K key, int level) {
        this.key = key;
        this.forward = new Node[level + 1];
    }
}
```

### 랜덤 레벨 생성
```java
private int randomLevel() {
    int level = 0;
    while (random.nextDouble() < P && level < MAX_LEVEL) {
        level++;
    }
    return level;
}
// P = 0.5, MAX_LEVEL = 16 (일반적)
```

### 탐색 알고리즘
```java
Node<K> search(K key) {
    Node<K> current = head;
    
    // 최상위 레벨부터 아래로
    for (int i = level; i >= 0; i--) {
        // 현재 레벨에서 가능한 멀리 이동
        while (current.forward[i] != null && 
               current.forward[i].key.compareTo(key) < 0) {
            current = current.forward[i];
        }
    }
    
    // 레벨 0에서 다음 노드 확인
    current = current.forward[0];
    if (current != null && current.key.equals(key)) {
        return current;
    }
    return null;
}
```

---

## ✅ 체크리스트

- [ ] 기본 add, search, remove 구현
- [ ] 랜덤 레벨 생성
- [ ] 다층 구조 유지
- [ ] floor, ceiling 구현
- [ ] range 쿼리 구현
- [ ] Key-Value 버전 구현
- [ ] Iterator 구현

---

## 📚 참고

- Redis의 Sorted Set (ZSET)
- LevelDB의 MemTable
- ConcurrentSkipListMap (Java)
- [Skip List Visualizer](https://people.ok.ubc.ca/ylucet/DS/SkipList.html)
