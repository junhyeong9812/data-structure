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

        }

        @Nested
        @DisplayName("search 메서드 테스트")
        class SearchTest {}

        @Nested
        @DisplayName("delete 메서드 테스트")
        class DeleteTest {}

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