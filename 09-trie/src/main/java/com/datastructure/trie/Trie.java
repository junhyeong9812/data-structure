package com.datastructure.trie;

import java.util.List;

/**
 * 문자열 집합. 접두사를 공유해서 저장한다.
 *
 * 05번 해시맵은 키를 일부러 흩뿌렸다. 그래서 O(1) 을 얻었고 순서를 잃었다.
 * 06번 BST 는 순서를 되찾았다. "5 다음", "3 이상 8 이하"를 물을 수 있게 됐다.
 *
 * 그런데 둘 다 못 하는 질문이 있다. **"car 로 시작하는 것 전부"**.
 * 해시맵은 car 와 card 를 전혀 다른 버킷에 넣으니 아예 불가능하다.
 * BST 는 정렬돼 있어 범위로 흉내낼 수는 있지만, 비교마다 문자열 전체를 훑는다.
 *
 * 트라이는 **키를 통째로 저장하지 않는다.** 한 글자를 간선 하나로 쓰고, 경로가 곧 키다.
 * car 와 card 와 care 는 c-a-r 을 공유한다.
 *
 * | 질문 | 해시맵 | BST | 트라이 |
 * |---|---|---|---|
 * | contains(w) | **O(1)** 평균 | O(log n * L) | O(L) |
 * | "car 로 시작하는 것 전부" | O(n) 전수 | O(log n * L + k) | **O(L + 출력)** |
 * | 공통 접두사 | O(n * L) | O(n * L) | **O(L)** |
 * | 정렬 순회 | O(n log n) | **O(n)** | O(n) |
 *
 * L 은 문자열 길이다. **n 이 아무리 커져도 트라이의 조회는 L 에만 의존한다.**
 * 100만 개가 들어 있어도 "abc" 를 찾는 비용은 세 걸음이다.
 *
 * 대가는 메모리다. 노드마다 자식 표를 들고 있어야 한다.
 * 그 표를 어떻게 잡느냐가 두 구현의 차이다.
 *
 *   MapTrie    자식을 맵으로 - 어떤 문자든 되고, 있는 만큼만 쓴다
 *   ArrayTrie  자식을 26칸 배열로 - a~z 만 되고, 비어 있어도 26칸을 쓴다
 *
 * 이 인터페이스에는 TODO 가 없다. 계약은 주어지는 것이다.
 */
public interface Trie {

    /**
     * 단어를 넣는다. 이미 있으면 아무 일도 없다.
     *
     * 빈 문자열도 유효한 단어다(모든 문자열의 접두사이므로 뿌리 노드가 담당한다).
     * null 은 IllegalArgumentException.
     */
    void insert(String word);

    /** 정확히 이 단어가 들어 있는가. "app" 이 있어도 "appl" 은 false 다. */
    boolean contains(String word);

    /** 이 접두사로 시작하는 단어가 하나라도 있는가. "apple" 만 있어도 "appl" 은 true 다. */
    boolean startsWith(String prefix);

    /** 지웠으면 true, 없어서 못 지웠으면 false. */
    boolean remove(String word);

    /** 들어 있는 단어 수. */
    int size();

    boolean isEmpty();

    void clear();

    /**
     * 이 접두사로 시작하는 단어를 **사전순으로** 전부.
     * 접두사 자신도 단어라면 포함한다. 없으면 빈 리스트.
     */
    List<String> keysWithPrefix(String prefix);

    /**
     * 이 접두사로 시작하는 단어 수.
     *
     * keysWithPrefix(p).size() 와 답은 같다. 다만 **비용이 달라야 한다.**
     * 왜 이 메서드가 따로 있는지는 성능 테스트가 말해준다.
     */
    int countWithPrefix(String prefix);
}
