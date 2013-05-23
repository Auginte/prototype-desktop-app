package lt.banelis.aurelijus.dinosy.prototype.helpers;

import java.awt.Color;
import java.util.List;
import javax.swing.JColorChooser;
import lt.banelis.aurelijus.dinosy.prototype.ZoomPanel;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableComponent;

/**
 * Common function to work with default colors;
 *
 * @author Aurelijus Banelis
 */
public class ColorsHelper {

    private static Color defaultForeground = Color.cyan;

    public static void setDefaultForeground(Color defaultForeground) {
        ColorsHelper.defaultForeground = defaultForeground;
    }

    public static Color getDefaultForeground() {
        return defaultForeground;
    }

    public void changeColor(List<ZoomableComponent> selected, final ZoomPanel panel) {
        Color defaultColor = selected.get(0).getComponent().getForeground();
        Color color = JColorChooser.showDialog(panel, "Select foreground", defaultColor);
        if (color != null) {
            for (ZoomableComponent zoomableComponent : selected) {
                zoomableComponent.getComponent().setForeground(color);
            }
            panel.repaint();
            panel.requestFocusInWindow();
        }
    }

    public void changeBackgroundColor(List<ZoomableComponent> selected, final ZoomPanel panel) {
        Color defaultColor = selected.get(0).getComponent().getForeground();
        Color color = JColorChooser.showDialog(panel, "Select foreground", defaultColor);
        if (color != null) {
            for (ZoomableComponent zoomableComponent : selected) {
                zoomableComponent.getComponent().setForeground(color);
            }
            panel.repaint();
            panel.requestFocusInWindow();
        }
    }
}
