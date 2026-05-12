package visual;

import logic.BinarySearchTree;
import util.Node;
import util.NodeLayout;

import java.awt.*;
import java.util.Map;

public class TreePainter {
    private static final int nodeWidth = 15;
    private static final int nodeHeight = 10;

    public static void drawTree(Graphics2D g2, Map<Integer, NodeLayout> layouts, Node node, boolean drawNumbers){
        drawTree(g2, layouts, node, nodeWidth, nodeHeight, drawNumbers);
    }
    public static void drawTree(Graphics2D g2, Map<Integer, NodeLayout> layouts, Node node, int nodeWidth, int nodeHeight, boolean drawNumbers){
        if(node == null) return;

        NodeLayout layout = layouts.get(node.value);
        if(layout == null) return;

        g2.setColor(new Color(10, 10, 10));

        if(node.left != null){
            NodeLayout leftLayout = layouts.get(node.left.value);
            if(leftLayout != null){
                g2.drawLine((int)layout.x, (int)layout.y, (int)leftLayout.x, (int)leftLayout.y);
                drawTree(g2, layouts, node.left, nodeWidth, nodeHeight, drawNumbers);
            }
        }
        if(node.right != null){
            NodeLayout rightLayout = layouts.get(node.right.value);
            if(rightLayout != null){
                g2.drawLine((int)layout.x, (int)layout.y, (int)rightLayout.x, (int)rightLayout.y);
                drawTree(g2, layouts, node.right, nodeWidth, nodeHeight, drawNumbers);
            }
        }

        if(layout.isActive)
            g2.setColor(new Color(255, 230, 190));
        else
            g2.setColor(new Color(255, 255, 255));

        g2.fillRoundRect(
                (int)layout.x - nodeWidth/2,
                (int)layout.y - nodeHeight/2,
                nodeWidth, nodeHeight,
                (int)(nodeWidth*0.25), (int)(nodeHeight*0.25)
        );
        g2.setColor(new Color(10, 10, 10));
        g2.drawRoundRect(
                (int)layout.x - nodeWidth/2,
                (int)layout.y - nodeHeight/2,
                nodeWidth, nodeHeight,
                (int)(nodeWidth*0.25), (int)(nodeHeight*0.25)
        );
        if(drawNumbers){
            String text = String.valueOf(node.value);
            g2.setFont(new Font("SansSerif", Font.BOLD, (int)(nodeHeight/1.25)));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (int)layout.x - fm.stringWidth(text) / 2;
            int ty = (int)layout.y + fm.getAscent() / 2-1;
            g2.drawString(text, tx, ty);
        }

    }
    public static boolean stepAnimation(Map<Integer, NodeLayout> layoutMap) {
        float speed = 0.01f; // Насколько быстро двигаются узлы (0.1 - 0.2 оптимально)
        boolean stillMoving = false;

        for (NodeLayout l : layoutMap.values()) {
            float dx = l.targetX - l.x;
            float dy = l.targetY - l.y;

            // Если дистанция до цели больше пикселя — двигаем
            if (Math.abs(dx) > 0.01 || Math.abs(dy) > 0.01) {
                l.x += dx * speed;
                l.y += dy * speed;
                stillMoving = true;
            } else {
                l.x = l.targetX;
                l.y = l.targetY;
            }
        }
        return stillMoving; // Возвращаем true, если анимация еще продолжается
    }
}
