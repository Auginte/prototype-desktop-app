package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * Grouping other zoomable elements
 *
 * @author Aurelijus Banelis
 */
public class ZoomableGroup extends JPanel implements Zoomable {
    private List<ZoomableComponent> components = new LinkedList<ZoomableComponent>();

    public ZoomableGroup() {
        setOpaque(false);
    }

    public void add(ZoomableComponent component) {
        components.add(component);
        this.add(component.getComponent());
    }

    public boolean remove(ZoomableComponent component) {
        this.remove(component.getComponent());
        return components.remove(component);
    }

    public List<ZoomableComponent> getZoomableComponents() {
        return Collections.unmodifiableList(components);
    }

    public void zoomed(double z) {
        for (ZoomableComponent zoomableComponent : components) {
            if (zoomableComponent.getComponent() instanceof Zoomable) {
                ((Zoomable) zoomableComponent.getComponent()).zoomed(z);
            }
        }
    }
}
