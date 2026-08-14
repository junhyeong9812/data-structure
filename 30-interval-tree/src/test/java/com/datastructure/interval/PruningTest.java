package com.datastructure.interval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * 한계 측정. maxEnd 한 필드가 실제로 일을 줄이는지, 그리고 언제 안 줄이는지를 숫자로 못 박는다.
 *
 * 시간을 재지 않는다. 기계와 JIT 에 따라 흔들리기 때문이다. 대신 들여다본 노드 수를 센다.
 *
 * 이 측정이 없으면 정답과 오답이 구별되지 않는다.
 * 가지치기 두 줄을 통째로 지워도 답은 똑같이 맞다. 방문 수만 전수 조사에 가까워진다.
 * 그것이 이 박스에서 가장 위험한 실패다.
 */
@DisplayName("가지치기의 효과와 한계")
class PruningTest {

    private static final int N = 4000;
    private static final long SPAN = 1_000_000;
    private static final long MAX_LENGTH = 2_000;

    private static List<Interval> dataset() {
        return new TestSupport.Dice(55L).intervals(N, SPAN, MAX_LENGTH);
    }

    @Nested
    @DisplayName("측정 1: maxEnd 는 실제로 줄인다")
    class ItWorks {

        @Test
        @DisplayName("findAll 200번")
        @Timeout(120)
        void findAllVisitsFarFewer() {
            List<Interval> intervals = dataset();
            NaiveIntervalStore naive = new NaiveIntervalStore();
            IntervalTree tree = new IntervalTree();
            for (Interval iv : intervals) {
                naive.insert(iv);
                tree.insert(iv);
            }
            int n = naive.size();

            long naiveTotal = 0;
            long treeTotal = 0;
            long results = 0;
            TestSupport.Dice dice = new TestSupport.Dice(31337L);
            for (int q = 0; q < 200; q++) {
                Interval query = dice.interval(SPAN, 3_000);
                results += naive.findAll(query).size();
                naiveTotal += naive.visitedNodes();
                tree.findAll(query);
                treeTotal += tree.visitedNodes();
            }

            assertEquals(200L * n, naiveTotal, "전수 조사는 질의마다 정확히 n 이다");
            assertTrue(results > 500, "결과가 " + results + "개뿐이면 측정이 시시하다");
            assertTrue(treeTotal * 20 < naiveTotal,
                    "트리가 " + treeTotal + " 번 봤다. 전수는 " + naiveTotal + " 번이다. "
                            + "가지치기 두 줄 중 하나만 지워도 이 줄에서 걸린다");
        }

        @Test
        @DisplayName("findAny 200번")
        @Timeout(120)
        void findAnyVisitsFarFewer() {
            List<Interval> intervals = dataset();
            NaiveIntervalStore naive = new NaiveIntervalStore();
            IntervalTree tree = new IntervalTree();
            for (Interval iv : intervals) {
                naive.insert(iv);
                tree.insert(iv);
            }

            long naiveTotal = 0;
            long treeTotal = 0;
            TestSupport.Dice dice = new TestSupport.Dice(4711L);
            for (int q = 0; q < 200; q++) {
                Interval query = dice.interval(SPAN, 100);
                naive.findAny(query);
                naiveTotal += naive.visitedNodes();
                tree.findAny(query);
                treeTotal += tree.visitedNodes();
            }

            assertTrue(treeTotal * 20 < naiveTotal,
                    "트리가 " + treeTotal + " 번, 전수가 " + naiveTotal + " 번 봤다");
            assertTrue(treeTotal < 200L * tree.height(),
                    "findAny 는 질의마다 한 갈래다. 합이 200 * 높이(" + tree.height() + ") 를 넘으면 안 된다");
        }

        @Test
        @DisplayName("경계에서 딱 멈춘다. 등호 하나가 여기서 갈린다")
        void stopsExactlyAtTheBoundary() {
            // 무작위 데이터로는 이 자리가 안 걸린다. 좌표가 정확히 같아야 드러난다.
            // 부등호를 >= 로 바꾸면 답은 그대로고 방문 수만 는다. 그래서 여기서만 잡힌다.
            IntervalTree left = new IntervalTree();
            left.insert(Interval.of(50, 60));
            left.insert(Interval.of(10, 20));
            assertEquals(20, left.root.left.maxEnd);

            Interval q = Interval.of(20, 30);
            assertEquals(List.of(), left.findAll(q), "[10,20) 은 [20,30) 과 안 겹친다");
            assertEquals(1, left.visitedNodes(),
                    "왼쪽의 maxEnd 가 질의 시작과 같으면 왼쪽에는 답이 없다. 안 내려가야 한다");
            assertNull(left.findAny(q));
            assertEquals(1, left.visitedNodes(), "findAny 도 같은 조건이다");

            IntervalTree right = new IntervalTree();
            right.insert(Interval.of(30, 40));
            right.insert(Interval.of(50, 60));

            Interval q2 = Interval.of(10, 30);
            assertEquals(List.of(), right.findAll(q2), "[30,40) 은 [10,30) 과 안 겹친다");
            assertEquals(1, right.visitedNodes(),
                    "노드의 시작이 질의 끝과 같으면 오른쪽은 전부 그 뒤다. 안 내려가야 한다");
        }
    }

    @Nested
    @DisplayName("측정 2: 고칠 수 없는 성질")
    class ItsLimits {

        @Test
        @DisplayName("한계 1 - 정렬된 순서로 넣으면 연결 리스트가 된다")
        void sortedInsertDegenerates() {
            // 06번과 같다. 버그가 아니라 이 구조의 성질이고, 올바른 구현에서도 그대로 나온다.
            // 균형 잡힌 트리라면 1000개의 높이가 10 근처여야 한다.
            IntervalTree ordered = new IntervalTree();
            for (int i = 0; i < 1_000; i++) ordered.insert(Interval.of(i * 10, i * 10 + 5));
            assertEquals(1_000, ordered.height(),
                    "정렬 입력에서는 높이가 원소 수와 같아진다. 사실상 연결 리스트다");

            IntervalTree shuffled = new IntervalTree();
            for (int i = 0; i < 1_000; i++) {
                long k = (i * 617L) % 1_000;
                shuffled.insert(Interval.of(k * 10, k * 10 + 5));
            }
            assertTrue(shuffled.height() < 50,
                    "흩어진 입력에서는 높이가 " + shuffled.height() + " 로 훨씬 낮다");
            assertEquals(ordered.toList(), shuffled.toList(), "담긴 것은 같다. 모양만 다르다");

            Interval query = Interval.of(5_000, 5_050);
            assertEquals(ordered.findAll(query).size(), shuffled.findAll(query).size(),
                    "답은 같다. 무너지는 것은 답이 아니라 걸음 수다");
            ordered.findAll(query);
            long orderedVisits = ordered.visitedNodes();
            shuffled.findAll(query);
            long shuffledVisits = shuffled.visitedNodes();
            assertTrue(orderedVisits > shuffledVisits * 20,
                    "같은 질의에 정렬 트리는 " + orderedVisits + " 번, 흩어진 트리는 "
                            + shuffledVisits + " 번 본다");
        }

        @Test
        @DisplayName("한계 2 - 결과가 크면 진다. O(log n + k) 의 k 가 n 이다")
        void wideAnswersDefeatPruning() {
            // 전부 한 점을 덮으면 가지치기가 한 번도 발동하지 않는다.
            // 25번 KD-트리에서 "모든 점이 같은 거리에 있으면 한 개도 못 건너뛴다"와 같은 자리다.
            IntervalTree tree = new IntervalTree();
            NaiveIntervalStore naive = new NaiveIntervalStore();
            TestSupport.Dice dice = new TestSupport.Dice(4040L);
            for (int i = 0; i < 1_000; i++) {
                long s = 500_000 - 1 - dice.next(400_000);
                Interval iv = new Interval(s, 500_000 + 1 + dice.next(400_000));
                tree.insert(iv);
                naive.insert(iv);
            }
            Interval query = Interval.of(500_000, 500_001);

            assertEquals(tree.size(), tree.findAll(query).size(), "전부가 답이다");
            assertEquals(tree.size(), tree.visitedNodes(),
                    "노드를 하나도 안 건너뛴다. 답이 전부라서 건너뛸 것이 없다");
            naive.findAll(query);
            assertEquals(naive.visitedNodes(), tree.visitedNodes(),
                    "전수 조사와 정확히 같은 걸음 수다. 이길 방법이 없다");
        }

        @Test
        @DisplayName("한계 3 - findAny 는 한 갈래인데 그 갈래가 n 일 수 있다")
        void degenerateTreeMakesEvenFindAnyLinear() {
            IntervalTree ordered = new IntervalTree();
            for (int i = 0; i < 500; i++) ordered.insert(Interval.of(i * 10, i * 10 + 5));
            ordered.findAny(Interval.of(4_995, 4_996));
            assertTrue(ordered.visitedNodes() > 400,
                    "한 갈래라도 높이가 n 이면 O(n) 이다. 방문 " + ordered.visitedNodes());
            assertEquals(500, ordered.height());
        }
    }
}
