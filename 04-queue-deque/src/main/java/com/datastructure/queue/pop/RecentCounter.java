package com.datastructure.queue.pop;

import java.util.ArrayDeque;
import java.util.Queue;

public class RecentCounter {

    private final static int TIME_WINDOW= 3000;
    private Queue<Integer> message = new ArrayDeque<>();

    public int ping(int t) {
        if (t <= 0) {
            throw new IllegalArgumentException("요청 시간은 양수여야 합니다.");
        }
        message.add(t);
        while (message.peek() < t - TIME_WINDOW) {
            message.remove();
        }
        return message.size(); }
}
