package com.datastructure.stack.pop;


public class StackProblems {
    public boolean isValidParentheses(String data) {
        char[] datas = data.toCharArray();

        // 데이터를 스택에 저장

        ValidateParentheses validateParentheses = new ValidateParentheses();

        return validateParentheses.isValid(datas);
    }

    public class ValidateParentheses {

        public boolean isValid(char[] datas) {
            int size = datas.length;
            ArrayStack<Character> openStack = new ArrayStack<>();
            for (int i = 0; i < size; i++) {
                char data = datas[i];
                if (data == '(') {
                    openStack.push(data);

                }
                if (data == '{') {
                    openStack.push(data);

                }
                if (data == '[') {
                    openStack.push(data);

                }
                if (data == ')') {
                    if (openStack.isEmpty() || openStack.pop() != '(') {
                        return false;
                    }
                }
                if (data == '}') {
                    if (openStack.isEmpty() || openStack.pop() != '{') {
                        return false;
                    }
                }
                if (data == ']') {
                    if (openStack.isEmpty() || openStack.pop() != '[') {
                        return false;
                    }
                }
            }
            if (!openStack.isEmpty()) {
                return false;
            }
            return true;
        }
    }



    public int evaluatePostfix(String data) {
        return 0;
    }

    public String infixToPostfix(String data) {
        return null;
    }
}
