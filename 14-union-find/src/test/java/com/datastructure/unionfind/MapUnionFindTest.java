package com.datastructure.unionfind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("MapUnionFind: 원소 수를 몰라도 되는")
class MapUnionFindTest extends UnionFindContractTest {

    @Override
    protected UnionFind create(int n) {
        MapUnionFind uf = new MapUnionFind();
        for (int i = 0; i < n; i++) {
            uf.add(i);
        }
        return uf;
    }

    @Nested
    @DisplayName("흩어진 아이디")
    class SparseIds {

        @Test
        @DisplayName("아무 정수나 된다")
        void anyInteger() {
            MapUnionFind uf = new MapUnionFind();
            uf.union(1_000_000, -5);
            uf.union(-5, Integer.MAX_VALUE);
            assertTrue(uf.connected(1_000_000, Integer.MAX_VALUE));
            assertEquals(3, uf.size(), "만든 것만 센다. 100만 칸을 잡지 않는다");
            assertEquals(1, uf.componentCount());
            assertEquals(3, uf.sizeOf(-5));
        }

        @Test
        @DisplayName("처음 보는 원소는 자동으로 생긴다")
        void autoCreates() {
            MapUnionFind uf = new MapUnionFind();
            assertEquals(0, uf.size());
            assertFalse(uf.contains(7));
            assertEquals(7, uf.find(7), "혼자짜리 묶음의 대표는 자기 자신이다");
            assertTrue(uf.contains(7));
            assertEquals(1, uf.size());
            assertEquals(1, uf.componentCount());
        }

        @Test
        @DisplayName("add 는 이미 있으면 false")
        void addIsIdempotent() {
            MapUnionFind uf = new MapUnionFind();
            assertTrue(uf.add(3));
            assertFalse(uf.add(3));
            assertEquals(1, uf.size());
            assertEquals(1, uf.componentCount());
        }
    }

    @Nested
    @DisplayName("Integer 캐시 함정")
    class IntegerCacheTrap {

        @Test
        @DisplayName("128 이상에서도 맞아야 한다")
        void beyondCacheRange() {
            // 자바는 -128..127 의 Integer 를 캐시한다.
            // 맵에서 꺼낸 Integer 를 `!=` 로 비교하면 그 범위 밖에서 조용히 틀린다.
            // **작은 테스트만 돌리면 절대 안 걸리는 종류의 버그다.**
            MapUnionFind uf = new MapUnionFind();
            for (int i = 1000; i < 1100; i++) {
                uf.add(i);
            }
            assertEquals(1000, uf.find(1000));
            assertEquals(1050, uf.find(1050));
            assertEquals(100, uf.componentCount());

            for (int i = 1001; i < 1100; i++) {
                assertTrue(uf.union(1000, i), i + " 를 합치지 못했다");
            }
            assertEquals(1, uf.componentCount());
            assertEquals(100, uf.sizeOf(1099));
            for (int i = 1000; i < 1100; i++) {
                assertTrue(uf.connected(1000, i));
            }
        }
    }
}
