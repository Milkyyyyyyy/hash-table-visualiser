package visual;

import logic.BinarySearchTree;
import util.Node;
import util.NodeLayout;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static visual.TreePainter.drawTree;
import static visual.TreePainter.stepAnimation;

public class BSTRenderer extends JPanel implements TableCellRenderer {
    private JTable table;
    private BinarySearchTree currentTree;
    Map<BinarySearchTree, Map<Integer, NodeLayout>> layouts = new HashMap<>();


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        currentTree = (BinarySearchTree) value;
        layouts.computeIfAbsent(currentTree, k -> new HashMap<>());
        this.table = table;

        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, table.getGridColor()));
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (currentTree == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        currentTree.updateLayout(layouts.get(currentTree), getWidth(), getHeight());

        boolean isAnimating = stepAnimation(layouts.get(currentTree));

        drawTree(g2, layouts.get(currentTree), currentTree.getRoot(), true);

        if (isAnimating && table != null) {
            // Чтобы не уйти в бесконечный цикл и не перегружать процессор,
            // используем invokeLater или просто repaint() у таблицы
            SwingUtilities.invokeLater(() -> table.repaint());
        }
    }
}
