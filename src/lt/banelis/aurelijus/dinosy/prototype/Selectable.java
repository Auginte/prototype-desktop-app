package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Elemetn can be selected.
 *
 * @author Aurelijus Banelis
 * @todo use inheritance not markers
 */
public interface Selectable {
    public boolean isSelectable();
    public void setSelectable(boolean selectable);
    public boolean isSelected();
    public void setSelected(boolean selected);
    public void paintSelected(Graphics g);

    public static Color selectionColor = new Color(250, 255, 150);
    public static Color deselectionColor = new Color(150, 255, 250);
    public static MouseListener defaultSelelectionListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.isShiftDown()) {
                Selectable selectable = null;
                if ((e.getComponent() instanceof Selectable) && ((Selectable) e.getComponent()).isSelectable()) {
                    selectable= (Selectable) e.getComponent();
                } else if (e.getComponent().getParent() != null && (e.getComponent().getParent() instanceof Selectable)) {
                    selectable= (Selectable) e.getComponent().getParent();
                }
                if (selectable != null) {
                    selectable.setSelected(!selectable.isSelected());
                }
            }
        }
    };
}
