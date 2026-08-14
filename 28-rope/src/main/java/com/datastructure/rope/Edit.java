package com.datastructure.rope;

/**
 * 편집 하나. 삽입이거나 삭제다.
 *
 * 에디터가 키 입력을 이렇게 적는다. 커서 자리에 글자 하나를 넣거나, 고른 구간을 지우거나.
 * 이 목록을 그대로 저장소에 흘려 넣는 것이 RopeProblems.applyEdits 다.
 *
 * 이 파일에는 TODO 가 없다.
 */
public sealed interface Edit {

    /** index 자리에 text 를 넣는다. */
    record Insert(int index, String text) implements Edit {
    }

    /** [from, to) 를 지운다. */
    record Delete(int from, int to) implements Edit {
    }
}
