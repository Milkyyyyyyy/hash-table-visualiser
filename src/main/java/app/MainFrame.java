package app;

import logic.hashtable.BSTHashTable;
import logic.hashtable.HashTableAlgorithmRunner;
import logic.tree.BinarySearchTree;
import model.settings.HashTableAlgorithmSettings;
import util.AppLogger;
import visual.animation.Step;
import visual.animation.StepPlayer;
import visual.common.SmoothScrollPane;
import visual.log.OperationLogPanel;
import visual.table.BSTRenderer;
import visual.table.HashTableModel;
import visual.table.SelectedCellBorderRenderer;
import visual.tree.BSTCanvas;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Главное окно приложения.
 * Содержит таблицу бакетов, канвас для детального просмотра дерева
 * и панель журнала операций.
 */
public class MainFrame extends JFrame {

    private static final int INITIAL_TABLE_CAPACITY = 5;
    private static final String ALGORITHM_NAME = "Визуализация хеш-таблицы";
    private static final String ALGORITHM_DESCRIPTION =
            "Алгоритм визуализирует хеш-таблицу с разрешением коллизий цепочками. " +
            "Каждый бакет хранит бинарное дерево поиска, а операции добавления, " +
            "поиска и удаления показываются пошагово.";

    private final Logger logger = AppLogger.getLogger(MainFrame.class);

    private final HashTableAlgorithmRunner algorithmRunner;
    private HashTableAlgorithmSettings algorithmSettings;

    private BSTHashTable hashTable;
    private HashTableModel tableModel;
    private final BSTCanvas bstCanvas;
    private final OperationLogPanel logPanel;
    private SmoothScrollPane scrollPane;

    private JTextField inputField;
    private JTable jTable;
    private JSlider tableSizeSlider;
    private JLabel sizeSliderLabel;
    private boolean suppressTableResizeEvents = false;

    private StepPlayer stepPlayer = null;

    public MainFrame() {
        setTitle("Визуализация хеш-таблицы");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        algorithmRunner = new HashTableAlgorithmRunner();
        algorithmSettings = new HashTableAlgorithmSettings(
                ALGORITHM_NAME,
                ALGORITHM_DESCRIPTION,
                INITIAL_TABLE_CAPACITY
        );
        algorithmRunner.run(algorithmSettings);
        hashTable = algorithmRunner.getCurrentTable();

        tableModel = new HashTableModel(hashTable);
        bstCanvas  = new BSTCanvas();
        logPanel   = new OperationLogPanel();

        stepPlayer = new StepPlayer(
                () -> {
                    tableModel.refresh();

                    if (stepPlayer != null && jTable != null) {
                        Step activeStep = stepPlayer.getActiveStep();
                        Integer row = activeStep == null ? null : activeStep.highlightTableRow;

                        if (row != null && row >= 0 && row < jTable.getRowCount()) {
                            jTable.setRowSelectionInterval(row, row);
                        } else {
                            jTable.clearSelection();
                        }
                    }

                    if (jTable != null) {
                        jTable.repaint();
                    }
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

        setJMenuBar(buildMenuBar());
        setContentPane(mainPanel);
    }

    private void applyGlobalUiSettings() {
        UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("Table.selectionInsets", new Insets(1, 1, 1, 1));
        UIManager.put("ScrollPane.smoothScrolling", true);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Файл");
        JMenuItem saveItem = new JMenuItem("Сохранить набор...");
        JMenuItem loadItem = new JMenuItem("Загрузить набор...");
        JMenuItem exitItem = new JMenuItem("Выход");

        saveItem.addActionListener(e -> onSave());
        loadItem.addActionListener(e -> onLoad());
        exitItem.addActionListener(e -> dispose());

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Справка");
        JMenuItem descriptionItem = new JMenuItem("Описание алгоритма");
        descriptionItem.addActionListener(e -> showAlgorithmDescription());
        helpMenu.add(descriptionItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    // ── Панель управления (верх) ──────────────────────────────────────────────

    private JPanel buildControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(140, 27));
        inputField.putClientProperty("JTextField.placeholderText", "Введите число");
        setupIntOnlyFilter(inputField);

        JSlider animationSlider = new JSlider(10, 2000, 500);
        stepPlayer.setStepDelay(animationSlider.getValue());
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

        tableSizeSlider = new JSlider(1, 20, INITIAL_TABLE_CAPACITY);
        tableSizeSlider.setPreferredSize(new Dimension(130, 40));
        tableSizeSlider.setMajorTickSpacing(5);
        tableSizeSlider.setMinorTickSpacing(1);
        tableSizeSlider.setPaintTicks(true);
        tableSizeSlider.setPaintLabels(true);

        JPanel sizeSliderPanel = new JPanel();
        sizeSliderPanel.setLayout(new BoxLayout(sizeSliderPanel, BoxLayout.Y_AXIS));

        sizeSliderLabel = new JLabel("Размер хеш-таблицы: " + INITIAL_TABLE_CAPACITY);
        sizeSliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        tableSizeSlider.setAlignmentX(Component.CENTER_ALIGNMENT);

        tableSizeSlider.addChangeListener(e -> {
            if (suppressTableResizeEvents || tableSizeSlider.getValueIsAdjusting()) {
                return;
            }
            resizeHashTable(tableSizeSlider.getValue());
        });

        sizeSliderPanel.add(sizeSliderLabel);
        sizeSliderPanel.add(tableSizeSlider);

        panel.add(sizeSliderPanel);
        panel.add(new JLabel("Число:"));
        panel.add(inputField);
        panel.add(buildButton("Добавить",  new Color(242, 249, 244), new Color(42,  111, 57),  this::onAdd));
        panel.add(buildButton("Удалить",   new Color(254, 249, 249), new Color(184, 97,  101), this::onRemove));
        panel.add(buildButton("Найти",     new Color(244, 249, 254), new Color(30,  111, 218), this::onFind));
        panel.add(buildButton("Очистить",   new Color(253, 253, 253), new Color(90,  91,  91), this::onClear));
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
        if (value == null) {
            logger.warning("Попытка добавить некорректное значение");
            return;
        }

        logger.info("Добавление значения: " + value);
        int bucketIndex = hashTable.getBucketIndex(value);
        List<Step> steps = hashTable.getBucketAt(bucketIndex).stepsForInsert(value);
        tableModel.refresh();
        stepPlayer.play(steps);
        refresh();
    }

    private void addWithoutAnim(int value){
        hashTable.add(value);
        logger.fine("Добавлено без анимации: " + value);
        refresh();
    }

    private void onRemove() {
        Integer value = readInput();
        if (value == null) {
            logger.warning("Попытка удалить некорректное значение");
            return;
        }

        logger.info("Удаление значения: " + value);
        int bucketIndex = hashTable.getBucketIndex(value);
        List<Step> steps = hashTable.getBucketAt(bucketIndex).stepsForRemove(value);
        tableModel.refresh();
        stepPlayer.play(steps);
        refresh();
    }

    private void onFind() {
        Integer value = readInput();
        if (value == null) {
            logger.warning("Попытка найти некорректное значение");
            return;
        }

        logger.info("Поиск значения: " + value);
        int bucketIndex = hashTable.getBucketIndex(value);
        List<Step> steps = hashTable.getBucketAt(bucketIndex).stepsForContains(value);
        tableModel.refresh();
        stepPlayer.play(steps);
        refresh();
    }

    private void onClear() {
        logger.info("Очистка таблицы");
        hashTable.clear();
        refresh();
    }

    private void onRandomize(){
        logger.info("Генерация случайного набора значений");
        onClear();
        Random random = new Random();
        int amount = random.nextInt(2, 10) * hashTable.getCapacity();
        int count = 0;
        int errCount = 0;
        while(count < amount){
            int nextValue = random.nextInt(-amount, amount);
            if(!hashTable.contains(nextValue) || errCount > 50){
                addWithoutAnim(nextValue);
                errCount = 0;
                count++;
            }
            else{
                errCount++;
            }
        }
    }

    private void onSave() {
        JFileChooser chooser = createFileChooser("Сохранить набор", false);
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = ensureExtension(chooser.getSelectedFile(), ".bstht");
        try {
            algorithmRunner.saveResults(file.toPath());
            logPanel.log("Состояние сохранено в файл: " + file.getName(), Step.COLOR_SUCCESS);
            logger.info("Сохранение выполнено: " + file.getAbsolutePath());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Ошибка при сохранении состояния", ex);
            JOptionPane.showMessageDialog(this,
                    "Не удалось сохранить файл:\n" + ex.getMessage(),
                    "Ошибка сохранения",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onLoad() {
        JFileChooser chooser = createFileChooser("Загрузить набор", true);
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        try {
            algorithmSettings = algorithmRunner.loadResults(file.toPath());
            setHashTable(algorithmRunner.getCurrentTable());
            logPanel.log("Состояние загружено из файла: " + file.getName(), Step.COLOR_SUCCESS);
            logger.info("Загрузка выполнена: " + file.getAbsolutePath());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Ошибка при загрузке состояния", ex);
            JOptionPane.showMessageDialog(this,
                    "Не удалось загрузить файл:\n" + ex.getMessage(),
                    "Ошибка загрузки",
                    JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            logger.log(Level.SEVERE, "Непредвиденная ошибка при загрузке", ex);
            JOptionPane.showMessageDialog(this,
                    "Непредвиденная ошибка:\n" + ex.getMessage(),
                    "Ошибка загрузки",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAlgorithmDescription() {
        JOptionPane.showMessageDialog(
                this,
                algorithmSettings.getDescription(),
                algorithmSettings.getName(),
                JOptionPane.INFORMATION_MESSAGE
        );
        logger.info("Показано описание алгоритма");
    }

    // ── Таблица бакетов (левая часть) ────────────────────────────────────────

    private JScrollPane buildTablePanel() {
        jTable = new JTable(tableModel);
        jTable.setRowHeight(80);
        jTable.setShowGrid(true);
        jTable.setGridColor(Color.LIGHT_GRAY);
        jTable.setSelectionBackground(new Color(100, 100, 255, 30));
        jTable.setSelectionForeground(new Color(10, 10, 10));
        jTable.setBackground(new Color(0xF6F6F6));
        jTable.setForeground(new Color(10, 10, 10));
        jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jTable.getTableHeader().setReorderingAllowed(false);

        configureTableColumns();

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
        rightPanel.add(logPanel,  BorderLayout.SOUTH);
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
            logger.log(Level.WARNING, "Некорректный ввод: " + text, ex);
            return null;
        }
    }

    /** Обновляет таблицу и канвас после каждой операции. */
    private void refresh() {
        tableModel.refresh();
        jTable.repaint();
        bstCanvas.repaint();
    }

    private void setHashTable(BSTHashTable newTable) {
        hashTable = newTable;
        tableModel = new HashTableModel(hashTable);

        if (jTable != null) {
            jTable.setModel(tableModel);
            configureTableColumns();
            jTable.clearSelection();
        }

        bstCanvas.setTree(null);
        syncTableSizeControls(hashTable.getCapacity());
        refresh();
    }

    private void syncTableSizeControls(int capacity) {
        if (tableSizeSlider == null || sizeSliderLabel == null) {
            return;
        }

        suppressTableResizeEvents = true;
        tableSizeSlider.setValue(capacity);
        sizeSliderLabel.setText("Размер хеш-таблицы: " + capacity);
        suppressTableResizeEvents = false;
    }

    private void resizeHashTable(int newCapacity) {
        if (newCapacity < 1) {
            logger.warning("Попытка задать недопустимый размер таблицы: " + newCapacity);
            return;
        }

        logger.info("Изменение размера таблицы: " + newCapacity);

        algorithmSettings = new HashTableAlgorithmSettings(
                ALGORITHM_NAME,
                ALGORITHM_DESCRIPTION,
                newCapacity
        );
        algorithmRunner.run(algorithmSettings);
        setHashTable(algorithmRunner.getCurrentTable());
    }

    private void configureTableColumns() {
        if (jTable == null) {
            return;
        }

        DefaultTableCellRenderer centeredRenderer = new DefaultTableCellRenderer();
        centeredRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        jTable.getColumnModel().getColumn(0).setCellRenderer(new SelectedCellBorderRenderer());
        jTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        jTable.getColumnModel().getColumn(0).setMaxWidth(60);

        jTable.getColumnModel().getColumn(1).setCellRenderer(new BSTRenderer(stepPlayer));
    }

    private JFileChooser createFileChooser(String title, boolean openDialog) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        chooser.setFileFilter(new FileNameExtensionFilter("Файлы хеш-таблицы (*.bstht)", "bstht"));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        return chooser;
    }

    private File ensureExtension(File file, String extension) {
        if (file == null) {
            return null;
        }

        String name = file.getName().toLowerCase(Locale.ROOT);
        if (name.endsWith(extension)) {
            return file;
        }

        File parent = file.getParentFile();
        String newName = file.getName() + extension;
        return parent == null ? new File(newName) : new File(parent, newName);
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
                String text = getText(fb);
                String newText = text.substring(0, offset) + string + text.substring(offset);
                if (isValidInteger(newText)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                String current = getText(fb);
                String newText = current.substring(0, offset) + text + current.substring(offset + length);
                if (isValidInteger(newText)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            private String getText(FilterBypass fb) {
                try {
                    return fb.getDocument().getText(0, fb.getDocument().getLength());
                } catch (BadLocationException ignore) {
                }
                return "";
            }

            private boolean isValidInteger(String s) {
                if (s.isEmpty()) return true;
                if (s.equals("-")) return true;
                return s.matches("-?\\d+");
            }
        });
    }
}
