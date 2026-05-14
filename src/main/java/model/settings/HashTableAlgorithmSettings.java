package model.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Настройки визуализации и запуска алгоритма для хеш-таблицы.
 *
 * Дополняет базовые параметры коллекцией исходных значений,
 * которые будут загружены в таблицу при запуске алгоритма.
 */
public class HashTableAlgorithmSettings extends AlgorithmSettings {

    private final List<Integer> initialValues = new ArrayList<>();

    public HashTableAlgorithmSettings(String name, String description, int collectionSize) {
        super(name, description, collectionSize);
    }

    public void addInitialValue(int value) {
        initialValues.add(value);
    }

    public void setInitialValues(List<Integer> values) {
        initialValues.clear();
        if (values != null) {
            initialValues.addAll(values);
        }
    }

    public List<Integer> getInitialValues() {
        return Collections.unmodifiableList(initialValues);
    }
}
