package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.BorderLayout;
import java.awt.Label;
import javax.swing.JLabel;
import org.junit.Test;
import static org.junit.Assert.*;

public class ZoomPanelTest {
    @Test
    public void testAddComponent() {
        JLabel label = new JLabel("Test1");
        ZoomPanel panel = new ZoomPanel();
        panel.add(label);
        assertEquals(1, panel.getZoomableComponentsCount());
        assertNotNull(panel.getZoomableComponent(label));
        assertEquals("Test1", getText(panel, label));
        panel.remove(0);
        assertEquals(0, panel.getZoomableComponentsCount());
    }

    @Test
    public void testAddWithConstraints() {
        ZoomPanel panel = new ZoomPanel();
        panel.add(new Label());
        panel.add(new JLabel(), BorderLayout.NORTH);
        panel.add(new JLabel(), BorderLayout.NORTH, 1);
    }

    @Test
    public void testAddMore() {
        JLabel label1 = new JLabel("Test1");
        JLabel label2 = new JLabel("Test2");
        JLabel label3 = new JLabel("Test3");
        ZoomPanel panel = new ZoomPanel();

        panel.add(label1);
        panel.add(label2);
        panel.add(label3);
        assertEquals("Test2",  getText(panel, label2));

        panel.remove(label2);
        assertNull(panel.getZoomableComponent(label2));
        assertEquals(2, panel.getZoomableComponentsCount());
        assertEquals("Test1", getText(panel, label1));
        assertEquals("Test3", getText(panel, label3));
    }

    private static String getText(ZoomPanel panel, JLabel label) {
        return ((JLabel) panel.getZoomableComponent(label).getComponent()).getText();
    }

    @Test
    public void testReset() {
        ZoomPanel panel = new ZoomPanel();
        JLabel label1 = new JLabel("Test1");
        JLabel label2 = new JLabel("Test2");
        label1.setSize(123, 456);
        label2.setLocation(234, 567);
        panel.add(label1);
        panel.add(label2);

        assertEquals(label1, panel.getComponent(0));
        assertEquals(label2, panel.getComponent(1));

        panel.translate(100, 15);
        assertNotSame(234, panel.getComponent(1).getLocation().getX());
        panel.reset();
        assertEquals(234, panel.getComponent(1).getLocation().getX(), 0.1);

        label1.setSize(987, 654);
        assertNotSame(123, panel.getComponent(0).getSize().getWidth());
        panel.reset();
        assertEquals(123, panel.getComponent(0).getSize().getWidth(), 0.1);
    }

    @Test
    public void testZoomSimple() {
        ZoomPanel panel = new ZoomPanel();
        JLabel label1 = new JLabel("Test1");
        label1.setLocation(-5, -5);
        label1.setSize(10, 10);
        panel.add(label1);
        panel.zoom(2, 0, 0);
        assertEquals(-10, label1.getLocation().x);
        assertEquals(-10, label1.getLocation().y);
        assertEquals(20, label1.getSize().width);
        assertEquals(20, label1.getSize().height);
    }

    @Test
    public void testZoomCenter() {
        ZoomPanel panel = new ZoomPanel();
        panel.setSize(100, 200);
        JLabel label1 = new JLabel("Test1");
        label1.setLocation(100/2, 200/2);
        label1.setSize(10, 20);
        panel.add(label1);

        panel.zoom(2);
        assertLabel(label1, 50, 100, 20, 40);
    }

    @Test
    public void testZoomConsistency() {
        ZoomPanel panel = new ZoomPanel();
        panel.setSize(100, 200);
        JLabel label1 = new JLabel("Test1");
        label1.setLocation(12, 34);
        label1.setSize(56, 78);

        panel.add(label1);
        JLabel label2 = new JLabel("Test2");
        label2.setLocation(90, 123);
        label2.setSize(45, 6879);
        panel.add(label2);

        panel.zoom(16);
        panel.zoom((double) 1/16);
        assertLabel(label1, 12, 34, 56, 78);
        assertLabel(label2, 90, 123, 45, 6879);
        assertZoomableComponent(panel, label1, 12, 34, 56, 78);
        assertZoomableComponent(panel, label2, 90, 123, 45, 6879);

        panel.zoom((double) 1/14, 123, 456);
        panel.zoom(14, 123, 456);
        assertLabel(label1, 12, 34, 56, 78);
        assertLabel(label2, 90, 123, 45, 6879);
    }

    @Test
    public void testHightPrecision() {
        ZoomPanel panel = new ZoomPanel();
        panel.setSize(100, 200);
        JLabel label1 = new JLabel("Test1");
        label1.setLocation(12, 34);
        label1.setSize(56, 78);

        panel.add(label1);
        JLabel label2 = new JLabel("Test2");
        label2.setLocation(90, 123);
        label2.setSize(45, 6879);
        panel.add(label2);

        panel.zoom((double) 1/12345, 123, 456);
        panel.zoom(12345, 123, 456);
        assertZoomableComponent(panel, label1, 12, 34, 56, 78, 0.000000001);
        assertZoomableComponent(panel, label2, 90, 123, 45, 6879, 0.000000001);
    }

    @Test
    public void testZoomMulti() {
        ZoomPanel panel = new ZoomPanel();
        JLabel label1 = new JLabel("Test1");
        label1.setLocation(0,0);
        label1.setSize(4, 4);
        panel.add(label1);
        panel.zoom(2, 2, 2);
        assertLabel(label1, -2, -2, 8, 8);

        panel.zoom(2, -1, 0);
        assertLabel(label1, -3, -4, 16, 16);

        panel.zoom((double) 1/2, 1, 4);
        assertLabel(label1, -1, 0, 8, 8);
    }

    private void assertLabel(JLabel label, int x, int y, int width, int height) {
        assertEquals(x, label.getLocation().getX(), 1);
        assertEquals(y, label.getLocation().getY(), 1);
        assertEquals(width, label.getSize().width);
        assertEquals(height, label.getSize().height);
    }

    private void assertZoomableComponent(ZoomPanel panel, JLabel label, int x, int y, int width, int height) {
        assertZoomableComponent(panel, label, x, y, width, height, 0.000000001);
    }

    private void assertZoomableComponent(ZoomPanel panel, JLabel label, int x, int y, int width, int height, double delta) {
        ZoomableComponent component = panel.getZoomableComponent(label);
        assertEquals(x, component.getPreciseLocation().getX(), delta);
        assertEquals(y, component.getPreciseLocation().getY(), delta);
        assertEquals(width, component.getPreciseSize().getWidth(), delta);
        assertEquals(height, component.getPreciseSize().getHeight(), delta);
    }

}