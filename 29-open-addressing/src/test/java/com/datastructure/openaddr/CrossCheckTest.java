package com.datastructure.openaddr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * java.util.HashMap 과의 무작위 대조. 이 문제집에서 버그를 가장 많이 잡는 테스트다.
 *
 * 개방 주소법의 사고는 조용하다. 예외가 나지 않고, 크기도 맞고, 방금 넣은 것도 찾아진다.
 * 다만 예전에 넣은 어떤 키 하나가 안 나온다. tombstone 을 잘못 다루거나
 * 뺏기 연쇄에서 들고 있던 항목을 흘리면 정확히 그렇게 된다.
 * 그런 누락은 넣기, 찾기, 지우기, 다시 넣기를 섞어 돌려보고 매 스텝 대조해야 잡힌다.
 *
 * 다섯 구현에 똑같은 연산을 똑같은 순서로 먹인다. 하나라도 다르면 그놈이 틀린 것이다.
 */
@DisplayName("HashMap 과의 무작위 대조")
class CrossCheckTest {

    private static List<ProbeMap<Integer, Integer>> fiveInt() {
        return List.of(
                new LinearProbeMap<>(),
                new QuadraticProbeMap<>(),
                new DoubleHashMap<>(),
                new RobinHoodMap<>(),
                new CuckooHashMap<>());
    }

    private static final List<String> NAMES = List.of("선형", "이차", "이중", "로빈후드", "쿠쿠");

    private static Set<Integer> keySet(ProbeMap<Integer, ?> map) {
        Set<Integer> set = new HashSet<>();
        map.keys().forEach(set::add);
        return set;
    }

    @Test
    @DisplayName("2만 스텝을 매 스텝 크기와 키 집합까지 대조한다")
    @Timeout(300)
    void twentyThousandSteps() {
        List<ProbeMap<Integer, Integer>> maps = fiveInt();
        HashMap<Integer, Integer> reference = new HashMap<>();
        Random random = new Random(31337L);

        for (int step = 0; step < 20_000; step++) {
            int key = random.nextInt(256);          // 좁은 영역이라 충돌과 재삽입이 잔뜩 난다
            int roll = random.nextInt(100);
            String what;
            if (roll < 50) {
                what = "put";
                Integer expected = reference.put(key, step);
                for (int i = 0; i < 5; i++) {
                    assertEquals(expected, maps.get(i).put(key, step),
                            NAMES.get(i) + " 의 put 반환값이 다르다. 스텝 " + step + " 키 " + key);
                }
            } else if (roll < 80) {
                what = "remove";
                Integer expected = reference.remove(key);
                for (int i = 0; i < 5; i++) {
                    assertEquals(expected, maps.get(i).remove(key),
                            NAMES.get(i) + " 의 remove 반환값이 다르다. 스텝 " + step + " 키 " + key);
                }
            } else {
                what = "get";
                Integer expected = reference.get(key);
                for (int i = 0; i < 5; i++) {
                    assertEquals(expected, maps.get(i).get(key),
                            NAMES.get(i) + " 의 get 이 다르다. 스텝 " + step + " 키 " + key);
                    assertEquals(reference.containsKey(key), maps.get(i).containsKey(key),
                            NAMES.get(i) + " 의 containsKey 가 다르다. 스텝 " + step + " 키 " + key);
                }
            }

            for (int i = 0; i < 5; i++) {
                assertEquals(reference.size(), maps.get(i).size(),
                        NAMES.get(i) + " 의 크기가 다르다. 스텝 " + step + " " + what);
                assertEquals(reference.keySet(), keySet(maps.get(i)),
                        NAMES.get(i) + " 의 키 집합이 다르다. 스텝 " + step + " " + what);
            }
        }

        for (int i = 0; i < 5; i++) {
            for (java.util.Map.Entry<Integer, Integer> entry : reference.entrySet()) {
                assertEquals(entry.getValue(), maps.get(i).get(entry.getKey()),
                        NAMES.get(i) + " 의 마지막 값 대조. 키 " + entry.getKey());
            }
        }
    }

    @Test
    @DisplayName("넓은 키 영역에서 2만 스텝 (리사이즈가 여러 번 일어난다)")
    @Timeout(300)
    void twentyThousandStepsWithGrowth() {
        List<ProbeMap<Integer, Integer>> maps = fiveInt();
        HashMap<Integer, Integer> reference = new HashMap<>();
        Random random = new Random(20250814L);

        for (int step = 0; step < 20_000; step++) {
            int key = random.nextInt(30_000) - 15_000;    // 음수 해시도 섞는다
            int roll = random.nextInt(100);
            if (roll < 65) {
                Integer expected = reference.put(key, step);
                for (int i = 0; i < 5; i++) {
                    assertEquals(expected, maps.get(i).put(key, step),
                            NAMES.get(i) + " 스텝 " + step + " 키 " + key);
                }
            } else {
                Integer expected = reference.remove(key);
                for (int i = 0; i < 5; i++) {
                    assertEquals(expected, maps.get(i).remove(key),
                            NAMES.get(i) + " 스텝 " + step + " 키 " + key);
                }
            }
            for (int i = 0; i < 5; i++) {
                assertEquals(reference.size(), maps.get(i).size(),
                        NAMES.get(i) + " 의 크기. 스텝 " + step);
            }
            if (step % 250 == 0) {
                for (int i = 0; i < 5; i++) {
                    assertEquals(reference.keySet(), keySet(maps.get(i)),
                            NAMES.get(i) + " 의 키 집합. 스텝 " + step);
                }
            }
        }

        for (int i = 0; i < 5; i++) {
            assertEquals(reference.keySet(), keySet(maps.get(i)), NAMES.get(i));
            for (java.util.Map.Entry<Integer, Integer> entry : reference.entrySet()) {
                assertEquals(entry.getValue(), maps.get(i).get(entry.getKey()),
                        NAMES.get(i) + " 의 키 " + entry.getKey());
            }
            assertTrue(maps.get(i).capacity() > 8, NAMES.get(i) + " 가 한 번도 안 커졌다");
        }
    }

    @Test
    @DisplayName("문자열 키로도 대조한다")
    @Timeout(300)
    void stringKeys() {
        // 정수 키는 hashCode 가 값 그대로라 분포가 특수하다. 문자열은 그렇지 않다.
        List<ProbeMap<String, Integer>> maps = List.of(
                new LinearProbeMap<>(), new QuadraticProbeMap<>(), new DoubleHashMap<>(),
                new RobinHoodMap<>(), new CuckooHashMap<>());
        HashMap<String, Integer> reference = new HashMap<>();
        Random random = new Random(4242L);

        for (int step = 0; step < 10_000; step++) {
            String key = "key" + random.nextInt(600);
            if (random.nextInt(100) < 60) {
                Integer expected = reference.put(key, step);
                for (int i = 0; i < 5; i++) {
                    assertEquals(expected, maps.get(i).put(key, step), NAMES.get(i) + " 스텝 " + step);
                }
            } else {
                Integer expected = reference.remove(key);
                for (int i = 0; i < 5; i++) {
                    assertEquals(expected, maps.get(i).remove(key), NAMES.get(i) + " 스텝 " + step);
                }
            }
            for (int i = 0; i < 5; i++) {
                assertEquals(reference.size(), maps.get(i).size(), NAMES.get(i) + " 스텝 " + step);
            }
        }

        for (int i = 0; i < 5; i++) {
            Set<String> keys = new HashSet<>();
            maps.get(i).keys().forEach(keys::add);
            assertEquals(reference.keySet(), keys, NAMES.get(i));
            assertNull(maps.get(i).get("없는키"), NAMES.get(i));
        }
    }

    @Test
    @DisplayName("같은 입력에 다섯이 같은 답을 준다 (구현끼리도 대조)")
    void allFiveAgree() {
        // HashMap 이 아니라 서로를 본다. 다섯이 같이 틀리는 일은 드물다.
        List<ProbeMap<Integer, Integer>> maps = fiveInt();
        Random random = new Random(777L);
        List<Integer> pool = new ArrayList<>();
        for (int i = 0; i < 500; i++) pool.add(random.nextInt(1000) - 500);

        for (int step = 0; step < 5_000; step++) {
            int key = pool.get(random.nextInt(pool.size()));
            int roll = random.nextInt(3);
            List<Object> answers = new ArrayList<>();
            for (ProbeMap<Integer, Integer> map : maps) {
                answers.add(switch (roll) {
                    case 0 -> map.put(key, step);
                    case 1 -> map.remove(key);
                    default -> map.get(key);
                });
            }
            for (int i = 1; i < 5; i++) {
                assertEquals(answers.get(0), answers.get(i),
                        NAMES.get(i) + " 가 " + NAMES.get(0) + " 와 다른 답을 냈다. 스텝 " + step);
                assertEquals(maps.get(0).size(), maps.get(i).size(), NAMES.get(i) + " 의 크기. 스텝 " + step);
            }
        }
    }
}
