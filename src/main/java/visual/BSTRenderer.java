package visual;

import logic.BinarySearchTree;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;


public class BSTRenderer extends JPanel implements TableCellRenderer {
    private BinarySearchTree<Integer, ?> currentTree;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                        boolean isSelected, boolean hasFocus, int row, int column){
        this.currentTree = (BinarySearchTree<Integer, ?>) value;

        if(isSelected){
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        }
        else{
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }

        Color gridColor = table.getGridColor();
        this.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, gridColor));

        return this;
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        if(currentTree == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.BLUE);
        g2.drawString("Здесь будет дерево", 10, 20);

        // TODO сделать визуализацию
    }
}
