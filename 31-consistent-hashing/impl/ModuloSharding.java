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
        return nodes.get(Hashing.bucketHash(key) % nodes.size());
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
