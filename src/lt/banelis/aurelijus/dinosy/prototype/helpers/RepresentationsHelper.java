package lt.banelis.aurelijus.dinosy.prototype.helpers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import javax.swing.JComponent;
import lt.banelis.aurelijus.dinosy.prototype.DataRepresentation;
import lt.banelis.aurelijus.dinosy.prototype.Idea;
import lt.banelis.aurelijus.dinosy.prototype.ZoomPanel;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableComponent;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableImage;
import lt.dinosy.datalib.Data;
import lt.dinosy.datalib.Representation;

/**
 * Common functions to work with Representation classes.
 *
 * @author Aurelijus Banelis
 */
public class RepresentationsHelper {

    public static void updateRepresentation(ZoomableComponent component, Representation representation, ZoomPanel panel) {
        representation.setAssigned(component.getComponent());
        iniciateRepresentation(component, representation);
        if (representation instanceof Representation.PlaceHolder) {
            int x = (panel.getWidth() / 2) - (component.getSize().width / 2);
            int y = (panel.getHeight() / 2) - (component.getSize().height / 2);
            component.setLocation(x, y);
        } else {
            component.zoom(1);
            Representation.Element element = (Representation.Element) representation;
            if (element.getForeground() != null) {
                component.getComponent().setForeground(element.getForeground());
            }
            if (element.getBackground() != null) {
                if (component.getComponent() instanceof JComponent) {
                    JComponent jComponent = (JComponent) component.getComponent();
                    jComponent.setOpaque(element.getBackground().getAlpha() == 255);
                }
                component.getComponent().setBackground(element.getBackground());
            }
        }
    }

    private static void iniciateRepresentation(ZoomableComponent component, Representation representation) {
        if (representation instanceof Representation.Element) {
            Representation.Element element = (Representation.Element) representation;
            component.zoom(element.getZ());
            component.setLocation(element.getPosition());
            component.setSize(element.getSize());
        } else if (!(component.getComponent() instanceof ZoomableImage)) {
            component.setSize(new Dimension(600, 600));
        }
    }

    public static Representation getSelf(DataRepresentation object) {
        for (Representation representation : object.getData().getRepresentations()) {
            if (representation.getAssigned() == object) {
                return representation;
            }
        }
        return null;
    }

    public static Representation.Element createRepresentation(Data data, ZoomableComponent component, Object assigned) {
        Dimension2D size = component.getOriginalSize();
        if (component.getZ() == 1) {
            size = component.getSize();
        }
        boolean mainIdea = false;
        if (assigned instanceof Idea) {
            mainIdea = ((Idea) assigned).isMainIdea();
        }
        int zIndex = component.getComponent().getParent().getComponentZOrder(component.getComponent());
        Color foreground = component.getComponent().getForeground();
        if (foreground == ColorsHelper.getDefaultForeground()) {
            foreground = null;
        }
        Color background = component.getComponent().getBackground();
        if (component.getComponent() instanceof JComponent) {
            JComponent jComponent = (JComponent) component.getComponent();
            if (!jComponent.isOpaque()) {
                background = null;
            }
        }
        return new Representation.Element(data, component.getLocation(), component.getZ(), size, zIndex, foreground, background, mainIdea, assigned);
    }

    public static Representation.Element getRepresentation(ZoomableComponent component) {
        Representation.Element result = null;
        if (component.getComponent() instanceof DataRepresentation) {
            DataRepresentation dataRepresentation = (DataRepresentation) component.getComponent();
            for (Representation representation : dataRepresentation.getData().getRepresentations()) {
                if (representation.getAssigned() == component.getComponent()) {
                    return (Representation.Element) representation;
                }
            }
        }
        return result;
    }

    public static void setRepresentation(Representation.Element representation, ZoomableComponent component) {
        Dimension2D size = component.getOriginalSize();
        if (component.getZ() == 1) {
            size = component.getSize();
        }
        representation.set(component.getLocation(), component.getZ(), size);
    }
}
