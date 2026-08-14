package com.datastructure.merkle;

/**
 * 일부러 약한 해시. 바이트를 그냥 더해서 4바이트로 낸다.
 *
 * <h2>무엇이 약한가</h2>
 *
 * 덧셈은 순서를 무시한다. 그래서 같은 바이트를 섞어 놓기만 해도 값이 같고,
 * 한 바이트를 1 올리고 다른 바이트를 1 내려도 같다. 충돌을 종이에 적어 만들 수 있다.
 *
 * <pre>
 *   hash({1, 2}) == hash({2, 1}) == hash({3, 0})
 * </pre>
 *
 * 이 클래스가 있는 이유는 하나다. 머클 트리의 "확정적"이라는 말이
 * 해시 위에 얹힌 조건부 문장임을 눈으로 보이기 위해서다.
 * 충돌을 만들 수 있으면 서로 다른 파일이 같은 뿌리를 갖고, 그 순간 증명도 비교도 전부 거짓말이 된다.
 *
 * 빠르기도 하다. 잎 100만 개짜리 트리를 지어 증명 길이를 재는 테스트에서는
 * 값의 안전성이 필요 없으므로 이쪽을 쓴다.
 *
 * 이 클래스에는 TODO 가 없다.
 */
public final class ToyHash implements HashFunction {

    @Override
    public byte[] hash(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("입력은 null 일 수 없다");
        }
        int acc = 0;
        for (byte b : data) {
            acc += b & 0xff;
        }
        return new byte[] {
            (byte) (acc >>> 24),
            (byte) (acc >>> 16),
            (byte) (acc >>> 8),
            (byte) acc
        };
    }

    @Override
    public String toString() {
        return "ToyHash(바이트 합)";
    }
}
