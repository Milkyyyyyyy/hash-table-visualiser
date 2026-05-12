package logic;

/**
 * Хеш-таблица с открытой адресацией (цепочками) на основе бинарных деревьев поиска.
 *
 * Структура данных:
 * - Использует разрешение коллизий методом цепочек (chaining)
 * - Каждый бакет (ячейка таблицы) содержит отдельное бинарное дерево поиска
 * - Элементы распределяются по бакетам с помощью хеш-функции
 *
 * Сложность операций:
 * - Добавление/Удаление/Поиск: O(log n) в среднем, O(n) в худшем случае
 * - где n — количество элементов в целевом бакете
 */
public class BSTHashTable {

    /** Количество бакетов в таблице. */
    private final int capacity;

    /** Массив бакетов, каждый из которых — отдельное бинарное дерево поиска. */
    private final BinarySearchTree[] buckets;

    /**
     * Создаёт новую хеш-таблицу с заданной ёмкостью.
     *
     * @param capacity количество бакетов (должно быть положительным)
     * @throws IllegalArgumentException если capacity <= 0
     */
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

    /**
     * Вычисляет индекс бакета для заданного значения.
     *
     * Использует MurmurHash-подобный алгоритм для хорошего распределения элементов.
     *
     * @param value значение для хеширования
     * @return индекс бакета в диапазоне [0, capacity)
     */
    private int hash(int value) {
        int h = value;
        h ^= (h >>> 16);
        h *= 0x9E3779B9;
        h ^= (h >>> 16);
        return Math.floorMod(h, capacity);
    }

    /**
     * Возвращает индекс бакета для заданного значения.
     *
     * @param value значение для поиска
     * @return индекс бакета
     */
    public int getBucketIndex(int value) {
        return hash(value);
    }

    /**
     * Добавляет значение в таблицу.
     * Если значение уже существует, оно не добавляется (дубликаты игнорируются).
     *
     * @param value значение для добавления
     */
    public void add(int value) {
        int hash = hash(value);
        buckets[hash].insert(value);
    }

    /**
     * Проверяет наличие значения в таблице.
     *
     * @param value значение для поиска
     * @return true, если значение находится в таблице; false в противном случае
     */
    public boolean contains(int value) {
        return buckets[hash(value)].contains(value);
    }

    /**
     * Удаляет значение из таблицы.
     * Если значения нет, операция не вызывает ошибку.
     *
     * @param value значение для удаления
     */
    public void remove(int value) {
        buckets[hash(value)].remove(value);
    }

    /**
     * Удаляет все элементы из таблицы.
     */
    public void clear() {
        for (BinarySearchTree bucket : buckets) {
            bucket.clear();
        }
    }

    /**
     * Возвращает количество бакетов в таблице.
     *
     * @return ёмкость таблицы
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Возвращает бакет (дерево поиска) по индексу.
     *
     * @param index индекс бакета
     * @return бинарное дерево поиска в этом бакете
     */
    public BinarySearchTree getBucketAt(int index) {
        return buckets[index];
    }
}
