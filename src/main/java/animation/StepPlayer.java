package animation;

import javax.swing.*;
import java.util.List;

/**
 * Контроллер воспроизведения анимированной последовательности шагов операций.
 *
 * Отвечает за:
 * - Запуск и остановку воспроизведения шагов
 * - Последовательное воспроизведение каждого шага с настраиваемой задержкой
 * - Вызов коллбэков для обновления UI (перерисовка) и логирования
 * - Выполнение действий, привязанных к шагам (например, реальные операции над данными)
 */
public class StepPlayer {

    /** Задержка между шагами в миллисекундах (по умолчанию 50 мс). */
    private int stepDelayMS = 50;

    /** Таймер Swing для регулярного воспроизведения шагов. */
    private final Timer timer;

    /** Список шагов для текущей операции. */
    private List<Step> steps;

    /** Текущий активный шаг. */
    private Step activeStep;

    /** Индекс текущего шага в списке. */
    private int currentIndex;

    /** Текущее подсвечиваемое значение узла. */
    private Integer activeValue;

    /** Текущая подсвечиваемая строка таблицы. */
    private Integer activeTableRow;

    /** Коллбэк для запроса перерисовки UI при изменении шага. */
    private final Runnable onRepaint;

    /** Коллбэк для записи сообщения в журнал операций. */
    private final java.util.function.BiConsumer<String, java.awt.Color> onLog;

    /**
     * Создаёт новый плеер шагов.
     *
     * @param onRepaint коллбэк для перерисовки компонентов при каждом шаге
     * @param onLog коллбэк для логирования сообщений шага
     */
    public StepPlayer(Runnable onRepaint,
                      java.util.function.BiConsumer<String, java.awt.Color> onLog) {
        this.onRepaint = onRepaint;
        this.onLog = onLog;

        timer = new Timer(stepDelayMS, e -> playNextStep());
        timer.setRepeats(true);
    }

    /**
     * Устанавливает задержку между шагами.
     *
     * @param stepDelayMS новая задержка в миллисекундах
     */
    public void setStepDelay(int stepDelayMS) {
        this.stepDelayMS = stepDelayMS;
        timer.setDelay(stepDelayMS);
    }

    /**
     * Начинает воспроизведение последовательности шагов.
     *
     * @param steps список шагов для воспроизведения
     */
    public void play(List<Step> steps) {
        timer.stop();

        this.steps = steps;
        this.currentIndex = 0;
        this.activeValue = null;

        timer.start();
    }

    /**
     * Возвращает текущее подсвечиваемое значение узла.
     *
     * @return значение узла или null
     */
    public Integer getActiveValue() {
        return activeValue;
    }

    /**
     * Воспроизводит следующий шаг в последовательности.
     * Вызывается таймером автоматически.
     */
    private void playNextStep() {
        // Если все шаги проиграны или список пуст — останавливаемся
        if (steps == null || currentIndex >= steps.size()) {
            activeValue = null;
            timer.stop();
            onRepaint.run();
            return;
        }

        // Получаем следующий шаг
        activeStep = steps.get(currentIndex++);

        // Выполняем привязанное к шагу действие (если есть)
        if (activeStep.action != null) {
            activeStep.action.run();
        }

        // Обновляем текущие значения для подсветки
        activeValue = activeStep.highlightValue;
        activeTableRow = activeStep.highlightTableRow;

        // Логируем сообщение шага
        onLog.accept(activeStep.message, activeStep.color);

        // Просим перерисовать UI
        onRepaint.run();
    }

    /**
     * Возвращает текущий активный шаг.
     *
     * @return активный шаг или null
     */
    public Step getActiveStep() {
        return activeStep;
    }
}
