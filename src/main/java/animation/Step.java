package animation;

import java.awt.*;

public class Step {
    public final Integer highlightValue; // какой узел подсветить (null = никакой)
    public final Integer highlightTableRow;
    public final String  message;        // текст для лога
    public final Color   color;          // цвет записи в логе
    public final Runnable action;

    public static final Color COLOR_DEFAULT = new Color(0x222222);
    public static final Color COLOR_SUCCESS = new Color(0x2E7D32);
    public static final Color COLOR_FAIL    = new Color(0xB85555);

    public Step(Integer highlightValue, Integer highlightTableRow, String message, Color color) {
        this.highlightValue = highlightValue;
        this.highlightTableRow = highlightTableRow;
        this.message        = message;
        this.color          = color;
        this.action = null;
    }
    public Step(Integer highlightValue, Integer highlightTableRow, String message, Color color, Runnable action) {
        this.highlightValue = highlightValue;
        this.highlightTableRow = highlightTableRow;
        this.message        = message;
        this.color          = color;
        this.action = action;
    }
}
