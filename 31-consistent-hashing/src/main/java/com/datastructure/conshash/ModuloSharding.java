package com.datastructure.conshash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 기준선. hash(key) % N.
 *
 * 05번 해시맵이 자리를 정하던 그 식이다. 맵 안에서는 이게 맞다.
 * 노드 목록에 쓰면 N 이 바뀔 때마다 거의 전부가 옮겨간다. MovementTest 가 그 수를 센다.
 *
 * 자리를 위해 들고 있는 것이 노드 이름 목록뿐이라 메모리는 제일 적다.
 * 이 방식이 나쁜 것은 메모리도 속도도 아니고 오직 이동량이다.
 */
public class ModuloSharding implements HashRing {

    private final List<String> nodes = new ArrayList<>();

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

    @Override
    public void removeNode(String node) {
        if (!nodes.remove(node)) {
            throw new IllegalArgumentException("없는 노드다: " + node);
        }
    }

    @Override
    public String getNode(String key) {
        if (key == null) {
            throw new IllegalArgumentException("키가 null 이다");
        }
        if (nodes.isEmpty()) {
            return null;
        }
        // TODO 1: Hashing.bucketHash(key) 를 노드 수로 나눈 나머지 자리의 노드를 준다.
        //
        // 05번 해시맵이 버킷을 고르던 그 한 줄이고, 여기서도 답은 맞다.
        // 분포도 고르다(ModuloShardingTest 가 확인한다). 이 방식의 문제는 다른 데 있다.
        //
        // 다 채운 뒤 MovementTest 를 보라. 노드 하나를 뺐을 때 자리가 바뀌는 키가 89,905개다.
        // 죽은 노드가 맡던 것은 10,029개뿐인데 나머지 8만 개는 멀쩡한 노드끼리 서로 넘긴다.
        // 나눗셈의 분모가 바뀌면 모든 몫이 바뀌기 때문이고, 이 한 줄로는 그것을 피할 수 없다.
        //
        // 나머지가 음수가 되지 않는 것은 bucketHash 가 최상위 비트를 지우기 때문이다.
        // 05번에서 Math.abs 로는 부족하다고 했던 그 자리다.
        throw new UnsupportedOperationException("TODO 1: getNode");
    }

    @Override
    public int nodeCount() {
        return nodes.size();
    }

    @Override
    public List<String> nodes() {
        return Collections.unmodifiableList(new ArrayList<>(nodes));
    }

    @Override
    public int slotCount() {
        return nodes.size();
    }
}
