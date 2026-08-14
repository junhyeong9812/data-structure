package com.datastructure.spatial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 가장 가까운 k 개를 유지하는 상자. 07번 힙을 쓰는 자리다.
 *
 * <h2>왜 최대 힙인가</h2>
 *
 * 가까운 것을 찾는데 최대 힙이라 거꾸로 보인다. 그런데 여기서 자주 하는 질문은
 * "가장 가까운 게 뭐냐"가 아니라 "지금 들고 있는 k 개 중 가장 먼 것보다 가까우냐"다.
 * 새 후보가 그것보다 멀면 볼 것도 없이 버리고, 가까우면 가장 먼 것을 빼고 넣는다.
 * 그 가장 먼 것을 O(1) 에 보려면 머리에 최댓값이 있어야 한다.
 *
 * <h2>그리고 그 머리가 가지치기 반경이다</h2>
 *
 * 이게 진짜 이유다. k 개가 다 찼으면 그 머리까지의 거리가 곧 "이보다 먼 곳은 볼 필요 없다"는
 * 경계선이 된다. radius() 가 그 값이다. 아직 k 개가 안 찼으면 아무것도 버릴 수 없으므로
 * 무한대를 준다. 힙을 안 쓰고 전부 모아서 마지막에 정렬하면 이 반경을 탐색 중에 알 수 없다.
 *
 * 전수 조사(NaiveSpatialIndex)는 어차피 다 볼 것이라 그냥 정렬한다. O(n log n) 이다.
 * 트리는 힙에 k 개만 들고 O(n log k) 로 가면서, 덤으로 반경을 얻는다.
 */
final class KNearest {

    private final Point2D target;
    private final int k;
    private final PriorityQueue<Point2D> heap;

    KNearest(Point2D target, int k) {
        if (target == null) throw new IllegalArgumentException("target 이 null 이다");
        if (k < 1) throw new IllegalArgumentException("k 는 1 이상이어야 한다: " + k);
        this.target = target;
        this.k = k;
        // 비교자가 뒤집혀 있다. target 에서 먼 것이 머리로 온다.
        this.heap = new PriorityQueue<>(
                (a, b) -> Long.compare(target.squaredDistanceTo(b), target.squaredDistanceTo(a)));
    }

    /** 후보를 하나 넣어본다. k 개 안에 들면 들어가고 아니면 버려진다. */
    void offer(Point2D candidate) {
        // TODO 5: 세 경우를 나눠라.
        //
        //   1. 아직 k 개가 안 찼다        -> 그냥 넣는다
        //   2. 찼는데 머리보다 가깝다      -> 머리를 빼고(poll) 넣는다
        //   3. 찼는데 머리보다 멀거나 같다 -> 아무것도 안 한다
        //
        // **1번을 빼먹는 것이 여기서 제일 위험하다.** 빈 힙에서 peek() 은 null 이라
        // 첫 후보에서 NullPointerException 이 난다.
        //
        // 2번의 순서는 **정직하게 말하면 어느 쪽이든 답이 같다.** 넣고 나서 빼도
        // 이미 "머리보다 가깝다"를 확인했으니 방금 넣은 것이 머리가 될 수 없기 때문이다.
        // (변종으로 돌려보면 112개가 다 통과한다) 다만 힙이 잠깐 k+1 개가 되어 한 번 더
        // 자리를 잡는다. 빼고 넣는 편이 싸다.
        //
        // 3번에서 "같으면 교체"로 써도 답의 거리 수열은 같다. 역시 일만 늘어난다.
        // (동점이면 어느 점을 담든 맞는 답이다. 그래서 테스트가 거리를 비교한다)
        throw new UnsupportedOperationException("TODO 5: offer");
    }

    /**
     * 가지치기 반경(제곱). k 개가 다 찼으면 그중 가장 먼 것까지의 제곱거리,
     * 아직 덜 찼으면 Long.MAX_VALUE.
     *
     * 덜 찼을 때 무한대를 주는 것이 중요하다. 그래야 트리가 아무 가지도 안 버리고
     * 일단 k 개를 채운다. 여기에 0 을 주면 첫 잎에서 탐색이 멈춘다.
     */
    long radius() {
        return heap.size() < k ? Long.MAX_VALUE : target.squaredDistanceTo(heap.peek());
    }

    int size() {
        return heap.size();
    }

    /** 가까운 순으로 꺼낸다. 힙 안의 순서는 정렬된 순서가 아니므로 여기서 정렬한다. */
    List<Point2D> drain() {
        List<Point2D> out = new ArrayList<>(heap);
        out.sort(Comparator.comparingLong(target::squaredDistanceTo));
        return out;
    }
}
