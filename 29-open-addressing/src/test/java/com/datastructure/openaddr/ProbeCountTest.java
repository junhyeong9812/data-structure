package com.datastructure.openaddr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * 한계 측정. 이 박스의 값어치가 여기 있다.
 *
 * 시간을 재지 않는다. 기계와 JIT 에 흔들리기 때문이다. 대신 들여다본 칸 수를 센다.
 * 결정적이고, 같은 수가 언제나 나온다. 05번에서 "체이닝 120ms 대 선형 탐사 75초"라고 적었던 것을
 * 여기서는 "8,006,000 대 8,000"이라고 적는다. 같은 이야기인데 흔들리지 않는다.
 *
 * 다섯 구현에 같은 입력을 주고 나란히 잰다. 기댓값은 파이썬 참조 구현으로 검산했다.
 */
@DisplayName("탐사 횟수 측정")
class ProbeCountTest {

    /** 연속된 정수 키를 담을 때 리사이즈가 끼면 측정이 흐려진다. 넉넉한 고정 용량을 쓴다. */
    private static final int BIG = 16_384;
    private static final int N = 4_000;

    private static List<ProbeMap<Integer, String>> five(int capacity, double maxLoad) {
        return List.of(
                new LinearProbeMap<>(capacity, maxLoad),
                new QuadraticProbeMap<>(capacity, maxLoad),
                new DoubleHashMap<>(capacity, maxLoad),
                new RobinHoodMap<>(capacity, maxLoad),
                new CuckooHashMap<>(capacity, maxLoad));
    }

    private static final List<String> NAMES = List.of("선형", "이차", "이중", "로빈후드", "쿠쿠");

    /** 서로 다른 무작위 키 n 개. 같은 seed 면 언제나 같은 목록이다. */
    private static List<Integer> distinctKeys(int n, long seed) {
        Random random = new Random(seed);
        Set<Integer> seen = new HashSet<>();
        List<Integer> keys = new ArrayList<>(n);
        while (keys.size() < n) {
            int key = random.nextInt(1 << 24);
            if (seen.add(key)) keys.add(key);
        }
        return keys;
    }

    /** 절대 담기지 않은 키 n 개. 위 키들은 2^24 미만이므로 그 위를 쓴다. */
    private static List<Integer> missingKeys(int n, long seed) {
        Random random = new Random(seed);
        List<Integer> keys = new ArrayList<>(n);
        for (int i = 0; i < n; i++) keys.add((1 << 24) + random.nextInt(1 << 24));
        return keys;
    }

    private static long totalProbes(ProbeMap<Integer, String> map, List<Integer> lookups) {
        long total = 0;
        for (Integer key : lookups) {
            map.get(key);
            total += map.lastProbeCount();
        }
        return total;
    }

    private static int worstProbe(ProbeMap<Integer, String> map, List<Integer> lookups) {
        int worst = 0;
        for (Integer key : lookups) {
            map.get(key);
            worst = Math.max(worst, map.lastProbeCount());
        }
        return worst;
    }

    @Nested
    @DisplayName("측정 1: 연속된 정수 키의 군집화")
    class Clustering {

        /** 키 0..N-1 을 담는다. 정수의 hashCode 는 값 그대로라 슬롯 0..N-1 에 빈틈없이 들어찬다. */
        private ProbeMap<Integer, String> filled(ProbeMap<Integer, String> map) {
            for (int i = 0; i < N; i++) map.put(i, "v");
            assertEquals(N, map.size());
            assertEquals(BIG, map.capacity(), "리사이즈가 일어나면 이 측정이 무의미해진다");
            return map;
        }

        @Test
        @DisplayName("있는 키만 보면 다섯이 완전히 같다")
        void hitsLookIdentical() {
            // 여기가 함정이다. 있는 키 조회로는 아무 차이도 안 보인다.
            // 충돌이 한 번도 안 났기 때문이다. 그래서 이 측정만 보고 "괜찮다"고 하면 안 된다.
            List<Integer> present = new ArrayList<>();
            for (int i = 0; i < N; i++) present.add(i);

            for (int i = 0; i < 5; i++) {
                ProbeMap<Integer, String> map = filled(five(BIG, 1.0).get(i));
                assertEquals(N, totalProbes(map, present), NAMES.get(i) + " 의 있는 키 조회");
                assertEquals(1, map.maxProbeCount(), NAMES.get(i) + " 의 최대 탐사");
            }
        }

        @Test
        @DisplayName("없는 키 조회에서 1000배가 갈린다")
        @Timeout(120)
        void missProbeTable() {
            // BIG + i 는 담기지 않은 키인데 홈은 i 다. 즉 덩어리 한가운데로 떨어진다.
            // 05번에서 시간으로 봤던 그 상황이고, 실무 해시맵이 해시를 섞는 이유다.
            List<Integer> misses = new ArrayList<>();
            for (int i = 0; i < N; i++) misses.add(BIG + i);

            long[] total = new long[5];
            int[] worst = new int[5];
            for (int i = 0; i < 5; i++) {
                ProbeMap<Integer, String> map = filled(five(BIG, 1.0).get(i));
                total[i] = totalProbes(map, misses);
                worst[i] = worstProbe(map, misses);
            }
            for (int i = 0; i < 5; i++) {
                System.out.printf("  %-8s 총 탐사 %,12d   최악 %,6d%n", NAMES.get(i), total[i], worst[i]);
            }

            assertEquals(8_006_000L, total[0], "선형 탐사");
            assertEquals(242_520L, total[1], "이차 탐사");
            assertEquals(13_784L, total[2], "이중 해싱");
            assertEquals(8_000L, total[3], "로빈후드");
            assertEquals(8_000L, total[4], "쿠쿠");

            assertEquals(N + 1, worst[0], "선형 탐사는 덩어리 끝까지 걷는다");
            assertEquals(2, worst[3], "로빈후드는 두 칸이면 없다는 것을 안다");
            assertEquals(2, worst[4], "쿠쿠는 자리가 둘뿐이다");
            assertTrue(total[0] > 1000 * total[3],
                    "선형 " + total[0] + " 대 로빈후드 " + total[3]);
        }

        @Test
        @DisplayName("로빈후드가 이기는 이유는 뺏기가 아니라 조기 종료다")
        void robinHoodWinsByStoppingEarly() {
            // 이 배치에서는 뺏기가 한 번도 일어나지 않는다. 충돌이 없기 때문이다.
            // 그런데도 없는 키 조회가 2 칸이면 끝난다. 조기 종료 규칙만으로 나오는 이득이다.
            RobinHoodMap<Integer, String> robin = new RobinHoodMap<>(BIG, 1.0);
            for (int i = 0; i < N; i++) robin.put(i, "v");
            for (int i = 0; i < robin.capacity(); i++) {
                if (robin.keys[i] != null) {
                    assertEquals(0, robin.distanceOf(i), "칸 " + i + " 은 자기 홈이어야 한다");
                }
            }
            robin.get(BIG);
            assertEquals(2, robin.lastProbeCount());
        }
    }

    @Nested
    @DisplayName("측정 2: 로빈후드는 평균이 아니라 최댓값을 낮춘다")
    class MaxProbeDistance {

        @Test
        @DisplayName("총 탐사는 선형 탐사와 정확히 같고 최댓값만 다르다")
        void sameTotalDifferentTail() {
            // 로빈후드는 자리를 바꿔 앉히기만 한다. 걸어야 하는 총 거리는 보존된다.
            // 그래서 있는 키 조회의 총합이 선형 탐사와 글자 그대로 같다. 줄어드는 것은 꼬리다.
            for (double load : new double[] {0.5, 0.7, 0.9}) {
                int capacity = 4096;
                int n = (int) (capacity * load);
                List<Integer> keys = distinctKeys(n, 20250814L);

                LinearProbeMap<Integer, String> linear = new LinearProbeMap<>(capacity, 0.95);
                RobinHoodMap<Integer, String> robin = new RobinHoodMap<>(capacity, 0.95);
                for (Integer key : keys) {
                    linear.put(key, "v");
                    robin.put(key, "v");
                }
                assertEquals(capacity, linear.capacity());
                assertEquals(capacity, robin.capacity());

                long linearTotal = totalProbes(linear, keys);
                long robinTotal = totalProbes(robin, keys);
                assertEquals(linearTotal, robinTotal,
                        "부하율 " + load + " 에서 총 탐사가 달라졌다. 로빈후드는 재배치일 뿐이다");
                assertTrue(robin.maxProbeCount() < linear.maxProbeCount(),
                        "부하율 " + load + ": 최악 " + linear.maxProbeCount()
                                + " -> " + robin.maxProbeCount());
                System.out.printf("  부하 %.1f  총 탐사 %,9d (양쪽 같음)  최악 선형 %,4d 로빈후드 %,4d%n",
                        load, linearTotal, linear.maxProbeCount(), robin.maxProbeCount());
            }
        }
    }

    @Nested
    @DisplayName("측정 3: 부하율이 0.9 가 되면 무슨 일이 일어나는가")
    class LoadFactorCollapse {

        @Test
        @DisplayName("0.5, 0.7, 0.9 의 평균과 최악")
        @Timeout(120)
        void collapse() {
            int capacity = 4096;
            List<Integer> misses = missingKeys(2000, 20250815L);
            double[] missAverage = new double[3];
            int[] linearWorst = new int[3];

            for (int row = 0; row < 3; row++) {
                double load = new double[] {0.5, 0.7, 0.9}[row];
                int n = (int) (capacity * load);
                List<Integer> keys = distinctKeys(n, 20250814L);
                for (int i = 0; i < 4; i++) {           // 쿠쿠는 이 부하율을 못 견딘다. 측정 4에서 따로 본다
                    ProbeMap<Integer, String> map = five(capacity, 0.95).get(i);
                    for (Integer key : keys) map.put(key, "v");
                    assertEquals(capacity, map.capacity(), NAMES.get(i) + " 가 리사이즈됐다");

                    long hitTotal = totalProbes(map, keys);
                    int hitWorst = map.maxProbeCount();
                    long missTotal = totalProbes(map, misses);
                    int missWorst = worstProbe(map, misses);
                    System.out.printf("  부하 %.1f  %-8s 적중 평균 %5.2f 최악 %,4d   실패 평균 %6.2f 최악 %,4d%n",
                            load, NAMES.get(i), (double) hitTotal / n, hitWorst,
                            (double) missTotal / misses.size(), missWorst);
                    if (i == 0) {
                        missAverage[row] = (double) missTotal / misses.size();
                        linearWorst[row] = missWorst;
                    }
                }
            }

            assertTrue(missAverage[0] < 3, "부하율 0.5 에서는 없는 키도 평균 " + missAverage[0] + " 칸이면 끝난다");
            assertTrue(missAverage[2] > 10 * missAverage[0],
                    "0.5 에서 " + missAverage[0] + " 이던 것이 0.9 에서 " + missAverage[2] + " 가 된다");
            assertTrue(linearWorst[2] > 200,
                    "부하율 0.9 의 선형 탐사는 없는 키 하나에 " + linearWorst[2] + " 칸까지 본다");
        }

        @Test
        @DisplayName("0.9 에서도 로빈후드의 최악은 두 자릿수에 머문다")
        void robinHoodKeepsTheTailShort() {
            int capacity = 4096;
            List<Integer> keys = distinctKeys((int) (capacity * 0.9), 20250814L);
            LinearProbeMap<Integer, String> linear = new LinearProbeMap<>(capacity, 0.95);
            RobinHoodMap<Integer, String> robin = new RobinHoodMap<>(capacity, 0.95);
            for (Integer key : keys) {
                linear.put(key, "v");
                robin.put(key, "v");
            }

            assertTrue(linear.maxProbeCount() > 200, "선형 최악 " + linear.maxProbeCount());
            assertTrue(robin.maxProbeCount() < 30, "로빈후드 최악 " + robin.maxProbeCount());
            assertTrue(linear.maxProbeCount() > 10 * robin.maxProbeCount(),
                    linear.maxProbeCount() + " 대 " + robin.maxProbeCount());
        }
    }

    @Nested
    @DisplayName("측정 4: 쿠쿠의 조회는 상수, 삽입은 비싸다")
    class CuckooCost {

        @Test
        @DisplayName("부하율 0.45 까지 채워도 조회는 두 칸이다")
        @Timeout(60)
        void lookupStaysConstant() {
            CuckooHashMap<Integer, String> map = new CuckooHashMap<>(4096, 0.45);
            List<Integer> keys = distinctKeys(1800, 20250814L);
            for (Integer key : keys) map.put(key, "v");

            assertTrue(map.loadFactor() >= 0.43, "부하율 " + map.loadFactor());
            assertEquals(4096, map.capacity(), "여기까지는 안 커져야 한다");
            assertEquals(2, map.maxProbeCount(), "담긴 키 전부의 최악이 2 다");
            assertEquals(2 * 2000, totalProbes(map, missingKeys(2000, 20250815L)),
                    "없는 키는 언제나 정확히 두 칸이다");
        }

        @Test
        @DisplayName("고리는 부하율 0.5 언저리에서 터진다")
        @Timeout(60)
        void cyclesExplodeNearHalfLoad() {
            // 뺏기 연쇄가 MAX_KICKS 를 넘으면 고리로 보고 전부 다시 넣는다.
            // 자리가 두 개뿐인 구조의 한계 지점이 어디인지 재본다.
            CuckooHashMap<Integer, String> map = new CuckooHashMap<>(1024, 0.999);
            Random random = new Random(99);
            Set<Integer> seen = new HashSet<>();
            int inserted = 0;
            // 상한을 둔다. 고리가 안 나는 구현을 만나면 이 반복이 테이블을 무한정 키운다.
            while (map.cycleRehashCount() == 0 && inserted < 1024) {
                int key = random.nextInt(1 << 24);
                if (!seen.add(key)) continue;
                map.put(key, "v");
                if (map.cycleRehashCount() == 0) inserted++;
            }
            assertTrue(inserted < 1024, "칸을 다 채울 때까지 고리가 한 번도 안 났다");

            double loadAtFirstCycle = inserted / 1024.0;
            System.out.printf("  첫 고리: %d 개째, 부하율 %.3f, 그때까지의 뺏기 %d 회%n",
                    inserted, loadAtFirstCycle, map.kickCount());
            assertTrue(loadAtFirstCycle > 0.4 && loadAtFirstCycle < 0.6,
                    "부하율 " + loadAtFirstCycle + " 에서 처음 고리가 났다");
            assertEquals(540, inserted, "seed 를 고정했으니 언제나 같은 자리에서 터진다");
        }

        @Test
        @DisplayName("삽입 비용은 부하율을 따라 오른다")
        @Timeout(60)
        void insertGetsExpensive() {
            // 조회가 상수인 대가가 여기 있다. 같은 1000개를 넣는데 뺏기 횟수가 부하율에 따라 다르다.
            long lowLoadKicks = kicksToFill(8192, 1000);
            long highLoadKicks = kicksToFill(2048, 1000);
            System.out.printf("  1000개 삽입의 뺏기 횟수: 용량 8192 에서 %d, 용량 2048 에서 %d%n",
                    lowLoadKicks, highLoadKicks);
            assertTrue(highLoadKicks > 5 * lowLoadKicks,
                    "부하율이 오르면 뺏기가 급증해야 한다: " + lowLoadKicks + " 대 " + highLoadKicks);
        }

        private long kicksToFill(int capacity, int n) {
            CuckooHashMap<Integer, String> map = new CuckooHashMap<>(capacity, 0.999);
            for (Integer key : distinctKeys(n, 7L)) map.put(key, "v");
            assertEquals(n, map.size());
            return map.kickCount();
        }
    }

    @Nested
    @DisplayName("측정 5: tombstone 대 backward shift")
    class TombstoneVersusShift {

        /**
         * 400개를 담아둔 채로 하나 지우고 하나 넣기를 2만 번 반복한다.
         * 키는 문자열이라 해시가 고르게 흩어진다. 군집화를 빼고 지운 자리의 효과만 본다.
         */
        private long[] churn(ProbeMap<String, String> map) {
            List<String> alive = new ArrayList<>();
            int next = 0;
            for (int i = 0; i < 400; i++) {
                map.put("k" + next, "v");
                alive.add("k" + next);
                next++;
            }
            Random random = new Random(7);
            long total = 0;
            for (int round = 0; round < 20_000; round++) {
                int victim = random.nextInt(alive.size());
                map.remove(alive.get(victim));
                total += map.lastProbeCount();
                String fresh = "k" + next++;
                map.put(fresh, "v");
                total += map.lastProbeCount();
                alive.set(victim, fresh);
            }
            assertEquals(400, map.size());

            long miss = 0;
            for (int i = 0; i < 2_000; i++) {
                map.get("miss" + i);
                miss += map.lastProbeCount();
            }
            return new long[] {total, miss};
        }

        @Test
        @DisplayName("넣고 지우기를 반복하면 tombstone 이 쌓여 조회가 무너진다")
        @Timeout(120)
        void tombstonesPileUp() {
            LinearProbeMap<String, String> linear = new LinearProbeMap<>(1024, 1.0);
            RobinHoodMap<String, String> robin = new RobinHoodMap<>(1024, 1.0);
            long[] linearCost = churn(linear);
            long[] robinCost = churn(robin);

            System.out.printf("  선형     churn 총 탐사 %,10d   이후 없는 키 2000회 %,10d   tombstone %d%n",
                    linearCost[0], linearCost[1], linear.tombstones());
            System.out.printf("  로빈후드 churn 총 탐사 %,10d   이후 없는 키 2000회 %,10d   tombstone %d%n",
                    robinCost[0], robinCost[1], 0);

            assertTrue(linear.tombstones() > 500,
                    "지운 자리가 " + linear.tombstones() + " 칸 쌓여 있다. 원소는 400개뿐인데 용량이 1024다");
            assertTrue(linearCost[1] > 100 * robinCost[1],
                    "없는 키 조회: 선형 " + linearCost[1] + " 대 로빈후드 " + robinCost[1]);
            assertTrue(linearCost[0] > 5 * robinCost[0],
                    "churn 자체의 비용: 선형 " + linearCost[0] + " 대 로빈후드 " + robinCost[0]);

            int occupied = 0;
            for (Object key : robin.keys) if (key != null) occupied++;
            assertEquals(400, occupied, "로빈후드에는 쌓일 자리가 없다");
        }
    }
}
