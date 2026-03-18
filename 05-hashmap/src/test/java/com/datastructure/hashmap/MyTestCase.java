package com.datastructure.hashmap;

import com.datastructure.hashmap.pop.ChainingHashMap;
import com.datastructure.hashmap.pop.LinearProbingHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Set;

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
        class ContainsValueTest {
            @Test
            @DisplayName("value가 존재하면 true를 반환한다.")
            void containsValue_returns_true_when_exists() {
                hashMap.put(1, "A");

                assertThat(hashMap.containsValue("A")).isTrue();
            }

            @Test
            @DisplayName("value가 존재하지 않으면 false를 반환한다.")
            void containsValue_returns_false_when_not_exists() {
                hashMap.put(1, "A");

                assertThat(hashMap.containsValue("B")).isFalse();
            }

            @Test
            @DisplayName("null인 value를 찾을 수 있다.")
            void containsValue_with_null_value() {
                hashMap.put(1, null);

                assertThat(hashMap.containsValue(null)).isTrue();
            }

            @Test
            @DisplayName("삭제 후 해당 value는 false를 반환한다.")
            void containsValue_returns_false_after_remove() {
                hashMap.put(1, "A");

                assertThat(hashMap.containsValue("A")).isTrue();

                hashMap.remove(1);

                assertThat(hashMap.containsValue("A")).isFalse();
            }
        }

        @Nested
        @DisplayName("size 메서드 테스트")
        class SizeTest {
            @Test
            @DisplayName("처음 size는 0이다.")
            void size_return_zero_when_empty() {
                assertThat(hashMap.size()).isZero();
            }

            @Test
            @DisplayName("put을 하면 size가 증가한다.")
            void size_increases_after_put() {
                assertThat(hashMap.size()).isZero();

                hashMap.put(1, "A");

                assertThat(hashMap.size()).isEqualTo(1);
            }

            @Test
            @DisplayName("remove를 하면 size가 감소한다.")
            void size_decreases_after_remove() {
                hashMap.put(1, "A");
                hashMap.put(2, "B");
                hashMap.put(3, "C");
                assertThat(hashMap.size()).isEqualTo(3);

                hashMap.remove(3);

                assertThat(hashMap.size()).isEqualTo(2);
            }
        }

        @Nested
        @DisplayName("isEmpty 메서드 테스트")
        class IsEmptyTest {

            @Test
            @DisplayName("빈 해시맵은 true를 반환한다.")
            void isEmpty_returns_true_when_empty() {
                assertThat(hashMap.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("요소가 있는 해시맵은 false를 반환한다.")
            void isEmpty_returns_false_when_not_empty() {
                hashMap.put(1, "A");

                assertThat(hashMap.isEmpty()).isFalse();
            }
        }

        @Nested
        @DisplayName("clear 메서드 테스트")
        class ClearTest {

            @Test
            @DisplayName("빈 해시맵에 동작한다.")
            void clear_empty_hashmap_remains_empty() {
                hashMap.clear();

                assertThat(hashMap.size()).isZero();
                assertThat(hashMap.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("요소가 있는 hashMap을 초기화한다.")
            void clear_removes_all_entries() {
                hashMap.put(1, "A");
                hashMap.put(2, "A");
                hashMap.put(3, "A");

                hashMap.clear();

                assertThat(hashMap.size()).isZero();
                assertThat(hashMap.isEmpty()).isTrue();
            }
        }

        @Nested
        @DisplayName("keySet 메서드 테스트")
        class KeySetTest {

            @Test
            @DisplayName("빈 hashMap에는 빈 Set이 반환된다.")
            void keySet_returns_empty_set_when_empty() {
                Set<Integer> keySet = hashMap.keySet();

                assertThat(keySet.size()).isZero();
            }

            @Test
            @DisplayName("모든 key가 반환된다.")
            void keySet_returns_all_keys() {
                hashMap.put(1, "A");
                hashMap.put(2, "A");
                hashMap.put(3, "A");

                Set<Integer> keySet = hashMap.keySet();

                assertThat(keySet.contains(1)).isTrue();
                assertThat(keySet.contains(2)).isTrue();
                assertThat(keySet.contains(3)).isTrue();
                assertThat(keySet).containsExactlyInAnyOrder(1, 2, 3);
            }

            @Test
            @DisplayName("null key도 포함된다.")
            void keySet_contains_null_key() {
                hashMap.put(null, "A");

                Set<Integer> keySet = hashMap.keySet();

                assertThat(keySet.contains(null)).isTrue();
            }
        }

        @Nested
        @DisplayName("values 메서드 테스트")
        class ValuesTest {

            @Test
            @DisplayName("빈 hashMap은 빈 값이 반환한다.")
            void values_returns_empty_collection_when_empty() {
                Collection<String> values = hashMap.values();

                assertThat(values.size()).isZero();
            }

            @Test
            @DisplayName("모든 values를 반환한다.")
            void values_returns_all_values() {
                hashMap.put(1, "A");
                hashMap.put(2, "B");
                hashMap.put(3, "C");

                Collection<String> values = hashMap.values();

                assertThat(values.contains("A")).isTrue();
                assertThat(values.contains("B")).isTrue();
                assertThat(values.contains("C")).isTrue();
                assertThat(values).containsExactlyInAnyOrder("A", "B", "C");
            }

            @Test
            @DisplayName("null인 value도 포함된다.")
            void values_contains_null_value() {
                hashMap.put(1, "A");
                hashMap.put(2, "B");
                hashMap.put(3, null);

                Collection<String> values = hashMap.values();

                assertThat(values.contains(null)).isTrue();
            }
        }

        @Nested
        @DisplayName("entrySet 메서드 테스트")
        class EntrySetTest {
            @Test
            @DisplayName("빈 hashMap에는 빈 Set을 반환한다")
            void entrySet_returns_empty_set_when_empty() {
                Set<ChainingHashMap.Entry<Integer, String>> entries = hashMap.entrySet();

                assertThat(entries.isEmpty()).isTrue();
            }

            @Test
            @DisplayName("모든 엔트리를 반환한다.")
            void entrySet_returns_all_entries() {
                hashMap.put(1, "A");
                hashMap.put(2, "B");
                hashMap.put(3, "C");
                Set<ChainingHashMap.Entry<Integer, String>> entries = hashMap.entrySet();

                assertThat(entries).hasSize(3);
                assertThat(entries).extracting(
                        ChainingHashMap.Entry::getKey
                ).containsExactlyInAnyOrder(1, 2, 3);
                assertThat(entries).extracting(
                        ChainingHashMap.Entry::getValue
                ).containsExactlyInAnyOrder("A", "B", "C");
            }

            @Test
            @DisplayName("null key를 가진 엔트리도 포함된다.")
            void entrySet_contains_null_key_entry() {
                hashMap.put(1, "A");
                hashMap.put(2, "B");
                hashMap.put(3, null);

                Set<ChainingHashMap.Entry<Integer, String>> entries = hashMap.entrySet();

                assertThat(entries).hasSize(3);
                assertThat(entries).extracting(
                        ChainingHashMap.Entry::getKey
                ).containsExactlyInAnyOrder(1, 2, 3);
                assertThat(entries).extracting(
                        ChainingHashMap.Entry::getValue
                ).containsExactlyInAnyOrder("A", "B", null);
            }
        }
    }

    @Nested
    @DisplayName("LinearProbingHashMap 테스트")
    class LinearProbingHashMapTest {

        LinearProbingHashMap<Integer, String> hashMap;
        @BeforeEach
        void setup() {hashMap = new LinearProbingHashMap<>();}

        @Nested
        @DisplayName("put 메서드 테스트")
        class PutTest {

            @Test
            @DisplayName("key-value 쌍으로 저장이 가능하다.")
            void put_stores_key_value_pair() {
                hashMap.put(1, "A");

                assertThat(hashMap.size()).isEqualTo(1);
                assertThat(hashMap.isEmpty()).isFalse();
                assertThat(hashMap.get(1)).isEqualTo("A");
            }

            @Test
            @DisplayName("같은 key값으로 넣으면 덮어쓰인다.")
            void put_overwrites_existing_key() {
                hashMap.put(1, "A");
                hashMap.put(1, "B");

                assertThat(hashMap.get(1)).isEqualTo("B");
                assertThat(hashMap.size()).isEqualTo(1);
            }

            @Test
            @DisplayName("key값이 null일 때 저장이 가능하다.")
            void put_null_key() {
                hashMap.put(null, "A");

                assertThat(hashMap.get(null)).isEqualTo("A");
                assertThat(hashMap.size()).isEqualTo(1);
            }

            @Test
            @DisplayName("value값이 null일 때 저장이 가능하다.")
            void put_null_value() {
                hashMap.put(1, null);

                assertThat(hashMap.get(1)).isNull();
                assertThat(hashMap.size()).isEqualTo(1);
            }

            @Test
            @DisplayName("같은 key값으로 저장하면 이전 값이 반환된다.")
            void put_returns_previous_value() {
                hashMap.put(1, "A");

                String before = hashMap.put(1, "B");

                assertThat(before).isEqualTo("A");
                assertThat(hashMap.size()).isEqualTo(1);
            }

            @Test
            @DisplayName("연속하여 저장할 수 있다.")
            void put_multiple_entries() {
                hashMap.put(1, "A");
                hashMap.put(2, "B");
                hashMap.put(3, "C");

                assertThat(hashMap.get(1)).isEqualTo("A");
                assertThat(hashMap.get(2)).isEqualTo("B");
                assertThat(hashMap.get(3)).isEqualTo("C");
            }

            @Test
            @DisplayName("put을 사용할 때 사이즈가 증가한다.")
            void put_increases_size() {
                assertThat(hashMap.size()).isEqualTo(0);

                hashMap.put(1, "A");

                assertThat(hashMap.size()).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("get 메서드 테스트")
        class GetTest {

            @Test
            @DisplayName("key를 통해 value를 찾을 수 있다.")
            void get_returns_value_by_key() {
                hashMap.put(1, "A");

                assertThat(hashMap.get(1)).isEqualTo("A");
            }

            @Test
            @DisplayName("존재하지 않는 key를 조회 시 null을 반환한다.")
            void get_returns_null_when_key_not_found() {
                hashMap.put(1, "A");

                assertThat(hashMap.get(2)).isNull();
            }

            @Test
            @DisplayName("null인 키의 value를 찾을 수 있다.")
            void get_with_null_key() {
                hashMap.put(null, "A");

                assertThat(hashMap.get(null)).isEqualTo("A");
            }

            @Test
            @DisplayName("같은 키를 여러번 조회하면 값은 같다.")
            void get_multiple_times_returns_same_value() {
                hashMap.put(1, "A");

                assertThat(hashMap.get(1)).isEqualTo("A");
                assertThat(hashMap.get(1)).isEqualTo("A");
                assertThat(hashMap.get(1)).isEqualTo("A");
            }
        }

        @Nested
        @DisplayName("remove 메서드 테스트")
        class RemoveTest {

            @Test
            @DisplayName("해당하는 key값의 value를 삭제할 수 있다.")
            void remove_returns_value_and_deletes() {
                hashMap.put(1, "A");

                String removed = hashMap.remove(1);

                assertThat(hashMap.size()).isZero();
                assertThat(hashMap.isEmpty()).isTrue();
                assertThat(removed).isEqualTo("A");
            }

            @Test
            @DisplayName("null인 key의 value를 삭제할 수 있다.")
            void remove_null_key() {
                hashMap.put(null, "A");

                String removed = hashMap.remove(null);

                assertThat(hashMap.size()).isZero();
                assertThat(hashMap.isEmpty()).isTrue();
                assertThat(removed).isEqualTo("A");
            }

            @Test
            @DisplayName("key가 존재하지 않으면 null을 반환한다.")
            void remove_returns_null_when_key_not_found() {
                hashMap.put(1, "A");

                String removed = hashMap.remove(2);

                assertThat(hashMap.size()).isOne();
                assertThat(hashMap.isEmpty()).isFalse();
                assertThat(removed).isEqualTo(null);
            }

            @Test
            @DisplayName("사이즈가 감소한다.")
            void remove_decreases_size() {
                hashMap.put(1, "A");

                hashMap.remove(1);

                assertThat(hashMap.size()).isEqualTo(0);
            }

            @Test
            @DisplayName("삭제 후 조회 시 null을 반환한다.")
            void remove_then_get_returns_null() {
                hashMap.put(1, "A");

                hashMap.remove(1);

                assertThat(hashMap.get(1)).isNull();
            }
        }

        @Nested
        @DisplayName("containsKey 메서드 테스트")
        class ContainsKeyTest {

            @Test
            @DisplayName("key가 존재하면 true를 반환한다.")
            void containsKey_returns_true_when_exists() {
                hashMap.put(1, "A");
                hashMap.put(2, "A");
                hashMap.put(3, "A");

                assertThat(hashMap.containsKey(1)).isTrue();
                assertThat(hashMap.containsKey(2)).isTrue();
                assertThat(hashMap.containsKey(3)).isTrue();
            }

            @Test
            @DisplayName("key가 존재하지 않으면 false를 반환한다.")
            void containsKey_returns_false_when_not_exists() {
                hashMap.put(1, "A");
                hashMap.put(2, "A");
                hashMap.put(3, "A");

                assertThat(hashMap.containsKey(4)).isFalse();
            }

            @Test
            @DisplayName("null인 키를 조회할 수 있다.")
            void containsKey_with_null_key() {
                hashMap.put(1, "A");
                hashMap.put(null, "A");
                hashMap.put(3, "A");

                assertThat(hashMap.containsKey(null)).isTrue();
            }
        }

        @Nested
        @DisplayName("containValue 메서드 테스트")
        class ContainsValueTest {

            @Test
            @DisplayName("value가 존재하면 true를 반환한다.")
            void containsValue_returns_true_when_exists() {
                hashMap.put(1, "A");
                hashMap.put(2, "B");
                hashMap.put(3, "C");

                assertThat(hashMap.containsValue("B")).isTrue();
            }

            @Test
            @DisplayName("value가 존재하지 않으면 false를 반환한다.")
            void containsValue_returns_false_when_not_exist() {
                hashMap.put(1, "A");
                hashMap.put(2, "B");
                hashMap.put(3, "C");

                assertThat(hashMap.containsValue("D")).isFalse();
            }

            @Test
            @DisplayName("null인 value를 조회할 수 있다.")
            void containsValue_with_null_value() {
                hashMap.put(1, "A");
                hashMap.put(2, null);
                hashMap.put(3, "C");

                assertThat(hashMap.containsValue(null)).isTrue();
            }
        }

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
