package lt.banelis.aurelijus.dinosy.prototype.operations;

import lt.banelis.aurelijus.dinosy.prototype.ZoomPanel;

/**
 * Operation specialized to ZoomPanel attributes.
 *
 * @author Aurelijus Banelis
 */
public abstract class PanelOperation extends Operation {

    public PanelOperation(ZoomPanel panel, String name, Key... keys) {
        super(panel, name, keys);
    }

    @Override
    public void perform() {
        perform(getPanel());
        getPanel().lastFocusOwner = null;
    }

    protected abstract void perform(ZoomPanel panel);
}
