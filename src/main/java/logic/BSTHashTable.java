package logic;

import visual.OperationLogPanel;

/**
 * Хеш-таблица с открытой адресацией через цепочки BST.
 * Каждый бакет — отдельное бинарное дерево поиска.
 */
public class BSTHashTable {

    private final int capacity;
    private final BinarySearchTree[] buckets;

    public BSTHashTable(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Ёмкость таблицы должна быть положительной");
        }
        this.capacity = capacity;
        this.buckets = new BinarySearchTree[capacity];
        for (int i = 0; i < capacity; i++) {
            buckets[i] = new BinarySearchTree(this);
        }
    }

    /** Возвращает индекс бакета для заданного значения. */
    private int hash(int value) {
        return Math.floorMod(value, capacity);
    }

    public int getBucketIndex(int value){
        return hash(value);
    }


    public void add(int value) {
        int hash = hash(value);
        buckets[hash].insert(value);

    }

    public boolean contains(int value) {
        return buckets[hash(value)].contains(value);
    }

    public void remove(int value) {
        buckets[hash(value)].remove(value);
    }

    public void clear() {
        for (BinarySearchTree bucket : buckets) {
            bucket.clear();
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public BinarySearchTree getBucketAt(int index) {
        return buckets[index];
    }
}
