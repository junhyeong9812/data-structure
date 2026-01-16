# 일관된 해싱 풀이 해설

## 📌 핵심 아이디어

일관된 해싱은 **해시 공간을 원형**으로 배치하여, 
노드 추가/제거 시 최소한의 키만 재배치되도록 합니다.

**핵심 원리**:
- 키와 노드 모두 같은 해시 함수로 해싱
- 키는 시계방향으로 가장 가까운 노드에 할당
- 가상 노드로 부하 균등 분배

---

## 🔑 핵심 개념

### 1. 해시 링 구조
```
해시 공간: 0 ~ 2^32-1 (또는 2^64-1)

        0
        │
   N3 ──┼── N1
  /     │     \
 /      │      \
│       │       │
│       │       │
 \      │      /
  \     │     /
   N2 ──┼── N4
        │
     2^32-1

키 K의 해시값이 N2와 N3 사이라면 → N3에 할당
(시계방향으로 가장 가까운 노드)
```

### 2. TreeMap 활용
```java
TreeMap의 주요 메서드:
- ceilingEntry(key): key 이상인 가장 작은 엔트리
- floorEntry(key): key 이하인 가장 큰 엔트리
- firstEntry(): 가장 작은 키의 엔트리
- higherEntry(key): key 초과인 가장 작은 엔트리

시계방향 탐색:
1. ceilingEntry(hash)로 hash 이상인 노드 찾기
2. 없으면 firstEntry()로 처음으로 돌아감
```

### 3. 가상 노드
```
물리 노드 1개 → 가상 노드 150개

server1 → server1#0, server1#1, ..., server1#149

각 가상 노드가 링의 다른 위치에 배치됨
→ 부하가 더 균등하게 분산
→ 노드 제거 시에도 영향 분산
```

---

## 📝 POP 구현 해설

### 완전한 구현
```java
public class ConsistentHashing {
    private final TreeMap<Long, String> ring = new TreeMap<>();
    private final Map<String, Set<Long>> nodePositions = new HashMap<>();
    private final int virtualNodeCount;
    
    public ConsistentHashing(int virtualNodeCount) {
        this.virtualNodeCount = virtualNodeCount;
    }
    
    // 노드 추가
    public void addNode(String node) {
        if (nodePositions.containsKey(node)) {
            return;  // 이미 존재
        }
        
        Set<Long> positions = new HashSet<>();
        
        for (int i = 0; i < virtualNodeCount; i++) {
            long hash = hash(node + "#" + i);
            ring.put(hash, node);
            positions.add(hash);
        }
        
        nodePositions.put(node, positions);
    }
    
    // 노드 제거
    public void removeNode(String node) {
        Set<Long> positions = nodePositions.remove(node);
        
        if (positions != null) {
            for (Long pos : positions) {
                ring.remove(pos);
            }
        }
    }
    
    // 키를 담당하는 노드 찾기
    public String getNode(String key) {
        if (ring.isEmpty()) {
            return null;
        }
        
        long hash = hash(key);
        
        // 시계방향으로 가장 가까운 노드
        Map.Entry<Long, String> entry = ring.ceilingEntry(hash);
        
        if (entry == null) {
            entry = ring.firstEntry();
        }
        
        return entry.getValue();
    }
    
    // 복제를 위한 다중 노드
    public List<String> getNodes(String key, int count) {
        if (ring.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<String> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        
        long hash = hash(key);
        
        // 시계방향으로 순회
        SortedMap<Long, String> tailMap = ring.tailMap(hash);
        
        for (String node : tailMap.values()) {
            if (seen.add(node)) {
                result.add(node);
                if (result.size() >= count) {
                    return result;
                }
            }
        }
        
        // 링의 처음부터 다시
        for (String node : ring.values()) {
            if (seen.add(node)) {
                result.add(node);
                if (result.size() >= count) {
                    return result;
                }
            }
        }
        
        return result;
    }
    
    // 해시 함수 (MD5 기반)
    private long hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(key.getBytes(StandardCharsets.UTF_8));
            
            // 처음 8바이트를 long으로 변환
            long hash = 0;
            for (int i = 0; i < 8; i++) {
                hash = (hash << 8) | (digest[i] & 0xFF);
            }
            return hash;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    // 현재 노드 수
    public int getNodeCount() {
        return nodePositions.size();
    }
    
    // 링의 총 엔트리 수 (가상 노드 포함)
    public int getRingSize() {
        return ring.size();
    }
}
```

### 가중치 기반 노드
```java
public class WeightedConsistentHashing {
    private final TreeMap<Long, String> ring = new TreeMap<>();
    private final Map<String, Integer> nodeWeights = new HashMap<>();
    private final int baseVirtualNodes;
    
    public WeightedConsistentHashing(int baseVirtualNodes) {
        this.baseVirtualNodes = baseVirtualNodes;
    }
    
    // 가중치와 함께 노드 추가
    public void addNode(String node, int weight) {
        int virtualCount = baseVirtualNodes * weight;
        
        for (int i = 0; i < virtualCount; i++) {
            long hash = hash(node + "#" + i);
            ring.put(hash, node);
        }
        
        nodeWeights.put(node, weight);
    }
    
    // 더 높은 가중치 = 더 많은 가상 노드 = 더 많은 트래픽
}
```

### 키 분포 분석
```java
public Map<String, Integer> getKeyDistribution(List<String> keys) {
    Map<String, Integer> distribution = new HashMap<>();
    
    for (String key : keys) {
        String node = getNode(key);
        distribution.merge(node, 1, Integer::sum);
    }
    
    return distribution;
}

// 표준편차로 균등도 측정
public double getDistributionStdDev(List<String> keys) {
    Map<String, Integer> dist = getKeyDistribution(keys);
    
    double mean = (double) keys.size() / nodePositions.size();
    double variance = 0;
    
    for (int count : dist.values()) {
        variance += Math.pow(count - mean, 2);
    }
    
    return Math.sqrt(variance / dist.size());
}
```

---

## 📝 해시 함수 비교
```java
// MD5 (일반적)
public long hashMD5(String key) {
    MessageDigest md = MessageDigest.getInstance("MD5");
    byte[] digest = md.digest(key.getBytes());
    return ByteBuffer.wrap(digest).getLong();
}

// MurmurHash (빠름)
public long hashMurmur(String key) {
    // Google Guava 사용
    return Hashing.murmur3_128().hashString(key, StandardCharsets.UTF_8)
                  .asLong();
}

// SHA-256 (암호학적)
public long hashSHA256(String key) {
    MessageDigest md = MessageDigest.getInstance("SHA-256");
    byte[] digest = md.digest(key.getBytes());
    return ByteBuffer.wrap(digest).getLong();
}

// 간단한 해시 (테스트용)
public long hashSimple(String key) {
    return key.hashCode() & 0xFFFFFFFFL;  // unsigned 32-bit
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 | 공간복잡도 |
|------|-----------|-----------|
| addNode | O(V log(NV)) | O(V) |
| removeNode | O(V log(NV)) | O(1) |
| getNode | O(log(NV)) | O(1) |
| getNodes(k) | O(k log(NV)) | O(k) |

N = 물리 노드 수, V = 가상 노드 수

### 키 재배치 비율
```
노드 추가 시: ~K/(N+1) 키 이동
노드 제거 시: ~K/N 키 이동

K = 전체 키 수
N = 노드 수

예: 4노드 → 5노드, 1000개 키
이동 키 ≈ 1000/5 = 200개 (20%)
```

---

## ❌ 흔한 실수

### 1. 링 순환 처리 누락
```java
// 잘못됨: 링 끝 도달 시 처리 안 함
public String getNode(String key) {
    long hash = hash(key);
    return ring.ceilingEntry(hash).getValue();  // NPE 가능!
}

// 올바름: 링 순환 처리
public String getNode(String key) {
    long hash = hash(key);
    Map.Entry<Long, String> entry = ring.ceilingEntry(hash);
    if (entry == null) {
        entry = ring.firstEntry();  // 처음으로 돌아감
    }
    return entry.getValue();
}
```

### 2. 중복 물리 노드
```java
// 잘못됨: getNodes에서 가상 노드 중복 카운트
public List<String> getNodes(String key, int count) {
    List<String> result = new ArrayList<>();
    // 같은 물리 노드의 여러 가상 노드가 결과에 포함될 수 있음!
}

// 올바름: Set으로 물리 노드 중복 제거
public List<String> getNodes(String key, int count) {
    Set<String> seen = new HashSet<>();
    List<String> result = new ArrayList<>();
    // seen으로 물리 노드 중복 방지
}
```

### 3. 해시 충돌
```java
// 잘못됨: 해시 충돌 시 덮어쓰기
ring.put(hash, node);  // 같은 해시 위치에 다른 노드 있으면 덮어씀

// 개선: 충돌 처리 (실제로는 64비트에서 거의 발생 안 함)
// 또는 좋은 해시 함수 사용
```

---

## 🔗 관련 문제

- 분산 캐시 설계 (Memcached, Redis Cluster)
- 로드 밸런서 설계
- 분산 데이터베이스 샤딩
- CDN 라우팅
