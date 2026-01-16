# 26. 파일 시스템 (File System)

## 📋 문제 정의

**트리 구조 기반의 인메모리 파일 시스템**을 구현하세요.

파일 시스템은 디렉토리와 파일을 계층적으로 관리하며,
경로 기반 탐색과 다양한 파일 연산을 지원합니다.

---

## 🎯 학습 목표

- 트리 자료구조 활용
- 경로 파싱과 탐색
- 재귀적 구조 처리
- 파일/디렉토리 메타데이터 관리
- 권한 시스템 기초

---

## 📝 요구사항

### 핵심 개념

| 개념 | 설명 |
|------|------|
| **Directory** | 파일과 하위 디렉토리를 포함하는 컨테이너 |
| **File** | 데이터를 저장하는 단위 |
| **Path** | 파일/디렉토리의 위치 (/home/user/file.txt) |
| **Root** | 최상위 디렉토리 (/) |

### 기본 연산

| 메서드 | 설명 |
|--------|------|
| `mkdir(path)` | 디렉토리 생성 |
| `touch(path)` | 빈 파일 생성 |
| `write(path, content)` | 파일에 내용 쓰기 |
| `read(path)` | 파일 내용 읽기 |
| `ls(path)` | 디렉토리 내용 나열 |
| `rm(path)` | 파일/디렉토리 삭제 |
| `mv(src, dst)` | 이동/이름 변경 |
| `cp(src, dst)` | 복사 |
| `find(path, name)` | 파일 검색 |

### 경로 형식

| 형식 | 예시 | 설명 |
|------|------|------|
| 절대 경로 | `/home/user/file.txt` | 루트부터 시작 |
| 상대 경로 | `./file.txt`, `../dir` | 현재 위치 기준 |
| 특수 경로 | `.` (현재), `..` (부모) | 상대 참조 |

---

## 📊 입출력 예시

### 예제 1: 기본 사용
```java
FileSystem fs = new FileSystem();

// 디렉토리 생성
fs.mkdir("/home");
fs.mkdir("/home/user");
fs.mkdir("/home/user/documents");

// 파일 생성 및 쓰기
fs.touch("/home/user/hello.txt");
fs.write("/home/user/hello.txt", "Hello, World!");

// 파일 읽기
String content = fs.read("/home/user/hello.txt");
System.out.println(content);  // "Hello, World!"

// 디렉토리 내용 나열
List<String> files = fs.ls("/home/user");
// ["documents", "hello.txt"]
```

### 예제 2: 트리 구조
```
/
├── home/
│   ├── user/
│   │   ├── documents/
│   │   │   └── report.pdf
│   │   ├── pictures/
│   │   └── hello.txt
│   └── guest/
├── etc/
│   └── config.ini
└── tmp/
```

### 예제 3: 경로 탐색
```java
FileSystem fs = new FileSystem();
fs.mkdir("/home/user");
fs.cd("/home/user");

// 상대 경로 사용
fs.mkdir("documents");     // /home/user/documents 생성
fs.touch("file.txt");      // /home/user/file.txt 생성

// 부모 디렉토리 참조
fs.cd("..");               // /home으로 이동
fs.cd("../etc");           // /etc로 이동 (존재하면)

// 현재 경로
String pwd = fs.pwd();     // "/etc"
```

### 예제 4: 복사와 이동
```java
FileSystem fs = new FileSystem();
fs.mkdir("/src");
fs.mkdir("/dst");
fs.write("/src/file.txt", "content");

// 파일 복사
fs.cp("/src/file.txt", "/dst/file.txt");

// 파일 이동 (이름 변경)
fs.mv("/src/file.txt", "/src/renamed.txt");

// 디렉토리 전체 복사 (재귀)
fs.mkdir("/src/subdir");
fs.touch("/src/subdir/nested.txt");
fs.cp("/src", "/backup");  // /backup에 src 내용 전체 복사
```

### 예제 5: 파일 검색
```java
FileSystem fs = new FileSystem();
// 여러 파일 생성
fs.write("/home/user/docs/readme.txt", "...");
fs.write("/home/user/readme.txt", "...");
fs.write("/tmp/readme.txt", "...");

// 이름으로 검색
List<String> results = fs.find("/", "readme.txt");
// ["/home/user/docs/readme.txt", "/home/user/readme.txt", "/tmp/readme.txt"]

// 패턴 검색 (선택)
List<String> txtFiles = fs.find("/home", "*.txt");
```

---

## 🔍 핵심 개념

### 노드 구조
```java
// 파일 시스템 노드 (파일 또는 디렉토리)
class FSNode {
    String name;
    boolean isDirectory;
    String content;           // 파일인 경우
    Map<String, FSNode> children;  // 디렉토리인 경우
    FSNode parent;
    long createdAt;
    long modifiedAt;
    long size;
}
```

### 경로 파싱
```java
// "/home/user/file.txt" → ["home", "user", "file.txt"]
private String[] parsePath(String path) {
    if (path.equals("/")) return new String[0];
    
    String normalized = path.startsWith("/") ? path.substring(1) : path;
    return normalized.split("/");
}
```

### 경로 탐색
```java
private FSNode navigate(String path) {
    if (path.equals("/")) return root;
    
    String[] parts = parsePath(path);
    FSNode current = path.startsWith("/") ? root : currentDir;
    
    for (String part : parts) {
        if (part.equals(".")) continue;
        if (part.equals("..")) {
            current = current.parent != null ? current.parent : current;
            continue;
        }
        
        if (!current.isDirectory || !current.children.containsKey(part)) {
            throw new FileNotFoundException(path);
        }
        current = current.children.get(part);
    }
    
    return current;
}
```

---

## 💡 힌트

### 기본 구조
```java
public class FileSystem {
    private final FSNode root;
    private FSNode currentDir;
    
    public FileSystem() {
        this.root = new FSNode("/", true);
        this.currentDir = root;
    }
    
    // 디렉토리 생성
    public void mkdir(String path) {
        String[] parts = parsePath(path);
        FSNode parent = navigateToParent(path);
        String name = parts[parts.length - 1];
        
        if (parent.children.containsKey(name)) {
            throw new FileExistsException(path);
        }
        
        FSNode dir = new FSNode(name, true);
        dir.parent = parent;
        parent.children.put(name, dir);
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
    
    public FSNode(String name, boolean isDirectory) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.content = isDirectory ? null : "";
        this.children = isDirectory ? new HashMap<>() : null;
        this.createdAt = System.currentTimeMillis();
        this.modifiedAt = this.createdAt;
    }
}
```

---

## ✅ 체크리스트

- [ ] 디렉토리 생성 (mkdir)
- [ ] 파일 생성 (touch)
- [ ] 파일 읽기/쓰기
- [ ] 디렉토리 내용 나열 (ls)
- [ ] 삭제 (rm, rm -r)
- [ ] 경로 탐색 (절대/상대)
- [ ] 이동/이름 변경 (mv)
- [ ] 복사 (cp)
- [ ] 검색 (find)
- [ ] 현재 디렉토리 (pwd, cd)

---

## 📚 참고

- Unix/Linux 파일 시스템 구조
- Tree 자료구조
- 경로 정규화 알고리즘
- inode 개념
