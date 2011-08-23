package lt.banelis.aurelijus.dinosy.prototype;

import javax.swing.JLabel;
import java.awt.Graphics;
import org.junit.After;
import org.junit.Before;
import java.util.Arrays;
import java.util.List;
import java.awt.Component;
import java.util.Calendar;
import java.util.LinkedList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests performance of zoomable component
 * Use this test (uncoment) only when testing performance.
 *
 * @author Aurelijus Banelis
 */
public class PerformanceTest {
//    private static final int n = 1000;
//    private static long started = 0;
//    private static String lastTest = "";
//    private static final boolean printTime = false;
//
//    @Before
//    public void safeStartTime() {
//        if (printTime) {
//            started = System.nanoTime();
//        }
//    }
//
//    @After
//    public void calculateTime() {
//        if (printTime) {
//            long took = System.nanoTime() - started;
//            System.out.println(lastTest);
//            System.out.println(" " + (took / 1000000000) + "s " + (took / 1000000) + " " + (took % 1000000 / 1000) + "," + (took % 1000) + " = " + took + " = " + System.nanoTime() + " - " + started);
//        }
//    }
//
//    @Test
//    public void testAddingAndRemoving() { lastTest = (new Throwable()).getStackTrace()[0].getMethodName();
//        ZoomPanel panel = new ZoomPanel();
//        for (int i= 0; i < n; i++) {
//            ZoomableLabel label = new ZoomableLabel("Test " + i);
//            panel.add(label);
//        }
//        List<Component> toDelete = new LinkedList<Component>();
//        toDelete.addAll(Arrays.asList(panel.getComponents()));
//        for (Component component : toDelete) {
//            panel.remove(component);
//        }
//    }
//
//    @Test
//    public void testManyPanels() { lastTest = (new Throwable()).getStackTrace()[0].getMethodName();
//        List<ZoomPanel> panels = new LinkedList<ZoomPanel>();
//        for (int i= 0; i < n; i++) {
//            panels.add(new ZoomPanel());
//        }
//        panels.clear();
//    }
//
//    @Test
//    public void testZooming1() { lastTest = (new Throwable()).getStackTrace()[0].getMethodName();
//        ZoomPanel panel = new ZoomPanel();
//        for (int i= 0; i < n; i++) {
//            ZoomableLabel label = new ZoomableLabel("Test label" + i);
//            panel.add(label);
//        }
//        for (int i= 0; i < n; i++) {
//            panel.zoom(1.1);
//        }
//        for (int i= 0; i < n; i++) {
//            panel.zoom(0.9);
//        }
//    }
//
//    @Test
//    public void testZooming2() { lastTest = (new Throwable()).getStackTrace()[0].getMethodName();
//        ZoomPanel panel = new ZoomPanel();
//        for (int i= 0; i < n; i++) {
//            ZoomableLabel label = new ZoomableLabel("Label" + i);
//            panel.add(label);
//        }
//        for (int i= 0; i < n; i++) {
//            panel.zoom(1.1, i % 5, i % 3);
//        }
//        for (int i= 0; i < n; i++) {
//            panel.zoom(0.9, -i % 10, i % 15);
//        }
//    }
//
//    @Test
//    public void testTranslating() { lastTest = (new Throwable()).getStackTrace()[0].getMethodName();
//        ZoomPanel panel = new ZoomPanel();
//        for (int i= 0; i < n; i++) {
//            ZoomableLabel label = new ZoomableLabel("T" + i);
//            panel.add(label);
//        }
//        for (int i= 0; i < n; i++) {
//            panel.translate(i % 5, -i % 18);
//        }
//        for (int i= 0; i < n; i++) {
//            panel.translate(-i % 9, i % 13);
//        }
//    }
//
//    @Test
//    public void testZoomingAndTranslating() { lastTest = (new Throwable()).getStackTrace()[0].getMethodName();
//        ZoomPanel panel = new ZoomPanel();
//        for (int i= 0; i < n; i++) {
//            ZoomableLabel label = new ZoomableLabel("T" + i);
//            panel.add(label);
//        }
//        for (int i= 0; i < n; i++) {
//            panel.translate(i % 5, -i % 18);
//            panel.zoom(0.9, i % 18, -i % 13);
//        }
//        for (int i= 0; i < n; i++) {
//            panel.zoom(0.9, -i % 11, -i % 14);
//            panel.translate(-i % 9, i % 13);
//        }
//    }
//
//    @Test
//    public void testAddingAndZooming() { lastTest = (new Throwable()).getStackTrace()[0].getMethodName();
//        ZoomPanel panel = new ZoomPanel();
//        for (int i= 0; i < n; i++) {
//            ZoomableLabel label = new ZoomableLabel("T" + i);
//            panel.add(label);
//            panel.zoom(0.9, i % 15, -i % 11);
//        }
//    }
//
//    @Test
//    public void testAddingAndTranslating() { lastTest = (new Throwable()).getStackTrace()[0].getMethodName();
//        ZoomPanel panel = new ZoomPanel();
//        for (int i= 0; i < n; i++) {
//            ZoomableLabel label = new ZoomableLabel("T" + i);
//            panel.add(label);
//            panel.translate(i % 5, i % 13);
//        }
//    }
//
//    @Test
//    public void testUpdateInformation() { lastTest = (new Throwable()).getStackTrace()[0].getMethodName();
//        ZoomPanel panel = new ZoomPanel();
//        for (int i= 0; i < n; i++) {
//            ZoomableLabel label = new ZoomableLabel("T" + i);
//            panel.add(label);
//        }
//        for (int i= 0; i < n; i++) {
//            panel.validate();
//        }
//        Graphics g = panel.getGraphics();
//        if (g != null) {
//            for (int i= 0; i < n; i++) {
//                panel.paintComponent(g);
//            }
//        }
//    }

    @Test
    public void noExecutionTests() {}
}