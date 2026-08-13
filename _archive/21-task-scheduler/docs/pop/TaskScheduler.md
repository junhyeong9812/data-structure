# pop/TaskScheduler.java

DelayQueue + 워커 스레드풀 기반 작업 스케줄러. submit/schedule/scheduleAt/scheduleAtFixedRate/cancel.

```java
package com.datastructure.taskscheduler.pop;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TaskScheduler {

    public enum Priority { LOW, NORMAL, HIGH, CRITICAL }

    public static class ScheduledTask implements Delayed {
        final String id;
        final Runnable task;
        long executeTimeNanos;
        final Priority priority;
        final long periodNanos; // 0 = 1회성
        final AtomicBoolean cancelled = new AtomicBoolean();

        ScheduledTask(String id, Runnable task, long executeTimeNanos,
                      Priority priority, long periodNanos) {
            this.id = id;
            this.task = task;
            this.executeTimeNanos = executeTimeNanos;
            this.priority = priority;
            this.periodNanos = periodNanos;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(executeTimeNanos - System.nanoTime(), TimeUnit.NANOSECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            ScheduledTask other = (ScheduledTask) o;
            int c = Long.compare(this.executeTimeNanos, other.executeTimeNanos);
            if (c != 0) return c;
            return Integer.compare(other.priority.ordinal(), this.priority.ordinal());
        }

        public String getId() { return id; }
        public boolean cancel() { return cancelled.compareAndSet(false, true); }
        public boolean isCancelled() { return cancelled.get(); }
    }

    private final DelayQueue<ScheduledTask> queue = new DelayQueue<>();
    private final ExecutorService workers;
    private final Thread dispatcher;
    private volatile boolean running = true;

    public TaskScheduler(int threadPoolSize) {
        this.workers = Executors.newFixedThreadPool(threadPoolSize);
        this.dispatcher = new Thread(this::dispatchLoop, "task-scheduler-dispatcher");
        this.dispatcher.setDaemon(true);
        this.dispatcher.start();
    }

    private void dispatchLoop() {
        while (running) {
            try {
                ScheduledTask t = queue.take();
                if (t.isCancelled()) continue;
                workers.submit(() -> runTask(t));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void runTask(ScheduledTask t) {
        try {
            t.task.run();
        } catch (Throwable ex) {
            // 로깅 자리
        }
        if (t.periodNanos > 0 && !t.isCancelled() && running) {
            t.executeTimeNanos = System.nanoTime() + t.periodNanos;
            queue.offer(t);
        }
    }

    public ScheduledTask submit(Runnable r) {
        return submit(r, Priority.NORMAL);
    }

    public ScheduledTask submit(Runnable r, Priority p) {
        ScheduledTask st = newTask(r, 0L, p, 0L);
        queue.offer(st);
        return st;
    }

    public ScheduledTask schedule(Runnable r, Duration delay) {
        return schedule(r, delay, Priority.NORMAL);
    }

    public ScheduledTask schedule(Runnable r, Duration delay, Priority p) {
        ScheduledTask st = newTask(r, delay.toNanos(), p, 0L);
        queue.offer(st);
        return st;
    }

    public ScheduledTask scheduleAt(Runnable r, Instant when) {
        long delayNanos = Duration.between(Instant.now(), when).toNanos();
        ScheduledTask st = newTask(r, Math.max(0, delayNanos), Priority.NORMAL, 0L);
        queue.offer(st);
        return st;
    }

    public ScheduledTask scheduleAtFixedRate(Runnable r, Duration period) {
        return scheduleAtFixedRate(r, Duration.ZERO, period);
    }

    public ScheduledTask scheduleAtFixedRate(Runnable r, Duration initialDelay, Duration period) {
        ScheduledTask st = newTask(r, initialDelay.toNanos(), Priority.NORMAL, period.toNanos());
        queue.offer(st);
        return st;
    }

    public boolean cancel(ScheduledTask task) {
        boolean ok = task.cancel();
        queue.remove(task);
        return ok;
    }

    public void shutdown() {
        running = false;
        dispatcher.interrupt();
        workers.shutdown();
    }

    private ScheduledTask newTask(Runnable r, long delayNanos, Priority p, long periodNanos) {
        long execute = System.nanoTime() + delayNanos;
        return new ScheduledTask(UUID.randomUUID().toString(), r, execute, p, periodNanos);
    }
}
```
