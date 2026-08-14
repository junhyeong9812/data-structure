package com.datastructure.rope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.datastructure.rope.CharSequenceStore.Split;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 두 구현이 지켜야 하는 계약. StringBuilderStore 와 Rope 가 이 한 벌을 같이 통과해야 한다.
 *
 * 여기에는 비용 이야기가 없다. 답이 같은지만 본다.
 * 비용은 CopyCostTest 가 따로 잰다. 두 관심사를 섞으면 "느린데 맞는" 구현과
 * "빠른데 틀린" 구현을 구별하지 못한다.
 */
abstract class CharSequenceStoreContractTest {

    /** 구현마다 이 하나만 다르다. */
    abstract CharSequenceStore of(String text);

    @Nested
    @DisplayName("기본")
    class Basics {

        @Test
        @DisplayName("담은 것이 그대로 나온다")
        void roundTrip() {
            for (String s : new String[]{"", "a", "ab", TestText.of(31), TestText.of(32),
                    TestText.of(33), TestText.of(1000)}) {
                assertEquals(s, of(s).toString(), "길이 " + s.length());
                assertEquals(s.length(), of(s).length());
            }
        }

        @Test
        @DisplayName("charAt 이 String 과 같다")
        void charAtMatchesString() {
            String s = TestText.of(500);
            CharSequenceStore store = of(s);
            for (int i = 0; i < s.length(); i++) {
                assertEquals(s.charAt(i), store.charAt(i), "index " + i);
            }
        }

        @Test
        @DisplayName("substring 이 String 과 같다")
        void substringMatchesString() {
            String s = TestText.of(200);
            CharSequenceStore store = of(s);
            Random rnd = new Random(28L);
            for (int t = 0; t < 500; t++) {
                int a = rnd.nextInt(s.length() + 1);
                int b = rnd.nextInt(s.length() + 1);
                int from = Math.min(a, b);
                int to = Math.max(a, b);
                assertEquals(s.substring(from, to), store.substring(from, to),
                        "[" + from + ", " + to + ")");
            }
            assertEquals("", store.substring(7, 7), "빈 구간은 빈 문자열이다");
            assertEquals(s, store.substring(0, s.length()));
        }
    }

    @Nested
    @DisplayName("경계")
    class Boundaries {

        @Test
        @DisplayName("범위 밖 charAt")
        void charAtOutOfRange() {
            CharSequenceStore store = of("abc");
            assertThrows(IndexOutOfBoundsException.class, () -> store.charAt(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> store.charAt(3));
            assertThrows(IndexOutOfBoundsException.class, () -> of("").charAt(0));
        }

        @Test
        @DisplayName("범위 밖 substring 과 delete")
        void rangeOutOfRange() {
            CharSequenceStore store = of("abcde");
            assertThrows(IndexOutOfBoundsException.class, () -> store.substring(-1, 2));
            assertThrows(IndexOutOfBoundsException.class, () -> store.substring(2, 6));
            assertThrows(IndexOutOfBoundsException.class, () -> store.substring(3, 2));
            assertThrows(IndexOutOfBoundsException.class, () -> store.delete(-1, 2));
            assertThrows(IndexOutOfBoundsException.class, () -> store.delete(2, 6));
            assertThrows(IndexOutOfBoundsException.class, () -> store.delete(3, 2));
        }

        @Test
        @DisplayName("범위 밖 insert 와 split")
        void indexOutOfRange() {
            CharSequenceStore store = of("abcde");
            assertThrows(IndexOutOfBoundsException.class, () -> store.insert(-1, "x"));
            assertThrows(IndexOutOfBoundsException.class, () -> store.insert(6, "x"));
            assertThrows(IndexOutOfBoundsException.class, () -> store.split(-1));
            assertThrows(IndexOutOfBoundsException.class, () -> store.split(6));
        }

        @Test
        @DisplayName("null 은 거부한다")
        void nullArguments() {
            CharSequenceStore store = of("abc");
            assertThrows(IllegalArgumentException.class, () -> store.insert(0, null));
            assertThrows(IllegalArgumentException.class, () -> store.concat(null));
        }

        @Test
        @DisplayName("빈 문자열 삽입과 빈 구간 삭제는 아무 일도 안 한다")
        void emptyEdits() {
            String s = TestText.of(70);
            CharSequenceStore store = of(s);
            assertEquals(s, store.insert(0, "").toString());
            assertEquals(s, store.insert(s.length(), "").toString());
            assertEquals(s, store.insert(35, "").toString());
            assertEquals(s, store.delete(0, 0).toString());
            assertEquals(s, store.delete(s.length(), s.length()).toString());
            assertEquals(0, store.insert(35, "").charsCopiedByLastOp(),
                    "할 일이 없으면 한 글자도 옮기지 않는다");
            assertEquals(0, store.delete(9, 9).charsCopiedByLastOp());
        }

        @Test
        @DisplayName("빈 저장소에도 넣을 수 있다")
        void editEmptyStore() {
            CharSequenceStore empty = of("");
            assertEquals("hello", empty.insert(0, "hello").toString());
            assertEquals("", empty.delete(0, 0).toString());
            assertEquals("", empty.substring(0, 0));
            assertEquals(0, empty.split(0).left().length());
            assertEquals(0, empty.split(0).right().length());
            assertEquals("hi", empty.concat(of("hi")).toString());
            assertEquals("hi", of("hi").concat(empty).toString());
        }
    }

    @Nested
    @DisplayName("이어붙이기")
    class Concat {

        @Test
        @DisplayName("양쪽 내용이 순서대로 이어진다")
        void concatenates() {
            String a = TestText.of(40);
            String b = TestText.of(37);
            assertEquals(a + b, of(a).concat(of(b)).toString());
            assertEquals(a + a, of(a).concat(of(a)).toString());
            assertEquals(a, of(a).concat(of("")).toString());
            assertEquals(a, of("").concat(of(a)).toString());
        }

        @Test
        @DisplayName("이어붙여도 원본이 그대로다")
        void leavesOperandsAlone() {
            CharSequenceStore a = of("왼쪽");
            CharSequenceStore b = of("오른쪽");
            a.concat(b);
            assertEquals("왼쪽", a.toString());
            assertEquals("오른쪽", b.toString());
        }

        @Test
        @DisplayName("세 번 이어붙여도 자리가 맞는다")
        void chained() {
            String a = TestText.of(5);
            String b = TestText.of(60);
            String c = TestText.of(3);
            CharSequenceStore joined = of(a).concat(of(b)).concat(of(c));
            assertEquals(a + b + c, joined.toString());
            for (int i = 0; i < joined.length(); i++) {
                assertEquals((a + b + c).charAt(i), joined.charAt(i), "index " + i);
            }
        }
    }

    @Nested
    @DisplayName("삽입과 삭제")
    class InsertDelete {

        @Test
        @DisplayName("어느 자리에 넣어도 String 과 같다")
        void insertEverywhere() {
            String s = TestText.of(100);
            CharSequenceStore store = of(s);
            for (int i = 0; i <= s.length(); i++) {
                String expected = s.substring(0, i) + "XY" + s.substring(i);
                assertEquals(expected, store.insert(i, "XY").toString(), "index " + i);
            }
        }

        @Test
        @DisplayName("어느 구간을 지워도 String 과 같다")
        void deleteEverywhere() {
            String s = TestText.of(80);
            CharSequenceStore store = of(s);
            for (int from = 0; from <= s.length(); from++) {
                for (int to = from; to <= s.length(); to += 7) {
                    String expected = s.substring(0, from) + s.substring(to);
                    assertEquals(expected, store.delete(from, to).toString(),
                            "[" + from + ", " + to + ")");
                }
            }
        }

        @Test
        @DisplayName("편집해도 원본이 그대로다")
        void doesNotTouchTheOriginal() {
            String s = TestText.of(90);
            CharSequenceStore store = of(s);
            store.insert(45, "끼워넣기");
            store.delete(10, 20);
            store.split(33);
            assertEquals(s, store.toString(), "편집은 새 저장소를 돌려준다. 원본은 그대로다");
            assertEquals(s.length(), store.length());
        }

        @Test
        @DisplayName("긴 문자열을 잎 크기보다 크게 넣어도 된다")
        void insertLongerThanLeaf() {
            String s = TestText.of(50);
            String big = TestText.of(500);
            assertEquals(s.substring(0, 25) + big + s.substring(25),
                    of(s).insert(25, big).toString());
        }
    }

    @Nested
    @DisplayName("쪼개기")
    class Splitting {

        @Test
        @DisplayName("어느 자리에서 쪼개도 두 조각이 원본이 된다")
        void splitEverywhere() {
            String s = TestText.of(70);
            CharSequenceStore store = of(s);
            for (int i = 0; i <= s.length(); i++) {
                Split parts = store.split(i);
                assertEquals(s.substring(0, i), parts.left().toString(), "index " + i);
                assertEquals(s.substring(i), parts.right().toString(), "index " + i);
                assertEquals(s, parts.left().concat(parts.right()).toString(), "index " + i);
            }
        }

        @Test
        @DisplayName("양 끝에서 쪼개면 한쪽이 빈다")
        void splitAtEnds() {
            String s = TestText.of(33);
            Split atZero = of(s).split(0);
            assertEquals("", atZero.left().toString());
            assertEquals(s, atZero.right().toString());
            Split atEnd = of(s).split(s.length());
            assertEquals(s, atEnd.left().toString());
            assertEquals("", atEnd.right().toString());
        }

        @Test
        @DisplayName("쪼갠 조각을 또 편집할 수 있다")
        void splitPartsAreUsable() {
            String s = TestText.of(120);
            Split parts = of(s).split(60);
            assertEquals(s.substring(0, 60) + "!", parts.left().insert(60, "!").toString());
            assertEquals(s.substring(61), parts.right().delete(0, 1).toString());
            assertEquals(s.charAt(59), parts.left().charAt(59));
            assertEquals(s.charAt(60), parts.right().charAt(0));
        }
    }

    @Nested
    @DisplayName("비용 계기의 계약")
    class CostMeter {

        @Test
        @DisplayName("처음 만든 저장소는 0 에서 출발한다")
        void startsAtZero() {
            CharSequenceStore store = of(TestText.of(1000));
            assertEquals(0, store.charsCopiedByLastOp(), "적재 비용은 안 센다");
            assertEquals(0, store.charsCopiedTotal());
        }

        @Test
        @DisplayName("누적은 왼쪽 계보를 따라 더해진다")
        void totalAccumulates() {
            CharSequenceStore store = of(TestText.of(200));
            long running = 0;
            for (int i = 0; i < 20; i++) {
                store = store.insert(i * 3, "z");
                running += store.charsCopiedByLastOp();
                assertEquals(running, store.charsCopiedTotal(), "step " + i);
            }
        }

        @Test
        @DisplayName("쪼갠 두 조각이 같은 비용을 들고 나간다")
        void bothHalvesReportTheSameCost() {
            CharSequenceStore store = of(TestText.of(100));
            Split parts = store.split(37);
            assertEquals(parts.left().charsCopiedByLastOp(), parts.right().charsCopiedByLastOp(),
                    "한 번의 연산이 만든 둘이다. 계보가 같으면 값도 같다");
            assertEquals(parts.left().charsCopiedTotal(), parts.right().charsCopiedTotal());
        }

        @Test
        @DisplayName("계기를 읽어도 아무것도 안 바뀐다")
        void readingTheMeterChangesNothing() {
            String s = TestText.of(64);
            CharSequenceStore store = of(s).insert(32, "!");
            long first = store.charsCopiedByLastOp();
            assertEquals(first, store.charsCopiedByLastOp(), "두 번 읽어도 같은 값이다");
            assertEquals(first, store.charsCopiedByLastOp());
            assertEquals(s.substring(0, 32) + "!" + s.substring(32), store.toString());
        }
    }
}
