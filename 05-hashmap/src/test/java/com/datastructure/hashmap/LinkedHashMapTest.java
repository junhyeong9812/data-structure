package com.datastructure.hashmap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LinkedHashMap 이 Map 계약을 지키는지 + **삽입 순서**라는 추가 계약.
 *
 * 부모(ChainingHashMap)의 계약 테스트를 그대로 물려받는다.
 * 상속으로 기능을 얹었으니 기존 계약이 깨지지 않았음을 확인해야 한다.
 */
class LinkedHashMapTest extends MapContractTest {

    @Override
    protected <K, V> Map<K, V> create() {
        return new LinkedHashMap<>();
    }

    private static <K> java.util.List<K> keyList(Map<K, ?> map) {
        java.util.List<K> out = new ArrayList<>();
        map.keys().forEach(out::add);
        return out;
    }

    @Test
    @DisplayName("넣은 순서대로 키가 나온다")
    void keepsInsertionOrder() {
        Map<String, Integer> map = create();
        map.put("c", 3);
        map.put("a", 1);
        map.put("b", 2);

        assertEquals(java.util.List.of("c", "a", "b"), keyList(map),
            "해시 순서가 아니라 넣은 순서여야 한다");
    }

    @Test
    @DisplayName("같은 키에 다시 넣어도 순서는 그대로다")
    void replaceDoesNotChangeOrder() {
        Map<String, Integer> map = create();
        map.put("a", 1);
        map.put("b", 2);
        map.put("a", 99);

        assertEquals(java.util.List.of("a", "b"), keyList(map),
            "값만 바뀐 것은 순서를 건드리지 않는다");
        assertEquals(99, map.get("a"));
    }

    @Test
    @DisplayName("지우면 순서에서도 빠진다")
    void removeDropsFromOrder() {
        Map<String, Integer> map = create();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);

        map.remove("b");
        assertEquals(java.util.List.of("a", "c"), keyList(map));

        map.remove("a");
        assertEquals(java.util.List.of("c"), keyList(map), "맨 앞을 지우는 경우");
    }

    @Test
    @DisplayName("지웠다 다시 넣으면 맨 뒤로 간다")
    void reinsertGoesToEnd() {
        Map<String, Integer> map = create();
        map.put("a", 1);
        map.put("b", 2);

        map.remove("a");
        map.put("a", 1);

        assertEquals(java.util.List.of("b", "a"), keyList(map));
    }

    @Test
    @DisplayName("리사이즈가 일어나도 순서가 유지된다")
    void keepsOrderAfterResize() {
        // 순서는 해시 배치와 무관해야 한다. 리사이즈가 순서를 흔들면 안 된다.
        Map<Integer, Integer> map = create();
        java.util.List<Integer> expected = new ArrayList<>();
        for (int i = 50; i >= 1; i--) {          // 일부러 내림차순으로 넣는다
            map.put(i, i);
            expected.add(i);
        }
        assertEquals(expected, keyList(map));
    }

    @Test
    @DisplayName("clear 후에는 순서도 비고 다시 쌓인다")
    void clearResetsOrder() {
        Map<String, Integer> map = create();
        map.put("a", 1);
        map.put("b", 2);

        map.clear();
        assertTrue(keyList(map).isEmpty());

        map.put("z", 9);
        map.put("y", 8);
        assertEquals(java.util.List.of("z", "y"), keyList(map));
    }
}
