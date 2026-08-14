package com.datastructure.allocator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 다섯 구현이 똑같이 지켜야 하는 것.
 *
 * bump 는 회수를 안 하므로 "돌려주면 다시 쓸 수 있다" 류를 못 지킨다.
 * 그것은 계약 위반이 아니라 이 계약이 회수를 요구하지 않기 때문이다.
 * 회수를 전제하는 검사는 recycles() 로 갈라둔다.
 *
 * 하위 클래스에서 @Nested 이름을 여기와 같게 지으면 상위 테스트가 조용히 사라진다.
 */
abstract class AllocatorContractTest {

    protected static final int CAPACITY = 1024;

    protected abstract Allocator create(int capacity);

    /** 돌려받은 자리를 다시 쓸 수 있는 구현인가. bump 만 false 다. */
    protected boolean recycles() {
        return true;
    }

    /** 이 구현이 요청보다 더 떼어줄 수 있는가. buddy 만 true 다. */
    protected boolean rounds() {
        return false;
    }

    /** 떼어준 자리들이 서로 겹치지 않는가. 할당자의 존재 이유다. */
    protected static void assertNoOverlap(List<int[]> spans) {
        List<int[]> sorted = new ArrayList<>(spans);
        sorted.sort((a, b) -> Integer.compare(a[0], b[0]));
        for (int i = 1; i < sorted.size(); i++) {
            assertTrue(sorted.get(i - 1)[0] + sorted.get(i - 1)[1] <= sorted.get(i)[0],
                    "겹친다: [" + sorted.get(i - 1)[0] + ", +" + sorted.get(i - 1)[1] + ") 와 ["
                            + sorted.get(i)[0] + ", +" + sorted.get(i)[1] + ")");
        }
    }

    @Nested
    @DisplayName("계약: 떼어주기")
    class HandingOut {

        @Test
        @DisplayName("떼어주면 주소가 나오고 범위 안이다")
        void addressIsInsideTheArena() {
            Allocator a = create(CAPACITY);
            int address = a.allocate(64);
            assertNotEquals(Allocator.FAIL, address);
            assertTrue(address >= 0 && address + 64 <= CAPACITY, "주소 " + address);
        }

        @Test
        @DisplayName("떼어준 자리끼리 겹치지 않는다")
        void allocationsDoNotOverlap() {
            Allocator a = create(CAPACITY);
            List<int[]> spans = new ArrayList<>();
            for (int size : List.of(64, 32, 128, 16, 256)) {
                int address = a.allocate(size);
                assertNotEquals(Allocator.FAIL, address, size + "바이트");
                spans.add(new int[] {address, size});
            }
            assertNoOverlap(spans);
        }

        @Test
        @DisplayName("같은 주소를 두 번 주지 않는다")
        void addressesAreUnique() {
            Allocator a = create(CAPACITY);
            Set<Integer> seen = new HashSet<>();
            for (int i = 0; i < 16; i++) {
                int address = a.allocate(16);
                assertTrue(seen.add(address), "주소 " + address + " 가 두 번 나왔다");
            }
        }

        @Test
        @DisplayName("자리가 없으면 FAIL 이다. 예외가 아니다")
        void runningOutIsNotAnError() {
            Allocator a = create(CAPACITY);
            assertNotEquals(Allocator.FAIL, a.allocate(CAPACITY));
            assertEquals(Allocator.FAIL, a.allocate(1));
        }

        @Test
        @DisplayName("용량보다 큰 요청도 FAIL 이다")
        void tooBigIsAlsoFail() {
            Allocator a = create(CAPACITY);
            assertEquals(Allocator.FAIL, a.allocate(CAPACITY + 1));
            assertEquals(Allocator.FAIL, a.allocate(CAPACITY * 4));
        }

        @Test
        @DisplayName("0 과 음수는 던진다")
        void zeroAndNegativeThrow() {
            Allocator a = create(CAPACITY);
            assertThrows(IllegalArgumentException.class, () -> a.allocate(0));
            assertThrows(IllegalArgumentException.class, () -> a.allocate(-1));
        }

        @Test
        @DisplayName("전체를 한 번에 떼어줄 수 있다")
        void wholeArenaInOneGo() {
            Allocator a = create(CAPACITY);
            assertNotEquals(Allocator.FAIL, a.allocate(CAPACITY));
            assertEquals(0, a.largestFreeBlock());
        }
    }

    @Nested
    @DisplayName("계약: 돌려받기")
    class TakingBack {

        @Test
        @DisplayName("떼어준 적 없는 주소를 돌려주면 던진다")
        void freeingAStrangerThrows() {
            Allocator a = create(CAPACITY);
            a.allocate(64);
            assertThrows(IllegalArgumentException.class, () -> a.free(9999));
            assertThrows(IllegalArgumentException.class, () -> a.free(-1));
        }

        @Test
        @DisplayName("같은 주소를 두 번 돌려주면 던진다")
        void doubleFreeThrows() {
            Allocator a = create(CAPACITY);
            if (!recycles()) {
                return;
            }
            int address = a.allocate(64);
            a.free(address);
            // 조용히 넘어가면 같은 자리를 두 번 세어 남은 공간이 부푼다.
            // 그 뒤로는 없는 자리를 떼어주게 되고, 손상이 한참 뒤에 드러난다.
            assertThrows(IllegalArgumentException.class, () -> a.free(address));
        }

        @Test
        @DisplayName("주소의 가운데를 돌려주면 던진다")
        void freeingIntoTheMiddleThrows() {
            Allocator a = create(CAPACITY);
            if (!recycles()) {
                return;
            }
            int address = a.allocate(64);
            assertThrows(IllegalArgumentException.class, () -> a.free(address + 8));
        }
    }

    @Nested
    @DisplayName("계약: 재는 값")
    class Accounting {

        @Test
        @DisplayName("빈 할당자는 전부 비어 있다")
        void emptyArena() {
            Allocator a = create(CAPACITY);
            assertEquals(CAPACITY, a.capacity());
            assertEquals(0, a.usedBytes());
            assertEquals(CAPACITY, a.freeBytes());
            assertEquals(CAPACITY, a.largestFreeBlock());
            assertEquals(1, a.freeBlockCount());
            assertEquals(0, a.wastedBytes());
        }

        @Test
        @DisplayName("쓴 것과 남은 것의 합이 늘 용량이다")
        void nothingIsLost() {
            Allocator a = create(CAPACITY);
            for (int size : List.of(64, 32, 128)) {
                a.allocate(size);
                // buddy 는 요청보다 더 떼어주므로 낭비까지 합쳐야 맞는다.
                assertEquals(CAPACITY, a.usedBytes() + a.freeBytes(),
                        "쓴 것 " + a.usedBytes() + " 남은 것 " + a.freeBytes());
            }
        }

        @Test
        @DisplayName("가장 큰 덩어리는 남은 총량을 넘지 않는다")
        void largestIsNeverMoreThanTotal() {
            Allocator a = create(CAPACITY);
            a.allocate(100);
            a.allocate(200);
            assertTrue(a.largestFreeBlock() <= a.freeBytes(),
                    a.largestFreeBlock() + " > " + a.freeBytes());
        }

        @Test
        @DisplayName("가장 큰 덩어리만큼은 반드시 떼어줄 수 있다")
        void largestBlockIsAPromise() {
            Allocator a = create(CAPACITY);
            a.allocate(100);
            a.allocate(50);
            int largest = a.largestFreeBlock();
            // 이 약속이 깨지면 largestFreeBlock 은 아무 뜻도 없는 수다.
            assertNotEquals(Allocator.FAIL, a.allocate(largest), "가장 큰 덩어리가 " + largest);
        }

        @Test
        @DisplayName("가장 큰 덩어리보다 1 큰 것은 못 떼어준다")
        void oneMoreThanLargestFails() {
            Allocator a = create(CAPACITY);
            a.allocate(100);
            int largest = a.largestFreeBlock();
            if (largest < CAPACITY) {
                assertEquals(Allocator.FAIL, a.allocate(largest + 1));
            }
        }

        @Test
        @DisplayName("낭비는 buddy 만 0 이 아니다")
        void onlyRoundingWastes() {
            Allocator a = create(CAPACITY);
            a.allocate(33);
            if (rounds()) {
                assertTrue(a.wastedBytes() > 0, "2의 거듭제곱으로 올림하면 낭비가 생긴다");
                assertEquals(31, a.wastedBytes());
            } else {
                assertEquals(0, a.wastedBytes(), "요청한 만큼만 떼어주면 낭비가 없다");
            }
        }
    }

    @Nested
    @DisplayName("계약: 회수하는 구현이라면")
    class Recycling {

        @Test
        @DisplayName("돌려주면 남은 공간이 돌아온다")
        void freeingGivesTheSpaceBack() {
            Allocator a = create(CAPACITY);
            if (!recycles()) {
                return;
            }
            int address = a.allocate(256);
            int afterAlloc = a.freeBytes();
            a.free(address);
            assertTrue(a.freeBytes() > afterAlloc, afterAlloc + " -> " + a.freeBytes());
            assertEquals(CAPACITY, a.freeBytes());
            assertEquals(0, a.usedBytes());
        }

        @Test
        @DisplayName("전부 돌려주면 처음 상태로 돌아온다")
        void everythingBackMeansAsGoodAsNew() {
            Allocator a = create(CAPACITY);
            if (!recycles()) {
                return;
            }
            List<Integer> addresses = new ArrayList<>();
            for (int size : List.of(64, 32, 128, 16, 200)) {
                addresses.add(a.allocate(size));
            }
            for (int address : addresses) {
                a.free(address);
            }

            assertEquals(0, a.usedBytes());
            assertEquals(CAPACITY, a.freeBytes());
            // 여기가 합치기를 검사하는 자리다. 안 합치면 총량은 맞는데 덩어리가 잘게 남는다.
            assertEquals(CAPACITY, a.largestFreeBlock(), "전부 돌려줬으면 한 덩어리여야 한다");
            assertEquals(1, a.freeBlockCount());
            assertNotEquals(Allocator.FAIL, a.allocate(CAPACITY));
        }

        @Test
        @DisplayName("가운데를 돌려주고 다시 떼어줄 수 있다")
        void aHoleInTheMiddleIsReusable() {
            Allocator a = create(CAPACITY);
            if (!recycles()) {
                return;
            }
            // 꽉 채운 뒤에 가운데 하나만 비운다. 그러면 빈 자리가 그것 하나뿐이라
            // 어느 전략이든 반드시 그 자리를 골라야 한다.
            // (채우지 않고 비우면 worst fit 은 뒤쪽의 더 큰 자리를 고른다. 그것도 맞는 동작이다.)
            List<Integer> all = new ArrayList<>();
            for (int i = 0; i < CAPACITY / 128; i++) {
                int address = a.allocate(128);
                assertNotEquals(Allocator.FAIL, address, i + "번째");
                all.add(address);
            }
            assertEquals(0, a.freeBytes());

            int middle = all.get(1);
            a.free(middle);

            int again = a.allocate(128);
            assertNotEquals(Allocator.FAIL, again);
            assertEquals(middle, again, "빈 자리가 그것뿐인데 다른 데를 골랐다");
        }

        @Test
        @DisplayName("돌려주고 떼어주기를 반복해도 새는 것이 없다")
        void repeatedCyclesDoNotLeak() {
            Allocator a = create(CAPACITY);
            if (!recycles()) {
                return;
            }
            for (int round = 0; round < 200; round++) {
                int one = a.allocate(64);
                int two = a.allocate(128);
                assertNotEquals(Allocator.FAIL, one, round + "번째");
                assertNotEquals(Allocator.FAIL, two, round + "번째");
                a.free(one);
                a.free(two);
                assertEquals(CAPACITY, a.freeBytes(), round + "번째");
                assertEquals(CAPACITY, a.largestFreeBlock(), round + "번째에 부서졌다");
            }
        }

        @Test
        @DisplayName("무작위로 떼고 돌려줘도 겹치지 않고 총량이 맞는다")
        void randomChurnStaysConsistent() {
            Allocator a = create(CAPACITY);
            if (!recycles()) {
                return;
            }
            long state = 35_000L;
            List<int[]> live = new ArrayList<>();     // [주소, 크기]
            for (int step = 0; step < 3_000; step++) {
                state = state * 6364136223846793005L + 1442695040888963407L;
                int roll = (int) Math.floorMod(state >>> 33, 100);
                if (roll < 55 || live.isEmpty()) {
                    state = state * 6364136223846793005L + 1442695040888963407L;
                    int size = 1 + (int) Math.floorMod(state >>> 33, 96);
                    int address = a.allocate(size);
                    if (address != Allocator.FAIL) {
                        live.add(new int[] {address, size});
                    }
                } else {
                    state = state * 6364136223846793005L + 1442695040888963407L;
                    int i = (int) Math.floorMod(state >>> 33, live.size());
                    a.free(live.remove(i)[0]);
                }
                assertNoOverlap(live);
                assertEquals(CAPACITY, a.usedBytes() + a.freeBytes(), step + "번째");
                assertTrue(a.largestFreeBlock() <= a.freeBytes(), step + "번째");
            }

            for (int[] span : live) {
                a.free(span[0]);
            }
            assertEquals(CAPACITY, a.freeBytes());
            assertEquals(CAPACITY, a.largestFreeBlock(), "3천 번을 돌리고도 한 덩어리여야 한다");
        }
    }
}
