# pop/Blockchain.java

해시 체인 + PoW 난이도. addBlock(채굴) + isValid(체인 검증).

```java
package com.datastructure.blockchain.pop;

import java.util.ArrayList;
import java.util.List;

public class Blockchain {
    private final List<Block> chain = new ArrayList<>();
    private final int difficulty;

    public Blockchain(int difficulty) {
        if (difficulty < 1) throw new IllegalArgumentException();
        this.difficulty = difficulty;
        chain.add(createGenesisBlock());
    }

    private Block createGenesisBlock() {
        Block g = new Block(0, "Genesis Block", "0".repeat(64));
        g.mine(difficulty);
        return g;
    }

    public Block addBlock(String data) {
        Block last = getLatestBlock();
        Block block = new Block(last.getIndex() + 1, data, last.getHash());
        block.mine(difficulty);
        chain.add(block);
        return block;
    }

    public Block getBlock(int index) {
        return chain.get(index);
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    public int size() {
        return chain.size();
    }

    public int getDifficulty() {
        return difficulty;
    }

    public boolean isValid() {
        String target = "0".repeat(difficulty);
        if (chain.isEmpty()) return true;

        Block genesis = chain.get(0);
        if (!genesis.getHash().equals(genesis.calculateHash())) return false;
        if (!genesis.getHash().startsWith(target)) return false;

        for (int i = 1; i < chain.size(); i++) {
            Block cur = chain.get(i);
            Block prev = chain.get(i - 1);

            if (!cur.getHash().equals(cur.calculateHash())) return false;
            if (!cur.getPreviousHash().equals(prev.getHash())) return false;
            if (!cur.getHash().startsWith(target)) return false;
        }
        return true;
    }
}
```
