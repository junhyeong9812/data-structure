package com.datastructure.lsm;

/**
 * 삭제 표시.
 *
 * LSM 트리는 지우지 않는다. 지울 자리를 찾아가는 것이 곧 임의 읽기와 임의 쓰기이기 때문이다.
 * 대신 "이 키는 지워졌다" 는 표식을 값 자리에 넣어 새로 쓴다.
 *
 *   delete(k)  ->  put(k, MARKER)
 *
 * 그래서 삭제가 쓰기와 정확히 같은 비용이고, 삭제해도 저장 공간이 줄지 않는다.
 * 오히려 는다. 그 대가를 compaction 이 나중에 치른다.
 *
 * 값 자리에 null 을 쓰지 않는 이유가 있다. 이 저장소에서 null 은
 * "이 층에는 그 키가 없다" 는 뜻으로 이미 쓰이고 있어서, 삭제와 부재를 구별할 수 없게 된다.
 * 구별을 못 하면 아래 층의 옛 값이 되살아난다.
 *
 * 이 클래스에는 TODO 가 없다.
 */
public final class Tombstone {

    /** 하나뿐인 표식. 동일성(==) 으로 판별하므로 equals 를 쓸 일이 없다. */
    public static final Tombstone MARKER = new Tombstone();

    private Tombstone() {
    }

    public static boolean is(Object value) {
        return value == MARKER;
    }

    @Override
    public String toString() {
        return "<tombstone>";
    }
}
