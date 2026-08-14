package com.datastructure.persistent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

/**
 * 영속 자료구조로 푸는 문제 둘.
 */
public final class PersistentProblems {

    private PersistentProblems() {
    }

    /**
     * 문제 1: 명령을 하나씩 실행하며 매 시점의 맵을 전부 남긴다.
     * 결과의 0번은 아무것도 실행하기 전, i+1번은 i번 명령을 실행한 뒤다.
     *
     * 명령은 두 가지다.
     * <pre>
     *   {"put", 키, 값}    값은 정수 문자열
     *   {"remove", 키}
     * </pre>
     * 그 밖의 것, 칸 수가 안 맞는 것, 빈 명령은 IllegalArgumentException 이다.
     *
     * 가변 맵으로 이 일을 하면 스냅샷마다 맵을 통째로 복사해야 해서 O(m n) 이다.
     * 여기서는 O(m log n) 이다. 그 차이를 테스트가 노드 수로 잰다.
     */
    public static List<PersistentMap<String, Integer>> replay(List<String[]> commands) {
        if (commands == null) {
            throw new IllegalArgumentException("명령 목록이 필요하다");
        }
        // TODO 9: 빈 맵에서 시작해 명령을 하나씩 적용하고, 매번 결과를 리스트에 담는다.
        //
        //   0번 스냅샷은 아무 명령도 실행하기 전 상태다. 루프 앞에서 한 번 담는다.
        //   그래서 결과 길이가 commands.size() + 1 이다. **이 +1 이 흔한 실수다.**
        //
        // 여기서 아무 데도 복사가 없다는 것을 보라. put 이 돌려준 참조를 리스트에 넣을 뿐이다.
        // 가변 맵이었다면 이 자리에 new TreeMap<>(map) 이 있어야 하고, 그 한 줄이 O(n) 이다.
        // 스냅샷 1124개를 남길 때 그 차이가 노드 10,317개 대 631,126개다.
        //
        // 값은 Integer.parseInt 로 읽는다. 숫자가 아니면 NumberFormatException 이 나는데
        // 그것도 IllegalArgumentException 이므로 따로 잡을 필요가 없다.
        // switch 의 default 를 빼먹지 마라. 모르는 명령을 조용히 넘기면
        // 그 시점 스냅샷이 하나 어긋난 채로 뒤가 전부 밀린다.
        throw new UnsupportedOperationException("TODO 9: replay");
    }

    /**
     * 문제 2: 두 버전이 실제로 공유하는 노드의 수.
     *
     * 값이 같은 것이 아니라 같은 객체인 것만 센다.
     * 1000개짜리 맵에 키 하나를 넣으면 990개가 넘게 나와야 한다.
     */
    public static <K extends Comparable<K>, V> long countSharedNodes(
            PersistentTreeMap<K, V> before, PersistentTreeMap<K, V> after) {
        if (before == null || after == null) {
            throw new IllegalArgumentException("두 버전이 모두 필요하다");
        }
        // TODO 10: 두 걸음이다.
        //
        //   1. before 의 노드를 전부 모아 **참조 동일성** 집합에 담는다
        //   2. after 를 훑으며 그 집합에 있는 노드를 센다
        //
        // 1번에서 HashSet 을 써도 **테스트는 전부 통과한다.** Node 가 equals 를 정의하지 않아
        // 지금은 참조 비교가 되기 때문이다. 그래도 IdentityHashMap 을 쓴다.
        // 나중에 누가 Node 에 equals 를 붙이면 그날부터 값이 같은 남의 노드를
        // 공유했다고 세기 시작하고, 그 버그는 아무 테스트도 잡지 못한다.
        // 우연히 맞는 코드와 뜻을 적은 코드의 차이다.
        //
        //   Set<...> s = Collections.newSetFromMap(new IdentityHashMap<>());
        //
        // 2번에는 지름길이 있다. **공유하는 노드를 만나면 그 아래는 통째로 공유다.**
        // 노드가 불변이라 자식이 바뀔 수 없기 때문이다. 그러니 더 내려가지 말고
        // node.size 를 더하고 멈춘다. 그러면 이 순회가 O(바뀐 경로) 다.
        // 끝까지 내려가도 답은 같다. 왜 같은지 설명할 수 있으면 이 자료구조를 이해한 것이다.
        //
        // 두 인자가 아무 관계 없는 맵이면 0 이 나온다. 그것이 통째 복사 구현의 결과다.
        throw new UnsupportedOperationException("TODO 10: countSharedNodes");
    }
}
