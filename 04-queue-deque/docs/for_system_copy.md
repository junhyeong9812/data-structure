# 순환 배열에서의 for문을 통한 배열 복사와 System.arraycopy의 트레이드 오프

```commandline
private void growArrayDeque() {
        if (size >= capacity) {
            int newCapacity = (int)(capacity * 1.5);
            E[] newElements = (E[])(new Object[newCapacity]);

            for (int i = 0; i < size; i++) {
                newElements[i] = elements[(front + i) % capacity];
            }
            
            elements = newElements;
            capacity = newCapacity;
            front = 0;
            rear = size;
        }
    }
```
for문을 이용한 코드

```commandline
private void growArrayDeque() {
        if (size >= capacity) {
            int newCapacity = (int)(capacity * 1.5);
            E[] newElements = (E[])(new Object[newCapacity]);

            if (front + size <= capacity) {
                // front부터 끝까지 한번 복사
                System.arraycopy(elements, front, newElements, 0, size);
            } else {
                // front부터 배열 끝까지
                int firstPart = capacity - front;
                System.arraycopy(elements, front, newElements, 0, firstPart);
                // 배열 처음부터 나머지
                System.arraycopy(elements, 0, newElements, firstPart, size - firstPart);
            }
            
            elements = newElements;
            capacity = newCapacity;
            front = 0;
            rear = size;
        }
    }
```

위와 같이 front부터 끝까지 한번에 복사를 하거나. 
아니면 분기에 의래 front 뒷부분을 먼저 처리 후 나머지 부분을 처리해야되기에 1번의 분기 및 2번의 복사가 일어난다.
이렇기 때문에 약간의 성능을 위해 위와 같이 분기 연산 및 복사 서순을 여러번 처리하는 것보다 for문이 더 직관적이다.