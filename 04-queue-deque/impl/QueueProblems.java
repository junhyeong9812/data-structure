package com.datastructure.queue;

/**
 * [구현] 큐/데크 응용 문제.
 *
 * 파라미터 타입이 곧 요구하는 능력이다.
 * 회문과 슬라이딩 윈도우는 양쪽 끝이 필요해 Deque, 스트림 문제는 한쪽씩만 쓰므로 Queue 다.
 */
public final class QueueProblems {

    private QueueProblems() {
    }

    /**
     * 문제 1. 회문 판별.
     *
     * 양쪽 끝에서 하나씩 꺼내 비교한다. 큐로는 못 하고 데크여야 하는 이유가 이것이다.
     *
     * 주의: Character 를 == 로 비교하면 참조 비교가 된다(-128~127 밖에서 틀린다).
     * char 로 풀어서 비교한다.
     */
    public static boolean isPalindrome(String input, Deque<Character> buffer) {
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                buffer.addLast(Character.toLowerCase(c));
            }
        }
        while (buffer.size() > 1) {
            char front = buffer.removeFirst();
            char back = buffer.removeLast();
            if (front != back) {
                return false;
            }
        }
        return true;
    }

    /**
     * 문제 2. 슬라이딩 윈도우 최댓값 (단조 데크).
     *
     * 데크에 인덱스를 담되, 값이 내림차순이 되도록 유지한다.
     *
     * 새 값이 들어오면 뒤쪽의 더 작은(또는 같은) 후보들은 전부 버린다.
     * 그것들은 새 값보다 앞서 창을 벗어나면서 값도 작으니, 앞으로 영원히 답이 될 수 없다.
     * 그래서 데크 맨 앞이 항상 현재 창의 최댓값이다.
     *
     * 복잡도가 O(n) 인 이유: 각 인덱스는 한 번 들어가고 한 번 나온다.
     * 03번 nextGreater 와 같은 상환 논리다.
     */
    public static int[] slidingWindowMax(int[] values, int k, Deque<Integer> buffer) {
        int n = values.length;
        if (n == 0 || k <= 0 || k > n) {
            return new int[0];
        }
        int[] result = new int[n - k + 1];

        for (int i = 0; i < n; i++) {
            // 뒤쪽의 쓸모없어진 후보를 버린다
            while (!buffer.isEmpty() && values[buffer.peekLast()] <= values[i]) {
                buffer.removeLast();
            }
            buffer.addLast(i);

            // 창을 벗어난 후보는 앞에서 빠진다
            if (buffer.peekFirst() <= i - k) {
                buffer.removeFirst();
            }
            if (i >= k - 1) {
                result[i - k + 1] = values[buffer.peekFirst()];
            }
        }
        return result;
    }

    /**
     * 문제 3. 스트림에서 처음 한 번만 나온 문자.
     *
     * 큐에는 "아직 유일할 가능성이 있는" 문자를 등장 순서대로 담는다.
     * 앞에 있는 것이 이미 두 번 이상 나왔다면 그건 답이 될 수 없으므로 버린다.
     *
     * 한 번 버려진 문자는 다시 들어오지 않는다. 그래서 전체가 O(n) 이다.
     * 앞에서 빼고 뒤에 넣는 것만 필요하므로 Queue 로 충분하다.
     */
    public static String firstUniqueStream(String input, Queue<Character> buffer) {
        int[] counts = new int[26];
        StringBuilder out = new StringBuilder(input.length());

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            counts[c - 'a']++;
            buffer.enqueue(c);

            while (!buffer.isEmpty() && counts[buffer.peek() - 'a'] > 1) {
                buffer.dequeue();
            }
            out.append(buffer.isEmpty() ? '#' : buffer.peek());
        }
        return out.toString();
    }

    /**
     * 문제 4. k 칸 오른쪽 회전.
     *
     * 뒤에서 하나 빼서 앞에 넣기를 shift 번 하면 끝난다.
     * 01번(세 번 뒤집기), 02번(링크 재연결)에 이은 세 번째 방법이고 가장 단순하다.
     *
     * k 를 그대로 반복하면 k 가 크면 느리다. n 으로 나눈 나머지만 돌면 된다.
     * 자바의 % 는 음수에 음수를 주므로 0 이상으로 정규화한다.
     */
    public static <E> void rotate(Deque<E> deque, int k) {
        int n = deque.size();
        if (n <= 1) {
            return;
        }
        int shift = ((k % n) + n) % n;
        for (int i = 0; i < shift; i++) {
            deque.addFirst(deque.removeLast());
        }
    }
}
