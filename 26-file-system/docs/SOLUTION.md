# 파일 시스템 풀이 해설

## 📌 핵심 아이디어

파일 시스템은 **트리 자료구조**로 표현됩니다.
각 노드는 파일 또는 디렉토리이며, 경로를 통해 탐색합니다.

**핵심 구성요소**:
- 트리 구조 (디렉토리 = 내부 노드, 파일 = 리프 노드)
- 경로 파싱 및 탐색
- 재귀적 연산 (복사, 삭제, 검색)

---

## 🔑 핵심 개념

### 1. 트리 구조
```
루트(/)
├── home/           (디렉토리 노드)
│   ├── user/       (디렉토리 노드)
│   │   └── a.txt   (파일 노드)
│   └── guest/      (디렉토리 노드)
└── etc/            (디렉토리 노드)
    └── config      (파일 노드)

노드 구조:
- 디렉토리: children Map 보유
- 파일: content String 보유
- 모든 노드: parent 참조, 메타데이터
```

### 2. 경로 처리
```java
// 절대 경로: 루트부터 시작
"/home/user/file.txt" → root → home → user → file.txt

// 상대 경로: 현재 디렉토리부터 시작
"./docs/file.txt" → currentDir → docs → file.txt
"../other/file.txt" → currentDir.parent → other → file.txt

// 경로 정규화
"/home/user/../guest/./file.txt" → "/home/guest/file.txt"
```

### 3. 재귀적 연산
```java
// 재귀 삭제
void deleteRecursive(FSNode node) {
    if (node.isDirectory()) {
        for (FSNode child : node.children.values()) {
            deleteRecursive(child);
        }
        node.children.clear();
    }
    // 부모에서 제거
    node.parent.children.remove(node.name);
}

// 재귀 복사
FSNode copyRecursive(FSNode source, FSNode newParent) {
    FSNode copy = new FSNode(source.name, source.isDirectory);
    copy.parent = newParent;
    
    if (source.isDirectory) {
        for (FSNode child : source.children.values()) {
            FSNode childCopy = copyRecursive(child, copy);
            copy.children.put(childCopy.name, childCopy);
        }
    } else {
        copy.content = source.content;
    }
    
    return copy;
}
```

---

## 📝 POP 구현 해설

### 완전한 구현
```java
public class FileSystem {
    private final FSNode root;
    private FSNode currentDir;
    
    public FileSystem() {
        this.root = new FSNode("/", true, null);
        this.currentDir = root;
    }
    
    // 디렉토리 생성
    public void mkdir(String path) {
        mkdirWithParents(path, false);
    }
    
    // 중간 디렉토리 포함 생성 (mkdir -p)
    public void mkdirp(String path) {
        mkdirWithParents(path, true);
    }
    
    private void mkdirWithParents(String path, boolean createParents) {
        String[] parts = parsePath(path);
        FSNode current = getStartNode(path);
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            
            if (part.equals(".")) continue;
            if (part.equals("..")) {
                current = current.getParent() != null ? current.getParent() : current;
                continue;
            }
            
            if (!current.hasChild(part)) {
                if (!createParents && i < parts.length - 1) {
                    throw new FileNotFoundException("Parent not found: " + part);
                }
                FSNode newDir = new FSNode(part, true, current);
                current.addChild(newDir);
            }
            
            current = current.getChild(part);
            
            if (!current.isDirectory() && i < parts.length - 1) {
                throw new NotDirectoryException(part);
            }
        }
    }
    
    // 파일 생성
    public void touch(String path) {
        String[] parts = parsePath(path);
        FSNode parent = navigateToParent(path);
        String fileName = parts[parts.length - 1];
        
        if (!parent.hasChild(fileName)) {
            FSNode file = new FSNode(fileName, false, parent);
            parent.addChild(file);
        } else {
            // touch는 기존 파일의 수정 시간 업데이트
            parent.getChild(fileName).updateModifiedTime();
        }
    }
    
    // 파일 쓰기
    public void write(String path, String content) {
        FSNode node = navigate(path);
        
        if (node == null) {
            // 파일이 없으면 생성
            touch(path);
            node = navigate(path);
        }
        
        if (node.isDirectory()) {
            throw new IsDirectoryException(path);
        }
        
        node.setContent(content);
    }
    
    // 파일에 추가
    public void append(String path, String content) {
        FSNode node = navigate(path);
        
        if (node == null || node.isDirectory()) {
            throw new FileNotFoundException(path);
        }
        
        node.setContent(node.getContent() + content);
    }
    
    // 파일 읽기
    public String read(String path) {
        FSNode node = navigate(path);
        
        if (node == null) {
            throw new FileNotFoundException(path);
        }
        
        if (node.isDirectory()) {
            throw new IsDirectoryException(path);
        }
        
        return node.getContent();
    }
    
    // 디렉토리 내용 나열
    public List<String> ls(String path) {
        FSNode node = navigate(path);
        
        if (node == null) {
            throw new FileNotFoundException(path);
        }
        
        if (!node.isDirectory()) {
            return List.of(node.getName());
        }
        
        return node.getChildrenNames().stream()
            .sorted()
            .toList();
    }
    
    // 상세 목록 (ls -l)
    public List<FileInfo> lsDetailed(String path) {
        FSNode node = navigate(path);
        
        if (node == null) {
            throw new FileNotFoundException(path);
        }
        
        if (!node.isDirectory()) {
            return List.of(FileInfo.from(node));
        }
        
        return node.getChildren().stream()
            .map(FileInfo::from)
            .sorted(Comparator.comparing(FileInfo::name))
            .toList();
    }
    
    // 삭제
    public void rm(String path) {
        rm(path, false);
    }
    
    // 재귀 삭제 (rm -r)
    public void rm(String path, boolean recursive) {
        FSNode node = navigate(path);
        
        if (node == null) {
            throw new FileNotFoundException(path);
        }
        
        if (node == root) {
            throw new IllegalOperationException("Cannot remove root");
        }
        
        if (node.isDirectory() && !node.getChildren().isEmpty() && !recursive) {
            throw new DirectoryNotEmptyException(path);
        }
        
        node.getParent().removeChild(node.getName());
    }
    
    // 이동/이름 변경
    public void mv(String src, String dst) {
        FSNode srcNode = navigate(src);
        
        if (srcNode == null) {
            throw new FileNotFoundException(src);
        }
        
        if (srcNode == root) {
            throw new IllegalOperationException("Cannot move root");
        }
        
        FSNode dstParent = navigateToParent(dst);
        String newName = getFileName(dst);
        
        // 목적지가 디렉토리이면 그 안으로 이동
        FSNode dstNode = navigate(dst);
        if (dstNode != null && dstNode.isDirectory()) {
            dstParent = dstNode;
            newName = srcNode.getName();
        }
        
        // 원본에서 제거
        srcNode.getParent().removeChild(srcNode.getName());
        
        // 새 위치에 추가
        srcNode.setName(newName);
        srcNode.setParent(dstParent);
        dstParent.addChild(srcNode);
    }
    
    // 복사
    public void cp(String src, String dst) {
        cp(src, dst, false);
    }
    
    // 재귀 복사 (cp -r)
    public void cp(String src, String dst, boolean recursive) {
        FSNode srcNode = navigate(src);
        
        if (srcNode == null) {
            throw new FileNotFoundException(src);
        }
        
        if (srcNode.isDirectory() && !recursive) {
            throw new IsDirectoryException("Use recursive copy for directories");
        }
        
        FSNode dstParent = navigateToParent(dst);
        String newName = getFileName(dst);
        
        FSNode copy = copyRecursive(srcNode, newName);
        copy.setParent(dstParent);
        dstParent.addChild(copy);
    }
    
    private FSNode copyRecursive(FSNode source, String newName) {
        FSNode copy = new FSNode(newName, source.isDirectory(), null);
        
        if (source.isDirectory()) {
            for (FSNode child : source.getChildren()) {
                FSNode childCopy = copyRecursive(child, child.getName());
                childCopy.setParent(copy);
                copy.addChild(childCopy);
            }
        } else {
            copy.setContent(source.getContent());
        }
        
        return copy;
    }
    
    // 검색
    public List<String> find(String basePath, String name) {
        FSNode baseNode = navigate(basePath);
        
        if (baseNode == null) {
            throw new FileNotFoundException(basePath);
        }
        
        List<String> results = new ArrayList<>();
        findRecursive(baseNode, name, getAbsolutePath(baseNode), results);
        return results;
    }
    
    private void findRecursive(FSNode node, String name, String currentPath, 
                               List<String> results) {
        if (node.getName().equals(name) || matchesPattern(node.getName(), name)) {
            results.add(currentPath);
        }
        
        if (node.isDirectory()) {
            for (FSNode child : node.getChildren()) {
                String childPath = currentPath.equals("/") 
                    ? "/" + child.getName() 
                    : currentPath + "/" + child.getName();
                findRecursive(child, name, childPath, results);
            }
        }
    }
    
    // 패턴 매칭 (간단한 * 와일드카드)
    private boolean matchesPattern(String name, String pattern) {
        if (!pattern.contains("*")) {
            return name.equals(pattern);
        }
        
        String regex = pattern.replace(".", "\\.").replace("*", ".*");
        return name.matches(regex);
    }
    
    // 현재 디렉토리 변경
    public void cd(String path) {
        FSNode node = navigate(path);
        
        if (node == null) {
            throw new FileNotFoundException(path);
        }
        
        if (!node.isDirectory()) {
            throw new NotDirectoryException(path);
        }
        
        currentDir = node;
    }
    
    // 현재 경로
    public String pwd() {
        return getAbsolutePath(currentDir);
    }
    
    // 존재 여부
    public boolean exists(String path) {
        return navigate(path) != null;
    }
    
    // 디렉토리 여부
    public boolean isDirectory(String path) {
        FSNode node = navigate(path);
        return node != null && node.isDirectory();
    }
    
    // 파일 여부
    public boolean isFile(String path) {
        FSNode node = navigate(path);
        return node != null && !node.isDirectory();
    }
    
    // 파일 크기
    public long size(String path) {
        FSNode node = navigate(path);
        
        if (node == null) {
            throw new FileNotFoundException(path);
        }
        
        if (node.isDirectory()) {
            return calculateDirectorySize(node);
        }
        
        return node.getContent().length();
    }
    
    private long calculateDirectorySize(FSNode dir) {
        long total = 0;
        for (FSNode child : dir.getChildren()) {
            if (child.isDirectory()) {
                total += calculateDirectorySize(child);
            } else {
                total += child.getContent().length();
            }
        }
        return total;
    }
    
    // === 헬퍼 메서드 ===
    
    private String[] parsePath(String path) {
        if (path.equals("/")) return new String[0];
        
        String normalized = path;
        if (normalized.startsWith("/")) normalized = normalized.substring(1);
        if (normalized.endsWith("/")) normalized = normalized.substring(0, normalized.length() - 1);
        
        return normalized.split("/");
    }
    
    private FSNode getStartNode(String path) {
        return path.startsWith("/") ? root : currentDir;
    }
    
    private FSNode navigate(String path) {
        if (path.equals("/")) return root;
        
        String[] parts = parsePath(path);
        FSNode current = getStartNode(path);
        
        for (String part : parts) {
            if (part.isEmpty() || part.equals(".")) continue;
            
            if (part.equals("..")) {
                current = current.getParent() != null ? current.getParent() : current;
                continue;
            }
            
            if (!current.isDirectory() || !current.hasChild(part)) {
                return null;
            }
            
            current = current.getChild(part);
        }
        
        return current;
    }
    
    private FSNode navigateToParent(String path) {
        String[] parts = parsePath(path);
        
        if (parts.length <= 1) {
            return getStartNode(path);
        }
        
        String parentPath = String.join("/", 
            Arrays.copyOf(parts, parts.length - 1));
        
        if (path.startsWith("/")) {
            parentPath = "/" + parentPath;
        }
        
        FSNode parent = navigate(parentPath);
        
        if (parent == null || !parent.isDirectory()) {
            throw new FileNotFoundException("Parent directory not found");
        }
        
        return parent;
    }
    
    private String getFileName(String path) {
        String[] parts = parsePath(path);
        return parts.length > 0 ? parts[parts.length - 1] : "";
    }
    
    private String getAbsolutePath(FSNode node) {
        if (node == root) return "/";
        
        List<String> parts = new ArrayList<>();
        FSNode current = node;
        
        while (current != root) {
            parts.add(current.getName());
            current = current.getParent();
        }
        
        Collections.reverse(parts);
        return "/" + String.join("/", parts);
    }
}
```

### FSNode 클래스
```java
public class FSNode {
    private String name;
    private boolean isDirectory;
    private String content;
    private Map<String, FSNode> children;
    private FSNode parent;
    private long createdAt;
    private long modifiedAt;
    
    public FSNode(String name, boolean isDirectory, FSNode parent) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.parent = parent;
        this.content = isDirectory ? null : "";
        this.children = isDirectory ? new HashMap<>() : null;
        this.createdAt = System.currentTimeMillis();
        this.modifiedAt = this.createdAt;
    }
    
    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public boolean isDirectory() { return isDirectory; }
    
    public String getContent() { return content; }
    public void setContent(String content) { 
        this.content = content;
        this.modifiedAt = System.currentTimeMillis();
    }
    
    public FSNode getParent() { return parent; }
    public void setParent(FSNode parent) { this.parent = parent; }
    
    public boolean hasChild(String name) {
        return isDirectory && children.containsKey(name);
    }
    
    public FSNode getChild(String name) {
        return isDirectory ? children.get(name) : null;
    }
    
    public Collection<FSNode> getChildren() {
        return isDirectory ? children.values() : List.of();
    }
    
    public Set<String> getChildrenNames() {
        return isDirectory ? children.keySet() : Set.of();
    }
    
    public void addChild(FSNode child) {
        if (isDirectory) {
            children.put(child.getName(), child);
            this.modifiedAt = System.currentTimeMillis();
        }
    }
    
    public void removeChild(String name) {
        if (isDirectory) {
            children.remove(name);
            this.modifiedAt = System.currentTimeMillis();
        }
    }
    
    public void updateModifiedTime() {
        this.modifiedAt = System.currentTimeMillis();
    }
    
    public long getCreatedAt() { return createdAt; }
    public long getModifiedAt() { return modifiedAt; }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 |
|------|-----------|
| navigate | O(d) |
| mkdir | O(d) |
| touch/write/read | O(d) |
| ls | O(d + c) |
| rm (단일) | O(d) |
| rm -r | O(d + n) |
| cp | O(d + n) |
| find | O(n) |

d = 경로 깊이
c = 자식 수
n = 서브트리 노드 수

---

## ❌ 흔한 실수

### 1. 부모 참조 누락
```java
// 잘못됨: 부모 설정 안 함
FSNode child = new FSNode("file", false, null);
parent.addChild(child);
// child.getParent() == null!

// 올바름
FSNode child = new FSNode("file", false, parent);
parent.addChild(child);
```

### 2. 경로 정규화 미처리
```java
// 잘못됨: "." ".." 처리 안 함
"/home/./user/../guest" → 에러

// 올바름: 각 부분 처리
for (String part : parts) {
    if (part.equals(".")) continue;
    if (part.equals("..")) { current = current.parent; continue; }
    // ...
}
```

### 3. 루트 특수 처리
```java
// 잘못됨
"/".split("/") → ["", ""]

// 올바름
if (path.equals("/")) return new String[0];
```

---

## 🔗 관련 문제

- 트라이 (Trie)
- 트리 순회
- 경로 압축
- 가상 파일 시스템
