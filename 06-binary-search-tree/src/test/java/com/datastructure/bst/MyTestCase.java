package com.datastructure.bst;

import com.datastructure.bst.pop.BinarySearchTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MyTestCase {

    @Nested
    @DisplayName("BinarySearchTree 테스트")
    class BinarySearchTreeTest {

        BinarySearchTree<Integer> tree;

        @BeforeEach
        void setup() { tree = new BinarySearchTree<>(); }

        @Nested
        @DisplayName("insert 메서드 테스트")
        class InsertTest {

            @Test
            @DisplayName("값을 삽입할 수 있다.")
            void insert_value() {
                tree.insert(1);

                assertThat(tree.size()).isEqualTo(1);
                assertThat(tree.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("null인 값을 삽입하면 예외가 발생한다.")
            void insert_null_throws_exception() {
                assertThatThrownBy(() -> tree.insert(null))
                        .isInstanceOf(Exception.class);
            }

            @Test
            @DisplayName("중복 값을 삽입하면 무시된다.")
            void insert_duplicate_ignored() {
                tree.insert(1);

                tree.insert(1);

                assertThat(tree.size()).isEqualTo(1);
                assertThat(tree.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("삽입 후 size가 증가한다.")
            void insert_increases_size() {
                tree.insert(1);
                assertThat(tree.size()).isEqualTo(1);

                tree.insert(2);

                assertThat(tree.size()).isEqualTo(2);
                assertThat(tree.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("여러 값을 삽입하면 BST 속성을 유지한다.")
            void insert_maintains_bst_property() {
                tree.insert(1);
                tree.insert(2);
                tree.insert(3);

                assertThat(tree.size()).isEqualTo(3);
                assertThat(tree.inorder()).containsExactly(1, 2, 3);
            }
        }

        @Nested
        @DisplayName("search 메서드 테스트")
        class SearchTest {

            @Test
            @DisplayName("빈 트리에서 조회 시 false를 반환한다.")
            void search_returns_false_when_empty() {
                boolean result = tree.search(1);

                assertThat(tree.size()).isZero();
                assertThat(tree.isEmpty()).isTrue();
                assertThat(result).isFalse();
            }

            @Test
            @DisplayName("값이 존재하면 true를 반환한다.")
            void search_returns_true_when_exists() {
                tree.insert(1);

                boolean result = tree.search(1);

                assertThat(result).isTrue();
            }

            @Test
            @DisplayName("값이 없으면 false를 반환한다.")
            void search_returns_false_when_not_exists() {
                tree.insert(1);
                tree.insert(2);
                tree.insert(3);

                boolean result = tree.search(4);

                assertThat(result).isFalse();
            }

            @Test
            @DisplayName("반복해서 조회해도 같은 값을 반환한다.")
            void search_multiple_times_returns_same_result() {

                tree.insert(1);
                tree.insert(2);
                tree.insert(3);

                assertThat(tree.search(1)).isTrue();
                assertThat(tree.search(1)).isTrue();
                assertThat(tree.search(1)).isTrue();
            }
        }

        @Nested
        @DisplayName("delete 메서드 테스트")
        class DeleteTest {


        }

        @Nested
        @DisplayName("contains 메서드 테스트")
        class ContainsTest {}

        @Nested
        @DisplayName("min 메서드 테스트")
        class MinTest {}

        @Nested
        @DisplayName("max 메서드 테스트")
        class MaxTest {}

        @Nested
        @DisplayName("size 메서드 테스트")
        class SizeTest {}

        @Nested
        @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {}

        @Nested
        @DisplayName("clear 메서드 테스트")
        class ClearTest {}

        @Nested
        @DisplayName("height 메서드 테스트")
        class HeightTest {}

        @Nested
        @DisplayName("inorder 순회 테스트")
        class InorderTest {}

        @Nested
        @DisplayName("preorder 순회 테스트")
        class PreorderTest {}

        @Nested
        @DisplayName("postorder 순회 테스트")
        class PostorderTest {}

        @Nested
        @DisplayName("levelorder 순회 테스트")
        class LevelorderTest {}

        @Nested
        @DisplayName("floor 메서드 테스트")
        class FloorTest {}

        @Nested
        @DisplayName("ceiling 메서드 테스트")
        class CeilingTest {}

        @Nested
        @DisplayName("rank 메서드 테스트")
        class RankTest {}

        @Nested
        @DisplayName("select 메서드 테스트")
        class SelectTest {}

        @Nested
        @DisplayName("predecessor 메서드 테스트")
        class PredecessorTest {}

        @Nested
        @DisplayName("successor 메서드 테스트")
        class SuccessorTest {}
    }
}