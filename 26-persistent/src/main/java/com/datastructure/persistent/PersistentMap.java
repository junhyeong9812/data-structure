package com.datastructure.persistent;

import java.util.List;

/**
 * 바뀌지 않는 맵. put 도 remove 도 새 맵을 반환한다.
 *
 * <h2>05번, 06번과의 차이는 반환형 하나다</h2>
 *
 * <pre>
 *   05번 해시맵, 06번 이진 탐색 트리   V    put(K key, V value)   옛 값을 준다
 *   여기                              Map  put(K key, V value)   새 맵을 준다
 * </pre>
 *
 * 저 반환형 하나가 모든 것을 바꾼다. 옛 맵이 살아 있으므로
 * put 을 백 번 해도 첫 번째 맵은 그대로다. 그것이 이 문제집에서 처음 생기는 시간 축이다.
 *
 * <h2>통째로 복사하면 안 되는 이유</h2>
 *
 * "새 맵을 반환한다"를 지키는 가장 쉬운 방법은 매번 전체를 복사하는 것이다.
 * 답은 맞는다. 다만 put 하나가 O(n) 이고 메모리도 O(n) 이라 쓸 수 없다.
 *
 * PersistentTreeMap 은 바뀐 길목만 새로 만들고 나머지는 옛 맵과 같은 노드를 가리킨다.
 * 그래서 새 버전 하나가 O(log n) 이다. 답만 보면 두 방식이 구별되지 않는다.
 * 참조 동일성을 봐야 갈린다.
 *
 * 이 인터페이스에는 TODO 가 없다. 계약은 주어지는 것이다.
 */
public interface PersistentMap<K, V> {

    /** key 에 value 를 넣은 새 맵. 이 맵은 바뀌지 않는다. */
    PersistentMap<K, V> put(K key, V value);

    /** 없으면 null. */
    V get(K key);

    /** key 를 지운 새 맵. 없는 키면 바뀔 것이 없다. */
    PersistentMap<K, V> remove(K key);

    boolean containsKey(K key);

    int size();

    default boolean isEmpty() {
        return size() == 0;
    }

    /** 오름차순 전체 키. */
    List<K> keys();
}
