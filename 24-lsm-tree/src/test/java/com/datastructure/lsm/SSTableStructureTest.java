package com.datastructure.lsm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 구조 테스트. 계약만으로는 안 잡히는 것을 본다.
 *
 * SSTable 이 정말 불변인지, memtable 이 정말 정렬해서 넘기는지,
 * 이진 탐색이 정말 맞는지는 밖에서 값만 봐서는 알 수 없다.
 *
 * 리플렉션으로 내부를 보므로 필드 이름이 계약이다. SSTable 은 keys, values, bytes, bloom 을 갖는다.
 */
@DisplayName("SSTable 구조")
class SSTableStructureTest {

    private static SSTable<Integer, String> table(int... keys) {
        List<Map.Entry<Integer, Object>> entries = new ArrayList<>();
        for (int k : keys) {
            entries.add(SSTable.cell(k, "v" + k));
        }
        return new SSTable<>(entries, true);
    }

    @Nested
    @DisplayName("한 번 만들면 안 바뀐다")
    class Immutable {

        @Test
        @DisplayName("필드가 전부 final 이다")
        void allFieldsFinal() {
            // **순차 쓰기만 한다는 말의 코드 수준 뜻이다.**
            // 제자리를 고칠 수 있는 문법적 통로가 아예 없어야 한다.
            for (Field f : SSTable.class.getDeclaredFields()) {
                assertTrue(Modifier.isFinal(f.getModifiers()),
                        "SSTable." + f.getName() + " 이 final 이 아니다");
            }
            assertTrue(Modifier.isFinal(SSTable.class.getModifiers()),
                    "SSTable 자체도 final 이어야 상속으로 뚫을 수 없다");
        }

        @Test
        @DisplayName("고치는 메서드가 없다")
        void noMutators() {
            String[] forbidden = {"set", "add", "put", "remove", "delete", "clear", "insert",
                    "update", "merge", "sort", "fill"};
            for (Method m : SSTable.class.getDeclaredMethods()) {
                for (String prefix : forbidden) {
                    assertFalse(m.getName().startsWith(prefix),
                            "SSTable." + m.getName() + " 은 고치는 메서드로 보인다");
                }
            }
        }

        @Test
        @DisplayName("만든 뒤 원본 목록을 고쳐도 안 바뀐다")
        void copiesTheInput() {
            List<Map.Entry<Integer, Object>> entries = new ArrayList<>();
            entries.add(SSTable.cell(1, "a"));
            entries.add(SSTable.cell(2, "b"));
            SSTable<Integer, String> t = new SSTable<>(entries, false);

            entries.clear();
            entries.add(SSTable.cell(9, "z"));

            assertEquals(2, t.size(), "밖에서 목록을 비웠는데 테이블이 따라 비면 안 된다");
            assertEquals("a", t.rawValue(1));
            assertNull(t.rawValue(9));
        }

        @Test
        @DisplayName("entries() 는 매번 새 목록이다")
        void entriesIsACopy() {
            SSTable<Integer, String> t = table(1, 2, 3);
            List<Map.Entry<Integer, Object>> first = t.entries();
            assertNotSameList(first, t.entries());
            first.clear();
            assertEquals(3, t.entries().size());
            assertEquals(3, t.size());
        }

        @Test
        @DisplayName("천 번 읽어도 내부 배열이 그대로다")
        void readsDoNotChangeAnything() throws Exception {
            SSTable<Integer, String> t = table(1, 3, 5, 7, 9, 11, 13);
            Object[] keysBefore = internal(t, "keys").clone();
            Object[] valuesBefore = internal(t, "values").clone();

            Random rnd = new Random(1L);
            for (int i = 0; i < 1000; i++) {
                t.rawValue(rnd.nextInt(20));
                t.mightContain(rnd.nextInt(20));
            }

            assertEquals(keysBefore.length, internal(t, "keys").length);
            assertTrue(Arrays.equals(keysBefore, internal(t, "keys")), "키 배열이 바뀌었다");
            assertTrue(Arrays.equals(valuesBefore, internal(t, "values")), "값 배열이 바뀌었다");
        }

        private static Object[] internal(SSTable<Integer, String> t, String name) throws Exception {
            Field f = SSTable.class.getDeclaredField(name);
            f.setAccessible(true);
            return (Object[]) f.get(t);
        }

        private static void assertNotSameList(Object a, Object b) {
            assertFalse(a == b, "같은 목록 객체를 두 번 돌려주면 밖에서 고칠 수 있다");
        }
    }

    @Nested
    @DisplayName("정렬된 입력만 받는다")
    class SortedInput {

        @Test
        @DisplayName("어긋난 순서는 거부한다")
        void rejectsUnsorted() {
            List<Map.Entry<Integer, Object>> bad = List.of(SSTable.cell(3, "c"), SSTable.cell(1, "a"));
            assertThrows(IllegalArgumentException.class, () -> new SSTable<>(bad, false));
        }

        @Test
        @DisplayName("같은 키가 두 번 나오면 거부한다")
        void rejectsDuplicates() {
            List<Map.Entry<Integer, Object>> bad = List.of(SSTable.cell(1, "a"), SSTable.cell(1, "b"));
            assertThrows(IllegalArgumentException.class, () -> new SSTable<>(bad, false),
                    "한 SSTable 안에 같은 키가 두 번 있으면 이진 탐색의 답이 흔들린다");
        }

        @Test
        @DisplayName("빈 SSTable 도 만들 수 있다")
        void empty() {
            SSTable<Integer, String> t = new SSTable<>(List.of(), true);
            assertEquals(0, t.size());
            assertEquals(0, t.byteSize());
            assertNull(t.rawValue(1));
        }

        @Test
        @DisplayName("MemTable 이 정렬해서 넘긴다")
        void memtableFeedsSorted() {
            // **왜 memtable 이 정렬 구조여야 하는가.**
            // 해시맵이었다면 여기서 한 번 정렬해야 하고, 그러면 flush 가 O(n log n) 이 된다.
            // 정렬해 두면 flush 는 그냥 훑어 쓰기다. 그것이 "순차 쓰기" 라는 말의 실제 내용이다.
            MemTable<Integer, String> m = new MemTable<>();
            Random rnd = new Random(5L);
            TreeMap<Integer, Object> ref = new TreeMap<>();
            for (int i = 0; i < 200; i++) {
                int k = rnd.nextInt(1000);
                m.put(k, "v" + i);
                ref.put(k, "v" + i);
            }
            List<Map.Entry<Integer, Object>> out = m.entriesInOrder();
            assertEquals(new ArrayList<>(ref.entrySet()), out);

            SSTable<Integer, String> t = new SSTable<>(out, false);
            assertEquals(ref.size(), t.size(), "그대로 SSTable 이 된다");

            m.clear();
            assertEquals(ref.size(), out.size(), "memtable 을 비워도 뽑아둔 목록은 살아 있어야 한다");
        }
    }

    @Nested
    @DisplayName("이진 탐색")
    class BinarySearch {

        @Test
        @DisplayName("있는 키의 자리를 정확히 찾는다")
        void findsExactPosition() {
            SSTable<Integer, String> t = table(2, 4, 6, 8, 10);
            for (int i = 0; i < 5; i++) {
                assertEquals(i, t.indexOf(2 * i + 2), "키 " + (2 * i + 2));
            }
        }

        @Test
        @DisplayName("없는 키는 -1 이다")
        void absentIsMinusOne() {
            SSTable<Integer, String> t = table(2, 4, 6, 8, 10);
            for (int k : new int[]{1, 3, 5, 7, 9, 11, -100, 1000}) {
                assertEquals(-1, t.indexOf(k), "키 " + k);
                assertNull(t.rawValue(k));
            }
        }

        @Test
        @DisplayName("무작위 배열에서 전수 대조한다")
        void matchesLinearScan() {
            Random rnd = new Random(99L);
            for (int trial = 0; trial < 200; trial++) {
                int n = rnd.nextInt(60) + 1;
                TreeMap<Integer, Object> ref = new TreeMap<>();
                while (ref.size() < n) {
                    ref.put(rnd.nextInt(200), "v" + ref.size());
                }
                List<Integer> sortedKeys = new ArrayList<>(ref.keySet());
                List<Map.Entry<Integer, Object>> cells = new ArrayList<>();
                for (Integer k : sortedKeys) {
                    cells.add(SSTable.cell(k, ref.get(k)));
                }
                SSTable<Integer, String> t = new SSTable<>(cells, false);

                for (int k = -5; k < 205; k++) {
                    assertEquals(sortedKeys.indexOf(k), t.indexOf(k),
                            "trial " + trial + " 키 " + k);
                    assertEquals(ref.get(k), t.rawValue(k), "trial " + trial + " 키 " + k);
                }
            }
        }
    }

    @Nested
    @DisplayName("블룸 필터")
    class Bloom {

        @Test
        @DisplayName("11번과 같은 크기 공식이다")
        void sameFormulaAsProblemEleven() {
            assertEquals(959, TinyBloomFilter.optimalBits(100, 0.01), "원소당 9.59 비트");
            assertEquals(7, TinyBloomFilter.optimalHashCount(959, 100));
            TinyBloomFilter f = new TinyBloomFilter(100, 0.01);
            assertEquals(959, f.bitSize());
            assertEquals(7, f.hashCount());
        }

        @Test
        @DisplayName("담은 것을 없다고 하지 않는다")
        void noFalseNegatives() {
            TinyBloomFilter f = new TinyBloomFilter(1000, 0.01);
            for (int i = 0; i < 1000; i++) {
                f.add(i);
            }
            for (int i = 0; i < 1000; i++) {
                assertTrue(f.mightContain(i), "담은 " + i + " 를 없다고 했다");
            }
        }

        @Test
        @DisplayName("오탐률이 공식 근처다")
        void falsePositiveRateIsNearTheTarget() {
            TinyBloomFilter f = new TinyBloomFilter(1000, 0.01);
            for (int i = 0; i < 1000; i++) {
                f.add(i);
            }
            int fp = 0;
            for (int i = 1_000_000; i < 1_100_000; i++) {
                if (f.mightContain(i)) {
                    fp++;
                }
            }
            assertEquals(1030, fp, "10만 개 중 1030 개. 해시가 결정적이라 정확히 이 값이다");
            assertTrue(fp < 2000, "목표 1% 의 두 배는 넘지 않는다");
        }

        @Test
        @DisplayName("끄면 언제나 통과시킨다")
        void disabledFilterNeverSkips() {
            SSTable<Integer, String> t = new SSTable<>(List.of(SSTable.cell(1, "a")), false);
            assertFalse(t.hasBloom());
            assertTrue(t.mightContain(1));
            assertTrue(t.mightContain(999), "필터가 없으면 무조건 뒤져봐야 한다");
        }
    }
}
