# 파일 시스템 구현에 유용한 Java API

## 📦 컬렉션

### Map
```java
import java.util.HashMap;
import java.util.Map;

// 자식 노드 저장
Map<String, FSNode> children = new HashMap<>();

// 추가
children.put(name, node);

// 조회
FSNode child = children.get(name);

// 존재 여부
boolean exists = children.containsKey(name);

// 삭제
children.remove(name);

// 모든 자식 순회
for (FSNode child : children.values()) {
    // ...
}

// 이름 목록
Set<String> names = children.keySet();
```

### List
```java
import java.util.ArrayList;
import java.util.List;

List<String> results = new ArrayList<>();

// 추가
results.add(path);

// 정렬
results.sort(String::compareTo);

// 불변 리스트 (Java 11+)
return List.of(name);

// 스트림으로 변환
return children.keySet().stream()
    .sorted()
    .toList();  // Java 16+
```

### Deque (경로 처리)
```java
import java.util.ArrayDeque;
import java.util.Deque;

// 절대 경로 구성
Deque<String> parts = new ArrayDeque<>();

FSNode current = node;
while (current != root) {
    parts.addFirst(current.getName());
    current = current.getParent();
}

String path = "/" + String.join("/", parts);
```

---

## 📝 문자열 처리

### String 분리/결합
```java
// 경로 분리
String path = "/home/user/file.txt";
String[] parts = path.split("/");
// ["", "home", "user", "file.txt"]

// 빈 문자열 제거
String normalized = path.substring(1);  // "home/user/file.txt"
String[] parts = normalized.split("/");
// ["home", "user", "file.txt"]

// 경로 결합
String joined = String.join("/", parts);
// "home/user/file.txt"

// 절대 경로로
String absolute = "/" + joined;
```

### String 검사
```java
// 시작/끝 확인
path.startsWith("/");
path.endsWith("/");

// 빈 문자열
part.isEmpty();

// 특수 디렉토리
part.equals(".");
part.equals("..");
```

### Arrays 유틸
```java
import java.util.Arrays;

// 배열 복사 (부모 경로 추출)
String[] parentParts = Arrays.copyOf(parts, parts.length - 1);

// 배열을 리스트로
List<String> list = Arrays.asList(parts);

// 배열 결합
String path = String.join("/", parts);
```

---

## 🔄 재귀 처리

### 재귀 삭제
```java
private void deleteRecursive(FSNode node) {
    if (node.isDirectory()) {
        // 자식 먼저 삭제 (ConcurrentModificationException 방지)
        List<FSNode> children = new ArrayList<>(node.getChildren());
        for (FSNode child : children) {
            deleteRecursive(child);
        }
    }
    
    if (node.getParent() != null) {
        node.getParent().removeChild(node.getName());
    }
}
```

### 재귀 복사
```java
private FSNode copyRecursive(FSNode source) {
    FSNode copy = new FSNode(source.getName(), source.isDirectory(), null);
    
    if (source.isDirectory()) {
        for (FSNode child : source.getChildren()) {
            FSNode childCopy = copyRecursive(child);
            childCopy.setParent(copy);
            copy.addChild(childCopy);
        }
    } else {
        copy.setContent(source.getContent());
    }
    
    return copy;
}
```

### 재귀 검색
```java
private void findRecursive(FSNode node, String name, 
                           String currentPath, List<String> results) {
    // 현재 노드 검사
    if (matches(node.getName(), name)) {
        results.add(currentPath);
    }
    
    // 자식 재귀 탐색
    if (node.isDirectory()) {
        for (FSNode child : node.getChildren()) {
            String childPath = buildPath(currentPath, child.getName());
            findRecursive(child, name, childPath, results);
        }
    }
}
```

---

## ⏱️ 시간 관련

### System.currentTimeMillis()
```java
// 파일 생성/수정 시간
long createdAt = System.currentTimeMillis();
long modifiedAt = System.currentTimeMillis();

// 수정 시간 갱신
public void updateModifiedTime() {
    this.modifiedAt = System.currentTimeMillis();
}
```

### Instant
```java
import java.time.Instant;

Instant now = Instant.now();
long epochMilli = now.toEpochMilli();

// 타임스탬프 변환
Instant created = Instant.ofEpochMilli(createdAt);
```

---

## 🧪 테스트

### AssertJ
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldCreateDirectory() {
    FileSystem fs = new FileSystem();
    
    fs.mkdir("/home");
    fs.mkdir("/home/user");
    
    assertThat(fs.exists("/home")).isTrue();
    assertThat(fs.isDirectory("/home")).isTrue();
    assertThat(fs.ls("/")).contains("home");
}

@Test
void shouldWriteAndReadFile() {
    FileSystem fs = new FileSystem();
    fs.mkdir("/home");
    
    fs.write("/home/test.txt", "Hello, World!");
    
    assertThat(fs.read("/home/test.txt")).isEqualTo("Hello, World!");
}

@Test
void shouldListDirectoryContents() {
    FileSystem fs = new FileSystem();
    fs.mkdir("/home");
    fs.touch("/home/a.txt");
    fs.touch("/home/b.txt");
    fs.mkdir("/home/docs");
    
    List<String> contents = fs.ls("/home");
    
    assertThat(contents).containsExactlyInAnyOrder("a.txt", "b.txt", "docs");
}

@Test
void shouldFindFiles() {
    FileSystem fs = new FileSystem();
    fs.mkdir("/home/user");
    fs.touch("/home/readme.txt");
    fs.touch("/home/user/readme.txt");
    
    List<String> results = fs.find("/", "readme.txt");
    
    assertThat(results).containsExactlyInAnyOrder(
        "/home/readme.txt",
        "/home/user/readme.txt"
    );
}

@Test
void shouldHandleRelativePaths() {
    FileSystem fs = new FileSystem();
    fs.mkdir("/home/user");
    fs.cd("/home/user");
    
    fs.mkdir("docs");
    fs.touch("file.txt");
    
    assertThat(fs.exists("/home/user/docs")).isTrue();
    assertThat(fs.pwd()).isEqualTo("/home/user");
}
```

---

## 📚 Java 21 관련

### Record
```java
// 파일 정보
public record FileInfo(
    String name,
    boolean isDirectory,
    long size,
    long createdAt,
    long modifiedAt
) {
    public static FileInfo from(FSNode node) {
        return new FileInfo(
            node.getName(),
            node.isDirectory(),
            node.isDirectory() ? 0 : node.getContent().length(),
            node.getCreatedAt(),
            node.getModifiedAt()
        );
    }
}

// 사용
List<FileInfo> info = fs.lsDetailed("/home");
```

### Sealed Classes
```java
public sealed interface FileSystemEntry permits Directory, File {
    String name();
    FSNode parent();
}

public record Directory(String name, FSNode parent, Map<String, FSNode> children) 
    implements FileSystemEntry {}

public record File(String name, FSNode parent, String content) 
    implements FileSystemEntry {}
```

### Pattern Matching
```java
public long calculateSize(FSNode node) {
    return switch (node) {
        case FSNode d when d.isDirectory() -> 
            d.getChildren().stream()
                .mapToLong(this::calculateSize)
                .sum();
        case FSNode f -> f.getContent().length();
    };
}
```

### Text Blocks
```java
// 트리 출력
public String printTree(FSNode node, String indent) {
    StringBuilder sb = new StringBuilder();
    sb.append(indent).append(node.getName());
    
    if (node.isDirectory()) {
        sb.append("/\n");
        for (FSNode child : node.getChildren()) {
            sb.append(printTree(child, indent + "  "));
        }
    } else {
        sb.append("\n");
    }
    
    return sb.toString();
}
```

---

## ⚡ 성능 팁

### 1. 경로 캐싱
```java
// 자주 접근하는 경로 캐싱
private final Map<String, FSNode> pathCache = new HashMap<>();

public FSNode navigate(String path) {
    // 캐시 확인
    if (pathCache.containsKey(path)) {
        return pathCache.get(path);
    }
    
    FSNode node = navigateInternal(path);
    
    // 캐시 저장
    if (node != null) {
        pathCache.put(path, node);
    }
    
    return node;
}

// 변경 시 캐시 무효화
public void invalidateCache(String path) {
    pathCache.entrySet().removeIf(e -> e.getKey().startsWith(path));
}
```

### 2. 대용량 디렉토리
```java
// TreeMap으로 정렬된 순서 유지
Map<String, FSNode> children = new TreeMap<>();

// ls 호출 시 정렬 불필요
public List<String> ls(String path) {
    FSNode node = navigate(path);
    return new ArrayList<>(node.getChildrenNames());  // 이미 정렬됨
}
```

### 3. 지연 로딩 (lazy loading)
```java
// 대용량 파일의 지연 로딩
public class LazyFile extends FSNode {
    private Supplier<String> contentSupplier;
    private String cachedContent;
    
    public String getContent() {
        if (cachedContent == null) {
            cachedContent = contentSupplier.get();
        }
        return cachedContent;
    }
}
```

---

## 🔀 예외 클래스
```java
// 파일을 찾을 수 없음
public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(String path) {
        super("File not found: " + path);
    }
}

// 디렉토리가 아님
public class NotDirectoryException extends RuntimeException {
    public NotDirectoryException(String path) {
        super("Not a directory: " + path);
    }
}

// 디렉토리임 (파일 연산 시)
public class IsDirectoryException extends RuntimeException {
    public IsDirectoryException(String path) {
        super("Is a directory: " + path);
    }
}

// 디렉토리가 비어있지 않음
public class DirectoryNotEmptyException extends RuntimeException {
    public DirectoryNotEmptyException(String path) {
        super("Directory not empty: " + path);
    }
}

// 파일이 이미 존재함
public class FileExistsException extends RuntimeException {
    public FileExistsException(String path) {
        super("File exists: " + path);
    }
}

// 잘못된 연산
public class IllegalOperationException extends RuntimeException {
    public IllegalOperationException(String message) {
        super(message);
    }
}
```
