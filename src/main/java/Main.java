import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatPropertiesLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;

/**
 * Точка входа в приложение.
 * Инициализирует тему FlatLaf и запускает главное окно в потоке EDT.
 */
public class Main {
    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
            UIManager.setLookAndFeel(new FlatMacLightLaf());

        } catch (Exception ex) {
            System.err.println("Не удалось инициализировать FlatLaf: " + ex.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
