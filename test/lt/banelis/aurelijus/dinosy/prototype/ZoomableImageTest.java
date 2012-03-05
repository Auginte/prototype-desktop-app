/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.banelis.aurelijus.dinosy.prototype;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Aurelijus Banelis
 */
public class ZoomableImageTest {
    
    public ZoomableImageTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testStack() {
        ZoomableImage.Stack<String> stack = new ZoomableImage.Stack<String>("Pirmas");
        stack.add("antras");
        stack.add("trecias");
        String[] result = new String[3];
        int i = 0;
        for (String string : stack) {
            result[i] = string;
            i++;
        }
        assertArrayEquals(new String[] {"Pirmas", "antras", "trecias"}, result);
    }
    
    @Test
    public void testConcurent() {
        ZoomableImage.Stack<String> stack = new ZoomableImage.Stack<String>("Pirmas");
        stack.add("antras");
        stack.add("trecias");
        int i = 1;
        for (String string : stack) {
            if (i == 1 || i == 4) {
                stack.add("Dar" + i);
            }
            i++;
        }
        assertEquals(5, stack.size());
    }
    
    @Test
    public void testClear() {
        ZoomableImage.Stack<String> stack = new ZoomableImage.Stack<String>("Pirmas");
        stack.add("antras");
        stack.add("trecias");
        int i = 0;
        for (String string : stack) {
            if (i == 1) {
                stack.clear();
            }
            i++;
        }
        assertEquals(3, i);
        
        i = 0;
        for (String string : stack) {
            i++;
        }
        assertEquals(0, i);
        
        stack.add("1-ma");
        stack.add("2-as");
        assertEquals(2, stack.size());
    }
}
