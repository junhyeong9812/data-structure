package com.datastructure.trie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 자식을 맵으로 들고 있는 트라이.
 *
 * 어떤 문자든 담을 수 있다. 한글도, 이모지도, 숫자도.
 * 그리고 실제로 있는 자식만큼만 메모리를 쓴다.
 *
 * 대신 한 글자 내려갈 때마다 맵 조회 비용이 붙고, 노드마다 맵 객체가 하나씩 생긴다.
 * ArrayTrie 와 비교해보면 그 차이가 숫자로 보인다.
 *
 * 왜 TreeMap 인가. keysWithPrefix 가 사전순을 약속하기 때문이다.
 * HashMap 을 쓰면 모아놓고 정렬해야 하는데, TreeMap 은 순회 자체가 이미 사전순이다.
 * 05번에서 "해시맵은 순서를 버린다"고 했던 것의 대가를 여기서 치른다.
 *
 * 필드가 패키지 공개다. 테스트와 WordDictionary, TrieProblems 가 내부를 직접 본다.
 * 그래서 이 이름들은 계약의 일부다.
 */
public class MapTrie implements Trie {

    static final class Node {
        final Map<Character, Node> children = new TreeMap<>();

        /** 여기서 끝나는 단어가 있는가. "app" 과 "apple" 을 구분하는 유일한 표시다. */
        boolean end;

        /**
         * 이 노드를 지나는 단어 수 = 이 노드까지의 경로를 접두사로 갖는 단어 수.
         *
         * 뿌리의 wordsBelow 가 곧 size() 다.
         * 01번에서 size 를 세지 않고 들고 있었던 것과 같은 선택이다.
         */
        int wordsBelow;
    }

    Node root = new Node();

    @Override
    public void insert(String word) {
        requireWord(word);
        // TODO 1: 단어를 넣는다.
        //
        //   1. 이미 있으면 아무 일도 하지 않는다. wordsBelow 를 두 번 올리면 size 가 틀어진다.
        //   2. 뿌리부터 한 글자씩 내려가며 없는 자식은 만든다.
        //   3. **지나는 노드마다** wordsBelow 를 1 올린다. 뿌리도 포함이다.
        //   4. 마지막 노드에 end 를 세운다.
        //
        // 빈 문자열이면 어떻게 되는가? 루프가 한 번도 안 돌고 뿌리에 end 가 선다. 그게 맞다.
        throw new UnsupportedOperationException("TODO 1: insert");
    }

    @Override
    public boolean remove(String word) {
        requireWord(word);
        // TODO 2: 단어를 지운다. 없으면 false.
        //
        // 노드를 지워야 하는가? **다른 단어가 그 경로를 쓰고 있으면 안 된다.**
        // "app" 과 "apple" 이 있을 때 "apple" 을 지운다고 a-p-p 를 건드리면 "app" 이 사라진다.
        //
        // 그런데 wordsBelow 가 그 판단을 이미 해준다.
        // 내려가면서 wordsBelow 를 1씩 내리다가 **0 이 되는 노드를 만나면**
        // 그 아래로는 아무 단어도 없다는 뜻이라 통째로 끊고 끝내면 된다.
        //
        // 0 이 안 되면 끝까지 내려가 end 를 내린다.
        // 05번 tombstone 과 같은 계열의 함정이다. "지운 자리를 어떻게 처리하는가".
        throw new UnsupportedOperationException("TODO 2: remove");
    }

    /** 이 문자열 경로 끝의 노드. 없으면 null. contains/startsWith/countWithPrefix 가 전부 이걸 쓴다. */
    Node findNode(String s) {
        // TODO 3: 뿌리부터 한 글자씩 따라 내려간다. 도중에 끊기면 null.
        //
        // 이 메서드 하나로 아래 세 개가 한 줄이 된다는 점을 보라.
        // 같은 걸음을 세 번 쓰고 있었다는 뜻이다.
        throw new UnsupportedOperationException("TODO 3: findNode");
    }

    /** node 아래의 모든 단어를 path 를 앞에 붙여 사전순으로 out 에 담는다. */
    static void collect(Node node, StringBuilder path, List<String> out) {
        // TODO 4: 재귀로 훑는다.
        //
        //   - 이 노드가 end 면 지금까지의 path 가 단어 하나다.
        //   - 자식을 순회하며 path 에 글자를 붙이고 내려갔다가 **돌아와서 떼어낸다.**
        //
        // path 를 매번 새 문자열로 만들면 O(단어 길이) 복사가 노드마다 붙는다.
        // StringBuilder 하나를 붙였다 뗐다 하는 이유가 그것이다.
        // 떼는 것을 빠뜨리면 결과가 조용히 뒤죽박죽이 된다.
        throw new UnsupportedOperationException("TODO 4: collect");
    }

    @Override
    public int countWithPrefix(String prefix) {
        requireWord(prefix);
        // TODO 5: 이 접두사로 시작하는 단어 수.
        //
        // 아래로 훑어 end 를 세도 답은 맞다. 다만 O(부분 트리) 다.
        // wordsBelow 를 이미 유지하고 있다면 걸음 수는 접두사 길이뿐이다.
        //
        // **저장할 것인가 계산할 것인가.** 성능 테스트가 어느 쪽을 요구하는지 말해준다.
        throw new UnsupportedOperationException("TODO 5: countWithPrefix");
    }

    @Override
    public List<String> keysWithPrefix(String prefix) {
        requireWord(prefix);
        List<String> out = new ArrayList<>();
        Node start = findNode(prefix);
        if (start != null) {
            collect(start, new StringBuilder(prefix), out);
        }
        return out;
    }

    @Override
    public boolean contains(String word) {
        requireWord(word);
        Node n = findNode(word);
        return n != null && n.end;
    }

    @Override
    public boolean startsWith(String prefix) {
        requireWord(prefix);
        // 노드가 있느냐로는 부족하다. **뿌리는 단어가 하나도 없어도 존재한다.**
        // 빈 트라이에 startsWith("") 를 물으면 그것만으로는 true 가 나온다.
        Node n = findNode(prefix);
        return n != null && n.wordsBelow > 0;
    }

    @Override
    public int size() {
        return root.wordsBelow;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public void clear() {
        // 06번 트리와 같다. 뿌리를 끊으면 아래가 통째로 GC 대상이다.
        root = new Node();
    }

    private static void requireWord(String s) {
        if (s == null) {
            throw new IllegalArgumentException("null 은 단어가 아니다");
        }
    }
}
