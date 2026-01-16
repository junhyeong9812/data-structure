# 블록체인 구현에 유용한 Java API

## 📦 암호화 (java.security)

### MessageDigest (SHA-256)
```java
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

// SHA-256 해시 계산
public static String sha256(String input) {
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        
        // 16진수 문자열로 변환
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
    }
}

// 사용
String hash = sha256("Hello, World!");
// "dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f"
```

### 다른 해시 알고리즘
```java
// MD5 (보안용으로 사용 금지)
MessageDigest md5 = MessageDigest.getInstance("MD5");

// SHA-1 (보안용으로 사용 금지)
MessageDigest sha1 = MessageDigest.getInstance("SHA-1");

// SHA-512
MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
```

### HexFormat (Java 17+)
```java
import java.util.HexFormat;

// 바이트 배열 → 16진수 문자열 (더 간단)
byte[] hash = digest.digest(input.getBytes());
String hexString = HexFormat.of().formatHex(hash);

// 16진수 문자열 → 바이트 배열
byte[] bytes = HexFormat.of().parseHex(hexString);
```

---

## 📊 컬렉션

### ArrayList
```java
import java.util.ArrayList;
import java.util.List;

List<Block> chain = new ArrayList<>();

// 추가
chain.add(block);

// 최신 블록
Block latest = chain.get(chain.size() - 1);
Block latest = chain.getLast();  // Java 21

// 순회
for (int i = 1; i < chain.size(); i++) {
    Block current = chain.get(i);
    Block previous = chain.get(i - 1);
}
```

### Collections
```java
import java.util.Collections;

// 불변 리스트
List<Block> immutableChain = Collections.unmodifiableList(chain);

// 복사
List<Block> copy = new ArrayList<>(chain);
```

---

## ⏱️ 시간 관련

### System.currentTimeMillis()
```java
// 타임스탬프 (epoch 밀리초)
long timestamp = System.currentTimeMillis();

// 블록에서 사용
public Block(int index, String data, String previousHash) {
    this.timestamp = System.currentTimeMillis();
    // ...
}
```

### Instant
```java
import java.time.Instant;

Instant now = Instant.now();
long epochMilli = now.toEpochMilli();

// 타임스탬프로부터 복원
Instant time = Instant.ofEpochMilli(timestamp);
```

---

## 🔢 문자열 처리

### String 반복
```java
// 난이도만큼 0 반복
String target = "0".repeat(difficulty);  // Java 11+

// 예: difficulty=4 → "0000"
```

### String 비교
```java
// 해시가 타겟으로 시작하는지
boolean valid = hash.startsWith(target);

// 문자열 비교
boolean equal = hash.equals(expectedHash);
```

### StringBuilder
```java
StringBuilder hexString = new StringBuilder();
for (byte b : hash) {
    String hex = Integer.toHexString(0xff & b);
    if (hex.length() == 1) {
        hexString.append('0');
    }
    hexString.append(hex);
}
String result = hexString.toString();
```

### 16진수 변환
```java
// 바이트 → 16진수
String hex = Integer.toHexString(0xff & b);

// 0xff & b: 부호 없는 바이트로 변환
// byte -1 → int 255 → "ff"
// byte 15 → int 15 → "f" (앞에 0 추가 필요)
```

---

## 🧪 테스트

### AssertJ
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldCreateGenesisBlock() {
    Blockchain blockchain = new Blockchain(2);
    
    Block genesis = blockchain.getBlock(0);
    
    assertThat(genesis.getIndex()).isEqualTo(0);
    assertThat(genesis.getData()).isEqualTo("Genesis Block");
    assertThat(genesis.getPreviousHash()).isEqualTo("0".repeat(64));
}

@Test
void shouldMineBlock() {
    Blockchain blockchain = new Blockchain(2);
    
    Block block = blockchain.addBlock("Test Data");
    
    assertThat(block.getHash()).startsWith("00");
    assertThat(block.getNonce()).isPositive();
}

@Test
void shouldValidateChain() {
    Blockchain blockchain = new Blockchain(2);
    blockchain.addBlock("Block 1");
    blockchain.addBlock("Block 2");
    
    assertThat(blockchain.isValid()).isTrue();
}

@Test
void shouldDetectTampering() {
    Blockchain blockchain = new Blockchain(2);
    blockchain.addBlock("Block 1");
    
    // 변조
    blockchain.getBlock(1).setData("Tampered!");
    
    assertThat(blockchain.isValid()).isFalse();
}

@Test
void shouldLinkBlocks() {
    Blockchain blockchain = new Blockchain(2);
    blockchain.addBlock("Block 1");
    
    Block block0 = blockchain.getBlock(0);
    Block block1 = blockchain.getBlock(1);
    
    assertThat(block1.getPreviousHash()).isEqualTo(block0.getHash());
}
```

### 채굴 시간 테스트
```java
@Test
void shouldMineInReasonableTime() {
    Blockchain blockchain = new Blockchain(3);  // 적당한 난이도
    
    long start = System.currentTimeMillis();
    blockchain.addBlock("Test");
    long elapsed = System.currentTimeMillis() - start;
    
    assertThat(elapsed).isLessThan(5000);  // 5초 이내
}
```

---

## 📚 Java 21 관련

### Record
```java
// 트랜잭션
public record Transaction(
    String from,
    String to,
    double amount,
    long timestamp
) {}

// 블록 정보 (불변 뷰)
public record BlockInfo(
    int index,
    String hash,
    String previousHash,
    long timestamp
) {
    public static BlockInfo from(Block block) {
        return new BlockInfo(
            block.getIndex(),
            block.getHash(),
            block.getPreviousHash(),
            block.getTimestamp()
        );
    }
}
```

### Pattern Matching
```java
// 블록 타입에 따른 처리
public void processBlock(Object block) {
    switch (block) {
        case GenesisBlock g -> handleGenesis(g);
        case TransactionBlock t -> handleTransactions(t);
        case Block b -> handleRegular(b);
        default -> throw new IllegalArgumentException();
    }
}
```

### Sequenced Collections
```java
// 최신 블록 (Java 21)
Block latest = chain.getLast();

// 첫 블록 (제네시스)
Block genesis = chain.getFirst();

// 역순 순회
for (Block b : chain.reversed()) {
    // 최신 → 제네시스 순
}
```

---

## ⚡ 성능 팁

### 1. MessageDigest 재사용
```java
// 느림: 매번 인스턴스 생성
public String calculateHash() {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    return hexString(digest.digest(input.getBytes()));
}

// 빠름: ThreadLocal 사용
private static final ThreadLocal<MessageDigest> SHA256 = 
    ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    });

public String calculateHash() {
    MessageDigest digest = SHA256.get();
    digest.reset();
    return hexString(digest.digest(input.getBytes()));
}
```

### 2. StringBuilder 초기 용량
```java
// SHA-256은 항상 64자
StringBuilder hexString = new StringBuilder(64);
```

### 3. 병렬 채굴 (선택)
```java
// 여러 스레드가 다른 nonce 범위 시도
public void mineParallel(int difficulty, int threadCount) {
    String target = "0".repeat(difficulty);
    AtomicBoolean found = new AtomicBoolean(false);
    
    IntStream.range(0, threadCount).parallel().forEach(threadId -> {
        int localNonce = threadId;
        
        while (!found.get()) {
            String testHash = calculateHashWithNonce(localNonce);
            if (testHash.startsWith(target)) {
                found.set(true);
                this.nonce = localNonce;
                this.hash = testHash;
            }
            localNonce += threadCount;  // 다음 범위로
        }
    });
}
```

---

## 🔀 직렬화

### JSON (수동)
```java
public String toJson() {
    return String.format(
        "{\"index\":%d,\"timestamp\":%d,\"data\":\"%s\"," +
        "\"previousHash\":\"%s\",\"nonce\":%d,\"hash\":\"%s\"}",
        index, timestamp, data, previousHash, nonce, hash
    );
}
```

### toString
```java
@Override
public String toString() {
    return """
        Block #%d
        ├── Timestamp: %d
        ├── Data: %s
        ├── Previous Hash: %s
        ├── Nonce: %d
        └── Hash: %s
        """.formatted(index, timestamp, data, 
                     previousHash.substring(0, 16) + "...",
                     nonce, hash.substring(0, 16) + "...");
}
```
