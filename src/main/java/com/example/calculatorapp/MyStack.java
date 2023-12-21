package com.example.calculatorapp;

import java.util.ArrayList;
import java.util.List;

    public class MyStack<T> {
        private List<T> stack;

        public MyStack() {
            stack = new ArrayList<>();
        }

        public void push(T element) {
            stack.add(element);
        }

        public T pop() {
            if (isEmpty()) {
                throw new IllegalStateException("Stack is empty");
            }
            return stack.remove(size() - 1);
        }

        public T peek() {
            if (isEmpty()) {
                throw new IllegalStateException("Stack is empty");
            }
            return stack.get(size() - 1);
        }

        public int size() {
            return stack.size();
        }

        public boolean isEmpty() {
            return stack.isEmpty();
        }

        public void clear() {
            stack.clear();
        }

        public void swap() {
            if (size() < 2) {
                throw new IllegalStateException("Swap requires at least two elements in the stack");
            }

            int lastIndex = size() - 1;
            T lastElement = stack.get(lastIndex);
            T secondToLastElement = stack.get(lastIndex - 1);

            stack.set(lastIndex, secondToLastElement);
            stack.set(lastIndex - 1, lastElement);
        }

        public void dup() {
            if (isEmpty()) {
                throw new IllegalStateException("Dup requires at least one element in the stack");
            }
            push(peek());
        }

        public void drop() {
            if (isEmpty()) {
                throw new IllegalStateException("Drop requires at least one element in the stack");
            }
            stack.remove(size() - 1);
        }

        public void over() {
            if (size() < 2) {
                throw new IllegalStateException("Over requires at least two elements in the stack");
            }

            push(stack.get(size() - 2));
        }
    }

