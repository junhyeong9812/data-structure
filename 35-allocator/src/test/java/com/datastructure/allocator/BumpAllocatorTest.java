package com.datastructure.allocator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 계약을 bump 로 돌리고, 회수를 안 한다는 사실을 따로 못 박는다.
 *
 * 회수를 전제하는 검사는 계약 쪽에서 recycles() 로 걸러진다.
 * 걸러지는 것을 그냥 두면 "통과했다"가 아무 뜻이 없으므로 여기서 반대 방향으로 검사한다.
 */
@DisplayName("bump (회수 없음)")
class BumpAllocatorTest extends AllocatorContractTest {

    @Override
    protected Allocator create(int capacity) {
        return new BumpAllocator(capacity);
    }

    @Override
    protected boolean recycles() {
        return false;
    }

    @Test
    @DisplayName("돌려줘도 공간이 안 돌아온다. 그것이 이 설계다")
    void freeingChangesNothing() {
        BumpAllocator a = new BumpAllocator(1024);
        int address = a.allocate(512);
        a.free(address);

        assertEquals(512, a.usedBytes(), "돌려줘도 쓴 것으로 센다");
        assertEquals(512, a.freeBytes());
        assertEquals(1, a.ignoredFrees());
        assertEquals(Allocator.FAIL, a.allocate(600), "돌려준 512 를 다시 못 쓴다");
    }

    @Test
    @DisplayName("천 번 돌려줘도 마찬가지다")
    void freeingIsAlwaysIgnored() {
        BumpAllocator a = new BumpAllocator(1024);
        for (int i = 0; i < 16; i++) {
            a.free(a.allocate(64));
        }
        assertEquals(1024, a.usedBytes());
        assertEquals(0, a.freeBytes());
        assertEquals(16, a.ignoredFrees());
        assertEquals(Allocator.FAIL, a.allocate(1));
    }

    @Test
    @DisplayName("회수하는 유일한 방법은 통째로 버리는 것이다")
    void resetIsTheOnlyWayBack() {
        BumpAllocator a = new BumpAllocator(1024);
        a.allocate(1024);
        assertEquals(Allocator.FAIL, a.allocate(1));

        a.reset();

        assertEquals(0, a.usedBytes());
        assertEquals(1024, a.freeBytes());
        assertNotEquals(Allocator.FAIL, a.allocate(1024));
    }

    @Test
    @DisplayName("단편화가 아예 없다. 남은 것은 늘 뒤쪽 한 덩어리다")
    void neverFragments() {
        BumpAllocator a = new BumpAllocator(1024);
        for (int i = 0; i < 10; i++) {
            a.free(a.allocate(37));
        }
        assertEquals(a.freeBytes(), a.largestFreeBlock(), "남은 것이 전부 한 덩어리다");
        assertEquals(1, a.freeBlockCount());
    }
}
