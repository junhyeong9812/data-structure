package com.datastructure.merkle;

/**
 * 잎과 내부 노드를 각각 어떻게 해시할지 정하는 규칙.
 *
 * <h2>왜 규칙을 따로 두는가</h2>
 *
 * 해시 함수만 정하면 될 것 같지만 아니다. 트리에는 서로 다른 두 종류의 입력이 있다.
 *
 * <pre>
 *   잎     블록 하나를 요약한다
 *   내부   자식 해시 둘을 이어 요약한다
 * </pre>
 *
 * 이 둘을 같은 방식으로 해시하면 내부 노드의 해시와 잎의 해시가 같은 값의 공간에 산다.
 * 그러면 공격자가 내부 노드의 입력(자식 해시 둘을 이어붙인 64바이트)을
 * 블록인 척 제출할 수 있다. 검증하는 쪽은 그 블록의 잎 해시를 계산하는데
 * 그 값이 원래 내부 노드의 해시와 정확히 같으므로 뿌리가 맞아떨어진다.
 * 두 번째 원상 공격(second preimage attack) 이다.
 *
 * 막는 방법은 접두사 한 바이트다. 잎에는 0x00, 내부에는 0x01 을 붙인다.
 * 그러면 두 집합이 겹치지 않아 어떤 내부 노드도 잎으로 위장할 수 없다.
 * 인증서 투명성 로그(RFC 6962) 가 정확히 이 규칙을 쓴다.
 *
 * PrefixedHashing 이 그 규칙이고, UnprefixedHashing 은 공격이 성립하는 것을 보이려고 둔다.
 * 이 인터페이스에는 TODO 가 없다.
 */
public interface MerkleHashing {

    /** 잎에 붙이는 접두사. */
    byte LEAF_PREFIX = 0x00;

    /** 내부 노드에 붙이는 접두사. */
    byte NODE_PREFIX = 0x01;

    /**
     * 블록 하나의 잎 해시. block 이 null 이면 IllegalArgumentException.
     */
    byte[] leafHash(byte[] block);

    /**
     * 자식 해시 둘을 합친 내부 노드 해시.
     *
     * 좌우 순서가 값에 들어간다. 바꿔 넣으면 다른 해시가 나온다.
     * 그래서 증명에 형제 해시만 담으면 안 되고 좌우 방향도 같이 담아야 한다.
     *
     * left 나 right 가 null 이면 IllegalArgumentException.
     */
    byte[] nodeHash(byte[] left, byte[] right);

    /** 밑에 깔린 해시 함수. */
    HashFunction function();
}
