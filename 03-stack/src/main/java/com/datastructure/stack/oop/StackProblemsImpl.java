package com.datastructure.stack.oop;

public class StackProblemsImpl implements StackProblems{
    private ParenthesesValidator validator;
    private StackCalculator calculator;

    public StackProblemsImpl() {
        this.validator = new ParenthesesValidator();
        this.calculator = new StackCalculator();
    }

    @Override
    public boolean isValidParentheses(String data) {
        return validator.validate(data);
    }

    @Override
    public int evaluatePostfix(String data) {
        return 0;
    }

    @Override
    public String infixToPostfix(String data) {
        return "";
    }
}
