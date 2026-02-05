package com.datastructure.stack.pop;


import java.util.Objects;

public class StackProblems {
    public boolean isValidParentheses(String data) {
        char[] datas = data.toCharArray();

        // 데이터를 스택에 저장

        int size = datas.length;
        ArrayStack<Character> openStack = new ArrayStack<>();
        for (int i = 0; i < size; i++) {
            if (datas[i] == '(') {
                openStack.push(datas[i]);

            }
            if (datas[i] == '{') {
                openStack.push(datas[i]);

            }
            if (datas[i] == '[') {
                openStack.push(datas[i]);

            }
            if (datas[i] == ')') {
                if (openStack.isEmpty() || openStack.pop() != '(') {
                    return false;
                }
            }
            if (datas[i] == '}') {
                if (openStack.isEmpty() || openStack.pop() != '{') {
                    return false;
                }
            }
            if (datas[i] == ']') {
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




    public int evaluatePostfix(String data) {
        if (data.isEmpty()) {
            throw new IllegalArgumentException();
        }
        String[] tokens = data.split(" ");

        ArrayStack<Integer> numbers = new ArrayStack<>();
        for (int i = 0; i < tokens.length; i++) {
            if (isNumber(String.valueOf(tokens[i]))) {
                numbers.push(Integer.parseInt(tokens[i]));
            } else {
                if (Objects.equals(tokens[i],"+")) {
                    int rightOperand = numbers.pop();
                    int leftOperand = numbers.pop();
                    numbers.push(leftOperand + rightOperand);
                }
                if (Objects.equals(tokens[i],"-")) {
                    int rightOperand = numbers.pop();
                    int leftOperand = numbers.pop();
                    numbers.push(leftOperand - rightOperand);
                }
                if (Objects.equals(tokens[i],"*")) {
                    int rightOperand = numbers.pop();
                    int leftOperand = numbers.pop();
                    numbers.push(leftOperand * rightOperand);
                }
                if (Objects.equals(tokens[i],"/")) {
                    int rightOperand = numbers.pop();
                    int leftOperand = numbers.pop();
                    numbers.push(leftOperand / rightOperand);
                }
                if (Objects.equals(tokens[i],"%")) {
                    int rightOperand = numbers.pop();
                    int leftOperand = numbers.pop();
                    numbers.push(leftOperand % rightOperand);
                }
            }

        }
        return numbers.pop();
    }

    public boolean isNumber(String data) {
        try {
            Integer.parseInt(data);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String infixToPostfix(String data) {
        return null;
    }
}
