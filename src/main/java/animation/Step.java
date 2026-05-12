package animation;

import java.awt.*;

/**
 * Представляет один шаг анимации операции в хеш-таблице.
 *
 * Каждый ша�� содержит информацию о том, какой узел подсветить, какое сообщение
 * вывести в лог операций и в какой цвет его окрасить. Опционально может содержать
 * Runnable для выполнения действия (например, реального добавления/удаления элемента).
 */
public class Step {

    /** Значение узла для подсветки (null = никакой узел не подсвечивается). */
    public final Integer highlightValue;

    /** Индекс строки таблицы для подсветки (null = строка не подсвечивается). */
    public final Integer highlightTableRow;

    /** Текст сообщения для журнала операций. */
    public final String message;

    /** Цвет текста сообщения в логе. */
    public final Color color;

    /** Опциональное действие, выполняемое при воспроизведении этого шага. */
    public final Runnable action;

    /** Стандартный цвет сообщения (тёмный серый). */
    public static final Color COLOR_DEFAULT = new Color(0x222222);

    /** Цвет успешной операции (зелёный). */
    public static final Color COLOR_SUCCESS = new Color(0x2E7D32);

    /** Цвет ошибки или неудачи (красный). */
    public static final Color COLOR_FAIL    = new Color(0xB85555);

    /**
     * Создаёт шаг анимации без действия.
     *
     * @param highlightValue значение узла для подсветки
     * @param highlightTableRow индекс строки таблицы для подсветки
     * @param message текст сообщения для журнала
     * @param color цвет сообщения
     */
    public Step(Integer highlightValue, Integer highlightTableRow, String message, Color color) {
        this.highlightValue = highlightValue;
        this.highlightTableRow = highlightTableRow;
        this.message = message;
        this.color = color;
        this.action = null;
    }

    /**
     * Создаёт шаг анимации с действием.
     *
     * @param highlightValue значение узла для подсветки
     * @param highlightTableRow индекс строки таблицы для подсветки
     * @param message текст сообщения для журнала
     * @param color цвет сообщения
     * @param action действие, выполняемое при воспроизведении шага
     */
    public Step(Integer highlightValue, Integer highlightTableRow, String message, Color color, Runnable action) {
        this.highlightValue = highlightValue;
        this.highlightTableRow = highlightTableRow;
        this.message = message;
        this.color = color;
        this.action = action;
    }
}
