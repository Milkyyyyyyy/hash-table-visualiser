package visual;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Панель журнала операций с временными метками.
 *
 * Основные методы:
 *   log(String message)              — добавить запись цветом по умолчанию
 *   log(String message, Color color) — добавить запись произвольным цветом
 *   clear()                          — очистить журнал
 */
public class OperationLogPanel extends JPanel {

    // ── Цвета ─────────────────────────────────────────────────────────────────
    private static final Color BG_COLOR        = new Color(0xF9F9F9);
    private static final Color HEADER_BG       = new Color(0xF0F0F0);
    private static final Color BORDER_COLOR    = new Color(0xD0D0D0);
    private static final Color TIMESTAMP_COLOR = new Color(0x888888);
    public static final Color DEFAULT_COLOR   = new Color(0x222222);

    private static final Font MONO_FONT  = new Font("Monospaced", Font.PLAIN, 13);
    private static final Font LABEL_FONT = new Font("SansSerif",  Font.BOLD,  13);

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    // ── Компоненты ────────────────────────────────────────────────────────────
    private final JPanel      entriesPanel;
    private final SmoothScrollPane scrollPane;

    // ─────────────────────────────────────────────────────────────────────────

    public OperationLogPanel() {
        super(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        // Шапка
        add(buildHeader(), BorderLayout.NORTH);

        // Список записей
        entriesPanel = new JPanel();
        entriesPanel.setLayout(new BoxLayout(entriesPanel, BoxLayout.Y_AXIS));
        entriesPanel.setBackground(BG_COLOR);
        entriesPanel.setBorder(new EmptyBorder(4, 0, 4, 0));

        // Скроллируемая область
        scrollPane = new SmoothScrollPane(entriesPanel);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // в конце конструктора, для теста
        log("Панель инициализирована");
    }

    // ── Публичный API ─────────────────────────────────────────────────────────

    /** Добавляет запись цветом по умолчанию. */
    public void log(String message) {
        log(message, DEFAULT_COLOR);
    }

    /** Добавляет запись с заданным цветом текста. */
    public void log(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            entriesPanel.add(buildRow(message, color));
            entriesPanel.revalidate();
            entriesPanel.repaint();
            SwingUtilities.invokeLater(this::scrollToBottom);
        });
    }

    /** Удаляет все записи из журнала. */
    public void clear() {
        SwingUtilities.invokeLater(() -> {
            entriesPanel.removeAll();
            entriesPanel.revalidate();
            entriesPanel.repaint();
        });
    }

    // ── Построение UI ─────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel title = new JLabel("Журнал операций");
        title.setFont(LABEL_FONT);
        title.setForeground(DEFAULT_COLOR);

        JButton clearBtn = new JButton("Очистить лог");
        clearBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> clear());

        header.add(title,    BorderLayout.WEST);
        header.add(clearBtn, BorderLayout.EAST);
        return header;
    }

    /** Собирает одну строку лога: метка времени + текст сообщения. */
    private JPanel buildRow(String message, Color textColor) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setBackground(BG_COLOR);
        row.setBorder(new EmptyBorder(2, 10, 2, 10));

        JLabel timestamp = new JLabel("[" + LocalTime.now().format(TIME_FORMAT) + "]   ");
        timestamp.setFont(MONO_FONT);
        timestamp.setForeground(TIMESTAMP_COLOR);

        JLabel text = new JLabel(message);
        text.setFont(MONO_FONT);
        text.setForeground(textColor);

        row.add(timestamp);
        row.add(text);

        // Только после добавления компонентов — иначе height будет 0
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));

        return row;
    }

    /** Прокручивает журнал вниз после добавления новой записи. */
    /** Плавно прокручивает журнал вниз с анимацией. */
    private void scrollToBottom() {
        JScrollBar bar = scrollPane.getVerticalScrollBar();
        int target = bar.getMaximum() - bar.getVisibleAmount();

        Timer timer = new Timer(16, null);
        timer.addActionListener(e -> {
            int current = bar.getValue();
            int distance = target - current;

            if (distance <= 1) {
                bar.setValue(target);
                timer.stop();
            } else {
                // Lerp: каждый кадр проходим 15% оставшегося расстояния
                bar.setValue(current + Math.max(1, distance / 7));
            }
        });
        timer.start();
    }
}
