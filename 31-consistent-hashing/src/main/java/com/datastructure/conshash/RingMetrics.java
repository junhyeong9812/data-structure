package com.datastructure.conshash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 측정 도구. TODO 가 없다. 이 박스의 값어치가 이 네 메서드 위에 있다.
 *
 * <h2>이동량을 재는 방법</h2>
 *
 * 시간을 재지 않는다. 키 집합을 만들고, 매핑을 두 번 구해서 다른 것을 센다.
 * 그게 전부이고 결정적이다. 같은 입력이면 언제나 같은 수가 나온다.
 *
 * <pre>
 *   Map&lt;String, String&gt; before = RingMetrics.assign(ring, keys);
 *   ring.removeNode("node-7");
 *   Map&lt;String, String&gt; after = RingMetrics.assign(ring, keys);
 *   int moved = RingMetrics.moved(before, after);
 * </pre>
 *
 * <h2>키 이름을 무작위로 만들지 않는 이유</h2>
 *
 * key-0 부터 key-99999 까지를 그냥 쓴다. seed 를 고정한 Random 보다 한 단계 더 단순하고,
 * 파이썬으로 같은 수를 다시 계산해 검산하기가 쉽다.
 * 연속된 이름이 문제가 되지 않는 것은 MIXED 가 흩어주기 때문인데,
 * 그 전제가 깨지면 어떻게 되는지는 WEAK 로 재본다.
 */
public final class RingMetrics {

    private RingMetrics() {
    }

    /** key-0 부터 key-(n-1) 까지. */
    public static List<String> keys(int n) {
        List<String> keys = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            keys.add("key-" + i);
        }
        return Collections.unmodifiableList(keys);
    }

    /** 키마다 지금 어느 노드가 맡는지. */
    public static Map<String, String> assign(HashRing ring, List<String> keys) {
        Map<String, String> map = new HashMap<>(keys.size() * 2);
        for (String key : keys) {
            map.put(key, ring.getNode(key));
        }
        return map;
    }

    /** 두 매핑에서 담당이 달라진 키의 수. */
    public static int moved(Map<String, String> before, Map<String, String> after) {
        if (before.size() != after.size()) {
            throw new IllegalArgumentException("같은 키 집합이어야 한다");
        }
        int moved = 0;
        for (Map.Entry<String, String> entry : before.entrySet()) {
            String now = after.get(entry.getKey());
            if (!java.util.Objects.equals(entry.getValue(), now)) {
                moved++;
            }
        }
        return moved;
    }

    /**
     * 가장 많이 맡은 노드 대 가장 적게 맡은 노드의 비.
     *
     * 하나도 못 받은 노드가 있으면 무한대다. 0 으로 나누는 것을 피하려고 1 을 더하지 않는다.
     * "0 개를 맡았다"는 사실이 이 측정에서 제일 중요한 값이라 뭉개면 안 된다.
     */
    public static double imbalance(Map<String, Integer> counts) {
        if (counts.isEmpty()) {
            throw new IllegalArgumentException("노드가 없다");
        }
        int min = Integer.MAX_VALUE;
        int max = 0;
        for (int count : counts.values()) {
            min = Math.min(min, count);
            max = Math.max(max, count);
        }
        return min == 0 ? Double.POSITIVE_INFINITY : (double) max / min;
    }
}
