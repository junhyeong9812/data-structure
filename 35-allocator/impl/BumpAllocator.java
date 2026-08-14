package com.datastructure.allocator;

/**
 * [구현] 그냥 밀어올린다. 돌려받지 않는다.
 *
 * <pre>
 *   allocate   지금 위치를 주고 그만큼 앞으로 민다
 *   free       아무것도 안 한다
 * </pre>
 *
 * 코드가 이게 전부다. 빈 자리 목록도, 합치기도, 고르는 규칙도 없다.
 * 그래서 <b>제일 빠르고 단편화가 아예 없다.</b>
 *
 * 대가가 하나뿐이다. <b>돌려받지를 못한다.</b> 100 바이트를 백 번 떼어주고
 * 아흔아홉 번 돌려받아도 남은 공간은 그대로 0 이다.
 *
 * 이것이 쓸모없는 설계가 아니다. 요청 하나를 처리하는 동안만 쓰고 끝나면
 * 통째로 버리면 되므로, 실제로 이 방식을 쓰는 곳이 많다
 * (요청 단위 아레나, 컴파일러의 패스별 메모리, 게임의 프레임 메모리).
 *
 * <b>수명이 다 같으면 관리할 것이 없다.</b> 관리 비용은 수명이 제각각일 때 생긴다.
 * 그 사실을 재기 위해 이 기준선이 있다.
 */
public class BumpAllocator implements Allocator {

    private final int capacity;
    private int next;
    private int handedOut;
    private int freeCalls;

    public BumpAllocator(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("용량은 1 이상이다: " + capacity);
        }
        this.capacity = capacity;
    }

    @Override
    public int allocate(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("크기는 1 이상이다: " + size);
        }
        if (next + size > capacity) {
            return FAIL;
        }
        int address = next;
        next += size;
        handedOut += size;
        return address;
    }

    /**
     * 아무것도 안 한다. 그런데 던지지도 않는다.
     *
     * 던지면 이 할당자를 다른 것과 바꿔 끼울 수 없다. 계약을 지키되
     * 회수를 안 하는 것이 이 설계의 정직한 표현이다.
     * 그 대신 몇 번 불렸는지 세어두어, 얼마나 낭비하고 있는지 볼 수 있게 한다.
     */
    @Override
    public void free(int address) {
        if (address < 0 || address >= next) {
            throw new IllegalArgumentException("떼어준 적 없는 주소다: " + address);
        }
        freeCalls++;
    }

    @Override
    public int capacity() {
        return capacity;
    }

    /** 돌려받은 것을 안 빼므로 떼어준 총량 그대로다. */
    @Override
    public int usedBytes() {
        return handedOut;
    }

    @Override
    public int freeBytes() {
        return capacity - next;
    }

    /** 남은 것은 늘 뒤쪽 한 덩어리다. 단편화가 없다는 말이 이 뜻이다. */
    @Override
    public int largestFreeBlock() {
        return capacity - next;
    }

    @Override
    public int freeBlockCount() {
        return next < capacity ? 1 : 0;
    }

    @Override
    public int wastedBytes() {
        return 0;
    }

    /** free 가 불린 횟수. 돌려주려 했지만 안 돌아간 횟수다. */
    public int ignoredFrees() {
        return freeCalls;
    }

    /** 전부 버리고 처음으로. 이 할당자가 회수하는 유일한 방법이다. */
    public void reset() {
        next = 0;
        handedOut = 0;
        freeCalls = 0;
    }

    @Override
    public String toString() {
        return "bump(회수 없음)";
    }
}
