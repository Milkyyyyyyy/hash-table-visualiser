package visual;

import logic.BSTHashTable;

import javax.swing.table.AbstractTableModel;

/**
 * Табличная модель для отображения содержимого хеш-таблицы в JTable.
 *
 * Представляет данные с двумя колонками:
 * 1. «Индекс» — номер бакета (0, 1, 2, ...)
 * 2. «Бакет (BST)» — бинарное дерево поиска, соде��жащееся в этом бакете
 *
 * Количество строк таблицы равно ёмкости хеш-таблицы.
 */
public class HashTableModel extends AbstractTableModel {

    /** Имена колонок таблицы. */
    private static final String[] COLUMN_NAMES = {"Индекс", "Бакет (BST)"};

    /** Хеш-таблица, данные которой отображаются. */
    private final BSTHashTable hashTable;

    /**
     * Создаёт модель таблицы для заданной хеш-таблицы.
     *
     * @param hashTable хеш-таблица для отображения
     */
    public HashTableModel(BSTHashTable hashTable) {
        this.hashTable = hashTable;
    }

    /**
     * Возвращает количество строк в таблице.
     * Равно ёмкости хеш-таблицы (одна строка на один бакет).
     *
     * @return количество бакетов в таблице
     */
    @Override
    public int getRowCount() {
        return hashTable.getCapacity();
    }

    /**
     * Возвращает количество колонок в таблице.
     *
     * @return 2 (Индекс и Бакет)
     */
    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    /**
     * Возвращает имя колонки.
     *
     * @param column номер колонки
     * @return имя колонки
     */
    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    /**
     * Возвращает значение ячейки по её позиции.
     *
     * @param rowIndex номер строки (индекс бакета)
     * @param columnIndex номер колонки (0 = индекс, 1 = дерево)
     * @return значение ячейки
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return rowIndex;  // Первая колонка — индекс бакета
        }
        return hashTable.getBucketAt(rowIndex);  // Вторая колонка — дерево поиска
    }

    /**
     * Уведомляет таблицу об изменении данных и запускает перерисовку.
     * Следует вызывать после каждой операции добавления/удаления элемента.
     */
    public void refresh() {
        fireTableDataChanged();
    }
}
