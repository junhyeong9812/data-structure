package com.datastructure.allocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * 2의 거듭제곱으로만 쪼개고, 짝끼리만 합친다.
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
        // TODO 8: 담을 수 있는 가장 작은 지수를 구하고, 빈 자리가 있는 데까지 올라갔다가
        //         내려오면서 쪼갠다.
        //
        // 쪼갤 때 **뒤쪽 반을 내놓고 앞쪽 반을 계속 쪼갠다.** 반대로 하면 주소가
        // 2의 거듭제곱의 배수가 아니게 되고, 그러면 짝을 XOR 로 못 구한다.
        // 그 손상은 free 를 할 때까지 안 드러난다.
        //
        // 요청한 크기와 실제로 떼어준 크기를 **둘 다** 기록하라. 둘의 차이가 내부 단편화이고,
        // 그것을 안 세면 이 할당자의 유일한 대가가 어떤 자에도 안 잡힌다.
        throw new UnsupportedOperationException("TODO 8: allocate");
    }

    @Override
    public void free(int address) {
        // TODO 9: 짝이 비어 있는 동안 계속 합쳐 올라간다.
        //
        // 짝의 주소는 위 javadoc 의 식 하나로 구한다. 목록을 훑지 않는다.
        // 그것이 이 구조가 O(1) 로 합치는 이유다.
        //
        // 합친 뒤 **시작 주소가 둘 중 앞쪽**이라는 것을 놓치기 쉽다. 자기 주소를 그대로 쓰면
        // 뒤쪽 짝을 돌려줄 때 어긋난 자리가 목록에 들어가고, 다음 합치기부터 전부 틀어진다.
        //
        // 짝이 안 비었으면 거기서 멈춘다. 더 올라가면 남의 자리를 가져간다.
        throw new UnsupportedOperationException("TODO 9: free");
    }

    /** size 를 담을 수 있는 가장 작은 2의 거듭제곱의 지수. */
    private int levelFor(int size) {
        // TODO 10: size 를 담을 수 있는 가장 작은 2의 거듭제곱의 지수.
        //
        // 내림하면 요청보다 작은 자리를 떼어준다. 예외는 안 나고 남의 자리를 밟는다.
        throw new UnsupportedOperationException("TODO 10: levelFor");
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
