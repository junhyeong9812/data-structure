package com.datastructure.depresolve;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 누가 누구에게 기대는가. 두 해석기가 이것을 공유한다.
 *
 * 08번 그래프의 인접 리스트 그대로다. 다른 것은 이름 하나뿐이다.
 *
 * <h2>화살표의 방향을 정해야 한다</h2>
 *
 * <pre>
 *   dependsOn("앱", "라이브러리")     앱이 라이브러리에 기댄다
 *
 *   그러면 화살표는 어느 쪽인가. 둘 다 말이 된다.
 *     앱 -&gt; 라이브러리    "기댄다" 방향
 *     라이브러리 -&gt; 앱    "먼저 와야 한다" 방향
 * </pre>
 *
 * 여기서는 <b>뒤쪽</b>을 고른다. 위상 정렬이 화살표 방향대로 늘어놓는 것이고,
 * 우리가 원하는 순서가 "먼저 와야 하는 것이 앞" 이기 때문이다.
 * 앞쪽을 고르면 답이 정확히 뒤집혀 나온다. 그리고 그것은 예외가 아니라 조용한 오답이다.
 *
 * 방향을 헷갈린 채로도 순환 탐지는 멀쩡히 동작한다. 순환은 방향을 뒤집어도 순환이라서다.
 * 그래서 "순환 테스트가 통과하니까 그래프는 맞다" 가 성립하지 않는다.
 *
 * <h2>순서를 정해두는 이유</h2>
 *
 * TreeMap 과 TreeSet 이다. 위상 정렬은 답이 여럿이라, 자료구조의 순회 순서가
 * 그대로 답의 순서가 된다. HashMap 을 쓰면 같은 입력에 같은 답이 나온다는 보장이 없다.
 * 05번에서 "해시 순회 순서에 기대지 마라" 고 했던 것이 여기서는 재현성 문제가 된다.
 */
public class DependencyGraph {

    /** 이름 -> 그 이름 뒤에 와야 하는 것들. 화살표 방향이다. */
    private final Map<String, TreeSet<String>> edges = new TreeMap<>();

    /** 이름 -> 그 앞에 와야 하는 것의 개수. 칸 알고리즘의 진입 차수다. */
    private final Map<String, Integer> inDegree = new TreeMap<>();

    /** 노드 하나. 이미 있으면 아무 일도 안 한다. */
    public void add(String name) {
        require(name);
        // TODO 1: 노드 하나를 등록한다. 이미 있으면 아무 일도 안 한다.
        //
        // put 으로 쓰면 이미 있던 간선이 날아간다. add 를 두 번 부르는 것은 정상 동작이다.
        throw new UnsupportedOperationException("TODO 1: add");
    }

    /**
     * dependent 가 dependency 에 기댄다. 없는 이름은 만들어 넣는다.
     *
     * 같은 의존을 두 번 걸어도 진입 차수는 한 번만 올라가야 한다.
     * 두 번 올리면 그 노드는 영원히 0 이 안 되고, 순환이 없는데도 순환으로 보고된다.
     */
    public void dependsOn(String dependent, String dependency) {
        require(dependent);
        require(dependency);
        // TODO 2: 간선 하나를 건다. 위 javadoc 이 화살표의 방향을 정해뒀다.
        //
        // 방향을 반대로 걸어도 순환 탐지는 멀쩡히 동작한다. 순환은 뒤집어도 순환이라서다.
        // 그래서 순환 테스트가 통과하는 것으로는 방향이 맞다는 증거가 안 된다.
        //
        // 같은 의존을 두 번 걸었을 때 진입 차수를 두 번 올리면, 그 노드는 영원히 0 이 안 되고
        // 순환이 없는데도 순환으로 보고된다.
        //
        // 자기 자신에게 기대는 것은 막지 않는다. 길이 1 짜리 순환으로 다룬다.
        // 여기서 조용히 무시하면 진짜 순환 하나가 사라진다.
        throw new UnsupportedOperationException("TODO 2: dependsOn");
    }

    /** 이름들. 오름차순. */
    public List<String> names() {
        return List.copyOf(edges.keySet());
    }

    /** name 뒤에 와야 하는 것들. 오름차순. */
    public List<String> after(String name) {
        TreeSet<String> found = edges.get(name);
        return found == null ? List.of() : List.copyOf(found);
    }

    /** name 앞에 와야 하는 것의 개수. */
    public int inDegreeOf(String name) {
        return inDegree.getOrDefault(name, 0);
    }

    /** name 이 직접 기대는 것들. after 의 반대 방향이라 훑어서 만든다. */
    public List<String> dependenciesOf(String name) {
        // TODO 3: name 이 직접 기대는 것들. 화살표의 반대 방향이라 훑어서 만든다.
        throw new UnsupportedOperationException("TODO 3: dependenciesOf");
    }

    public int size() {
        return edges.size();
    }

    public int edgeCount() {
        int total = 0;
        for (TreeSet<String> to : edges.values()) {
            total += to.size();
        }
        return total;
    }

    private static void require(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름이 비었다");
        }
    }

    @Override
    public String toString() {
        return "의존 그래프(노드 " + size() + ", 간선 " + edgeCount() + ")";
    }
}
