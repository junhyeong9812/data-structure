# oop/StackProblems.java

스택 응용 문제 인터페이스. 괄호 매칭, 후위 표기법 계산, 중위→후위 변환을 정의한다.

```java
package com.datastructure.stack.oop;

public interface StackProblems {
    // 괄호 매칭
    public boolean isValidParentheses(String data);
    // 후위 표기법 계산
    public int evaluatePostfix(String data);
    // 중위 -> 후위 변환
    public String infixToPostfix(String data);
}
```
