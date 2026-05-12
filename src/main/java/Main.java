import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
        } catch (Exception ex) {
            System.err.println("Не удалось инициализировать FlatLaf");
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
