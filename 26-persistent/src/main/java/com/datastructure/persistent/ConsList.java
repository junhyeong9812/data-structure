package com.datastructure.persistent;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * 앞에만 붙이는 불변 목록. cons 는 리스프에서 온 이름이다.
 *
 * <h2>노드가 곧 목록이다</h2>
 *
 * 02번에서는 SinglyLinkedList 라는 껍데기가 head 노드를 들고 있었다.
 * 여기서는 그 구분이 없다. 목록 하나가 곧 노드 하나이고, 꼬리도 목록이다.
 *
 * 그래서 tail() 이 아무것도 만들지 않고 이미 있는 목록을 그대로 돌려줄 수 있다.
 * 그리고 prepend 는 새 노드 하나만 만들고 옛 목록 전체를 꼬리로 가리킨다.
 * 노드를 아무도 고치지 않으므로 같이 써도 안전하다.
 *
 * <h2>대가</h2>
 *
 * get(i) 가 O(i) 다. 앞에서부터 세는 수밖에 없다. 02번과 같은 이유다.
 * 뒤에 붙이는 연산은 아예 없다. 있으면 O(n) 이라 계약에 넣지 않았다.
 *
 * 참고: 필드 이름 head, tail, size 는 테스트가 직접 들여다본다.
 */
public final class ConsList<E> implements PersistentList<E> {

    // 빈 목록은 상태가 없으므로 여러 개 있을 이유가 없다. 모든 목록의 끝이 이 하나다.
    private static final ConsList<Object> EMPTY = new ConsList<>();

    private final E head;
    private final ConsList<E> tail;
    private final int size;

    private ConsList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * 이 생성자가 private 인 것이 중요하다.
     * 밖에서 노드를 만들 수 있으면 남의 꼬리에 아무 목록이나 붙일 수 있다.
     */
    private ConsList(E head, ConsList<E> tail) {
        this.head = head;
        this.tail = tail;
        this.size = tail.size + 1;      // size 를 들고 있으므로 size() 가 O(1) 이다
    }

    // ------------------------------------------------------------------
    // 채워져 있는 부분
    // ------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public static <E> ConsList<E> empty() {
        return (ConsList<E>) EMPTY;
    }

    /** 준 순서 그대로의 목록. 뒤에서부터 앞에 붙인다. */
    @SafeVarargs
    public static <E> ConsList<E> of(E... elements) {
        ConsList<E> list = empty();
        for (int i = elements.length - 1; i >= 0; i--) {
            list = list.prepend(elements[i]);
        }
        return list;
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
    public E head() {
        if (size == 0) {
            throw new NoSuchElementException("빈 목록에는 머리가 없다");
        }
        return head;
    }

    @Override
    public ConsList<E> tail() {
        if (size == 0) {
            throw new NoSuchElementException("빈 목록에는 꼬리가 없다");
        }
        return tail;
    }

    @Override
    public List<E> toList() {
        List<E> out = new ArrayList<>(size);
        for (ConsList<E> cur = this; cur.size > 0; cur = cur.tail) {
            out.add(cur.head);
        }
        return out;
    }

    /**
     * 값이 같으면 같다고 본다.
     *
     * 그래서 assertEquals 로는 구조 공유를 검증할 수 없다.
     * 통째로 복사한 목록도 값은 같으므로 통과한다.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConsList<?> other) || size != other.size) {
            return false;
        }
        ConsList<?> a = this;
        ConsList<?> b = other;
        while (a.size > 0) {
            if (!Objects.equals(a.head, b.head)) {
                return false;
            }
            a = a.tail;
            b = b.tail;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 1;
        for (ConsList<E> cur = this; cur.size > 0; cur = cur.tail) {
            h = 31 * h + Objects.hashCode(cur.head);
        }
        return h;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (ConsList<E> cur = this; cur.size > 0; cur = cur.tail) {
            if (cur != this) {
                sb.append(", ");
            }
            sb.append(cur.head);
        }
        return sb.append(']').toString();
    }

    // ------------------------------------------------------------------
    // 여기부터가 본체
    // ------------------------------------------------------------------

    /**
     * 앞에 붙인 새 목록. 이 목록은 바뀌지 않는다.
     */
    @Override
    public ConsList<E> prepend(E element) {
        // TODO 1: 한 줄이다. 다만 그 한 줄이 이 문제집의 새 아이디어다.
        //
        //   새 노드의 꼬리로 **무엇을 넘길 것인가.**
        //   옛 목록을 복사한 것을 넘기면 O(n) 이고, 옛 목록 자신을 넘기면 O(1) 이다.
        //
        // 복사해도 답은 똑같이 나온다. toList 도 get 도 equals 도 전부 통과한다.
        // 갈리는 것은 assertSame 하나뿐이다.
        //   b = a.prepend(1) 일 때 b.tail() 이 a 와 **같은 객체**여야 한다.
        //
        // 그래도 되는 이유는 아무도 노드를 고치지 않기 때문이다.
        // 02번에서 같은 짓을 하면 한쪽에서 add 한 것이 다른 쪽에 보인다.
        throw new UnsupportedOperationException("TODO 1: prepend");
    }

    /**
     * index 번째 원소. O(index) 다.
     */
    @Override
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("인덱스 " + index + ", 크기 " + size);
        }
        // TODO 2: 앞에서부터 index 번 꼬리를 따라간다.
        //
        // 02번의 node(index) 와 같은 순회다. 여기서도 지름길은 없다.
        // 앞에 붙이는 것이 O(1) 인 대가로 인덱스 접근이 O(i) 인 것이고,
        // 그 둘은 같은 사실의 앞뒤다.
        throw new UnsupportedOperationException("TODO 2: get");
    }

    /**
     * 뒤집은 새 목록.
     */
    @Override
    public ConsList<E> reverse() {
        // TODO 3: 빈 목록에서 시작해 앞에서부터 읽어 앞에 붙인다.
        //
        //   [1, 2, 3] 을 앞에서부터 읽으면 1, 2, 3 이고
        //   그것을 차례로 앞에 붙이면 [3, 2, 1] 이 된다.
        //
        // 02번 reverse 는 노드의 next 를 뒤집어 제자리에서 했다.
        // 여기서는 고칠 수 없으므로 노드 n 개를 전부 새로 만든다.
        // 방향이 반대라 재사용할 수 있는 꼬리가 하나도 없기 때문이다.
        //
        // **불변이 공짜로 주는 것은 prepend 뿐이다.** reverse 는 제값을 낸다.
        throw new UnsupportedOperationException("TODO 3: reverse");
    }
}
