package com.datastructure.filesystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 기준선. 트리가 없다. 전체 경로가 키인 맵 하나다.
 *
 * <pre>
 *   "/"          -&gt; null (디렉터리)
 *   "/home"      -&gt; null
 *   "/home/a.txt" -&gt; Blob("hello")
 * </pre>
 *
 * 경로 하나를 여는 데 해시 한 번이다. 트리는 조각 수만큼 내려간다.
 * <b>깊은 경로를 자주 열기만 한다면 이쪽이 이긴다.</b> 이 구현은 느려서 기준선인 것이 아니다.
 *
 * 지는 곳은 <b>구조를 물어볼 때</b>다. 부모와 자식 관계가 어디에도 안 적혀 있어서
 * 매번 전체를 훑어 문자열로 되살려야 한다. ls, find, size, 그리고 디렉터리 mv 가 전부 그렇다.
 *
 * 디렉터리 mv 가 특히 나쁘다. 경로가 키라서 옮긴 디렉터리 아래 있던 항목의 키를
 * <b>전부 다시 써야 한다.</b> 트리는 링크 하나를 고친다. 답은 같고 일이 다르다.
 *
 * <h2>여기 심어둔 함정</h2>
 *
 * 접두사 판정을 {@code key.startsWith(from)} 로 하면 {@code /ab} 가 {@code /a} 의 자손으로 잡힌다.
 * {@code mv /a /z} 한 번에 {@code /ab} 가 {@code /zb} 로 끌려간다.
 * 조각 단위로 봐야 한다. Paths.isAncestorOrSame 이 그 판정을 소유한다.
 */
public class FlatPathFileSystem implements FileSystem, FsStats {

    /** 값이 null 이면 디렉터리, 아니면 파일이다. */
    private final Map<String, Blob> entries = new HashMap<>();

    private long visitedNodes;
    private long rewrittenEntries;

    public FlatPathFileSystem() {
        entries.put(Paths.ROOT, null);
    }

    private void reset() {
        visitedNodes = 0;
        rewrittenEntries = 0;
    }

    private boolean has(String path) {
        visitedNodes++;
        return entries.containsKey(path);
    }

    private void requireExists(String path) {
        if (!has(path)) {
            throw new IllegalArgumentException("없는 경로다: " + path);
        }
    }

    private void requireDirectory(String path) {
        requireExists(path);
        if (entries.get(path) != null) {
            throw new IllegalArgumentException("디렉터리가 아니다: " + path);
        }
    }

    // ------------------------------------------------------------------ 만들기

    @Override
    public void mkdir(String path) {
        reset();
        String p = requireNotRoot(path);
        requireDirectory(Paths.parent(p));
        if (has(p)) {
            throw new IllegalArgumentException("이미 있다: " + p);
        }
        entries.put(p, null);
    }

    @Override
    public void mkdirs(String path) {
        reset();
        String here = Paths.ROOT;
        for (String part : Paths.split(path)) {
            here = Paths.join(here, part);
            visitedNodes++;
            if (entries.containsKey(here)) {
                if (entries.get(here) != null) {
                    throw new IllegalArgumentException("경로 중간이 파일이다: " + Paths.normalize(path));
                }
                continue;
            }
            entries.put(here, null);
        }
    }

    @Override
    public void touch(String path) {
        reset();
        String p = requireNotRoot(path);
        requireDirectory(Paths.parent(p));
        if (!has(p)) {
            entries.put(p, new Blob(""));
        }
    }

    @Override
    public void write(String path, String content) {
        reset();
        String p = requireNotRoot(path);
        requireDirectory(Paths.parent(p));
        Blob blob = entries.get(p);
        visitedNodes++;
        if (blob == null && entries.containsKey(p)) {
            throw new IllegalArgumentException("디렉터리에는 못 쓴다: " + p);
        }
        if (blob == null) {
            entries.put(p, new Blob(content));
        } else {
            blob.setContent(content);
        }
    }

    // ------------------------------------------------------------------ 읽기

    @Override
    public String read(String path) {
        reset();
        String p = Paths.normalize(path);
        requireExists(p);
        Blob blob = entries.get(p);
        if (blob == null) {
            throw new IllegalArgumentException("디렉터리는 못 읽는다: " + p);
        }
        return blob.content();
    }

    /**
     * 자식 이름 목록.
     *
     * 부모와 자식 관계가 어디에도 안 적혀 있으므로 전체를 훑어 되살린다.
     * 자식 셋을 얻으려고 항목 만 개를 다 보는 일이 벌어진다.
     */
    @Override
    public List<String> ls(String path) {
        reset();
        String p = Paths.normalize(path);
        requireDirectory(p);
        // TODO 19: 전체를 훑어 부모가 p 인 것만 고른다.
        //
        // 자기 자신은 제외해야 한다. 루트에서 특히 티가 난다.
        // 그리고 이 맵은 HashMap 이다. 정렬을 빼면 답이 기계마다 달라진다.
        throw new UnsupportedOperationException("TODO 19: ls");
    }

    @Override
    public boolean exists(String path) {
        reset();
        return has(Paths.normalize(path));
    }

    @Override
    public boolean isDirectory(String path) {
        reset();
        String p = Paths.normalize(path);
        return has(p) && entries.get(p) == null;
    }

    @Override
    public long size(String path) {
        reset();
        String p = Paths.normalize(path);
        requireExists(p);
        Blob self = entries.get(p);
        if (self != null) {
            return self.content().length();
        }
        // TODO 20: 자손인 파일의 길이를 전부 더한다.
        throw new UnsupportedOperationException("TODO 20: size");
    }

    @Override
    public List<String> find(String path, String name) {
        reset();
        String p = Paths.normalize(path);
        requireExists(p);
        // TODO 21: 자손 중 이름이 같은 것의 전체 경로.
        //
        // 트리 쪽 find 와 결과가 같아야 한다. 순서까지 같아야 한다.
        throw new UnsupportedOperationException("TODO 21: find");
    }

    // ------------------------------------------------------------------ 지우기

    @Override
    public void rm(String path) {
        reset();
        String p = requireNotRoot(path);
        requireExists(p);
        Blob blob = entries.get(p);
        if (blob == null) {
            throw new IllegalArgumentException("디렉터리는 rm 으로 못 지운다: " + p);
        }
        entries.remove(p);
        blob.release();
    }

    @Override
    public void rmdir(String path) {
        reset();
        String p = requireNotRoot(path);
        requireDirectory(p);
        if (!ls(p).isEmpty()) {
            throw new IllegalArgumentException("비어 있지 않다: " + p);
        }
        reset();
        entries.remove(p);
    }

    @Override
    public void rmr(String path) {
        reset();
        String p = requireNotRoot(path);
        requireExists(p);
        // TODO 22: 자손을 전부 지운다.
        //
        // 훑으면서 바로 지우면 ConcurrentModificationException 이 난다.
        // 지울 것을 먼저 모으고 나서 지워라.
        throw new UnsupportedOperationException("TODO 22: rmr");
    }

    // ------------------------------------------------------------------ 옮기고 복사하기

    /**
     * 여기가 이 구현이 지는 자리다.
     *
     * 옮긴 디렉터리 아래 있던 항목의 키를 전부 다시 쓴다. 트리는 링크 하나를 고친다.
     * rewrittenEntries 가 그 수를 센다.
     */
    @Override
    public void mv(String src, String dst) {
        reset();
        String from = requireNotRoot(src);
        String to = Paths.normalize(dst);
        if (Paths.isAncestorOrSame(from, to)) {
            throw new IllegalArgumentException("자기 자신이나 그 안으로는 못 옮긴다: " + from + " -> " + to);
        }
        requireExists(from);
        requireDirectory(Paths.parent(to));
        if (has(to)) {
            throw new IllegalArgumentException("목적지가 이미 있다: " + to);
        }

        // TODO 23: 자손의 키를 전부 새 접두사로 다시 쓴다.
        //
        // **여기가 이 구현이 지는 자리다.** 다시 쓴 개수를 rewrittenEntries 에 적어라.
        // 트리 쪽 TODO 15 와 나란히 놓고 보면 두 구조의 차이가 수 하나로 보인다.
        //
        // 자손을 고르는 판정은 Paths 가 소유한다. 여기서 문자열 접두사로 고르면
        // /a 를 옮길 때 /ab 가 딸려온다.
        throw new UnsupportedOperationException("TODO 23: mv");
    }

    @Override
    public void cp(String src, String dst) {
        reset();
        String from = Paths.normalize(src);
        String to = Paths.normalize(dst);
        if (Paths.isAncestorOrSame(from, to)) {
            throw new IllegalArgumentException("자기 자신이나 그 안으로는 못 복사한다: " + from + " -> " + to);
        }
        requireExists(from);
        requireDirectory(Paths.parent(to));
        if (has(to)) {
            throw new IllegalArgumentException("목적지가 이미 있다: " + to);
        }

        // TODO 24: 자손을 새 접두사 아래에 만든다.
        //
        // Blob 을 그대로 넣으면 복사가 아니라 하드 링크가 된다. 예외는 안 나고,
        // 사본에 쓴 것이 원본에 비치는 것으로 한참 뒤에 드러난다.
        throw new UnsupportedOperationException("TODO 24: cp");
    }

    // ------------------------------------------------------------------ 하드 링크

    /**
     * 평면 맵도 하드 링크를 할 수 있다. 키 둘이 같은 Blob 을 가리키면 된다.
     *
     * 이름과 내용이 갈라져 있으면 구조와 상관없이 되는 일이라는 것이 요점이다.
     * 트리냐 맵이냐가 정하는 것은 이 능력이 아니라 비용이다.
     */
    @Override
    public void link(String existingPath, String newPath) {
        reset();
        String from = Paths.normalize(existingPath);
        String to = Paths.normalize(newPath);
        requireExists(from);
        Blob blob = entries.get(from);
        if (blob == null) {
            throw new IllegalArgumentException("디렉터리에는 하드 링크를 못 건다: " + from);
        }
        requireDirectory(Paths.parent(to));
        if (has(to)) {
            throw new IllegalArgumentException("목적지가 이미 있다: " + to);
        }
        // TODO 25: 키 둘이 같은 Blob 을 가리키게 한다. 참조 수도 올린다.
        throw new UnsupportedOperationException("TODO 25: link");
    }

    @Override
    public int linkCount(String path) {
        reset();
        String p = Paths.normalize(path);
        requireExists(p);
        Blob blob = entries.get(p);
        return blob == null ? 1 : blob.links();
    }

    // ------------------------------------------------------------------ 측정

    @Override
    public long visitedNodes() {
        return visitedNodes;
    }

    @Override
    public long rewrittenEntries() {
        return rewrittenEntries;
    }

    /** 담고 있는 항목 수. 트리에는 없는 값이라 크기 비교에 쓴다. */
    public int entryCount() {
        return entries.size();
    }

    private static String requireNotRoot(String path) {
        String p = Paths.normalize(path);
        if (p.equals(Paths.ROOT)) {
            throw new IllegalArgumentException("루트에는 그 연산을 못 한다");
        }
        return p;
    }

    @Override
    public String toString() {
        return "평면 경로 맵(" + entries.size() + "개 항목)";
    }
}
