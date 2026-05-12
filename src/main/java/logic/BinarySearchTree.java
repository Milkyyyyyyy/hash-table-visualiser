package logic;

import animation.Step;
import util.Node;
import util.NodeLayout;
import visual.OperationLogPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Бинарное дерево поиска для целых чисел.
 * Используется как цепочка (бакет) в хеш-таблице.
 */
public class BinarySearchTree {

    BSTHashTable table;
    private Node root;

    public BinarySearchTree(BSTHashTable table){
        this.table = table;
    }

    // ── Основные операции ─────────────────────────────────────────────────────

    public Node getRoot() {
        return root;
    }

    public boolean isEmpty() {
        return root == null;
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
        // Дубликаты игнорируются
        return node;
    }

    public boolean contains(int value) {
        return contains(root, value);
    }

    private boolean contains(Node node, int value) {
        if (node == null) return false;
        if (value < node.value) return contains(node.left, value);
        if (value > node.value) return contains(node.right, value);
        return true;
    }

    public void remove(int value) {
        root = remove(root, value);
    }

    private Node remove(Node node, int value) {
        if (node == null) return null;

        if (value < node.value) {
            node.left = remove(node.left, value);
        } else if (value > node.value) {
            node.right = remove(node.right, value);
        } else {
            // Найден узел для удаления
            if (node.right == null) return node.left;
            if (node.left  == null) return node.right;

            // Узел с двумя потомками: заменяем минимальным из правого поддерева
            Node successor = findMin(node.right);
            node.value = successor.value;
            node.right = deleteMin(node.right);
        }
        return node;
    }

    /** Возвращает узел с минимальным значением в поддереве. */
    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    /** Удаляет минимальный узел из поддерева и возвращает новый корень. */
    private Node deleteMin(Node node) {
        if (node.left == null) return node.right;
        node.left = deleteMin(node.left);
        return node;
    }

    public void clear() {
        root = null;
    }

    // ── Строковое представление ───────────────────────────────────────────────

    /** Возвращает элементы дерева в порядке возрастания (in-order). */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendInOrder(root, sb);
        return sb.isEmpty() ? "Пусто" : sb.toString().trim();
    }

    private void appendInOrder(Node node, StringBuilder sb) {
        if (node == null) return;
        appendInOrder(node.left, sb);
        sb.append(node.value).append(' ');
        appendInOrder(node.right, sb);
    }

    // ── Вычисление позиций для отрисовки ─────────────────────────────────────

    /**
     * Пересчитывает целевые координаты всех узлов в {@code layoutMap}
     * и удаляет записи удалённых узлов.
     *
     * @param layoutMap карта «значение → позиция», используется рендерером
     * @param width     ширина области отрисовки (в пикселях)
     * @param height    высота области отрисовки (в пикселях)
     */
    public void updateLayout(Map<Integer, NodeLayout> layoutMap, int width, int height) {
        int treeHeight = calculateHeight(root);
        float startX   = width / 2f;
        float startY   = height * 0.2f;
        float xStep    = width / 4f;
        float yStep    = (treeHeight > 0) ? height * 0.8f / treeHeight : height * 0.8f;

        calculateTargetPositions(layoutMap, root, startX, startY, xStep, yStep, null);

        // Удаляем узлы, которых больше нет в дереве
        layoutMap.keySet().removeIf(key -> !contains(key));
    }

    private int calculateHeight(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(calculateHeight(node.left), calculateHeight(node.right));
    }

    /**
     * Рекурсивно назначает целевые позиции узлам дерева.
     * Новые узлы появляются в позиции родителя и затем анимированно
     * смещаются в целевую точку.
     */
    private void calculateTargetPositions(
            Map<Integer, NodeLayout> layoutMap,
            Node node,
            float x, float y,
            float xOffset, float yOffset,
            NodeLayout parentLayout
    ) {
        if (node == null) return;

        NodeLayout layout = layoutMap.get(node.value);

        if (layout == null) {
            // Новый узел: стартуем из позиции родителя (или сразу из целевой)
            layout = (parentLayout != null)
                    ? new NodeLayout(parentLayout.x, parentLayout.y)
                    : new NodeLayout(x, y);
            layout.targetX = x;
            layout.targetY = y;
            layoutMap.put(node.value, layout);
        } else {
            layout.targetX = x;
            layout.targetY = y;
        }

        // Если только один потомок — не смещаем его в сторону
        float childXOffset = (node.left == null) != (node.right == null) ? (int)(xOffset*0.2) : xOffset;

        calculateTargetPositions(layoutMap, node.left,  x - childXOffset, y + yOffset, xOffset / 2, yOffset, layout);
        calculateTargetPositions(layoutMap, node.right, x + childXOffset, y + yOffset, xOffset / 2, yOffset, layout);
    }

    // ========= ШАГИ =========
    public List<Step> stepsForContains(int value){
        List<Step> steps = new ArrayList<>();

        int bucketIndex = table.getBucketIndex(value);

        steps.add(new Step(
                null,
                bucketIndex,
                "Вычисленная хеш-функция: " + bucketIndex,
                Step.COLOR_DEFAULT
        ));

        Node current = root;
        while(current != null){
            if(value == current.value){
                steps.add(new Step(
                        current.value,
                        bucketIndex,
                        "Найдено: " + value,
                        Step.COLOR_SUCCESS
                ));
                return steps;
            }
            current = getNode(value, steps, bucketIndex, current);
        }
        steps.add(new Step(null, bucketIndex,
                "Не найдено: " + value, Step.COLOR_FAIL
        ));

        return steps;

    }

    public List<Step> stepsForInsert(int value){
        List<Step> steps = new ArrayList<>();

        int bucketIndex = table.getBucketIndex(value);

        steps.add(new Step(
                null,
                bucketIndex,
                "Вычисленная хеш-функция: " + bucketIndex,
                Step.COLOR_DEFAULT
        ));

        Node current = root;
        while(current != null){
            if(value == current.value){
                steps.add(new Step(
                        current.value,
                        bucketIndex,
                        value + " уже есть в дереве", Step.COLOR_FAIL
                ));
                return steps;
            }
            current = getNode(value, steps, bucketIndex, current);
        }
        steps.add(new Step(
                value,
                bucketIndex,
                "Вставлено: " + value,
                Step.COLOR_SUCCESS,
                () -> {insert(value);}
        ));

        return steps;
    }

    public List<Step> stepsForRemove(int value) {
        List<Step> steps = new ArrayList<>();

        // Шаг 1: вычисляем бакет
        int bucketIndex = table.getBucketIndex(value);
        steps.add(new Step(null, bucketIndex,
                "Вычисленная хеш-функция: " + bucketIndex, Step.COLOR_DEFAULT));

        // Шаг 2: обход дерева до нужного узла
        Node current = root;
        while (current != null) {
            if (value == current.value) break;

            steps.add(new Step(current.value, null,
                    "Сравниваем " + value + " и " + current.value, Step.COLOR_DEFAULT));
            current = getNode(value, steps, bucketIndex, current);
        }

        // Узел не найден
        if (current == null) {
            steps.add(new Step(null, null,
                    "Не найдено: " + value, Step.COLOR_FAIL));
            return steps;
        }

        // Шаг 3: нашли узел — показываем какой случай удаления
        steps.add(new Step(current.value, null,
                "Нашли узел: " + value, Step.COLOR_SUCCESS));

        boolean hasLeft  = current.left  != null;
        boolean hasRight = current.right != null;

        if (!hasLeft && !hasRight) {
            // Случай 1: листовой узел
            steps.add(new Step(current.value, null,
                    "Узел — лист, просто удаляем", Step.COLOR_SUCCESS));

        } else if (!hasLeft || !hasRight) {
            // Случай 2: один потомок
            Node child = hasRight ? current.right : current.left;
            steps.add(new Step(current.value, null,
                    "Один потомок — заменяем узел на " + child.value, Step.COLOR_DEFAULT));
            steps.add(new Step(child.value, null,
                    child.value + " встаёт на место " + value, Step.COLOR_SUCCESS));

        } else {
            // Случай 3: два потомка — ищем минимум правого поддерева
            steps.add(new Step(current.value, null,
                    "Два потомка — ищем минимум правого поддерева", Step.COLOR_DEFAULT));

            Node successor = current.right;
            while (successor.left != null) {
                steps.add(new Step(successor.value, null,
                        "Идём влево: " + successor.value, Step.COLOR_DEFAULT));
                successor = successor.left;
            }
            steps.add(new Step(successor.value, null,
                    "Минимум правого поддерева: " + successor.value, Step.COLOR_DEFAULT));
            steps.add(new Step(current.value, null,
                    "Заменяем " + value + " → " + successor.value, Step.COLOR_SUCCESS));
        }

        // Реальное удаление — в самом конце, после всех визуальных шагов
        steps.add(new Step(null, null,
                "Удалено: " + value, Step.COLOR_SUCCESS,
                () -> remove(value)));

        return steps;
    }

    private Node getNode(int value, List<Step> steps, int bucketIndex, Node current) {
        steps.add(new Step(
                current.value,
                bucketIndex,
                value + (value < current.value ? " < " : " > ") + current.value
                        + " → идём " + (value < current.value ? "влево" : "вправо"),
                Step.COLOR_DEFAULT
        ));
        return value < current.value ? current.left : current.right;
    }
}
