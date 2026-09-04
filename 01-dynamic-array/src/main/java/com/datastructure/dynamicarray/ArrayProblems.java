package com.datastructure.dynamicarray;

import java.util.function.Predicate;

/**
 * 동적 배열로 푸는 응용 문제들.
 *
 * 자료구조를 만들고 나면 "그래서 이걸로 뭘 하는가"가 남는다.
 * 여기 있는 문제들은 전부 배열의 성질(연속 저장, O(1) 인덱스 접근, O(n) 시프트)을
 * 직접 이용해야 풀리는 것들이다.
 *
 * 특히 4번은 이 자료구조의 성능 함정을 그대로 보여준다. 순진하게 짜면 통과하지 못한다.
 *
 * 계약: 정렬을 다루는 문제(1, 3, 5)의 입력에는 null 이 들어오지 않는다고 가정한다.
 * DynamicArray 자체는 null 을 담을 수 있지만, "오름차순"이라는 조건이 null 의 순서를 정의하지 않는다.
 */
public final class ArrayProblems {

    private ArrayProblems() {
    }

    /**
     * 문제 1. 정렬된 배열에서 중복 제거 (제자리)
     *
     * 오름차순으로 정렬된 배열이 주어진다. 중복을 없애고 남은 개수를 반환한다.
     * 새 배열을 만들지 말고 주어진 배열을 직접 줄여야 한다.
     *
     *   [1, 1, 2, 2, 2, 3]  ->  [1, 2, 3],  반환 3
     *
     * 생각할 것
     *   - 정렬되어 있다는 조건을 어디에 쓸 수 있는가?
     *   - 한 번의 순회로 끝낼 수 있는가?
     *
     * TODO(10): 구현하라.
     */
    public static int removeDuplicatesSorted(DynamicArray<Integer> array) {
        if (array.size() == 0) return 0;
        int writePlace = 1;
        for (int read = 0; read < array.size(); read++) {
            if (!array.get(read).equals(array.get(writePlace - 1))) {
                array.set(writePlace, array.get(read));
                writePlace++;
            }
        }
        for (int i = array.size() - 1; i >= writePlace; i--) {
            array.remove(i);
        }
        return array.size();
    }

    /**
     * 문제 2. k 칸 오른쪽으로 회전
     *
     *   [1, 2, 3, 4, 5], k=2  ->  [4, 5, 1, 2, 3]
     *
     * 생각할 것
     *   - k 가 size 보다 클 수 있다. k 가 음수일 수도 있다.
     *   - 임시 배열을 하나 써서 옮겨도 되고, 세 번 뒤집는 방법도 있다.
     *     (전체 뒤집기 -> 앞 k 개 뒤집기 -> 나머지 뒤집기)
     *
     * TODO(11): 구현하라.
     */
    public static <E> void rotate(DynamicArray<E> array, int k) {
        throw new UnsupportedOperationException("TODO(11): rotate");
    }

    /**
     * 문제 3. 정렬을 유지하며 삽입
     *
     * 오름차순 배열에 값을 넣되 정렬이 깨지지 않게 한다. 삽입된 위치를 반환한다.
     * 같은 값이 이미 있으면 그 뒤에 넣는다.
     *
     *   [1, 3, 5], 4  ->  [1, 3, 4, 5],  반환 2
     *
     * 생각할 것
     *   - 위치를 찾는 데 이진 탐색을 쓰면 O(log n) 이다.
     *     그런데 삽입 자체가 O(n) 시프트다. 전체 복잡도는 결국 무엇인가?
     *
     * TODO(12): 구현하라.
     */
    public static int insertSorted(DynamicArray<Integer> array, int value) {
        throw new UnsupportedOperationException("TODO(12): insertSorted");
    }

    /**
     * 문제 4. 조건에 맞는 원소를 모두 제거 (이 문제집의 핵심)
     *
     * predicate 가 true 인 원소를 전부 없애고, 제거한 개수를 반환한다.
     *
     * 함정
     *   가장 먼저 떠오르는 방법은 훑으면서 remove(i) 를 부르는 것이다. 정답은 나온다.
     *   그런데 remove 한 번의 비용이 얼마인가? 그걸 n 번 부르면 총 비용은?
     *   테스트에 100만 개짜리 케이스가 있고 시간 제한이 걸려 있다.
     *
     * TODO(13): 구현하라. O(n) 이어야 한다.
     */
    public static <E> int removeAllIf(DynamicArray<E> array, Predicate<? super E> predicate) {
        throw new UnsupportedOperationException("TODO(13): removeAllIf");
    }

    /**
     * 문제 5. 정렬된 두 배열 병합
     *
     * 오름차순 배열 둘을 합쳐 오름차순 새 배열로 만든다. 원본은 건드리지 않는다.
     *
     *   [1, 3, 5] + [2, 3, 6]  ->  [1, 2, 3, 3, 5, 6]
     *
     * 생각할 것
     *   - 합쳐서 정렬하면 O((n+m) log(n+m)) 이다. 이미 정렬되어 있다는 조건을 쓰면 O(n+m) 으로 된다.
     *   - 결과 배열의 최종 크기를 미리 알 수 있다. 그러면 확장이 몇 번 일어나는가?
     *
     * TODO(14): 구현하라.
     */
    public static DynamicArray<Integer> merge(DynamicArray<Integer> a, DynamicArray<Integer> b) {
        throw new UnsupportedOperationException("TODO(14): merge");
    }
}
