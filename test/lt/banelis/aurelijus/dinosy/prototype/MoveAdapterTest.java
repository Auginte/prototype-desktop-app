package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JLabel;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Aurelijus Banelis
 */
public class MoveAdapterTest {
    @Test
    public void testMoveObject() {
        ZoomPanel panel = new ZoomPanel();
        AdaptedLabel label = new AdaptedLabel("Test1");
        label.setLocation(123, 456);
        label.setSize(100, 100);
        panel.add(label);

        label.onMousePressed(label, 10, 20);
        label.onDrag(label, 80, 60);
        assertEquals(123 + (80 - 10), label.getLocation().x);
        assertEquals(456 + (60 - 20), label.getLocation().y);

        label.onDrag(label, 90, 70);
        assertEquals(123 + (80 - 10) + (90 - 10), label.getLocation().x);
        assertEquals(456 + (60 - 20) + (70 - 20), label.getLocation().y);
    }

    @Test
    public void testMovePlane() {
        AdaptedZoomPanel panel = new AdaptedZoomPanel();
        panel.setTranslateEdges(0);
        JLabel label1 = new JLabel("Test1");
        label1.setLocation(10, 20);
        panel.add(label1);
        JLabel label2 = new JLabel("Test1");
        label2.setLocation(31, 41);
        panel.add(label2);

        panel.onMousePressed(panel, 50, 60);
        panel.onDrag(panel, 40, 30);
        assertLabelLocation(label1, 10 + (40 - 50), 20 + (30 - 60));
        assertLabelLocation(label2, 31 + (40 - 50), 41 + (30 - 60));

        panel.onDrag(panel, 100, 115);
        assertLabelLocation(label1, 0 + (100 - 40), -10 + (115 - 30));
        assertLabelLocation(label2, 21 + (100 - 40), 11 + (115 - 30));
    }

    private void assertLabelLocation(JLabel label, int x, int y) {
        assertEquals(x, label.getLocation().x);
        assertEquals(y, label.getLocation().y);
    }

    private class AdaptedZoomPanel extends ZoomPanel {
        public void onMousePressed(Component component, int x, int y) {
            processMouseEvent(new MouseEvent(component, MouseEvent.MOUSE_PRESSED, 0, MouseEvent.BUTTON1_MASK, x, y, 1, false, MouseEvent.BUTTON1));
        }

        public void onDrag(Component component, int x, int y) {
            processMouseMotionEvent(new MouseEvent(component, MouseEvent.MOUSE_DRAGGED, 0, MouseEvent.BUTTON1_MASK, x, y, 1, false, MouseEvent.BUTTON1));
        }

        public void onMouseReleased(Component component, int x, int y) {
            processMouseEvent(new MouseEvent(component, MouseEvent.MOUSE_RELEASED, 0, MouseEvent.BUTTON1_MASK, x, y, 1, false, MouseEvent.BUTTON1));
        }
    }

    private class AdaptedLabel extends JLabel {
        public AdaptedLabel(String text) {
            super(text);
        }

        public void onMousePressed(Component component, int x, int y) {
            processMouseEvent(new MouseEvent(component, MouseEvent.MOUSE_PRESSED, 0, MouseEvent.BUTTON1_MASK, x, y, 1, false, MouseEvent.BUTTON1));
        }

        public void onDrag(Component component, int x, int y) {
            processMouseMotionEvent(new MouseEvent(component, MouseEvent.MOUSE_DRAGGED, 0, MouseEvent.BUTTON1_MASK, x, y, 1, false, MouseEvent.BUTTON1));
        }

        public void onMouseReleased(Component component, int x, int y) {
            processMouseEvent(new MouseEvent(component, MouseEvent.MOUSE_RELEASED, 0, MouseEvent.BUTTON1_MASK, x, y, 1, false, MouseEvent.BUTTON1));
        }
    }
}