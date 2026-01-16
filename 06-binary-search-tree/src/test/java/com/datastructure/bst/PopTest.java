package com.datastructure.bst;

import com.datastructure.bst.pop.BinarySearchTree;
import com.datastructure.bst.pop.BSTProblems;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("이진 탐색 트리 - POP 구현 테스트")
class PopTest {

    private BinarySearchTree bst;

    @BeforeEach
    void setUp() {
        bst = new BinarySearchTree();
    }

    @Nested
    @DisplayName("기본 연산")
    class BasicOperations {

        @Test
        @DisplayName("01. 빈 트리 생성 시 size는 0이다")
        void test01_emptyTreeSizeIsZero() {
            // TODO: size() 구현 후 테스트
            // assertThat(bst.size()).isEqualTo(0);
        }

        @Test
        @DisplayName("02. 빈 트리는 isEmpty가 true다")
        void test02_emptyTreeIsEmpty() {
            // TODO: isEmpty() 구현 후 테스트
            // assertThat(bst.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("03. insert로 값을 추가할 수 있다")
        void test03_insert() {
            // TODO: insert() 구현 후 테스트
            // bst.insert(5);
            // assertThat(bst.contains(5)).isTrue();
        }

        @Test
        @DisplayName("04. 중복 값은 무시된다")
        void test04_duplicateIgnored() {
            // TODO: 중복 처리 테스트
            // bst.insert(5);
            // bst.insert(5);
            // assertThat(bst.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("05. search로 값을 검색할 수 있다")
        void test05_search() {
            // TODO: search() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // assertThat(bst.search(3)).isTrue();
            // assertThat(bst.search(6)).isFalse();
        }

        @Test
        @DisplayName("06. min으로 최솟값을 얻을 수 있다")
        void test06_min() {
            // TODO: min() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // bst.insert(1);
            // assertThat(bst.min()).isEqualTo(1);
        }

        @Test
        @DisplayName("07. max로 최댓값을 얻을 수 있다")
        void test07_max() {
            // TODO: max() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // bst.insert(9);
            // assertThat(bst.max()).isEqualTo(9);
        }

        @Test
        @DisplayName("08. 빈 트리에서 min 시 예외가 발생한다")
        void test08_minOnEmptyTree() {
            // TODO: 예외 처리 테스트
            // assertThatThrownBy(() -> bst.min())
            //     .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("삭제")
    class Delete {

        @Test
        @DisplayName("09. 리프 노드 삭제")
        void test09_deleteLeaf() {
            // TODO: 리프 노드 삭제 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // bst.delete(3);
            // assertThat(bst.contains(3)).isFalse();
            // assertThat(bst.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("10. 자식 하나인 노드 삭제")
        void test10_deleteOneChild() {
            // TODO: 자식 하나인 노드 삭제 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // bst.insert(6);
            // bst.delete(7);  // 6이 승계
            // assertThat(bst.contains(6)).isTrue();
        }

        @Test
        @DisplayName("11. 자식 둘인 노드 삭제 (후계자로 대체)")
        void test11_deleteTwoChildren() {
            // TODO: 자식 둘인 노드 삭제 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // bst.insert(6);
            // bst.insert(8);
            // bst.delete(7);  // 후계자 8로 대체
            // assertThat(bst.contains(5)).isTrue();
            // assertThat(bst.contains(8)).isTrue();
        }

        @Test
        @DisplayName("12. 루트 노드 삭제")
        void test12_deleteRoot() {
            // TODO: 루트 삭제 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // bst.delete(5);
            // assertThat(bst.contains(5)).isFalse();
            // assertThat(bst.size()).isEqualTo(2);
        }

        @Test
        @DisplayName("13. 존재하지 않는 값 삭제는 무시된다")
        void test13_deleteNonExistent() {
            // TODO: 존재하지 않는 값 삭제 테스트
            // bst.insert(5);
            // bst.delete(10);  // 없는 값
            // assertThat(bst.size()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("순회")
    class Traversal {

        @Test
        @DisplayName("14. 중위 순회는 정렬된 순서를 반환한다")
        void test14_inorder() {
            // TODO: inorder() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // bst.insert(1);
            // bst.insert(9);
            // assertThat(bst.inorder()).containsExactly(1, 3, 5, 7, 9);
        }

        @Test
        @DisplayName("15. 전위 순회")
        void test15_preorder() {
            // TODO: preorder() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // assertThat(bst.preorder()).containsExactly(5, 3, 7);
        }

        @Test
        @DisplayName("16. 후위 순회")
        void test16_postorder() {
            // TODO: postorder() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // assertThat(bst.postorder()).containsExactly(3, 7, 5);
        }

        @Test
        @DisplayName("17. 레벨 순회 (BFS)")
        void test17_levelorder() {
            // TODO: levelorder() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // bst.insert(1);
            // bst.insert(9);
            // assertThat(bst.levelorder()).containsExactly(5, 3, 7, 1, 9);
        }
    }

    @Nested
    @DisplayName("추가 연산")
    class AdditionalOperations {

        @Test
        @DisplayName("18. height로 트리 높이를 얻을 수 있다")
        void test18_height() {
            // TODO: height() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // bst.insert(1);
            // assertThat(bst.height()).isEqualTo(2);
        }

        @Test
        @DisplayName("19. 빈 트리의 높이는 -1이다")
        void test19_emptyTreeHeight() {
            // TODO: 빈 트리 높이 테스트
            // assertThat(bst.height()).isEqualTo(-1);
        }

        @Test
        @DisplayName("20. floor로 value 이하의 최댓값을 얻을 수 있다")
        void test20_floor() {
            // TODO: floor() 구현 후 테스트
            // bst.insert(10);
            // bst.insert(20);
            // bst.insert(30);
            // assertThat(bst.floor(25)).isEqualTo(20);
            // assertThat(bst.floor(10)).isEqualTo(10);
            // assertThat(bst.floor(5)).isNull();
        }

        @Test
        @DisplayName("21. ceiling으로 value 이상의 최솟값을 얻을 수 있다")
        void test21_ceiling() {
            // TODO: ceiling() 구현 후 테스트
            // bst.insert(10);
            // bst.insert(20);
            // bst.insert(30);
            // assertThat(bst.ceiling(15)).isEqualTo(20);
            // assertThat(bst.ceiling(30)).isEqualTo(30);
            // assertThat(bst.ceiling(35)).isNull();
        }

        @Test
        @DisplayName("22. predecessor로 바로 이전 값을 얻을 수 있다")
        void test22_predecessor() {
            // TODO: predecessor() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // bst.insert(1);
            // assertThat(bst.predecessor(5)).isEqualTo(3);
            // assertThat(bst.predecessor(1)).isNull();
        }

        @Test
        @DisplayName("23. successor로 바로 다음 값을 얻을 수 있다")
        void test23_successor() {
            // TODO: successor() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // bst.insert(9);
            // assertThat(bst.successor(5)).isEqualTo(7);
            // assertThat(bst.successor(9)).isNull();
        }
    }

    @Nested
    @DisplayName("기타")
    class Miscellaneous {

        @Test
        @DisplayName("24. clear로 모든 노드를 삭제할 수 있다")
        void test24_clear() {
            // TODO: clear() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // bst.clear();
            // assertThat(bst.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("25. 대량 데이터 삽입/검색")
        void test25_largeData() {
            // TODO: 대량 데이터 테스트
            // for (int i = 0; i < 1000; i++) {
            //     bst.insert(i);
            // }
            // assertThat(bst.size()).isEqualTo(1000);
            // assertThat(bst.contains(500)).isTrue();
        }

        @Test
        @DisplayName("26. 정렬된 배열로 균형 트리 생성")
        void test26_sortedArrayToBST() {
            // TODO: BSTProblems.sortedArrayToBST() 구현 후 테스트
            // int[] sorted = {1, 2, 3, 4, 5, 6, 7};
            // BinarySearchTree bst = BSTProblems.sortedArrayToBST(sorted);
            // assertThat(bst.height()).isEqualTo(2);  // 균형 트리
        }

        @Test
        @DisplayName("27. BST 유효성 검사")
        void test27_isValidBST() {
            // TODO: BSTProblems.isValidBST() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // assertThat(BSTProblems.isValidBST(bst)).isTrue();
        }

        @Test
        @DisplayName("28. LCA(최소 공통 조상) 찾기")
        void test28_lowestCommonAncestor() {
            // TODO: BSTProblems.lowestCommonAncestor() 구현 후 테스트
            // bst.insert(6);
            // bst.insert(2);
            // bst.insert(8);
            // bst.insert(0);
            // bst.insert(4);
            // assertThat(BSTProblems.lowestCommonAncestor(bst, 2, 8)).isEqualTo(6);
            // assertThat(BSTProblems.lowestCommonAncestor(bst, 0, 4)).isEqualTo(2);
        }

        @Test
        @DisplayName("29. K번째 작은 값 찾기")
        void test29_kthSmallest() {
            // TODO: kthSmallest() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // bst.insert(1);
            // bst.insert(4);
            // assertThat(bst.kthSmallest(1)).isEqualTo(1);
            // assertThat(bst.kthSmallest(3)).isEqualTo(4);
        }

        @Test
        @DisplayName("30. toString으로 트리 구조 확인")
        void test30_toString() {
            // TODO: toString() 구현 후 테스트
            // bst.insert(5);
            // bst.insert(3);
            // bst.insert(7);
            // assertThat(bst.toString()).contains("5", "3", "7");
        }
    }
}
