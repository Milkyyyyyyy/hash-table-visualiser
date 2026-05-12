package visual;

import animation.StepPlayer;
import logic.BinarySearchTree;
import util.NodeLayout;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Панель для детальной визуализации одного дерева BST.
 * Отображается в правой части главного окна при выборе строки таблицы.
 */
public class BSTCanvas extends JPanel {

    private BinarySearchTree tree;
    private final Map<Integer, NodeLayout> layouts = new HashMap<>();
    private StepPlayer stepPlayer;

    public void setStepPlayer(StepPlayer stepPlayer) {
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

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Основная рамка вокруг канваса
            g2.setColor(new Color(10, 10, 10, 50));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 30, 30);

            if (tree == null || tree.isEmpty()) {
                String text = "Пусто :(";

                g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 14f));
                FontMetrics fm = g2.getFontMetrics();

                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;

                g2.setColor(new Color(0, 0, 0, 20)); // очень тускло и полупрозрачно
                g2.drawString(text, x, y);
                return;
            }

            // Обновляем целевые позиции узлов
            tree.updateLayout(layouts, w, (int) (h * 0.78));

            Integer active = stepPlayer != null ? stepPlayer.getActiveValue() : null;
            if (active != null) {
                for (Map.Entry<Integer, NodeLayout> e : layouts.entrySet()) {
                    e.getValue().isActive = e.getKey().equals(active);
                }
            }

            // Делаем шаг анимации
            boolean animating = TreePainter.stepAnimation(layouts);

            // Рисуем дерево
            TreePainter.drawTree(g2, layouts, tree.getRoot(), 25, 15, true);

            // Плашка снизу слева
            drawInfoBox(g2);

            if (animating) {
                repaint();
            }
        } finally {
            g2.dispose();
        }
    }

    private void drawInfoBox(Graphics2D g2) {
        List<Integer> values = collectValuesInOrder();
        int count = values.size();

        String valuesText = values.isEmpty()
                ? "-"
                : values.stream().map(String::valueOf).reduce((a, b) -> a + " " + b).orElse("-");

        String title = "Количество элементов: " + count;

        int boxX = 15;
        int boxW = Math.max(260, getWidth() - 30);
        int boxH = 60;
        int boxY = getHeight() - boxH - 15; // снизу слева

        // Фон
        g2.setColor(new Color(255, 255, 255, 230));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 22, 22);

        // Обводка
        g2.setColor(new Color(10, 10, 10, 60));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 22, 22);

        g2.setColor(new Color(30, 30, 30));

        Font oldFont = g2.getFont();

        g2.setFont(oldFont.deriveFont(Font.BOLD, 13f));
        g2.drawString(title, boxX + 14, boxY + 22);

        g2.setFont(oldFont.deriveFont(Font.PLAIN, 12f));
        String clipped = clipText(g2, valuesText, boxW - 28);
        g2.drawString(clipped, boxX + 14, boxY + 44);

        g2.setFont(oldFont);
    }

    private String clipText(Graphics2D g2, String text, int maxWidth) {
        FontMetrics fm = g2.getFontMetrics();
        if (fm.stringWidth(text) <= maxWidth) {
            return text;
        }

        String ellipsis = "...";
        int ellipsisWidth = fm.stringWidth(ellipsis);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            String test = sb + String.valueOf(text.charAt(i)) + ellipsis;
            if (fm.stringWidth(test) > maxWidth - ellipsisWidth) {
                break;
            }
            sb.append(text.charAt(i));
        }
        return sb + ellipsis;
    }

    private List<Integer> collectValuesInOrder() {
        List<Integer> values = new ArrayList<>();
        Object root = tree.getRoot();
        collectValuesRecursive(root, values);
        return values;
    }

    private void collectValuesRecursive(Object node, List<Integer> out) {
        if (node == null) return;

        Object left = readChild(node, "left", "getLeft");
        Object right = readChild(node, "right", "getRight");

        collectValuesRecursive(left, out);

        Integer value = readIntValue(node);
        if (value != null) {
            out.add(value);
        }

        collectValuesRecursive(right, out);
    }

    private Object readChild(Object node, String fieldName, String methodName) {
        Object value = invokeNoArg(node, methodName);
        if (value != null) return value;
        return readField(node, fieldName);
    }

    private Integer readIntValue(Object node) {
        Object value = invokeNoArg(node, "getValue");
        if (value == null) value = invokeNoArg(node, "getData");
        if (value == null) value = invokeNoArg(node, "getKey");

        if (value == null) {
            value = readField(node, "value");
            if (value == null) value = readField(node, "data");
            if (value == null) value = readField(node, "key");
        }

        if (value instanceof Integer i) return i;
        if (value instanceof Number n) return n.intValue();
        return null;
    }

    private Object invokeNoArg(Object target, String methodName) {
        if (target == null) return null;
        try {
            Method m;
            try {
                m = target.getClass().getMethod(methodName);
            } catch (NoSuchMethodException ex) {
                m = target.getClass().getDeclaredMethod(methodName);
                m.setAccessible(true);
            }
            return m.invoke(target);
        } catch (Exception ignored) {
            return null;
        }
    }

    private Object readField(Object target, String fieldName) {
        if (target == null) return null;
        try {
            Field f;
            try {
                f = target.getClass().getField(fieldName);
            } catch (NoSuchFieldException ex) {
                f = target.getClass().getDeclaredField(fieldName);
                f.setAccessible(true);
            }
            return f.get(target);
        } catch (Exception ignored) {
            return null;
        }
    }
}