package logic.hashtable;

import logic.AlgorithmRunner;
import model.settings.HashTableAlgorithmSettings;
import logic.tree.BinarySearchTree;
import util.AppLogger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Реализация интерфейса запуска/сохранения/загрузки для визуализации хеш-таблицы.
 */
public class HashTableAlgorithmRunner implements AlgorithmRunner<HashTableAlgorithmSettings> {

    private static final String FILE_HEADER = "# hash-table-visualiser v1";
    private static final String FILE_EXTENSION = ".bstht";

    private final Logger logger = AppLogger.getLogger(HashTableAlgorithmRunner.class);

    private BSTHashTable currentTable;
    private HashTableAlgorithmSettings currentSettings;

    @Override
    public void run(HashTableAlgorithmSettings settings) {
        if (settings == null) {
            throw new IllegalArgumentException("Настройки алгоритма не должны быть null");
        }

        currentSettings = settings;
        currentTable = new BSTHashTable(settings.getCollectionSize());

        for (int value : settings.getInitialValues()) {
            currentTable.add(value);
        }

        logger.info(() -> String.format(Locale.ROOT,
                "Алгоритм инициализирован: capacity=%d, values=%d",
                settings.getCollectionSize(), settings.getInitialValues().size()));
    }

    public BSTHashTable getCurrentTable() {
        return currentTable;
    }

    public HashTableAlgorithmSettings getCurrentSettings() {
        return currentSettings;
    }

    @Override
    public void saveResults(Path file) throws IOException {
        ensureTableReady();

        Path normalized = normalizeOutputPath(file);
        Files.createDirectories(normalized.toAbsolutePath().getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(normalized, StandardCharsets.UTF_8)) {
            writer.write(FILE_HEADER);
            writer.newLine();
            writer.write("capacity=" + currentTable.getCapacity());
            writer.newLine();

            for (int i = 0; i < currentTable.getCapacity(); i++) {
                BinarySearchTree bucket = currentTable.getBucketAt(i);
                List<Integer> values = bucket.getPreOrderValues();
                writer.write("bucket" + i + "=" + join(values));
                writer.newLine();
            }
        }

        logger.info(() -> "Состояние сохранено в файл: " + normalized.toAbsolutePath());
    }

    @Override
    public HashTableAlgorithmSettings loadResults(Path file) throws IOException {
        Path normalized = normalizeInputPath(file);
        List<String> lines = Files.readAllLines(normalized, StandardCharsets.UTF_8);

        if (lines.isEmpty() || !FILE_HEADER.equals(lines.get(0).trim())) {
            logger.warning(() -> "Попытка загрузки файла неверного формата: " + normalized.toAbsolutePath());
            throw new IOException("Неверный формат файла хеш-таблицы");
        }

        int capacity = -1;
        List<List<Integer>> buckets = new ArrayList<>();

        for (int lineIndex = 1; lineIndex < lines.size(); lineIndex++) {
            String line = lines.get(lineIndex).trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            if (line.startsWith("capacity=")) {
                capacity = parseIntStrict(line.substring("capacity=".length()), "capacity");
                buckets = new ArrayList<>(capacity);
                for (int i = 0; i < capacity; i++) {
                    buckets.add(new ArrayList<>());
                }
                continue;
            }

            if (!line.startsWith("bucket")) {
                logger.warning(() -> "Пропущена неизвестная строка: " + line);
                continue;
            }

            int eq = line.indexOf('=');
            if (eq < 0) {
                throw new IOException("Неверная строка бакета: " + line);
            }

            String prefix = line.substring(0, eq).trim();
            int bucketIndex = parseBucketIndex(prefix);
            String payload = line.substring(eq + 1).trim();

            if (capacity < 0) {
                throw new IOException("Сначала должна идти строка capacity=");
            }
            if (bucketIndex < 0 || bucketIndex >= capacity) {
                throw new IOException("Индекс бакета вне диапазона: " + bucketIndex);
            }

            buckets.set(bucketIndex, parseValues(payload));
        }

        if (capacity <= 0) {
            throw new IOException("Файл не содержит корректную ёмкость таблицы");
        }

        HashTableAlgorithmSettings settings = new HashTableAlgorithmSettings(
                "Визуализация хеш-таблицы",
                "Бинарное дерево поиска в каждом бакете хеш-таблицы",
                capacity
        );

        run(settings);
        for (int i = 0; i < capacity; i++) {
            for (int value : buckets.get(i)) {
                currentTable.add(value);
            }
        }

        logger.info(() -> "Состояние загружено из файла: " + normalized.toAbsolutePath());
        return settings;
    }

    private void ensureTableReady() {
        if (currentTable == null) {
            throw new IllegalStateException("Таблица ещё не инициализирована");
        }
    }

    private Path normalizeOutputPath(Path file) {
        if (file == null) {
            throw new IllegalArgumentException("Путь к файлу не должен быть null");
        }

        String name = file.getFileName() == null ? "" : file.getFileName().toString().toLowerCase(Locale.ROOT);
        if (!name.endsWith(FILE_EXTENSION)) {
            return file.resolveSibling(file.getFileName() + FILE_EXTENSION);
        }
        return file;
    }

    private Path normalizeInputPath(Path file) {
        if (file == null) {
            throw new IllegalArgumentException("Путь к файлу не должен быть null");
        }
        return file;
    }

    private String join(List<Integer> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(values.get(i));
        }
        return sb.toString();
    }

    private List<Integer> parseValues(String text) throws IOException {
        List<Integer> values = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return values;
        }

        String[] parts = text.split(",");
        for (String part : parts) {
            String token = part.trim();
            if (!token.isEmpty()) {
                values.add(parseIntStrict(token, "value"));
            }
        }
        return values;
    }

    private int parseBucketIndex(String prefix) throws IOException {
        if (!prefix.startsWith("bucket")) {
            throw new IOException("Неверный префикс бакета: " + prefix);
        }
        return parseIntStrict(prefix.substring("bucket".length()), "bucket index");
    }

    private int parseIntStrict(String text, String fieldName) throws IOException {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException ex) {
            throw new IOException("Не удалось разобрать поле " + fieldName + ": " + text, ex);
        }
    }
}
