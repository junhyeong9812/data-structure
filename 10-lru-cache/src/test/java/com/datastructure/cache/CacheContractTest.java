package com.datastructure.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/** Cache 계약. 세 구현이 똑같이 지켜야 하는 것만 여기 있다. */
abstract class CacheContractTest {

    protected abstract Cache<Integer, String> create(int capacity);

    @Nested
    @DisplayName("생성")
    class Construction {

        @Test
        @DisplayName("용량은 1 이상이어야 한다")
        void capacityMustBePositive() {
            assertThrows(IllegalArgumentException.class, () -> create(0),
                    "용량 0 짜리 캐시는 캐시가 아니다");
            assertThrows(IllegalArgumentException.class, () -> create(-1));
        }

        @Test
        @DisplayName("용량을 그대로 기억한다")
        void remembersCapacity() {
            assertEquals(3, create(3).capacity());
            assertEquals(1, create(1).capacity());
        }

        @Test
        @DisplayName("처음엔 비어 있다")
        void startsEmpty() {
            Cache<Integer, String> c = create(3);
            assertEquals(0, c.size());
            assertTrue(c.isEmpty());
            assertNull(c.get(1));
            assertFalse(c.containsKey(1));
            assertEquals(List.of(), c.keysInOrder());
            assertEquals(0, c.hits());
            assertEquals(1, c.misses(), "위에서 get 을 한 번 했다");
            assertEquals(0, c.evictions());
        }
    }

    @Nested
    @DisplayName("기본 동작")
    class Basics {

        @Test
        @DisplayName("넣으면 꺼낼 수 있다")
        void putThenGet() {
            Cache<Integer, String> c = create(3);
            c.put(1, "a");
            assertEquals("a", c.get(1));
            assertEquals(1, c.size());
            assertTrue(c.containsKey(1));
        }

        @Test
        @DisplayName("없는 키는 null")
        void missingIsNull() {
            Cache<Integer, String> c = create(3);
            c.put(1, "a");
            assertNull(c.get(2));
        }

        @Test
        @DisplayName("같은 키에 다시 넣으면 값이 바뀐다")
        void overwrite() {
            Cache<Integer, String> c = create(3);
            c.put(1, "a");
            c.put(1, "b");
            assertEquals("b", c.get(1));
            assertEquals(1, c.size(), "크기는 안 늘어난다");
            assertEquals(0, c.evictions(), "갱신은 축출이 아니다");
        }

        @Test
        @DisplayName("remove")
        void remove() {
            Cache<Integer, String> c = create(3);
            c.put(1, "a");
            c.put(2, "b");
            assertEquals("a", c.remove(1));
            assertNull(c.remove(1));
            assertNull(c.get(1));
            assertEquals(1, c.size());
            assertEquals(List.of(2), c.keysInOrder());
            assertEquals(0, c.evictions(), "명시적 삭제는 축출이 아니다");
        }

        @Test
        @DisplayName("clear")
        void clear() {
            Cache<Integer, String> c = create(3);
            c.put(1, "a");
            c.put(2, "b");
            c.clear();
            assertEquals(0, c.size());
            assertTrue(c.isEmpty());
            assertEquals(List.of(), c.keysInOrder());
            assertNull(c.get(1));
            c.put(3, "c");
            assertEquals("c", c.get(3), "비운 뒤에도 쓸 수 있다");
        }

        @Test
        @DisplayName("null 키와 null 값은 거부한다")
        void rejectsNulls() {
            Cache<Integer, String> c = create(3);
            assertThrows(IllegalArgumentException.class, () -> c.put(null, "a"));
            assertThrows(IllegalArgumentException.class, () -> c.put(1, null));
        }
    }

    @Nested
    @DisplayName("순서")
    class Ordering {

        @Test
        @DisplayName("넣은 순서대로 줄을 선다")
        void insertionOrder() {
            Cache<Integer, String> c = create(3);
            c.put(1, "a");
            c.put(2, "b");
            c.put(3, "c");
            assertEquals(List.of(1, 2, 3), c.keysInOrder(), "맨 앞이 가장 오래된 것이다");
        }

        @Test
        @DisplayName("get 이 순서를 바꾼다")
        void getRefreshes() {
            Cache<Integer, String> c = create(3);
            c.put(1, "a");
            c.put(2, "b");
            c.put(3, "c");
            c.get(1);
            assertEquals(List.of(2, 3, 1), c.keysInOrder(),
                    "get 은 읽기가 아니다. 1 이 맨 뒤로 가야 한다");
        }

        @Test
        @DisplayName("put 도 기존 키를 맨 뒤로 옮긴다")
        void putRefreshes() {
            Cache<Integer, String> c = create(3);
            c.put(1, "a");
            c.put(2, "b");
            c.put(3, "c");
            c.put(1, "z");
            assertEquals(List.of(2, 3, 1), c.keysInOrder());
        }

        @Test
        @DisplayName("containsKey 는 순서를 바꾸지 않는다")
        void containsDoesNotRefresh() {
            Cache<Integer, String> c = create(3);
            c.put(1, "a");
            c.put(2, "b");
            c.put(3, "c");
            assertTrue(c.containsKey(1));
            assertEquals(List.of(1, 2, 3), c.keysInOrder(), "엿보기는 순서를 건드리지 않는다");
            assertEquals(0, c.hits());
            assertEquals(0, c.misses(), "통계에도 안 잡힌다");
        }

        @Test
        @DisplayName("실패한 get 은 순서를 바꾸지 않는다")
        void missDoesNotRefresh() {
            Cache<Integer, String> c = create(3);
            c.put(1, "a");
            c.put(2, "b");
            assertNull(c.get(99));
            assertEquals(List.of(1, 2), c.keysInOrder());
        }
    }

    @Nested
    @DisplayName("축출")
    class Eviction {

        @Test
        @DisplayName("꽉 차면 가장 오래된 것이 밀려난다")
        void evictsOldest() {
            Cache<Integer, String> c = create(2);
            c.put(1, "a");
            c.put(2, "b");
            c.put(3, "c");
            assertEquals(2, c.size(), "용량을 넘지 않는다");
            assertNull(c.get(1), "1 이 밀려났어야 한다");
            assertEquals("b", c.get(2));
            assertEquals("c", c.get(3));
            assertEquals(1, c.evictions());
        }

        @Test
        @DisplayName("get 으로 되살린 것은 안 밀려난다")
        void refreshedSurvives() {
            Cache<Integer, String> c = create(2);
            c.put(1, "a");
            c.put(2, "b");
            c.get(1);              // 1 을 되살린다
            c.put(3, "c");         // 이제 2 가 가장 오래된 것이다
            assertEquals("a", c.get(1), "1 은 살아 있어야 한다");
            assertNull(c.get(2), "2 가 밀려났어야 한다");
        }

        @Test
        @DisplayName("용량 1")
        void capacityOne() {
            Cache<Integer, String> c = create(1);
            c.put(1, "a");
            c.put(2, "b");
            assertEquals(1, c.size());
            assertNull(c.get(1));
            assertEquals("b", c.get(2));
            assertEquals(1, c.evictions());
            assertEquals(List.of(2), c.keysInOrder());
        }

        @Test
        @DisplayName("지운 뒤에는 자리가 생긴다")
        void removeFreesSpace() {
            Cache<Integer, String> c = create(2);
            c.put(1, "a");
            c.put(2, "b");
            c.remove(1);
            c.put(3, "c");
            assertEquals(0, c.evictions(), "자리가 있었으니 축출이 없다");
            assertEquals("b", c.get(2));
            assertEquals("c", c.get(3));
        }

        @Test
        @DisplayName("갱신만 반복하면 축출이 없다")
        void updatesNeverEvict() {
            Cache<Integer, String> c = create(2);
            c.put(1, "a");
            c.put(2, "b");
            for (int i = 0; i < 100; i++) {
                c.put(1, "x" + i);
                c.put(2, "y" + i);
            }
            assertEquals(2, c.size());
            assertEquals(0, c.evictions());
        }

        @Test
        @DisplayName("축출 순서 전체 추적")
        void fullSequence() {
            Cache<Integer, String> c = create(3);
            c.put(1, "a");
            c.put(2, "b");
            c.put(3, "c");
            assertEquals(List.of(1, 2, 3), c.keysInOrder());
            c.get(1);
            assertEquals(List.of(2, 3, 1), c.keysInOrder());
            c.put(4, "d");
            assertEquals(List.of(3, 1, 4), c.keysInOrder(), "2 가 밀려난다");
            c.put(3, "cc");
            assertEquals(List.of(1, 4, 3), c.keysInOrder());
            c.put(5, "e");
            assertEquals(List.of(4, 3, 5), c.keysInOrder(), "1 이 밀려난다");
            assertEquals(2, c.evictions());
        }
    }

    @Nested
    @DisplayName("통계")
    class Stats {

        @Test
        @DisplayName("hits 와 misses")
        void hitsAndMisses() {
            Cache<Integer, String> c = create(2);
            c.put(1, "a");
            c.get(1);       // hit
            c.get(1);       // hit
            c.get(2);       // miss
            assertEquals(2, c.hits());
            assertEquals(1, c.misses());
        }

        @Test
        @DisplayName("put 은 hits/misses 에 안 잡힌다")
        void putIsNotAccess() {
            Cache<Integer, String> c = create(2);
            c.put(1, "a");
            c.put(1, "b");
            assertEquals(0, c.hits());
            assertEquals(0, c.misses());
        }

        @Test
        @DisplayName("밀려난 키를 다시 찾으면 miss 다")
        void evictedBecomesMiss() {
            Cache<Integer, String> c = create(1);
            c.put(1, "a");
            c.put(2, "b");
            assertNull(c.get(1));
            assertEquals(1, c.misses());
            assertEquals(1, c.evictions());
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class RandomCrossCheck {

        /** 리스트로만 만든 느린 참조 구현. 오래된 것이 앞이다. */
        private static class Reference {
            final int capacity;
            final List<Integer> order = new ArrayList<>();
            final List<String> values = new ArrayList<>();
            long hits;
            long misses;
            long evictions;

            Reference(int capacity) {
                this.capacity = capacity;
            }

            String get(int k) {
                int i = order.indexOf(k);
                if (i < 0) {
                    misses++;
                    return null;
                }
                hits++;
                String v = values.remove(i);
                order.remove(i);
                order.add(k);
                values.add(v);
                return v;
            }

            void put(int k, String v) {
                int i = order.indexOf(k);
                if (i >= 0) {
                    order.remove(i);
                    values.remove(i);
                } else if (order.size() == capacity) {
                    order.remove(0);
                    values.remove(0);
                    evictions++;
                }
                order.add(k);
                values.add(v);
            }

            String remove(int k) {
                int i = order.indexOf(k);
                if (i < 0) {
                    return null;
                }
                order.remove(i);
                return values.remove(i);
            }
        }

        @Test
        @DisplayName("느린 참조 구현과 계속 같은 답을 낸다")
        void matchesReference() {
            Random rnd = new Random(20260813L);
            for (int cap : new int[]{1, 2, 5, 16}) {
                Cache<Integer, String> c = create(cap);
                Reference ref = new Reference(cap);

                for (int step = 0; step < 3000; step++) {
                    int key = rnd.nextInt(20);
                    int op = rnd.nextInt(10);
                    if (op < 5) {
                        assertEquals(ref.get(key), c.get(key),
                                "cap=" + cap + " step=" + step + " get(" + key + ")");
                    } else if (op < 9) {
                        String v = "v" + step;
                        ref.put(key, v);
                        c.put(key, v);
                    } else {
                        assertEquals(ref.remove(key), c.remove(key),
                                "cap=" + cap + " step=" + step + " remove(" + key + ")");
                    }
                    assertEquals(ref.order.size(), c.size(), "크기가 갈렸다 (step " + step + ")");
                    assertEquals(ref.order, c.keysInOrder(), "순서가 갈렸다 (step " + step + ")");
                }
                assertEquals(ref.hits, c.hits());
                assertEquals(ref.misses, c.misses());
                assertEquals(ref.evictions, c.evictions());
            }
        }
    }

    @Nested
    @DisplayName("성능")
    class Performance {

        @Test
        @Timeout(15)
        @DisplayName("get 과 put 이 용량에 안 끌린다")
        void constantTime() {
            // 용량 10만 짜리 캐시에 100만 번 접근한다.
            // 가장 오래된 것을 찾느라 훑는 구현이면 여기서 죽는다.
            Cache<Integer, String> c = create(100_000);
            for (int i = 0; i < 100_000; i++) {
                c.put(i, "v" + i);
            }
            for (int round = 0; round < 10; round++) {
                for (int i = 0; i < 100_000; i++) {
                    assertEquals("v" + i, c.get(i));
                }
            }
            assertEquals(100_000, c.size());
            assertEquals(0, c.evictions());
        }

        @Test
        @Timeout(15)
        @DisplayName("축출이 계속 일어나도 느려지지 않는다")
        void evictionIsConstantTime() {
            Cache<Integer, String> c = create(1000);
            for (int i = 0; i < 1_000_000; i++) {
                c.put(i, "v");
            }
            assertEquals(1000, c.size());
            assertEquals(999_000, c.evictions());
        }
    }
}
