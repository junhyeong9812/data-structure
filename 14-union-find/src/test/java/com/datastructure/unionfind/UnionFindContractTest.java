package com.datastructure.unionfind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/** UnionFind 계약. 두 구현이 똑같이 지켜야 하는 것만 여기 있다. */
abstract class UnionFindContractTest {

    /** 0 부터 n-1 까지를 각자 혼자짜리 묶음으로 준비한다. */
    protected abstract UnionFind create(int n);

    @Nested
    @DisplayName("처음 상태")
    class Initial {

        @Test
        @DisplayName("전부 따로 있다")
        void allSeparate() {
            UnionFind uf = create(5);
            assertEquals(5, uf.size());
            assertEquals(5, uf.componentCount());
            for (int i = 0; i < 5; i++) {
                assertEquals(1, uf.sizeOf(i));
                for (int j = 0; j < 5; j++) {
                    assertEquals(i == j, uf.connected(i, j), i + " 와 " + j);
                }
            }
        }

        @Test
        @DisplayName("자기 자신과는 늘 연결돼 있다")
        void selfConnected() {
            UnionFind uf = create(3);
            for (int i = 0; i < 3; i++) {
                assertTrue(uf.connected(i, i));
            }
            assertFalse(uf.union(1, 1), "자기와 합치는 것은 변화가 없다");
            assertEquals(3, uf.componentCount());
        }
    }

    @Nested
    @DisplayName("합치기")
    class Union {

        @Test
        @DisplayName("합치면 연결된다")
        void unionConnects() {
            UnionFind uf = create(5);
            assertTrue(uf.union(0, 1));
            assertTrue(uf.connected(0, 1));
            assertEquals(4, uf.componentCount());
            assertEquals(2, uf.sizeOf(0));
            assertEquals(2, uf.sizeOf(1));
            assertFalse(uf.connected(0, 2));
        }

        @Test
        @DisplayName("이미 같으면 false")
        void alreadyTogether() {
            UnionFind uf = create(5);
            assertTrue(uf.union(0, 1));
            assertFalse(uf.union(0, 1));
            assertFalse(uf.union(1, 0), "방향이 바뀌어도 마찬가지다");
            assertEquals(4, uf.componentCount(), "묶음 수가 더 줄면 안 된다");
        }

        @Test
        @DisplayName("연결은 전이된다")
        void transitive() {
            UnionFind uf = create(5);
            uf.union(0, 1);
            uf.union(1, 2);
            assertTrue(uf.connected(0, 2), "직접 합친 적이 없어도 연결돼 있다");
            assertEquals(3, uf.sizeOf(0));
            assertEquals(3, uf.componentCount());
        }

        @Test
        @DisplayName("두 묶음을 통째로 합친다")
        void mergesGroups() {
            UnionFind uf = create(6);
            uf.union(0, 1);
            uf.union(1, 2);
            uf.union(3, 4);
            assertEquals(3, uf.componentCount());
            assertTrue(uf.union(2, 3));
            assertEquals(2, uf.componentCount());
            assertEquals(5, uf.sizeOf(0));
            assertTrue(uf.connected(0, 4));
            assertFalse(uf.connected(0, 5));
        }

        @Test
        @DisplayName("전부 합치면 묶음이 하나")
        void allInOne() {
            UnionFind uf = create(100);
            for (int i = 1; i < 100; i++) {
                assertTrue(uf.union(0, i));
            }
            assertEquals(1, uf.componentCount());
            assertEquals(100, uf.sizeOf(50));
            for (int i = 0; i < 100; i++) {
                assertEquals(uf.find(0), uf.find(i), "대표가 같아야 한다");
            }
        }
    }

    @Nested
    @DisplayName("find 는 대표를 준다")
    class Representative {

        @Test
        @DisplayName("같은 묶음이면 같은 대표")
        void sameGroupSameRoot() {
            UnionFind uf = create(6);
            uf.union(0, 1);
            uf.union(2, 3);
            assertEquals(uf.find(0), uf.find(1));
            assertEquals(uf.find(2), uf.find(3));
            assertTrue(uf.find(0) != uf.find(2));
        }

        @Test
        @DisplayName("여러 번 불러도 같은 답")
        void stable() {
            UnionFind uf = create(10);
            uf.union(3, 7);
            int r = uf.find(3);
            for (int i = 0; i < 100; i++) {
                assertEquals(r, uf.find(3), "경로 압축이 대표를 바꾸면 안 된다");
                assertEquals(r, uf.find(7));
            }
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class CrossCheck {

        @Test
        @DisplayName("느린 참조 구현과 계속 같다")
        void matchesNaive() {
            // 참조 구현: 색칠하기. 합칠 때마다 한쪽 색을 전부 다른 색으로 바꾼다. O(n) 이지만 확실하다.
            Random rnd = new Random(20260813L);
            int n = 200;
            UnionFind uf = create(n);
            int[] color = new int[n];
            for (int i = 0; i < n; i++) {
                color[i] = i;
            }

            for (int step = 0; step < 3000; step++) {
                int a = rnd.nextInt(n);
                int b = rnd.nextInt(n);
                if (rnd.nextInt(3) == 0) {
                    boolean expected = color[a] != color[b];
                    if (expected) {
                        int from = color[b];
                        int to = color[a];
                        for (int i = 0; i < n; i++) {
                            if (color[i] == from) {
                                color[i] = to;
                            }
                        }
                    }
                    assertEquals(expected, uf.union(a, b), "union step " + step);
                } else {
                    assertEquals(color[a] == color[b], uf.connected(a, b), "connected step " + step);
                }

                Map<Integer, Integer> counts = new HashMap<>();
                for (int i = 0; i < n; i++) {
                    counts.merge(color[i], 1, Integer::sum);
                }
                assertEquals(counts.size(), uf.componentCount(), "묶음 수가 갈렸다 (step " + step + ")");
                assertEquals(counts.get(color[a]).intValue(), uf.sizeOf(a),
                        "묶음 크기가 갈렸다 (step " + step + ")");
            }
        }
    }

    @Nested
    @DisplayName("성능")
    class Performance {

        @Test
        @Timeout(20)
        @DisplayName("한 줄로 이어도 조회가 안 느려진다")
        void chainStaysFast() {
            // 0-1, 1-2, 2-3 ... 순서로 합치면 최적화가 없을 때 한 줄짜리 나무가 된다.
            int n = 200_000;
            UnionFind uf = create(n);
            for (int i = 1; i < n; i++) {
                uf.union(i - 1, i);
            }
            assertEquals(1, uf.componentCount());
            for (int round = 0; round < 20; round++) {
                for (int i = 0; i < n; i += 3) {
                    assertTrue(uf.connected(i, 0));
                }
            }
            assertEquals(n, uf.sizeOf(n / 2));
        }
    }
}
