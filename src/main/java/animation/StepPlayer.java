package animation;

import javax.swing.*;
import java.util.List;

public class StepPlayer {

    private int stepDelayMS = 50;

    private final Timer timer;

    private List<Step> steps;
    private Step activeStep;
    private int currentIndex;

    private Integer activeValue;
    private Integer activeTableRow;

    private final Runnable onRepaint;
    private final java.util.function.BiConsumer<String, java.awt.Color> onLog;

    public StepPlayer(Runnable onRepaint,
                      java.util.function.BiConsumer<String, java.awt.Color> onLog) {

        this.onRepaint = onRepaint;
        this.onLog = onLog;

        timer = new Timer(stepDelayMS, e -> playNextStep());
        timer.setRepeats(true);
    }

    public void setStepDelay(int stepDelayMS) {
        this.stepDelayMS = stepDelayMS;
        timer.setDelay(stepDelayMS);
    }

    public void play(List<Step> steps) {
        timer.stop();

        this.steps = steps;
        this.currentIndex = 0;
        this.activeValue = null;

        timer.start();
    }

    public Integer getActiveValue() {
        return activeValue;
    }

    private void playNextStep() {

        if (steps == null || currentIndex >= steps.size()) {
            activeValue = null;
            timer.stop();
            onRepaint.run();
            return;
        }

        activeStep = steps.get(currentIndex++);

        if (activeStep.action != null)
            activeStep.action.run();

        activeValue = activeStep.highlightValue;

        onLog.accept(activeStep.message, activeStep.color);

        onRepaint.run();
    }

    public Step getActiveStep() {
        return activeStep;
    }
}