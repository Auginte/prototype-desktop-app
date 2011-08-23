package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.event.MouseEvent;
import java.io.Serializable;
import javax.swing.event.MouseInputListener;

/**
 * Translates plane when mouse reaches edge
 *
 * @author Aurelijus Banelis
 */
class EdgeAdapter implements MouseInputListener, Serializable {
    private ZoomPanel parent;
    private volatile boolean translating = false;
    private volatile int edgeLeft;
    private volatile int edgeTop;
    private volatile int edgeRight;
    private volatile int edgeBottom;
    private transient Thread updater = null;
    private boolean isDragged = false;


    public EdgeAdapter(ZoomPanel parent, int delay) {
        this.parent = parent;
    }

    public void mouseMoved(MouseEvent e) {
        isDragged = false;
        record(e);
    }

    public void mouseEntered(MouseEvent e) {
        isDragged = false;
        record(e);
    }

    public void mouseExited(MouseEvent e) {
        translating = false;
    }

    public void mouseDragged(MouseEvent e) {
        if (e.getComponent() == parent) {
            isDragged = true;
        }
        record(e);
    }

    private void record(MouseEvent e) {
        if (parent.getTranslateEdge() > 0) {
            int x = -1;
            int y = -1;
            if (e.getComponent() == parent) {
                x = e.getX();
                y = e.getY();
            } else if (e.getComponent().getParent() == parent) {
                x = e.getX() + e.getComponent().getLocation().x;
                y = e.getY() + e.getComponent().getLocation().y;
            } else if (e.getComponent().getParent().getParent() == parent) {
                x = e.getX() + e.getComponent().getLocation().x + e.getComponent().getParent().getLocation().x;
                y = e.getY() + e.getComponent().getLocation().y + e.getComponent().getParent().getLocation().y;
            }
            if (x != -1) {
                edgeLeft = parent.getTranslateEdge() - x;
                edgeTop = parent.getTranslateEdge() - y;
                edgeRight = parent.getTranslateEdge() - (parent.getWidth() - x);
                edgeBottom = parent.getTranslateEdge() - (parent.getHeight() - y);
                if (edgeLeft > 0 || edgeRight > 0 || edgeTop > 0 || edgeBottom > 0) {
                    translating = true;
                } else {
                    translating = false;
                }
                runTimmer();
            }
        }
    }

    private void runTimmer() {
        if (updater == null || updater.getState() == Thread.State.TERMINATED) {
            updater = new Thread() {
                @Override
                public void run() {
                    while (translating) {
                        translate();
                        try {
                            Thread.sleep(parent.getTranslateEdgesDelay());
                        } catch (InterruptedException ex) {
                            translate();
                        }
                    }
                }
            };
            updater.start();
        }
    }

    private synchronized void translate() {
        if (parent.getTranslateEdge() > 0) {
            float sign = isDragged ? -0.5f : 0.5f;
            if (edgeLeft > 0) {
                parent.translate(sign * edgeLeft, 0);
            } else if (edgeRight > 0) {
                parent.translate(-sign * edgeRight, 0);
            }
            if (edgeTop > 0) {
                parent.translate(0, sign * edgeTop);
            } else if (edgeBottom > 0) {
                parent.translate(0, -sign * edgeBottom);
            }
            parent.repaint();
        }
    }

    public void mouseClicked(MouseEvent e) { }
    public void mousePressed(MouseEvent e) { }
    public void mouseReleased(MouseEvent e) { }
}
