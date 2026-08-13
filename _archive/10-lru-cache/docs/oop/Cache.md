# oop/Cache.java

캐시 인터페이스. 제네릭 K, V.

```java
package com.datastructure.lrucache.oop;

import java.util.List;

public interface Cache<K, V> {
    V get(K key);
    void put(K key, V value);
    V remove(K key);

    V peek(K key);
    boolean containsKey(K key);

    int size();
    int capacity();
    boolean isEmpty();
    void clear();

    List<K> keys();
    List<V> values();
}
```
