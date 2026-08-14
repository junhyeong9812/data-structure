package com.datastructure.radix;

/**
 * IP 라우팅 테이블. longestPrefixOf 가 실제로 쓰이는 자리다.
 *
 * 라우터는 도착 주소마다 "어디로 보낼까"를 정해야 한다. 규칙은 이렇게 생겼다.
 *
 * <pre>
 *   0.0.0.0/0        -> 기본 게이트웨이
 *   10.0.0.0/8       -> 사내망
 *   10.1.0.0/16      -> 지사
 *   10.1.2.0/24      -> 실험실
 * </pre>
 *
 * 10.1.2.3 은 넷 다에 걸린다. 규칙이 겹치는 것이 정상이고, 이길 것은 가장 긴 것이다.
 * 이것을 최장 접두사 매칭(longest prefix match)이라고 부른다.
 *
 * 해시맵으로는 이 질문에 답할 수 없다. 정확히 일치하는 키를 찾는 자료구조이기 때문이다.
 * 길이 0 부터 32 까지 33번 조회하면 되기는 한다. 트라이는 그것을 한 번에 한다.
 *
 * 안에서는 주소를 32비트 이진 문자열로 바꿔 담는다. "10.0.0.0/8" 이면 앞 8자리만 쓴다.
 * 문자가 '0' 과 '1' 둘뿐이라 간선마다 자식이 최대 둘이다. 그래서 이 모양을 이진 트라이라고도 한다.
 *
 * 왜 문자열인가. 이 박스의 RadixTrie 를 그대로 쓰기 위해서다.
 * 실제 커널은 int 와 시프트로 같은 일을 한다. 문자열 32개는 그 대가다.
 */
public class RoutingTable {

    private final RadixTrie<String> routes = new RadixTrie<>();

    /**
     * 경로를 넣는다. cidr 은 "10.0.0.0/8" 형식.
     *
     * 같은 prefix 를 다시 넣으면 덮어쓴다.
     * 형식이 틀리면 IllegalArgumentException.
     */
    public void add(String cidr, String nextHop) {
        if (cidr == null) {
            throw new IllegalArgumentException("null 은 CIDR 이 아니다");
        }
        if (nextHop == null) {
            throw new IllegalArgumentException("null 은 next hop 이 아니다");
        }
        // TODO 1: '/' 로 주소와 prefix 길이를 가른다.
        //
        //   - '/' 가 없거나 두 개 이상이면 형식 오류다
        //   - prefix 길이는 0~32 의 정수여야 한다. 숫자가 아니면 오류
        //   - 주소를 32비트 문자열로 바꾼 뒤 **앞에서 prefix 길이만큼만** 잘라 키로 쓴다
        //
        // 자르는 것 자체가 마스크다. 10.1.2.3/8 은 10.0.0.0/8 과 같은 규칙이 된다.
        // 실제 라우터도 prefix 밖의 비트는 안 본다.
        //
        // Integer.parseInt 에 그냥 넘기지 마라. "-1" 을 통과시키면 substring 에서
        // **다른 예외**가 나온다. 계약은 IllegalArgumentException 이다.
        throw new UnsupportedOperationException("TODO 1: add");
    }

    /** 이 주소를 덮는 가장 구체적인 경로의 next hop. 없으면 null. */
    public String lookup(String ip) {
        // TODO 2: 주소를 32비트로 바꾸고 longestPrefixOf 로 규칙을 찾는다.
        //
        // 찾은 것은 **키**이지 값이 아니다. 한 번 더 get 해야 next hop 이 나온다.
        // 못 찾았으면(null) 그대로 null 이다. 기본 경로가 있으면 "" 이 잡히므로 여기로 안 온다.
        //
        // 이 메서드가 세 줄인 것이 요점이다. **어려운 것은 전부 RadixTrie 가 했다.**
        throw new UnsupportedOperationException("TODO 2: lookup");
    }

    /** 등록된 경로 수. */
    public int size() {
        return routes.size();
    }

    /** "10.1.2.0" 을 "00001010000000010000001000000000" 으로. */
    static String toBits(String ip) {
        if (ip == null) {
            throw new IllegalArgumentException("null 은 IP 가 아니다");
        }
        // TODO 3: 옥텟 4개를 각각 **8자리로 채워** 이어 붙인다.
        //
        // 길이는 언제나 32 여야 한다. Integer.toBinaryString 을 그냥 쓰면
        // 10 이 "1010" 네 자리로 나와서 **자리가 통째로 밀린다.**
        // 앞을 0 으로 채우거나 비트를 7 부터 0 까지 직접 훑어라.
        //
        // 검사할 것:
        //   - '.' 로 갈라 정확히 4조각인가 (split 은 뒤쪽 빈 조각을 버린다. limit 에 -1 을 줘라)
        //   - 각 조각이 비지 않은 숫자인가, 0~255 인가
        //   - **앞자리 0 이 붙어 있지 않은가**
        //
        // 마지막 것이 실제로 사고가 난 자리다. "023" 을 8진수 19 로 읽는 구현이 있어서
        // 같은 문자열이 라이브러리마다 다른 주소가 됐다.
        // 접근 제어 목록에서 이런 불일치는 그대로 우회 경로가 된다.
        // **답이 갈리는 입력은 아예 안 받는 편이 낫다.**
        throw new UnsupportedOperationException("TODO 3: toBits");
    }
}
