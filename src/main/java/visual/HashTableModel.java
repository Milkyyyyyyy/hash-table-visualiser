package visual;

import logic.BSTHashTable;

import javax.swing.table.AbstractTableModel;

public class HashTableModel extends AbstractTableModel {
    private final BSTHashTable hashTable;
    private final String[] columnNames = {"Индекс", "Бакет (BST)"};

    public HashTableModel(BSTHashTable hashTable) {
        this.hashTable = hashTable;
    }

    @Override
    public int getRowCount() {
        return hashTable.getCapacity();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return rowIndex;
        }
        return hashTable.getTreeAt(rowIndex);
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public void refresh() {
        fireTableDataChanged();
    }
}
