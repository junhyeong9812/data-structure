package com.datastructure.lsm;

import java.util.List;
import java.util.Map;

/**
 * 키-값 저장소. 15번 SearchTree 와 담는 것은 같고, 거래가 정반대다.
 *
 * <h2>15번이 잘하던 것</h2>
 *
 * B+트리는 읽기에 최적화되어 있다. 높이가 4면 디스크를 4번 읽고 끝난다.
 * 그런데 쓰기를 보라. 키 하나를 넣으려면 그 키가 들어갈 잎을 찾아가 그 자리에 써야 한다.
 * 디스크에서 그것은 임의 쓰기(random write) 다.
 *
 * 디스크는 순차 쓰기가 임의 쓰기보다 수십에서 수백 배 빠르다.
 * SSD 도 그렇다. 읽기와 쓰기의 단위(페이지)보다 지우기의 단위(블록)가 훨씬 커서,
 * 제자리를 고치려면 블록을 통째로 옮겨 쓰고 지워야 한다.
 *
 * 그래서 쓰기가 압도적으로 많은 워크로드(로그, 시계열, 이벤트 스트림)에서는 B+트리가 무너진다.
 *
 * <h2>LSM 트리가 뒤집는 것</h2>
 *
 *   쓰기   메모리의 정렬 구조(memtable) 에 넣는다. 디스크를 안 건드린다.
 *          차면 통째로 디스크에 순차로 쏟는다(SSTable). 절대 제자리를 안 고친다
 *   읽기   memtable 부터 최신 SSTable 까지 차례로 뒤진다. 여러 번 봐야 한다
 *   삭제   지우지 않는다. "지웠다"는 표시(tombstone) 를 새로 쓴다
 *   정리   쌓인 SSTable 을 주기적으로 합친다(compaction)
 *
 * | | B+트리 (15) | LSM 트리 (24) |
 * |---|---|---|
 * | 쓰기 | 임의 쓰기 | 순차 쓰기만 |
 * | 읽기 | O(log n) 한 번 | SSTable 수만큼 |
 * | 삭제 | 즉시 | tombstone, 나중에 정리 |
 * | 공간 증폭 | 낮다 | 높다 (같은 키의 옛 버전들) |
 * | 쓰는 곳 | MySQL, PostgreSQL | RocksDB, 카산드라, LevelDB, HBase |
 *
 * 공짜가 없다. 쓰기를 순차로 만든 대가를 읽기와 공간이 낸다.
 * 그 대가를 줄이려고 블룸 필터(11번) 와 compaction 이 붙는다.
 *
 * <h2>put 이 옛 값을 안 돌려준다</h2>
 *
 * 15번 SearchTree 의 put 은 옛 값을 반환했다. 여기서는 반환하지 않는다.
 * 옛 값을 돌려주려면 먼저 읽어야 하고, 읽지 않는 것이 이 구조의 전부이기 때문이다.
 * RocksDB 도 카산드라도 Put 은 값을 돌려주지 않는다. 시그니처가 이미 설계를 말한다.
 *
 * 이 인터페이스에는 TODO 가 없다. 계약은 주어지는 것이다.
 */
public interface KeyValueStore<K extends Comparable<K>, V> {

    /**
     * memtable 에 쓴다. 옛 값을 읽지 않으므로 반환값도 없다.
     * memtable 이 임계치에 닿으면 그 자리에서 flush 가 일어난다.
     */
    void put(K key, V value);

    /** 없거나 지워졌으면 null. 값에 null 을 넣을 수 없으므로 null 은 언제나 "없다" 는 뜻이다. */
    V get(K key);

    /** tombstone 을 쓴다. 지우는 것도 쓰는 것이다. */
    void delete(K key);

    boolean containsKey(K key);

    /**
     * 살아 있는 키의 수.
     *
     * 이 연산이 싸지 않다는 것이 LSM 의 성질이다. 층마다 같은 키의 옛 버전과 tombstone 이
     * 흩어져 있어서, 정확한 개수를 알려면 전부 훑어 병합해야 한다.
     * (그래서 실제 LSM 저장소들은 정확한 count 를 API 로 잘 주지 않는다)
     */
    int size();

    boolean isEmpty();

    /** 살아 있는 키 전부, 정렬 순서로. */
    List<K> keys();

    /** from 이상 to 이하. 양끝 포함. 최신 버전만, 정렬 순서로. */
    List<Map.Entry<K, V>> rangeScan(K from, K to);

    /** memtable 을 SSTable 로 굳힌다. 비어 있으면 아무 일도 없다. */
    void flush();

    /** memtable 을 굳힌 뒤 모든 SSTable 을 하나로 합친다. tombstone 이 여기서 사라진다. */
    void compact();

    int sstableCount();

    /**
     * get 이 SSTable 을 몇 번 뒤졌나.
     *
     * memtable 조회는 메모리라 세지 않는다. 블룸 필터가 "없다" 고 해서 건너뛴 것도 세지 않는다.
     * 그 둘의 차이가 이 문제의 측정 대상이다.
     */
    long diskReads();

    /** flush 와 compaction 이 순차로 쏟아낸 바이트의 누적. 제자리 고쳐쓰기는 한 번도 없다. */
    long sequentialBytesWritten();

    /** memtable 과 모든 SSTable 에 실제로 들어 있는 엔트리 수. 옛 버전과 tombstone 을 포함한다. */
    long storedEntryCount();

    /** storedEntryCount / size. 살아 있는 키 하나당 몇 개를 들고 있나. 산 키가 없으면 분모를 1 로 본다. */
    double spaceAmplification();
}
