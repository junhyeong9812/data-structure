package com.datastructure.queue.oop;

public class SlidingWindowMax {
    public int[] calculate(int[] nums, int windowsLength) {
        if (nums.length == 0) {
            throw new IllegalArgumentException();
        }
        if (nums.length < windowsLength) {
            throw new IllegalArgumentException();
        }
        int count = nums.length - windowsLength + 1;
        int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            int max = nums[i];
            for (int j = 0; j < windowsLength; j++) {
                if (nums[i+j] > max) {
                    max = nums[i+j];
                }
            }
            result[i] = max;
        }
        return result;
    }
}
