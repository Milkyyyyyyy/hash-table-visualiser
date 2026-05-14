package logic;

import model.settings.AlgorithmSettings;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Интерфейс запуска и сериализации результата работы алгоритма.
 *
 * @param <S> тип настроек алгоритма
 */
public interface AlgorithmRunner<S extends AlgorithmSettings> {

    /**
     * Запускает алгоритм с заданными настройками.
     *
     * @param settings настройки работы алгоритма
     */
    void run(S settings);

    /**
     * Сохраняет результат работы алгоритма в текстовый файл.
     *
     * @param file путь к файлу
     * @throws IOException если произошла ошибка ввода-вывода
     */
    void saveResults(Path file) throws IOException;

    /**
     * Загружает результат работы алгоритма из текстового файла.
     *
     * @param file путь к файлу
     * @return настройки загруженного состояния
     * @throws IOException если произошла ошибка ввода-вывода или формат файла неверный
     */
    S loadResults(Path file) throws IOException;
}
