# 스택 풀이 해설

## 📌 핵심 아이디어

스택은 **LIFO(Last In First Out)** 원칙을 따르는 선형 자료구조입니다.
한쪽 끝(top)에서만 삽입과 삭제가 이루어집니다.

---

## 🔑 핵심 개념

### 1. 스택 구조
```
push(3)  push(7)  push(1)  pop()   peek()
                              
  ┌───┐   ┌───┐   ┌───┐   ┌───┐   ┌───┐
  │   │   │   │   │ 1 │   │   │   │   │
  ├───┤   ├───┤   ├───┤   ├───┤   ├───┤
  │   │   │ 7 │   │ 7 │   │ 7 │ ← │ 7 │ (peek: 7)
  ├───┤   ├───┤   ├───┤   ├───┤   ├───┤
  │ 3 │   │ 3 │   │ 3 │   │ 3 │   │ 3 │
  └───┘   └───┘   └───┘   └───┘   └───┘
   top↑    top↑    top↑    top↑
```

### 2. 배열 기반 vs 연결 리스트 기반

| 구현 방식 | 장점 | 단점 |
|----------|------|------|
| 배열 | 캐시 친화적, 단순함 | 크기 제한, 확장 비용 |
| 연결 리스트 | 동적 크기, 확장 O(1) | 메모리 오버헤드, 캐시 비효율 |

---

## 📝 POP 구현 해설 (배열 기반)
```java
public class ArrayStack {
    private static final int DEFAULT_CAPACITY = 10;
    private int[] data;
    private int top;  // 다음에 추가될 인덱스 (= 현재 크기)
    
    public ArrayStack() {
        this.data = new int[DEFAULT_CAPACITY];
        this.top = 0;
    }
    
    public void push(int element) {
        if (top == data.length) {
            grow();
        }
        data[top++] = element;
    }
    
    public int pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return data[--top];
    }
    
    public int peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return data[top - 1];
    }
    
    public boolean isEmpty() {
        return top == 0;
    }
    
    public int size() {
        return top;
    }
    
    private void grow() {
        int newCapacity = data.length + (data.length >> 1);
        data = Arrays.copyOf(data, newCapacity);
    }
    
    // search: top에서부터의 거리 (1-based)
    public int search(int element) {
        for (int i = top - 1; i >= 0; i--) {
            if (data[i] == element) {
                return top - i;  // top=1 기준
            }
        }
        return -1;
    }
}
```

---

## 📝 OOP 구현 해설 (연결 리스트 기반)
```java
public class LinkedStack<E> implements Stack<E> {
    private Node<E> top;
    private int size;
    
    private static class Node<E> {
        E data;
        Node<E> next;
        
        Node(E data, Node<E> next) {
            this.data = data;
            this.next = next;
        }
    }
    
    @Override
    public void push(E element) {
        top = new Node<>(element, top);
        size++;
    }
    
    @Override
    public E pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        E data = top.data;
        top = top.next;
        size--;
        return data;
    }
    
    @Override
    public E peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return top.data;
    }
    
    @Override
    public boolean isEmpty() {
        return top == null;
    }
    
    @Override
    public int size() {
        return size;
    }
}
```

---

## 🎯 응용 알고리즘

### 1. 괄호 매칭
```java
public boolean isValidParentheses(String s) {
    Stack<Character> stack = new Stack<>();
    
    for (char c : s.toCharArray()) {
        if (c == '(' || c == '{' || c == '[') {
            stack.push(c);
        } else {
            if (stack.isEmpty()) return false;
            
            char top = stack.pop();
            if (c == ')' && top != '(') return false;
            if (c == '}' && top != '{') return false;
            if (c == ']' && top != '[') return false;
        }
    }
    
    return stack.isEmpty();
}
```

### 2. 후위 표기법 계산
```java
public int evaluatePostfix(String expression) {
    Stack<Integer> stack = new Stack<>();
    String[] tokens = expression.split(" ");
    
    for (String token : tokens) {
        if (isOperator(token)) {
            int b = stack.pop();  // 두 번째 피연산자
            int a = stack.pop();  // 첫 번째 피연산자
            stack.push(calculate(a, b, token));
        } else {
            stack.push(Integer.parseInt(token));
        }
    }
    
    return stack.pop();
}

private int calculate(int a, int b, String op) {
    return switch (op) {
        case "+" -> a + b;
        case "-" -> a - b;
        case "*" -> a * b;
        case "/" -> a / b;
        default -> throw new IllegalArgumentException();
    };
}
```

### 3. 중위 → 후위 변환 (Shunting Yard Algorithm)
```java
public String infixToPostfix(String expression) {
    StringBuilder output = new StringBuilder();
    Stack<String> operators = new Stack<>();
    String[] tokens = expression.split(" ");
    
    for (String token : tokens) {
        if (isNumber(token)) {
            output.append(token).append(" ");
        } else if (token.equals("(")) {
            operators.push(token);
        } else if (token.equals(")")) {
            while (!operators.peek().equals("(")) {
                output.append(operators.pop()).append(" ");
            }
            operators.pop();  // "(" 제거
        } else if (isOperator(token)) {
            while (!operators.isEmpty() && 
                   !operators.peek().equals("(") &&
                   precedence(operators.peek()) >= precedence(token)) {
                output.append(operators.pop()).append(" ");
            }
            operators.push(token);
        }
    }
    
    while (!operators.isEmpty()) {
        output.append(operators.pop()).append(" ");
    }
    
    return output.toString().trim();
}

private int precedence(String op) {
    return switch (op) {
        case "+", "-" -> 1;
        case "*", "/" -> 2;
        default -> 0;
    };
}
```

---

## ⏱️ 복잡도 분석

| 연산 | 시간복잡도 | 설명 |
|------|-----------|------|
| push | O(1)* | 배열: Amortized O(1) |
| pop | O(1) | - |
| peek | O(1) | - |
| isEmpty | O(1) | - |
| size | O(1) | - |
| search | O(n) | 선형 탐색 |

---

## ❌ 흔한 실수

### 1. 빈 스택 체크 누락
```java
// 잘못됨
public int pop() {
    return data[--top];  // top이 0이면 -1이 됨!
}

// 올바름
public int pop() {
    if (isEmpty()) {
        throw new EmptyStackException();
    }
    return data[--top];
}
```

### 2. top 인덱스 의미 혼동
```java
// 방식 1: top = 다음에 추가될 위치 (= 현재 크기)
push: data[top++] = element;
pop:  return data[--top];
peek: return data[top - 1];

// 방식 2: top = 마지막 요소 위치 (초기값 -1)
push: data[++top] = element;
pop:  return data[top--];
peek: return data[top];
```

### 3. 후위 표기법에서 피연산자 순서
```java
// 잘못됨 - a와 b 순서 바뀜
int a = stack.pop();
int b = stack.pop();
stack.push(a - b);  // 5 3 - 는 3-5=-2가 됨!

// 올바름
int b = stack.pop();  // 나중에 push된 것이 두 번째 피연산자
int a = stack.pop();  // 먼저 push된 것이 첫 번째 피연산자
stack.push(a - b);    // 5 3 - 는 5-3=2
```

---

## 🔗 관련 문제

- LeetCode 20: Valid Parentheses
- LeetCode 150: Evaluate Reverse Polish Notation
- LeetCode 155: Min Stack
- LeetCode 232: Implement Queue using Stacks
- LeetCode 739: Daily Temperatures
