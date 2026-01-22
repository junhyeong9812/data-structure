package com.datastructure.linkedlist;

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

        private SinglyLinkedList<Object> list;
        @BeforeEach
        void setUp() {
            list = new SinglyLinkedList<>();
        }

        @Nested
        @DisplayName("단일 연결 리스트 생성")
        class Creation {
            // 단일 연결 리스트가 생성된다.
            @Test
            @DisplayName("단일 연결 리스트가 생성된다.")
            void creationListTest() {
                assertThat(list).isNotNull();
            }
            // 생성 시 size가 0이다.
            @Test
            @DisplayName("단일 연결 리스트 생성 시 size는 0이다.")
            void sizeIsZeroTest() {
                assertThat(list.size()).isEqualTo(0);
            }
            // 생성 시 isEmpty가 true이다.
            @Test
            @DisplayName("단일 연결 리스트 생성 시 isEmpty는 true다.")
            void isEmptyIsTrueTest() {
                assertThat(list.isEmpty()).isEqualTo(true);
            }
        }

        @Nested
        @DisplayName("addFirst 메서드 테스트")
        class AddFirstTest {
            // 맨 앞에 요소가 추가된다.
            // 빈 리스트에 추가하는 경우
            @Test
            @DisplayName("빈 리스트에 추가하는 경우")
            void addFirst_blank_addsToFront() {
                list.addFirst(1);
                assertThat(list.get(0)).isEqualTo(1);
                assertThat(list.size()).isEqualTo(1);
            }
            // 요소가 null인 경우
            @Test
            @DisplayName("추가하는 요소가 null인 경우")
            void addFirst_Null_addsToFront() {
                list.addFirst(null);
                assertThat(list.get(0)).isEqualTo(null);
                assertThat(list.size()).isEqualTo(1);
            }
            // 데이터가 1개일 경우
            @Test
            @DisplayName("데이터가 1개일 경우")
            void addFirst_whenOneElement_addToFront() {
                list.addFirst(1);
                list.addFirst(2);
                assertThat(list.get(0)).isEqualTo(2);
                assertThat(list.size()).isEqualTo(2);
            }
            // 데이터가 여러개일 경우
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
            // 맨 뒤에 요소가 추가된다.
            // 빈 리스트에 추가하는 경우
            @Test
            @DisplayName("빈 리스트에 추가하는 경우")
            void addLast_blank_addsToBack() {
                list.addLast(1);
                assertThat(list.get(0)).isEqualTo(1);
                assertThat(list.size()).isEqualTo(1);
            }

            // 요소가 null인 경우
            @Test
            @DisplayName("요소가 null인 경우")
            void addLast_null_addsToBack() {
                list.addLast(null);
                assertThat(list.get(0)).isEqualTo(null);
                assertThat(list.size()).isEqualTo(1);
            }

            // 데이터가 1개일 경우
            @Test
            @DisplayName("데이터가 1개일 경우")
            void addLast_whenOneElement_addToBack() {
                list.addLast(1);
                list.addLast(2);
                assertThat(list.get(1)).isEqualTo(2);
                assertThat(list.size()).isEqualTo(2);
            }

            // 데이터가 여러개일 경우
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
            // 특정 위치에 요소가 추가된다.
            // 빈 리스트에 추가하는 경우 (index 0)
            @Test
            @DisplayName("빈 리스트에 추가하는 경우 (index = 0)")
            void add_blankList() {
                list.add(0,1);
                assertThat(list.get(0)).isEqualTo(1);
                assertThat(list.size()).isEqualTo(1);
            }
            // 요소가 null인 경우
            @Test
            @DisplayName("요소가 null인 경우")
            void add_whenNullElement_addToIndex() {
                list.add(0, null);
                assertThat(list.get(0)).isEqualTo(null);
                assertThat(list.size()).isEqualTo(1);
            }
            // 데이터가 1개일 경우
            @Test
            @DisplayName("데이터가 1개일 경우")
            void add_whenOneElement_addToIndex() {
                list.addFirst(0);
                list.add(0,1);
                assertThat(list.get(0)).isEqualTo(1);
                assertThat(list.size()).isEqualTo(2);
            }
            // 데이터가 여러개일 경우
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
            // 맨 앞에 추가하는 경우 (index 0)
            @Test
            @DisplayName("맨 앞에 추가하는 경우 (index = 0)")
            void add_atFront_success() {
                list.addFirst(1);
                list.add(0,0);
                assertThat(list.get(0)).isEqualTo(0);
                assertThat(list.size()).isEqualTo(2);
            }
            // 중간에 추가하는 경우
            @Test
            @DisplayName("중간에 추가하는 경우")
            void add_atMiddle_success() {
                list.addFirst(0);
                list.addLast(2);
                list.add(1,1);
                assertThat(list.get(1)).isEqualTo(1);
                assertThat(list.size()).isEqualTo(3);
            }
            // 맨 뒤에 추가하는 경우 (index == size)
            @Test
            @DisplayName("맨 뒤에 추가하는 경우 (index = size)")
            void add_atEnd_success() {
                list.addFirst(1);
                list.addFirst(0);
                list.add(2,2);
                assertThat(list.get(2)).isEqualTo(2);
                assertThat(list.size()).isEqualTo(3);
            }
            // 특정 위치에 요소가 추가 되지않는다.
            // 음수 인덱스인 경우 -> indexOutOfBoundsException
            @Test
            @DisplayName("음수 인덱스인 경우")
            void add_negativeIndex_throwsException() {
                list.addFirst(0);
                list.addLast(1);
                assertThatThrownBy(() -> list.add(-1,3))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
            // size보다 큰 인덱스인 경우 -> indexOutOfBoundsException
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
            // 맨 앞의 위치에 요소가 삭제된다.
            // 데이터가 1개일 경우
            @Test
            @DisplayName("데이터가 1개일 경우")
            void removeFirst_oneElement_success() {
                list.addFirst(1);
                list.removeFirst();
                assertThat(list.size()).isEqualTo(0);
                assertThat(list.isEmpty()).isEqualTo(true);
            }
            // 데이터가 여러개일 경우
            @Test
            @DisplayName("데이터가 여러개일 경우")
            void removeFirst_multipleElements_success() {
                list.addFirst(1);
                list.addFirst(0);
                list.removeFirst();
                assertThat(list.size()).isEqualTo(1);
                assertThat(list.get(0)).isEqualTo(1);
            }
            //맨 앞 요소가 삭제되지 않는다.
            // 빈 리스트에 삭제를 하는 경우(index 0)
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
            //맨 뒤 위치의 요소가 삭제된다.
            // 데이터가 1개일 경우
            @Test
            @DisplayName("데이터가 1일 경우")
            void removeLast_oneElement_success() {
                list.addFirst(0);
                Object removed = list.removeLast();
                assertThat(removed).isEqualTo(0);
                assertThat(list.size()).isEqualTo(0);
                assertThat(list.isEmpty()).isEqualTo(true);
            }
            // 데이터가 여러개일 경우
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
            // 맨 앞 요소가 삭제되지 않는다.
            // 빈 리스트에 삭제를 하는 경우(index 0)
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
            // index 요소의 데이터를 삭제한다.
            // 맨 앞 삭제 (index 0)
            @Test
            @DisplayName("맨 앞 삭제 (index = 0)")
            void remove_atFront_success() {
                list.addFirst(0);
                list.addFirst(1);
                Object removed= list.remove(0);
                assertThat(removed).isEqualTo(1);
                assertThat(list.size()).isEqualTo(1);
            }
            // 중간 삭제
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
            // 맨 뒤 삭제 (index == size - 1)
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

            // index 위치의 요소가 삭제되지 않는다.
            // 빈 리스트를 삭제하는 경우 (index 0)
            @Test
            @DisplayName("빈 리스트에서 삭제하는 경우")
            void remove_emptyList_throwsException() {
                assertThatThrownBy(() -> list.remove(0))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
            // index가 음수일 경우
            @Test
            @DisplayName("index가 음수일 경우")
            void remove_negativeIndex_throwsException() {
                assertThatThrownBy(() -> list.remove(-1))
                        .isInstanceOf(IndexOutOfBoundsException.class);
            }
            // index가 size보다 클 경우
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
            // index의 요소를 반환한다.
            //맨 앞 인덱스를 조회한다.
            @Test
            @DisplayName("맨 앞 인덱스를 조회한다")
            void get_atFront_success() {

            }
            //중간 인덱스를 조회한다.
            @Test
            @DisplayName("중간 인덱스를 조회한다")
            void get_atMiddle_success() {

            }
            //맨 뒤 인덱스를 조회힌다.
            @Test
            @DisplayName("맨 뒤 인덱스를 조회한다")
            void get_atEnd_success() {

            }
            // index 위치의 요소를 반환하지 않는다.
            // 빈 리스트일 경우
            @Test
            @DisplayName("빈 리스트일 경우")
            void get_emptyList_throwsException() {

            }
            // index가 음수인 경우
            @Test
            @DisplayName("index가 음수인 경우")
            void get_negativeIndex_throwsException() {

            }
            // index가 size 이상일 경우
            @Test
            @DisplayName("index가 size 이상일 경우")
            void get_indexOverSize_throwsException() {

            }
        }

        @Nested
        @DisplayName("set 메서드 테스트")
        class SetTest {
            // index의 요소를 변경한다.
            // 맨 앞 index의 요소를 변경한다.
            @Test
            @DisplayName("맨 앞 index의 요소를 변경한다")
            void set_atFront_success() {

            }
            // 중간 index의 요소를 변경한다.
            @Test
            @DisplayName("중간 index의 요소를 변경한다")
            void set_atMiddle_success() {

            }
            // 맨 뒤 index의 요소를 변경한다.
            @Test
            @DisplayName("맨 뒤 index의 요소를 변경한다")
            void set_atEnd_success() {

            }

            // index 위치의 요소를 변경할 수 없다.
            // 빈 리스트일 경우
            @Test
            @DisplayName("빈 리스트일 경우")
            void set_emptyList_throwsException() {

            }
            // index가 음수일 경우
            @Test
            @DisplayName("index가 음수인 경우")
            void set_negativeIndex_throwsException() {

            }
            // index가 size 이상일 경우
            @Test
            @DisplayName("index가 size 이상일 경우")
            void set_indexOverSize_throwsException() {

            }
        }

        @Nested
        @DisplayName("size 메서드 테스트")
        class SizeTest {
            // 사이즈를 반환한다.
            // 빈 리스트일 경우 0을 반환한다.
            @Test
            @DisplayName("빈 리스트일 경우 0을 반환한다")
            void size_emptyList_returnsZero() {

            }
            // 데이터의 길이만큼 반환한다.
            @Test
            @DisplayName("데이터의 길이만큼 반환한다")
            void size_withElements_returnCount() {

            }
            // 추가/삭제 후 정확히 반영되는 지 확인
            @Test
            @DisplayName("추가/삭제 후 정확히 반영되는 지 확인")
            void size_afterAddAndRemove_updatesCorrectly() {

            }
        }

        @Nested
        @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {
            // 빈 리스트일 경우 true를 반환한다.
            @Test
            @DisplayName("빈 리스트일 경우 true를 반환한다")
            void isEmpty_emptyList_returnsTrue() {

            }
            // 데이터가 존재하면 false를 반환한다.
            @Test
            @DisplayName("데이터가 존재하면 false를 반환한다")
            void isEmpty_withElements_returnsFalse() {

            }
        }

        @Nested
        @DisplayName("contains 메서드 테스트")
        class ContainsTest {
            // 요소가 존재하면 True를 반환한다.
            @Test
            @DisplayName("요소가 존재하면 true를 반환한다")
            void contains_existingElement_returnFalse() {

            }
            // 요소가 존재하지 않으면 false를 반환한다.
            @Test
            @DisplayName("요소가 존재하지 않으면 false를 반환한다")
            void contains_nonExistingElement_returnFalse() {

            }
            // 빈 리스트일 경우 false를 반환한다.
            @Test
            @DisplayName("빈 리스트일 경우 false를 반환한다")
            void contains_emptyList_returnsFalse() {

            }
            // null 요소를 찾는 경우
            @Test
            @DisplayName("null 요소를 찾는 경우")
            void contains_nullElement_returnCorrectly() {

            }
        }

        @Nested
        @DisplayName("indexOf 메서드 테스트")
        class IndexOfTest {
            // 요소가 존재하면 해당 index를 반환한다.
            @Test
            @DisplayName("요소가 존재하면 해당 index를 반환한다")
            void indexOf_existingElement_returnIndex() {

            }
            // 요소가 존재하지 않으면 -1을 반환한다.
            @Test
            @DisplayName("요소가 존재하지 않으면 -1을 반환한다")
            void indexOf_nonExistingElement_returnsMinusOne() {

            }
            // 빈리스트일 경우 -1을 반환한다.
            @Test
            @DisplayName("빈 리스트일 경우 -1을 반환한다")
            void indexOf_emptyList_returnsMinusOne() {

            }
            // 중복 요소가 있으면 첫 번째 index를 반환한다.
            @Test
            @DisplayName("중복 요소가 있으면 첫 번째 index를 반환한다")
            void indexOf_duplicateElements_returnsFirstIndex() {

            }
            // null 요소를 찾는 경우
            @Test
            @DisplayName("null 요소를 찾는 경우")
            void indexOf_nullElement_returnsCorrectly() {

            }
        }

        @Nested
        @DisplayName("clear 메서드 테스트")
        class ClearTest {
            // 해당 데이터를 빈 리스트로 변경한다.
            @Test
            @DisplayName("해당 데이터를 빈 리스트로 변경한다")
            void clear_withElements_becomesEmpty() {

            }
            //빈 리스트에 clear를 호출해도 정상 동작한다.
            @Test
            @DisplayName("빈 리스트에 clear를 호출해도 정상 동작한다")
            void clear_emptyList_success() {

            }
            // clear 후 size가 0이다.
            @Test
            @DisplayName("clear 후 size가 0이다")
            void clear_afterClear_sizeIsZero() {

            }
            // clear 후 isEmpty가 true다.
            @Test
            @DisplayName("clear 후 isEmpty가 true다")
            void clear_afterClear_isEmptyTrue() {

            }
        }

        @Nested
        @DisplayName("reverse 메서드 테스트")
        class ReverseTest {
            // 리스트를 뒤집는다.
            // 빈 리스트는 뒤집어도 정상 동작한다.
            @Test
            @DisplayName("빈 리스트를 뒤집어도 정상 동작한다")
            void reverse_emptyList_success() {

            }
            // 요소가 1개인 리스트를 뒤집는다.
            @Test
            @DisplayName("요소가 1개인 리스트를 뒤집는다")
            void reverse_oneElement_success() {

            }
            // 요소가 여러 개인리스트를 뒤집는다.
            @Test
            @DisplayName("요소가 여러 개인 리스트를 뒤집는다")
            void reverse_multipleElements_success() {

            }
        }
    }

    @Nested
    @DisplayName("이중 연결 리스트")
    class DoublyLinkedListTest {

        @Nested
        @DisplayName("이중 연결 리스트 생성")
        class Creation {
            // 이중 연결 리스트가 생성이 된다.
            @Test
            @DisplayName("이중 연결 리스트가 생성된다")
            void creation_success() {

            }
            // 이중 연결 리스트의 size가 0이다.
            @Test
            @DisplayName("이중 연결 리스트의 size는 0이다")
            void creation_sizeIsZero() {

            }
            // 이중 연결 리스트의 isEmpty는 true이다.
            @Test
            @DisplayName("생성 시 isEmpty가 true이다")
            void creation_isEmptyTrue() {

            }
        }

        @Nested
        @DisplayName("addFirst 메서드 테스트")
        class AddFirstTest {
            // 요소가 맨처음 추가된다.
            // 빈 리스트에 맨처음 추가된다.
            @Test
            @DisplayName("빈 리스트에 추가된다")
            void addFirst_emptyList_success() {

            }
            // 데이터가 1개일 때 추가된다.
            @Test
            @DisplayName("데이터가 1개일 때 추가된다")
            void addFirst_oneElement_success() {

            }
            // 데이터가 여러개일 때 추가된다.
            @Test
            @DisplayName("데이터가 여러개일 때 추가된다")
            void addFirst_multipleElements_success() {

            }
            // null 요소를 추가한다.
            @Test
            @DisplayName("null 요소를 추가한다")
            void addFirst_nullElement_success() {

            }
        }

        @Nested
        @DisplayName("addLast 메서드 테스트")
        class AddLastTest {
            //요소가 마지막에 추가된다.
            //빈 리스트에 맨 마지막에 추가된다.
            @Test
            @DisplayName("빈 리스트에 추가된다")
            void addLast_emptyList_success() {

            }
            // 데이터가 1개일 때 추가된다.
            @Test
            @DisplayName("데이터가 1개일 때 추가된다")
            void addLast_oneElement_success() {

            }
            // 데이터가 여러개일 때 추가된다.
            @Test
            @DisplayName("데이터가 여러개일 때 추가된다")
            void addLast_multipleElements_success() {

            }
            // null 요소를 추가한다.
            @Test
            @DisplayName("null 요소를 추가한다")
            void addLast_nullElement_success() {

            }
        }

        @Nested
        @DisplayName("add 메서드 테스트")
        class AddTest {
            // 요소가 index 위치에 추가된다.
            // 요소가 맨 앞에 추가된다. (index = 0)
            @Test
            @DisplayName("요소가 맨 앞에 추가된다 (index = 0)")
            void add_atFront_success() {

            }

            // 요소가 중간에 추가된다.
            @Test
            @DisplayName("요소가 맨 뒤에 추가된다 (index = size)")
            void add_atMiddle_success() {

            }
            // 요소가 맨 뒤에 추가된다. (index = size)
            @Test
            @DisplayName("요소가 맨 뒤에 추가된다 (index = size)")
            void add_atEnd_success() {

            }
            // null 요소가 추가된다.
            @Test
            @DisplayName("null 요소가 추가된다")
            void add_nullElement_success() {

            }
            //요소가 추가되지 않는다.
            //index가 음수일 때 추가되지않는다.
            @Test
            @DisplayName("index가 음수일 때 추가되지 않는다")
            void add_negativeIndex_throwsException() {

            }
            //index가 size보다 클 경우 추가되지 않는다.
            @Test
            @DisplayName("index가 size보다 클 경우 추가되지 않는다")
            void add_indexOverSize_throwsException() {

            }
        }

        @Nested
        @DisplayName("removeFirst 메서드 테스트")
        class RemoveFirstTest {
            // 맨 앞의 요소가 삭제된다.
            // 데이터가 1개일 때 삭제하는 경우
            @Test
            @DisplayName("데이터가 1개일 때 삭제하는 경우")
            void removeFirst_oneElement_success() {

            }
            // 데이터가 여러개일 때 삭제하는 경우
            @Test
            @DisplayName("데이터가 여러개일 때 삭제하는 경우")
            void removeFirst_multipleElements_success() {

            }
            // 맨 앞의 요소가 삭제되지 않는다.
            // 빈 리스트에 삭제하는 경우
            @Test
            @DisplayName("빈 리스트에서 삭제하는 경우")
            void removeFirst_emptyList_throwsException() {

            }
        }

        @Nested
        @DisplayName("removeLast 메서드 테스트")
        class RemoveLastTest {
            // 맨 뒤의 요소가 삭제된다.
            // 데이터가 1개일 때 삭제하는 경우
            @Test
            @DisplayName("데이터가 1개일 때 삭제하는 경우")
            void removeLast_oneElement_success() {

            }
            // 데이터가 여러개일 경우 삭제하는 경우
            @Test
            @DisplayName("데이터가 여러개일 경우 삭제하는 경우")
            void removeLast_multipleElements_success() {

            }
            // 맨 뒤의 요소가 삭제되지 않는 경우
            // 빈 리스트에 삭제하는 경우
            @Test
            @DisplayName("빈 리스트에서 삭제하는 경우")
            void removeLast_emptyList_throwsException() {

            }
        }

        @Nested
        @DisplayName("remove 메서드 테스트")
        class RemoveTest {
            // index 요소가 삭제된다.
            // 맨 앞의 요소를 삭제하는 경우 index = 0
            @Test
            @DisplayName("맨 앞의 요소를 삭제하는 경우 (index = 0)")
            void remove_atFront_success() {

            }
            // 중간 요소를 삭제하는 경우
            @Test
            @DisplayName("중간 요소를 삭제하는 경우")
            void remove_atMiddle_success() {

            }
            // 맨 마지막의 요소를 삭제하는 경우 index = size - 1
            @Test
            @DisplayName("맨 마지막의 요소를 삭제하는 경우( index = size - 1)")
            void remove_atEnd_success() {

            }

            // index 요소가 삭제되지 않는 경우.
            // index가 음수일 경우
            @Test
            @DisplayName("index가 음수 일 경우")
            void remove_negativeIndex_throwsException() {

            }
            // index가 size보다 클 경우
            @Test
            @DisplayName("index가 size 이상일 경우")
            void remove_indexOverSize_throwsException() {

            }
            // 빈 리스트일 경우
            @Test
            @DisplayName("빈 리스트일 경우")
            void remove_emptyList_throwsException() {

            }
        }

        @Nested
        @DisplayName("get 메서드 테스트")
        class GetTest {
            // index 요소를 반환한다.
            // 맨 앞 요소를 조회한다.  (index = 0 )
            @Test
            @DisplayName("맨 앞 요소를 조회한다 (index = 0)")
            void get_atFront_success() {

            }
            // 중간 요소를 조회한다.
            @Test
            @DisplayName("중간 요소를 조회한다")
            void get_atMiddle_success() {

            }
            // 맨 뒤 요소를 조회한다. (index = size - 1)
            @Test
            @DisplayName("맨 뒤 요소를 조회한다 (index = size - 1)")
            void get_atEnd_success() {

            }

            // index 요소가 반환되지 않는다.
            // 빈 리스트를 조회하는 경우
            @Test
            @DisplayName("빈 리스트를 조회하는 경우")
            void get_emptyList_throwsException() {

            }
            // index가 음수인 경우
            @Test
            @DisplayName("index가 음수인 경우")
            void get_negativeIndex_throwsException() {

            }
            // index가 size 이상인 경우
            @Test
            @DisplayName("index가 size 이상인 경우")
            void get_indexOverSize_throwsException() {

            }
        }

        @Nested
        @DisplayName("set 메서드 테스트")
        class SetTest {
            // index 요소를 수정한다.
            // 맨 앞 요소를 수정한다 (index = 0)
            @Test
            @DisplayName("맨 앞 요소를 수정한다 (index = 0)")
            void set_atFront_success() {

            }
            // 중간 요소를 수정한다.
            @Test
            @DisplayName("중간 요소를 수정한다")
            void set_atMiddle_success() {

            }
            // 맨 뒤 요소를 수정한다 (index = size - 1)
            @Test
            @DisplayName("맨 뒤 요소를 수정한다 (index = size - 1)")
            void set_atEnd_success() {

            }
            // index 요소를 수정하지 않는다.
            // 빈 리스트인 경우
            @Test
            @DisplayName("빈 리스트인 경우")
            void set_emptyList_throwsException() {

            }
            // index가 음수인 경우
            @Test
            @DisplayName("index가 음수인 경우")
            void set_negativeIndex_throwsException() {

            }
            // index가 size 이상인 경우
            @Test
            @DisplayName("index가 size 이상인 경우")
            void set_indexOverSize_throwsException() {

            }
        }

        @Nested
        @DisplayName("size 메서드 테스트")
        class SizeTest {
            // size를 조회할 수 있다.
            // 빈 리스트인 경우
            @Test
            @DisplayName("빈 리스트인 경우 0을 반환한다")
            void size_emptyList_returnsZero() {

            }
            // 데이터가 1개일 경우
            @Test
            @DisplayName("데이터가 1개일 경우")
            void size_oneElement_returnsOne() {

            }
            // 데이터가 여러개일 경우
            @Test
            @DisplayName("데이터가 여러개일 경우")
            void size_multipleElements_returnsCount() {

            }
        }

        @Nested
        @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {
            // IsEmpty유무를 조회할 수 있다.
            // 빈 리스트인 경우 true를 반환한다.
            @Test
            @DisplayName("빈 리스트인 경우 true를 반환한다")
            void isEmpty_emptyList_returnsTrue() {

            }
            // 데이터가 존재할 경우 false를 반환한다.
            @Test
            @DisplayName("데이터가 존재할 경우 false를 반환한다")
            void isEmpty_withElements_returnFalse() {

            }
        }

        @Nested
        @DisplayName("contains 메서드 테스트")
        class ContainsTest {
            // 요소가 존재하면 true를 반환한다.
            @Test
            @DisplayName("요소가 존재하면 true를 반환한다")
            void contains_existingElement_returnsTrue() {

            }
            // 요소가 존재하지 않으면 false를 반환한다.
            @Test
            @DisplayName("요소가 존재하지 않으면 false를 반환한다")
            void contains_nonExistingElement_returnsFalse() {

            }
            // 빈 리스트일 경우 false를 반환한다.
            @Test
            @DisplayName("빈 리스트일 경우 false를 반환한다")
            void contains_emptyList_returnsFalse() {

            }
            // null 요소를 찾는 경우
            @Test
            @DisplayName("null 요소를 찾는 경우")
            void contains_nullElement_returnsCorrectly() {

            }
        }

        @Nested
        @DisplayName("indexOf 메서드 테스트")
        class IndexOfTest {
            // 요소가 존재하면 해당 인덱스를 반환한다.
            @Test
            @DisplayName("요소가 존재하면 해당 인덱스를 반환한다")
            void indexOf_existingElement_returnsIndex() {

            }
            // 요소가 존재하지 않으면 -1을 반환한다.
            @Test
            @DisplayName("요소가 존재하지 않으면 -1을 반환한다")
            void indexOf_nonExistingElement_returnsMinusOne() {

            }
            // 빈 리스트일 경우 -1을 반환한다.
            @Test
            @DisplayName("빈 리스트일 경우 -1을 반환한다")
            void indexOf_emptyList_returnsMinusOne() {

            }
            // 중복 요소가 있으면 첫 번째 index를 반환한다.
            @Test
            @DisplayName("중복 요소가 있으면 첫 번째 index를 반환한다")
            void indexOf_duplicateElements_returnsFirstIndex() {

            }
            // null 요소를 찾는 경우
            @Test
            @DisplayName("null 요소를 찾는 경우")
            void indexOf_nullElement_returnsCorrectly() {

            }
        }

        @Nested
        @DisplayName("clear 메서드 테스트")
        class ClearTest {
            // 빈리스트가 된다.
            @Test
            @DisplayName("빈 리스트가 된다.")
            void clear_withElements_becomesEmpty() {

            }
            // 빈 리스트에 clear 호출해도 정상 동작한다.
            @Test
            @DisplayName("빈 리스트에 clear 호출해도 정상 동작한다")
            void clear_emptyList_success() {

            }
            // size가 0이 된다.
            @Test
            @DisplayName("size가 0이 된다")
            void clear_afterClear_sizeIsZero() {

            }
            // isEmpty가 true가 된다.
            @Test
            @DisplayName("isEmpty가 true가 된다")
            void clear_afterClear_isEmptyTrue() {

            }
        }

        @Nested
        @DisplayName("reverse 메서드 테스트")
        class ReverseTest {
            // 리스트의 순서가 뒤집힌다.
            // 빈 리스트일 경우
            @Test
            @DisplayName("빈 리스트일 경우 정상 동작한다")
            void reverse_emptyList_success() {

            }
            // 데이터가 1개일 경우
            @Test
            @DisplayName("데이터가 1개일 경우")
            void reverse_oneElement_success() {

            }
            // 데이터가 여러개일 경우
            @Test
            @DisplayName("데이터가 여러개일 경우")
            void reverse_multipleElements_success() {

            }
        }
    }
}
