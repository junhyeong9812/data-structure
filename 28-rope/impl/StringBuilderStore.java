package com.datastructure.rope;

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
        int n = buf.length();
        int m = other.length();
        StringBuilder next = new StringBuilder(n + m);
        next.append(buf);
        next.append(other.toString());
        return new StringBuilderStore(next, (long) n + m, copiedTotal);
    }

    @Override
    public StringBuilderStore insert(int index, String s) {
        if (s == null) {
            throw new IllegalArgumentException("넣을 문자열이 null 이다");
        }
        if (index < 0 || index > buf.length()) {
            throw new IndexOutOfBoundsException("index " + index + " (길이 " + buf.length() + ")");
        }
        if (s.isEmpty()) {
            return new StringBuilderStore(buf, 0, copiedTotal);
        }
        int n = buf.length();
        StringBuilder next = new StringBuilder(n + s.length());
        next.append(buf, 0, index);
        next.append(s);
        next.append(buf, index, n);
        return new StringBuilderStore(next, n, copiedTotal);
    }

    @Override
    public StringBuilderStore delete(int from, int to) {
        checkRange(from, to);
        if (from == to) {
            return new StringBuilderStore(buf, 0, copiedTotal);
        }
        int n = buf.length();
        StringBuilder next = new StringBuilder(n - (to - from));
        next.append(buf, 0, from);
        next.append(buf, to, n);
        return new StringBuilderStore(next, n - (long) (to - from), copiedTotal);
    }

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
