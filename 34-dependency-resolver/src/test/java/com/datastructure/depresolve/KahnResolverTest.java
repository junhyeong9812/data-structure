package com.datastructure.depresolve;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 계약을 칸 알고리즘으로 돌리고, 이 구현만의 보장을 따로 본다.
 *
 * @Nested 클래스를 새로 만들지 않는다. 상위와 이름이 겹치면 상위 테스트가
 * 실패가 아니라 조용히 사라진다.
 */
@DisplayName("칸 알고리즘")
class KahnResolverTest extends ResolverContractTest {

    @Override
    protected Resolver resolverFor(DependencyGraph graph) {
        return new KahnResolver(graph);
    }

    @Test
    @DisplayName("이 구현만의 보장: 사전순으로 가장 이른 답")
    void producesTheLexicographicallySmallestOrder() {
        DependencyGraph g = graphOf("a <- z", "a <- b", "a <- m");
        // z, b, m 은 서로 상관없다. 어느 순서든 맞는데 이 구현은 b, m, z 로 고정한다.
        assertEquals(List.of("a", "b", "m", "z"), new KahnResolver(g).resolve());
    }

    @Test
    @DisplayName("이 구현만의 보장: 섬이 여럿이어도 사전순이다")
    void smallestAcrossComponents() {
        DependencyGraph g = graphOf("y <- z", "a <- b");
        assertEquals(List.of("a", "b", "y", "z"), new KahnResolver(g).resolve());
    }

    @Test
    @DisplayName("이 구현의 한계: 순환의 위치를 모른다")
    void cannotLocateTheCycle() {
        DependencyGraph g = graphOf("a <- b", "b <- c", "c <- a", "far1 <- far2");
        CycleException e = assertThrows(CycleException.class, () -> new KahnResolver(g).resolve());

        // 순환이 있다는 것만 안다. 어디인지는 모른다.
        assertEquals(List.of(), e.path());
        assertEquals(List.of(), new KahnResolver(g).cycle());
        // 모르는 것을 아는 척하지 않는다. DfsResolver 가 이것을 안다.
    }

    @Test
    @DisplayName("진입 차수를 내린 횟수가 간선 수와 같다")
    void everyEdgeIsRelaxedExactlyOnce() {
        DependencyGraph g = graphOf("a <- b", "a <- c", "b <- d", "c <- d");
        KahnResolver r = new KahnResolver(g);
        r.resolve();
        assertEquals(g.edgeCount(), r.relaxations());
        assertEquals(4, r.relaxations());
    }
}
