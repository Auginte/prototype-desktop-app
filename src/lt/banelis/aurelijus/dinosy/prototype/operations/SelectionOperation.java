package lt.banelis.aurelijus.dinosy.prototype.operations;

import java.util.List;
import lt.banelis.aurelijus.dinosy.prototype.ZoomPanel;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableComponent;

/**
 * Operations performed on all selected elements in ZoomPanel.
 *
 * @author Aurelijus Banelis
 */
public abstract class SelectionOperation extends Operation {

    public SelectionOperation(ZoomPanel panel, String name, Key... keys) {
        super(panel, name, keys);
    }

    @Override
    public void perform() {
        //TODO: optimize
        List<ZoomableComponent> selected = getPanel().getSelected();
        if ((getPanel().getFocusOwner() != null) && (!selected.contains(getPanel().getFocusOwner()))) {
            selected.add(getPanel().getFocusOwner());
        }
        if (selected.size() > 0) {
            perform(selected, getPanel());
        }
        getPanel().lastFocusOwner = null;
    }

    protected abstract void perform(List<ZoomableComponent> selected, ZoomPanel panel);
}
