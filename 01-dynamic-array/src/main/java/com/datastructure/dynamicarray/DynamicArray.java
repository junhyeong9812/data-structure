package com.datastructure.dynamicarray;

import java.util.Arrays;

/**
 * 크기가 자동으로 늘어나는 배열.
 *
 * 자바 배열은 길이가 고정이다. 이 클래스는 내부에 배열을 두고, 꽉 차면 더 큰 배열을 만들어
 * 옮겨 담는 방식으로 "늘어나는 것처럼" 보이게 한다. ArrayList 가 하는 일이 이것이다.
 *
 * 지켜야 할 계약
 *   - size 는 실제로 담긴 원소의 개수다. capacity(내부 배열 길이)와 다르다.
 *   - 인덱스 접근은 O(1) 이다.
 *   - 중간 삽입/삭제는 뒤쪽을 밀거나 당겨야 하므로 O(n) 이다.
 *   - 맨 뒤 추가는 대부분 O(1) 이고, 확장이 일어나는 순간만 O(n) 이다 (상환 O(1)).
 *
 * 채워져 있는 것과 비어 있는 것
 *   자명한 메서드는 미리 채워두었다. 따라치면서 흐름을 잡는 용도다.
 *   TODO 가 붙은 것이 이 문제의 본체다. 채워야 테스트가 통과한다.
 */
public class DynamicArray<E> {

    /** 처음 만들 때의 내부 배열 크기. */
    private static final int DEFAULT_CAPACITY = 4;

    private Object[] elements;
    private int size;

    public DynamicArray() {
        this(DEFAULT_CAPACITY);
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("초기 용량은 음수일 수 없다: " + initialCapacity);
        }
        this.elements = new Object[initialCapacity];
        this.size = 0;
    }

    // ------------------------------------------------------------------
    // 채워져 있는 부분 (읽고 넘어가면 된다)
    // ------------------------------------------------------------------

    /** 담긴 원소의 개수. 내부 배열의 길이가 아니다. */
    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * 내부 배열의 길이. 보통 외부에 노출하지 않지만,
     * 확장 전략이 의도대로 동작하는지 테스트로 확인하기 위해 열어둔다.
     */
    public int capacity() {
        return elements.length;
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        checkIndex(index);
        return (E) elements[index];
    }

    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    /** 조회용 인덱스 검사. 유효 범위는 0 이상 size 미만. */
    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("인덱스 " + index + ", 크기 " + size);
        }
    }

    /**
     * 삽입용 인덱스 검사. 유효 범위는 0 이상 size 이하다.
     * size 위치에 삽입하는 것은 맨 뒤에 붙이는 것이므로 허용해야 한다.
     */
    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("인덱스 " + index + ", 크기 " + size);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            if (i > 0) sb.append(", ");
            sb.append(elements[i]);
        }
        return sb.append(']').toString();
    }

    // ------------------------------------------------------------------
    // 여기부터가 본체
    // ------------------------------------------------------------------

    /**
     * 최소 minCapacity 만큼은 담을 수 있도록 내부 배열을 키운다.
     * 이미 충분하면 아무것도 하지 않는다.
     *
     * 생각할 것
     *   - 1씩 늘리면 add 를 n 번 할 때 총 복사량이 얼마인가? 배수로 늘리면 얼마인가?
     *   - 그 차이가 add 한 번의 "상환" 비용을 어떻게 바꾸는가?
     *   - 배수 전략이 모든 초기 상태에서 그대로 통하는가?
     *
     * TODO(01): 구현하라. 테스트가 어떤 배수를 기대하는지는 DynamicArrayTest 를 보면 된다.
     */
    private void ensureCapacity(int minCapacity) {
        throw new UnsupportedOperationException("TODO(01): ensureCapacity");
    }

    /**
     * 맨 뒤에 원소를 추가한다.
     *
     * TODO(02): 구현하라. 용량 확보를 잊지 말 것.
     */
    public void add(E element) {
        throw new UnsupportedOperationException("TODO(02): add");
    }

    /**
     * index 위치에 원소를 끼워 넣는다. 그 자리부터 뒤에 있던 것들은 한 칸씩 뒤로 밀린다.
     *
     * 생각할 것
     *   - 밀 때 앞에서부터 복사하면 아직 안 읽은 값을 덮어쓴다. 어느 방향으로 옮겨야 하는가?
     *   - 이 메서드도 용량이 부족할 수 있다.
     *   - index == size 이면 맨 뒤 추가와 같다. 이것도 허용해야 한다.
     *
     * TODO(03): 구현하라.
     */
    public void add(int index, E element) {
        throw new UnsupportedOperationException("TODO(03): add(index, element)");
    }

    /**
     * index 위치의 원소를 새 값으로 바꾸고 이전 값을 반환한다.
     *
     * TODO(04): 구현하라.
     */
    @SuppressWarnings("unchecked")
    public E set(int index, E element) {
        throw new UnsupportedOperationException("TODO(04): set");
    }

    /**
     * index 위치의 원소를 제거하고 그 값을 반환한다. 뒤에 있던 것들은 한 칸씩 앞으로 당겨진다.
     *
     * 생각할 것
     *   - 범위 밖 인덱스가 들어오면? index == size 는 배열 범위 안이라 그냥 두면 예외조차 안 난다.
     *   - 당기고 난 뒤 마지막 칸에는 무엇이 남는가. 그대로 둬도 되는가.
     *
     * TODO(05): 구현하라. 지운 뒤에는 maybeShrink() 를 부른다.
     */
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        throw new UnsupportedOperationException("TODO(05): remove(index)");
    }

    /**
     * 값이 같은 첫 번째 원소를 제거한다. 제거했으면 true.
     *
     * TODO(06): 구현하라. indexOf 를 활용할 수 있다.
     */
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("TODO(06): remove(Object)");
    }

    /**
     * 값이 같은 첫 번째 원소의 인덱스. 없으면 -1.
     *
     * 생각할 것
     *   - null 도 담길 수 있다. null 을 찾을 때 equals 를 부르면 NPE 가 난다.
     *
     * TODO(07): 구현하라.
     */
    public int indexOf(Object o) {
        throw new UnsupportedOperationException("TODO(07): indexOf");
    }

    /**
     * 모든 원소를 비운다. 내부 배열의 용량은 유지한다.
     *
     * 생각할 것
     *   - size 를 0 으로 만드는 것만으로 충분한가?
     *
     * TODO(08): 구현하라.
     */
    public void clear() {
        throw new UnsupportedOperationException("TODO(08): clear");
    }


    /**
     * 너무 헐거워졌으면 내부 배열을 줄인다.
     *
     * 늘리기만 하고 안 줄이면, 한 번 커진 배열은 원소를 다 빼도 그 크기 그대로 메모리를 붙잡는다.
     *
     * 규칙 (테스트가 이걸 그대로 검사한다)
     *   - size 가 capacity 의 1/4 이하로 떨어지면 capacity 를 절반으로 줄인다.
     *   - 단 절반이 {@link #DEFAULT_CAPACITY} 보다 작아지면 줄이지 않는다.
     *
     * 생각할 것
     *   - 왜 1/2 이 아니라 1/4 에서 줄이는가?
     *     capacity 가 16 이고 size 가 8 인 상태에서 add 와 remove 를 번갈아 한다고 해보자.
     *     1/2 규칙이면 remove 할 때마다 줄이고 add 할 때마다 다시 늘린다.
     *     연산 하나하나가 O(n) 전체 복사가 되고, 상환 O(1) 이 무너진다.
     *     늘리는 문턱과 줄이는 문턱 사이에 여유를 두어야 이 왕복이 안 생긴다.
     *     이걸 thrashing 이라고 부른다.
     *
     * TODO(15): 구현하라.
     */
    private void maybeShrink() {
        throw new UnsupportedOperationException("TODO(15): maybeShrink");
    }

    /**
     * 담긴 원소만 담은 새 배열을 반환한다.
     *
     * TODO(09): 구현하라.
     */
    public Object[] toArray() {
        throw new UnsupportedOperationException("TODO(09): toArray");
    }
}
