# oop/FileSystem.java

OOP 인터페이스 + Node 추상화. File / Directory 분리.

```java
package com.datastructure.filesystem.oop;

import java.util.List;

public interface FileSystem {
    void mkdir(String path);
    void touch(String path);
    void write(String path, String content);
    String read(String path);
    List<String> ls(String path);
    void rm(String path);
    void mv(String src, String dst);
    void cp(String src, String dst);
    List<String> find(String path, String name);
    void cd(String path);
    String pwd();
}
```

---

# oop/FSNode.java

```java
package com.datastructure.filesystem.oop;

import java.util.Map;

public abstract class FSNode {
    protected String name;
    protected FSNode parent;
    protected final long createdAt;
    protected long modifiedAt;

    protected FSNode(String name) {
        this.name = name;
        this.createdAt = System.currentTimeMillis();
        this.modifiedAt = createdAt;
    }

    public abstract boolean isDirectory();
    public abstract long size();

    public String getName() { return name; }
    public FSNode getParent() { return parent; }
    public long getCreatedAt() { return createdAt; }
    public long getModifiedAt() { return modifiedAt; }

    public Map<String, FSNode> getChildren() {
        throw new UnsupportedOperationException();
    }

    public String getContent() {
        throw new UnsupportedOperationException();
    }
}
```

---

# oop/FileNode.java

```java
package com.datastructure.filesystem.oop;

public class FileNode extends FSNode {
    private String content = "";

    public FileNode(String name) {
        super(name);
    }

    @Override public boolean isDirectory() { return false; }
    @Override public long size() { return content.length(); }
    @Override public String getContent() { return content; }

    public void setContent(String content) {
        this.content = content;
        this.modifiedAt = System.currentTimeMillis();
    }
}
```

---

# oop/DirectoryNode.java

```java
package com.datastructure.filesystem.oop;

import java.util.Map;
import java.util.TreeMap;

public class DirectoryNode extends FSNode {
    private final Map<String, FSNode> children = new TreeMap<>();

    public DirectoryNode(String name) {
        super(name);
    }

    @Override public boolean isDirectory() { return true; }
    @Override public Map<String, FSNode> getChildren() { return children; }

    @Override
    public long size() {
        long total = 0;
        for (FSNode c : children.values()) total += c.size();
        return total;
    }

    public void addChild(FSNode child) {
        child.parent = this;
        children.put(child.name, child);
        this.modifiedAt = System.currentTimeMillis();
    }

    public FSNode removeChild(String name) {
        FSNode removed = children.remove(name);
        if (removed != null) this.modifiedAt = System.currentTimeMillis();
        return removed;
    }
}
```

---

# oop/InMemoryFileSystem.java

```java
package com.datastructure.filesystem.oop;

import java.util.*;

public class InMemoryFileSystem implements FileSystem {
    private final DirectoryNode root;
    private DirectoryNode currentDir;

    public InMemoryFileSystem() {
        this.root = new DirectoryNode("/");
        this.currentDir = root;
    }

    @Override
    public void mkdir(String path) {
        DirectoryNode parent = navigateToParentDir(path);
        String name = lastSegment(path);
        if (parent.getChildren().containsKey(name)) {
            throw new RuntimeException("Already exists: " + path);
        }
        parent.addChild(new DirectoryNode(name));
    }

    @Override
    public void touch(String path) {
        DirectoryNode parent = navigateToParentDir(path);
        String name = lastSegment(path);
        if (parent.getChildren().containsKey(name)) return;
        parent.addChild(new FileNode(name));
    }

    @Override
    public void write(String path, String content) {
        FSNode node;
        try { node = navigate(path); }
        catch (RuntimeException e) { touch(path); node = navigate(path); }
        if (node.isDirectory()) throw new IllegalArgumentException("Is a directory");
        ((FileNode) node).setContent(content);
    }

    @Override
    public String read(String path) {
        FSNode node = navigate(path);
        if (node.isDirectory()) throw new IllegalArgumentException("Is a directory");
        return node.getContent();
    }

    @Override
    public List<String> ls(String path) {
        FSNode node = navigate(path);
        if (!node.isDirectory()) return List.of(node.getName());
        return new ArrayList<>(node.getChildren().keySet());
    }

    @Override
    public void rm(String path) {
        FSNode node = navigate(path);
        if (node == root) throw new IllegalArgumentException();
        ((DirectoryNode) node.getParent()).removeChild(node.getName());
    }

    @Override
    public void mv(String src, String dst) {
        FSNode node = navigate(src);
        DirectoryNode dstParent = navigateToParentDir(dst);
        String newName = lastSegment(dst);
        ((DirectoryNode) node.getParent()).removeChild(node.getName());
        node.name = newName;
        dstParent.addChild(node);
    }

    @Override
    public void cp(String src, String dst) {
        FSNode node = navigate(src);
        DirectoryNode dstParent = navigateToParentDir(dst);
        String newName = lastSegment(dst);
        FSNode copy = deepCopy(node, newName);
        dstParent.addChild(copy);
    }

    private FSNode deepCopy(FSNode src, String newName) {
        if (!src.isDirectory()) {
            FileNode f = new FileNode(newName);
            f.setContent(src.getContent());
            return f;
        }
        DirectoryNode d = new DirectoryNode(newName);
        for (Map.Entry<String, FSNode> e : src.getChildren().entrySet()) {
            d.addChild(deepCopy(e.getValue(), e.getKey()));
        }
        return d;
    }

    @Override
    public List<String> find(String path, String name) {
        FSNode start = navigate(path);
        List<String> out = new ArrayList<>();
        findRec(start, pathOf(start).equals("/") ? "" : pathOf(start), name, out);
        return out;
    }

    private void findRec(FSNode node, String prefix, String name, List<String> out) {
        if (!node.isDirectory()) return;
        for (Map.Entry<String, FSNode> e : node.getChildren().entrySet()) {
            String childPath = prefix + "/" + e.getKey();
            if (e.getKey().equals(name)) out.add(childPath);
            findRec(e.getValue(), childPath, name, out);
        }
    }

    @Override
    public void cd(String path) {
        FSNode node = navigate(path);
        if (!node.isDirectory()) throw new IllegalArgumentException();
        currentDir = (DirectoryNode) node;
    }

    @Override
    public String pwd() {
        return pathOf(currentDir);
    }

    private String pathOf(FSNode node) {
        if (node == root) return "/";
        Deque<String> stack = new ArrayDeque<>();
        FSNode cur = node;
        while (cur != root) { stack.push(cur.getName()); cur = cur.getParent(); }
        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty()) sb.append("/").append(stack.pop());
        return sb.toString();
    }

    private FSNode navigate(String path) {
        if ("/".equals(path)) return root;
        FSNode cur = path.startsWith("/") ? root : currentDir;
        for (String p : tokenize(path)) {
            if (p.equals(".") || p.isEmpty()) continue;
            if (p.equals("..")) {
                cur = cur.getParent() == null ? cur : cur.getParent();
                continue;
            }
            if (!cur.isDirectory() || !cur.getChildren().containsKey(p)) {
                throw new RuntimeException("Not found: " + path);
            }
            cur = cur.getChildren().get(p);
        }
        return cur;
    }

    private DirectoryNode navigateToParentDir(String path) {
        String[] parts = tokenize(path);
        if (parts.length == 0) throw new IllegalArgumentException();
        FSNode cur = path.startsWith("/") ? root : currentDir;
        for (int i = 0; i < parts.length - 1; i++) {
            String p = parts[i];
            if (p.equals(".") || p.isEmpty()) continue;
            if (p.equals("..")) {
                cur = cur.getParent() == null ? cur : cur.getParent();
                continue;
            }
            cur = cur.getChildren().get(p);
            if (cur == null) throw new RuntimeException("Not found: " + path);
        }
        return (DirectoryNode) cur;
    }

    private String lastSegment(String path) {
        String[] parts = tokenize(path);
        return parts[parts.length - 1];
    }

    private String[] tokenize(String path) {
        String t = path.startsWith("/") ? path.substring(1) : path;
        if (t.endsWith("/")) t = t.substring(0, t.length() - 1);
        if (t.isEmpty()) return new String[0];
        return t.split("/");
    }
}
```
