package com.datastructure.persistent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("PersistentTreeMap: 계약")
class PersistentTreeMapTest {

    private static PersistentTreeMap<Integer, String> of(int... keys) {
        PersistentTreeMap<Integer, String> map = PersistentTreeMap.empty();
        for (int k : keys) {
            map = map.put(k, "v" + k);
        }
        return map;
    }

    @Nested
    @DisplayName("빈 맵")
    class Empty {

        @Test
        @DisplayName("아무것도 없다")
        void nothing() {
            PersistentTreeMap<Integer, String> map = PersistentTreeMap.empty();
            assertEquals(0, map.size());
            assertTrue(map.isEmpty());
            assertNull(map.get(1));
            assertFalse(map.containsKey(1));
            assertEquals(List.of(), map.keys());
            assertEquals(0, map.height());
            assertSame(PersistentTreeMap.empty(), PersistentTreeMap.empty());
        }
    }

    @Nested
    @DisplayName("기본")
    class Basics {

        @Test
        @DisplayName("넣고 꺼낸다")
        void putGet() {
            PersistentTreeMap<Integer, String> map = of(5, 3, 8, 1, 9);
            assertEquals("v5", map.get(5));
            assertNull(map.get(4));
            assertTrue(map.containsKey(8));
            assertFalse(map.containsKey(4));
            assertEquals(5, map.size());
            assertEquals(List.of(1, 3, 5, 8, 9), map.keys());
        }

        @Test
        @DisplayName("같은 키에 다시 넣으면 크기는 그대로다")
        void overwrite() {
            PersistentTreeMap<Integer, String> a = of(1, 2, 3);
            PersistentTreeMap<Integer, String> b = a.put(2, "바뀐값");
            assertEquals(3, b.size());
            assertEquals("바뀐값", b.get(2));
            assertEquals("v2", a.get(2), "옛 맵의 값이 바뀌었다");
        }

        @Test
        @DisplayName("null 은 거부한다")
        void rejectsNull() {
            PersistentTreeMap<Integer, String> map = of(1, 2);
            assertThrows(IllegalArgumentException.class, () -> map.put(null, "a"));
            assertThrows(IllegalArgumentException.class, () -> map.put(1, null));
            assertThrows(IllegalArgumentException.class, () -> map.get(null));
            assertThrows(IllegalArgumentException.class, () -> map.remove(null));
        }

        @Test
        @DisplayName("toString 은 키 순서다")
        void string() {
            assertEquals("{1=v1, 2=v2, 3=v3}", of(3, 1, 2).toString());
        }
    }

    @Nested
    @DisplayName("지우기")
    class Removal {

        @Test
        @DisplayName("잎, 자식 하나, 자식 둘")
        void threeCases() {
            PersistentTreeMap<Integer, String> map = of(50, 30, 70, 20, 40, 60, 80, 65);

            assertEquals(List.of(20, 30, 40, 50, 60, 65, 70, 80), map.remove(999).keys());
            assertEquals(List.of(30, 40, 50, 60, 65, 70, 80), map.remove(20).keys(), "잎");
            assertEquals(List.of(20, 30, 40, 50, 65, 70, 80), map.remove(60).keys(), "자식 하나");
            assertEquals(List.of(20, 30, 40, 60, 65, 70, 80), map.remove(50).keys(), "자식 둘, 뿌리");
            assertEquals("v65", map.remove(50).get(65));
            assertEquals(8, map.size(), "원본이 줄었다");
        }

        @Test
        @DisplayName("없는 키를 지우면 같은 맵이 돌아온다")
        void removingAbsentKeyChangesNothing() {
            PersistentTreeMap<Integer, String> map = of(1, 2, 3);
            assertSame(map, map.remove(99), "바뀐 것이 없으면 새 버전을 만들 이유가 없다");
            assertSame(PersistentTreeMap.empty(), PersistentTreeMap.<Integer, String>empty().remove(1));
        }

        @Test
        @DisplayName("전부 지우면 빈 맵이 된다")
        void removeAll() {
            PersistentTreeMap<Integer, String> map = TestTrees.balanced(255);
            PersistentTreeMap<Integer, String> cur = map;
            for (int i = 0; i < 255; i++) {
                cur = cur.remove(2 * i);
                assertEquals(254 - i, cur.size(), "키 " + (2 * i) + " 를 지운 뒤");
            }
            assertTrue(cur.isEmpty());
            assertEquals(0, cur.height());
            assertEquals(255, map.size(), "원본이 줄었다");
        }
    }

    @Nested
    @DisplayName("옛 버전이 안 변한다")
    class OldVersionsSurvive {

        @Test
        @DisplayName("100번을 고쳐도 첫 버전이 그대로다")
        void firstVersionStaysPut() {
            PersistentTreeMap<Integer, String> first = of(1, 2, 3);
            PersistentTreeMap<Integer, String> cur = first;
            for (int i = 0; i < 100; i++) {
                cur = cur.put(i, "덮어씀" + i).put(1, "덮어씀");
                if (i % 3 == 0) {
                    cur = cur.remove(2);
                }
            }
            assertEquals(List.of(1, 2, 3), first.keys(), "첫 버전의 키가 바뀌었다");
            assertEquals("v1", first.get(1), "첫 버전의 값이 바뀌었다");
            assertEquals("v2", first.get(2));
            assertEquals(3, first.size());
        }

        @Test
        @DisplayName("모든 중간 버전이 그 시점의 답을 준다")
        void everySnapshotAnswersForItsMoment() {
            List<PersistentTreeMap<Integer, String>> versions = new ArrayList<>();
            PersistentTreeMap<Integer, String> cur = PersistentTreeMap.empty();
            versions.add(cur);
            for (int i = 0; i < 200; i++) {
                cur = cur.put(i, "v" + i);
                versions.add(cur);
            }
            for (int v = 0; v <= 200; v++) {
                PersistentTreeMap<Integer, String> snapshot = versions.get(v);
                assertEquals(v, snapshot.size(), v + "번 버전의 크기");
                assertNull(snapshot.get(v), v + "번 버전이 미래의 키를 알고 있다");
                if (v > 0) {
                    assertEquals("v" + (v - 1), snapshot.get(v - 1));
                }
            }
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class CrossCheck {

        @Test
        @Timeout(30)
        @DisplayName("TreeMap 과 3000 스텝을 대조하고 스냅샷 3000개를 마지막에 전부 확인한다")
        void matchesTreeMapAtEveryMoment() {
            Random rnd = new Random(20260814L);
            PersistentTreeMap<Integer, String> map = PersistentTreeMap.empty();
            TreeMap<Integer, String> ref = new TreeMap<>();

            List<PersistentTreeMap<Integer, String>> snapshots = new ArrayList<>();
            List<TreeMap<Integer, String>> refSnapshots = new ArrayList<>();

            for (int step = 0; step < 3000; step++) {
                int key = rnd.nextInt(300);
                if (rnd.nextInt(10) < 6) {
                    String value = "v" + step;
                    map = map.put(key, value);
                    ref.put(key, value);
                } else {
                    map = map.remove(key);
                    ref.remove(key);
                }
                assertEquals(ref.size(), map.size(), "크기가 갈렸다 (step " + step + ")");
                assertEquals(new ArrayList<>(ref.keySet()), map.keys(), "키가 갈렸다 (step " + step + ")");

                snapshots.add(map);
                // 가변 맵으로 시점을 남기려면 이렇게 통째로 복사해야 한다.
                // 그 비용이 이 자료구조가 없애려는 바로 그것이다.
                refSnapshots.add(new TreeMap<>(ref));
            }

            // 영속성의 정의. 3000번을 고친 뒤에도 3000개의 옛 버전이 전부 그 시점의 답을 준다.
            for (int i = 0; i < snapshots.size(); i++) {
                PersistentTreeMap<Integer, String> snapshot = snapshots.get(i);
                TreeMap<Integer, String> expected = refSnapshots.get(i);
                assertEquals(expected.size(), snapshot.size(), i + "번 스냅샷의 크기");
                assertEquals(new ArrayList<>(expected.keySet()), snapshot.keys(), i + "번 스냅샷의 키");
                for (var entry : expected.entrySet()) {
                    assertEquals(entry.getValue(), snapshot.get(entry.getKey()),
                            i + "번 스냅샷의 키 " + entry.getKey());
                }
            }
        }

        @Test
        @Timeout(30)
        @DisplayName("5000개를 균형 순서로 넣고 지운다")
        void bulk() {
            PersistentTreeMap<Integer, String> map = TestTrees.balanced(5000);
            assertEquals(5000, map.size());
            assertEquals(13, map.height(), "가운데부터 넣으면 높이가 log2 규모다");
            for (int i = 0; i < 5000; i++) {
                assertEquals("v" + (2 * i), map.get(2 * i), "키 " + (2 * i));
                assertNull(map.get(2 * i + 1));
            }

            PersistentTreeMap<Integer, String> cur = map;
            for (int x : TestTrees.balancedOrder(5000)) {
                cur = cur.remove(2 * x);
            }
            assertTrue(cur.isEmpty());
            assertEquals(5000, map.size(), "원본이 줄었다");
        }
    }
}
