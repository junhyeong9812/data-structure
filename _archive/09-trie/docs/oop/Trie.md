# oop/Trie.java

트라이 인터페이스.

```java
package com.datastructure.trie.oop;

import java.util.List;

public interface Trie {
    void insert(String word);
    boolean search(String word);
    boolean startsWith(String prefix);
    boolean delete(String word);

    int countWordsEqualTo(String word);
    int countWordsStartingWith(String prefix);

    List<String> autocomplete(String prefix);
    List<String> autocomplete(String prefix, int limit);
    List<String> searchWithWildcard(String pattern);
    List<String> getAllWords();
    String longestCommonPrefix();

    int size();
    boolean isEmpty();
    void clear();
}
```
