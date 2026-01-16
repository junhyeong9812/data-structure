# 문서 인덱서 구현에 유용한 Java API

## 📦 문자열 처리

### 정규식
```java
import java.util.regex.Pattern;
import java.util.regex.Matcher;

// 소문자 변환
String lower = text.toLowerCase();

// 구두점 제거
String clean = text.replaceAll("[^a-zA-Z0-9\\s]", "");

// 공백으로 분리
String[] tokens = text.split("\\s+");

// 여러 공백을 하나로
String normalized = text.replaceAll("\\s+", " ").trim();

// 패턴 컴파일 (재사용)
Pattern NON_ALPHA = Pattern.compile("[^a-z0-9\\s]");
String clean = NON_ALPHA.matcher(text).replaceAll("");
```

### String 메서드
```java
// 포함 여부
boolean contains = text.contains("keyword");

// 시작/끝 확인
text.startsWith("prefix");
text.endsWith("suffix");

// 부분 문자열
String sub = text.substring(start, end);

// 공백 제거
String trimmed = text.trim();
String stripped = text.strip();  // Java 11+, 유니코드 공백도 처리

// 분리/결합
String[] parts = text.split(",");
String joined = String.join(", ", parts);
```

### StringBuilder
```java
StringBuilder sb = new StringBuilder();
sb.append("hello");
sb.append(" ");
sb.append("world");
String result = sb.toString();

// 또는
String result = new StringBuilder()
    .append("hello")
    .append(" ")
    .append("world")
    .toString();
```

---

## 📊 컬렉션

### Map 연산
```java
import java.util.HashMap;
import java.util.Map;

Map<String, Integer> wordCount = new HashMap<>();

// 카운팅 (merge)
wordCount.merge("word", 1, Integer::sum);

// 없으면 기본값
int count = wordCount.getOrDefault("word", 0);

// 없으면 생성 (computeIfAbsent)
invertedIndex.computeIfAbsent(term, k -> new HashMap<>())
    .put(docId, frequency);

// 있으면 수정 (computeIfPresent)
wordCount.computeIfPresent("word", (k, v) -> v + 1);

// 조건부 제거
wordCount.entrySet().removeIf(e -> e.getValue() < 2);
```

### Set 연산
```java
import java.util.HashSet;
import java.util.Set;

Set<String> set1 = new HashSet<>(List.of("a", "b", "c"));
Set<String> set2 = new HashSet<>(List.of("b", "c", "d"));

// 교집합 (AND 검색)
Set<String> intersection = new HashSet<>(set1);
intersection.retainAll(set2);  // {"b", "c"}

// 합집합 (OR 검색)
Set<String> union = new HashSet<>(set1);
union.addAll(set2);  // {"a", "b", "c", "d"}

// 차집합
Set<String> difference = new HashSet<>(set1);
difference.removeAll(set2);  // {"a"}
```

### List 정렬
```java
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

List<SearchResult> results = new ArrayList<>();

// 점수 내림차순 정렬
results.sort((a, b) -> Double.compare(b.score(), a.score()));

// 또는 Comparator 사용
results.sort(Comparator.comparingDouble(SearchResult::score).reversed());

// 다중 조건 정렬
results.sort(Comparator
    .comparingDouble(SearchResult::score).reversed()
    .thenComparing(SearchResult::docId));
```

---

## 🔢 수학

### Math 클래스
```java
// 로그 (IDF 계산)
double idf = Math.log(totalDocs / (double) docsWithTerm);

// 자연로그
double ln = Math.log(x);

// 상용로그
double log10 = Math.log10(x);

// 제곱근
double sqrt = Math.sqrt(x);

// 최대/최소
double max = Math.max(a, b);
double min = Math.min(a, b);
```

### 코사인 유사도
```java
// 두 벡터의 코사인 유사도
public double cosineSimilarity(double[] a, double[] b) {
    double dotProduct = 0;
    double normA = 0;
    double normB = 0;
    
    for (int i = 0; i < a.length; i++) {
        dotProduct += a[i] * b[i];
        normA += a[i] * a[i];
        normB += b[i] * b[i];
    }
    
    if (normA == 0 || normB == 0) return 0;
    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
}
```

---

## 🔄 Stream API

### 토큰화
```java
import java.util.Arrays;
import java.util.stream.Collectors;

// 토큰화 파이프라인
List<String> tokens = Arrays.stream(content.toLowerCase()
        .replaceAll("[^a-z0-9\\s]", "")
        .split("\\s+"))
    .filter(token -> !token.isEmpty())
    .filter(token -> token.length() > 1)
    .filter(token -> !stopWords.contains(token))
    .toList();
```

### 집계
```java
// 단어 빈도 계산
Map<String, Long> wordFreq = tokens.stream()
    .collect(Collectors.groupingBy(
        Function.identity(),
        Collectors.counting()
    ));

// 또는 Integer로
Map<String, Integer> wordCount = new HashMap<>();
tokens.forEach(t -> wordCount.merge(t, 1, Integer::sum));
```

### 정렬 및 제한
```java
// 상위 N개
List<SearchResult> topN = results.stream()
    .sorted(Comparator.comparingDouble(SearchResult::score).reversed())
    .limit(10)
    .toList();
```

---

## 🧪 테스트

### AssertJ
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldIndexAndSearch() {
    DocumentIndexer indexer = new DocumentIndexer();
    
    indexer.addDocument("doc1", "hello world");
    indexer.addDocument("doc2", "hello java");
    indexer.addDocument("doc3", "goodbye world");
    
    List<String> results = indexer.search("hello");
    
    assertThat(results).containsExactlyInAnyOrder("doc1", "doc2");
}

@Test
void shouldRankByRelevance() {
    DocumentIndexer indexer = new DocumentIndexer();
    
    indexer.addDocument("doc1", "java java java");
    indexer.addDocument("doc2", "java python");
    
    List<SearchResult> results = indexer.searchWithScore("java");
    
    // doc1이 더 높은 TF로 상위
    assertThat(results.get(0).docId()).isEqualTo("doc1");
    assertThat(results.get(0).score()).isGreaterThan(results.get(1).score());
}

@Test
void shouldHandleAndSearch() {
    DocumentIndexer indexer = new DocumentIndexer();
    
    indexer.addDocument("doc1", "cat dog");
    indexer.addDocument("doc2", "cat bird");
    indexer.addDocument("doc3", "dog bird");
    
    List<String> results = indexer.search("cat dog");
    
    assertThat(results).containsExactly("doc1");
}
```

---

## 📚 Java 21 관련

### Record
```java
// 검색 결과
public record SearchResult(String docId, double score) 
    implements Comparable<SearchResult> {
    
    @Override
    public int compareTo(SearchResult other) {
        return Double.compare(other.score, this.score);  // 내림차순
    }
}

// 포스팅
public record Posting(String docId, int frequency, List<Integer> positions) {}

// 문서
public record Document(String id, String content, Map<String, Object> metadata) {}
```

### Pattern Matching
```java
public List<String> search(Object query) {
    return switch (query) {
        case String s -> searchText(s);
        case List<?> list -> searchMultiple((List<String>) list);
        case SearchQuery q -> executeQuery(q);
        default -> List.of();
    };
}
```

### Text Blocks
```java
String stopWordsConfig = """
    the
    a
    an
    is
    are
    was
    were
    """;

Set<String> stopWords = Arrays.stream(stopWordsConfig.split("\\n"))
    .map(String::trim)
    .filter(s -> !s.isEmpty())
    .collect(Collectors.toSet());
```

---

## ⚡ 성능 팁

### 1. 불용어 Set 사용
```java
// 느림: List 검색
List<String> stopWords = List.of("the", "a", ...);
if (stopWords.contains(token)) ...  // O(n)

// 빠름: Set 검색
Set<String> stopWords = Set.of("the", "a", ...);
if (stopWords.contains(token)) ...  // O(1)
```

### 2. 정규식 패턴 재사용
```java
// 느림: 매번 컴파일
text.replaceAll("[^a-z0-9]", "");

// 빠름: 미리 컴파일
private static final Pattern NON_ALPHANUM = Pattern.compile("[^a-z0-9]");
NON_ALPHANUM.matcher(text).replaceAll("");
```

### 3. 작은 역인덱스 먼저 처리
```java
// AND 검색 시 가장 작은 포스팅 리스트부터
List<String> sortedTerms = terms.stream()
    .sorted(Comparator.comparingInt(t -> 
        invertedIndex.getOrDefault(t, Map.of()).size()))
    .toList();
```
