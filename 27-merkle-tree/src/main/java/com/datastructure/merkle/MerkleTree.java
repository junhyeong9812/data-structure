package com.datastructure.merkle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 머클 트리. 블록 목록을 해시 하나로 요약하고, 다를 때 어디가 다른지까지 답한다.
 *
 * <h2>지금까지와 다른 질문</h2>
 *
 * 01번부터 26번까지의 자료구조는 전부 "무엇이 들어 있나"에 답했다.
 * 여기서 묻는 것은 다른 질문이다. "이게 그대로인가."
 *
 * 블록 100만 개짜리 파일이 상대에게도 있다. 같은지 확인하려면 전부 보내 비교하면 된다.
 * 그건 파일 전체를 한 번 더 옮기는 것이다. 해시 하나로 요약하면 같은지는 알 수 있는데
 * 다를 때 어디가 다른지를 모른다.
 *
 * 머클 트리는 잎에 블록 해시를 두고 위로 올라가며 둘씩 합쳐 해시한다.
 * 뿌리 하나가 전체를 요약하고, 다르면 뿌리부터 내려가며 O(log n) 만에 어느 블록인지 찾는다.
 * 같은 부분트리는 해시 한 번 비교로 통째로 건너뛴다.
 *
 * <h2>층 배열로 담는다</h2>
 *
 * <pre>
 *   levels[0]   잎 해시 n 개
 *   levels[1]   둘씩 합친 것 ceil(n/2) 개
 *   ...
 *   levels[h]   뿌리 하나
 * </pre>
 *
 * levels[k][j] 의 자식은 levels[k-1][2j] 와 levels[k-1][2j+1] 이다.
 * 자리를 인덱스 산술로 알 수 있으니 노드 객체도 포인터도 필요 없다.
 * 07번 힙이 트리를 배열에 담던 것과 같은 발상이고, 여기서는 층마다 배열을 따로 둘 뿐이다.
 *
 * <h2>잎이 2의 거듭제곱이 아닐 때</h2>
 *
 * 이 문제집은 짝이 없는 마지막 노드를 그대로 위로 올린다(promote).
 * 흔한 다른 방법은 마지막 노드를 자기 자신과 짝지어 해시하는 것인데(비트코인 방식),
 * 그쪽에는 알려진 취약점이 있다. CVE-2012-2459 다.
 * 블록 목록 [a, b, c] 와 [a, b, c, c] 가 정확히 같은 뿌리를 낸다. 서로 다른 파일인데 구별이 안 된다.
 * OddLeafCountTest 가 두 규칙의 값을 나란히 계산해 그것을 보여준다.
 *
 * 승격에도 값은 있다. 승격된 노드는 부모와 해시가 같아서 그 자리의 증명이 한 걸음 짧다.
 * 증명 길이가 잎마다 다르다는 뜻이고, 그 자체가 어느 잎인지에 대한 정보를 조금 흘린다.
 *
 * <h2>대가</h2>
 *
 * 잎 하나가 바뀌면 뿌리가 바뀐다. 그래서 "블록 하나만 고친 파일"은 다른 파일이다.
 * 갱신은 경로만 다시 계산하면 되지만(withLeafReplaced), 블록을 끼워 넣거나 빼면
 * 그 뒤 모든 잎의 자리가 밀려서 트리를 다시 지어야 한다.
 * 이 자료구조는 "자리가 고정된 목록"에만 쓸 수 있다.
 */
public class MerkleTree {

    private final MerkleHashing hashing;

    /** levels[k][j] = k 층 j 번 노드의 해시. levels[0] 이 잎, 마지막 층이 뿌리 하나. */
    private final byte[][][] levels;

    private long comparisons;

    /** 접두사 규칙(PrefixedHashing) 으로 짓는다. 보통 이쪽을 쓴다. */
    public MerkleTree(List<byte[]> blocks, HashFunction function) {
        this(blocks, new PrefixedHashing(function));
    }

    /**
     * 해시 규칙을 직접 주고 짓는다. 접두사 없는 규칙으로 공격을 재현할 때 쓴다.
     *
     * blocks 가 null 이거나 비어 있으면 IllegalArgumentException.
     * 잎이 0개인 머클 트리는 뿌리가 없다. 빈 파일을 다루려면 "빈 목록의 해시"를
     * 프로토콜이 따로 정해야 하는데, 그 결정을 자료구조가 몰래 하지 않는다.
     */
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

    /** 이미 계산된 층 배열로 트리를 만든다. withLeafReplaced 가 쓴다. */
    private MerkleTree(byte[][][] levels, MerkleHashing hashing) {
        this.levels = levels;
        this.hashing = hashing;
    }

    private static byte[][][] build(List<byte[]> blocks, MerkleHashing hashing) {
        // TODO 3: 잎 층을 만들고 하나가 남을 때까지 위로 접는다.
        //
        //   1. 블록마다 hashing.leafHash 를 불러 levels[0] 을 만든다
        //      (블록이 null 이면 IllegalArgumentException. 몇 번째인지 메시지에 넣어라)
        //   2. 층 길이가 1보다 크면 다음 층을 만든다
        //        다음 층 길이 = (길이 + 1) / 2      <- 올림 나눗셈이다. +1 을 빼면 마지막 노드를 잃는다
        //        i 번 노드의 자식은 2i 와 2i+1
        //        2i+1 이 없으면 승격이다. **다시 해시하지 말고 자식 참조를 그대로 올린다**
        //   3. 만든 층들을 순서대로 List 에 모아 byte[0][][] 로 변환해 반환한다
        //
        // 2번의 승격에서 hashing.nodeHash(child, child) 를 부르고 싶어지는데
        // 그게 비트코인 방식이고 CVE-2012-2459 다. [a,b,c] 와 [a,b,c,c] 의 뿌리가 같아진다.
        //
        // 참조를 그대로 올리는 것에는 값이 하나 더 있다. 승격된 노드는 자식과 **같은 객체**라
        // withLeafReplaced 가 무엇을 새로 만들었는지 참조 비교로 셀 수 있다.
        //
        // 잎이 1개면 층이 하나뿐이고 그 잎 해시가 곧 뿌리다. 반복문이 한 번도 안 돌면 된다.
        throw new UnsupportedOperationException("TODO 3: build");
    }

    /**
     * 뿌리 해시. 이 하나가 전체 블록 목록을 요약한다.
     *
     * 복사본을 준다. 이걸 고친다고 트리가 바뀌면 안 된다.
     */
    public byte[] rootHash() {
        return levels[levels.length - 1][0].clone();
    }

    /** 그 잎의 해시(복사본). 증명을 검증할 때 검증하는 쪽이 블록에서 직접 계산하는 값이다. */
    public byte[] leafHash(int leafIndex) {
        return hashAt(0, requireLeafIndex(leafIndex)).clone();
    }

    /**
     * 그 잎이 이 트리에 들어 있다는 포함 증명.
     *
     * 파일 전체가 아니라 경로의 형제 해시 log n 개만 담는다.
     * 잎 100만 개면 20개, SHA-256 이니 640바이트다. 검증하는 쪽은 파일을 안 봐도 된다.
     */
    public MerkleProof proofFor(int leafIndex) {
        int index = requireLeafIndex(leafIndex);
        // TODO 4: 잎에서 뿌리까지 올라가며 **형제**를 주워 담는다.
        //
        //   층마다
        //     내 인덱스가 짝수면 형제는 index + 1 이고 형제는 내 **오른쪽**에 있다
        //     홀수면        형제는 index - 1 이고 형제는 내 **왼쪽**에 있다
        //     형제 자리가 그 층의 길이를 넘으면 승격이라 형제가 없다. 걸음을 안 담고 넘어간다
        //   그리고 index /= 2 로 부모 자리로 올라간다. 마지막 층(뿌리)에서는 담을 것이 없다
        //
        // **방향을 같이 담는 것이 핵심이다.** nodeHash 는 좌우 순서가 값에 들어가므로
        // 형제 해시만 있으면 검증하는 쪽이 어느 쪽에 붙일지 모른다.
        // 그리고 방향을 "내가 왼쪽인가"로 담을지 "형제가 왼쪽인가"로 담을지 정하고 일관되게 써라.
        // MerkleProof.Step 은 siblingIsLeft, 즉 **형제 기준**이다. 뒤집어 담으면 verify 가
        // 좌우를 바꿔 합치고, 그러면 뿌리가 안 맞는다.
        //
        // 승격 때문에 증명 길이가 잎마다 다를 수 있다. 그것이 정상이다.
        // 잎 3개짜리 트리에서 2번 잎의 증명은 한 걸음뿐이다(높이는 2인데).
        throw new UnsupportedOperationException("TODO 4: proofFor");
    }

    /**
     * 두 트리를 비교해 처음 다른 잎의 인덱스. 같으면 -1.
     *
     * 잎을 하나씩 비교하면 O(n) 이다. 여기서는 O(log n) 이어야 한다.
     * 비교한 노드 수는 comparisons() 로 잰다.
     *
     * 잎 개수가 다르면 IllegalArgumentException. 자리가 밀려서 인덱스를 맞출 수 없다.
     */
    public int findFirstDifference(MerkleTree other) {
        requireComparable(other);
        // TODO 5: 뿌리부터 내려간다.
        //
        //   1. 뿌리가 같으면 -1. **여기서 끝난다.** 100만 블록이 같은지를 비교 한 번으로 답한다
        //   2. 다르면 한 층씩 내려가며 **다른 쪽 자식**을 고른다
        //        왼쪽 자식이 다르면 왼쪽으로 (처음 다른 잎이 목표이므로 왼쪽이 먼저다)
        //        왼쪽이 같으면 오른쪽으로 (부모가 다른데 왼쪽이 같으면 다른 쪽은 오른쪽뿐이다)
        //   3. 0층에 닿으면 그 인덱스가 답이다
        //
        // 2번에서 오른쪽 자식은 **비교하지 않고** 내려간다. 부모가 다르다는 사실이
        // 이미 증거이기 때문이다. 그래서 층마다 비교가 1번이고 전체가 1 + height 번이다.
        // 양쪽을 다 비교해도 답은 같고 비교 수만 두 배가 된다.
        //
        // 오른쪽 자식이 없는 승격 노드도 있다. 그때는 왼쪽으로 간다.
        // 승격 노드는 부모와 해시가 같으니 부모가 다르면 그 자식도 다르다.
        // (오른쪽이 없는데 왼쪽이 같은 경우는 해시 충돌 없이는 못 일어난다. 그래서 이 방어선은
        //  ToyHash 로만 닿을 수 있고 우리 테스트는 못 잡는다. 지우지 않고 둔다)
        throw new UnsupportedOperationException("TODO 5: findFirstDifference");
    }

    /**
     * 잎 하나를 바꾼 새 트리. 원본은 그대로 남는다.
     *
     * 트리를 통째로 다시 짓지 않는다. 바뀐 잎에서 뿌리까지 경로 위의 노드만 새로 계산하고
     * 나머지는 원본의 배열을 그대로 가리킨다. 26번 영속 자료구조의 경로 복사와 같은 발상이다.
     * 잎 1024개면 해시 계산이 2047번에서 11번이 된다.
     */
    public MerkleTree withLeafReplaced(int leafIndex, byte[] newBlock) {
        int index = requireLeafIndex(leafIndex);
        if (newBlock == null) {
            throw new IllegalArgumentException("블록은 null 일 수 없다");
        }
        // TODO 6: 층 배열을 얕게 복사하고 경로만 다시 계산한다.
        //
        //   1. 층마다 levels[k].clone() 으로 **참조 배열만** 복사한다
        //      (byte[] 안의 값은 복사하지 않는다. 안 바뀐 해시는 원본과 같은 객체를 가리킨다)
        //   2. 0층의 그 자리에 새 잎 해시를 넣는다
        //   3. 위로 올라가며 부모를 다시 계산한다
        //        parent = index / 2
        //        부모의 자식은 2*parent 와 2*parent+1 이다. **내 형제는 안 바뀌었으니 그대로 읽는다**
        //        오른쪽 자식이 없으면 승격이므로 왼쪽 자식 참조를 그대로 올린다
        //        index = parent 로 올라간다
        //   4. private 생성자로 새 트리를 만들어 반환한다
        //
        // 3번에서 **복사본에서 자식을 읽어야 한다.** 원본에서 읽으면 아래층에서 방금 고친 값 대신
        // 옛 값을 합치게 되고, 그러면 뿌리가 옛날 값 그대로 나온다.
        // 새 뿌리가 옛 뿌리와 같은 것은 이 자료구조에서 "아무 일도 안 일어났다"는 뜻이라
        // 예외 없이 조용히 틀린다. PathCopyTest 가 그 자리를 잡는다.
        //
        // 얕은 복사가 안전한 이유도 보라. 이 트리는 해시 배열을 절대 제자리에서 고치지 않는다.
        // 공유해도 되는 것은 아무도 안 고치기 때문이지, 복사가 비싸서가 아니다.
        throw new UnsupportedOperationException("TODO 6: withLeafReplaced");
    }

    /**
     * 같은 자리의 노드가 같은가. 비교 횟수를 하나 올린다.
     *
     * byte[] 를 equals 로 비교하면 참조를 비교한다. 컴파일도 되고 예외도 안 나고
     * 언제나 false 를 준다. 그러면 "전부 다르다"가 되어 트리를 끝까지 내려간다.
     * 답은 맞을 수 있는데 O(log n) 이 사라진다. Arrays.equals 를 써야 한다.
     */
    public boolean sameNode(MerkleTree other, int level, int index) {
        comparisons++;
        return Arrays.equals(hashAt(level, index), other.hashAt(level, index));
    }

    /**
     * k 층 j 번 노드의 해시. 내부 배열을 그대로 준다. 고치지 마라.
     *
     * 복사본을 주면 트리를 내려갈 때마다 32바이트를 새로 할당한다.
     * 이 메서드는 구조를 들여다보는 자리라 복사를 안 한다. 대신 규칙을 문서로 못 박는다.
     */
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

    /** 그 층의 노드 개수. */
    public int levelSize(int level) {
        if (level < 0 || level >= levels.length) {
            throw new IndexOutOfBoundsException("층이 범위를 벗어났다: " + level + " (높이 " + height() + ")");
        }
        return levels[level].length;
    }

    /** 잎의 개수, 즉 블록 수. */
    public int leafCount() {
        return levels[0].length;
    }

    /** 잎에서 뿌리까지의 층 수. 잎이 1개면 0, 2개면 1, 1000개면 10 이다. */
    public int height() {
        return levels.length - 1;
    }

    /** 이 트리가 쓰는 해시 규칙. 증명을 검증하는 쪽도 같은 규칙을 써야 한다. */
    public MerkleHashing hashing() {
        return hashing;
    }

    /** 태어난 뒤 지금까지 비교한 노드 수. 걸음 수를 재는 자다. */
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
