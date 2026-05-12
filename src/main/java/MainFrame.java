import animation.Step;
import animation.StepPlayer;
import logic.BSTHashTable;
import logic.BinarySearchTree;
import visual.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * Главное окно приложения.
 * Содержит таблицу бакетов, канвас для детального просмотра дерева
 * и панель журнала операций.
 */
public class MainFrame extends JFrame {

    private static final int INITIAL_TABLE_CAPACITY = 5;

    private BSTHashTable hashTable;
    private HashTableModel tableModel;
    private final BSTCanvas      bstCanvas;
    private final OperationLogPanel logPanel;
    private SmoothScrollPane scrollPane;

    private JTextField inputField;
    private JTable     jTable;

    private StepPlayer stepPlayer = null;

    public MainFrame() {
        setTitle("Визуализация хеш-таблицы");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        hashTable = new BSTHashTable(INITIAL_TABLE_CAPACITY);
        tableModel = new HashTableModel(hashTable);
        bstCanvas  = new BSTCanvas();
        logPanel   = new OperationLogPanel();
        logPanel.setPreferredSize(new Dimension(700, 200));

        stepPlayer = new StepPlayer(
                () -> {
                    tableModel.refresh();

                    // Выделяем строку таблицы если шаг этого требует
                    if(stepPlayer != null){
                        Step activeStep = stepPlayer.getActiveStep();
                        Integer row = activeStep == null ? null : activeStep.highlightTableRow;

                        if (row != null) {
                            jTable.setRowSelectionInterval(row, row);
                        } else {
                            jTable.clearSelection();
                        }
                    }

                    jTable.repaint();
                    bstCanvas.repaint();
                },
                logPanel::log
        );
        bstCanvas.setStepPlayer(stepPlayer);

        initComponents();
    }

    // ── Инициализация интерфейса ──────────────────────────────────────────────

    private void initComponents() {
        applyGlobalUiSettings();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(buildControlPanel(), BorderLayout.NORTH);
        mainPanel.add(buildTablePanel(),   BorderLayout.WEST);
        mainPanel.add(buildRightPanel(),   BorderLayout.EAST);

        setContentPane(mainPanel);
    }

    private void applyGlobalUiSettings() {
        UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("Table.selectionInsets", new Insets(1, 1, 1, 1));
        UIManager.put("ScrollPane.smoothScrolling", true);
    }

    // ── Панель управления (верх) ──────────────────────────────────────────────

    private JPanel buildControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(140, 27));
        inputField.putClientProperty("JTextField.placeholderText", "Введите число");
        setupIntOnlyFilter(inputField);

        JSlider animationSlider = new JSlider(50, 2000, 50);
        animationSlider.setPreferredSize(new Dimension(130, 30));
        animationSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                stepPlayer.setStepDelay(animationSlider.getValue());
            }
        });

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));

        JLabel sliderLabel = new JLabel("Скорость анимации");
        sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        animationSlider.setAlignmentX(Component.CENTER_ALIGNMENT);

        sliderPanel.add(sliderLabel);
        sliderPanel.add(animationSlider);

        JSlider tableSizeSlider = new JSlider(1, 20, INITIAL_TABLE_CAPACITY);
        tableSizeSlider.setPreferredSize(new Dimension(130, 38));
        tableSizeSlider.setMajorTickSpacing(5);
        tableSizeSlider.setMinorTickSpacing(1);
        tableSizeSlider.setPaintTicks(true);
        tableSizeSlider.setPaintLabels(true);

        JPanel sizeSliderPanel = new JPanel();
        sizeSliderPanel.setLayout(new BoxLayout(sizeSliderPanel, BoxLayout.Y_AXIS));

        JLabel sizeSliderLabel = new JLabel("Размер хеш-таблицы");
        sizeSliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tableSizeSlider.setAlignmentX(Component.CENTER_ALIGNMENT);

        tableSizeSlider.addChangeListener(e -> {
            if (!tableSizeSlider.getValueIsAdjusting()) {
                resizeHashTable(tableSizeSlider.getValue());
                sizeSliderLabel.setText("Размер хеш-таблицы: " + tableSizeSlider.getValue());
            }
        });

        sizeSliderPanel.add(sizeSliderLabel);
        sizeSliderPanel.add(tableSizeSlider);

        panel.add(sizeSliderPanel);
        panel.add(new JLabel("Число:"));
        panel.add(inputField);
        panel.add(buildButton("Добавить",  new Color(242, 249, 244), new Color(42,  111, 57),  this::onAdd));
        panel.add(buildButton("Удалить",   new Color(254, 249, 249), new Color(184, 97,  101), this::onRemove));
        panel.add(buildButton("Найти",     new Color(244, 249, 254), new Color(30,  111, 218), this::onFind));
        panel.add(buildButton("Очистить",  new Color(253, 253, 253), new Color(90,  91,  91),  this::onClear));
        panel.add(sliderPanel);
        panel.add(buildButton("Рандомизировать", new Color(253, 253, 253), new Color(90,  91,  91), this::onRandomize));


        return panel;
    }

    private JButton buildButton(String label, Color bg, Color fg, Runnable action) {
        JButton button = new JButton(label);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setMargin(new Insets(7, 5, 5, 7));
        button.addActionListener(e -> action.run());
        return button;
    }

    // ── Обработчики кнопок ────────────────────────────────────────────────────

    private void onAdd() {
        Integer value = readInput();
        if (value == null) return;
        int bucketIndex = hashTable.getBucketIndex(value);
        List<Step> steps = hashTable.getBucketAt(bucketIndex).stepsForInsert(value);
        tableModel.refresh();
        stepPlayer.play(steps);
        refresh();
    }
    private void addWithoutAnim(int value){
        hashTable.add(value);
        refresh();
    }

    private void onRemove() {
        Integer value = readInput();
        if (value == null) return;
        int bucketIndex = hashTable.getBucketIndex(value);
        List<Step> steps = hashTable.getBucketAt(bucketIndex).stepsForRemove(value);
        tableModel.refresh();
        stepPlayer.play(steps);
        refresh();
    }

    private void onFind() {
        Integer value = readInput();
        if (value == null) return;
        int bucketIndex = hashTable.getBucketIndex(value);
        List<Step> steps = hashTable.getBucketAt(bucketIndex).stepsForContains(value);
        tableModel.refresh();
        stepPlayer.play(steps);
        refresh();
    }

    private void onClear() {
        hashTable.clear();
        refresh();
    }
    private void onRandomize(){
        onClear();
        Random random = new Random();
        int amount = random.nextInt(2, 4) * hashTable.getCapacity();
        int count = 0;
        while(count < amount){
            int nextValue = random.nextInt(0, 300);
            if(!hashTable.contains(nextValue)){
                addWithoutAnim(nextValue);
                count++;
            }
        }
    }

    // ── Таблица бакетов (левая часть) ────────────────────────────────────────

    private JScrollPane buildTablePanel() {
        jTable = new JTable(tableModel);
        jTable.setRowHeight(80);
        jTable.setShowGrid(true);
        jTable.setGridColor(Color.LIGHT_GRAY);
        jTable.setSelectionBackground(new Color(239, 244, 250));
        jTable.setSelectionForeground(new Color(10, 10, 10));
        jTable.setForeground(new Color(10, 10, 10));
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable.getTableHeader().setReorderingAllowed(false);

        // Колонка индекса — по центру, фиксированная ширина
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setVerticalAlignment(JLabel.CENTER);
        jTable.getColumnModel().getColumn(0).setMaxWidth(100);
        jTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        // Колонка дерева — кастомный рендерер
        jTable.getColumnModel().getColumn(1).setCellRenderer(new BSTRenderer(stepPlayer));

        // При выборе строки показываем соответствующее дерево на канвасе
        jTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onTableRowSelected();
            }
        });

        scrollPane = new SmoothScrollPane(jTable);

        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    private void onTableRowSelected() {
        int selectedRow = jTable.getSelectedRow();
        if (selectedRow == -1) return;

        int modelRow = jTable.convertRowIndexToModel(selectedRow);
        Object value = jTable.getModel().getValueAt(modelRow, 1);

        if (value instanceof BinarySearchTree selectedTree) {
            bstCanvas.setTree(selectedTree);
        }
        scrollPane.smoothScrollToRow();
    }

    // ── Правая панель (канвас + лог) ─────────────────────────────────────────

    private JPanel buildRightPanel() {
        bstCanvas.setPreferredSize(new Dimension(670, 270));

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(bstCanvas, BorderLayout.NORTH);
        rightPanel.add(logPanel,  BorderLayout.CENTER);
        return rightPanel;
    }

    // ── Вспомогательные методы ────────────────────────────────────────────────

    /** Считывает целое число из поля ввода. Возвращает null, если поле пустое или некорректное. */
    private Integer readInput() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return null;
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /** Обновляет таблицу и канвас после каждой операции. */
    private void refresh() {
        tableModel.refresh();
        jTable.repaint();
        bstCanvas.repaint();
    }

    /**
     * Устанавливает фильтр документа, разрешающий вводить только цифры.
     * Допускает пустую строку и опциональный минус в начале для будущего расширения.
     */
    private void setupIntOnlyFilter(JTextField field) {
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
    private void resizeHashTable(int newCapacity) {
        if (newCapacity < 1) return;

        hashTable = new BSTHashTable(newCapacity);
        tableModel = new HashTableModel(hashTable);

        jTable.setModel(tableModel);
        jTable.getColumnModel().getColumn(1).setCellRenderer(new BSTRenderer(stepPlayer));

        bstCanvas.setTree(null);
        jTable.clearSelection();
        refresh();
    }
}
