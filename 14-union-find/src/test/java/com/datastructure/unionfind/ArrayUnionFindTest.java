package com.datastructure.unionfind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("ArrayUnionFind: 배열 두 개")
class ArrayUnionFindTest extends UnionFindContractTest {

    @Override
    protected UnionFind create(int n) {
        return new ArrayUnionFind(n);
    }

    @Nested
    @DisplayName("인자 검사")
    class Args {

        @Test
        @DisplayName("거부한다")
        void rejects() {
            assertThrows(IllegalArgumentException.class, () -> new ArrayUnionFind(0));
            assertThrows(IllegalArgumentException.class, () -> new ArrayUnionFind(-1));
            ArrayUnionFind uf = new ArrayUnionFind(3);
            assertThrows(IndexOutOfBoundsException.class, () -> uf.find(3));
            assertThrows(IndexOutOfBoundsException.class, () -> uf.find(-1));
        }
    }

    @Nested
    @DisplayName("경로 압축의 효과")
    class PathCompression {

        @Test
        @DisplayName("find 한 번이면 그 경로가 통째로 뿌리에 붙는다")
        void findFlattensPath() {
            // 크기 붙이기를 끄고 한 줄짜리 나무를 강제로 만든다.
            ArrayUnionFind uf = new ArrayUnionFind(10, false, true);
            for (int i = 1; i < 10; i++) {
                uf.union(0, i);       // 늘 i 를 0 밑에... 이 아니라 뒤에 오는 것이 밑으로 간다
            }
            // 크기를 안 보면 늘 두 번째 인자의 뿌리가 첫 번째 밑으로 간다.
            // 여기서는 0 이 계속 뿌리라 깊이가 1 이다. 그래서 순서를 바꿔 한 줄을 만든다.
            ArrayUnionFind chain = new ArrayUnionFind(10, false, true);
            for (int i = 1; i < 10; i++) {
                chain.union(i, i - 1);
            }
            assertTrue(chain.depthOf(0) >= 5,
                    "한 줄짜리 나무가 만들어져야 이 테스트가 뜻이 있다: 깊이 " + chain.depthOf(0));

            chain.find(0);
            assertEquals(1, chain.depthOf(0), "find 뒤에는 뿌리 바로 밑이어야 한다");
            for (int i = 0; i < 10; i++) {
                assertTrue(chain.depthOf(i) <= 1, i + " 의 깊이가 " + chain.depthOf(i) + " 다");
            }
        }

        @Test
        @DisplayName("압축을 끄면 나무가 깊은 채로 남는다")
        void withoutCompressionStaysDeep() {
            ArrayUnionFind uf = new ArrayUnionFind(1000, false, false);
            for (int i = 1; i < 1000; i++) {
                uf.union(i, i - 1);
            }
            assertEquals(999, uf.depthOf(0), "999 걸음짜리 한 줄이다");
            uf.find(0);
            assertEquals(999, uf.depthOf(0), "압축이 꺼져 있으면 find 를 해도 그대로다");
        }

        @Test
        @DisplayName("압축은 대표를 바꾸지 않는다")
        void compressionPreservesRoot() {
            ArrayUnionFind uf = new ArrayUnionFind(100, false, true);
            for (int i = 1; i < 100; i++) {
                uf.union(i, i - 1);
            }
            int root = uf.find(50);
            for (int i = 0; i < 100; i++) {
                assertEquals(root, uf.find(i), "구조만 바뀌고 답은 그대로여야 한다");
            }
            assertEquals(100, uf.sizeOf(0));
            assertEquals(1, uf.componentCount());
        }
    }

    @Nested
    @DisplayName("크기로 붙이기의 효과")
    class UnionBySize {

        @Test
        @DisplayName("크기를 보면 깊이가 log n 을 안 넘는다")
        void depthStaysLogarithmic() {
            // 압축을 꺼서 붙이기 전략만 비교한다.
            int n = 1024;
            ArrayUnionFind smart = new ArrayUnionFind(n, true, false);
            ArrayUnionFind dumb = new ArrayUnionFind(n, false, false);
            for (int i = 1; i < n; i++) {
                smart.union(i, i - 1);
                dumb.union(i, i - 1);
            }

            int smartMax = 0;
            int dumbMax = 0;
            for (int i = 0; i < n; i++) {
                smartMax = Math.max(smartMax, smart.depthOf(i));
                dumbMax = Math.max(dumbMax, dumb.depthOf(i));
            }
            assertEquals(n - 1, dumbMax, "크기를 안 보면 한 줄이 된다");
            assertTrue(smartMax <= 10, "log2(1024) = 10 을 안 넘어야 한다. 실제 " + smartMax);
        }

        @Test
        @Timeout(30)
        @DisplayName("한계: 최적화를 둘 다 끄면 실제로 느려진다")
        void bothOffIsSlow() {
            // 이건 "고칠 수 없는 성질"이 아니라 **왜 두 최적화를 넣는지**를 보여주는 측정이다.
            int n = 20_000;
            ArrayUnionFind dumb = new ArrayUnionFind(n, false, false);
            for (int i = 1; i < n; i++) {
                dumb.union(i, i - 1);
            }
            assertEquals(n - 1, dumb.depthOf(0));

            ArrayUnionFind smart = new ArrayUnionFind(n);
            for (int i = 1; i < n; i++) {
                smart.union(i, i - 1);
            }
            smart.find(0);
            assertTrue(smart.depthOf(0) <= 1,
                    "둘 다 켜면 사실상 평평하다: 깊이 " + smart.depthOf(0));
        }
    }
}
