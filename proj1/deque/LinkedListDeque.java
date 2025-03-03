package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {

    private class Node {
        private T item;
        private Node next;
        private Node prev;

        private Node(T i, Node n) {
            item = i;
            next = n;
        }
    }
    private int size;
    private Node sentinel;
    public LinkedListDeque() {
        sentinel = new Node(null, null);
        size = 0;
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
    }

    @Override
    public Iterator<T> iterator() {
        return new LLDIterator();
    }

    public class LLDIterator implements Iterator<T> {

        Node curr;

        public LLDIterator() {
            curr = sentinel;
        }

        @Override
        public boolean hasNext() {
            if (curr.next != null){
                return true;
            }
            return false;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T currItem = curr.item;
            curr = curr.next;
            return currItem;
        }
    }

    @Override
    public void addFirst(T item) {
        Node oldFirst = sentinel.next;
        oldFirst.prev = new Node(item, oldFirst);
        oldFirst.prev.prev = sentinel;
        sentinel.next = oldFirst.prev;
        size++;
    }

    @Override
    public void addLast(T item) {
        Node oldLast = sentinel.prev;
        oldLast.next = new Node(item, sentinel);
        oldLast.next.prev = oldLast;
        sentinel.prev = oldLast.next;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        if (isEmpty()) {
            System.out.println("The Linked List Deque is currently empty.");
        }
        for (Node p = sentinel.next; p.item != null; p = p.next) {
            System.out.print(p.item + " ");
        }
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T oldFirstItem = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size--;
        return oldFirstItem;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T oldLastItem = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size--;
        return oldLastItem;
    }

    @Override
    public T get(int index) {
        if (isEmpty()) { return null; }
        Node p = sentinel.next;
        for (int i = 0; p.item != null; i++) {
            if (i == index) {
                return p.item;
            }
            p = p.next;
        }
        return null;
    }

    public T getRecursive(int index) {
        if (isEmpty()) { return null; }
        if (index < 0) { return null; }
        int curr = 0;
        Node p = sentinel.next;
        return getRecursiveHelper(index, curr, p);
    }

    private T getRecursiveHelper(int index, int curr, Node p) {
        if (p.item == null){ return null; }
        if (index == curr ) {
            return p.item;
        }
        return getRecursiveHelper(index, curr + 1, p.next);
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof LinkedListDeque otherLLD) {
            if (otherLLD.size != this.size) {
                return false;
            }
            for (int i = 0; i < size; i++) {
                if (!this.get(i).equals(otherLLD.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
