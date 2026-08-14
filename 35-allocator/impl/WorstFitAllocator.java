package com.datastructure.allocator;

import java.util.List;

/**
 * [구현] 맞는 것 중 제일 큰 것.
 *
 * best fit 의 반대다. 남는 조각을 크게 만들어 다음 요청도 받을 수 있게 하자는 생각이다.
 *
 * 이것도 그럴듯하다. 그리고 실제로는 셋 중 대체로 제일 나쁘다.
 * <b>큰 자리를 제일 먼저 깎아먹기 때문이다.</b> 큰 요청이 나중에 오면 받을 자리가 없다.
 *
 * 여기 넣어둔 이유는 이기라고가 아니다. "그럴듯한 이유"가 둘 다 있는데
 * 결과가 갈린다는 것을 보여주려는 것이다. 재기 전에는 어느 쪽이 맞는지 알 수 없다.
 */
public class WorstFitAllocator extends FreeListAllocator {

    public WorstFitAllocator(int capacity) {
        super(capacity);
    }

    @Override
    protected int choose(List<Block> blocks, int size) {
        int worst = -1;
        for (int i = 0; i < blocks.size(); i++) {
            countScan(1);
            int blockSize = blocks.get(i).size;
            if (blockSize < size) {
                continue;
            }
            if (worst < 0 || blockSize > blocks.get(worst).size) {
                worst = i;
            }
        }
        return worst;
    }

    @Override
    public String toString() {
        return "worst fit";
    }
}
