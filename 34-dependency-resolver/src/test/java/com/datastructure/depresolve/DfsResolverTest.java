package com.datastructure.depresolve;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 계약을 DFS 로 돌리고, 이 구현만의 능력을 따로 본다.
 *
 * 이 구현이 존재하는 이유는 답이 아니라 순환의 위치다.
 */
@DisplayName("DFS 후위 뒤집기")
class DfsResolverTest extends ResolverContractTest {

    @Override
    protected Resolver resolverFor(DependencyGraph graph) {
        return new DfsResolver(graph);
    }

    @Test
    @DisplayName("이 구현만의 능력: 도는 경로를 그대로 준다")
    void reportsTheActualCycle() {
        DependencyGraph g = graphOf("a <- b", "b <- c", "c <- a", "far1 <- far2");
        CycleException e = assertThrows(CycleException.class, () -> new DfsResolver(g).resolve());

        List<String> path = e.path();
        assertEquals(4, path.size(), "고리 세 개에 처음을 한 번 더: " + path);
        assertEquals(path.get(0), path.get(path.size() - 1), "처음과 끝이 같아야 고리다");
        assertTrue(path.containsAll(List.of("a", "b", "c")), path.toString());
        assertTrue(!path.contains("far1"), "순환과 상관없는 것이 섞이면 안 된다: " + path);
    }

    @Test
    @DisplayName("자기 자신에게 기대면 길이 1 짜리 고리다")
    void selfLoopPath() {
        DependencyGraph g = new DependencyGraph();
        g.dependsOn("a", "a");
        assertEquals(List.of("a", "a"), new DfsResolver(g).cycle());
    }

    @Test
    @DisplayName("고리로 내려가는 길에 끝난 가지가 경로에 섞이면 안 된다")
    void completedBranchesAreNotPartOfTheCycle() {
        // a 에서 n 으로 한 번 내려갔다 돌아온 뒤에 p, q 로 내려가서 a 를 다시 만난다.
        // n 은 이미 끝난 가지다. 고리가 아니다.
        //
        //   a -> n              (내려갔다 돌아온다)
        //   a -> p -> q -> a
        DependencyGraph g = new DependencyGraph();
        g.dependsOn("n", "a");
        g.dependsOn("p", "a");
        g.dependsOn("q", "p");
        g.dependsOn("a", "q");

        List<String> path = new DfsResolver(g).cycle();

        // 돌아올 때 자기를 경로에서 안 빼면 n 이 여기 섞인다. 예외는 안 나고 진단만 틀린다.
        assertEquals(List.of("a", "p", "q", "a"), path);
        assertTrue(!path.contains("n"), "끝난 가지가 섞였다: " + path);
    }

    @Test
    @DisplayName("고리가 둘이면 먼저 만난 하나를 준다")
    void reportsOneCycle() {
        DependencyGraph g = graphOf("a <- b", "b <- a", "x <- y", "y <- x");
        List<String> path = new DfsResolver(g).cycle();
        assertEquals(3, path.size(), path.toString());
        assertEquals(path.get(0), path.get(2));
        // 둘 다 주려면 강한 연결 요소를 구해야 한다. 그건 이 박스의 범위 밖이다.
    }

    @Test
    @DisplayName("메시지에 경로가 들어간다. 받는 쪽이 고칠 수 있어야 한다")
    void messageCarriesThePath() {
        DependencyGraph g = graphOf("a <- b", "b <- a");
        CycleException e = assertThrows(CycleException.class, () -> new DfsResolver(g).resolve());
        assertTrue(e.getMessage().contains("->"), e.getMessage());
        assertTrue(e.getMessage().contains("a"), e.getMessage());
    }

    @Test
    @DisplayName("노드마다 정확히 한 번만 내려간다")
    void everyNodeIsVisitedOnce() {
        DependencyGraph g = graphOf("a <- b", "a <- c", "b <- d", "c <- d");
        DfsResolver r = new DfsResolver(g);
        r.resolve();
        // 다이아몬드에서 d 를 두 번 만나지만 내려가는 것은 한 번이다.
        // 검은색을 안 보고 다시 내려가면 여기가 5 가 되고, 넓은 다이아몬드에서는 지수로 터진다.
        assertEquals(4, r.visits());
    }

    @Test
    @DisplayName("깊은 다이아몬드에서 방문이 지수로 늘지 않는다")
    void memoisationKeepsItLinear() {
        DependencyGraph g = new DependencyGraph();
        for (int i = 0; i < 18; i++) {
            g.dependsOn("l" + (i + 1), "l" + i);
            g.dependsOn("r" + (i + 1), "l" + i);
            g.dependsOn("l" + (i + 1), "r" + i);
            g.dependsOn("r" + (i + 1), "r" + i);
        }
        DfsResolver r = new DfsResolver(g);
        r.resolve();
        // 색을 안 쓰면 2^18 번 내려간다. 노드 수는 38 개뿐이다.
        assertEquals(g.size(), r.visits());
        assertEquals(38, r.visits());
    }
}
