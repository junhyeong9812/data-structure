package com.datastructure.queue;

/**
 * 최근 요청 카운터.
 *
 * 요청이 들어올 때마다 시각(밀리초)을 받아, **최근 3000ms 안에 들어온 요청 수**를 반환한다.
 * 시각은 항상 이전보다 크거나 같게 들어온다.
 *
 *   ping(1)     -> 1        [1]
 *   ping(100)   -> 2        [1, 100]
 *   ping(3001)  -> 3        [1, 100, 3001]        1 은 아직 창 안이다 (3001-3000 = 1)
 *   ping(3002)  -> 3        [100, 3001, 3002]     1 이 창을 벗어났다
 *
 * 이 클래스가 큐를 쓰는 이유
 *   창을 벗어나는 것은 **항상 가장 오래된 것**이다. 그게 곧 FIFO 다.
 *   중간에서 뭔가를 지울 일이 없으므로 큐로 충분하고, 큐여야 O(1) 이다.
 *
 * 이런 형태를 실무에서는 sliding window rate limiter 라고 부른다.
 * 19번 token-bucket 에서 다시 만난다.
 */
public class RecentCounter {

    /** 창 크기(밀리초). 이 시간 안에 들어온 요청만 센다. */
    public static final int WINDOW_MILLIS = 3000;

    private final Queue<Integer> requests;

    /** 어떤 큐 구현을 쓸지는 바깥에서 정한다. 이 클래스는 계약만 안다. */
    public RecentCounter(Queue<Integer> queue) {
        if (!queue.isEmpty()) {
            throw new IllegalArgumentException("비어 있는 큐를 넘겨야 한다");
        }
        this.requests = queue;
    }

    /**
     * 시각 t 에 요청이 들어왔다. 최근 WINDOW_MILLIS 안의 요청 수를 반환한다.
     * 창의 경계는 포함이다. t - WINDOW_MILLIS 시각의 요청은 아직 유효하다.
     *
     * 생각할 것
     *   - 새 요청을 넣기 전에 할 일이 있는가, 넣은 뒤에 할 일이 있는가?
     *   - 창을 벗어난 것을 어디서 빼는가?
     *   - 요청이 n 번 들어오면 전체 비용은 얼마인가? 각 요청은 몇 번 들어가고 몇 번 나오는가?
     *
     * TODO(25): 구현하라.
     */
    public int ping(int t) {
        throw new UnsupportedOperationException("TODO(25): ping");
    }

    /** 현재 창 안에 남아 있는 요청 수. */
    public int size() {
        return requests.size();
    }
}
