package com.datastructure.merkle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 포함 증명. "이 잎이 그 뿌리 밑에 있다"를 파일 없이 보이는 물건이다.
 *
 * <h2>무엇이 들었나</h2>
 *
 * 잎에서 뿌리까지 올라가는 경로의 형제 해시들, 그리고 각 형제가 왼쪽인지 오른쪽인지.
 * 잎이 100만 개면 20개, SHA-256 이니 640바이트다. 파일은 안 보낸다.
 *
 * <pre>
 *   cur = 잎 해시
 *   형제가 왼쪽이면  cur = nodeHash(형제, cur)
 *   오른쪽이면       cur = nodeHash(cur, 형제)
 *   다 합친 cur 이 뿌리와 같으면 통과
 * </pre>
 *
 * <h2>방향이 왜 필요한가</h2>
 *
 * nodeHash 는 좌우 순서가 값에 들어간다. 방향을 잃으면 어느 쪽에 붙일지 몰라
 * 검증이 반반 확률로 실패한다. 방향 비트가 곧 그 잎이 트리 어디에 있는지를 말해주는 값이고,
 * 형제 해시 목록과 방향 비트 목록을 합친 것이 경로 그 자체다.
 *
 * <h2>이 증명이 보장하지 않는 것</h2>
 *
 * 뿌리가 맞다는 것만 보인다. 그 뿌리가 진짜인지는 이 물건 밖의 문제다.
 * 공격자가 자기 뿌리를 주면 자기 증명이 통과한다. 그래서 실무에서는 뿌리를 서명하거나
 * 블록체인 같은 다른 경로로 받는다. 11번 블룸 필터가 "없다"만 보장했듯이
 * 여기서 보장하는 것도 딱 한 문장이다.
 */
public final class MerkleProof {

    /**
     * 경로 한 걸음. 형제 해시와 그 형제가 왼쪽인지.
     *
     * record 로 만들지 않았다. record 의 equals 는 byte[] 를 참조로 비교해서
     * 값이 같은 두 걸음이 다르다고 나온다. 컴파일도 되고 예외도 안 난다.
     * 그 조용한 틀림을 문제집에 심고 싶지 않아 평범한 클래스로 둔다.
     */
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

        /** 형제 해시(복사본). */
        public byte[] siblingHash() {
            return siblingHash.clone();
        }

        /** 형제가 왼쪽에 있나. false 면 오른쪽이다. */
        public boolean siblingIsLeft() {
            return siblingIsLeft;
        }

        /** 검증 안쪽에서만 쓰는 무복사 접근. 걸음마다 32바이트를 새로 뜨지 않으려고 둔다. */
        byte[] rawSiblingHash() {
            return siblingHash;
        }

        /** 방향만 뒤집은 걸음. 잘못된 증명이 거부되는지 보는 테스트가 쓴다. */
        public Step flipped() {
            return new Step(siblingHash, !siblingIsLeft);
        }
    }

    private final List<Step> steps;
    private final MerkleHashing hashing;

    /**
     * 잎 쪽부터 뿌리 쪽 순서로 걸음을 담는다.
     *
     * 검증하는 쪽이 이 규칙을 알아야 하므로 순서가 곧 계약이다.
     * steps 나 hashing 이 null 이면, 또는 걸음 중 null 이 있으면 IllegalArgumentException.
     */
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

    /**
     * 이 증명이 leafHash 를 expectedRoot 아래에 놓는가.
     *
     * 검증하는 쪽은 트리를 갖고 있지 않다. 잎 해시 하나, 증명, 뿌리 해시가 전부다.
     * leafHash 나 expectedRoot 가 null 이면 IllegalArgumentException.
     */
    public boolean verify(byte[] leafHash, byte[] expectedRoot) {
        if (leafHash == null || expectedRoot == null) {
            throw new IllegalArgumentException("잎 해시와 뿌리 해시가 필요하다");
        }
        // TODO 7: 걸음을 순서대로 합쳐 올라가고 마지막에 뿌리와 견준다.
        //
        //   cur 을 leafHash 로 시작해서 걸음마다
        //     siblingIsLeft 면  cur = hashing.nodeHash(형제, cur)
        //     아니면            cur = hashing.nodeHash(cur, 형제)
        //   전부 합친 뒤 cur 과 expectedRoot 를 비교한다
        //
        // **비교는 Arrays.equals 다.** cur.equals(expectedRoot) 는 컴파일되고 예외도 안 나고
        // 언제나 false 를 준다. 그러면 모든 증명이 거부되고, 테스트는 "잘못된 증명을 거부한다"
        // 쪽만 통과한다. 통과하는 테스트가 늘어서 더 헷갈린다.
        //
        // 반대 방향의 실수도 있다. Arrays.equals 대신 길이만 비교하거나
        // 앞 몇 바이트만 보면 어떤 위조든 통과한다. 검증은 전부 보거나 안 보거나 둘 중 하나다.
        //
        // 걸음이 0개인 증명도 정상이다. 잎이 하나뿐인 트리에서는 잎 해시가 곧 뿌리라
        // 아무것도 합치지 않고 바로 비교한다.
        throw new UnsupportedOperationException("TODO 7: verify");
    }

    /** 걸음 목록(수정 불가). */
    public List<Step> steps() {
        return steps;
    }

    /** 걸음 수. 이것이 증명 크기이고 log n 이어야 한다. */
    public int size() {
        return steps.size();
    }

    /** 이 증명이 전제하는 해시 규칙. */
    public MerkleHashing hashing() {
        return hashing;
    }
}
