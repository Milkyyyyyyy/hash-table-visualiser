package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class AppLogger {

    private static boolean configured = false;

    private AppLogger() {
    }

    public static synchronized void configure() {
        if (configured) return;

        Logger root = Logger.getLogger("");
        root.setUseParentHandlers(false);

        for (java.util.logging.Handler handler : root.getHandlers()) {
            root.removeHandler(handler);
        }

        root.setLevel(Level.WARNING);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(new SimpleFormatter());
        root.addHandler(consoleHandler);

        try {
            Path logDir = Path.of("logs");
            Files.createDirectories(logDir);

            String timestamp = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

            FileHandler fileHandler = new FileHandler(logDir.resolve("app_" + timestamp + ".log").toString());
            fileHandler.setLevel(Level.INFO);
            fileHandler.setFormatter(new SimpleFormatter());
            root.addHandler(fileHandler);
        } catch (IOException ex) {
            root.log(Level.WARNING, "Не удалось создать файловый логгер", ex);
        }

        Logger.getLogger("sun.awt").setLevel(Level.WARNING);
        Logger.getLogger("java.awt").setLevel(Level.WARNING);
        Logger.getLogger("javax.swing").setLevel(Level.WARNING);
        Logger.getLogger("java.sql").setLevel(Level.WARNING);

        Logger.getLogger("your.package.name").setLevel(Level.INFO);

        configured = true;
    }

    public static Logger getLogger(Class<?> type) {
        configure();
        Logger logger = Logger.getLogger(type.getName());
        logger.setLevel(Level.ALL);
        return logger;
    }
}