# pop/AccountExample.java

EventStore를 사용한 은행 계좌 도메인 예시 (이벤트 + 프로젝션).

```java
package com.datastructure.eventsourcing.pop;

import java.time.Instant;
import java.util.List;

public class AccountExample {

    // ── 이벤트 ─────────────────────────────────────────
    public static class AccountCreated implements EventStore.Event {
        public final String aggregateId;
        public final String ownerName;
        public final Instant timestamp;
        public final int version;

        public AccountCreated(String id, String owner, int version) {
            this.aggregateId = id;
            this.ownerName = owner;
            this.timestamp = Instant.now();
            this.version = version;
        }
        @Override public String getAggregateId() { return aggregateId; }
        @Override public Instant getTimestamp() { return timestamp; }
        @Override public int getVersion() { return version; }
    }

    public static class MoneyDeposited implements EventStore.Event {
        public final String aggregateId;
        public final long amount;
        public final Instant timestamp;
        public final int version;
        public MoneyDeposited(String id, long amount, int version) {
            this.aggregateId = id;
            this.amount = amount;
            this.timestamp = Instant.now();
            this.version = version;
        }
        @Override public String getAggregateId() { return aggregateId; }
        @Override public Instant getTimestamp() { return timestamp; }
        @Override public int getVersion() { return version; }
    }

    public static class MoneyWithdrawn implements EventStore.Event {
        public final String aggregateId;
        public final long amount;
        public final Instant timestamp;
        public final int version;
        public MoneyWithdrawn(String id, long amount, int version) {
            this.aggregateId = id;
            this.amount = amount;
            this.timestamp = Instant.now();
            this.version = version;
        }
        @Override public String getAggregateId() { return aggregateId; }
        @Override public Instant getTimestamp() { return timestamp; }
        @Override public int getVersion() { return version; }
    }

    // ── 도메인 모델 ─────────────────────────────────────
    public static class Account {
        public String id;
        public String owner;
        public long balance;
        public int version;
    }

    // ── 프로젝션 ────────────────────────────────────────
    public static class AccountProjection {
        public Account project(List<EventStore.Event> events) {
            return applyTo(new Account(), events);
        }

        public Account applyTo(Account a, List<EventStore.Event> events) {
            for (EventStore.Event e : events) apply(a, e);
            return a;
        }

        private void apply(Account a, EventStore.Event e) {
            if (e instanceof AccountCreated) {
                AccountCreated c = (AccountCreated) e;
                a.id = c.aggregateId;
                a.owner = c.ownerName;
            } else if (e instanceof MoneyDeposited) {
                a.balance += ((MoneyDeposited) e).amount;
            } else if (e instanceof MoneyWithdrawn) {
                a.balance -= ((MoneyWithdrawn) e).amount;
            }
            a.version = e.getVersion();
        }
    }

    // ── 사용 예시 ──────────────────────────────────────
    public static Account run() {
        EventStore store = new EventStore();
        String id = "ACC-001";

        store.append(id, new AccountCreated(id, "홍길동", 1));
        store.append(id, new MoneyDeposited(id, 1000, 2));
        store.append(id, new MoneyWithdrawn(id, 300, 3));
        store.append(id, new MoneyDeposited(id, 500, 4));

        AccountProjection p = new AccountProjection();
        return p.project(store.getEvents(id));
    }
}
```
