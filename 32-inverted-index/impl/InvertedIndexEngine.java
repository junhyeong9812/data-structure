package com.datastructure.searchindex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * [구현] 색인의 방향을 뒤집는다.
 *
 *   정방향 색인   문서 -&gt; 그 안의 항들       문서를 보여줄 때 쓴다
 *   역색인        항 -&gt; 그 항이 있는 문서들  검색할 때 쓴다
 *
 * "고양이" 를 찾으면 그 항의 포스팅 리스트만 보면 된다. 나머지 문서는 열어보지도 않는다.
 * "고양이 AND 강아지" 는 두 리스트의 교집합이다.
 *
 * 포스팅 리스트를 문서 번호 오름차순으로 유지하는 것이 이 구조의 값을 만든다.
 * 정렬돼 있으면 교집합을 한 번 훑기로 한다. 안 돼 있으면 한쪽마다 상대를 다 뒤져야 한다.
 *
 * 참고: 필드 이름 index, indexedDocIds 와 메서드 postings, postingCount, positionCount 를
 * 테스트가 직접 들여다본다.
 */
public class InvertedIndexEngine implements SearchEngine, SearchStats {

    private final Analyzer indexAnalyzer;
    private final Analyzer queryAnalyzer;
    private final Scorer scorer;
    private final MergeOrder mergeOrder;

    /** 항 -> 포스팅 리스트. 리스트는 늘 문서 번호 오름차순이다. */
    final Map<String, List<Posting>> index = new HashMap<>();

    /** 색인에 들어온 문서 번호. 항이 하나도 없는 문서도 여기에는 들어간다. */
    final Set<Integer> indexedDocIds = new TreeSet<>();

    private long visitedDocs;
    private long comparisons;

    public InvertedIndexEngine() {
        this(new StandardAnalyzer(), new TfIdfScorer());
    }

    public InvertedIndexEngine(Analyzer analyzer, Scorer scorer) {
        this(analyzer, analyzer, scorer, MergeOrder.SHORTEST_FIRST);
    }

    public InvertedIndexEngine(Analyzer analyzer, Scorer scorer, MergeOrder mergeOrder) {
        this(analyzer, analyzer, scorer, mergeOrder);
    }

    public InvertedIndexEngine(Analyzer indexAnalyzer, Analyzer queryAnalyzer, Scorer scorer) {
        this(indexAnalyzer, queryAnalyzer, scorer, MergeOrder.SHORTEST_FIRST);
    }

    public InvertedIndexEngine(Analyzer indexAnalyzer, Analyzer queryAnalyzer,
                               Scorer scorer, MergeOrder mergeOrder) {
        if (indexAnalyzer == null || queryAnalyzer == null || scorer == null || mergeOrder == null) {
            throw new IllegalArgumentException("분석기, 채점기, 병합 순서는 null 일 수 없다");
        }
        this.indexAnalyzer = indexAnalyzer;
        this.queryAnalyzer = queryAnalyzer;
        this.scorer = scorer;
        this.mergeOrder = mergeOrder;
    }

    // ------------------------------------------------------------------ 색인

    @Override
    public void index(int docId, String text) {
        if (text == null) {
            throw new IllegalArgumentException("본문이 null 이다");
        }
        if (docId < 0) {
            throw new IllegalArgumentException("문서 번호는 0 이상이다: " + docId);
        }
        if (!indexedDocIds.add(docId)) {
            throw new IllegalArgumentException("이미 색인된 문서 번호다: " + docId);
        }

        // 문서 하나 안에서 항별로 모은다. 같은 항이 여러 번 나와도 포스팅은 하나다.
        Map<String, Posting> perDocument = new LinkedHashMap<>();
        List<String> terms = indexAnalyzer.analyze(text);
        for (int position = 0; position < terms.size(); position++) {
            perDocument.computeIfAbsent(terms.get(position), term -> new Posting(docId))
                    .addPosition(position);
        }
        for (Map.Entry<String, Posting> entry : perDocument.entrySet()) {
            insertSorted(index.computeIfAbsent(entry.getKey(), term -> new ArrayList<>()),
                    entry.getValue());
        }
    }

    /**
     * 포스팅을 문서 번호 오름차순 자리에 꽂는다.
     *
     * 문서를 번호 순서대로 넣으면 늘 맨 뒤가 정답이라 append 만 해도 맞다.
     * 그런데 순서가 뒤섞여 들어오는 순간 append 는 리스트를 망가뜨리고,
     * 그러면 교집합 병합이 조용히 답을 빠뜨린다.
     */
    private static void insertSorted(List<Posting> postings, Posting posting) {
        int low = 0;
        int high = postings.size();
        while (low < high) {
            int mid = (low + high) >>> 1;
            if (postings.get(mid).docId() < posting.docId()) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        postings.add(low, posting);
    }

    @Override
    public int docCount() {
        return indexedDocIds.size();
    }

    /** 맵 크기 하나다. 전수 조사는 이 값을 알려면 전부 다시 분석해야 한다. */
    @Override
    public int termCount() {
        return index.size();
    }

    /** 항 하나의 포스팅 리스트. 없으면 빈 목록. 밖에서 못 고친다. */
    public List<Posting> postings(String term) {
        List<Posting> found = index.get(term);
        return found == null ? List.of() : Collections.unmodifiableList(found);
    }

    /** 색인에 든 (항, 문서) 쌍의 개수. 색인의 크기를 재는 첫 번째 자다. */
    public long postingCount() {
        long total = 0;
        for (List<Posting> postings : index.values()) {
            total += postings.size();
        }
        return total;
    }

    /** 색인에 든 위치 정수의 개수. 원문의 항 개수와 같아야 한다. 두 번째 자다. */
    public long positionCount() {
        long total = 0;
        for (List<Posting> postings : index.values()) {
            for (Posting posting : postings) {
                total += posting.frequency();
            }
        }
        return total;
    }

    // ------------------------------------------------------------------ 질의

    /**
     * 질의어를 전부 가진 문서 번호를 오름차순으로.
     *
     * 짧은 리스트부터 병합한다. 중간 결과는 절대 커지지 않으므로,
     * 작은 것으로 시작하면 그 뒤의 모든 병합이 작은 쪽 길이에 묶인다.
     * 흔한 말부터 시작하면 첫 병합에서 긴 리스트를 통째로 훑고, 그 일은 되돌릴 수 없다.
     */
    private List<Integer> intersect(List<String> terms) {
        List<List<Posting>> lists = new ArrayList<>();
        for (String term : terms) {
            List<Posting> postings = index.get(term);
            if (postings == null) {
                return List.of();       // 하나라도 없으면 AND 의 답은 공집합이다
            }
            lists.add(postings);
        }
        if (mergeOrder == MergeOrder.SHORTEST_FIRST) {
            lists.sort(Comparator.comparingInt(List::size));
        }

        List<Integer> merged = new ArrayList<>();
        for (Posting posting : lists.get(0)) {
            merged.add(posting.docId());
            visitedDocs++;
        }
        for (int li = 1; li < lists.size() && !merged.isEmpty(); li++) {
            List<Posting> other = lists.get(li);
            List<Integer> next = new ArrayList<>();
            int i = 0;
            int j = 0;
            while (i < merged.size() && j < other.size()) {
                comparisons++;
                int left = merged.get(i);
                int right = other.get(j).docId();
                if (left == right) {
                    next.add(left);
                    i++;
                    j++;
                    visitedDocs += 2;
                } else if (left < right) {
                    i++;
                    visitedDocs++;
                } else {
                    j++;
                    visitedDocs++;
                }
            }
            merged = next;
        }
        return merged;
    }

    @Override
    public List<SearchResult> search(String query, int k) {
        if (query == null) {
            throw new IllegalArgumentException("질의가 null 이다");
        }
        if (k < 0) {
            throw new IllegalArgumentException("k 는 0 이상이다: " + k);
        }
        visitedDocs = 0;
        comparisons = 0;
        List<String> terms = SearchEngine.distinctTerms(queryAnalyzer.analyze(query));
        if (terms.isEmpty() || k == 0) {
            return List.of();
        }
        List<Integer> candidates = intersect(terms);
        if (candidates.isEmpty()) {
            return List.of();
        }

        // df 는 포스팅 리스트의 길이다. 전수 조사가 전부 훑어야 얻는 값을 여기서는 그냥 읽는다.
        int t = terms.size();
        int[] documentFrequency = new int[t];
        for (int i = 0; i < t; i++) {
            documentFrequency[i] = index.get(terms.get(i)).size();
        }

        int n = docCount();
        List<SearchResult> results = new ArrayList<>();
        for (int docId : candidates) {
            double sum = 0.0;
            // 병합 순서가 아니라 질의어 순서로 더한다. 그래야 전수 조사와 합이 비트까지 같다.
            for (int i = 0; i < t; i++) {
                sum += scorer.score(termFrequency(terms.get(i), docId), documentFrequency[i], n);
            }
            results.add(new SearchResult(docId, sum));
        }
        Collections.sort(results);
        return List.copyOf(results.subList(0, Math.min(k, results.size())));
    }

    @Override
    public List<Integer> searchPhrase(String phrase) {
        if (phrase == null) {
            throw new IllegalArgumentException("구문이 null 이다");
        }
        visitedDocs = 0;
        comparisons = 0;
        // 여기서는 중복을 안 걷어낸다. 순서와 반복 자체가 질의의 뜻이기 때문이다.
        List<String> terms = queryAnalyzer.analyze(phrase);
        if (terms.isEmpty()) {
            return List.of();
        }
        List<Integer> out = new ArrayList<>();
        for (int docId : intersect(SearchEngine.distinctTerms(terms))) {
            if (containsPhrase(terms, docId)) {
                out.add(docId);
            }
        }
        return List.copyOf(out);
    }

    /**
     * 이 문서에 항들이 그 순서 그대로 붙어 있나.
     *
     * 첫 항이 나온 자리마다 그 뒤가 이어지는지 본다. 첫 항이 p 에 있으면
     * i 번째 항은 p 더하기 i 에 있어야 한다.
     */
    private boolean containsPhrase(List<String> terms, int docId) {
        for (int start : positionsOf(terms.get(0), docId)) {
            boolean ok = true;
            for (int i = 1; i < terms.size(); i++) {
                if (!positionsOf(terms.get(i), docId).contains(start + i)) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                return true;
            }
        }
        return false;
    }

    private List<Integer> positionsOf(String term, int docId) {
        Posting posting = find(index.get(term), docId);
        return posting == null ? List.of() : posting.positions();
    }

    private int termFrequency(String term, int docId) {
        Posting posting = find(index.get(term), docId);
        return posting == null ? 0 : posting.frequency();
    }

    /** 오름차순 포스팅 리스트에서 문서 번호를 이분 탐색으로 찾는다. */
    private static Posting find(List<Posting> postings, int docId) {
        if (postings == null) {
            return null;
        }
        int low = 0;
        int high = postings.size() - 1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            int current = postings.get(mid).docId();
            if (current == docId) {
                return postings.get(mid);
            }
            if (current < docId) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }

    @Override
    public long visitedDocs() {
        return visitedDocs;
    }

    @Override
    public long comparisons() {
        return comparisons;
    }

    @Override
    public String toString() {
        return "역색인(문서 " + docCount() + "개, 항 " + termCount() + "개, 포스팅 "
                + postingCount() + "개)";
    }
}
