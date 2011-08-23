package lt.banelis.aurelijus.dinosy.prototype;

import java.util.Arrays;
import java.util.List;
import java.awt.Dimension;
import java.util.LinkedList;
import lt.banelis.parser.File;
import lt.banelis.parser.Function;
import lt.banelis.parser.NullNotAllowedException;
import lt.banelis.parser.OnlyRefferenceException;
import lt.banelis.parser.Variable;
import lt.dinosy.datalib.Data;
import lt.dinosy.datalib.Representation;
import lt.dinosy.datalib.Representation.Element;
import lt.dinosy.datalib.Source;
import org.junit.Test;
import static org.junit.Assert.*;
import static lt.banelis.aurelijus.dinosy.prototype.BasicVisualization.getSelf;

/**
 *
 * @author Aurelijus Banelis
 */
public class DataRepresentationTest {

    private Source testSource() {
        return new Source.Event();
    }

    private List<String> s(String ... arguments) {
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


    /*
     * ClassRepresentation
     */

    @Test
    public void testClassRepresentationWithData() {
        Source s = testSource();
        Data.Class data = new Data.Class(s, "A",  s("B"), s(), s("a:int"), s("a()", "b()"));
        ClassRepresentation cr = new ClassRepresentation(data);
        assertNotNull(cr.getData());
        assertSame(data, cr.getData());
        assertEquals("B", cr.whatExtends());
        assertSame(s, cr.getData().getSource());
        assertEquals("A", cr.getClassName());
    }

    @Test
    public void testClassRepresentationCreateData() throws NullNotAllowedException, OnlyRefferenceException {
        lt.banelis.parser.Class classObject = new lt.banelis.parser.Class("A");
        classObject.setExtend(new lt.banelis.parser.Class("B"));
        classObject.setParentFile(new File("test.123"));
        classObject.getParentFile().addVariable(new Variable("a", "int", ""));
        classObject.getParentFile().addFunction(new Function("a"));
        classObject.getParentFile().addFunction(new Function("b"));
        Source s = testSource();
        ClassRepresentation cr = new ClassRepresentation(s, classObject);
        assertNotNull(cr.getData());
        assertEquals("A", cr.getClassName());
        assertEquals("B", cr.whatExtends());

        ZoomPanel panel = new ZoomPanel();
        ZoomableComponent zoomable = panel.addComponent(cr);
        cr.updateData(zoomable);
        assertNotNull(cr.getData().getSource());
        assertTrue(cr.getData().getRepresentations().size() > 0);
        assertNotNull(getSelf(cr));
        assertSame(cr, getSelf(cr).getAssigned());
    }

    @Test
    public void testClassRepresentationUpdatingData() {
        Source s = testSource();
        Data.Class data = new Data.Class(s, "A",  s("B"), s(), s("a:int"), s("a()", "b()"));
        ZoomPanel panel = new ZoomPanel();
        ClassRepresentation cr = new ClassRepresentation(data);
        ZoomableComponent zoomable = panel.addComponent(cr);
        zoomable.setLocation(15, 18);
        zoomable.setSize(new Dimension(20, 27));
        cr.updateData(zoomable);
        assertNotNull(cr.getData());
        Representation.Element representation = (Element) getSelf(cr);
        assertNotNull(representation);
        assertSame(cr, representation.getAssigned());
        assertEquals(15, representation.getPosition().getX(), 0.1);
        assertEquals(18, representation.getPosition().getY(), 0.1);
        assertEquals(20, representation.getSize().getWidth(), 0.1);
        assertEquals(27, representation.getSize().getHeight(), 0.1);
    }
}