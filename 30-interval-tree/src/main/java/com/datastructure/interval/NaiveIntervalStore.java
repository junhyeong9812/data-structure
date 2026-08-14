package com.datastructure.interval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 전수 조사 기준선. 리스트에 담고 매번 다 훑는다.
 *
 * 이 클래스가 쉽다는 것이 요점이다. 인터벌 트리가 하는 일은 이것과 답이 같아야 한다.
 * 가지치기는 "볼 필요 없는 곳을 안 본다"인데, 조건을 하나만 틀리게 쓰면
 * "봐야 하는 곳을 안 본다"가 된다. 그러면 예외도 안 나고 조용히 몇 개가 빠진다.
 * 그 조용한 누락을 잡는 유일한 방법이 이 느린 구현과의 대조다.
 */
public class NaiveIntervalStore implements IntervalStore, VisitCounting {

    private final List<Interval> intervals = new ArrayList<>();
    private long visitedNodes;

    @Override
    public boolean insert(Interval iv) {
        if (iv == null) {
            throw new IllegalArgumentException("구간이 null 이다");
        }
        if (intervals.contains(iv)) {
            return false;
        }
        intervals.add(iv);
        return true;
    }

    @Override
    public boolean remove(Interval iv) {
        if (iv == null) {
            throw new IllegalArgumentException("구간이 null 이다");
        }
        return intervals.remove(iv);
    }

    @Override
    public int size() {
        return intervals.size();
    }

    @Override
    public void clear() {
        intervals.clear();
    }

    @Override
    public Interval findAny(Interval query) {
        if (query == null) {
            throw new IllegalArgumentException("질의 구간이 null 이다");
        }
        visitedNodes = 0;
        for (Interval iv : intervals) {
            visitedNodes++;
            if (iv.overlaps(query)) {
                return iv;
            }
        }
        return null;
    }

    /**
     * 겹치는 것 전부. 전부 훑는다.
     *
     * TODO 3: 구현하라. 하나 볼 때마다 visitedNodes 를 하나 올린다.
     *
     * 이 값이 이 문제의 자 역할을 한다. findAll 은 언제나 정확히 n 이 나와야 하고,
     * 트리가 그보다 훨씬 적게 보는지가 이 박스의 주장이다. 세는 것을 빠뜨리면 잴 자가 없어진다.
     */
    @Override
    public List<Interval> findAll(Interval query) {
        if (query == null) {
            throw new IllegalArgumentException("질의 구간이 null 이다");
        }
        visitedNodes = 0;
        throw new UnsupportedOperationException("TODO 3: findAll");
    }

    @Override
    public boolean anyOverlaps(Interval query) {
        return findAny(query) != null;
    }

    @Override
    public List<Interval> toList() {
        List<Interval> out = new ArrayList<>(intervals);
        Collections.sort(out);
        return out;
    }

    @Override
    public long visitedNodes() {
        return visitedNodes;
    }
}
