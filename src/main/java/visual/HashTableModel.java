package visual;

import logic.BSTHashTable;

import javax.swing.table.AbstractTableModel;

public class HashTableModel extends AbstractTableModel{
    private final BSTHashTable<Integer, String> hashTable;
    private final String[] columnNames = {"Индекс", "Бакет (BST)"};

    public HashTableModel(BSTHashTable<Integer, String> hashTable) {
        this.hashTable = hashTable;
    }

    @Override
    public int getRowCount(){
        return hashTable.getCapacity();
    }

    @Override
    public int getColumnCount(){
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return rowIndex; // Первая колонка: индекс
        } else {
            return hashTable.getTreeAt(rowIndex); // Вторая колонка: само дерево
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    // Метод для уведомления таблицы об изменениях
    public void refresh() {
        fireTableDataChanged();
    }
}
