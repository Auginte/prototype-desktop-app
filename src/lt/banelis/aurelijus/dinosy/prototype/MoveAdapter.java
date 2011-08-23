package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputListener;

/**
 * Mouse Listener that enambles moving in ZoomPanel
 *
 * @author Aurelijus Banelis
 */
public class MoveAdapter implements MouseInputListener {
    private ZoomPanel parent;
    private ZoomableComponent zoomable;
    private boolean enabled = false;
    private int grabbedX;
    private int grabbedY;
    private boolean beingDragged = false;
    private boolean dragInside = false;

    /**
     * Moves whole plane (all elements)
     */
    public MoveAdapter(ZoomPanel container) {
        dragInside = true;
        this.parent = container;
    }

    /*
     * Move component in zoomable panel
     */
    public MoveAdapter(ZoomPanel parent, ZoomableComponent zoomable) {
        this.parent = parent;
        this.zoomable = zoomable;
    }

    /**
     * @return  <code>true</code> when button delegated for draggin is pressed
     */
    protected boolean isMoveButton(MouseEvent e) {
        boolean controllButtons = e.isShiftDown() || e.isControlDown() || e.isAltDown() || e.isAltGraphDown() || e.isMetaDown();
        return (e.getButton() == MouseEvent.BUTTON1) && (!controllButtons);
    }

    public boolean isEnabled() {
        return parent.isMovable();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    void setBeingDragged(boolean beingDragged) {
        this.beingDragged = beingDragged;
    }

    public boolean isBeingDragged() {
        return beingDragged;
    }

    public void mousePressed(MouseEvent e) {
        if (isEnabled() && isMoveButton(e)) {
            grabbedX = e.getX();
            grabbedY = e.getY();
            beingDragged = true;
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (isEnabled()) {
            beingDragged = false;
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (beingDragged) {
            drag(e);
            //TODO: optimise
            e.getComponent().repaint();
        }
    }

    private void drag(MouseEvent e) {
        if (dragInside) {
            int differenceX = e.getX() - grabbedX;
            int differenceY = e.getY() - grabbedY;
            parent.translate(differenceX, differenceY, true);
            grabbedX = e.getX();
            grabbedY = e.getY();
        } else {
            double x = zoomable.getLocation().getX() + e.getX() - grabbedX;
            double y = zoomable.getLocation().getY() + e.getY() - grabbedY;
            zoomable.setLocation(x, y);
            if (parent.getConnections().size() > 0) {
                parent.repaint();
            }
        }
    }

    
    public void mouseClicked(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    public void mouseExited(MouseEvent e) { }
    public void mouseMoved(MouseEvent e) { }
}
