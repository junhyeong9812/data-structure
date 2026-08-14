package com.datastructure.depresolve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * [구현] 진입 차수가 0 인 것부터 뽑는다. 칸 알고리즘.
 *
 * <pre>
 *   1. 앞에 아무것도 없는 것들을 모은다
 *   2. 하나 꺼내 답에 붙이고, 그 뒤에 오는 것들의 진입 차수를 하나씩 내린다
 *   3. 0 이 된 것을 모음에 넣는다
 *   4. 모음이 빌 때까지 반복
 * </pre>
 *
 * <h2>순환은 "다 못 뽑았다" 로 드러난다</h2>
 *
 * 순환에 든 노드는 서로가 서로를 막아서 진입 차수가 영원히 0 이 안 된다.
 * 그래서 모음이 먼저 비고, 답의 길이가 노드 수보다 짧다.
 *
 * <b>남은 것이 순환에 든 것들이라는 것까지는 안다. 그런데 그중 어느 것들이 실제로 도는
 * 고리인지는 모른다.</b> 순환 하나에 딸린 나머지도 전부 남기 때문이다.
 * 그래서 cycle 이 빈 목록을 준다. 모르는 것을 아는 척하지 않는다.
 *
 * DfsResolver 가 그것을 안다. 같은 문제를 푸는 두 알고리즘의 차이가 답이 아니라
 * <b>실패했을 때 말해줄 수 있는 것</b>에 있다.
 *
 * <h2>답을 하나로 고정한다</h2>
 *
 * 꺼낼 것이 여럿일 때 무엇을 먼저 꺼내도 맞다. PriorityQueue 로 이름이 작은 것부터 꺼낸다.
 * 그러면 이 구현의 답은 <b>사전순으로 가장 이른 위상 정렬</b> 하나로 정해진다.
 * 정해두지 않으면 같은 입력에 같은 답이 나온다는 보장이 없고, 그러면 기댓값을 쓸 수 없다.
 */
public class KahnResolver implements Resolver {

    private final DependencyGraph graph;

    private long relaxations;

    public KahnResolver(DependencyGraph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("그래프가 null 이다");
        }
        this.graph = graph;
    }

    @Override
    public List<String> resolve() {
        relaxations = 0;
        Map<String, Integer> remaining = new HashMap<>();
        PriorityQueue<String> ready = new PriorityQueue<>();
        for (String name : graph.names()) {
            int degree = graph.inDegreeOf(name);
            remaining.put(name, degree);
            if (degree == 0) {
                ready.add(name);
            }
        }

        List<String> order = new ArrayList<>();
        while (!ready.isEmpty()) {
            String name = ready.poll();
            order.add(name);
            for (String next : graph.after(name)) {
                relaxations++;
                int left = remaining.merge(next, -1, Integer::sum);
                if (left == 0) {
                    ready.add(next);
                }
            }
        }

        if (order.size() != graph.size()) {
            // 부분 결과를 주지 않는다. 절반만 설치된 상태가 아무것도 안 한 상태보다 나쁘다.
            throw new CycleException(cycle());
        }
        return List.copyOf(order);
    }

    /** 이 구현은 경로를 모른다. 빈 목록이 그 뜻이다. */
    @Override
    public List<String> cycle() {
        return List.of();
    }

    /**
     * 층 나누기는 이 알고리즘의 부산물이다.
     *
     * 진입 차수 0 인 것을 하나씩 꺼내는 대신 <b>그 시점의 것을 통째로</b> 꺼내면 그게 한 층이다.
     * DFS 로는 이만큼 자연스럽게 안 나온다. 같은 값을 두 알고리즘이 다른 비용으로 내는 자리다.
     */
    @Override
    public List<List<String>> layers() {
        Map<String, Integer> remaining = new HashMap<>();
        List<String> ready = new ArrayList<>();
        for (String name : graph.names()) {
            int degree = graph.inDegreeOf(name);
            remaining.put(name, degree);
            if (degree == 0) {
                ready.add(name);
            }
        }

        List<List<String>> out = new ArrayList<>();
        int placed = 0;
        while (!ready.isEmpty()) {
            // 이 층을 통째로 확정한 뒤에 다음 층을 만든다.
            // 한 개씩 처리하면서 새로 0 이 된 것을 같은 층에 넣으면 층이 뭉개진다.
            List<String> layer = List.copyOf(ready);
            out.add(layer);
            placed += layer.size();
            List<String> next = new ArrayList<>();
            for (String name : layer) {
                for (String child : graph.after(name)) {
                    if (remaining.merge(child, -1, Integer::sum) == 0) {
                        next.add(child);
                    }
                }
            }
            next.sort(null);
            ready = next;
        }

        if (placed != graph.size()) {
            throw new CycleException(cycle());
        }
        return List.copyOf(out);
    }

    /** 진입 차수를 내린 횟수. 간선 수와 같아야 한다. */
    public long relaxations() {
        return relaxations;
    }

    @Override
    public String toString() {
        return "칸 알고리즘";
    }
}
