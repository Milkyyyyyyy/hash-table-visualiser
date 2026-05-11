import logic.BSTHashTable;
import visual.BSTRenderer;
import visual.HashTableModel;
import visual.SmoothScrollPane;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class MainFrame extends JFrame {
    private HashTableModel tableModel;
    private BSTHashTable<Integer, String> myTable;

    public MainFrame() {
        // 1. Базовые настройки окна
        setTitle("Визуализация хеш-таблицы");            // Заголовок окна
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Закрывать программу при закрытии крестиком
        setSize(1200, 700);                   // Размеры окна (ширина, высота)
        setLocationRelativeTo(null);                     // Разместить окно по центру экрана
        setResizable(false);

        // 2. Инициализация пользовательского интерфейса
        initComponents();
    }

    private void initComponents() {
        // Создаем главную панель (контейнер) для размещения элементов
        JPanel mainPanel = new JPanel();

        UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("Table.selectionInsets", new Insets(1, 1, 1, 1));
        UIManager.put("ScrollPane.smoothScrolling", true);

        // Устанавливаем менеджер компоновки (BorderLayout удобен для разделения на зоны: центр, края)
        mainPanel.setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        // Ключ
        JLabel keyLabel = new JLabel("Ключ: ");
        JTextField keyField = new JTextField(0);
        setupIntOnlyField(keyField);
        keyField.setPreferredSize(new Dimension(140, 27));
        keyField.putClientProperty("JTextField.placeholderText", "Введите ключ");

        Insets buttonInsets = new Insets(7, 5, 5, 7);

        // Кнопка добавить
        JButton addButton = new JButton("Добавить");
        // addButton.setPreferredSize(new Dimension(82, 27));
        addButton.setBackground(new Color(242,249,244));
        addButton.setForeground(new Color(42,111,57));
        addButton.setMargin(buttonInsets);

        // Кнопка удалить
        JButton removeButton = new JButton("Удалить");
        removeButton.setMargin(buttonInsets);
        removeButton.setBackground(new Color(254,249,249));
        removeButton.setForeground(new Color(184,97,101));

        // Кнопка найти
        JButton findButton = new JButton("Найти");
        findButton.setMargin(buttonInsets);
        findButton.setBackground(new Color(244,249,254));
        findButton.setForeground(new Color(30,111,218));

        // Кнопка очистить
        JButton clearButton = new JButton("Очистить");
        clearButton.setMargin(buttonInsets);
        clearButton.setBackground(new Color(253,253,253));
        clearButton.setForeground(new Color(90,91,91));

        // ТАБЛИЦА
        myTable = new BSTHashTable<>(15);
        tableModel = new HashTableModel(myTable);
        JTable jTable = new JTable(tableModel);

        // Настройка внешнего вида таблицы
        jTable.setRowHeight(80);
        jTable.setShowGrid(true);
        jTable.setGridColor(Color.LIGHT_GRAY);
        jTable.setSelectionBackground(new Color(239,244,250));
        jTable.setSelectionForeground(new Color(10, 10, 10));
        jTable.setForeground(new Color(10, 10, 10));
        jTable.getColumnModel().getColumn(0).setMaxWidth(100);
        jTable.getColumnModel().getColumn(1).setCellRenderer(new BSTRenderer());

        jTable.getTableHeader().setReorderingAllowed(false);
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER); // Центровка по горизонтали
        centerRenderer.setVerticalAlignment(JLabel.CENTER);   // Центровка по вертикали (если ячейка высокая)
        jTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        SmoothScrollPane scrollPane = new SmoothScrollPane(jTable);

        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        mainPanel.add(scrollPane, BorderLayout.WEST);

        // Добавляем всё на панель
        controlPanel.add(keyLabel);
        controlPanel.add(keyField);
        controlPanel.add(addButton);
        controlPanel.add(removeButton);
        controlPanel.add(findButton);
        controlPanel.add(clearButton);

        // Добавляем панель в верхнюю часть окна
        mainPanel.add(controlPanel, BorderLayout.NORTH);

        // TODO визуализация таблицы, журнал операций, визуализация дерева

        // Прикрепляем главную панель к самому окну
        setContentPane(mainPanel);
    }
    private void setupIntOnlyField(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string.matches("\\d+")) { // Проверка: только цифры
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text.matches("\\d+")) { // Проверка: только цифры
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
}