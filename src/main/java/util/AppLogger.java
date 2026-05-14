package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Общая настройка логирования приложения.
 *
 * Использует консольный и файловый вывод, чтобы фиксировать действия и ошибки
 * по разным уровням логирования.
 */
public final class AppLogger {

    private static boolean configured = false;

    private AppLogger() {
    }

    public static synchronized void configure() {
        if (configured) {
            return;
        }

        Logger root = Logger.getLogger("");
        for (java.util.logging.Handler handler : root.getHandlers()) {
            root.removeHandler(handler);
        }

        root.setLevel(Level.ALL);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(new SimpleFormatter());
        root.addHandler(consoleHandler);

        try {
            Path logDir = Path.of("logs");
            Files.createDirectories(logDir);

            FileHandler fileHandler = new FileHandler(logDir.resolve("app.log").toString(), true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            root.addHandler(fileHandler);
        } catch (IOException ex) {
            root.log(Level.WARNING, "Не удалось создать файловый логгер", ex);
        }

        configured = true;
    }

    public static Logger getLogger(Class<?> type) {
        configure();
        Logger logger = Logger.getLogger(type.getName());
        logger.setLevel(Level.ALL);
        return logger;
    }
}
