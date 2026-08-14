package com.datastructure.filesystem;

/**
 * 파일의 내용. 이름과 분리돼 있다.
 *
 * 실제 파일 시스템의 inode 가 이 자리다. 이름은 디렉터리에 있고 내용은 여기 있다.
 * 둘을 나누면 이름 하나에 내용 하나라는 전제가 사라지고, 그래서 하드 링크가 가능해진다.
 *
 * 나눈 대가가 참조 수 관리다. 이름이 사라질 때마다 하나씩 내리고,
 * 0 이 되는 순간에만 내용이 죽는다. 이 수를 안 세면 둘 중 하나가 일어난다.
 *
 *   너무 일찍 죽인다   이름 하나를 지웠는데 다른 이름으로 읽던 것이 같이 사라진다
 *   영영 안 죽인다     이름을 다 지웠는데 내용이 남는다. 실제 시스템에서는 누수다
 *
 * 앞쪽이 11번 블룸 필터에서 "A 를 지우면 B 가 같이 사라진다"고 했던 것과 같은 모양이다.
 * 거기서는 못 고쳤고(비트를 나눠 쓰니까) 여기서는 세어서 고친다.
 *
 * 이 클래스에는 TODO 가 없다. 세는 규칙 자체가 문제의 답이라 미리 세워둔다.
 */
public final class Blob {

    private String content;
    private int links;

    Blob(String content) {
        this.content = content == null ? "" : content;
        this.links = 1;
    }

    public String content() {
        return content;
    }

    void setContent(String value) {
        this.content = value == null ? "" : value;
    }

    /** 이 내용을 가리키는 이름의 개수. */
    public int links() {
        return links;
    }

    void retain() {
        links++;
    }

    /** 이름 하나가 사라졌다. 0 이 되면 내용이 죽은 것이다. */
    boolean release() {
        links--;
        return links <= 0;
    }
}
