# oop/BloomFilter.java

블룸 필터 인터페이스. 제네릭 타입 + Funnel(직렬화) 위임.

```java
package com.datastructure.bloomfilter.oop;

public interface BloomFilter<T> {
    void add(T element);
    boolean mightContain(T element);

    void clear();

    int size();              // 비트 수
    int bitCount();          // set된 비트 수
    int hashFunctionCount();
    int approximateCount();
    double expectedFpp();
}
```
