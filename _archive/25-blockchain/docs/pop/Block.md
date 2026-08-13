# pop/Block.java

블록 단위. SHA-256 해시 계산 + Proof of Work 채굴.

```java
package com.datastructure.blockchain.pop;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        String input = index + String.valueOf(timestamp) + data + previousHash + nonce;
        return sha256(input);
    }

    /** 난이도만큼 0으로 시작하는 해시를 찾을 때까지 nonce 증가. */
    public void mine(int difficulty) {
        String target = "0".repeat(difficulty);
        while (!hash.startsWith(target)) {
            nonce++;
            hash = calculateHash();
        }
    }

    public int getIndex() { return index; }
    public long getTimestamp() { return timestamp; }
    public String getData() { return data; }
    public String getPreviousHash() { return previousHash; }
    public int getNonce() { return nonce; }
    public String getHash() { return hash; }

    public void setData(String data) {
        this.data = data;
        // 의도적으로 hash를 갱신하지 않음 — 변조 탐지 시연용
    }

    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
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
}
```
