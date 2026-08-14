package com.datastructure.splay;

import java.util.ArrayList;
import java.util.List;

/**
 * 스플레이 트리. 균형을 잡지 않는다. 접근한 노드를 뿌리로 끌어올릴 뿐이다.
 *
 * <h2>노드를 보라</h2>
 *
 * key, value, left, right. 그게 전부다.
 * 16번 레드블랙 트리에는 여기에 색 비트가 있었고, 12번 스킵 리스트에는 층 배열이,
 * 15번 B-트리에는 키 배열과 자식 배열이 있었다. 여기는 없다.
 * 균형에 관한 정보를 하나도 저장하지 않는다.
 *
 * <h2>그런데도 O(log n) 이 나온다. 단 상환으로</h2>
 *
 * 연산 하나는 O(n) 일 수 있다. 정렬 순서로 넣으면 트리가 한 줄이 되고
 * 맨 밑을 조회하면 노드 n 개를 지난다. 그건 사실이고 숨길 수 없다.
 *
 * 대신 그 조회가 지나간 경로를 절반으로 접는다. 그래서 m 번의 연산 전체는 O(m log n) 이다.
 * 비싼 연산이 다음 연산들을 싸게 만든다. 이것이 상환(amortized) 분석이고
 * 이 문제집에서 여기 처음 나온다. 01번 동적 배열의 2배로 키우기와 같은 종류의 논증이다.
 *
 * <h2>세 경우</h2>
 *
 * <pre>
 *   zig       부모가 뿌리다. 회전 한 번으로 끝
 *   zig-zig   나와 부모가 같은 방향이다. 할아버지를 먼저 돌리고 부모를 돈다
 *   zig-zag   방향이 다르다. 부모를 돌리고 할아버지를 돈다
 * </pre>
 *
 * zig-zig 의 순서가 이 자료구조의 전부다. 부모를 먼저 돌리면 그냥 회전을 반복하는 것과
 * 같아지고(move-to-root), 경로가 절반으로 접히지 않아 상환 O(log n) 이 안 나온다.
 * 답은 똑같이 나오고 비용만 달라진다. MoveToRootTree 가 그 차이를 회전 수로 보여준다.
 *
 * <h2>공짜로 따라오는 것</h2>
 *
 * 최근에 쓴 것이 뿌리 근처에 있다. 접근이 몰려 있으면 비용이 트리 크기가 아니라
 * 최근에 쓴 것의 개수로 정해진다. 10번 LRU 캐시가 리스트를 손으로 관리해 얻던 성질이다.
 *
 * <h2>대가</h2>
 *
 * 조회가 쓰기다. get 도 floorKey 도 트리를 바꾼다.
 * 그래서 읽기 잠금을 쓸 수 없다. 10번 ThreadSafeLRUCache 와 같은 이유다.
 */
public class SplayTree<K extends Comparable<K>, V> {

    static final class Node<K, V> {
        K key;
        V value;
        Node<K, V> left;
        Node<K, V> right;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    Node<K, V> root;
    private int size;
    private long rotations;

    /**
     * h 의 왼쪽 자식을 위로 올린다. 새 부모를 반환한다.
     * 16번과 같은 회전이다. 다만 옮길 색이 없어서 두 줄 짧다.
     */
    private Node<K, V> rotateRight(Node<K, V> h) {
        rotations++;
        Node<K, V> x = h.left;
        h.left = x.right;
        x.right = h;
        return x;
    }

    /** rotateRight 의 거울상. */
    private Node<K, V> rotateLeft(Node<K, V> h) {
        rotations++;
        Node<K, V> x = h.right;
        h.right = x.left;
        x.left = h;
        return x;
    }

    /**
     * key 를 뿌리로 끌어올린 새 뿌리를 반환한다.
     * key 가 없으면 탐색이 멈춘 자리의 노드, 즉 가장 가까운 노드를 올린다.
     */
    private Node<K, V> splay(Node<K, V> h, K key) {
        // TODO 1: 이 문제의 본체다. 부모 포인터 없이 재귀로 두 층씩 내려간다.
        //
        // 16번에서 "반환값을 부모의 left/right 에 다시 대입한다"로 트리를 고쳤다.
        // 여기서도 같다. 그래서 부모 포인터가 필요 없다.
        //
        // 뼈대는 이렇다. 왼쪽으로 갈 때만 적는다. 오른쪽은 거울상이다.
        //
        //   1. h 가 null 이면 null
        //   2. key 가 h 와 같으면 h 가 이미 뿌리다. 그대로 반환
        //   3. key < h.key 인데 h.left 가 null 이면 **여기서 멈춘다.** h 를 반환한다
        //      (key 가 트리에 없는 경우다. 가장 가까운 노드가 h 다)
        //   4. 아니면 h.left 와 **다시 비교해** 두 경우로 나눈다
        //
        //        key < h.left.key   zig-zig (왼쪽-왼쪽)
        //        key > h.left.key   zig-zag (왼쪽-오른쪽)
        //        같으면 아무것도 안 한다. 6번의 회전 하나로 끝나는 zig 다
        //
        //      어느 쪽이든 손자 자리를 splay 로 먼저 정리한다.
        //        zig-zig 이면  h.left.left  = splay(h.left.left, key)
        //        zig-zag 이면  h.left.right = splay(h.left.right, key)
        //
        //   5. 그리고 회전한다. **여기가 갈린다.**
        //
        //        zig-zig:  h 를(할아버지를) 돌린다.       h = rotateRight(h)
        //        zig-zag:  h.left 를(부모를) 돌린다.      h.left = rotateLeft(h.left)
        //
        //      zig-zag 쪽은 손자 자리가 null 이면 돌릴 것이 없으니 건너뛴다.
        //
        //   6. 마지막으로 h.left 가 null 이면 h 를, 아니면 rotateRight(h) 를 반환한다.
        //      이 마지막 회전이 세 경우 모두에서 목표를 한 층 더 끌어올린다.
        //
        // **5번의 zig-zig 순서가 이 자료구조의 핵심이다.**
        // 부모를 먼저 돌리면 그냥 회전을 반복하는 것과 같아진다. 답은 맞지만
        // 경로가 절반으로 접히지 않아 다음 조회가 또 밑바닥까지 내려간다.
        // 계약 테스트로는 안 잡힌다. AmortizedCostTest 가 회전 수로 잡는다.
        //
        // 그림으로 보면 zig-zig 은 이렇다. x 를 올린다.
        //
        //        g              p                x
        //       /              / \                \
        //      p       ->     x   g      ->        p
        //     /                                     \
        //    x                                       g
        //
        //   할아버지를 먼저 돌려 g 와 p 를 뒤집고, 그 다음 p 를 돌려 x 를 올린다.
        //   x 아래로 g 와 p 가 한 줄이 아니라 갈라져 붙는다. 그래서 깊이가 절반이 된다.
        throw new UnsupportedOperationException("TODO 1: splay");
    }

    public V put(K key, V value) {
        requireKey(key);
        if (value == null) {
            throw new IllegalArgumentException("값은 null 일 수 없다");
        }
        // TODO 2: splay 를 부르고 새 노드를 뿌리 자리에 놓는다.
        //
        //   1. 트리가 비었으면 새 노드가 곧 뿌리다
        //   2. root = splay(root, key)
        //   3. 뿌리가 key 면 이미 있는 키다. 값만 갈고 옛 값을 반환한다
        //   4. 아니면 새 노드를 만들어 옛 뿌리를 그 밑에 매단다
        //
        // 4번이 이 자료구조다운 부분이다. splay 뒤 뿌리는 key 의 바로 옆 값이므로
        // **옛 뿌리의 한쪽 부분트리만 떼어 오면** 정렬이 맞는다.
        //
        //   key < root.key 이면
        //     새 노드의 왼쪽   = root.left     (전부 key 보다 작다)
        //     새 노드의 오른쪽 = root
        //     그리고 root.left 를 null 로 끊는다   <- 이 줄을 빠뜨리면 같은 노드가 두 곳에 달린다
        //
        //   key > root.key 이면 좌우를 바꾼다
        //
        // 06번처럼 잎까지 내려가 매다는 것이 아니다. 새 키는 언제나 뿌리에 온다.
        // size 를 올리는 것도 잊지 마라. 값만 갈아치울 때는 올리면 안 된다.
        throw new UnsupportedOperationException("TODO 2: put");
    }

    /**
     * 값을 꺼낸다. 그러면서 그 키를 뿌리로 옮긴다.
     * 이름은 get 인데 트리를 고쳐 쓴다. 10번 LRU 캐시의 get 과 같은 성질이다.
     */
    public V get(K key) {
        requireKey(key);
        if (root == null) {
            return null;
        }
        root = splay(root, key);
        return key.compareTo(root.key) == 0 ? root.value : null;
    }

    public V remove(K key) {
        requireKey(key);
        if (root == null) {
            return null;
        }
        // TODO 3: 뿌리로 올려놓고 떼어낸 다음, 남은 둘을 다시 잇는다.
        //
        //   1. root = splay(root, key). 뿌리가 key 가 아니면 없는 키다. null 을 반환한다
        //   2. 뿌리를 떼면 부분트리 둘이 남는다. left 는 전부 key 보다 작고 right 는 전부 크다
        //   3. left 가 없으면 right 가 그대로 새 뿌리다
        //   4. 있으면 **left 에서 splay 를 한 번 더** 부른다
        //
        // 4번이 요령이다. left 안의 모든 키가 key 보다 작으므로 splay(left, key) 는
        // left 의 최댓값을 그 부분트리의 뿌리로 올린다.
        // 최댓값이니 오른쪽 자식이 없다. 그 빈자리에 right 를 붙이면 끝이다.
        //
        //   left 의 최댓값을 따로 찾아 잘라내도 되지만, 이미 있는 splay 를 한 번 더 부르는
        //   쪽이 짧다. 없는 키로 splay 하면 가장 가까운 것이 올라온다는 성질을 그대로 쓴다.
        //
        // 06번의 삭제와 비교해보라. 거기서는 자식이 둘인 노드를 지울 때
        // 후속자를 찾아 값을 옮겨 심었다. 여기서는 부분트리 둘을 잇기만 한다.
        //
        // size 를 내리고 옛 값을 반환한다.
        throw new UnsupportedOperationException("TODO 3: remove");
    }

    public K floorKey(K key) {
        requireKey(key);
        if (root == null) {
            return null;
        }
        // TODO 4: key 이하인 것 중 가장 큰 키.
        //
        // 16번에서는 후보를 손에 들고 내려가야 했다. 여기서는 splay 가 그 일을 대신한다.
        //
        //   root = splay(root, key) 를 부르고 나면 뿌리는 key 이거나 key 의 바로 이웃이다.
        //   뿌리가 key 이하면 그게 답이다.
        //   뿌리가 key 보다 크면 답은 **왼쪽 부분트리의 최댓값**이다. 왼쪽으로 한 번 간 뒤
        //   오른쪽 끝까지 내려간다.
        //   왼쪽이 아예 없으면 key 이하인 것이 없다. null.
        //
        // 뿌리가 key 보다 클 수도, 작을 수도 있다는 것이 함정이다. 한쪽만 다루면
        // 절반의 입력에서 조용히 틀린다. 없는 키로 splay 했을 때 어느 쪽이 올라오는지는
        // 트리 모양에 달렸고 미리 알 수 없다.
        //
        // 이 메서드가 트리를 바꾼다는 점도 보라. 조회처럼 생겼는데 쓰기다.
        // ceilingKey 는 아래에 채워져 있다. 거울상이니 비교해보면 된다.
        throw new UnsupportedOperationException("TODO 4: floorKey");
    }

    public K ceilingKey(K key) {
        requireKey(key);
        if (root == null) {
            return null;
        }
        root = splay(root, key);
        if (root.key.compareTo(key) >= 0) {
            return root.key;
        }
        Node<K, V> h = root.right;
        if (h == null) {
            return null;
        }
        while (h.left != null) {
            h = h.left;
        }
        return h.key;
    }

    /** key 가 지금 몇 층에 있는지. 뿌리가 0, 없으면 -1. 이것만은 트리를 건드리지 않는다. */
    public int depthOf(K key) {
        requireKey(key);
        Node<K, V> cur = root;
        int depth = 0;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp < 0) {
                cur = cur.left;
            } else if (cmp > 0) {
                cur = cur.right;
            } else {
                return depth;
            }
            depth++;
        }
        return -1;
    }

    /** 태어난 뒤 지금까지의 누적 회전 수. 상환 비용을 재는 자다. */
    public long rotations() {
        return rotations;
    }

    public List<K> keys() {
        List<K> out = new ArrayList<>(size);
        inorder(root, out);
        return out;
    }

    private void inorder(Node<K, V> h, List<K> out) {
        if (h == null) {
            return;
        }
        inorder(h.left, out);
        out.add(h.key);
        inorder(h.right, out);
    }

    public K firstKey() {
        if (root == null) {
            return null;
        }
        Node<K, V> cur = root;
        while (cur.left != null) {
            cur = cur.left;
        }
        return cur.key;
    }

    public K lastKey() {
        if (root == null) {
            return null;
        }
        Node<K, V> cur = root;
        while (cur.right != null) {
            cur = cur.right;
        }
        return cur.key;
    }

    public int height() {
        return height(root);
    }

    private int height(Node<K, V> h) {
        return h == null ? 0 : 1 + Math.max(height(h.left), height(h.right));
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

    public void clear() {
        root = null;
        size = 0;
    }

    private static void requireKey(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("키는 null 일 수 없다");
        }
    }
}
