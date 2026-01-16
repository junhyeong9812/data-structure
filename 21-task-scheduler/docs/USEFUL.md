# 작업 스케줄러 구현에 유용한 Java API

## 📦 java.util.concurrent

### DelayQueue
```java
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

// Delayed 인터페이스 구현 필요
class DelayedTask implements Delayed {
    private final long executeTime;
    private final Runnable task;
    
    public DelayedTask(Runnable task, long delayNanos) {
        this.task = task;
        this.executeTime = System.nanoTime() + delayNanos;
    }
    
    @Override
    public long getDelay(TimeUnit unit) {
        long remaining = executeTime - System.nanoTime();
        return unit.convert(remaining, TimeUnit.NANOSECONDS);
    }
    
    @Override
    public int compareTo(Delayed other) {
        return Long.compare(
            this.getDelay(TimeUnit.NANOSECONDS),
            other.getDelay(TimeUnit.NANOSECONDS)
        );
    }
}

// 사용
DelayQueue<DelayedTask> queue = new DelayQueue<>();
queue.offer(new DelayedTask(task, delay));

// 블로킹 take (지연 시간까지 대기)
DelayedTask task = queue.take();

// 논블로킹 poll
DelayedTask task = queue.poll();  // null if none ready
DelayedTask task = queue.poll(1, TimeUnit.SECONDS);  // 타임아웃
```

### PriorityBlockingQueue
```java
import java.util.concurrent.PriorityBlockingQueue;

// Comparable 구현 또는 Comparator 제공
PriorityBlockingQueue<Task> queue = new PriorityBlockingQueue<>();

// Comparator 사용
PriorityBlockingQueue<Task> queue = new PriorityBlockingQueue<>(
    11,  // 초기 용량
    Comparator.comparing(Task::getPriority).reversed()
);

queue.offer(task);
Task highest = queue.take();  // 블로킹
Task highest = queue.poll();  // 논블로킹
```

### ExecutorService
```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// 고정 스레드 풀
ExecutorService executor = Executors.newFixedThreadPool(4);

// 캐시드 스레드 풀 (필요시 생성)
ExecutorService executor = Executors.newCachedThreadPool();

// 단일 스레드
ExecutorService executor = Executors.newSingleThreadExecutor();

// Virtual Threads (Java 21)
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

// 작업 제출
executor.submit(() -> doWork());
Future<String> future = executor.submit(() -> compute());

// 종료
executor.shutdown();  // 현재 작업 완료 후 종료
executor.shutdownNow();  // 즉시 종료 시도
executor.awaitTermination(10, TimeUnit.SECONDS);
```

### ScheduledExecutorService
```java
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

// 지연 실행
ScheduledFuture<?> future = scheduler.schedule(
    () -> task(), 
    5, TimeUnit.SECONDS
);

// 고정 주기 (시작 시간 기준)
scheduler.scheduleAtFixedRate(
    () -> task(),
    0,              // 초기 지연
    10,             // 주기
    TimeUnit.SECONDS
);

// 고정 지연 (완료 시간 기준)
scheduler.scheduleWithFixedDelay(
    () -> task(),
    0,              // 초기 지연
    10,             // 지연
    TimeUnit.SECONDS
);

// 취소
future.cancel(false);  // 실행 중이면 완료 대기
future.cancel(true);   // 인터럽트 시도
```

---

## 📊 시간 관련

### java.time
```java
import java.time.*;

// 현재 시간
LocalDateTime now = LocalDateTime.now();
Instant instant = Instant.now();

// Duration
Duration delay = Duration.ofSeconds(5);
Duration.ofMinutes(10);
Duration.ofHours(1);
Duration.between(start, end);

// 시간 계산
LocalDateTime future = now.plusMinutes(30);
LocalDateTime past = now.minusHours(2);

// Duration 변환
long nanos = duration.toNanos();
long millis = duration.toMillis();
long seconds = duration.getSeconds();
```

### System 시간
```java
// 나노초 (경과 시간 측정용, 단조 증가)
long nanoTime = System.nanoTime();

// 밀리초 (epoch 기준, 절대 시간)
long currentMillis = System.currentTimeMillis();

// 경과 시간 측정
long start = System.nanoTime();
// ... 작업
long elapsed = System.nanoTime() - start;
```

### TimeUnit
```java
import java.util.concurrent.TimeUnit;

// 변환
long millis = TimeUnit.SECONDS.toMillis(5);
long nanos = TimeUnit.MILLISECONDS.toNanos(100);

// sleep
TimeUnit.SECONDS.sleep(1);

// 타임아웃 대기
boolean completed = latch.await(5, TimeUnit.SECONDS);
```

---

## 🔐 동시성

### AtomicLong
```java
import java.util.concurrent.atomic.AtomicLong;

AtomicLong counter = new AtomicLong(0);
long id = counter.incrementAndGet();
long id = counter.getAndIncrement();
```

### ConcurrentHashMap
```java
import java.util.concurrent.ConcurrentHashMap;

ConcurrentHashMap<String, Task> tasks = new ConcurrentHashMap<>();

tasks.put(id, task);
tasks.get(id);
tasks.remove(id);
tasks.computeIfAbsent(id, k -> new Task());
```

### volatile
```java
// 가시성 보장
private volatile boolean shutdown = false;

public void shutdown() {
    shutdown = true;
}

// 워커 루프
while (!shutdown) {
    // ...
}
```

### CountDownLatch
```java
import java.util.concurrent.CountDownLatch;

CountDownLatch latch = new CountDownLatch(3);

// 워커에서
latch.countDown();

// 메인에서 대기
latch.await();
latch.await(10, TimeUnit.SECONDS);
```

---

## 🧪 테스트

### AssertJ
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldExecuteImmediately() throws InterruptedException {
    AtomicBoolean executed = new AtomicBoolean(false);
    
    scheduler.submit(() -> executed.set(true));
    
    Thread.sleep(100);
    assertThat(executed.get()).isTrue();
}

@Test
void shouldExecuteAfterDelay() throws InterruptedException {
    AtomicBoolean executed = new AtomicBoolean(false);
    
    scheduler.schedule(() -> executed.set(true), Duration.ofMillis(500));
    
    Thread.sleep(100);
    assertThat(executed.get()).isFalse();
    
    Thread.sleep(500);
    assertThat(executed.get()).isTrue();
}

@Test
void shouldRespectPriority() throws InterruptedException {
    List<String> order = Collections.synchronizedList(new ArrayList<>());
    CountDownLatch latch = new CountDownLatch(3);
    
    scheduler.submit(() -> { order.add("LOW"); latch.countDown(); }, Priority.LOW);
    scheduler.submit(() -> { order.add("HIGH"); latch.countDown(); }, Priority.HIGH);
    scheduler.submit(() -> { order.add("NORMAL"); latch.countDown(); }, Priority.NORMAL);
    
    latch.await(1, TimeUnit.SECONDS);
    
    assertThat(order.get(0)).isEqualTo("HIGH");
}
```

### 시간 제어
```java
// Clock 주입으로 테스트 용이성 확보
public class TaskScheduler {
    private final Clock clock;
    
    public TaskScheduler(int poolSize, Clock clock) {
        this.clock = clock;
    }
    
    private long now() {
        return clock.instant().toEpochMilli();
    }
}

// 테스트
@Test
void testWithFixedClock() {
    Clock fixed = Clock.fixed(Instant.now(), ZoneId.systemDefault());
    TaskScheduler scheduler = new TaskScheduler(2, fixed);
    // ...
}
```

---

## 📚 Java 21 관련

### Record
```java
// 작업 정보
public record TaskInfo(
    String id,
    Instant scheduledTime,
    Priority priority,
    TaskStatus status
) {}

// 실행 결과
public record ExecutionResult(
    String taskId,
    boolean success,
    Duration executionTime,
    Exception error
) {}

// 우선순위
public enum Priority {
    LOW(0), NORMAL(5), HIGH(10), CRITICAL(20);
    
    private final int value;
    Priority(int value) { this.value = value; }
    public int getValue() { return value; }
}
```

### Virtual Threads
```java
// 가상 스레드로 스케줄러 구현
ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

// 많은 작업도 효율적으로 처리
for (int i = 0; i < 10000; i++) {
    executor.submit(() -> {
        Thread.sleep(1000);  // 블로킹 OK
        process();
    });
}
```

### Pattern Matching
```java
public void handleTask(Object task) {
    switch (task) {
        case ImmediateTask t -> executeNow(t);
        case DelayedTask t -> scheduleDelayed(t);
        case CronTask t -> scheduleCron(t);
        default -> throw new IllegalArgumentException();
    }
}
```

---

## ⚡ 성능 팁

### 1. 효율적인 대기
```java
// 바쁜 대기 피하기
while (!shutdown) {
    Task task = queue.poll();  // CPU 낭비!
}

// 블로킹 대기 사용
while (!shutdown) {
    Task task = queue.poll(100, TimeUnit.MILLISECONDS);
    if (task != null) {
        process(task);
    }
}
```

### 2. 작업 배치
```java
// 작은 작업 여러 개보다 배치로
public void submitBatch(List<Runnable> tasks) {
    executor.submit(() -> {
        for (Runnable task : tasks) {
            task.run();
        }
    });
}
```

### 3. 적절한 스레드 풀 크기
```java
// CPU 바운드 작업
int poolSize = Runtime.getRuntime().availableProcessors();

// I/O 바운드 작업
int poolSize = Runtime.getRuntime().availableProcessors() * 2;

// 또는 동적 조절
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    corePoolSize,
    maxPoolSize,
    60L, TimeUnit.SECONDS,
    new LinkedBlockingQueue<>()
);
```

---

## 🔀 Cron 유틸리티
```java
// 간단한 Cron 필드 파싱
public class CronUtils {
    
    public static Set<Integer> parseField(String field, int min, int max) {
        Set<Integer> values = new TreeSet<>();
        
        if ("*".equals(field)) {
            for (int i = min; i <= max; i++) values.add(i);
            return values;
        }
        
        if (field.startsWith("*/")) {
            int step = Integer.parseInt(field.substring(2));
            for (int i = min; i <= max; i += step) values.add(i);
            return values;
        }
        
        for (String part : field.split(",")) {
            if (part.contains("-")) {
                String[] range = part.split("-");
                int start = Integer.parseInt(range[0]);
                int end = Integer.parseInt(range[1]);
                for (int i = start; i <= end; i++) values.add(i);
            } else {
                values.add(Integer.parseInt(part));
            }
        }
        
        return values;
    }
}
```
