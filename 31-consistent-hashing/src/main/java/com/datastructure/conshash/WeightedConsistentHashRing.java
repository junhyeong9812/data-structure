package com.datastructure.conshash;

/**
 * 서버 용량이 다를 때. 용량이 두 배인 서버에 자리를 두 배 준다.
 *
 * 원 위에서 노드가 맡는 몫은 자기 앞의 빈 구간 길이의 합이다.
 * 자리를 두 배 찍으면 그 합의 기댓값도 두 배가 된다. 그래서 자리 수가 곧 가중치다.
 *
 * 이게 가능한 이유는 배정 규칙에 노드 수 N 이 안 들어 있기 때문이다.
 * 모듈로 방식에서 같은 것을 하려면 노드 목록에 같은 이름을 두 번 넣어야 하는데,
 * 그러면 N 이 커져서 이동량 문제가 더 나빠진다.
 *
 * 무게가 정확히 비율대로 나오지는 않는다. 자리를 무작위로 찍는 이상 오차가 남는다.
 * BalanceTest 가 그 오차까지 숫자로 적어둔다.
 */
public class WeightedConsistentHashRing extends ConsistentHashRing {

    public WeightedConsistentHashRing() {
        super();
    }

    public WeightedConsistentHashRing(int virtualNodes) {
        super(virtualNodes);
    }

    public WeightedConsistentHashRing(int virtualNodes, RingHash hash) {
        super(virtualNodes, hash);
    }

    /**
     * 가중치를 주고 노드를 넣는다. weight 가 1 이면 addNode(node) 와 같다.
     *
     * weight 가 1 미만이면 IllegalArgumentException.
     */
    public void addNode(String node, int weight) {
        if (weight < 1) {
            throw new IllegalArgumentException("가중치는 1 이상이어야 한다: " + weight);
        }
        // TODO 5: 가중치만큼 자리를 더 주고 부모의 addSlots 로 넘긴다.
        //
        // 한 줄이다. 그런데 이 한 줄이 이 방식의 성질을 그대로 보여준다.
        // 원 위에서 노드의 몫은 자기 앞 구간 길이의 합이고, 자리를 두 배 찍으면 그 합도 두 배가 된다.
        // 배정 규칙에 노드 수 N 이 안 들어 있어서 가능한 일이다.
        // 모듈로에서 같은 것을 하려면 목록에 이름을 두 번 넣어야 하고, 그러면 N 이 커진다.
        //
        // 두 배가 정확히 두 배는 아니다. 자리를 무작위로 찍으니 오차가 남는다.
        // BalanceTest 가 같은 가중치인 두 노드 사이에서 6% 차이가 난다는 것까지 적어둔다.
        throw new UnsupportedOperationException("TODO 5: addNode(node, weight)");
    }
}
