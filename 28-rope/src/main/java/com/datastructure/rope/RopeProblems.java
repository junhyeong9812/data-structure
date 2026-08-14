package com.datastructure.rope;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * 로프로 푸는 문제 둘.
 *
 * 하나는 이 자료구조의 값을 재는 것이고, 하나는 불변이라서만 할 수 있는 것이다.
 */
public final class RopeProblems {

    private RopeProblems() {
    }

    /** 공통 접두사의 길이와 그것을 알아내려고 실제로 비교한 글자 수. */
    public record Lcp(int length, long comparedChars) {
    }

    /**
     * 문제 1: 편집 목록을 순서대로 적용한다.
     *
     * 에디터가 하는 일이 이것이다. 키 입력 하나가 편집 하나이고, 문서는 그때마다 새로 만들어진다.
     *
     * 같은 목록을 StringBuilderStore 와 Rope 에 주고 charsCopiedTotal 을 비교하는 것이
     * 이 박스의 한계 측정이다. 답은 반드시 같고 옮긴 글자 수만 다르다.
     */
    public static CharSequenceStore applyEdits(CharSequenceStore doc, List<Edit> edits) {
        if (doc == null || edits == null) {
            throw new IllegalArgumentException("문서와 편집 목록이 필요하다");
        }
        // TODO 11: 편집을 앞에서부터 하나씩 적용한다.
        //
        //   Edit 은 sealed 이므로 switch 패턴 매칭으로 두 경우를 다 덮을 수 있다.
        //   default 를 쓰지 마라. 나중에 Replace 가 생기면 컴파일러가 잡아줘야 한다.
        //
        // **반환값을 받아 다음 편집에 넘겨라.** 이 계약에서 doc 은 안 바뀐다.
        // doc.insert(...) 를 부르고 결과를 버리면 아무 일도 안 일어난 것이 된다.
        // 편집 목록이 비었으면 받은 문서를 그대로 돌려주면 된다.
        throw new UnsupportedOperationException("TODO 11: applyEdits");
    }

    /** 문제 2 의 요구 형태. 길이만 필요할 때 쓴다. */
    public static int longestCommonPrefixLength(CharSequenceStore a, CharSequenceStore b) {
        return longestCommonPrefix(a, b).length();
    }

    /**
     * 문제 2: 두 문서의 공통 접두사 길이. 비교한 글자 수도 같이 돌려준다.
     *
     * 로프 둘이면 구조를 이용하고, 아니면 나이브로 간다.
     */
    public static Lcp longestCommonPrefix(CharSequenceStore a, CharSequenceStore b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("두 문서가 필요하다");
        }
        if (a instanceof Rope ra && b instanceof Rope rb) {
            return sharedAwarePrefix(ra, rb);
        }
        return naiveLongestCommonPrefix(a, b);
    }

    /**
     * 같은 부분트리를 만나면 통째로 건너뛴다.
     *
     * 근거는 불변성 하나다. 노드를 아무도 안 고치므로 참조가 같으면 내용도 같다.
     * 편집으로 갈라져 나온 두 문서는 앞부분이 실제로 같은 객체라서 이 최적화가 듣는다.
     * 따로 만든 두 로프에는 공유가 없어 나이브와 같아진다. 그것도 정직하게 재서 보여준다.
     */
    private static Lcp sharedAwarePrefix(Rope a, Rope b) {
        Deque<Rope.Node> stackA = new ArrayDeque<>();
        Deque<Rope.Node> stackB = new ArrayDeque<>();
        if (a.root().length > 0) {
            stackA.push(a.root());
        }
        if (b.root().length > 0) {
            stackB.push(b.root());
        }
        // TODO 12: 양쪽을 왼쪽부터 동시에 훑는다. 상태가 넷이다.
        //
        //   stackA, stackB   아직 안 본 부분트리들. pop 해서 잎이면 글자를 보고,
        //                    내부 노드면 right 를 먼저 push 하고 left 를 push 한다
        //                    (그래야 다음 pop 이 왼쪽이다)
        //   bufA/offA        지금 보고 있는 잎의 문자열과 그 안에서의 자리
        //   matched          여기까지 같다고 확정한 글자 수
        //   compared         **실제로 두 글자를 맞대 본 횟수.** 이것이 측정 대상이다
        //
        // 루프의 첫 검사가 이 문제의 전부다.
        //
        //   양쪽 다 잎 중간이 아니고(offA 와 offB 가 각 buf 의 끝) 두 스택의 맨 위가
        //   **같은 객체이면**(== 로 본다. equals 가 아니다) 둘 다 pop 하고
        //   matched 에 그 부분트리의 length 를 통째로 더한다. compared 는 안 는다.
        //
        // 잎 중간에서는 이 검사를 하면 안 된다. 양쪽이 같은 자리에 서 있지 않기 때문이다.
        //
        // 나머지는 평범하다. 양쪽에 볼 글자가 있으면 min(남은 A, 남은 B) 만큼 한 글자씩
        // 비교하며 compared 를 올리고, 다르면 그 자리에서 (matched + k, compared) 로 끝낸다.
        // 한쪽이 다 떨어지면 거기까지가 답이다.
        //
        // 잎 크기가 다른 두 로프도 들어온다. 잎 경계가 어긋나도 답이 같아야 한다.
        throw new UnsupportedOperationException("TODO 12: sharedAwarePrefix");
    }

    /**
     * 나이브 비교. 미리 채워뒀다. 대조와 대비에 쓴다.
     *
     * 두 문서가 실제로 같은 조각을 공유하고 있어도 이쪽은 알 길이 없다.
     * charAt 으로 한 글자씩 물어보는 순간 구조가 안 보이기 때문이다.
     */
    public static Lcp naiveLongestCommonPrefix(CharSequenceStore a, CharSequenceStore b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("두 문서가 필요하다");
        }
        int n = Math.min(a.length(), b.length());
        long compared = 0;
        for (int i = 0; i < n; i++) {
            compared++;
            if (a.charAt(i) != b.charAt(i)) {
                return new Lcp(i, compared);
            }
        }
        return new Lcp(n, compared);
    }
}
