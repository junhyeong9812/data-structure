# 문서 인덱서 풀이 해설

## 📌 핵심 아이디어

문서 인덱서는 **역인덱스**를 사용하여 단어에서 문서로의 
매핑을 구축합니다. 이를 통해 O(1)에 특정 단어를 포함하는 
문서 목록을 조회할 수 있습니다.

**핵심 구성요소**:
- 역인덱스 (단어 → 문서 목록)
- 토큰화 (텍스트 → 단어 목록)
- TF-IDF (검색 관련성 점수)

---

## 🔑 핵심 개념

### 1. 역인덱스 자료구조
```java
// 기본 역인덱스
Map<String, Set<String>> invertedIndex;
// term → {docId1, docId2, ...}

// TF 포함 역인덱스
Map<String, Map<String, Integer>> invertedIndexWithTf;
// term → {docId → termFrequency}

// 위치 포함 역인덱스 (구문 검색용)
Map<String, Map<String, List<Integer>>> invertedIndexWithPositions;
// term → {docId → [position1, position2, ...]}
```

### 2. TF-IDF 공식
```
TF (Term Frequency):
  방법 1: 원시 빈도
    tf(t,d) = f(t,d)  (단어 t가 문서 d에 나타난 횟수)
  
  방법 2: 정규화
    tf(t,d) = f(t,d) / (문서 d의 총 단어 수)
  
  방법 3: 로그 스케일
    tf(t,d) = 1 + log(f(t,d))  (f > 0일 때)

IDF (Inverse Document Frequency):
  idf(t) = log(N / df(t))
  
  N = 전체 문서 수
  df(t) = 단어 t를 포함하는 문서 수

TF-IDF:
  tf-idf(t,d) = tf(t,d) × idf(t)
```

### 3. Boolean 검색
```java
// AND 검색: 모든 단어 포함
Set<String> andSearch(List<String> terms) {
    if (terms.isEmpty()) return Set.of();
    
    Set<String> result = new HashSet<>(
        invertedIndex.getOrDefault(terms.get(0), Set.of())
    );
    
    for (int i = 1; i < terms.size(); i++) {
        result.retainAll(
            invertedIndex.getOrDefault(terms.get(i), Set.of())
        );
    }
    
    return result;
}

// OR 검색: 하나 이상 단어 포함
Set<String> orSearch(List<String> terms) {
    Set<String> result = new HashSet<>();
    
    for (String term : terms) {
        result.addAll(
            invertedIndex.getOrDefault(term, Set.of())
        );
    }
    
    return result;
}
```

---

## 📝 POP 구현 해설

### 완전한 구현
```java
public class DocumentIndexer {
    private final Map<String, Map<String, Integer>> invertedIndex = new HashMap<>();
    private final Map<String, String> documents = new HashMap<>();
    private final Map<String, Integer> documentLengths = new HashMap<>();
    private final Set<String> stopWords;
    
    public DocumentIndexer() {
        this.stopWords = Set.of(
            "the", "a", "an", "is", "are", "was", "were",
            "in", "on", "at", "to", "for", "of", "and", "or"
        );
    }
    
    // 문서 추가
    public void addDocument(String docId, String content) {
        // 기존 문서가 있으면 먼저 제거
        if (documents.containsKey(docId)) {
            removeDocument(docId);
        }
        
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
    
    // 문서 제거
    public void removeDocument(String docId) {
        if (!documents.containsKey(docId)) return;
        
        String content = documents.remove(docId);
        documentLengths.remove(docId);
        
        List<String> tokens = tokenize(content);
        Set<String> uniqueTokens = new HashSet<>(tokens);
        
        for (String token : uniqueTokens) {
            Map<String, Integer> postings = invertedIndex.get(token);
            if (postings != null) {
                postings.remove(docId);
                if (postings.isEmpty()) {
                    invertedIndex.remove(token);
                }
            }
        }
    }
    
    // 토큰화
    private List<String> tokenize(String content) {
        return Arrays.stream(content.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .split("\\s+"))
            .filter(token -> !token.isEmpty())
            .filter(token -> token.length() > 1)
            .filter(token -> !stopWords.contains(token))
            .toList();
    }
    
    // 기본 검색 (AND)
    public List<String> search(String query) {
        return searchWithScore(query).stream()
            .map(SearchResult::docId)
            .toList();
    }
    
    // 점수와 함께 검색
    public List<SearchResult> searchWithScore(String query) {
        List<String> queryTerms = tokenize(query);
        if (queryTerms.isEmpty()) {
            return List.of();
        }
        
        // AND 검색으로 후보 문서 찾기
        Set<String> candidates = andSearch(queryTerms);
        
        // TF-IDF 점수 계산
        List<SearchResult> results = new ArrayList<>();
        for (String docId : candidates) {
            double score = 0;
            for (String term : queryTerms) {
                score += calculateTfIdf(term, docId);
            }
            results.add(new SearchResult(docId, score));
        }
        
        // 점수 내림차순 정렬
        results.sort((a, b) -> Double.compare(b.score(), a.score()));
        
        return results;
    }
    
    // OR 검색
    public List<SearchResult> searchOr(String query) {
        List<String> queryTerms = tokenize(query);
        if (queryTerms.isEmpty()) {
            return List.of();
        }
        
        Set<String> candidates = orSearch(queryTerms);
        
        List<SearchResult> results = new ArrayList<>();
        for (String docId : candidates) {
            double score = 0;
            for (String term : queryTerms) {
                score += calculateTfIdf(term, docId);
            }
            results.add(new SearchResult(docId, score));
        }
        
        results.sort((a, b) -> Double.compare(b.score(), a.score()));
        return results;
    }
    
    private Set<String> andSearch(List<String> terms) {
        if (terms.isEmpty()) return Set.of();
        
        Map<String, Integer> firstPostings = invertedIndex.get(terms.get(0));
        if (firstPostings == null) return Set.of();
        
        Set<String> result = new HashSet<>(firstPostings.keySet());
        
        for (int i = 1; i < terms.size(); i++) {
            Map<String, Integer> postings = invertedIndex.get(terms.get(i));
            if (postings == null) return Set.of();
            result.retainAll(postings.keySet());
        }
        
        return result;
    }
    
    private Set<String> orSearch(List<String> terms) {
        Set<String> result = new HashSet<>();
        for (String term : terms) {
            Map<String, Integer> postings = invertedIndex.get(term);
            if (postings != null) {
                result.addAll(postings.keySet());
            }
        }
        return result;
    }
    
    private double calculateTfIdf(String term, String docId) {
        Map<String, Integer> postings = invertedIndex.get(term);
        if (postings == null || !postings.containsKey(docId)) {
            return 0;
        }
        
        // TF
        int termCount = postings.get(docId);
        int docLength = documentLengths.get(docId);
        double tf = (double) termCount / docLength;
        
        // IDF
        int totalDocs = documents.size();
        int docsWithTerm = postings.size();
        double idf = Math.log((double) totalDocs / docsWithTerm);
        
        return tf * idf;
    }
    
    // 통계
    public int getDocumentCount() {
        return documents.size();
    }
    
    public int getTermCount() {
        return invertedIndex.size();
    }
}
```

### SearchResult Record
```java
public record SearchResult(String docId, double score) {}
```

### 구문 검색 (위치 기반)
```java
public class PhraseSearchIndexer {
    // 위치 포함 역인덱스
    private final Map<String, Map<String, List<Integer>>> invertedIndex = new HashMap<>();
    
    public void addDocument(String docId, String content) {
        List<String> tokens = tokenize(content);
        
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            invertedIndex.computeIfAbsent(token, k -> new HashMap<>())
                .computeIfAbsent(docId, k -> new ArrayList<>())
                .add(i);
        }
    }
    
    // 구문 검색
    public List<String> phraseSearch(String phrase) {
        List<String> terms = tokenize(phrase);
        if (terms.isEmpty()) return List.of();
        
        // 첫 단어를 포함하는 문서들
        Map<String, List<Integer>> firstPostings = invertedIndex.get(terms.get(0));
        if (firstPostings == null) return List.of();
        
        List<String> results = new ArrayList<>();
        
        for (String docId : firstPostings.keySet()) {
            if (containsPhrase(docId, terms)) {
                results.add(docId);
            }
        }
        
        return results;
    }
    
    private boolean containsPhrase(String docId, List<String> terms) {
        List<Integer> positions = invertedIndex.get(terms.get(0)).get(docId);
        
        for (int startPos : positions) {
            boolean found = true;
            for (int i = 1; i < terms.size(); i++) {
                Map<String, List<Integer>> postings = invertedIndex.get(terms.get(i));
                if (postings == null || !postings.containsKey(docId)) {
                    found = false;
                    break;
                }
                
                List<Integer> termPositions = postings.get(docId);
                if (!termPositions.contains(startPos + i)) {
                    found = false;
                    break;
                }
            }
            
            if (found) return true;
        }
        
        return false;
    }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 |
|------|-----------|
| addDocument | O(n) |
| removeDocument | O(n) |
| search (AND) | O(k × m) |
| search (OR) | O(k × m) |
| TF-IDF 계산 | O(1) |

n = 문서의 단어 수
k = 검색어 수
m = 검색어당 평균 문서 수

---

## ❌ 흔한 실수

### 1. 빈 토큰 처리
```java
// 잘못됨: 빈 토큰 필터링 누락
String[] tokens = content.split("\\s+");
// "  hello  world  " → ["", "hello", "", "world", ""]

// 올바름: 빈 토큰 필터링
Arrays.stream(content.split("\\s+"))
    .filter(t -> !t.isEmpty())
    ...
```

### 2. 문서 제거 시 역인덱스 정리
```java
// 잘못됨: 역인덱스에서 제거 안 함
public void removeDocument(String docId) {
    documents.remove(docId);
    // 역인덱스에 여전히 참조 남아있음!
}

// 올바름: 역인덱스에서도 제거
public void removeDocument(String docId) {
    String content = documents.remove(docId);
    for (String token : tokenize(content)) {
        Map<String, Integer> postings = invertedIndex.get(token);
        if (postings != null) {
            postings.remove(docId);
            if (postings.isEmpty()) {
                invertedIndex.remove(token);
            }
        }
    }
}
```

### 3. IDF에서 0으로 나누기
```java
// 잘못됨: docsWithTerm이 0일 때
double idf = Math.log(totalDocs / docsWithTerm);  // 0으로 나눔!

// 올바름: 체크 추가
if (docsWithTerm == 0) return 0;
double idf = Math.log((double) totalDocs / docsWithTerm);
```

---

## 🔗 관련 문제

- 검색 엔진 설계
- 자동 완성 시스템
- 유사 문서 찾기
- 스팸 필터
