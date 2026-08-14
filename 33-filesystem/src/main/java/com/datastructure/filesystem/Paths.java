package com.datastructure.filesystem;

import java.util.ArrayList;
import java.util.List;

/**
 * 경로 문자열을 다루는 규칙 전부. 이 박스의 버그 밀집 구역이다.
 *
 * 파일 시스템 본체는 트리를 걷는 일만 한다. 어려운 것은 전부 여기 있다.
 * 규칙을 한 곳에 모아두는 이유는, 흩어놓으면 구현마다 조금씩 다르게 해석하기 때문이다.
 * 그러면 두 구현의 답이 갈리는데 원인이 트리가 아니라 문자열에 있다.
 *
 * <h2>정규화 규칙</h2>
 *
 * <pre>
 *   //a///b   -&gt;  /a/b        빈 조각은 버린다
 *   /a/b/     -&gt;  /a/b        후행 슬래시는 없앤다
 *   /a/./b    -&gt;  /a/b        점 하나는 제자리다
 *   /a/b/..   -&gt;  /a          점 둘은 한 칸 올라간다
 *   /..       -&gt;  /           루트 위는 루트다. 여기가 함정이다
 *   /a/../..  -&gt;  /           올라갈 곳이 없으면 그냥 머문다
 * </pre>
 *
 * 마지막 셋을 빼먹으면 경로가 루트 밖으로 나간다. 실제 시스템에서는 이것이 취약점이다
 * ({@code ../../etc/passwd} 로 시작하는 사고가 전부 이 자리에서 난다).
 * 여기서는 취약점이 아니라 조용한 오답이 된다. 어느 쪽이든 테스트가 없으면 안 보인다.
 *
 * 상대 경로는 다루지 않는다. 모든 경로는 슬래시로 시작한다. 현재 디렉터리 개념을 넣으면
 * 문제가 커지는데, 그 크기가 이 박스가 가르치려는 것과 상관이 없다.
 */
public final class Paths {

    /** 루트의 정규 표기. */
    public static final String ROOT = "/";

    private Paths() {
    }

    /**
     * 경로를 정규 표기로 만든다. 위 표의 규칙 전부가 여기 있다.
     */
    public static String normalize(String path) {
        require(path);
        // TODO 1: 위 표의 규칙 전부. 조각을 앞에서부터 쌓되 ".." 이면 하나 걷어낸다.
        //
        // "/a/b" 를 만들 때 조각을 그냥 이어붙이면 루트에서 슬래시가 겹친다.
        // 그리고 **걷어낼 것이 없을 때** 무엇을 할지가 이 메서드의 전부다.
        // 그 자리를 비워두면 경로가 루트 밖을 가리키고, 예외는 안 난다.
        throw new UnsupportedOperationException("TODO 1: normalize");
    }

    /** 정규화한 뒤의 조각들. 루트는 빈 목록이다. */
    public static List<String> split(String path) {
        // TODO 2: 정규화한 뒤 조각으로 쪼갠다. 루트는 빈 목록이다.
        //
        // 정규화를 건너뛰고 쪼개면 ".." 이 조각으로 남는다. 그 뒤로는 전부 어긋난다.
        throw new UnsupportedOperationException("TODO 2: split");
    }

    /**
     * 부모 경로. 루트의 부모는 루트다.
     *
     * 여기서 문자열을 자르지 않고 split 을 거쳐 다시 잇는다.
     * 잘라 쓰면 "/a/b/.." 같은 입력에서 부모가 "/a/b" 로 나온다. 정규화 전의 모양을 자르기 때문이다.
     */
    public static String parent(String path) {
        // TODO 3: 부모 경로. 루트의 부모는 루트다.
        //
        // 마지막 슬래시에서 문자열을 자르고 싶어지는데, 위 javadoc 이 그러지 말라는 이유를 적어뒀다.
        throw new UnsupportedOperationException("TODO 3: parent");
    }

    /** 마지막 조각. 루트의 이름은 빈 문자열이다. */
    public static String name(String path) {
        List<String> parts = split(path);
        return parts.isEmpty() ? "" : parts.get(parts.size() - 1);
    }

    /** 부모 경로와 이름을 잇는다. 루트에 이으면 슬래시가 겹치지 않아야 한다. */
    public static String join(String parent, String name) {
        // TODO 4: 부모와 이름을 잇는다.
        //
        // 루트에 이을 때만 모양이 다르다. 그냥 이으면 "//a" 가 된다.
        throw new UnsupportedOperationException("TODO 4: join");
    }

    /**
     * ancestor 가 path 의 조상이거나 같은가.
     *
     * 문자열 startsWith 로 하면 "/ab" 가 "/a" 의 자손으로 나온다. 조각 단위로 봐야 한다.
     * mv 가 자기 자신 안으로 들어가는 것을 막을 때 이 판정을 쓴다.
     */
    public static boolean isAncestorOrSame(String ancestor, String path) {
        // TODO 5: ancestor 가 path 의 조상이거나 같은가.
        //
        // startsWith 로 쓰면 대부분의 테스트가 통과한다. 딱 하나가 안 통과한다.
        // 그 하나를 못 잡으면 mv 한 번에 관계없는 형제 디렉터리가 끌려간다.
        throw new UnsupportedOperationException("TODO 5: isAncestorOrSame");
    }

    private static void require(String path) {
        if (path == null) {
            throw new IllegalArgumentException("경로가 null 이다");
        }
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("절대 경로만 받는다: " + path);
        }
    }
}
