package visual.table;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Рендерер ячейки таблицы с управляемой обводкой при выделении.
 *
 * Отвечает за визуальное отображение ячейки индекса в таблице хеш-таблицы,
 * показывая синюю обводку при выделении строки.
 */
public class SelectedCellBorderRenderer extends DefaultTableCellRenderer {

    /** Цвет обводки выделенной ячейки (прозрачный синий). */
    private static final Color SELECTED_BORDER = new Color(0, 120, 255, 120);

    /**
     * Возвращает компонент для отрисовки ячейки таблицы.
     *
     * При выделении ячейки применяется цветная обводка, при отсутствии выделения —
     * пустой отступ для выравнивания.
     *
     * @param table таблица, содержащая ячейку
     * @param value значение ячейки
     * @param isSelected выделена ли ячейка
     * @param hasFocus имеет ли ячейка фокус
     * @param row номер строки ячейки
     * @param column номер колонки ячейки
     * @return компонент для отрисовки
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Выравниваем текст по центру
        setHorizontalAlignment(SwingConstants.CENTER);

        // Устанавливаем обводку в зависимости от состояния выделения
        if (isSelected) {
            setBorder(BorderFactory.createLineBorder(SELECTED_BORDER, 1));
        } else {
            setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        }

        return this;
    }
}
