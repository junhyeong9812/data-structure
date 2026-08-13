package com.datastructure.skiplist;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 확률로 균형을 잡는 정렬 자료구조.
 *
 * **트리가 아니다.** 정렬된 연결 리스트를 여러 층 쌓은 것이다.
 * 노드마다 forward 배열이 있고, 배열 길이가 그 노드의 층수다.
 *
 *   forward[0] 은 바로 다음 노드   (모든 노드가 갖는다)
 *   forward[1] 은 한 층 위의 다음  (절반이 갖는다)
 *   forward[2] 는 두 층 위의 다음  (4분의 1이 갖는다)
 *
 * head 는 센티넬이고 늘 MAX_LEVEL 층을 갖는다. 10번 LRU 의 센티넬과 같은 역할이다.
 *
 * **찾기의 모양이 이 자료구조의 전부다.**
 * 맨 위 층에서 시작해 "다음이 목표보다 작으면 전진, 아니면 한 층 내려간다"를 반복한다.
 * 위층이 크게 건너뛰므로 이진 탐색과 같은 걸음 수가 나온다.
 *
 * 층수는 **동전으로** 정한다. 앞면이면 한 층 더. 그래서 절반이 1층, 4분의 1이 2층이 된다.
 * 06번 BST 와 달리 **입력 순서가 구조에 영향을 주지 않는다.**
 * 정렬된 순서로 넣어도 무작위로 넣어도 같은 분포가 나온다.
 *
 * 테스트를 위해 seed 를 받는 생성자를 뒀다. **무작위를 쓰는 자료구조는 seed 를 주입받아야
 * 테스트할 수 있다.** 안 그러면 실패를 재현할 수 없다.
 */
public class SkipList<K extends Comparable<K>, V> {

    static final int MAX_LEVEL = 32;
    static final double P = 0.5;

    static final class Node<K, V> {
        final K key;
        V value;
        final Node<K, V>[] forward;

        @SuppressWarnings("unchecked")
        Node(K key, V value, int level) {
            this.key = key;
            this.value = value;
            this.forward = new Node[level];
        }
    }

    final Node<K, V> head = new Node<>(null, null, MAX_LEVEL);
    private final Random random;
    int level = 1;
    private int size;

    public SkipList() {
        this(new Random());
    }

    public SkipList(long seed) {
        this(new Random(seed));
    }

    SkipList(Random random) {
        this.random = random;
    }

    /** 새 노드를 몇 층까지 올릴지 동전으로 정한다. */
    int randomLevel() {
        // TODO 1: 1 에서 시작해 앞면(확률 P)이 나오는 동안 1씩 올린다. MAX_LEVEL 을 넘지 않는다.
        //
        // 왜 이게 균형을 만드는가.
        //   레벨 1 이상: 전부       (100%)
        //   레벨 2 이상: 절반       (50%)
        //   레벨 3 이상: 4분의 1    (25%)
        // 층마다 노드 수가 절반씩 주니 맨 위층에서 시작해 내려오면 log n 걸음이다.
        //
        // MAX_LEVEL 상한이 없으면 아주 낮은 확률로 배열이 무한히 커진다.
        // 32 면 2^32 개까지 감당한다.
        throw new UnsupportedOperationException("TODO 1: randomLevel");
    }

    /** key 보다 **작은** 마지막 노드를 레벨마다 찾아 update 에 담고, 레벨 0 의 그 노드를 준다. */
    @SuppressWarnings("unchecked")
    Node<K, V>[] findPredecessors(K key) {
        Node<K, V>[] update = new Node[MAX_LEVEL];
        // TODO 2: 층마다 "key 보다 작은 마지막 노드"를 찾아 update[i] 에 담는다.
        //
        // 맨 위 층(level - 1)에서 시작해 0 까지 내려온다.
        //   다음 노드가 있고 그 키가 key 보다 **작으면** 전진한다.
        //   더 못 가면 지금 자리를 update[i] 에 적고 한 층 내려간다.
        //
        // **한 층 내려갈 때 cur 을 head 로 되돌리면 안 된다.** 지금 자리에서 이어서 내려간다.
        // 그게 "이미 지나온 구간을 다시 안 본다"는 뜻이고, 그래서 log n 이 된다.
        //
        // 이 배열이 put 과 remove 양쪽에서 쓰인다.
        // 링크를 고치려면 **각 층의 앞 노드**를 알아야 하기 때문이다. 02번과 같은 이유다.
        throw new UnsupportedOperationException("TODO 2: findPredecessors");
    }

    public V get(K key) {
        requireKey(key);
        // TODO 3: 위층부터 내려오며 전진하고, 레벨 0 의 다음 노드가 찾던 키인지 본다.
        //
        // 내려오기가 끝났을 때 cur 은 **key 보다 작은 마지막 노드**다.
        // 그러니 답이 있다면 cur.forward[0] 이다. 없으면 그 자리에 다른 키가 있거나 null 이다.
        //
        // findPredecessors 를 불러 써도 되지만, 조회는 update 배열이 필요 없다.
        // 32칸 배열을 매번 만들 이유가 없으니 여기서는 따로 돈다.
        throw new UnsupportedOperationException("TODO 3: get");
    }

    public V put(K key, V value) {
        requireKey(key);
        // TODO 4: 넣는다.
        //
        //   1. findPredecessors 로 각 층의 앞 노드를 구한다.
        //   2. 이미 있는 키면 값만 갈고 옛 값을 준다. **층 구조는 안 건드린다.**
        //   3. 새 키면 randomLevel 로 층수를 뽑는다.
        //   4. 뽑은 층수가 지금 최고 레벨보다 크면, **새로 생긴 층의 앞 노드는 head 다.**
        //      findPredecessors 는 지금 레벨까지만 채워주므로 그 위는 비어 있다.
        //      이걸 빠뜨리면 null 참조로 터지거나 조용히 링크가 끊긴다.
        //   5. 층마다 링크를 잇는다. **새 노드의 forward 를 먼저 잡고 앞 노드를 고쳐야 한다.**
        //      순서를 바꾸면 뒤쪽을 잃는다. 02번 reverse 와 같은 함정이다.
        throw new UnsupportedOperationException("TODO 4: put");
    }

    public V remove(K key) {
        requireKey(key);
        // TODO 5: 지운다.
        //
        //   1. 앞 노드들을 구하고, 레벨 0 의 다음이 찾던 키인지 본다. 아니면 null.
        //   2. 층마다 앞 노드의 forward 를 목표의 다음으로 잇는다.
        //      **그 층에서 앞 노드가 목표를 가리키고 있지 않으면 거기서 멈춘다.**
        //      목표가 그 층까지 안 올라간 것이므로 더 볼 이유가 없다.
        //   3. 맨 위층이 비었으면 레벨을 내린다. 안 내리면 조회가 빈 층을 헛돈다.
        //      **레벨은 1 아래로 내려가면 안 된다.**
        throw new UnsupportedOperationException("TODO 5: remove");
    }

    public K floorKey(K key) {
        requireKey(key);
        // TODO 6: key **이하**인 것 중 가장 큰 키.
        //
        // get 과 딱 한 글자 다르다. 전진 조건이 `< 0` 이 아니라 `<= 0` 이다.
        // 그래야 key 자신이 있을 때 그 자리에 선다.
        //
        // 다 내려왔을 때 cur 이 head 면 key 이하인 것이 하나도 없다는 뜻이다.
        // 06번에서는 후보를 따로 기억해야 했는데, 여기서는 **멈춘 자리가 곧 답**이다.
        throw new UnsupportedOperationException("TODO 6: floorKey");
    }

    public K ceilingKey(K key) {
        requireKey(key);
        // TODO 7: key **이상**인 것 중 가장 작은 키.
        //
        // floorKey 와 반대로 `< 0` 으로 전진하고, 멈춘 자리의 **다음** 노드가 답이다.
        // 그 다음이 null 이면 key 보다 큰 것이 없다.
        throw new UnsupportedOperationException("TODO 7: ceilingKey");
    }

    public List<K> keys() {
        List<K> out = new ArrayList<>(size);
        for (Node<K, V> cur = head.forward[0]; cur != null; cur = cur.forward[0]) {
            out.add(cur.key);
        }
        return out;
    }

    public List<K> keysInRange(K from, K to) {
        requireKey(from);
        requireKey(to);
        // TODO 8: from 이상 to 이하를 정렬 순서로. from > to 면 빈 리스트.
        //
        // **전부 훑고 걸러내면 O(n) 이다.** from 자리로 log n 만에 내려간 다음
        // 레벨 0 을 따라 to 까지만 걸으면 O(log n + 결과 개수) 다.
        // 06번 범위 조회의 가지치기와 같은 이야기인데, 여기서는 훨씬 단순하다.
        // **레벨 0 이 이미 정렬된 연결 리스트라서** 시작점만 찾으면 그냥 걸으면 된다.
        throw new UnsupportedOperationException("TODO 8: keysInRange");
    }

    public K firstKey() {
        Node<K, V> n = head.forward[0];
        return n == null ? null : n.key;
    }

    public K lastKey() {
        // TODO 9: 가장 큰 키. 비었으면 null.
        //
        // 레벨 0 을 끝까지 걸으면 O(n) 이다. 위층부터 갈 데까지 가면 O(log n) 이다.
        // (그래도 firstKey 의 O(1) 보다는 비싸다. 앞뒤가 대칭이 아닌 구조다 -
        //  02번 단일 연결 리스트의 removeLast 와 같은 종류의 비대칭이다)
        throw new UnsupportedOperationException("TODO 9: lastKey");
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int currentLevel() {
        return level;
    }

    public void clear() {
        for (int i = 0; i < MAX_LEVEL; i++) {
            head.forward[i] = null;
        }
        level = 1;
        size = 0;
    }

    private static void requireKey(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("키는 null 일 수 없다");
        }
    }
}
