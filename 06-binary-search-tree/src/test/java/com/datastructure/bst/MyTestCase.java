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

            @Test
            @DisplayName("빈 트리에서 삭제 시 예외가 발생한다.")
            void delete_throws_exception_when_empty() {
                assertThatThrownBy(() -> tree.delete(1))
                        .isInstanceOf(Exception.class);
            }

            @Test
            @DisplayName("리프 노드를 삭제할 수 있다.")
            void delete_leaf_node() {
                tree.insert(5);
                tree.insert(3);
                tree.insert(7);

                tree.delete(7);

                assertThat(tree.size()).isEqualTo(2);
                assertThat(tree.inorder()).containsExactly(3, 5);
            }

            @Test
            @DisplayName("자식이 하나인 노드를 삭제할 수 있다.")
            void delete_node_with_one_child() {
                tree.insert(5);
                tree.insert(3);
                tree.insert(7);
                tree.insert(9);

                tree.delete(7);

                assertThat(tree.size()).isEqualTo(3);
                assertThat(tree.inorder()).containsExactly(3, 5, 9);
            }

            @Test
            @DisplayName("자식이 둘인 노드를 삭제할 수 있다.")
            void delete_node_with_two_children() {
                tree.insert(5);
                tree.insert(3);
                tree.insert(7);
                tree.insert(1);
                tree.insert(4);

                tree.delete(3);

                assertThat(tree.size()).isEqualTo(4);
                assertThat(tree.inorder()).containsExactly(1, 4, 5, 7);
            }

            @Test
            @DisplayName("존재하지 않는 값을 삭제하면 예외가 발생한다.")
            void delete_non_existent_value() {
                assertThatThrownBy(() -> tree.delete(1))
                        .isInstanceOf(Exception.class);
            }

            @Test
            @DisplayName("삭제 후 size가 감소한다.")
            void delete_decreases_size() {
                tree.insert(1);
                tree.insert(2);
                tree.insert(3);
                tree.insert(4);

                tree.delete(3);

                assertThat(tree.size()).isEqualTo(3);
            }

            @Test
            @DisplayName("삭제 후 BST 속성이 유지된다.")
            void delete_maintains_bst_property() {
                tree.insert(1);
                tree.insert(2);
                tree.insert(3);
                tree.insert(4);
                tree.insert(5);

                tree.delete(3);

                assertThat(tree.size()).isEqualTo(4);
                assertThat(tree.inorder()).containsExactlyInAnyOrder(1, 2, 4, 5);
            }
        }

        @Nested
        @DisplayName("contains 메서드 테스트")
        class ContainsTest {
            @Test
            @DisplayName("빈 트리에서는 false를 반환한다.")
            void contains_returns_false_when_empty() {
                boolean result = tree.contains(1);

                assertThat(result).isFalse();
            }

            @Test
            @DisplayName("존재하면 true를 반환한다.")
            void contains_returns_true_when_exists() {
                tree.insert(5);
                tree.insert(3);
                tree.insert(7);

                boolean result = tree.contains(3);

                assertThat(result).isTrue();
            }

            @Test
            @DisplayName("존재하지 않으면 false를 반환한다.")
            void contains_returns_false_when_not_exists() {
                tree.insert(5);
                tree.insert(3);
                tree.insert(7);

                boolean result = tree.contains(9);

                assertThat(result).isFalse();
            }
        }

        @Nested
        @DisplayName("min 메서드 테스트")
        class MinTest {

            @Test
            @DisplayName("빈 값일 경우 예외가 발생한다.")
            void min_throws_exception_when_empty() {
                assertThatThrownBy(() -> tree.min())
                        .isInstanceOf(Exception.class);
            }

            @Test
            @DisplayName("최솟값을 반환한다.")
            void min_returns_minimum_value() {
                tree.insert(5);
                tree.insert(3);
                tree.insert(7);

                int result = tree.min();

                assertThat(result).isEqualTo(3);
            }

            @Test
            @DisplayName("노드가 하나면 그 값을 반환한다.")
            void min_returns_single_node_value() {
                tree.insert(5);

                int result = tree.min();

                assertThat(result).isEqualTo(5);
            }
        }

        @Nested
        @DisplayName("max 메서드 테스트")
        class MaxTest {
            @Test
            @DisplayName("빈 값일 경우 예외가 발생한다.")
            void max_throws_exception_when_empty() {
                assertThatThrownBy(() -> tree.max())
                        .isInstanceOf(Exception.class);
            }

            @Test
            @DisplayName("최댓값을 반환한다.")
            void max_returns_maximum_value() {
                tree.insert(5);
                tree.insert(3);
                tree.insert(7);

                int result = tree.max();

                assertThat(result).isEqualTo(7);
            }

            @Test
            @DisplayName("노드가 하나면 그 값을 반환한다.")
            void max_returns_single_node_value() {
                tree.insert(5);

                int result = tree.max();

                assertThat(result).isEqualTo(5);
            }
        }

        @Nested
        @DisplayName("size 메서드 테스트")
        class SizeTest {
            @Test
            @DisplayName("빈 트리의 size는 0이다.")
            void size_returns_zero_when_empty() {
                assertThat(tree.size()).isZero();
            }

            @Test
            @DisplayName("insert 시 size가 증가한다.")
            void size_increases_after_insert() {
                tree.insert(5);
                tree.insert(3);
                tree.insert(7);
                assertThat(tree.size()).isEqualTo(3);

                tree.insert(9);
                assertThat(tree.size()).isEqualTo(4);

            }

            @Test
            @DisplayName("delete 시 size가 감소한다.")
            void size_decreases_after_delete() {
                tree.insert(5);
                tree.insert(3);
                tree.insert(7);
                assertThat(tree.size()).isEqualTo(3);

                tree.delete(7);
                assertThat(tree.size()).isEqualTo(2);
            }
        }

        @Nested
        @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {

            @Test
            @DisplayName("빈 트리에서는 true를 반환한다.")
            void isEmpty_returns_true_when_empty() {
                assertThat(tree.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("요소가 있는 트리는 false를 반환한다.")
            void isEmpty_returns_false_when_not_empty() {
                tree.insert(5);
                tree.insert(3);
                tree.insert(7);
                assertThat(tree.isEmpty()).isFalse();
            }
        }

        @Nested
        @DisplayName("clear 메서드 테스트")
        class ClearTest {

            @Test
            @DisplayName("빈 tree는 초기화 된다.")
            void clear_empty_tree_remains_empty() {
                tree.clear();

                assertThat(tree.size()).isZero();
                assertThat(tree.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("요소가 있는 tree는 초기화된다.")
            void clear_removes_all_nodes() {
                tree.insert(5);
                tree.insert(3);
                tree.insert(7);
                assertThat(tree.size()).isEqualTo(3);

                tree.clear();

                assertThat(tree.size()).isZero();
                assertThat(tree.isEmpty()).isTrue();
            }
        }

        @Nested
        @DisplayName("height 메서드 테스트")
        class HeightTest {
            @Test
            @DisplayName("빈 트리는 결과가 0이다.")
            void height_returns_zero() {
                assertThat(tree.height()).isZero();
            }

            @Test
            @DisplayName("루트가 1개인 트리의 결과는 1이다.")
            void height_returns_one_for_single_node() {
                tree.insert(5);

                assertThat(tree.height()).isEqualTo(1);
            }

            @Test
            @DisplayName("편향 트리는 결과가 편향된 높이이다.")
            void height_returns_skewed_height() {
                tree.insert(1);
                tree.insert(2);
                tree.insert(3);
                tree.insert(4);
                tree.insert(5);
                tree.insert(6);

                assertThat(tree.height()).isEqualTo(6);
            }

            @Test
            @DisplayName("요소가 있는 트리는 리프 노드가 높이의 기준이 된다.")
            void height_returns_height_based_on_deepest_leaf() {
                tree.insert(5);
                tree.insert(3);
                tree.insert(7);
                tree.insert(8);

                assertThat(tree.height()).isEqualTo(3);
            }
        }

        @Nested
        @DisplayName("inorder 순회 테스트")
        class InorderTest {
            @Test
            @DisplayName("빈 트리는 빈 배열이 반환된다.")
            void inorder_returns_empty_list_when_empty() {
                assertThat(tree.inorder()).isEmpty();
            }

            @Test
            @DisplayName("요소가 존재하는 트리의 경우 정렬된 순서로 반횐된다.")
            void inorder_returns_sorted_order() {
                tree.insert(5);
                tree.insert(3);
                tree.insert(7);
                tree.insert(8);

                assertThat(tree.inorder()).containsExactly(3, 5, 7, 8);
            }
        }

        @Nested
        @DisplayName("preorder 순회 테스트")
        class PreorderTest {
            @Test
            @DisplayName("빈 트리는 빈 배열이 반환된다.")
            void preorder_returns_empty_list_when_empty() {}

            @Test
            @DisplayName("요소가 존재하는 트리의 경우 루트-왼쪽-오른쪽 순서로 반환된다.")
            void preorder_returns_root_left_right_order() {
                tree.insert(5);
                tree.insert(3);
                tree.insert(7);
                tree.insert(8);

                assertThat(tree.preorder()).containsExactly(5, 3, 7, 8);
            }
        }

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