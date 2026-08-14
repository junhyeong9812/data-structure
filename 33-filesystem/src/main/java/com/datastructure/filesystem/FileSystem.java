package com.datastructure.filesystem;

import java.util.List;

/**
 * 인메모리 파일 시스템의 계약.
 *
 * 두 구현이 이 계약을 지킨다. 답은 같고 비용이 다르다.
 *
 *   TreeFileSystem       이름 -&gt; 자식 맵을 층층이 쌓는다. 진짜 트리
 *   FlatPathFileSystem   전체 경로를 키로 쓰는 맵 하나. 트리가 없다
 *
 * 두 번째가 훨씬 쉽다. 그리고 대부분의 연산에서 답이 같다.
 * 어디서 갈라지는지가 이 박스의 내용이다.
 *
 * 경로 규칙은 Paths 가 단독으로 소유한다. 여기서는 정규화된 경로만 다룬다고 본다.
 *
 * 이 인터페이스에는 TODO 가 없다.
 */
public interface FileSystem {

    /** 디렉터리 하나를 만든다. 부모가 없으면 던진다. mkdir -p 가 아니다. */
    void mkdir(String path);

    /** 없는 부모를 전부 만들면서 내려간다. mkdir -p 다. */
    void mkdirs(String path);

    /** 빈 파일을 만든다. 이미 있으면 내용을 지우지 않는다. */
    void touch(String path);

    /** 파일에 내용을 쓴다. 없으면 만든다. */
    void write(String path, String content);

    /** 파일 내용. 디렉터리이거나 없으면 던진다. */
    String read(String path);

    /** 디렉터리의 자식 이름. 늘 이름 오름차순이다. 순서가 정해져야 답이 하나로 정해진다. */
    List<String> ls(String path);

    /** 파일 하나를 지운다. 디렉터리면 던진다. */
    void rm(String path);

    /** 빈 디렉터리를 지운다. 안에 무언가 있으면 던진다. */
    void rmdir(String path);

    /** 서브트리째 지운다. rm -r 이다. */
    void rmr(String path);

    /** 옮기거나 이름을 바꾼다. 자기 자신의 안으로는 못 옮긴다. */
    void mv(String src, String dst);

    /** 복사한다. 디렉터리면 서브트리 전체를 복사한다. */
    void cp(String src, String dst);

    boolean exists(String path);

    boolean isDirectory(String path);

    /** 파일이면 내용의 길이, 디렉터리면 서브트리 안 모든 파일 길이의 합. */
    long size(String path);

    /** path 아래에서 이름이 정확히 name 인 것들의 전체 경로. 오름차순. */
    List<String> find(String path, String name);

    /**
     * 하드 링크. 같은 내용에 이름을 하나 더 붙인다.
     *
     * 이것 하나 때문에 구조가 트리가 아니게 된다. 자식이 부모를 둘 가지기 때문이다.
     * 그러면 "지운다"의 뜻도 바뀐다. 마지막 이름이 사라질 때만 내용이 죽는다.
     *
     * FlatPathFileSystem 은 이것을 못 한다. 경로가 곧 내용이라 이름을 나눌 자리가 없다.
     */
    void link(String existingPath, String newPath);

    /** 이 내용을 가리키는 이름의 개수. 하드 링크가 없으면 늘 1 이다. */
    int linkCount(String path);
}
