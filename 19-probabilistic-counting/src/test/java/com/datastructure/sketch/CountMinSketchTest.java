package com.datastructure.sketch;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("CountMinSketch: d x w 계수기 배열")
class CountMinSketchTest extends FrequencyEstimatorContractTest {

    @Override
    protected FrequencyEstimator create() {
        return new CountMinSketch(0.001, 0.01);
    }

    @Nested
    @DisplayName("크기 공식")
    class Sizing {

        @Test
        @DisplayName("w = ceil(e / epsilon)")
        void width() {
            assertEquals(272, CountMinSketch.widthFor(0.01));
            assertEquals(2719, CountMinSketch.widthFor(0.001));
            assertEquals(28, CountMinSketch.widthFor(0.1));
            assertEquals(27183, CountMinSketch.widthFor(0.0001));
            assertEquals(6, CountMinSketch.widthFor(0.5), "e/0.5 = 5.44 -> 6");
        }

        @Test
        @DisplayName("d = ceil(ln(1 / delta))")
        void depth() {
            assertEquals(5, CountMinSketch.depthFor(0.01));
            assertEquals(7, CountMinSketch.depthFor(0.001));
            assertEquals(3, CountMinSketch.depthFor(0.1));
            assertEquals(10, CountMinSketch.depthFor(0.0001));
            assertEquals(1, CountMinSketch.depthFor(0.5), "ln2 = 0.69 -> 1");
        }

        @Test
        @DisplayName("잘못된 인자를 거부한다")
        void rejectsBadArgs() {
            assertThrows(IllegalArgumentException.class, () -> CountMinSketch.widthFor(0.0));
            assertThrows(IllegalArgumentException.class, () -> CountMinSketch.widthFor(1.0));
            assertThrows(IllegalArgumentException.class, () -> CountMinSketch.widthFor(-0.1));
            assertThrows(IllegalArgumentException.class, () -> CountMinSketch.depthFor(0.0));
            assertThrows(IllegalArgumentException.class, () -> CountMinSketch.depthFor(1.0));
            assertThrows(IllegalArgumentException.class, () -> CountMinSketch.widthFor(1e-9),
                    "칸이 27억 개 필요하다. int 로 자르면 음수 배열 크기가 된다");
            assertThrows(IllegalArgumentException.class, () -> CountMinSketch.depthFor(1e-40),
                    "행이 93개 필요하다. 이 정도면 설정 실수다");
            assertThrows(IllegalArgumentException.class, () -> new CountMinSketch(0, 5, 0L));
            assertThrows(IllegalArgumentException.class, () -> new CountMinSketch(10, 0, 0L));
        }

        @Test
        @DisplayName("칸 수가 원소 종류와 무관하게 고정이다")
        void fixedSize() {
            CountMinSketch s = new CountMinSketch(0.01, 0.01);
            assertEquals(272, s.width());
            assertEquals(5, s.depth());
            long before = s.memoryBytes();
            for (int i = 0; i < 500_000; i++) {
                s.add(i);
            }
            assertEquals(before, s.memoryBytes(), "50만 종류를 넣어도 크기가 그대로다");
            assertEquals(272L * 5 * 8, before, "long 하나가 8바이트다");
        }
    }

    @Nested
    @DisplayName("이중 해싱 (11번과 같은 방식)")
    class DoubleHashing {

        @Test
        @DisplayName("같은 원소는 늘 같은 자리를 준다")
        void deterministic() {
            CountMinSketch s = new CountMinSketch(500, 5, 0L);
            assertArrayEquals(s.indexes(42), s.indexes(42));
            assertArrayEquals(s.indexes(-7), s.indexes(-7));
        }

        @Test
        @DisplayName("자리가 행 수만큼이고 전부 범위 안이다")
        void countAndRange() {
            CountMinSketch s = new CountMinSketch(500, 5, 0L);
            for (int key : new int[]{0, 1, -1, 42, Integer.MIN_VALUE, Integer.MAX_VALUE}) {
                int[] idx = s.indexes(key);
                assertEquals(5, idx.length);
                for (int i : idx) {
                    assertTrue(i >= 0 && i < s.width(),
                            "자리 " + i + " 가 범위 밖이다. floorMod 를 안 썼을 수 있다");
                }
            }
        }

        @Test
        @DisplayName("연속된 정수가 앞쪽에 몰리지 않는다")
        void spreadsSequentialKeys() {
            CountMinSketch s = new CountMinSketch(1000, 5, 0L);
            int firstTenth = 0;
            int total = 0;
            for (int key = 0; key < 1000; key++) {
                for (int i : s.indexes(key)) {
                    total++;
                    if (i < s.width() / 10) {
                        firstTenth++;
                    }
                }
            }
            double share = (double) firstTenth / total;
            assertTrue(share > 0.05 && share < 0.15,
                    "앞 10% 구간에 자리의 " + share + " 가 몰렸다. 해시를 안 섞은 것이다");
        }

        @Test
        @DisplayName("seed 가 다르면 자리가 다르다")
        void seedChangesLayout() {
            // 12번 스킵 리스트처럼 무작위를 주입받게 만들었기 때문에 이 테스트가 가능하다.
            CountMinSketch a = new CountMinSketch(500, 5, 1L);
            CountMinSketch b = new CountMinSketch(500, 5, 2L);
            int different = 0;
            for (int key = 0; key < 200; key++) {
                if (a.indexes(key)[0] != b.indexes(key)[0]) {
                    different++;
                }
            }
            assertTrue(different > 180, "seed 를 바꿨는데 자리가 " + different + "/200 만 달라졌다");
        }

        @Test
        @DisplayName("h2 가 0 이면 모든 행이 같은 칸을 본다")
        void h2ZeroCollapsesRows() {
            // 11번에서는 이 방어선을 **어떤 테스트로도 못 잡았다.** 입력이 int 42억 가지뿐이라
            // mix64 의 상위 32비트가 0 이 되는 값이 없었기 때문이다.
            //
            // 여기서는 seed 를 주입받으므로 mix64 의 입력이 64비트다.
            // mix64 를 거꾸로 풀어 상위 32비트가 0 이 되는 (원소, seed) 짝을 만들 수 있다.
            // 아래 짝이 그것이다. **h2 를 1 로 바꾸지 않으면 다섯 행이 전부 같은 칸을 본다.**
            int poisonItem = -905649768;
            long poisonSeed = 188963932365389824L;
            assertEquals(0, (int) (CountMinSketch.mix64(poisonItem ^ poisonSeed) >>> 32),
                    "이 짝은 h2 가 0 이 되도록 만들어졌다");

            CountMinSketch s = new CountMinSketch(64, 5, poisonSeed);
            int[] idx = s.indexes(poisonItem);
            boolean allSame = true;
            for (int i = 1; i < idx.length; i++) {
                if (idx[i] != idx[0]) {
                    allSame = false;
                }
            }
            assertFalse(allSame,
                    "자리 5개가 전부 " + idx[0] + " 이다. h2 가 0 이면 1 로 바꿔야 한다");
        }
    }

    @Nested
    @DisplayName("추정은 행별 값의 최소")
    class MinimumOverRows {

        @Test
        @DisplayName("실제로 행들의 최솟값이다")
        void estimateIsRowMinimum() {
            // 최대나 합, 평균이 아니라 **최소**여야 하는 이유가 여기 있다.
            // 칸은 남의 계수까지 더해 부풀어 있으므로 가장 덜 부푼 행이 가장 정확하다.
            CountMinSketch s = new CountMinSketch(50, 5, 777L);
            Random rnd = new Random(1L);
            for (int i = 0; i < 20_000; i++) {
                s.add(rnd.nextInt(5000));
            }
            int rowsDiffer = 0;
            for (int key = 0; key < 200; key++) {
                int[] idx = s.indexes(key);
                long min = Long.MAX_VALUE;
                long max = Long.MIN_VALUE;
                for (int r = 0; r < s.depth(); r++) {
                    min = Math.min(min, s.cell(r, idx[r]));
                    max = Math.max(max, s.cell(r, idx[r]));
                }
                assertEquals(min, s.estimateCount(key), "키 " + key + " 에서 최솟값이 아니다");
                if (max > min) {
                    rowsDiffer++;
                }
            }
            assertTrue(rowsDiffer > 150,
                    "행마다 값이 달라야 최소를 고르는 의미가 있는데 " + rowsDiffer + "/200 만 다르다");
        }

        @Test
        @DisplayName("행이 늘면 초과분이 줄어든다")
        void moreRowsMeansLessOvershoot() {
            Random rnd = new Random(9L);
            int[] stream = new int[200_000];
            for (int i = 0; i < stream.length; i++) {
                stream[i] = rnd.nextInt(20_000);
            }
            Map<Integer, Integer> truth = new HashMap<>();
            for (int x : stream) {
                truth.merge(x, 1, Integer::sum);
            }
            long[] avgOvershoot = new long[2];
            int[] depths = {1, 7};
            for (int t = 0; t < 2; t++) {
                CountMinSketch s = new CountMinSketch(500, depths[t], 0L);
                for (int x : stream) {
                    s.add(x);
                }
                long sum = 0;
                for (Map.Entry<Integer, Integer> e : truth.entrySet()) {
                    sum += s.estimateCount(e.getKey()) - e.getValue();
                }
                avgOvershoot[t] = sum / truth.size();
            }
            // 실측: 행 1개 400, 행 7개 314
            assertTrue(avgOvershoot[1] < avgOvershoot[0] * 0.9,
                    "행 1개 초과분 " + avgOvershoot[0] + ", 행 7개 " + avgOvershoot[1]
                            + " - 행을 늘렸는데 안 좋아졌다");
        }

        @Test
        @DisplayName("칸이 2개뿐이라 다 충돌해도 과소평가는 없다")
        void collisionsNeverUnderestimate() {
            // 최악의 스케치를 일부러 만든다. 정확도는 무너져도 **방향은 안 무너진다.**
            CountMinSketch s = new CountMinSketch(2, 1, 0L);
            Map<Integer, Long> truth = new HashMap<>();
            for (int i = 0; i < 1000; i++) {
                s.add(i, i + 1);
                truth.put(i, (long) i + 1);
            }
            for (Map.Entry<Integer, Long> e : truth.entrySet()) {
                assertTrue(s.estimateCount(e.getKey()) >= e.getValue(),
                        "칸이 2개여도 과소평가는 안 된다: " + e.getKey());
            }
        }
    }

    @Nested
    @DisplayName("오차 한계")
    class ErrorBound {

        @Test
        @DisplayName("추정 <= 실제 + epsilon x 전체개수")
        void withinBound() {
            CountMinSketch s = new CountMinSketch(0.001, 0.01);
            Map<Integer, Long> truth = new HashMap<>();
            Random rnd = new Random(7L);
            for (int i = 0; i < 500_000; i++) {
                int v = rnd.nextInt(50_000);
                s.add(v);
                truth.merge(v, 1L, Long::sum);
            }
            long bound = s.errorBound();
            assertEquals(500, bound, "epsilon 0.001 x 50만 = 500");
            long worst = 0;
            for (Map.Entry<Integer, Long> e : truth.entrySet()) {
                long over = s.estimateCount(e.getKey()) - e.getValue();
                assertTrue(over >= 0, "과소평가: " + e.getKey());
                worst = Math.max(worst, over);
            }
            // 실측 최대 초과 240. 한계 500 안에 있다.
            assertTrue(worst <= bound, "최대 초과 " + worst + " 가 한계 " + bound + " 를 넘었다");
        }

        @Test
        @DisplayName("epsilon 은 칸 수가 정한다")
        void epsilonFromWidth() {
            CountMinSketch s = new CountMinSketch(0.001, 0.01);
            assertTrue(s.epsilon() <= 0.001,
                    "올림했으니 실제 오차율은 요청보다 좋아야 한다: " + s.epsilon());
            assertTrue(s.epsilon() > 0.0009);
        }
    }

    @Nested
    @DisplayName("한계 측정: 작은 빈도에서는 못 쓴다")
    class UselessForRareItems {

        @Test
        @DisplayName("heavy hitter 는 0.2% 안에 맞고, 드문 것은 100배 넘게 틀린다")
        void heavyAccurateRareHopeless() {
            // **이것이 이 자료구조의 사용 범위를 정한다.**
            // 오차는 절대량(epsilon x 전체개수)이라 빈도가 큰 원소에는 무시할 만하고
            // 빈도가 1~3 인 원소에는 통째로 잡음이다.
            CountMinSketch s = new CountMinSketch(0.001, 0.01);
            ExactCounter exact = new ExactCounter();
            Random rnd = new Random(42L);
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 100_000; j++) {
                    s.add(i);
                    exact.add(i);
                }
            }
            for (int i = 0; i < 200_000; i++) {
                int v = 1000 + i;
                long c = 1 + rnd.nextInt(3);
                s.add(v, c);
                exact.add(v, c);
            }

            double worstHeavy = 0;
            for (int i = 0; i < 5; i++) {
                long t = exact.estimateCount(i);
                worstHeavy = Math.max(worstHeavy, (s.estimateCount(i) - t) / (double) t);
            }
            double worstRare = 0;
            for (int i = 0; i < 200_000; i++) {
                long t = exact.estimateCount(1000 + i);
                worstRare = Math.max(worstRare, (s.estimateCount(1000 + i) - t) / (double) t);
            }
            // 실측: heavy 최악 0.0014, 드문 것 최악 174배
            assertTrue(worstHeavy < 0.01, "heavy hitter 상대오차 " + worstHeavy);
            assertTrue(worstRare > 20,
                    "드문 원소 상대오차가 " + worstRare + " 배다. 이건 고칠 수 없는 성질이다");
        }

        @Test
        @DisplayName("한 번도 안 넣은 원소가 0 이 아닐 수 있다")
        void absentItemsCanBeNonZero() {
            // 11번의 오탐이 여기서는 "없는 원소의 빈도가 0 이 아니다"로 나타난다.
            // **원소를 담지 않았으므로 되찾을 수도, 구별할 수도 없다.**
            CountMinSketch s = new CountMinSketch(64, 3, 0L);
            for (int i = 0; i < 10_000; i++) {
                s.add(i);
            }
            int nonZero = 0;
            for (int i = 100_000_000; i < 100_001_000; i++) {
                if (s.estimateCount(i) > 0) {
                    nonZero++;
                }
            }
            assertEquals(1000, nonZero, "칸이 꽉 차면 안 넣은 것도 전부 0 이 아니게 된다");
        }
    }
}
