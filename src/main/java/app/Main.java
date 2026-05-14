package app;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import util.AppLogger;

import javax.swing.*;

/**
 * Главная точка входа в приложение.
 */
public class Main {

    public static void main(String[] args) {
        AppLogger.configure();

        try {
            FlatLightLaf.setup();
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (Exception ex) {
            System.err.println("Ошибка при инициализации FlatLaf: " + ex.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
