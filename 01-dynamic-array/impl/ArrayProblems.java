package com.datastructure.dynamicarray;

import java.util.function.Predicate;

/**
 * [구현] 동적 배열 응용 문제.
 *
 * 다섯 문제 중 넷이 같은 기법을 쓴다. 읽는 위치와 쓰는 위치를 따로 두는 것이다.
 * 이 기법을 두 포인터(two pointer) 라고 부른다. 배열을 다루는 문제의 절반은 여기서 나온다.
 *
 * 계약: 정렬을 다루는 문제(1, 3, 5)의 입력에는 null 이 들어오지 않는다고 가정한다.
 */
public final class ArrayProblems {

    private ArrayProblems() {
    }

    /**
     * 문제 1. 정렬된 배열 중복 제거.
     *
     * read 는 계속 앞으로 가고, write 는 "살릴 값"을 만났을 때만 앞으로 간다.
     * 정렬되어 있으므로 직전에 살린 값(write-1)과 다르면 새 값이다.
     */
    public static int removeDuplicatesSorted(DynamicArray<Integer> array) {
        if (array.isEmpty()) {
            return 0;
        }
        int write = 1;
        for (int read = 1; read < array.size(); read++) {
            if (!array.get(read).equals(array.get(write - 1))) {
                array.set(write, array.get(read));
                write++;
            }
        }
        truncate(array, write);
        return write;
    }

    /**
     * 문제 2. k 칸 오른쪽 회전.
     *
     * 세 번 뒤집기로 푼다. 추가 배열이 필요 없다.
     *   [1,2,3,4,5] k=2
     *   전체 뒤집기      -> [5,4,3,2,1]
     *   앞 k 개 뒤집기    -> [4,5,3,2,1]
     *   나머지 뒤집기     -> [4,5,1,2,3]
     *
     * k 가 음수이거나 size 보다 클 수 있으므로 먼저 정규화한다.
     * 자바의 % 는 음수에 대해 음수를 주므로 ((k % n) + n) % n 으로 0 이상으로 만든다.
     */
    public static <E> void rotate(DynamicArray<E> array, int k) {
        int n = array.size();
        if (n == 0) {
            return;
        }
        int shift = ((k % n) + n) % n;
        if (shift == 0) {
            return;
        }
        reverse(array, 0, n - 1);
        reverse(array, 0, shift - 1);
        reverse(array, shift, n - 1);
    }

    /**
     * 문제 3. 정렬 유지 삽입.
     *
     * "값보다 큰 첫 위치"(upper bound)를 이진 탐색으로 찾는다.
     * `<=` 로 비교해야 같은 값들의 뒤에 들어간다. `<` 로 하면 앞에 들어간다.
     *
     * 탐색은 O(log n) 이지만 삽입 자체가 O(n) 시프트라 전체는 O(n) 이다.
     * 이진 탐색이 전체 복잡도를 바꾸지는 못한다. 그래도 비교 횟수는 크게 줄어든다.
     */
    public static int insertSorted(DynamicArray<Integer> array, int value) {
        int lo = 0;
        int hi = array.size();
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (array.get(mid) <= value) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        array.add(lo, value);
        return lo;
    }

    /**
     * 문제 4. 조건부 일괄 삭제.
     *
     * 핵심은 지우지 않는 것이다. 살릴 값만 앞으로 모으고 마지막에 뒤를 한 번에 잘라낸다.
     * remove(i) 를 반복하면 매번 O(n) 시프트라 O(n^2) 이 된다.
     *
     * truncate 는 맨 뒤에서만 지우므로 시프트가 없다. 한 번에 O(1) 이고 전체 O(n) 이다.
     */
    public static <E> int removeAllIf(DynamicArray<E> array, Predicate<? super E> predicate) {
        int n = array.size();
        int write = 0;
        for (int read = 0; read < n; read++) {
            E value = array.get(read);
            if (!predicate.test(value)) {
                if (write != read) {
                    array.set(write, value);
                }
                write++;
            }
        }
        truncate(array, write);
        return n - write;
    }

    /**
     * 문제 5. 정렬된 두 배열 병합.
     *
     * 양쪽에 포인터를 두고 작은 쪽을 먼저 담는다. 이미 정렬되어 있으니 O(n+m) 이다.
     * 결과 크기를 미리 알 수 있으므로 그만큼 용량을 잡고 시작한다. 확장이 한 번도 안 일어난다.
     */
    public static DynamicArray<Integer> merge(DynamicArray<Integer> a, DynamicArray<Integer> b) {
        DynamicArray<Integer> result = new DynamicArray<>(a.size() + b.size());
        int i = 0;
        int j = 0;
        while (i < a.size() && j < b.size()) {
            if (a.get(i) <= b.get(j)) {
                result.add(a.get(i++));
            } else {
                result.add(b.get(j++));
            }
        }
        while (i < a.size()) {
            result.add(a.get(i++));
        }
        while (j < b.size()) {
            result.add(b.get(j++));
        }
        return result;
    }

    // ------------------------------------------------------------------

    /** 뒤에서부터 지운다. 맨 뒤 삭제는 시프트가 없어 O(1) 이다. */
    private static void truncate(DynamicArray<?> array, int newSize) {
        while (array.size() > newSize) {
            array.remove(array.size() - 1);
        }
    }

    private static <E> void reverse(DynamicArray<E> array, int from, int to) {
        while (from < to) {
            E tmp = array.get(from);
            array.set(from, array.get(to));
            array.set(to, tmp);
            from++;
            to--;
        }
    }
}
