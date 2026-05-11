package visual;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SmoothScrollPane extends JScrollPane {
    private float velocityY = 0f;
    private Timer scrollTimer;
    private static final float FRICTION = 0.9f; // затухание
    private static final float ACCEL = 0.1f;

    public SmoothScrollPane(Component view) {
        super(view);
        initSmoothScrolling();
    }
    private void initSmoothScrolling() {
        final JViewport viewport = getViewport();

        viewport.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                e.consume();
                float delta = (float)e.getPreciseWheelRotation() * 60f; // подбери под себя
                velocityY += delta * ACCEL;

                if (!scrollTimer.isRunning()) {
                    scrollTimer.start();
                }
            }
        });

        scrollTimer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                Point pos = viewport.getViewPosition();
                float step = velocityY;
                velocityY *= FRICTION;

                int newY = (int) (pos.y + step);
                Dimension viewSize = viewport.getViewSize();
                Dimension extent = viewport.getExtentSize();
                newY = Math.max(0, Math.min(newY, viewSize.height - extent.height));

                viewport.setViewPosition(new Point(pos.x, newY));

                if (Math.abs(velocityY) < 0.1f) {
                    scrollTimer.stop();
                    velocityY = 0f;
                }
            }
        });
    }
}