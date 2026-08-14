package com.datastructure.btree;

import java.util.ArrayList;
import java.util.List;

/**
 * 값을 잎에만 두고 잎끼리 이어놓은 B-트리.
 *
 * B-트리와 두 가지가 다르고, 그 둘이 실무에서 B+트리를 쓰게 만든다.
 *
 * 1. 내부 노드는 길잡이 키만 들고 있다. 값이 없다.
 *    그래서 같은 크기의 노드에 키를 더 많이 담는다. 팬아웃이 커지고 높이가 더 낮아진다.
 *    (디스크 블록 16KB 에 키+포인터만 넣으면 수천 개가 들어간다)
 *
 * 2. 잎이 사슬로 이어져 있다. 첫 잎에서 next 만 따라가면 전부가 정렬 순서로 나온다.
 *    범위 조회가 시작 잎을 찾고 그 다음부터는 그냥 걷기가 된다.
 *    디스크에서는 이게 순차 읽기라 임의 읽기보다 수십 배 빠르다.
 *
 * | | B-트리 | B+트리 |
 * |---|---|---|
 * | 값의 위치 | 모든 노드 | 잎에만 |
 * | 운 좋은 조회 | 뿌리에서 바로 | 늘 잎까지 내려간다 |
 * | 범위 조회 | 트리 순회 | 잎 사슬 걷기 |
 * | 키 중복 | 없다 | 구분키가 잎에 또 있다 |
 * | 쓰는 곳 | 파일 시스템 일부 | 거의 모든 DB 인덱스 |
 *
 * 세 번째 줄이 대가다. B-트리는 운이 좋으면 한 번에 끝나는데 여기서는 늘 끝까지 간다.
 * 그런데 실무에서는 범위 조회가 압도적으로 많아서(WHERE age BETWEEN, ORDER BY, 페이지네이션)
 * 그 대가를 치를 값어치가 있다.
 *
 * 구분키의 뜻: keys[i] 는 "children[i+1] 의 모든 키가 이 값 이상"이라는 경계다.
 * 값이 아니라 길잡이다. 지워진 키가 구분키로 남아 있어도 아무 문제가 없다.
 */
public class BPlusTree<K extends Comparable<K>, V> implements SearchTree<K, V> {

    static final class Node<K, V> {
        final boolean leaf;
        final List<K> keys = new ArrayList<>();
        final List<V> values = new ArrayList<>();
        final List<Node<K, V>> children = new ArrayList<>();
        Node<K, V> next;

        Node(boolean leaf) {
            this.leaf = leaf;
        }
    }

    private static final class Split<K, V> {
        final K key;
        final Node<K, V> right;

        Split(K key, Node<K, V> right) {
            this.key = key;
            this.right = right;
        }
    }

    private final int order;
    Node<K, V> root = new Node<>(true);
    private int size;

    /** order 는 노드 하나가 가질 수 있는 자식의 최대 수다. 키는 order-1 개까지. */
    public BPlusTree(int order) {
        if (order < 3) {
            throw new IllegalArgumentException("차수는 3 이상이어야 한다: " + order);
        }
        this.order = order;
    }

    private int maxKeys() {
        return order - 1;
    }

    private int minKeys() {
        return maxKeys() / 2;
    }

    /** keys 에서 key 이상인 첫 자리. */
    static <K extends Comparable<K>, V> int lowerBound(Node<K, V> node, K key) {
        int lo = 0;
        int hi = node.keys.size();
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (node.keys.get(mid).compareTo(key) < 0) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }

    /** 내부 노드에서 key 를 찾아 내려갈 자식의 인덱스. */
    /** 내부 노드에서 key 를 찾아 내려갈 자식의 인덱스. */
    static <K extends Comparable<K>, V> int childIndex(Node<K, V> node, K key) {
        // TODO 1: keys[i] 보다 **큰** 첫 자리를 찾는다. 그게 내려갈 자식 번호다.
        //
        // lowerBound 와 부등호 하나가 다르다. 그런데 그 하나가 정확성을 가른다.
        // 구분키가 "오른쪽은 이 값 **이상**"이므로, key 가 구분키와 같으면 **오른쪽**으로 가야 한다.
        // lowerBound 를 쓰면 왼쪽으로 가서 못 찾는다.
        throw new UnsupportedOperationException("TODO 1: childIndex");
    }

    Node<K, V> findLeaf(K key) {
        Node<K, V> cur = root;
        while (!cur.leaf) {
            cur = cur.children.get(childIndex(cur, key));
        }
        return cur;
    }

    @Override
    public V get(K key) {
        requireKey(key);
        Node<K, V> leaf = findLeaf(key);
        int i = lowerBound(leaf, key);
        if (i < leaf.keys.size() && leaf.keys.get(i).compareTo(key) == 0) {
            return leaf.values.get(i);
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        requireKey(key);
        if (value == null) {
            throw new IllegalArgumentException("값은 null 일 수 없다");
        }
        Object[] old = new Object[1];
        Split<K, V> split = insert(root, key, value, old);
        if (split != null) {
            Node<K, V> newRoot = new Node<>(false);
            newRoot.keys.add(split.key);
            newRoot.children.add(root);
            newRoot.children.add(split.right);
            root = newRoot;
        }
        @SuppressWarnings("unchecked")
        V result = (V) old[0];
        return result;
    }

    private Split<K, V> insert(Node<K, V> node, K key, V value, Object[] old) {
        if (node.leaf) {
            int i = lowerBound(node, key);
            if (i < node.keys.size() && node.keys.get(i).compareTo(key) == 0) {
                old[0] = node.values.get(i);
                node.values.set(i, value);
                return null;
            }
            node.keys.add(i, key);
            node.values.add(i, value);
            size++;
            return node.keys.size() > maxKeys() ? splitLeaf(node) : null;
        }
        int i = childIndex(node, key);
        Split<K, V> s = insert(node.children.get(i), key, value, old);
        if (s == null) {
            return null;
        }
        node.keys.add(i, s.key);
        node.children.add(i + 1, s.right);
        return node.keys.size() > maxKeys() ? splitInternal(node) : null;
    }

    private Split<K, V> splitLeaf(Node<K, V> node) {
        // TODO 2: 잎을 반으로 나누고 **사슬에 새 잎을 끼운다.**
        //
        // 올려보낼 키는 오른쪽 잎의 첫 키인데, **옮기는 것이 아니라 복사다.**
        // 그 키의 값은 잎에 있어야 하기 때문이다. 내부 노드로 올려보내고 잎에서 지우면 값을 잃는다.
        // **B-트리의 분할과 여기가 갈린다.**
        //
        // 사슬 잇기: right.next = node.next 를 **먼저** 하고 node.next = right 를 한다.
        // 순서를 바꾸면 뒤쪽이 통째로 끊긴다. 02번 연결 리스트와 같은 함정이다.
        throw new UnsupportedOperationException("TODO 2: splitLeaf");
    }

    private Split<K, V> splitInternal(Node<K, V> node) {
        // TODO 3: 내부 노드를 나눈다. 가운데 키는 **올려보내고 여기서 지운다.**
        //
        // splitLeaf 와 정반대다. 내부 노드의 키는 길잡이일 뿐이라 복사할 이유가 없다.
        // 잎에는 이미 실제 키가 있다.
        //
        // 키 k 개를 나누면 왼쪽 mid 개, 올림 1 개, 오른쪽 나머지가 된다.
        // 자식은 mid+1 개와 나머지로 나뉜다. **키보다 자식이 하나 많다는 것을 기억하라.**
        throw new UnsupportedOperationException("TODO 3: splitInternal");
    }

    @Override
    public V remove(K key) {
        requireKey(key);
        V old = get(key);
        if (old == null) {
            return null;
        }
        removeFrom(root, key);
        if (!root.leaf && root.children.size() == 1) {
            root = root.children.get(0);
        }
        size--;
        return old;
    }

    private void removeFrom(Node<K, V> node, K key) {
        if (node.leaf) {
            int i = lowerBound(node, key);
            if (i < node.keys.size() && node.keys.get(i).compareTo(key) == 0) {
                node.keys.remove(i);
                node.values.remove(i);
            }
            return;
        }
        int i = childIndex(node, key);
        Node<K, V> child = node.children.get(i);
        removeFrom(child, key);
        if (child.keys.size() < minKeys()) {
            fix(node, i);
        }
        // 구분키를 여기서 더 고칠 필요는 없다.
        // 구분키 s 는 "왼쪽은 s 미만, 오른쪽은 s 이상"만 지키면 되고,
        // 지우기로 오른쪽의 최솟값이 s 보다 커져도 그 성질은 그대로다.
        // (빌려오기와 병합에서는 고쳐야 한다. 키가 실제로 경계를 넘어가기 때문이다)
    }

    private void fix(Node<K, V> node, int i) {
        // TODO 5: BTree.fill 과 같은 우선순위다. 왼쪽 빌리기 -> 오른쪽 빌리기 -> 병합.
        //
        // 여기서는 내려갈 인덱스를 돌려줄 필요가 없다. **이미 다 지우고 올라온 뒤**라서다.
        // (B-트리는 내려가기 전에 채우고, B+트리는 올라오면서 고친다. 둘 다 되는 방식이다)
        throw new UnsupportedOperationException("TODO 5: fix");
    }

    private void borrowFromLeft(Node<K, V> node, int i) {
        Node<K, V> child = node.children.get(i);
        Node<K, V> left = node.children.get(i - 1);
        if (child.leaf) {
            child.keys.add(0, left.keys.remove(left.keys.size() - 1));
            child.values.add(0, left.values.remove(left.values.size() - 1));
            node.keys.set(i - 1, child.keys.get(0));
        } else {
            child.keys.add(0, node.keys.get(i - 1));
            child.children.add(0, left.children.remove(left.children.size() - 1));
            node.keys.set(i - 1, left.keys.remove(left.keys.size() - 1));
        }
    }

    private void borrowFromRight(Node<K, V> node, int i) {
        Node<K, V> child = node.children.get(i);
        Node<K, V> right = node.children.get(i + 1);
        if (child.leaf) {
            child.keys.add(right.keys.remove(0));
            child.values.add(right.values.remove(0));
            node.keys.set(i, right.keys.get(0));
        } else {
            child.keys.add(node.keys.get(i));
            child.children.add(right.children.remove(0));
            node.keys.set(i, right.keys.remove(0));
        }
    }

    private void merge(Node<K, V> node, int i) {
        // TODO 6: 잎이냐 내부 노드냐에 따라 다르다. **여기가 B-트리와 크게 갈린다.**
        //
        //   잎이면:     오른쪽 키/값을 왼쪽에 붙이고, **사슬을 다시 잇고**(left.next = right.next),
        //               부모의 구분키를 **그냥 버린다.** 그 키는 잎에 이미 있다
        //   내부면:     부모의 구분키를 **끌어내려** 가운데 넣는다. B-트리와 같다
        //
        // 잎 병합에서 next 를 안 이어주면 **그 뒤의 잎이 사슬에서 통째로 사라진다.**
        // 조회는 트리를 타므로 멀쩡한데 범위 조회만 조용히 틀린다. 찾기 아주 나쁜 버그다.
        throw new UnsupportedOperationException("TODO 6: merge");
    }

    /** 가장 왼쪽 잎. 여기서부터 next 를 따라가면 전체가 정렬 순서로 나온다. */
    Node<K, V> firstLeaf() {
        Node<K, V> cur = root;
        while (!cur.leaf) {
            cur = cur.children.get(0);
        }
        return cur;
    }

    @Override
    public List<K> keys() {
        List<K> out = new ArrayList<>(size);
        for (Node<K, V> leaf = firstLeaf(); leaf != null; leaf = leaf.next) {
            out.addAll(leaf.keys);
        }
        return out;
    }

    /** from 이상 to 이하를 정렬 순서로. 잎 사슬을 따라 걷기만 하면 된다. */
    /** from 이상 to 이하를 정렬 순서로. 잎 사슬을 따라 걷기만 하면 된다. */
    public List<K> keysInRange(K from, K to) {
        requireKey(from);
        requireKey(to);
        // TODO 4: **이 메서드가 B+트리의 존재 이유다.**
        //
        //   1. from 이 있을 잎을 찾는다 (O(log n), 트리를 한 번 내려간다)
        //   2. 그 잎의 from 자리부터 시작해 to 를 넘을 때까지 **next 를 따라 걷는다**
        //
        // 트리를 다시 내려갈 일이 없다. 디스크에서는 이게 순차 읽기다.
        // B-트리로 같은 것을 하려면 매번 트리를 순회해야 하고 그건 임의 읽기다.
        //
        // from > to 면 빈 리스트.
        throw new UnsupportedOperationException("TODO 4: keysInRange");
    }

    @Override
    public K firstKey() {
        Node<K, V> leaf = firstLeaf();
        return leaf.keys.isEmpty() ? null : leaf.keys.get(0);
    }

    @Override
    public K lastKey() {
        Node<K, V> cur = root;
        while (!cur.leaf) {
            cur = cur.children.get(cur.children.size() - 1);
        }
        return cur.keys.isEmpty() ? null : cur.keys.get(cur.keys.size() - 1);
    }

    @Override
    public int height() {
        int h = 1;
        Node<K, V> cur = root;
        while (!cur.leaf) {
            cur = cur.children.get(0);
            h++;
        }
        return h;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        root = new Node<>(true);
        size = 0;
    }

    int order() {
        return order;
    }

    private static void requireKey(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("키는 null 일 수 없다");
        }
    }
}
