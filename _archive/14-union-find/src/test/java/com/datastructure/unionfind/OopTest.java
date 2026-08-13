package com.datastructure.unionfind;

import com.datastructure.unionfind.oop.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("유니온 파인드 - OOP 구현 테스트")
class OopTest {

    @Nested
    @DisplayName("ArrayUnionFind")
    class ArrayUnionFindTest {

        private DisjointSet<Integer> ds;

        @BeforeEach
        void setUp() {
            ds = new ArrayUnionFind(10);
        }

        @Test
        @DisplayName("01. find 인터페이스")
        void test01_findInterface() {
            // TODO: find() 테스트
            // assertThat(ds.find(0)).isEqualTo(0);
        }

        @Test
        @DisplayName("02. union 인터페이스")
        void test02_unionInterface() {
            // TODO: union() 테스트
            // ds.union(0, 1);
            // assertThat(ds.find(0)).isEqualTo(ds.find(1));
        }

        @Test
        @DisplayName("03. connected 인터페이스")
        void test03_connectedInterface() {
            // TODO: connected() 테스트
            // assertThat(ds.connected(0, 1)).isFalse();
            // ds.union(0, 1);
            // assertThat(ds.connected(0, 1)).isTrue();
        }

        @Test
        @DisplayName("04. 크기 조회")
        void test04_sizeQuery() {
            // TODO: getSize() 테스트
            // ds.union(0, 1);
            // assertThat(ds.getSize(0)).isEqualTo(2);
        }

        @Test
        @DisplayName("05. 집합 개수")
        void test05_setCount() {
            // TODO: getSetCount() 테스트
            // ds.union(0, 1);
            // ds.union(2, 3);
            // assertThat(ds.getSetCount()).isEqualTo(8);
        }
    }

    @Nested
    @DisplayName("MapUnionFind (제네릭)")
    class MapUnionFindTest {

        private DisjointSet<String> ds;

        @BeforeEach
        void setUp() {
            ds = new MapUnionFind<>();
        }

        @Test
        @DisplayName("06. makeSet")
        void test06_makeSet() {
            // TODO: makeSet() 테스트
            // ds.makeSet("Alice");
            // assertThat(ds.find("Alice")).isEqualTo("Alice");
        }

        @Test
        @DisplayName("07. String union")
        void test07_stringUnion() {
            // TODO: String 타입 union 테스트
            // ds.makeSet("Alice");
            // ds.makeSet("Bob");
            // ds.union("Alice", "Bob");
            // assertThat(ds.connected("Alice", "Bob")).isTrue();
        }

        @Test
        @DisplayName("08. 동적 원소 추가")
        void test08_dynamicAddition() {
            // TODO: 동적으로 원소 추가 테스트
            // ds.union("A", "B");  // 자동으로 makeSet
            // ds.union("B", "C");
            // assertThat(ds.connected("A", "C")).isTrue();
        }

        @Test
        @DisplayName("09. 여러 타입")
        void test09_differentTypes() {
            // TODO: Integer 타입 테스트
            // DisjointSet<Integer> intDs = new MapUnionFind<>();
            // intDs.union(1, 2);
            // assertThat(intDs.connected(1, 2)).isTrue();
        }

        @Test
        @DisplayName("10. Record 타입")
        void test10_recordType() {
            // TODO: Record 타입 테스트
            // record Person(String name, int age) {}
            // DisjointSet<Person> personDs = new MapUnionFind<>();
            // Person alice = new Person("Alice", 30);
            // Person bob = new Person("Bob", 25);
            // personDs.union(alice, bob);
            // assertThat(personDs.connected(alice, bob)).isTrue();
        }
    }

    @Nested
    @DisplayName("DisjointSet 인터페이스")
    class DisjointSetInterfaceTest {

        @Test
        @DisplayName("11. 다형성")
        void test11_polymorphism() {
            // TODO: 다형성 테스트
            // DisjointSet<Integer> ds1 = new ArrayUnionFind(5);
            // DisjointSet<Integer> ds2 = new MapUnionFind<>();
            // 둘 다 동일한 인터페이스로 사용
        }

        @Test
        @DisplayName("12. getComponents")
        void test12_getComponents() {
            // TODO: 모든 컴포넌트 반환 테스트
            // DisjointSet<Integer> ds = new ArrayUnionFind(5);
            // ds.union(0, 1);
            // ds.union(2, 3);
            // var components = ds.getComponents();
            // assertThat(components).hasSize(3);
        }
    }

    @Nested
    @DisplayName("Builder 패턴")
    class BuilderPatternTest {

        @Test
        @DisplayName("13. Builder 생성")
        void test13_builder() {
            // TODO: Builder 패턴 테스트 (선택)
            // DisjointSet<Integer> ds = ArrayUnionFind.builder()
            //     .size(10)
            //     .pathCompression(true)
            //     .unionByRank(true)
            //     .build();
        }
    }

    @Nested
    @DisplayName("전략 패턴")
    class StrategyPatternTest {

        @Test
        @DisplayName("14. Union 전략 - Rank")
        void test14_unionByRank() {
            // TODO: Union by Rank 전략 테스트
        }

        @Test
        @DisplayName("15. Union 전략 - Size")
        void test15_unionBySize() {
            // TODO: Union by Size 전략 테스트
        }

        @Test
        @DisplayName("16. Find 전략 - Full Compression")
        void test16_fullCompression() {
            // TODO: 전체 경로 압축 전략 테스트
        }

        @Test
        @DisplayName("17. Find 전략 - Half Compression")
        void test17_halfCompression() {
            // TODO: 절반 경로 압축 전략 테스트
        }
    }

    @Nested
    @DisplayName("Iterable 지원")
    class IterableTest {

        @Test
        @DisplayName("18. 루트 순회")
        void test18_iterateRoots() {
            // TODO: 루트 순회 테스트
            // for (Integer root : ds.roots()) { ... }
        }

        @Test
        @DisplayName("19. 원소 순회")
        void test19_iterateElements() {
            // TODO: 모든 원소 순회 테스트
            // for (Integer elem : ds) { ... }
        }
    }

    @Nested
    @DisplayName("기타")
    class Miscellaneous {

        @Test
        @DisplayName("20. equals")
        void test20_equals() {
            // TODO: equals() 테스트
            // DisjointSet<Integer> ds1 = new ArrayUnionFind(5);
            // DisjointSet<Integer> ds2 = new ArrayUnionFind(5);
            // ds1.union(0, 1);
            // ds2.union(0, 1);
            // 같은 집합 구조면 equal?
        }

        @Test
        @DisplayName("21. hashCode")
        void test21_hashCode() {
            // TODO: hashCode() 테스트
        }

        @Test
        @DisplayName("22. toString")
        void test22_toString() {
            // TODO: toString() 테스트
            // assertThat(ds.toString()).contains("DisjointSet");
        }

        @Test
        @DisplayName("23. 복사 생성자")
        void test23_copyConstructor() {
            // TODO: 복사 생성자 테스트
            // DisjointSet<Integer> original = new ArrayUnionFind(5);
            // original.union(0, 1);
            // DisjointSet<Integer> copy = new ArrayUnionFind(original);
            // copy.union(2, 3);
            // assertThat(original.connected(2, 3)).isFalse();
        }

        @Test
        @DisplayName("24. snapshot")
        void test24_snapshot() {
            // TODO: 현재 상태 스냅샷 테스트
            // Map<Integer, Set<Integer>> snapshot = ds.snapshot();
        }

        @Test
        @DisplayName("25. 불변 뷰")
        void test25_immutableView() {
            // TODO: 불변 뷰 테스트
            // DisjointSet<Integer> view = ds.asUnmodifiable();
            // assertThatThrownBy(() -> view.union(0, 1))
            //     .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("26. 스트림")
        void test26_stream() {
            // TODO: Stream API 테스트
            // ds.stream().filter(...)
        }

        @Test
        @DisplayName("27. 대용량 테스트")
        void test27_largeScale() {
            // TODO: 대용량 OOP 테스트
            // DisjointSet<Integer> ds = new ArrayUnionFind(100000);
            // for (int i = 0; i < 99999; i++) {
            //     ds.union(i, i + 1);
            // }
        }

        @Test
        @DisplayName("28. null 처리")
        void test28_nullHandling() {
            // TODO: null 처리 테스트
            // MapUnionFind<String> ds = new MapUnionFind<>();
            // assertThatThrownBy(() -> ds.makeSet(null))
            //     .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("29. 이벤트 리스너")
        void test29_eventListener() {
            // TODO: Union 이벤트 리스너 테스트 (선택)
            // ds.addUnionListener((x, y, newRoot) -> {...});
        }

        @Test
        @DisplayName("30. 문서화")
        void test30_documentation() {
            // TODO: Javadoc 확인 (수동)
        }
    }
}
