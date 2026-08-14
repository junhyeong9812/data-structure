package com.datastructure.persistent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

/**
 * [구현] 영속 자료구조로 푸는 문제 둘.
 */
public final class PersistentProblems {

    private PersistentProblems() {
    }

    /**
     * 명령을 하나씩 실행하며 매 시점의 맵을 전부 남긴다.
     * 결과의 0번은 아무것도 실행하기 전, i+1번은 i번 명령을 실행한 뒤다.
     *
     * 스냅샷을 남기는 데 드는 비용이 없다는 것이 요점이다. 참조를 리스트에 넣을 뿐이다.
     */
    public static List<PersistentMap<String, Integer>> replay(List<String[]> commands) {
        if (commands == null) {
            throw new IllegalArgumentException("명령 목록이 필요하다");
        }
        List<PersistentMap<String, Integer>> snapshots = new ArrayList<>(commands.size() + 1);
        PersistentMap<String, Integer> map = PersistentTreeMap.empty();
        snapshots.add(map);
        for (int i = 0; i < commands.size(); i++) {
            String[] command = commands.get(i);
            if (command == null || command.length == 0) {
                throw new IllegalArgumentException(i + "번 명령이 비어 있다");
            }
            switch (command[0]) {
                case "put" -> {
                    if (command.length != 3) {
                        throw new IllegalArgumentException(i + "번 put 은 {put, 키, 값} 세 칸이어야 한다");
                    }
                    map = map.put(command[1], Integer.parseInt(command[2]));
                }
                case "remove" -> {
                    if (command.length != 2) {
                        throw new IllegalArgumentException(i + "번 remove 는 {remove, 키} 두 칸이어야 한다");
                    }
                    map = map.remove(command[1]);
                }
                default -> throw new IllegalArgumentException(i + "번 명령을 모르겠다: " + command[0]);
            }
            snapshots.add(map);
        }
        return snapshots;
    }

    /**
     * 두 버전이 실제로 공유하는 노드의 수. 값이 같은 것이 아니라 같은 객체인 것만 센다.
     *
     * 옛 버전의 노드를 참조 동일성 집합에 담아두고 새 버전을 훑는다.
     * 공유하는 노드를 만나면 그 아래는 통째로 공유이므로 부분트리 크기를 더하고 멈춘다.
     * 노드가 불변이라 성립하는 지름길이다. 자식을 고칠 수 있었다면 끝까지 내려가야 한다.
     */
    public static <K extends Comparable<K>, V> long countSharedNodes(
            PersistentTreeMap<K, V> before, PersistentTreeMap<K, V> after) {
        if (before == null || after == null) {
            throw new IllegalArgumentException("두 버전이 모두 필요하다");
        }
        Set<PersistentTreeMap.Node<K, V>> oldNodes = Collections.newSetFromMap(new IdentityHashMap<>());
        collect(before.root, oldNodes);
        return countShared(after.root, oldNodes);
    }

    private static <K, V> void collect(
            PersistentTreeMap.Node<K, V> node, Set<PersistentTreeMap.Node<K, V>> out) {
        if (node == null) {
            return;
        }
        out.add(node);
        collect(node.left, out);
        collect(node.right, out);
    }

    private static <K, V> long countShared(
            PersistentTreeMap.Node<K, V> node, Set<PersistentTreeMap.Node<K, V>> oldNodes) {
        if (node == null) {
            return 0;
        }
        if (oldNodes.contains(node)) {
            return node.size;
        }
        return countShared(node.left, oldNodes) + countShared(node.right, oldNodes);
    }
}
