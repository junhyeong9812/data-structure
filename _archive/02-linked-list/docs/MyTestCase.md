# MyTestCase

02-linked-list의 단방향/양방향 연결 리스트 테스트. 두 구현이 같은 `LinkedList<E>` 인터페이스를 구현하므로 한 `MyTestCase` 안에 두 최상위 `@Nested`(`SinglyLinkedListTest`, `DoublyLinkedListTest`)로 통합되어 있다. POP 구현 테스트는 `setUp`에서 구현체만 바꾸면 동일하게 재사용 가능 (주석 라인 참조).

```java
package com.datastructure.linkedlist;

import com.datastructure.linkedlist.oop.DoublyLinkedListImpl;
import com.datastructure.linkedlist.oop.LinkedList;
import com.datastructure.linkedlist.oop.SinglyLinkedListImpl;
import com.datastructure.linkedlist.pop.DoublyLinkedList;
import com.datastructure.linkedlist.pop.SinglyLinkedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

public class MyTestCase {

    @Nested
    @DisplayName("단일 연결 리스트")
    class SinglyLinkedListTest {

//        private SinglyLinkedList<Object> list;
        private LinkedList<Object> list;
        @BeforeEach
        void setUp() {
//            list = new SinglyLinkedList<>();
            list = new SinglyLinkedListImpl<>();
        }

        @Nested
        @DisplayName("단일 연결 리스트 생성")
        class Creation {
            @Test
            @DisplayName("단일 연결 리스트가 생성된다.")
            void creationListTest() {
                assertThat(list).isNotNull();
            }
            @Test
            @DisplayName("단일 연결 리스트 생성 시 size는 0이다.")
            void sizeIsZeroTest() {
                assertThat(list.size()).isEqualTo(0);
            }
            @Test
            @DisplayName("단일 연결 리스트 생성 시 isEmpty는 true다.")
            void isEmptyIsTrueTest() {
                assertThat(list.isEmpty()).isEqualTo(true);
            }
        }

        @Nested
        @DisplayName("addFirst 메서드 테스트")
        class AddFirstTest {
            @Test
            @DisplayName("빈 리스트에 추가하는 경우")
            void addFirst_blank_addsToFront() {
                list.addFirst(1);
                assertThat(list.get(0)).isEqualTo(1);
                assertThat(list.size()).isEqualTo(1);
            }
            @Test
            @DisplayName("추가하는 요소가 null인 경우")
            void addFirst_Null_addsToFront() {
                list.addFirst(null);
                assertThat(list.get(0)).isEqualTo(null);
                assertThat(list.size()).isEqualTo(1);
            }
            @Test
            @DisplayName("데이터가 1개일 경우")
            void addFirst_whenOneElement_addToFront() {
                list.addFirst(1);
                list.addFirst(2);
                assertThat(list.get(0)).isEqualTo(2);
                assertThat(list.size()).isEqualTo(2);
            }
            @Test
            @DisplayName("데이터가 여러개일 경우")
            void addFirst_whenMultiElement_addToFront() {
                list.addFirst(1);
                list.addFirst(2);
                list.addFirst(3);
                assertThat(list.get(0)).isEqualTo(3);
                assertThat(list.size()).isEqualTo(3);
            }
        }

        @Nested
        @DisplayName("addLast 메서드 테스트")
        class AddLastTest {
            @Test
            @DisplayName("빈 리스트에 추가하는 경우")
            void addLast_blank_addsToBack() {
                list.addLast(1);
                assertThat(list.get(0)).isEqualTo(1);
                assertThat(list.size()).isEqualTo(1);
            }

            @Test
            @DisplayName("요소가 null인 경우")
            void addLast_null_addsToBack() {
                list.addLast(null);
                assertThat(list.get(0)).isEqualTo(null);
                assertThat(list.size()).isEqualTo(1);
            }

            @Test
            @DisplayName("데이터가 1개일 경우")
            void addLast_whenOneElement_addToBack() {
                list.addLast(1);
                list.addLast(2);
                assertThat(list.get(1)).isEqualTo(2);
                assertThat(list.size()).isEqualTo(2);
            }

            @Test
            @DisplayName("데이터가 여러개일 경우")
            void addLast_whenMultiElement_addToBack() {
                list.addLast(1);
                list.addLast(2);
                list.addLast(3);
                assertThat(list.get(2)).isEqualTo(3);
                assertThat(list.size()).isEqualTo(3);
            }
        }

        @Nested
        @DisplayName("add 메서드 테스트")
        class AddTest {
            @Test
            @DisplayName("빈 리스트에 추가하는 경우 (index = 0)")
            void add_blankList() {
                list.add(0,1);
                assertThat(list.get(0)).isEqualTo(1);
                assertThat(list.size()).isEqualTo(1);
            }
            @Test
            @DisplayName("요소가 null인 경우")
            void add_whenNullElement_addToIndex() {
                list.add(0, null);
                assertThat(list.get(0)).isEqualTo(null);
                assertThat(list.size()).isEqualTo(1);
            }
            @Test
            @DisplayName("데이터가 1개일 경우")
            void add_whenOneElement_addToIndex() {
                list.addFirst(0);
                list.add(0,1);
                assertThat(list.get(0)).isEqualTo(1);
                assertThat(list.size()).isEqualTo(2);
            }
            @Test
            @DisplayName("데이터가 여러개인 경우")
            void add_whenMultiElement_addToIndex() {
                list.addFirst(0);
                list.addFirst(1);
                list.add(1,2);
                assertThat(list.get(1)).isEqualTo(2);
                assertThat(list.get(2)).isEqualTo(0);
                assertThat(list.size()).isEqualTo(3);
            }
            @Test
            @DisplayName("맨 앞에 추가하는 경우 (index = 0)")
            void add_atFront_success() {
                list.addFirst(1);
                list.add(0,0);
                assertThat(list.get(0)).isEqualTo(0);
                assertThat(list.size()).isEqualTo(2);
            }
            @Test
            @DisplayName("중간에 추가하는 경우")
            void add_atMiddle_success() {
                list.addFirst(0);
                list.addLast(2);
                list.add(1,1);
                assertThat(list.get(1)).isEqualTo(1);
                assertThat(list.size()).isEqualTo(3);
            }
            @Test
            @DisplayName("맨 뒤에 추가하는 경우 (index = size)")
            void add_atEnd_success() {
                list.addFirst(1);
                list.addFirst(0);
                list.add(2,2);
                assertThat(list.get(2)).isEqualTo(2);
                assertThat(list.size()).isEqualTo(3);
            }
            @Test
            @DisplayName("음수 인덱스인 경우")
            void add_negativeIndex_throwsException() {
                list.addFirst(0);
                list.addLast(1);
                assertThatThrownBy(() -> list.add(-1,3))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
            @Test
            @DisplayName("size보다 큰 인덱스인 경우")
            void add_indexOverSize_throwsException() {
                list.addFirst(0);
                assertThatThrownBy(() -> list.add(2,2))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
        }

        @Nested
        @DisplayName("removeFirst 메서드 테스트")
        class RemoveFirstTest {
            @Test
            @DisplayName("데이터가 1개일 경우")
            void removeFirst_oneElement_success() {
                list.addFirst(1);
                Object removed= list.removeFirst();
                assertThat(removed).isEqualTo(1);
                assertThat(list.size()).isEqualTo(0);
                assertThat(list.isEmpty()).isEqualTo(true);
            }
            @Test
            @DisplayName("데이터가 여러개일 경우")
            void removeFirst_multipleElements_success() {
                list.addFirst(1);
                list.addFirst(0);
                Object removed = list.removeFirst();
                assertThat(removed).isEqualTo(0);
                assertThat(list.size()).isEqualTo(1);
                assertThat(list.get(0)).isEqualTo(1);
            }
            @Test
            @DisplayName("빈 리스트에서 삭제하는 경우")
            void removeFirst_emptyList_throwsException() {
                assertThatThrownBy(() -> list.removeFirst())
                        .isInstanceOf(NoSuchElementException.class);
            }
        }

        @Nested
        @DisplayName("removeLast 메서드 테스트")
        class RemoveLastTest {
            @Test
            @DisplayName("데이터가 1일 경우")
            void removeLast_oneElement_success() {
                list.addFirst(0);
                Object removed = list.removeLast();
                assertThat(removed).isEqualTo(0);
                assertThat(list.size()).isEqualTo(0);
                assertThat(list.isEmpty()).isEqualTo(true);
            }
            @Test
            @DisplayName("데이터가 여러개일 경우")
            void removeLast_multipleElements_success() {
                list.addFirst(0);
                list.addLast(1);
                list.addLast(2);
                Object removed = list.removeLast();
                assertThat(removed).isEqualTo(2);
                assertThat(list.size()).isEqualTo(2);
            }
            @Test
            @DisplayName("빈 리스트에서 삭제하는 경우")
            void removeLast_emptyList_throwsException() {
                assertThatThrownBy(() -> list.removeLast())
                        .isInstanceOf(NoSuchElementException.class);
            }
        }

        @Nested
        @DisplayName("remove 메서드 테스트")
        class RemoveTest {
            @Test
            @DisplayName("맨 앞 삭제 (index = 0)")
            void remove_atFront_success() {
                list.addFirst(0);
                list.addFirst(1);
                Object removed= list.remove(0);
                assertThat(removed).isEqualTo(1);
                assertThat(list.size()).isEqualTo(1);
            }
            @Test
            @DisplayName("중간 삭제")
            void remove_atMiddle_success() {
                list.addFirst(3);
                list.addFirst(2);
                list.addFirst(1);
                Object removed = list.remove(1);
                assertThat(removed).isEqualTo(2);
                assertThat(list.size()).isEqualTo(2);
            }
            @Test
            @DisplayName("맨 뒤 삭제 (index = size - 1)")
            void remove_atEnd_success() {
                list.addFirst(3);
                list.addFirst(2);
                list.addFirst(1);
                Object removed = list.remove(2);
                assertThat(removed).isEqualTo(3);
                assertThat(list.size()).isEqualTo(2);
            }
            @Test
            @DisplayName("빈 리스트에서 삭제하는 경우")
            void remove_emptyList_throwsException() {
                assertThatThrownBy(() -> list.remove(0))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
            @Test
            @DisplayName("index가 음수일 경우")
            void remove_negativeIndex_throwsException() {
                assertThatThrownBy(() -> list.remove(-1))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
            @Test
            @DisplayName("index가 size이상일 경우")
            void remove_indexOverSize_throwsException() {
                list.addFirst(0);
                list.addLast(1);
                list.addLast(2);
                assertThatThrownBy(() -> list.remove(list.size() + 1))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
        }

        @Nested
        @DisplayName("get 메서드 테스트")
        class GetTest {
            @Test
            @DisplayName("맨 앞 인덱스를 조회한다")
            void get_atFront_success() {
                list.addFirst(0);
                list.add(1,1);
                list.addLast(2);
                assertThat(list.get(0)).isEqualTo(0);
            }
            @Test
            @DisplayName("중간 인덱스를 조회한다")
            void get_atMiddle_success() {
                list.addFirst(0);
                list.add(1,1);
                list.addLast(2);
                assertThat(list.get(1)).isEqualTo(1);
            }
            @Test
            @DisplayName("맨 뒤 인덱스를 조회한다")
            void get_atEnd_success() {
                list.addFirst(0);
                list.add(1,1);
                list.addLast(2);
                assertThat(list.get(2)).isEqualTo(2);
            }
            @Test
            @DisplayName("빈 리스트일 경우")
            void get_emptyList_throwsException() {
                assertThatThrownBy(() -> list.get(0))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
            @Test
            @DisplayName("index가 음수인 경우")
            void get_negativeIndex_throwsException() {
                assertThatThrownBy(() -> list.get(-1))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
            @Test
            @DisplayName("index가 size 이상일 경우")
            void get_indexOverSize_throwsException() {
                assertThatThrownBy(() -> list.get(list.size() + 1))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
        }

        @Nested
        @DisplayName("set 메서드 테스트")
        class SetTest {
            @Test
            @DisplayName("맨 앞 index의 요소를 변경한다")
            void set_atFront_success() {
                list.addFirst(0);
                list.add(1,1);
                list.addLast(2);
                Object beforeObject = list.set(0,3);

                assertThat(beforeObject).isEqualTo(0);
                assertThat(list.get(0)).isEqualTo(3);
                assertThat(list.size()).isEqualTo(3);
            }
            @Test
            @DisplayName("중간 index의 요소를 변경한다")
            void set_atMiddle_success() {
                list.addFirst(0);
                list.add(1,1);
                list.addLast(2);

                Object beforeObject = list.set(1,3);

                assertThat(beforeObject).isEqualTo(1);
                assertThat(list.get(1)).isEqualTo(3);
                assertThat(list.size()).isEqualTo(3);
            }
            @Test
            @DisplayName("맨 뒤 index의 요소를 변경한다")
            void set_atEnd_success() {
                list.addFirst(0);
                list.add(1,1);
                list.addLast(2);

                Object beforeObject = list.set(2,3);

                assertThat(beforeObject).isEqualTo(2);
                assertThat(list.get(2)).isEqualTo(3);
                assertThat(list.size()).isEqualTo(3);
            }
            @Test
            @DisplayName("빈 리스트일 경우")
            void set_emptyList_throwsException() {
                assertThatThrownBy(() -> list.set(1,1))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
            @Test
            @DisplayName("index가 음수인 경우")
            void set_negativeIndex_throwsException() {
                list.addFirst(0);
                list.add(1,1);
                list.addLast(2);

                assertThatThrownBy(() -> list.set(-1,1))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
            @Test
            @DisplayName("index가 size 이상일 경우")
            void set_indexOverSize_throwsException() {
                list.addFirst(0);
                list.add(1,1);
                list.addLast(2);

                assertThatThrownBy(() -> list.set(list.size() + 1, 10))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
        }

        @Nested
        @DisplayName("size 메서드 테스트")
        class SizeTest {
            @Test
            @DisplayName("빈 리스트일 경우 0을 반환한다")
            void size_emptyList_returnsZero() {
                assertThat(list.size()).isEqualTo(0);
            }
            @Test
            @DisplayName("데이터의 길이만큼 반환한다")
            void size_withElements_returnCount() {
                list.addFirst(0);
                list.add(1,1);
                list.addLast(2);

                assertThat(list.size()).isEqualTo(3);
            }
            @Test
            @DisplayName("추가/삭제 후 정확히 반영되는 지 확인")
            void size_afterAddAndRemove_updatesCorrectly() {
                list.addFirst(0);
                list.add(1,1);

                assertThat(list.size()).isEqualTo(2);
                list.addLast(2);
                assertThat(list.size()).isEqualTo(3);
                list.remove(2);
                assertThat(list.size()).isEqualTo(2);
            }
        }

        @Nested
        @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {
            @Test
            @DisplayName("빈 리스트일 경우 true를 반환한다")
            void isEmpty_emptyList_returnsTrue() {
                assertThat(list.isEmpty()).isEqualTo(true);
            }
            @Test
            @DisplayName("데이터가 존재하면 false를 반환한다")
            void isEmpty_withElements_returnsFalse() {
                list.addFirst(0);
                assertThat(list.isEmpty()).isEqualTo(false);
            }
        }

        @Nested
        @DisplayName("contains 메서드 테스트")
        class ContainsTest {
            @Test
            @DisplayName("요소가 존재하면 true를 반환한다")
            void contains_existingElement_returnTrue() {
                list.addFirst(1);
                list.addFirst(0);

                assertThat(list.contains(0)).isEqualTo(true);
            }
            @Test
            @DisplayName("요소가 존재하지 않으면 false를 반환한다")
            void contains_nonExistingElement_returnFalse() {
                list.addFirst(1);
                assertThat(list.contains(0)).isEqualTo(false);
            }
            @Test
            @DisplayName("빈 리스트일 경우 false를 반환한다")
            void contains_emptyList_returnsFalse() {
                assertThat(list.contains(0)).isEqualTo(false);
            }
            @Test
            @DisplayName("null 요소를 찾는 경우")
            void contains_nullElement_returnCorrectly() {
                list.addFirst(null);
                assertThat(list.contains(null)).isEqualTo(true);
            }
        }

        @Nested
        @DisplayName("indexOf 메서드 테스트")
        class IndexOfTest {
            @Test
            @DisplayName("요소가 존재하면 해당 index를 반환한다")
            void indexOf_existingElement_returnIndex() {
                list.addFirst(2);
                list.addFirst(1);
                list.addFirst(0);

                assertThat(list.indexOf(2)).isEqualTo(2);
                assertThat(list.indexOf(0)).isEqualTo(0);
            }
            @Test
            @DisplayName("요소가 존재하지 않으면 -1을 반환한다")
            void indexOf_nonExistingElement_returnsMinusOne() {
                list.addFirst(0);

                assertThat(list.indexOf(1)).isEqualTo(-1);
            }
            @Test
            @DisplayName("빈 리스트일 경우 -1을 반환한다")
            void indexOf_emptyList_returnsMinusOne() {
                assertThat(list.indexOf(0)).isEqualTo(-1);
            }
            @Test
            @DisplayName("중복 요소가 있으면 첫 번째 index를 반환한다")
            void indexOf_duplicateElements_returnsFirstIndex() {
                list.addFirst(3);
                list.addFirst(1);
                list.addFirst(3);
                list.addFirst(0);

                assertThat(list.indexOf(3)).isEqualTo(1);
            }
            @Test
            @DisplayName("null 요소를 찾는 경우")
            void indexOf_nullElement_returnsCorrectly() {
                list.addFirst(3);
                list.addFirst(null);
                list.addFirst(2);

                assertThat(list.indexOf(null)).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("clear 메서드 테스트")
        class ClearTest {
            @Test
            @DisplayName("해당 데이터를 빈 리스트로 변경한다")
            void clear_withElements_becomesEmpty() {
                list.addFirst(3);
                list.addFirst(2);
                list.addFirst(1);

                list.clear();
                assertThat(list.size()).isEqualTo(0);
                assertThat(list.isEmpty()).isEqualTo(true);
            }
            @Test
            @DisplayName("빈 리스트에 clear를 호출해도 정상 동작한다")
            void clear_emptyList_success() {
                list.clear();
                assertThat(list.size()).isEqualTo(0);
                assertThat(list.isEmpty()).isEqualTo(true);
            }
        }

        @Nested
        @DisplayName("reverse 메서드 테스트")
        class ReverseTest {
            @Test
            @DisplayName("빈 리스트를 뒤집어도 정상 동작한다")
            void reverse_emptyList_success() {
                list.reverse();
                assertThat(list.isEmpty()).isTrue();
            }
            @Test
            @DisplayName("요소가 1개인 리스트를 뒤집는다")
            void reverse_oneElement_success() {
                list.addFirst(0);
                list.reverse();
                assertThat(list.size()).isEqualTo(1);
                assertThat(list.get(0)).isEqualTo(0);
            }
            @Test
            @DisplayName("요소가 여러 개인 리스트를 뒤집는다")
            void reverse_multipleElements_success() {
                list.addFirst(3);
                list.addFirst(2);
                list.addFirst(1);
                list.reverse();
                assertThat(list.get(0)).isEqualTo(3);
                assertThat(list.get(1)).isEqualTo(2);
                assertThat(list.get(2)).isEqualTo(1);
            }

            @Test
            @DisplayName("reverse 두 번하면 원래 순서로 돌아간다")
            void reverse_twice_returnsOriginal() {
                list.addFirst(3);
                list.addFirst(2);
                list.addFirst(1);

                list.reverse();
                list.reverse();

                assertThat(list.get(0)).isEqualTo(1);
                assertThat(list.get(1)).isEqualTo(2);
                assertThat(list.get(2)).isEqualTo(3);
            }
        }
    }

    @Nested
    @DisplayName("이중 연결 리스트")
    class DoublyLinkedListTest {

//        private DoublyLinkedList<Object> list;
        private LinkedList<Object> list;

        @BeforeEach
        void setUp() {
            list = new DoublyLinkedListImpl<>();
        }

        @Nested
        @DisplayName("이중 연결 리스트 생성")
        class Creation {
            @Test
            @DisplayName("이중 연결 리스트가 생성된다")
            void creation_success() {
                assertThat(list).isNotNull();
                assertThat(list.size()).isEqualTo(0);
                assertThat(list.isEmpty()).isTrue();
            }
        }

        @Nested
        @DisplayName("addFirst 메서드 테스트")
        class AddFirstTest {
            @Test
            @DisplayName("빈 리스트에 추가된다")
            void addFirst_emptyList_success() {
                list.addFirst(1);

                assertThat(list.size()).isEqualTo(1);
                assertThat(list.get(0)).isEqualTo(1);
            }
            @Test
            @DisplayName("데이터가 1개일 때 추가된다")
            void addFirst_oneElement_success() {
                list.addFirst(1);
                list.addFirst(0);

                assertThat(list.size()).isEqualTo(2);
                assertThat(list.get(0)).isEqualTo(0);
            }
            @Test
            @DisplayName("데이터가 여러개일 때 추가된다")
            void addFirst_multipleElements_success() {
                list.addFirst(2);
                list.addFirst(1);
                list.addFirst(0);

                assertThat(list.size()).isEqualTo(3);
                assertThat(list.get(0)).isEqualTo(0);
            }
            @Test
            @DisplayName("null 요소를 추가한다")
            void addFirst_nullElement_success() {
                list.addFirst(null);

                assertThat(list.size()).isEqualTo(1);
                assertThat(list.get(0)).isEqualTo(null);
            }
        }

        @Nested
        @DisplayName("addLast 메서드 테스트")
        class AddLastTest {
            @Test
            @DisplayName("빈 리스트에 추가된다")
            void addLast_emptyList_success() {
                list.addLast(0);

                assertThat(list.size()).isEqualTo(1);
                assertThat(list.get(0)).isEqualTo(0);
            }
            @Test
            @DisplayName("데이터가 1개일 때 추가된다")
            void addLast_oneElement_success() {
                list.addLast(0);
                list.addLast(1);

                assertThat(list.size()).isEqualTo(2);
                assertThat(list.get(1)).isEqualTo(1);
            }
            @Test
            @DisplayName("데이터가 여러개일 때 추가된다")
            void addLast_multipleElements_success() {
                list.addLast(0);
                list.addLast(1);
                list.addLast(2);

                assertThat(list.size()).isEqualTo(3);
                assertThat(list.get(2)).isEqualTo(2);
            }
            @Test
            @DisplayName("null 요소를 추가한다")
            void addLast_nullElement_success() {
                list.addLast(null);

                assertThat(list.size()).isEqualTo(1);
                assertThat(list.get(0)).isEqualTo(null);
            }
        }

        @Nested
        @DisplayName("add 메서드 테스트")
        class AddTest {
            @Test
            @DisplayName("요소가 맨 앞에 추가된다 (index = 0)")
            void add_atFront_success() {
                list.addLast(0);
                list.add(0, 1);

                assertThat(list.size()).isEqualTo(2);
                assertThat(list.get(0)).isEqualTo(1);
            }

            @Test
            @DisplayName("요소가 맨 뒤에 추가된다 (index = size)")
            void add_atMiddle_success() {
                list.addLast(0);
                list.addLast(2);
                list.add(1, 1);


                assertThat(list.size()).isEqualTo(3);
                assertThat(list.get(1)).isEqualTo(1);
            }
            @Test
            @DisplayName("요소가 맨 뒤에 추가된다 (index = size)")
            void add_atEnd_success() {
                list.addLast(0);
                list.add(list.size(), 1);

                assertThat(list.size()).isEqualTo(2);
                assertThat(list.get(1)).isEqualTo(1);
            }
            @Test
            @DisplayName("null 요소가 추가된다")
            void add_nullElement_success() {
                list.add(0, null);

                assertThat(list.size()).isEqualTo(1);
                assertThat(list.get(0)).isEqualTo(null);
            }
            @Test
            @DisplayName("index가 음수일 때 추가되지 않는다")
            void add_negativeIndex_throwsException() {
                assertThatThrownBy(() -> list.add(-1, 10))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
            @Test
            @DisplayName("index가 size보다 클 경우 추가되지 않는다")
            void add_indexOverSize_throwsException() {
                assertThatThrownBy(() -> list.add(list.size() + 1, 10))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
        }

        @Nested
        @DisplayName("removeFirst 메서드 테스트")
        class RemoveFirstTest {
            @Test
            @DisplayName("데이터가 1개일 때 삭제하는 경우")
            void removeFirst_oneElement_success() {
                list.addLast(0);

                Object removed = list.removeFirst();

                assertThat(list.size()).isEqualTo(0);
                assertThat(removed).isEqualTo(0);
            }
            @Test
            @DisplayName("데이터가 여러개일 때 삭제하는 경우")
            void removeFirst_multipleElements_success() {
                list.addLast(0);
                list.addLast(1);

                Object removed = list.removeFirst();

                assertThat(list.size()).isEqualTo(1);
                assertThat(removed).isEqualTo(0);
            }
            @Test
            @DisplayName("빈 리스트에서 삭제하는 경우")
            void removeFirst_emptyList_throwsException() {
                assertThatThrownBy(() -> list.removeFirst())
                        .isInstanceOf(NoSuchElementException.class);
            }
        }

        @Nested
        @DisplayName("removeLast 메서드 테스트")
        class RemoveLastTest {
            @Test
            @DisplayName("데이터가 1개일 때 삭제하는 경우")
            void removeLast_oneElement_success() {
                list.addLast(0);
                Object removed = list.removeLast();

                assertThat(list.size()).isEqualTo(0);
                assertThat(removed).isEqualTo(0);
            }
            @Test
            @DisplayName("데이터가 여러개일 경우 삭제하는 경우")
            void removeLast_multipleElements_success() {
                list.addLast(0);
                list.addLast(1);

                Object removed = list.removeLast();

                assertThat(list.size()).isEqualTo(1);
                assertThat(removed).isEqualTo(1);
            }
            @Test
            @DisplayName("빈 리스트에서 삭제하는 경우")
            void removeLast_emptyList_throwsException() {
                assertThatThrownBy(() -> list.removeLast())
                        .isInstanceOf(NoSuchElementException.class);
            }
        }

        @Nested
        @DisplayName("remove 메서드 테스트")
        class RemoveTest {
            @Test
            @DisplayName("맨 앞의 요소를 삭제하는 경우 (index = 0)")
            void remove_atFront_success() {
                list.addLast(0);
                list.addLast(1);
                list.addLast(2);

                Object removed = list.remove(0);

                assertThat(list.size()).isEqualTo(2);
                assertThat(removed).isEqualTo(0);
            }
            @Test
            @DisplayName("중간 요소를 삭제하는 경우")
            void remove_atMiddle_success() {
                list.addLast(0);
                list.addLast(1);
                list.addLast(2);

                Object removed = list.remove(1);

                assertThat(list.size()).isEqualTo(2);
                assertThat(removed).isEqualTo(1);
            }
            @Test
            @DisplayName("맨 마지막의 요소를 삭제하는 경우( index = size - 1)")
            void remove_atEnd_success() {
                list.addLast(0);
                list.addLast(1);
                list.addLast(2);

                Object removed = list.remove(list.size() - 1);

                assertThat(list.size()).isEqualTo(2);
                assertThat(removed).isEqualTo(2);
            }
            @Test
            @DisplayName("index가 음수 일 경우")
            void remove_negativeIndex_throwsException() {
                list.addLast(0);

                assertThatThrownBy(() -> list.remove(-1))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
            @Test
            @DisplayName("index가 size 이상일 경우")
            void remove_indexOverSize_throwsException() {
                list.addLast(0);

                assertThatThrownBy(() -> list.remove(list.size()))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
            @Test
            @DisplayName("빈 리스트일 경우")
            void remove_emptyList_throwsException() {
                assertThatThrownBy(() -> list.remove(0))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
        }

        @Nested
        @DisplayName("get 메서드 테스트")
        class GetTest {
            @Test
            @DisplayName("맨 앞 요소를 조회한다 (index = 0)")
            void get_atFront_success() {
                list.addLast(0);
                list.addLast(1);

                assertThat(list.get(0)).isEqualTo(0);
            }
            @Test
            @DisplayName("중간 요소를 조회한다")
            void get_atMiddle_success() {
                list.addLast(0);
                list.addLast(1);
                list.addLast(2);

                assertThat(list.get(1)).isEqualTo(1);
            }
            @Test
            @DisplayName("맨 뒤 요소를 조회한다 (index = size - 1)")
            void get_atEnd_success() {
                list.addLast(0);
                list.addLast(1);
                list.addLast(2);

                assertThat(list.get(list.size() - 1)).isEqualTo(2);
            }
            @Test
            @DisplayName("빈 리스트를 조회하는 경우")
            void get_emptyList_throwsException() {
                assertThatThrownBy(() -> list.get(0))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
            @Test
            @DisplayName("index가 음수인 경우")
            void get_negativeIndex_throwsException() {
                list.addLast(0);
                list.addLast(1);
                assertThatThrownBy(() ->list.get(-1))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
            @Test
            @DisplayName("index가 size 이상인 경우")
            void get_indexOverSize_throwsException() {
                list.addLast(0);
                list.addLast(1);
                assertThatThrownBy(() ->list.get(list.size()))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
        }

        @Nested
        @DisplayName("set 메서드 테스트")
        class SetTest {
            @Test
            @DisplayName("맨 앞 요소를 수정한다 (index = 0)")
            void set_atFront_success() {
                list.addLast(0);
                list.addLast(1);
                list.addLast(2);

                Object targetValue = list.set(0,3);

                assertThat(list.size()).isEqualTo(3);
                assertThat(targetValue).isEqualTo(0);
                assertThat(list.get(0)).isEqualTo(3);
            }
            @Test
            @DisplayName("중간 요소를 수정한다")
            void set_atMiddle_success() {
                list.addLast(0);
                list.addLast(1);
                list.addLast(2);

                Object targetValue = list.set(1,3);

                assertThat(list.size()).isEqualTo(3);
                assertThat(targetValue).isEqualTo(1);
                assertThat(list.get(1)).isEqualTo(3);
            }
            @Test
            @DisplayName("맨 뒤 요소를 수정한다 (index = size - 1)")
            void set_atEnd_success() {
                list.addLast(0);
                list.addLast(1);
                list.addLast(2);

                Object targetValue = list.set(list.size() - 1,3);

                assertThat(list.size()).isEqualTo(3);
                assertThat(targetValue).isEqualTo(2);
                assertThat(list.get(list.size() - 1)).isEqualTo(3);
            }
            @Test
            @DisplayName("빈 리스트인 경우")
            void set_emptyList_throwsException() {
                assertThatThrownBy(() -> list.set(0,1))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
            @Test
            @DisplayName("index가 음수인 경우")
            void set_negativeIndex_throwsException() {
                list.addLast(0);

                assertThatThrownBy(() -> list.set(-1, 0))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
            @Test
            @DisplayName("index가 size 이상인 경우")
            void set_indexOverSize_throwsException() {
                list.addLast(0);

                assertThatThrownBy(() -> list.set(2, 0))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
        }

        @Nested
        @DisplayName("size 메서드 테스트")
        class SizeTest {
            @Test
            @DisplayName("빈 리스트인 경우 0을 반환한다")
            void size_emptyList_returnsZero() {
                assertThat(list.size()).isEqualTo(0);
            }
            @Test
            @DisplayName("데이터가 1개일 경우")
            void size_oneElement_returnsOne() {
                list.addLast(0);

                assertThat(list.size()).isEqualTo(1);
            }
            @Test
            @DisplayName("데이터가 여러개일 경우")
            void size_multipleElements_returnsCount() {
                list.addLast(0);
                list.addLast(1);

                assertThat(list.size()).isEqualTo(2);
            }
        }

        @Nested
        @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {
            @Test
            @DisplayName("빈 리스트인 경우 true를 반환한다")
            void isEmpty_emptyList_returnsTrue() {
                assertThat(list.isEmpty()).isTrue();
            }
            @Test
            @DisplayName("데이터가 존재할 경우 false를 반환한다")
            void isEmpty_withElements_returnFalse() {
                list.addLast(0);
                assertThat(list.isEmpty()).isFalse();
            }
        }

        @Nested
        @DisplayName("contains 메서드 테스트")
        class ContainsTest {
            @Test
            @DisplayName("요소가 존재하면 true를 반환한다")
            void contains_existingElement_returnsTrue() {
                list.addLast(0);
                assertThat(list.contains(0)).isTrue();
            }
            @Test
            @DisplayName("요소가 존재하지 않으면 false를 반환한다")
            void contains_nonExistingElement_returnsFalse() {
                list.addLast(1);
                assertThat(list.contains(0)).isFalse();
            }
            @Test
            @DisplayName("빈 리스트일 경우 false를 반환한다")
            void contains_emptyList_returnsFalse() {
                assertThat(list.contains(0)).isFalse();
            }
            @Test
            @DisplayName("null 요소를 찾는 경우")
            void contains_nullElement_returnsCorrectly() {
                list.addLast(null);
                assertThat(list.contains(null)).isTrue();
            }
        }

        @Nested
        @DisplayName("indexOf 메서드 테스트")
        class IndexOfTest {
            @Test
            @DisplayName("요소가 존재하면 해당 인덱스를 반환한다")
            void indexOf_existingElement_returnsIndex() {
                list.addLast(0);

                assertThat(list.indexOf(0)).isEqualTo(0);
            }
            @Test
            @DisplayName("요소가 존재하지 않으면 -1을 반환한다")
            void indexOf_nonExistingElement_returnsMinusOne() {
                list.addLast(1);

                assertThat(list.indexOf(0)).isEqualTo(-1);
            }
            @Test
            @DisplayName("빈 리스트일 경우 -1을 반환한다")
            void indexOf_emptyList_returnsMinusOne() {
                assertThat(list.indexOf(0)).isEqualTo(-1);
            }
            @Test
            @DisplayName("중복 요소가 있으면 첫 번째 index를 반환한다")
            void indexOf_duplicateElements_returnsFirstIndex() {
                list.addLast(0);
                list.addLast(1);
                list.addLast(0);

                assertThat(list.indexOf(0)).isEqualTo(0);
            }
            @Test
            @DisplayName("null 요소를 찾는 경우")
            void indexOf_nullElement_returnsCorrectly() {
                list.addLast(null);

                assertThat(list.indexOf(null)).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("clear 메서드 테스트")
        class ClearTest {
            @Test
            @DisplayName("빈 리스트가 된다.")
            void clear_withElements_becomesEmpty() {
                list.addLast(0);
                list.addLast(1);

                list.clear();
                assertThat(list.size()).isEqualTo(0);
                assertThat(list.isEmpty()).isTrue();
            }
            @Test
            @DisplayName("빈 리스트에 clear 호출해도 정상 동작한다")
            void clear_emptyList_success() {
                list.clear();
                assertThat(list.size()).isEqualTo(0);
                assertThat(list.isEmpty()).isTrue();
            }
        }

        @Nested
        @DisplayName("reverse 메서드 테스트")
        class ReverseTest {
            @Test
            @DisplayName("빈 리스트일 경우 정상 동작한다")
            void reverse_emptyList_success() {
                list.reverse();

                assertThat(list.size()).isEqualTo(0);
                assertThat(list.isEmpty()).isTrue();
            }
            @Test
            @DisplayName("데이터가 1개일 경우")
            void reverse_oneElement_success() {
                list.addLast(0);
                list.reverse();
                assertThat(list.size()).isEqualTo(1);
                assertThat(list.get(0)).isEqualTo(0);
            }
            @Test
            @DisplayName("데이터가 여러개일 경우")
            void reverse_multipleElements_success() {
                list.addLast(0);
                list.addLast(1);
                list.addLast(2);
                list.reverse();
                assertThat(list.size()).isEqualTo(3);
                assertThat(list.get(0)).isEqualTo(2);
                assertThat(list.get(1)).isEqualTo(1);
                assertThat(list.get(2)).isEqualTo(0);
            }
        }
    }
}
```
