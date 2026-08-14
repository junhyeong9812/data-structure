package com.datastructure.depresolve;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 깊이 우선으로 내려갔다가 돌아오는 순서를 뒤집는다.
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
        // TODO 6: 흰색 노드마다 내려간다. 다 끝나면 후위 목록을 뒤집는다.
        //
        // 섬이 여럿일 수 있으므로 한 노드에서 시작해서는 안 된다. 전부 훑어야 한다.
        // 뒤집는 것을 빼면 답이 정확히 거꾸로 나온다. 예외는 안 난다.
        throw new UnsupportedOperationException("TODO 6: resolve");
    }

    /** 못 내려가면 false. 그때 foundCycle 에 경로가 담겨 있다. */
    private boolean walk(String name, Map<String, Integer> color,
                         List<String> path, List<String> postorder) {
        visits++;
        // TODO 7: 이 박스의 본체다. 색 세 가지를 정확히 다뤄야 한다.
        //
        // 내려갈 때 회색, 다 보고 돌아오면 검은색. 회색을 다시 만나면 순환이다.
        //
        // 넷을 조심하라.
        //
        //   1. **검은색은 순환이 아니다.** 다이아몬드에서 정상적으로 일어나는 일이다.
        //      회색과 검은색을 뭉뚱그리면 멀쩡한 그래프를 순환이라고 한다.
        //   2. **검은색은 다시 내려가지 않는다.** 다시 파면 답은 맞는데 넓은 다이아몬드에서
        //      방문이 2의 거듭제곱으로 터진다. visits 가 그것을 잰다.
        //   3. 순환을 찾았으면 **그 지점부터** 지금까지가 고리다. 경로 전체가 아니다.
        //      처음과 끝을 같게 맞춰라. 그래야 고리라는 것이 목록만 보고도 드러난다.
        //   4. 돌아올 때 자기를 경로에서 빼라. 안 빼면 이미 끝난 가지가 고리에 섞인다.
        //      예외는 안 나고 진단만 조용히 틀린다.
        //
        // 후위다. 자기를 적는 것은 자식을 전부 본 **뒤**다. 먼저 적으면 전위가 되고,
        // 전위를 뒤집은 것은 위상 정렬이 아니다.
        throw new UnsupportedOperationException("TODO 7: walk");
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
        // TODO 8: 각 노드의 층 = 자기 앞의 것들의 층 중 **최댓값** 더하기 1.
        //
        // 최솟값이나 처음 것을 쓰면 "가장 이른 때" 가 아니라 아무 때나 된다.
        // 그러면 층 안에 서로 기대는 것이 같이 들어가서 병렬로 돌릴 수 없게 된다.
        //
        // 위상 순서대로 훑으면 자기 앞의 것이 이미 확정돼 있어 한 번에 끝난다.
        // 칸 알고리즘에서는 그냥 나오는 값을 여기서는 따로 구해야 한다.
        throw new UnsupportedOperationException("TODO 8: layers");
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
