# 스킵 리스트 풀이 해설

## 📌 핵심 아이디어

스킵 리스트는 **다층 연결 리스트**로, 상위 레벨은 "고속도로"처럼
빠르게 이동하고, 하위 레벨에서 정밀하게 탐색합니다.

**확률적 균형**: 각 노드의 레벨을 확률적으로 결정하여
평균적으로 균형 잡힌 구조를 유지합니다.

---

## 🔑 핵심 개념

### 1. 다층 구조
```
스킵 리스트 (n=8):

Level 3:  HEAD ────────────────────→ 17 ────────────→ NIL
Level 2:  HEAD ────→ 6 ────────────→ 17 ────→ 25 ──→ NIL
Level 1:  HEAD → 3 → 6 → 9 → 12 → 17 → 21 → 25 → NIL

탐색 (search 12):
1. Level 3: HEAD → 17 (12 < 17, 아래로)
2. Level 2: HEAD → 6 → 17 (12 < 17, 아래로)
3. Level 1: 6 → 9 → 12 (발견!)
```

### 2. 레벨 결정 확률
```
P = 0.5 (동전 던지기)

randomLevel():
  level = 0
  while (random() < 0.5):
    level++
  return level

결과 분포:
  Level 0: 50%
  Level 1: 25%
  Level 2: 12.5%
  Level 3: 6.25%
  ...
```

### 3. 삽입 과정
```
insert(12):

1. 탐색 경로 기록 (update[])
   Level 2: HEAD → 6 → ...
   Level 1: ... → 9 → ...
   Level 0: ... → 9 → ...

2. 새 레벨 생성: randomLevel() = 1

3. 노드 삽입:
   Level 1: 9.forward[1] = 12, 12.forward[1] = old(9.forward[1])
   Level 0: 9.forward[0] = 12, 12.forward[0] = old(9.forward[0])
```

---

## 📝 POP 구현 해설
```java
public class SkipList<K extends Comparable<K>> {
    private static final double P = 0.5;
    private static final int MAX_LEVEL = 16;
    
    private final Node<K> head;
    private int level;  // 현재 최대 레벨
    private int size;
    private final Random random;
    
    private static class Node<K> {
        K key;
        Node<K>[] forward;
        
        @SuppressWarnings("unchecked")
        Node(K key, int level) {
            this.key = key;
            this.forward = new Node[level + 1];
        }
    }
    
    public SkipList() {
        this.head = new Node<>(null, MAX_LEVEL);
        this.level = 0;
        this.size = 0;
        this.random = new Random();
    }
    
    // 랜덤 레벨 생성
    private int randomLevel() {
        int lvl = 0;
        while (random.nextDouble() < P && lvl < MAX_LEVEL) {
            lvl++;
        }
        return lvl;
    }
    
    // 검색: O(log n) 평균
    public boolean search(K key) {
        Node<K> current = head;
        
        // 위에서 아래로, 오른쪽으로
        for (int i = level; i >= 0; i--) {
            while (current.forward[i] != null &&
                   current.forward[i].key.compareTo(key) < 0) {
                current = current.forward[i];
            }
        }
        
        current = current.forward[0];
        return current != null && current.key.equals(key);
    }
    
    // 삽입: O(log n) 평균
    @SuppressWarnings("unchecked")
    public void add(K key) {
        Node<K>[] update = new Node[MAX_LEVEL + 1];
        Node<K> current = head;
        
        // 삽입 위치 찾기 + update 배열 채우기
        for (int i = level; i >= 0; i--) {
            while (current.forward[i] != null &&
                   current.forward[i].key.compareTo(key) < 0) {
                current = current.forward[i];
            }
            update[i] = current;
        }
        
        current = current.forward[0];
        
        // 이미 존재하면 (값 업데이트 또는 무시)
        if (current != null && current.key.equals(key)) {
            return;  // 또는 값 업데이트
        }
        
        // 새 레벨 결정
        int newLevel = randomLevel();
        
        // 현재 최대 레벨보다 높으면 확장
        if (newLevel > level) {
            for (int i = level + 1; i <= newLevel; i++) {
                update[i] = head;
            }
            level = newLevel;
        }
        
        // 새 노드 생성 및 연결
        Node<K> newNode = new Node<>(key, newLevel);
        for (int i = 0; i <= newLevel; i++) {
            newNode.forward[i] = update[i].forward[i];
            update[i].forward[i] = newNode;
        }
        
        size++;
    }
    
    // 삭제: O(log n) 평균
    @SuppressWarnings("unchecked")
    public boolean remove(K key) {
        Node<K>[] update = new Node[MAX_LEVEL + 1];
        Node<K> current = head;
        
        // 삭제할 노드 찾기
        for (int i = level; i >= 0; i--) {
            while (current.forward[i] != null &&
                   current.forward[i].key.compareTo(key) < 0) {
                current = current.forward[i];
            }
            update[i] = current;
        }
        
        current = current.forward[0];
        
        // 존재하지 않으면
        if (current == null || !current.key.equals(key)) {
            return false;
        }
        
        // 연결 해제
        for (int i = 0; i <= level; i++) {
            if (update[i].forward[i] != current) {
                break;
            }
            update[i].forward[i] = current.forward[i];
        }
        
        // 빈 레벨 제거
        while (level > 0 && head.forward[level] == null) {
            level--;
        }
        
        size--;
        return true;
    }
    
    // floor: key 이하의 최대 원소
    public K floor(K key) {
        Node<K> current = head;
        
        for (int i = level; i >= 0; i--) {
            while (current.forward[i] != null &&
                   current.forward[i].key.compareTo(key) < 0) {
                current = current.forward[i];
            }
        }
        
        // current는 key보다 작은 최대 노드
        // current.forward[0]이 key 이하인지 확인
        if (current.forward[0] != null && 
            current.forward[0].key.compareTo(key) <= 0) {
            return current.forward[0].key;
        }
        
        return current.key;  // head면 null
    }
    
    // ceiling: key 이상의 최소 원소
    public K ceiling(K key) {
        Node<K> current = head;
        
        for (int i = level; i >= 0; i--) {
            while (current.forward[i] != null &&
                   current.forward[i].key.compareTo(key) < 0) {
                current = current.forward[i];
            }
        }
        
        current = current.forward[0];
        if (current != null) {
            return current.key;
        }
        return null;
    }
    
    // 범위 쿼리
    public List<K> range(K from, K to) {
        List<K> result = new ArrayList<>();
        Node<K> current = head;
        
        // from 위치로 이동
        for (int i = level; i >= 0; i--) {
            while (current.forward[i] != null &&
                   current.forward[i].key.compareTo(from) < 0) {
                current = current.forward[i];
            }
        }
        
        current = current.forward[0];
        
        // to까지 수집
        while (current != null && current.key.compareTo(to) <= 0) {
            result.add(current.key);
            current = current.forward[0];
        }
        
        return result;
    }
    
    public int size() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public K getMin() {
        return head.forward[0] != null ? head.forward[0].key : null;
    }
    
    public K getMax() {
        Node<K> current = head;
        for (int i = level; i >= 0; i--) {
            while (current.forward[i] != null) {
                current = current.forward[i];
            }
        }
        return current.key;
    }
}
```

---

## 📝 Key-Value 버전
```java
public class SkipListMap<K extends Comparable<K>, V> {
    
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V>[] forward;
        
        @SuppressWarnings("unchecked")
        Node(K key, V value, int level) {
            this.key = key;
            this.value = value;
            this.forward = new Node[level + 1];
        }
    }
    
    public V get(K key) {
        Node<K, V> node = findNode(key);
        return node != null ? node.value : null;
    }
    
    public V put(K key, V value) {
        // search 로직과 동일하게 위치 찾기
        // 존재하면 값 업데이트, 아니면 삽입
    }
    
    public V remove(K key) {
        // 삭제 후 기존 값 반환
    }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 평균 | 최악 |
|------|------|------|
| search | O(log n) | O(n) |
| add | O(log n) | O(n) |
| remove | O(log n) | O(n) |
| range | O(log n + k) | O(n) |

*k = 범위 내 원소 수

### 공간 복잡도
- O(n) 평균 (각 노드가 평균 2개의 포인터)
- O(n × MAX_LEVEL) 최악

### 왜 평균 O(log n)?
```
n개 노드에서:
- Level 0: n개
- Level 1: n/2개 (평균)
- Level 2: n/4개 (평균)
- ...
- Level log n: 1개

탐색 시 각 레벨에서 평균 2번 이동
→ 총 이동: 2 × log n = O(log n)
```

---

## ❌ 흔한 실수

### 1. 레벨 배열 크기
```java
// 잘못됨: 레벨만큼만 생성
Node<K>[] forward = new Node[level];  // level 인덱스 접근 불가!

// 올바름: level + 1
Node<K>[] forward = new Node[level + 1];
```

### 2. update 배열 초기화
```java
// 잘못됨: 새 레벨 > 현재 레벨일 때 update 미초기화
// NullPointerException 발생

// 올바름: head로 초기화
if (newLevel > level) {
    for (int i = level + 1; i <= newLevel; i++) {
        update[i] = head;
    }
}
```

### 3. 레벨 감소 누락
```java
// 삭제 후 빈 레벨 정리 필수
while (level > 0 && head.forward[level] == null) {
    level--;
}
```

---

## 🔗 관련 문제

- LeetCode 1206: Design Skiplist
- 범위 쿼리가 필요한 정렬 집합 문제
- Redis ZSET 구현 이해
- Java ConcurrentSkipListMap 학습
