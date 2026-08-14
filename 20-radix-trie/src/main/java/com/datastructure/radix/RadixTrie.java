package com.datastructure.radix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 압축 트라이. 간선 하나에 문자열 조각을 담는다.
 *
 * 09번 MapTrie 와 밖에서 보면 같은 자료구조다. 안이 다를 뿐이다.
 *
 * <pre>
 *   09번 (romane, romanus)        여기
 *      r                            "roman"
 *      o                            /     \
 *      m                         "e"      "us"
 *      a
 *      n          노드 8개          노드 3개
 *     / \
 *    e   u
 *        s
 * </pre>
 *
 * 규칙 하나가 이 구조 전부를 결정한다.
 * 뿌리가 아닌 노드는 키이거나 갈림길이다. 둘 다 아니면 부모에 흡수돼야 한다.
 * 이 불변식을 지키느라 삽입은 간선을 쪼개고(split) 삭제는 다시 합친다(merge).
 *
 * 자식은 간선의 첫 글자로 찾는다. 한 노드에서 나가는 간선들의 첫 글자는 서로 다르다.
 * (같으면 그만큼은 공통이라 이미 하나로 눌렸어야 한다.)
 * 그래서 첫 글자로 정렬하면 그게 곧 간선 문자열의 사전순이고, TreeMap 이 순회 순서를 준다.
 * 09번 MapTrie 가 TreeMap 을 쓴 이유와 같다.
 *
 * 필드가 패키지 공개다. 테스트와 RadixTrieProblems 가 내부를 직접 본다.
 * 그래서 이 이름들은 계약의 일부다.
 */
public class RadixTrie<V> implements PrefixMap<V> {

    static final class Node<V> {

        /**
         * 부모에서 이 노드로 오는 간선에 적힌 조각. 뿌리는 빈 문자열이다.
         *
         * final 이 아니다. 쪼갤 때도 합칠 때도 이 값이 바뀐다. 그게 이 구조의 본체다.
         */
        String edge;

        /** 간선의 첫 글자 -> 자식. 사전순 순회를 위해 TreeMap 이다. */
        final Map<Character, Node<V>> children = new TreeMap<>();

        /** 이 노드에서 끝나는 키의 값. null 이면 키가 아니다. 09번의 end 자리에 값이 들어앉았다. */
        V value;

        /** 이 노드를 지나는 키 수. 09번 wordsBelow 와 같은 역할이다. 뿌리의 값이 곧 size() 다. */
        int keysBelow;

        Node(String edge) {
            this.edge = edge;
        }
    }

    Node<V> root = new Node<>("");

    /**
     * edge 와 s 의 from 위치부터가 몇 글자나 같은가.
     *
     * 이 한 줄이 put 의 세 경우를 갈라준다. 그래서 따로 떼어놨다.
     */
    static int commonPrefixLength(String edge, String s, int from) {
        // TODO 1: 두 문자열을 앞에서부터 맞춰본다. 짧은 쪽 길이를 넘지 않는다.
        //
        // 끝 조건이 둘이다. **어느 쪽이든 먼저 끝나면 거기까지다.**
        //   - edge 를 다 봤다         -> 간선이 통째로 s 안에 들어간다
        //   - s 의 남은 부분을 다 봤다 -> s 가 간선 도중에 끝난다
        // 하나만 검사하면 StringIndexOutOfBounds 가 난다. 05번 음수 해시와 같은 종류의 함정이다.
        throw new UnsupportedOperationException("TODO 1: commonPrefixLength");
    }

    @Override
    public V put(String key, V value) {
        requireKey(key);
        if (value == null) {
            throw new IllegalArgumentException("null 값은 담지 않는다");
        }
        // TODO 2: **이 박스의 본체다.** 뿌리부터 내려가며 남은 조각을 간선과 맞춰본다.
        //
        // 먼저 정할 것: 이 키가 새 키인가. get(key) 를 한 번 불러 예전 값을 잡아두면
        // 반환값도 나오고 keysBelow 를 올릴지도 정해진다. (09번 insert 의 contains 선검사와 같다)
        // **덮어쓰기인데 keysBelow 를 올리면 size 가 조용히 부푼다.**
        //
        // cur 노드에서 남은 조각이 key.substring(pos) 일 때 경우가 넷이다.
        //
        //   0. pos 가 key 끝에 닿았다   -> cur 에 값을 놓는다. 끝
        //   1. 첫 글자로 갈 자식이 없다  -> 남은 조각 통째로 새 잎을 매단다. 끝
        //   2. 간선이 통째로 일치한다    -> 그 자식으로 내려간다 (pos += 간선 길이)
        //   3. **일부만 일치한다**      -> 여기가 split 이다
        //
        // 2번에는 "간선이 남은 조각보다 짧다"도 들어간다. test 에 tester 를 넣는 경우다.
        // 그때는 그냥 내려가서 남은 "er" 로 1번을 만난다.
        //
        // split(3번)은 이렇게 한다. 앞 common 글자만 일치했다면
        //
        //   a. 새 노드 mid 를 만든다. 간선은 기존 간선의 앞 common 글자
        //   b. **기존 자식의 간선을 뒤쪽만 남기고 잘라** mid 의 자식으로 매단다
        //   c. 부모의 자식 자리를 mid 로 바꾼다
        //   d. 새 키가 딱 여기서 끝나면(common == 남은 조각 길이) mid 에 값을 놓고,
        //      아니면 남은 뒤쪽으로 잎을 하나 더 만들어 mid 에 매단다
        //
        // b 를 빼먹으면 같은 글자가 두 번 저장되고, c 를 빼먹으면 만든 mid 가 트리에 안 붙는다.
        // **d 에서 값을 놓는 경우와 안 놓는 경우를 구별하라.**
        // 늘 놓으면 romane 과 romanus 만 넣었는데 "roman" 이 키가 돼버린다.
        //
        // keysBelow: 새 키면 지나는 노드마다 1 올린다. 뿌리도 포함이다.
        // mid 는 새로 생긴 노드다. **자른 자식의 keysBelow 를 물려받은 뒤** 1 을 올린다.
        // (3번에 닿았다는 것은 그 아래에 이 키가 없었다는 뜻이므로 여기는 늘 새 키다)
        throw new UnsupportedOperationException("TODO 2: put");
    }

    /** 이 키의 경로가 정확히 끝나는 노드. 없으면 null. */
    Node<V> findNode(String key) {
        // TODO 3: 09번 findNode 와 같은 일인데 한 걸음이 한 글자가 아니다.
        //
        //   1. 남은 첫 글자로 자식을 찾는다. 없으면 null
        //   2. **그 자식의 간선이 통째로 맞는지** 확인한다. 반만 맞으면 null 이다
        //   3. 간선 길이만큼 건너뛴다
        //
        // 2번을 빼면 "romane" 만 있을 때 findNode("rom") 이 노드를 준다.
        // 그러면 get("rom") 이 값을 내놓는다. **키가 아닌 것이 키가 된다.**
        // String.startsWith(간선, pos) 가 2번을 한 줄로 해준다.
        throw new UnsupportedOperationException("TODO 3: findNode");
    }

    /**
     * 이 접두사로 시작하는 키를 전부 담고 있는 노드. 없으면 null.
     * path 에는 그 노드까지의 전체 경로를 채워준다(접두사보다 길 수 있다).
     */
    Node<V> prefixRoot(String prefix, StringBuilder path) {
        // TODO 4: findNode 와 거의 같은데 **한 경우가 더 있다.**
        //
        // 접두사가 간선 중간에서 끝날 수 있다. "romane" 만 있을 때의 "rom" 이다.
        // 멈출 노드가 없다. 그래도 답(romane)은 있다.
        //
        //   남은 접두사 길이 < 간선 길이 이면
        //     -> 간선이 남은 접두사로 시작하는가? 그렇다면 **그 자식이 통째로 답이다**
        //        아니면 null
        //
        // 이때 path 는 접두사가 아니라 **자식의 간선까지 붙인 전체 경로**여야 한다.
        // 접두사를 그대로 쓰면 collect 가 "rom" 뒤에 아래쪽 간선을 이어붙여
        // "romane" 대신 "rome" 같은 없는 문자열을 만든다.
        // **경로는 노드가 정하는 것이지 질문이 정하는 것이 아니다.**
        throw new UnsupportedOperationException("TODO 4: prefixRoot");
    }

    /** node 아래의 모든 키를 사전순으로 out 에 담는다. path 는 node 까지의 전체 경로다. */
    static <V> void collect(Node<V> node, StringBuilder path, List<String> out) {
        // TODO 5: 09번 collect 와 같은 재귀다. 붙였다 떼는 단위만 다르다.
        //
        //   09번: 글자 하나를 append 했다가 deleteCharAt
        //   여기: **간선 문자열을 append 했다가 그 길이만큼 setLength**
        //
        // 떼는 것을 빠뜨리면 결과가 조용히 뒤죽박죽이 된다. 다만 09번보다 티가 덜 난다.
        // 한 글자가 아니라 조각 단위로 어긋나기 때문이다.
        //
        // 자식 순회가 이미 사전순인 이유를 보라. 간선의 첫 글자는 서로 달라서
        // **첫 글자 순서 = 간선 문자열 순서**다. TreeMap 이 그것을 해준다.
        throw new UnsupportedOperationException("TODO 5: collect");
    }

    @Override
    public V remove(String key) {
        requireKey(key);
        // TODO 6: 09번 remove 와 앞부분은 같다. **뒷정리가 하나 더 있다.**
        //
        // 앞부분(09번과 같다): 없으면 null. 있으면 뿌리부터 keysBelow 를 1씩 깎으며 내려가고,
        // 0 이 되는 노드를 만나면 그 아래엔 아무 키도 없다는 뜻이라 통째로 끊는다.
        // 끝까지 갔으면 그 노드의 value 를 지운다.
        //
        // **여기서 끝내면 09번으로 퇴화한다.**
        // romane, romanus, romulus 에서 romulus 를 지우면 rom -> an 이 남는다.
        // 'rom' 은 이제 키도 아니고 갈림길도 아니다. **불변식이 깨졌다.**
        // 그러니 고친 자리에서 compress 를 한 번 부른다. 자리는 딱 둘이다.
        //
        //   - 자식을 끊어낸 노드 (자식이 하나 줄었다)
        //   - 값을 지운 노드     (키가 아니게 됐다)
        //
        // 그 위쪽은 볼 필요가 없다. 자식 수도 값도 안 바뀌었기 때문이다.
        // **구조 테스트가 이것을 잡는다. 계약 테스트는 못 잡는다** -- 답은 다 맞기 때문이다.
        throw new UnsupportedOperationException("TODO 6: remove");
    }

    /** 자식이 하나뿐이고 키도 아닌 노드를 그 자식과 합친다. 이미 압축돼 있으면 아무 일도 안 한다. */
    void compress(Node<V> node) {
        // TODO 7: 합칠 수 있는 조건 셋을 먼저 적어라. **하나라도 어기면 자료가 사라진다.**
        //
        //   - 뿌리가 아니다   -> 뿌리를 합치면 뿌리의 간선이 빈 문자열이 아니게 된다
        //   - 값이 없다      -> 값이 있으면 그 노드가 키다. 흡수하면 그 키가 사라진다
        //   - 자식이 정확히 1 -> 0 이면 합칠 것이 없고 2 이상이면 갈림길이라 남겨야 한다
        //
        // 합치기: 내 간선 뒤에 자식의 간선을 이어 붙이고, 자식의 값과 자식들을 가져온다.
        // 부모의 자식 맵은 손댈 필요가 없다. **간선의 첫 글자가 그대로이기 때문이다.**
        // keysBelow 도 그대로다. 값 없이 자식 하나면 내 keysBelow 는 이미 그 자식 것과 같다.
        throw new UnsupportedOperationException("TODO 7: compress");
    }

    @Override
    public String longestPrefixOf(String s) {
        requireKey(s);
        // TODO 8: **라우팅 테이블이 쓰는 연산이다.**
        //
        // 뿌리부터 간선을 따라 내려가되, 지나온 노드 중 **값이 있는 마지막 자리**를 기억한다.
        // 끝까지 못 가도 상관없다. 답은 도중에 지나온 것 중에 있다.
        //
        // 두 가지가 함정이다.
        //   - 간선이 반만 맞으면 **거기서 끝이다.** 키는 노드에서만 끝나므로 간선 중간은 답이 아니다
        //   - 빈 문자열이 키일 수 있다. 그러면 뭘 물어도 최소한 "" 은 답이다. 시작값을 그렇게 잡는다
        //
        // 아무것도 못 찾으면 null 이다. **"" 과 null 은 다르다.**
        // "" 은 "기본 경로에 걸렸다"이고 null 은 "어디에도 안 걸렸다"이다.
        // 라우터라면 하나는 기본 게이트웨이로 보내고 하나는 패킷을 버린다.
        throw new UnsupportedOperationException("TODO 8: longestPrefixOf");
    }

    @Override
    public V get(String key) {
        requireKey(key);
        Node<V> n = findNode(key);
        return n == null ? null : n.value;
    }

    @Override
    public boolean containsKey(String key) {
        return get(key) != null;
    }

    @Override
    public List<String> keysWithPrefix(String prefix) {
        requireKey(prefix);
        List<String> out = new ArrayList<>();
        StringBuilder path = new StringBuilder();
        Node<V> start = prefixRoot(prefix, path);
        if (start != null) {
            collect(start, path, out);
        }
        return out;
    }

    @Override
    public int countWithPrefix(String prefix) {
        requireKey(prefix);
        // 아래로 훑어 세도 답은 맞다. 다만 O(부분 트리) 다.
        // keysBelow 를 유지하고 있다면 걸음 수는 접두사 길이뿐이다. 09번과 같은 거래다.
        Node<V> start = prefixRoot(prefix, new StringBuilder());
        return start == null ? 0 : start.keysBelow;
    }

    @Override
    public List<String> keys() {
        return keysWithPrefix("");
    }

    @Override
    public int size() {
        return root.keysBelow;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public void clear() {
        root = new Node<>("");
    }

    /**
     * 뿌리를 뺀 노드 수. 한계 측정용이다.
     *
     * 09번식 트라이의 노드 수(= 서로 다른 접두사 개수)와 나란히 놓으면
     * 이 구조가 무엇을 줄였고 무엇을 못 줄였는지가 숫자로 나온다.
     */
    int nodeCount() {
        return countNodes(root) - 1;
    }

    private static <V> int countNodes(Node<V> node) {
        int n = 1;
        for (Node<V> child : node.children.values()) {
            n += countNodes(child);
        }
        return n;
    }

    private static void requireKey(String s) {
        if (s == null) {
            throw new IllegalArgumentException("null 은 키가 아니다");
        }
    }
}
