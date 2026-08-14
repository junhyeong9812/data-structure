package com.datastructure.sketch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/** CardinalityEstimator 계약. 정확한 구현과 HyperLogLog 가 똑같이 지켜야 하는 것만 여기 있다. */
abstract class CardinalityEstimatorContractTest {

    protected abstract CardinalityEstimator create();

    /** 허용 상대 오차. 정확한 구현은 0 이다. */
    protected abstract double tolerance();

    private void assertClose(long truth, long estimate, String what) {
        double allowed = tolerance() * truth;
        assertTrue(Math.abs(estimate - truth) <= allowed,
                what + ": 실제 " + truth + ", 추정 " + estimate
                        + " (허용 " + allowed + ")");
    }

    @Nested
    @DisplayName("작은 카디널리티는 정확해야 한다")
    class SmallIsExact {

        @Test
        @DisplayName("빈 것은 0 이다")
        void empty() {
            assertEquals(0, create().estimate(), "아무것도 안 넣었는데 0 이 아니다");
        }

        @Test
        @DisplayName("하나 넣으면 1 이다")
        void single() {
            CardinalityEstimator c = create();
            c.add(42);
            assertEquals(1, c.estimate());
        }

        @Test
        @DisplayName("같은 것을 1000번 넣어도 1 이다")
        void duplicatesCountOnce() {
            // 이것이 빈도 세기와 다른 질문이다. 조회수가 아니라 순 방문자다.
            CardinalityEstimator c = create();
            for (int i = 0; i < 1000; i++) {
                c.add(7);
            }
            assertEquals(1, c.estimate());
        }

        @Test
        @DisplayName("열 개는 열 개다")
        void ten() {
            CardinalityEstimator c = create();
            for (int i = 0; i < 10; i++) {
                c.add(i * 7 + 1);
            }
            assertEquals(10, c.estimate());
        }

        @Test
        @DisplayName("음수와 극단값도 센다")
        void extremeKeys() {
            CardinalityEstimator c = create();
            int[] keys = {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE};
            for (int k : keys) {
                c.add(k);
            }
            assertEquals(5, c.estimate());
        }
    }

    @Nested
    @DisplayName("순서와 중복에 흔들리지 않는다")
    class OrderIndependent {

        @Test
        @DisplayName("순서를 바꿔도 같은 답이다")
        void shuffleGivesSameAnswer() {
            // 자리를 정하는 것이 해시뿐이고 갱신이 max 라 순서가 상관없다.
            // 이 성질이 나중에 merge 를 가능하게 한다.
            List<Integer> items = new ArrayList<>();
            for (int i = 0; i < 20_000; i++) {
                items.add(i * 3);
            }
            CardinalityEstimator a = create();
            for (int x : items) {
                a.add(x);
            }
            Collections.shuffle(items, new Random(5L));
            CardinalityEstimator b = create();
            for (int x : items) {
                b.add(x);
            }
            assertEquals(a.estimate(), b.estimate(), "순서를 바꿨더니 답이 달라졌다");
        }

        @Test
        @DisplayName("중복을 잔뜩 섞어도 종류만 센다")
        void duplicatesEverywhere() {
            CardinalityEstimator c = create();
            Random rnd = new Random(11L);
            for (int i = 0; i < 200_000; i++) {
                c.add(rnd.nextInt(5000));
            }
            assertClose(5000, c.estimate(), "5000 종류를 40배 중복해서 넣었다");
        }
    }

    @Nested
    @DisplayName("큰 카디널리티")
    class LargeCardinality {

        @Test
        @DisplayName("10만, 100만")
        void hundredThousandAndMillion() {
            for (int n : new int[]{100_000, 1_000_000}) {
                CardinalityEstimator c = create();
                for (int i = 0; i < n; i++) {
                    c.add(i);
                }
                assertClose(n, c.estimate(), n + " 개");
            }
        }
    }

    @Nested
    @DisplayName("메모리")
    class MemoryReport {

        @Test
        @DisplayName("줄지 않고, 0 이상이다")
        void monotonic() {
            CardinalityEstimator c = create();
            long prev = c.memoryBytes();
            assertTrue(prev >= 0);
            for (int i = 0; i < 5000; i++) {
                c.add(i);
                long now = c.memoryBytes();
                assertTrue(now >= prev, "메모리가 줄었다: " + prev + " -> " + now);
                prev = now;
            }
            assertTrue(prev > 0);
        }
    }

    @Nested
    @DisplayName("성능")
    class Performance {

        @Test
        @Timeout(30)
        @DisplayName("100만 번 넣기")
        void millionAdds() {
            CardinalityEstimator c = create();
            for (int i = 0; i < 1_000_000; i++) {
                c.add(i % 200_000);
            }
            assertClose(200_000, c.estimate(), "20만 종류를 100만 번");
        }
    }
}
