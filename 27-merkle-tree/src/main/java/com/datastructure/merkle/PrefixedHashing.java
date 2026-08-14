package com.datastructure.merkle;

/**
 * 잎에는 0x00, 내부에는 0x01 을 붙이고 해시하는 규칙. 이것이 정답 쪽이다.
 *
 * 접두사 한 바이트가 무엇을 사는지는 MerkleHashing 의 설명과
 * SecondPreimageTest 를 보라. 이 클래스에서 그 한 바이트를 빼면
 * 서로 다른 블록 목록이 같은 뿌리를 갖는 위조가 성립한다.
 */
public final class PrefixedHashing implements MerkleHashing {

    private final HashFunction function;

    public PrefixedHashing(HashFunction function) {
        if (function == null) {
            throw new IllegalArgumentException("해시 함수가 필요하다");
        }
        this.function = function;
    }

    @Override
    public byte[] leafHash(byte[] block) {
        if (block == null) {
            throw new IllegalArgumentException("블록은 null 일 수 없다");
        }
        // TODO 1: LEAF_PREFIX 한 바이트를 앞에 붙이고 해시한다.
        //
        //   hash(0x00 || block)
        //
        // byte[] 를 새로 만들고 System.arraycopy 로 1번 칸부터 채우면 된다.
        // (01번 동적 배열에서 쓰던 그 함수다)
        //
        // **접두사를 안 붙여도 이 문제의 계약 테스트는 거의 다 통과한다.**
        // 뿌리도 나오고 증명도 검증되고 차이도 찾아진다. 딱 하나가 깨진다.
        // 내부 노드를 잎인 척 제출하는 위조를 막지 못한다.
        // 그래서 이 한 바이트는 "왜 있는지 모르겠는 줄"처럼 보인다. 그게 이 문제의 절반이다.
        throw new UnsupportedOperationException("TODO 1: leafHash");
    }

    @Override
    public byte[] nodeHash(byte[] left, byte[] right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("자식 해시는 null 일 수 없다");
        }
        // TODO 2: NODE_PREFIX 를 붙이고 자식 둘을 이어 해시한다.
        //
        //   hash(0x01 || left || right)
        //
        // 좌우 순서가 값에 들어간다는 점을 보라. nodeHash(a, b) 와 nodeHash(b, a) 는 다르다.
        // 그래서 증명이 형제 해시만으로는 부족하고 방향을 같이 담아야 한다.
        //
        // 버퍼 길이는 left.length + right.length + 1 이다. left.length * 2 + 1 로 잡아도
        // 지금은 맞는데, 그건 "두 자식이 같은 해시 함수의 출력이라 길이가 같다"에 기댄 것이다.
        // 값이 아니라 가정에 기대는 줄은 나중에 조용히 틀린다.
        throw new UnsupportedOperationException("TODO 2: nodeHash");
    }

    @Override
    public HashFunction function() {
        return function;
    }
}
