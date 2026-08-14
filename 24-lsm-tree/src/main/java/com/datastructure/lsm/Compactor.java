package com.datastructure.lsm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 쌓인 SSTable 을 합친다.
 *
 * <h2>왜 필요한가</h2>
 *
 * 쓰기를 순차로 만든 값을 두 곳에서 낸다.
 *
 *   읽기 증폭   층이 10개면 없는 키 하나를 확인하는 데 10번 뒤진다
 *   공간 증폭   같은 키의 옛 버전과 tombstone 이 층마다 남아 있다
 *
 * compaction 이 그 둘을 갚는다. 여러 장을 한 번 훑어 한 장으로 만들면서
 * 같은 키의 옛 버전을 버린다. 그리고 그 대가로 세 번째 것이 생긴다.
 *
 *   쓰기 증폭   합칠 때마다 살아 있는 데이터를 통째로 다시 쓴다
 *
 * LSM 튜닝은 이 셋의 삼각형이고, 셋을 동시에 줄일 수는 없다.
 * 자주 합치면 읽기와 공간이 좋아지고 쓰기가 는다. 안 합치면 반대다.
 *
 * <h2>합치는 방향</h2>
 *
 * 목록의 0번이 가장 최신이다. 같은 키를 만나면 앞에 있는 것이 이긴다.
 * 이 한 줄이 정확성의 전부다. 뒤집으면 컴파일도 되고 예외도 안 나고 옛 값이 나온다.
 */
public final class Compactor {

    private Compactor() {
    }

    /**
     * 여러 장을 정렬 순서로 합친 엔트리 목록.
     *
     * @param newestFirst    0번이 가장 최신인 SSTable 목록
     * @param dropTombstones tombstone 을 버릴지. 맨 아래층까지 합칠 때만 true 여야 한다
     */
    public static <K extends Comparable<K>, V> List<Map.Entry<K, Object>> mergeEntries(
            List<SSTable<K, V>> newestFirst, boolean dropTombstones) {
        // TODO 5: k 개의 정렬된 목록을 한 번 훑어 하나로 합친다.
        //
        // 테이블마다 커서를 하나씩 두고 이것을 반복한다.
        //
        //   1. 커서가 가리키는 키들 중 **가장 작은 키** 를 고른다
        //   2. 그 키를 가진 테이블이 여럿이면 **가장 최신(인덱스가 작은) 것의 값** 을 쓴다
        //   3. 그 키를 가진 커서를 **전부** 한 칸 전진시킨다. 옛 버전은 버려진다
        //   4. dropTombstones 이고 고른 값이 tombstone 이면 결과에 안 넣는다
        //   5. 커서가 전부 끝에 닿을 때까지 되풀이한다
        //
        // 2번을 위해 앞에서부터 훑으면서 **더 작을 때만** 갱신하라. 같을 때도 갱신하면
        // 마지막(= 가장 오래된) 테이블이 이겨서 옛 값이 살아난다.
        //
        // 3번을 빠뜨리면 같은 키가 결과에 두 번 들어간다. SSTable 생성자가 그때 터진다.
        // (일부러 그러라고 그 검사를 넣어뒀다)
        //
        // 힙을 쓰면 O(n log k) 지만 여기서는 k 가 작으므로 매번 훑어도 된다.
        // 요점은 **입력을 한 번씩만 지나간다** 는 것이다. 그래서 합치는 것도 순차 읽기, 순차 쓰기다.
        // 통째로 TreeMap 에 부어도 답은 같지만, 그러면 합치는 동안 전부를 메모리에 올려야 한다.
        // 실제 LSM 이 테라바이트를 합칠 수 있는 이유가 이 훑기다.
        //
        // 4번은 조건이 하나 더 있다. **dropTombstones 을 함부로 true 로 주면 안 된다.**
        // 아래에 아직 옛 값이 남아 있는데 삭제 표시를 지우면 그 삭제가 없던 일이 된다.
        // 그 함정은 TombstoneTest 가 실제로 재현해 보여준다.
        throw new UnsupportedOperationException("TODO 5: mergeEntries");
    }

    /** 합친 결과로 새 SSTable 을 만든다. 옛 장들은 부르는 쪽에서 버린다. */
    public static <K extends Comparable<K>, V> SSTable<K, V> compact(
            List<SSTable<K, V>> newestFirst, boolean dropTombstones, boolean withBloom) {
        if (newestFirst == null || newestFirst.isEmpty()) {
            throw new IllegalArgumentException("합칠 SSTable 이 없다");
        }
        return new SSTable<>(mergeEntries(newestFirst, dropTombstones), withBloom);
    }
}
