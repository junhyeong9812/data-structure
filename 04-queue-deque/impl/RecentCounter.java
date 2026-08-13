package com.datastructure.queue;

/**
 * [구현] 최근 요청 카운터.
 *
 * 창을 벗어나는 것은 항상 가장 오래된 것이므로 FIFO 로 충분하다.
 */
public class RecentCounter {

    public static final int WINDOW_MILLIS = 3000;

    private final Queue<Integer> requests;

    public RecentCounter(Queue<Integer> queue) {
        if (!queue.isEmpty()) {
            throw new IllegalArgumentException("비어 있는 큐를 넘겨야 한다");
        }
        this.requests = queue;
    }

    /**
     * 넣고 나서 버린다.
     *
     * 경계가 포함이므로 조건은 `< t - WINDOW` 다. `<=` 로 쓰면 딱 3000ms 전 요청이 잘못 빠진다.
     *
     * 각 요청은 한 번 들어가고 최대 한 번 나온다. n 번 호출의 총비용이 O(n) 이다.
     * while 이 한 번에 여러 개를 버려도 전체를 통틀면 pop 횟수가 n 을 넘지 않는다.
     */
    public int ping(int t) {
        requests.enqueue(t);
        while (requests.peek() < t - WINDOW_MILLIS) {
            requests.dequeue();
        }
        return requests.size();
    }

    public int size() {
        return requests.size();
    }
}
