# 스택 구현에 유용한 Java API

## 📦 기본 스택 관련

### java.util.Stack<E>
Java 표준 스택 (Vector 기반, 레거시)
```java
import java.util.Stack;

Stack<Integer> stack = new Stack<>();

stack.push(1);        // 요소 추가
stack.pop();          // 맨 위 요소 제거 및 반환
stack.peek();         // 맨 위 요소 조회 (제거 안함)
stack.empty();        // 비어있는지 확인 (isEmpty() 권장)
stack.search(obj);    // 요소 위치 반환 (top=1 기준, 없으면 -1)
```

### java.util.Deque<E> (권장)
Stack 대신 Deque 사용 권장 (Java 공식 권장)
```java
import java.util.Deque;
import java.util.ArrayDeque;

Deque<Integer> stack = new ArrayDeque<>();

stack.push(1);        // addFirst와 동일
stack.pop();          // removeFirst와 동일
stack.peek();         // peekFirst와 동일
stack.isEmpty();      // 비어있는지 확인
stack.size();         // 크기
```

### ArrayDeque vs LinkedList
```java
// ArrayDeque - 배열 기반, 더 빠름 (권장)
Deque<Integer> stack1 = new ArrayDeque<>();

// LinkedList - 연결 리스트 기반
Deque<Integer> stack2 = new LinkedList<>();
```

---

## ⚠️ 예외 클래스

### EmptyStackException
빈 스택에서 pop/peek 시
```java
import java.util.EmptyStackException;

public E pop() {
    if (isEmpty()) {
        throw new EmptyStackException();
    }
    // ...
}
```

### NoSuchElementException
Deque 사용 시 빈 스택에서 pop/peek
```java
import java.util.NoSuchElementException;

// ArrayDeque.pop()은 NoSuchElementException 발생
try {
    stack.pop();
} catch (NoSuchElementException e) {
    // 빈 스택
}

// 예외 없이 null 반환하려면 poll/peek 사용
Integer value = stack.pollFirst();  // 비어있으면 null
```

---

## 🔤 문자열 처리 (괄호 매칭용)

### String 메서드
```java
String s = "({[]})";

s.toCharArray();           // char[] 변환
s.charAt(index);           // 특정 위치 문자
s.length();                // 길이

// 빈 문자열 체크
s.isEmpty();               // length() == 0
s.isBlank();               // 공백만 있어도 true (Java 11+)
```

### Character 유틸리티
```java
char c = '(';

Character.isDigit(c);      // 숫자인지
Character.isLetter(c);     // 문자인지
Character.isWhitespace(c); // 공백인지
```

---

## 🔢 수식 계산 (후위 표기법용)

### Integer 파싱
```java
String token = "42";

Integer.parseInt(token);    // int로 변환 (예외 발생 가능)
Integer.valueOf(token);     // Integer 객체로 변환

// 숫자인지 확인
public boolean isNumber(String s) {
    try {
        Integer.parseInt(s);
        return true;
    } catch (NumberFormatException e) {
        return false;
    }
}

// 정규식으로 확인
s.matches("-?\\d+");        // 정수 패턴
s.matches("-?\\d+(\\.\\d+)?"); // 실수 패턴
```

### 문자열 분리
```java
String expression = "3 + 4 * 2";

String[] tokens = expression.split(" ");      // 공백으로 분리
String[] tokens = expression.split("\\s+");   // 여러 공백 처리

// StringTokenizer (레거시)
StringTokenizer st = new StringTokenizer(expression);
while (st.hasMoreTokens()) {
    String token = st.nextToken();
}
```

### 연산자 우선순위 맵
```java
import java.util.Map;

Map<String, Integer> precedence = Map.of(
    "+", 1,
    "-", 1,
    "*", 2,
    "/", 2,
    "^", 3
);

int p = precedence.getOrDefault("+", 0);
```

---

## 🔄 Switch Expression (Java 14+)

### 연산 처리
```java
// 전통적인 switch
int result;
switch (operator) {
    case "+": result = a + b; break;
    case "-": result = a - b; break;
    default: throw new IllegalArgumentException();
}

// Switch Expression (권장)
int result = switch (operator) {
    case "+" -> a + b;
    case "-" -> a - b;
    case "*" -> a * b;
    case "/" -> a / b;
    default -> throw new IllegalArgumentException("Unknown operator: " + operator);
};
```

---

## 📐 배열 유틸리티

### Arrays 클래스
```java
import java.util.Arrays;

// 배열 복사 (확장)
int[] newData = Arrays.copyOf(data, newCapacity);

// 배열 출력
System.out.println(Arrays.toString(data));

// 배열 채우기
Arrays.fill(data, 0);
```

### System.arraycopy()
```java
// 부분 복사
System.arraycopy(src, srcPos, dest, destPos, length);
```

---

## 🧱 StringBuilder (출력 조합용)

### 후위 표기법 결과 조합
```java
StringBuilder sb = new StringBuilder();

sb.append("3");
sb.append(" ");
sb.append("4");
sb.append(" +");

String result = sb.toString();  // "3 4 +"

// 마지막 문자 제거
sb.deleteCharAt(sb.length() - 1);

// 트림
result.trim();  // 앞뒤 공백 제거
```

### StringJoiner
```java
import java.util.StringJoiner;

StringJoiner sj = new StringJoiner(" ");  // 구분자
sj.add("3");
sj.add("4");
sj.add("+");
String result = sj.toString();  // "3 4 +"
```

---

## 🎯 제네릭 배열

### Object 배열 사용
```java
public class Stack<E> {
    private Object[] data;
    
    @SuppressWarnings("unchecked")
    public E pop() {
        return (E) data[--top];
    }
}
```

### 리플렉션 사용
```java
import java.lang.reflect.Array;

@SuppressWarnings("unchecked")
public E[] toArray(Class<E> clazz) {
    E[] result = (E[]) Array.newInstance(clazz, size);
    // ...
    return result;
}
```

---

## 🧪 테스트 관련

### JUnit 5 + AssertJ
```java
import static org.assertj.core.api.Assertions.*;

@Test
void shouldPushAndPop() {
    Stack<Integer> stack = new Stack<>();
    stack.push(1);
    stack.push(2);
    
    assertThat(stack.pop()).isEqualTo(2);
    assertThat(stack.pop()).isEqualTo(1);
    assertThat(stack.isEmpty()).isTrue();
}

@Test
void shouldThrowOnEmptyPop() {
    Stack<Integer> stack = new Stack<>();
    
    assertThatThrownBy(() -> stack.pop())
        .isInstanceOf(EmptyStackException.class);
}

// 여러 값 한번에 검증
@Test
void shouldValidateParentheses() {
    assertThat(isValid("()")).isTrue();
    assertThat(isValid("()[]{}")).isTrue();
    assertThat(isValid("(]")).isFalse();
    assertThat(isValid("([)]")).isFalse();
}
```

---

## 📚 Java 21 관련

### Pattern Matching (instanceof)
```java
public boolean equals(Object obj) {
    if (obj instanceof Stack<?> other) {
        return this.size == other.size && 
               Arrays.equals(this.data, other.data);
    }
    return false;
}
```

### Record (불변 결과용)
```java
// 수식 계산 결과
public record CalculationResult(String expression, int result) {}

CalculationResult result = new CalculationResult("3 4 +", 7);
System.out.println(result.expression()); // "3 4 +"
System.out.println(result.result());     // 7
```

### SequencedCollection (Java 21)
```java
// Deque도 SequencedCollection 구현
Deque<Integer> stack = new ArrayDeque<>();
stack.addFirst(1);
stack.addFirst(2);

Integer first = stack.getFirst();  // 2
Integer last = stack.getLast();    // 1
```

---

## ⚡ 성능 팁

### 1. 초기 용량 지정
```java
// 대량 데이터 예상 시
Deque<Integer> stack = new ArrayDeque<>(1000);
```

### 2. 오토박싱 피하기
```java
// 기본형 전용 스택 (성능 중요 시)
public class IntStack {
    private int[] data;
    // Integer 대신 int 사용
}
```

### 3. 재사용
```java
// 빈번한 생성 대신 clear() 후 재사용
stack.clear();
// 또는
while (!stack.isEmpty()) {
    stack.pop();
}
```
