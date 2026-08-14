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
        for (int i = 0; i < count; i++) {
            ring.put(hash.position(virtualName(node, i)), node);
        }
        placed.put(node, count);
    }

    @Override
    public void removeNode(String node) {
        Integer count = placed.remove(node);
        if (count == null) {
            throw new IllegalArgumentException("없는 노드다: " + node);
        }
        for (int i = 0; i < count; i++) {
            long position = hash.position(virtualName(node, i));
            if (node.equals(ring.get(position))) {
                ring.remove(position);
            }
        }
    }

    @Override
    public String getNode(String key) {
        if (key == null) {
            throw new IllegalArgumentException("키가 null 이다");
        }
        if (ring.isEmpty()) {
            return null;
        }
        Map.Entry<Long, String> entry = ring.ceilingEntry(hash.position(key));
        return entry != null ? entry.getValue() : ring.firstEntry().getValue();
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
