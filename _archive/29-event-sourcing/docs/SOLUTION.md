# 이벤트 소싱 풀이 해설

## 📌 핵심 아이디어

이벤트 소싱은 **상태 대신 이벤트를 저장**합니다.
현재 상태는 이벤트를 순서대로 재생하여 계산합니다.

**핵심 장점**:
- 완전한 감사 추적 (Audit Trail)
- 시간 여행 디버깅
- 이벤트 기반 통합
- 상태 재구성 가능

---

## 🔑 핵심 개념

### 1. 전통적 CRUD vs 이벤트 소싱
```
CRUD (현재 상태만 저장):
┌─────────────────┐
│ Account         │
│ balance: 1200   │  ← 어떻게 1200이 됐는지 모름
└─────────────────┘

Event Sourcing (이벤트 저장):
┌─────────────────────────────┐
│ AccountCreated              │
│ MoneyDeposited(1000)        │  ← 모든 변경 이력 보존
│ MoneyWithdrawn(300)         │
│ MoneyDeposited(500)         │
└─────────────────────────────┘
```

### 2. 프로젝션 (Fold)
```java
// 이벤트 시퀀스를 상태로 변환
State = fold(events, initialState, applyFunction)

// 예: 잔액 계산
balance = events.stream()
    .reduce(0L, (bal, event) -> {
        return switch (event) {
            case Deposited d -> bal + d.amount();
            case Withdrawn w -> bal - w.amount();
            default -> bal;
        };
    }, Long::sum);
```

### 3. 버전과 낙관적 동시성
```java
// 버전을 이용한 동시성 제어
void append(String aggregateId, Event event, int expectedVersion) {
    List<Event> events = getEvents(aggregateId);
    
    if (events.size() != expectedVersion) {
        throw new ConcurrencyException(
            "Expected version " + expectedVersion + 
            " but was " + events.size()
        );
    }
    
    eventStreams.get(aggregateId).add(event);
}
```

---

## 📝 POP 구현 해설

### 완전한 구현
```java
public class EventStore {
    private final Map<String, List<Event>> eventStreams = new ConcurrentHashMap<>();
    private final Map<String, Snapshot> snapshots = new ConcurrentHashMap<>();
    private final AtomicLong globalSequence = new AtomicLong(0);
    private final List<EventListener> listeners = new CopyOnWriteArrayList<>();
    
    // 이벤트 추가
    public synchronized void append(String aggregateId, Event event) {
        List<Event> events = eventStreams.computeIfAbsent(
            aggregateId, k -> new ArrayList<>()
        );
        
        // 버전 설정
        int version = events.size() + 1;
        Event versionedEvent = event.withVersion(version);
        
        events.add(versionedEvent);
        
        // 글로벌 시퀀스 할당
        long sequence = globalSequence.incrementAndGet();
        
        // 리스너 알림
        notifyListeners(versionedEvent, sequence);
    }
    
    // 낙관적 동시성 제어
    public synchronized void append(String aggregateId, Event event, 
                                    int expectedVersion) {
        List<Event> events = eventStreams.get(aggregateId);
        int currentVersion = events != null ? events.size() : 0;
        
        if (currentVersion != expectedVersion) {
            throw new ConcurrencyException(
                "Expected version " + expectedVersion + 
                " but current is " + currentVersion
            );
        }
        
        append(aggregateId, event);
    }
    
    // 모든 이벤트 조회
    public List<Event> getEvents(String aggregateId) {
        return List.copyOf(
            eventStreams.getOrDefault(aggregateId, List.of())
        );
    }
    
    // 특정 버전까지 이벤트
    public List<Event> getEventsUpTo(String aggregateId, int version) {
        List<Event> events = eventStreams.getOrDefault(aggregateId, List.of());
        return events.stream()
            .filter(e -> e.getVersion() <= version)
            .toList();
    }
    
    // 특정 버전 이후 이벤트
    public List<Event> getEventsAfter(String aggregateId, int version) {
        List<Event> events = eventStreams.getOrDefault(aggregateId, List.of());
        return events.stream()
            .filter(e -> e.getVersion() > version)
            .toList();
    }
    
    // 시간 범위 이벤트
    public List<Event> getEventsBetween(String aggregateId, 
                                        Instant start, Instant end) {
        return getEvents(aggregateId).stream()
            .filter(e -> !e.getTimestamp().isBefore(start) && 
                        !e.getTimestamp().isAfter(end))
            .toList();
    }
    
    // 스냅샷 저장
    public void saveSnapshot(String aggregateId, Object state, int version) {
        snapshots.put(aggregateId, new Snapshot(
            aggregateId, state, version, Instant.now()
        ));
    }
    
    // 스냅샷 조회
    public Optional<Snapshot> getSnapshot(String aggregateId) {
        return Optional.ofNullable(snapshots.get(aggregateId));
    }
    
    // 스냅샷 + 이후 이벤트로 복원
    public <T> T restore(String aggregateId, Projection<T> projection) {
        Snapshot snapshot = snapshots.get(aggregateId);
        
        if (snapshot != null) {
            @SuppressWarnings("unchecked")
            T state = (T) snapshot.state();
            List<Event> newEvents = getEventsAfter(aggregateId, snapshot.version());
            return projection.applyTo(state, newEvents);
        }
        
        return projection.project(getEvents(aggregateId));
    }
    
    // 리스너 등록
    public void subscribe(EventListener listener) {
        listeners.add(listener);
    }
    
    // 리스너 해제
    public void unsubscribe(EventListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners(Event event, long sequence) {
        for (EventListener listener : listeners) {
            try {
                listener.onEvent(event, sequence);
            } catch (Exception e) {
                // 로깅 또는 에러 핸들링
            }
        }
    }
    
    // 모든 집계 ID
    public Set<String> getAllAggregateIds() {
        return Set.copyOf(eventStreams.keySet());
    }
    
    // 전체 이벤트 수
    public long getTotalEventCount() {
        return eventStreams.values().stream()
            .mapToLong(List::size)
            .sum();
    }
    
    // 특정 집계의 현재 버전
    public int getCurrentVersion(String aggregateId) {
        return eventStreams.getOrDefault(aggregateId, List.of()).size();
    }
}
```

### Event 인터페이스와 구현
```java
public interface Event {
    String getAggregateId();
    Instant getTimestamp();
    int getVersion();
    Event withVersion(int version);
}

// Record 기반 이벤트
public record AccountCreated(
    String aggregateId,
    String ownerName,
    Instant timestamp,
    int version
) implements Event {
    
    public AccountCreated(String aggregateId, String ownerName) {
        this(aggregateId, ownerName, Instant.now(), 0);
    }
    
    @Override
    public Event withVersion(int version) {
        return new AccountCreated(aggregateId, ownerName, timestamp, version);
    }
}

public record MoneyDeposited(
    String aggregateId,
    long amount,
    Instant timestamp,
    int version
) implements Event {
    
    public MoneyDeposited(String aggregateId, long amount) {
        this(aggregateId, amount, Instant.now(), 0);
    }
    
    @Override
    public Event withVersion(int version) {
        return new MoneyDeposited(aggregateId, amount, timestamp, version);
    }
}

public record MoneyWithdrawn(
    String aggregateId,
    long amount,
    Instant timestamp,
    int version
) implements Event {
    
    public MoneyWithdrawn(String aggregateId, long amount) {
        this(aggregateId, amount, Instant.now(), 0);
    }
    
    @Override
    public Event withVersion(int version) {
        return new MoneyWithdrawn(aggregateId, amount, timestamp, version);
    }
}
```

### Projection 구현
```java
public interface Projection<T> {
    T project(List<Event> events);
    T applyTo(T state, List<Event> events);
}

public class AccountProjection implements Projection<Account> {
    
    @Override
    public Account project(List<Event> events) {
        Account account = new Account();
        return applyTo(account, events);
    }
    
    @Override
    public Account applyTo(Account account, List<Event> events) {
        for (Event event : events) {
            apply(account, event);
        }
        return account;
    }
    
    private void apply(Account account, Event event) {
        switch (event) {
            case AccountCreated e -> {
                account.setId(e.aggregateId());
                account.setOwner(e.ownerName());
                account.setBalance(0);
            }
            case MoneyDeposited e -> {
                account.setBalance(account.getBalance() + e.amount());
            }
            case MoneyWithdrawn e -> {
                account.setBalance(account.getBalance() - e.amount());
            }
            default -> {}
        }
        account.setVersion(event.getVersion());
    }
}

// Account 모델
public class Account {
    private String id;
    private String owner;
    private long balance;
    private int version;
    
    // getters and setters
}
```

### 이벤트 리스너
```java
@FunctionalInterface
public interface EventListener {
    void onEvent(Event event, long sequence);
}

// 예: 알림 서비스
public class NotificationListener implements EventListener {
    @Override
    public void onEvent(Event event, long sequence) {
        if (event instanceof MoneyWithdrawn w && w.amount() > 1000000) {
            sendAlert("Large withdrawal: " + w.amount());
        }
    }
}

// 예: 읽기 모델 업데이트
public class ReadModelUpdater implements EventListener {
    private final Map<String, AccountReadModel> readModels = new ConcurrentHashMap<>();
    
    @Override
    public void onEvent(Event event, long sequence) {
        switch (event) {
            case AccountCreated e -> 
                readModels.put(e.aggregateId(), new AccountReadModel(e.ownerName(), 0));
            case MoneyDeposited e -> 
                readModels.computeIfPresent(e.aggregateId(), 
                    (id, model) -> model.withBalance(model.balance() + e.amount()));
            case MoneyWithdrawn e -> 
                readModels.computeIfPresent(e.aggregateId(), 
                    (id, model) -> model.withBalance(model.balance() - e.amount()));
            default -> {}
        }
    }
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 |
|------|-----------|
| append | O(1) |
| getEvents | O(n) |
| project | O(n) |
| restore (with snapshot) | O(k) |
| subscribe | O(1) |

n = 총 이벤트 수
k = 스냅샷 이후 이벤트 수

---

## ❌ 흔한 실수

### 1. 이벤트 수정
```java
// 잘못됨: 이벤트 수정
event.setAmount(newAmount);  // 이벤트는 불변이어야 함!

// 올바름: 새 이벤트 발행
store.append(aggregateId, new AmountCorrected(oldAmount, newAmount));
```

### 2. 비즈니스 로직을 프로젝션에
```java
// 잘못됨: 프로젝션에서 검증
void apply(Account account, MoneyWithdrawn event) {
    if (account.getBalance() < event.amount()) {
        throw new InsufficientFundsException();  // 프로젝션에서 예외?
    }
    account.withdraw(event.amount());
}

// 올바름: 커맨드 핸들러에서 검증
void handle(WithdrawCommand cmd) {
    Account account = restore(cmd.accountId());
    if (account.getBalance() < cmd.amount()) {
        throw new InsufficientFundsException();
    }
    store.append(cmd.accountId(), new MoneyWithdrawn(...));
}
```

### 3. 스냅샷 버전 불일치
```java
// 잘못됨: 스냅샷 버전 관리 누락
saveSnapshot(aggregateId, state);  // 버전 없음

// 올바름: 버전 포함
saveSnapshot(aggregateId, state, currentVersion);
```

---

## 🔗 관련 문제

- CQRS 패턴
- 도메인 주도 설계 (DDD)
- 메시지 큐
- 분산 트랜잭션
