package logic;

public class BSTHashTable {

    private final int capacity;
    private final BinarySearchTree[] table;

    public BSTHashTable(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.table = new BinarySearchTree[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new BinarySearchTree();
        }
    }

    private int hash(int value) {
        return Math.floorMod(value, capacity);
    }

    public void add(int value) {
        table[hash(value)].insert(value);
    }

    public boolean contains(int value) {
        return table[hash(value)].contains(value);
    }

    public void remove(int value) {
        table[hash(value)].remove(value);
    }

    public void clear() {
        for (BinarySearchTree tree : table) {
            tree.clear();
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public BinarySearchTree getTreeAt(int index) {
        return table[index];
    }
}
