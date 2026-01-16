# 21. 작업 스케줄러 (Task Scheduler)

## 📋 문제 정의

**우선순위 기반 작업 실행, 지연 실행, 주기적 실행**을 지원하는 
작업 스케줄러를 구현하세요.

작업 스케줄러는 운영체제, 백그라운드 작업 처리, 크론 작업 등에서
핵심적인 역할을 합니다.

---

## 🎯 학습 목표

- 우선순위 큐를 활용한 작업 스케줄링
- 지연 실행(Delayed Execution) 구현
- Cron 표현식 파싱
- 스레드 풀과 작업 큐
- 작업 의존성 관리

---

## 📝 요구사항

### 작업 유형

| 유형 | 설명 |
|------|------|
| **Immediate** | 즉시 실행 |
| **Delayed** | 지정 시간 후 실행 |
| **Scheduled** | 특정 시각에 실행 |
| **Periodic** | 주기적으로 반복 실행 |
| **Cron** | Cron 표현식 기반 실행 |

### 기본 연산

| 메서드 | 설명 |
|--------|------|
| `submit(task)` | 즉시 실행 작업 제출 |
| `schedule(task, delay)` | 지연 실행 작업 예약 |
| `scheduleAt(task, time)` | 특정 시각 실행 예약 |
| `scheduleAtFixedRate(task, period)` | 고정 주기 반복 |
| `scheduleCron(task, cronExpr)` | Cron 스케줄 |
| `cancel(taskId)` | 작업 취소 |

### 설정 옵션

| 설정 | 설명 | 기본값 |
|------|------|--------|
| `threadPoolSize` | 워커 스레드 수 | CPU 코어 수 |
| `queueCapacity` | 대기 큐 크기 | 무제한 |
| `defaultPriority` | 기본 우선순위 | NORMAL |

---

## 📊 입출력 예시

### 예제 1: 기본 사용
```java
TaskScheduler scheduler = new TaskScheduler(4);  // 4 스레드

// 즉시 실행
scheduler.submit(() -> System.out.println("Hello!"));

// 5초 후 실행
scheduler.schedule(() -> System.out.println("Delayed!"), 
    Duration.ofSeconds(5));

// 특정 시각에 실행
scheduler.scheduleAt(() -> System.out.println("Scheduled!"), 
    LocalDateTime.of(2024, 12, 25, 9, 0));

// 매 10초마다 실행
scheduler.scheduleAtFixedRate(() -> System.out.println("Periodic!"), 
    Duration.ofSeconds(10));
```

### 예제 2: 우선순위
```java
// 우선순위 지정
scheduler.submit(() -> lowPriorityWork(), Priority.LOW);
scheduler.submit(() -> highPriorityWork(), Priority.HIGH);
scheduler.submit(() -> criticalWork(), Priority.CRITICAL);

// 높은 우선순위 작업이 먼저 실행됨
```

### 예제 3: Cron 표현식
```java
// 매일 오전 9시
scheduler.scheduleCron(() -> dailyReport(), "0 9 * * *");

// 매주 월요일 오전 10시
scheduler.scheduleCron(() -> weeklyMeeting(), "0 10 * * 1");

// 매월 1일 자정
scheduler.scheduleCron(() -> monthlyBilling(), "0 0 1 * *");

// 매 5분마다
scheduler.scheduleCron(() -> healthCheck(), "*/5 * * * *");
```

### 예제 4: 작업 의존성
```java
Task taskA = scheduler.submit(() -> fetchData());
Task taskB = scheduler.submit(() -> processData()).dependsOn(taskA);
Task taskC = scheduler.submit(() -> saveResult()).dependsOn(taskB);

// A 완료 → B 실행 → C 실행
```

---

## 🔍 핵심 개념

### 스케줄링 큐 구조
```
┌─────────────────────────────────────────┐
│            Task Scheduler                │
├─────────────────────────────────────────┤
│                                          │
│  ┌──────────────┐   ┌──────────────┐    │
│  │ Delay Queue  │   │ Priority Q   │    │
│  │ (시간 기반)   │   │ (우선순위)    │    │
│  └──────┬───────┘   └──────┬───────┘    │
│         │                   │            │
│         └───────┬───────────┘            │
│                 ▼                        │
│         ┌──────────────┐                 │
│         │  Work Queue  │                 │
│         └──────┬───────┘                 │
│                │                         │
│    ┌───────────┼───────────┐             │
│    ▼           ▼           ▼             │
│ ┌──────┐  ┌──────┐    ┌──────┐          │
│ │Worker│  │Worker│ .. │Worker│          │
│ │  1   │  │  2   │    │  N   │          │
│ └──────┘  └──────┘    └──────┘          │
│                                          │
└─────────────────────────────────────────┘
```

### Cron 표현식
```
┌───────────── 분 (0-59)
│ ┌───────────── 시 (0-23)
│ │ ┌───────────── 일 (1-31)
│ │ │ ┌───────────── 월 (1-12)
│ │ │ │ ┌───────────── 요일 (0-6, 0=일요일)
│ │ │ │ │
* * * * *

예:
0 9 * * *     → 매일 09:00
*/15 * * * *  → 매 15분
0 0 1 * *     → 매월 1일 00:00
0 10 * * 1-5  → 평일 10:00
```

---

## 💡 힌트

### 기본 구조
```java
public class TaskScheduler {
    private final PriorityBlockingQueue<ScheduledTask> taskQueue;
    private final DelayQueue<ScheduledTask> delayQueue;
    private final ExecutorService executor;
    private final ScheduledExecutorService timerService;
    
    public TaskScheduler(int threadPoolSize) {
        this.taskQueue = new PriorityBlockingQueue<>();
        this.delayQueue = new DelayQueue<>();
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
        this.timerService = Executors.newScheduledThreadPool(1);
        startWorkers();
    }
}
```

### ScheduledTask
```java
public class ScheduledTask implements Delayed, Comparable<ScheduledTask> {
    private final String id;
    private final Runnable task;
    private final long executeTime;  // nano timestamp
    private final Priority priority;
    private volatile boolean cancelled;
    
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(
            executeTime - System.nanoTime(), 
            TimeUnit.NANOSECONDS
        );
    }
    
    @Override
    public int compareTo(ScheduledTask other) {
        int timeCompare = Long.compare(this.executeTime, other.executeTime);
        if (timeCompare != 0) return timeCompare;
        return Integer.compare(
            other.priority.ordinal(), 
            this.priority.ordinal()
        );
    }
}
```

### Cron 파서
```java
public class CronExpression {
    private final Set<Integer> minutes;
    private final Set<Integer> hours;
    private final Set<Integer> daysOfMonth;
    private final Set<Integer> months;
    private final Set<Integer> daysOfWeek;
    
    public static CronExpression parse(String expression) {
        String[] parts = expression.split("\\s+");
        // 각 필드 파싱
    }
    
    public LocalDateTime nextExecutionTime(LocalDateTime from) {
        // 다음 실행 시간 계산
    }
}
```

---

## ✅ 체크리스트

- [ ] 즉시 실행 (submit)
- [ ] 지연 실행 (schedule)
- [ ] 특정 시각 실행 (scheduleAt)
- [ ] 주기적 실행 (scheduleAtFixedRate)
- [ ] Cron 표현식 지원
- [ ] 우선순위 지원
- [ ] 작업 취소
- [ ] 작업 의존성 (선택)
- [ ] 재시도 정책 (선택)

---

## 📚 참고

- Java ScheduledExecutorService
- Quartz Scheduler
- Spring @Scheduled
- Linux cron
