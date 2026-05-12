package visual;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class SelectedCellBorderRenderer extends DefaultTableCellRenderer {

    private static final Color SELECTED_BORDER = new Color(0, 120, 255, 120); // прозрачный синий

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        setHorizontalAlignment(SwingConstants.CENTER);

        if (isSelected) {
            setBorder(BorderFactory.createLineBorder(SELECTED_BORDER, 1));
        } else {
            setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        }

        return this;
    }
}
