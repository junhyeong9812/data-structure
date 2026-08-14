package com.datastructure.persistent;

import java.util.ArrayList;
import java.util.List;

/**
 * 버전을 전부 들고 있는 저장소. undo, redo, 그리고 아무 시점 조회.
 *
 * <h2>이 클래스에 어려운 것이 없다는 것이 요점이다</h2>
 *
 * 버전 목록은 PersistentTreeMap 참조의 배열일 뿐이다.
 * 맵이 불변이라 참조 하나가 그 시점의 상태 전체를 뜻하기 때문이다.
 *
 * 05번 해시맵이나 06번 트리로 같은 것을 만들려면 시점마다 맵을 통째로 복사해야 한다.
 * 버전 m 개에 O(m n) 메모리다. 여기서는 O(n + m log n) 이다.
 * 자료구조를 바꾸면 그 위에 짓는 기능이 통째로 쉬워지는 예다.
 *
 * <h2>이것이 무엇의 축소판인가</h2>
 *
 * git 의 커밋 그래프, 데이터베이스의 MVCC, 편집기의 undo 스택,
 * 리액트의 상태 이력이 전부 같은 모양이다.
 */
public final class VersionedStore<K extends Comparable<K>, V> {

    /**
     * 만들어진 모든 버전. 붙이기만 하고 지우지 않는다.
     * 그래서 한 번 받은 버전 번호가 나중에 무효가 되지 않는다.
     */
    private final List<PersistentTreeMap<K, V>> versions = new ArrayList<>();

    /**
     * undo, redo 가 오가는 길. 버전 번호의 목록이다.
     * 목록이 둘인 이유가 이 클래스의 전부다. 버려진 가지는 여기서만 빠진다.
     */
    private final List<Integer> history = new ArrayList<>();

    /** 지금 history 의 몇 번째를 보고 있는가. undo, redo 가 이 값을 움직인다. */
    private int cursor;

    private long nodesCreated;

    public VersionedStore() {
        versions.add(PersistentTreeMap.empty());
        history.add(0);
        this.cursor = 0;
    }

    // ------------------------------------------------------------------
    // 채워져 있는 부분
    // ------------------------------------------------------------------

    public int currentVersion() {
        return history.get(cursor);
    }

    /** 버려진 가지까지 포함해 지금까지 만들어진 버전의 수. 버전 번호는 재사용하지 않는다. */
    public int versionCount() {
        return versions.size();
    }

    /** 태어난 뒤 지금까지 새로 만든 노드의 총합. 이 저장소가 쓴 메모리다. */
    public long nodesCreated() {
        return nodesCreated;
    }

    public int put(K key, V value) {
        return commit(current().put(key, value));
    }

    public int remove(K key) {
        return commit(current().remove(key));
    }

    private PersistentTreeMap<K, V> current() {
        return versions.get(currentVersion());
    }

    /** 지금 버전에서의 값. */
    public V get(K key) {
        return current().get(key);
    }

    /** 그 시점의 값. 버전은 0 부터 versionCount() - 1 까지다. */
    public V get(int version, K key) {
        return snapshot(version).get(key);
    }

    /**
     * 그 시점의 맵 자체.
     *
     * 불변이라 방어적 복사 없이 그냥 넘겨도 안전하다.
     * 가변 맵이었다면 여기서 O(n) 복사를 하거나, 안 하고 조용히 깨지거나 둘 중 하나였다.
     */
    public PersistentTreeMap<K, V> snapshot(int version) {
        if (version < 0 || version >= versions.size()) {
            throw new IndexOutOfBoundsException("그런 버전이 없다: " + version + " (버전 수 " + versions.size() + ")");
        }
        return versions.get(version);
    }

    // ------------------------------------------------------------------
    // 여기부터가 본체
    // ------------------------------------------------------------------

    /**
     * 새 상태를 버전 목록에 붙이고 그 번호를 반환한다.
     */
    private int commit(PersistentTreeMap<K, V> next) {
        // TODO 6: 다섯 줄인데 순서와 조건에 함정이 둘 있다.
        //
        //   1. next 가 **지금 버전과 같은 객체**면 바뀐 것이 없다는 뜻이다.
        //      버전을 늘리지 말고 지금 번호를 그대로 반환한다.
        //      (없는 키를 지우면 remove 가 맵 자신을 돌려주므로 이 경우가 생긴다.
        //       이걸 빼면 아무 일도 안 한 커밋이 이력에 쌓이고 nodesCreated 도 틀리게 샌다)
        //   2. 아니면 nodesCreated 에 next.nodesCreatedByLastPut() 을 더하고 versions 에 붙인다
        //   3. **history 에서 cursor 뒤에 남아 있는 것을 전부 잘라낸다.**
        //      undo 로 뒤로 간 뒤에 새로 썼다면 앞으로 갈 가지가 버려져야 하기 때문이다
        //   4. 그리고 새 버전 번호를 history 에 붙이고 cursor 를 그 끝으로 옮긴다
        //
        // 3번을 빼면 되돌린 뒤 다른 것을 쳤는데도 앞으로 가기가 살아 있다.
        // 그리고 undo 가 아무 관계 없는 옛 가지로 걸어 들어간다.
        //
        // 버려진 가지를 versions 에서 **지우지는 않는다.** 지우면 옛 버전 번호가 무효가 된다.
        // 아무도 안 가리킬 뿐 메모리에는 살아 있다. git 의 dangling commit 과 같은 상태다.
        throw new UnsupportedOperationException("TODO 6: commit");
    }

    /**
     * 한 버전 뒤로. 더 갈 곳이 없으면 false 를 반환하고 아무것도 하지 않는다.
     */
    public boolean undo() {
        // TODO 7: 두 줄이다. 경계만 조심하라. history 의 맨 앞보다 앞은 없다.
        //
        // 버전을 지우지 않는다는 것에 유의하라. 커서만 움직인다.
        // 옛 상태를 되살리려고 무언가를 되돌려 실행할 필요가 없다.
        // 그 상태가 이미 통째로 저장되어 있다.
        throw new UnsupportedOperationException("TODO 7: undo");
    }

    /**
     * 한 버전 앞으로. 버려진 가지로는 가지 않는다.
     */
    public boolean redo() {
        // TODO 8: undo 의 거울상인데 **상한이 versions.size() - 1 이 아니다.**
        //
        // 무엇과 비교해야 하는지 TODO 6 의 3번을 보라.
        // 둘을 헷갈리면 버려진 가지로 넘어간다. 그 버그는 undo 를 하고 새로 쓴 다음,
        // 거기서 또 undo 를 해봐야 드러난다.
        throw new UnsupportedOperationException("TODO 8: redo");
    }
}
