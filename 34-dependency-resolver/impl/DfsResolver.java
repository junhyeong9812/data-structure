package com.datastructure.depresolve;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [구현] 깊이 우선으로 내려갔다가 돌아오는 순서를 뒤집는다.
 *
 * 한 노드에서 갈 수 있는 데를 전부 가본 뒤에 자기를 적는다(후위). 그 목록을 뒤집으면
 * 자기가 자기 뒤에 오는 것들보다 앞에 온다. 그것이 위상 정렬이다.
 *
 * <h2>색 세 가지가 이 알고리즘의 전부다</h2>
 *
 * <pre>
 *   흰색   아직 안 봤다
 *   회색   지금 내려가는 중이다 (스택에 있다)
 *   검은색 다 보고 돌아왔다
 * </pre>
 *
 * <b>회색을 다시 만나면 순환이다.</b> 지금 내려온 길 위의 노드를 또 만났다는 뜻이라,
 * 그 길이 곧 도는 경로다. 그래서 이 구현은 순환의 <b>위치를 안다.</b>
 *
 * 색을 둘로 줄이면(봤다/안 봤다) 이것이 무너진다. 다이아몬드 모양에서
 * 검은색 노드를 다시 만나는 일이 정상적으로 일어나는데, 그것을 순환으로 잘못 보고한다.
 *
 * <pre>
 *      a
 *     / \      d 를 b 로도 c 로도 만난다. 순환이 아니다.
 *    b   c     색이 둘이면 두 번째 만남을 구별할 수 없다.
 *     \ /
 *      d
 * </pre>
 *
 * 이 실수는 <b>순환이 없는 그래프를 순환이라고 하는</b> 쪽이라 테스트에 걸린다.
 * 반대 실수(순환을 놓치는 것)보다 낫다. 시끄러운 실패가 조용한 실패보다 낫다.
 *
 * <h2>답이 칸 알고리즘과 다르다</h2>
 *
 * 둘 다 맞다. 그래서 두 구현의 답을 그냥 비교할 수 없고, 성질로 검증한다.
 * 이 박스에서 32번 33번의 대조 방식이 성립하지 않는 이유다.
 */
public class DfsResolver implements Resolver {

    private static final int WHITE = 0;
    private static final int GRAY = 1;
    private static final int BLACK = 2;

    private final DependencyGraph graph;

    private long visits;
    private List<String> foundCycle = List.of();

    public DfsResolver(DependencyGraph graph) {
        if (graph == null) {
            throw new IllegalArgumentException("그래프가 null 이다");
        }
        this.graph = graph;
    }

    @Override
    public List<String> resolve() {
        visits = 0;
        foundCycle = List.of();
        Map<String, Integer> color = new HashMap<>();
        for (String name : graph.names()) {
            color.put(name, WHITE);
        }

        List<String> postorder = new ArrayList<>();
        List<String> path = new ArrayList<>();
        for (String name : graph.names()) {
            if (color.get(name) == WHITE && !walk(name, color, path, postorder)) {
                throw new CycleException(foundCycle);
            }
        }
        Collections.reverse(postorder);
        return List.copyOf(postorder);
    }

    /** 못 내려가면 false. 그때 foundCycle 에 경로가 담겨 있다. */
    private boolean walk(String name, Map<String, Integer> color,
                         List<String> path, List<String> postorder) {
        visits++;
        color.put(name, GRAY);
        path.add(name);

        for (String next : graph.after(name)) {
            int c = color.getOrDefault(next, WHITE);
            if (c == GRAY) {
                // 지금 내려온 길 위에서 다시 만났다. 그 지점부터 여기까지가 도는 고리다.
                int from = path.indexOf(next);
                List<String> loop = new ArrayList<>(path.subList(from, path.size()));
                loop.add(next);         // 처음과 끝을 같게 해서 고리임을 드러낸다
                foundCycle = List.copyOf(loop);
                return false;
            }
            if (c == WHITE && !walk(next, color, path, postorder)) {
                return false;
            }
            // 검은색이면 이미 다 본 것이다. 다시 안 내려간다. 순환도 아니다.
        }

        path.remove(path.size() - 1);
        color.put(name, BLACK);
        postorder.add(name);
        return true;
    }

    @Override
    public List<String> cycle() {
        try {
            resolve();
            return List.of();
        } catch (CycleException e) {
            return e.path();
        }
    }

    /**
     * 층은 이 알고리즘의 부산물이 아니다.
     *
     * 각 노드의 층 = 자기가 기대는 것들의 층 중 최댓값 더하기 1 이다.
     * 후위 순서로 훑으면 자기 뒤의 것이 먼저 확정돼 있으므로 한 번에 계산된다.
     * 칸 알고리즘은 그냥 나오는 값을 여기서는 따로 구해야 한다.
     */
    @Override
    public List<List<String>> layers() {
        List<String> order = resolve();      // 순환이면 여기서 던진다
        if (order.isEmpty()) {
            // 이 검사가 없으면 빈 그래프에서 빈 층 하나가 딸려 나온다.
            // "층이 없다" 와 "빈 층이 하나 있다" 는 다르다.
            return List.of();
        }
        Map<String, Integer> depth = new HashMap<>();
        int deepest = 0;
        for (String name : order) {
            int mine = depth.getOrDefault(name, 0);
            for (String next : graph.after(name)) {
                depth.merge(next, mine + 1, Math::max);
            }
            deepest = Math.max(deepest, mine);
        }

        List<List<String>> out = new ArrayList<>();
        for (int i = 0; i <= deepest; i++) {
            out.add(new ArrayList<>());
        }
        for (String name : order) {
            out.get(depth.getOrDefault(name, 0)).add(name);
        }
        List<List<String>> sorted = new ArrayList<>();
        for (List<String> layer : out) {
            layer.sort(null);
            sorted.add(List.copyOf(layer));
        }
        return List.copyOf(sorted);
    }

    /** 내려간 횟수. 노드 수와 같아야 한다. 같지 않으면 같은 자리를 두 번 판 것이다. */
    public long visits() {
        return visits;
    }

    @Override
    public String toString() {
        return "DFS 후위 뒤집기";
    }
}
