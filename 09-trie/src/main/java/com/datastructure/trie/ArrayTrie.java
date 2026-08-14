package com.datastructure.trie;

import java.util.ArrayList;
import java.util.List;

/**
 * 자식을 26칸 배열로 들고 있는 트라이. a~z 만 담는다.
 *
 * MapTrie 와 로직은 완전히 같다. 자식을 어디에 두느냐만 다르다.
 * 그래서 여기서는 이미 배운 부분을 채워뒀고 달라지는 지점에만 TODO 가 있다.
 * 02번 DoublyLinkedList 와 같은 방식이다.
 *
 * | | MapTrie | ArrayTrie |
 * |---|---|---|
 * | 문자 집합 | 아무거나 | a~z 만 |
 * | 자식 접근 | 맵 조회 | 배열 인덱스, O(1) |
 * | 빈 자식의 비용 | 없다 | 26칸을 늘 잡는다 |
 * | 사전순 순회 | TreeMap 이 해준다 | 0~25 로 돌면 그게 사전순 |
 *
 * 마지막 줄이 공짜로 얻는 것이다. 인덱스가 곧 문자라서 순서가 자료 배치에 이미 들어 있다.
 *
 * 대가는 메모리다. 자식이 하나뿐인 노드도 26칸을 쓴다.
 * 긴 단어일수록 그런 노드가 많아진다. ArrayTrieTest 가 그 비용을 숫자로 못 박는다.
 *
 * 범위 밖 문자의 처리가 메서드마다 다르다.
 * insert/remove 는 예외를 던진다. 담을 수 없는 것을 담으라고 했으니 조용히 넘어가면 자료가 사라진다.
 * 조회는 false / 빈 리스트 / 0 을 준다. "담을 수 없는 문자를 가진 단어가 있느냐"는 질문의 답은 "없다"로 정해져 있다.
 */
public class ArrayTrie implements Trie {

    static final int ALPHABET = 26;

    static final class Node {
        final Node[] children = new Node[ALPHABET];
        boolean end;
        int wordsBelow;
    }

    Node root = new Node();

    /** 담을 수 있는 문자면 0~25, 아니면 -1. */
    static int indexOf(char c) {
        // TODO 1: 'a' 를 0 으로 맞춘다. 범위 밖이면 -1.
        //
        // 'a' 를 빼는 것만으로는 부족하다. 'A' 는 -32 가 되고 'z' 뒤의 문자는 26 이상이 된다.
        // 음수 인덱스는 예외지만 26 이상은 **다른 노드의 메모리를 밟지 않고도 예외가 안 날 수** 있다.
        // 05번 음수 해시와 같은 종류의 함정이다. 범위 검사를 양쪽 다 해야 한다.
        throw new UnsupportedOperationException("TODO 1: indexOf");
    }

    @Override
    public boolean remove(String word) {
        requireWord(word);
        for (int i = 0; i < word.length(); i++) {
            requireIndex(word.charAt(i));
        }
        // TODO 2: MapTrie.remove 와 같은 알고리즘이다.
        //
        //   wordsBelow 를 내려가며 1씩 깎고, 0 이 되는 노드를 만나면 끊고 끝낸다.
        //   맵에서 지우는 대신 **배열 칸에 null 을 넣는다.**
        //
        // 여기서 놓치기 쉬운 것: 칸을 null 로 만들어도 **배열 자체는 그대로 26칸이다.**
        // 노드가 사라져야 26칸이 반환된다.
        throw new UnsupportedOperationException("TODO 2: remove");
    }

    /** node 아래의 모든 단어를 path 를 앞에 붙여 사전순으로 out 에 담는다. */
    static void collect(Node node, StringBuilder path, List<String> out) {
        // TODO 3: MapTrie.collect 와 같은 재귀다. 다만 자식을 도는 방법이 다르다.
        //
        //   0 부터 25 까지 돌면서 null 이 아닌 칸만 내려간다.
        //   인덱스 i 에 해당하는 문자는 (char) ('a' + i) 다.
        //
        // **이 순회는 이미 사전순이다.** 정렬이 필요 없다. 그게 배열 자식의 덤이다.
        // 대신 자식이 하나뿐이어도 26칸을 전부 확인한다. 08번 인접 행렬의 neighbors 와 같은 모양이다.
        throw new UnsupportedOperationException("TODO 3: collect");
    }

    @Override
    public void insert(String word) {
        requireWord(word);
        for (int i = 0; i < word.length(); i++) {
            requireIndex(word.charAt(i));
        }
        if (contains(word)) {
            return;
        }
        Node cur = root;
        cur.wordsBelow++;
        for (int i = 0; i < word.length(); i++) {
            int idx = indexOf(word.charAt(i));
            if (cur.children[idx] == null) {
                cur.children[idx] = new Node();
            }
            cur = cur.children[idx];
            cur.wordsBelow++;
        }
        cur.end = true;
    }

    Node findNode(String s) {
        Node cur = root;
        for (int i = 0; i < s.length(); i++) {
            int idx = indexOf(s.charAt(i));
            if (idx < 0) {
                return null;
            }
            cur = cur.children[idx];
            if (cur == null) {
                return null;
            }
        }
        return cur;
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
    public int countWithPrefix(String prefix) {
        requireWord(prefix);
        Node n = findNode(prefix);
        return n == null ? 0 : n.wordsBelow;
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
        root = new Node();
    }

    private static int requireIndex(char c) {
        int i = indexOf(c);
        if (i < 0) {
            throw new IllegalArgumentException("ArrayTrie 는 a~z 만 담는다: '" + c + "'");
        }
        return i;
    }

    private static void requireWord(String s) {
        if (s == null) {
            throw new IllegalArgumentException("null 은 단어가 아니다");
        }
    }
}
