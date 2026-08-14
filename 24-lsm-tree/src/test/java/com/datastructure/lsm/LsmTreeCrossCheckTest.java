package com.datastructure.lsm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * TreeMap 과의 무작위 대조. 이 문제집에서 가장 많은 버그를 잡는 방식이다.
 *
 * 여기서 잡히는 것은 대부분 층 순서 버그다. 층이 하나뿐일 때는 어떤 구현이든 맞고,
 * flush 와 compact 가 무작위로 끼어들어 층이 여러 개가 된 뒤에야 갈린다.
 * 그래서 대조 중간에 flush 와 compact 를 섞는다.
 */
@DisplayName("LsmTree 무작위 대조")
class LsmTreeCrossCheckTest {

    @Test
    @DisplayName("TreeMap 과 2만 스텝을 대조한다")
    void matchesTreeMap() {
        // 임계치를 바꿔 가며 돈다. 1 이면 쓸 때마다 새 층이 생겨 층이 수십 개가 되고,
        // 128 이면 대부분이 memtable 에 머문다. 두 극단에서 답이 같아야 한다.
        for (int threshold : new int[]{1, 3, 16, 128}) {
            Random rnd = new Random(20260814L + threshold);
            LsmTree<Integer, String> t = new LsmTree<>(threshold);
            TreeMap<Integer, String> ref = new TreeMap<>();

            for (int step = 0; step < 20_000; step++) {
                int key = rnd.nextInt(300);
                int op = rnd.nextInt(100);
                String where = "임계 " + threshold + " step " + step;
                if (op < 45) {
                    String v = "v" + step;
                    ref.put(key, v);
                    t.put(key, v);
                } else if (op < 68) {
                    ref.remove(key);
                    t.delete(key);
                } else if (op < 90) {
                    assertEquals(ref.get(key), t.get(key), where + " get " + key);
                } else if (op < 95) {
                    int a = rnd.nextInt(300);
                    int b = rnd.nextInt(300);
                    int from = Math.min(a, b);
                    int to = Math.max(a, b);
                    assertEquals(new ArrayList<>(ref.subMap(from, true, to, true).entrySet()),
                            t.rangeScan(from, to), where + " rangeScan " + from + ".." + to);
                } else if (op < 98) {
                    t.flush();
                } else {
                    t.compact();
                }
            }
            assertEquals(new ArrayList<>(ref.keySet()), t.keys(), "임계 " + threshold);
            assertEquals(ref.size(), t.size(), "임계 " + threshold);
            assertEquals(new ArrayList<>(ref.entrySet()), t.rangeScan(0, 299), "임계 " + threshold);

            t.compact();
            assertEquals(new ArrayList<>(ref.entrySet()), t.rangeScan(0, 299),
                    "임계 " + threshold + ": compact 가 답을 바꾸면 안 된다");
            assertEquals(ref.size(), t.storedEntryCount(),
                    "임계 " + threshold + ": 다 합치면 산 것만 남는다");
        }
    }

    @Test
    @DisplayName("문자열 키로도 같다")
    void stringKeys() {
        Random rnd = new Random(777L);
        LsmTree<String, Integer> t = new LsmTree<>(8);
        TreeMap<String, Integer> ref = new TreeMap<>();
        for (int step = 0; step < 20_000; step++) {
            String key = "k" + rnd.nextInt(400);
            int op = rnd.nextInt(10);
            if (op < 5) {
                ref.put(key, step);
                t.put(key, step);
            } else if (op < 7) {
                ref.remove(key);
                t.delete(key);
            } else if (op < 9) {
                assertEquals(ref.get(key), t.get(key), "step " + step);
            } else if (op == 9 && step % 500 == 0) {
                t.compact();
            }
        }
        assertEquals(new ArrayList<>(ref.keySet()), t.keys());
        for (Map.Entry<String, Integer> e : ref.entrySet()) {
            assertEquals(e.getValue(), t.get(e.getKey()), "키 " + e.getKey());
        }
    }

    @Test
    @Timeout(25)
    @DisplayName("20만 개를 쓰고 읽는다")
    void twoHundredThousand() {
        LsmTree<Integer, String> t = new LsmTree<>(4096);
        for (int i = 0; i < 200_000; i++) {
            t.put(i, "v");
        }
        assertEquals(200_000 / 4096, t.sstableCount());
        for (int i = 0; i < 200_000; i += 7) {
            assertEquals("v", t.get(i), "키 " + i);
        }
        t.compact();
        assertEquals(1, t.sstableCount());
        assertEquals(200_000, t.size());
        assertEquals(1.0, t.spaceAmplification(), 1e-9);
    }
}
