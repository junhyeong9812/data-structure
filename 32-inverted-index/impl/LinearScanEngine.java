package com.datastructure.searchindex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * [구현] 전수 조사 기준선. 원문을 그대로 들고 질의마다 전부 훑는다.
 *
 * 색인이 없다. 문서를 넣을 때 아무것도 안 만들고, 물어보면 그때 다 읽는다.
 * 그래서 넣는 것이 O(1) 이고 찾는 것이 문서 수에 정비례한다.
 *
 * 이 클래스가 쉽다는 것이 요점이다. 역색인이 하는 일은 이것과 답이 같아야 한다.
 * 교집합 병합은 조건 하나만 틀려도 "봐야 하는 문서를 안 본다"가 되는데,
 * 그러면 예외도 안 나고 결과가 조용히 몇 개 빠진다. 그 조용한 누락을 잡는 유일한 방법이 이 대조다.
 *
 * 문서를 TreeMap 에 담아 문서 번호 오름차순으로 훑는다. 순서가 정해져야 답이 하나로 정해진다.
 */
public class LinearScanEngine implements SearchEngine, SearchStats {

    private final Analyzer indexAnalyzer;
    private final Analyzer queryAnalyzer;
    private final Scorer scorer;
    private final Map<Integer, String> documents = new TreeMap<>();

    private long visitedDocs;

    public LinearScanEngine() {
        this(new StandardAnalyzer(), new TfIdfScorer());
    }

    public LinearScanEngine(Analyzer analyzer, Scorer scorer) {
        this(analyzer, analyzer, scorer);
    }

    /** 색인 분석기와 질의 분석기를 따로 받는다. 다르게 줄 수 있다는 것이 이 박스의 함정 하나다. */
    public LinearScanEngine(Analyzer indexAnalyzer, Analyzer queryAnalyzer, Scorer scorer) {
        if (indexAnalyzer == null || queryAnalyzer == null || scorer == null) {
            throw new IllegalArgumentException("분석기와 채점기는 null 일 수 없다");
        }
        this.indexAnalyzer = indexAnalyzer;
        this.queryAnalyzer = queryAnalyzer;
        this.scorer = scorer;
    }

    @Override
    public void index(int docId, String text) {
        if (text == null) {
            throw new IllegalArgumentException("본문이 null 이다");
        }
        if (docId < 0) {
            throw new IllegalArgumentException("문서 번호는 0 이상이다: " + docId);
        }
        if (documents.containsKey(docId)) {
            throw new IllegalArgumentException("이미 색인된 문서 번호다: " + docId);
        }
        documents.put(docId, text);
    }

    @Override
    public int docCount() {
        return documents.size();
    }

    /** 항 개수를 물어도 전부 다시 분석한다. 저장해둔 것이 없기 때문이다. */
    @Override
    public int termCount() {
        Set<String> terms = new HashSet<>();
        for (String text : documents.values()) {
            terms.addAll(indexAnalyzer.analyze(text));
        }
        return terms.size();
    }

    /**
     * 전부 훑는다. 한 번만 훑는다.
     *
     * 문서 하나를 볼 때 질의어별 빈도를 세고, 그 자리에서 df 도 같이 올린다.
     * 두 번 훑으면 방문 수가 2n 이 되어 "질의마다 정확히 n" 이라는 자가 망가진다.
     */
    @Override
    public List<SearchResult> search(String query, int k) {
        if (query == null) {
            throw new IllegalArgumentException("질의가 null 이다");
        }
        if (k < 0) {
            throw new IllegalArgumentException("k 는 0 이상이다: " + k);
        }
        visitedDocs = 0;
        List<String> terms = SearchEngine.distinctTerms(queryAnalyzer.analyze(query));
        if (terms.isEmpty() || k == 0) {
            return List.of();
        }

        int t = terms.size();
        int[] documentFrequency = new int[t];
        Map<Integer, int[]> matched = new TreeMap<>();

        for (Map.Entry<Integer, String> entry : documents.entrySet()) {
            visitedDocs++;
            int[] counts = new int[t];
            for (String term : indexAnalyzer.analyze(entry.getValue())) {
                int i = terms.indexOf(term);
                if (i >= 0) {
                    counts[i]++;
                }
            }
            boolean hasAll = true;
            for (int i = 0; i < t; i++) {
                if (counts[i] > 0) {
                    documentFrequency[i]++;
                } else {
                    hasAll = false;
                }
            }
            if (hasAll) {
                matched.put(entry.getKey(), counts);
            }
        }

        int n = documents.size();
        List<SearchResult> results = new ArrayList<>();
        for (Map.Entry<Integer, int[]> entry : matched.entrySet()) {
            double sum = 0.0;
            int[] counts = entry.getValue();
            for (int i = 0; i < t; i++) {
                sum += scorer.score(counts[i], documentFrequency[i], n);
            }
            results.add(new SearchResult(entry.getKey(), sum));
        }
        Collections.sort(results);
        return List.copyOf(results.subList(0, Math.min(k, results.size())));
    }

    /**
     * 구문 검색도 전부 훑는다.
     *
     * 원문을 들고 있으므로 위치를 따로 안 담아도 된다. 다시 분석하면 순서가 그대로 나온다.
     * 역색인은 원문을 버렸기 때문에 위치를 저장해두지 않으면 이 질의를 아예 못 한다.
     */
    @Override
    public List<Integer> searchPhrase(String phrase) {
        if (phrase == null) {
            throw new IllegalArgumentException("구문이 null 이다");
        }
        visitedDocs = 0;
        List<String> terms = queryAnalyzer.analyze(phrase);
        if (terms.isEmpty()) {
            return List.of();
        }
        List<Integer> out = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : documents.entrySet()) {
            visitedDocs++;
            List<String> tokens = indexAnalyzer.analyze(entry.getValue());
            for (int start = 0; start + terms.size() <= tokens.size(); start++) {
                if (tokens.subList(start, start + terms.size()).equals(terms)) {
                    out.add(entry.getKey());
                    break;
                }
            }
        }
        return List.copyOf(out);
    }

    @Override
    public long visitedDocs() {
        return visitedDocs;
    }

    /** 전수 조사에는 병합이 없다. 늘 0 이다. */
    @Override
    public long comparisons() {
        return 0;
    }

    @Override
    public String toString() {
        return "전수 조사(" + documents.size() + "개 문서)";
    }
}
