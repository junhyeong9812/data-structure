# 작업 스케줄러 풀이 해설

## 📌 핵심 아이디어

작업 스케줄러는 **우선순위 큐**와 **지연 큐**를 조합하여
다양한 스케줄링 요구사항을 처리합니다.

**핵심 구성요소**:
- DelayQueue: 시간 기반 스케줄링
- PriorityQueue: 우선순위 기반 실행
- ThreadPool: 병렬 작업 처리

---

## 🔑 핵심 개념

### 1. DelayQueue 활용
```java
// DelayQueue는 Delayed 인터페이스 구현 필요
public class ScheduledTask implements Delayed {
    private final long executeTimeNanos;
    
    @Override
    public long getDelay(TimeUnit unit) {
        long remaining = executeTimeNanos - System.nanoTime();
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
```

### 2. 워커 스레드
```java
private void startWorkers() {
    // 지연 큐 처리 스레드
    Thread delayProcessor = new Thread(() -> {
        while (!shutdown) {
            try {
                ScheduledTask task = delayQueue.take();
                if (!task.isCancelled()) {
                    executor.submit(task.getRunnable());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    });
    delayProcessor.setDaemon(true);
    delayProcessor.start();
}
```

### 3. 주기적 작업
```java
public void scheduleAtFixedRate(Runnable task, Duration period) {
    Runnable repeatingTask = new Runnable() {
        @Override
        public void run() {
            try {
                task.run();
            } finally {
                // 다음 실행 예약
                if (!shutdown) {
                    schedule(this, period);
                }
            }
        }
    };
    
    schedule(repeatingTask, Duration.ZERO);
}
```

---

## 📝 POP 구현 해설

### 완전한 구현
```java
public class TaskScheduler {
    private final DelayQueue<ScheduledTask> delayQueue = new DelayQueue<>();
    private final Map<String, ScheduledTask> taskRegistry = new ConcurrentHashMap<>();
    private final ExecutorService executor;
    private final AtomicLong taskIdGenerator = new AtomicLong(0);
    private volatile boolean shutdown = false;
    
    public TaskScheduler(int threadPoolSize) {
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
        startDelayProcessor();
    }
    
    private void startDelayProcessor() {
        Thread processor = new Thread(() -> {
            while (!shutdown) {
                try {
                    ScheduledTask task = delayQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (task != null && !task.isCancelled()) {
                        executor.submit(() -> {
                            try {
                                task.run();
                            } catch (Exception e) {
                                // 예외 처리/로깅
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "delay-processor");
        processor.setDaemon(true);
        processor.start();
    }
    
    // 즉시 실행
    public String submit(Runnable task) {
        return submit(task, Priority.NORMAL);
    }
    
    public String submit(Runnable task, Priority priority) {
        return schedule(task, Duration.ZERO, priority);
    }
    
    // 지연 실행
    public String schedule(Runnable task, Duration delay) {
        return schedule(task, delay, Priority.NORMAL);
    }
    
    public String schedule(Runnable task, Duration delay, Priority priority) {
        String taskId = generateTaskId();
        long executeTime = System.nanoTime() + delay.toNanos();
        
        ScheduledTask scheduledTask = new ScheduledTask(
            taskId, task, executeTime, priority
        );
        
        taskRegistry.put(taskId, scheduledTask);
        delayQueue.offer(scheduledTask);
        
        return taskId;
    }
    
    // 특정 시각 실행
    public String scheduleAt(Runnable task, LocalDateTime dateTime) {
        Duration delay = Duration.between(LocalDateTime.now(), dateTime);
        if (delay.isNegative()) {
            delay = Duration.ZERO;
        }
        return schedule(task, delay);
    }
    
    // 주기적 실행
    public String scheduleAtFixedRate(Runnable task, Duration initialDelay, 
                                       Duration period) {
        String taskId = generateTaskId();
        
        Runnable repeatingTask = new Runnable() {
            @Override
            public void run() {
                if (shutdown) return;
                
                try {
                    task.run();
                } finally {
                    // 다음 실행 예약
                    if (!shutdown && !taskRegistry.get(taskId).isCancelled()) {
                        long nextExecuteTime = System.nanoTime() + period.toNanos();
                        ScheduledTask next = new ScheduledTask(
                            taskId, this, nextExecuteTime, Priority.NORMAL
                        );
                        taskRegistry.put(taskId, next);
                        delayQueue.offer(next);
                    }
                }
            }
        };
        
        long executeTime = System.nanoTime() + initialDelay.toNanos();
        ScheduledTask scheduledTask = new ScheduledTask(
            taskId, repeatingTask, executeTime, Priority.NORMAL
        );
        
        taskRegistry.put(taskId, scheduledTask);
        delayQueue.offer(scheduledTask);
        
        return taskId;
    }
    
    // 작업 취소
    public boolean cancel(String taskId) {
        ScheduledTask task = taskRegistry.get(taskId);
        if (task != null) {
            task.cancel();
            return true;
        }
        return false;
    }
    
    // 종료
    public void shutdown() {
        shutdown = true;
        executor.shutdown();
    }
    
    public void shutdownNow() {
        shutdown = true;
        executor.shutdownNow();
    }
    
    private String generateTaskId() {
        return "task-" + taskIdGenerator.incrementAndGet();
    }
}
```

### Cron 표현식 파서
```java
public class CronExpression {
    private final Set<Integer> minutes;
    private final Set<Integer> hours;
    private final Set<Integer> daysOfMonth;
    private final Set<Integer> months;
    private final Set<Integer> daysOfWeek;
    
    private CronExpression(Set<Integer> minutes, Set<Integer> hours,
                           Set<Integer> daysOfMonth, Set<Integer> months,
                           Set<Integer> daysOfWeek) {
        this.minutes = minutes;
        this.hours = hours;
        this.daysOfMonth = daysOfMonth;
        this.months = months;
        this.daysOfWeek = daysOfWeek;
    }
    
    public static CronExpression parse(String expression) {
        String[] parts = expression.trim().split("\\s+");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid cron: " + expression);
        }
        
        return new CronExpression(
            parseField(parts[0], 0, 59),   // 분
            parseField(parts[1], 0, 23),   // 시
            parseField(parts[2], 1, 31),   // 일
            parseField(parts[3], 1, 12),   // 월
            parseField(parts[4], 0, 6)     // 요일
        );
    }
    
    private static Set<Integer> parseField(String field, int min, int max) {
        Set<Integer> values = new TreeSet<>();
        
        if (field.equals("*")) {
            for (int i = min; i <= max; i++) {
                values.add(i);
            }
            return values;
        }
        
        // */n 형식 (매 n마다)
        if (field.startsWith("*/")) {
            int step = Integer.parseInt(field.substring(2));
            for (int i = min; i <= max; i += step) {
                values.add(i);
            }
            return values;
        }
        
        // 쉼표로 구분된 값들
        for (String part : field.split(",")) {
            if (part.contains("-")) {
                // 범위 (예: 1-5)
                String[] range = part.split("-");
                int start = Integer.parseInt(range[0]);
                int end = Integer.parseInt(range[1]);
                for (int i = start; i <= end; i++) {
                    values.add(i);
                }
            } else {
                values.add(Integer.parseInt(part));
            }
        }
        
        return values;
    }
    
    public LocalDateTime nextExecutionTime(LocalDateTime from) {
        LocalDateTime next = from.plusMinutes(1).withSecond(0).withNano(0);
        
        // 최대 4년까지 탐색
        LocalDateTime limit = from.plusYears(4);
        
        while (next.isBefore(limit)) {
            if (matches(next)) {
                return next;
            }
            next = next.plusMinutes(1);
        }
        
        throw new IllegalStateException("No matching time found");
    }
    
    private boolean matches(LocalDateTime dt) {
        return minutes.contains(dt.getMinute())
            && hours.contains(dt.getHour())
            && daysOfMonth.contains(dt.getDayOfMonth())
            && months.contains(dt.getMonthValue())
            && daysOfWeek.contains(dt.getDayOfWeek().getValue() % 7);
    }
}
```

### Cron 스케줄링
```java
public String scheduleCron(Runnable task, String cronExpression) {
    CronExpression cron = CronExpression.parse(cronExpression);
    String taskId = generateTaskId();
    
    Runnable cronTask = new Runnable() {
        @Override
        public void run() {
            if (shutdown) return;
            
            try {
                task.run();
            } finally {
                // 다음 실행 시간 계산 및 예약
                if (!shutdown && !taskRegistry.get(taskId).isCancelled()) {
                    LocalDateTime nextTime = cron.nextExecutionTime(LocalDateTime.now());
                    Duration delay = Duration.between(LocalDateTime.now(), nextTime);
                    
                    long executeTime = System.nanoTime() + delay.toNanos();
                    ScheduledTask next = new ScheduledTask(
                        taskId, this, executeTime, Priority.NORMAL
                    );
                    taskRegistry.put(taskId, next);
                    delayQueue.offer(next);
                }
            }
        }
    };
    
    // 첫 실행 예약
    LocalDateTime firstRun = cron.nextExecutionTime(LocalDateTime.now());
    Duration initialDelay = Duration.between(LocalDateTime.now(), firstRun);
    
    long executeTime = System.nanoTime() + initialDelay.toNanos();
    ScheduledTask scheduledTask = new ScheduledTask(
        taskId, cronTask, executeTime, Priority.NORMAL
    );
    
    taskRegistry.put(taskId, scheduledTask);
    delayQueue.offer(scheduledTask);
    
    return taskId;
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 |
|------|-----------|
| submit | O(log n) |
| schedule | O(log n) |
| cancel | O(1) |
| 작업 추출 | O(log n) |

n = 대기 중인 작업 수

---

## ❌ 흔한 실수

### 1. 시간 단위 혼동
```java
// 잘못됨: 밀리초와 나노초 혼동
long executeTime = System.currentTimeMillis() + delay.toMillis();
// System.nanoTime()과 혼용하면 안 됨!

// 올바름: 일관된 단위 사용
long executeTime = System.nanoTime() + delay.toNanos();
```

### 2. 주기적 작업의 드리프트
```java
// 잘못됨: 실행 완료 시점 기준
long nextTime = System.nanoTime() + period.toNanos();

// 올바름 (Fixed Rate): 예정 시간 기준
long nextTime = lastScheduledTime + period.toNanos();
```

### 3. 취소된 작업 처리
```java
// 잘못됨: 취소 확인 없이 실행
task.run();

// 올바름: 취소 확인
if (!task.isCancelled()) {
    task.run();
}
```

---

## 🔗 관련 문제

- LeetCode 621: Task Scheduler
- 운영체제 프로세스 스케줄링
- 분산 작업 큐 (Celery, Sidekiq)
