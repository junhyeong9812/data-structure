package com.datastructure.openaddr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 쿠쿠 해싱. 자리가 두 개뿐이다.
 *
 * 앞의 넷은 전부 "찾을 때까지 걷는다"였다. 그래서 조회 비용의 상한이 부하율에 딸려 있었다.
 * 여기서는 다르다. 모든 키가 정해진 두 자리 중 하나에 반드시 있다.
 * 조회는 두 칸을 보고 끝난다. 부하율이 얼마든 최악 O(1) 이다. 이게 이 구현이 파는 것이다.
 *
 * 값은 삽입이 치른다. 넣으려는 자리가 차 있으면 그 주인을 쫓아낸다.
 * 쫓겨난 주인은 자기 반대편 자리로 가고, 거기도 차 있으면 또 쫓아낸다. 뻐꾸기가 알을 밀어내듯이.
 * 이 연쇄가 정해진 횟수를 넘으면 고리에 빠진 것으로 보고 테이블을 키워 전부 다시 넣는다.
 *
 * 배열 하나를 반으로 갈라 두 테이블로 쓴다. 앞 절반이 1번, 뒤 절반이 2번이다.
 *   1번 자리 = hash & mask
 *   2번 자리 = half + (mix(hash) & mask)
 *
 * 삭제는 다섯 중 제일 쉽다. 자리가 정해져 있으므로 tombstone 없이 그냥 비우면 된다.
 * 탐사 사슬이라는 것이 아예 없기 때문이다.
 *
 * 고칠 수 없는 한계가 하나 있다. hashCode 가 같고 equals 가 다른 키 두 개는 두 자리가 완전히 겹친다.
 * 용량을 아무리 키워도 겹치므로 둘 다 담을 수 없다. 재해싱을 몇 번 하다 포기하고 예외를 던진다.
 * 체이닝은 이런 키를 그냥 사슬에 매단다. 상수 조회를 얻고 이걸 내준 것이다.
 */
public class CuckooHashMap<K, V> implements ProbeMap<K, V> {

    /** 뺏기 연쇄의 상한. 이걸 넘으면 고리로 본다. */
    static final int MAX_KICKS = 32;
    /** 한 번의 put 에서 허용하는 재해싱 횟수. 이걸 넘으면 담을 수 없는 키다. */
    static final int MAX_REHASH = 4;

    static final int DEFAULT_CAPACITY = 8;
    /** 두 자리뿐이라 0.5 근처에서 고리가 급증한다. 그보다 낮게 잡는다. */
    static final double DEFAULT_MAX_LOAD = 0.45;

    Object[] keys;      // 앞 절반이 1번 테이블, 뒤 절반이 2번 테이블
    Object[] values;
    int half;
    int mask;           // half - 1
    int size;
    final double maxLoad;
    int lastProbes;

    long kicks;         // 누적 뺏기 횟수
    int rehashes;       // 누적 재해싱 횟수(부하율 때문에 키운 것 포함)
    int cycleRehashes;  // 그중 고리 때문에 한 것

    public CuckooHashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_MAX_LOAD);
    }

    /** capacity 는 두 테이블을 합친 칸 수다. */
    public CuckooHashMap(int capacity, double maxLoad) {
        if (maxLoad <= 0 || maxLoad > 1) {
            throw new IllegalArgumentException("부하율은 0 초과 1 이하여야 한다: " + maxLoad);
        }
        this.maxLoad = maxLoad;
        allocate(Hashing.tableSizeFor(capacity));
    }

    private void allocate(int capacity) {
        this.keys = new Object[capacity];
        this.values = new Object[capacity];
        this.half = capacity / 2;
        this.mask = half - 1;
        this.size = 0;
    }

    int slot1(int hash) {
        return hash & mask;
    }

    int slot2(int hash) {
        return half + (Hashing.mix(hash) & mask);
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

    /** 지금까지 일어난 뺏기 횟수. 삽입이 치르는 값이다. */
    public long kickCount() {
        return kicks;
    }

    /** 지금까지 일어난 재해싱 횟수. 부하율 때문에 키운 것도 포함한다. */
    public int rehashCount() {
        return rehashes;
    }

    /** 그중 고리 때문에 한 재해싱. 부하율이 0.5 에 가까워지면 이게 급증한다. */
    public int cycleRehashCount() {
        return cycleRehashes;
    }

    public void resetCounters() {
        kicks = 0;
        rehashes = 0;
        cycleRehashes = 0;
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

    /**
     * 키를 지운다. 그냥 비운다.
     *
     * 선형/이차/이중은 여기서 tombstone 을 남겨야 했다. 지운 자리를 지나 뒤에 들어간 키가 있어서다.
     * 쿠쿠에는 그런 키가 없다. 모든 키는 자기 두 자리 중 하나에만 있기 때문이다.
     */
    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        int i = indexOf(key);
        if (i < 0) {
            return null;
        }
        V old = (V) values[i];
        keys[i] = null;
        values[i] = null;
        size--;
        return old;
    }

    // ------------------------------------------------------------------
    // 여기부터가 본체
    // ------------------------------------------------------------------

    /**
     * 키가 있는 칸. 없으면 -1. 어떤 경우에도 두 칸만 본다.
     */
    int indexOf(Object key) {
        lastProbes = 0;
        if (key == null) {
            return -1;
        }
        // TODO 9: 두 자리만 보고 답하라.
        //
        //   1. slot1(hash) 을 본다. 키가 같으면 그 칸이다.
        //   2. 아니면 slot2(hash) 를 본다. 키가 같으면 그 칸이다.
        //   3. 둘 다 아니면 없다.
        //
        // 두 번 다 lastProbes 를 올려라. 조회 비용이 언제나 2 이하라는 것이
        // 이 구현이 파는 것 전부이고, 테스트가 그 수를 단언한다.
        //
        // 걷는 코드를 쓰고 싶어지면 멈춰라. 여기에는 탐사 사슬이 없다.
        // 모든 키는 자기 두 자리 중 하나에 있거나 아예 없다.
        throw new UnsupportedOperationException("TODO 9: indexOf");
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
        // TODO 10: 이미 있으면 값만 바꾸고, 없으면 뺏기 연쇄로 넣어라.
        //
        //   1. indexOf 로 있는지 본다. 있으면 값만 바꾸고 이전 값을 반환한다.
        //   2. 부하율이 넘치면 rehash(keys.length * 2) 로 키운다. 여기까지는 채워진 코드와 같다.
        //   3. tryInsert 를 부른다. null 을 주면 들어간 것이다. size 를 올리고 끝낸다.
        //   4. null 이 아니면 고리에 빠져서 항목 하나를 손에 든 채 돌아온 것이다.
        //      rehash 로 키우고, 그 손에 든 항목을 다시 tryInsert 한다. 들어갈 때까지 반복한다.
        //      cycleRehashes 를 올려라. 이 수가 급증하는 지점이 이 구조의 한계다.
        //   5. MAX_REHASH 번을 넘기면 포기한다. giveUp 이 뒷정리를 해두었으니 부르고 나서
        //      IllegalStateException 을 던져라. 메시지에 "재해싱"을 넣어라.
        //
        // 4번에서 손에 든 항목을 버리면 안 된다. 그건 테이블 밖에 있는 진짜 데이터이고,
        // 그것이 방금 넣으려던 키라는 보장도 없다. 뻐꾸기가 돌려가며 들기 때문이다.
        // 버리면 예외도 안 나고 크기도 맞는데 키 하나가 사라진다. 대조 테스트가 그걸 잡는다.
        throw new UnsupportedOperationException("TODO 10: put");
    }

    /**
     * 포기한다. 들고 있던 항목과 테이블의 나머지에서 이번에 넣으려던 키만 빼고 다시 짓는다.
     *
     * 뺏기 연쇄 도중에 들고 있는 항목은 테이블 밖에 있다. 그대로 예외를 던지면
     * 그 항목이 사라진다. 그것이 이번에 넣으려던 키라는 보장도 없다. 뻐꾸기가 돌려가며 들기 때문이다.
     * 실패한 삽입이 멀쩡히 있던 데이터를 삼키면 안 된다.
     */
    private void giveUp(Object[] homeless, K key) {
        List<Object[]> keep = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] != null && !keys[i].equals(key)) {
                keep.add(new Object[] { keys[i], values[i] });
            }
        }
        if (!homeless[0].equals(key)) {
            keep.add(homeless);
        }
        int capacity = keys.length;
        for (int round = 0; round < 8; round++) {
            allocate(capacity);
            boolean ok = true;
            for (Object[] entry : keep) {
                if (tryInsert(entry[0], entry[1]) != null) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                size = keep.size();
                return;
            }
            capacity *= 2;
        }
        throw new IllegalStateException("복구조차 실패했다. capacity=" + capacity);
    }

    /**
     * 뺏기 연쇄로 넣어본다. 넣었으면 null, 고리에 빠졌으면 들고 있던 항목을 반환한다.
     *
     * 반환값을 버리면 그 항목이 통째로 사라진다. 크기만 맞고 키가 없어지는 조용한 사고다.
     */
    private Object[] tryInsert(Object key, Object value) {
        // TODO 11: 뺏기 연쇄를 돌려라.
        //
        //   1. 새 항목은 1번 테이블의 자기 자리(slot1)부터 본다.
        //   2. 그 칸이 비어 있으면 앉히고 null 을 반환한다.
        //   3. 차 있으면 주인을 들어내고 그 자리에 들고 있던 것을 앉힌다. kicks 를 올린다.
        //   4. 쫓겨난 항목은 반대편 테이블의 자기 자리로 간다.
        //      1번 테이블에서 밀렸으면 slot2 로, 2번에서 밀렸으면 slot1 로.
        //      지금 칸이 어느 테이블인지는 slot < half 로 안다.
        //   5. MAX_KICKS 를 넘도록 못 앉혔으면 고리다. 들고 있던 항목을
        //      new Object[] { 키, 값 } 으로 싸서 반환한다. 절대 버리지 마라.
        //
        // 4번을 "다음 칸"으로 쓰면 그건 선형 탐사다. 쿠쿠에는 다음 칸이 없다.
        // 자리는 두 개뿐이고, 그래서 조회가 두 칸으로 끝난다.
        // 칸을 볼 때마다 lastProbes 를 올려라. 삽입 비용이 여기서 나온다.
        throw new UnsupportedOperationException("TODO 11: tryInsert");
    }

    /**
     * 용량을 키워 전부 다시 넣는다.
     *
     * 다시 넣다가 또 고리에 빠질 수 있다. 그러면 더 키워서 처음부터 다시 한다.
     * 재해싱 안에서 쓴 탐사는 이 put 의 탐사가 아니므로 lastProbes 를 되돌려 놓는다.
     */
    private void rehash(int newCapacity) {
        rehashes++;
        int savedProbes = lastProbes;
        Object[] oldKeys = keys;
        Object[] oldValues = values;

        for (int round = 0; round < 8; round++) {
            allocate(newCapacity);
            boolean ok = true;
            for (int i = 0; i < oldKeys.length; i++) {
                if (oldKeys[i] != null && tryInsert(oldKeys[i], oldValues[i]) != null) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                size = 0;
                for (Object k : oldKeys) {
                    if (k != null) size++;
                }
                lastProbes = savedProbes;
                return;
            }
            newCapacity *= 2;
        }
        throw new IllegalStateException("재해싱이 계속 고리에 빠진다. capacity=" + newCapacity);
    }
}
