# 23. 문서 인덱서 (Document Indexer)

## 📋 문제 정의

**전문 검색(Full-Text Search)**을 위한 역인덱스(Inverted Index) 기반 
문서 인덱서를 구현하세요.

문서 인덱서는 검색 엔진의 핵심 구성요소로, 대량의 문서에서 
키워드 기반 검색을 빠르게 수행합니다.

---

## 🎯 학습 목표

- 역인덱스(Inverted Index) 구조
- 토큰화(Tokenization)
- TF-IDF 스코어링
- 불용어(Stop Words) 처리
- 검색 랭킹 알고리즘

---

## 📝 요구사항

### 핵심 개념

| 개념 | 설명 |
|------|------|
| **Inverted Index** | 단어 → 문서 ID 목록 매핑 |
| **Tokenization** | 문서를 단어로 분리 |
| **TF (Term Frequency)** | 단어의 문서 내 빈도 |
| **IDF (Inverse Document Frequency)** | 단어의 희소성 |
| **TF-IDF** | TF × IDF, 검색 관련성 점수 |

### 기본 연산

| 메서드 | 설명 |
|--------|------|
| `addDocument(id, content)` | 문서 인덱싱 |
| `removeDocument(id)` | 문서 제거 |
| `search(query)` | 검색 (관련성 순) |
| `searchWithScore(query)` | 점수와 함께 검색 |

### 검색 옵션

| 옵션 | 설명 |
|------|------|
| `AND` | 모든 키워드 포함 |
| `OR` | 하나 이상 키워드 포함 |
| `phrase` | 구문 검색 (연속된 단어) |

---

## 📊 입출력 예시

### 예제 1: 기본 사용
```java
DocumentIndexer indexer = new DocumentIndexer();

// 문서 추가
indexer.addDocument("doc1", "The quick brown fox jumps over the lazy dog");
indexer.addDocument("doc2", "A quick brown dog runs in the park");
indexer.addDocument("doc3", "The lazy cat sleeps all day");

// 검색
List<String> results = indexer.search("quick brown");
// ["doc1", "doc2"] - 두 단어가 모두 있는 문서

List<String> results2 = indexer.search("lazy");
// ["doc1", "doc3"] - lazy가 있는 문서
```

### 예제 2: TF-IDF 스코어링
```java
// 점수와 함께 검색
List<SearchResult> results = indexer.searchWithScore("quick fox");

// doc1: quick(1번) + fox(1번) → 높은 점수
// doc2: quick(1번) + fox(0번) → 낮은 점수

for (SearchResult r : results) {
    System.out.println(r.docId() + ": " + r.score());
}
// doc1: 0.85
// doc2: 0.42
```

### 예제 3: 역인덱스 구조
```
문서들:
  doc1: "cat dog cat"
  doc2: "dog bird"
  doc3: "cat bird cat cat"

역인덱스:
  "cat"  → [(doc1, 2), (doc3, 3)]
  "dog"  → [(doc1, 1), (doc2, 1)]
  "bird" → [(doc2, 1), (doc3, 1)]

검색 "cat dog":
  cat 포함: doc1, doc3
  dog 포함: doc1, doc2
  AND 결과: doc1 (둘 다 포함)
```

### 예제 4: TF-IDF 계산
```
TF(t, d) = 단어 t가 문서 d에 나타난 횟수 / 문서 d의 총 단어 수

IDF(t) = log(전체 문서 수 / 단어 t를 포함하는 문서 수)

TF-IDF(t, d) = TF(t, d) × IDF(t)

예: 3개 문서, "cat"이 2개 문서에 등장
  IDF("cat") = log(3/2) ≈ 0.176
  
  doc1에서 "cat" 2번, 총 3단어
  TF("cat", doc1) = 2/3 ≈ 0.667
  
  TF-IDF("cat", doc1) = 0.667 × 0.176 ≈ 0.117
```

---

## 🔍 핵심 개념

### 역인덱스 구조
```
┌─────────────────────────────────────────┐
│           Inverted Index                │
├─────────────────────────────────────────┤
│                                         │
│  Term      │  Posting List              │
│  ──────────┼────────────────────────── │
│  "apple"   │  [(doc1,3), (doc5,1)]     │
│  "banana"  │  [(doc2,2), (doc3,4)]     │
│  "cat"     │  [(doc1,1), (doc2,1)]     │
│  "dog"     │  [(doc4,2)]               │
│  ...       │  ...                       │
│                                         │
└─────────────────────────────────────────┘

Posting List 구조:
  (문서ID, 해당 문서에서의 출현 횟수)
```

### 토큰화 파이프라인
```
원본: "The Quick BROWN fox's"
      ↓
소문자 변환: "the quick brown fox's"
      ↓
구두점 제거: "the quick brown foxs"
      ↓
불용어 제거: "quick brown foxs"
      ↓
스테밍(선택): "quick brown fox"
      ↓
토큰: ["quick", "brown", "fox"]
```

---

## 💡 힌트

### 기본 구조
```java
public class DocumentIndexer {
    // 역인덱스: 단어 → (문서ID → 출현횟수)
    private final Map<String, Map<String, Integer>> invertedIndex;
    
    // 문서 저장소: 문서ID → 원본 내용
    private final Map<String, String> documents;
    
    // 문서별 단어 수
    private final Map<String, Integer> documentLengths;
    
    // 불용어 집합
    private final Set<String> stopWords;
    
    public DocumentIndexer() {
        this.invertedIndex = new HashMap<>();
        this.documents = new HashMap<>();
        this.documentLengths = new HashMap<>();
        this.stopWords = Set.of("the", "a", "an", "is", "are", "in", "on", "at");
    }
}
```

### 토큰화
```java
private List<String> tokenize(String content) {
    return Arrays.stream(content.toLowerCase()
            .replaceAll("[^a-z0-9\\s]", "")
            .split("\\s+"))
        .filter(token -> !token.isEmpty())
        .filter(token -> !stopWords.contains(token))
        .toList();
}
```

### 문서 인덱싱
```java
public void addDocument(String docId, String content) {
    documents.put(docId, content);
    
    List<String> tokens = tokenize(content);
    documentLengths.put(docId, tokens.size());
    
    // 단어별 빈도 계산
    Map<String, Integer> termFreq = new HashMap<>();
    for (String token : tokens) {
        termFreq.merge(token, 1, Integer::sum);
    }
    
    // 역인덱스 업데이트
    for (Map.Entry<String, Integer> entry : termFreq.entrySet()) {
        invertedIndex.computeIfAbsent(entry.getKey(), k -> new HashMap<>())
            .put(docId, entry.getValue());
    }
}
```

### TF-IDF 계산
```java
private double calculateTfIdf(String term, String docId) {
    Map<String, Integer> postings = invertedIndex.get(term);
    if (postings == null || !postings.containsKey(docId)) {
        return 0;
    }
    
    // TF: 정규화된 단어 빈도
    int termCount = postings.get(docId);
    int docLength = documentLengths.get(docId);
    double tf = (double) termCount / docLength;
    
    // IDF: 역문서 빈도
    int totalDocs = documents.size();
    int docsWithTerm = postings.size();
    double idf = Math.log((double) totalDocs / docsWithTerm);
    
    return tf * idf;
}
```

---

## ✅ 체크리스트

- [ ] 역인덱스 구현
- [ ] 토큰화 (소문자, 구두점 제거)
- [ ] 불용어 필터링
- [ ] 기본 검색 (AND/OR)
- [ ] TF-IDF 스코어링
- [ ] 검색 결과 랭킹
- [ ] 구문 검색 (선택)
- [ ] 와일드카드 검색 (선택)

---

## 📚 참고

- Apache Lucene
- Elasticsearch
- Apache Solr
- Information Retrieval 교재
