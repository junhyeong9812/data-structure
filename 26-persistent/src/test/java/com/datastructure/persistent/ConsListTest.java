package com.datastructure.persistent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ConsList: 계약과 구조 공유")
class ConsListTest {

    @Nested
    @DisplayName("빈 목록")
    class Empty {

        @Test
        @DisplayName("아무것도 없다")
        void nothing() {
            ConsList<Integer> list = ConsList.empty();
            assertEquals(0, list.size());
            assertTrue(list.isEmpty());
            assertEquals(List.of(), list.toList());
            assertThrows(NoSuchElementException.class, list::head);
            assertThrows(NoSuchElementException.class, list::tail);
            assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        }

        @Test
        @DisplayName("빈 목록은 하나뿐이다")
        void isSingleton() {
            assertSame(ConsList.empty(), ConsList.empty());
            assertSame(ConsList.empty(), ConsList.of());
            assertSame(ConsList.empty(), ConsList.<Integer>empty().reverse());
            // 빈 목록은 상태가 없으므로 여러 개 있을 이유가 없다.
            // 목록 끝은 언제나 이 하나의 객체다.
            assertSame(ConsList.empty(), ConsList.of(1, 2, 3).tail().tail().tail());
        }
    }

    @Nested
    @DisplayName("기본")
    class Basics {

        @Test
        @DisplayName("of 는 준 순서 그대로다")
        void ofKeepsOrder() {
            ConsList<Integer> list = ConsList.of(1, 2, 3);
            assertEquals(List.of(1, 2, 3), list.toList());
            assertEquals(3, list.size());
            assertEquals(1, list.head());
            assertEquals(List.of(2, 3), list.tail().toList());
        }

        @Test
        @DisplayName("prepend 는 앞에 붙는다")
        void prepend() {
            ConsList<String> list = ConsList.<String>empty().prepend("c").prepend("b").prepend("a");
            assertEquals(List.of("a", "b", "c"), list.toList());
            assertEquals("[a, b, c]", list.toString());
        }

        @Test
        @DisplayName("get 은 인덱스 순서다")
        void get() {
            ConsList<Integer> list = ConsList.of(10, 20, 30);
            assertEquals(10, list.get(0));
            assertEquals(20, list.get(1));
            assertEquals(30, list.get(2));
            assertThrows(IndexOutOfBoundsException.class, () -> list.get(3));
            assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        }

        @Test
        @DisplayName("reverse")
        void reverse() {
            assertEquals(List.of(3, 2, 1), ConsList.of(1, 2, 3).reverse().toList());
            assertEquals(List.of(1), ConsList.of(1).reverse().toList());
            assertEquals(List.of(1, 2, 3), ConsList.of(1, 2, 3).reverse().reverse().toList());
        }

        @Test
        @DisplayName("null 원소도 담는다")
        void nullElements() {
            ConsList<String> list = ConsList.of("a", null, "c");
            assertEquals(3, list.size());
            assertEquals(java.util.Arrays.asList("a", null, "c"), list.toList());
            assertEquals(java.util.Arrays.asList("c", null, "a"), list.reverse().toList());
        }

        @Test
        @DisplayName("값이 같으면 equals 다")
        void equality() {
            assertEquals(ConsList.of(1, 2, 3), ConsList.of(1, 2, 3));
            assertEquals(ConsList.of(1, 2, 3).hashCode(), ConsList.of(1, 2, 3).hashCode());
            assertFalse(ConsList.of(1, 2, 3).equals(ConsList.of(1, 2)));
            assertFalse(ConsList.of(1, 2, 3).equals(ConsList.of(1, 2, 4)));
        }
    }

    @Nested
    @DisplayName("옛 버전이 안 변한다")
    class OldVersionsSurvive {

        @Test
        @DisplayName("prepend 는 원본을 건드리지 않는다")
        void originalUntouched() {
            ConsList<Integer> a = ConsList.of(2, 3);
            ConsList<Integer> b = a.prepend(1);

            assertEquals(List.of(2, 3), a.toList(), "원본이 바뀌었다");
            assertEquals(2, a.size());
            assertEquals(List.of(1, 2, 3), b.toList());
            assertEquals(3, b.size());
        }

        @Test
        @DisplayName("100번을 붙여도 모든 중간 버전이 그대로다")
        void everyVersionSurvives() {
            List<ConsList<Integer>> versions = new ArrayList<>();
            ConsList<Integer> cur = ConsList.empty();
            versions.add(cur);
            for (int i = 0; i < 100; i++) {
                cur = cur.prepend(i);
                versions.add(cur);
            }
            // 02번 연결 리스트라면 여기서 versions 의 모든 원소가 같은 객체를 가리키고
            // 전부 마지막 상태를 보여줬을 것이다.
            for (int v = 0; v <= 100; v++) {
                ConsList<Integer> snapshot = versions.get(v);
                assertEquals(v, snapshot.size(), v + "번 버전의 크기");
                for (int i = 0; i < v; i++) {
                    assertEquals(v - 1 - i, snapshot.get(i), v + "번 버전의 " + i + "번째");
                }
            }
        }
    }

    @Nested
    @DisplayName("구조 공유")
    class StructuralSharing {

        @Test
        @DisplayName("새 목록의 꼬리는 옛 목록 자신이다")
        void tailIsTheSameObject() {
            ConsList<Integer> a = ConsList.of(2, 3);
            ConsList<Integer> b = a.prepend(1);

            assertSame(a, b.tail(), "꼬리를 복사했다. 그러면 prepend 가 O(n) 이다");
            // 이 한 줄이 이 자료구조의 전부다. 값이 아니라 객체가 같아야 한다.
        }

        @Test
        @DisplayName("1000번을 붙여도 매 단계가 직전 목록을 그대로 가리킨다")
        void everyPrependSharesEverything() {
            ConsList<Integer> base = ConsList.of(0);
            ConsList<Integer> cur = base;
            List<ConsList<Integer>> versions = new ArrayList<>();
            versions.add(cur);
            for (int i = 1; i <= 1000; i++) {
                ConsList<Integer> next = cur.prepend(i);
                assertSame(cur, next.tail(), i + "번째 prepend 가 꼬리를 복사했다");
                cur = next;
                versions.add(cur);
            }
            // 1001개 버전이 살아 있는데 노드는 1001개뿐이다. 버전마다 목록을 복사했다면
            // 1 + 2 + ... + 1001 = 501,501 개가 필요했을 것이다.
            ConsList<Integer> walk = cur;
            for (int i = 1000; i >= 0; i--) {
                assertSame(versions.get(i), walk, i + "번 버전이 사슬에서 벗어났다");
                if (i > 0) {
                    walk = walk.tail();
                }
            }
        }

        @Test
        @DisplayName("assertEquals 로는 이 계약을 검증할 수 없다")
        void equalsCannotSeeSharing() {
            ConsList<Integer> a = ConsList.of(2, 3);
            ConsList<Integer> shared = a.prepend(1);
            ConsList<Integer> copied = ConsList.of(1, 2, 3);

            // 값만 보면 둘이 같다.
            assertEquals(shared, copied);
            assertEquals(shared.toList(), copied.toList());

            // 그런데 하나는 꼬리를 공유하고 하나는 통째로 새로 만든 것이다.
            assertSame(a, shared.tail());
            assertNotSame(a, copied.tail());
            // 그래서 "복사하지 마라"는 계약은 assertSame 으로만 잡힌다.
            // 통째 복사 구현은 assertEquals 만 있는 테스트를 전부 통과한다.
        }

        @Test
        @DisplayName("reverse 는 아무것도 공유하지 못한다")
        void reverseSharesNothing() {
            ConsList<Integer> a = ConsList.of(1, 2, 3, 4, 5);
            ConsList<Integer> r = a.reverse();

            List<ConsList<Integer>> original = new ArrayList<>();
            for (ConsList<Integer> cur = a; !cur.isEmpty(); cur = cur.tail()) {
                original.add(cur);
            }
            for (ConsList<Integer> cur = r; !cur.isEmpty(); cur = cur.tail()) {
                for (ConsList<Integer> old : original) {
                    assertNotSame(old, cur, "뒤집힌 목록이 옛 노드를 재사용했다");
                }
            }
            // 방향이 반대라 재사용할 수 있는 꼬리가 없다. 노드 n 개를 전부 새로 만든다.
            // 불변성이 공짜로 주는 것은 prepend 뿐이고 reverse 는 제값을 낸다.
            assertEquals(List.of(1, 2, 3, 4, 5), a.toList(), "reverse 가 원본을 건드렸다");
        }
    }

    @Nested
    @DisplayName("무작위 대조")
    class CrossCheck {

        @Test
        @DisplayName("ArrayList 와 2000 스텝을 대조하고 모든 옛 버전을 다시 확인한다")
        void matchesArrayList() {
            Random rnd = new Random(20260814L);
            ConsList<Integer> list = ConsList.empty();
            List<Integer> ref = new ArrayList<>();

            List<ConsList<Integer>> snapshots = new ArrayList<>();
            List<List<Integer>> refSnapshots = new ArrayList<>();

            for (int step = 0; step < 2000; step++) {
                int op = rnd.nextInt(10);
                if (op < 6) {
                    int value = rnd.nextInt(100);
                    list = list.prepend(value);
                    ref.add(0, value);
                } else if (op < 8 && !ref.isEmpty()) {
                    list = list.tail();
                    ref.remove(0);
                } else {
                    list = list.reverse();
                    Collections.reverse(ref);
                }
                assertEquals(ref.size(), list.size(), "크기가 갈렸다 (step " + step + ")");
                assertEquals(ref, list.toList(), "내용이 갈렸다 (step " + step + ")");
                snapshots.add(list);
                refSnapshots.add(new ArrayList<>(ref));
            }

            // 여기가 영속성의 정의다. 2000 스텝이 끝난 뒤에도 모든 옛 버전이
            // 그 시점의 답을 그대로 준다.
            for (int i = 0; i < snapshots.size(); i++) {
                assertEquals(refSnapshots.get(i), snapshots.get(i).toList(), i + "번 스냅샷");
            }
        }
    }
}
