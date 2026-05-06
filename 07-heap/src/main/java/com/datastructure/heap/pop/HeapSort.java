package com.datastructure.heap.pop;

public class HeapSort {

    public static int[] sortAscending(int[] array) {
        if (array == null || array.length <= 1) return array != null ? array.clone() : new int[0];

        int[] result = array.clone();
        int n = result.length;

        // heapify: MaxHeap 구성
        for (int i = (n - 2) / 2; i >= 0; i--) {
            siftDown(result, i , n);
        }

        // 하나씩 꺼내서 뒤에서부터 채움
        for (int i = n - 1; i > 0; i--) {
            int temp = result[0];
            result[0] = result[i];
            result[i] = temp;
            siftDown(result, 0, i);
        }
        return result; }

    public static int[] sortDescending(int[] array) { return null; }

    private static void siftDown(int[] array, int index, int size) {
        while (true) {
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            int largest = index;

            if (left < size && array[left] > array[largest]) largest = left;
            if (right < size && array[right] > array[largest]) largest = right;
            if (largest == index) break;
            int temp = array[index];
            array[index] = array[largest];
            array[largest] = temp;
            index = largest;
        }
    }
}
