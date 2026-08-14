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
 * 색인의 방향을 뒤집는다.
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

        // TODO 7: 본문을 분석해 항 목록을 얻고, 문서 하나 안에서 항별로 모은 다음
        //         모은 포스팅을 각 항의 포스팅 리스트에 꽂는다(TODO 8 이 꽂는 일을 한다).
        //
        // 같은 항이 한 문서에 열 번 나와도 포스팅은 하나다. 위치가 열 개 붙을 뿐이다.
        // 나올 때마다 새 포스팅을 만들면 리스트에 같은 문서 번호가 여러 번 들어가고,
        // 그러면 df 가 부풀어 점수가 틀리고 교집합 병합의 전제(문서 번호가 유일하고 오름차순)가 깨진다.
        //
        // 위치는 분석 결과 목록에서의 인덱스다. 원문의 글자 위치가 아니다.
        throw new UnsupportedOperationException("TODO 7: index");
    }

    /**
     * 포스팅을 문서 번호 오름차순 자리에 꽂는다.
     *
     * 문서를 번호 순서대로 넣으면 늘 맨 뒤가 정답이라 append 만 해도 맞다.
     * 그런데 순서가 뒤섞여 들어오는 순간 append 는 리스트를 망가뜨리고,
     * 그러면 교집합 병합이 조용히 답을 빠뜨린다.
     */
    private static void insertSorted(List<Posting> postings, Posting posting) {
        // TODO 8: 들어갈 자리를 찾아 꽂는다. 리스트가 이미 정렬돼 있으니 이분 탐색으로 찾는다.
        //
        // 중간값을 (low + high) / 2 로 쓰면 리스트가 아주 길 때 넘칠 수 있다.
        // 05번 해시맵에서 본 것과 같은 종류다. >>> 1 을 쓰면 그 자리가 사라진다.
        //
        // 맨 뒤에 붙이기만 해도 테스트 대부분이 통과한다. 문서를 번호 순으로 넣는 한 맨 뒤가 정답이기 때문이다.
        // 뒤섞어 넣는 테스트 하나만 그 구현을 잡는다. 위 javadoc 이 그 이야기다.
        throw new UnsupportedOperationException("TODO 8: insertSorted");
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
        // TODO 9: 질의어들의 포스팅 리스트를 모아, 두 정렬 목록을 한 번 훑기로 겹쳐 나간다.
        //         mergeOrder 가 SHORTEST_FIRST 이면 짧은 리스트부터 병합한다.
        //
        // 여기가 이 박스의 본체다. 넷을 조심하라.
        //
        //   1. 질의어 하나라도 색인에 없으면 AND 의 답은 공집합이다. 그 항을 **건너뛰면 안 된다.**
        //      건너뛰면 없는 말을 넣을수록 결과가 늘어나는, 예외 없이 조용한 오답이 된다.
        //   2. 한 번 훑기는 세 갈래다. 같으면 담고 둘 다 전진, 아니면 **작은 쪽만** 전진.
        //      큰 쪽을 전진시키면 두 포인터가 서로를 지나쳐 답이 통째로 빈다.
        //   3. 중간 결과가 비면 더 볼 것이 없다. 남은 리스트를 계속 훑으면 답은 맞고 자만 부푼다.
        //   4. visitedDocs 와 comparisons 를 여기서 센다. 이 두 자가 전수 조사와의 차이를 보여주는
        //      유일한 증거다. 안 세면 MeasurementTest 가 "빠르다"를 확인할 방법이 없어진다.
        //
        // 그리고 이 메서드는 리스트를 정렬한다. index 안의 리스트 자체를 정렬하면
        // 포스팅 리스트의 순서가 아니라 **리스트들의 순서**만 바뀌어야 하는데,
        // 무엇을 담은 목록을 정렬하는지 헷갈리면 색인을 망가뜨린다.
        throw new UnsupportedOperationException("TODO 9: intersect");
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
        // TODO 10: intersect 로 후보를 얻고, 후보에만 점수를 매겨 정렬한 뒤 앞에서 k 개를 준다.
        //
        // df 를 어디서 얻는지가 색인의 값을 보여주는 자리다. 전수 조사는 문서를 전부 훑어야 얻는 수인데
        // 여기서는 포스팅 리스트의 길이 하나다. 그 차이가 이 박스가 만들고 싶은 그림이다.
        //
        // 더하는 순서를 **질의어 순서로** 고정하라. 병합 순서로 더하면 값은 거의 같은데
        // double 오차가 다르게 쌓여 전수 조사와 마지막 자리에서 갈린다.
        // 그러면 두 엔진을 맞대보는 검증이 정답을 두고 실패한다.
        throw new UnsupportedOperationException("TODO 10: search");
    }

    @Override
    public List<Integer> searchPhrase(String phrase) {
        if (phrase == null) {
            throw new IllegalArgumentException("구문이 null 이다");
        }
        visitedDocs = 0;
        comparisons = 0;
        // TODO 11: 구문의 항을 전부 가진 문서를 먼저 좁히고, 그 안에서만 위치를 본다(TODO 12).
        //
        // 좁히는 데 쓰는 목록과 위치를 보는 데 쓰는 목록이 **다르다.**
        //   좁힐 때는 중복을 걷어낸 목록 - 교집합에 같은 항을 두 번 넣어봐야 같은 답이고 일만 는다
        //   위치를 볼 때는 걷어내지 않은 원래 목록 - 반복과 순서가 구문 질의의 뜻이기 때문이다
        // 둘을 같은 것으로 쓰면 "간다 또 간다" 가 "간다 또" 를 찾는 질의로 바뀐다.
        //
        // 좁히지 않고 색인의 모든 문서를 도는 구현도 답은 맞다. 전수 조사가 되어 자만 무너진다.
        throw new UnsupportedOperationException("TODO 11: searchPhrase");
    }

    /**
     * 이 문서에 항들이 그 순서 그대로 붙어 있나.
     *
     * 첫 항이 나온 자리마다 그 뒤가 이어지는지 본다. 첫 항이 p 에 있으면
     * i 번째 항은 p 더하기 i 에 있어야 한다.
     */
    private boolean containsPhrase(List<String> terms, int docId) {
        // TODO 12: 위 javadoc 의 판정. positionsOf 가 항 하나의 위치 목록을 준다.
        //
        // 첫 항의 위치 하나가 안 맞았다고 false 로 나가면 안 된다. 그 자리에서 실패해도
        // 다음 자리에서 붙어 있을 수 있다. 원문에 첫 항이 한 번만 나오는 문서에서는
        // 그 잘못이 드러나지 않아서, 대충 써도 테스트가 여럿 통과한다.
        throw new UnsupportedOperationException("TODO 12: containsPhrase");
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
