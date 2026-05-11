import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import javax.swing.*;

public class Main {
    /** Точка входа в программу */
    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        }
        catch(Exception ex) {
            System.err.println("Не удалось инициализировать тему оформления");
        }
        SwingUtilities.invokeLater(() -> {
            // Создаем экземпляр нашего окна
            MainFrame frame = new MainFrame();
            // Делаем его видимым
            frame.setVisible(true);
        });
    }
}
