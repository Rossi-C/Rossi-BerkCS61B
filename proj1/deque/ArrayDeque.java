package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    public ArrayDeque(){
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 0;
        nextLast = 0;
    }

    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        int newIndex = 0;
        for (int i = 0; i < items.length; i++) {
            if(items[i] != null){
                a[newIndex] = items[i];
                newIndex += 1;
            }
        }
        items = a;
        nextFirst = items.length - 1;
        nextLast = size;
    }

    public void addFirst(T item){
        if(size == items.length){
            resize(size * 2);
        }

        items[nextFirst] = item;
        if(nextFirst == 0){
            nextFirst = items.length - 1;
        } else {
            nextFirst --;
        }
        size ++;
    }
    public void addLast(T item){
        if(size == items.length){
            resize(size * 2);
        }
        items[nextLast] = item;
        if(nextLast == items.length - 1){
            nextLast = 0;
        } else {
            nextLast = nextLast + 1;
        }
        size ++;
    }
    public boolean isEmpty(){
        if(size == 0){
            return true;
        }
        return false;
    }
    public int size(){
        return size;
    }
    public void printDeque(){
        if(isEmpty()){
            System.out.println("No items currently in the Array Deque");
        } else {
            for(int i = 0; i < items.length; i++){
                if(items[i] != null) {
                    System.out.print(items[i] + " ");
                }
            }
            System.out.println();
        }
    }
    public T removeFirst(){
        if(isEmpty()) {
            return null;
        }
        if(size <= items.length/4 && items.length >= 16){
            resize(size * 2);
        }

        int firstInd;
        T first;
        if(nextFirst == items.length - 1) {
            nextFirst = 0;
            firstInd = nextFirst;
        } else {
            nextFirst += 1;
            firstInd =  nextFirst;
        }

        first = items[firstInd];
        items[firstInd] = null;
        size--;
        return first;
    }
    public T removeLast(){
        if(isEmpty()){
            return null;
        }
        if(size <= items.length/4 && items.length >= 16){
            resize(size * 2);
        }

        int lastInd;
        T last;
        if(nextLast == 0) {
            nextLast = items.length - 1;
            lastInd = nextLast;
        } else {
            nextLast -= 1;
            lastInd =  nextLast;
        }

        last = items[lastInd];
        items[lastInd] = null;
        size--;
        return last;
    }
    public T get(int index){
        T returnVal;
        if(items[index] == null || index >= items.length){
            return null;
        }
        return returnVal = items[index];
    }

    @Override
    public Iterator<T> iterator() {
        return new ADIterator();
    }

    private class ADIterator implements Iterator<T> {
        int currPos;
        private ADIterator() {
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
    public boolean equals(Object other) {
        if (this == other) { return true; }
        if (other instanceof ArrayDeque otherAD) {
            if (this.size != otherAD.size) { return false; }
            for (int i = 0; i < size; i++) {
                if (!(this.get(i).equals(otherAD.get(i)))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
