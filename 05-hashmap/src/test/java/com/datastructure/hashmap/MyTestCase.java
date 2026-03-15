package com.datastructure.hashmap;

import com.datastructure.hashmap.pop.ChainingHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MyTestCase {

    @Nested
    @DisplayName("ChainingHashMap 테스트")
    class ChainingHashMapTest {

        ChainingHashMap<Integer, String> hashMap;

        @BeforeEach
        void setUp() {
            hashMap = new ChainingHashMap<>();
        }

        @Nested
        @DisplayName("put 메서드 테스트")
        class PutTest {

            @Test
            @DisplayName("key와 value 쌍으로 저장할 수 있다.")
            void put_stores_key_value_pair() {
                hashMap.put(1,"A");

                assertThat(hashMap.get(1)).isEqualTo("A");
                assertThat(hashMap.size()).isOne();
                assertThat(hashMap.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("같은 key로 저장하면 값을 덮어쓰기된다.")
            void put_overwrites_existing_key() {
                hashMap.put(1,"A");
                hashMap.put(1,"B");

                assertThat(hashMap.get(1)).isEqualTo("B");
                assertThat(hashMap.size()).isOne();
                assertThat(hashMap.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("null key를 저장할 수 있다.")
            void put_null_key() {
                hashMap.put(null, "A");

                assertThat(hashMap.get(null)).isEqualTo("A");
                assertThat(hashMap.size()).isOne();
                assertThat(hashMap.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("null value를 저장할 수 있다.")
            void put_null_value() {
                hashMap.put(1, null);

                assertThat(hashMap.get(1)).isNull();
                assertThat(hashMap.size()).isOne();
                assertThat(hashMap.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("여러 key-value 쌍을 저장할 수 있다.")
            void put_multiple_entries() {
                hashMap.put(1, "A");
                hashMap.put(2, "B");
                hashMap.put(3, "C");

                assertThat(hashMap.size()).isEqualTo(3);
                assertThat(hashMap.isEmpty()).isFalse();
            }

            @Test
            @DisplayName("put 후 size가 증가한다.")
            void put_increases_size() {
                hashMap.put(1,"A");
                assertThat(hashMap.size()).isEqualTo(1);

                hashMap.put(2,"B");

                assertThat(hashMap.size()).isEqualTo(2);
            }

            @Test
            @DisplayName("덮어쓰기 시 이전 값을 반환한다.")
            void put_returns_previous_value() {
                hashMap.put(1, "A");

                String before = hashMap.put(1, "B");

                assertThat(before).isEqualTo("A");
                assertThat(hashMap.size()).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("get 메서드 테스트")
        class GetTest {}

        @Nested
        @DisplayName("remove 메서드 테스트")
        class RemoveTest {}

        @Nested
        @DisplayName("containsKey 메서드 테스트")
        class ContainsKeyTest {}

        @Nested
        @DisplayName("containValue 메서드 테스트")
        class ContainsValueTest {}

        @Nested
        @DisplayName("size 메서드 테스트")
        class SizeTest {}

        @Nested
        @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {}

        @Nested
        @DisplayName("clear 메서드 테스트")
        class ClearTest {}

        @Nested
        @DisplayName("keySet 메서드 테스트")
        class KeySetTest {}

        @Nested
        @DisplayName("values 메서드 테스트")
        class ValuesTest {}

        @Nested
        @DisplayName("entrySet 메서드 테스트")
        class EntrySetTest {}
    }

    @Nested
    @DisplayName("LinearProbingHashMap 테스트")
    class LinearProbingHashMapTest {

        @Nested
        @DisplayName("put 메서드 테스트")
        class PutTest {}

        @Nested
        @DisplayName("get 메서드 테스트")
        class GetTest {}

        @Nested
        @DisplayName("remove 메서드 테스트")
        class RemoveTest {}

        @Nested
        @DisplayName("containsKey 메서드 테스트")
        class ContainsKeyTest {}

        @Nested
        @DisplayName("containValue 메서드 테스트")
        class ContainsValueTest {}

        @Nested
        @DisplayName("size 메서드 테스트")
        class SizeTest {}

        @Nested
        @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {}

        @Nested
        @DisplayName("clear 메서드 테스트")
        class ClearTest {}

        @Nested
        @DisplayName("keySet 메서드 테스트")
        class KeySetTest {}

        @Nested
        @DisplayName("values 메서드 테스트")
        class ValuesTest {}

        @Nested
        @DisplayName("entrySet 메서드 테스트")
        class EntrySetTest {}
    }

}
