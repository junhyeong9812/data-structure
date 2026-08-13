# 29. 이벤트 소싱 (Event Sourcing)

## 📋 문제 정의

**이벤트 저장소(Event Store)**와 **프로젝션(Projection)**을 기반으로 한
이벤트 소싱 시스템을 구현하세요.

이벤트 소싱은 상태를 저장하는 대신 상태 변경 이벤트의 시퀀스를 저장하여,
완전한 감사 추적과 시간 여행 디버깅을 가능하게 합니다.

---

## 🎯 학습 목표

- 이벤트 소싱 패턴
- CQRS (Command Query Responsibility Segregation)
- 이벤트 저장소 설계
- 프로젝션과 상태 재구성
- 스냅샷 최적화

---

## 📝 요구사항

### 핵심 개념

| 개념 | 설명 |
|------|------|
| **Event** | 발생한 사실을 나타내는 불변 객체 |
| **Event Store** | 이벤트를 순서대로 저장하는 저장소 |
| **Aggregate** | 이벤트의 대상이 되는 도메인 객체 |
| **Projection** | 이벤트로부터 읽기 모델을 생성 |
| **Snapshot** | 특정 시점의 상태를 캐싱 |

### 기본 연산

| 메서드 | 설명 |
|--------|------|
| `append(aggregateId, event)` | 이벤트 추가 |
| `getEvents(aggregateId)` | 집계의 모든 이벤트 조회 |
| `getEventsAfter(version)` | 특정 버전 이후 이벤트 |
| `project(aggregateId)` | 현재 상태로 프로젝션 |
| `projectAt(aggregateId, version)` | 특정 시점 상태로 프로젝션 |

### 고급 기능

| 기능 | 설명 |
|------|------|
| **Snapshot** | 주기적 상태 저장으로 재생 최적화 |
| **Subscription** | 실시간 이벤트 구독 |
| **Replay** | 이벤트 재생 (마이그레이션, 버그 수정) |
| **Versioning** | 이벤트 스키마 진화 |

---

## 📊 입출력 예시

### 예제 1: 기본 사용 (은행 계좌)
```java
EventStore store = new EventStore();

// 이벤트 발행
String accountId = "ACC-001";
store.append(accountId, new AccountCreated(accountId, "홍길동"));
store.append(accountId, new MoneyDeposited(accountId, 1000));
store.append(accountId, new MoneyWithdrawn(accountId, 300));
store.append(accountId, new MoneyDeposited(accountId, 500));

// 현재 상태 프로젝션
AccountProjection projection = new AccountProjection();
Account account = projection.project(store.getEvents(accountId));

System.out.println(account.getBalance());  // 1200
System.out.println(account.getOwner());    // "홍길동"
```

### 예제 2: 이벤트 흐름
```
시간 →
┌─────────────────────────────────────────────────────────┐
│  Event Store (ACC-001)                                  │
├─────────────────────────────────────────────────────────┤
│  v1: AccountCreated("홍길동")                            │
│  v2: MoneyDeposited(1000)        balance: 0 → 1000     │
│  v3: MoneyWithdrawn(300)         balance: 1000 → 700   │
│  v4: MoneyDeposited(500)         balance: 700 → 1200   │
└─────────────────────────────────────────────────────────┘

프로젝션 결과:
  Account { owner: "홍길동", balance: 1200, version: 4 }
```

### 예제 3: 시간 여행 (Time Travel)
```java
// 버전 2 시점의 상태
Account accountV2 = projection.projectAt(
    store.getEventsUpTo(accountId, 2)
);
System.out.println(accountV2.getBalance());  // 1000

// 버전 3 시점의 상태
Account accountV3 = projection.projectAt(
    store.getEventsUpTo(accountId, 3)
);
System.out.println(accountV3.getBalance());  // 700
```

### 예제 4: 스냅샷
```java
// 스냅샷 저장
store.saveSnapshot(accountId, account, 4);

// 이후 이벤트만 재생
store.append(accountId, new MoneyDeposited(accountId, 200));

// 스냅샷 + 이후 이벤트로 복원
Account restored = store.restore(accountId);
// 스냅샷(v4, balance=1200) + MoneyDeposited(200) = balance=1400
```

### 예제 5: 장바구니 예제
```java
EventStore store = new EventStore();
String cartId = "CART-001";

// 이벤트 발행
store.append(cartId, new CartCreated(cartId));
store.append(cartId, new ItemAdded(cartId, "item-1", "노트북", 1, 1500000));
store.append(cartId, new ItemAdded(cartId, "item-2", "마우스", 2, 50000));
store.append(cartId, new ItemRemoved(cartId, "item-2"));
store.append(cartId, new ItemQuantityChanged(cartId, "item-1", 2));

// 프로젝션
CartProjection projection = new CartProjection();
Cart cart = projection.project(store.getEvents(cartId));

// 결과
// items: [{item-1, 노트북, 2, 1500000}]
// total: 3000000
```

---

## 🔍 핵심 개념

### 이벤트 구조
```java
public interface Event {
    String getAggregateId();
    Instant getTimestamp();
    int getVersion();
}

public record AccountCreated(
    String aggregateId,
    String ownerName,
    Instant timestamp,
    int version
) implements Event {}

public record MoneyDeposited(
    String aggregateId,
    long amount,
    Instant timestamp,
    int version
) implements Event {}
```

### 이벤트 저장소 구조
```java
EventStore
├── events: Map<String, List<Event>>     // aggregateId → events
├── snapshots: Map<String, Snapshot>     // aggregateId → snapshot
└── globalSequence: AtomicLong           // 전역 시퀀스
```

### 프로젝션 패턴
```java
public class AccountProjection {
    public Account project(List<Event> events) {
        Account account = new Account();
        
        for (Event event : events) {
            apply(account, event);
        }
        
        return account;
    }
    
    private void apply(Account account, Event event) {
        switch (event) {
            case AccountCreated e -> account.setOwner(e.ownerName());
            case MoneyDeposited e -> account.deposit(e.amount());
            case MoneyWithdrawn e -> account.withdraw(e.amount());
            default -> {}
        }
    }
}
```

---

## 💡 힌트

### 기본 구조
```java
public class EventStore {
    private final Map<String, List<Event>> eventStreams = new ConcurrentHashMap<>();
    private final Map<String, Snapshot> snapshots = new ConcurrentHashMap<>();
    private final AtomicLong globalSequence = new AtomicLong(0);
    private final List<EventListener> listeners = new CopyOnWriteArrayList<>();
    
    public void append(String aggregateId, Event event) {
        eventStreams.computeIfAbsent(aggregateId, k -> new ArrayList<>())
            .add(event);
        
        // 리스너에게 알림
        listeners.forEach(l -> l.onEvent(event));
    }
    
    public List<Event> getEvents(String aggregateId) {
        return eventStreams.getOrDefault(aggregateId, List.of());
    }
}
```

### Event 베이스
```java
public abstract class BaseEvent implements Event {
    private final String aggregateId;
    private final Instant timestamp;
    private final int version;
    
    protected BaseEvent(String aggregateId, int version) {
        this.aggregateId = aggregateId;
        this.timestamp = Instant.now();
        this.version = version;
    }
}
```

### 스냅샷
```java
public record Snapshot(
    String aggregateId,
    Object state,
    int version,
    Instant createdAt
) {}

public <T> T restore(String aggregateId, Projection<T> projection) {
    Snapshot snapshot = snapshots.get(aggregateId);
    List<Event> events = getEvents(aggregateId);
    
    if (snapshot != null) {
        // 스냅샷 이후 이벤트만 재생
        List<Event> newEvents = events.stream()
            .filter(e -> e.getVersion() > snapshot.version())
            .toList();
        
        return projection.applyTo((T) snapshot.state(), newEvents);
    }
    
    return projection.project(events);
}
```

---

## ✅ 체크리스트

- [ ] Event 인터페이스 정의
- [ ] EventStore 구현
- [ ] 이벤트 추가 (append)
- [ ] 이벤트 조회 (getEvents)
- [ ] Projection 구현
- [ ] 시간 여행 (특정 버전 프로젝션)
- [ ] 스냅샷
- [ ] 이벤트 구독 (Subscription)

---

## 📚 참고

- Martin Fowler의 Event Sourcing 패턴
- Greg Young의 CQRS/ES
- EventStoreDB
- Axon Framework
