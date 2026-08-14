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
        long b = -1;
        long j = 0;
        while (j < numBuckets) {
            b = j;
            key = key * 2862933555777941757L + 1;
            j = (long) ((b + 1) * ((double) (1L << 31) / (double) ((key >>> 33) + 1)));
        }
        return (int) b;
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
        if (!nodes.get(nodes.size() - 1).equals(node)) {
            throw new UnsupportedOperationException(
                    "점프 해시는 맨 뒤 노드만 뺄 수 있다. 뺄 수 있는 것은 "
                            + nodes.get(nodes.size() - 1) + " 인데 " + node + " 를 요청했다");
        }
        nodes.remove(nodes.size() - 1);
    }

    @Override
    public String getNode(String key) {
        if (key == null) {
            throw new IllegalArgumentException("키가 null 이다");
        }
        if (nodes.isEmpty()) {
            return null;
        }
        return nodes.get(jumpHash(Hashing.fnv64(key), nodes.size()));
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
