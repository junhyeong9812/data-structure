package com.datastructure.stack;

/**
 * [구현] 스택 응용 문제.
 *
 * 네 문제 전부 Stack 인터페이스만 안다. 그래서 두 구현 어느 쪽으로도 돌아간다.
 * 여기서 new ArrayStack<>() 이라고 쓰는 순간 그 자유가 사라진다.
 */
public final class StackProblems {

    private StackProblems() {
    }

    /**
     * 문제 1. 괄호 짝 맞추기.
     *
     * 여는 괄호는 쌓고, 닫는 괄호를 만나면 방금 쌓은 것과 짝이 맞는지 본다.
     * "가장 최근에 열린 것이 가장 먼저 닫혀야 한다"가 곧 LIFO 라서 스택이 딱 맞는다.
     *
     * 다 훑고도 스택에 남아 있으면 안 닫힌 괄호가 있다는 뜻이다.
     */
    public static boolean isBalanced(String input, Stack<Character> buffer) {
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '(':
                case '[':
                case '{':
                    buffer.push(c);
                    break;
                case ')':
                    if (buffer.isEmpty() || buffer.pop() != '(') return false;
                    break;
                case ']':
                    if (buffer.isEmpty() || buffer.pop() != '[') return false;
                    break;
                case '}':
                    if (buffer.isEmpty() || buffer.pop() != '{') return false;
                    break;
                default:
                    break;   // 괄호가 아닌 문자는 무시한다
            }
        }
        return buffer.isEmpty();
    }

    /**
     * 문제 2. 후위 표기식 계산.
     *
     * 숫자는 쌓고, 연산자를 만나면 두 개를 꺼내 계산해 다시 쌓는다.
     *
     * 꺼내는 순서가 중요하다. 나중에 쌓인 것이 먼저 나오므로 **두 번째로 꺼낸 것이 왼쪽 피연산자**다.
     * 반대로 하면 "3 4 -" 가 1 이 나온다. 정답은 -1 이다.
     */
    public static int evaluatePostfix(String expression, Stack<Integer> buffer) {
        for (String token : expression.trim().split("\\s+")) {
            if (token.isEmpty()) {
                continue;
            }
            if (isOperator(token)) {
                if (buffer.size() < 2) {
                    throw new IllegalArgumentException("피연산자가 모자란다: " + token);
                }
                int right = buffer.pop();
                int left = buffer.pop();
                buffer.push(apply(left, right, token));
            } else {
                try {
                    buffer.push(Integer.parseInt(token));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("알 수 없는 토큰: " + token, e);
                }
            }
        }
        if (buffer.size() != 1) {
            throw new IllegalArgumentException("식이 올바르지 않다. 남은 값 " + buffer.size() + "개");
        }
        return buffer.pop();
    }

    private static boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

    private static int apply(int left, int right, String operator) {
        switch (operator) {
            case "+": return left + right;
            case "-": return left - right;
            case "*": return left * right;
            case "/": return left / right;
            default: throw new IllegalArgumentException("알 수 없는 연산자: " + operator);
        }
    }

    /**
     * 문제 3. 오른쪽의 첫 번째 더 큰 값 (단조 스택).
     *
     * 스택에 "아직 답을 못 찾은 인덱스"를 쌓는다.
     * 새 값이 들어오면, 그보다 작은 값들은 답이 한꺼번에 정해진다. 그것들을 전부 꺼내 답을 채운다.
     *
     * 복잡도가 O(n) 인 이유: 각 인덱스는 스택에 딱 한 번 들어가고 한 번 나온다.
     * 안쪽 while 이 여러 번 돌아도 전체를 통틀면 pop 횟수가 n 을 넘지 않는다.
     * 이걸 상환 분석이라고 부른다. 01번 배열 확장과 같은 논리다.
     *
     * 끝까지 남은 것들은 오른쪽에 더 큰 값이 없었다는 뜻이므로 -1 이다.
     */
    public static int[] nextGreater(int[] values, Stack<Integer> buffer) {
        int n = values.length;
        int[] result = new int[n];

        for (int i = 0; i < n; i++) {
            while (!buffer.isEmpty() && values[buffer.peek()] < values[i]) {
                result[buffer.pop()] = values[i];
            }
            buffer.push(i);
        }
        while (!buffer.isEmpty()) {
            result[buffer.pop()] = -1;
        }
        return result;
    }

    /**
     * 문제 4. 스택 정렬.
     *
     * helper 를 "top 이 가장 작은" 상태로 유지한다.
     * 새 값을 넣으려는데 helper 위쪽에 더 작은 것들이 있으면, 그것들을 원래 스택으로 되돌려 자리를 만든다.
     *
     * 마지막에 helper 를 통째로 옮기면 순서가 한 번 더 뒤집혀서
     * 원래 스택은 "top 이 가장 큰" 오름차순이 된다.
     *
     * 되돌리는 비용 때문에 O(n^2) 이다. 스택 두 개만 쓰는 제약에서는 이게 한계다.
     */
    public static void sortAscending(Stack<Integer> stack, Stack<Integer> helper) {
        while (!stack.isEmpty()) {
            int value = stack.pop();
            while (!helper.isEmpty() && helper.peek() < value) {
                stack.push(helper.pop());
            }
            helper.push(value);
        }
        while (!helper.isEmpty()) {
            stack.push(helper.pop());
        }
    }

    /**
     * 문제 5. 중위 -> 후위 (shunting yard).
     *
     * 연산자는 "아직 출력할 때가 안 된 것"이라 스택에 대기시킨다.
     * 새 연산자가 왔을 때, 스택 위에 우선순위가 같거나 높은 것이 있으면 그것이 먼저 계산되어야 하므로
     * 꺼내서 출력한다.
     *
     * "같거나"가 왼쪽 결합성을 만든다.
     * `3 - 4 - 5` 에서 두 번째 `-` 를 만났을 때 스택의 `-` 를 꺼내야 `3 4 - 5 -` 가 되고,
     * 그래야 (3-4)-5 로 계산된다. "높은"만 보면 `3 4 5 - -` 가 되어 3-(4-5) 가 된다. 답이 달라진다.
     *
     * 괄호 불일치는 두 곳에서 잡힌다.
     *   닫는 괄호를 만났는데 여는 괄호가 안 나온 채 스택이 빈 경우
     *   다 끝났는데 스택에 여는 괄호가 남아 있는 경우
     */
    public static String infixToPostfix(String expression, Stack<Character> buffer) {
        StringBuilder out = new StringBuilder();

        for (String token : expression.trim().split("\\s+")) {
            if (token.isEmpty()) {
                continue;
            }
            char c = token.charAt(0);

            if (token.equals("(")) {
                buffer.push('(');
            } else if (token.equals(")")) {
                while (!buffer.isEmpty() && buffer.peek() != '(') {
                    append(out, buffer.pop());
                }
                if (buffer.isEmpty()) {
                    throw new IllegalArgumentException("여는 괄호가 없다");
                }
                buffer.pop();                       // 여는 괄호는 출력하지 않고 버린다
            } else if (isOperator(token)) {
                while (!buffer.isEmpty() && buffer.peek() != '('
                    && precedence(buffer.peek()) >= precedence(c)) {
                    append(out, buffer.pop());
                }
                buffer.push(c);
            } else {
                try {
                    Integer.parseInt(token);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("알 수 없는 토큰: " + token, e);
                }
                append(out, token);
            }
        }

        while (!buffer.isEmpty()) {
            char top = buffer.pop();
            if (top == '(') {
                throw new IllegalArgumentException("닫는 괄호가 없다");
            }
            append(out, top);
        }
        return out.toString();
    }

    private static int precedence(char operator) {
        return (operator == '*' || operator == '/') ? 2 : 1;
    }

    private static void append(StringBuilder out, Object token) {
        if (out.length() > 0) {
            out.append(' ');
        }
        out.append(token);
    }
}
