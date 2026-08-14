package com.datastructure.radix;

import java.util.List;

/**
 * 압축 트라이로 푸는 문제 둘.
 *
 * 둘 다 09번에서 이미 푼 문제다. 같은 문제를 다시 푸는 것이 요점이다.
 * 구조가 바뀌면 같은 문제가 어떻게 달라지는지가 보인다.
 */
public final class RadixTrieProblems {

    private RadixTrieProblems() {
    }

    /**
     * 문제 1: 주어진 단어들의 가장 긴 공통 접두사.
     *
     * words 가 비었거나 null 이면 "". 공통 접두사가 없으면 "".
     *
     * 09번에서는 뿌리부터 한 글자씩 내려가며 갈림길을 찾아야 했다.
     * 자식이 2개 이상이거나 그 노드가 단어이면 멈추는 식이었다.
     *
     * 여기서는 그 사슬이 이미 간선 하나로 눌려 있다.
     */
    public static String longestCommonPrefix(String[] words) {
        // TODO 1: 전부 넣고 **뿌리의 자식을 본다.** 내려갈 필요가 없다.
        //
        //   - 자식이 하나면 그 간선이 곧 답이다
        //   - 자식이 둘 이상이면 뿌리에서 갈라진다. 답은 ""
        //   - **뿌리가 키이면(빈 문자열이 들어 있으면) 답은 "" 다.** 09번의 "end 면 멈춘다"와 같은 조건이다
        //
        // 왜 첫 간선이 곧 답인가. 압축 불변식이 그것을 보장한다.
        // 뿌리 아래 첫 노드는 키이거나 갈림길이고, 거기까지 오는 길에는 갈림길이 없다.
        // **"갈림길이 없는 구간"이 곧 공통 접두사의 정의다.**
        //
        // 09번은 O(답 길이)만큼 노드를 밟았다. 여기는 필드 하나를 읽는다.
        // 다만 **트라이를 만드는 비용은 그대로다.** 공짜로 빨라진 것이 아니라 읽는 자리가 바뀐 것이다.
        throw new UnsupportedOperationException("TODO 1: longestCommonPrefix");
    }

    /**
     * 문제 2: 접두사로 시작하는 키를 사전순 앞에서 k 개.
     *
     * k 개보다 적으면 있는 만큼. k 가 0 이하면 빈 리스트.
     *
     * 여기 시간 제한이 있다. 접두사에 10만 개가 걸려 있는데 k 가 10 인 질의를 반복한다.
     * trie.keysWithPrefix(prefix).subList(0, k) 는 답은 맞지만 매번 10만 개를 다 모은다.
     *
     * RadixTrie 를 구체 타입으로 받는 이유가 09번과 같다. 중간에 멈추려면 노드를 봐야 한다.
     * 인터페이스가 주는 것만으로는 이 문제를 풀 수 없다. 그것도 정보다.
     */
    public static List<String> autocomplete(RadixTrie<String> trie, String prefix, int k) {
        // TODO 2: prefixRoot 로 시작 노드와 **전체 경로**를 받아, collect 를 하되 k 개가 차면 그만둔다.
        //
        // 09번과 다른 점 하나. 시작 경로가 prefix 가 아니다.
        // 접두사가 간선 중간에서 끝났으면 시작 노드의 경로는 접두사보다 길다.
        // prefixRoot 가 채워주는 StringBuilder 를 그대로 쓰면 된다.
        // **prefix 로 새 StringBuilder 를 만들면 없는 키가 나온다.**
        //
        // 개수 확인은 재귀 진입부, 담은 직후, 자식에서 돌아온 직후 셋 중 하나면 답이 맞다.
        // 나머지는 헛걸음을 줄일 뿐이다(09번에서 변종으로 확인했다).
        // 요점은 **전부 모은 뒤 앞에서 k 개를 자르는 것과의 차이**다. 성능 테스트가 그것을 잡는다.
        throw new UnsupportedOperationException("TODO 2: autocomplete");
    }
}
