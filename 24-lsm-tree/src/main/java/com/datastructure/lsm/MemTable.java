package com.datastructure.lsm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 메모리에 있는 쓰기 버퍼. LSM 트리에서 모든 쓰기가 여기로 들어온다.
 *
 * <h2>왜 정렬 구조여야 하는가</h2>
 *
 * "그냥 해시맵이면 되지 않나" 가 여기서 제일 흔한 질문이다. 안 된다. 이유가 셋이다.
 *
 * 1. flush 가 그냥 훑어 쓰기가 된다. 해시맵이었다면 쏟기 직전에 정렬해야 하고,
 *    그러면 flush 마다 O(n log n) 이 붙는다. 정렬 구조는 넣을 때 조금씩 나눠 낸 셈이라
 *    쏟는 순간에는 낼 것이 없다.
 * 2. SSTable 이 정렬을 요구한다. 정렬돼 있어야 이진 탐색을 하고, 정렬돼 있어야 층끼리
 *    한 번 훑어 병합할 수 있다(Compactor). 정렬은 SSTable 의 전제 조건이지 취향이 아니다.
 * 3. rangeScan 이 memtable 도 훑어야 한다. 해시맵이면 범위 조회에서 전부를 봐야 한다.
 *
 * 실제 LSM 저장소들은 여기에 12번 스킵 리스트를 쓴다. 락 없이 동시 삽입이 되고
 * 노드마다 메모리를 나눠 잡아 크기 예측이 쉽기 때문이다.
 * 여기서는 그 부분이 이 문제의 주제가 아니라서 TreeMap 을 쓴다. 계약은 같다.
 *
 * <h2>값 자리에 Object 를 쓰는 이유</h2>
 *
 * 값이 V 일 수도 있고 Tombstone.MARKER 일 수도 있다. 삭제도 쓰기라서 같은 자리에 들어온다.
 */
public final class MemTable<K extends Comparable<K>, V> {

    private final TreeMap<K, Object> entries = new TreeMap<>();

    /** value 는 진짜 값이거나 Tombstone.MARKER 다. 같은 키가 오면 덮어쓴다. */
    public void put(K key, Object value) {
        entries.put(key, value);
    }

    /** 없으면 null. 있으면 값이거나 Tombstone.MARKER 다. 이 둘을 구별하는 것은 부르는 쪽 몫이다. */
    public Object get(K key) {
        return entries.get(key);
    }

    public boolean containsKey(K key) {
        return entries.containsKey(key);
    }

    /** 키 개수. flush 임계치를 이 값으로 잰다. 같은 키를 백 번 덮어써도 1 이다. */
    public int size() {
        return entries.size();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public void clear() {
        entries.clear();
    }

    /** 정렬 순서로 뽑아낸 목록. 이 목록이 그대로 SSTable 의 재료가 된다. */
    public List<Map.Entry<K, Object>> entriesInOrder() {
        // TODO 1: 담고 있는 것을 키 순서대로 목록에 담아 돌려준다.
        //
        //   SSTable.cell(key, value) 이 엔트리 한 칸을 만들어 준다.
        //   TreeMap 을 순회하면 이미 키 순서다. 정렬을 여기서 다시 할 일이 없다.
        //   그것이 memtable 을 정렬 구조로 잡은 이유다.
        //
        // **이 목록은 memtable 과 끊어져 있어야 한다.** flush 는 이 목록으로 SSTable 을
        // 만든 다음 memtable 을 비운다. 뽑아낸 것이 memtable 을 들여다보는 뷰라면
        // 그 SSTable 이 무엇을 담고 있는지 알 수 없게 된다.
        //
        // 반환 타입이 List<Map.Entry<K, Object>> 인 것을 보라. V 가 아니라 Object 다.
        // **tombstone 도 함께 굳혀야 하기 때문이다.** 삭제 표시를 여기서 걸러 버리면
        // 그 삭제가 아래층의 옛 값에 묻혀 사라진다.
        throw new UnsupportedOperationException("TODO 1: entriesInOrder");
    }
}
