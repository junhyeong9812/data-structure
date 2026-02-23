# 트리(Tree)의 기본 개념 
E extends Comparable<E>인 이유
- 트리(특히 BST)는 값의 크고 작음을 비교해서 왼쪽/오른쪽에 배치해야 하므로 compareTo() 메서드가 필요하다.

compareTo() 규약
- 음수: element가 node.value보다 작다.
- 0: 같음
- 양수: element가 node.value보다 크다.
- Integer는 뺄셈 결과를 반환하기도 하지만, String등은 다른 방식으로 비교, 부호(음/0/양)만 의미를 가진다.

BST의 중복 처리
- BST는 기본적으로 중복을 허용하지 않는다.
- 중복을 처리하려면: 노드에 int count 필드를 두거나, 별도 배열/자료구조를 추가

protected static class Node 사용이유
- private -> 하위 클래스(BST, AVL)에서 접근 불가
- public -> 외부에 내부 구조 노출 (불필요)
- protected -> 하위 클래스에서만 접근 가능 (적절)

# 재귀 탐색 (findNode)
- 루트부터 compareTo로 비교하며 왼쪽/오른쪽으로 내려간다.
- 값을 찾으면 return node, 못 찾고 null까지 내려가면 return null
- 중간 노드들은 아래에서 받은 결과를 그대로 위로 전달(백배 전달)하는 구조
- 콜스택을 타고 최종 결과가 최초 호출자에세 반환된다.

# findMin/findMax
- findMin: 가장 왼쪽 노드 = 트리 전체의 최솟값
- findMax: 가장 오른쪽 노드 = 트리 전체의 최댓값
- BST 규칙이 "왼쪽<부모<오른쪽"이기 때문.
- 가장 왼쪽/오른쪽 노드가 반드시 리프 노드는 아닐 수 있다.


## 트리 종류 비교

| 구분 | BST | AVL | 힙 (Heap) |
|------|-----|-----|-----------|
| 규칙 | 왼쪽 < 부모 < 오른쪽 | BST와 동일 + 균형 유지 | 부모 < 자식 (최소힙) 또는 부모 > 자식 (최대힙) |
| 루트 | 처음 삽입된 값 (중간값 아님) | 회전으로 중간값에 가까워짐 | 최솟값 또는 최댓값 |
| 특징 | 편향 시 O(n) | 회전으로 항상 O(log n) 보장 | 최솟값/최댓값 빠르게 꺼냄 |
| 용도 | 탐색/정렬 | 탐색/정렬 (균형 보장) | 우선순위 큐 (PriorityQueue) |

## 중위 순회 (inOrder Traversal)
동작 원리
- "왼쪽 -> 자기 자신 -> 오른쪽" 순서로 재귀 탐색.
- 왼쪽 끝(null)에 도달하면 return되고, 바로 자기 값을 result에 추가한 뒤의 오른쪽으로 이동.
- 지식이 없으면(null) 그냥 return되고 바로 자기 값을 추가하는 구조

시각화 예시
```commandline
        5
       / \
      3    7
     / \
    1   4
```
호출 흐름:
```commandline
inOrder(5)
    inOrder(3)
        inOrder(1)
            inOrder(null) -> return <- 왼쪽 끝
            result.add(1)
            inOrder(null) -> return <- 오른쪽도 없음
        result.add(3)
        inOrder(4)
            inOrder(null) -> return
            result.add(4)
            inOrder(null) -> return
        result.add(5)
        inOrder(7)
            inOrder(null) -> return
            result.add(7)
            inOrder(null) -> return
```
결과: [1, 3, 4, 5, 7] -> BST에서 inOrder 순회는 오름차순 정렬 결과를 만든다.

## 전위 순회 (preOrder Traversal)
동작 원리
- "자기 자신 -> 왼쪽 -> 오른쪽"순서로 재귀 탐색.
- 루트부터 먼저 추가하고 왼쪽으로 내려간 뒤 오른쪽으로 이동
- 오름차순 정렬이 아니며, 트리 구조 자체를 복사하거나 저장할 때 주로 사용.
- 이 순서대로 BST에 다시 삽입하면 원래 트리 구조가 그대로 복원된다.

시각화 예시
```commandline
        5
       / \
      3    7
     / \
    1   4
```
호출 흐름:
```commandline
preOrder(5)
    result.add(5)
    preOrder(3)
        result.add(3)
        preOrder(1)
            result.add(1)
            preOrder(null) -> return
            preOrder(null) -> return
        preOrder(4)
            result.add(4)
            preOrder(null) -> return
            preOrder(null) -> return
    preOrder(7)
        result.add(7)
        preOrder(null) -> return
        preOrder(null) -> return
```
결과 [ 5, 3, 1, 4, 7] -> 정렬이 아닌 트리 구조 순서.

## 후위 순위 (postOrder Traversal)
동작원리
- "왼쪽 -> 오른쪽 -> 자기 자신" 순서로 재귀 탐색.
- 지식을 먼저 처리하고 부모를 나중에 처리하는 구조.
- 내림차순 정렬이 아니며, 트리를 삭제할 때 주로 사용. (자식 노드를 먼저 해제하고 마지막에 부모를 해제해야 안전하기 때문이다.)
- 참고: 내림차순을 얻으려면 inOrder의 역순(오른쪽 -> 자기 자신 -> 왼쪽)을 하면 된다.

시각화 예시
```commandline
        5
       / \
      3    7
     / \
    1   4
```
호출 흐름:
```commandline
postOrder(5)
    postOrder(3)
        postOrder(1)
            postOrder(null) -> return
            postOrder(null) -> return
            result.add(1)
        postOrder(4)
            postOrder(null) -> return
            postOrder(null) -> return
            result.add(4)
        result.add(3)
    postOrder(7)
        postOrder(null) -> return
        postOrder(null) -> return
        result.add(7)
    result.add(5)
```
결과: [1, 4, 3, 7, 5] -> 정렬이 아닌 자식 먼저 부모 나중 순서