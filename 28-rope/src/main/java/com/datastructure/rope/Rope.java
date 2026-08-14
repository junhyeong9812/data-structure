package com.datastructure.rope;

import java.util.ArrayList;
import java.util.List;

/**
 * 문자열을 이진 트리의 잎에 조각으로 나눠 담은 저장소.
 *
 * <h2>모양</h2>
 *
 * <pre>
 *   잎        실제 문자열 조각을 들고 있다
 *   내부 노드  글자를 하나도 안 들고 있다. 왼쪽 부분트리의 전체 길이(weight)만 안다
 * </pre>
 *
 * weight 하나가 이 자료구조의 전부다. i 번째 글자를 찾을 때 i 를 weight 와 비교해
 * 왼쪽으로 갈지 오른쪽으로 갈지 정하고, 오른쪽으로 가면 i 에서 weight 를 뺀다.
 * 그것뿐이다.
 *
 * <h2>불변이다</h2>
 *
 * 모든 연산이 새 Rope 를 돌려주고 옛 Rope 는 그대로 산다. 26번 영속 자료구조와 같은 발상이다.
 * 그리고 concat 이 O(1) 인 이유가 바로 이것이다.
 * 아무도 노드를 안 고치므로 두 로프를 합칠 때 양쪽을 그대로 자식으로 삼아도 안전하다.
 * 고칠 수 있는 자료구조였다면 한쪽을 복사해야 했을 것이다.
 *
 * <h2>필드 이름이 계약이다</h2>
 *
 * RopeStructureTest 가 리플렉션으로 필드를 직접 본다.
 * root, leafMax, charAtVisits 라는 이름과 Node 의 필드들을 바꾸면 그 테스트가 깨진다.
 */
public final class Rope implements CharSequenceStore {

    /**
     * 잎 하나에 담는 최대 글자 수.
     *
     * 이 상수가 절충이다. 1 로 두면 글자마다 노드라 4096자 문서에 노드가 8191개이고
     * 조회 한 번에 13노드를 지난다. 문서 전체를 잎 하나에 담으면 노드는 1개지만
     * 가운데 한 글자를 넣을 때마다 그 조각을 통째로 쪼개야 한다(= 배열과 같은 값).
     *
     * 32 는 그 사이다. RopeStructureTest 의 LeafMaxTradeoff 가 다섯 값을 표로 잰다.
     * 실제 에디터들은 훨씬 크게 잡는다(수백에서 수천 바이트). 캐시 한 줄에 들어가는 것이
     * 노드 하나를 따라가는 것보다 훨씬 싸기 때문이다.
     */
    public static final int DEFAULT_LEAF_MAX = 32;

    /**
     * 잎이면 text 가 있고 left, right 가 null 이다. 내부 노드면 반대다.
     *
     * length 와 depth 는 weight 만으로도 계산할 수 있지만 매번 훑으면 O(n) 이라
     * 만들 때 한 번 계산해 들고 있는다. 전부 final 이라 나중에 어긋날 수 없다.
     */
    static final class Node {
        final String text;
        final Node left;
        final Node right;
        final int weight;
        final int length;
        final int depth;

        Node(String text) {
            this.text = text;
            this.left = null;
            this.right = null;
            this.weight = text.length();
            this.length = text.length();
            this.depth = 0;
        }

        Node(Node left, Node right) {
            this.text = null;
            this.left = left;
            this.right = right;
            this.weight = left.length;
            this.length = left.length + right.length;
            this.depth = 1 + Math.max(left.depth, right.depth);
        }

        boolean isLeaf() {
            return text != null;
        }
    }

    /** 빈 잎 하나를 돌려 쓴다. 불변이라 공유해도 된다. */
    static final Node EMPTY = new Node("");

    private final Node root;
    private final int leafMax;
    private final long copiedByLastOp;
    private final long copiedTotal;

    /**
     * 계측 전용. 논리적 상태가 아니다.
     *
     * 담긴 글자에 아무 영향이 없고, 이 값을 지워도 답이 안 바뀐다.
     * 24번 LsmTree 의 diskReads 와 같은 자리다. 여기서만 final 이 아니다.
     */
    private long charAtVisits;

    public Rope(String text) {
        this(text, DEFAULT_LEAF_MAX);
    }

    public Rope(String text, int leafMax) {
        if (text == null) {
            throw new IllegalArgumentException("문자열이 null 이다");
        }
        if (leafMax < 1) {
            throw new IllegalArgumentException("leafMax 는 1 이상이어야 한다: " + leafMax);
        }
        this.root = buildLeaves(text, leafMax, new long[1]);
        this.leafMax = leafMax;
        this.copiedByLastOp = 0;
        this.copiedTotal = 0;
    }

    /** 트리와 이번에 옮긴 글자 수로 다음 로프를 만든다. */
    private Rope(Node root, int leafMax, long copied, long previousTotal) {
        this.root = root;
        this.leafMax = leafMax;
        this.copiedByLastOp = copied;
        this.copiedTotal = previousTotal + copied;
    }

    /**
     * 문자열을 leafMax 크기로 잘라 균형 트리에 담는다. 미리 채워뒀다.
     *
     * 조각내는 순간 새 String 이 생기므로 그때만 복사를 센다.
     * leafMax 이하면 받은 문자열을 그대로 잎에 들고 있으므로 0 이다.
     * String 이 불변이라 이 참조를 들고 있어도 안전하다.
     */
    static Node buildLeaves(String text, int leafMax, long[] copied) {
        if (text.isEmpty()) {
            return EMPTY;
        }
        if (text.length() <= leafMax) {
            return new Node(text);
        }
        List<Node> chunks = new ArrayList<>();
        for (int i = 0; i < text.length(); i += leafMax) {
            chunks.add(new Node(text.substring(i, Math.min(text.length(), i + leafMax))));
        }
        copied[0] += text.length();
        return balancedOver(chunks, 0, chunks.size());
    }

    /** 잎 목록 [from, to) 를 반으로 갈라 균형 트리로 세운다. 미리 채워뒀다. */
    static Node balancedOver(List<Node> nodes, int from, int to) {
        if (from >= to) {
            return EMPTY;
        }
        if (to - from == 1) {
            return nodes.get(from);
        }
        int mid = (from + to) >>> 1;
        return new Node(balancedOver(nodes, from, mid), balancedOver(nodes, mid, to));
    }

    /**
     * 두 부분트리를 잇는다.
     *
     * 이 세 줄이 로프의 핵심이다. TODO 4 를 채우면서 왜 복사가 없는지 확인하라.
     */
    static Node concatNodes(Node a, Node b) {
        // TODO 4: 새 내부 노드 하나를 만들어 a 와 b 를 자식으로 삼는다.
        //
        // **글자를 옮기지 마라.** 옮길 이유가 없다. 조각들은 있던 자리에 그대로 있고
        // 새로 생기는 것은 "왼쪽이 a, 오른쪽이 b" 라는 사실을 적은 노드 하나뿐이다.
        // 01번 동적 배열이 뒤에 붙일 때 배열을 통째로 옮기던 것과 대비된다.
        //
        // 한쪽이 비었으면 노드를 만들지 말고 반대쪽을 그대로 돌려줘라.
        // 빈 잎을 트리에 매달면 조회할 때마다 지나가야 하고 leafCount 가 부풀며
        // split 이 만든 빈 조각이 트리에 계속 쌓인다. (Node.length 로 판별한다)
        throw new UnsupportedOperationException("TODO 4: concatNodes");
    }

    @Override
    public int length() {
        return root.length;
    }

    @Override
    public char charAt(int index) {
        if (index < 0 || index >= root.length) {
            throw new IndexOutOfBoundsException("index " + index + " (길이 " + root.length + ")");
        }
        // TODO 5: 뿌리에서 잎까지 내려가며 index 를 좁힌다.
        //
        //   내부 노드에서 index < weight 면 왼쪽으로 간다.
        //   아니면 **index 에서 weight 를 빼고** 오른쪽으로 간다.
        //   잎에 닿으면 text.charAt(index) 다.
        //
        // 빼는 것을 빠뜨리는 것이 여기서 제일 흔한 실수다. 컴파일도 되고 예외도 안 나고
        // 왼쪽 절반은 맞는 답이 나온다. 무작위 대조가 그것을 잡는다.
        //
        // 반복문으로 짜라. 재귀로 짜면 기운 트리에서 스택이 터진다.
        // (앞에만 계속 붙인 로프는 깊이가 10만이 될 수 있다. README 한계 3번)
        //
        // 내려가면서 지난 노드 수를 charAtVisits 에 더하라. 잎도 센다.
        // 이 계기가 "배열이면 1 이었을 것" 을 숫자로 보여준다.
        throw new UnsupportedOperationException("TODO 5: charAt");
    }

    @Override
    public String substring(int from, int to) {
        checkRange(from, to);
        StringBuilder out = new StringBuilder(to - from);
        appendRange(root, from, to, out);
        return out.toString();
    }

    /** [from, to) 를 out 에 담는다. from 과 to 는 이 부분트리 안에서의 자리다. */
    private static void appendRange(Node node, int from, int to, StringBuilder out) {
        // TODO 9: 겹치는 부분만 훑는다. 13번 세그먼트 트리의 구간 질의와 같은 모양이다.
        //
        //   빈 구간이면 아무것도 안 한다
        //   잎이면 text 의 [from, to) 를 담는다
        //   내부 노드면 왼쪽과 오른쪽에 나눠 묻는다
        //     왼쪽은 [from, min(to, weight))
        //     오른쪽은 [max(0, from - weight), to - weight)   <- 자리를 weight 만큼 당긴다
        //
        // 안 겹치는 쪽은 아예 안 내려가야 O(log n + 길이) 가 된다.
        // 전부 내려가면 답은 맞는데 O(n) 이다. 그러면 toString 과 다를 게 없다.
        throw new UnsupportedOperationException("TODO 9: appendRange");
    }

    /**
     * 두 로프를 잇는다. 미리 채워뒀다. 일은 concatNodes 가 한다.
     *
     * 상대가 로프가 아니면 글자를 꺼내 와야 하므로 그때만 복사가 생긴다.
     * 이 문제집에서는 StringBuilderStore 와 붙일 때가 그렇다.
     */
    @Override
    public Rope concat(CharSequenceStore other) {
        if (other == null) {
            throw new IllegalArgumentException("붙일 저장소가 null 이다");
        }
        long copied = 0;
        Node otherRoot;
        if (other instanceof Rope rope) {
            otherRoot = rope.root;
        } else {
            otherRoot = buildLeaves(other.toString(), leafMax, new long[1]);
            copied = other.length();
        }
        return new Rope(concatNodes(root, otherRoot), leafMax, copied, copiedTotal);
    }

    /**
     * index 를 경계로 트리를 둘로 가른다. 삽입도 삭제도 전부 이것으로 만들어진다.
     *
     * 결과 둘 다 이번에 옮긴 글자 수를 같이 들고 나간다.
     */
    @Override
    public Split split(int index) {
        if (index < 0 || index > root.length) {
            throw new IndexOutOfBoundsException("index " + index + " (길이 " + root.length + ")");
        }
        long[] copied = new long[1];
        Node[] parts = splitNode(root, index, copied);
        return new Split(new Rope(parts[0], leafMax, copied[0], copiedTotal),
                new Rope(parts[1], leafMax, copied[0], copiedTotal));
    }

    /** {왼쪽, 오른쪽} 두 부분트리. 잘린 잎이 있으면 그 길이만큼 copied 에 더한다. */
    static Node[] splitNode(Node node, int index, long[] copied) {
        // TODO 6: 여기가 이 문제의 본체다. 네 경우로 나뉜다.
        //
        //   index == 0            전부 오른쪽이다. {EMPTY, node}
        //   index == node.length  전부 왼쪽이다.   {node, EMPTY}
        //   잎이다                 문자열을 자른다. **여기서만 글자를 복사한다.**
        //                         잘린 조각 둘의 길이 합 = node.length 를 copied 에 더한다
        //   내부 노드다             weight 와 비교해 한쪽으로 내려가고, 갈라진 조각을
        //                         반대쪽과 다시 concatNodes 로 이어 붙인다
        //
        // 내부 노드의 세 갈래를 정확히 보라.
        //
        //   index < weight   왼쪽을 쪼갠다. 왼쪽의 오른쪽 조각에 이 노드의 right 를 붙인다
        //   index > weight   오른쪽을 (index - weight) 자리에서 쪼갠다.
        //                    이 노드의 left 에 오른쪽의 왼쪽 조각을 붙인다
        //   index == weight  이미 경계다. {left, right} 를 그대로 돌려준다. **복사가 0 이다**
        //
        // 마지막 경우가 왜 중요한지 보라. 편집이 잎 경계에 떨어지면 쪼갤 것이 없다.
        // 그래서 같은 자리를 계속 치는 편집은 두 번째부터 공짜다(CopyCostTest 가 그 값을 잰다).
        //
        // 조각을 반대쪽과 다시 붙이는 것을 빠뜨리면 글자가 조용히 사라진다.
        // 컴파일도 예외도 없고 길이만 줄어든다. 무작위 대조가 그것을 잡는다.
        throw new UnsupportedOperationException("TODO 6: splitNode");
    }

    @Override
    public Rope insert(int index, String s) {
        if (s == null) {
            throw new IllegalArgumentException("넣을 문자열이 null 이다");
        }
        if (index < 0 || index > root.length) {
            throw new IndexOutOfBoundsException("index " + index + " (길이 " + root.length + ")");
        }
        // TODO 7: split 한 번과 concat 두 번이다.
        //
        //   splitNode 로 [앞, 뒤] 를 얻는다
        //   buildLeaves 로 s 를 담은 부분트리를 만든다 (copied 배열을 그대로 넘겨라)
        //   앞 + 가운데 + 뒤 를 concatNodes 로 잇는다
        //
        // s 가 비었으면 트리를 그대로 쓰는 새 로프를 돌려줘라. 복사는 0 이다.
        // (계기가 "이번 연산" 을 가리켜야 하므로 this 를 그대로 돌려주면 안 된다.
        //  옛 로프의 charsCopiedByLastOp 가 딸려 나온다)
        //
        // 새 로프를 만드는 비용이 얼마인지 세어 보라. 트리에서 새로 생기는 노드는
        // 쪼개진 경로 위의 것들뿐이고, 나머지는 옛 로프와 **같은 객체를 공유한다.**
        // 4096자 문서에서 노드 12개다(RopeStructureTest 가 그것을 센다).
        throw new UnsupportedOperationException("TODO 7: insert");
    }

    @Override
    public Rope delete(int from, int to) {
        checkRange(from, to);
        // TODO 8: split 두 번이다.
        //
        //   from 에서 쪼개 [앞, 나머지] 를 얻는다
        //   나머지를 (to - from) 자리에서 쪼개 [지울것, 뒤] 를 얻는다
        //   앞과 뒤를 잇는다. 가운데는 버린다
        //
        // 두 번째 자리가 to 가 아니라 **to - from** 이다. 나머지의 시작이 원래 from 이라
        // 그만큼 당겨야 한다. 여기가 이 메서드에서 유일하게 틀리는 자리다.
        //
        // 버린 가운데는 어떻게 되는가. 아무 데서도 안 가리키면 GC 가 가져간다.
        // 다만 **옛 로프는 여전히 그 조각을 가리키고 있다.** 그래서 실행 취소가 공짜다.
        //
        // from == to 면 트리 그대로, 복사 0 인 새 로프다.
        throw new UnsupportedOperationException("TODO 8: delete");
    }

    /**
     * 잎을 순서대로 모아 균형 트리로 다시 세운다.
     *
     * 교과서의 표준 방법은 피보나치 수열로 부분트리 길이의 하한을 정해두고
     * 그 조건을 어긴 곳만 다시 세우는 것이다. 여기서는 그렇게 하지 않는다.
     * 전부 다시 세우는 단순한 방법을 쓴다. O(잎 개수) 이고 글자는 한 개도 안 옮긴다.
     *
     * 언제 부를지는 정책이고 이 클래스는 정하지 않는다. 실제 에디터는
     * 깊이가 임계를 넘으면 자동으로 부른다. 여기서는 부르는 쪽이 정한다.
     */
    public Rope rebalance() {
        // TODO 10: collectLeaves 로 잎을 순서대로 모아 balancedOver 로 다시 세운다.
        //
        // 왜 답이 안 바뀌는가. 잎의 순서가 문서의 순서이고 내부 노드는 순서를 안 바꾼다.
        // 모양만 바뀐다. 06번 BST 나 23번 스플레이 트리에서 본 이야기와 같다.
        //
        // 왜 복사가 0 인가. **잎 객체를 그대로 다시 매달기 때문이다.**
        // 새로 만드는 것은 내부 노드뿐이고 내부 노드에는 글자가 없다.
        // 불변이 아니었다면 이렇게 못 한다. 남의 잎을 빌려 쓸 수 없기 때문이다.
        //
        // 잎이 하나도 없으면(빈 문서) EMPTY 다.
        throw new UnsupportedOperationException("TODO 10: rebalance");
    }

    /** 빈 잎은 빼고 왼쪽부터 차례로 모은다. 미리 채워뒀다. */
    private static void collectLeaves(Node node, List<Node> out) {
        if (node.isLeaf()) {
            if (node.length > 0) {
                out.add(node);
            }
            return;
        }
        collectLeaves(node.left, out);
        collectLeaves(node.right, out);
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder(root.length);
        appendAll(root, out);
        return out.toString();
    }

    private static void appendAll(Node node, StringBuilder out) {
        if (node.isLeaf()) {
            out.append(node.text);
            return;
        }
        appendAll(node.left, out);
        appendAll(node.right, out);
    }

    @Override
    public long charsCopiedByLastOp() {
        return copiedByLastOp;
    }

    @Override
    public long charsCopiedTotal() {
        return copiedTotal;
    }

    public int leafMax() {
        return leafMax;
    }

    /** 잎 하나짜리 로프는 0 이다. */
    public int depth() {
        return root.depth;
    }

    public int leafCount() {
        return countLeaves(root);
    }

    private static int countLeaves(Node node) {
        return node.isLeaf() ? 1 : countLeaves(node.left) + countLeaves(node.right);
    }

    public int nodeCount() {
        return countNodes(root);
    }

    private static int countNodes(Node node) {
        return node.isLeaf() ? 1 : 1 + countNodes(node.left) + countNodes(node.right);
    }

    /** 잎에 담긴 조각들. 빈 잎은 빼고 문서 순서대로. */
    public List<String> leaves() {
        List<Node> nodes = new ArrayList<>();
        collectLeaves(root, nodes);
        List<String> out = new ArrayList<>(nodes.size());
        for (Node n : nodes) {
            out.add(n.text);
        }
        return out;
    }

    /** charAt 이 지금까지 지난 노드 수. 계측 전용이다. */
    public long charAtVisits() {
        return charAtVisits;
    }

    public void resetCharAtVisits() {
        charAtVisits = 0;
    }

    Node root() {
        return root;
    }

    private void checkRange(int from, int to) {
        if (from < 0 || to > root.length || from > to) {
            throw new IndexOutOfBoundsException(
                    "[" + from + ", " + to + ") (길이 " + root.length + ")");
        }
    }
}
