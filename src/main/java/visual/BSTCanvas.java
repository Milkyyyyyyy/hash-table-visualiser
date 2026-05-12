package visual;

import animation.StepPlayer;
import logic.BinarySearchTree;
import util.NodeLayout;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Панель для детальной визуализации одного дерева BST.
 * Отображается в правой части главного окна при выборе строки таблицы.
 */
public class BSTCanvas extends JPanel {

    private BinarySearchTree tree;
    private final Map<Integer, NodeLayout> layouts = new HashMap<>();
    StepPlayer stepPlayer;

    public void setStepPlayer(StepPlayer stepPlayer){
        this.stepPlayer = stepPlayer;
    }
    /** Задаёт дерево для отрисовки и запускает перерисовку. */
    public void setTree(BinarySearchTree tree) {
        this.tree = tree;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Рамка вокруг канваса
        g2.setColor(new Color(10, 10, 10, 50));
        g2.drawRoundRect(0, 0, (int) (getWidth() * 0.95), (int) (getHeight() * 0.95), 30, 30);

        if (tree == null || tree.isEmpty()) {
            g.drawString("Выберите дерево для визуализации", 20, 20);
            return;
        }

        // Обновляем целевые позиции узлов
        tree.updateLayout(layouts, getWidth(), (int) (getHeight() * 0.8));
        Integer active = stepPlayer.getActiveValue();
        if(active != null) {
            for (Map.Entry<Integer, NodeLayout> e : layouts.entrySet()) {
                e.getValue().isActive = e.getKey().equals(active);
            }
        }

        // Делаем шаг анимации
        boolean animating = TreePainter.stepAnimation(layouts);

        // Рисуем дерево с увеличенными узлами
        TreePainter.drawTree(g2, layouts, tree.getRoot(), 50, 30, true);

        // Пока анимация не завершена — перерисовываем каждый кадр
        if (animating) {
            repaint();
        }
    }
}
