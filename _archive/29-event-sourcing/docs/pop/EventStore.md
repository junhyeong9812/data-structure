# pop/EventStore.java

이벤트 저장소 + 스냅샷 + 구독자. 단일 파일 구현 (계좌 도메인 예시 포함).

```java
package com.datastructure.eventsourcing.pop;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class EventStore {

    public interface Event {
        String getAggregateId();
        Instant getTimestamp();
        int getVersion();
    }

    public static class Snapshot {
        public final String aggregateId;
        public final Object state;
        public final int version;
        public final Instant createdAt;

        public Snapshot(String aggregateId, Object state, int version) {
            this.aggregateId = aggregateId;
            this.state = state;
            this.version = version;
            this.createdAt = Instant.now();
        }
    }

    private final Map<String, List<Event>> streams = new ConcurrentHashMap<>();
    private final Map<String, Snapshot> snapshots = new ConcurrentHashMap<>();
    private final AtomicLong globalSeq = new AtomicLong();
    private final List<Consumer<Event>> listeners = new CopyOnWriteArrayList<>();

    public synchronized void append(String aggregateId, Event event) {
        streams.computeIfAbsent(aggregateId, k -> new ArrayList<>()).add(event);
        globalSeq.incrementAndGet();
        for (Consumer<Event> l : listeners) l.accept(event);
    }

    public List<Event> getEvents(String aggregateId) {
        return Collections.unmodifiableList(
                streams.getOrDefault(aggregateId, Collections.emptyList()));
    }

    public List<Event> getEventsAfter(String aggregateId, int afterVersion) {
        List<Event> all = getEvents(aggregateId);
        List<Event> result = new ArrayList<>();
        for (Event e : all) if (e.getVersion() > afterVersion) result.add(e);
        return result;
    }

    public List<Event> getEventsUpTo(String aggregateId, int version) {
        List<Event> all = getEvents(aggregateId);
        List<Event> result = new ArrayList<>();
        for (Event e : all) if (e.getVersion() <= version) result.add(e);
        return result;
    }

    public void saveSnapshot(String aggregateId, Object state, int version) {
        snapshots.put(aggregateId, new Snapshot(aggregateId, state, version));
    }

    public Snapshot getSnapshot(String aggregateId) {
        return snapshots.get(aggregateId);
    }

    public void subscribe(Consumer<Event> listener) {
        listeners.add(listener);
    }

    public int getCurrentVersion(String aggregateId) {
        List<Event> events = streams.get(aggregateId);
        if (events == null || events.isEmpty()) return 0;
        return events.get(events.size() - 1).getVersion();
    }

    public long globalSequence() {
        return globalSeq.get();
    }
}
```
