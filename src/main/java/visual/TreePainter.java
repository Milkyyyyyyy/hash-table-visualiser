package visual;

import util.Node;
import util.NodeLayout;

import java.awt.*;
import java.util.Map;

/**
 * Утилитный класс для отрисовки BST и обновления анимации.
 */
public class TreePainter {

    /** Размер узла по умолчанию (для отрисовки в мини-режиме внутри таблицы). */
    private static final int DEFAULT_NODE_WIDTH  = 15;
    private static final int DEFAULT_NODE_HEIGHT = 10;

    /** Скорость интерполяции анимации (lerp-коэффициент, 0–1). */
    private static final float ANIMATION_SPEED = 0.05f;

    /** Порог: если расстояние до цели меньше этого — считаем, что узел на месте. */
    private static final float ANIMATION_THRESHOLD = 0.01f;

    private static final Color COLOR_EDGE     = new Color(10, 10, 10);
    private static final Color COLOR_NODE_BG  = Color.WHITE;
    private static final Color COLOR_NODE_ACTIVE = new Color(255, 230, 160);
    private static final Color COLOR_TEXT     = new Color(10, 10, 10);

    // ── Отрисовка дерева ─────────────────────────────────────────────────────

    /**
     * Рисует дерево с размерами узлов по умолчанию.
     * Используется в рендерере ячейки таблицы.
     */
    public static void drawTree(Graphics2D g2, Map<Integer, NodeLayout> layouts,
                                Node root, boolean drawNumbers) {
        drawTree(g2, layouts, root, DEFAULT_NODE_WIDTH, DEFAULT_NODE_HEIGHT, drawNumbers);
    }

    /**
     * Рисует дерево с заданными размерами узлов.
     * Сначала рисует рёбра, затем поверх них — сами узлы.
     */
    public static void drawTree(Graphics2D g2, Map<Integer, NodeLayout> layouts,
                                Node root, int nodeWidth, int nodeHeight, boolean drawNumbers) {
        if (root == null) return;
        drawEdges(g2, layouts, root);
        drawNodes(g2, layouts, root, nodeWidth, nodeHeight, drawNumbers);
    }

    /** Рисует все рёбра (линии между узлами) рекурсивно. */
    private static void drawEdges(Graphics2D g2, Map<Integer, NodeLayout> layouts, Node node) {
        if (node == null) return;

        NodeLayout layout = layouts.get(node.value);
        if (layout == null) return;

        g2.setColor(COLOR_EDGE);

        if (node.left != null) {
            NodeLayout leftLayout = layouts.get(node.left.value);
            if (leftLayout != null) {
                g2.drawLine((int) layout.x, (int) layout.y, (int) leftLayout.x, (int) leftLayout.y);
            }
            drawEdges(g2, layouts, node.left);
        }
        if (node.right != null) {
            NodeLayout rightLayout = layouts.get(node.right.value);
            if (rightLayout != null) {
                g2.drawLine((int) layout.x, (int) layout.y, (int) rightLayout.x, (int) rightLayout.y);
            }
            drawEdges(g2, layouts, node.right);
        }
    }

    /** Рисует все узлы (прямоугольники с числами) рекурсивно. */
    private static void drawNodes(Graphics2D g2, Map<Integer, NodeLayout> layouts,
                                  Node node, int nodeWidth, int nodeHeight, boolean drawNumbers) {
        if (node == null) return;

        NodeLayout layout = layouts.get(node.value);
        if (layout == null) return;

        int x = (int) layout.x - nodeWidth  / 2;
        int y = (int) layout.y - nodeHeight / 2;
        int arcW = (int) (nodeWidth  * 0.25);
        int arcH = (int) (nodeHeight * 0.25);

        // Заливка узла
        g2.setColor(layout.isActive ? COLOR_NODE_ACTIVE : COLOR_NODE_BG);
        g2.fillRoundRect(x, y, nodeWidth, nodeHeight, arcW, arcH);

        // Рамка узла
        g2.setColor(COLOR_EDGE);
        g2.drawRoundRect(x, y, nodeWidth, nodeHeight, arcW, arcH);

        // Подпись
        if (drawNumbers) {
            String text = String.valueOf(node.value);
            g2.setColor(COLOR_TEXT);
            g2.setFont(new Font("SansSerif", Font.BOLD, (int) (nodeHeight / 1.25)));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (int) layout.x - fm.stringWidth(text) / 2;
            int ty = (int) layout.y + fm.getAscent() / 2 - 1;
            g2.drawString(text, tx, ty);
        }

        drawNodes(g2, layouts, node.left,  nodeWidth, nodeHeight, drawNumbers);
        drawNodes(g2, layouts, node.right, nodeWidth, nodeHeight, drawNumbers);
    }

    // ── Анимация ──────────────────────────────────────────────────────────────

    /**
     * Двигает все узлы в layoutMap на один шаг к их целевым позициям (lerp).
     *
     * @return {@code true}, если хотя бы один узел ещё не достиг цели
     */
    public static boolean stepAnimation(Map<Integer, NodeLayout> layoutMap) {
        boolean stillMoving = false;

        for (NodeLayout layout : layoutMap.values()) {
            float dx = layout.targetX - layout.x;
            float dy = layout.targetY - layout.y;

            if (Math.abs(dx) > ANIMATION_THRESHOLD || Math.abs(dy) > ANIMATION_THRESHOLD) {
                layout.x += dx * ANIMATION_SPEED;
                layout.y += dy * ANIMATION_SPEED;
                stillMoving = true;
            } else {
                // Снапаем узел точно в цель, чтобы не было бесконечного дрожания
                layout.x = layout.targetX;
                layout.y = layout.targetY;
            }
        }

        return stillMoving;
    }
}
