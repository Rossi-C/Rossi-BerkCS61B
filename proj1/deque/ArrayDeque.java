package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 3;
        nextLast = 4;
    }

    private void resize(int capacity) {
        T[] newItems = (T[]) new Object[capacity];
        int currIndex = capacity / 4;
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null){
                newItems[currIndex] = items[i];
                currIndex++;
            }
        }
        nextFirst = (capacity / 4) - 1;
        nextLast = currIndex;
        items = newItems;
    }

    private void resizeUp(int capacity) {
        T[] newItems = (T[]) new Object[capacity];
        int startingPos = capacity / 4;
        if (nextFirst == items.length - 1) {
            System.arraycopy(items, 0, newItems, startingPos, size);
        } else {
            System.arraycopy(items, nextFirst + 1, newItems, startingPos, size - (nextFirst + 1));
            System.arraycopy(items, 0, newItems, startingPos + size - (nextFirst + 1), nextFirst + 1);
        }
        items = newItems;
        nextFirst = startingPos - 1;
        nextLast = startingPos + size;
    }

    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resizeUp(size * 2);
        }
        items[nextFirst] = item;
        size++;
        if (nextFirst == 0) {
            nextFirst = items.length - 1;
        } else {
            nextFirst--;
        }
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resizeUp(size * 2);
        }
        items[nextLast] = item;
        size++;
        if (nextLast == items.length - 1) {
            nextLast = 0;
        } else {
            nextLast++;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        if (isEmpty()) {
            System.out.println("The Array Deque is currently empty.");
        }
        for (int i = nextFirst + 1, j = items.length; j > 0; i++, j--) {
            if (i == items.length) { i = 0; }
            if (items[i] != null) {
                System.out.print(items[i] + " ");
            }
        }
    }

    @Override
    public T removeFirst() {
        if (size == 0) { return null; }
        if (size <= items.length / 4 && items.length >= 16 ) {
            resize(size * 2);
        }
        T oldFirst;
        if (nextFirst == items.length - 1) {
            oldFirst = items[0];
            nextFirst = 0;
        } else {
            oldFirst = items[nextFirst + 1];
            nextFirst++;
        }
        items[nextFirst] = null;
        size--;
        return oldFirst;
    }

    @Override
    public T removeLast() {
        if (size == 0) { return null; }
        if (size <= items.length / 4 && items.length >= 16 ) {
            resize(size * 2);
        }
        T oldLast;
        if (nextLast == 0) {
            oldLast = items[items.length - 1];
            nextLast = items.length - 1;
        } else {
            oldLast = items[nextLast - 1];
            nextLast--;
        }
        items[nextLast] = null;
        size--;
        return oldLast;
    }

    @Override
    public T get(int index) {
        if (index < items.length && items[index] != null){
            return items[index];
        }
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        return new ADIterator();
    }

    private class ADIterator implements Iterator<T> {
        int currPosition;

        public ADIterator() {
            currPosition = 0;
        }

        @Override
        public boolean hasNext() {
            return currPosition < size;
        }

        @Override
        public T next() {
            T currItem = get(currPosition);
            currPosition++;
            return currItem;
        }
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ArrayDeque otherAD) {
            if (otherAD.size != this.size) {
                return false;
            }
            for (int i = 0; i < size; i++) {
                if (!this.get(i).equals(otherAD.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
