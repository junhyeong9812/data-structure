package com.datastructure.redblack;

import java.util.ArrayList;
import java.util.List;

/**
 * 좌편향 레드블랙 트리. **회전으로 균형을 보장한다.**
 *
 * 링크마다 색이 하나 있다(노드에 저장하지만 뜻은 "부모에서 나에게 오는 링크의 색"이다).
 * 지켜야 할 것이 넷이다.
 *
 *   1. 뿌리는 검다
 *   2. **빨간 링크는 왼쪽에만 있다**            <- 좌편향(left-leaning)의 정의
 *   3. 빨간 노드가 연달아 나오지 않는다
 *   4. **모든 뿌리-잎 경로의 검은 링크 수가 같다**
 *
 * 4번이 균형의 정체다. 검은 높이가 같으니 가장 짧은 경로(전부 검정)와
 * 가장 긴 경로(검빨검빨...)의 길이 차이가 **두 배를 못 넘는다.**
 * 그래서 높이가 2*log2(n+1) 이하로 **보장된다.** 12번 스킵 리스트의 확률과 다른 점이다.
 *
 * **2-3 트리로 읽으면 회전이 마법이 아니게 된다.**
 *
 *   빨간 링크로 이어진 두 노드  =  2-3 트리의 키 2개짜리 노드
 *   검은 링크                    =  2-3 트리의 진짜 간선
 *
 * 15번 B-트리에서 노드가 꽉 차면 쪼개 가운데를 위로 올렸다.
 * 여기서 flipColors 가 정확히 그 일을 한다. **키 3개짜리가 되면 가운데를 위로 올린다.**
 * 회전 둘은 "빨간 링크를 왼쪽으로 몰아 모양을 정규화하는" 정리 작업이다.
 *
 * 2번 규칙(좌편향)이 있는 이유가 이것이다. 모양의 가짓수를 줄여 경우의 수를 줄인다.
 * 좌우를 다 허용하면(고전적인 레드블랙 트리) 2-3-4 트리가 되고 코드가 두 배가 된다.
 */
public class RedBlackTree<K extends Comparable<K>, V> {

    static final boolean RED = true;
    static final boolean BLACK = false;

    static final class Node<K, V> {
        K key;
        V value;
        Node<K, V> left;
        Node<K, V> right;
        boolean color;

        Node(K key, V value, boolean color) {
            this.key = key;
            this.value = value;
            this.color = color;
        }
    }

    Node<K, V> root;
    private int size;

    static <K, V> boolean isRed(Node<K, V> node) {
        return node != null && node.color == RED;
    }

    /** 오른쪽으로 기운 빨간 링크를 왼쪽으로 돌린다. 새 부모를 반환한다. */
    private Node<K, V> rotateLeft(Node<K, V> h) {
        // TODO 1: h 의 오른쪽 자식 x 를 위로 올린다.
        //
        //        h(?)                x(?)
        //          \  red     ->     /  red
        //           x               h
        //
        //   x 의 왼쪽 부분트리는 h 의 오른쪽으로 간다. 순서가 그대로 유지된다.
        //   **색도 옮겨야 한다.** x 가 h 의 색을 물려받고 h 는 빨강이 된다.
        //   (h 자리에 x 가 들어왔으니 위에서 보는 색은 그대로여야 한다)
        //
        // 색을 안 옮기면 회전이 검은 높이를 바꿔 불변식 4번이 깨진다.
        throw new UnsupportedOperationException("TODO 1: rotateLeft");
    }

    /** rotateLeft 의 거울상. */
    private Node<K, V> rotateRight(Node<K, V> h) {
        // TODO 2: rotateLeft 를 좌우만 바꿔 쓰면 된다.
        throw new UnsupportedOperationException("TODO 2: rotateRight");
    }

    /** h 와 두 자식의 색을 **전부 뒤집는다.** */
    private void flipColors(Node<K, V> h) {
        // TODO 3: 세 줄이다. h 와 h.left 와 h.right 의 색을 각각 반전한다.
        //
        // **이 한 동작이 두 가지로 쓰인다.**
        //   넣기: 자식 둘이 빨강일 때 부르면 자식이 검게, h 가 빨갛게 된다
        //         = 2-3 트리에서 키 3개짜리 노드의 **가운데를 위로 올리는 것**
        //   지우기: h 가 빨강일 때 부르면 자식이 빨갛게 된다
        //         = 위에서 빨강 하나를 **빌려 내려보내는 것**
        //
        // 방향이 반대인데 코드가 같다. "반전"으로 쓰면 둘 다 된다.
        // 대입(= RED)으로 쓰면 지우기 쪽이 깨진다.
        throw new UnsupportedOperationException("TODO 3: flipColors");
    }

    /** 불변식이 깨진 노드를 세 번의 검사로 되돌린다. **모든 수정 연산이 여기로 끝난다.** */
    private Node<K, V> balance(Node<K, V> h) {
        // TODO 4: 세 규칙을 **이 순서대로** 적용한다.
        //
        //   1. 오른쪽만 빨갛다        -> rotateLeft   (좌편향으로 돌린다)
        //   2. 왼쪽과 그 왼쪽이 빨갛다 -> rotateRight  (빨강 둘이 한 줄로 섰다)
        //   3. 양쪽이 다 빨갛다        -> flipColors   (키 3개짜리다. 가운데를 올린다)
        //
        // **순서가 계약이다.** 1번이 2번의 조건을 만들고, 2번이 3번의 조건을 만든다.
        // 순서를 바꾸면 한 번에 정리가 안 되고 불변식이 깨진 채로 위로 올라간다.
        //
        // 그리고 세 검사가 **연달아** 일어난다는 점을 보라. else if 가 아니다.
        throw new UnsupportedOperationException("TODO 4: balance");
    }

    public V put(K key, V value) {
        requireKey(key);
        if (value == null) {
            throw new IllegalArgumentException("값은 null 일 수 없다");
        }
        Object[] old = new Object[1];
        root = put(root, key, value, old);
        root.color = BLACK;
        @SuppressWarnings("unchecked")
        V result = (V) old[0];
        return result;
    }

    private Node<K, V> put(Node<K, V> h, K key, V value, Object[] old) {
        // TODO 5: 06번 BST 의 삽입에 **마지막 한 줄만 더한다.**
        //
        //   빈 자리면 **빨간** 노드를 만든다. 검은 노드를 만들면 그 경로만 검은 높이가 늘어
        //   불변식 4번이 즉시 깨진다. 빨강으로 넣으면 검은 높이가 그대로다.
        //   (2-3 트리로 읽으면 "기존 노드에 키를 하나 더 끼워 넣는 것"이다)
        //
        //   같은 키면 값만 갈고 옛 값을 old[0] 에 담는다.
        //
        //   그리고 **돌아오는 길에 balance 를 부른다.** 그게 전부다.
        //   회전과 색 바꾸기가 아래에서 위로 전파되며 불변식을 복구한다.
        //
        // 반환값을 부모의 left/right 에 다시 대입하는 방식(재귀로 트리 고치기)이라
        // 회전으로 부모가 바뀌어도 링크가 저절로 맞는다. **부모 포인터가 필요 없는 이유다.**
        throw new UnsupportedOperationException("TODO 5: put");
    }

    public V get(K key) {
        requireKey(key);
        Node<K, V> cur = root;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp < 0) {
                cur = cur.left;
            } else if (cmp > 0) {
                cur = cur.right;
            } else {
                return cur.value;
            }
        }
        return null;
    }

    /** 왼쪽으로 내려가야 하는데 h.left 와 그 왼쪽이 둘 다 검을 때, 빨강 하나를 왼쪽으로 보낸다. */
    private Node<K, V> moveRedLeft(Node<K, V> h) {
        // TODO 6: **지우기가 어려운 이유가 여기 있다.**
        //
        // 검은 노드를 지우면 그 경로의 검은 높이가 줄어 불변식 4번이 깨진다.
        // 그래서 **내려가기 전에 미리 빨강을 확보해둔다.** 그러면 지울 때 검은 높이가 안 변한다.
        // 15번 B-트리에서 "내려가기 전에 미리 채운다"고 했던 것과 정확히 같은 발상이다.
        //
        //   1. flipColors 로 h 의 빨강을 두 자식에게 내려보낸다
        //   2. **오른쪽 형제가 빌려줄 것이 있으면**(h.right.left 가 빨강이면) 그걸 가져온다
        //      - 오른쪽을 오른쪽으로 돌리고, h 를 왼쪽으로 돌리고, 다시 flipColors
        //   3. 없으면 1번만으로 끝. 형제와 합쳐진 셈이다
        //
        // 2번이 B-트리의 "형제에게 빌리기", 3번이 "형제와 병합하기"다.
        throw new UnsupportedOperationException("TODO 6: moveRedLeft");
    }

    private Node<K, V> moveRedRight(Node<K, V> h) {
        flipColors(h);
        if (isRed(h.left.left)) {
            h = rotateRight(h);
            flipColors(h);
        }
        return h;
    }

    /** 부분트리에서 가장 작은 키를 지운다. */
    private Node<K, V> deleteMin(Node<K, V> h) {
        // TODO 7: 왼쪽 끝까지 내려가며 지운다.
        //
        //   왼쪽이 없으면 h 자신이 최소다. **그냥 null 을 반환한다.**
        //   (여기까지 왔다는 것은 h 가 빨갛다는 뜻이라 지워도 검은 높이가 안 변한다)
        //
        //   내려가기 전에 h.left 와 h.left.left 가 둘 다 검으면 moveRedLeft 로 빨강을 확보한다.
        //   돌아오는 길에 balance.
        throw new UnsupportedOperationException("TODO 7: deleteMin");
    }

    public V remove(K key) {
        requireKey(key);
        V old = get(key);
        if (old == null) {
            return null;
        }
        // 뿌리와 두 자식이 다 검으면 뿌리를 빨갛게 만들어두고 시작한다.
        // "delete 에 들어가는 노드는 자기 또는 왼쪽 자식이 빨갛다"를 맞춰주는 관례다.
        //
        // 정직하게 적어두면, **이 줄을 지워도 우리 테스트는 전부 통과한다.**
        // (크기 1~6 짜리 트리의 모든 삭제 순서 전수 + 무작위 3000스텝 + TreeMap 대조 2만스텝)
        // 뿌리에서 flipColors 가 어차피 색을 뒤집어 같은 효과를 내고,
        // 마지막에 뿌리를 검게 강제하기 때문으로 보인다.
        // 그래도 남긴다. 표준 구현의 계약이고, 조건을 명시해두는 편이 읽기 쉽다.
        if (!isRed(root.left) && !isRed(root.right)) {
            root.color = RED;
        }
        root = delete(root, key);
        if (root != null) {
            root.color = BLACK;
        }
        size--;
        return old;
    }

    private Node<K, V> delete(Node<K, V> h, K key) {
        // TODO 8: 이 문제집에서 **가장 조밀한 20줄**이다. 한 줄씩 이유가 있다.
        //
        // 왼쪽으로 갈 때:
        //   moveRedLeft 로 빨강을 확보하고 내려간다.
        //
        // 그 밖(찾았거나 오른쪽으로 갈 때):
        //   1. h.left 가 빨갛다면 **먼저 오른쪽으로 돌린다.** 좌편향을 풀어
        //      오른쪽으로 내려갈 준비를 하는 것이다
        //   2. 찾았는데 **오른쪽이 없으면** 여기가 끝이다. null 을 반환해 지운다
        //   3. moveRedRight 로 빨강을 확보한다
        //   4. 다시 찾았는지 본다(1번의 회전으로 h 가 바뀌었을 수 있다).
        //      찾았으면 **오른쪽 부분트리의 최솟값**을 여기로 옮기고 그 최솟값을 지운다.
        //      06번의 "후속자를 데려온다"와 같다.
        //      아니면 오른쪽으로 내려간다
        //
        // **2번과 4번에서 key 비교를 두 번 하는 이유**가 1번의 회전이다.
        // 회전하면 h 가 다른 노드가 되므로 다시 비교해야 한다. 한 번만 하면 조용히 틀린다.
        //
        // 마지막에 balance.
        throw new UnsupportedOperationException("TODO 8: delete");
    }

    private Node<K, V> min(Node<K, V> h) {
        while (h.left != null) {
            h = h.left;
        }
        return h;
    }

    public K floorKey(K key) {
        requireKey(key);
        // TODO 9: key 이하인 것 중 가장 큰 키. 06번과 같다.
        //
        // 지금 노드가 key 이하면 **그게 답일 수도 있는데** 오른쪽에 더 가까운 답이 있을 수 있다.
        // 그래서 후보로 붙잡아두고 오른쪽으로 계속 내려간다.
        // "찾으면 바로 반환"이 안 되는 유일한 탐색이다.
        //
        // (12번 스킵 리스트에서는 멈춘 자리가 곧 답이었다. 트리에서는 후보를 들고 다녀야 한다)
        throw new UnsupportedOperationException("TODO 9: floorKey");
    }

    public K ceilingKey(K key) {
        requireKey(key);
        Node<K, V> cur = root;
        K best = null;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp == 0) {
                return cur.key;
            }
            if (cmp > 0) {
                cur = cur.right;
            } else {
                best = cur.key;
                cur = cur.left;
            }
        }
        return best;
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
        return root == null ? null : min(root).key;
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

    /** 뿌리에서 잎까지 지나는 **검은 링크의 수**. 모든 경로에서 같아야 한다. */
    public int blackHeight() {
        int bh = 0;
        Node<K, V> cur = root;
        while (cur != null) {
            if (!isRed(cur)) {
                bh++;
            }
            cur = cur.left;
        }
        return bh;
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
