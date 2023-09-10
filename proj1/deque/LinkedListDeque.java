package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T>{

    public class Node {
        public T item;
        public Node next;
        public Node prev;
        public Node (T i, Node n){
            item = i;
            next = n;
        }
    }

    private Node sentinel;
    private int size;

    public LinkedListDeque(){
        sentinel = new Node(null, null);
        size = 0;
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
    }

    @Override
    public Iterator<T> iterator() {
        return new LLDIterator();
    }

    private class LLDIterator implements Iterator<T> {
        int currPos;
        private LLDIterator() {
            currPos = 0;
        }
        @Override
        public boolean hasNext() {
            return currPos < size;
        }

        @Override
        public T next() {
            T currItem = get(currPos);
            currPos++;
            return currItem;
        }
    }

    @Override
    public void addFirst(T item){
        Node oldFirst = sentinel.next;
        oldFirst.prev = new Node(item,oldFirst);
        oldFirst.prev.prev = sentinel;
        sentinel.next = oldFirst.prev;
        size += 1;
    }

    @Override
    public void addLast(T item){
        Node oldLast = sentinel.prev;
        oldLast.next = new Node(item, sentinel);
        oldLast.next.prev = oldLast;
        sentinel.prev = oldLast.next;
        size += 1;
    }

    @Override
    public int size(){
        return size;
    }

    @Override
    public void printDeque(){
        Node p = sentinel.next;
        while (p.item != null){
            System.out.println(p.item + " ");
            p = p.next;
        }
    }

    @Override
    public T removeFirst(){
        if (isEmpty()) {
            return null;
        }

        T removee = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size -= 1;

        return removee;
    }

    @Override
    public T removeLast(){
        if (isEmpty()){
            return null;
        }

        T removee = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;
        sentinel.prev.next = sentinel;
        size -= 1;

        return removee;
    }

    @Override
    public T get(int index){
        if (isEmpty()){
            return null;
        }

        Node p = sentinel.next;
        for (int i = 0; p.item !=null; i++){
            if (i == index){
                return p.item;
            }
            p = p.next;
        }
        return null;
    }

    private T getRecursiveHelper(int index, Node currNode, int currIndex){
        if (currNode.item == null){
            return null;
        }
        if (currIndex == index) {
            return currNode.item;
        }
        return getRecursiveHelper(index, currNode.next, currIndex + 1);
    }

    public T getRecursive(int index){
        if (isEmpty()){
            return null;
        }
        if (index < 0){
            return null;
        }

        Node currNode = sentinel.next;
        int currIndex = 0;

        return getRecursiveHelper(index, currNode, currIndex);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof LinkedListDeque otherLLD) {
            if (this.size != otherLLD.size) {
                return false;
            }
            for (int i = 0; i < size; i++) {
                if (!(this.get(i).equals(otherLLD.get(i)))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
