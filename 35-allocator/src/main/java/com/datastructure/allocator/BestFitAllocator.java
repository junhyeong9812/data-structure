package com.datastructure.allocator;

import java.util.List;

/**
 * 맞는 것 중 제일 작은 것.
 *
 * 큰 자리를 아껴두자는 생각이다. 그럴듯하고, 실제로 그럴듯한 만큼만 맞다.
 *
 * 대신 <b>쓸모없이 작은 조각을 만든다.</b> 100 바이트 자리에서 99 를 떼면
 * 1 바이트가 남는데, 그 1 바이트는 앞으로 거의 아무 요청도 못 받는다.
 * 그런 조각이 쌓여 목록만 길어진다.
 *
 * 그리고 매번 전부 훑어야 한다. 제일 작은 것을 찾으려면 다 봐야 하기 때문이다.
 * first fit 은 만나면 멈춘다. scanned 가 그 차이를 잰다.
 */
public class BestFitAllocator extends FreeListAllocator {

    public BestFitAllocator(int capacity) {
        super(capacity);
    }

    @Override
    protected int choose(List<Block> blocks, int size) {
        // TODO 6: 맞는 것 중 제일 작은 것. 제일 작은 것을 알려면 전부 봐야 한다.
        //
        // 부등호를 뒤집으면 worst fit 이 된다. 답은 여전히 맞고 단편화만 달라진다.
        throw new UnsupportedOperationException("TODO 6: choose");
    }

    @Override
    public String toString() {
        return "best fit";
    }
}
