package com.datastructure.conshash;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 키를 노드(서버) 하나에 배정하는 규칙. TODO 가 없다. 계약은 주어지는 것이다.
 *
 * <h2>05번이 못 하는 것</h2>
 *
 * 05번 해시맵은 hash(key) % 버킷수 로 자리를 정했다. 버킷 수가 바뀌면 리사이즈에서
 * 전부 다시 계산했고, 그건 그 맵 안에서 끝나는 일이라 값이 쌌다.
 *
 * 같은 식을 서버 N 대에 쓰면 사정이 다르다. 서버 한 대가 늘거나 죽으면 N 이 바뀌고,
 * % 4 가 % 5 가 되는 순간 거의 모든 키의 담당 서버가 바뀐다.
 * 캐시면 전부 미스가 되고, 저장소면 데이터를 실제로 옮겨야 한다.
 * "다시 계산하면 된다"가 여기서는 "네트워크로 다 옮겨라"가 된다.
 *
 * 이 박스가 재는 것은 속도가 아니라 이동량이다.
 *
 * <h2>네 구현</h2>
 *
 * <pre>
 *   ModuloSharding               기준선. hash(key) % N. 여기서 이동량이 터진다
 *   ConsistentHashRing           원 위에 올린다. 죽은 노드의 구간만 넘어간다
 *   WeightedConsistentHashRing   용량이 다른 서버에 자리를 더 준다
 *   JumpConsistentHash           원도 가상 노드도 없다. 대신 맨 뒤에서만 늘고 준다
 * </pre>
 *
 * <h2>공통 계약</h2>
 *
 * <pre>
 *   addNode(null), getNode(null)   IllegalArgumentException
 *   이미 있는 노드를 addNode        IllegalArgumentException
 *   없는 노드를 removeNode          IllegalArgumentException
 *   노드가 하나도 없을 때 getNode   null
 *   그 외에는 getNode 가 절대 null 을 주지 않는다
 * </pre>
 *
 * 마지막 줄이 이 박스에서 제일 자주 깨지는 계약이다. 원의 끝을 넘어간 키를 되돌리지 않으면
 * 거기서 null 이 나온다.
 */
public interface HashRing {

    /** 노드를 넣는다. 이미 있으면 IllegalArgumentException. */
    void addNode(String node);

    /** 노드를 뺀다. 없으면 IllegalArgumentException. */
    void removeNode(String node);

    /** 이 키를 맡는 노드. 노드가 하나도 없으면 null, 그 외에는 절대 null 이 아니다. */
    String getNode(String key);

    int nodeCount();

    /** 지금 살아 있는 노드를 넣은 순서로. 변경 불가. */
    List<String> nodes();

    /**
     * 이 방식이 배정을 위해 들고 있는 자리의 수. 메모리 비용이다.
     *
     * 모듈로는 노드 수와 같고, 원은 노드 수 곱하기 가상 노드 수,
     * 점프 해시는 0 이다. 점프 해시가 파는 것이 이 열이다.
     */
    int slotCount();

    /**
     * 이 키들을 나눠 가지면 노드마다 몇 개인가. 균형 측정이 이걸 쓴다.
     *
     * 키를 하나도 못 받은 노드도 0 으로 들어 있다. 그 0 이 이 박스의 측정 하나다.
     */
    default Map<String, Integer> keyCounts(Iterable<String> keys) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (String node : nodes()) {
            counts.put(node, 0);
        }
        for (String key : keys) {
            String node = getNode(key);
            if (node == null) {
                throw new IllegalStateException("노드가 있는데 getNode 가 null 을 줬다: " + key);
            }
            if (!counts.containsKey(node)) {
                throw new IllegalStateException("살아 있지 않은 노드로 보냈다: " + key + " -> " + node);
            }
            counts.merge(node, 1, Integer::sum);
        }
        return counts;
    }
}
