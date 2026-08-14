package com.datastructure.sparsetable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * SparseTable 계약. 세 구현(min, max, gcd)이 똑같이 지켜야 하는 것만 여기 있다.
 *
 * 셋 다 멱등이라는 것이 이 계약의 전제다. f(x, x) = x 가 아니면 여기 못 들어온다.
 * 합이 왜 못 들어오는지는 IdempotenceTest 가 본다.
 */
abstract class SparseTableContractTest {

    protected abstract SparseTable create(long[] values);

    /** 느린 참조 구현. 그냥 훑는다. 모든 구간을 이것과 대조하는 것이 이 문제집의 주력 검증이다. */
    protected abstract long naive(long[] values, int from, int to);

    protected abstract long identityValue();

    /**
     * 결정적 난수. Random 을 쓰면 시드를 바꿀 때 기댓값이 흔들린다.
     * 여기서는 대조 대상이 naive 뿐이라 값 자체는 아무래도 좋고, 재현되는 것만 중요하다.
     * gcd 도 함께 쓰므로 1 이상의 양수만 만든다.
     */
    protected static long[] deterministic(int n, long seed) {
        long[] a = new long[n];
        long x = seed;
        for (int i = 0; i < n; i++) {
            x = x * 6364136223846793005L + 1442695040888963407L;
            a[i] = Math.floorMod(x >>> 33, 720) + 1;
        }
        return a;
    }

    @Nested
    @DisplayName("기본")
    class Basics {

        @Test
        @DisplayName("모든 구간이 맞다")
        void everyRange() {
            long[] a = {5, 2, 8, 1, 9, 3};
            SparseTable t = create(a);
            for (int from = 0; from < a.length; from++) {
                for (int to = from; to < a.length; to++) {
                    assertEquals(naive(a, from, to), t.query(from, to),
                            "구간 [" + from + ", " + to + "]");
                }
            }
        }

        @Test
        @DisplayName("원소가 하나")
        void singleton() {
            SparseTable t = create(new long[]{42});
            assertEquals(1, t.size());
            assertEquals(42, t.query(0, 0));
            assertEquals(42, t.get(0));
            assertEquals(1, t.levels(), "n=1 이면 레벨은 0층 하나뿐이다");
        }

        @Test
        @DisplayName("길이 1 구간은 원소 그대로다")
        void lengthOneRange() {
            // **여기가 멱등성이 제일 먼저 드러나는 자리다.**
            // 길이 1 이면 k=0 이라 두 창이 완전히 겹친다. combine(a[i], a[i]) 를 부른다.
            long[] a = deterministic(37, 11L);
            SparseTable t = create(a);
            for (int i = 0; i < a.length; i++) {
                assertEquals(a[i], t.query(i, i), "구간 [" + i + ", " + i + "]");
            }
        }

        @Test
        @DisplayName("get 은 원래 값을 그대로 돌려준다")
        void getReturnsOriginal() {
            long[] a = deterministic(20, 7L);
            SparseTable t = create(a);
            for (int i = 0; i < a.length; i++) {
                assertEquals(a[i], t.get(i), i + "번");
            }
            assertEquals(20, t.size());
        }

        @Test
        @DisplayName("from > to 면 항등원")
        void reversedRangeIsIdentity() {
            SparseTable t = create(new long[]{5, 7, 9});
            assertEquals(identityValue(), t.query(2, 0));
            assertEquals(identityValue(), t.query(1, 0));
        }

        @Test
        @DisplayName("잘못된 인자")
        void badArgs() {
            assertThrows(IllegalArgumentException.class, () -> create(new long[0]));
            assertThrows(IllegalArgumentException.class, () -> create(null));
            SparseTable t = create(new long[]{1, 2});
            assertThrows(IndexOutOfBoundsException.class, () -> t.query(0, 2));
            assertThrows(IndexOutOfBoundsException.class, () -> t.query(-1, 1));
            assertThrows(IndexOutOfBoundsException.class, () -> t.get(2));
        }

        @Test
        @DisplayName("원본 배열을 나중에 고쳐도 흔들리지 않는다")
        void defensiveCopy() {
            long[] a = {5, 2, 8, 1};
            SparseTable t = create(a);
            long before = t.query(0, 3);
            a[0] = -999;
            a[3] = 12345;
            assertEquals(before, t.query(0, 3), "생성자에서 복사해두지 않으면 여기서 어긋난다");
            assertEquals(5, t.get(0));
        }
    }

    @Nested
    @DisplayName("전수 대조")
    class Exhaustive {

        @Test
        @DisplayName("n=1..60 의 모든 구간이 느린 구현과 같다")
        void everyRangeOfEverySize() {
            // **경계 버그는 전부 여기서 잡힌다.**
            // k = floor(log2(len)) 의 +1 하나, 오른쪽 창 시작점 r - 2^k + 1 의 +1 하나가
            // 이 문제에서 제일 흔한 실수다. 길이 1, 2, 3 과 2의 거듭제곱 근처에서 드러난다.
            for (int n = 1; n <= 60; n++) {
                long[] a = deterministic(n, 1000L + n);
                SparseTable t = create(a);
                for (int from = 0; from < n; from++) {
                    for (int to = from; to < n; to++) {
                        assertEquals(naive(a, from, to), t.query(from, to),
                                "n=" + n + " 구간 [" + from + ", " + to + "]");
                    }
                }
            }
        }

        @Test
        @DisplayName("같은 값만 있어도 맞다")
        void allEqual() {
            for (int n : new int[]{1, 2, 3, 7, 8, 9, 33}) {
                long[] a = new long[n];
                java.util.Arrays.fill(a, 12L);
                SparseTable t = create(a);
                for (int from = 0; from < n; from++) {
                    for (int to = from; to < n; to++) {
                        assertEquals(naive(a, from, to), t.query(from, to), "n=" + n);
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName("멱등성")
    class Idempotence {

        @Test
        @DisplayName("combine(x, x) = x 다")
        void combineWithItself() {
            // **이 성질이 없으면 이 자료구조를 쓸 수 없다.**
            // 두 창이 겹치기 때문이다. 겹친 자리는 두 번 접히는데,
            // 두 번 접어도 한 번 접은 것과 같아야 답이 맞는다.
            SparseTable t = create(new long[]{1});
            for (long x : new long[]{1, 2, 7, 100, 1_000_000_007L, 12, 360}) {
                assertEquals(x, t.combine(x, x), x + " 을 자기와 접으면 자기가 나와야 한다");
            }
        }

        @Test
        @DisplayName("항등원과 접으면 그대로다")
        void identityIsNeutral() {
            SparseTable t = create(new long[]{1});
            for (long x : new long[]{1, 5, 42, 720}) {
                assertEquals(x, t.combine(x, t.identity()), "항등원이 값을 바꾸면 안 된다");
                assertEquals(x, t.combine(t.identity(), x), "항등원은 양쪽 다 중립이어야 한다");
            }
        }

        @Test
        @DisplayName("결합법칙도 여전히 필요하다")
        void stillAssociative() {
            // 멱등성이 결합법칙을 대신하는 것이 아니다. **둘 다 있어야 한다.**
            // 13번에서 요구한 것 위에 하나가 더 얹힌 것이다.
            SparseTable t = create(new long[]{1});
            long[] xs = {3, 12, 25, 100, 7};
            for (long x : xs) {
                for (long y : xs) {
                    for (long z : xs) {
                        assertEquals(t.combine(t.combine(x, y), z), t.combine(x, t.combine(y, z)),
                                x + ", " + y + ", " + z);
                    }
                }
            }
        }
    }
}
