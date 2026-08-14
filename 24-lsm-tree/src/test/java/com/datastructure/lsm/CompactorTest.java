package com.datastructure.lsm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 병합만 따로 본다.
 *
 * compaction 이 하는 일은 하나다. 정렬된 여러 장을 한 번 훑어 정렬된 한 장으로 만든다.
 * 그 과정에서 같은 키를 만나면 최신 것만 살린다. 순서가 곧 정확성이다.
 */
@DisplayName("Compactor")
class CompactorTest {

    @SafeVarargs
    private static SSTable<Integer, String> table(Map.Entry<Integer, Object>... cells) {
        return new SSTable<>(List.of(cells), false);
    }

    private static List<Map.Entry<Integer, Object>> merge(
            boolean dropTombstones, List<SSTable<Integer, String>> newestFirst) {
        return Compactor.mergeEntries(newestFirst, dropTombstones);
    }

    @Nested
    @DisplayName("최신이 이긴다")
    class NewestWins {

        @Test
        @DisplayName("같은 키가 여러 층에 있으면 앞의 것만 남는다")
        void duplicateKeys() {
            SSTable<Integer, String> newest = table(SSTable.cell(1, "new"));
            SSTable<Integer, String> middle = table(SSTable.cell(1, "mid"));
            SSTable<Integer, String> oldest = table(SSTable.cell(1, "old"));

            assertEquals(List.of(SSTable.cell(1, "new")),
                    merge(false, List.of(newest, middle, oldest)));
            assertEquals(List.of(SSTable.cell(1, "old")),
                    merge(false, List.of(oldest, middle, newest)),
                    "목록 순서를 뒤집으면 답이 뒤집힌다. 여기가 정확성의 전부다");
        }

        @Test
        @DisplayName("겹치는 구간을 섞어 합친다")
        void interleaved() {
            SSTable<Integer, String> newest = table(
                    SSTable.cell(1, "n1"), SSTable.cell(4, "n4"), SSTable.cell(7, "n7"));
            SSTable<Integer, String> oldest = table(
                    SSTable.cell(2, "o2"), SSTable.cell(4, "o4"), SSTable.cell(5, "o5"),
                    SSTable.cell(9, "o9"));

            assertEquals(List.of(
                            SSTable.cell(1, "n1"), SSTable.cell(2, "o2"), SSTable.cell(4, "n4"),
                            SSTable.cell(5, "o5"), SSTable.cell(7, "n7"), SSTable.cell(9, "o9")),
                    merge(false, List.of(newest, oldest)));
        }

        @Test
        @DisplayName("결과는 정렬되어 있고 키가 한 번씩만 나온다")
        void outputIsSortedAndUnique() {
            Random rnd = new Random(31L);
            for (int trial = 0; trial < 100; trial++) {
                List<SSTable<Integer, String>> tables = new ArrayList<>();
                TreeMap<Integer, Object> expected = new TreeMap<>();
                int layers = rnd.nextInt(5) + 1;
                for (int layer = 0; layer < layers; layer++) {
                    TreeMap<Integer, Object> content = new TreeMap<>();
                    int n = rnd.nextInt(12);
                    for (int i = 0; i < n; i++) {
                        content.put(rnd.nextInt(30), "L" + layer + "_" + i);
                    }
                    List<Map.Entry<Integer, Object>> cells = new ArrayList<>();
                    for (Map.Entry<Integer, Object> e : content.entrySet()) {
                        cells.add(SSTable.cell(e.getKey(), e.getValue()));
                    }
                    tables.add(new SSTable<>(cells, false));
                    // 나중에 추가되는 층일수록 오래된 것이므로 이미 있는 키는 덮지 않는다
                    for (Map.Entry<Integer, Object> e : content.entrySet()) {
                        expected.putIfAbsent(e.getKey(), e.getValue());
                    }
                }
                List<Map.Entry<Integer, Object>> merged = merge(false, tables);
                assertEquals(new ArrayList<>(expected.entrySet()), merged, "trial " + trial);

                Integer previous = null;
                for (Map.Entry<Integer, Object> e : merged) {
                    assertTrue(previous == null || previous < e.getKey(), "정렬이 깨졌다");
                    previous = e.getKey();
                }
                assertEquals(merged.size(),
                        new SSTable<>(merged, false).size(),
                        "결과를 그대로 SSTable 에 다시 넣을 수 있어야 한다");
            }
        }
    }

    @Nested
    @DisplayName("tombstone 처리")
    class Tombstones {

        @Test
        @DisplayName("안 지우면 데리고 간다")
        void keepsThem() {
            SSTable<Integer, String> newest = table(SSTable.cell(1, Tombstone.MARKER));
            SSTable<Integer, String> oldest = table(SSTable.cell(1, "old"), SSTable.cell(2, "two"));

            List<Map.Entry<Integer, Object>> merged = merge(false, List.of(newest, oldest));
            assertEquals(2, merged.size());
            assertTrue(Tombstone.is(merged.get(0).getValue()));
            assertEquals("two", merged.get(1).getValue());
        }

        @Test
        @DisplayName("지우면 그 키가 통째로 없어진다")
        void dropsThem() {
            SSTable<Integer, String> newest = table(SSTable.cell(1, Tombstone.MARKER));
            SSTable<Integer, String> oldest = table(SSTable.cell(1, "old"), SSTable.cell(2, "two"));

            List<Map.Entry<Integer, Object>> merged = merge(true, List.of(newest, oldest));
            assertEquals(List.of(SSTable.cell(2, "two")), merged,
                    "옛 값도 tombstone 도 안 남는다. 맨 아래층까지 합쳤을 때만 해도 되는 일이다");
        }

        @Test
        @DisplayName("tombstone 이 더 오래된 쪽이면 아무 일도 없다")
        void olderTombstoneLoses() {
            SSTable<Integer, String> newest = table(SSTable.cell(1, "revived"));
            SSTable<Integer, String> oldest = table(SSTable.cell(1, Tombstone.MARKER));

            assertEquals(List.of(SSTable.cell(1, "revived")),
                    merge(true, List.of(newest, oldest)));
            assertEquals(List.of(SSTable.cell(1, "revived")),
                    merge(false, List.of(newest, oldest)));
        }

        @Test
        @DisplayName("전부 tombstone 이고 다 지우면 빈 결과다")
        void everythingDropped() {
            SSTable<Integer, String> only = table(
                    SSTable.cell(1, Tombstone.MARKER), SSTable.cell(2, Tombstone.MARKER));
            assertEquals(List.of(), merge(true, List.of(only)));
            assertEquals(2, merge(false, List.of(only)).size());
        }
    }

    @Nested
    @DisplayName("가장자리")
    class Edges {

        @Test
        @DisplayName("빈 테이블이 섞여 있어도 된다")
        void emptyTablesAreFine() {
            SSTable<Integer, String> empty = new SSTable<>(List.of(), false);
            SSTable<Integer, String> filled = table(SSTable.cell(1, "a"));
            assertEquals(List.of(SSTable.cell(1, "a")), merge(false, List.of(empty, filled)));
            assertEquals(List.of(SSTable.cell(1, "a")), merge(false, List.of(filled, empty)));
            assertEquals(List.of(), merge(false, List.of(empty, empty)));
        }

        @Test
        @DisplayName("한 장만 줘도 된다")
        void singleTable() {
            SSTable<Integer, String> only = table(SSTable.cell(1, "a"), SSTable.cell(2, "b"));
            assertEquals(only.entries(), merge(false, List.of(only)));
        }

        @Test
        @DisplayName("합칠 것이 없으면 거부한다")
        void nothingToCompact() {
            assertThrows(IllegalArgumentException.class,
                    () -> Compactor.compact(List.<SSTable<Integer, String>>of(), true, false));
        }

        @Test
        @DisplayName("합친 결과의 바이트는 살아남은 엔트리의 합이다")
        void byteSizeOfResult() {
            SSTable<Integer, String> newest = table(SSTable.cell(1, "aa"));
            SSTable<Integer, String> oldest = table(SSTable.cell(1, "bbbbb"), SSTable.cell(2, "c"));
            SSTable<Integer, String> merged =
                    Compactor.compact(List.of(newest, oldest), true, false);
            assertEquals(SSTable.entryBytes(1, "aa") + SSTable.entryBytes(2, "c"),
                    merged.byteSize(), "버린 옛 값의 바이트는 안 센다");
            assertEquals("aa", merged.rawValue(1));
            assertNull(merged.rawValue(3));
        }
    }
}
