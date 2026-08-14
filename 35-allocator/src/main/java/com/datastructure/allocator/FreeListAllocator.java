package com.datastructure.allocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 빈 자리 목록을 들고 있다가 하나를 골라 떼어준다.
 *
 * 목록은 <b>주소 오름차순</b>으로 유지한다. 그래야 인접한 빈 자리를 합칠 수 있다.
 * 크기순으로 들면 고르기는 빨라지는데 합치기가 불가능해진다.
 * 무엇을 정렬해 두느냐가 무엇을 할 수 있는지를 정하는 자리다.
 *
 * <h2>고르는 규칙만 하위 클래스가 정한다</h2>
 *
 * <pre>
 *   first fit   맞는 것 중 제일 앞
 *   best fit    맞는 것 중 제일 작은 것
 *   worst fit   맞는 것 중 제일 큰 것
 * </pre>
 *
 * 셋의 차이는 코드 세 줄인데 결과는 크게 갈린다. MeasurementTest 가 그것을 잰다.
 *
 * <h2>합치기가 이 구현의 생명이다</h2>
 *
 * 돌려받은 자리를 그냥 목록에 넣기만 하면, 옆자리가 비어 있어도 따로 논다.
 * 그러면 8 바이트짜리 빈 자리 백 개가 생기고 <b>9 바이트 요청이 실패한다.</b>
 * 남은 공간은 800 바이트인데 말이다.
 *
 * 예외도 안 나고 계약 테스트도 다 통과한다. 남은 공간을 세는 자와
 * 가장 큰 덩어리를 재는 자를 나란히 놓아야만 드러난다.
 */
public abstract class FreeListAllocator implements Allocator {

    /** 빈 자리 하나. 주소 오름차순으로 목록에 들어 있다. */
    protected static final class Block {
        int start;
        int size;

        Block(int start, int size) {
            this.start = start;
            this.size = size;
        }

        int end() {
            return start + size;
        }

        @Override
        public String toString() {
            return "[" + start + ".." + end() + ") " + size + "바이트";
        }
    }

    private final int capacity;
    private final List<Block> free = new ArrayList<>();

    /** 주소 -> 떼어준 크기. free 가 얼마를 돌려받아야 하는지 알아야 한다. */
    private final Map<Integer, Integer> allocated = new HashMap<>();

    private long scanned;

    protected FreeListAllocator(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("용량은 1 이상이다: " + capacity);
        }
        this.capacity = capacity;
        free.add(new Block(0, capacity));
    }

    /**
     * 맞는 것 중 무엇을 쓸지 고른다. 없으면 -1.
     *
     * blocks 는 주소 오름차순이다. 이 목록을 고치면 안 된다.
     */
    protected abstract int choose(List<Block> blocks, int size);

    @Override
    public int allocate(int size) {
        requireSize(size);
        // TODO 2: 하위 클래스에게 자리를 고르게 하고, 그 자리에서 앞쪽을 떼어준다.
        //
        // 셋을 조심하라.
        //
        //   1. 어디를 떼어줬는지 **크기까지** 기록해야 한다. free 가 얼마를 돌려받는지
        //      알 방법이 그것뿐이다. 안 적어두면 돌려받을 때 크기를 추측하게 된다.
        //   2. 딱 맞는 자리를 쪼개면 **0 바이트짜리 빈 자리**가 남는다. 예외는 안 나는데
        //      목록이 쓸모없는 항목으로 불어나고 freeBlockCount 가 거짓말을 한다.
        //   3. choose 가 준 자리가 정말 요청보다 큰지 여기서 확인하라. 하위 클래스를
        //      믿고 넘어가면 남의 자리를 덮어쓰게 되는데, 그건 아무 데서도 안 잡힌다.
        throw new UnsupportedOperationException("TODO 2: allocate");
    }

    @Override
    public void free(int address) {
        // TODO 3: 기록에서 빼고, 그 자리를 빈 자리 목록에 되돌린다.
        //
        // 모르는 주소면 던져야 한다. 조용히 넘어가면 같은 자리를 두 번 세어
        // 남은 공간이 부풀고, 그 뒤로는 없는 자리를 떼어주게 된다.
        throw new UnsupportedOperationException("TODO 3: free");
    }

    /**
     * 주소 순서에 맞는 자리에 꽂고, 양옆이 붙어 있으면 합친다.
     *
     * 양옆을 다 봐야 한다. 오른쪽만 보면 앞에서 돌려받은 자리와 안 합쳐지고,
     * 왼쪽만 보면 그 반대다. 어느 쪽이든 반쪽만 합쳐져서 천천히 부서진다.
     */
    private void insertAndCoalesce(Block block) {
        // TODO 4: 주소 순서에 맞는 자리에 꽂고, 붙어 있는 이웃과 합친다.
        //
        // **이 메서드가 이 구현의 생명이다.** 합치지 않아도 계약 테스트는 거의 다 통과한다.
        // 남은 총량이 맞기 때문이다. 잘게 부서진 것은 가장 큰 덩어리를 재야만 드러난다.
        //
        // **양옆을 다 봐야 한다.** 한쪽만 보면 반쪽만 합쳐져서 천천히 부서진다.
        // 그리고 지우는 순서에 따라 인덱스가 밀린다. 위 javadoc 이 그 이야기를 적어뒀다.
        throw new UnsupportedOperationException("TODO 4: insertAndCoalesce");
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public int usedBytes() {
        int total = 0;
        for (int size : allocated.values()) {
            total += size;
        }
        return total;
    }

    @Override
    public int freeBytes() {
        int total = 0;
        for (Block block : free) {
            total += block.size;
        }
        return total;
    }

    @Override
    public int largestFreeBlock() {
        int largest = 0;
        for (Block block : free) {
            largest = Math.max(largest, block.size);
        }
        return largest;
    }

    @Override
    public int freeBlockCount() {
        return free.size();
    }

    /** 요청한 만큼만 떼어준다. 내부 단편화가 없다. 그 대신 외부 단편화를 짊어진다. */
    @Override
    public int wastedBytes() {
        return 0;
    }

    /** 자리를 고르느라 들여다본 빈 자리의 수. 전략별 비용이다. */
    public long scanned() {
        return scanned;
    }

    protected void countScan(long n) {
        scanned += n;
    }

    /** 빈 자리 목록. 테스트가 들여다본다. */
    public List<String> freeBlocks() {
        List<String> out = new ArrayList<>();
        for (Block block : free) {
            out.add(block.toString());
        }
        return List.copyOf(out);
    }

    protected static void requireSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("크기는 1 이상이다: " + size);
        }
    }
}
