package com.datastructure.bst;

import com.datastructure.bst.oop.BST;
import com.datastructure.bst.oop.BinarySearchTreeImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("이진 탐색 트리 - OOP 구현 테스트")
class OopTest {

    private BST<Integer> bst;

    @BeforeEach
    void setUp() {
        bst = new BinarySearchTreeImpl<>();
    }

    @Nested
    @DisplayName("기본 연산")
    class BasicOperations {

        @Test
        @DisplayName("01. 빈 트리 생성 시 isEmpty는 true다")
        void test01_emptyTreeIsEmpty() {
            // TODO: isEmpty() 구현 후 테스트
            // assertThat(bst.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("02. insert로 값을 추가할 수 있다")
        void test02_insert() {
            // TODO: insert() 구현 후 테스트
            // bst.insert(5);
            // assertThat(bst.contains(5)).isTrue();
        }

        @Test
        @DisplayName("03. search로 값을 검색할 수 있다")
        void test03_search() {
            // TODO: search() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // assertThat(bst.search(3)).isTrue();
            // assertThat(bst.search(6)).isFalse();
        }

        @Test
        @DisplayName("04. delete로 값을 삭제할 수 있다")
        void test04_delete() {
            // TODO: delete() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.delete(3);
            // assertThat(bst.contains(3)).isFalse();
        }

        @Test
        @DisplayName("05. min/max 동작")
        void test05_minMax() {
            // TODO: min(), max() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // assertThat(bst.min()).isEqualTo(3);
            // assertThat(bst.max()).isEqualTo(7);
        }
    }

    @Nested
    @DisplayName("제네릭 타입")
    class GenericType {

        @Test
        @DisplayName("06. String 타입 BST")
        void test06_stringBST() {
            // TODO: 제네릭 테스트
            // BST<String> stringBst = new BinarySearchTreeImpl<>();
            // stringBst.insert("banana");
            // stringBst.insert("apple");
            // stringBst.insert("cherry");
            // assertThat(stringBst.inorder()).containsExactly("apple", "banana", "cherry");
        }

        @Test
        @DisplayName("07. 사용자 정의 Comparable 객체")
        void test07_customComparable() {
            // TODO: 사용자 정의 객체 테스트
            // record Person(String name, int age) implements Comparable<Person> {
            //     public int compareTo(Person o) { return Integer.compare(age, o.age); }
            // }
            // BST<Person> personBst = new BinarySearchTreeImpl<>();
            // personBst.insert(new Person("Alice", 30));
            // personBst.insert(new Person("Bob", 25));
            // assertThat(personBst.min().name()).isEqualTo("Bob");
        }
    }

    @Nested
    @DisplayName("순회")
    class Traversal {

        @Test
        @DisplayName("08. inorder 순회")
        void test08_inorder() {
            // TODO: inorder() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // assertThat(bst.inorder()).containsExactly(3, 5, 7);
        }

        @Test
        @DisplayName("09. preorder 순회")
        void test09_preorder() {
            // TODO: preorder() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // assertThat(bst.preorder()).containsExactly(5, 3, 7);
        }

        @Test
        @DisplayName("10. postorder 순회")
        void test10_postorder() {
            // TODO: postorder() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // assertThat(bst.postorder()).containsExactly(3, 7, 5);
        }

        @Test
        @DisplayName("11. levelorder 순회")
        void test11_levelorder() {
            // TODO: levelorder() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // assertThat(bst.levelorder()).containsExactly(5, 3, 7);
        }

        @Test
        @DisplayName("12. Iterator로 순회 (중위)")
        void test12_iterator() {
            // TODO: Iterator 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // List<Integer> result = new ArrayList<>();
            // for (Integer val : bst) {
            //     result.add(val);
            // }
            // assertThat(result).containsExactly(3, 5, 7);
        }
    }

    @Nested
    @DisplayName("고급 연산")
    class AdvancedOperations {

        @Test
        @DisplayName("13. rank - value보다 작은 키 개수")
        void test13_rank() {
            // TODO: rank() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // bst.insert(1);
            // assertThat(bst.rank(5)).isEqualTo(2);  // 1, 3
            // assertThat(bst.rank(1)).isEqualTo(0);
        }

        @Test
        @DisplayName("14. select - k번째로 작은 값")
        void test14_select() {
            // TODO: select() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // bst.insert(1);
            // assertThat(bst.select(0)).isEqualTo(1);
            // assertThat(bst.select(2)).isEqualTo(5);
        }

        @Test
        @DisplayName("15. floor")
        void test15_floor() {
            // TODO: floor() 구현 후 테스트
            // bst.insert(10);
            // bst.insert(20);
            // bst.insert(30);
            // assertThat(bst.floor(25)).isEqualTo(20);
        }

        @Test
        @DisplayName("16. ceiling")
        void test16_ceiling() {
            // TODO: ceiling() 구현 후 테스트
            // bst.insert(10);
            // bst.insert(20);
            // bst.insert(30);
            // assertThat(bst.ceiling(15)).isEqualTo(20);
        }

        @Test
        @DisplayName("17. predecessor")
        void test17_predecessor() {
            // TODO: predecessor() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // assertThat(bst.predecessor(5)).isEqualTo(3);
        }

        @Test
        @DisplayName("18. successor")
        void test18_successor() {
            // TODO: successor() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // assertThat(bst.successor(5)).isEqualTo(7);
        }
    }

    @Nested
    @DisplayName("삭제 케이스")
    class DeleteCases {

        @Test
        @DisplayName("19. 리프 노드 삭제")
        void test19_deleteLeaf() {
            // TODO: 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.delete(3);
            // assertThat(bst.contains(3)).isFalse();
        }

        @Test
        @DisplayName("20. 자식 하나인 노드 삭제")
        void test20_deleteOneChild() {
            // TODO: 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(1);
            // bst.delete(3);
            // assertThat(bst.contains(1)).isTrue();
        }

        @Test
        @DisplayName("21. 자식 둘인 노드 삭제")
        void test21_deleteTwoChildren() {
            // TODO: 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // bst.insert(6);
            // bst.insert(8);
            // bst.delete(7);
            // assertThat(bst.inorder()).containsExactly(3, 5, 6, 8);
        }

        @Test
        @DisplayName("22. 루트 삭제")
        void test22_deleteRoot() {
            // TODO: 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // bst.delete(5);
            // assertThat(bst.contains(5)).isFalse();
            // assertThat(bst.size()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("기타")
    class Miscellaneous {

        @Test
        @DisplayName("23. height 계산")
        void test23_height() {
            // TODO: height() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // bst.insert(1);
            // assertThat(bst.height()).isEqualTo(2);
        }

        @Test
        @DisplayName("24. clear")
        void test24_clear() {
            // TODO: clear() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.clear();
            // assertThat(bst.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("25. rangeSearch - 범위 검색")
        void test25_rangeSearch() {
            // TODO: rangeSearch() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // bst.insert(1);
            // bst.insert(9);
            // assertThat(bst.rangeSearch(3, 7)).containsExactly(3, 5, 7);
        }

        @Test
        @DisplayName("26. equals 비교")
        void test26_equals() {
            // TODO: equals() 구현 후 테스트
            // BST<Integer> bst2 = new BinarySearchTreeImpl<>();
            // bst.insert(5);
            // bst.insert(3);
            // bst2.insert(5);
            // bst2.insert(3);
            // assertThat(bst).isEqualTo(bst2);
        }

        @Test
        @DisplayName("27. hashCode 일관성")
        void test27_hashCode() {
            // TODO: hashCode() 구현 후 테스트
            // BST<Integer> bst2 = new BinarySearchTreeImpl<>();
            // bst.insert(5);
            // bst2.insert(5);
            // assertThat(bst.hashCode()).isEqualTo(bst2.hashCode());
        }

        @Test
        @DisplayName("28. toString")
        void test28_toString() {
            // TODO: toString() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // assertThat(bst.toString()).contains("5", "3");
        }

        @Test
        @DisplayName("29. stream 지원")
        void test29_stream() {
            // TODO: stream() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // int sum = bst.stream().mapToInt(Integer::intValue).sum();
            // assertThat(sum).isEqualTo(15);
        }

        @Test
        @DisplayName("30. toArray 변환")
        void test30_toArray() {
            // TODO: toArray() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // Integer[] arr = bst.toArray(new Integer[0]);
            // assertThat(arr).containsExactly(3, 5, 7);
        }
    }
}
