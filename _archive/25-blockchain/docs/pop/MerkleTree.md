# pop/MerkleTree.java

머클 트리. 트랜잭션 리스트의 루트 해시 계산 + 멤버십 증명.

```java
package com.datastructure.blockchain.pop;

import java.util.ArrayList;
import java.util.List;

public class MerkleTree {
    private final String root;
    private final List<List<String>> levels;

    public MerkleTree(List<String> data) {
        if (data == null || data.isEmpty()) throw new IllegalArgumentException("empty");
        this.levels = new ArrayList<>();

        List<String> current = new ArrayList<>();
        for (String d : data) current.add(Block.sha256(d));
        levels.add(current);

        while (current.size() > 1) {
            List<String> next = new ArrayList<>();
            for (int i = 0; i < current.size(); i += 2) {
                String left = current.get(i);
                String right = (i + 1 < current.size()) ? current.get(i + 1) : left;
                next.add(Block.sha256(left + right));
            }
            levels.add(next);
            current = next;
        }
        this.root = current.get(0);
    }

    public String getRoot() {
        return root;
    }

    public int height() {
        return levels.size();
    }

    /** 인덱스의 데이터에 대한 멤버십 증명 경로 (sibling 해시 리스트) */
    public List<String> getProof(int index) {
        List<String> proof = new ArrayList<>();
        for (int level = 0; level < levels.size() - 1; level++) {
            List<String> nodes = levels.get(level);
            int sibling = (index % 2 == 0) ? index + 1 : index - 1;
            if (sibling >= nodes.size()) sibling = index;
            proof.add(nodes.get(sibling));
            index /= 2;
        }
        return proof;
    }

    public static boolean verify(String data, int index, List<String> proof, String expectedRoot) {
        String hash = Block.sha256(data);
        for (String sibling : proof) {
            hash = (index % 2 == 0)
                    ? Block.sha256(hash + sibling)
                    : Block.sha256(sibling + hash);
            index /= 2;
        }
        return hash.equals(expectedRoot);
    }
}
```
