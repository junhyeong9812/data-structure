package com.datastructure.persistent;

import java.util.ArrayList;
import java.util.List;

/**
 * 경로 복사로 만드는 불변 이진 탐색 트리.
 *
 * <h2>06번과 무엇이 다른가</h2>
 *
 * 탐색은 한 글자도 다르지 않다. 왼쪽은 작고 오른쪽은 크다.
 * 다른 것은 고치는 방식뿐이다.
 *
 * <pre>
 *   06번   내려가서 잎에 새 노드를 매단다. 지나온 노드는 그대로 둔다
 *   여기   내려간 뒤 되짚어 올라오며 지나온 노드를 전부 새로 만든다
 * </pre>
 *
 * 그래서 옛 뿌리는 옛 트리를 그대로 가리키고 있다. 아무것도 안 바뀌었으니까.
 * 새 뿌리만 새 트리를 가리킨다. 두 트리는 안 지나간 부분을 전부 같이 쓴다.
 *
 * <h2>노드를 보라</h2>
 *
 * key, value, left, right, size 다섯이 전부 final 이다.
 * 만든 뒤에 고칠 방법이 없으므로 여러 버전이 같은 노드를 가리켜도 안전하다.
 * 공유가 안전한 이유는 불변이기 때문이지 그 반대가 아니다.
 *
 * <h2>대가 셋</h2>
 *
 * <pre>
 *   1. 상수 인자가 크다. 수정 하나에 노드를 log n 개 만든다. 가변은 하나다
 *   2. 균형을 안 잡는다. 정렬 입력이면 06번처럼 한 줄이 되고,
 *      그때는 버전 하나가 O(n) 메모리다
 *   3. 재귀라 깊은 트리에서 스택이 터진다
 * </pre>
 *
 * 참고: 필드 이름 root 와 Node 의 key, value, left, right, size 는 테스트가 직접 들여다본다.
 */
public final class PersistentTreeMap<K extends Comparable<K>, V> implements PersistentMap<K, V> {

    /**
     * 전부 final 이다. 이 클래스에는 고치는 메서드가 하나도 없다.
     * 필드를 하나라도 열면 옛 버전이 조용히 바뀔 수 있다.
     */
    static final class Node<K, V> {
        final K key;
        final V value;
        final Node<K, V> left;
        final Node<K, V> right;
        final int size;

        Node(K key, V value, Node<K, V> left, Node<K, V> right) {
            this.key = key;
            this.value = value;
            this.left = left;
            this.right = right;
            this.size = 1 + sizeOf(left) + sizeOf(right);
        }
    }

    // 빈 맵은 상태가 없으므로 하나만 있으면 된다. 타입 인자는 형식이고 내용물이 없다.
    private static final PersistentTreeMap<?, ?> EMPTY = new PersistentTreeMap<Integer, Object>(null, 0);

    final Node<K, V> root;
    private final int nodesCreated;

    private PersistentTreeMap(Node<K, V> root, int nodesCreated) {
        this.root = root;
        this.nodesCreated = nodesCreated;
    }

    // ------------------------------------------------------------------
    // 채워져 있는 부분
    // ------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public static <K extends Comparable<K>, V> PersistentTreeMap<K, V> empty() {
        return (PersistentTreeMap<K, V>) EMPTY;
    }

    private static int sizeOf(Node<?, ?> node) {
        return node == null ? 0 : node.size;
    }

    private static void requireKey(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("키는 null 일 수 없다");
        }
    }

    /**
     * 이 맵을 만들어낸 연산이 새로 만든 노드의 수.
     *
     * 이 자료구조의 비용을 재는 자다. 시간을 재는 대신 이것을 센다.
     * 균형이 잡혀 있으면 이 값이 height + 1 이고, 정렬 입력이면 n + 1 이다.
     * 없는 키를 지운 경우처럼 아무것도 안 바뀌면 맵 자신이 돌아오므로 값도 그대로다.
     */
    public int nodesCreatedByLastPut() {
        return nodesCreated;
    }

    @Override
    public int size() {
        return sizeOf(root);
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    /** 06번의 탐색과 같다. 읽기는 트리를 건드리지 않는다. 23번과 반대다. */
    @Override
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

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public List<K> keys() {
        List<K> out = new ArrayList<>(size());
        inorder(root, out);
        return out;
    }

    private void inorder(Node<K, V> node, List<K> out) {
        if (node == null) {
            return;
        }
        inorder(node.left, out);
        out.add(node.key);
        inorder(node.right, out);
    }

    /** 가장 긴 뿌리-잎 경로의 길이. 비었으면 0. 균형을 안 잡으므로 이 값이 커질 수 있다. */
    public int height() {
        return height(root);
    }

    private int height(Node<K, V> node) {
        return node == null ? 0 : 1 + Math.max(height(node.left), height(node.right));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (K key : keys()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(key).append('=').append(get(key));
            first = false;
        }
        return sb.append('}').toString();
    }

    // ------------------------------------------------------------------
    // 여기부터가 본체
    // ------------------------------------------------------------------

    @Override
    public PersistentTreeMap<K, V> put(K key, V value) {
        requireKey(key);
        if (value == null) {
            throw new IllegalArgumentException("값은 null 일 수 없다");
        }
        // created 는 재귀가 만든 노드 수를 담아 올라오는 통이다.
        // 자바에는 out 파라미터가 없어서 길이 1짜리 배열을 쓴다.
        int[] created = new int[1];
        return new PersistentTreeMap<>(put(root, key, value, created), created[0]);
    }

    /**
     * key 를 넣은 새 부분트리를 반환한다. node 는 건드리지 않는다.
     */
    private Node<K, V> put(Node<K, V> node, K key, V value, int[] created) {
        // TODO 4: 이 문제의 본체다. 06번의 put 과 모양이 거의 같은데 대입이 하나도 없다.
        //
        //   1. node 가 null 이면 새 잎을 만들어 반환한다
        //   2. key 가 더 작으면 **왼쪽만** 재귀로 새로 만들고,
        //      그 결과와 **원래 오른쪽 참조 그대로**를 붙인 새 노드를 반환한다
        //   3. 더 크면 거울상
        //   4. 같으면 값만 바꾼 새 노드를 반환한다. 자식 둘은 그대로 넘긴다
        //
        // 2번의 "원래 오른쪽 참조 그대로"가 이 자료구조의 전부다.
        // 거기서 오른쪽 부분트리를 복사하면 답은 똑같고 메모리만 O(n) 이 된다.
        // 계약 테스트로는 절대 안 잡힌다. StructuralSharingTest 의 assertSame 이 잡는다.
        //
        // created[0]++ 를 **new Node 를 부르는 자리마다** 넣어라. 넷 다 새 노드다.
        // 그 합이 이 put 이 쓴 메모리이고 measurement 가 그 값을 단언한다.
        // 맨 위에서 한 번만 올리면 네 경우를 모두 덮는다는 것도 보라.
        //
        // 06번에서는 while 로 내려가며 parent 를 들고 있었다. 여기서는 재귀라야 한다.
        // 되짚어 올라오면서 새 노드를 만들어야 하는데, 반복문에는 되짚을 길이 없기 때문이다.
        // (16번에서 "반환값을 부모의 left/right 에 다시 대입한다"고 했던 것과 같은 모양이다.
        //  다만 여기서는 대입이 아니라 새 노드의 생성자 인자로 들어간다.)
        throw new UnsupportedOperationException("TODO 4: put");
    }

    @Override
    public PersistentTreeMap<K, V> remove(K key) {
        requireKey(key);
        int[] created = new int[1];
        Node<K, V> newRoot = remove(root, key, created);
        if (newRoot == root) {
            return this;      // 없는 키였다. 새 버전을 만들 이유가 없다
        }
        return new PersistentTreeMap<>(newRoot, created[0]);
    }

    /**
     * key 를 지운 새 부분트리를 반환한다. 지울 것이 없으면 node 를 그대로 반환한다.
     */
    private Node<K, V> remove(Node<K, V> node, K key, int[] created) {
        // TODO 5: 06번의 삭제와 같은 세 경우인데, 값을 옮겨 심는 대신 새 노드를 만든다.
        //
        //   찾아 내려가는 동안
        //     하위 호출의 결과가 **원래 자식과 같은 객체**면 그 아래에서 아무것도 안 바뀐 것이다.
        //     그러면 이쪽도 새로 만들지 말고 node 를 그대로 반환한다.
        //     그래야 없는 키를 지웠을 때 뿌리까지 같은 참조가 올라와 위의 remove 가
        //     맵 자신을 돌려줄 수 있다. 이 검사를 빼면 없는 키를 지울 때마다
        //     경로 전체를 새로 만든다. 답은 맞고 메모리만 샌다.
        //
        //   찾았으면
        //     자식이 없거나 하나면 그 자식을 그대로 반환한다. 새로 만들 것이 없다
        //     자식이 둘이면 06번처럼 후속자(오른쪽 부분트리의 최솟값)를 쓴다.
        //       후속자의 키와 값을 가진 새 노드를 만들고, 오른쪽에는
        //       **후속자를 뺀 오른쪽 부분트리**를 붙인다. 그 일은 removeMin 이 한다
        //
        // 06번에서는 cur.key = successor.key 로 값을 덮어썼다. 여기서는 못 한다.
        // final 이기도 하고, 덮어쓰면 그 노드를 같이 쓰는 옛 버전이 조용히 바뀐다.
        throw new UnsupportedOperationException("TODO 5: remove");
    }

    /**
     * 가장 작은 노드를 뺀 새 부분트리. 내려간 길목만 새로 만든다.
     *
     * 왼쪽 끝까지 내려가서 그 노드의 오른쪽 자식을 대신 올리면 된다.
     * 왼쪽 끝이므로 왼쪽 자식은 없다.
     */
    private Node<K, V> removeMin(Node<K, V> node, int[] created) {
        if (node.left == null) {
            return node.right;
        }
        created[0]++;
        return new Node<>(node.key, node.value, removeMin(node.left, created), node.right);
    }
}
