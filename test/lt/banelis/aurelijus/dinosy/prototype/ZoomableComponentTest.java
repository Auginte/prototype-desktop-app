package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.Point;
import java.awt.Dimension;
import javax.swing.JLabel;
import org.junit.Test;
import static org.junit.Assert.*;

public class ZoomableComponentTest {
    @Test
    public void testComponent() {
        ZoomableComponent zoomableComponent = withLabel();
        assertTrue(zoomableComponent.getComponent() instanceof JLabel);
        JLabel label = (JLabel) zoomableComponent.getComponent();
        assertEquals("Test", label.getText());
        
        label.setText("Different test");
        assertEquals("Different test", label.getText());
    }

    @Test
    public void testDimentionReset() {
        ZoomableComponent zoomableComponent = withLabel();
        JLabel label = (JLabel) zoomableComponent.getComponent();
        Dimension originalSize = new Dimension(15, 73);
        label.setSize(100, 200);
        assertEquals(100, zoomableComponent.getComponent().getSize().width);
        assertEquals(200, zoomableComponent.getComponent().getSize().height);
        zoomableComponent.reset();
        assertEquals(zoomableComponent.getComponent().getSize(), originalSize);
    }

    @Test
    public void testLocationReset() {
        ZoomableComponent zoomableComponent = withLabel();
        JLabel label = (JLabel) zoomableComponent.getComponent();
        Point originalLocation = label.getLocation();
        label.setLocation(100, 200);
        assertEquals(100, zoomableComponent.getComponent().getLocation().x);
        assertEquals(200, zoomableComponent.getComponent().getLocation().y);
        zoomableComponent.reset();
        assertEquals(originalLocation, zoomableComponent.getComponent().getLocation());
    }

    @Test
    public void testZoomReset() {
        ZoomableComponent zoomableComponent = withLabel();
        JLabel label = (JLabel) zoomableComponent.getComponent();
        Point originalLocation = label.getLocation();
        Dimension originalSize = label.getSize();

        zoomableComponent.zoom(15, 0, 0);
        zoomableComponent.reset();
        assertEquals(originalLocation, zoomableComponent.getComponent().getLocation());
        assertEquals(originalSize, zoomableComponent.getComponent().getSize());

        zoomableComponent.zoom(-0.5, 0, 0);
        zoomableComponent.reset();
        assertEquals(originalLocation, zoomableComponent.getComponent().getLocation());
        assertEquals(originalSize, zoomableComponent.getComponent().getSize());
    }

    private ZoomableComponent withLabel() {
        JLabel label = new JLabel("Test");
        label.setSize(15, 73);
        return new ZoomableComponent(label);
    }

}
