# HashMap 상속 관계의 한계

## 배경

`LinkedHashMap`을 구현하면서 `HashMap`을 상속하려 했으나, 대부분의 메서드를 오버라이드해야 하는 상황이 발생했다.
이 문서는 그 경험에서 도출한 상속의 한계와 대안을 정리한다.

---

## 문제 상황

### 기존 설계 의도

```
Map<K, V> (인터페이스)
    └── HashMap<K, V> (선형 탐사 기반 구현)
            └── LinkedHashMap<K, V> (삽입 순서 유지)
```

`LinkedHashMap`은 `HashMap`의 기능을 재사용하면서 삽입 순서만 추가하려는 의도였다.

### 실제 발생한 문제

`HashMap`의 내부 구현이 선형 탐사(Linear Probing)에 강하게 결합되어 있었다.

- `put()`: 빈 칸을 찾아 `(index + 1) % capacity`로 이동
- `get()`: 같은 방식으로 순회하며 key 탐색
- `remove()`: 삭제 후 `rehashCluster()`로 연속된 엔트리 재배치
- `growCapacity()`: 모든 엔트리를 새 배열에 재삽입

`LinkedHashMap`에서 삽입 순서를 유지하려면 이중 연결 리스트를 추가해야 하는데,
위 메서드들이 모두 배열을 직접 조작하고 있어서 **거의 모든 메서드를 오버라이드**해야 한다.

### 오버라이드가 필요한 메서드

| 메서드 | 이유 |
|--------|------|
| `put()` | 연결 리스트에 노드 추가 필요 |
| `remove()` | 연결 리스트에서 노드 제거 필요 |
| `clear()` | 연결 리스트도 초기화 필요 |
| `keySet()` | 삽입 순서대로 반환해야 함 |
| `values()` | 삽입 순서대로 반환해야 함 |
| `entrySet()` | 삽입 순서대로 반환해야 함 |

결국 상속받아도 재사용할 수 있는 메서드가 `size()`, `isEmpty()` 정도뿐이다.

---

## 왜 이런 문제가 발생하는가

### 1. 구현 상세에 대한 강한 결합

`HashMap`의 메서드들이 내부 배열 구조(선형 탐사)에 직접 의존한다.
자식 클래스가 내부 구조를 변경하려면 부모의 구현을 거의 무시해야 한다.

### 2. 상속은 "is-a" 관계여야 한다

`LinkedHashMap`은 "순서가 보장되는 HashMap"이지만, 내부 동작 방식이 근본적으로 다르다.
행위(behavior)는 유사하나 구현(implementation)이 달라서 상속이 적절하지 않다.

### 3. 리스코프 치환 원칙(LSP) 관점

상속이 적절하려면 자식 클래스가 부모 클래스를 완전히 대체할 수 있어야 한다.
오버라이드한 메서드들이 부모의 내부 상태(배열)를 무시하고 별도의 자료구조(연결 리스트)를 사용한다면,
부모 클래스의 불변식(invariant)이 깨질 수 있다.

---

## 대안: 인터페이스 기반 구현

```
Map<K, V> (인터페이스)
    ├── HashMap<K, V> (선형 탐사 기반)
    └── LinkedHashMap<K, V> (해시 + 이중 연결 리스트 기반)
```

각 클래스가 `Map` 인터페이스를 독립적으로 구현한다.

### 장점

- 각 구현체가 자신에게 최적화된 내부 구조를 자유롭게 선택할 수 있다
- 부모 클래스의 구현에 끌려다니지 않는다
- 코드 중복이 발생하더라도 각 클래스의 의도가 명확하다

### 단점

- `size()`, `isEmpty()` 같은 공통 로직이 중복된다
- 공통 로직이 많아지면 추상 클래스로 묶는 것을 고려할 수 있다

---

## 정리

| 기준 | 상속 | 인터페이스 구현 |
|------|------|----------------|
| 코드 재사용 | 거의 없음 (대부분 오버라이드) | 중복 발생하나 명확함 |
| 결합도 | 부모 구현에 강하게 결합 | 독립적 |
| 유지보수 | 부모 변경 시 자식에 영향 | 각자 독립 |
| 확장성 | 제한적 | 자유로움 |

**결론**: 내부 구현이 근본적으로 다른 경우, 상속보다 인터페이스 기반 독립 구현이 적절하다.
상속은 부모의 구현을 실질적으로 재사용할 수 있을 때만 의미가 있다.

---

## 그렇다면 상속이 가능하려면 HashMap은 어떤 구조여야 하는가

### 선형 탐사가 상속에 불리한 이유

선형 탐사는 모든 데이터가 하나의 배열에 직접 저장된다.
삽입, 삭제, 탐색 모두 배열 인덱스를 직접 조작하기 때문에,
자식 클래스가 "삽입 순서 유지"같은 부가 동작을 끼워넣을 틈이 없다.

### 체이닝(Separate Chaining) 방식이라면 가능하다

체이닝 방식의 HashMap 구조:

```
buckets[0] → Entry(key, value) → Entry(key, value) → null
buckets[1] → null
buckets[2] → Entry(key, value) → null
...
```

각 버킷이 연결 리스트(Entry 체인)를 갖는 구조다.

### 체이닝에서 상속이 가능한 이유

핵심은 **Entry를 노드 단위로 다룬다**는 점이다.

부모 HashMap이 Entry 생성/삭제를 메서드로 분리하면:

```java
// HashMap (부모)
public class HashMap<K, V> {

    // 자식이 오버라이드할 수 있는 훅(hook) 메서드
    protected Entry<K, V> createEntry(K key, V value) {
        return new Entry<>(key, value);
    }

    protected void afterInsert(Entry<K, V> entry) {
        // 기본 구현: 아무것도 안 함
    }

    protected void afterRemove(Entry<K, V> entry) {
        // 기본 구현: 아무것도 안 함
    }

    public V put(K key, V value) {
        // ... 해시 계산, 체인 탐색 ...
        Entry<K, V> newEntry = createEntry(key, value);
        // ... 버킷에 추가 ...
        afterInsert(newEntry);
        return null;
    }
}
```

자식 LinkedHashMap은 훅 메서드만 오버라이드하면 된다:

```java
// LinkedHashMap (자식)
public class LinkedHashMap<K, V> extends HashMap<K, V> {

    private Entry<K, V> head;  // 이중 연결 리스트의 시작
    private Entry<K, V> tail;  // 이중 연결 리스트의 끝

    @Override
    protected void afterInsert(Entry<K, V> entry) {
        // 이중 연결 리스트의 끝에 연결
        linkToTail(entry);
    }

    @Override
    protected void afterRemove(Entry<K, V> entry) {
        // 이중 연결 리스트에서 제거
        unlinkEntry(entry);
    }
}
```

### 이 구조가 가능한 조건

| 조건 | 설명 |
|------|------|
| Entry가 독립적인 노드 | 배열 슬롯이 아니라 객체 단위로 관리 |
| 훅 메서드 제공 | 삽입/삭제 시점에 자식이 개입할 수 있는 확장 포인트 |
| 내부 배열 직접 조작 최소화 | 자식이 부모의 배열을 몰라도 동작 가능 |

실제 Java의 `java.util.HashMap`도 체이닝 방식이며,
`LinkedHashMap`은 `afterNodeInsertion()`, `afterNodeRemoval()` 같은 훅 메서드를 오버라이드해서
삽입 순서를 유지한다.

### 정리

```
상속이 가능한 구조 (체이닝)          상속이 어려운 구조 (선형 탐사)
┌─────────────────────┐          ┌─────────────────────┐
│ HashMap (체이닝)       │          │ HashMap (선형 탐사)    │
│  - Entry 노드 기반     │          │  - 배열 슬롯 직접 조작  │
│  - 훅 메서드 제공       │          │  - 확장 포인트 없음     │
│        ↓ 상속          │          │        ↓ 상속          │
│ LinkedHashMap          │          │ LinkedHashMap          │
│  - 훅만 오버라이드      │          │  - 거의 전부 오버라이드  │
│  - 부모 로직 재사용     │          │  - 상속 의미 없음       │
└─────────────────────┘          └─────────────────────┘
```

**결론**: `LinkedHashMap`을 상속으로 구현하려면 부모 `HashMap`이 체이닝 방식이어야 하며,
Entry 생성/삭제 시점에 자식이 개입할 수 있는 훅 메서드를 제공해야 한다.