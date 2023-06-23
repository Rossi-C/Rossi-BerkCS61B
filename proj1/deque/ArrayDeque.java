package deque;

public class ArrayDeque<T> {
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    public ArrayDeque(){
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }
    public void addFirst(T item){
        /*come back and implement this later
        if(size == items.length){
            resize()
        }
        */
        items[nextFirst] = item;
        if(nextFirst == 0){
            nextFirst = items.length - 1;
        } else {
            nextFirst --;
        }
        size ++;
    }
    public void addLast(T item){
        /*come back and implement this later
        if(size == items.length){
            resize()
        }
        */
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
        if(isEmpty()){
            return null;
        }

        int firstInd;
        T first;
        if(nextFirst == items.length - 1) {
            firstInd = 0;
        } else {
            firstInd =  nextFirst + 1;
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

        int lastInd;
        T last;
        if(nextLast == 0) {
            lastInd = items.length - 1;
        } else {
            lastInd =  nextLast - 1;
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
}
