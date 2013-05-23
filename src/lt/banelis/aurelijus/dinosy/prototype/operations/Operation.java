package lt.banelis.aurelijus.dinosy.prototype.operations;

import lt.banelis.aurelijus.dinosy.prototype.ZoomPanel;

/**
 * Runnable element
 *
 * @author Aurelijus Banelis
 */
public abstract class Operation {

    private ZoomPanel panel;
    private String name;
    private Key[] keys;
    //TODO: icon, description, internalization, groups, context

    public Operation(ZoomPanel panel, String name, Key... keys) {
        this.panel = panel;
        this.name = name;
        this.keys = keys;
    }

    public Key[] getKeys() {
        return keys;
    }

    public String getName() {
        return name;
    }

    public ZoomPanel getPanel() {
        return panel;
    }

    public void setPanel(ZoomPanel panel) {
        this.panel = panel;
    }

    public abstract void perform();
    //TODO: public boolean isActive
}
