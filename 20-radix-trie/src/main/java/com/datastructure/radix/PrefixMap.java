package com.datastructure.radix;

import java.util.List;

/**
 * 문자열 키 맵. 접두사 질의를 지원한다.
 *
 * 09번 트라이는 한 글자에 노드 하나였다. "internationalization" 을 넣으면 노드가 20개다.
 * 그 20개 중 갈림길은 하나도 없다. 자식이 하나뿐인 노드는 "여기서 갈라진다"는 정보를
 * 담지 않는다. 다음 글자가 무엇인지만 말해줄 뿐이고, 그건 문자열에 이미 적혀 있다.
 *
 * 압축 트라이는 그 사슬을 간선 하나에 문자열 조각으로 눌러 담는다.
 * 노드 수가 글자 수가 아니라 키 수에 비례하게 된다(키 n 개면 최대 2n-1 개).
 *
 * | | 09번 트라이 | 압축 트라이 |
 * |---|---|---|
 * | 노드 수 | 서로 다른 접두사 개수 | 최대 2n-1 |
 * | 노드당 비교 | 문자 하나 | 문자열 조각 하나 |
 * | 깊이 | 키 길이 | 갈림길 수 |
 * | 삽입 | 없는 자식을 만든다 | 간선을 쪼갠다 |
 *
 * 공짜가 아니다. 노드는 줄지만 노드당 하는 일이 늘어난다.
 * 간선마다 문자열 비교가 붙고, 삽입은 간선을 잘라야 하고, 삭제는 다시 합쳐야 한다.
 *
 * 여기 09번에 없던 연산이 하나 더 있다. longestPrefixOf 다.
 * "이 문자열의 접두사이면서 맵에 들어 있는 키 중 가장 긴 것".
 * 라우팅 테이블과 URL 라우터가 이것 하나로 돌아간다.
 *
 * 값에 null 을 허용하지 않는다. 허용하면 get 이 null 을 줬을 때
 * "키가 없다"와 "값이 null 이다"를 구별할 수 없다. 05번 해시맵에서 본 것과 같은 결정이다.
 *
 * 이 인터페이스에는 TODO 가 없다. 계약은 주어지는 것이다.
 */
public interface PrefixMap<V> {

    /**
     * 키에 값을 넣는다. 이미 있던 키면 값을 바꾸고 예전 값을 준다. 처음이면 null.
     *
     * 빈 문자열도 유효한 키다(모든 문자열의 접두사이므로 뿌리가 담당한다).
     * key 가 null 이거나 value 가 null 이면 IllegalArgumentException.
     */
    V put(String key, V value);

    /** 이 키의 값. 없으면 null. */
    V get(String key);

    /** 정확히 이 키가 있는가. "app" 이 있어도 "appl" 은 false 다. */
    boolean containsKey(String key);

    /** 지웠으면 그 값, 없었으면 null. */
    V remove(String key);

    /** 들어 있는 키 수. */
    int size();

    boolean isEmpty();

    void clear();

    /** 모든 키를 사전순으로. */
    List<String> keys();

    /**
     * 이 접두사로 시작하는 키를 사전순으로 전부.
     * 접두사 자신이 키이기도 하면 포함한다. 없으면 빈 리스트.
     *
     * 압축 트라이에서 이게 09번보다 까다롭다. 접두사가 간선 중간에서 끝날 수 있다.
     * "romane" 만 들어 있을 때 "rom" 으로 물으면 멈출 노드가 아예 없다.
     */
    List<String> keysWithPrefix(String prefix);

    /**
     * 이 접두사로 시작하는 키 수.
     *
     * keysWithPrefix(p).size() 와 답은 같다. 다만 비용이 달라야 한다.
     * 09번과 같은 이유로 이 메서드가 따로 있다.
     */
    int countWithPrefix(String prefix);

    /**
     * s 의 접두사이면서 이 맵에 들어 있는 키 중 가장 긴 것. 없으면 null.
     *
     * s 자신이 키면 s 를 준다. 빈 문자열이 키로 들어 있으면 무엇을 물어도 최소한 "" 은 나온다.
     * 그게 라우팅 테이블의 기본 경로(0.0.0.0/0)다.
     *
     * containsKey 와 뭐가 다른가. containsKey 는 "정확히 이것"을 묻고
     * 이건 "이것을 덮는 가장 구체적인 규칙" 을 묻는다.
     */
    String longestPrefixOf(String s);
}
