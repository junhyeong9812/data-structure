package com.datastructure.merkle;

import java.util.ArrayList;
import java.util.List;

public final class MerkleProblems {

    private MerkleProblems() {
    }

    public static List<Integer> diffBlocks(MerkleTree local, MerkleTree remote) {
        if (local == null || remote == null) {
            throw new IllegalArgumentException("트리 둘이 필요하다");
        }
        if (local.leafCount() != remote.leafCount()) {
            throw new IllegalArgumentException(
                    "잎 개수가 다르면 자리를 맞출 수 없다: " + local.leafCount() + " 대 " + remote.leafCount());
        }
        List<Integer> out = new ArrayList<>();
        collect(local, remote, local.height(), 0, out);
        return out;
    }

    private static void collect(MerkleTree local, MerkleTree remote, int level, int index, List<Integer> out) {
        if (local.sameNode(remote, level, index)) {
            return;
        }
        if (level == 0) {
            out.add(index);
            return;
        }
        int left = 2 * index;
        int right = left + 1;
        collect(local, remote, level - 1, left, out);
        if (right < local.levelSize(level - 1)) {
            collect(local, remote, level - 1, right, out);
        }
    }

    public static boolean verifyBatch(byte[] root, List<MerkleProof> proofs, List<byte[]> leafHashes) {
        if (root == null || proofs == null || leafHashes == null) {
            throw new IllegalArgumentException("뿌리, 증명 목록, 잎 해시 목록이 모두 필요하다");
        }
        if (proofs.size() != leafHashes.size()) {
            throw new IllegalArgumentException(
                    "증명과 잎 해시의 개수가 다르다: " + proofs.size() + " 대 " + leafHashes.size());
        }
        for (int i = 0; i < proofs.size(); i++) {
            MerkleProof proof = proofs.get(i);
            byte[] leafHash = leafHashes.get(i);
            if (proof == null || leafHash == null) {
                throw new IllegalArgumentException("증명과 잎 해시는 null 일 수 없다: " + i + "번");
            }
            if (!proof.verify(leafHash, root)) {
                return false;
            }
        }
        return true;
    }
}
