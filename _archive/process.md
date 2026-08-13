# 자료구조 TDD 학습 가이드

## 개요

이 문서는 Claude와 함께 자료구조를 TDD(Test-Driven Development) 방식으로 학습하는 워크플로우를 정리한 것입니다. 원형 큐, 힙 등을 이 방식으로 학습했으며, 이후 그래프, 트리 등 다른 자료구조에도 동일하게 적용할 수 있습니다.

---

## 학습 흐름

### 1단계: 요구사항 분석 및 테스트 대분류 설계

요구사항 문서를 Claude에게 공유하고, 테스트 클래스의 대분류(`@Nested` 구조)를 먼저 잡습니다.

**요청 예시:**
> 이 요구사항에 대해서 테스트 케이스 대분류를 잡아줘.

**결과물:** `MyTestCase.java`에 빈 `@Nested` 클래스 구조가 완성됩니다.

```java
public class MyTestCase {
    @Nested @DisplayName("MaxHeap 테스트")
    class MaxHeapTest {
        @Nested @DisplayName("insert 메서드 테스트")
        class InsertTest {}
        // ...
    }
}
```

---

### 2단계: 구현체 메서드 시그니처 확정

테스트 대상 클래스의 메서드 시그니처만 먼저 정의합니다. 구현은 하지 않고 기본 반환값(`return null`, `return 0`, `return false`)만 채웁니다.

**요청 예시:**
> 여기 메서드 시그니처만 우선 제공해줄래? 간단한 리턴들도 null이나 0으로 해놔줘.

**결과물:**
```java
public class MaxHeap<E extends Comparable<E>> {
    public void insert(E value) {}
    public boolean offer(E value) { return false; }
    public E extractMax() { return null; }
    // ...
}
```

**설계 포인트:**
- 예외를 던지는 메서드와 안전한 메서드를 쌍으로 설계 (예: `insert`/`offer`, `extractMax`/`poll`, `getMax`/`peek`)
- 제네릭 사용 여부를 이 단계에서 확정
- `Comparable` 바운드 등 타입 제약도 이 단계에서 결정

---

### 3단계: 테스트 코드 작성 → 검토 반복

각 `@Nested` 클래스 안의 테스트를 **직접 작성**하고, Claude에게 검토를 요청합니다.

**워크플로우:**

1. **테스트 케이스 목록 작성** (빈 메서드)
2. **메서드 시그니처 검토 요청** → Claude가 네이밍 제안
3. **테스트 내용 작성** (assert 포함)
4. **검토 요청** → Claude가 오류/누락 지적
5. **수정 후 다음 메서드로 이동**

**요청 예시:**
> 이거 맞아? 그리고 케이스가 더 있지않아?

**Claude가 검토하는 항목:**
- 메서드명이 DisplayName과 일치하는지
- 검증 값이 올바른지 (예: MinHeap인데 최댓값을 기대하고 있진 않은지)
- 누락된 테스트 케이스가 있는지
- 예외 타입이 적절한지
- MaxHeap 코드를 복사하면서 안 바꾼 부분이 있는지

---

### 4단계: 구현

모든 테스트가 작성된 후, 빨간 테스트를 하나씩 초록으로 만듭니다.

**요청 예시:**
> insert 구현 이렇게 했는데 맞아?

Claude에게 구현 코드를 보여주고 검토받으면서, 자료구조의 내부 동작 원리를 질문합니다.

**이해가 안 되는 부분은 바로 질문:**
> 여기서 parent에 대해서 index-1을 하고 2로 나누는 이유가 뭐야?
> 힙이면 사실 5,4,3,2,1로 들어가야 되지 않나?

Claude가 코드블럭으로 트리 구조와 배열 매핑을 시각적으로 보여줍니다.

---

## 테스트 작성 컨벤션

### 네이밍 규칙

```java
// DisplayName: 한글로 동작 설명
// 메서드명: 영어 snake_case로 동작 요약

@Test @DisplayName("빈 heap에 요소를 추가할 수 있다.")
void insert_into_empty_heap() {}

@Test @DisplayName("빈 힙에서 호출하면 예외가 발생한다.")
void extractMax_empty_heap_throws_exception() {}
```

### 예외 메서드 쌍 패턴

| 상황 | 예외 메서드 | 안전 메서드 |
|------|-----------|-----------|
| 삽입 실패 | `insert` → `IllegalArgumentException` | `offer` → `return false` |
| 빈 컬렉션 조회 | `getMax` → `NoSuchElementException` | `peek` → `return null` |
| 빈 컬렉션 삭제 | `extractMax` → `NoSuchElementException` | `poll` → `return null` |
| 인덱스 범위 초과 | `IndexOutOfBoundsException` | - |

### 공통 테스트 케이스 체크리스트

모든 자료구조에 반복적으로 적용되는 테스트 항목:

**생성 테스트:**
- 빈 객체 생성 시 isEmpty=true, size=0
- 초기 용량 지정 생성

**삽입 테스트:**
- 빈 상태에 삽입
- 요소가 있는 상태에 삽입
- null 삽입 시 예외
- 중복 값 삽입
- 자료구조 고유 속성 유지 확인 (힙 속성, 정렬 등)

**삭제 테스트:**
- 빈 상태에서 삭제 시 예외/null
- 요소 하나인 경우 삭제
- 여러 요소에서 삭제
- 연속 삭제 시 순서 검증

**조회 테스트:**
- 빈 상태에서 조회 시 예외/null
- 여러 번 조회해도 같은 값

**유틸 테스트:**
- size 증가/감소 추적
- isEmpty 경계 확인
- clear 후 재사용 가능 확인

---

## 자주 하는 실수 & 검토 포인트

### 1. MaxHeap ↔ MinHeap 복사 실수
MaxHeap 테스트를 복사해서 MinHeap 테스트를 만들 때 바꿔야 할 것:
- `peek()` 기댓값: 최댓값 → 최솟값
- DisplayName: "최댓값" → "최솟값"
- 메서드명: `max` → `min`
- 정렬 순서: "내림차순" → "오름차순"

### 2. 메서드명과 DisplayName 불일치
```java
// ❌ 이름은 exception인데 실제로는 null 반환
@DisplayName("빈 힙에서는 null을 반환한다.")
void poll_empty_heap_throws_exception() {}

// ✅ 일치
@DisplayName("빈 힙에서는 null을 반환한다.")
void poll_empty_heap_returns_null() {}
```

### 3. 검증 값 오류
```java
// ❌ 3개 넣었는데 size를 1로 검증
heap.insert(1); heap.insert(2); heap.insert(3);
assertThat(heap.size()).isOne();

// ✅ 올바른 검증
assertThat(heap.size()).isEqualTo(3);
```

### 4. containsExactly vs containsExactlyInAnyOrder
```java
// ❌ 정렬 테스트인데 순서 무시
assertThat(result).containsExactlyInAnyOrder(1, 2, 3);

// ✅ 정렬 테스트는 순서까지 검증
assertThat(result).containsExactly(1, 2, 3);
```

### 5. 힙 내부 배열 순서 가정 금지
```java
// ❌ 힙 내부 배열 순서는 구현에 따라 다름
heap.insert(3); heap.insert(1); heap.insert(5);
int result = heap.delete(2);
assertThat(result).isEqualTo(1); // index 2에 뭐가 있는지 알 수 없음

// ✅ 확실한 케이스로 테스트
heap.insert(10);
int result = heap.delete(0);
assertThat(result).isEqualTo(10);
```

---

## 프로젝트 패키지 구조

```
com.datastructure.{자료구조명}
├── pop/          ← 절차적(POP) 구현
│   ├── MaxHeap.java
│   ├── MinHeap.java
│   ├── HeapSort.java
│   └── HeapProblems.java
├── oop/          ← 객체지향(OOP) 구현
│   ├── Heap.java (인터페이스)
│   ├── PriorityQueue.java (인터페이스)
│   ├── BinaryHeap.java
│   ├── MedianFinder.java
│   └── KthLargest.java
└── MyTestCase.java
```

- **pop**: 각 자료구조를 독립 클래스로 구현 (MaxHeap, MinHeap 따로)
- **oop**: 인터페이스와 Comparator로 추상화 (BinaryHeap 하나로 통합)
- **응용 문제**: pop 패키지에 유틸 클래스로, oop 패키지에 독립 클래스로

---

## 세션 시작 시 컨텍스트 전달 방법

새 세션에서 이전 학습을 이어가려면:

> 원형 큐 테스트코드 검토 대화처럼 이어서 진행하려고 해.

또는 이 문서를 첨부하며:

> 이 가이드 방식대로 {자료구조명} 학습을 진행하려고 해. 우선 MyTestCase 대분류부터 잡아줘.

Claude가 이전 대화를 검색하여 컨벤션과 패턴을 파악한 후 일관된 방식으로 진행합니다.