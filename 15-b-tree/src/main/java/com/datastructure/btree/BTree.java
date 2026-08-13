package com.datastructure.btree;

import java.util.ArrayList;
import java.util.List;

/**
 * 노드 하나가 키를 여러 개 갖는 균형 트리.
 *
 * **06번 BST 와 자라는 방향이 반대다.**
 * BST 는 잎에 새 노드를 매달아 아래로 자란다. 그래서 순서가 나쁘면 한쪽으로 길어진다.
 * B-트리는 **뿌리가 쪼개질 때만 위로** 자란다. 그래서 무슨 순서로 넣어도
 * **모든 잎이 언제나 같은 깊이다.** 균형이 결과가 아니라 구조다.
 *
 * 최소 차수 t 를 정하면 (뿌리를 뺀) 모든 노드가 이걸 지킨다.
 *
 *   키 개수      t-1 개 이상 2t-1 개 이하
 *   자식 개수    (잎이 아니면) 키 개수 + 1
 *
 * t = 2 면 키가 1~3 개, 자식이 2~4 개다. 이걸 2-3-4 트리라고 부른다.
 * 실제 데이터베이스는 t 를 수백으로 잡는다. 노드 하나가 디스크 블록 하나가 되도록.
 *
 * **넣기의 요령: 내려가면서 미리 쪼갠다.**
 * 꽉 찬 노드를 만나면 그 자리에서 쪼개고 내려간다. 그러면 잎에 도착했을 때
 * 위로 되돌아갈 일이 없다. 한 번 내려가는 것으로 끝난다.
 * (디스크에서는 되돌아가기가 곧 다시 읽기라 이게 중요하다)
 *
 * **지우기가 이 문제집에서 가장 복잡한 코드다.**
 * 06번에서 "자식이 둘인 노드 삭제"가 어려웠는데, 여기서는 그 위에
 * "키가 모자라면 어떻게 하나"가 더 붙는다. 형제에게 빌리거나 형제와 합친다.
 */
public class BTree<K extends Comparable<K>, V> implements SearchTree<K, V> {

    static final class Node<K, V> {
        final List<K> keys = new ArrayList<>();
        final List<V> values = new ArrayList<>();
        final List<Node<K, V>> children = new ArrayList<>();

        boolean leaf() {
            return children.isEmpty();
        }
    }

    private final int minDegree;
    Node<K, V> root = new Node<>();
    private int size;

    /** minDegree(t) 가 2 면 2-3-4 트리다. 노드는 키를 t-1 개 이상 2t-1 개 이하로 갖는다. */
    public BTree(int minDegree) {
        if (minDegree < 2) {
            throw new IllegalArgumentException("최소 차수는 2 이상이어야 한다: " + minDegree);
        }
        this.minDegree = minDegree;
    }

    private int maxKeys() {
        return 2 * minDegree - 1;
    }

    /** node.keys 에서 key 가 들어갈 자리. 같은 키가 있으면 그 자리. */
    int lowerBound(Node<K, V> node, K key) {
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

    @Override
    public V get(K key) {
        requireKey(key);
        Node<K, V> cur = root;
        while (true) {
            int i = lowerBound(cur, key);
            if (i < cur.keys.size() && cur.keys.get(i).compareTo(key) == 0) {
                return cur.values.get(i);
            }
            if (cur.leaf()) {
                return null;
            }
            cur = cur.children.get(i);
        }
    }

    @Override
    public V put(K key, V value) {
        requireKey(key);
        if (value == null) {
            throw new IllegalArgumentException("값은 null 일 수 없다");
        }
        if (root.keys.size() == maxKeys()) {
            Node<K, V> newRoot = new Node<>();
            newRoot.children.add(root);
            splitChild(newRoot, 0);
            root = newRoot;
        }
        return insertNonFull(root, key, value);
    }

    /** parent.children[i] 가 꽉 찼을 때 반으로 쪼개고 가운데 키를 부모로 올린다. */
    private void splitChild(Node<K, V> parent, int i) {
        // TODO 1: 꽉 찬 자식(키 2t-1 개)을 셋으로 나눈다.
        //
        //   앞쪽 t-1 개  -> 원래 노드에 남는다
        //   가운데 1 개  -> **부모로 올라간다**
        //   뒤쪽 t-1 개  -> 새 노드로 간다
        //
        // 잎이 아니면 자식 2t 개도 t 개씩 나눈다.
        // 그리고 새 노드를 부모의 children 에서 **원래 노드 바로 뒤**에 끼운다.
        //
        // 순서에 주의하라. 가운데 키와 값을 **먼저 꺼내두고** 잘라내야 한다.
        // 자르고 나서 꺼내려 하면 이미 없다.
        //
        // 여기서 트리가 자란다. 부모가 뿌리이고 뿌리가 꽉 차 있으면
        // put 이 미리 새 뿌리를 만들어두므로 이 메서드는 그 경우를 몰라도 된다.
        throw new UnsupportedOperationException("TODO 1: splitChild");
    }

    private V insertNonFull(Node<K, V> node, K key, V value) {
        // TODO 2: 꽉 차지 않은 노드에 넣는다. 내려가면서 꽉 찬 자식을 미리 쪼갠다.
        //
        //   1. 이 노드에 같은 키가 있으면 값만 갈고 옛 값을 준다
        //   2. 잎이면 자리에 끼워 넣는다
        //   3. 아니면 내려갈 자식이 꽉 찼는지 본다. 꽉 찼으면 **먼저 쪼갠다**
        //
        // 3번 뒤가 함정이다. 쪼개면 가운데 키가 이 노드로 올라와
        // **어느 쪽으로 내려가야 하는지가 바뀐다.** 올라온 키와 다시 비교해야 한다.
        // 그리고 올라온 키가 찾던 키일 수도 있다. 그 경우도 처리해야 한다.
        throw new UnsupportedOperationException("TODO 2: insertNonFull");
    }

    @Override
    public V remove(K key) {
        requireKey(key);
        if (get(key) == null) {
            return null;
        }
        V old = get(key);
        removeFrom(root, key);
        if (root.keys.isEmpty() && !root.leaf()) {
            root = root.children.get(0);
        }
        size--;
        return old;
    }

    private void removeFrom(Node<K, V> node, K key) {
        // TODO 3: 이 문제집에서 가장 경우가 많은 메서드다. 넷이다.
        //
        //   A. 이 노드에 있고 잎이다        -> 그냥 뺀다
        //   B. 이 노드에 있고 잎이 아니다   -> 06번의 "자식 둘인 노드 삭제"와 같은 문제다
        //   C. 없고 잎이다                  -> 아무것도 안 한다
        //   D. 없고 잎이 아니다             -> 해당 자식으로 내려간다
        //
        // **B 가 핵심이다.** 06번에서는 선행자나 후속자를 데려왔다. 여기서도 같은데,
        // 데려올 쪽이 키를 t 개 이상 갖고 있어야 한다(빼도 최소를 안 깨야 하므로).
        //   - 왼쪽 자식이 넉넉하면 선행자(왼쪽 부분트리의 가장 큰 키)를 올리고 거기서 지운다
        //   - 오른쪽이 넉넉하면 후속자를 올리고 거기서 지운다
        //   - **둘 다 빠듯하면** 좌우와 이 키를 하나로 합치고 거기서 지운다
        //
        // **D 에서 내려가기 전에 미리 채워야 한다.** 넣기에서 미리 쪼갰던 것과 대칭이다.
        // 자식이 t-1 개뿐이면 형제에게 빌리거나 형제와 합친 뒤에 내려간다.
        // 그래야 되돌아올 일이 없다.
        //
        // 합치면 자식 인덱스가 바뀐다. fill 이 **내려갈 인덱스를 돌려주는 이유**가 그것이다.
        throw new UnsupportedOperationException("TODO 3: removeFrom");
    }

    /** children[i] 의 키가 모자라면 형제에게 빌리거나 병합한다. 내려갈 자식 인덱스를 준다. */
    /** children[i] 의 키가 모자라면 형제에게 빌리거나 병합한다. 내려갈 자식 인덱스를 준다. */
    private int fill(Node<K, V> node, int i) {
        // TODO 4: 우선순위가 있다.
        //
        //   1. 왼쪽 형제가 넉넉하면 빌린다
        //   2. 오른쪽 형제가 넉넉하면 빌린다
        //   3. 둘 다 빠듯하면 합친다
        //
        // **빌리기를 먼저 시도하는 이유**는 합치면 트리가 낮아질 수 있어 비용이 크기 때문이다.
        //
        // 3번에서 오른쪽 형제가 없으면(맨 끝 자식이면) 왼쪽 형제와 합쳐야 하고,
        // 그러면 **내려갈 자식이 i-1 번이 된다.** 이 반환값을 안 쓰면 엉뚱한 곳으로 내려간다.
        throw new UnsupportedOperationException("TODO 4: fill");
    }

    private void borrowFromLeft(Node<K, V> node, int i) {
        Node<K, V> child = node.children.get(i);
        Node<K, V> left = node.children.get(i - 1);

        child.keys.add(0, node.keys.get(i - 1));
        child.values.add(0, node.values.get(i - 1));
        node.keys.set(i - 1, left.keys.remove(left.keys.size() - 1));
        node.values.set(i - 1, left.values.remove(left.values.size() - 1));
        if (!left.leaf()) {
            child.children.add(0, left.children.remove(left.children.size() - 1));
        }
    }

    private void borrowFromRight(Node<K, V> node, int i) {
        Node<K, V> child = node.children.get(i);
        Node<K, V> right = node.children.get(i + 1);

        child.keys.add(node.keys.get(i));
        child.values.add(node.values.get(i));
        node.keys.set(i, right.keys.remove(0));
        node.values.set(i, right.values.remove(0));
        if (!right.leaf()) {
            child.children.add(right.children.remove(0));
        }
    }

    /** children[i] 와 children[i+1] 을 keys[i] 를 가운데 끼워 하나로 합친다. */
    /** children[i] 와 children[i+1] 을 keys[i] 를 가운데 끼워 하나로 합친다. */
    private void mergeChildren(Node<K, V> node, int i) {
        // TODO 5: 왼쪽 + 부모의 구분키 + 오른쪽 을 왼쪽 하나로 만든다.
        //
        // **순서가 중요하다.** 구분키가 왼쪽 키들 뒤, 오른쪽 키들 앞에 와야 정렬이 유지된다.
        // 그리고 부모에서 그 키와 오른쪽 자식 참조를 빼야 한다.
        //
        // 합친 결과가 키 (t-1) + 1 + (t-1) = 2t-1 개다. 정확히 최대치다. **넘치지 않는다.**
        throw new UnsupportedOperationException("TODO 5: mergeChildren");
    }

    @Override
    public List<K> keys() {
        List<K> out = new ArrayList<>(size);
        collect(root, out);
        return out;
    }

    private void collect(Node<K, V> node, List<K> out) {
        if (node.leaf()) {
            out.addAll(node.keys);
            return;
        }
        for (int i = 0; i < node.keys.size(); i++) {
            collect(node.children.get(i), out);
            out.add(node.keys.get(i));
        }
        collect(node.children.get(node.children.size() - 1), out);
    }

    @Override
    public K firstKey() {
        if (size == 0) {
            return null;
        }
        Node<K, V> cur = root;
        while (!cur.leaf()) {
            cur = cur.children.get(0);
        }
        return cur.keys.get(0);
    }

    @Override
    public K lastKey() {
        if (size == 0) {
            return null;
        }
        Node<K, V> cur = root;
        while (!cur.leaf()) {
            cur = cur.children.get(cur.children.size() - 1);
        }
        return cur.keys.get(cur.keys.size() - 1);
    }

    @Override
    public int height() {
        int h = 1;
        Node<K, V> cur = root;
        while (!cur.leaf()) {
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
        root = new Node<>();
        size = 0;
    }

    int minDegree() {
        return minDegree;
    }

    private static void requireKey(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("키는 null 일 수 없다");
        }
    }
}
