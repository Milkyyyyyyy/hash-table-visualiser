import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class MainFrame extends JFrame {

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