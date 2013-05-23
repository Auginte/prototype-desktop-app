package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import static lt.banelis.aurelijus.dinosy.prototype.helpers.RepresentationsHelper.getSelf;
import lt.dinosy.datalib.Data;
import lt.dinosy.datalib.Representation;
import lt.dinosy.datalib.Representation.Element;
import lt.dinosy.datalib.Source;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Aurelijus Banelis
 */
public class DataRepresentationTest {

    private Source testSource() {
        return new Source.Event();
    }

    private List<String> s(String... arguments) {
        List<String> result = new LinkedList<String>();
        result.addAll(Arrays.asList(arguments));
        return result;
    }

    /*
     * ZoomableLabel
     */
    @Test
    public void testZoomableLabelWithData() {
        Data testData = new Data.Plain("Test data", testSource());
        ZoomableLabel label = new ZoomableLabel(testData);
        assertNotNull(label.getData());
        assertSame(testData, label.getData());
        assertSame(testData.getData(), label.getData().getData());
    }

    @Test
    public void testZoomableLabelCreatingData() {
        ZoomableLabel label = new ZoomableLabel("Test string");
        assertNotNull(label.getData());
        assertSame("Test string", label.getData().getData());
    }

    @Test
    public void testZoomableLabelUpdatingData() {
        ZoomPanel panel = new ZoomPanel();
        ZoomableLabel label = new ZoomableLabel("Test string");
        ZoomableComponent zoomable = panel.addComponent(label);
        zoomable.setLocation(15, 18);
        zoomable.setSize(new Dimension(20, 27));
        label.updateData(zoomable);
        assertNotNull(label.getData());
        Representation.Element representation = (Element) getSelf(label);
        assertNotNull(representation);
        assertSame(label, representation.getAssigned());
        assertEquals(15, representation.getPosition().getX(), 0.1);
        assertEquals(18, representation.getPosition().getY(), 0.1);
        assertEquals(20, representation.getSize().getWidth(), 0.1);
        assertEquals(27, representation.getSize().getHeight(), 0.1);
    }

    @Test
    public void testZoomableLabelSource() {
        Source s = testSource();
        Data data = new Data.Plain("Test data", s);
        ZoomableLabel label = new ZoomableLabel(data);
        assertNotNull(label.getData().getSource());
        assertSame(s, label.getData().getSource());
    }
}
