package com.datastructure.stack.oop;

public class StackCalculator {
//    public int evaluatePostfix(String data) {
//        String[] datas = data.split(" ");
//
//        Stack<Integer> numbers = new ArrayStackImpl<>();
//        for (int i = 0; i < datas.length; i++) {
//            if (isNumber(datas[i])) {
//                numbers.push(Integer.parseInt(String.valueOf(datas[i])));
//            }
//            if (datas[i].equals("+") ) {
//                if (numbers.size() >= 2) {
//                    int rightNumber= numbers.pop();
//                    int leftNumber= numbers.pop();
//                    numbers.push(leftNumber + rightNumber);
//                } else {
//                    throw new IllegalArgumentException();
//                }
//            }
//            if (datas[i].equals("-")) {
//                if (numbers.size() >= 2) {
//                    int rightNumber= numbers.pop();
//                    int leftNumber= numbers.pop();
//                    numbers.push(leftNumber - rightNumber);
//                } else {
//                    throw new IllegalArgumentException();
//                }
//            }
//            if (datas[i].equals("*")) {
//                if (numbers.size() >= 2) {
//                    int rightNumber= numbers.pop();
//                    int leftNumber= numbers.pop();
//                    numbers.push(leftNumber * rightNumber);
//                } else {
//                    throw new IllegalArgumentException();
//                }
//            }
//            if (datas[i].equals("/")) {
//                if (numbers.size() >= 2) {
//                    int rightNumber= numbers.pop();
//                    int leftNumber= numbers.pop();
//                    if (rightNumber == 0) {
//                        throw new ArithmeticException();
//                    }
//                    numbers.push(leftNumber / rightNumber);
//                } else {
//                    throw new IllegalArgumentException();
//                }
//            }
//            if (datas[i].equals("%")) {
//                if (numbers.size() >= 2) {
//                    int rightNumber= numbers.pop();
//                    int leftNumber= numbers.pop();
//                    if (rightNumber == 0) {
//                        throw new ArithmeticException();
//                    }
//                    numbers.push(leftNumber % rightNumber);
//                } else {
//                    throw new IllegalArgumentException();
//                }
//            }
//        }
//        return numbers.pop();
//    }

    public int evaluatePostfix(String data) {

        if (data == null || data.trim().isEmpty()) throw new IllegalArgumentException();

        String[] datas = data.split(" ");

        Stack<Integer> numbers = new ArrayStackImpl<>();
        for (int i = 0; i < datas.length; i++) {
            if (isNumber(datas[i])) {
                numbers.push(Integer.parseInt(String.valueOf(datas[i])));
            }
            if (datas[i].equals("+") ) {
                calculate(numbers,"+");
            }
            if (datas[i].equals("-")) {
                calculate(numbers,"-");
            }
            if (datas[i].equals("*")) {
                calculate(numbers,"*");
            }
            if (datas[i].equals("/")) {
                calculate(numbers,"/");
            }
            if (datas[i].equals("%")) {
                calculate(numbers,"%");
            }
        }
        return numbers.pop();
    }

    private void calculate(Stack<Integer> numbers, String operator) {
        int[] operands = popOperand(numbers);

        if ((operator.equals("/") || operator.equals("%")) && operands[1] == 0) {
            throw new ArithmeticException();
        }

        int result = switch (operator) {
            case "+" -> operands[0] + operands[1];
            case "-" -> operands[0] - operands[1];
            case "*" -> operands[0] * operands[1];
            case "/" -> operands[0] / operands[1];
            default -> operands[0] % operands[1];
        };
        numbers.push(result);
    }

    private int[] popOperand(Stack<Integer> numbers) {
        if (numbers.size() < 2) throw new IllegalArgumentException();
        int right = numbers.pop();
        int left = numbers.pop();
        return new int[] {left, right};
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
