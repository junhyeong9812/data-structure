package com.datastructure.stack.oop;

public interface StackProblems {
    // 괄호 매칭
    public boolean isValidParentheses(String data);
    // 후위 표기법 계산
    public int evaluatePostfix(String data);
    // 중위 -> 후위 변환
    public String infixToPostfix(String data);
}
