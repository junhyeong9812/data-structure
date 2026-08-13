# pop/DocumentIndexer.java

역인덱스 + TF-IDF 점수 + AND/OR/구문 검색.

```java
package com.datastructure.documentindexer.pop;

import java.util.*;

public class DocumentIndexer {

    public enum Mode { AND, OR }

    public static class SearchResult {
        public final String docId;
        public final double score;

        public SearchResult(String docId, double score) {
            this.docId = docId;
            this.score = score;
        }

        @Override
        public String toString() {
            return docId + "=" + score;
        }
    }

    private final Map<String, Map<String, Integer>> invertedIndex = new HashMap<>();
    private final Map<String, String> documents = new HashMap<>();
    private final Map<String, Integer> documentLengths = new HashMap<>();
    private final Map<String, List<String>> documentTokens = new HashMap<>();
    private final Set<String> stopWords;

    public DocumentIndexer() {
        this(Set.of("the", "a", "an", "is", "are", "was", "were",
                "in", "on", "at", "to", "of", "and", "or", "but"));
    }

    public DocumentIndexer(Set<String> stopWords) {
        this.stopWords = stopWords;
    }

    public void addDocument(String docId, String content) {
        if (documents.containsKey(docId)) removeDocument(docId);
        documents.put(docId, content);

        List<String> tokens = tokenize(content);
        documentLengths.put(docId, tokens.size());
        documentTokens.put(docId, tokens);

        Map<String, Integer> tf = new HashMap<>();
        for (String t : tokens) tf.merge(t, 1, Integer::sum);
        for (Map.Entry<String, Integer> e : tf.entrySet()) {
            invertedIndex.computeIfAbsent(e.getKey(), k -> new HashMap<>())
                    .put(docId, e.getValue());
        }
    }

    public void removeDocument(String docId) {
        if (!documents.containsKey(docId)) return;
        documents.remove(docId);
        documentLengths.remove(docId);
        documentTokens.remove(docId);
        Iterator<Map.Entry<String, Map<String, Integer>>> it = invertedIndex.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Map<String, Integer>> e = it.next();
            e.getValue().remove(docId);
            if (e.getValue().isEmpty()) it.remove();
        }
    }

    public List<String> search(String query) {
        return search(query, Mode.AND);
    }

    public List<String> search(String query, Mode mode) {
        List<String> terms = tokenize(query);
        if (terms.isEmpty()) return Collections.emptyList();

        Set<String> result = null;
        for (String term : terms) {
            Set<String> docs = invertedIndex.containsKey(term)
                    ? invertedIndex.get(term).keySet()
                    : Collections.emptySet();
            if (result == null) result = new HashSet<>(docs);
            else if (mode == Mode.AND) result.retainAll(docs);
            else result.addAll(docs);
        }
        return new ArrayList<>(result == null ? Set.of() : result);
    }

    public List<SearchResult> searchWithScore(String query) {
        return searchWithScore(query, Mode.AND);
    }

    public List<SearchResult> searchWithScore(String query, Mode mode) {
        List<String> docs = search(query, mode);
        List<String> terms = tokenize(query);
        List<SearchResult> results = new ArrayList<>();
        for (String docId : docs) {
            double score = 0.0;
            for (String term : terms) score += tfIdf(term, docId);
            results.add(new SearchResult(docId, score));
        }
        results.sort((a, b) -> Double.compare(b.score, a.score));
        return results;
    }

    /** 구문 검색: 토큰 리스트가 문서에 연속해서 나타나는지. */
    public List<String> searchPhrase(String phrase) {
        List<String> phraseTokens = tokenize(phrase);
        if (phraseTokens.isEmpty()) return Collections.emptyList();
        List<String> matches = new ArrayList<>();
        for (Map.Entry<String, List<String>> e : documentTokens.entrySet()) {
            if (containsPhrase(e.getValue(), phraseTokens)) matches.add(e.getKey());
        }
        return matches;
    }

    private boolean containsPhrase(List<String> tokens, List<String> phrase) {
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
        Map<String, Integer> postings = invertedIndex.get(term);
        if (postings == null || !postings.containsKey(docId)) return 0.0;
        int count = postings.get(docId);
        int len = documentLengths.get(docId);
        double tf = (double) count / Math.max(1, len);
        double idf = Math.log((1.0 + documents.size()) / (1.0 + postings.size())) + 1.0;
        return tf * idf;
    }

    private List<String> tokenize(String content) {
        if (content == null) return Collections.emptyList();
        String cleaned = content.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
        List<String> result = new ArrayList<>();
        for (String t : cleaned.split("\\s+")) {
            if (t.isEmpty() || stopWords.contains(t)) continue;
            result.add(t);
        }
        return result;
    }

    public int documentCount() {
        return documents.size();
    }

    public int termCount() {
        return invertedIndex.size();
    }
}
```
