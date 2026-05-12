package visual;

import logic.BinarySearchTree;
import util.NodeLayout;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static visual.TreePainter.stepAnimation;

public class BSTCanvas extends JPanel {
    private BinarySearchTree tree;
    Map<Integer, NodeLayout> layouts = new HashMap<>();

    public void setTree(BinarySearchTree tree) {
        this.tree = tree;
        repaint(); // Перерисовываем, когда дерево меняется
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Очистка фона

        Graphics2D g2 = (Graphics2D) g;
        // Включаем сглаживание
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(10, 10, 10, 50));
        g2.drawRoundRect(0, 0, (int)(getWidth()*0.95), (int)(getHeight()*0.95), 30, 30);

        if (tree == null || tree.isEmpty()) {
            g.drawString("Выберите дерево для визуализации", 20, 20);
            return;
        }



        // 1. Обновляем координаты под большой размер
        tree.updateLayout(layouts, getWidth(), (int)(getHeight()*0.8));

        // 2. Двигаем анимацию
        boolean animating = stepAnimation(layouts);

        // 3. Рисуем дерево
        TreePainter.drawTree(g2, layouts, tree.getRoot(), 50, 30, true);



        // 5. Если дерево еще "плывет", продолжаем цикл анимации
        if (animating) {
            repaint();
        }
    }
}
