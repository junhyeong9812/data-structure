package com.datastructure.lsm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * LSM 트리 본체. Log-Structured Merge-tree.
 *
 * <h2>층</h2>
 *
 *   memtable        메모리. 모든 쓰기가 여기로 온다. 가장 최신이다
 *   sstables[0]     가장 최근에 굳힌 것
 *   sstables[1]     그 전 것
 *   ...
 *   sstables[n-1]   가장 오래된 것
 *
 * 조회는 이 순서대로 내려가며 처음 만난 것을 답으로 삼는다.
 * 아래로 더 내려가면 안 된다. 아래에 있는 것은 전부 옛날 것이기 때문이다.
 *
 * <h2>측정</h2>
 *
 * 파일을 쓰지 않는 대신 세 가지를 센다. 시간이 아니라 횟수라서 결정적이다.
 *
 *   diskReads                get 이 SSTable 을 몇 번 뒤졌나
 *   sequentialBytesWritten   flush 와 compaction 이 순차로 쏟은 바이트
 *   storedEntryCount         살아 있든 죽었든 지금 들고 있는 엔트리 수
 *
 * 이 셋이 각각 읽기 증폭, 쓰기 증폭, 공간 증폭이다.
 */
public class LsmTree<K extends Comparable<K>, V> implements KeyValueStore<K, V> {

    private final int memtableThreshold;
    private final boolean bloomEnabled;
    private final MemTable<K, V> memtable = new MemTable<>();

    /** 0번이 가장 최신이다. 이 순서가 정확성을 만든다. */
    private final List<SSTable<K, V>> sstables = new ArrayList<>();

    private long diskReads;
    private long sequentialBytesWritten;
    private long flushCount;
    private long compactionCount;

    /** 블룸 필터를 켠 채로 만든다. */
    public LsmTree(int memtableThreshold) {
        this(memtableThreshold, true);
    }

    public LsmTree(int memtableThreshold, boolean bloomEnabled) {
        if (memtableThreshold < 1) {
            throw new IllegalArgumentException("memtable 임계치는 1 이상이어야 한다: " + memtableThreshold);
        }
        this.memtableThreshold = memtableThreshold;
        this.bloomEnabled = bloomEnabled;
    }

    /** put 과 delete 가 함께 지나는 길. 값이거나 Tombstone.MARKER 다. */
    private void write(K key, Object value) {
        // TODO 6: memtable 에만 쓴다. 그리고 꽉 찼으면 쏟는다.
        //
        //   1. memtable 에 넣는다
        //   2. memtable 의 키 개수가 memtableThreshold 에 닿았으면 flush 한다
        //
        // 이 메서드에 **디스크를 읽는 코드가 하나도 없다는 것** 이 이 자료구조의 요점이다.
        // 15번 B+트리의 put 은 잎을 찾아 내려가느라 높이만큼 읽었다.
        // 여기서는 그 키가 이미 있는지조차 확인하지 않는다. 그래서 put 이 옛 값을 못 돌려준다.
        //
        // delete 도 여기를 지난다. **지우는 것이 쓰는 것이다.** 그래서 삭제가 공간을 늘린다.
        //
        // 조건을 > 로 쓰면 임계보다 하나 더 담긴 뒤에 쏟는다. >= 로 쓰라.
        // 테스트가 "4개째에 SSTable 이 생긴다" 를 정확히 본다.
        throw new UnsupportedOperationException("TODO 6: write");
    }

    @Override
    public void put(K key, V value) {
        requireKey(key);
        if (value == null) {
            throw new IllegalArgumentException("값에 null 을 넣을 수 없다. 지우려면 delete 를 써라");
        }
        write(key, value);
    }

    @Override
    public void delete(K key) {
        requireKey(key);
        write(key, Tombstone.MARKER);
    }

    @Override
    public V get(K key) {
        requireKey(key);
        // TODO 7: 최신 층부터 차례로 뒤져 **처음 만난 것** 을 답으로 삼는다.
        //
        //   1. memtable 을 본다. null 이 아니면 그것이 답이다
        //   2. sstables 를 0번부터 차례로 본다
        //        - table.mightContain(key) 가 false 면 **뒤지지 않고 건너뛴다.**
        //          블룸 필터가 "확실히 없다" 고 한 것이다. diskReads 도 안 는다
        //        - 아니면 diskReads 를 하나 늘리고 table.rawValue(key) 를 본다
        //        - null 이 아니면 그것이 답이다
        //   3. 끝까지 못 찾으면 null
        //
        // rawValue 의 반환값 셋을 구별하라. **null 과 tombstone 이 다르다.**
        //   null              이 층에 그 키가 없다  -> 아래층을 더 본다
        //   Tombstone.MARKER  이 층에서 지워졌다    -> 여기서 멈추고 null 을 돌려준다
        //
        // tombstone 을 만났는데 아래층을 더 보면 **지운 키가 되살아난다.**
        // unwrap 이 그 변환을 해준다. 다만 unwrap 을 부르기 전에 "찾았는지" 를 먼저 판정하라.
        // 순서를 뒤집으면(오래된 층부터 보면) 컴파일도 되고 예외도 안 나고 옛 값이 나온다.
        // 이 문제에서 가장 조용한 버그다.
        throw new UnsupportedOperationException("TODO 7: get");
    }

    /** tombstone 이면 null, 아니면 값. */
    @SuppressWarnings("unchecked")
    private V unwrap(Object value) {
        return Tombstone.is(value) ? null : (V) value;
    }

    @Override
    public void flush() {
        // TODO 8: memtable 을 SSTable 로 굳힌다.
        //
        //   1. memtable 이 비었으면 아무것도 안 한다. 빈 SSTable 을 쌓으면 읽기만 느려진다
        //   2. memtable.entriesInOrder() 로 새 SSTable 을 만든다(bloomEnabled 를 넘긴다)
        //   3. **목록의 맨 앞(0번)** 에 넣는다. 방금 만든 것이 가장 최신이다
        //   4. sequentialBytesWritten 에 그 테이블의 byteSize 를 더한다
        //   5. flushCount 를 늘리고 memtable 을 비운다
        //
        // 3번에서 add(t) 를 쓰면 맨 뒤에 붙어 **가장 오래된 것이 된다.** 그러면 조회 순서가
        // 통째로 뒤집혀 옛 값이 나온다. add(0, t) 다.
        //
        // 5번의 clear 를 빠뜨리면 같은 데이터가 memtable 과 SSTable 에 둘 다 있게 된다.
        // 답은 당장 안 틀리는데(memtable 이 더 최신이니까) 다음 flush 때 또 굳혀서
        // 저장량이 계속 는다. storedEntryCount 를 보는 테스트가 그걸 잡는다.
        throw new UnsupportedOperationException("TODO 8: flush");
    }

    /**
     * 최신 count 장을 하나로 합친다.
     *
     * count 가 지금 가진 장수와 같으면 맨 아래층까지 합치는 것이므로 tombstone 을 버려도 된다.
     * 아니면 버리면 안 된다. 그 판정이 이 메서드의 핵심이다.
     */
    public void compactNewest(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("한 장 이상을 합쳐야 한다: " + count);
        }
        if (count > sstables.size()) {
            throw new IllegalArgumentException(
                    "SSTable 이 " + sstables.size() + " 장뿐인데 " + count + " 장을 합칠 수 없다");
        }
        // TODO 9: 앞에서 count 장을 떼어 하나로 합쳐 도로 앞에 넣는다.
        //
        //   1. 맨 아래층까지 합치는가? count == sstables.size() 면 그렇다
        //   2. 앞 count 장을 Compactor.compact(대상, 1번의 답, bloomEnabled) 에 넘긴다
        //   3. 앞 count 장을 목록에서 뺀다
        //   4. 합친 것이 비어 있지 않으면 맨 앞에 넣는다
        //      (전부 지워진 뒤 맨 아래까지 합치면 남는 것이 없다. 빈 장을 남길 이유가 없다)
        //   5. sequentialBytesWritten 에 합친 것의 byteSize 를 더하고 compactionCount 를 늘린다
        //
        // **1번이 이 박스에서 제일 위험한 판정이다.**
        // 아래에 아직 층이 남아 있는데 tombstone 을 버리면, 그 아래층의 옛 값이 되살아난다.
        // 삭제가 조용히 취소되는 것이고, 합친 직후에는 아무 증상이 없다.
        // TombstoneTest.Resurrection 이 그 장면을 그대로 재현한다.
        //
        // 2번에서 sstables.subList(0, count) 를 그대로 넘기면 3번에서 목록을 고치는 순간
        // 그 뷰가 함께 변한다. 새 목록으로 복사해서 넘겨라.
        //
        // 5번을 빠뜨리면 "compaction 은 공짜가 아니다" 라는 사실이 숫자에서 사라진다.
        // 살아 있는 데이터를 통째로 다시 쓴 것이므로 그 바이트는 실제로 쓴 바이트다.
        throw new UnsupportedOperationException("TODO 9: compactNewest");
    }

    @Override
    public void compact() {
        flush();
        if (sstables.isEmpty()) {
            return;
        }
        compactNewest(sstables.size());
    }

    /**
     * 모든 층을 훑어 살아 있는 것만 정렬 순서로 모은다.
     *
     * from 과 to 가 null 이면 그쪽 끝이 없는 것으로 본다.
     * size, keys, rangeScan 이 전부 이 하나에서 나온다.
     */
    private List<Map.Entry<K, V>> mergedLive(K from, K to) {
        // TODO 10: 층을 최신부터 훑어 병합한다. 같은 키는 **처음 본 것만** 채택한다.
        //
        //   1. TreeMap<K, Object> 를 하나 두고
        //   2. memtable 부터, 그 다음 sstables 를 0번부터 차례로 훑으며
        //   3. inRange 를 통과한 키를 **아직 없을 때만** 담는다(putIfAbsent)
        //   4. 다 담은 뒤 tombstone 인 것을 걸러내고 목록으로 만든다
        //
        // 3번의 "아직 없을 때만" 이 get 의 "처음 만난 것이 답" 과 같은 규칙이다.
        // put 을 쓰면 나중에 훑는 오래된 층이 이겨서 답이 뒤집힌다.
        //
        // 4번을 3번보다 먼저 하면 안 된다. **tombstone 도 일단 담아야 한다.**
        // 담지 않고 건너뛰면 그 아래층의 옛 값이 그 자리를 차지한다. 지운 키가 되살아난다.
        // 담아서 자리를 차지하게 한 다음, 마지막에 통째로 걷어내는 순서다.
        //
        // 이 메서드는 diskReads 를 세지 않는다. compaction 과 범위 조회는 층을 통째로
        // 훑는 순차 읽기라, 키 하나를 찾느라 뒤진 횟수와 성격이 다르기 때문이다.
        //
        // size() 가 이것을 부른다. **LSM 에서 개수 세기가 싼 연산이 아니라는 뜻이다.**
        throw new UnsupportedOperationException("TODO 10: mergedLive");
    }

    private boolean inRange(K key, K from, K to) {
        if (from != null && key.compareTo(from) < 0) {
            return false;
        }
        return to == null || key.compareTo(to) <= 0;
    }

    @Override
    public List<Map.Entry<K, V>> rangeScan(K from, K to) {
        requireKey(from);
        requireKey(to);
        return mergedLive(from, to);
    }

    @Override
    public List<K> keys() {
        List<K> out = new ArrayList<>();
        for (Map.Entry<K, V> e : mergedLive(null, null)) {
            out.add(e.getKey());
        }
        return out;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public int size() {
        return mergedLive(null, null).size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public int sstableCount() {
        return sstables.size();
    }

    @Override
    public long diskReads() {
        return diskReads;
    }

    @Override
    public long sequentialBytesWritten() {
        return sequentialBytesWritten;
    }

    @Override
    public long storedEntryCount() {
        long total = memtable.size();
        for (SSTable<K, V> table : sstables) {
            total += table.size();
        }
        return total;
    }

    @Override
    public double spaceAmplification() {
        return storedEntryCount() / (double) Math.max(1, size());
    }

    public int memtableSize() {
        return memtable.size();
    }

    public long flushCount() {
        return flushCount;
    }

    public long compactionCount() {
        return compactionCount;
    }

    public boolean bloomEnabled() {
        return bloomEnabled;
    }

    /** 0번이 가장 최신이다. 테스트가 층 하나를 직접 들여다볼 때 쓴다. */
    public SSTable<K, V> sstableAt(int index) {
        return sstables.get(index);
    }

    /** 측정 구간을 자를 때 쓴다. */
    public void resetDiskReads() {
        diskReads = 0;
    }

    private void requireKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("키에 null 을 쓸 수 없다");
        }
    }
}
