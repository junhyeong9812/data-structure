package com.datastructure.bitset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/** BitVector 계약. 세 구현이 똑같이 지켜야 하는 것만 여기 있다. */
abstract class BitVectorContractTest {

    protected abstract BitVector create(int size);

    protected BitVector of(int size, int... bits) {
        BitVector v = create(size);
        for (int b : bits) {
            v.set(b);
        }
        return v;
    }

    @Nested
    @DisplayName("빈 비트셋")
    class Empty {

        @Test
        @DisplayName("전부 꺼져 있다")
        void allClear() {
            BitVector v = create(100);
            assertEquals(100, v.size());
            assertEquals(0, v.cardinality());
            assertTrue(v.isEmpty());
            assertEquals(-1, v.nextSetBit(0));
            assertEquals(List.of(), v.toList());
            for (int i = 0; i < 100; i++) {
                assertFalse(v.get(i), i + "번이 켜져 있다");
            }
        }
    }

    @Nested
    @DisplayName("한 비트씩")
    class SingleBits {

        @Test
        @DisplayName("켜고 끄고 뒤집는다")
        void setClearFlip() {
            BitVector v = create(100);
            v.set(42);
            assertTrue(v.get(42));
            assertEquals(1, v.cardinality());
            assertFalse(v.isEmpty());

            v.set(42);
            assertEquals(1, v.cardinality(), "이미 켜진 것을 또 켜도 하나다");

            v.clear(42);
            assertFalse(v.get(42));
            assertEquals(0, v.cardinality());

            v.flip(42);
            assertTrue(v.get(42));
            v.flip(42);
            assertFalse(v.get(42));
        }

        @Test
        @DisplayName("set(index, value)")
        void setWithValue() {
            BitVector v = create(10);
            v.set(3, true);
            assertTrue(v.get(3));
            v.set(3, false);
            assertFalse(v.get(3));
        }

        @Test
        @DisplayName("워드 경계를 넘는 자리")
        void wordBoundaries() {
            // 63 -> 64 에서 워드가 바뀐다. >>> 6 과 & 63 을 잘못 쓰면 여기서 엉뚱한 워드를 건드린다.
            BitVector v = create(200);
            for (int b : new int[]{0, 63, 64, 65, 127, 128, 191, 199}) {
                v.set(b);
            }
            for (int b : new int[]{0, 63, 64, 65, 127, 128, 191, 199}) {
                assertTrue(v.get(b), b + "번이 안 켜졌다");
            }
            assertEquals(8, v.cardinality());
            assertFalse(v.get(62));
            assertFalse(v.get(66));
        }

        @Test
        @DisplayName("마지막 워드의 남는 자리는 셈에 안 들어간다")
        void tailBitsAreNotCounted() {
            // 크기 100 이면 워드가 2개(128비트)다. 남는 28비트가 켜지면 안 된다.
            BitVector v = create(100);
            for (int i = 0; i < 100; i++) {
                v.set(i);
            }
            assertEquals(100, v.cardinality(), "크기보다 많이 세면 꼬리 비트가 켜진 것이다");
            assertEquals(99, v.nextSetBit(99), "99가 마지막이다");
            assertEquals(100, v.toList().size());
        }

        @Test
        @DisplayName("clearAll")
        void clearAll() {
            BitVector v = of(100, 1, 50, 99);
            v.clearAll();
            assertEquals(0, v.cardinality());
            assertTrue(v.isEmpty());
            v.set(7);
            assertEquals(List.of(7), v.toList());
        }

        @Test
        @DisplayName("flipAll 은 여집합이다")
        void flipAllIsComplement() {
            BitVector v = of(10, 1, 3, 5);
            v.flipAll();
            assertEquals(List.of(0, 2, 4, 6, 7, 8, 9), v.toList());
            assertEquals(7, v.cardinality());
            v.flipAll();
            assertEquals(List.of(1, 3, 5), v.toList(), "두 번 뒤집으면 제자리다");
        }

        @Test
        @DisplayName("flipAll 이 크기를 넘는 자리를 켜면 안 된다")
        void flipAllKeepsTailClean() {
            // **워드를 통째로 뒤집으면 남는 자리까지 켜진다.**
            // 크기 70 이면 워드 2개(128비트)라 58비트가 남는다.
            BitVector v = create(70);
            v.flipAll();
            assertEquals(70, v.cardinality(), "70 보다 크면 꼬리가 켜진 것이다");
            assertEquals(69, v.toList().get(v.toList().size() - 1));
            assertEquals(-1, v.nextSetBit(70) == -1 ? -1 : 0, "크기 밖을 반환하면 안 된다");
        }

        @Test
        @DisplayName("크기가 64의 배수일 때도 맞다")
        void flipAllOnWordMultiple() {
            // 남는 자리가 **없을 때** 꼬리 정리를 잘못하면 마지막 워드를 통째로 지운다.
            for (int size : new int[]{64, 128, 192}) {
                BitVector v = create(size);
                v.flipAll();
                assertEquals(size, v.cardinality(), "크기 " + size + " 에서 비트가 사라졌다");
                assertTrue(v.get(size - 1), "크기 " + size + " 의 마지막 비트가 꺼졌다");
            }
        }

        @Test
        @DisplayName("범위 밖")
        void outOfRange() {
            BitVector v = create(10);
            assertThrows(IndexOutOfBoundsException.class, () -> v.get(10));
            assertThrows(IndexOutOfBoundsException.class, () -> v.get(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> v.set(10));
            assertThrows(IndexOutOfBoundsException.class, () -> v.clear(-1));
            assertThrows(IllegalArgumentException.class, () -> create(0));
        }
    }

    @Nested
    @DisplayName("nextSetBit")
    class NextSetBit {

        @Test
        @DisplayName("켜진 것만 순회한다")
        void iteratesSetBits() {
            BitVector v = of(1000, 5, 100, 101, 500, 999);
            assertEquals(5, v.nextSetBit(0));
            assertEquals(5, v.nextSetBit(5));
            assertEquals(100, v.nextSetBit(6));
            assertEquals(101, v.nextSetBit(101));
            assertEquals(500, v.nextSetBit(102));
            assertEquals(999, v.nextSetBit(501));
            assertEquals(999, v.nextSetBit(999));
        }

        @Test
        @DisplayName("끝을 넘으면 -1")
        void pastEnd() {
            BitVector v = of(100, 10);
            assertEquals(-1, v.nextSetBit(11));
            assertEquals(-1, v.nextSetBit(100), "크기와 같으면 -1");
            assertEquals(-1, v.nextSetBit(1000));
        }

        @Test
        @DisplayName("음수는 예외")
        void negativeThrows() {
            BitVector v = create(10);
            assertThrows(IndexOutOfBoundsException.class, () -> v.nextSetBit(-1));
        }

        @Test
        @DisplayName("toList 가 오름차순이다")
        void toListIsSorted() {
            BitVector v = of(500, 400, 1, 200, 0, 499);
            assertEquals(List.of(0, 1, 200, 400, 499), v.toList());
            assertEquals(5, v.cardinality());
        }
    }

    @Nested
    @DisplayName("집합 연산")
    class SetOperations {

        @Test
        @DisplayName("교집합")
        void and() {
            BitVector a = of(100, 1, 2, 3, 70);
            BitVector b = of(100, 2, 3, 4, 71);
            a.and(b);
            assertEquals(List.of(2, 3), a.toList());
        }

        @Test
        @DisplayName("합집합")
        void or() {
            BitVector a = of(100, 1, 2, 70);
            BitVector b = of(100, 2, 3, 71);
            a.or(b);
            assertEquals(List.of(1, 2, 3, 70, 71), a.toList());
        }

        @Test
        @DisplayName("대칭차집합")
        void xor() {
            BitVector a = of(100, 1, 2, 3, 70);
            BitVector b = of(100, 2, 3, 4, 71);
            a.xor(b);
            assertEquals(List.of(1, 4, 70, 71), a.toList());
        }

        @Test
        @DisplayName("차집합")
        void andNot() {
            BitVector a = of(100, 1, 2, 3, 70);
            BitVector b = of(100, 2, 3, 4, 71);
            a.andNot(b);
            assertEquals(List.of(1, 70), a.toList());
        }

        @Test
        @DisplayName("자기 자신과의 연산")
        void withItself() {
            BitVector a = of(100, 1, 50, 99);
            BitVector copy = of(100, 1, 50, 99);
            a.and(copy);
            assertEquals(List.of(1, 50, 99), a.toList());
            a.xor(copy);
            assertEquals(List.of(), a.toList(), "같은 것끼리 xor 하면 빈다");
        }

        @Test
        @DisplayName("크기가 다르면 거부한다")
        void sizeMismatch() {
            BitVector a = create(100);
            BitVector b = create(200);
            assertThrows(IllegalArgumentException.class, () -> a.and(b));
            assertThrows(IllegalArgumentException.class, () -> a.or(b));
            assertThrows(IllegalArgumentException.class, () -> a.and(null));
        }

        @Test
        @DisplayName("합집합 뒤에도 꼬리 비트가 안 켜진다")
        void orKeepsTailClean() {
            // 크기 70 이면 워드 2개인데 58비트가 남는다.
            // 워드 단위로 or 하면 남는 자리까지 함께 켜질 수 있다.
            BitVector a = create(70);
            BitVector b = create(70);
            for (int i = 0; i < 70; i++) {
                a.set(i);
                b.set(i);
            }
            a.or(b);
            assertEquals(70, a.cardinality(), "70 보다 크면 꼬리가 켜진 것이다");
            assertEquals(69, a.toList().get(a.toList().size() - 1));
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class CrossCheck {

        @Test
        @DisplayName("TreeSet 과 계속 같다")
        void matchesTreeSet() {
            Random rnd = new Random(20260814L);
            int n = 500;
            BitVector v = create(n);
            TreeSet<Integer> ref = new TreeSet<>();

            for (int step = 0; step < 5000; step++) {
                int bit = rnd.nextInt(n);
                int op = rnd.nextInt(4);
                if (op == 0) {
                    v.set(bit);
                    ref.add(bit);
                } else if (op == 1) {
                    v.clear(bit);
                    ref.remove(bit);
                } else if (op == 2) {
                    v.flip(bit);
                    if (!ref.add(bit)) {
                        ref.remove(bit);
                    }
                } else {
                    assertEquals(ref.contains(bit), v.get(bit), "get step " + step);
                }
                assertEquals(ref.size(), v.cardinality(), "개수가 갈렸다 (step " + step + ")");
            }
            assertEquals(new ArrayList<>(ref), v.toList());
        }

        @Test
        @DisplayName("집합 연산도 TreeSet 과 같다")
        void setOpsMatchTreeSet() {
            Random rnd = new Random(999L);
            int n = 300;
            for (int trial = 0; trial < 40; trial++) {
                TreeSet<Integer> ra = new TreeSet<>();
                TreeSet<Integer> rb = new TreeSet<>();
                BitVector a = create(n);
                BitVector b = create(n);
                for (int i = 0; i < 80; i++) {
                    int x = rnd.nextInt(n);
                    int y = rnd.nextInt(n);
                    a.set(x);
                    ra.add(x);
                    b.set(y);
                    rb.add(y);
                }
                switch (trial % 4) {
                    case 0 -> {
                        a.and(b);
                        ra.retainAll(rb);
                    }
                    case 1 -> {
                        a.or(b);
                        ra.addAll(rb);
                    }
                    case 2 -> {
                        a.andNot(b);
                        ra.removeAll(rb);
                    }
                    default -> {
                        a.xor(b);
                        TreeSet<Integer> sym = new TreeSet<>(ra);
                        sym.addAll(rb);
                        TreeSet<Integer> both = new TreeSet<>(ra);
                        both.retainAll(rb);
                        sym.removeAll(both);
                        ra = sym;
                    }
                }
                assertEquals(new ArrayList<>(ra), a.toList(), "trial " + trial);
            }
        }
    }

    @Nested
    @DisplayName("성능")
    class Performance {

        @Test
        @Timeout(20)
        @DisplayName("100만 비트를 다룬다")
        void millionBits() {
            BitVector v = create(1_000_000);
            for (int i = 0; i < 1_000_000; i += 7) {
                v.set(i);
            }
            assertEquals(142_858, v.cardinality());
            int count = 0;
            for (int i = v.nextSetBit(0); i >= 0; i = i + 1 < 1_000_000 ? v.nextSetBit(i + 1) : -1) {
                count++;
            }
            assertEquals(142_858, count);
        }
    }
}
