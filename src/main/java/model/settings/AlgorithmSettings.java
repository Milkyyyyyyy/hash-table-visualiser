package model.settings;

/**
 * Базовый класс настроек работы алгоритма.
 *
 * Содержит общие параметры, подходящие для любой визуализируемой задачи:
 * название, описание и размер используемой коллекции.
 */
public abstract class AlgorithmSettings {

    private final String name;
    private final String description;
    private final int collectionSize;

    protected AlgorithmSettings(String name, String description, int collectionSize) {
        if (collectionSize <= 0) {
            throw new IllegalArgumentException("Размер коллекции должен быть положительным");
        }
        this.name = name;
        this.description = description;
        this.collectionSize = collectionSize;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCollectionSize() {
        return collectionSize;
    }
}
