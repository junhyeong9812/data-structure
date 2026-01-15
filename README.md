# 자료구조 & 알고리즘 구현 프로젝트

Java 21 기반으로 30가지 자료구조와 알고리즘을 직접 구현하는 학습 프로젝트입니다.
각 구현은 **POP(절차지향)** 와 **OOP(객체지향)** 두 가지 패러다임으로 작성되어 있습니다.

## 📚 목차

- [프로젝트 소개](#-프로젝트-소개)
- [구현 목록](#-구현-목록)
- [프로젝트 구조](#-프로젝트-구조)
- [시작하기](#-시작하기)
- [학습 가이드](#-학습-가이드)
- [테스트 실행](#-테스트-실행)

---

## 🎯 프로젝트 소개

### 목표
- 자료구조의 내부 동작 원리 이해
- 시간/공간 복잡도 분석 능력 배양
- 두 가지 프로그래밍 패러다임 비교 학습
- 테스트 주도 개발(TDD) 경험

### 특징
- **Java 21** 최신 기능 활용 (Record, Pattern Matching, Virtual Threads 등)
- **POP vs OOP** 동일 문제를 두 가지 방식으로 해결
- **테스트 우선** 각 프로젝트당 30개 이상의 테스트 케이스
- **문서화** 상세한 풀이 해설 및 복잡도 분석 포함

### 패러다임 비교

| 구분 | POP (절차지향) | OOP (객체지향) |
|------|---------------|---------------|
| 데이터 | 배열, Map, 기본형 중심 | 클래스, 객체 중심 |
| 로직 | 함수/메서드에 집중 | 객체의 책임으로 분산 |
| 장점 | 단순함, 성능 | 확장성, 유지보수성 |
| 학습 | 알고리즘 로직 이해 | 설계 패턴 학습 |

---

## 📋 구현 목록

### 기초 자료구조 (1-10)

| # | 프로젝트 | 핵심 개념 | 난이도 |
|---|---------|----------|--------|
| 01 | [동적 배열](./01-dynamic-array) | 배열 확장, Amortized O(1) | ⭐ |
| 02 | [연결 리스트](./02-linked-list) | 노드 연결, 포인터 조작 | ⭐ |
| 03 | [스택](./03-stack) | LIFO, 괄호 매칭 | ⭐ |
| 04 | [큐/덱](./04-queue-deque) | FIFO, 양방향 연산 | ⭐ |
| 05 | [해시맵](./05-hashmap) | 해싱, 충돌 해결 | ⭐⭐ |
| 06 | [이진 탐색 트리](./06-binary-search-tree) | BST 속성, 순회 | ⭐⭐ |
| 07 | [힙/우선순위 큐](./07-heap) | 완전 이진 트리, Heapify | ⭐⭐ |
| 08 | [그래프](./08-graph) | 인접 리스트, BFS/DFS | ⭐⭐ |
| 09 | [트라이](./09-trie) | 접두사 트리, 자동완성 | ⭐⭐ |
| 10 | [LRU 캐시](./10-lru-cache) | 해시맵 + 이중 연결 리스트 | ⭐⭐ |

### 고급 자료구조 (11-20)

| # | 프로젝트 | 핵심 개념 | 난이도 |
|---|---------|----------|--------|
| 11 | [블룸 필터](./11-bloom-filter) | 확률적 자료구조, 해시 함수 | ⭐⭐ |
| 12 | [스킵 리스트](./12-skip-list) | 확률적 균형, 다층 리스트 | ⭐⭐⭐ |
| 13 | [구간 트리](./13-segment-tree) | 구간 쿼리, 지연 전파 | ⭐⭐⭐ |
| 14 | [유니온 파인드](./14-union-find) | 경로 압축, 랭크 최적화 | ⭐⭐ |
| 15 | [B-트리](./15-b-tree) | 다진 트리, 디스크 최적화 | ⭐⭐⭐⭐ |
| 16 | [Red-Black 트리](./16-red-black-tree) | 자가 균형, 색상 규칙 | ⭐⭐⭐⭐ |
| 17 | [펜윅 트리](./17-fenwick-tree) | 비트 조작, 누적 합 | ⭐⭐⭐ |
| 18 | [서킷 브레이커](./18-circuit-breaker) | 장애 격리, 상태 전이 | ⭐⭐ |
| 19 | [토큰 버킷](./19-token-bucket) | 속도 제한, 버스트 허용 | ⭐⭐ |
| 20 | [일관된 해싱](./20-consistent-hashing) | 분산 시스템, 가상 노드 | ⭐⭐⭐ |

### 시스템 설계 (21-30)

| # | 프로젝트 | 핵심 개념 | 난이도 |
|---|---------|----------|--------|
| 21 | [작업 스케줄러](./21-task-scheduler) | 우선순위, 지연 실행, Cron | ⭐⭐⭐ |
| 22 | [타임시리즈 DB](./22-timeseries-db) | 시간 인덱싱, 다운샘플링 | ⭐⭐⭐ |
| 23 | [문서 인덱서](./23-document-indexer) | 역인덱스, TF-IDF | ⭐⭐⭐ |
| 24 | [분산 락](./24-distributed-lock) | 상호 배제, Fencing Token | ⭐⭐⭐ |
| 25 | [블록체인](./25-blockchain) | 해시 체인, 작업 증명 | ⭐⭐⭐ |
| 26 | [파일 시스템](./26-file-system) | 트리 구조, 경로 탐색 | ⭐⭐⭐ |
| 27 | [의존성 해결기](./27-dependency-resolver) | 위상 정렬, 순환 탐지 | ⭐⭐⭐ |
| 28 | [메모리 풀](./28-memory-pool) | 메모리 할당, 버디 시스템 | ⭐⭐⭐⭐ |
| 29 | [이벤트 소싱](./29-event-sourcing) | 이벤트 저장소, 프로젝션 | ⭐⭐⭐ |
| 30 | [분산 ID 생성기](./30-distributed-id-generator) | Snowflake, UUID, ULID | ⭐⭐⭐ |

---

## 📁 프로젝트 구조
```
data-structure/
├── README.md                    # 이 파일
├── 01-dynamic-array/
│   ├── build.gradle
│   ├── settings.gradle
│   ├── README.md                # 문제 정의
│   ├── docs/
│   │   └── SOLUTION.md          # 풀이 해설
│   └── src/
│       ├── main/java/com/datastructure/dynamicarray/
│       │   ├── pop/             # 절차지향 구현
│       │   │   └── DynamicArray.java
│       │   └── oop/             # 객체지향 구현
│       │       ├── DynamicArray.java
│       │       └── DynamicArrayImpl.java
│       └── test/java/com/datastructure/dynamicarray/
│           ├── PopTest.java     # POP 테스트 (30개)
│           └── OopTest.java     # OOP 테스트 (30개)
├── 02-linked-list/
│   └── ...
└── ...
```

### 각 프로젝트 구성

| 파일/폴더 | 설명 |
|----------|------|
| `README.md` | 문제 정의, 입출력 형식, 예제 |
| `docs/SOLUTION.md` | 알고리즘 해설, 복잡도 분석, 핵심 코드 |
| `src/main/.../pop/` | 절차지향 구현 (TODO 상태) |
| `src/main/.../oop/` | 객체지향 구현 (TODO 상태) |
| `src/test/...Test.java` | 테스트 케이스 (구현 검증용) |

---

## 🚀 시작하기

### 요구 사항

- **Java 21** 이상
- **Gradle 8.x** (Wrapper 포함)

### 빌드 및 실행
```bash
# 프로젝트 클론
git clone <repository-url>
cd data-structure

# 특정 프로젝트로 이동
cd 01-dynamic-array

# 테스트 실행 (구현 전에는 실패)
./gradlew test

# 전체 빌드
./gradlew build
```

### IDE 설정

**IntelliJ IDEA** (권장)
1. `File > Open` 에서 프로젝트 폴더 선택
2. Gradle 프로젝트로 인식됨
3. Java 21 SDK 설정 확인

**VS Code**
1. Extension Pack for Java 설치
2. 프로젝트 폴더 열기
3. Java 21 경로 설정

---

## 📖 학습 가이드

### 권장 학습 순서
```
1단계: 기초 (1-4)
├── 동적 배열 → 연결 리스트 → 스택 → 큐
└── 기본적인 자료구조의 원리 이해

2단계: 핵심 (5-10)
├── 해시맵 → BST → 힙 → 그래프 → 트라이 → LRU
└── 탐색, 정렬, 캐싱의 핵심 개념

3단계: 고급 (11-17)
├── 블룸 필터 → 스킵 리스트 → 구간 트리 → ...
└── 확률적 자료구조, 균형 트리, 구간 쿼리

4단계: 시스템 (18-30)
├── 서킷 브레이커 → 토큰 버킷 → 일관된 해싱 → ...
└── 실무 시스템 설계에 사용되는 패턴
```

### 학습 방법

1. **README.md 읽기**
    - 문제 정의 이해
    - 입출력 형식 파악
    - 예제로 동작 확인

2. **테스트 먼저 실행**
```bash
   ./gradlew test
```
- 실패하는 테스트 확인
- 테스트 케이스로 요구사항 파악

3. **POP 구현**
    - `src/main/.../pop/` 의 TODO 채우기
    - 알고리즘 로직에 집중
    - 테스트 통과 확인

4. **OOP 구현**
    - `src/main/.../oop/` 의 TODO 채우기
    - 객체 설계에 집중
    - 테스트 통과 확인

5. **SOLUTION.md 참고**
    - 구현 후 풀이 해설 확인
    - 더 나은 방법 학습
    - 복잡도 분석 이해

### 코드 작성 팁
```java
// POP 스타일 - 함수와 데이터 분리
public class DynamicArray {
    private int[] data;
    private int size;
    
    public void add(int element) {
        // 직접적인 배열 조작
    }
}

// OOP 스타일 - 객체의 책임
public class DynamicArray<E> implements List<E> {
    private Object[] elements;
    private int size;
    
    @Override
    public boolean add(E element) {
        // 인터페이스 계약 준수
    }
}
```

---

## 🧪 테스트 실행

### 개별 프로젝트 테스트
```bash
cd 01-dynamic-array
./gradlew test
```

### 특정 테스트 클래스 실행
```bash
./gradlew test --tests "PopTest"
./gradlew test --tests "OopTest"
```

### 특정 테스트 메서드 실행
```bash
./gradlew test --tests "PopTest.test01_*"
```

### 테스트 리포트 확인
```bash
# 테스트 실행 후
open build/reports/tests/test/index.html
```

### 전체 프로젝트 일괄 테스트
```bash
# 프로젝트 루트에서
for dir in */; do
    if [ -f "$dir/build.gradle" ]; then
        echo "Testing $dir..."
        (cd "$dir" && ./gradlew test)
    fi
done
```

---

## 📊 복잡도 요약

### 기초 자료구조

| 자료구조 | 접근 | 탐색 | 삽입 | 삭제 | 공간 |
|---------|------|------|------|------|------|
| 동적 배열 | O(1) | O(n) | O(1)* | O(n) | O(n) |
| 연결 리스트 | O(n) | O(n) | O(1) | O(1) | O(n) |
| 스택 | O(n) | O(n) | O(1) | O(1) | O(n) |
| 큐 | O(n) | O(n) | O(1) | O(1) | O(n) |
| 해시맵 | - | O(1)* | O(1)* | O(1)* | O(n) |
| BST | - | O(log n)* | O(log n)* | O(log n)* | O(n) |
| 힙 | O(1) | O(n) | O(log n) | O(log n) | O(n) |

*평균 시간복잡도

### 고급 자료구조

| 자료구조 | 주요 연산 | 시간복잡도 | 공간 |
|---------|----------|-----------|------|
| 블룸 필터 | 추가/조회 | O(k) | O(m) |
| 스킵 리스트 | 탐색/삽입/삭제 | O(log n)* | O(n) |
| 구간 트리 | 쿼리/갱신 | O(log n) | O(n) |
| 유니온 파인드 | 합집합/찾기 | O(α(n)) | O(n) |
| B-트리 | 탐색/삽입/삭제 | O(log n) | O(n) |
| Red-Black 트리 | 탐색/삽입/삭제 | O(log n) | O(n) |
| 펜윅 트리 | 쿼리/갱신 | O(log n) | O(n) |

---

## 🔗 참고 자료

### 도서
- *Introduction to Algorithms* (CLRS)
- *Algorithms* by Robert Sedgewick
- *데이터 중심 애플리케이션 설계* (DDIA)

### 온라인 자료
- [visualgo.net](https://visualgo.net/) - 자료구조 시각화
- [Big-O Cheat Sheet](https://www.bigocheatsheet.com/)
- [GeeksforGeeks](https://www.geeksforgeeks.org/)

### Java 관련
- [Java 21 Documentation](https://docs.oracle.com/en/java/javase/21/)
- [Effective Java 3rd Edition](https://www.oreilly.com/library/view/effective-java/9780134686097/)

---

## 📝 라이선스

이 프로젝트는 학습 목적으로 제작되었습니다.

---

## 🤝 기여하기

1. 버그 리포트 및 개선 제안 환영
2. 추가 테스트 케이스 기여 환영
3. 문서 개선 기여 환영

---

**Happy Coding! 🚀**