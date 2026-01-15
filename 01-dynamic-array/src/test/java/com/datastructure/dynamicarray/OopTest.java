package com.datastructure.dynamicarray;

import com.datastructure.dynamicarray.oop.DynamicArray;
import com.datastructure.dynamicarray.oop.DynamicArrayImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("동적 배열 - OOP 구현 테스트")
class OopTest {

    private DynamicArray<Integer> arr;

    @BeforeEach
    void setUp() {
        arr = new DynamicArrayImpl<>();
    }

    @Nested
    @DisplayName("기본 생성")
    class Creation {

        @Test
        @DisplayName("01. 빈 배열 생성 시 size는 0이다")
        void test01_emptyArraySizeIsZero() {
            // TODO: size() 메서드 구현 후 테스트
            // assertThat(arr.size()).isEqualTo(0);
        }

        @Test
        @DisplayName("02. 빈 배열은 isEmpty가 true다")
        void test02_emptyArrayIsEmpty() {
            // TODO: isEmpty() 메서드 구현 후 테스트
            // assertThat(arr.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("03. 초기 용량을 지정하여 생성할 수 있다")
        void test03_createWithInitialCapacity() {
            // TODO: 생성자 구현 후 테스트
            // DynamicArray<Integer> arrWithCapacity = new DynamicArrayImpl<>(100);
            // assertThat(arrWithCapacity.size()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("요소 추가 (add)")
    class Add {

        @Test
        @DisplayName("04. 요소를 추가하면 size가 증가한다")
        void test04_addIncreasesSize() {
            // TODO: add() 메서드 구현 후 테스트
            // arr.add(1);
            // assertThat(arr.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("05. 여러 요소를 순서대로 추가할 수 있다")
        void test05_addMultipleElements() {
            // TODO: add() 메서드 구현 후 테스트
            // arr.add(1);
            // arr.add(2);
            // arr.add(3);
            // assertThat(arr.size()).isEqualTo(3);
        }

        @Test
        @DisplayName("06. 특정 인덱스에 요소를 삽입할 수 있다")
        void test06_addAtIndex() {
            // TODO: add(index, element) 메서드 구현 후 테스트
            // arr.add(1);
            // arr.add(3);
            // arr.add(1, 2);
            // assertThat(arr.get(1)).isEqualTo(2);
        }

        @Test
        @DisplayName("07. 맨 앞에 요소를 삽입할 수 있다")
        void test07_addAtFront() {
            // TODO: add(0, element) 테스트
            // arr.add(2);
            // arr.add(3);
            // arr.add(0, 1);
            // assertThat(arr.get(0)).isEqualTo(1);
        }

        @Test
        @DisplayName("08. null 요소를 추가할 수 있다")
        void test08_addNullElement() {
            // TODO: null 처리 구현 후 테스트
            // arr.add(null);
            // assertThat(arr.get(0)).isNull();
        }
    }

    @Nested
    @DisplayName("요소 조회 (get)")
    class Get {

        @Test
        @DisplayName("09. 인덱스로 요소를 조회할 수 있다")
        void test09_getByIndex() {
            // TODO: get() 메서드 구현 후 테스트
            // arr.add(10);
            // arr.add(20);
            // assertThat(arr.get(0)).isEqualTo(10);
            // assertThat(arr.get(1)).isEqualTo(20);
        }

        @Test
        @DisplayName("10. 음수 인덱스 조회 시 예외가 발생한다")
        void test10_getWithNegativeIndex() {
            // TODO: 예외 처리 구현 후 테스트
            // arr.add(1);
            // assertThatThrownBy(() -> arr.get(-1))
            //     .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @Test
        @DisplayName("11. 범위 초과 인덱스 조회 시 예외가 발생한다")
        void test11_getWithOutOfBoundsIndex() {
            // TODO: 예외 처리 구현 후 테스트
            // arr.add(1);
            // assertThatThrownBy(() -> arr.get(1))
            //     .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    @Nested
    @DisplayName("요소 수정 (set)")
    class Set {

        @Test
        @DisplayName("12. 특정 인덱스의 요소를 수정할 수 있다")
        void test12_setElement() {
            // TODO: set() 메서드 구현 후 테스트
            // arr.add(1);
            // arr.set(0, 100);
            // assertThat(arr.get(0)).isEqualTo(100);
        }

        @Test
        @DisplayName("13. set은 이전 값을 반환한다")
        void test13_setReturnsOldValue() {
            // TODO: set() 반환값 구현 후 테스트
            // arr.add(1);
            // Integer old = arr.set(0, 100);
            // assertThat(old).isEqualTo(1);
        }

        @Test
        @DisplayName("14. 유효하지 않은 인덱스에 set 시 예외가 발생한다")
        void test14_setWithInvalidIndex() {
            // TODO: 예외 처리 구현 후 테스트
            // assertThatThrownBy(() -> arr.set(0, 1))
            //     .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    @Nested
    @DisplayName("요소 삭제 (remove)")
    class Remove {

        @Test
        @DisplayName("15. 특정 인덱스의 요소를 삭제할 수 있다")
        void test15_removeByIndex() {
            // TODO: remove() 메서드 구현 후 테스트
            // arr.add(1);
            // arr.add(2);
            // arr.add(3);
            // arr.remove(1);
            // assertThat(arr.size()).isEqualTo(2);
            // assertThat(arr.get(1)).isEqualTo(3);
        }

        @Test
        @DisplayName("16. remove는 삭제된 요소를 반환한다")
        void test16_removeReturnsElement() {
            // TODO: remove() 반환값 구현 후 테스트
            // arr.add(100);
            // Integer removed = arr.remove(0);
            // assertThat(removed).isEqualTo(100);
        }

        @Test
        @DisplayName("17. 맨 앞 요소를 삭제할 수 있다")
        void test17_removeFirst() {
            // TODO: remove(0) 테스트
            // arr.add(1);
            // arr.add(2);
            // arr.remove(0);
            // assertThat(arr.get(0)).isEqualTo(2);
        }

        @Test
        @DisplayName("18. 맨 뒤 요소를 삭제할 수 있다")
        void test18_removeLast() {
            // TODO: remove(size-1) 테스트
            // arr.add(1);
            // arr.add(2);
            // arr.remove(1);
            // assertThat(arr.size()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("검색 (contains, indexOf)")
    class Search {

        @Test
        @DisplayName("19. 존재하는 요소를 contains로 확인할 수 있다")
        void test19_containsExistingElement() {
            // TODO: contains() 메서드 구현 후 테스트
            // arr.add(1);
            // arr.add(2);
            // assertThat(arr.contains(1)).isTrue();
        }

        @Test
        @DisplayName("20. 존재하지 않는 요소는 contains가 false다")
        void test20_containsNonExistingElement() {
            // TODO: contains() 메서드 구현 후 테스트
            // arr.add(1);
            // assertThat(arr.contains(999)).isFalse();
        }

        @Test
        @DisplayName("21. indexOf로 요소의 인덱스를 찾을 수 있다")
        void test21_indexOfExistingElement() {
            // TODO: indexOf() 메서드 구현 후 테스트
            // arr.add(10);
            // arr.add(20);
            // arr.add(30);
            // assertThat(arr.indexOf(20)).isEqualTo(1);
        }

        @Test
        @DisplayName("22. 존재하지 않는 요소의 indexOf는 -1이다")
        void test22_indexOfNonExistingElement() {
            // TODO: indexOf() 메서드 구현 후 테스트
            // arr.add(1);
            // assertThat(arr.indexOf(999)).isEqualTo(-1);
        }
    }

    @Nested
    @DisplayName("자동 확장 (grow)")
    class AutoGrow {

        @Test
        @DisplayName("23. 초기 용량(10)을 초과하면 자동 확장된다")
        void test23_autoGrowBeyondInitialCapacity() {
            // TODO: 자동 확장 구현 후 테스트
            // for (int i = 0; i < 15; i++) {
            //     arr.add(i);
            // }
            // assertThat(arr.size()).isEqualTo(15);
        }

        @Test
        @DisplayName("24. 대량의 요소를 추가해도 정상 동작한다")
        void test24_addManyElements() {
            // TODO: 대량 추가 테스트
            // for (int i = 0; i < 1000; i++) {
            //     arr.add(i);
            // }
            // assertThat(arr.size()).isEqualTo(1000);
            // assertThat(arr.get(999)).isEqualTo(999);
        }
    }

    @Nested
    @DisplayName("자동 축소 (shrink)")
    class AutoShrink {

        @Test
        @DisplayName("25. 요소가 1/4 이하가 되면 자동 축소된다")
        void test25_autoShrink() {
            // TODO: 자동 축소 구현 후 테스트
            // for (int i = 0; i < 100; i++) {
            //     arr.add(i);
            // }
            // for (int i = 0; i < 90; i++) {
            //     arr.remove(arr.size() - 1);
            // }
            // assertThat(arr.size()).isEqualTo(10);
        }

        @Test
        @DisplayName("26. 최소 용량(10) 이하로는 축소되지 않는다")
        void test26_shrinkMinimumCapacity() {
            // TODO: 최소 용량 유지 테스트
            // arr.add(1);
            // arr.remove(0);
            // assertThat(arr.size()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("제네릭 타입")
    class GenericType {

        @Test
        @DisplayName("27. String 타입을 저장할 수 있다")
        void test27_stringType() {
            // TODO: 제네릭 테스트
            // DynamicArray<String> strArr = new DynamicArrayImpl<>();
            // strArr.add("Hello");
            // strArr.add("World");
            // assertThat(strArr.get(0)).isEqualTo("Hello");
        }

        @Test
        @DisplayName("28. 사용자 정의 객체를 저장할 수 있다")
        void test28_customObjectType() {
            // TODO: 사용자 정의 객체 테스트
            // record Person(String name, int age) {}
            // DynamicArray<Person> personArr = new DynamicArrayImpl<>();
            // personArr.add(new Person("Alice", 30));
            // assertThat(personArr.get(0).name()).isEqualTo("Alice");
        }
    }

    @Nested
    @DisplayName("기타 연산")
    class Miscellaneous {

        @Test
        @DisplayName("29. clear로 모든 요소를 삭제할 수 있다")
        void test29_clear() {
            // TODO: clear() 메서드 구현 후 테스트
            // arr.add(1);
            // arr.add(2);
            // arr.add(3);
            // arr.clear();
            // assertThat(arr.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("30. Iterator로 순회할 수 있다")
        void test30_iterator() {
            // TODO: Iterator 구현 후 테스트
            // arr.add(1);
            // arr.add(2);
            // arr.add(3);
            // int sum = 0;
            // for (Integer i : arr) {
            //     sum += i;
            // }
            // assertThat(sum).isEqualTo(6);
        }
    }
}
