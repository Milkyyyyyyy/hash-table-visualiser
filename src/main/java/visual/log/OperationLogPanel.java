package visual.log;

import visual.common.SmoothScrollPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Path2D;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Панель журнала операций с временными метками.
 */
public class OperationLogPanel extends JPanel {

    private static final Color BG_COLOR        = new Color(0xF6F6F6);
    private static final Color HEADER_BG       = new Color(0xF6F6F6);
    private static final Color BORDER_COLOR    = new Color(0xD0D0D0);
    private static final Color TIMESTAMP_COLOR = new Color(0x888888);
    public static final Color DEFAULT_COLOR    = new Color(0x222222);

    private static final Font MONO_FONT  = new Font("Monospaced", Font.PLAIN, 13);
    private static final Font LABEL_FONT  = new Font("SansSerif", Font.BOLD, 13);

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final int TOP_LEFT_ARC = 18;

    private final JPanel entriesPanel;
    private final SmoothScrollPane scrollPane;

    public OperationLogPanel() {
        super(new BorderLayout());
        setOpaque(false);
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(1, 1, 1, 1)); // резервируем место под обводку
        setPreferredSize(new Dimension(700, 300));

        add(buildHeader(), BorderLayout.NORTH);

        entriesPanel = new JPanel();
        entriesPanel.setLayout(new BoxLayout(entriesPanel, BoxLayout.Y_AXIS));
        entriesPanel.setOpaque(false);
        entriesPanel.setBackground(BG_COLOR);
        entriesPanel.setBorder(new EmptyBorder(4, 0, 4, 0));

        scrollPane = new SmoothScrollPane(entriesPanel);
        scrollPane.setOpaque(false);
        scrollPane.setBackground(BG_COLOR);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(BG_COLOR);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        log("Панель инициализирована");
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = TOP_LEFT_ARC;

        Shape shape = createTopLeftRoundedShape(w - 1, h - 1, arc);

        g2.setColor(BG_COLOR);
        g2.fill(shape);

        g2.setColor(BORDER_COLOR);
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(shape);

        g2.dispose();
    }

    @Override
    protected void paintChildren(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Shape shape = createTopLeftRoundedShape(getWidth(), getHeight(), TOP_LEFT_ARC);

        g2.setClip(shape);
        super.paintChildren(g2);
        g2.dispose();

        // Линия поверх детей
        Graphics2D g3 = (Graphics2D) g.create();
        g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g3.setColor(BORDER_COLOR);
        g3.setStroke(new BasicStroke(1.2f));

        g3.draw(shape);
        g3.dispose();
    }

    private Shape createTopLeftRoundedShape(int width, int height, int arc) {
        Path2D path = new Path2D.Double();

        path.moveTo(arc, 0);

        // верх
        path.lineTo(width, 0);

        // правая сторона
        path.lineTo(width, height);

        // низ
        path.lineTo(0, height);

        // левая сторона
        path.lineTo(0, arc);

        // скругление
        path.quadTo(0, 0, arc, 0);


        path.closePath();

        return path;
    }

    public void log(String message) {
        log(message, DEFAULT_COLOR);
    }

    public void log(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            entriesPanel.add(buildRow(message, color));
            entriesPanel.revalidate();
            entriesPanel.repaint();
            SwingUtilities.invokeLater(this::scrollToBottom);
        });
    }

    public void clear() {
        SwingUtilities.invokeLater(() -> {
            entriesPanel.removeAll();
            entriesPanel.revalidate();
            entriesPanel.repaint();
        });
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
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

        header.add(title, BorderLayout.WEST);
        header.add(clearBtn, BorderLayout.EAST);
        return header;
    }

    private JPanel buildRow(String message, Color textColor) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
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
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));

        return row;
    }

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
                bar.setValue(current + Math.max(1, distance / 7));
            }
        });
        timer.start();
    }
}