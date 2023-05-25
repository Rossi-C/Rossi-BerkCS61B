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
        Node newFirst = new Node(item,oldFirst);
        sentinel.next = newFirst;
        newFirst.prev = sentinel;
        size += 1;
    }
}
