package visual;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Панель журнала операций.
 * Наследуется от JPanel и отображает лог действий с временными метками.
 *
 * Основные методы:
 *   log(String message)             — добавить запись стандартным цветом
 *   log(String message, Color color) — добавить запись произвольным цветом
 *   clear()                         — очистить лог
 */
public class OperationLogPanel extends JPanel {

    // ── внешний вид ──────────────────────────────────────────────────────────
    private static final Color BG_COLOR        = new Color(0xF9F9F9);
    private static final Color HEADER_BG       = new Color(0xF0F0F0);
    private static final Color BORDER_COLOR    = new Color(0xD0D0D0);
    private static final Color TIMESTAMP_COLOR = new Color(0x888888);
    private static final Color DEFAULT_TEXT    = new Color(0x222222);
    private static final Color SUCCESS_COLOR   = new Color(0x2E7D32); // тёмно-зелёный

    private static final Font MONO_FONT  = new Font("Monospaced", Font.PLAIN, 13);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 13);

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    // ── компоненты ────────────────────────────────────────────────────────────
    private final JPanel  entriesPanel; // вертикальный список записей
    private final JScrollPane scrollPane;

    // ─────────────────────────────────────────────────────────────────────────

    public OperationLogPanel() {
        super(new BorderLayout());
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createLineBorder(BORDER_COLOR));

        // ── шапка ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(6, 10, 6, 10));

        JLabel title = new JLabel("Журнал операций");
        title.setFont(LABEL_FONT);
        title.setForeground(DEFAULT_TEXT);
        header.add(title, BorderLayout.WEST);

        JButton clearBtn = new JButton("Очистить лог");
        clearBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener((ActionEvent e) -> clear());
        header.add(clearBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ── тело: список записей ─────────────────────────────────────────────
        entriesPanel = new JPanel();
        entriesPanel.setLayout(new BoxLayout(entriesPanel, BoxLayout.Y_AXIS));
        entriesPanel.setBackground(BG_COLOR);
        entriesPanel.setBorder(new EmptyBorder(4, 0, 4, 0));

        scrollPane = new JScrollPane(entriesPanel);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    // ── публичные методы ──────────────────────────────────────────────────────

    /**
     * Добавить запись стандартным (тёмным) цветом.
     */
    public void log(String message) {
        log(message, DEFAULT_TEXT);
    }

    /**
     * Добавить запись произвольным цветом.
     *
     * @param message текст записи
     * @param color   цвет текста
     */
    public void log(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            JPanel row = buildRow(message, color);
            entriesPanel.add(row);
            entriesPanel.revalidate();
            entriesPanel.repaint();
            scrollToBottom();
        });
    }

    /**
     * Удобный вариант — передать цвет через RGB-int, например 0x2E7D32.
     */
    public void log(String message, int rgb) {
        log(message, new Color(rgb));
    }

    /**
     * Очистить все записи.
     */
    public void clear() {
        SwingUtilities.invokeLater(() -> {
            entriesPanel.removeAll();
            entriesPanel.revalidate();
            entriesPanel.repaint();
        });
    }

    // ── вспомогательные ───────────────────────────────────────────────────────

    /** Собирает одну строку лога: [время] + текст. */
    private JPanel buildRow(String message, Color textColor) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setBackground(BG_COLOR);
        row.setBorder(new EmptyBorder(2, 10, 2, 10));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));

        // метка времени
        JLabel timestamp = new JLabel("[" + LocalTime.now().format(TIME_FMT) + "]   ");
        timestamp.setFont(MONO_FONT);
        timestamp.setForeground(TIMESTAMP_COLOR);

        // текст сообщения
        JLabel text = new JLabel(message);
        text.setFont(MONO_FONT);
        text.setForeground(textColor);

        row.add(timestamp);
        row.add(text);
        return row;
    }

    /** Прокрутить журнал вниз после добавления записи. */
    private void scrollToBottom() {
        JScrollBar bar = scrollPane.getVerticalScrollBar();
        bar.setValue(bar.getMaximum());
    }



}
