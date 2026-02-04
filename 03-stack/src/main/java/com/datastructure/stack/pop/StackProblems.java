package com.datastructure.stack.pop;

public class StackProblems {
    public boolean isValidParentheses(String data) {
        char[] datas = data.toCharArray();
        Parentheses parentheses = new Parentheses();
        Braces braces = new Braces();
        Brackets brackets = new Brackets();

        for(int i = 0; i <datas.length; i++) {
            parentheses.checkParentheses(datas[i]);
            braces.checkBraces(datas[i]);
            brackets.checkBrackets(datas[i]);
        }

        return parentheses.isMatched() && braces.isMatched() && brackets.isMatched();
    }

    private class Parentheses {
        private int openParenthesis;
        private int closeParenthesis;

        public Parentheses() {
            this.openParenthesis = 0;
            this.closeParenthesis = 0;
        }

        public void checkParentheses(char data) {
            if (data == '(') {
                openParenthesis++;
            }
            if (data == ')') {
                closeParenthesis++;
            }
        }
        public boolean isMatched() {
            return openParenthesis == closeParenthesis;
        }
    }

    private class Braces {
        private int openBrace;
        private int closeBrace;

        public Braces() {
            this.openBrace = 0;
            this.closeBrace = 0;
        }

        public void checkBraces(char data) {
            if (data == '{') {
                openBrace++;
            }
            if (data == '}') {
                closeBrace++;
            }
        }

        public boolean isMatched() {
            return openBrace == closeBrace;
        }
    }

    private class Brackets {
        private int openBracket;
        private int closeBracket;

        public Brackets() {
            this.openBracket = 0;
            this.closeBracket = 0;
        }

        public void checkBrackets(char data) {
            if (data == '[') {
                openBracket++;
            }
            if (data == ']') {
                closeBracket++;
            }
        }

        public boolean isMatched() {
            return openBracket == closeBracket;
        }
    }

    public int evaluatePostfix(String data) {
        return 0;
    }

    public String infixToPostfix(String data) {
        return null;
    }
}
