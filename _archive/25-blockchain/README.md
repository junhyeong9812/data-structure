# 25. 블록체인 (Blockchain)

## 📋 문제 정의

**해시 체인과 작업 증명(Proof of Work)**을 기반으로 한 
간단한 블록체인을 구현하세요.

블록체인은 분산 원장 기술의 핵심으로, 데이터의 무결성과 
변조 불가능성을 암호학적으로 보장합니다.

---

## 🎯 학습 목표

- 해시 체인의 원리
- 작업 증명(Proof of Work) 알고리즘
- 블록 구조와 연결
- 체인 유효성 검증
- 머클 트리(Merkle Tree)

---

## 📝 요구사항

### 핵심 개념

| 개념 | 설명 |
|------|------|
| **Block** | 데이터와 메타데이터를 담은 단위 |
| **Hash** | 블록의 고유 식별자 (SHA-256) |
| **Previous Hash** | 이전 블록의 해시 (체인 연결) |
| **Nonce** | 작업 증명용 임의의 숫자 |
| **Difficulty** | 해시가 만족해야 하는 조건 |

### 블록 구조

| 필드 | 설명 |
|------|------|
| `index` | 블록 번호 |
| `timestamp` | 생성 시간 |
| `data` | 블록에 저장된 데이터 |
| `previousHash` | 이전 블록의 해시 |
| `nonce` | 작업 증명 값 |
| `hash` | 현재 블록의 해시 |

### 기본 연산

| 메서드 | 설명 |
|--------|------|
| `addBlock(data)` | 새 블록 추가 (채굴) |
| `isValid()` | 체인 유효성 검증 |
| `getBlock(index)` | 특정 블록 조회 |
| `getLatestBlock()` | 최신 블록 조회 |

---

## 📊 입출력 예시

### 예제 1: 기본 사용
```java
Blockchain blockchain = new Blockchain(4);  // 난이도 4

// 제네시스 블록 자동 생성
Block genesis = blockchain.getBlock(0);
System.out.println(genesis.getData());  // "Genesis Block"

// 새 블록 추가 (채굴)
blockchain.addBlock("Transaction: Alice -> Bob: 10 BTC");
blockchain.addBlock("Transaction: Bob -> Charlie: 5 BTC");

// 체인 유효성 검증
boolean valid = blockchain.isValid();  // true
```

### 예제 2: 블록 구조
```
Block #0 (Genesis)
├── Index: 0
├── Timestamp: 2024-01-01T00:00:00Z
├── Data: "Genesis Block"
├── Previous Hash: "0000000000000000..."
├── Nonce: 12345
└── Hash: "0000abc123..."

Block #1
├── Index: 1
├── Timestamp: 2024-01-01T00:01:00Z
├── Data: "Transaction: Alice -> Bob"
├── Previous Hash: "0000abc123..."  ← Block #0의 해시
├── Nonce: 67890
└── Hash: "0000def456..."
```

### 예제 3: 작업 증명 (Proof of Work)
```java
// 난이도 4 = 해시가 "0000"으로 시작해야 함

// 채굴 과정:
// nonce=0: hash="a1b2c3..." ❌
// nonce=1: hash="f4e5d6..." ❌
// nonce=2: hash="9876ab..." ❌
// ...
// nonce=54321: hash="0000ab..." ✓ 성공!

Block block = blockchain.mineBlock("Some data");
System.out.println(block.getHash().startsWith("0000"));  // true
```

### 예제 4: 체인 변조 탐지
```java
Blockchain blockchain = new Blockchain(4);
blockchain.addBlock("Block 1");
blockchain.addBlock("Block 2");

// 체인 유효성
System.out.println(blockchain.isValid());  // true

// 변조 시도
blockchain.getBlock(1).setData("Tampered!");

// 변조 탐지
System.out.println(blockchain.isValid());  // false
// Block 1의 해시가 변경되어 Block 2의 previousHash와 불일치
```

### 예제 5: 해시 체인 시각화
```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Block #0      │     │   Block #1      │     │   Block #2      │
│   (Genesis)     │     │                 │     │                 │
├─────────────────┤     ├─────────────────┤     ├─────────────────┤
│ prevHash: 0000  │     │ prevHash: abc...│────▶│ prevHash: def...│
│ data: Genesis   │     │ data: Tx1       │     │ data: Tx2       │
│ nonce: 12345    │     │ nonce: 67890    │     │ nonce: 11111    │
│ hash: abc...    │────▶│ hash: def...    │────▶│ hash: 789...    │
└─────────────────┘     └─────────────────┘     └─────────────────┘

체인 연결: Block N의 hash = Block N+1의 previousHash
```

---

## 🔍 핵심 개념

### 해시 계산
```java
// 블록의 해시 = SHA-256(index + timestamp + data + previousHash + nonce)

public String calculateHash() {
    String input = index + timestamp + data + previousHash + nonce;
    return sha256(input);
}
```

### 작업 증명 (Proof of Work)
```
난이도(difficulty) = 해시 앞부분에 필요한 0의 개수

difficulty=1: hash가 "0"으로 시작
difficulty=2: hash가 "00"으로 시작
difficulty=4: hash가 "0000"으로 시작

난이도가 높을수록 채굴 시간 증가
```

### 체인 유효성 검증
```java
for (int i = 1; i < chain.size(); i++) {
    Block current = chain.get(i);
    Block previous = chain.get(i - 1);
    
    // 1. 현재 블록의 해시 검증
    if (!current.getHash().equals(current.calculateHash())) {
        return false;
    }
    
    // 2. 이전 블록과의 연결 검증
    if (!current.getPreviousHash().equals(previous.getHash())) {
        return false;
    }
    
    // 3. 작업 증명 검증
    if (!current.getHash().startsWith(getTarget())) {
        return false;
    }
}
```

---

## 💡 힌트

### 기본 구조
```java
public class Blockchain {
    private final List<Block> chain = new ArrayList<>();
    private final int difficulty;
    
    public Blockchain(int difficulty) {
        this.difficulty = difficulty;
        chain.add(createGenesisBlock());
    }
    
    private Block createGenesisBlock() {
        return new Block(0, "Genesis Block", "0".repeat(64));
    }
    
    private String getTarget() {
        return "0".repeat(difficulty);
    }
}
```

### Block 클래스
```java
public class Block {
    private final int index;
    private final long timestamp;
    private String data;
    private final String previousHash;
    private int nonce;
    private String hash;
    
    public Block(int index, String data, String previousHash) {
        this.index = index;
        this.timestamp = System.currentTimeMillis();
        this.data = data;
        this.previousHash = previousHash;
        this.nonce = 0;
        this.hash = calculateHash();
    }
    
    public String calculateHash() {
        String input = index + Long.toString(timestamp) + data + previousHash + nonce;
        return sha256(input);
    }
}
```

### SHA-256 해시
```java
private static String sha256(String input) {
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
        throw new RuntimeException(e);
    }
}
```

---

## ✅ 체크리스트

- [ ] Block 구조 구현
- [ ] SHA-256 해시 계산
- [ ] 제네시스 블록 생성
- [ ] 작업 증명 (채굴)
- [ ] 블록 추가
- [ ] 체인 유효성 검증
- [ ] 머클 트리 (선택)
- [ ] 트랜잭션 구조 (선택)

---

## 📚 참고

- Bitcoin Whitepaper (Satoshi Nakamoto)
- Ethereum Yellow Paper
- Mastering Bitcoin (Andreas Antonopoulos)
- SHA-256 알고리즘
