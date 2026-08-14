package com.datastructure.persistent;

import java.util.List;

/**
 * 바뀌지 않는 목록. 모든 연산이 새 목록을 반환한다.
 *
 * <h2>02번과 같은 자료구조인데 계약이 다르다</h2>
 *
 * 02번 연결 리스트에는 add, remove, reverse 가 있었고 전부 void 였다.
 * 부르고 나면 그 리스트가 바뀌어 있었고, 바뀌기 전 상태는 사라졌다.
 *
 * 여기서는 void 메서드가 하나도 없다. prepend 는 새 목록을 돌려주고
 * 원본은 그대로 남는다. 그래서 "5분 전 상태"를 물을 수 있다.
 *
 * <h2>그런데 왜 앞에만 붙이는가</h2>
 *
 * 노드가 다음만 가리키고 아무도 고치지 않으므로, 앞에 붙인 새 노드는
 * 옛 목록 전체를 꼬리로 그냥 가리키면 된다. 복사가 한 칸도 없다. O(1) 이다.
 *
 * 뒤에 붙이려면 마지막 노드를 고쳐야 하는데 고칠 수 없으므로
 * n 개를 전부 새로 만들어야 한다. O(n) 이다. 그래서 이 계약에는 append 가 없다.
 * 불변성이 어떤 연산은 공짜로, 어떤 연산은 불가능에 가깝게 만든다.
 *
 * <h2>대가</h2>
 *
 * get(i) 가 O(i) 다. 02번과 같은 이유이고 여기서도 고칠 수 없다.
 *
 * 이 인터페이스에는 TODO 가 없다. 계약은 주어지는 것이다.
 */
public interface PersistentList<E> {

    /**
     * 앞에 붙인 새 목록. 이 목록은 바뀌지 않는다.
     * 새 목록의 꼬리는 이 목록 자신이다. 복사하지 않는다.
     */
    PersistentList<E> prepend(E element);

    /** 첫 원소. 비었으면 NoSuchElementException. */
    E head();

    /** 첫 원소를 뺀 나머지. 비었으면 NoSuchElementException. */
    PersistentList<E> tail();

    int size();

    boolean isEmpty();

    /** index 번째 원소. O(index) 다. */
    E get(int index);

    /** 뒤집은 새 목록. 이쪽은 노드 n 개를 전부 새로 만든다. 무엇도 공유하지 못한다. */
    PersistentList<E> reverse();

    /** 앞에서부터 담은 보통의 List. 비교와 출력에 쓴다. */
    List<E> toList();
}
