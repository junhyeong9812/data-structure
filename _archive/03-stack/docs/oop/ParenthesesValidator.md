# oop/ParenthesesValidator.java

`(`, `{`, `[` 세 종류의 괄호 매칭 검증. 여는 괄호는 스택에 푸시, 닫는 괄호 만나면 top과 짝이 맞는지 확인 후 pop. 마지막에 스택이 비어있어야 true.

```java
package com.datastructure.stack.oop;

public class ParenthesesValidator {

    public boolean validate(String data) {
        char[] datas= data.toCharArray();
        Stack<Character> openParentheses = new ArrayStackImpl<>();
        for (int i = 0; i < datas.length ;i++) {
            if (datas[i]=='(') {
                openParentheses.push(datas[i]);
            }
            if (datas[i] == '{') {
                openParentheses.push(datas[i]);
            }
            if (datas[i] == '[') {
                openParentheses.push(datas[i]);
            }
            if (datas[i] == ')') {
                if (openParentheses.isEmpty() || openParentheses.peek() !='(') {
                    return false;
                }
                openParentheses.pop();
            }

            if (datas[i] == '}') {
                if (openParentheses.isEmpty() || openParentheses.peek()!='{') {
                    return false;
                }
                openParentheses.pop();
            }

            if (datas[i] == ']') {
                if (openParentheses.isEmpty() || openParentheses.peek()!='[') {
                    return false;
                }
                openParentheses.pop();
            }
        }
        return openParentheses.isEmpty();
    }
}
```
