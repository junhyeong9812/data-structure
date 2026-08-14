package com.datastructure.trie;

import java.util.List;

/**
 * 트라이로 푸는 문제 셋.
 *
 * 셋 다 트라이 없이도 풀린다. 그래서 트라이를 쓸 이유가 무엇인지가 문제의 핵심이다.
 */
public final class TrieProblems {

    private TrieProblems() {
    }

    /**
     * 문제 1: 주어진 단어들의 가장 긴 공통 접두사.
     *
     * words 가 비었으면 "". 공통 접두사가 없으면 "".
     *
     * 흔한 풀이는 첫 단어를 기준으로 나머지와 한 글자씩 비교하는 것이고, 그게 더 짧다.
     * 여기서 트라이로 푸는 이유는 "공통 접두사"가 트라이에서 무엇인지 눈으로 보기 위해서다.
     */
    public static String longestCommonPrefix(String[] words) {
        // TODO 1: 전부 넣고 뿌리에서 내려간다.
        //
        // 언제 멈추는가. 두 조건이다.
        //   - 자식이 2개 이상이면 거기서 갈라진다. 공통이 아니다.
        //   - **자식이 하나여도 지금 노드가 end 면 멈춘다.** 그 단어 자체가 더 못 간다.
        //     ["a", "ab"] 의 답은 "a" 다. 이 조건을 빼면 "ab" 가 나온다.
        //
        // 즉 공통 접두사는 **뿌리에서 첫 갈림길(또는 첫 단어 끝)까지의 경로**다.
        throw new UnsupportedOperationException("TODO 1: longestCommonPrefix");
    }

    /**
     * 문제 2: 접두사로 시작하는 단어를 사전순 앞에서 k 개.
     *
     * k 개보다 적으면 있는 만큼. k 가 0 이하면 빈 리스트.
     *
     * 여기 시간 제한이 있다. 접두사에 20만 개가 걸려 있는데 k 가 10 인 질의를 반복한다.
     * trie.keysWithPrefix(prefix).subList(0, k) 는 답은 맞지만 매번 20만 개를 다 모은다.
     *
     * MapTrie 를 구체 타입으로 받는 이유가 이것이다. 중간에 멈추려면 노드를 봐야 한다.
     * 인터페이스가 주는 것만으로는 이 문제를 풀 수 없다. 그것도 정보다.
     */
    public static List<String> autocomplete(MapTrie trie, String prefix, int k) {
        // TODO 2: collect 를 하되 k 개가 차면 그만둔다.
        //
        // 개수를 확인할 자리가 셋이다. 재귀 진입부, 단어를 담은 직후, 자식에서 돌아온 직후.
        // **셋 중 하나만 있어도 답은 맞다.** 나머지는 헛걸음을 줄일 뿐이다.
        // (변종으로 하나씩 빼봤는데 130개가 다 통과했다. 개수를 묶는 데는 하나면 된다.)
        //
        // 진짜 요점은 다른 데 있다. **아예 확인하지 않고 전부 모은 뒤 앞에서 k 개를 자르는 것**과의
        // 차이다. 그것도 답은 맞다. 다만 접두사에 10만 개가 걸려 있으면 질의마다 10만 걸음이다.
        // 성능 테스트가 그 차이를 잡는다.
        throw new UnsupportedOperationException("TODO 2: autocomplete");
    }

    /**
     * 문제 3: 문자열 s 의 서로 다른 부분 문자열 개수. (빈 문자열은 세지 않는다)
     *
     * "abc" 면 a, ab, abc, b, bc, c 로 6개.
     * "aaa" 면 a, aa, aaa 로 3개. (같은 것은 한 번만)
     *
     * 모든 접미사를 트라이에 넣으면 노드 하나가 부분 문자열 하나다.
     * 접미사 "abc", "bc", "c" 를 넣고 만들어진 노드를 세면 그게 답이다.
     * 중복 제거를 따로 할 필요가 없다. 같은 부분 문자열은 같은 경로라 노드를 다시 안 만든다.
     *
     * 여기서는 Trie 대신 MapTrie.Node 를 직접 써도 된다. end 도 wordsBelow 도 필요 없고
     * 만들어진 노드 수만 세면 되기 때문이다.
     *
     * 이 풀이의 한계도 같이 보라. 길이 n 이면 노드가 최대 n(n+1)/2 개다.
     * n 이 10만이면 50억 개라 아예 못 만든다. 접미사 배열과 접미사 오토마타가 존재하는 이유다.
     */
    public static int countDistinctSubstrings(String s) {
        // TODO 3: i 를 0 부터 s.length()-1 까지 옮기며 s[i..] 를 뿌리부터 밀어 넣는다.
        //         **새 노드를 만들 때마다** 1 을 센다.
        //
        // s 가 null 이거나 비었으면 0.
        throw new UnsupportedOperationException("TODO 3: countDistinctSubstrings");
    }
}
