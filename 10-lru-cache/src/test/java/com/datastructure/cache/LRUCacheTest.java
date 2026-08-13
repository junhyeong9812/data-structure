package com.datastructure.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("LRUCache: 해시맵 + 이중 연결 리스트")
class LRUCacheTest extends CacheContractTest {

    @Override
    protected Cache<Integer, String> create(int capacity) {
        return new LRUCache<>(capacity);
    }

    private LRUCache<Integer, String> lru(int capacity) {
        return new LRUCache<>(capacity);
    }

    /**
     * 줄이 건전한가. **앞으로 훑은 결과와 뒤로 훑은 결과가 서로의 역순이어야 한다.**
     *
     * 02번 DoublyLinkedList 의 assertSound 와 같다.
     * 이게 없으면 prev 링크를 제대로 안 고치는 구현도 대부분의 테스트를 통과한다.
     */
    private void assertSound(LRUCache<Integer, String> c) {
        List<Integer> forward = new ArrayList<>();
        for (LRUCache.Node<Integer, String> n = c.head.next; n != c.tail; n = n.next) {
            assertNotNull(n, "앞으로 훑다가 null 을 만났다. 줄이 끊겼다");
            forward.add(n.key);
            assertTrue(forward.size() <= c.size() + 1, "고리가 생겼다. 무한히 돈다");
        }
        List<Integer> backward = new ArrayList<>();
        for (LRUCache.Node<Integer, String> n = c.tail.prev; n != c.head; n = n.prev) {
            assertNotNull(n, "뒤로 훑다가 null 을 만났다. prev 링크가 안 이어져 있다");
            backward.add(n.key);
            assertTrue(backward.size() <= c.size() + 1, "뒤쪽에 고리가 생겼다");
        }
        List<Integer> reversed = new ArrayList<>(backward);
        java.util.Collections.reverse(reversed);
        assertEquals(forward, reversed, "앞으로 훑은 것과 뒤로 훑은 것이 서로의 역순이 아니다");
        assertEquals(c.size(), forward.size(), "맵의 크기와 줄의 길이가 다르다");
        assertEquals(forward, c.keysInOrder());
    }

    @Nested
    @DisplayName("줄의 건전성")
    class Soundness {

        @Test
        @DisplayName("연산마다 앞뒤 링크가 맞다")
        void linksStayConsistent() {
            LRUCache<Integer, String> c = lru(3);
            assertSound(c);
            c.put(1, "a");
            assertSound(c);
            c.put(2, "b");
            c.put(3, "c");
            assertSound(c);
            c.get(1);
            assertSound(c);
            c.put(4, "d");          // 축출
            assertSound(c);
            c.remove(3);
            assertSound(c);
            c.put(5, "e");
            c.put(6, "f");
            assertSound(c);
            c.clear();
            assertSound(c);
        }

        @Test
        @DisplayName("센티넬은 결과에 안 들어간다")
        void sentinelsAreInvisible() {
            LRUCache<Integer, String> c = lru(3);
            assertEquals(List.of(), c.keysInOrder());
            assertNull(c.head.key, "head 는 값을 담지 않는다");
            assertNull(c.tail.key, "tail 은 값을 담지 않는다");
            c.put(1, "a");
            assertEquals(List.of(1), c.keysInOrder());
            assertSame(c.head.next, c.tail.prev, "원소가 하나면 같은 노드여야 한다");
        }

        @Test
        @DisplayName("비었을 때 센티넬끼리 이어져 있다")
        void emptyLinksSentinels() {
            LRUCache<Integer, String> c = lru(3);
            assertSame(c.tail, c.head.next);
            assertSame(c.head, c.tail.prev);
            c.put(1, "a");
            c.remove(1);
            assertSame(c.tail, c.head.next, "다 지우면 처음 상태로 돌아와야 한다");
            assertSame(c.head, c.tail.prev);
        }
    }

    @Nested
    @DisplayName("떼어낸 노드")
    class DetachedNodes {

        @Test
        @DisplayName("밀려난 노드는 줄을 붙들고 있지 않는다")
        void evictedNodeIsCut() {
            LRUCache<Integer, String> c = lru(2);
            c.put(1, "a");
            LRUCache.Node<Integer, String> first = c.head.next;
            c.put(2, "b");
            c.put(3, "c");          // 1 이 밀려난다

            assertNull(first.prev, "떼어낸 노드가 앞을 붙들고 있으면 줄 전체가 GC 되지 않는다");
            assertNull(first.next);
            assertSound(c);
        }

        @Test
        @DisplayName("remove 한 노드도 마찬가지다")
        void removedNodeIsCut() {
            LRUCache<Integer, String> c = lru(3);
            c.put(1, "a");
            c.put(2, "b");
            LRUCache.Node<Integer, String> second = c.head.next.next;
            c.remove(2);
            assertNull(second.prev);
            assertNull(second.next);
            assertSound(c);
        }
    }

    @Nested
    @DisplayName("맵과 줄이 어긋나지 않는다")
    class MapAndListAgree {

        @Test
        @DisplayName("축출이 양쪽에서 일어난다")
        void evictionTouchesBoth() {
            // 맵에서만 지우고 줄에 남겨두면 keysInOrder 가 size 보다 길어진다.
            // 줄에서만 떼고 맵에 남겨두면 그 키가 영원히 살아 있게 된다.
            LRUCache<Integer, String> c = lru(3);
            for (int i = 0; i < 100; i++) {
                c.put(i, "v" + i);
                assertEquals(c.size(), c.keysInOrder().size(),
                        "맵 크기와 줄 길이가 어긋났다 (i=" + i + ")");
                assertSound(c);
            }
            assertEquals(3, c.size());
            assertEquals(List.of(97, 98, 99), c.keysInOrder());
        }
    }
}
