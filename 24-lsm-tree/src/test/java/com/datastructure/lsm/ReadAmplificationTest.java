package com.datastructure.lsm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 한계 측정 1: 순차 쓰기의 대가는 읽기가 낸다.
 *
 * 15번 B+트리는 조회 한 번에 높이만큼(4번) 읽고 끝났다.
 * 여기서는 층이 쌓인 만큼 읽는다. 시간을 재지 않고 뒤진 횟수를 센다.
 */
@DisplayName("한계 측정: 읽기 증폭과 블룸 필터")
class ReadAmplificationTest {

    /** 임계 100 으로 키 0..999 를 넣으면 SSTable 이 정확히 10 장 쌓인다. */
    private static LsmTree<Integer, String> tenTables(boolean bloom) {
        LsmTree<Integer, String> t = new LsmTree<>(100, bloom);
        for (int i = 0; i < 1000; i++) {
            t.put(i, "v" + i);
        }
        return t;
    }

    @Nested
    @DisplayName("읽기 증폭")
    class ReadAmplification {

        @Test
        @DisplayName("없는 키 하나에 SSTable 을 10번 뒤진다")
        void absentKeyProbesEveryTable() {
            LsmTree<Integer, String> t = tenTables(false);
            assertEquals(10, t.sstableCount());
            t.resetDiskReads();

            assertNull(t.get(123456));
            assertEquals(10, t.diskReads(),
                    "없다는 것을 확인하려면 모든 층을 다 봐야 한다. B+트리는 4번이면 끝났다");
        }

        @Test
        @DisplayName("어느 층에 있느냐가 비용을 정한다")
        void costDependsOnLayer() {
            LsmTree<Integer, String> t = tenTables(false);
            t.resetDiskReads();
            assertEquals("v999", t.get(999));
            assertEquals(1, t.diskReads(), "가장 최근에 쓴 키는 첫 층에서 끝난다");

            t.resetDiskReads();
            assertEquals("v0", t.get(0));
            assertEquals(10, t.diskReads(), "가장 오래된 키는 마지막 층까지 내려간다");

            t.resetDiskReads();
            assertEquals("v500", t.get(500));
            assertEquals(5, t.diskReads());
        }

        @Test
        @DisplayName("memtable 에 있으면 디스크를 안 읽는다")
        void memtableIsFree() {
            LsmTree<Integer, String> t = tenTables(false);
            t.put(7, "새 값");
            t.resetDiskReads();
            assertEquals("새 값", t.get(7));
            assertEquals(0, t.diskReads(), "쓰기가 싼 이유가 이것이다");
        }

        @Test
        @DisplayName("compact 가 읽기 증폭을 되돌린다")
        void compactionUndoesIt() {
            LsmTree<Integer, String> t = tenTables(false);
            t.resetDiskReads();
            t.get(123456);
            assertEquals(10, t.diskReads());

            t.compact();
            t.resetDiskReads();
            t.get(123456);
            assertEquals(1, t.diskReads(), "한 장으로 합치면 한 번이면 된다");
            assertEquals(1, t.sstableCount());
            // compaction 이 공짜가 아니다. 살아 있는 1000개를 통째로 다시 썼다.
            assertEquals(14_780 * 2, t.sequentialBytesWritten(),
                    "flush 로 14780 바이트, compaction 이 같은 양을 또 썼다");
        }

        @Test
        @DisplayName("있는 키 1000개를 전부 조회하면 5500번이다")
        void allPresentKeys() {
            // 층 i 에 있는 키는 i+1 번 뒤진다. 100개씩 10층이라 100*(10+9+...+1) = 5500.
            LsmTree<Integer, String> t = tenTables(false);
            t.resetDiskReads();
            for (int i = 0; i < 1000; i++) {
                assertEquals("v" + i, t.get(i));
            }
            assertEquals(5500, t.diskReads());
        }
    }

    @Nested
    @DisplayName("블룸 필터가 그것을 얼마나 줄이는가")
    class BloomFilterEffect {

        @Test
        @DisplayName("없는 키 1만 개: 10만번이 955번이 된다")
        void absentKeysWithAndWithoutBloom() {
            // **이 박스의 가장 중요한 측정이다.**
            // 두 숫자 다 파이썬 참조 구현으로 검산했다. 해시가 결정적이라 오탐 수까지 정확히 같다.
            LsmTree<Integer, String> without = tenTables(false);
            LsmTree<Integer, String> with = tenTables(true);
            assertFalse(without.sstableAt(0).hasBloom());
            assertTrue(with.sstableAt(0).hasBloom());

            without.resetDiskReads();
            with.resetDiskReads();
            for (int key = 1000; key < 11_000; key++) {
                assertNull(without.get(key), "키 " + key);
                assertNull(with.get(key), "키 " + key);
            }

            assertEquals(100_000, without.diskReads(), "1만 개 x 10층");
            assertEquals(955, with.diskReads(),
                    "블룸이 통과시킨 것만 뒤진다. 955 는 오탐(1% 근처)의 수다");
            assertTrue(without.diskReads() / with.diskReads() > 100,
                    "104배다. 없는 키를 찾는 비용이 사실상 사라진다");
        }

        @Test
        @DisplayName("있는 키에는 별로 못 줄인다")
        void presentKeysGainLess() {
            // 블룸은 "없다" 만 확실히 안다. 있는 키의 층을 건너뛰게 해주지는 못한다.
            // 5500 이 1051 로 준 것은 **그 키가 없는 위쪽 층들** 을 건너뛴 몫이다.
            LsmTree<Integer, String> with = tenTables(true);
            with.resetDiskReads();
            for (int i = 0; i < 1000; i++) {
                assertEquals("v" + i, with.get(i));
            }
            assertEquals(1051, with.diskReads());
            assertTrue(with.diskReads() >= 1000,
                    "적어도 키마다 한 번은 진짜 읽어야 한다. 그건 못 줄인다");
        }

        @Test
        @DisplayName("누락은 없다")
        void neverSkipsAKeyThatIsThere() {
            // 블룸 필터의 유일한 보장이다. 이게 깨지면 조회가 조용히 틀린다.
            LsmTree<Integer, String> t = tenTables(true);
            for (int i = 0; i < 1000; i++) {
                assertEquals("v" + i, t.get(i), "키 " + i + " 를 블룸이 잘못 걸렀다");
            }
            for (int i = 0; i < t.sstableCount(); i++) {
                SSTable<Integer, String> table = t.sstableAt(i);
                for (int j = 0; j < table.size(); j++) {
                    assertTrue(table.mightContain(table.keyAt(j)),
                            "담은 키를 mightContain 이 false 라고 했다");
                }
            }
        }

        @Test
        @DisplayName("블룸을 켜도 답은 같다")
        void sameAnswers() {
            LsmTree<Integer, String> without = tenTables(false);
            LsmTree<Integer, String> with = tenTables(true);
            assertEquals(without.keys(), with.keys());
            assertEquals(without.rangeScan(200, 800), with.rangeScan(200, 800));
            assertEquals(without.storedEntryCount(), with.storedEntryCount(),
                    "블룸은 엔트리 수를 안 바꾼다");
        }
    }
}
