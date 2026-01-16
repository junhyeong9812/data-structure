# 이벤트 소싱 구현에 유용한 Java API

## 📦 컬렉션

### ConcurrentHashMap
```java
import java.util.concurrent.ConcurrentHashMap;

// 스레드 안전한 이벤트 스트림 저장
Map<String, List<Event>> eventStreams = new ConcurrentHashMap<>();

// 없으면 생성
eventStreams.computeIfAbsent(aggregateId, k -> new ArrayList<>());

// 있으면 수정
eventStreams.computeIfPresent(aggregateId, (k, events) -> {
    events.add(event);
    return events;
});
```

### CopyOnWriteArrayList
```java
import java.util.concurrent.CopyOnWriteArrayList;

// 스레드 안전한 리스너 목록
List<EventListener> listeners = new CopyOnWriteArrayList<>();

// 순회 중 수정 안전
for (EventListener listener : listeners) {
    listener.onEvent(event);  // 다른 스레드가 수정해도 안전
}
```

### List 필터링
```java
import java.util.List;

// 버전 필터링
List<Event> filtered = events.stream()
    .filter(e -> e.getVersion() > version)
    .toList();

// 타입 필터링
List<MoneyDeposited> deposits = events.stream()
    .filter(e -> e instanceof MoneyDeposited)
    .map(e -> (MoneyDeposited) e)
    .toList();

// 불변 복사본
List<Event> copy = List.copyOf(events);
```

---

## ⏱️ 시간 관련

### Instant
```java
import java.time.Instant;

// 현재 시간
Instant now = Instant.now();

// 타임스탬프 비교
boolean before = event.getTimestamp().isBefore(cutoff);
boolean after = event.getTimestamp().isAfter(start);

// 시간 범위 필터링
events.stream()
    .filter(e -> !e.getTimestamp().isBefore(start))
    .filter(e -> !e.getTimestamp().isAfter(end))
    .toList();
```

### Duration
```java
import java.time.Duration;
import java.time.Instant;

// 이벤트 처리 시간 측정
Instant start = Instant.now();
// ... 처리
Duration elapsed = Duration.between(start, Instant.now());
System.out.println("처리 시간: " + elapsed.toMillis() + "ms");
```

---

## 🔄 함수형 인터페이스

### Consumer
```java
import java.util.function.Consumer;

// 이벤트 핸들러
Consumer<Event> handler = event -> {
    switch (event) {
        case MoneyDeposited d -> processDeposit(d);
        case MoneyWithdrawn w -> processWithdrawal(w);
        default -> {}
    }
};

events.forEach(handler);
```

### BiFunction (Reducer)
```java
import java.util.function.BiFunction;

// 이벤트 리듀서
BiFunction<Account, Event, Account> reducer = (account, event) -> {
    return switch (event) {
        case MoneyDeposited d -> account.withBalance(account.balance() + d.amount());
        case MoneyWithdrawn w -> account.withBalance(account.balance() - w.amount());
        default -> account;
    };
};

Account result = events.stream()
    .reduce(new Account(), reducer, (a, b) -> b);
```

---

## 🧪 테스트

### AssertJ
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldAppendAndRetrieveEvents() {
    EventStore store = new EventStore();
    String accountId = "ACC-001";
    
    store.append(accountId, new AccountCreated(accountId, "홍길동"));
    store.append(accountId, new MoneyDeposited(accountId, 1000));
    
    List<Event> events = store.getEvents(accountId);
    
    assertThat(events).hasSize(2);
    assertThat(events.get(0)).isInstanceOf(AccountCreated.class);
    assertThat(events.get(1)).isInstanceOf(MoneyDeposited.class);
}

@Test
void shouldProjectCurrentState() {
    EventStore store = new EventStore();
    String accountId = "ACC-001";
    
    store.append(accountId, new AccountCreated(accountId, "홍길동"));
    store.append(accountId, new MoneyDeposited(accountId, 1000));
    store.append(accountId, new MoneyWithdrawn(accountId, 300));
    
    AccountProjection projection = new AccountProjection();
    Account account = projection.project(store.getEvents(accountId));
    
    assertThat(account.getOwner()).isEqualTo("홍길동");
    assertThat(account.getBalance()).isEqualTo(700);
}

@Test
void shouldSupportTimeTravelDebugging() {
    EventStore store = new EventStore();
    String accountId = "ACC-001";
    
    store.append(accountId, new AccountCreated(accountId, "홍길동"));
    store.append(accountId, new MoneyDeposited(accountId, 1000));
    store.append(accountId, new MoneyDeposited(accountId, 500));
    
    AccountProjection projection = new AccountProjection();
    
    // 버전 2 시점
    Account atV2 = projection.project(store.getEventsUpTo(accountId, 2));
    assertThat(atV2.getBalance()).isEqualTo(1000);
    
    // 버전 3 시점
    Account atV3 = projection.project(store.getEventsUpTo(accountId, 3));
    assertThat(atV3.getBalance()).isEqualTo(1500);
}

@Test
void shouldRestoreFromSnapshot() {
    EventStore store = new EventStore();
    String accountId = "ACC-001";
    
    // 초기 이벤트
    store.append(accountId, new AccountCreated(accountId, "홍길동"));
    store.append(accountId, new MoneyDeposited(accountId, 1000));
    
    // 스냅샷 저장
    Account snapshot = new Account();
    snapshot.setOwner("홍길동");
    snapshot.setBalance(1000);
    store.saveSnapshot(accountId, snapshot, 2);
    
    // 추가 이벤트
    store.append(accountId, new MoneyDeposited(accountId, 500));
    
    // 복원
    AccountProjection projection = new AccountProjection();
    Account restored = store.restore(accountId, projection);
    
    assertThat(restored.getBalance()).isEqualTo(1500);
}
```

---

## 📚 Java 21 관련

### Record
```java
// 이벤트 정의
public sealed interface Event permits AccountCreated, MoneyDeposited, MoneyWithdrawn {
    String getAggregateId();
    Instant getTimestamp();
    int getVersion();
}

public record AccountCreated(
    String aggregateId,
    String ownerName,
    Instant timestamp,
    int version
) implements Event {
    public AccountCreated(String aggregateId, String ownerName) {
        this(aggregateId, ownerName, Instant.now(), 0);
    }
}

// 스냅샷
public record Snapshot(
    String aggregateId,
    Object state,
    int version,
    Instant createdAt
) {}

// 읽기 모델
public record AccountReadModel(
    String owner,
    long balance
) {
    public AccountReadModel withBalance(long newBalance) {
        return new AccountReadModel(owner, newBalance);
    }
}
```

### Pattern Matching
```java
// 이벤트 처리
public void apply(Account account, Event event) {
    switch (event) {
        case AccountCreated e -> {
            account.setId(e.aggregateId());
            account.setOwner(e.ownerName());
        }
        case MoneyDeposited e -> 
            account.setBalance(account.getBalance() + e.amount());
        case MoneyWithdrawn e -> 
            account.setBalance(account.getBalance() - e.amount());
        default -> {}
    }
}

// 이벤트 타입별 스트림
long totalDeposits = events.stream()
    .mapMulti((event, consumer) -> {
        if (event instanceof MoneyDeposited d) {
            consumer.accept(d.amount());
        }
    })
    .mapToLong(Long.class::cast)
    .sum();
```

### Sealed Classes
```java
public sealed interface AccountEvent extends Event 
    permits AccountCreated, MoneyDeposited, MoneyWithdrawn, AccountClosed {
}

// 모든 케이스를 처리해야 함 (exhaustive)
String describe(AccountEvent event) {
    return switch (event) {
        case AccountCreated e -> "계좌 생성: " + e.ownerName();
        case MoneyDeposited e -> "입금: " + e.amount();
        case MoneyWithdrawn e -> "출금: " + e.amount();
        case AccountClosed e -> "계좌 폐쇄";
    };
}
```

---

## ⚡ 성능 팁

### 1. 스냅샷 주기
```java
// N개 이벤트마다 스냅샷
public void appendWithAutoSnapshot(String aggregateId, Event event, 
                                   Projection<?> projection, int snapshotInterval) {
    append(aggregateId, event);
    
    int version = getCurrentVersion(aggregateId);
    if (version % snapshotInterval == 0) {
        Object state = projection.project(getEvents(aggregateId));
        saveSnapshot(aggregateId, state, version);
    }
}
```

### 2. 배치 추가
```java
public void appendBatch(String aggregateId, List<Event> events) {
    List<Event> stream = eventStreams.computeIfAbsent(
        aggregateId, k -> new ArrayList<>()
    );
    
    int startVersion = stream.size();
    
    for (int i = 0; i < events.size(); i++) {
        Event event = events.get(i).withVersion(startVersion + i + 1);
        stream.add(event);
    }
}
```

### 3. 비동기 리스너
```java
private final ExecutorService listenerExecutor = Executors.newVirtualThreadPerTaskExecutor();

private void notifyListenersAsync(Event event, long sequence) {
    for (EventListener listener : listeners) {
        listenerExecutor.submit(() -> {
            try {
                listener.onEvent(event, sequence);
            } catch (Exception e) {
                // 에러 처리
            }
        });
    }
}
```

---

## 🔀 예외 클래스
```java
public class ConcurrencyException extends RuntimeException {
    public ConcurrencyException(String message) {
        super(message);
    }
}

public class AggregateNotFoundException extends RuntimeException {
    public AggregateNotFoundException(String aggregateId) {
        super("Aggregate not found: " + aggregateId);
    }
}

public class EventVersionMismatchException extends RuntimeException {
    public EventVersionMismatchException(int expected, int actual) {
        super("Expected version " + expected + " but got " + actual);
    }
}
```
