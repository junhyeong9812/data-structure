package com.datastructure.stack.oop;

public class StackCalculator {
    public int evaluatePostfix(String data) {
        String[] datas = data.split(" ");

        Stack<Integer> numbers = new ArrayStackImpl<>();
        for (int i = 0; i < datas.length; i++) {
            if (isNumber(datas[i])) {
                numbers.push(Integer.parseInt(String.valueOf(datas[i])));
            }
            if (datas[i].equals("+") ) {
                if (numbers.size() >= 2) {
                    int rightNumber= numbers.pop();
                    int leftNumber= numbers.pop();
                    numbers.push(leftNumber + rightNumber);
                } else {
                    throw new IllegalArgumentException();
                }
            }
            if (datas[i].equals("-")) {
                if (numbers.size() >= 2) {
                    int rightNumber= numbers.pop();
                    int leftNumber= numbers.pop();
                    numbers.push(leftNumber - rightNumber);
                } else {
                    throw new IllegalArgumentException();
                }
            }
            if (datas[i].equals("*")) {
                if (numbers.size() >= 2) {
                    int rightNumber= numbers.pop();
                    int leftNumber= numbers.pop();
                    numbers.push(leftNumber * rightNumber);
                } else {
                    throw new IllegalArgumentException();
                }
            }
            if (datas[i].equals("/")) {
                if (numbers.size() >= 2) {
                    int rightNumber= numbers.pop();
                    int leftNumber= numbers.pop();
                    if (rightNumber == 0) {
                        throw new ArithmeticException();
                    }
                    numbers.push(leftNumber / rightNumber);
                } else {
                    throw new IllegalArgumentException();
                }
            }
            if (datas[i].equals("%")) {
                if (numbers.size() >= 2) {
                    int rightNumber= numbers.pop();
                    int leftNumber= numbers.pop();
                    if (rightNumber == 0) {
                        throw new ArithmeticException();
                    }
                    numbers.push(leftNumber % rightNumber);
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }
        return numbers.pop();
    }

    private void calculate(Stack<Integer> stack, String operator) {
        int
    }

    private boolean isNumber(String data) {
        try {
            Integer.parseInt(data);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    public String infixToPostfix(String data){
        return null;
    }
}
