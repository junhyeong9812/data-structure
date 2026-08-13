package com.datastructure.cache;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeLRUCache<K, V> implements Cache<K, V> {

    private final Cache<K, V> delegate;
    private final ReentrantLock lock = new ReentrantLock();

    public ThreadSafeLRUCache(int capacity) {
        this(new LRUCache<>(capacity));
    }

    public ThreadSafeLRUCache(Cache<K, V> delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("감쌀 캐시가 필요하다");
        }
        this.delegate = delegate;
    }

    @Override
    public V get(K key) {
        lock.lock();
        try {
            return delegate.get(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void put(K key, V value) {
        lock.lock();
        try {
            delegate.put(key, value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V remove(K key) {
        lock.lock();
        try {
            return delegate.remove(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsKey(K key) {
        lock.lock();
        try {
            return delegate.containsKey(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<K> keysInOrder() {
        lock.lock();
        try {
            return delegate.keysInOrder();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        lock.lock();
        try {
            return delegate.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        lock.lock();
        try {
            return delegate.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        lock.lock();
        try {
            delegate.clear();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int capacity() {
        return delegate.capacity();
    }

    @Override
    public long hits() {
        lock.lock();
        try {
            return delegate.hits();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long misses() {
        lock.lock();
        try {
            return delegate.misses();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long evictions() {
        lock.lock();
        try {
            return delegate.evictions();
        } finally {
            lock.unlock();
        }
    }
}
