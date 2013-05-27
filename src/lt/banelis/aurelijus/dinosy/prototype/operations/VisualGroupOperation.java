package lt.banelis.aurelijus.dinosy.prototype.operations;

import java.awt.Component;
import java.util.List;
import lt.banelis.aurelijus.dinosy.prototype.ZoomPanel;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableComponent;

/**
 * Operations specialized to visually grouped elements.
 *
 * @author Aurelijus Banelis
 */
public abstract class VisualGroupOperation extends Operation {

    public VisualGroupOperation(ZoomPanel panel, String name, Key... keys) {
        super(panel, name, keys);
    }

    @Override
    public void perform() {
        ZoomableComponent component = getPanel().getFocusOwner();
        if (component != null) {
            List<Component> group = getPanel().getVisualGrouping().getGroup(component.getComponent());
            perform(group, getPanel());
        }
    }

    protected abstract void perform(List<Component> component, ZoomPanel panel);
}
