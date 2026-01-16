# 05. 해시맵 (HashMap)

## 📋 문제 정의

**해시 함수**를 사용하여 키-값 쌍을 저장하는 **해시맵(HashMap)**을 구현하세요.

평균 O(1) 시간에 삽입, 삭제, 검색이 가능한 핵심 자료구조입니다.

---

## 🎯 학습 목표

- 해시 함수의 원리 이해
- 해시 충돌(Collision) 해결 방법
- 체이닝(Chaining) vs 개방 주소법(Open Addressing)
- 로드 팩터(Load Factor)와 리해싱(Rehashing)
- equals()와 hashCode() 계약 이해

---

## 📝 요구사항

### 기본 연산

| 메서드 | 설명 | 평균 시간복잡도 |
|--------|------|----------------|
| `put(key, value)` | 키-값 쌍 저장 (키 있으면 덮어쓰기) | O(1) |
| `get(key)` | 키에 해당하는 값 반환 | O(1) |
| `remove(key)` | 키-값 쌍 삭제 | O(1) |
| `containsKey(key)` | 키 존재 여부 확인 | O(1) |
| `containsValue(value)` | 값 존재 여부 확인 | O(n) |
| `size()` | 저장된 키-값 쌍 개수 | O(1) |
| `isEmpty()` | 비어있는지 확인 | O(1) |
| `clear()` | 모든 요소 삭제 | O(n) |
| `keySet()` | 모든 키 집합 반환 | O(n) |
| `values()` | 모든 값 컬렉션 반환 | O(n) |
| `entrySet()` | 모든 키-값 쌍 집합 반환 | O(n) |

### 충돌 해결 방식

1. **체이닝 (Separate Chaining)**
   - 각 버킷에 연결 리스트 저장
   - Java 8+에서는 트리화 (8개 이상 → Red-Black Tree)

2. **개방 주소법 (Open Addressing)**
   - 선형 탐사 (Linear Probing)
   - 이차 탐사 (Quadratic Probing)
   - 이중 해싱 (Double Hashing)

---

## 📊 입출력 예시

### 예제 1: 기본 사용
```java
HashMap<String, Integer> map = new HashMap<>();
map.put("apple", 100);
map.put("banana", 200);
map.put("cherry", 300);

System.out.println(map.get("apple"));     // 출력: 100
System.out.println(map.get("grape"));     // 출력: null
System.out.println(map.containsKey("banana")); // 출력: true
```

### 예제 2: 값 덮어쓰기
```java
HashMap<String, Integer> map = new HashMap<>();
map.put("key", 1);
map.put("key", 2);  // 기존 값 덮어쓰기

System.out.println(map.get("key"));  // 출력: 2
System.out.println(map.size());      // 출력: 1
```

### 예제 3: 삭제
```java
HashMap<String, Integer> map = new HashMap<>();
map.put("a", 1);
map.put("b", 2);

Integer removed = map.remove("a");
System.out.println(removed);              // 출력: 1
System.out.println(map.containsKey("a")); // 출력: false
```

### 예제 4: null 키/값
```java
HashMap<String, Integer> map = new HashMap<>();
map.put(null, 100);      // null 키 허용
map.put("key", null);    // null 값 허용

System.out.println(map.get(null));   // 출력: 100
System.out.println(map.get("key"));  // 출력: null
```

---

## 🔍 제약 조건

- **초기 용량**: 16 (2의 거듭제곱)
- **로드 팩터**: 0.75 (75% 차면 리해싱)
- **리해싱**: 용량 2배로 확장
- `null` 키/값 허용 (구현 방식에 따라)
- equals()와 hashCode() 일관성 필요

---

## 💡 힌트

### 해시 함수
```java
// 버킷 인덱스 계산
int hash = key.hashCode();
int index = hash & (capacity - 1);  // capacity가 2의 거듭제곱일 때

// 또는
int index = Math.abs(hash) % capacity;
```

### 체이닝 구현 힌트
```java
class Entry<K, V> {
    K key;
    V value;
    Entry<K, V> next;  // 연결 리스트
}

Entry<K, V>[] buckets = new Entry[capacity];
```

### 개방 주소법 힌트
```java
// 선형 탐사
int index = hash & (capacity - 1);
while (buckets[index] != null && !buckets[index].key.equals(key)) {
    index = (index + 1) & (capacity - 1);
}
```

---

## ✅ 체크리스트

- [ ] 체이닝 방식 해시맵 구현
- [ ] 개방 주소법(선형 탐사) 해시맵 구현
- [ ] 로드 팩터 기반 리해싱
- [ ] null 키/값 처리
- [ ] equals/hashCode 올바른 사용
- [ ] keySet, values, entrySet 구현
- [ ] Iterator 구현

---

## 📚 참고

- [Java HashMap 소스코드](https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/HashMap.java)
- hashCode()와 equals() 계약
- 해시 충돌 공격 (Hash DoS)
