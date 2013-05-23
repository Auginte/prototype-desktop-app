package lt.banelis.aurelijus.dinosy.prototype.operations;

import lt.banelis.aurelijus.dinosy.prototype.ZoomPanel;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableComponent;

/**
 * Operations specialized to focused zoomable element in ZoomPanel.
 *
 * @author Aurelijus Banelis
 */
public abstract class FocusedOperation extends Operation {

    public FocusedOperation(ZoomPanel panel, String name, Key... keys) {
        super(panel, name, keys);
    }

    @Override
    public void perform() {
        ZoomableComponent component = getPanel().getFocusOwner();
        if (component != null) {
            perform(component, getPanel());
        }
        getPanel().lastFocusOwner = null;
    }

    protected abstract void perform(ZoomableComponent focused, ZoomPanel panel);
}
