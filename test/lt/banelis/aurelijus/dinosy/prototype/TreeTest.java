package lt.banelis.aurelijus.dinosy.prototype;

import java.util.LinkedList;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Aurelijus Banelis
 */
public class TreeTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testOne() {
        Tree<String> tree = new Tree<String>("Vienas");
        assertEquals("Vienas", tree.getHead());
    }

    @Test
    public void testSimpleTree() {
        /*       A
         *  A1 A2 A3 A4
         */
        Tree<String> tree = new Tree<String>("A");
        tree.addChild("A1");
        tree.addChild(new Tree<String>("A2"));
        LinkedList<Tree<String>> subTree = new LinkedList<Tree<String>>();
        subTree.add(new Tree<String>("A3"));
        subTree.add(new Tree<String>("A4"));
        tree.addChilds(subTree);

        assertEquals("A", tree.getHead());
        assertTrue(tree.contains("A1"));
        assertTrue(tree.contains("A2"));
        assertTrue(tree.contains("A3"));
        assertTrue(tree.contains("A4"));

        tree.remove("A3");
        assertFalse(tree.contains("A3"));
    }

    @Test
    public void testGetDeep() {
        /*       A
         *    A1   A2
         * A11 A12
         * A111
         * A1111
         */
        Tree<String> tree = new Tree<String>("A", "A1", "A2");
        tree.get("A1").addChilds("A11", "A12");
        tree.getDeep("A11").addChild(new Tree<String>("A111", new Tree<String>("A1111")));
        assertNotNull(tree.getDeep("A1"));
        assertNotNull(tree.getDeep("A11"));
        assertNotNull(tree.getDeep("A111"));
        assertNotNull(tree.getDeep("A1111"));
    }

    @Test
    public void testAddParent() {
        /*    A
         *    B
         *  C1 C2
         *  D
         * E F
         */
        Tree<String> tree = new Tree<String>("D", "E", "F");
        tree = tree.addParent("C1");
        tree = tree.addParent("B");
        tree.addChild("C2");
        tree = tree.addParent("A");
        assertEquals("A", tree.getHead());
        assertEquals("B", tree.get("B").getHead());
        assertTrue(tree.get("B").contains("C2"));
        assertTrue(tree.getDeep("D").contains("F"));
        assertTrue(tree.get("B").get("C1").get("D").contains("E"));
    }
}
