package com.datastructure.merkle;

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
        byte[] input = new byte[block.length + 1];
        input[0] = LEAF_PREFIX;
        System.arraycopy(block, 0, input, 1, block.length);
        return function.hash(input);
    }

    @Override
    public byte[] nodeHash(byte[] left, byte[] right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("자식 해시는 null 일 수 없다");
        }
        byte[] input = new byte[left.length + right.length + 1];
        input[0] = NODE_PREFIX;
        System.arraycopy(left, 0, input, 1, left.length);
        System.arraycopy(right, 0, input, 1 + left.length, right.length);
        return function.hash(input);
    }

    @Override
    public HashFunction function() {
        return function;
    }
}
