package lt.banelis.aurelijus.dinosy.prototype.operations;

import lt.banelis.aurelijus.dinosy.prototype.ZoomPanel;

/**
 * Operations adding new elements to panel.
 *
 * @author Aurelijus Banelis
 */
public abstract class AddOperation extends PanelOperation {

    public AddOperation(ZoomPanel panel, String what, Key... keys) {
        super(panel, what, keys);
    }

    @Override
    public final String getName() {
        return "Add " + super.getName();
    }
}
