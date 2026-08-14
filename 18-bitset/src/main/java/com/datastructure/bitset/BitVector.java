package com.datastructure.bitset;

import java.util.List;

/**
 * 켜짐/꺼짐 비트의 집합. 11번 블룸 필터에서 쓰기만 하고 안 가르친 것이다.
 *
 * 자바에서 `boolean[]` 은 원소 하나가 1비트가 아니라 1바이트다.
 * JVM 이 바이트 단위로 주소를 잡기 때문이다. 100만 개면 1MB 다.
 * 비트로 눌러 담으면 125KB 다. 8배 차이이고, 그것만으로도 이유가 된다.
 *
 * 그런데 진짜 이유는 메모리가 아니다.
 *
 * 집합 연산이 64배 빨라진다.
 * `boolean[]` 의 교집합은 100만 번 돈다. long 배열이면 `a[i] & b[i]` 를 15,625번 돈다.
 * CPU 가 64비트를 한 번에 and 하기 때문이다. 개수를 세는 것도 popcount 명령 하나로 끝난다.
 *
 * 이게 데이터베이스의 비트맵 인덱스가 존재하는 이유다.
 * "성별=남 AND 지역=서울 AND 등급=VIP" 를 각 조건의 비트맵을 and 하는 것으로 답한다.
 * 행을 하나씩 보는 대신 워드 단위로 접는다.
 *
 * | | boolean[] | long[] 비트셋 |
 * |---|---|---|
 * | 100만 비트 메모리 | 1,000,000 바이트 | 125,000 바이트 |
 * | 교집합 걸음 수 | 1,000,000 | 15,625 |
 * | 개수 세기 | 1,000,000 번 비교 | 15,625 번 popcount |
 * | 다음 켜진 비트 | 하나씩 본다 | 꺼진 워드는 통째로 건너뛴다 |
 *
 * 구현이 셋이다.
 *
 *   BooleanArrayBitSet  boolean[] 그대로. 먼저 만들어보고 무엇이 문제인지 본다
 *   WordBitSet          long[] 에 눌러 담는다. 이 문제의 본체
 *   SparseBitSet        아주 드문드문 켜질 때. 워드를 맵에 담는다
 *
 * 크기는 만들 때 정한다. 자바 표준 `java.util.BitSet` 은 자동으로 늘어나지만,
 * 여기서는 고정 크기로 둔다. 비트맵 인덱스의 실제 쓰임이 그렇고(행 수가 정해져 있다),
 * 크기가 같아야 집합 연산이 단순해진다.
 *
 * 이 인터페이스에는 TODO 가 없다. 계약은 주어지는 것이다.
 */
public interface BitVector {

    /** 담을 수 있는 비트 수. 만들 때 정하고 안 바뀐다. */
    int size();

    boolean get(int index);

    /** 켠다. */
    void set(int index);

    void set(int index, boolean value);

    /** 끈다. */
    void clear(int index);

    /** 뒤집는다. */
    void flip(int index);

    /** 전부 끈다. */
    void clearAll();

    /**
     * 전부 뒤집는다. 여집합이다.
     *
     * 이 연산이 있어야 "꼬리 비트" 문제가 실제로 생긴다.
     * set/clear 만 있으면 범위 검사 때문에 남는 자리가 켜질 일이 없지만,
     * 워드를 통째로 뒤집으면 크기를 넘는 자리까지 켜진다.
     */
    void flipAll();

    /** 켜진 비트 수. */
    int cardinality();

    boolean isEmpty();

    /**
     * from 이상에서 처음 켜진 비트의 자리. 없으면 -1.
     *
     * 이 메서드가 있는 이유는 켜진 것만 순회하기 위해서다.
     * 100만 비트 중 10개만 켜져 있으면 10번만 돌아야 한다.
     */
    int nextSetBit(int from);

    /** 교집합. 상대와 크기가 같아야 한다. */
    void and(BitVector other);

    /** 합집합. */
    void or(BitVector other);

    /** 대칭차집합. */
    void xor(BitVector other);

    /** 차집합. this 에서 other 를 뺀다. */
    void andNot(BitVector other);

    /** 켜진 비트의 자리를 오름차순으로. */
    List<Integer> toList();

    /**
     * 저장에 쓰는 단위의 개수. boolean 배열이면 원소 수, long 배열이면 워드 수다.
     *
     * 집합 연산과 개수 세기가 정확히 이만큼 돈다.
     * 측정을 위해 열어둔 것이다(07번 힙의 moves, 14번 유니온 파인드의 depthOf 와 같다).
     */
    int unitCount();

    /** 저장에 쓰는 대략의 바이트 수. */
    long memoryBytes();
}
