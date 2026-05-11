package logic;

public class BSTHashTable<K extends Comparable<K>, V> {

    private final int capacity;
    private final BinarySearchTree<K, V>[] table;

    @SuppressWarnings("unchecked")
    public BSTHashTable(int capacity) {
        this.capacity = capacity;
        // Создаем массив деревьев
        table = (BinarySearchTree<K, V>[]) new BinarySearchTree[capacity];

        // Инициализируем каждую корзину пустым деревом
        for (int i = 0; i < capacity; i++) {
            table[i] = new BinarySearchTree<>();
        }
    }

    // Вычисление индекса корзины на основе хеш-кода
    private int hash(K key) {
        // Убираем знак минус с помощью побитового И и берем остаток от деления
        return (key.hashCode() & 0x7fffffff) % capacity;
    }

    // Добавление элемента
    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("Ключ не может быть null");
        int index = hash(key);
        table[index].put(key, value);
    }

    // Получение элемента
    public V get(K key) {
        if (key == null) throw new IllegalArgumentException("Ключ не может быть null");
        int index = hash(key);
        return table[index].get(key);
    }

    // Удаление элемента
    public void remove(K key) {
        if (key == null) throw new IllegalArgumentException("Ключ не может быть null");
        int index = hash(key);
        table[index].remove(key);
    }
    public int getCapacity(){
        return this.capacity;
    }
    public BinarySearchTree<K, V> getTreeAt(int index) {
        return table[index];
    }
}
