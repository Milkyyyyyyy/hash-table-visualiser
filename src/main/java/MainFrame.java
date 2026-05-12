import logic.BSTHashTable;
import logic.BinarySearchTree;
import visual.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class MainFrame extends JFrame {
    private HashTableModel tableModel;
    private BSTHashTable myTable;
    private JTextField keyField;
    private JTable jTable;
    OperationLogPanel logPanel;


    BSTCanvas bstCanvas = new BSTCanvas();

    public MainFrame() {
        setTitle("Визуализация хеш-таблицы");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("Table.selectionInsets", new Insets(1, 1, 1, 1));
        UIManager.put("ScrollPane.smoothScrolling", true);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JLabel keyLabel = new JLabel("Число:");
        keyField = new JTextField(0);
        setupIntOnlyField(keyField);
        keyField.setPreferredSize(new Dimension(140, 27));
        keyField.putClientProperty("JTextField.placeholderText", "Введите число");

        Insets buttonInsets = new Insets(7, 5, 5, 7);

        JButton addButton = new JButton("Добавить");
        addButton.setBackground(new Color(242, 249, 244));
        addButton.setForeground(new Color(42, 111, 57));
        addButton.setMargin(buttonInsets);

        JButton removeButton = new JButton("Удалить");
        removeButton.setMargin(buttonInsets);
        removeButton.setBackground(new Color(254, 249, 249));
        removeButton.setForeground(new Color(184, 97, 101));

        JButton findButton = new JButton("Найти");
        findButton.setMargin(buttonInsets);
        findButton.setBackground(new Color(244, 249, 254));
        findButton.setForeground(new Color(30, 111, 218));

        JButton clearButton = new JButton("Очистить");
        clearButton.setMargin(buttonInsets);
        clearButton.setBackground(new Color(253, 253, 253));
        clearButton.setForeground(new Color(90, 91, 91));

        myTable = new BSTHashTable(2);
        tableModel = new HashTableModel(myTable);
        jTable = new JTable(tableModel);

        jTable.setRowHeight(80);
        jTable.setShowGrid(true);
        jTable.setGridColor(Color.LIGHT_GRAY);
        jTable.setSelectionBackground(new Color(239, 244, 250));
        jTable.setSelectionForeground(new Color(10, 10, 10));
        jTable.setForeground(new Color(10, 10, 10));
        jTable.getColumnModel().getColumn(0).setMaxWidth(100);
        jTable.getColumnModel().getColumn(1).setCellRenderer(new BSTRenderer());
        jTable.getTableHeader().setReorderingAllowed(false);
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jTable.getSelectionModel().addListSelectionListener(e ->{
            if(!e.getValueIsAdjusting()){
                int selectedRow = jTable.getSelectedRow();
                if(selectedRow != -1){
                    int modelRow = jTable.convertRowIndexToModel(selectedRow);

                    Object value = jTable.getModel().getValueAt(modelRow, 1);

                    if(value instanceof BinarySearchTree){
                        BinarySearchTree selectedTree = (BinarySearchTree) value;
                        bstCanvas.setTree(selectedTree);
                    }
                }
            }
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setVerticalAlignment(JLabel.CENTER);
        jTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        SmoothScrollPane scrollPane = new SmoothScrollPane(jTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.WEST);


        addButton.addActionListener(e -> {
            Integer value = readIntFromField();
            if (value == null) return;
            myTable.add(value);
            refreshTrees("Добавлено: " + value);
        });

        removeButton.addActionListener(e -> {
            Integer value = readIntFromField();
            if (value == null) return;
            myTable.remove(value);
            refreshTrees("Удалено: " + value);
        });

        findButton.addActionListener(e -> {
            Integer value = readIntFromField();
            if (value == null) return;
            boolean found = myTable.contains(value);
            refreshTrees(found ? "Найдено: " + value : "Не найдено: " + value);
        });

        clearButton.addActionListener(e -> {
            myTable.clear();
            refreshTrees("Таблица очищена");
        });



        controlPanel.add(keyLabel);
        controlPanel.add(keyField);
        controlPanel.add(addButton);
        controlPanel.add(removeButton);
        controlPanel.add(findButton);
        controlPanel.add(clearButton);


        bstCanvas.setPreferredSize(new Dimension(670, 270));
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(700, 300));
        rightPanel.add(bstCanvas, BorderLayout.NORTH);

        logPanel = new OperationLogPanel();
        rightPanel.add(logPanel, BorderLayout.CENTER);

        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        setContentPane(mainPanel);
    }

    private Integer readIntFromField() {
        String text = keyField.getText().trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void refreshTrees(String status) {
        tableModel.refresh();
        jTable.repaint();
        bstCanvas.repaint();
    }

    private void setupIntOnlyField(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string.matches("\\d+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text.isEmpty() || text.matches("\\d+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
}
