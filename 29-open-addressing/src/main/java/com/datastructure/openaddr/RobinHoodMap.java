package com.datastructure.openaddr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 로빈후드 해싱. 부자에게서 뺏어 가난한 자에게 준다.
 *
 * 수열은 선형 탐사와 똑같다. 옆칸으로 한 칸씩 간다. 다른 것은 자리 다툼의 규칙뿐이다.
 *
 *   넣으려는 키가 걸어온 거리 > 지금 그 칸에 있는 키가 걸어온 거리 이면 자리를 뺏는다.
 *   뺏긴 키를 들고 계속 걷는다.
 *
 * 그래서 오래 걸은 놈이 앞자리를 갖는다. 평균 탐사 거리는 선형 탐사와 같다.
 * 자리 배치가 바뀔 뿐 총합은 보존되기 때문이다. 줄어드는 것은 분산, 즉 최댓값이다.
 * 이 구별이 이 구현의 전부다. 평균이 아니라 꼬리를 자른다.
 *
 * 조회에서 이득이 하나 더 나온다. 걸어온 거리가 지금 칸의 거리보다 커지는 순간
 * 그 뒤에는 이 키가 있을 수 없다. 뺏겼어야 하기 때문이다. 그래서 빈칸까지 안 가고 멈춘다.
 * 군집 한가운데로 떨어진 없는 키 조회가 선형 탐사에서는 덩어리 끝까지 갔는데 여기서는 몇 칸이면 끝난다.
 *
 * 삭제도 다르다. tombstone 이 필요 없다. 뒤에서 당겨오면(backward shift) 되기 때문이다.
 * 자기 홈에 있는 키(거리 0)를 만나면 멈춘다. 그 앞의 키들은 전부 홈이 뒤에 있어서 당겨도 된다.
 *
 * 필드 이름 keys, values, hashes 는 테스트가 직접 들여다본다. 빈칸은 keys[i] == null 이다.
 */
public class RobinHoodMap<K, V> implements ProbeMap<K, V> {

    static final int DEFAULT_CAPACITY = 8;
    static final double DEFAULT_MAX_LOAD = 0.5;

    Object[] keys;
    Object[] values;
    int[] hashes;      // 홈 버킷을 다시 계산하지 않으려고 들고 있는다
    int size;
    int mask;
    final double maxLoad;
    int lastProbes;

    public RobinHoodMap() {
        this(DEFAULT_CAPACITY, DEFAULT_MAX_LOAD);
    }

    public RobinHoodMap(int capacity, double maxLoad) {
        if (maxLoad <= 0 || maxLoad > 1) {
            throw new IllegalArgumentException("부하율은 0 초과 1 이하여야 한다: " + maxLoad);
        }
        this.maxLoad = maxLoad;
        allocate(Hashing.tableSizeFor(capacity));
    }

    private void allocate(int capacity) {
        this.keys = new Object[capacity];
        this.values = new Object[capacity];
        this.hashes = new int[capacity];
        this.mask = capacity - 1;
        this.size = 0;
    }

    /** 그 칸에 있는 키가 홈에서 몇 칸 밀려났나. 되감김을 고려해 마스크로 잰다. */
    int distanceOf(int slot) {
        return (slot - (hashes[slot] & mask)) & mask;
    }

    // ------------------------------------------------------------------
    // 채워져 있는 부분
    // ------------------------------------------------------------------

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int capacity() {
        return keys.length;
    }

    @Override
    public int lastProbeCount() {
        return lastProbes;
    }

    @Override
    public boolean containsKey(Object key) {
        return indexOf(key) >= 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        int i = indexOf(key);
        return i < 0 ? null : (V) values[i];
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterable<K> keys() {
        List<K> result = new ArrayList<>(size);
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != null) result.add((K) keys[i]);
        }
        return result;
    }

    @Override
    public void clear() {
        Arrays.fill(keys, null);
        Arrays.fill(values, null);
        Arrays.fill(hashes, 0);
        size = 0;
        lastProbes = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (K key : keys()) {
            if (!first) sb.append(", ");
            sb.append(key).append('=').append(get(key));
            first = false;
        }
        return sb.append('}').toString();
    }

    private void place(int slot, Object key, Object value, int hash) {
        keys[slot] = key;
        values[slot] = value;
        hashes[slot] = hash;
    }

    @SuppressWarnings("unchecked")
    void resize() {
        Object[] oldKeys = keys;
        Object[] oldValues = values;
        allocate(oldKeys.length * 2);
        for (int i = 0; i < oldKeys.length; i++) {
            if (oldKeys[i] != null) {
                put((K) oldKeys[i], (V) oldValues[i]);   // 크기가 두 배라 여기서 또 리사이즈되지 않는다
            }
        }
    }

    // ------------------------------------------------------------------
    // 여기부터가 본체
    // ------------------------------------------------------------------

    /**
     * 키가 있는 칸. 없으면 -1.
     */
    int indexOf(Object key) {
        lastProbes = 0;
        if (key == null) {
            return -1;
        }
        // TODO 6: 선형 탐사처럼 걷되, 두 가지 이유로 멈춘다.
        //
        //   1. 빈칸(keys[slot] == null)을 만나면 없다.
        //   2. 지금 칸 주인의 탐사 거리(distanceOf)가 내가 걸어온 거리보다 작으면 없다.
        //      찾는 키가 이 뒤에 있었다면 이 칸을 뺏었어야 하기 때문이다.
        //      이게 로빈후드가 조회에서 버는 이득 전부다. 빼도 답은 맞고 탐사만 는다.
        //      (ProbeCountTest 가 그 차이를 센다. 없는 키 4000번에 8,000 대 8,006,000 이다)
        //
        // 걸어온 거리를 세는 변수를 따로 두어야 한다. slot 만으로는 알 수 없다.
        // hashes[slot] 을 먼저 비교하면 equals 호출을 아낄 수 있다. 문자열 키에서 이게 크다.
        throw new UnsupportedOperationException("TODO 6: indexOf");
    }

    /**
     * 키에 값을 넣고 이전 값을 반환한다.
     */
    @Override
    @SuppressWarnings("unchecked")
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("null 키는 담을 수 없다");
        }
        if (size + 1 > keys.length * maxLoad) {
            resize();
        }
        lastProbes = 0;
        // TODO 7: 걸으면서 자리를 뺏어라. 손에는 언제나 아직 앉히지 못한 항목 하나가 있다.
        //
        //   1. 홈에서 출발한다. 들고 있는 것은 처음에는 넣으려는 (키, 값, 해시)다.
        //   2. 빈칸을 만나면 들고 있던 것을 앉히고 size 를 올린다. 여기서 끝난다.
        //   3. 아직 원래 키를 들고 있는 동안에만 "같은 키인가"를 본다. 같으면 값만 바꾸고
        //      이전 값을 반환한다. 자리는 옮기지 않는다.
        //   4. 이 칸 주인의 거리가 내가 걸어온 거리보다 작으면 자리를 뺏는다.
        //      들고 있던 것을 그 칸에 앉히고, 쫓겨난 것을 들고 계속 걷는다.
        //      걸어온 거리도 쫓겨난 놈의 거리로 바꾼다. 이제 그를 대신해 걷기 때문이다.
        //   5. 한 번 뺏은 뒤에는 3번을 할 필요가 없다.
        //      뺏은 순간 원래 키가 테이블에 없다는 것이 확정됐기 때문이다(있었다면 그 전에 만났다).
        //
        // 4번의 부등호가 이 구현의 전부다. <= 로 쓰면 거리가 같은데도 자리를 뺏어
        // 대조 테스트가 잡는다(152개 중 6개가 무너진다).
        //
        // 정직하게 적어둔다. 4번의 "거리도 바꾼다"와 5번은 빼도 우리 테스트가 전부 통과한다.
        // 거리를 안 바꾸면 거리가 같은 둘의 자리가 서로 바뀔 뿐, 칸마다의 탐사 거리는 글자 그대로
        // 같기 때문이다(200번 시행에서 거리 배열이 한 번도 안 달랐다). 5번도 비교만 늘 뿐이다.
        // 그래도 이렇게 쓰는 이유는 dist 가 "지금 들고 있는 놈이 걸어온 거리"라는 뜻이기 때문이다.
        // 뜻이 어긋난 변수는 다음 사람이 고칠 때 터진다.
        throw new UnsupportedOperationException("TODO 7: put");
    }

    /**
     * 키를 지우고 그 값을 반환한다. tombstone 없이 뒤에서 당겨온다.
     */
    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        int hole = indexOf(key);
        if (hole < 0) {
            return null;
        }
        V old = (V) values[hole];
        // TODO 8: 뒤엣것을 앞으로 당겨오고 마지막 자리를 진짜로 비워라.
        //
        //   1. 지운 자리 다음 칸부터 본다.
        //   2. 그 칸이 비어 있으면 멈춘다.
        //   3. 그 칸의 주인이 자기 홈에 앉아 있으면(거리 0) 멈춘다.
        //      거리 0 인 키를 당기면 자기 홈보다 앞으로 가버려서 영영 못 찾는다.
        //   4. 아니면 그 항목을 구멍으로 옮기고, 구멍이 그 칸이 된다. 계속 당긴다.
        //   5. 마지막 구멍은 null 로 비운다. size 를 줄인다.
        //
        // 이걸 하면 tombstone 이 필요 없다. 05번이 tombstone 을 세운 이유는
        // "지운 자리 뒤에 이 자리를 지나온 키가 있을 수 있다"였는데, 여기서는 그 키들을
        // 아예 앞으로 데려오기 때문이다. 값 참조도 같이 끊어라(clear 와 같은 이유다).
        //
        // 지우기가 O(1) 이 아니라 덩어리 길이에 비례한다는 것이 대가다.
        // 그 대신 넣고 지우기를 2만 번 반복해도 안 느려진다(ProbeCountTest 측정 5).
        throw new UnsupportedOperationException("TODO 8: remove");
    }
}
