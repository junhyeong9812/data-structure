package com.datastructure.conshash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 노드와 키를 같은 원 위에 올리고, 키는 시계 방향으로 만나는 첫 노드가 맡는다.
 *
 * <h2>왜 이동량이 줄어드는가</h2>
 *
 * 노드가 죽으면 그 노드가 서 있던 자리가 원에서 사라진다. 그 자리로 오던 키는 다음 자리로 밀린다.
 * 사라지지 않은 자리들 사이의 구간은 아무것도 바뀌지 않는다.
 * 그래서 옮겨가는 키가 죽은 노드가 맡던 것뿐이고, 그게 1/N 이다.
 *
 * 모듈로는 N 이 식 안에 들어 있어서 N 이 바뀌면 모든 키의 계산이 바뀐다.
 * 여기서는 N 이 식에 없다. 자리들의 배치만 있다. 그 차이가 전부다.
 *
 * <h2>가상 노드</h2>
 *
 * 노드 하나를 자리 하나에 올리면 구간 길이가 들쭉날쭉하다. 자리 열 개를 무작위로 찍었을 때
 * 그 사이가 고르게 나뉠 이유가 없기 때문이다. 실측으로 최대 구간이 최소 구간의 18배였다.
 *
 * 그래서 노드 하나를 name#0, name#1, ... 여러 이름으로 원 위 여러 곳에 올린다.
 * 자리가 많아질수록 큰 조각과 작은 조각이 서로 상쇄되어 합이 고르게 간다.
 * 대가는 메모리다. 자리 수가 노드 수 곱하기 가상 노드 수만큼 늘고, 그게 slotCount 다.
 *
 * <h2>알려진 구멍</h2>
 *
 * 두 가상 이름이 같은 자리에 떨어지면 TreeMap 이 조용히 덮어쓴다.
 * 2^32 개 자리에 수천 개를 놓는 동안에는 거의 일어나지 않고, 이 문제집의 측정에서도 한 번도 안 났다.
 * 실무 구현은 충돌하면 다음 빈 자리로 밀어 넣는다. 여기서는 그 처리를 넣지 않았고,
 * 대신 removeNode 가 자기 것인지 확인하고 지운다.
 * 즉 충돌이 나면 자리를 하나 잃을 뿐 남의 자리를 지우지는 않는다.
 */
public class ConsistentHashRing implements HashRing {

    /** 노드 하나를 원 위 몇 곳에 올릴지의 기본값. */
    public static final int DEFAULT_VIRTUAL_NODES = 100;

    /** 자리 -> 그 자리에 선 노드. 정렬되어 있어야 시계 방향 다음을 찾을 수 있다. */
    final NavigableMap<Long, String> ring = new TreeMap<>();

    /** 노드 -> 실제로 올린 자리 수. 가중치가 붙으면 노드마다 다르다. */
    private final Map<String, Integer> placed = new LinkedHashMap<>();

    private final int virtualNodes;
    private final RingHash hash;

    public ConsistentHashRing() {
        this(DEFAULT_VIRTUAL_NODES, Hashing.MIXED);
    }

    public ConsistentHashRing(int virtualNodes) {
        this(virtualNodes, Hashing.MIXED);
    }

    public ConsistentHashRing(int virtualNodes, RingHash hash) {
        if (virtualNodes < 1) {
            throw new IllegalArgumentException("가상 노드는 1개 이상이어야 한다: " + virtualNodes);
        }
        if (hash == null) {
            throw new IllegalArgumentException("해시가 필요하다");
        }
        this.virtualNodes = virtualNodes;
        this.hash = hash;
    }

    /** 가상 이름. 이 규칙이 바뀌면 원 위의 배치가 통째로 바뀐다. */
    static String virtualName(String node, int index) {
        return node + "#" + index;
    }

    protected int virtualNodes() {
        return virtualNodes;
    }

    @Override
    public void addNode(String node) {
        addSlots(node, virtualNodes);
    }

    /**
     * 노드를 원 위 count 곳에 올린다. 가중치 있는 링이 count 를 다르게 준다.
     */
    protected void addSlots(String node, int count) {
        if (node == null) {
            throw new IllegalArgumentException("노드 이름이 null 이다");
        }
        if (placed.containsKey(node)) {
            throw new IllegalArgumentException("이미 있는 노드다: " + node);
        }
        if (count < 1) {
            throw new IllegalArgumentException("자리는 1개 이상이어야 한다: " + count);
        }
        // TODO 2: 이 노드를 원 위 count 곳에 올린다.
        //
        //   자리 = hash.position(virtualName(node, i))     i 는 0 부터 count-1 까지
        //   ring 에 자리 -> node 로 넣고, 마지막에 placed 에 count 를 기록한다
        //
        // placed 에 넣는 것이 count 여야 한다. virtualNodes 를 넣으면 지금은 같은 값이라
        // 테스트가 전부 통과하고, 가중치 링을 만드는 순간 조용히 틀린다.
        // 자리를 300개 찍고 100개만 기억하는 노드가 되고, 그 노드를 빼면 200개가 원에 남는다.
        // 죽은 서버 이름이 원에 남아 있으면 키가 그리로 간다.
        //
        // i 를 0 부터 세는 것도 계약이다. removeNode 가 같은 이름을 다시 만들어 지운다.
        throw new UnsupportedOperationException("TODO 2: addSlots");
    }

    @Override
    public void removeNode(String node) {
        Integer count = placed.remove(node);
        if (count == null) {
            throw new IllegalArgumentException("없는 노드다: " + node);
        }
        // TODO 3: 이 노드가 올라가 있던 자리를 원에서 지운다.
        //
        // 자리는 저장돼 있지 않다. 넣을 때와 같은 이름으로 다시 계산해서 찾는다.
        // 그래서 removeNode 는 addSlots 와 같은 규칙을 써야 하고,
        // count 도 addSlots 가 실제로 찍은 수여야 한다. 위에서 placed 에 넣은 그 값이다.
        //
        // 지우기 전에 그 자리가 정말 이 노드 것인지 확인하라(ring.get(position) 비교).
        // 두 가상 이름이 같은 자리에 떨어지면 나중 것이 앞의 것을 덮어쓴 상태인데,
        // 확인 없이 지우면 남의 자리를 지운다. 그러면 그 노드는 원에서 조금씩 사라진다.
        //
        // 실제 해시로는 충돌이 안 나서 키 100,000개를 던져도 이 줄의 값어치가 안 보인다.
        // 그래서 구조 테스트가 일부러 겹치는 해시를 주입해 그 상황을 만든다.
        // 확률이 낮은 것과 안 일어나는 것은 다르다.
        throw new UnsupportedOperationException("TODO 3: removeNode");
    }

    @Override
    public String getNode(String key) {
        if (key == null) {
            throw new IllegalArgumentException("키가 null 이다");
        }
        if (ring.isEmpty()) {
            return null;
        }
        // TODO 4: 키의 자리에서 시계 방향으로 처음 만나는 노드를 준다.
        //
        // TreeMap 이 정렬을 들고 있으니 직접 훑을 필요는 없다.
        // "이 값 이상인 첫 항목"을 O(log n) 에 주는 메서드가 NavigableMap 에 있다.
        // 정렬 배열에 이진 탐색을 직접 걸어도 같다. 06번, 22번에서 하던 그 탐색이다.
        //
        // 그 메서드가 null 을 줄 때가 문제다. 키의 자리가 원에서 가장 큰 자리보다 뒤라는 뜻인데,
        // 원이니까 없는 것이 아니라 한 바퀴 돌아 첫 자리로 가야 한다.
        // 이걸 빠뜨리는 것이 이 문제에서 제일 흔한 실수다.
        //
        // 그리고 잘 안 걸린다. 자리가 1000개면 그런 키가 100,000개 중 18개뿐이다.
        // 그래서 계약 테스트가 "모든 키가 살아 있는 노드로 간다"를 따로 확인하고,
        // 구조 테스트는 자리를 6개로 줄여 그런 키를 270개로 만들어 둔다.
        throw new UnsupportedOperationException("TODO 4: getNode");
    }

    @Override
    public int nodeCount() {
        return placed.size();
    }

    @Override
    public List<String> nodes() {
        return Collections.unmodifiableList(new ArrayList<>(placed.keySet()));
    }

    @Override
    public int slotCount() {
        return ring.size();
    }

    /** 그 노드가 원 위에 선 자리의 수. */
    public int slotsOf(String node) {
        return placed.getOrDefault(node, 0);
    }

    /** 원을 그대로 본다. 구조 테스트가 자리 배치를 직접 확인할 때 쓴다. */
    public SortedMap<Long, String> ringView() {
        return Collections.unmodifiableSortedMap(ring);
    }
}
