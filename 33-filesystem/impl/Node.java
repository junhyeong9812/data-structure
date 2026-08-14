package com.datastructure.filesystem;

import java.util.Map;
import java.util.TreeMap;

/**
 * [구현] 트리의 자리 하나. 디렉터리이거나 파일이다.
 *
 * 자식 맵이 TreeMap 이다. HashMap 을 쓰면 ls 의 순서가 기계와 JDK 판에 따라 달라지고,
 * 그러면 답이 하나로 정해지지 않아 두 구현을 맞대볼 수가 없다.
 * 05번에서 "해시 순회 순서에 기대지 마라"고 했던 것이 여기서 계약이 된다.
 *
 * 파일 노드는 Blob 을 가리킨다. 가리킬 뿐 소유하지 않는다.
 * 하드 링크는 서로 다른 노드 둘이 같은 Blob 을 가리키는 것이고, 그 순간 이 구조는
 * 트리가 아니게 된다. 이름은 여전히 트리에 하나씩 달려 있지만 내용은 공유된다.
 *
 * 이 클래스에는 TODO 가 없다. 담는 그릇이다.
 */
public final class Node {

    private final String name;
    private final Blob blob;
    private final Map<String, Node> children;

    private Node(String name, Blob blob, Map<String, Node> children) {
        this.name = name;
        this.blob = blob;
        this.children = children;
    }

    static Node directory(String name) {
        return new Node(name, null, new TreeMap<>());
    }

    static Node file(String name, Blob blob) {
        return new Node(name, blob, null);
    }

    public String name() {
        return name;
    }

    public boolean isDirectory() {
        return children != null;
    }

    public Blob blob() {
        return blob;
    }

    /** 디렉터리의 자식. 이름 오름차순으로 순회된다. 파일에서 부르면 던진다. */
    public Map<String, Node> children() {
        if (children == null) {
            throw new IllegalStateException("파일에는 자식이 없다: " + name);
        }
        return children;
    }

    @Override
    public String toString() {
        return isDirectory() ? name + "/" : name + " (" + blob.content().length() + "자)";
    }
}
