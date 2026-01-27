package com.datastructure.stack;

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



    }
}
