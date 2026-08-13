package com.datastructure.queue.pop;

public class QueueProblems {

    public int[] slidingWindow(int[] nums, int windowsLength) {
        if ( nums.length == 0 ) {
            throw new IllegalArgumentException("배열이 비어있습니다.");
        }
        if ( nums.length < windowsLength ) {
            throw new IllegalArgumentException("윈도우 크기가 배열 길이보다 큽니다.");
        }
        if ( windowsLength == 1 ) {
            return nums;
        }
        int sequence = nums.length - windowsLength + 1;
        int [] result = new int[sequence];
        for (int i = 0; i < sequence; i++) {
            // 배열 i부터 windowsLength까지의 데이터를 비교
            int max = nums[i];
            for (int j= 0; j < windowsLength; j++) {
                if (nums[j + i] > max) {
                    max = nums[j + i];
                }
            }
            result[i] = max;
        }
        return result;
    }
}
