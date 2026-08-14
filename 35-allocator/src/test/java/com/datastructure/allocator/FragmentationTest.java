package com.datastructure.allocator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 이 박스의 본론.
 *
 * 계약 테스트 117개는 다섯 구현이 다 통과한다. 자리를 떼어주고 돌려받는 일은
 * 어느 쪽으로 해도 맞다. 갈리는 것은 <b>한참 쓰고 난 뒤의 모양</b>이다.
 *
 * <pre>
 *   외부 단편화   남은 총량은 충분한데 연속된 자리가 없어 실패한다
 *   내부 단편화   요청보다 더 떼어줘서 그 안에 갇힌다
 * </pre>
 *
 * 둘은 맞바꾸는 관계다. 한쪽을 없애면 다른 쪽이 생긴다.
 */
@DisplayName("단편화 측정")
class FragmentationTest {

    private static final int CAPACITY = 1024;

    private static List<Allocator> freeListAllocators() {
        return List.of(new FirstFitAllocator(CAPACITY),
                new BestFitAllocator(CAPACITY),
                new WorstFitAllocator(CAPACITY));
    }

    @Nested
    @DisplayName("측정 1: 빈 공간은 충분한데 실패한다")
    class ExternalFragmentation {

        @Test
        @DisplayName("한 칸 걸러 비우면 절반이 남았는데 아무것도 못 넣는다")
        void alternatingFreeMakesTheSpaceUseless() {
            for (Allocator a : freeListAllocators()) {
                List<Integer> addresses = new ArrayList<>();
                for (int i = 0; i < 64; i++) {
                    addresses.add(a.allocate(16));      // 16 바이트 64 개로 꽉 채운다
                }
                assertEquals(0, a.freeBytes());

                for (int i = 0; i < 64; i += 2) {
                    a.free(addresses.get(i));           // 짝수 번째만 돌려준다
                }

                System.out.printf("  %-12s 남은 %,4d   가장 큰 덩어리 %,3d   덩어리 수 %d%n",
                        a, a.freeBytes(), a.largestFreeBlock(), a.freeBlockCount());

                assertEquals(512, a.freeBytes(), "절반이 남았다");
                assertEquals(16, a.largestFreeBlock(), "그런데 연속된 것은 16 뿐이다");
                assertEquals(32, a.freeBlockCount(), "32 조각으로 부서졌다");
                assertEquals(Allocator.FAIL, a.allocate(17),
                        "512 가 남았는데 17 을 못 준다");
            }
        }

        @Test
        @DisplayName("합치기가 있으니까 이 정도에서 멈춘다")
        void coalescingIsWhatKeepsItFromGettingWorse() {
            FirstFitAllocator a = new FirstFitAllocator(CAPACITY);
            List<Integer> addresses = new ArrayList<>();
            for (int i = 0; i < 64; i++) {
                addresses.add(a.allocate(16));
            }
            for (int i = 0; i < 64; i++) {
                a.free(addresses.get(i));
            }
            // 64 조각을 순서대로 돌려줬다. 합치기가 있으면 1 개, 없으면 64 개다.
            System.out.printf("  전부 돌려준 뒤 덩어리 수 %d, 가장 큰 덩어리 %,d%n",
                    a.freeBlockCount(), a.largestFreeBlock());
            assertEquals(1, a.freeBlockCount());
            assertEquals(CAPACITY, a.largestFreeBlock());
        }

        @Test
        @DisplayName("buddy 라고 외부 단편화를 피하지 못한다")
        void buddyIsNotImmune() {
            BuddyAllocator buddy = new BuddyAllocator(CAPACITY);
            List<Integer> addresses = new ArrayList<>();
            for (int i = 0; i < 64; i++) {
                addresses.add(buddy.allocate(16));
            }
            for (int i = 0; i < 64; i += 2) {
                buddy.free(addresses.get(i));
            }

            System.out.printf("  buddy        남은 %,4d   가장 큰 덩어리 %,3d   덩어리 수 %d%n",
                    buddy.freeBytes(), buddy.largestFreeBlock(), buddy.freeBlockCount());

            assertEquals(512, buddy.freeBytes());
            assertEquals(16, buddy.largestFreeBlock(), "짝이 안 비어서 여기서도 16 이다");
            assertEquals(32, buddy.freeBlockCount(), "빈 자리 목록과 똑같이 32 조각이다");
            // buddy 가 이기는 것은 크기가 제각각일 때다. 여기서는 요청이 이미
            // 2의 거듭제곱이라 올림이 안 일어나고, 그러면 buddy 의 이점이 통째로 사라진다.
            // 이길 조건을 안 갖춘 채로 "이 구조가 낫다" 고 말하면 안 된다.
            assertEquals(Allocator.FAIL, buddy.allocate(17));
        }
    }

    @Nested
    @DisplayName("측정 2: 전략마다 언제 무너지는가")
    class StrategiesDiverge {

        /**
         * 크고 작은 요청을 섞어 넣고 작은 것만 돌려준다.
         * 실제 프로그램의 수명 분포를 아주 거칠게 흉내낸 것이다.
         */
        private int[] churn(FreeListAllocator a) {
            List<Integer> small = new ArrayList<>();
            long state = 35_100L;
            int failures = 0;
            for (int round = 0; round < 300; round++) {
                state = state * 6364136223846793005L + 1442695040888963407L;
                int size = 8 + (int) Math.floorMod(state >>> 33, 56);
                int address = a.allocate(size);
                if (address == Allocator.FAIL) {
                    failures++;
                    if (!small.isEmpty()) {
                        a.free(small.remove(0));
                    }
                    continue;
                }
                if (size < 32) {
                    small.add(address);
                } else if (!small.isEmpty() && round % 3 == 0) {
                    a.free(small.remove(0));
                }
            }
            return new int[] {failures, a.freeBytes(), a.largestFreeBlock(), a.freeBlockCount()};
        }

        @Test
        @DisplayName("같은 요청 열에 세 전략의 결과가 갈린다")
        void sameWorkloadDifferentOutcomes() {
            FirstFitAllocator first = new FirstFitAllocator(CAPACITY);
            BestFitAllocator best = new BestFitAllocator(CAPACITY);
            WorstFitAllocator worst = new WorstFitAllocator(CAPACITY);

            int[] f = churn(first);
            int[] b = churn(best);
            int[] w = churn(worst);

            System.out.printf("  요청 300개를 같은 순서로 넣는다%n");
            System.out.printf("    %-12s 실패 %3d   남은 %,4d   가장 큰 %,4d   덩어리 %2d   훑음 %,6d%n",
                    "first fit", f[0], f[1], f[2], f[3], first.scanned());
            System.out.printf("    %-12s 실패 %3d   남은 %,4d   가장 큰 %,4d   덩어리 %2d   훑음 %,6d%n",
                    "best fit", b[0], b[1], b[2], b[3], best.scanned());
            System.out.printf("    %-12s 실패 %3d   남은 %,4d   가장 큰 %,4d   덩어리 %2d   훑음 %,6d%n",
                    "worst fit", w[0], w[1], w[2], w[3], worst.scanned());

            assertEquals(169, f[0], "first fit 실패");
            assertEquals(170, b[0], "best fit 실패");
            assertEquals(178, w[0], "worst fit 실패");

            // worst fit 이 제일 많이 실패하고 제일 많이 훑는다.
            // 큰 자리를 먼저 깎아먹어서, 큰 요청이 나중에 오면 받을 자리가 없다.
            assertTrue(w[0] > f[0], "worst fit 이 더 실패한다: " + w[0] + " 대 " + f[0]);
            assertTrue(worst.scanned() > first.scanned() * 1.5,
                    worst.scanned() + " 대 " + first.scanned());

            assertEquals(7, f[3], "first fit 덩어리");
            assertEquals(7, b[3], "best fit 덩어리");
            assertEquals(9, w[3], "worst fit 덩어리");

            // best fit 이 first fit 보다 조금 더 실패한다. "큰 자리를 아낀다" 는
            // 그럴듯한 이유가 여기서는 안 통한다. 대신 훑는 비용은 확실히 더 든다.
        }

        @Test
        @DisplayName("훑는 비용은 first fit 이 제일 싸다")
        void firstFitScansLeast() {
            FirstFitAllocator first = new FirstFitAllocator(CAPACITY);
            BestFitAllocator best = new BestFitAllocator(CAPACITY);
            for (int i = 0; i < 32; i++) {
                first.allocate(16);
                best.allocate(16);
            }
            System.out.printf("  16 바이트 32 번: first %,d 훑음, best %,d 훑음%n",
                    first.scanned(), best.scanned());

            assertEquals(32, first.scanned(), "맞는 것을 만나면 멈춘다");
            assertEquals(32, best.scanned(), "여기서는 덩어리가 하나뿐이라 같다");

            // 덩어리가 여럿이어야 차이가 난다.
            FirstFitAllocator f2 = new FirstFitAllocator(CAPACITY);
            BestFitAllocator b2 = new BestFitAllocator(CAPACITY);
            for (FreeListAllocator a : List.of(f2, b2)) {
                List<Integer> addresses = new ArrayList<>();
                for (int i = 0; i < 32; i++) {
                    addresses.add(a.allocate(16));
                }
                for (int i = 0; i < 32; i += 2) {
                    a.free(addresses.get(i));
                }
            }
            long fBefore = f2.scanned();
            long bBefore = b2.scanned();
            f2.allocate(16);
            b2.allocate(16);

            System.out.printf("  덩어리 16개일 때 한 번 더: first %d, best %d%n",
                    f2.scanned() - fBefore, b2.scanned() - bBefore);
            assertEquals(1, f2.scanned() - fBefore, "첫 덩어리에서 멈춘다");
            assertEquals(17, b2.scanned() - bBefore, "전부 봐야 제일 작은 것을 안다");
        }
    }

    @Nested
    @DisplayName("측정 3: 내부 단편화는 buddy 만의 대가다")
    class InternalFragmentation {

        @Test
        @DisplayName("2의 거듭제곱 바로 위 크기가 제일 나쁘다")
        void justOverAPowerOfTwoIsTheWorst() {
            System.out.printf("  요청 크기별 buddy 의 낭비%n");
            for (int size : new int[] {16, 17, 32, 33, 64, 65}) {
                BuddyAllocator a = new BuddyAllocator(CAPACITY);
                a.allocate(size);
                System.out.printf("    %3d 요청 -> %3d 떼어줌, 낭비 %3d%n",
                        size, a.usedBytes(), a.wastedBytes());
            }

            BuddyAllocator exact = new BuddyAllocator(CAPACITY);
            exact.allocate(64);
            assertEquals(0, exact.wastedBytes());

            BuddyAllocator justOver = new BuddyAllocator(CAPACITY);
            justOver.allocate(65);
            assertEquals(128, justOver.usedBytes());
            assertEquals(63, justOver.wastedBytes(), "1 을 더 달랬더니 63 이 갇힌다");
        }

        @Test
        @DisplayName("한계 - 최악의 요청 열에서는 절반 가까이가 갇힌다")
        void nearlyHalfCanBeLost() {
            BuddyAllocator a = new BuddyAllocator(CAPACITY);
            int count = 0;
            while (a.allocate(33) != Allocator.FAIL) {
                count++;
            }
            System.out.printf("  33 바이트를 %d 번 떼어줬다%n", count);
            System.out.printf("    실제로 쓰는 것 %,d, 갇힌 것 %,d (%.0f%%)%n",
                    count * 33, a.wastedBytes(), 100.0 * a.wastedBytes() / CAPACITY);

            assertEquals(16, count, "64 씩 떼어주므로 16 번이면 꽉 찬다");
            assertEquals(1024, a.usedBytes());
            assertEquals(0, a.freeBytes(), "남은 공간이 0 이다");
            assertEquals(496, a.wastedBytes(), "그런데 496 은 아무도 안 쓴다");

            // 어떤 자에도 안 잡히는 낭비다. freeBytes 가 0 이라 "꽉 찼다" 로 보인다.
            // 빈 자리 목록 방식은 같은 요청 열에서 31 개를 받는다.
            FirstFitAllocator list = new FirstFitAllocator(CAPACITY);
            int listCount = 0;
            while (list.allocate(33) != Allocator.FAIL) {
                listCount++;
            }
            System.out.printf("  같은 요청을 first fit 은 %d 번 받는다%n", listCount);
            assertEquals(31, listCount);
            assertTrue(listCount > count * 1.9, listCount + " 대 " + count);
        }
    }

    @Nested
    @DisplayName("측정 4: 수명이 다 같으면 관리할 것이 없다")
    class WhenLifetimesMatch {

        @Test
        @DisplayName("bump 는 단편화가 0 이다. 대신 아무것도 회수 못 한다")
        void bumpHasNoFragmentationAndNoRecovery() {
            BumpAllocator bump = new BumpAllocator(CAPACITY);
            FirstFitAllocator list = new FirstFitAllocator(CAPACITY);

            long state = 35_200L;
            List<Integer> listLive = new ArrayList<>();
            int bumpCount = 0;
            int listCount = 0;
            for (int i = 0; i < 40; i++) {
                state = state * 6364136223846793005L + 1442695040888963407L;
                int size = 8 + (int) Math.floorMod(state >>> 33, 40);
                if (bump.allocate(size) != Allocator.FAIL) {
                    bumpCount++;
                }
                int address = list.allocate(size);
                if (address != Allocator.FAIL) {
                    listLive.add(address);
                    listCount++;
                }
            }

            System.out.printf("  요청 40개: bump %d 개 성공, first fit %d 개 성공%n",
                    bumpCount, listCount);
            System.out.printf("    bump 덩어리 수 %d, first fit 덩어리 수 %d%n",
                    bump.freeBlockCount(), list.freeBlockCount());

            assertEquals(bumpCount, listCount, "돌려주지 않는 동안에는 똑같다");
            assertEquals(1, bump.freeBlockCount(), "bump 는 늘 한 덩어리다");
            assertEquals(1, list.freeBlockCount(), "돌려준 적이 없으니 여기도 한 덩어리다");
            // 관리 비용은 수명이 제각각일 때 생긴다. 다 같으면 bump 가 이긴다.

            for (int address : listLive) {
                list.free(address);
            }
            assertEquals(CAPACITY, list.freeBytes(), "first fit 은 회수한다");
            assertNotEquals(CAPACITY, bump.freeBytes(), "bump 는 못 한다");
        }
    }
}
