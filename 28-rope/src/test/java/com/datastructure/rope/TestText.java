package com.datastructure.rope;

/**
 * 측정에 쓰는 본문. 내용이 결정적이어야 숫자가 재현된다.
 *
 * 같은 글자만 반복하면 longestCommonPrefix 측정이 우연히 통과할 수 있어서
 * 26글자를 7칸씩 건너뛰며 돈다. 파이썬 참조 구현도 같은 규칙을 쓴다.
 */
final class TestText {

    private TestText() {
    }

    static String of(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append((char) ('a' + (i * 7) % 26));
        }
        return sb.toString();
    }
}
