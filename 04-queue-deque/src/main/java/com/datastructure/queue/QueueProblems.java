package com.datastructure.queue;

/**
 * 큐와 데크로 푸는 응용 문제들.
 *
 * 파라미터 타입을 보라. 어떤 문제는 Deque 를 받고 어떤 문제는 Queue 를 받는다.
 * **필요한 능력만 받는다.** Deque 로 받으면 호출자가 앞으로도 넣을 수 있게 되는데,
 * 그건 그 알고리즘이 의도한 계약이 아니다.
 *
 * 계약: 작업용 큐/데크는 비어 있는 상태로 들어온다.
 */
public final class QueueProblems {

    private QueueProblems() {
    }

    /**
     * 문제 1. 회문 판별
     *
     * 알파벳과 숫자만 보고 대소문자는 무시한다.
     *
     *   "A man, a plan, a canal: Panama"  -> true
     *   "race a car"                       -> false
     *   ""                                 -> true
     *
     * 생각할 것
     *   - 왜 데크인가? 양쪽 끝을 동시에 봐야 하기 때문이다. 큐로는 안 된다.
     *   - 원소가 하나 남았을 때는 어떻게 되는가?
     *
     * TODO(21): 구현하라.
     */
    public static boolean isPalindrome(String input, Deque<Character> buffer) {
        throw new UnsupportedOperationException("TODO(21): isPalindrome");
    }

    /**
     * 문제 2. 슬라이딩 윈도우 최댓값 (이 문제집의 함정)
     *
     * 크기 k 인 창을 왼쪽부터 한 칸씩 옮기며 각 창의 최댓값을 모은다.
     *
     *   [1, 3, -1, -3, 5, 3, 6, 7], k=3  ->  [3, 3, 5, 5, 6, 7]
     *
     * 함정
     *   창마다 k 개를 훑으면 O(n*k) 다. 테스트에 20만 개 x k=1000 케이스와 시간 제한이 있다.
     *
     * 생각할 것
     *   - 데크에 "아직 최댓값이 될 수 있는 후보"의 인덱스만 남기면 어떻게 되는가?
     *   - 새 값이 들어왔을 때, 그보다 작은 뒤쪽 후보들은 앞으로 영원히 답이 될 수 있는가?
     *   - 창을 벗어난 후보는 어느 쪽 끝에서 빠지는가?
     *   - 각 인덱스가 데크에 몇 번 들어가고 몇 번 나오는가? 그게 복잡도다.
     *
     * TODO(22): 구현하라. O(n) 이어야 한다. buffer 에는 인덱스를 담으면 편하다.
     */
    public static int[] slidingWindowMax(int[] values, int k, Deque<Integer> buffer) {
        throw new UnsupportedOperationException("TODO(22): slidingWindowMax");
    }

    /**
     * 문제 3. 스트림에서 처음으로 한 번만 나온 문자
     *
     * 문자열을 앞에서부터 읽으며, 그 시점까지 딱 한 번만 나온 문자 중 가장 먼저 나온 것을 기록한다.
     * 없으면 '#' 을 넣는다.
     *
     *   "abcabc"  ->  "aaabc#"
     *              a      -> a
     *              ab     -> a 가 여전히 처음
     *              abc    -> a
     *              abca   -> a 가 두 번 나왔다. b 가 답
     *              abcab  -> b 도 두 번. c 가 답
     *              abcabc -> 전부 두 번씩. 남은 게 없으므로 #
     *
     * 이 문제는 **Queue 만** 받는다. 앞에서 빼고 뒤에 넣는 것만 필요하기 때문이다.
     * 데크가 아니어도 풀린다는 것 자체가 정보다.
     *
     * 생각할 것
     *   - 큐에는 무엇을 담아야 하는가?
     *   - 큐 앞에 있는 문자가 이미 두 번 나왔다면 어떻게 하는가?
     *
     * TODO(23): 구현하라. 문자는 소문자 알파벳만 들어온다.
     */
    public static String firstUniqueStream(String input, Queue<Character> buffer) {
        throw new UnsupportedOperationException("TODO(23): firstUniqueStream");
    }

    /**
     * 문제 4. k 칸 오른쪽으로 회전
     *
     *   [1, 2, 3, 4, 5], k=2  ->  [4, 5, 1, 2, 3]
     *
     * 01번(배열)에서는 세 번 뒤집었고, 02번(연결)에서는 링크 몇 개만 바꿨다.
     * 데크에서는 세 번째 방법이 있다. 훨씬 단순하다.
     *
     * 생각할 것
     *   - 뒤에서 하나 빼서 앞에 넣으면 무슨 일이 일어나는가? 그걸 몇 번 하면 되는가?
     *   - 그 방법의 복잡도는? k 가 아주 크면 어떻게 줄이는가?
     *
     * TODO(24): 구현하라.
     */
    public static <E> void rotate(Deque<E> deque, int k) {
        throw new UnsupportedOperationException("TODO(24): rotate");
    }
}
