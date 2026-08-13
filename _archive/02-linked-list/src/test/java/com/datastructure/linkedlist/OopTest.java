package com.datastructure.linkedlist;

import com.datastructure.linkedlist.oop.LinkedList;
import com.datastructure.linkedlist.oop.SinglyLinkedListImpl;
import com.datastructure.linkedlist.oop.DoublyLinkedListImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("연결 리스트 - OOP 구현 테스트")
class OopTest {

    @Nested
    @DisplayName("단일 연결 리스트")
    class SinglyLinkedListTest {
        
        private LinkedList<Integer> list;

        @BeforeEach
        void setUp() {
            list = new SinglyLinkedListImpl<>();
        }

        @Nested
        @DisplayName("기본 생성")
        class Creation {

            @Test
            @DisplayName("01. 빈 리스트 생성 시 size는 0이다")
            void test01_emptyListSizeIsZero() {
                // TODO: size() 메서드 구현 후 테스트
                // assertThat(list.size()).isEqualTo(0);
            }

            @Test
            @DisplayName("02. 빈 리스트는 isEmpty가 true다")
            void test02_emptyListIsEmpty() {
                // TODO: isEmpty() 메서드 구현 후 테스트
                // assertThat(list.isEmpty()).isTrue();
            }
        }

        @Nested
        @DisplayName("요소 추가")
        class Add {

            @Test
            @DisplayName("03. addFirst로 맨 앞에 추가할 수 있다")
            void test03_addFirst() {
                // TODO: addFirst() 메서드 구현 후 테스트
                // list.addFirst(1);
                // assertThat(list.getFirst()).isEqualTo(1);
            }

            @Test
            @DisplayName("04. addLast로 맨 뒤에 추가할 수 있다")
            void test04_addLast() {
                // TODO: addLast() 메서드 구현 후 테스트
                // list.addLast(1);
                // assertThat(list.getLast()).isEqualTo(1);
            }

            @Test
            @DisplayName("05. 여러 요소를 순서대로 추가할 수 있다")
            void test05_addMultipleElements() {
                // TODO: 구현 후 테스트
                // list.addLast(1);
                // list.addLast(2);
                // list.addLast(3);
                // assertThat(list.size()).isEqualTo(3);
            }

            @Test
            @DisplayName("06. 특정 인덱스에 삽입할 수 있다")
            void test06_addAtIndex() {
                // TODO: add(index, element) 구현 후 테스트
                // list.addLast(1);
                // list.addLast(3);
                // list.add(1, 2);
                // assertThat(list.get(1)).isEqualTo(2);
            }

            @Test
            @DisplayName("07. 맨 앞(index 0)에 삽입할 수 있다")
            void test07_addAtIndexZero() {
                // TODO: add(0, element) 테스트
                // list.addLast(2);
                // list.add(0, 1);
                // assertThat(list.getFirst()).isEqualTo(1);
            }

            @Test
            @DisplayName("08. 맨 뒤(index size)에 삽입할 수 있다")
            void test08_addAtIndexSize() {
                // TODO: add(size, element) 테스트
                // list.addLast(1);
                // list.add(1, 2);
                // assertThat(list.getLast()).isEqualTo(2);
            }
        }

        @Nested
        @DisplayName("요소 조회")
        class Get {

            @Test
            @DisplayName("09. get으로 특정 인덱스 요소를 조회할 수 있다")
            void test09_getByIndex() {
                // TODO: get() 메서드 구현 후 테스트
                // list.addLast(10);
                // list.addLast(20);
                // assertThat(list.get(1)).isEqualTo(20);
            }

            @Test
            @DisplayName("10. getFirst로 첫 요소를 조회할 수 있다")
            void test10_getFirst() {
                // TODO: getFirst() 구현 후 테스트
                // list.addLast(1);
                // list.addLast(2);
                // assertThat(list.getFirst()).isEqualTo(1);
            }

            @Test
            @DisplayName("11. getLast로 마지막 요소를 조회할 수 있다")
            void test11_getLast() {
                // TODO: getLast() 구현 후 테스트
                // list.addLast(1);
                // list.addLast(2);
                // assertThat(list.getLast()).isEqualTo(2);
            }

            @Test
            @DisplayName("12. 음수 인덱스 조회 시 예외가 발생한다")
            void test12_getWithNegativeIndex() {
                // TODO: 예외 처리 구현 후 테스트
                // list.addLast(1);
                // assertThatThrownBy(() -> list.get(-1))
                //     .isInstanceOf(IndexOutOfBoundsException.class);
            }

            @Test
            @DisplayName("13. 범위 초과 인덱스 조회 시 예외가 발생한다")
            void test13_getWithOutOfBoundsIndex() {
                // TODO: 예외 처리 구현 후 테스트
                // list.addLast(1);
                // assertThatThrownBy(() -> list.get(1))
                //     .isInstanceOf(IndexOutOfBoundsException.class);
            }
        }

        @Nested
        @DisplayName("요소 수정")
        class Set {

            @Test
            @DisplayName("14. set으로 특정 인덱스 요소를 수정할 수 있다")
            void test14_setElement() {
                // TODO: set() 메서드 구현 후 테스트
                // list.addLast(1);
                // list.set(0, 100);
                // assertThat(list.get(0)).isEqualTo(100);
            }

            @Test
            @DisplayName("15. set은 이전 값을 반환한다")
            void test15_setReturnsOldValue() {
                // TODO: set() 반환값 구현 후 테스트
                // list.addLast(1);
                // Integer old = list.set(0, 100);
                // assertThat(old).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("요소 삭제")
        class Remove {

            @Test
            @DisplayName("16. removeFirst로 첫 요소를 삭제할 수 있다")
            void test16_removeFirst() {
                // TODO: removeFirst() 구현 후 테스트
                // list.addLast(1);
                // list.addLast(2);
                // list.removeFirst();
                // assertThat(list.getFirst()).isEqualTo(2);
            }

            @Test
            @DisplayName("17. removeLast로 마지막 요소를 삭제할 수 있다")
            void test17_removeLast() {
                // TODO: removeLast() 구현 후 테스트
                // list.addLast(1);
                // list.addLast(2);
                // list.removeLast();
                // assertThat(list.getLast()).isEqualTo(1);
            }

            @Test
            @DisplayName("18. remove(index)로 특정 위치 요소를 삭제할 수 있다")
            void test18_removeByIndex() {
                // TODO: remove(index) 구현 후 테스트
                // list.addLast(1);
                // list.addLast(2);
                // list.addLast(3);
                // list.remove(1);
                // assertThat(list.get(1)).isEqualTo(3);
            }

            @Test
            @DisplayName("19. 빈 리스트에서 removeFirst 시 예외가 발생한다")
            void test19_removeFirstFromEmptyList() {
                // TODO: 예외 처리 구현 후 테스트
                // assertThatThrownBy(() -> list.removeFirst())
                //     .isInstanceOf(NoSuchElementException.class);
            }

            @Test
            @DisplayName("20. 삭제된 요소가 반환된다")
            void test20_removeReturnsElement() {
                // TODO: remove 반환값 테스트
                // list.addLast(100);
                // Integer removed = list.removeFirst();
                // assertThat(removed).isEqualTo(100);
            }
        }

        @Nested
        @DisplayName("검색")
        class Search {

            @Test
            @DisplayName("21. contains로 요소 존재 여부를 확인할 수 있다")
            void test21_contains() {
                // TODO: contains() 구현 후 테스트
                // list.addLast(1);
                // list.addLast(2);
                // assertThat(list.contains(1)).isTrue();
                // assertThat(list.contains(3)).isFalse();
            }

            @Test
            @DisplayName("22. indexOf로 요소의 인덱스를 찾을 수 있다")
            void test22_indexOf() {
                // TODO: indexOf() 구현 후 테스트
                // list.addLast(10);
                // list.addLast(20);
                // list.addLast(30);
                // assertThat(list.indexOf(20)).isEqualTo(1);
            }

            @Test
            @DisplayName("23. 존재하지 않는 요소의 indexOf는 -1이다")
            void test23_indexOfNotFound() {
                // TODO: indexOf() 구현 후 테스트
                // list.addLast(1);
                // assertThat(list.indexOf(999)).isEqualTo(-1);
            }
        }

        @Nested
        @DisplayName("기타 연산")
        class Miscellaneous {

            @Test
            @DisplayName("24. clear로 모든 요소를 삭제할 수 있다")
            void test24_clear() {
                // TODO: clear() 구현 후 테스트
                // list.addLast(1);
                // list.addLast(2);
                // list.clear();
                // assertThat(list.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("25. reverse로 리스트를 뒤집을 수 있다")
            void test25_reverse() {
                // TODO: reverse() 구현 후 테스트
                // list.addLast(1);
                // list.addLast(2);
                // list.addLast(3);
                // list.reverse();
                // assertThat(list.getFirst()).isEqualTo(3);
                // assertThat(list.getLast()).isEqualTo(1);
            }

            @Test
            @DisplayName("26. Iterator로 순회할 수 있다")
            void test26_iterator() {
                // TODO: Iterator 구현 후 테스트
                // list.addLast(1);
                // list.addLast(2);
                // list.addLast(3);
                // int sum = 0;
                // for (Integer i : list) {
                //     sum += i;
                // }
                // assertThat(sum).isEqualTo(6);
            }

            @Test
            @DisplayName("27. String 타입을 저장할 수 있다")
            void test27_stringType() {
                // TODO: 제네릭 테스트
                // LinkedList<String> strList = new SinglyLinkedListImpl<>();
                // strList.addLast("Hello");
                // strList.addLast("World");
                // assertThat(strList.getFirst()).isEqualTo("Hello");
            }
        }
    }

    @Nested
    @DisplayName("이중 연결 리스트")
    class DoublyLinkedListTest {

        private LinkedList<Integer> list;

        @BeforeEach
        void setUp() {
            list = new DoublyLinkedListImpl<>();
        }

        @Test
        @DisplayName("28. removeLast가 O(1)로 동작한다")
        void test28_removeLastO1() {
            // TODO: 이중 연결 리스트의 removeLast() 테스트
            // list.addLast(1);
            // list.addLast(2);
            // list.addLast(3);
            // Integer removed = list.removeLast();
            // assertThat(removed).isEqualTo(3);
            // assertThat(list.getLast()).isEqualTo(2);
        }

        @Test
        @DisplayName("29. 역순 Iterator를 제공한다")
        void test29_reverseIterator() {
            // TODO: descendingIterator() 구현 후 테스트
            // list.addLast(1);
            // list.addLast(2);
            // list.addLast(3);
            // Iterator<Integer> iter = list.descendingIterator();
            // assertThat(iter.next()).isEqualTo(3);
        }

        @Test
        @DisplayName("30. Sentinel 노드로 엣지 케이스가 단순화된다")
        void test30_sentinelNodeSimplification() {
            // TODO: Sentinel 노드 구현 후 테스트
            // 빈 리스트에서 addFirst/addLast가 동일하게 동작
            // list.addFirst(1);
            // assertThat(list.getFirst()).isEqualTo(1);
            // assertThat(list.getLast()).isEqualTo(1);
        }
    }
}
