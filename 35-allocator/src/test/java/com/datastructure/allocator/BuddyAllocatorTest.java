package com.datastructure.allocator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** 계약을 buddy 로 돌리고, 쪼개고 합치는 규칙을 따로 본다. */
@DisplayName("buddy")
class BuddyAllocatorTest extends AllocatorContractTest {

    @Override
    protected Allocator create(int capacity) {
        return new BuddyAllocator(capacity);
    }

    @Override
    protected boolean rounds() {
        return true;
    }

    @Test
    @DisplayName("용량이 2의 거듭제곱이 아니면 던진다. 짝 계산이 성립하지 않는다")
    void capacityMustBeAPowerOfTwo() {
        assertThrows(IllegalArgumentException.class, () -> new BuddyAllocator(1000));
        assertThrows(IllegalArgumentException.class, () -> new BuddyAllocator(3));
        assertThrows(IllegalArgumentException.class, () -> new BuddyAllocator(0));
    }

    @Test
    @DisplayName("1 을 달래도 1 을 준다. 그 아래로는 안 쪼갠다")
    void smallestBlockIsOne() {
        BuddyAllocator a = new BuddyAllocator(1024);
        a.allocate(1);
        assertEquals(1, a.usedBytes());
        assertEquals(0, a.wastedBytes());
        assertEquals(10, a.splits(), "1024 에서 1 까지 열 번 쪼갠다");
    }

    @Test
    @DisplayName("올림한 만큼이 낭비다")
    void roundingIsTheCost() {
        BuddyAllocator a = new BuddyAllocator(1024);
        a.allocate(33);
        assertEquals(64, a.usedBytes(), "33 을 달라면 64 를 준다");
        assertEquals(31, a.wastedBytes());

        a.allocate(65);
        assertEquals(64 + 128, a.usedBytes());
        assertEquals(31 + 63, a.wastedBytes());
    }

    @Test
    @DisplayName("2의 거듭제곱을 딱 맞춰 달라면 낭비가 0 이다")
    void exactPowersWasteNothing() {
        BuddyAllocator a = new BuddyAllocator(1024);
        for (int size : List.of(256, 128, 64, 32)) {
            a.allocate(size);
        }
        assertEquals(0, a.wastedBytes());
        assertEquals(256 + 128 + 64 + 32, a.usedBytes());
    }

    @Test
    @DisplayName("짝이 둘 다 비면 즉시 합쳐 올라간다")
    void buddiesMergeAllTheWayUp() {
        BuddyAllocator a = new BuddyAllocator(1024);
        int one = a.allocate(256);
        int two = a.allocate(256);
        assertEquals(2, a.splits());

        a.free(one);
        assertEquals(0, a.merges(), "짝이 아직 안 비었다");
        a.free(two);
        assertEquals(2, a.merges(), "256 둘이 512 로, 512 둘이 1024 로");
        assertEquals(1024, a.largestFreeBlock());
        assertEquals(1, a.freeBlockCount());
    }

    @Test
    @DisplayName("한계 - 붙어 있어도 짝이 아니면 못 합친다")
    void adjacentIsNotEnough() {
        BuddyAllocator a = new BuddyAllocator(1024);
        // 256 짜리 넷을 떼어낸 뒤 가운데 둘만 돌려준다.
        int b0 = a.allocate(256);
        int b1 = a.allocate(256);
        int b2 = a.allocate(256);
        int b3 = a.allocate(256);
        assertEquals(List.of(0, 256, 512, 768), List.of(b0, b1, b2, b3));

        a.free(b1);
        a.free(b2);

        // [256,512) 와 [512,768) 은 붙어 있다. 그런데 서로의 짝이 아니다.
        // 256 의 짝은 0 이고 512 의 짝은 768 이다. 그래서 512 짜리 하나가 못 된다.
        assertEquals(512, a.freeBytes());
        assertEquals(256, a.largestFreeBlock(), "붙어 있는데 512 를 못 만든다");
        assertEquals(0, a.merges());
        assertEquals(Allocator.FAIL, a.allocate(300), "빈 공간 512 인데 300 이 실패한다");
    }

    @Test
    @DisplayName("빈 자리 목록 방식은 같은 상황에서 성공한다")
    void aFreeListWouldSucceedThere() {
        FirstFitAllocator a = new FirstFitAllocator(1024);
        int b0 = a.allocate(256);
        int b1 = a.allocate(256);
        int b2 = a.allocate(256);
        a.allocate(256);
        a.free(b1);
        a.free(b2);

        assertEquals(512, a.freeBytes());
        assertEquals(512, a.largestFreeBlock(), "주소가 붙어 있으면 합친다");
        assertNotEquals(Allocator.FAIL, a.allocate(300));
        assertNotEquals(b0, Allocator.FAIL);
        // 대가는 합치기가 O(1) 이 아니라는 것이다. 목록에서 옆자리를 찾아야 한다.
    }

    @Test
    @DisplayName("쪼갠 만큼 합쳐서 돌아온다")
    void splitsAndMergesBalance() {
        BuddyAllocator a = new BuddyAllocator(1024);
        List<Integer> addresses = new java.util.ArrayList<>();
        for (int i = 0; i < 8; i++) {
            addresses.add(a.allocate(128));
        }
        long splits = a.splits();
        for (int address : addresses) {
            a.free(address);
        }
        assertEquals(splits, a.merges(), "쪼갠 횟수와 합친 횟수가 같아야 한다");
        assertEquals(1024, a.largestFreeBlock());
        assertTrue(splits > 0);
    }
}
