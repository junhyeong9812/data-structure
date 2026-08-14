package com.datastructure.rope;

/**
 * 편집할 수 있는 문자열 저장소.
 *
 * <h2>계약은 값(value) 이다</h2>
 *
 * 모든 편집 연산이 저장소를 돌려준다. 호출자는 언제나 반환값을 써야 한다.
 * 구현이 새 것을 만들어 줄 수도 있고 자기 것을 고쳐 줄 수도 있는데,
 * 그 차이가 이 문제의 절반이다. 옛 참조를 계속 쓰면 어느 쪽인지에 따라 답이 달라진다.
 *
 * <h2>비용 계기</h2>
 *
 * 이 인터페이스에는 자료구조 교과서에 없는 메서드가 둘 있다.
 * charsCopiedByLastOp 과 charsCopiedTotal 이다. 시간을 재면 기계와 GC 에 따라 값이 흔들리므로
 * 이 문제집은 언제나 걸음 수를 센다. 여기서 세는 걸음은 "옮긴 글자 수" 다.
 *
 * 세는 규칙이 두 구현에서 같아야 비교가 성립한다. 규칙은 하나다.
 *
 *   저장소를 만들어 내는 연산(concat, insert, delete, split) 이 실제로 메모리에서
 *   메모리로 옮긴 글자만 센다. 이미 있는 문자열을 참조로 들고만 있으면 0 이다.
 *
 * substring 과 charAt 은 저장소를 안 만들므로 세지 않는다. 처음 문서를 적재하는 비용도 안 센다.
 * 두 구현이 같은 문서에서 0 으로 출발해야 편집 비용만 남는다.
 *
 * <h2>이 인터페이스에는 TODO 가 없다</h2>
 *
 * 계약은 주어지는 것이다.
 */
public interface CharSequenceStore {

    /** 담긴 글자 수. */
    int length();

    /** index 번째 글자. 범위 밖이면 IndexOutOfBoundsException. */
    char charAt(int index);

    /** [from, to) 구간의 문자열. from == to 면 빈 문자열이다. */
    String substring(int from, int to);

    /**
     * 이 저장소 뒤에 other 를 붙인 저장소. 양쪽 다 안 바뀐다.
     *
     * 여기가 이 문제의 출발점이다. 배열 위에서는 n + m 글자를 옮겨야 하지만
     * 트리 위에서는 뿌리 하나만 만들면 된다.
     */
    CharSequenceStore concat(CharSequenceStore other);

    /** index 자리에 s 를 끼워 넣은 저장소. index 는 0 부터 length 까지다. */
    CharSequenceStore insert(int index, String s);

    /** [from, to) 를 지운 저장소. from == to 면 아무 일도 안 일어난다. */
    CharSequenceStore delete(int from, int to);

    /** index 를 경계로 둘로 자른다. 왼쪽이 [0, index), 오른쪽이 [index, length) 다. */
    Split split(int index);

    /** 담긴 글자 전부. 참조 구현과 대조할 때 쓴다. */
    @Override
    String toString();

    /**
     * 이 저장소를 만들어 낸 마지막 연산이 옮긴 글자 수.
     *
     * "마지막 연산" 은 이 객체를 돌려준 그 연산이다. 시간 순서가 아니라 계보다.
     * a.insert(...) 가 b 를 돌려줬다면 b 에게 물어야 한다.
     */
    long charsCopiedByLastOp();

    /** 이 저장소에 이르기까지 누적된 복사량. 왼쪽 계보만 따라 더한다. */
    long charsCopiedTotal();

    /** split 의 결과 둘. */
    record Split(CharSequenceStore left, CharSequenceStore right) {
    }
}
