# oop/StackCalculator.java

후위 표기식 평가와 중위→후위 변환. 토큰은 공백으로 구분. `evaluatePostfix`는 빈 입력/피연산자 부족 시 `IllegalArgumentException`, 0 나눗셈 시 `ArithmeticException`. `infixToPostfix`는 사전에 `ParenthesesValidator`로 괄호 균형 검증.

```java
package com.datastructure.stack.oop;

public class StackCalculator {

    private ParenthesesValidator validator = new ParenthesesValidator();

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
        if (!validator.validate(data)) {
            throw new IllegalArgumentException();
        }

        String[] datas = data.split(" ");

        Stack<String> operators = new ArrayStackImpl<>();
        StringBuilder evaluatePostfix = new StringBuilder();
        for (int i = 0; i < datas.length; i++) {
            if (isNumber(datas[i])) {
                evaluatePostfix.append(datas[i]).append(" ");
            }
            if (data.equals("+")|| data.equals("-")) {
                while (!operators.isEmpty() && (operators.peek().equals("*") || operators.peek().equals("/") || operators.peek().equals("%"))) {
                    evaluatePostfix.append(operators.pop()).append(" ");
                }
            }
            checkOperator(operators, datas[i]);
            if (datas[i].equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    evaluatePostfix.append(operators.pop()).append(" ");
                }
                operators.pop();
            }
        }
        while (!operators.isEmpty()) {
            evaluatePostfix.append(operators.pop()).append(" ");
        }
        return evaluatePostfix.toString().trim();
    }

    private void checkOperator(Stack<String> operators, String data) {
        if (data.equals("+") || data.equals("-") || data.equals("*") || data.equals("/") || data.equals("%") || data.equals("(")) {
            operators.push(data);
        }
    }
}
```
