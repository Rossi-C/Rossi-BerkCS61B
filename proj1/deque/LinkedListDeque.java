package deque;

public class LinkedListDeque<T> {
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

    LinkedListDeque(){
        sentinel = new Node(null, null);
        size = 0;
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
    }

    public void addFirst(T item){
        Node oldFirst = sentinel.next;
        oldFirst.prev = new Node(item,oldFirst);
        oldFirst.prev.prev = sentinel;
        sentinel.next = oldFirst.prev;
        size += 1;
    }

    public void addLast(T item){
        Node oldLast = sentinel.prev;
        oldLast.next = new Node(item, sentinel);
        oldLast.next.prev = oldLast;
        sentinel.prev = oldLast.next;
        size += 1;
    }

    public boolean isEmpty(){
        if (size == 0){
            return true;
        }
        return false;
    }

    public int size(){
        return size;
    }

    public void printDeque(){
        Node p = sentinel.next;
        while (p.item != null){
            System.out.println(p.item + " ");
            p = p.next;
        }
    }

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

}
