package com.datastructure.sketch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * 스케치로 푸는 문제 둘.
 */
public final class SketchProblems {

    static final double HEAVY_HITTER_EPSILON = 0.001;
    static final double HEAVY_HITTER_DELTA = 0.01;

    /**
     * 순위가 낮은 것이 먼저 나오는 순서. 힙에서는 이 순서의 앞쪽이 버려진다.
     *
     * 빈도가 작은 것이 먼저, 빈도가 같으면 값이 큰 것이 먼저.
     * 최종 출력은 이것을 뒤집은 순서다. 두 순서를 따로 쓰면 어긋나므로 하나만 정의한다.
     */
    static final Comparator<int[]> WORST_FIRST = (a, b) -> {
        if (a[1] != b[1]) {
            return Integer.compare(a[1], b[1]);
        }
        return Integer.compare(b[0], a[0]);
    };

    private SketchProblems() {
    }

    /**
     * 문제 1: 스트림에서 가장 많이 나온 k 개를 빈도 내림차순으로.
     *
     * 반환은 {값, 추정빈도} 의 목록이다. 동점이면 값이 작은 것이 먼저다.
     *
     * 스트림이 30만 개, 종류가 10만 개여도 계수기는 108KB 로 고정이다.
     */
    public static List<int[]> heavyHitters(int[] stream, int k) {
        if (stream == null) {
            throw new IllegalArgumentException("스트림이 없다");
        }
        if (k < 1) {
            throw new IllegalArgumentException("k 는 1 이상이어야 한다: " + k);
        }
        // TODO 1: 스케치로 세고, 후보 k 개를 최소 힙으로 관리한다.
        //
        // 두 번 훑는다.
        //   1) new CountMinSketch(HEAVY_HITTER_EPSILON, HEAVY_HITTER_DELTA) 에 전부 add
        //   2) 다시 훑으며 estimateCount 로 상위 k 개를 고른다
        //
        // **왜 두 번 훑는가.** 스케치는 원소를 담지 않아 스스로 후보를 나열하지 못한다.
        // "가장 큰 칸"을 찾아도 그 칸이 어느 원소의 것인지 알 방법이 없다.
        // 이것이 11번 "원소를 꺼낼 수 없다"와 같은 대가다.
        //
        // 상위 k 개는 07번 KthLargest 와 같은 발상이다. **최소 힙에 k 개만 들고 있는다.**
        // 처음엔 뒤집혀 보이지만, 버려야 할 것이 늘 "가장 나쁜 것"이라 그렇다.
        // WORST_FIRST 로 만든 힙의 머리가 그 버릴 후보다.
        //
        // 함정: 같은 값이 스트림에 여러 번 나온다. 그때마다 힙에 넣으면 중복이 쌓인다.
        // 지금 힙에 있는 값이 무엇인지 **k 개짜리 집합**으로 따로 들고 있어라.
        // (원소 전체를 집합에 넣으면 ExactCounter 와 같아져서 하는 일이 없어진다.
        //  들고 있어도 되는 것은 k 개까지다)
        //
        // 마지막에 힙을 꺼내 WORST_FIRST.reversed() 로 정렬하면 출력 순서가 된다.
        throw new UnsupportedOperationException("TODO 1: heavyHitters");
    }

    /**
     * 문제 2: 샤드마다 따로 세고 병합해서 전체 카디널리티를 구한다.
     *
     * 요점은 원본을 하나도 안 옮긴다는 것이다.
     * 샤드가 각자 2만 5천 개를 들고 있어도 오가는 것은 레지스터 16KB 씩이다.
     */
    public static long distinctAcrossShards(int[][] shards, int p) {
        if (shards == null) {
            throw new IllegalArgumentException("샤드가 없다");
        }
        // TODO 2: 샤드마다 HyperLogLog 를 만들어 채우고, 하나로 merge 한다.
        //
        // 샤드가 null 이면 IllegalArgumentException. 빈 샤드는 길이 0 이어야 한다.
        //
        // 이 함수가 짧다는 것이 요점이다. **merge 가 있으면 분산 집계가 공짜다.**
        // ExactCardinality 로 같은 일을 하려면 샤드마다 원소 전체를 보내야 한다.
        // 8개 샤드 x 2만5천 개 x 4바이트 = 80만 바이트 대 16KB x 8 = 13만 바이트다.
        // 그리고 이 비율은 샤드가 커질수록 벌어진다. 한쪽만 커지기 때문이다.
        //
        // 결과가 "통째로 센 것과 비슷하다"가 아니라 **정확히 같다**는 점을 확인하라.
        // sameAsSinglePass 테스트가 그것을 단언한다.
        //
        // 그리고 바로 그 이유로, 샤드별 스케치를 안 만들고 원소를 전부 merged 에 바로
        // 넣어도 **답이 완전히 같고 테스트 96개가 다 통과한다.** 달라지는 것은 답이 아니라
        // 네트워크로 오가는 바이트 수인데, 한 프로세스 안에서 도는 테스트는 그것을 볼 수 없다.
        // **테스트가 지켜주지 못하는 설계 결정도 있다.**
        throw new UnsupportedOperationException("TODO 2: distinctAcrossShards");
    }
}
