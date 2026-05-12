package util;

/**
 * Хранит текущую и целевую позицию узла для анимации перемещения.
 */
public class NodeLayout {
    /** Текущая позиция (обновляется каждый кадр анимации). */
    public float x, y;

    /** Целевая позиция, к которой узел движется. */
    public float targetX, targetY;

    /** Используется рендерером для выделения активного узла. */
    public boolean isActive = false;

    public NodeLayout(float x, float y) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
    }
}
