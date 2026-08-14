package com.datastructure.lsm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 한계 측정 2: 제자리를 안 고치면 옛 것이 그대로 남는다.
 *
 * 15번 B+트리는 같은 키를 100번 갱신해도 저장된 엔트리가 1개였다. 그 자리를 고쳐 썼으니까.
 * 여기서는 100개가 된다. 그것이 순차 쓰기의 값이다.
 */
@DisplayName("한계 측정: 공간 증폭과 쓰기 증폭")
class SpaceAmplificationTest {

    @Nested
    @DisplayName("공간 증폭")
    class SpaceAmplification {

        @Test
        @DisplayName("같은 키를 100번 갱신하면 100개가 쌓인다")
        void hundredVersionsOfOneKey() {
            LsmTree<Integer, String> t = new LsmTree<>(100);
            for (int i = 0; i < 100; i++) {
                t.put(42, "v" + i);
                t.flush();
            }
            assertEquals(100, t.sstableCount());
            assertEquals(100, t.storedEntryCount(), "산 키는 하나인데 100개를 들고 있다");
            assertEquals(1, t.size());
            assertEquals(100.0, t.spaceAmplification(), 1e-9);
            assertEquals("v99", t.get(42), "답은 그래도 최신이다");
            assertEquals(1290, t.sequentialBytesWritten(), "파이썬 참조로 검산한 값이다");
        }

        @Test
        @DisplayName("compact 가 99개를 걷어낸다")
        void compactionCollapsesThem() {
            LsmTree<Integer, String> t = new LsmTree<>(100);
            for (int i = 0; i < 100; i++) {
                t.put(42, "v" + i);
                t.flush();
            }
            t.compact();

            assertEquals(1, t.sstableCount());
            assertEquals(1, t.storedEntryCount(), "옛 버전 99개가 사라진다");
            assertEquals(1.0, t.spaceAmplification(), 1e-9);
            assertEquals("v99", t.get(42));
            assertEquals(1303, t.sequentialBytesWritten(),
                    "1290 을 쓰고, 걷어내려고 13 을 또 썼다. 이것이 쓰기 증폭이다");
        }

        @Test
        @DisplayName("서로 다른 키만 넣으면 증폭이 없다")
        void distinctKeysDoNotAmplify() {
            LsmTree<Integer, String> t = new LsmTree<>(100);
            for (int i = 0; i < 1000; i++) {
                t.put(i, "v" + i);
            }
            assertEquals(1000, t.storedEntryCount());
            assertEquals(1000, t.size());
            assertEquals(1.0, t.spaceAmplification(), 1e-9,
                    "덮어쓴 것이 없으면 LSM 도 공간을 더 안 쓴다");
            assertEquals(14_780, t.sequentialBytesWritten());
        }

        @Test
        @DisplayName("절반을 갱신하면 1.5배가 된다")
        void halfUpdated() {
            LsmTree<Integer, String> t = new LsmTree<>(1000);
            for (int i = 0; i < 1000; i++) {
                t.put(i, "a" + i);
            }
            for (int i = 0; i < 500; i++) {
                t.put(i, "b" + i);
            }
            t.flush();
            assertEquals(1500, t.storedEntryCount());
            assertEquals(1000, t.size());
            assertEquals(1.5, t.spaceAmplification(), 1e-9);

            t.compact();
            assertEquals(1000, t.storedEntryCount());
            assertEquals(1.0, t.spaceAmplification(), 1e-9);
        }
    }

    @Nested
    @DisplayName("쓰기는 언제나 순차다")
    class SequentialWritesOnly {

        @Test
        @DisplayName("flush 한 번이 memtable 전체를 한 번에 쏟는다")
        void flushWritesWholeMemtable() {
            LsmTree<Integer, String> t = new LsmTree<>(4);
            for (int i = 0; i < 12; i++) {
                t.put(i, "v" + i);
            }
            assertEquals(136, t.sequentialBytesWritten());
            assertEquals(3, t.flushCount());
            long sum = 0;
            for (int i = 0; i < t.sstableCount(); i++) {
                sum += t.sstableAt(i).byteSize();
            }
            assertEquals(sum, t.sequentialBytesWritten(),
                    "쓴 바이트는 지금 들고 있는 SSTable 의 합과 정확히 같다. 고쳐 쓴 것이 없으니까");
        }

        @Test
        @DisplayName("엔트리 바이트 계산")
        void entryBytes() {
            assertEquals(11, SSTable.entryBytes(7, "v7"), "머리 8 + 키 1 + 값 2");
            assertEquals(9, SSTable.entryBytes(7, Tombstone.MARKER), "tombstone 은 값이 없다");
            assertEquals(15, SSTable.entryBytes(100, "v100"));
        }

        @Test
        @DisplayName("쓰기 증폭: compact 를 많이 할수록 총 쓰기가 는다")
        void compactionCostsWrites() {
            // 읽기를 싸게 하려고 compaction 을 자주 하면 쓴 바이트가 는다.
            // LSM 튜닝은 읽기 증폭, 공간 증폭, 쓰기 증폭 셋의 삼각형이고 셋 다 줄일 수는 없다.
            // 블룸 필터를 꺼서 읽기 증폭만 본다. 켜면 없는 키를 건너뛰어 셈이 흐려진다.
            LsmTree<Integer, String> lazy = new LsmTree<>(100, false);
            LsmTree<Integer, String> eager = new LsmTree<>(100, false);
            for (int i = 0; i < 1000; i++) {
                lazy.put(i, "v" + i);
                eager.put(i, "v" + i);
                if (i % 100 == 99) {
                    eager.compact();
                }
            }
            assertEquals(14_780, lazy.sequentialBytesWritten());
            assertEquals(95_080, eager.sequentialBytesWritten(),
                    "flush 14780 + compaction 80300. 파이썬 참조로 검산한 값이다");
            assertEquals(1, eager.sstableCount());
            assertEquals(10, lazy.sstableCount());
            assertTrue(eager.sequentialBytesWritten() > lazy.sequentialBytesWritten() * 6,
                    "부지런히 합친 쪽이 6.4배를 썼다");

            lazy.resetDiskReads();
            eager.resetDiskReads();
            lazy.get(999_999);
            eager.get(999_999);
            assertEquals(10, lazy.diskReads(), "게으른 쪽은 읽을 때 갚는다");
            assertEquals(1, eager.diskReads());
        }
    }
}
