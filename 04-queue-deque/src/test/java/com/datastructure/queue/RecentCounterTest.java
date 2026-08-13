package com.datastructure.queue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/** RecentCounter 는 Queue 계약만 알고 있으므로 네 구현 전부에서 같게 동작해야 한다. */
class RecentCounterTest {

    static Stream<Arguments> queues() {
        return Stream.of(
            Arguments.of("ArrayQueue", (Supplier<Queue<Integer>>) ArrayQueue::new),
            Arguments.of("CircularQueue", (Supplier<Queue<Integer>>) CircularQueue::new),
            Arguments.of("ArrayDeque", (Supplier<Queue<Integer>>) ArrayDeque::new),
            Arguments.of("LinkedDeque", (Supplier<Queue<Integer>>) LinkedDeque::new)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("queues")
    @DisplayName("창 안의 요청만 센다")
    void countsWithinWindow(String n, Supplier<Queue<Integer>> f) {
        RecentCounter counter = new RecentCounter(f.get());

        assertEquals(1, counter.ping(1));
        assertEquals(2, counter.ping(100));
        assertEquals(3, counter.ping(3001), "3001 - 3000 = 1 이므로 첫 요청도 아직 창 안이다");
        assertEquals(3, counter.ping(3002), "이제 1 은 창을 벗어났다");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("queues")
    @DisplayName("경계는 포함이다")
    void boundaryIsInclusive(String n, Supplier<Queue<Integer>> f) {
        RecentCounter counter = new RecentCounter(f.get());
        counter.ping(0);
        assertEquals(2, counter.ping(3000), "3000 - 3000 = 0 이므로 첫 요청이 포함된다");
        assertEquals(2, counter.ping(3001), "0 은 빠지고 3000, 3001 이 남는다");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("queues")
    @DisplayName("같은 시각의 요청도 각각 센다")
    void countsSameTimestamps(String n, Supplier<Queue<Integer>> f) {
        RecentCounter counter = new RecentCounter(f.get());
        assertEquals(1, counter.ping(5));
        assertEquals(2, counter.ping(5));
        assertEquals(3, counter.ping(5));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("queues")
    @DisplayName("멀리 떨어진 요청은 전부 빠진다")
    void dropsAllWhenFarApart(String n, Supplier<Queue<Integer>> f) {
        RecentCounter counter = new RecentCounter(f.get());
        counter.ping(1);
        counter.ping(2);
        assertEquals(1, counter.ping(1_000_000));
        assertEquals(1, counter.size());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("queues")
    @DisplayName("10만 번 호출을 5초 안에 (각 요청은 한 번 들어가고 한 번 나온다)")
    void amortizedLinear(String name, Supplier<Queue<Integer>> f) {
        RecentCounter counter = new RecentCounter(f.get());
        assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
            for (int t = 1; t <= 100_000; t++) counter.ping(t);
            assertEquals(3001, counter.size(), "창 크기만큼만 남아야 한다");
        });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("queues")
    @DisplayName("비어 있지 않은 큐는 거부한다")
    void rejectsNonEmptyQueue(String n, Supplier<Queue<Integer>> f) {
        Queue<Integer> dirty = f.get();
        dirty.enqueue(1);
        assertThrows(IllegalArgumentException.class, () -> new RecentCounter(dirty));
    }
}
