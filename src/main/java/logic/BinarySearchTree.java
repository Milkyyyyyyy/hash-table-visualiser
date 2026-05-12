package logic;

import util.Node;
import util.NodeLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * Простое BST для целых чисел.
 * Используется как цепочка в одной корзине хеш-таблицы.
 */
public class BinarySearchTree {

    private Node root;

    public Node getRoot(){
        return root;
    }
    public void insert(int value) {
        root = insert(root, value);
    }

    private Node insert(Node node, int value) {
        if (node == null) {
            return new Node(value);
        }

        if (value < node.value) {
            node.left = insert(node.left, value);
        } else if (value > node.value) {
            node.right = insert(node.right, value);
        }
        return node;
    }

    public boolean contains(int value) {
        return contains(root, value);
    }

    private boolean contains(Node node, int value) {
        if (node == null) {
            return false;
        }
        if (value < node.value) {
            return contains(node.left, value);
        }
        if (value > node.value) {
            return contains(node.right, value);
        }

        return true;
    }


    public void remove(int value) {
        root = remove(root, value);
    }

    private Node remove(Node node, int value) {
        if (node == null) {
            return null;
        }

        if (value < node.value) {
            node.left = remove(node.left, value);
        } else if (value > node.value) {
            node.right = remove(node.right, value);
        } else {
            if (node.right == null) {
                return node.left;
            }
            if (node.left == null) {
                return node.right;
            }

            Node successor = min(node.right);
            node.value = successor.value;
            node.right = deleteMin(node.right);
        }
        return node;
    }

    private Node min(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private Node deleteMin(Node node) {
        if (node.left == null) {
            return node.right;
        }
        node.left = deleteMin(node.left);
        return node;
    }

    public void clear() {
        root = null;
    }

    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        buildInOrder(root, sb);
        if (sb.length() == 0) {
            return "Пусто";
        }
        return sb.toString().trim();
    }

    private void buildInOrder(Node node, StringBuilder sb) {
        if (node == null) {
            return;
        }
        buildInOrder(node.left, sb);
        sb.append(node.value).append(' ');
        buildInOrder(node.right, sb);
    }
    private int calculateHeight(Node node) {
        if (node == null) return 0;
        // Высота — это 1 (текущий узел) + высота самого глубокого поддерева
        return 1 + Math.max(calculateHeight(node.left), calculateHeight(node.right));
    }
    // Главный метод, который вызывает рендерер
    public void updateLayout(Map<Integer, NodeLayout> layoutMap, int width, int height) {
        // Вычисляем новые целевые позиции
        calculateTargets(layoutMap, root, width / 2f, height*0.2f, width / 4f, (height*0.8f/(float)(calculateHeight(root))), null);

        // Удаляем из карты тех, кого больше нет в дереве
        layoutMap.keySet().removeIf(key -> !contains(key));
    }
    private void calculateTargets(Map<Integer, NodeLayout> layoutMap, Node node, float x, float y, float xOffset, float yOffset, NodeLayout parentLayout){
        if(node == null) return;

        NodeLayout layout = layoutMap.get(node.value);

        if(layout == null){
            if(parentLayout != null){
                layout = new NodeLayout(parentLayout.x, parentLayout.y);
                layout.targetX = x;
                layout.targetY = y;
            }
            else
                layout = new NodeLayout(x, y);
            layoutMap.put(node.value, layout);
        }
        else{
            layout.targetX = x;
            layout.targetY = y;
        }

        float rXOffset = xOffset;
        if((node.left == null && node.right != null) || (node.left != null && node.right == null)){
            rXOffset = 0;
        }
        calculateTargets(layoutMap, node.left, x-rXOffset,   y + yOffset, xOffset/2, yOffset, layout);
        calculateTargets(layoutMap, node.right, x +rXOffset, y + yOffset, xOffset/2, yOffset, layout);
    }

}
