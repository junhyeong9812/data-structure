package com.datastructure.stack.oop;

import java.util.Objects;

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
                if (openParentheses.isEmpty() && openParentheses.peek() !='(') {
                    return false;
                }
            }

            if (datas[i] == '}') {
                if (!openParentheses.isEmpty() && openParentheses.peek()=='{') {
                    return false;
                }
                openParentheses.pop();
            }

            if (datas[i] == ']') {
                if (!openParentheses.isEmpty() && openParentheses.peek()=='[') {
                    return false;
                }
                openParentheses.pop();
            }
        }
        return openParentheses.isEmpty();
    }
}
