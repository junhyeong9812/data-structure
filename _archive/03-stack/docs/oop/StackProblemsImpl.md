# oop/StackProblemsImpl.java

`StackProblems`의 위임(facade) 구현. 실제 로직은 `ParenthesesValidator`와 `StackCalculator`에 위임한다.

```java
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
        return calculator.evaluatePostfix(data);
    }

    @Override
    public String infixToPostfix(String data) {
        return calculator.infixToPostfix(data);
    }
}
```
