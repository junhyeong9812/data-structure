package com.datastructure.allocator;

import java.util.List;

/**
 * [구현] 맞는 것 중 제일 앞. 기준선이다.
 *
 * 고르는 데 드는 시간이 제일 짧다. 맞는 것을 만나는 순간 멈추기 때문이다.
 *
 * 앞쪽이 잘게 부서지는 성질이 있다. 늘 앞에서부터 떼어가므로 앞부분에
 * 작은 조각이 쌓이고, 그 조각들을 매번 다시 훑게 된다.
 * 실제 구현들이 "지난번에 멈춘 자리부터" 로 고치는 이유가 그것이다(next fit).
 */
public class FirstFitAllocator extends FreeListAllocator {

    public FirstFitAllocator(int capacity) {
        super(capacity);
    }

    @Override
    protected int choose(List<Block> blocks, int size) {
        for (int i = 0; i < blocks.size(); i++) {
            countScan(1);
            if (blocks.get(i).size >= size) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return "first fit";
    }
}
