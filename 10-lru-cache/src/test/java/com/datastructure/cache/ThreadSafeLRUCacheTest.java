package com.datastructure.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@DisplayName("ThreadSafeLRUCache: 동시에 써도 되는")
class ThreadSafeLRUCacheTest extends CacheContractTest {

    @Override
    protected Cache<Integer, String> create(int capacity) {
        return new ThreadSafeLRUCache<>(capacity);
    }

    @Nested
    @DisplayName("감싸기")
    class Wrapping {

        @Test
        @DisplayName("다른 캐시를 감쌀 수 있다")
        void wrapsAnyCache() {
            Cache<Integer, String> c = new ThreadSafeLRUCache<>(new LinkedHashMapLRU<>(2));
            c.put(1, "a");
            c.put(2, "b");
            c.put(3, "c");
            assertEquals(2, c.size());
            assertEquals(List.of(2, 3), c.keysInOrder());
        }

        @Test
        @DisplayName("감쌀 대상이 없으면 예외")
        void rejectsNullDelegate() {
            assertThrows(IllegalArgumentException.class,
                    () -> new ThreadSafeLRUCache<Integer, String>((Cache<Integer, String>) null));
        }
    }

    @Nested
    @DisplayName("동시 접근")
    class Concurrency {

        @Test
        @Timeout(60)
        @DisplayName("여러 스레드가 써도 불변식이 깨지지 않는다")
        void invariantsHoldUnderContention() throws Exception {
            final int threads = 8;
            final int opsPerThread = 20_000;
            final int capacity = 64;

            for (int trial = 0; trial < 5; trial++) {
                Cache<Integer, String> cache = create(capacity);
                ExecutorService pool = Executors.newFixedThreadPool(threads);
                CountDownLatch start = new CountDownLatch(1);
                CountDownLatch done = new CountDownLatch(threads);
                AtomicReference<Throwable> failure = new AtomicReference<>();

                for (int t = 0; t < threads; t++) {
                    final int id = t;
                    pool.execute(() -> {
                        try {
                            start.await();
                            for (int i = 0; i < opsPerThread; i++) {
                                int key = (id * 31 + i) % 200;
                                if (i % 3 == 0) {
                                    cache.get(key);
                                } else {
                                    cache.put(key, "v" + key);
                                }
                            }
                        } catch (Throwable e) {
                            failure.compareAndSet(null, e);
                        } finally {
                            done.countDown();
                        }
                    });
                }
                start.countDown();
                assertTrue(done.await(50, TimeUnit.SECONDS), "스레드가 안 끝났다. 잠금이 안 풀렸을 수 있다");
                pool.shutdown();

                assertEquals(null, failure.get(), "작업 스레드가 예외를 던졌다: " + failure.get());

                // 잠금이 제 일을 했다면 이 셋이 전부 성립한다.
                List<Integer> order = cache.keysInOrder();
                assertTrue(cache.size() <= capacity, "용량을 넘었다: " + cache.size());
                assertEquals(cache.size(), order.size(), "맵 크기와 줄 길이가 어긋났다");
                Set<Integer> unique = new HashSet<>(order);
                assertEquals(order.size(), unique.size(), "같은 키가 줄에 두 번 들어 있다");
                for (Integer k : order) {
                    assertNotNull(cache.get(k), "줄에 있는데 맵에 없는 키가 있다: " + k);
                }
            }
        }

        @Test
        @Timeout(60)
        @DisplayName("예외가 나도 잠금은 풀린다")
        void lockIsReleasedOnException() throws Exception {
            Cache<Integer, String> c = create(2);
            for (int i = 0; i < 50; i++) {
                assertThrows(IllegalArgumentException.class, () -> c.put(null, "x"));
            }
            // try/finally 없이 잠갔다면 여기서 영원히 멈춘다.
            Thread other = new Thread(() -> c.put(1, "a"));
            other.start();
            other.join(5000);
            assertTrue(!other.isAlive(), "다른 스레드가 잠금을 못 얻었다. try/finally 가 빠졌다");
            assertEquals("a", c.get(1));
        }
    }
}
