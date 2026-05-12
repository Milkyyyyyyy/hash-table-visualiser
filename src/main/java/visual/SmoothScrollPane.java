package visual;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * JScrollPane с инерционной прокруткой колёсиком мыши
 * и плавным переходом к нужной области.
 */
public class SmoothScrollPane extends JScrollPane {

    private static final float FRICTION = 0.9f;
    private static final float WHEEL_SENSITIVITY = 6f;
    private static final int TIMER_INTERVAL_MS = 16;

    private static final float SCROLL_TO_TARGET_SPEED = 0.18f;

    private float velocityY = 0f;
    private Point targetPosition = null;

    private final Timer scrollTimer;

    JTable table;

    public SmoothScrollPane(Component view) {
        super(view);
        if(view instanceof JTable){
            table = (JTable)view;
        }
        scrollTimer = createScrollTimer();
        initMouseWheelListener();
    }

    private void initMouseWheelListener() {
        getViewport().addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                e.consume();
                stopSmoothScrollToTarget();

                velocityY += (float) e.getPreciseWheelRotation() * WHEEL_SENSITIVITY;
                if (!scrollTimer.isRunning()) {
                    scrollTimer.start();
                }
            }
        });
    }

    private Timer createScrollTimer() {
        return new Timer(TIMER_INTERVAL_MS, e -> {
            JViewport viewport = getViewport();
            Point pos = viewport.getViewPosition();

            if (targetPosition != null) {
                int dx = targetPosition.x - pos.x;
                int dy = targetPosition.y - pos.y;

                if (Math.abs(dx) < 1 && Math.abs(dy) < 1) {
                    viewport.setViewPosition(targetPosition);
                    targetPosition = null;
                    velocityY = 0f;
                    scrollTimer.stop();
                    return;
                }

                int newX = pos.x + (int) (dx * SCROLL_TO_TARGET_SPEED);
                int newY = pos.y + (int) (dy * SCROLL_TO_TARGET_SPEED);

                viewport.setViewPosition(clampToViewport(new Point(newX, newY)));
                return;
            }

            int newY = (int) (pos.y + velocityY);
            viewport.setViewPosition(clampToViewport(new Point(pos.x, newY)));

            velocityY *= FRICTION;

            if (Math.abs(velocityY) < 0.1f) {
                velocityY = 0f;
                scrollTimer.stop();
            }
        });
    }

    public void smoothScrollTo(Rectangle targetRect) {
        if (targetRect == null) return;

        JViewport viewport = getViewport();
        if (viewport == null || viewport.getView() == null) return;

        stopWheelInertia();

        Rectangle viewRect = viewport.getViewRect();
        int targetY = targetRect.y - (viewRect.height / 2) + (targetRect.height / 2);

        targetPosition = clampToViewport(new Point(viewRect.x, targetY));

        if (!scrollTimer.isRunning()) {
            scrollTimer.start();
        }
    }

    public void smoothScrollToComponent(Component component) {
        if (component == null) return;

        Rectangle bounds = SwingUtilities.convertRectangle(
                component.getParent(),
                component.getBounds(),
                getViewport().getView()
        );
        smoothScrollTo(bounds);
    }
    public void smoothScrollToRow() {
        int selectedRow = table.getSelectedRow();
        smoothScrollToRow(table, selectedRow);
    }
    public void smoothScrollToRow(JTable table, int row) {
        if (table == null || row < 0 || row >= table.getRowCount()) return;
        Rectangle rect = table.getCellRect(row, 0, true);
        smoothScrollTo(rect);
    }

    private void stopWheelInertia() {
        velocityY = 0f;
    }

    private void stopSmoothScrollToTarget() {
        targetPosition = null;
    }

    private Point clampToViewport(Point p) {
        JViewport viewport = getViewport();
        Dimension viewSize = viewport.getViewSize();
        Dimension extentSize = viewport.getExtentSize();

        int maxX = Math.max(0, viewSize.width - extentSize.width);
        int maxY = Math.max(0, viewSize.height - extentSize.height);

        int x = Math.max(0, Math.min(p.x, maxX));
        int y = Math.max(0, Math.min(p.y, maxY));

        return new Point(x, y);
    }
}