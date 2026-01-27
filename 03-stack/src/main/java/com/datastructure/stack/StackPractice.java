package com.datastructure.stack;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Stack;

public class StackPractice {
    public static void main(String[] args) {

        Stack<Integer> stack = new Stack<>();

        System.out.println("empty : " + stack.isEmpty());
        stack.push(1);
        System.out.println("stack.push(1) = " + stack);
        System.out.println("empty : " + stack.isEmpty());
        stack.push(2);
        System.out.println("stack.push(2) = " + stack);
        stack.push(3);
        System.out.println("stack.push(3) = " + stack);
        System.out.println("pop : " + stack.pop());
        System.out.println("peek : " + stack.peek());
        /**
         * empty : true
         * stack.push(1) = [1]
         * empty : false
         * stack.push(2) = [1, 2]
         * stack.push(3) = [1, 2, 3]
         * pop : 3
         * peek : 2
         */

        System.out.println("Deque 기능 확인");
        Deque<Integer> stackDeque = new ArrayDeque<>();
        stackDeque.push(1);
        System.out.println("stackDeque.push(1) = " + stackDeque);
        stackDeque.push(2);
        System.out.println("stackDeque.push(1) = " + stackDeque);
        stackDeque.push(3);
        System.out.println("stackDeque.push(1) = " + stackDeque);
        System.out.println("stackDeque.pop() = " + stackDeque.pop());
        System.out.println("stackDeque.peek() =" + stackDeque.peek());
        System.out.println("stackDeque =" + stackDeque);
        System.out.println("stackDeque.pop() = " + stackDeque.pop());
        /**
         * Deque 기능 확인
         * stackDeque.push(1) = [1]
         * stackDeque.push(1) = [2, 1]
         * stackDeque.push(1) = [3, 2, 1]
         * stackDeque.pop() = 3
         * stackDeque.peek() =2
         * stackDeque =[2, 1]
         * stackDeque.pop() = 2
         */

        Deque<Integer> stackArray = new ArrayDeque<>();
        Deque<Integer> stackLink = new LinkedList<>();
        // Array 배열기반이 더 빠름.
    }
}
