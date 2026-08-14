package com.datastructure.filesystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * [구현] 이름에서 자식으로 내려가는 진짜 트리.
 *
 * 09번 트라이는 글자 하나로 내려갔다. 여기서는 경로 조각 하나로 내려간다.
 * 알파벳이 유한하지 않고 자식 수도 정해져 있지 않다는 것 말고는 같은 모양이다.
 *
 * <h2>이 구조가 사는 곳</h2>
 *
 * 디렉터리를 옮기는 일이 <b>링크 하나 고치기</b>다. 그 아래 무엇이 몇 개 있든 상관없다.
 * 부모에서 떼어 다른 부모에 붙이면 서브트리가 통째로 따라온다.
 *
 * 평면 맵은 그렇게 못 한다. 경로가 키라서, 옮긴 디렉터리 아래 있던 항목의 키를 전부 다시 써야 한다.
 * MeasurementTest 가 그 수를 센다.
 *
 * <h2>이 구조가 지는 곳</h2>
 *
 * 경로 하나를 열려면 조각 수만큼 내려가야 한다. 평면 맵은 해시 한 번이다.
 * 깊이가 얕으면 트리의 이점이 없고 걷는 비용만 남는다.
 */
public class TreeFileSystem implements FileSystem, FsStats {

    private final Node root = Node.directory("");

    private long visitedNodes;
    private long rewrittenEntries;

    // ------------------------------------------------------------------ 걷기

    /** 경로가 가리키는 자리. 없으면 null. 걷는 동안 방문 수를 센다. */
    private Node lookup(String path) {
        Node current = root;
        visitedNodes++;
        for (String part : Paths.split(path)) {
            if (!current.isDirectory()) {
                return null;        // 파일을 뚫고 더 내려갈 수는 없다
            }
            current = current.children().get(part);
            visitedNodes++;
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    private Node requireNode(String path) {
        Node node = lookup(path);
        if (node == null) {
            throw new IllegalArgumentException("없는 경로다: " + Paths.normalize(path));
        }
        return node;
    }

    private Node requireDirectory(String path) {
        Node node = requireNode(path);
        if (!node.isDirectory()) {
            throw new IllegalArgumentException("디렉터리가 아니다: " + Paths.normalize(path));
        }
        return node;
    }

    /** 부모 디렉터리. 없거나 파일이면 던진다. */
    private Node requireParent(String path) {
        return requireDirectory(Paths.parent(path));
    }

    private void reset() {
        visitedNodes = 0;
        rewrittenEntries = 0;
    }

    // ------------------------------------------------------------------ 만들기

    @Override
    public void mkdir(String path) {
        reset();
        String name = requireName(path);
        Node parent = requireParent(path);
        if (parent.children().containsKey(name)) {
            throw new IllegalArgumentException("이미 있다: " + Paths.normalize(path));
        }
        parent.children().put(name, Node.directory(name));
    }

    @Override
    public void mkdirs(String path) {
        reset();
        Node current = root;
        visitedNodes++;
        for (String part : Paths.split(path)) {
            if (!current.isDirectory()) {
                throw new IllegalArgumentException("경로 중간이 파일이다: " + Paths.normalize(path));
            }
            Node next = current.children().get(part);
            if (next == null) {
                next = Node.directory(part);
                current.children().put(part, next);
            }
            current = next;
            visitedNodes++;
        }
        if (!current.isDirectory()) {
            throw new IllegalArgumentException("파일이 이미 있다: " + Paths.normalize(path));
        }
    }

    @Override
    public void touch(String path) {
        reset();
        String name = requireName(path);
        Node parent = requireParent(path);
        // 이미 있으면 아무것도 안 한다. 내용을 지우면 touch 가 아니라 truncate 다.
        if (!parent.children().containsKey(name)) {
            parent.children().put(name, Node.file(name, new Blob("")));
        }
    }

    @Override
    public void write(String path, String content) {
        reset();
        String name = requireName(path);
        Node parent = requireParent(path);
        Node existing = parent.children().get(name);
        if (existing == null) {
            parent.children().put(name, Node.file(name, new Blob(content)));
            return;
        }
        if (existing.isDirectory()) {
            throw new IllegalArgumentException("디렉터리에는 못 쓴다: " + Paths.normalize(path));
        }
        // Blob 에 쓴다. 하드 링크로 이어진 다른 이름에서도 같이 보여야 하기 때문이다.
        existing.blob().setContent(content);
    }

    // ------------------------------------------------------------------ 읽기

    @Override
    public String read(String path) {
        reset();
        Node node = requireNode(path);
        if (node.isDirectory()) {
            throw new IllegalArgumentException("디렉터리는 못 읽는다: " + Paths.normalize(path));
        }
        return node.blob().content();
    }

    @Override
    public List<String> ls(String path) {
        reset();
        Node node = requireDirectory(path);
        // TreeMap 이라 순회 자체가 오름차순이다. 따로 정렬하지 않는다.
        return List.copyOf(node.children().keySet());
    }

    @Override
    public boolean exists(String path) {
        reset();
        return lookup(path) != null;
    }

    @Override
    public boolean isDirectory(String path) {
        reset();
        Node node = lookup(path);
        return node != null && node.isDirectory();
    }

    @Override
    public long size(String path) {
        reset();
        return sizeOf(requireNode(path));
    }

    private long sizeOf(Node node) {
        if (!node.isDirectory()) {
            return node.blob().content().length();
        }
        long total = 0;
        for (Node child : node.children().values()) {
            visitedNodes++;
            total += sizeOf(child);
        }
        return total;
    }

    @Override
    public List<String> find(String path, String name) {
        reset();
        List<String> out = new ArrayList<>();
        collect(requireNode(path), Paths.normalize(path), name, out);
        return List.copyOf(out);
    }

    private void collect(Node node, String here, String name, List<String> out) {
        if (node.name().equals(name)) {
            out.add(here);
        }
        if (!node.isDirectory()) {
            return;
        }
        // TreeMap 순회라 결과가 저절로 오름차순이다.
        for (Map.Entry<String, Node> e : node.children().entrySet()) {
            visitedNodes++;
            collect(e.getValue(), Paths.join(here, e.getKey()), name, out);
        }
    }

    // ------------------------------------------------------------------ 지우기

    @Override
    public void rm(String path) {
        reset();
        String name = requireName(path);
        Node parent = requireParent(path);
        Node node = parent.children().get(name);
        if (node == null) {
            throw new IllegalArgumentException("없는 경로다: " + Paths.normalize(path));
        }
        if (node.isDirectory()) {
            throw new IllegalArgumentException("디렉터리는 rm 으로 못 지운다: " + Paths.normalize(path));
        }
        parent.children().remove(name);
        // 이름 하나가 사라졌다. 내용은 마지막 이름이 사라질 때만 죽는다.
        node.blob().release();
    }

    @Override
    public void rmdir(String path) {
        reset();
        String name = requireName(path);
        Node parent = requireParent(path);
        Node node = requireDirectory(path);
        if (!node.children().isEmpty()) {
            throw new IllegalArgumentException("비어 있지 않다: " + Paths.normalize(path));
        }
        parent.children().remove(name);
    }

    @Override
    public void rmr(String path) {
        reset();
        String name = requireName(path);
        Node parent = requireParent(path);
        Node node = parent.children().get(name);
        if (node == null) {
            throw new IllegalArgumentException("없는 경로다: " + Paths.normalize(path));
        }
        parent.children().remove(name);
        releaseAll(node);
    }

    private void releaseAll(Node node) {
        if (!node.isDirectory()) {
            node.blob().release();
            return;
        }
        for (Node child : node.children().values()) {
            visitedNodes++;
            releaseAll(child);
        }
    }

    // ------------------------------------------------------------------ 옮기고 복사하기

    @Override
    public void mv(String src, String dst) {
        reset();
        String from = Paths.normalize(src);
        String to = Paths.normalize(dst);
        if (from.equals(Paths.ROOT)) {
            throw new IllegalArgumentException("루트는 못 옮긴다");
        }
        // 자기 자신 안으로 옮기면 서브트리가 트리에서 떨어져 나간다.
        // 예외도 안 나고 그냥 사라진다. 문자열 비교로 막으면 /ab 가 /a 의 자손으로 잡힌다.
        if (Paths.isAncestorOrSame(from, to)) {
            throw new IllegalArgumentException("자기 자신이나 그 안으로는 못 옮긴다: " + from + " -> " + to);
        }
        Node node = requireNode(from);
        Node oldParent = requireParent(from);
        Node newParent = requireDirectory(Paths.parent(to));
        String newName = Paths.name(to);
        if (newParent.children().containsKey(newName)) {
            throw new IllegalArgumentException("목적지가 이미 있다: " + to);
        }

        oldParent.children().remove(Paths.name(from));
        // 이름이 바뀌므로 노드를 새로 만든다. Node 의 이름은 바뀌지 않는다.
        newParent.children().put(newName, rename(node, newName));
        // 링크 두 개를 고쳤을 뿐이다. 서브트리가 몇 개든 상관없다.
        rewrittenEntries = 1;
    }

    private Node rename(Node node, String newName) {
        if (!node.isDirectory()) {
            return Node.file(newName, node.blob());
        }
        Node copy = Node.directory(newName);
        // 자식 맵의 내용을 그대로 옮긴다. 자식 노드 자체는 건드리지 않는다.
        copy.children().putAll(node.children());
        return copy;
    }

    @Override
    public void cp(String src, String dst) {
        reset();
        String from = Paths.normalize(src);
        String to = Paths.normalize(dst);
        if (Paths.isAncestorOrSame(from, to)) {
            throw new IllegalArgumentException("자기 자신이나 그 안으로는 못 복사한다: " + from + " -> " + to);
        }
        Node node = requireNode(from);
        Node newParent = requireDirectory(Paths.parent(to));
        String newName = Paths.name(to);
        if (newParent.children().containsKey(newName)) {
            throw new IllegalArgumentException("목적지가 이미 있다: " + to);
        }
        newParent.children().put(newName, deepCopy(node, newName));
    }

    /**
     * 서브트리를 통째로 복사한다.
     *
     * Blob 을 새로 만든다는 것이 요점이다. 같은 Blob 을 가리키게 하면 복사가 아니라 링크다.
     * 그러면 사본에 쓴 것이 원본에 비치는데, 예외가 안 나므로 한참 뒤에야 드러난다.
     * 26번 영속 자료구조에서 구조를 공유해도 되는 이유가 불변이기 때문이었다.
     * 여기 Blob 은 가변이라 공유하면 안 된다.
     */
    private Node deepCopy(Node node, String newName) {
        rewrittenEntries++;
        if (!node.isDirectory()) {
            return Node.file(newName, new Blob(node.blob().content()));
        }
        Node copy = Node.directory(newName);
        for (Map.Entry<String, Node> e : node.children().entrySet()) {
            visitedNodes++;
            copy.children().put(e.getKey(), deepCopy(e.getValue(), e.getKey()));
        }
        return copy;
    }

    // ------------------------------------------------------------------ 하드 링크

    @Override
    public void link(String existingPath, String newPath) {
        reset();
        Node node = requireNode(existingPath);
        if (node.isDirectory()) {
            // 디렉터리에 하드 링크를 허용하면 트리에 고리가 생긴다.
            // 그러면 find 와 size 의 재귀가 안 끝난다. 실제 시스템도 같은 이유로 막는다.
            throw new IllegalArgumentException("디렉터리에는 하드 링크를 못 건다: " + existingPath);
        }
        String newName = Paths.name(newPath);
        Node newParent = requireDirectory(Paths.parent(newPath));
        if (newParent.children().containsKey(newName)) {
            throw new IllegalArgumentException("목적지가 이미 있다: " + Paths.normalize(newPath));
        }
        node.blob().retain();
        newParent.children().put(newName, Node.file(newName, node.blob()));
    }

    @Override
    public int linkCount(String path) {
        reset();
        Node node = requireNode(path);
        return node.isDirectory() ? 1 : node.blob().links();
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

    private static String requireName(String path) {
        String name = Paths.name(path);
        if (name.isEmpty()) {
            throw new IllegalArgumentException("루트에는 그 연산을 못 한다");
        }
        return name;
    }

    @Override
    public String toString() {
        return "트리 파일 시스템";
    }
}
