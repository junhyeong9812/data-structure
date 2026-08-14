package com.datastructure.rope;

/**
 * 기준선. 배열 하나에 문서를 통째로 담는다.
 *
 * <h2>먼저 이것부터 만든다</h2>
 *
 * 로프를 만들기 전에 이쪽을 끝내라. 세 메서드를 채우면서
 * "왜 한 글자를 넣는데 문서 전체를 옮기는가" 를 손으로 보는 것이 목적이다.
 *
 * <h2>왜 제자리에서 안 고치는가</h2>
 *
 * 실제 StringBuilder 는 제자리를 고친다. insert 는 뒤쪽만 밀고(n - index),
 * append 는 상환 O(1) 이다. 여기서는 그렇게 하지 않는다.
 *
 * 계약이 값이기 때문이다. 편집 전 문서가 살아 있어야 두 구현을 같은 자리에 놓고 비교할 수 있고,
 * 배열 위에서 그것을 지키는 방법은 매번 새 버퍼로 옮기는 것뿐이다.
 * 제자리 편집을 허용해도 가운데 삽입이 O(n) 이라는 사실은 안 바뀐다.
 *
 * 그래서 buf 는 만든 뒤 절대 안 고친다. 새 저장소에 그대로 넘겨 써도 안전하다.
 */
public final class StringBuilderStore implements CharSequenceStore {

    private final StringBuilder buf;
    private final long copiedByLastOp;
    private final long copiedTotal;

    public StringBuilderStore(String text) {
        if (text == null) {
            throw new IllegalArgumentException("문자열이 null 이다");
        }
        this.buf = new StringBuilder(text);
        this.copiedByLastOp = 0;
        this.copiedTotal = 0;
    }

    /** 새 버퍼와 이번에 옮긴 글자 수로 다음 저장소를 만든다. 누적은 여기서 더한다. */
    private StringBuilderStore(StringBuilder buf, long copied, long previousTotal) {
        this.buf = buf;
        this.copiedByLastOp = copied;
        this.copiedTotal = previousTotal + copied;
    }

    @Override
    public int length() {
        return buf.length();
    }

    @Override
    public char charAt(int index) {
        if (index < 0 || index >= buf.length()) {
            throw new IndexOutOfBoundsException("index " + index + " (길이 " + buf.length() + ")");
        }
        return buf.charAt(index);
    }

    @Override
    public String substring(int from, int to) {
        checkRange(from, to);
        return buf.substring(from, to);
    }

    @Override
    public StringBuilderStore concat(CharSequenceStore other) {
        if (other == null) {
            throw new IllegalArgumentException("붙일 저장소가 null 이다");
        }
        // TODO 1: 길이 n + m 짜리 새 StringBuilder 에 양쪽을 차례로 담는다.
        //
        //   new StringBuilderStore(next, 옮긴글자수, copiedTotal) 로 돌려준다.
        //
        // 옮긴 글자 수가 얼마인가. 왼쪽 n 글자도 오른쪽 m 글자도 새 버퍼로 옮겨야 한다.
        // 붙일 자리가 없어서다. 배열은 자기 크기만큼만 잡혀 있다.
        //
        // **여기가 28번이 존재하는 이유다.** 로프는 같은 연산에서 0 이다.
        throw new UnsupportedOperationException("TODO 1: concat");
    }

    @Override
    public StringBuilderStore insert(int index, String s) {
        if (s == null) {
            throw new IllegalArgumentException("넣을 문자열이 null 이다");
        }
        if (index < 0 || index > buf.length()) {
            throw new IndexOutOfBoundsException("index " + index + " (길이 " + buf.length() + ")");
        }
        // TODO 2: 앞 조각, 넣을 문자열, 뒤 조각을 순서대로 새 버퍼에 담는다.
        //
        //   append(buf, 0, index) -> append(s) -> append(buf, index, n)
        //
        // 옮긴 글자 수는 n 이다. **넣는 s 는 안 센다.**
        // 이미 있는 문자열을 자리에 놓는 것과 있던 문서를 통째로 다시 쓰는 것은 다른 일이고,
        // 로프도 s 를 안 센다. 두 구현이 같은 규칙으로 세야 비교가 성립한다.
        //
        // s 가 빈 문자열이면 할 일이 없다. 0 을 옮긴 저장소를 돌려줘라
        // (buf 를 그대로 넘겨도 된다. 아무도 안 고치니까).
        throw new UnsupportedOperationException("TODO 2: insert");
    }

    @Override
    public StringBuilderStore delete(int from, int to) {
        checkRange(from, to);
        // TODO 3: 지운 구간을 뺀 나머지를 새 버퍼에 담는다.
        //
        // 옮긴 글자 수가 insert 와 다르다. 여기서는 살아남는 글자만 옮기므로
        // n - (to - from) 이다. from == to 면 0 이다.
        //
        // 많이 지울수록 싸진다는 것이 배열의 성질이다. 로프는 반대다.
        // 지운 구간이 넓으면 잎을 두 번 쪼개야 한다(CopyCostTest 가 그 장면을 잡는다).
        throw new UnsupportedOperationException("TODO 3: delete");
    }

    /**
     * 쪼개기. 미리 채워뒀다. TODO 1~3 을 어떻게 쓰는지 여기서 보라.
     *
     * 양쪽을 다 새로 만들어야 하므로 어디서 쪼개든 n 글자를 옮긴다.
     * 로프는 잎 경계에서 쪼개면 0 이고 잎 한가운데여도 leafMax 이하다.
     */
    @Override
    public Split split(int index) {
        if (index < 0 || index > buf.length()) {
            throw new IndexOutOfBoundsException("index " + index + " (길이 " + buf.length() + ")");
        }
        int n = buf.length();
        StringBuilder l = new StringBuilder(index);
        l.append(buf, 0, index);
        StringBuilder r = new StringBuilder(n - index);
        r.append(buf, index, n);
        return new Split(new StringBuilderStore(l, n, copiedTotal),
                new StringBuilderStore(r, n, copiedTotal));
    }

    @Override
    public String toString() {
        return buf.toString();
    }

    @Override
    public long charsCopiedByLastOp() {
        return copiedByLastOp;
    }

    @Override
    public long charsCopiedTotal() {
        return copiedTotal;
    }

    private void checkRange(int from, int to) {
        if (from < 0 || to > buf.length() || from > to) {
            throw new IndexOutOfBoundsException(
                    "[" + from + ", " + to + ") (길이 " + buf.length() + ")");
        }
    }
}
