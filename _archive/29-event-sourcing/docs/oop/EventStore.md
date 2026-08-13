# oop/EventStore.java

OOP 인터페이스 + Projection 추상화. 제네릭 상태 타입.

```java
package com.datastructure.eventsourcing.oop;

import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;

public interface EventStore {

    interface Event {
        String getAggregateId();
        Instant getTimestamp();
        int getVersion();
    }

    interface Projection<S> {
        S init();
        S apply(S state, Event event);
        default S project(List<Event> events) {
            S state = init();
            for (Event e : events) state = apply(state, e);
            return state;
        }
    }

    void append(String aggregateId, Event event);
    List<Event> getEvents(String aggregateId);
    List<Event> getEventsAfter(String aggregateId, int afterVersion);
    List<Event> getEventsUpTo(String aggregateId, int version);

    void saveSnapshot(String aggregateId, Object state, int version);
    Object getSnapshotState(String aggregateId);
    int getSnapshotVersion(String aggregateId);

    void subscribe(Consumer<Event> listener);
    int getCurrentVersion(String aggregateId);

    /** 스냅샷 + 이후 이벤트로 복원 */
    <S> S restore(String aggregateId, Projection<S> projection);
}
```

---

# oop/InMemoryEventStore.java

```java
package com.datastructure.eventsourcing.oop;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class InMemoryEventStore implements EventStore {

    private static class Snapshot {
        final Object state;
        final int version;
        Snapshot(Object state, int version) {
            this.state = state;
            this.version = version;
        }
    }

    private final Map<String, List<Event>> streams = new ConcurrentHashMap<>();
    private final Map<String, Snapshot> snapshots = new ConcurrentHashMap<>();
    private final List<Consumer<Event>> listeners = new CopyOnWriteArrayList<>();

    @Override
    public synchronized void append(String aggregateId, Event event) {
        streams.computeIfAbsent(aggregateId, k -> new ArrayList<>()).add(event);
        for (Consumer<Event> l : listeners) l.accept(event);
    }

    @Override
    public List<Event> getEvents(String aggregateId) {
        return Collections.unmodifiableList(streams.getOrDefault(aggregateId, List.of()));
    }

    @Override
    public List<Event> getEventsAfter(String aggregateId, int afterVersion) {
        List<Event> out = new ArrayList<>();
        for (Event e : getEvents(aggregateId)) {
            if (e.getVersion() > afterVersion) out.add(e);
        }
        return out;
    }

    @Override
    public List<Event> getEventsUpTo(String aggregateId, int version) {
        List<Event> out = new ArrayList<>();
        for (Event e : getEvents(aggregateId)) {
            if (e.getVersion() <= version) out.add(e);
        }
        return out;
    }

    @Override
    public void saveSnapshot(String aggregateId, Object state, int version) {
        snapshots.put(aggregateId, new Snapshot(state, version));
    }

    @Override
    public Object getSnapshotState(String aggregateId) {
        Snapshot s = snapshots.get(aggregateId);
        return s == null ? null : s.state;
    }

    @Override
    public int getSnapshotVersion(String aggregateId) {
        Snapshot s = snapshots.get(aggregateId);
        return s == null ? 0 : s.version;
    }

    @Override
    public void subscribe(Consumer<Event> listener) {
        listeners.add(listener);
    }

    @Override
    public int getCurrentVersion(String aggregateId) {
        List<Event> events = streams.get(aggregateId);
        if (events == null || events.isEmpty()) return 0;
        return events.get(events.size() - 1).getVersion();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S> S restore(String aggregateId, Projection<S> projection) {
        Snapshot s = snapshots.get(aggregateId);
        if (s == null) {
            return projection.project(getEvents(aggregateId));
        }
        S state = (S) s.state;
        for (Event e : getEventsAfter(aggregateId, s.version)) {
            state = projection.apply(state, e);
        }
        return state;
    }
}
```
