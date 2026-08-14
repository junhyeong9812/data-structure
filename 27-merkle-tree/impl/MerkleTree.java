package com.datastructure.merkle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MerkleTree {

    private final MerkleHashing hashing;
    private final byte[][][] levels;
    private long comparisons;

    public MerkleTree(List<byte[]> blocks, HashFunction function) {
        this(blocks, new PrefixedHashing(function));
    }

    public MerkleTree(List<byte[]> blocks, MerkleHashing hashing) {
        if (hashing == null) {
            throw new IllegalArgumentException("해시 규칙이 필요하다");
        }
        if (blocks == null || blocks.isEmpty()) {
            throw new IllegalArgumentException("블록이 하나 이상 있어야 한다");
        }
        this.hashing = hashing;
        this.levels = build(blocks, hashing);
    }

    private MerkleTree(byte[][][] levels, MerkleHashing hashing) {
        this.levels = levels;
        this.hashing = hashing;
    }

    private static byte[][][] build(List<byte[]> blocks, MerkleHashing hashing) {
        byte[][] current = new byte[blocks.size()][];
        for (int i = 0; i < blocks.size(); i++) {
            byte[] block = blocks.get(i);
            if (block == null) {
                throw new IllegalArgumentException("블록은 null 일 수 없다: " + i + "번");
            }
            current[i] = hashing.leafHash(block);
        }
        List<byte[][]> out = new ArrayList<>();
        out.add(current);
        while (current.length > 1) {
            byte[][] next = new byte[(current.length + 1) / 2][];
            for (int i = 0; i < next.length; i++) {
                int left = 2 * i;
                int right = left + 1;
                next[i] = right < current.length
                        ? hashing.nodeHash(current[left], current[right])
                        : current[left];
            }
            out.add(next);
            current = next;
        }
        return out.toArray(new byte[0][][]);
    }

    public byte[] rootHash() {
        return levels[levels.length - 1][0].clone();
    }

    public byte[] leafHash(int leafIndex) {
        return hashAt(0, requireLeafIndex(leafIndex)).clone();
    }

    public MerkleProof proofFor(int leafIndex) {
        int index = requireLeafIndex(leafIndex);
        List<MerkleProof.Step> steps = new ArrayList<>();
        for (int level = 0; level < levels.length - 1; level++) {
            byte[][] current = levels[level];
            boolean even = index % 2 == 0;
            int sibling = even ? index + 1 : index - 1;
            if (sibling < current.length) {
                steps.add(new MerkleProof.Step(current[sibling], !even));
            }
            index /= 2;
        }
        return new MerkleProof(steps, hashing);
    }

    public int findFirstDifference(MerkleTree other) {
        requireComparable(other);
        int level = levels.length - 1;
        if (sameNode(other, level, 0)) {
            return -1;
        }
        int index = 0;
        while (level > 0) {
            int left = 2 * index;
            int right = left + 1;
            if (right >= levels[level - 1].length || !sameNode(other, level - 1, left)) {
                index = left;
            } else {
                index = right;
            }
            level--;
        }
        return index;
    }

    public MerkleTree withLeafReplaced(int leafIndex, byte[] newBlock) {
        int index = requireLeafIndex(leafIndex);
        if (newBlock == null) {
            throw new IllegalArgumentException("블록은 null 일 수 없다");
        }
        byte[][][] copy = new byte[levels.length][][];
        for (int level = 0; level < levels.length; level++) {
            copy[level] = levels[level].clone();
        }
        copy[0][index] = hashing.leafHash(newBlock);
        for (int level = 0; level < levels.length - 1; level++) {
            int parent = index / 2;
            int left = 2 * parent;
            int right = left + 1;
            byte[][] current = copy[level];
            copy[level + 1][parent] = right < current.length
                    ? hashing.nodeHash(current[left], current[right])
                    : current[left];
            index = parent;
        }
        return new MerkleTree(copy, hashing);
    }

    public boolean sameNode(MerkleTree other, int level, int index) {
        comparisons++;
        return Arrays.equals(hashAt(level, index), other.hashAt(level, index));
    }

    public byte[] hashAt(int level, int index) {
        if (level < 0 || level >= levels.length) {
            throw new IndexOutOfBoundsException("층이 범위를 벗어났다: " + level + " (높이 " + height() + ")");
        }
        if (index < 0 || index >= levels[level].length) {
            throw new IndexOutOfBoundsException(
                    "노드가 범위를 벗어났다: " + index + " (" + level + "층은 " + levels[level].length + "개)");
        }
        return levels[level][index];
    }

    public int levelSize(int level) {
        if (level < 0 || level >= levels.length) {
            throw new IndexOutOfBoundsException("층이 범위를 벗어났다: " + level + " (높이 " + height() + ")");
        }
        return levels[level].length;
    }

    public int leafCount() {
        return levels[0].length;
    }

    public int height() {
        return levels.length - 1;
    }

    public MerkleHashing hashing() {
        return hashing;
    }

    public long comparisons() {
        return comparisons;
    }

    public void resetComparisons() {
        comparisons = 0;
    }

    private int requireLeafIndex(int leafIndex) {
        if (leafIndex < 0 || leafIndex >= leafCount()) {
            throw new IndexOutOfBoundsException(
                    "잎이 범위를 벗어났다: " + leafIndex + " (잎 " + leafCount() + "개)");
        }
        return leafIndex;
    }

    private void requireComparable(MerkleTree other) {
        if (other == null) {
            throw new IllegalArgumentException("비교 대상이 필요하다");
        }
        if (other.leafCount() != leafCount()) {
            throw new IllegalArgumentException(
                    "잎 개수가 다르면 자리를 맞출 수 없다: " + leafCount() + " 대 " + other.leafCount());
        }
    }
}
