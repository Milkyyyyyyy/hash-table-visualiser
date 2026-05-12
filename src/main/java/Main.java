import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatPropertiesLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;

/**
 * Главная точка входа в приложение.
 *
 * Отвечает за:
 * - Инициализацию темы оформления FlatLaf (современный светлый стиль)
 * - Запуск главного окна приложения в потоке EDT (Event Dispatch Thread)
 *
 * Используемая тема: {@link FlatMacLightLaf} — чистый светлый дизайн, похожий на macOS.
 */
public class Main {

    /**
     * Точка входа приложения.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (Exception ex) {
            System.err.println("Ошибка при инициализации FlatLaf: " + ex.getMessage());
        }

        // Запускаем GUI в потоке EDT, как требует Swing
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
