package com.datastructure.segment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("GenericSegmentTree: 결합 함수를 인자로")
class GenericSegmentTreeTest {

    @Nested
    @DisplayName("같은 클래스로 여러 연산")
    class ManyOperations {

        @Test
        @DisplayName("합")
        void sum() {
            GenericSegmentTree<Long> t = new GenericSegmentTree<>(
                    List.of(1L, 2L, 3L, 4L), 0L, Long::sum);
            assertEquals(10L, t.query(0, 3));
            assertEquals(5L, t.query(1, 2));
            t.update(0, 100L);
            assertEquals(109L, t.query(0, 3));
        }

        @Test
        @DisplayName("최대")
        void max() {
            GenericSegmentTree<Long> t = new GenericSegmentTree<>(
                    List.of(5L, 2L, 9L, 1L), Long.MIN_VALUE, Math::max);
            assertEquals(9L, t.query(0, 3));
            assertEquals(2L, t.query(1, 1));
            assertEquals(Long.MIN_VALUE, t.query(3, 0), "빈 구간은 항등원");
        }

        @Test
        @DisplayName("문자열 이어붙이기")
        void concat() {
            // T 가 숫자일 필요가 없다. 결합법칙과 항등원만 있으면 된다.
            GenericSegmentTree<String> t = new GenericSegmentTree<>(
                    List.of("a", "b", "c", "d"), "", String::concat);
            assertEquals("abcd", t.query(0, 3));
            assertEquals("bc", t.query(1, 2));
            t.update(1, "X");
            assertEquals("aXcd", t.query(0, 3));

            // 이어붙이기는 교환법칙이 없다. **결합법칙만 있으면 된다는 것을 보여준다.**
            assertEquals("Xc", t.query(1, 2));
        }

        @Test
        @DisplayName("집합 합집합")
        void unionOfSets() {
            List<Set<Integer>> init = List.of(Set.of(1, 2), Set.of(2, 3), Set.of(9));
            GenericSegmentTree<Set<Integer>> t = new GenericSegmentTree<>(
                    init, Set.of(), (x, y) -> {
                        Set<Integer> merged = new LinkedHashSet<>(x);
                        merged.addAll(y);
                        return merged;
                    });
            assertEquals(new TreeSet<>(List.of(1, 2, 3, 9)), new TreeSet<>(t.query(0, 2)));
            assertEquals(new TreeSet<>(List.of(2, 3, 9)), new TreeSet<>(t.query(1, 2)));
        }
    }

    @Nested
    @DisplayName("평균은 결합법칙이 없다")
    class AverageIsNotAssociative {

        @Test
        @DisplayName("평균을 그대로 접으면 틀린다")
        void naiveAverageIsWrong() {
            // 원소가 4개면 좌우가 2개씩이라 **우연히 맞는다.** (1.5 + 3.5)/2 = 2.5.
            // 3개면 좌우가 2개와 1개라 어긋난다. (1.5 + 3)/2 = 2.25 인데 진짜 평균은 2 다.
            // **결합법칙이 없는 연산은 세그먼트 트리에 넣을 수 없다.**
            GenericSegmentTree<Double> even = new GenericSegmentTree<>(
                    List.of(1.0, 2.0, 3.0, 4.0), 0.0, (a, b) -> (a + b) / 2);
            assertEquals(2.5, even.query(0, 3), 1e-9, "4개는 우연히 맞는다. 그래서 더 위험하다");

            GenericSegmentTree<Double> odd = new GenericSegmentTree<>(
                    List.of(1.0, 2.0, 3.0), 0.0, (a, b) -> (a + b) / 2);
            double got = odd.query(0, 2);
            assertEquals(2.25, got, 1e-9, "쪼갠 크기가 다르면 어긋난다");
            assertTrue(Math.abs(got - 2.0) > 0.1,
                    "진짜 평균은 2 인데 " + got + " 가 나온다. 접는 순서가 답을 바꾼다");
        }

        @Test
        @DisplayName("합과 개수를 같이 들고 다니면 된다")
        void carrySumAndCount() {
            // 평균 자체는 결합법칙이 없지만 **합과 개수는 있다.** 마지막에 나누면 된다.
            // MinMaxSegmentTree 와 같은 발상이다.
            record SumCount(double sum, int count) {
            }
            GenericSegmentTree<SumCount> t = new GenericSegmentTree<>(
                    List.of(new SumCount(1, 1), new SumCount(2, 1),
                            new SumCount(3, 1), new SumCount(4, 1)),
                    new SumCount(0, 0),
                    (a, b) -> new SumCount(a.sum() + b.sum(), a.count() + b.count()));

            SumCount whole = t.query(0, 3);
            assertEquals(2.5, whole.sum() / whole.count(), 1e-9);
            SumCount part = t.query(1, 2);
            assertEquals(2.5, part.sum() / part.count(), 1e-9);

            t.update(3, new SumCount(100, 1));
            SumCount after = t.query(0, 3);
            assertEquals(106.0 / 4, after.sum() / after.count(), 1e-9);
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class CrossCheck {

        @Test
        @DisplayName("SumSegmentTree 와 같은 답을 낸다")
        void agreesWithTypedVersion() {
            Random rnd = new Random(2024L);
            int n = 150;
            long[] a = new long[n];
            List<Long> boxed = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                a[i] = rnd.nextInt(200) - 100;
                boxed.add(a[i]);
            }
            SumSegmentTree typed = new SumSegmentTree(a);
            GenericSegmentTree<Long> generic = new GenericSegmentTree<>(boxed, 0L, Long::sum);

            for (int step = 0; step < 600; step++) {
                if (rnd.nextBoolean()) {
                    int idx = rnd.nextInt(n);
                    long v = rnd.nextInt(200) - 100;
                    typed.update(idx, v);
                    generic.update(idx, v);
                } else {
                    int from = rnd.nextInt(n);
                    int to = from + rnd.nextInt(n - from);
                    assertEquals(typed.query(from, to), generic.query(from, to),
                            "step " + step);
                }
            }
        }
    }

    @Nested
    @DisplayName("잘못된 인자")
    class BadArgs {

        @Test
        @DisplayName("거부한다")
        void rejects() {
            assertThrows(IllegalArgumentException.class,
                    () -> new GenericSegmentTree<>(List.<Long>of(), 0L, Long::sum));
            assertThrows(IllegalArgumentException.class,
                    () -> new GenericSegmentTree<>(List.of(1L), null, Long::sum));
            assertThrows(IllegalArgumentException.class,
                    () -> new GenericSegmentTree<Long>(List.of(1L), 0L, null));
            GenericSegmentTree<Long> t = new GenericSegmentTree<>(List.of(1L, 2L), 0L, Long::sum);
            assertThrows(IllegalArgumentException.class, () -> t.update(0, null));
            assertThrows(IndexOutOfBoundsException.class, () -> t.query(0, 5));
        }
    }
}
