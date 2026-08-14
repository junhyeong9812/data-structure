package com.datastructure.allocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [구현] 빈 자리 목록을 들고 있다가 하나를 골라 떼어준다.
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
        int index = choose(List.copyOf(free), size);
        if (index < 0) {
            return FAIL;
        }
        Block block = free.get(index);
        if (block.size < size) {
            throw new IllegalStateException("고른 자리가 요청보다 작다: " + block + " < " + size);
        }

        int address = block.start;
        allocated.put(address, size);
        if (block.size == size) {
            // 딱 맞으면 통째로 뺀다. 0 바이트짜리 빈 자리를 남기면
            // 목록이 쓸모없는 항목으로 불어나고 freeBlockCount 가 거짓말을 한다.
            free.remove(index);
        } else {
            block.start += size;
            block.size -= size;
        }
        return address;
    }

    @Override
    public void free(int address) {
        Integer size = allocated.remove(address);
        if (size == null) {
            throw new IllegalArgumentException("떼어준 적 없거나 이미 돌려받은 주소다: " + address);
        }
        insertAndCoalesce(new Block(address, size));
    }

    /**
     * 주소 순서에 맞는 자리에 꽂고, 양옆이 붙어 있으면 합친다.
     *
     * 양옆을 다 봐야 한다. 오른쪽만 보면 앞에서 돌려받은 자리와 안 합쳐지고,
     * 왼쪽만 보면 그 반대다. 어느 쪽이든 반쪽만 합쳐져서 천천히 부서진다.
     */
    private void insertAndCoalesce(Block block) {
        int i = 0;
        while (i < free.size() && free.get(i).start < block.start) {
            i++;
        }
        if (i < free.size() && free.get(i).start < block.end()) {
            throw new IllegalStateException("빈 자리가 겹친다: " + block + " 와 " + free.get(i));
        }
        free.add(i, block);

        // 오른쪽을 먼저 합친다. 왼쪽부터 해도 되기는 하는데, 왼쪽을 지우면 i 가 한 칸 밀리므로
        // 그것을 보정해야 한다. 보정을 빼먹으면 오른쪽 이웃을 엉뚱한 자리에서 찾아 못 합친다.
        // 오른쪽부터 하면 i 가 안 밀려서 보정할 것이 없다. 순서가 취향이 아니라 이 이유다.
        if (i + 1 < free.size() && free.get(i).end() == free.get(i + 1).start) {
            free.get(i).size += free.get(i + 1).size;
            free.remove(i + 1);
        }
        if (i > 0 && free.get(i - 1).end() == free.get(i).start) {
            free.get(i - 1).size += free.get(i).size;
            free.remove(i);
        }
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
