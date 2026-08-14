package com.datastructure.persistent;

import java.util.ArrayList;
import java.util.List;

/**
 * [구현] 버전을 전부 들고 있는 저장소. undo, redo, 그리고 아무 시점 조회.
 *
 * 버전 목록은 PersistentTreeMap 참조의 배열일 뿐이다.
 * 맵이 불변이라 참조 하나가 그 시점의 상태 전체를 뜻한다.
 * 가변 맵이라면 시점마다 통째로 복사해 두어야 같은 일을 할 수 있다.
 */
public final class VersionedStore<K extends Comparable<K>, V> {

    /** 만들어진 모든 버전. 붙이기만 하고 지우지 않는다. 그래서 버전 번호가 무효가 되지 않는다. */
    private final List<PersistentTreeMap<K, V>> versions = new ArrayList<>();

    /** undo, redo 가 오가는 길. 버전 번호의 목록이다. 버려진 가지는 여기서만 빠진다. */
    private final List<Integer> history = new ArrayList<>();

    private int cursor;
    private long nodesCreated;

    public VersionedStore() {
        versions.add(PersistentTreeMap.empty());
        history.add(0);
        this.cursor = 0;
    }

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

    /**
     * 새 상태를 버전 목록에 붙이고 그 번호를 반환한다.
     *
     * 목록이 둘인 것이 이 클래스의 유일한 까다로운 부분이다.
     * undo 로 뒤로 간 뒤에 새로 쓰면 앞으로 갈 가지가 버려져야 하므로 history 를 자른다.
     * 그런데 versions 에서 지우지는 않는다. 지우면 옛 버전 번호가 무효가 되기 때문이다.
     */
    private int commit(PersistentTreeMap<K, V> next) {
        if (next == current()) {
            return currentVersion();      // 바뀐 것이 없으면 버전도 늘지 않는다
        }
        nodesCreated += next.nodesCreatedByLastPut();
        versions.add(next);
        while (history.size() > cursor + 1) {
            history.remove(history.size() - 1);      // 버려진 가지를 길에서 뺀다
        }
        history.add(versions.size() - 1);
        cursor = history.size() - 1;
        return currentVersion();
    }

    /** 지금 버전에서의 값. */
    public V get(K key) {
        return current().get(key);
    }

    /** 그 시점의 값. 버전은 0 부터 versionCount() - 1 까지다. */
    public V get(int version, K key) {
        return snapshot(version).get(key);
    }

    /** 그 시점의 맵 자체. 불변이므로 그냥 넘겨도 안전하다. */
    public PersistentTreeMap<K, V> snapshot(int version) {
        if (version < 0 || version >= versions.size()) {
            throw new IndexOutOfBoundsException("그런 버전이 없다: " + version + " (버전 수 " + versions.size() + ")");
        }
        return versions.get(version);
    }

    /** 한 버전 뒤로. 더 갈 곳이 없으면 false 를 반환하고 아무것도 하지 않는다. */
    public boolean undo() {
        if (cursor == 0) {
            return false;
        }
        cursor--;
        return true;
    }

    /** 한 버전 앞으로. 버려진 가지로는 가지 않는다. */
    public boolean redo() {
        if (cursor >= history.size() - 1) {
            return false;
        }
        cursor++;
        return true;
    }
}
