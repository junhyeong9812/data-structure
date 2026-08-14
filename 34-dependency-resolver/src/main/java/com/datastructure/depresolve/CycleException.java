package com.datastructure.depresolve;

import java.util.List;

/**
 * 순환이 있어서 순서를 정할 수 없다.
 *
 * 메시지에 경로를 담는다. "순환이 있습니다" 만 던지면 받는 쪽이 할 수 있는 일이 없다.
 * 어느 셋이 서로 물려 있는지 알아야 고칠 수 있고, 그것을 아는 것이 이 박스의 본론이다.
 *
 * path 가 빈 목록일 수 있다. 순환이 있다는 것만 알고 어디인지는 모르는 구현이 있기 때문이다.
 * 그 경우도 예외는 던져야 한다. 모르는 것과 없는 것은 다르다.
 *
 * 이 클래스에는 TODO 가 없다.
 */
public class CycleException extends RuntimeException {

    private final List<String> path;

    public CycleException(List<String> path) {
        super(path.isEmpty()
                ? "순환이 있다. 경로는 이 구현이 알려주지 못한다"
                : "순환이 있다: " + String.join(" -> ", path));
        this.path = List.copyOf(path);
    }

    /** 도는 경로. 처음과 끝이 같다. 모르는 구현이면 빈 목록. */
    public List<String> path() {
        return path;
    }
}
