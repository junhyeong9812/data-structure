package com.datastructure.linkedlist;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * List 계약 테스트 - 순회와 뒤집기.
 *
 * ListContractTest 를 물려받으므로 기본 연산도 함께 검사된다.
 * 한 파일에 다 넣으면 250줄을 넘어 따라치기 어려워지므로 나눴다.
 */
abstract class ListIterationContractTest extends ListContractTest {

    private List<Integer> of(int... values) {
        List<Integer> list = create();
        for (int v : values) list.add(v);
        return list;
    }

    @Nested
    @DisplayName("반복자")
    class Iteration {

        @Test
        @DisplayName("for-each 로 순서대로 훑는다")
        void iteratesInOrder() {
            java.util.List<Integer> seen = new ArrayList<>();
            for (int v : of(1, 2, 3)) seen.add(v);
            assertEquals(java.util.List.of(1, 2, 3), seen);
        }

        @Test
        @DisplayName("빈 리스트는 바로 끝난다")
        void emptyIteratesNothing() {
            Iterator<Integer> it = of().iterator();
            assertFalse(it.hasNext());
            assertThrows(NoSuchElementException.class, it::next);
        }

        @Test
        @DisplayName("next 를 끝까지 부르면 예외가 난다")
        void throwsPastEnd() {
            Iterator<Integer> it = of(1).iterator();
            assertEquals(1, it.next());
            assertFalse(it.hasNext());
            assertThrows(NoSuchElementException.class, it::next);
        }

        @Test
        @DisplayName("순회하면서 지울 수 있다")
        void removesDuringIteration() {
            List<Integer> list = of(1, 2, 3, 4);
            Iterator<Integer> it = list.iterator();
            while (it.hasNext()) {
                if (it.next() % 2 == 0) it.remove();
            }
            assertArrayEquals(new Object[]{1, 3}, list.toArray());
            assertEquals(2, list.size(), "지운 만큼 크기가 줄어야 한다");
        }

        @Test
        @DisplayName("맨 앞과 맨 뒤도 순회 중에 지울 수 있다")
        void removesAtBothEnds() {
            List<Integer> list = of(1, 2, 3);
            Iterator<Integer> it = list.iterator();
            it.next();
            it.remove();                     // 첫 원소
            assertArrayEquals(new Object[]{2, 3}, list.toArray());

            it.next();
            it.next();
            it.remove();                     // 마지막 원소
            assertArrayEquals(new Object[]{2}, list.toArray());

            list.add(9);                     // tail 이 제대로 갱신됐는지
            assertArrayEquals(new Object[]{2, 9}, list.toArray());
        }

        @Test
        @DisplayName("전부 지울 수도 있다")
        void removesEverything() {
            List<Integer> list = of(1, 2, 3);
            Iterator<Integer> it = list.iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
            assertTrue(list.isEmpty());

            list.add(7);
            assertArrayEquals(new Object[]{7}, list.toArray());
        }

        @Test
        @DisplayName("next 없이 remove 하거나 연속으로 remove 하면 예외다")
        void rejectsIllegalRemove() {
            Iterator<Integer> it = of(1, 2).iterator();
            assertThrows(IllegalStateException.class, it::remove, "next 를 먼저 불러야 한다");

            it.next();
            it.remove();
            assertThrows(IllegalStateException.class, it::remove, "연속으로 두 번은 안 된다");
        }
    }

    @Nested
    @DisplayName("뒤집기")
    class Reverse {

        @Test
        void reversesOrder() {
            List<Integer> list = of(1, 2, 3, 4);
            list.reverse();
            assertArrayEquals(new Object[]{4, 3, 2, 1}, list.toArray());
        }

        @Test
        @DisplayName("뒤집은 뒤에도 인덱스 접근과 추가가 정상이다")
        void staysUsableAfterReverse() {
            List<Integer> list = of(1, 2, 3);
            list.reverse();

            assertEquals(3, list.get(0));
            assertEquals(1, list.get(2));

            list.add(0);                     // tail 이 제대로 바뀌었는지
            assertArrayEquals(new Object[]{3, 2, 1, 0}, list.toArray());
            assertEquals(4, list.size());
        }

        @Test
        @DisplayName("두 번 뒤집으면 원래대로다")
        void doubleReverseIsIdentity() {
            List<Integer> list = of(1, 2, 3, 4, 5);
            list.reverse();
            list.reverse();
            assertArrayEquals(new Object[]{1, 2, 3, 4, 5}, list.toArray());
        }

        @Test
        void handlesSmallSizes() {
            List<Integer> one = of(7);
            one.reverse();
            assertArrayEquals(new Object[]{7}, one.toArray());

            List<Integer> empty = of();
            assertDoesNotThrow(empty::reverse);
            assertEquals(0, empty.size());
        }
    }
}
