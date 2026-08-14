package com.datastructure.merkle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class MerkleProof {

    public static final class Step {

        private final byte[] siblingHash;
        private final boolean siblingIsLeft;

        public Step(byte[] siblingHash, boolean siblingIsLeft) {
            if (siblingHash == null) {
                throw new IllegalArgumentException("형제 해시가 필요하다");
            }
            this.siblingHash = siblingHash.clone();
            this.siblingIsLeft = siblingIsLeft;
        }

        public byte[] siblingHash() {
            return siblingHash.clone();
        }

        public boolean siblingIsLeft() {
            return siblingIsLeft;
        }

        byte[] rawSiblingHash() {
            return siblingHash;
        }

        public Step flipped() {
            return new Step(siblingHash, !siblingIsLeft);
        }
    }

    private final List<Step> steps;
    private final MerkleHashing hashing;

    public MerkleProof(List<Step> steps, MerkleHashing hashing) {
        if (steps == null) {
            throw new IllegalArgumentException("걸음 목록이 필요하다");
        }
        if (hashing == null) {
            throw new IllegalArgumentException("해시 규칙이 필요하다");
        }
        for (int i = 0; i < steps.size(); i++) {
            if (steps.get(i) == null) {
                throw new IllegalArgumentException("걸음은 null 일 수 없다: " + i + "번");
            }
        }
        this.steps = Collections.unmodifiableList(new ArrayList<>(steps));
        this.hashing = hashing;
    }

    public boolean verify(byte[] leafHash, byte[] expectedRoot) {
        if (leafHash == null || expectedRoot == null) {
            throw new IllegalArgumentException("잎 해시와 뿌리 해시가 필요하다");
        }
        byte[] current = leafHash;
        for (Step step : steps) {
            byte[] sibling = step.rawSiblingHash();
            current = step.siblingIsLeft()
                    ? hashing.nodeHash(sibling, current)
                    : hashing.nodeHash(current, sibling);
        }
        return Arrays.equals(current, expectedRoot);
    }

    public List<Step> steps() {
        return steps;
    }

    public int size() {
        return steps.size();
    }

    public MerkleHashing hashing() {
        return hashing;
    }
}
