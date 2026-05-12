package visual;

import logic.BSTHashTable;

import javax.swing.table.AbstractTableModel;

/**
 * Табличная модель для отображения хеш-таблицы в JTable.
 * Колонки: «Индекс» (int) и «Бакет (BST)» (BinarySearchTree).
 */
public class HashTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {"Индекс", "Бакет (BST)"};

    private final BSTHashTable hashTable;

    public HashTableModel(BSTHashTable hashTable) {
        this.hashTable = hashTable;
    }

    @Override
    public int getRowCount() {
        return hashTable.getCapacity();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return rowIndex;
        }
        return hashTable.getBucketAt(rowIndex);
    }

    /** Уведомляет таблицу, что данные изменились, и требует перерисовки. */
    public void refresh() {
        fireTableDataChanged();
    }
}
