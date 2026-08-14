package com.datastructure.cache;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 여러 스레드가 동시에 써도 되는 LRU 캐시. 다른 캐시를 감싼다.
 *
 * 왜 ReadWriteLock 을 안 쓰는가. 이 문제의 전부가 여기 있다.
 *
 * 보통 캐시라면 읽기가 압도적으로 많으니 읽기끼리는 동시에 통과시키는 것이 맞다.
 * 그런데 LRU 의 get 은 읽기가 아니다. 값을 꺼내면서 그 키를 줄 맨 뒤로 옮긴다.
 * 연결 리스트의 링크 네 개를 고쳐 쓴다.
 *
 * 그걸 읽기 잠금으로 열어주면 두 스레드가 같은 노드를 동시에 옮기다가
 * 줄이 끊기거나 고리가 생긴다. 그리고 그 손상은 다음 순회에서야 드러난다.
 * 무한 루프로 나타나는 경우가 많아 원인을 찾기가 특히 나쁘다.
 *
 * 그래서 배타 잠금 하나로 간다. 읽기 성능을 포기하는 것이 아니라,
 * 애초에 읽기 연산이 없다는 사실을 인정하는 것이다.
 *
 * (진짜로 읽기를 동시에 통과시키고 싶으면 순서 갱신을 미루는 설계로 가야 한다.
 *  Caffeine 이 그렇게 한다. 접근 기록을 버퍼에 쌓아두고 나중에 몰아서 반영한다.)
 *
 * 나머지 메서드는 채워뒀다. 같은 형태가 반복되는 것 자체가 이 클래스의 내용이다.
 */
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
        // TODO 1: 잠그고 delegate.get 을 부른다.
        //
        // 반드시 try/finally 다. delegate 가 예외를 던져도 잠금은 풀려야 한다.
        // 안 그러면 그 뒤의 모든 스레드가 영원히 멈춘다.
        // **한 번의 예외가 프로세스 전체를 세우는** 종류의 버그다.
        throw new UnsupportedOperationException("TODO 1: get");
    }

    @Override
    public void put(K key, V value) {
        // TODO 2: 같은 방식으로.
        throw new UnsupportedOperationException("TODO 2: put");
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
            // delegate 가 새 리스트를 만들어 준다. 그 복사가 잠금 **안에서** 일어나야 한다.
            // 밖에서 하면 훑는 도중에 줄이 바뀐다.
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
        // 생성 후 바뀌지 않는 값이라 잠글 필요가 없다.
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
