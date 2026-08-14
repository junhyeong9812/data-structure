package com.datastructure.merkle;

/**
 * 접두사를 붙이지 않는 규칙. 공격당하려고 존재하는 클래스다.
 *
 * 23번의 MoveToRootTree 와 같은 자리다. 틀린 쪽을 실제로 돌려봐야
 * 맞는 쪽이 무엇을 사는지 알 수 있으므로 지우지 않고 둔다.
 *
 * 여기서는 잎이든 내부든 그냥 해시한다. 그래서 다음이 성립한다.
 *
 * <pre>
 *   leafHash(x || y) == nodeHash(x, y)
 * </pre>
 *
 * 왼쪽은 길이 64인 블록 하나의 잎 해시이고 오른쪽은 내부 노드의 해시인데 값이 같다.
 * 공격자는 이 성질로 블록 4개짜리 파일과 같은 뿌리를 갖는 블록 2개짜리 파일을 만든다.
 * SecondPreimageTest 가 그 위조를 실제로 성공시킨다.
 *
 * 이 클래스에는 TODO 가 없다. 틀린 코드를 두 번 쓸 이유는 없다.
 */
public final class UnprefixedHashing implements MerkleHashing {

    private final HashFunction function;

    public UnprefixedHashing(HashFunction function) {
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
        return function.hash(block);
    }

    @Override
    public byte[] nodeHash(byte[] left, byte[] right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("자식 해시는 null 일 수 없다");
        }
        byte[] input = new byte[left.length + right.length];
        System.arraycopy(left, 0, input, 0, left.length);
        System.arraycopy(right, 0, input, left.length, right.length);
        return function.hash(input);
    }

    @Override
    public HashFunction function() {
        return function;
    }

    @Override
    public String toString() {
        return "접두사 없음 (취약)";
    }
}
