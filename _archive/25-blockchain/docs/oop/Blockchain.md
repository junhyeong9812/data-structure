# oop/Blockchain.java

OOP: Transaction 기반 블록, 머클 루트 포함, 인터페이스 분리.

```java
package com.datastructure.blockchain.oop;

import java.util.List;

public interface Blockchain {
    Block addBlock(List<Transaction> transactions);
    Block getBlock(int index);
    Block getLatestBlock();
    int size();
    int getDifficulty();
    boolean isValid();
}
```

---

# oop/Transaction.java

```java
package com.datastructure.blockchain.oop;

import java.time.Instant;

public class Transaction {
    public final String from;
    public final String to;
    public final double amount;
    public final Instant timestamp;

    public Transaction(String from, String to, double amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.timestamp = Instant.now();
    }

    public String serialize() {
        return from + "->" + to + ":" + amount + "@" + timestamp;
    }

    @Override
    public String toString() {
        return serialize();
    }
}
```

---

# oop/Block.java

```java
package com.datastructure.blockchain.oop;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Block {
    private final int index;
    private final long timestamp;
    private final List<Transaction> transactions;
    private final String previousHash;
    private final String merkleRoot;
    private int nonce;
    private String hash;

    public Block(int index, List<Transaction> transactions, String previousHash) {
        this.index = index;
        this.timestamp = System.currentTimeMillis();
        this.transactions = new ArrayList<>(transactions);
        this.previousHash = previousHash;
        this.merkleRoot = computeMerkleRoot(this.transactions);
        this.nonce = 0;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        String input = index + String.valueOf(timestamp) + merkleRoot + previousHash + nonce;
        return sha256(input);
    }

    public void mine(int difficulty) {
        String target = "0".repeat(difficulty);
        while (!hash.startsWith(target)) {
            nonce++;
            hash = calculateHash();
        }
    }

    private static String computeMerkleRoot(List<Transaction> txs) {
        if (txs.isEmpty()) return sha256("");
        List<String> level = new ArrayList<>();
        for (Transaction t : txs) level.add(sha256(t.serialize()));
        while (level.size() > 1) {
            List<String> next = new ArrayList<>();
            for (int i = 0; i < level.size(); i += 2) {
                String l = level.get(i);
                String r = (i + 1 < level.size()) ? level.get(i + 1) : l;
                next.add(sha256(l + r));
            }
            level = next;
        }
        return level.get(0);
    }

    public static String sha256(String input) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256")
                    .digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) sb.append('0');
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public int getIndex() { return index; }
    public long getTimestamp() { return timestamp; }
    public List<Transaction> getTransactions() { return transactions; }
    public String getPreviousHash() { return previousHash; }
    public String getMerkleRoot() { return merkleRoot; }
    public int getNonce() { return nonce; }
    public String getHash() { return hash; }
}
```

---

# oop/SimpleBlockchain.java

```java
package com.datastructure.blockchain.oop;

import java.util.ArrayList;
import java.util.List;

public class SimpleBlockchain implements Blockchain {
    private final List<Block> chain = new ArrayList<>();
    private final int difficulty;

    public SimpleBlockchain(int difficulty) {
        this.difficulty = difficulty;
        Block genesis = new Block(0, new ArrayList<>(), "0".repeat(64));
        genesis.mine(difficulty);
        chain.add(genesis);
    }

    @Override
    public Block addBlock(List<Transaction> txs) {
        Block last = getLatestBlock();
        Block block = new Block(last.getIndex() + 1, txs, last.getHash());
        block.mine(difficulty);
        chain.add(block);
        return block;
    }

    @Override public Block getBlock(int index) { return chain.get(index); }
    @Override public Block getLatestBlock() { return chain.get(chain.size() - 1); }
    @Override public int size() { return chain.size(); }
    @Override public int getDifficulty() { return difficulty; }

    @Override
    public boolean isValid() {
        String target = "0".repeat(difficulty);
        for (int i = 0; i < chain.size(); i++) {
            Block cur = chain.get(i);
            if (!cur.getHash().equals(cur.calculateHash())) return false;
            if (!cur.getHash().startsWith(target)) return false;
            if (i > 0 && !cur.getPreviousHash().equals(chain.get(i - 1).getHash())) return false;
        }
        return true;
    }
}
```
