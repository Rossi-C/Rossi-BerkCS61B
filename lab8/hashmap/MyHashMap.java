package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @Rossi YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    // You should probably define some more!

    //hash table size
    private int numBuckets;
    //number of key-value pairs
    private int keyValuePairs;
    //load factor to determine resizing. If keyValuePairs/numBuckets exceeds loadFactor, then resize.
    private double loadFactor;
    //default size/number of buckets in the hashmap
    private static int initSize = 16;
    //Hash Set that contains all keys
    private Set<K> keys = new HashSet<>();

    /** Constructors */
    public MyHashMap() {
        this(initSize);
    }

    public MyHashMap(int initialSize) {
        numBuckets = initialSize;
        loadFactor = .75;
        buckets = createTable(numBuckets);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        numBuckets = initialSize;
        loadFactor = maxLoad;
        buckets = createTable(numBuckets);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    private int hash(K key) {
        return key.hashCode() % numBuckets;
    }

    @Override
    public void clear() {
        keys.clear();
        for (Collection<Node> bucket : buckets) {
            bucket.clear();
        }
        keyValuePairs = 0;
    }

    @Override
    public boolean containsKey(K key) {
        if (keys.contains(key)) return true;
        return false;
    }

    @Override
    public V get(K key) {
        int i = hash(key);
        Collection<Node> bucket = buckets[i];
        if (bucket == null) return null;
        if (!bucket.contains(key)) return null;
        Iterator<Node> bucketIterator = bucket.iterator();
        while (bucketIterator.hasNext()) {
            Node nextNode = bucketIterator.next();
            if (nextNode.key == key) {
                return nextNode.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return keyValuePairs;
    }

    /**
     * scenario 1: no bucket at the new keys hash position in buckets.
     * create a new bucket at position i and then add the node
     * scenario 2: bucket already contains the key given. Check if value given
     * is same as existing value. If not override existing value with new one
     * Scenario 3: bucket does not contain the key. new node is created with given
     * key value pair and inserted in bucket i.
     */
    @Override
    public void put(K key, V value) {
        int i = hash(key);
        Iterator<Node> bucketIterator = buckets[i].iterator();
        if (buckets[i] == null) {
            buckets[i] = createBucket();
            Node newNode = createNode(key, value);
            buckets[i].add(newNode);
        } else if (buckets[i].contains(key)) {
            while (bucketIterator.hasNext()) {
                Node nextNode = bucketIterator.next();
                if (nextNode.key == key && nextNode.value != value) {
                    nextNode.value = value;
                }
            }
        } else {
            Node newNode = createNode(key, value);
            keyValuePairs += 1;
            if ((keyValuePairs/numBuckets) > loadFactor) {
                resize();
            }
            buckets[i].add(newNode);
        }
        if (!keys.contains(key)) keys.add(key);
    }

    private void resize(){
        numBuckets *= 2;
        Collection<Node>[] newBuckets = createTable(numBuckets);
        for (Collection<Node> bucket : buckets) {
            if (bucket != null) {
                for (Node item : bucket) {

                }
            }
        }
    }

    @Override
    public Set<K> keySet() {
        return keys;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }

    private class MyHashMapIterator implements Iterator<K> {
        Set<K> usedKeys = new HashSet<>();

        @Override
        public boolean hasNext() {
            if (usedKeys.size() == keys.size()){
                return false;
            }
            return true;
        }

        @Override
        public K next() {
            if (!hasNext()) {
                return null;
            }
            for (Collection<Node> bucket : buckets) {
                if (bucket != null) {
                    for (Node node : bucket) {
                        if (!usedKeys.contains(node.key)) {
                            usedKeys.add(node.key);
                            return node.key;
                        }
                    }
                }
            }
           return null;
        }
    }
}
