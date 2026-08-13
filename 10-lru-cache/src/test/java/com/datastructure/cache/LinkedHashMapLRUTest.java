package com.datastructure.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("LinkedHashMapLRU: 표준 라이브러리가 해준다")
class LinkedHashMapLRUTest extends CacheContractTest {

    @Override
    protected Cache<Integer, String> create(int capacity) {
        return new LinkedHashMapLRU<>(capacity);
    }

    @Nested
    @DisplayName("두 구현이 완전히 같게 움직인다")
    class SameBehaviour {

        @Test
        @DisplayName("같은 연산을 주면 같은 순서가 나온다")
        void identicalToHandWritten() {
            Cache<Integer, String> hand = new LRUCache<>(3);
            Cache<Integer, String> std = new LinkedHashMapLRU<>(3);

            int[][] ops = {{1, 1}, {1, 2}, {1, 3}, {0, 1}, {1, 4}, {0, 2}, {1, 3}, {1, 5}, {0, 4}};
            for (int[] op : ops) {
                if (op[0] == 1) {
                    hand.put(op[1], "v" + op[1]);
                    std.put(op[1], "v" + op[1]);
                } else {
                    assertEquals(hand.get(op[1]), std.get(op[1]), "get(" + op[1] + ")");
                }
                assertEquals(hand.keysInOrder(), std.keysInOrder(), "순서가 갈렸다");
                assertEquals(hand.size(), std.size());
            }
            assertEquals(hand.hits(), std.hits());
            assertEquals(hand.misses(), std.misses());
            assertEquals(hand.evictions(), std.evictions());
        }
    }

    @Nested
    @DisplayName("설정 두 개가 하는 일")
    class TwoSwitches {

        @Test
        @DisplayName("accessOrder 가 꺼져 있으면 get 이 순서를 안 바꾼다")
        void accessOrderMatters() {
            // 이 테스트는 accessOrder=true 를 실제로 켰는지 본다.
            // 기본값(false)으로 만들면 삽입 순서가 유지되어 여기서 걸린다.
            Cache<Integer, String> c = create(3);
            c.put(1, "a");
            c.put(2, "b");
            c.put(3, "c");
            c.get(1);
            assertEquals(List.of(2, 3, 1), c.keysInOrder(),
                    "삽입 순서 그대로면 accessOrder 를 안 켠 것이다");
        }

        @Test
        @DisplayName("removeEldestEntry 의 부호 하나")
        void thresholdOffByOne() {
            // size() >= capacity 로 쓰면 아직 자리가 있는데도 버린다.
            Cache<Integer, String> c = create(3);
            c.put(1, "a");
            c.put(2, "b");
            c.put(3, "c");
            assertEquals(3, c.size(), "용량만큼은 담겨야 한다");
            assertEquals(0, c.evictions());
            assertEquals(List.of(1, 2, 3), c.keysInOrder());
            c.put(4, "d");
            assertEquals(3, c.size());
            assertEquals(1, c.evictions());
            assertNull(c.get(1));
        }
    }
}
