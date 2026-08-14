package com.datastructure.filesystem;

/**
 * 연산 하나가 무엇을 얼마나 만졌는지.
 *
 * 두 구현은 답이 같다. 그러니 답만 보면 어느 쪽을 써도 상관없다는 결론이 나온다.
 * 무엇이 다른지는 이 두 수로만 드러난다.
 *
 *   visitedNodes       경로를 따라가며 들여다본 자리의 수
 *   rewrittenEntries   연산 때문에 키를 다시 쓴 항목의 수
 *
 * 두 번째가 이 박스의 핵심 자다. 디렉터리를 옮길 때 트리는 링크 하나를 고치고,
 * 평면 맵은 그 아래 있던 항목 전부의 키를 다시 쓴다. 답은 같고 일이 다르다.
 *
 * 이 인터페이스에는 TODO 가 없다.
 */
public interface FsStats {

    /** 마지막 연산이 들여다본 자리의 수. 연산 시작마다 0 으로 되돌아간다. */
    long visitedNodes();

    /** 마지막 연산이 키를 다시 쓴 항목의 수. */
    long rewrittenEntries();
}
