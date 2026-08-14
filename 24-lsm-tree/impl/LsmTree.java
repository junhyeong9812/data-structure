package com.datastructure.lsm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LsmTree<K extends Comparable<K>, V> implements KeyValueStore<K, V> {

    private final int memtableThreshold;
    private final boolean bloomEnabled;
    private final MemTable<K, V> memtable = new MemTable<>();
    private final List<SSTable<K, V>> sstables = new ArrayList<>();

    private long diskReads;
    private long sequentialBytesWritten;
    private long flushCount;
    private long compactionCount;

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

    private void write(K key, Object value) {
        memtable.put(key, value);
        if (memtable.size() >= memtableThreshold) {
            flush();
        }
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
        Object inMemory = memtable.get(key);
        if (inMemory != null) {
            return unwrap(inMemory);
        }
        for (SSTable<K, V> table : sstables) {
            if (!table.mightContain(key)) {
                continue;
            }
            diskReads++;
            Object found = table.rawValue(key);
            if (found != null) {
                return unwrap(found);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private V unwrap(Object value) {
        return Tombstone.is(value) ? null : (V) value;
    }

    @Override
    public void flush() {
        if (memtable.isEmpty()) {
            return;
        }
        SSTable<K, V> frozen = new SSTable<>(memtable.entriesInOrder(), bloomEnabled);
        sstables.add(0, frozen);
        sequentialBytesWritten += frozen.byteSize();
        flushCount++;
        memtable.clear();
    }

    public void compactNewest(int count) {
        if (count < 1) {
            throw new IllegalArgumentException("한 장 이상을 합쳐야 한다: " + count);
        }
        if (count > sstables.size()) {
            throw new IllegalArgumentException(
                    "SSTable 이 " + sstables.size() + " 장뿐인데 " + count + " 장을 합칠 수 없다");
        }
        boolean bottommost = count == sstables.size();
        List<SSTable<K, V>> target = new ArrayList<>(sstables.subList(0, count));
        SSTable<K, V> merged = Compactor.compact(target, bottommost, bloomEnabled);
        sstables.subList(0, count).clear();
        if (merged.size() > 0) {
            sstables.add(0, merged);
        }
        sequentialBytesWritten += merged.byteSize();
        compactionCount++;
    }

    @Override
    public void compact() {
        flush();
        if (sstables.isEmpty()) {
            return;
        }
        compactNewest(sstables.size());
    }

    private List<Map.Entry<K, V>> mergedLive(K from, K to) {
        TreeMap<K, Object> newest = new TreeMap<>();
        for (Map.Entry<K, Object> e : memtable.entriesInOrder()) {
            if (inRange(e.getKey(), from, to)) {
                newest.putIfAbsent(e.getKey(), e.getValue());
            }
        }
        for (SSTable<K, V> table : sstables) {
            for (int i = 0; i < table.size(); i++) {
                K key = table.keyAt(i);
                if (inRange(key, from, to)) {
                    newest.putIfAbsent(key, table.valueAt(i));
                }
            }
        }
        List<Map.Entry<K, V>> out = new ArrayList<>();
        for (Map.Entry<K, Object> e : newest.entrySet()) {
            if (!Tombstone.is(e.getValue())) {
                out.add(Map.entry(e.getKey(), unwrap(e.getValue())));
            }
        }
        return out;
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

    public SSTable<K, V> sstableAt(int index) {
        return sstables.get(index);
    }

    public void resetDiskReads() {
        diskReads = 0;
    }

    private void requireKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("키에 null 을 쓸 수 없다");
        }
    }
}
