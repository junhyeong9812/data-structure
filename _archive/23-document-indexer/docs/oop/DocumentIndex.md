# oop/DocumentIndex.java

OOP 인터페이스 + 토크나이저 추상화 + 구현체.

```java
package com.datastructure.documentindexer.oop;

import java.util.List;

public interface DocumentIndex {
    enum Mode { AND, OR }

    void addDocument(String docId, String content);
    void removeDocument(String docId);

    List<String> search(String query, Mode mode);
    List<SearchResult> searchWithScore(String query, Mode mode);
    List<String> searchPhrase(String phrase);

    int documentCount();
    int termCount();

    class SearchResult {
        public final String docId;
        public final double score;

        public SearchResult(String docId, double score) {
            this.docId = docId;
            this.score = score;
        }
    }
}
```

---

# oop/Tokenizer.java

```java
package com.datastructure.documentindexer.oop;

import java.util.List;

public interface Tokenizer {
    List<String> tokenize(String content);
}
```

---

# oop/StandardTokenizer.java

```java
package com.datastructure.documentindexer.oop;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StandardTokenizer implements Tokenizer {
    private final Set<String> stopWords;

    public StandardTokenizer() {
        this(Set.of("the", "a", "an", "is", "are", "was", "were",
                "in", "on", "at", "to", "of", "and", "or", "but"));
    }

    public StandardTokenizer(Set<String> stopWords) {
        this.stopWords = stopWords;
    }

    @Override
    public List<String> tokenize(String content) {
        List<String> result = new ArrayList<>();
        if (content == null) return result;
        for (String t : content.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").split("\\s+")) {
            if (t.isEmpty() || stopWords.contains(t)) continue;
            result.add(t);
        }
        return result;
    }
}
```

---

# oop/InvertedIndex.java

```java
package com.datastructure.documentindexer.oop;

import java.util.*;

public class InvertedIndex implements DocumentIndex {
    private final Tokenizer tokenizer;
    private final Map<String, Map<String, Integer>> index = new HashMap<>();
    private final Map<String, Integer> lengths = new HashMap<>();
    private final Map<String, List<String>> docTokens = new HashMap<>();

    public InvertedIndex() {
        this(new StandardTokenizer());
    }

    public InvertedIndex(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    @Override
    public void addDocument(String docId, String content) {
        if (lengths.containsKey(docId)) removeDocument(docId);
        List<String> tokens = tokenizer.tokenize(content);
        lengths.put(docId, tokens.size());
        docTokens.put(docId, tokens);

        Map<String, Integer> tf = new HashMap<>();
        for (String t : tokens) tf.merge(t, 1, Integer::sum);
        for (Map.Entry<String, Integer> e : tf.entrySet()) {
            index.computeIfAbsent(e.getKey(), k -> new HashMap<>())
                    .put(docId, e.getValue());
        }
    }

    @Override
    public void removeDocument(String docId) {
        if (!lengths.containsKey(docId)) return;
        lengths.remove(docId);
        docTokens.remove(docId);
        Iterator<Map.Entry<String, Map<String, Integer>>> it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Map<String, Integer>> e = it.next();
            e.getValue().remove(docId);
            if (e.getValue().isEmpty()) it.remove();
        }
    }

    @Override
    public List<String> search(String query, Mode mode) {
        List<String> terms = tokenizer.tokenize(query);
        if (terms.isEmpty()) return Collections.emptyList();
        Set<String> result = null;
        for (String t : terms) {
            Set<String> docs = index.containsKey(t) ? index.get(t).keySet() : Set.of();
            if (result == null) result = new HashSet<>(docs);
            else if (mode == Mode.AND) result.retainAll(docs);
            else result.addAll(docs);
        }
        return new ArrayList<>(result == null ? Set.of() : result);
    }

    @Override
    public List<SearchResult> searchWithScore(String query, Mode mode) {
        List<String> docs = search(query, mode);
        List<String> terms = tokenizer.tokenize(query);
        List<SearchResult> out = new ArrayList<>();
        for (String docId : docs) {
            double score = 0.0;
            for (String t : terms) score += tfIdf(t, docId);
            out.add(new SearchResult(docId, score));
        }
        out.sort((a, b) -> Double.compare(b.score, a.score));
        return out;
    }

    @Override
    public List<String> searchPhrase(String phrase) {
        List<String> p = tokenizer.tokenize(phrase);
        List<String> out = new ArrayList<>();
        for (Map.Entry<String, List<String>> e : docTokens.entrySet()) {
            if (containsPhrase(e.getValue(), p)) out.add(e.getKey());
        }
        return out;
    }

    private boolean containsPhrase(List<String> tokens, List<String> phrase) {
        if (phrase.isEmpty()) return false;
        outer:
        for (int i = 0; i + phrase.size() <= tokens.size(); i++) {
            for (int j = 0; j < phrase.size(); j++) {
                if (!tokens.get(i + j).equals(phrase.get(j))) continue outer;
            }
            return true;
        }
        return false;
    }

    private double tfIdf(String term, String docId) {
        Map<String, Integer> postings = index.get(term);
        if (postings == null || !postings.containsKey(docId)) return 0.0;
        int count = postings.get(docId);
        int len = lengths.get(docId);
        double tf = (double) count / Math.max(1, len);
        double idf = Math.log((1.0 + lengths.size()) / (1.0 + postings.size())) + 1.0;
        return tf * idf;
    }

    @Override public int documentCount() { return lengths.size(); }
    @Override public int termCount() { return index.size(); }
}
```
