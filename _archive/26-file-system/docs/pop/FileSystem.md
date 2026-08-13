# pop/FileSystem.java

인메모리 트리 기반 파일 시스템. mkdir/touch/write/read/ls/rm/mv/cp/find/cd/pwd.

```java
package com.datastructure.filesystem.pop;

import java.util.*;

public class FileSystem {

    public static class FSNode {
        String name;
        final boolean isDirectory;
        String content;
        final Map<String, FSNode> children;
        FSNode parent;
        final long createdAt;
        long modifiedAt;

        FSNode(String name, boolean isDirectory) {
            this.name = name;
            this.isDirectory = isDirectory;
            this.content = isDirectory ? null : "";
            this.children = isDirectory ? new TreeMap<>() : null;
            this.createdAt = System.currentTimeMillis();
            this.modifiedAt = createdAt;
        }
    }

    public static class FileNotFoundException extends RuntimeException {
        public FileNotFoundException(String path) { super("Not found: " + path); }
    }

    public static class FileExistsException extends RuntimeException {
        public FileExistsException(String path) { super("Already exists: " + path); }
    }

    private final FSNode root;
    private FSNode currentDir;

    public FileSystem() {
        this.root = new FSNode("/", true);
        this.currentDir = root;
    }

    public void mkdir(String path) {
        FSNode parent = navigateToParent(path);
        String name = lastSegment(path);
        if (parent.children.containsKey(name)) throw new FileExistsException(path);
        FSNode dir = new FSNode(name, true);
        dir.parent = parent;
        parent.children.put(name, dir);
        parent.modifiedAt = System.currentTimeMillis();
    }

    public void touch(String path) {
        FSNode parent = navigateToParent(path);
        String name = lastSegment(path);
        if (parent.children.containsKey(name)) return; // idempotent
        FSNode file = new FSNode(name, false);
        file.parent = parent;
        parent.children.put(name, file);
        parent.modifiedAt = System.currentTimeMillis();
    }

    public void write(String path, String content) {
        FSNode node;
        try {
            node = navigate(path);
        } catch (FileNotFoundException e) {
            touch(path);
            node = navigate(path);
        }
        if (node.isDirectory) throw new IllegalArgumentException("Is a directory: " + path);
        node.content = content;
        node.modifiedAt = System.currentTimeMillis();
    }

    public String read(String path) {
        FSNode node = navigate(path);
        if (node.isDirectory) throw new IllegalArgumentException("Is a directory: " + path);
        return node.content;
    }

    public List<String> ls(String path) {
        FSNode node = navigate(path);
        if (!node.isDirectory) return List.of(node.name);
        return new ArrayList<>(node.children.keySet());
    }

    public void rm(String path) {
        FSNode node = navigate(path);
        if (node == root) throw new IllegalArgumentException("Cannot remove root");
        node.parent.children.remove(node.name);
        node.parent.modifiedAt = System.currentTimeMillis();
    }

    public void mv(String src, String dst) {
        FSNode srcNode = navigate(src);
        FSNode dstParent = navigateToParent(dst);
        String dstName = lastSegment(dst);
        if (dstParent.children.containsKey(dstName)) throw new FileExistsException(dst);

        srcNode.parent.children.remove(srcNode.name);
        srcNode.name = dstName;
        srcNode.parent = dstParent;
        dstParent.children.put(dstName, srcNode);
    }

    public void cp(String src, String dst) {
        FSNode srcNode = navigate(src);
        FSNode dstParent = navigateToParent(dst);
        String dstName = lastSegment(dst);
        if (dstParent.children.containsKey(dstName)) throw new FileExistsException(dst);

        FSNode copy = deepCopy(srcNode, dstName, dstParent);
        dstParent.children.put(dstName, copy);
    }

    private FSNode deepCopy(FSNode src, String newName, FSNode parent) {
        FSNode copy = new FSNode(newName, src.isDirectory);
        copy.parent = parent;
        if (src.isDirectory) {
            for (Map.Entry<String, FSNode> e : src.children.entrySet()) {
                copy.children.put(e.getKey(), deepCopy(e.getValue(), e.getKey(), copy));
            }
        } else {
            copy.content = src.content;
        }
        return copy;
    }

    public List<String> find(String path, String name) {
        FSNode start = navigate(path);
        List<String> results = new ArrayList<>();
        findRec(start, "/".equals(path) ? "" : pathOf(start), name, results);
        return results;
    }

    private void findRec(FSNode node, String prefix, String name, List<String> out) {
        if (node.name.equals(name)) {
            out.add(prefix.isEmpty() ? "/" + node.name : prefix);
        }
        if (node.isDirectory) {
            for (Map.Entry<String, FSNode> e : node.children.entrySet()) {
                String childPath = prefix + "/" + e.getKey();
                if (e.getKey().equals(name)) out.add(childPath);
                if (e.getValue().isDirectory) findRec(e.getValue(), childPath, name, out);
            }
        }
    }

    public void cd(String path) {
        FSNode node = navigate(path);
        if (!node.isDirectory) throw new IllegalArgumentException("Not a directory: " + path);
        currentDir = node;
    }

    public String pwd() {
        return pathOf(currentDir);
    }

    private String pathOf(FSNode node) {
        if (node == root) return "/";
        Deque<String> stack = new ArrayDeque<>();
        FSNode cur = node;
        while (cur != root) {
            stack.push(cur.name);
            cur = cur.parent;
        }
        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty()) sb.append("/").append(stack.pop());
        return sb.toString();
    }

    private FSNode navigate(String path) {
        if (path == null) throw new IllegalArgumentException();
        if ("/".equals(path)) return root;
        FSNode cur = path.startsWith("/") ? root : currentDir;
        for (String part : tokenize(path)) {
            if (part.equals(".") || part.isEmpty()) continue;
            if (part.equals("..")) {
                cur = cur.parent != null ? cur.parent : cur;
                continue;
            }
            if (!cur.isDirectory || !cur.children.containsKey(part)) {
                throw new FileNotFoundException(path);
            }
            cur = cur.children.get(part);
        }
        return cur;
    }

    private FSNode navigateToParent(String path) {
        String[] parts = tokenize(path);
        if (parts.length == 0) throw new IllegalArgumentException("Bad path: " + path);
        FSNode cur = path.startsWith("/") ? root : currentDir;
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            if (part.equals(".") || part.isEmpty()) continue;
            if (part.equals("..")) {
                cur = cur.parent != null ? cur.parent : cur;
                continue;
            }
            if (!cur.isDirectory || !cur.children.containsKey(part)) {
                throw new FileNotFoundException(path);
            }
            cur = cur.children.get(part);
        }
        return cur;
    }

    private String lastSegment(String path) {
        String[] parts = tokenize(path);
        if (parts.length == 0) throw new IllegalArgumentException();
        return parts[parts.length - 1];
    }

    private String[] tokenize(String path) {
        String trimmed = path.startsWith("/") ? path.substring(1) : path;
        if (trimmed.endsWith("/")) trimmed = trimmed.substring(0, trimmed.length() - 1);
        if (trimmed.isEmpty()) return new String[0];
        return trimmed.split("/");
    }
}
```
