package com.datastructure.allocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * [구현] 2의 거듭제곱으로만 쪼개고, 짝끼리만 합친다.
 *
 * <pre>
 *   1024 를 반으로  ->  512 512
 *   앞의 512 를 반  ->  256 256 512
 *
 *   저 두 256 이 서로의 짝이다. 둘 다 비면 즉시 512 로 돌아간다.
 * </pre>
 *
 * <h2>합치기가 O(1) 이다</h2>
 *
 * 빈 자리 목록은 인접한 자리를 찾느라 목록을 훑어야 했다.
 * 여기서는 <b>짝의 주소를 계산으로 구한다.</b>
 *
 * <pre>
 *   짝의 주소 = 주소 XOR 크기
 * </pre>
 *
 * 크기가 2의 거듭제곱이고 주소가 그 배수라 성립한다. 비트 하나만 뒤집으면 짝이다.
 * 18번 비트셋에서 본 "위치 계산을 산술로 바꾸기" 가 여기서 자료구조의 성질이 된다.
 *
 * <h2>대가는 내부 단편화다</h2>
 *
 * 33 바이트를 달라면 64 바이트를 준다. 31 바이트가 <b>쓰이지도 않고 돌려주지도 못한 채</b>
 * 그 안에 갇힌다. 빈 자리 목록 방식에는 없던 낭비다.
 *
 * 그 대신 외부 단편화가 크게 줄어든다. 어느 쪽이 나은지는 요청의 크기 분포가 정한다.
 * 크기가 2의 거듭제곱 근처면 이쪽이, 제각각이면 저쪽이 낫다.
 *
 * <h2>인접한데 못 합치는 자리가 있다</h2>
 *
 * 주소가 붙어 있어도 짝이 아니면 안 합친다. [256,512) 와 [512,768) 은 붙어 있지만
 * 서로의 짝이 아니라 512 짜리로 못 만든다. 그 제약이 O(1) 합치기의 값이다.
 */
public class BuddyAllocator implements Allocator {

    private final int capacity;
    private final int levels;

    /** 크기 지수 -> 그 크기의 빈 자리 주소들. 오름차순이라 답이 하나로 정해진다. */
    private final List<TreeSet<Integer>> freeByLevel = new ArrayList<>();

    /** 주소 -> [떼어준 크기, 요청한 크기]. 둘의 차이가 내부 단편화다. */
    private final Map<Integer, int[]> allocated = new HashMap<>();

    private long splits;
    private long merges;

    /** capacity 는 2의 거듭제곱이어야 한다. 아니면 짝 계산이 성립하지 않는다. */
    public BuddyAllocator(int capacity) {
        if (capacity <= 0 || Integer.bitCount(capacity) != 1) {
            throw new IllegalArgumentException("용량은 2의 거듭제곱이어야 한다: " + capacity);
        }
        this.capacity = capacity;
        this.levels = Integer.numberOfTrailingZeros(capacity);
        for (int i = 0; i <= levels; i++) {
            freeByLevel.add(new TreeSet<>());
        }
        freeByLevel.get(levels).add(0);
    }

    @Override
    public int allocate(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("크기는 1 이상이다: " + size);
        }
        if (size > capacity) {
            return FAIL;
        }
        int level = levelFor(size);

        // 위로 올라가며 쪼갤 수 있는 자리를 찾는다.
        int found = level;
        while (found <= levels && freeByLevel.get(found).isEmpty()) {
            found++;
        }
        if (found > levels) {
            return FAIL;
        }

        int address = freeByLevel.get(found).pollFirst();
        while (found > level) {
            found--;
            splits++;
            // 뒤쪽 반을 빈 자리로 내놓고 앞쪽 반을 계속 쪼갠다.
            freeByLevel.get(found).add(address + (1 << found));
        }
        allocated.put(address, new int[] {1 << level, size});
        return address;
    }

    @Override
    public void free(int address) {
        int[] record = allocated.remove(address);
        if (record == null) {
            throw new IllegalArgumentException("떼어준 적 없거나 이미 돌려받은 주소다: " + address);
        }
        int level = Integer.numberOfTrailingZeros(record[0]);

        // 짝이 비어 있는 동안 계속 합쳐 올라간다.
        while (level < levels) {
            int buddy = address ^ (1 << level);
            if (!freeByLevel.get(level).remove(buddy)) {
                break;      // 짝이 안 비었다. 여기서 멈춘다
            }
            merges++;
            address = Math.min(address, buddy);      // 합친 자리의 시작은 둘 중 앞쪽이다
            level++;
        }
        freeByLevel.get(level).add(address);
    }

    /** size 를 담을 수 있는 가장 작은 2의 거듭제곱의 지수. */
    private int levelFor(int size) {
        int level = 0;
        while ((1 << level) < size) {
            level++;
        }
        return level;
    }

    @Override
    public int capacity() {
        return capacity;
    }

    /** 실제로 떼어준 크기의 합이다. 요청한 크기가 아니다. */
    @Override
    public int usedBytes() {
        int total = 0;
        for (int[] record : allocated.values()) {
            total += record[0];
        }
        return total;
    }

    @Override
    public int freeBytes() {
        return capacity - usedBytes();
    }

    @Override
    public int largestFreeBlock() {
        for (int level = levels; level >= 0; level--) {
            if (!freeByLevel.get(level).isEmpty()) {
                return 1 << level;
            }
        }
        return 0;
    }

    @Override
    public int freeBlockCount() {
        int total = 0;
        for (TreeSet<Integer> set : freeByLevel) {
            total += set.size();
        }
        return total;
    }

    /** 떼어준 것과 요청한 것의 차이. 이 할당자만 0 이 아니다. */
    @Override
    public int wastedBytes() {
        int total = 0;
        for (int[] record : allocated.values()) {
            total += record[0] - record[1];
        }
        return total;
    }

    public long splits() {
        return splits;
    }

    public long merges() {
        return merges;
    }

    /** 지수별 빈 자리 개수. 테스트가 들여다본다. */
    public List<Integer> freeCountsByLevel() {
        List<Integer> out = new ArrayList<>();
        for (TreeSet<Integer> set : freeByLevel) {
            out.add(set.size());
        }
        return List.copyOf(out);
    }

    @Override
    public String toString() {
        return "buddy(" + capacity + ")";
    }
}
