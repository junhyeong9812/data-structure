package com.datastructure.lsm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 디스크에 한 번 쏟아진 정렬된 덩어리. Sorted String Table 의 준말이다.
 *
 * <h2>불변이다. 그것이 전부다</h2>
 *
 * 만들고 나면 절대 안 바뀐다. 키를 고치지도, 지우지도, 끼워 넣지도 않는다.
 * 새 값이 오면 새 SSTable 이 위에 쌓이고, 정리는 나중에 통째로 다시 쓰는 것(compaction) 으로 한다.
 *
 * 이 제약이 이 자료구조의 목적이다. 제자리를 안 고치므로 쓰기가 언제나 순차다.
 * 15번 B+트리는 잎을 찾아가 그 자리에 썼다. 그것이 임의 쓰기이고, 디스크에서 수십 배 비싸다.
 *
 * 코드에서 이 말은 "필드가 전부 final 이고 고치는 메서드가 없다" 는 뜻이다.
 * SSTableStructureTest 가 리플렉션으로 그것을 직접 확인한다. 필드 이름이 계약이므로
 * keys, values, bytes, bloom 이라는 이름을 바꾸지 마라.
 *
 * <h2>디스크는 흉내만 낸다</h2>
 *
 * 실제 파일을 쓰지 않는다. 대신 measurable 한 것만 남긴다.
 *
 *   byteSize()   순차로 쏟아낸 바이트 수. entryBytes 로 고정 규칙을 정해 결정적으로 센다
 *   rawValue()   이 한 번이 "디스크 읽기 한 번" 이다. 세는 것은 LsmTree 가 한다
 *
 * <h2>이진 탐색이 되는 이유</h2>
 *
 * 정렬돼 있기 때문이다. 정렬은 memtable 이 이미 해서 넘겨준다.
 * 생성자가 그 전제를 직접 검사한다. 안 지켜지면 조회가 조용히 틀리므로 일찍 터뜨린다.
 */
public final class SSTable<K extends Comparable<K>, V> {

    /** 엔트리 하나의 고정 머리. 키 길이, 값 길이, 삭제 여부를 담는 자리라고 치자. */
    static final int HEADER_BYTES = 8;

    private final Object[] keys;
    private final Object[] values;
    private final long bytes;
    private final TinyBloomFilter bloom;

    /**
     * 정렬된 엔트리 목록을 그대로 굳힌다. 값 자리에는 Tombstone.MARKER 가 들어올 수 있다.
     *
     * @param withBloom 필터를 붙일지. 붙이면 없는 키를 확인할 때 이 테이블을 건너뛸 수 있다
     */
    public SSTable(List<Map.Entry<K, Object>> sortedEntries, boolean withBloom) {
        if (sortedEntries == null) {
            throw new IllegalArgumentException("엔트리 목록이 null 이다");
        }
        // TODO 3: 목록을 두 개의 배열(keys, values) 로 굳히고, 바이트를 세고, 블룸을 채운다.
        //
        //   1. 길이 n 짜리 Object[] 를 둘 만든다. 제네릭 배열은 못 만드니 Object[] 다
        //   2. withBloom 이면 new TinyBloomFilter(Math.max(1, n), 0.01) 을 만든다
        //      (n 이 0 이면 생성자가 거부한다. 빈 SSTable 도 만들 수 있어야 한다)
        //   3. 목록을 훑으며 배열에 옮기고, entryBytes 를 더하고, 블룸에 키를 넣는다
        //   4. 마지막에 final 필드 넷을 한 번씩 대입한다
        //
        // **정렬 전제를 여기서 검사하라.** 바로 앞 키보다 크지 않으면 IllegalArgumentException 이다.
        // 같아도 안 된다. 한 테이블 안에 같은 키가 둘이면 이진 탐색이 어느 쪽을 줄지 알 수 없고,
        // 그 순간 "최신이 이긴다" 는 규칙이 깨진다.
        //
        // 이 검사를 빼도 정상 경로에서는 아무 일도 안 일어난다. memtable 과 Compactor 가
        // 정렬해서 주기 때문이다. 그런데 그 둘 중 하나가 틀리면 증상이 조회 결과로만 나타난다.
        // **틀린 곳에서 터지게 하려고 두는 검사다.**
        //
        // 그리고 목록을 그대로 들고 있지 말고 배열로 복사하라. 밖에서 그 목록을 고치면
        // 불변이라는 말이 거짓이 된다(copiesTheInput 이 그걸 본다).
        throw new UnsupportedOperationException("TODO 3: SSTable 생성자");
    }

    /** 키의 자리. 없으면 -1. */
    int indexOf(K key) {
        // TODO 4: 정렬된 keys 배열에서 이진 탐색한다.
        //
        //   lo, hi 를 양끝에 두고 mid 를 비교해 반씩 줄인다.
        //   비교는 compare(keys[mid], key) 를 쓴다. 배열이 Object[] 라 캐스팅이 필요한데
        //   그 부분은 아래 compare 에 이미 넣어뒀다.
        //
        // mid 를 (lo + hi) / 2 로 쓰지 마라. 배열이 커지면 lo + hi 가 int 를 넘친다.
        // (lo + hi) >>> 1 이 그것을 막는다. 01번 동적 배열에서 본 것과 같은 함정이다.
        //
        // Arrays.binarySearch 를 써도 된다. 다만 없는 키일 때 -(삽입점) - 1 을 돌려주므로
        // 그것을 -1 로 바꿔야 한다. 여기서 음수를 그대로 흘려보내면 rawValue 가
        // 배열 밖을 읽는다.
        throw new UnsupportedOperationException("TODO 4: indexOf");
    }

    @SuppressWarnings("unchecked")
    private static int compare(Object a, Object b) {
        return ((Comparable<Object>) a).compareTo(b);
    }

    /**
     * 엔트리 하나가 차지하는 바이트. 실제 직렬화 대신 쓰는 고정 규칙이다.
     * 결정적이어야 측정이 되므로 시간이 아니라 이런 셈을 센다.
     */
    public static long entryBytes(Object key, Object value) {
        long k = String.valueOf(key).length();
        long v = Tombstone.is(value) ? 0 : String.valueOf(value).length();
        return HEADER_BYTES + k + v;
    }

    /** 엔트리 한 칸을 만든다. 값 자리에 Tombstone.MARKER 를 넣어도 된다. */
    public static <K extends Comparable<K>> Map.Entry<K, Object> cell(K key, Object value) {
        return Map.entry(key, value);
    }

    /**
     * 이 테이블에서 키를 찾는다. 디스크 읽기 한 번에 해당한다.
     *
     * 반환값이 셋이라는 것이 중요하다.
     *   null                 이 테이블에는 그 키가 없다. 아래층을 더 봐야 한다
     *   Tombstone.MARKER     이 테이블에서 지워졌다. 아래층을 보면 안 된다
     *   그 외                 값이다
     */
    public Object rawValue(K key) {
        int idx = indexOf(key);
        return idx < 0 ? null : values[idx];
    }

    /** 필터가 없으면 언제나 true. 있으면 false 일 때만 확실히 없다. */
    public boolean mightContain(K key) {
        return bloom == null || bloom.mightContain(key);
    }

    public boolean hasBloom() {
        return bloom != null;
    }

    public int size() {
        return keys.length;
    }

    public long byteSize() {
        return bytes;
    }

    @SuppressWarnings("unchecked")
    public K keyAt(int index) {
        return (K) keys[index];
    }

    public Object valueAt(int index) {
        return values[index];
    }

    public boolean isTombstoneAt(int index) {
        return Tombstone.is(values[index]);
    }

    /** 전부, 정렬 순서로. 매번 새 목록이다. 내부 배열을 밖으로 내보내면 불변이 아니다. */
    public List<Map.Entry<K, Object>> entries() {
        List<Map.Entry<K, Object>> out = new ArrayList<>(keys.length);
        for (int i = 0; i < keys.length; i++) {
            out.add(cell(keyAt(i), values[i]));
        }
        return out;
    }

    public int tombstoneCount() {
        int c = 0;
        for (Object v : values) {
            if (Tombstone.is(v)) {
                c++;
            }
        }
        return c;
    }
}
