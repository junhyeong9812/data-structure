package com.datastructure.filesystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 이름에서 자식으로 내려가는 진짜 트리.
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
        // TODO 6: 루트에서 조각을 따라 내려간다. 없으면 null.
        //
        // 들여다본 자리마다 visitedNodes 를 올린다. 이 수가 트리의 비용 그 자체라,
        // 안 세면 MeasurementTest 가 잴 것이 없다.
        //
        // 중간에 파일을 만나면 더 내려갈 수 없다. 그 경우를 안 막으면
        // 파일에 children() 을 부르게 되고 엉뚱한 예외가 난다.
        throw new UnsupportedOperationException("TODO 6: lookup");
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
        // TODO 7: 부모를 찾아 자식 하나를 만든다. 부모가 없으면 던진다(mkdir -p 가 아니다).
        throw new UnsupportedOperationException("TODO 7: mkdir");
    }

    @Override
    public void mkdirs(String path) {
        reset();
        // TODO 8: 내려가면서 없는 것만 만든다. 이미 있는 디렉터리는 그냥 지난다.
        //
        // 중간에 **파일**이 있으면 멈추고 던져야 한다. 그 검사를 빼도 결국 예외는 난다.
        // 종류가 다를 뿐이다. 그래서 "던지기만 하면 통과" 인 테스트로는 빠뜨린 것을 못 잡는다.
        throw new UnsupportedOperationException("TODO 8: mkdirs");
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
        // TODO 9: 없으면 만들고 있으면 내용을 바꾼다.
        //
        // 있을 때 **새 Blob 으로 갈아끼우면 안 된다.** 하드 링크로 이어진 다른 이름이
        // 옛 Blob 을 계속 가리키게 되어, 같은 내용이어야 할 둘이 조용히 갈라진다.
        throw new UnsupportedOperationException("TODO 9: write");
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
        // TODO 10: 자식 이름 목록. 순서 규칙이 계약이다.
        //
        // Node 가 무슨 맵을 쓰는지 보고 오라. 여기서 정렬이 필요한지 아닌지가 거기서 정해진다.
        throw new UnsupportedOperationException("TODO 10: ls");
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
        // TODO 11: 파일이면 길이, 디렉터리면 자식들의 합.
        throw new UnsupportedOperationException("TODO 11: sizeOf");
    }

    @Override
    public List<String> find(String path, String name) {
        reset();
        List<String> out = new ArrayList<>();
        collect(requireNode(path), Paths.normalize(path), name, out);
        return List.copyOf(out);
    }

    private void collect(Node node, String here, String name, List<String> out) {
        // TODO 12: 서브트리를 훑으며 이름이 같은 자리의 전체 경로를 모은다.
        //
        // 시작 자리 자신도 후보다. 자식만 보면 find("/a/target", "target") 이 빈다.
        throw new UnsupportedOperationException("TODO 12: collect");
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
        // TODO 13: 부모에서 이름을 떼고, 그 내용의 참조 수를 하나 내린다.
        //
        // 참조 수를 안 내려도 이 파일은 사라진다. 그래서 대부분의 테스트가 통과한다.
        // 하드 링크를 걸어둔 경우에만 linkCount 가 안 줄어드는 것으로 드러난다.
        throw new UnsupportedOperationException("TODO 13: rm");
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
        // TODO 14: 서브트리의 모든 파일에 대해 참조 수를 내린다.
        throw new UnsupportedOperationException("TODO 14: releaseAll");
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
        // TODO 15: 옛 부모에서 떼고 새 부모에 붙인다. 그게 전부다.
        //
        // 서브트리 안의 무엇도 건드리지 않는다. **이 한 줄이 이 박스의 결론이다.**
        // rewrittenEntries 에 실제로 고친 자리의 수를 적어라. MeasurementTest 가 그 수를 본다.
        //
        // 목적지가 이미 있으면 던진다. 그리고 던졌으면 원래 모양이 그대로여야 한다.
        // 떼고 나서 검사하면 반쯤 옮긴 상태로 던지게 되는데, 그쪽이 더 나쁘다.
        throw new UnsupportedOperationException("TODO 15: mv");
    }

    private Node rename(Node node, String newName) {
        // TODO 16: 이름만 바꾼 노드. Node 의 이름은 만든 뒤에 못 바꾼다.
        //
        // 자식은 **그대로 물려준다.** 여기서 자식을 하나씩 새로 만들면 mv 가 cp 가 된다.
        // 답은 같고 비용만 서브트리 크기로 뛴다. 그러면 이 박스의 측정이 통째로 무너지는데,
        // 계약 테스트는 하나도 안 깨진다.
        throw new UnsupportedOperationException("TODO 16: rename");
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
        // TODO 17: 서브트리를 새로 만든다. 위 javadoc 이 Blob 을 어떻게 할지 적어뒀다.
        throw new UnsupportedOperationException("TODO 17: deepCopy");
    }

    // ------------------------------------------------------------------ 하드 링크

    @Override
    public void link(String existingPath, String newPath) {
        reset();
        // TODO 18: 같은 Blob 을 가리키는 이름을 하나 더 만든다.
        //
        // 디렉터리에는 걸면 안 된다. 걸리는 순간 트리에 고리가 생기고,
        // find 와 size 의 재귀가 안 끝난다. 실제 시스템도 같은 이유로 막는다.
        //
        // 참조 수를 올리는 것을 잊지 마라. 안 올리면 이름 하나만 지워도 내용이 죽는다.
        throw new UnsupportedOperationException("TODO 18: link");
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
