package com.datastructure.interval;

import static com.datastructure.interval.TestSupport.sorted;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 좌표 압축. 13번 세그먼트 트리로 이 문제를 풀려 할 때 무엇이 막히는지가 요점이다.
 *
 * 배열 한 칸이 좌표 한 개다. 좌표가 10 억까지 흩어져 있으면 배열도 그만큼이다.
 * 그런데 구간이 1000 개면 실제로 등장하는 좌표는 아무리 많아도 2000 개다.
 */
@DisplayName("좌표 압축")
class CoordinateCompressorTest {

    @Nested
    @DisplayName("번호 매기기")
    class Numbering {

        @Test
        @DisplayName("등장한 좌표만 0 부터 번호를 받는다")
        void assignsRanksToPresentCoordinatesOnly() {
            List<Interval> given = List.of(
                    Interval.of(1_000_000_000L, 2_000_000_000L),
                    Interval.of(5, 1_000_000_000L),
                    Interval.of(5, 7));
            CoordinateCompressor c = new CoordinateCompressor(given);

            assertEquals(4, c.size(), "좌표는 5, 7, 10억, 20억 넷뿐이다");
            assertEquals(0, c.compressPoint(5));
            assertEquals(1, c.compressPoint(7));
            assertEquals(2, c.compressPoint(1_000_000_000L));
            assertEquals(3, c.compressPoint(2_000_000_000L));
        }

        @Test
        @DisplayName("중복 좌표는 한 번호를 나눠 쓴다")
        void deduplicates() {
            List<Interval> given = new ArrayList<>();
            for (int i = 0; i < 50; i++) given.add(Interval.of(10, 20));
            given.add(Interval.of(20, 30));
            CoordinateCompressor c = new CoordinateCompressor(given);
            assertEquals(3, c.size(), "10, 20, 30 셋이다");
            assertEquals(1, c.compressPoint(20), "두 구간이 공유하는 20 은 하나의 번호다");
        }

        @Test
        @DisplayName("등장하지 않은 좌표는 거절한다")
        void rejectsUnknownCoordinate() {
            CoordinateCompressor c = new CoordinateCompressor(List.of(Interval.of(10, 20)));
            assertThrows(IllegalArgumentException.class, () -> c.compressPoint(15),
                    "가장 가까운 번호를 돌려주면 왕복이 조용히 깨진다");
            assertThrows(IllegalArgumentException.class, () -> c.compressPoint(0));
            assertThrows(IllegalArgumentException.class, () -> c.decompressPoint(2));
            assertThrows(IllegalArgumentException.class, () -> c.decompressPoint(-1));
        }

        @Test
        void handlesEmptyInput() {
            CoordinateCompressor c = new CoordinateCompressor(List.of());
            assertEquals(0, c.size());
            assertThrows(IllegalArgumentException.class, () -> c.compressPoint(0));
        }
    }

    @Nested
    @DisplayName("왕복과 순서 보존")
    class RoundTrip {

        @Test
        @DisplayName("구간 1000개를 압축했다 되돌리면 원본과 같다")
        void roundTripsExactly() {
            List<Interval> given = new ArrayList<>();
            TestSupport.Dice dice = new TestSupport.Dice(2026L);
            for (int i = 0; i < 1_000; i++) {
                long s = dice.next(1_000_000_000L);
                given.add(new Interval(s, s + 1 + dice.next(1_000)));
            }
            CoordinateCompressor c = new CoordinateCompressor(given);

            for (Interval iv : given) {
                Interval small = c.compress(iv);
                assertTrue(small.start < small.end, "압축해도 반개구간이다: " + small);
                assertTrue(small.end < c.size(), "번호는 0 부터 좌표 개수 미만이다");
                assertEquals(iv, c.decompress(small), iv + " 의 왕복이 깨졌다");
            }
        }

        @Test
        @DisplayName("압축은 순서를 보존하므로 겹침의 답이 안 바뀐다")
        void overlapAnswersSurviveCompression() {
            // 이게 압축이 쓸모 있는 이유다. 겹침은 좌표의 크기 비교로만 정해지므로
            // 순서만 지키면 좌표를 갈아끼워도 답이 같다.
            List<Interval> given = new ArrayList<>();
            TestSupport.Dice dice = new TestSupport.Dice(9001L);
            for (int i = 0; i < 300; i++) {
                long s = dice.next(1_000_000_000L);
                given.add(new Interval(s, s + 1 + dice.next(50_000_000L)));
            }
            CoordinateCompressor c = new CoordinateCompressor(given);

            IntervalTree big = new IntervalTree();
            IntervalTree small = new IntervalTree();
            for (Interval iv : given) {
                big.insert(iv);
                small.insert(c.compress(iv));
            }
            assertEquals(big.size(), small.size());

            for (Interval query : given) {
                List<Interval> fromBig = sorted(big.findAll(query));
                List<Interval> fromSmall = new ArrayList<>();
                for (Interval iv : small.findAll(c.compress(query))) fromSmall.add(c.decompress(iv));
                assertEquals(fromBig, sorted(fromSmall), "질의 " + query + " 의 답이 달라졌다");
            }
        }
    }

    @Nested
    @DisplayName("측정: 왜 필요한가")
    class WhyItMatters {

        @Test
        @DisplayName("좌표 범위 10억, 구간 1000개면 배열이 40억 칸 대 8000 칸이다")
        void arraySizeCollapses() {
            List<Interval> given = new ArrayList<>();
            TestSupport.Dice dice = new TestSupport.Dice(2026L);
            long span = 1_000_000_000L;
            for (int i = 0; i < 1_000; i++) {
                long s = dice.next(span);
                given.add(new Interval(s, s + 1 + dice.next(1_000)));
            }
            CoordinateCompressor c = new CoordinateCompressor(given);

            assertTrue(c.size() <= 2 * given.size(),
                    "구간 n 개의 좌표는 아무리 많아도 2n 이다. 실제로는 " + c.size() + "개");
            assertTrue(c.size() > given.size(), "좌표가 " + c.size() + "개면 대부분 서로 다르다");

            // 13번 세그먼트 트리의 관행은 배열을 4n 으로 잡는 것이었다.
            long rawCells = 4L * span;
            long compressedCells = 4L * c.size();
            assertEquals(4_000_000_000L, rawCells, "압축 없이는 좌표 범위만큼이 필요하다");
            assertTrue(rawCells / compressedCells > 100_000,
                    "압축하면 " + compressedCells + " 칸이다. " + (rawCells / compressedCells) + "배 차이다");
        }
    }
}
