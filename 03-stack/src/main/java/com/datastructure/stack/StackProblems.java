package com.datastructure.stack;

/**
 * 스택으로 푸는 응용 문제들.
 *
 * 모든 메서드가 구현체가 아니라 **Stack 인터페이스**를 받는다.
 * 그래서 같은 코드가 ArrayStack 으로도 LinkedStack 으로도 돌아간다.
 * 테스트가 두 구현 모두로 이 문제들을 돌린다.
 *
 * 작업용 스택을 인자로 받는 이유가 그것이다. 여기서 `new ArrayStack<>()` 라고 쓰는 순간
 * 이 코드는 특정 구현에 묶인다.
 *
 * 스택이 필요한 상황에는 공통점이 있다. "나중에 만난 것을 먼저 처리해야 할 때"다.
 * 괄호, 연산자 우선순위, 되돌리기, 함수 호출 스택이 전부 그렇다.
 */
public final class StackProblems {

    private StackProblems() {
    }

    /**
     * 문제 1. 괄호 짝 맞추기
     *
     * `()`, `[]`, `{}` 세 종류가 올바르게 짝지어졌는지 본다. 괄호 외의 문자는 무시한다.
     *
     *   "a(b[c]d)e"  -> true
     *   "([)]"       -> false     짝은 맞지만 순서가 어긋난다
     *   "(("         -> false     안 닫혔다
     *   ")("         -> false     닫는 게 먼저 왔다
     *
     * 생각할 것
     *   - 왜 스택인가? 가장 최근에 열린 괄호가 가장 먼저 닫혀야 하기 때문이다.
     *   - 다 훑고 나서 스택에 뭔가 남아 있으면 그건 무슨 뜻인가?
     *
     * TODO(10): 구현하라. buffer 는 비어 있는 상태로 들어온다.
     */
    public static boolean isBalanced(String input, Stack<Character> buffer) {
        throw new UnsupportedOperationException("TODO(10): isBalanced");
    }

    /**
     * 문제 2. 후위 표기식 계산
     *
     * 공백으로 구분된 후위 표기식을 계산한다. 정수와 `+ - * /` 만 나온다.
     *
     *   "3 4 +"        -> 7
     *   "3 4 + 2 *"    -> 14
     *   "5 1 2 + 4 * + 3 -"  -> 14
     *
     * 나눗셈은 정수 나눗셈이다. 0 으로 나누면 ArithmeticException 이 그대로 나가면 된다.
     * 식이 올바르지 않으면 IllegalArgumentException.
     *
     * 생각할 것
     *   - 연산자를 만났을 때 꺼내는 순서가 중요하다. "3 4 -" 는 3-4 인가 4-3 인가?
     *   - 다 계산하고 스택에 값이 정확히 하나 남아야 올바른 식이다.
     *
     * TODO(11): 구현하라.
     */
    public static int evaluatePostfix(String expression, Stack<Integer> buffer) {
        throw new UnsupportedOperationException("TODO(11): evaluatePostfix");
    }

    /**
     * 문제 3. 오른쪽의 첫 번째 더 큰 값 (이 문제집의 함정)
     *
     * 각 원소에 대해, 그 오른쪽에서 처음으로 나타나는 더 큰 값을 찾는다. 없으면 -1.
     *
     *   [2, 1, 3]     -> [3, 3, -1]
     *   [5, 4, 3]     -> [-1, -1, -1]
     *   [1, 3, 2, 4]  -> [3, 4, 4, -1]
     *
     * 함정
     *   각 원소마다 오른쪽을 훑으면 O(n^2) 이다. 테스트에 20만 개짜리 케이스와 시간 제한이 있다.
     *
     * 생각할 것
     *   - 스택에 "아직 답을 못 찾은 원소"를 쌓아두면 어떻게 되는가?
     *   - 새 값이 들어왔을 때, 그 값보다 작은 것들은 답이 한꺼번에 정해진다.
     *   - 각 원소가 스택에 몇 번 들어가고 몇 번 나오는가? 그게 복잡도다.
     *
     * TODO(12): 구현하라. O(n) 이어야 한다. buffer 에는 인덱스를 담으면 편하다.
     */
    public static int[] nextGreater(int[] values, Stack<Integer> buffer) {
        throw new UnsupportedOperationException("TODO(12): nextGreater");
    }

    /**
     * 문제 4. 스택 정렬
     *
     * 스택을 오름차순으로 만든다. 큰 값이 top 에 오게 한다.
     * 배열이나 리스트로 옮기지 말고, **주어진 보조 스택 하나만** 써서 해결하라.
     *
     *   [3, 1, 2] (top=2)  ->  [1, 2, 3] (top=3)
     *
     * 생각할 것
     *   - 보조 스택을 항상 정렬된 상태로 유지하면 어떻게 되는가?
     *   - 넣으려는 값보다 큰 것들이 보조 스택 위에 있으면 어떻게 하는가?
     *   - 마지막에 다시 옮겨 담으면 순서가 어떻게 되는가?
     *
     * TODO(13): 구현하라.
     */
    public static void sortAscending(Stack<Integer> stack, Stack<Integer> helper) {
        throw new UnsupportedOperationException("TODO(13): sortAscending");
    }

    /**
     * 문제 5. 중위 표기식을 후위 표기식으로
     *
     * 우리가 쓰는 표기(중위)를 문제 2가 계산할 수 있는 표기(후위)로 바꾼다.
     * 토큰은 공백으로 구분되고 정수, `+ - * /`, 괄호만 나온다.
     *
     *   "3 + 4 * 2"        ->  "3 4 2 * +"
     *   "( 3 + 4 ) * 2"    ->  "3 4 + 2 *"
     *   "3 - 4 - 5"        ->  "3 4 - 5 -"     왼쪽부터 묶인다
     *
     * 이 변환을 shunting yard 알고리즘이라고 부른다.
     * 결과를 문제 2에 그대로 넣으면 계산이 된다. 두 문제가 이어진다.
     *
     * 규칙
     *   숫자      바로 출력한다
     *   여는 괄호  쌓는다
     *   닫는 괄호  여는 괄호를 만날 때까지 꺼내 출력하고, 여는 괄호는 버린다
     *   연산자    스택 위에 "우선순위가 같거나 높은 연산자"가 있으면 먼저 꺼내 출력한 뒤 쌓는다
     *   끝나면    남은 것을 전부 꺼내 출력한다
     *
     * 생각할 것
     *   - 왜 "같거나 높은"인가? "높은"만 보면 `3 - 4 - 5` 가 어떻게 되는가?
     *     사칙연산은 왼쪽부터 묶이는데, 그 성질이 이 조건 하나에 들어 있다.
     *   - 괄호가 안 맞으면 언제 알 수 있는가? 두 가지 경우가 있다.
     *
     * 괄호가 맞지 않으면 IllegalArgumentException.
     *
     * TODO(14): 구현하라.
     */
    public static String infixToPostfix(String expression, Stack<Character> buffer) {
        throw new UnsupportedOperationException("TODO(14): infixToPostfix");
    }
}
