package com.datastructure.merkle;

import java.util.ArrayList;
import java.util.List;

/**
 * 머클 트리로 푸는 문제 둘. rsync 와 카산드라가 실제로 하는 일이다.
 */
public final class MerkleProblems {

    private MerkleProblems() {
    }

    /**
     * 문제 1: 두 트리에서 다른 블록을 전부 찾는다. 인덱스 오름차순.
     *
     * findFirstDifference 는 처음 하나만 찾았다. 여기서는 전부 찾는다.
     * 전수 비교와 답이 같아야 하고, 비교한 노드 수는 훨씬 적어야 한다.
     * 블록 1024개 중 3개가 다르면 전수는 1024번이고 이 방법은 60번 안쪽이다.
     *
     * 이것이 rsync 의 델타 전송과 카산드라의 anti-entropy 복구다.
     * 다른 블록만 골라 보내려면 먼저 어느 블록이 다른지 싸게 알아야 한다.
     *
     * 잎 개수가 다르면 IllegalArgumentException.
     */
    public static List<Integer> diffBlocks(MerkleTree local, MerkleTree remote) {
        if (local == null || remote == null) {
            throw new IllegalArgumentException("트리 둘이 필요하다");
        }
        if (local.leafCount() != remote.leafCount()) {
            throw new IllegalArgumentException(
                    "잎 개수가 다르면 자리를 맞출 수 없다: " + local.leafCount() + " 대 " + remote.leafCount());
        }
        // TODO 8: 뿌리에서 시작해 다른 부분트리만 따라 내려간다.
        //
        //   재귀 하나면 된다. (level, index) 에서
        //     1. local.sameNode(remote, level, index) 면 **통째로 건너뛴다**. 여기가 이 문제의 전부다
        //     2. 0층이면 그 인덱스를 답에 담는다
        //     3. 아니면 왼쪽 자식(2*index) 과 오른쪽 자식(2*index+1) 로 내려간다
        //        오른쪽 자식은 levelSize 를 넘을 수 있다(승격). 넘으면 왼쪽만 본다
        //
        // 왼쪽을 먼저 부르면 답이 저절로 오름차순이다. 나중에 정렬할 필요가 없다.
        //
        // 1번을 빼고 잎까지 다 내려가도 **답은 똑같이 맞는다.** 걸음 수만 1024번이 된다.
        // 정답과 오답이 답으로 안 갈리는 자리라 DiffTest 가 비교 횟수를 직접 센다.
        // 이 자료구조를 쓰는 이유가 통째로 그 숫자에 들어 있다.
        throw new UnsupportedOperationException("TODO 8: diffBlocks");
    }

    /**
     * 문제 2: 증명 여러 개를 한 번에 검증한다. 하나라도 틀리면 false.
     *
     * proofs.get(i) 가 leafHashes.get(i) 에 대한 증명이다.
     * 개수가 다르거나 null 이 섞이면 IllegalArgumentException.
     *
     * 검증하는 쪽에는 트리가 없다. 뿌리 하나와 증명들뿐이다.
     * 인증서 투명성 로그를 감시하는 쪽이 정확히 이 자세로 서 있다.
     */
    public static boolean verifyBatch(byte[] root, List<MerkleProof> proofs, List<byte[]> leafHashes) {
        if (root == null || proofs == null || leafHashes == null) {
            throw new IllegalArgumentException("뿌리, 증명 목록, 잎 해시 목록이 모두 필요하다");
        }
        if (proofs.size() != leafHashes.size()) {
            throw new IllegalArgumentException(
                    "증명과 잎 해시의 개수가 다르다: " + proofs.size() + " 대 " + leafHashes.size());
        }
        // TODO 9: 짝을 지어 하나씩 검증하고 하나라도 실패하면 false.
        //
        //   i 번째 증명과 i 번째 잎 해시가 짝이다. null 이 섞이면 IllegalArgumentException.
        //
        // **하나라도 실패하면 즉시 false 다.** 통과한 개수를 세서 "절반 넘게 맞으면 true" 같은
        // 완화를 넣으면 이 자료구조가 파는 유일한 물건이 사라진다.
        // 11번 블룸 필터는 확률을 팔았지만 여기서는 아무것도 팔지 않는다.
        //
        // 배치라고 해서 해시 계산이 줄지는 않는다. 증명 하나당 log n 번씩 그대로 든다.
        // 줄어드는 것은 왕복(round trip) 이지 계산이 아니다. 이름에 속지 마라.
        throw new UnsupportedOperationException("TODO 9: verifyBatch");
    }
}
