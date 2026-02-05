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
            if (isNumber(tokens[i])) {
                numbers.push(Integer.parseInt(tokens[i]));
            } else {
                if (Objects.equals(tokens[i],"+")) {
                    int[] checkNumbers= checkNumbers(numbers);
                    int rightOperand = checkNumbers[1];
                    int leftOperand = checkNumbers[0];
                    numbers.push(leftOperand + rightOperand);
                }
                if (Objects.equals(tokens[i],"-")) {
                    int[] checkNumbers= checkNumbers(numbers);
                    int rightOperand = checkNumbers[1];
                    int leftOperand = checkNumbers[0];
                    numbers.push(leftOperand - rightOperand);
                }
                if (Objects.equals(tokens[i],"*")) {
                    int[] checkNumbers= checkNumbers(numbers);
                    int rightOperand = checkNumbers[1];
                    int leftOperand = checkNumbers[0];
                    numbers.push(leftOperand * rightOperand);
                }
                if (Objects.equals(tokens[i],"/")) {
                    int[] checkNumbers= checkNumbers(numbers);
                    int rightOperand = checkNumbers[1];
                    int leftOperand = checkNumbers[0];
                    if (rightOperand == 0) {
                        throw new ArithmeticException();
                    }
                    numbers.push(leftOperand / rightOperand);
                }
                if (Objects.equals(tokens[i],"%")) {
                    int[] checkNumbers= checkNumbers(numbers);
                    int rightOperand = checkNumbers[1];
                    int leftOperand = checkNumbers[0];
                    if (rightOperand == 0) {
                        throw new ArithmeticException();
                    }
                    numbers.push(leftOperand % rightOperand);
                }
            }

        }
        return numbers.pop();
    }
    public int[] checkNumbers(ArrayStack<Integer> numbers) {
        if(numbers.isEmpty()) throw new IllegalArgumentException();
        int rightOperand = numbers.pop();
        if(numbers.isEmpty()) throw new IllegalArgumentException();
        int leftOperand = numbers.pop();
        return new int[]{leftOperand, rightOperand};
    }

    private boolean isNumber(String data) {
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
