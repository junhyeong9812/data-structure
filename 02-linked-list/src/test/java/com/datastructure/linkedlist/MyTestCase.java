package com.datastructure.linkedlist;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MyTestCase {

    @Nested
    @DisplayName("단일 연결 리스트")
    class SinglyLinkedListTest {

        @Nested
        @DisplayName("단일 연결 리스트 생성")
        class Creation {
            // 단일 연결 리스트가 생성된다.
            // 생성 시 size가 0이다.
            // 생성 시 isEmpty가 true이다.
        }

        @Nested
        @DisplayName("addFirst 메서드 테스트")
        class AddFirstTest {
            // 맨 앞에 요소가 추가된다.
            // 빈 리스트에 추가하는 경우
            // 요소가 null인 경우
            // 데이터가 1개일 경우
            // 데이터가 여러개일 경우
        }

        @Nested
        @DisplayName("addLast 메서드 테스트")
        class AddLastTest {
            // 맨 뒤에 요소가 추가된다.
            // 빈 리스트에 추가하는 경우
            // 요소가 null인 경우
            // 데이터가 1개일 경우
            // 데이터가 여러개일 경우
        }

        @Nested
        @DisplayName("add 메서드 테스트")
        class AddTest {
            // 특정 위치에 요소가 추가된다.
            // 빈 리스트에 추가하는 경우 (index 0)
            // 요소가 null인 경우
            // 데이터가 1개일 경우
            // 데이터가 여러개일 경우
            // 맨 앞에 추가하는 경우 (index 0)
            // 중간에 추가하는 경우
            // 맨 뒤에 추가하는 경우 (index == size)

            // 특정 위치에 요소가 추가 되지않는다.
            // 음수 인덱스인 경우 -> indexOutOfBoundsException
            // size보다 큰 인덱스인 경우 -> indexOutOfBoundsException
        }

        @Nested
        @DisplayName("removeFirst 메서드 테스트")
        class RemoveFirstTest {
            // 맨 앞의 위치에 요소가 삭제된다.
            // 데이터가 1개일 경우
            // 데이터가 여러개일 경우

            //맨 앞 요소가 삭제되지 않는다.
            // 빈 리스트에 삭제를 하는 경우(index 0)
        }

        @Nested
        @DisplayName("removeLast 메서드 테스트")
        class RemoveLastTest {
            //맨 뒤 위치의 요소가 삭제된다.
            // 데이터가 1개일 경우
            // 데이터가 여러개일 경우

            // 맨 앞 요소가 삭제되지 않는다.
            // 빈 리스트에 삭제를 하는 경우(index 0)
        }

        @Nested
        @DisplayName("remove 메서드 테스트")
        class RemoveTest {
            // index 요소의 데이터를 삭제한다.
            // 맨 앞 삭제 (index 0)
            // 중간 삭제
            // 맨 뒤 삭제 (index == size - 1)

            // index 위치의 요소가 삭제되지 않는다.
            // 빈 리스트를 삭제하는 경우 (index 0)
            // index가 음수일 경우
            // index가 size보다 클 경우
        }

        @Nested
        @DisplayName("get 메서드 테스트")
        class GetTest {
            // index의 요소를 반환한다.
            //맨 앞 인덱스를 조회한다.
            //중간 인덱스를 조회한다.
            //맨 뒤 인덱스를 조회힌다.

            // index 위치의 요소를 반환하지 않는다.
            // 빈 리스트일 경우
            // index가 음수인 경우
            // index가 size 이상일 경우
        }

        @Nested
        @DisplayName("set 메서드 테스트")
        class SetTest {
            // index의 요소를 변경한다.
            // 맨 앞 index의 요소를 변경한다.
            // 중간 index의 요소를 변경한다.
            // 맨 뒤 index의 요소를 변경한다.

            // index 위치의 요소를 변경할 수 없다.
            // 빈 리스트일 경우
            // index가 음수일 경우
            // index가 size 이상일 경우
        }

        @Nested
        @DisplayName("size 메서드 테스트")
        class SizeTest {
            // 사이즈를 반환한다.
            // 빈 리스트일 경우 0을 반환한다.
            // 데이터의 길이만큼 반환한다.
            // 추가/삭제 후 정확히 반영되는 지 확인
        }

        @Nested
        @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {
            // 빈 리스트일 경우 true를 반환한다.
            // 데이터가 존재하면 false를 반환한다.
        }

        @Nested
        @DisplayName("contains 메서드 테스트")
        class ContainsTest {
            // 요소가 존재하면 True를 반환한다.
            // 요소가 존재하지 않으면 false를 반환한다.
            // 빈 리스트일 경우 false를 반환한다.
            // null 요소를 찾는 경우
        }

        @Nested
        @DisplayName("indexOf 메서드 테스트")
        class IndexOfTest {
            // 요소가 존재하면 해당 index를 반환한다.
            // 요소가 존재하지 않으면 -1을 반환한다.
            // 빈리스트일 경우 -1을 반환한다.
            // 중복 요소가 있으면 첫 번째 index를 반환한다.
            // null 요소를 찾는 경우
        }

        @Nested
        @DisplayName("clear 메서드 테스트")
        class ClearTest {
            // 해당 데이터를 빈 리스트로 변경한다.
            //빈 리스트에 clear를 호출해도 정상 동작한다.
            // clear 후 size가 0이다.
            // clear 후 isEmpty가 true다.
        }

        @Nested
        @DisplayName("reverse 메서드 테스트")
        class ReverseTest {
            // 리스트를 뒤집는다.
            // 빈 리스트는 뒤집어도 정상 동작한다.
            // 요소가 1개인 리스트를 뒤집는다.
            // 요소가 여러 개인리스트를 뒤집는다.
        }
    }

    @Nested
    @DisplayName("이중 연결 리스트")
    class DoublyLinkedListTest {

        @Nested
        @DisplayName("이중 연결 리스트 생성")
        class Creation {
            // 이중 연결 리스트가 생성이 된다.
            // 이중 연결 리스트의 size가 0이다.
            // 이중 연결 리스트의 isEmpty는 true이다.

        }

        @Nested
        @DisplayName("addFirst 메서드 테스트")
        class AddFirstTest {
            // 요소가 맨처음 추가된다.
            // 빈 리스트에 맨처음 추가된다.
            // 데이터가 1개일 때 추가된다.
            // 데이터가 여러개일 때 추가된다.
            // null 요소를 추가한다.
        }

        @Nested
        @DisplayName("addLast 메서드 테스트")
        class AddLastTest {
            //요소가 마지막에 추가된다.
            //빈 리스트에 맨 마지막에 추가된다.
            // 데이터가 1개일 때 추가된다.
            // 데이터가 여러개일 때 추가된다.
            // null 요소를 추가한다.
        }

        @Nested
        @DisplayName("add 메서드 테스트")
        class AddTest {
            // 요소가 index 위치에 추가된다.
            // 요소가 맨 앞에 추가된다. (index = 0)
            // 요소가 중간에 추가된다.
            // 요소가 맨 뒤에 추가된다. (index = size)
            // null 요소가 추가된다.

            //요소가 추가되지 않는다.
            //index가 음수일 때 추가되지않는다.
            //index가 size보다 클 경우 추가되지 않는다.
        }

        @Nested
        @DisplayName("removeFirst 메서드 테스트")
        class RemoveFirstTest {
            // 맨 앞의 요소가 삭제된다.
            // 데이터가 1개일 때 삭제하는 경우
            // 데이터가 여러개일 때 삭제하는 경우

            // 맨 앞의 요소가 삭제되지 않는다.
            // 빈 리스트에 삭제하는 경우
        }

        @Nested
        @DisplayName("removeLast 메서드 테스트")
        class RemoveLastTest {
            // 맨 뒤의 요소가 삭제된다.
            // 데이터가 1개일 때 삭제하는 경우
            // 데이터가 여러개일 경우 삭제하는 경우

            // 맨 뒤의 요소가 삭제되지 않는 경우
            // 빈 리스트에 삭제하는 경우
        }

        @Nested
        @DisplayName("remove 메서드 테스트")
        class RemoveTest {
            // index 요소가 삭제된다.
            // 맨 앞의 요소를 삭제하는 경우 index = 0
            // 중간 요소를 삭제하는 경우
            // 맨 마지막의 요소를 삭제하는 경우 index = size - 1

            // index 요소가 삭제되지 않는 경우.
            // index가 음수일 경우
            // index가 size보다 클 경우
            // 빈 리스트일 경우
        }

        @Nested
        @DisplayName("get 메서드 테스트")
        class GetTest {
            // index 요소를 반환한다.
            // 맨 앞 요소를 조회한다.  (index = 0 )
            // 중간 요소를 조회한다.
            // 맨 뒤 요소를 조회한다. (index = size - 1)

            // index 요소가 반환되지 않는다.
            // 빈 리스트를 조회하는 경우
            // index가 음수인 경우
            // index가 size 이상인 경우
        }

        @Nested
        @DisplayName("set 메서드 테스트")
        class SetTest {
            // index 요소를 수정한다.
            // 맨 앞 요소를 수정한다 (index = 0)
            // 중간 요소를 수정한다.
            // 맨 뒤 요소를 수정한다 (index = size - 1)

            // index 요소를 수정하지 않는다.
            // 빈 리스트인 경우
            // index가 음수인 경우
            // index가 size 이상인 경우
        }

        @Nested
        @DisplayName("size 메서드 테스트")
        class SizeTest {
            // size를 조회할 수 있다.
            // 빈 리스트인 경우
            // 데이터가 1개일 경우
            // 데이터가 여러개일 경우
        }

        @Nested
        @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {
            // IsEmpty유무를 조회할 수 있다.
            // 빈 리스트인 경우 true를 반환한다.
            // 데이터가 존재할 경우 false를 반환한다.
        }

        @Nested
        @DisplayName("contains 메서드 테스트")
        class ContainsTest {
            // 요소가 존재하면 true를 반환한다.
            // 요소가 존재하지 않으면 false를 반환한다.
            // 빈 리스트일 경우 false를 반환한다.
            // null 요소를 찾는 경우
        }

        @Nested
        @DisplayName("indexOf 메서드 테스트")
        class IndexOfTest {
            // 요소가 존재하면 해당 인덱스를 반환한다.
            // 요소가 존재하지 않으면 -1을 반환한다.
            // 빈 리스트일 경우 -1을 반환한다.
            // 중복 요소가 있으면 첫 번째 index를 반환한다.
            // null 요소를 찾는 경우
        }

        @Nested
        @DisplayName("clear 메서드 테스트")
        class ClearTest {
            // 빈리스트가 된다.
            // 빈 리스트에 clear 호출해도 정상 동작한다.
            // size가 0이 된다.
            // isEmpty가 true가 된다.
        }

        @Nested
        @DisplayName("reverse 메서드 테스트")
        class ReverseTest {
            // 리스트의 순서가 뒤집힌다.
            // 빈 리스트일 경우
            // 데이터가 1개일 경우
            // 데이터가 여러개일 경우
        }
    }
}
