# 블록체인 풀이 해설

## 📌 핵심 아이디어

블록체인은 **해시로 연결된 블록들의 체인**입니다.
각 블록은 이전 블록의 해시를 포함하여 변조를 방지합니다.

**핵심 특징**:
- 해시 체인으로 무결성 보장
- 작업 증명으로 블록 생성 비용 부과
- 분산 합의를 통한 탈중앙화

---

## 🔑 핵심 개념

### 1. 해시 체인의 보안성
```
Block 1 변조 시:
┌─────────┐     ┌─────────┐     ┌─────────┐
│ Block 0 │     │ Block 1 │     │ Block 2 │
│ hash:A  │────▶│ prev:A  │────▶│ prev:B  │
│         │     │ hash:B  │     │ hash:C  │
└─────────┘     └─────────┘     └─────────┘

Block 1의 data 변경 → hash:B가 hash:B'로 변경
                    → Block 2의 prev:B ≠ B'
                    → 체인 무효!

모든 후속 블록을 재채굴해야 변조 가능
→ 난이도가 높을수록 사실상 불가능
```

### 2. 작업 증명 과정
```java
public void mine(int difficulty) {
    String target = "0".repeat(difficulty);
    
    while (!hash.startsWith(target)) {
        nonce++;
        hash = calculateHash();
    }
    
    // 예: difficulty=4, target="0000"
    // nonce=0: hash="a3f2..."     ❌
    // nonce=1: hash="7b1c..."     ❌
    // ...
    // nonce=52341: hash="0000a..."  ✓
}
```

### 3. 난이도와 채굴 시간
```
난이도  |  평균 시도 횟수  |  예상 시간
--------|----------------|------------
1       |  16            |  즉시
2       |  256           |  ~1ms
3       |  4,096         |  ~10ms
4       |  65,536        |  ~100ms
5       |  1,048,576     |  ~1초
6       |  16,777,216    |  ~10초

해시는 16진수이므로 난이도 N당 16^N 시도 필요
```

---

## 📝 POP 구현 해설

### 완전한 구현
```java
public class Blockchain {
    private final List<Block> chain = new ArrayList<>();
    private final int difficulty;
    
    public Blockchain(int difficulty) {
        this.difficulty = difficulty;
        chain.add(createGenesisBlock());
    }
    
    // 제네시스 블록 생성
    private Block createGenesisBlock() {
        Block genesis = new Block(0, "Genesis Block", "0".repeat(64));
        genesis.mine(difficulty);
        return genesis;
    }
    
    // 블록 추가 (채굴)
    public Block addBlock(String data) {
        Block previousBlock = getLatestBlock();
        Block newBlock = new Block(
            previousBlock.getIndex() + 1,
            data,
            previousBlock.getHash()
        );
        newBlock.mine(difficulty);
        chain.add(newBlock);
        return newBlock;
    }
    
    // 최신 블록
    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }
    
    // 특정 블록
    public Block getBlock(int index) {
        if (index < 0 || index >= chain.size()) {
            throw new IndexOutOfBoundsException();
        }
        return chain.get(index);
    }
    
    // 체인 유효성 검증
    public boolean isValid() {
        String target = "0".repeat(difficulty);
        
        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block previous = chain.get(i - 1);
            
            // 1. 해시 무결성
            if (!current.getHash().equals(current.calculateHash())) {
                return false;
            }
            
            // 2. 체인 연결
            if (!current.getPreviousHash().equals(previous.getHash())) {
                return false;
            }
            
            // 3. 작업 증명
            if (!current.getHash().startsWith(target)) {
                return false;
            }
        }
        
        return true;
    }
    
    // 체인 길이
    public int size() {
        return chain.size();
    }
    
    // 난이도
    public int getDifficulty() {
        return difficulty;
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
        String input = index + 
                       Long.toString(timestamp) + 
                       data + 
                       previousHash + 
                       nonce;
        return sha256(input);
    }
    
    public void mine(int difficulty) {
        String target = "0".repeat(difficulty);
        
        while (!hash.startsWith(target)) {
            nonce++;
            hash = calculateHash();
        }
    }
    
    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            
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
    
    // Getters
    public int getIndex() { return index; }
    public long getTimestamp() { return timestamp; }
    public String getData() { return data; }
    public String getPreviousHash() { return previousHash; }
    public int getNonce() { return nonce; }
    public String getHash() { return hash; }
    
    // 테스트용 (실제로는 불변이어야 함)
    void setData(String data) {
        this.data = data;
        // 주의: hash는 자동 갱신되지 않음
    }
    
    @Override
    public String toString() {
        return String.format(
            "Block{index=%d, hash=%s, previousHash=%s, nonce=%d}",
            index, hash.substring(0, 16) + "...", 
            previousHash.substring(0, 16) + "...", nonce
        );
    }
}
```

### 머클 트리 (선택)
```java
public class MerkleTree {
    
    public static String computeMerkleRoot(List<String> transactions) {
        if (transactions.isEmpty()) {
            return sha256("");
        }
        
        List<String> hashes = new ArrayList<>();
        for (String tx : transactions) {
            hashes.add(sha256(tx));
        }
        
        while (hashes.size() > 1) {
            List<String> newLevel = new ArrayList<>();
            
            for (int i = 0; i < hashes.size(); i += 2) {
                String left = hashes.get(i);
                String right = (i + 1 < hashes.size()) 
                    ? hashes.get(i + 1) 
                    : left;  // 홀수면 복제
                
                newLevel.add(sha256(left + right));
            }
            
            hashes = newLevel;
        }
        
        return hashes.get(0);
    }
}
```

### 트랜잭션 블록
```java
public class TransactionBlock extends Block {
    private final List<Transaction> transactions;
    private final String merkleRoot;
    
    public TransactionBlock(int index, List<Transaction> transactions, 
                           String previousHash) {
        super(index, "", previousHash);
        this.transactions = new ArrayList<>(transactions);
        this.merkleRoot = MerkleTree.computeMerkleRoot(
            transactions.stream()
                .map(Transaction::toString)
                .toList()
        );
    }
    
    @Override
    public String calculateHash() {
        String input = getIndex() + 
                       Long.toString(getTimestamp()) + 
                       merkleRoot + 
                       getPreviousHash() + 
                       getNonce();
        return sha256(input);
    }
}

public record Transaction(String from, String to, double amount, long timestamp) {
    @Override
    public String toString() {
        return from + to + amount + timestamp;
    }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 |
|------|-----------|
| 블록 추가 | O(16^d) 평균 |
| 체인 검증 | O(n) |
| 블록 조회 | O(1) |
| 해시 계산 | O(m) |

d = 난이도
n = 체인 길이
m = 데이터 크기

---

## ❌ 흔한 실수

### 1. 해시 계산에 모든 필드 포함
```java
// 잘못됨: hash 자체를 포함
public String calculateHash() {
    return sha256(index + data + hash);  // 순환 참조!
}

// 올바름: hash 제외
public String calculateHash() {
    return sha256(index + timestamp + data + previousHash + nonce);
}
```

### 2. 제네시스 블록의 previousHash
```java
// 잘못됨: null 사용
new Block(0, "Genesis", null);  // NPE 가능

// 올바름: 0으로 채워진 해시 사용
new Block(0, "Genesis", "0".repeat(64));
```

### 3. 채굴 후 해시 갱신
```java
// 잘못됨: 채굴 후 해시 갱신 누락
public void mine(int difficulty) {
    while (!hash.startsWith(target)) {
        nonce++;
        // hash = calculateHash();  // 누락!
    }
}

// 올바름
public void mine(int difficulty) {
    while (!hash.startsWith(target)) {
        nonce++;
        hash = calculateHash();  // 매번 갱신
    }
}
```

---

## 🔗 관련 문제

- 분산 합의 알고리즘 (PBFT, Raft)
- 암호화폐 구현
- 스마트 컨트랙트
- 분산 원장
