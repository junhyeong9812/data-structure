package com.datastructure.persistent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * 불변이라 잠금이 필요 없다는 것을 실제로 돌려서 본다.
 *
 * 단언은 결정적이다. 스레드가 어떤 순서로 끼어들든 답이 하나뿐이라는 것이 요점이므로
 * 시간이나 순서를 재지 않고 "틀린 답을 본 횟수가 0" 만 센다.
 */
@DisplayName("잠금 없는 동시 읽기")
class LockFreeReadTest {

    private static final int KEYS = 1023;
    private static final int READERS = 8;
    private static final int ROUNDS = 20;

    @Test
    @Timeout(30)
    @DisplayName("여러 스레드가 같은 버전을 동시에 읽는다. 동기화가 한 줄도 없다")
    void manyReadersOneVersion() throws Exception {
        PersistentTreeMap<Integer, String> shared = TestTrees.balanced(KEYS);

        ExecutorService pool = Executors.newFixedThreadPool(READERS);
        try {
            List<Callable<Integer>> readers = new ArrayList<>();
            for (int r = 0; r < READERS; r++) {
                readers.add(() -> {
                    int wrong = 0;
                    for (int round = 0; round < ROUNDS; round++) {
                        for (int i = 0; i < KEYS; i++) {
                            if (!("v" + (2 * i)).equals(shared.get(2 * i))) {
                                wrong++;
                            }
                        }
                        if (shared.size() != KEYS || shared.height() != 10) {
                            wrong++;
                        }
                    }
                    return wrong;
                });
            }
            int wrong = 0;
            for (Future<Integer> f : pool.invokeAll(readers)) {
                wrong += f.get();
            }
            assertEquals(0, wrong, "동시에 읽었더니 답이 달라졌다");
        } finally {
            pool.shutdown();
        }

        assertEquals(KEYS, shared.size());
        // 23번 스플레이 트리에서는 이 테스트를 쓸 수 없다. get 이 트리를 접기 때문이다.
        // 10번 LRU 캐시도, 14번 유니온파인드의 find 도 마찬가지였다.
        // 조회가 쓰기가 아니면 경쟁 자체가 성립하지 않는다.
    }

    @Test
    @Timeout(30)
    @DisplayName("쓰는 스레드가 옆에서 새 버전을 만들어도 읽는 쪽의 답은 안 흔들린다")
    void writerCannotDisturbReaders() throws Exception {
        PersistentTreeMap<Integer, String> original = TestTrees.balanced(KEYS);
        AtomicReference<PersistentTreeMap<Integer, String>> latest = new AtomicReference<>(original);

        ExecutorService pool = Executors.newFixedThreadPool(READERS + 1);
        try {
            List<Callable<Integer>> tasks = new ArrayList<>();

            // 쓰는 쪽. 새 버전을 2000개 만든다. 옛 버전의 노드는 건드리지 않는다.
            // 기존 키보다 큰 키만 넣으므로 트리의 왼쪽 절반은 계속 공유된다.
            tasks.add(() -> {
                for (int i = 0; i < 2000; i++) {
                    latest.set(latest.get().put(10_000 + i, "새것"));
                }
                return 0;
            });

            // 읽는 쪽. 처음 버전만 본다. 잠금이 없다.
            for (int r = 0; r < READERS; r++) {
                tasks.add(() -> {
                    int wrong = 0;
                    for (int round = 0; round < ROUNDS; round++) {
                        for (int i = 0; i < KEYS; i++) {
                            if (!("v" + (2 * i)).equals(original.get(2 * i))) {
                                wrong++;
                            }
                            if (original.get(2 * i + 1) != null) {
                                wrong++;      // 쓰는 쪽의 새 키가 옛 버전에 새어 들어왔다
                            }
                        }
                        if (original.size() != KEYS) {
                            wrong++;
                        }
                    }
                    return wrong;
                });
            }

            int wrong = 0;
            for (Future<Integer> f : pool.invokeAll(tasks)) {
                wrong += f.get();
            }
            assertEquals(0, wrong, "쓰는 쪽이 옛 버전을 흔들었다");
        } finally {
            pool.shutdown();
        }

        assertEquals(KEYS, original.size(), "옛 버전의 크기가 바뀌었다");
        assertEquals(KEYS + 2000, latest.get().size());
        assertTrue(PersistentProblems.countSharedNodes(original, latest.get()) > 0,
                "2000번 쓰고도 첫 버전과 노드를 나눠 쓴다");
    }
}
