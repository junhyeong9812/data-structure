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
        class GetTest {

            @Test
            @DisplayName("key값을 통해 value를 조회할 수 있다.")
            void get_returns_value_by_key() {
                hashMap.put(1, "A");

                assertThat(hashMap.get(1)).isEqualTo("A");
            }

            @Test
            @DisplayName("key값을 통해 여러번 조회 시 같은 value값을 리턴한다.")
            void get_multiple_times_returns_same_value() {
                hashMap.put(1, "A");

                assertThat(hashMap.get(1)).isEqualTo("A");
                assertThat(hashMap.get(1)).isEqualTo("A");
                assertThat(hashMap.get(1)).isEqualTo("A");
            }

            @Test
            @DisplayName("존재하지 않는 key로 조회하면 null을 반환한다.")
            void get_returns_null_when_key_not_found() {
                hashMap.put(1, "A");

                assertThat(hashMap.get(2)).isNull();
            }

            @Test
            @DisplayName("null key로 조회할 수 있다.")
            void get_with_null_key() {
                hashMap.put(null, "A");

                assertThat(hashMap.get(null)).isEqualTo("A");
            }

            @Test
            @DisplayName("값이 null인 경우 null을 반환한다.")
            void get_returns_null_value() {
                hashMap.put(1, null);

                assertThat(hashMap.get(1)).isNull();
            }
        }

        @Nested
        @DisplayName("remove 메서드 테스트")
        class RemoveTest {

            @Test
            @DisplayName("key에 해당하는 값을 삭제하고 값을 반환한다.")
            void remove_returns_value_and_deletes() {
                hashMap.put(1, "A");

                String removed = hashMap.remove(1);

                assertThat(removed).isEqualTo("A");
            }

            @Test
            @DisplayName("존재하지 않는 key를 삭제하면 null을 반한환다.")
            void remove_returns_null_when_key_not_found() {
                hashMap.put(1, "A");

                String removed = hashMap.remove(2);

                assertThat(removed).isEqualTo(null);
            }

            @Test
            @DisplayName("삭제 후 size가 감소한다.")
            void remove_decreases_size() {
                hashMap.put(1, "A");

                hashMap.remove(1);

                assertThat(hashMap.size()).isZero();
            }

            @Test
            @DisplayName("null key를 삭제할 수 있다.")
            void remove_null_key() {
                hashMap.put(null, "A");

                String removed = hashMap.remove(null);

                assertThat(removed).isEqualTo("A");
            }

            @Test
            @DisplayName("삭제 후 해당 key로 조회하면 null을 반환한다.")
            void remove_then_get_returns_null() {
                hashMap.put(1, "A");

                String removed = hashMap.remove(1);

                assertThat(removed).isEqualTo("A");
                assertThat(hashMap.get(1)).isNull();
            }
        }

        @Nested
        @DisplayName("containsKey 메서드 테스트")
        class ContainsKeyTest {

            @Test
            @DisplayName("존재하는 key를 찾을 수 있다.")
            void containsKey_returns_true_when_exists() {
                hashMap.put(1, "A");

                assertThat(hashMap.containsKey(1)).isTrue();
            }

            @Test
            @DisplayName("존재하지 않는 key는  false를 반환한다.")
            void containsKey_returns_false_when_not_exists() {
                hashMap.put(1, "A");

                assertThat(hashMap.containsKey(2)).isFalse();
            }

            @Test
            @DisplayName("null인 키를 찾을 수 있다.")
            void containsKey_with_null_key() {
                hashMap.put(null, "A");

                assertThat(hashMap.containsKey(null)).isTrue();
            }

            @Test
            @DisplayName("삭제 후 해당 key는 false를 반환한다.")
            void containsKey_return_false_after_remove() {
                hashMap.put(1, "A");

                assertThat(hashMap.containsKey(1)).isTrue();

                hashMap.remove(1);

                assertThat(hashMap.containsKey(1)).isFalse();
            }
        }

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
