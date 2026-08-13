# oop/TaskScheduler.java

OOP 인터페이스 + Cron 지원 구현.

```java
package com.datastructure.taskscheduler.oop;

import java.time.Duration;
import java.time.Instant;

public interface TaskScheduler {
    enum Priority { LOW, NORMAL, HIGH, CRITICAL }

    Task submit(Runnable r);
    Task submit(Runnable r, Priority p);
    Task schedule(Runnable r, Duration delay);
    Task scheduleAt(Runnable r, Instant when);
    Task scheduleAtFixedRate(Runnable r, Duration period);
    Task scheduleAtFixedRate(Runnable r, Duration initialDelay, Duration period);
    Task scheduleCron(Runnable r, String cronExpression);
    boolean cancel(Task task);
    void shutdown();

    interface Task {
        String getId();
        boolean isCancelled();
    }
}
```

---

# oop/StandardTaskScheduler.java

```java
package com.datastructure.taskscheduler.oop;

import com.datastructure.taskscheduler.pop.CronExpression;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class StandardTaskScheduler implements TaskScheduler {

    static class ScheduledTask implements Delayed, Task {
        final String id;
        final Runnable runnable;
        long executeNanos;
        final Priority priority;
        final long fixedRateNanos;
        final CronExpression cron;
        final AtomicBoolean cancelled = new AtomicBoolean();

        ScheduledTask(String id, Runnable r, long executeNanos, Priority p,
                      long fixedRateNanos, CronExpression cron) {
            this.id = id;
            this.runnable = r;
            this.executeNanos = executeNanos;
            this.priority = p;
            this.fixedRateNanos = fixedRateNanos;
            this.cron = cron;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(executeNanos - System.nanoTime(), TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            ScheduledTask other = (ScheduledTask) o;
            int c = Long.compare(executeNanos, other.executeNanos);
            return c != 0 ? c : Integer.compare(other.priority.ordinal(), priority.ordinal());
        }

        @Override public String getId() { return id; }
        @Override public boolean isCancelled() { return cancelled.get(); }
    }

    private final DelayQueue<ScheduledTask> queue = new DelayQueue<>();
    private final ExecutorService workers;
    private final Thread dispatcher;
    private volatile boolean running = true;

    public StandardTaskScheduler(int threadPoolSize) {
        this.workers = Executors.newFixedThreadPool(threadPoolSize);
        this.dispatcher = new Thread(this::loop, "scheduler-dispatcher");
        this.dispatcher.setDaemon(true);
        this.dispatcher.start();
    }

    private void loop() {
        while (running) {
            try {
                ScheduledTask t = queue.take();
                if (t.isCancelled()) continue;
                workers.submit(() -> runAndReschedule(t));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void runAndReschedule(ScheduledTask t) {
        try { t.runnable.run(); } catch (Throwable ignored) {}
        if (t.isCancelled() || !running) return;
        if (t.fixedRateNanos > 0) {
            t.executeNanos = System.nanoTime() + t.fixedRateNanos;
            queue.offer(t);
        } else if (t.cron != null) {
            LocalDateTime next = t.cron.nextExecutionTime(LocalDateTime.now());
            long delay = Duration.between(Instant.now(),
                    next.atZone(ZoneId.systemDefault()).toInstant()).toNanos();
            t.executeNanos = System.nanoTime() + Math.max(0, delay);
            queue.offer(t);
        }
    }

    @Override
    public Task submit(Runnable r) {
        return submit(r, Priority.NORMAL);
    }

    @Override
    public Task submit(Runnable r, Priority p) {
        return enqueue(new ScheduledTask(newId(), r, System.nanoTime(), p, 0L, null));
    }

    @Override
    public Task schedule(Runnable r, Duration delay) {
        return enqueue(new ScheduledTask(newId(), r,
                System.nanoTime() + delay.toNanos(), Priority.NORMAL, 0L, null));
    }

    @Override
    public Task scheduleAt(Runnable r, Instant when) {
        long delay = Math.max(0, Duration.between(Instant.now(), when).toNanos());
        return enqueue(new ScheduledTask(newId(), r,
                System.nanoTime() + delay, Priority.NORMAL, 0L, null));
    }

    @Override
    public Task scheduleAtFixedRate(Runnable r, Duration period) {
        return scheduleAtFixedRate(r, Duration.ZERO, period);
    }

    @Override
    public Task scheduleAtFixedRate(Runnable r, Duration initialDelay, Duration period) {
        return enqueue(new ScheduledTask(newId(), r,
                System.nanoTime() + initialDelay.toNanos(),
                Priority.NORMAL, period.toNanos(), null));
    }

    @Override
    public Task scheduleCron(Runnable r, String expr) {
        CronExpression cron = CronExpression.parse(expr);
        LocalDateTime next = cron.nextExecutionTime(LocalDateTime.now());
        long delay = Duration.between(Instant.now(),
                next.atZone(ZoneId.systemDefault()).toInstant()).toNanos();
        return enqueue(new ScheduledTask(newId(), r,
                System.nanoTime() + Math.max(0, delay),
                Priority.NORMAL, 0L, cron));
    }

    @Override
    public boolean cancel(Task t) {
        if (!(t instanceof ScheduledTask)) return false;
        ScheduledTask st = (ScheduledTask) t;
        boolean ok = st.cancelled.compareAndSet(false, true);
        queue.remove(st);
        return ok;
    }

    @Override
    public void shutdown() {
        running = false;
        dispatcher.interrupt();
        workers.shutdown();
    }

    private Task enqueue(ScheduledTask st) {
        queue.offer(st);
        return st;
    }

    private String newId() {
        return UUID.randomUUID().toString();
    }
}
```
