package visual;

import animation.Step;
import animation.StepPlayer;
import logic.BinarySearchTree;
import util.NodeLayout;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Рендерер ячейки таблицы, который рисует BST в миниатюре.
 * Каждое дерево хранит свою карту позиций, чтобы анимации не мешали друг другу.
 */
public class BSTRenderer extends JPanel implements TableCellRenderer {

    /** Карта: дерево → позиции его узлов. */
    private final Map<BinarySearchTree, Map<Integer, NodeLayout>> allLayouts = new HashMap<>();

    private BinarySearchTree currentTree;
    private JTable table;
    private final StepPlayer stepPlayer;


    public BSTRenderer(StepPlayer stepPlayer){
        this.stepPlayer = stepPlayer;
    }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        this.table = table;
        this.currentTree = (BinarySearchTree) value;

        // Инициализируем карту позиций для нового дерева, если её ещё нет
        allLayouts.computeIfAbsent(currentTree, k -> new HashMap<>());

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
        if (currentTree == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Map<Integer, NodeLayout> layouts = allLayouts.get(currentTree);

        currentTree.updateLayout(layouts, getWidth(), getHeight());

        Integer active = stepPlayer.getActiveValue();
        if(active != null){
            for (Map.Entry<Integer, NodeLayout> e : layouts.entrySet()) {
                e.getValue().isActive = e.getKey().equals(active);
            }
        }

        boolean animating = TreePainter.stepAnimation(layouts);

        TreePainter.drawTree(g2, layouts, currentTree.getRoot(), true);

        // Продолжаем анимацию через EDT, не блокируя поток отрисовки
        if (animating && table != null) {
            SwingUtilities.invokeLater(table::repaint);
        }
    }
}
