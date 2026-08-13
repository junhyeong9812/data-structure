package com.datastructure.unionfind;

import com.datastructure.unionfind.pop.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("유니온 파인드 - POP 구현 테스트")
class PopTest {

    @Nested
    @DisplayName("기본 UnionFind")
    class BasicUnionFindTest {

        private UnionFind uf;

        @BeforeEach
        void setUp() {
            uf = new UnionFind(10);
        }

        @Test
        @DisplayName("01. 초기 상태 - 각자 자신이 대표")
        void test01_initialState() {
            // TODO: find() 구현 후 테스트
            // for (int i = 0; i < 10; i++) {
            //     assertThat(uf.find(i)).isEqualTo(i);
            // }
        }

        @Test
        @DisplayName("02. 초기 집합 개수")
        void test02_initialSetCount() {
            // TODO: getSetCount() 구현 후 테스트
            // assertThat(uf.getSetCount()).isEqualTo(10);
        }

        @Test
        @DisplayName("03. union 후 같은 루트")
        void test03_unionSameRoot() {
            // TODO: union(), find() 테스트
            // uf.union(0, 1);
            // assertThat(uf.find(0)).isEqualTo(uf.find(1));
        }

        @Test
        @DisplayName("04. union 후 connected")
        void test04_connected() {
            // TODO: connected() 구현 후 테스트
            // assertThat(uf.connected(0, 1)).isFalse();
            // uf.union(0, 1);
            // assertThat(uf.connected(0, 1)).isTrue();
        }

        @Test
        @DisplayName("05. 여러 번 union")
        void test05_multipleUnions() {
            // TODO: 여러 번 union 테스트
            // uf.union(0, 1);
            // uf.union(2, 3);
            // uf.union(0, 2);
            // assertThat(uf.connected(1, 3)).isTrue();
        }
    }

    @Nested
    @DisplayName("집합 크기")
    class SetSizeTest {

        private UnionFind uf;

        @BeforeEach
        void setUp() {
            uf = new UnionFind(6);
        }

        @Test
        @DisplayName("06. 초기 크기는 1")
        void test06_initialSize() {
            // TODO: getSize() 구현 후 테스트
            // for (int i = 0; i < 6; i++) {
            //     assertThat(uf.getSize(i)).isEqualTo(1);
            // }
        }

        @Test
        @DisplayName("07. union 후 크기 증가")
        void test07_sizeAfterUnion() {
            // TODO: 크기 증가 테스트
            // uf.union(0, 1);
            // assertThat(uf.getSize(0)).isEqualTo(2);
            // assertThat(uf.getSize(1)).isEqualTo(2);
        }

        @Test
        @DisplayName("08. 여러 union 후 크기")
        void test08_sizeAfterMultipleUnions() {
            // TODO: 여러 union 후 크기 테스트
            // uf.union(0, 1);
            // uf.union(1, 2);
            // assertThat(uf.getSize(0)).isEqualTo(3);
            // uf.union(3, 4);
            // uf.union(0, 3);
            // assertThat(uf.getSize(0)).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("집합 개수")
    class SetCountTest {

        private UnionFind uf;

        @BeforeEach
        void setUp() {
            uf = new UnionFind(5);
        }

        @Test
        @DisplayName("09. union 후 집합 개수 감소")
        void test09_countDecrease() {
            // TODO: 집합 개수 테스트
            // assertThat(uf.getSetCount()).isEqualTo(5);
            // uf.union(0, 1);
            // assertThat(uf.getSetCount()).isEqualTo(4);
        }

        @Test
        @DisplayName("10. 이미 같은 집합 union")
        void test10_unionSameSet() {
            // TODO: 같은 집합 union 테스트 (개수 변화 없음)
            // uf.union(0, 1);
            // assertThat(uf.getSetCount()).isEqualTo(4);
            // uf.union(0, 1);  // 다시 union
            // assertThat(uf.getSetCount()).isEqualTo(4);  // 변화 없음
        }

        @Test
        @DisplayName("11. 모두 union 시 집합 개수 1")
        void test11_allUnion() {
            // TODO: 모두 union 테스트
            // for (int i = 0; i < 4; i++) {
            //     uf.union(i, i + 1);
            // }
            // assertThat(uf.getSetCount()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("경로 압축")
    class PathCompressionTest {

        @Test
        @DisplayName("12. 긴 체인 경로 압축")
        void test12_longChainCompression() {
            // TODO: 경로 압축 테스트
            // UnionFind uf = new UnionFind(1000);
            // for (int i = 0; i < 999; i++) {
            //     uf.union(i, i + 1);
            // }
            // // 경로 압축 후 빠른 find
            // long start = System.nanoTime();
            // for (int i = 0; i < 1000; i++) {
            //     uf.find(i);
            // }
            // long end = System.nanoTime();
            // // 충분히 빠름 확인 (ms 이내)
        }
    }

    @Nested
    @DisplayName("WeightedUnionFind")
    class WeightedUnionFindTest {

        private WeightedUnionFind wuf;

        @BeforeEach
        void setUp() {
            wuf = new WeightedUnionFind(5);
        }

        @Test
        @DisplayName("13. 가중치 union")
        void test13_weightedUnion() {
            // TODO: union(x, y, weight) 테스트
            // wuf.union(0, 1, 2.0);  // x/y = 2.0
            // assertThat(wuf.query(0, 1)).isEqualTo(2.0);
        }

        @Test
        @DisplayName("14. 가중치 쿼리")
        void test14_weightQuery() {
            // TODO: 연결된 가중치 쿼리 테스트
            // wuf.union(0, 1, 2.0);  // a/b = 2
            // wuf.union(1, 2, 3.0);  // b/c = 3
            // assertThat(wuf.query(0, 2)).isEqualTo(6.0);  // a/c = 6
        }

        @Test
        @DisplayName("15. 연결 안 된 쿼리")
        void test15_disconnectedQuery() {
            // TODO: 연결 안 된 경우 -1 반환
            // assertThat(wuf.query(0, 1)).isEqualTo(-1.0);
        }
    }

    @Nested
    @DisplayName("엣지 케이스")
    class EdgeCases {

        @Test
        @DisplayName("16. 크기 1")
        void test16_sizeOne() {
            // TODO: 크기 1 테스트
            // UnionFind uf = new UnionFind(1);
            // assertThat(uf.find(0)).isEqualTo(0);
            // assertThat(uf.getSetCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("17. 자기 자신과 union")
        void test17_selfUnion() {
            // TODO: 자기 자신 union 테스트
            // UnionFind uf = new UnionFind(5);
            // uf.union(0, 0);
            // assertThat(uf.getSetCount()).isEqualTo(5);  // 변화 없음
        }

        @Test
        @DisplayName("18. 인덱스 범위 확인")
        void test18_indexBounds() {
            // TODO: 범위 초과 테스트
            // UnionFind uf = new UnionFind(5);
            // assertThatThrownBy(() -> uf.find(-1))
            //     .isInstanceOf(IndexOutOfBoundsException.class);
            // assertThatThrownBy(() -> uf.find(5))
            //     .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    @Nested
    @DisplayName("응용: 그래프")
    class GraphApplicationTest {

        @Test
        @DisplayName("19. 연결 컴포넌트 개수")
        void test19_connectedComponents() {
            // TODO: 연결 컴포넌트 테스트
            // int[][] edges = {{0, 1}, {1, 2}, {3, 4}};
            // UnionFind uf = new UnionFind(5);
            // for (int[] e : edges) {
            //     uf.union(e[0], e[1]);
            // }
            // assertThat(uf.getSetCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("20. 사이클 탐지")
        void test20_cycleDetection() {
            // TODO: 사이클 탐지 테스트
            // int[][] edges = {{0, 1}, {1, 2}, {2, 0}};
            // UnionFind uf = new UnionFind(3);
            // boolean hasCycle = false;
            // for (int[] e : edges) {
            //     if (!uf.union(e[0], e[1])) {
            //         hasCycle = true;
            //         break;
            //     }
            // }
            // assertThat(hasCycle).isTrue();
        }
    }

    @Nested
    @DisplayName("대용량 테스트")
    class LargeScaleTest {

        @Test
        @DisplayName("21. 10만 개 원소")
        void test21_hundredThousand() {
            // TODO: 대용량 테스트
            // UnionFind uf = new UnionFind(100000);
            // for (int i = 0; i < 99999; i++) {
            //     uf.union(i, i + 1);
            // }
            // assertThat(uf.getSetCount()).isEqualTo(1);
        }

        @Test
        @DisplayName("22. 랜덤 union")
        void test22_randomUnion() {
            // TODO: 랜덤 union 테스트
            // UnionFind uf = new UnionFind(10000);
            // Random rand = new Random(42);
            // for (int i = 0; i < 50000; i++) {
            //     int x = rand.nextInt(10000);
            //     int y = rand.nextInt(10000);
            //     uf.union(x, y);
            // }
            // // 시간 내 완료 확인
        }
    }

    @Nested
    @DisplayName("기타")
    class Miscellaneous {

        @Test
        @DisplayName("23. union 반환값")
        void test23_unionReturnValue() {
            // TODO: union 반환값 테스트
            // UnionFind uf = new UnionFind(5);
            // assertThat(uf.union(0, 1)).isTrue();
            // assertThat(uf.union(0, 1)).isFalse();  // 이미 같은 집합
        }

        @Test
        @DisplayName("24. getComponents")
        void test24_getComponents() {
            // TODO: 모든 집합 반환 테스트
            // UnionFind uf = new UnionFind(5);
            // uf.union(0, 1);
            // uf.union(2, 3);
            // List<Set<Integer>> components = uf.getComponents();
            // assertThat(components).hasSize(3);
        }

        @Test
        @DisplayName("25. getRoots")
        void test25_getRoots() {
            // TODO: 모든 루트 반환 테스트
            // UnionFind uf = new UnionFind(5);
            // uf.union(0, 1);
            // Set<Integer> roots = uf.getRoots();
            // assertThat(roots).hasSize(4);
        }

        @Test
        @DisplayName("26. toString")
        void test26_toString() {
            // TODO: toString() 테스트
            // UnionFind uf = new UnionFind(3);
            // assertThat(uf.toString()).isNotEmpty();
        }

        @Test
        @DisplayName("27. 복사본 독립성")
        void test27_copyIndependence() {
            // TODO: 깊은 복사 테스트 (선택)
        }

        @Test
        @DisplayName("28. equals/hashCode")
        void test28_equalsHashCode() {
            // TODO: equals/hashCode 테스트 (선택)
        }

        @Test
        @DisplayName("29. 음수 가중치")
        void test29_negativeWeight() {
            // TODO: WeightedUnionFind 음수 가중치 테스트
            // WeightedUnionFind wuf = new WeightedUnionFind(5);
            // wuf.union(0, 1, -2.0);
            // assertThat(wuf.query(0, 1)).isEqualTo(-2.0);
        }

        @Test
        @DisplayName("30. 실수 가중치 정밀도")
        void test30_floatingPointPrecision() {
            // TODO: 실수 정밀도 테스트
            // WeightedUnionFind wuf = new WeightedUnionFind(3);
            // wuf.union(0, 1, 2.0);
            // wuf.union(1, 2, 3.0);
            // assertThat(wuf.query(0, 2)).isCloseTo(6.0, within(1e-9));
        }
    }
}
