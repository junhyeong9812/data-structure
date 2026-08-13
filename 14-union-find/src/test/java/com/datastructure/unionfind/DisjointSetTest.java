package com.datastructure.unionfind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("DisjointSet: 아무 타입이나")
class DisjointSetTest {

    @Nested
    @DisplayName("기본")
    class Basics {

        @Test
        @DisplayName("문자열을 묶는다")
        void strings() {
            DisjointSet<String> ds = new DisjointSet<>();
            assertTrue(ds.union("서울", "인천"));
            assertTrue(ds.union("부산", "울산"));
            assertTrue(ds.connected("서울", "인천"));
            assertFalse(ds.connected("서울", "부산"));
            assertEquals(2, ds.componentCount());
            assertEquals(4, ds.size());
            assertEquals(2, ds.sizeOf("서울"));
        }

        @Test
        @DisplayName("find 는 번호가 아니라 원소를 준다")
        void findReturnsItem() {
            DisjointSet<String> ds = new DisjointSet<>();
            ds.union("a", "b");
            String root = ds.find("a");
            assertEquals(root, ds.find("b"));
            assertTrue(Set.of("a", "b").contains(root), "대표도 원래 원소 중 하나다: " + root);
        }

        @Test
        @DisplayName("처음 보는 원소는 자동으로 생긴다")
        void autoAdd() {
            DisjointSet<String> ds = new DisjointSet<>();
            assertEquals("x", ds.find("x"));
            assertEquals(1, ds.size());
            assertTrue(ds.contains("x"));
            assertEquals(1, ds.sizeOf("x"));
        }

        @Test
        @DisplayName("add 는 이미 있으면 false")
        void addTwice() {
            DisjointSet<Integer> ds = new DisjointSet<>();
            assertTrue(ds.add(1));
            assertFalse(ds.add(1));
            assertEquals(1, ds.size());
        }

        @Test
        @DisplayName("connected 는 없는 원소를 만들지 않는다")
        void connectedDoesNotCreate() {
            DisjointSet<String> ds = new DisjointSet<>();
            ds.add("a");
            assertFalse(ds.connected("a", "없는것"));
            assertEquals(1, ds.size(), "묻기만 했는데 원소가 늘면 안 된다");
        }

        @Test
        @DisplayName("null 은 거부한다")
        void rejectsNull() {
            DisjointSet<String> ds = new DisjointSet<>();
            assertThrows(IllegalArgumentException.class, () -> ds.add(null));
            assertThrows(IllegalArgumentException.class, () -> ds.union(null, "a"));
            assertThrows(IllegalArgumentException.class, () -> ds.find(null));
        }
    }

    @Nested
    @DisplayName("묶음 목록")
    class Groups {

        @Test
        @DisplayName("묶음마다 원소를 모아준다")
        void groupsThem() {
            DisjointSet<String> ds = new DisjointSet<>();
            for (String s : List.of("a", "b", "c", "d", "e")) {
                ds.add(s);
            }
            ds.union("a", "b");
            ds.union("b", "c");
            ds.union("d", "e");

            Map<String, List<String>> g = ds.groups();
            assertEquals(2, g.size());
            for (List<String> members : g.values()) {
                assertTrue(members.size() == 3 || members.size() == 2, "크기 " + members.size());
            }
            assertEquals(5, g.values().stream().mapToInt(List::size).sum());
            assertTrue(g.get(ds.find("a")).containsAll(List.of("a", "b", "c")));
        }

        @Test
        @DisplayName("합치기 전에는 전부 혼자다")
        void allSingletons() {
            DisjointSet<Integer> ds = new DisjointSet<>();
            for (int i = 0; i < 5; i++) {
                ds.add(i);
            }
            assertEquals(5, ds.groups().size());
            for (List<Integer> members : ds.groups().values()) {
                assertEquals(1, members.size());
            }
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class CrossCheck {

        @Test
        @DisplayName("정수판과 같은 답을 낸다")
        void agreesWithIntVersion() {
            Random rnd = new Random(555L);
            int n = 300;
            DisjointSet<String> ds = new DisjointSet<>();
            UnionFind uf = new ArrayUnionFind(n);
            for (int i = 0; i < n; i++) {
                ds.add("item" + i);
            }
            for (int step = 0; step < 2000; step++) {
                int a = rnd.nextInt(n);
                int b = rnd.nextInt(n);
                if (rnd.nextBoolean()) {
                    assertEquals(uf.union(a, b), ds.union("item" + a, "item" + b), "step " + step);
                } else {
                    assertEquals(uf.connected(a, b), ds.connected("item" + a, "item" + b),
                            "step " + step);
                }
                assertEquals(uf.componentCount(), ds.componentCount(), "step " + step);
                assertEquals(uf.sizeOf(a), ds.sizeOf("item" + a), "step " + step);
            }
        }
    }
}
