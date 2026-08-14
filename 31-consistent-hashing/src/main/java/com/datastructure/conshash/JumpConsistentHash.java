package com.datastructure.conshash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 구글의 점프 일관 해시. 원도 가상 노드도 없다.
 *
 * <h2>무엇을 하는가</h2>
 *
 * 키 하나와 버킷 수 N 을 받아 0 이상 N 미만의 번호를 준다. 자료구조가 없다.
 * 정수 연산 몇 줄을 O(log N) 번 돌면 끝이고, 메모리는 0 이다. slotCount 가 0 인 이유다.
 *
 * <h2>왜 맞는가</h2>
 *
 * 버킷이 N 개에서 N+1 개로 늘 때, 각 키는 확률 1/(N+1) 로 새 버킷으로 옮겨가야 한다.
 * 이 알고리즘은 "다음에 자리가 바뀌는 시점"을 난수로 건너뛰며 찾는다.
 * key 를 선형 합동 생성기로 굴려 매번 같은 난수열을 얻으므로 저장할 것이 없다.
 * 옮겨가는 키는 언제나 새 버킷으로만 가고, 기존 버킷끼리는 절대 주고받지 않는다.
 *
 * <h2>무엇을 포기했는가</h2>
 *
 * 버킷 번호가 0 부터 N-1 까지 빈틈없이 이어져야 한다.
 * 그래서 노드를 맨 뒤에서만 더하고 뺄 수 있다. 가운데 노드가 죽는 것을 표현할 방법이 없다.
 *
 * 목록을 밀어 채우면 되지 않느냐고 하면, 그 순간 뒤쪽 노드의 번호가 전부 하나씩 당겨진다.
 * 실측으로 그때 이동량이 28.7% 였다. 1/N 인 10% 가 아니다.
 * 아무 노드나 죽을 수 있는 곳에서는 원 방식이 여전히 필요하다.
 *
 * 대신 균형은 원보다 낫다. 가상 노드 5000 개짜리 원이 1.106 일 때 이쪽은 1.026 이었고,
 * 그러면서 자리를 하나도 안 들고 있다.
 */
public class JumpConsistentHash implements HashRing {

    private final List<String> nodes = new ArrayList<>();

    /**
     * 키를 0 이상 numBuckets 미만의 버킷 번호로. numBuckets 가 0 이면 -1.
     *
     * 논문(Lamping, Veach 2014)의 의사코드 그대로다.
     */
    public static int jumpHash(long key, int numBuckets) {
        // TODO 6: 논문의 의사코드를 옮긴다. 지역 변수 둘과 반복문 하나다.
        //
        //   b = -1, j = 0 으로 시작해서 j 가 numBuckets 보다 작은 동안
        //     b 에 j 를 옮겨 담고
        //     key 를 선형 합동 생성기로 한 번 굴린다.  key = key * 2862933555777941757L + 1
        //     j = (b + 1) * (2^31 / ((key 를 33비트 논리 시프트한 값) + 1))
        //   반복이 끝나면 b 가 답이다.
        //
        // 세 군데가 함정이다.
        //
        //   1. 시프트는 >>> 다. >> 를 쓰면 key 가 음수일 때 결과가 음수가 되고
        //      나누는 수가 0 이하가 되어 j 가 뒤로 간다. 무한 루프이거나 범위 밖이다.
        //      11번, 18번에서 나온 그 시프트 이야기다.
        //   2. 나눗셈이 double 이어야 한다. 정수 나눗셈이면 2^31 을 큰 수로 나눌 때 0 이 되고,
        //      j 가 커지지 않아 역시 무한 루프다. (기본 시간 제한 30초가 걸려 있다)
        //   3. 마지막에 long 을 int 로 좁힌다. b 는 numBuckets 미만이라 안전하다.
        //
        // 왜 이게 맞는가. 버킷이 N 개에서 N+1 개로 늘 때 각 키는 1/(N+1) 확률로만 옮겨가야 한다.
        // 이 반복문은 "자리가 바뀌는 다음 시점"을 난수로 건너뛴다. key 를 굴려 얻는 난수열이
        // 언제나 같으므로 아무것도 저장하지 않아도 같은 답이 나온다. 그래서 메모리가 0 이다.
        //
        // numBuckets 가 0 이면 반복문이 한 번도 안 돌아 -1 이 나온다. 그 값이 계약이다.
        throw new UnsupportedOperationException("TODO 6: jumpHash");
    }

    @Override
    public void addNode(String node) {
        if (node == null) {
            throw new IllegalArgumentException("노드 이름이 null 이다");
        }
        if (nodes.contains(node)) {
            throw new IllegalArgumentException("이미 있는 노드다: " + node);
        }
        nodes.add(node);
    }

    /**
     * 맨 뒤 노드만 뺄 수 있다. 가운데 노드면 UnsupportedOperationException.
     *
     * 이 예외가 이 알고리즘의 한계를 그대로 드러낸다. 감추지 않는다.
     */
    @Override
    public void removeNode(String node) {
        if (!nodes.contains(node)) {
            throw new IllegalArgumentException("없는 노드다: " + node);
        }
        // TODO 7: 맨 뒤 노드면 빼고, 아니면 UnsupportedOperationException 을 던진다.
        //
        // 예외 메시지에 "뺄 수 있는 것은 무엇인지"를 넣어라. 계약 테스트가 그걸 확인한다.
        // 거부할 때는 목록이 하나도 안 바뀌어야 한다.
        //
        // 목록을 밀어서 채우고 싶어질 것이다. 그러면 컴파일도 되고 테스트도 대부분 통과한다.
        // 대신 뒤쪽 노드의 번호가 전부 하나씩 당겨진다.
        // MovementTest 가 그때 이동량을 재두었다. 1/N 인 10,000개가 아니라 28,682개다.
        // 못 하는 것을 되는 척하는 것이 여기서는 조용한 손실이라 예외로 드러낸다.
        throw new UnsupportedOperationException("TODO 7: removeNode");
    }

    @Override
    public String getNode(String key) {
        if (key == null) {
            throw new IllegalArgumentException("키가 null 이다");
        }
        // TODO 8: 키를 long 으로 만들어 jumpHash 에 넘기고, 나온 번호의 노드를 준다.
        //
        // 키를 long 으로 만드는 것은 Hashing.fnv64 가 한다.
        // 섞을 필요는 없다. jumpHash 가 안에서 굴리면서 섞기 때문이다.
        //
        // 노드가 하나도 없을 때를 먼저 막아라. 안 막으면 jumpHash 가 -1 을 주고
        // nodes.get(-1) 에서 IndexOutOfBoundsException 이 난다.
        // 계약은 "노드가 없으면 null" 이다.
        throw new UnsupportedOperationException("TODO 8: getNode");
    }

    @Override
    public int nodeCount() {
        return nodes.size();
    }

    @Override
    public List<String> nodes() {
        return Collections.unmodifiableList(new ArrayList<>(nodes));
    }

    /** 0 이다. 이 알고리즘이 파는 것이 이 열이다. */
    @Override
    public int slotCount() {
        return 0;
    }
}
