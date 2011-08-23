package lt.banelis.aurelijus.dinosy.prototype;

import java.util.Date;
import java.util.List;
import java.util.LinkedList;
import lt.banelis.parser.NullNotAllowedException;
import lt.banelis.parser.OnlyRefferenceException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import lt.banelis.parser.Class;
import static org.junit.Assert.*;
import lt.banelis.aurelijus.dinosy.prototype.arranging.Generalization;
import lt.dinosy.datalib.Source;
/**
 *
 * @author Aurelijus Banelis
 */
public class PhpUmlTest {

    public PhpUmlTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testArrangeSimple() {
        /*
         * AAA  B C   D
         * E    F GG  HHHH
         * E      GG
         * JJ   K LLL M
         *        LLL
         *        LLL
         */
        ClassRepresentation AAA = newClass("AAA", 300, 100);
        ClassRepresentation B = newClass("B", 100, 100);
        ClassRepresentation C = newClass("C", 30, 40);
        ClassRepresentation D = newClass("D", 59, 83);
        ClassRepresentation E = newClass("E", 90, 200);
        ClassRepresentation F = newClass("K", 10, 10);
        ClassRepresentation G = newClass("GG", 200, 200);
        ClassRepresentation HHHH = newClass("HHHH", 400, 50);
        ClassRepresentation JJ = newClass("JJ", 200, 87);
        ClassRepresentation K = newClass("K", 12, 18);
        ClassRepresentation LLL = newClass("LLL", 300, 300);

        ZoomPanel panel = new ZoomPanel();
        PhpUml phpUml = addToPanel(panel, AAA, B, C, D, E, F, G, HHHH, JJ, K, LLL);
        phpUml.simpleGrid.arrange();

        assertNotOverLap(AAA, B, C, D, E, F, G, HHHH, JJ, K, LLL);

        assertTrue(phpUml.simpleGrid.getHeight() >= 100+200+300);
    }

    private ClassRepresentation newClass(String name, int width, int height) {
        Source s = new Source.Project(new Date(), "Test project");
        try {
            ClassRepresentation result = new ClassRepresentation(s, new Class(name));
            result.setSize(width, height);
            return result;
        } catch (NullNotAllowedException ex) {
            fail("NullNotAllowedException: " + ex);
            return null;
        }
    }

    public PhpUml addToPanel(ZoomPanel panel, ClassRepresentation ... classes) {
        PhpUml result;
        for (ClassRepresentation class1 : classes) {
            panel.add(class1);
        }
        return new PhpUml(panel);
    }

    public void assertNotOverLap(ClassRepresentation ... classes) {
        for (ClassRepresentation class1 : classes) {
            for (ClassRepresentation class2 : classes) {
                if (class1 != class2 &&
                    in(class1.getLocation().x, class2.getLocation().x, class2.getWidth()) &&
                    in(class1.getLocation().y, class2.getLocation().y, class2.getHeight())) {
                    fail("Overlaps " + class1 + " " + class2 + "\t" + class1.getLocation() + " IN " + class2.getLocation() + " " + class2.getSize());
                }
            }
        }
    }

    private boolean in(double who, double start, double width) {
        return who >= start && who <= start + width;
    }

    @Test
    public void testArrangeGeneralization() {
        /*
         * A B C D
         *
         *     E
         *  F     G
         * H J   K L
         *
         *     M
         *   M1
         * M11 M12
         *     M121
         */
        ZoomPanel panel = new ZoomPanel();
        Class A = newClass("A");
        Class B = newClass("B");
        Class C = newClass("C");
        Class D = newClass("D");

        Class E = newClass("E");
        Class F = newClass("F", E);
        Class G = newClass("G", E);
        Class H = newClass("H", F);
        Class J = newClass("J", F);
        Class K = newClass("K", G);
        Class L = newClass("L", G);

        Class M = newClass("M");
        Class M1 = newClass("M1", M);
        Class M11 = newClass("M11", M1);
        Class M12 = newClass("M12", M1);
        Class M121 = newClass("M121", M12);

        PhpUml phpUml = addToPanel(panel, A, B, C, D, E, F, G, H, J, K, L, M, M1, M11, M12, M121);
        Generalization g = phpUml.generalizationGrid;
        g.arrange();

        assertTrue(contains(A, g.getBare()));
        assertTrue(contains(B, g.getBare()));
        assertTrue(contains(C, g.getBare()));
        assertTrue(contains(D, g.getBare()));

        assertFalse(contains(E, g.getBare()));
        assertFalse(contains(M, g.getBare()));
        assertFalse(contains(M121, g.getBare()));

        assertEquals(get(E, g.getTrees()), E, F, G, H, J, K, L);
        assertEquals(get(M, g.getTrees()), M, M1, M11, M12, M121);

        List<ClassRepresentation> treeE = get(E, g.getTrees()).getAll();
        List<ClassRepresentation> treeM = get(M, g.getTrees()).getAll();

        assertHorizontal(F, G, treeE);
        assertHorizontal(H, J, treeE);
        assertHorizontal(K, L, treeE);
        assertHorizontal(H, L, treeE);

        assertVertical(E, F, treeE);
        assertVertical(F, H, treeE);
        assertVertical(E, G, treeE);
        assertVertical(G, K, treeE);

        assertHorizontal(M11, M121, treeM);

        assertVertical(M, M121, treeM);
        assertVertical(M, M12, treeM);
        assertVertical(M1, M11, treeM);

    }

    @Test
    public void testArrangeGeneralization2() {
        /*
         * D Sasaja
         *
         * A
         * B
         *
         * Vienas
         * v2  v1
         * ve
         */
        Class Vienas = newClass("Vienas");
        Class v2 = newClass("v2", Vienas);
        Class v1 = newClass("v1", Vienas);
        Class veeeee2 = newClass("Ve", v2);

        Class A = newClass("A");
        Class B = newClass("B", A);

        Class D = newClass("D");
        Class Sasaja = newClass("Sasaja");

        PhpUml phpUml = addToPanel(new ZoomPanel(), veeeee2, v2, v1, Vienas, D, A, Sasaja, B);
        phpUml.generalizationGrid.arrange();

        List<ClassRepresentation> treeA = get(A, phpUml.generalizationGrid.getTrees()).getAll();
        List<ClassRepresentation> treeV = get(Vienas, phpUml.generalizationGrid.getTrees()).getAll();

        assertEquals(treeA, A, B);
        assertEquals(treeV, Vienas, v2, v1, veeeee2);
        assertVertical(A, B, treeA);

        assertLevel(v2, v1, treeV);
        assertVertical(Vienas, v2, treeV);
        assertVertical(Vienas, v1, treeV);
        assertVertical(v2, veeeee2, treeV);

    }

    @Test
    public void testArrangeGeneralization3() {
        /*
         * D Sasaja
         *
         * A
         * B
         *
         * Vienas
         * v2  v1
         * ve
         */
        Class Vienas = newClass("Vienas");
        Class v2 = newClass("v2", Vienas);
        Class v1 = newClass("v1", Vienas);
        Class veeeee2 = newClass("Ve", v2);

        Class A = newClass("A");
        Class B = newClass("B", A);

        Class D = newClass("D");
        Class Sasaja = newClass("Sasaja");


        PhpUml phpUml = addToPanel(new ZoomPanel(), veeeee2, Vienas, v1, v2, B, A, D, Sasaja);
        phpUml.generalizationGrid.arrange();

        List<ClassRepresentation> treeA = get(A, phpUml.generalizationGrid.getTrees()).getAll();
        List<ClassRepresentation> treeV = get(Vienas, phpUml.generalizationGrid.getTrees()).getAll();

        assertEquals(treeA, A, B);
        assertEquals(treeV, Vienas, v2, v1, veeeee2);
        assertVertical(A, B, treeA);

        assertLevel(v2, v1, treeV);
        assertVertical(Vienas, v2, treeV);
        assertVertical(Vienas, v1, treeV);
        assertVertical(v2, veeeee2, treeV);
    }

    private void assertEquals(Tree<ClassRepresentation> container, Class ... classes) {
        assertEquals(container.getAll(), classes);
    }

    private void assertEquals(List<ClassRepresentation> container, Class ... classes) {
        LinkedList<ClassRepresentation> copy = new LinkedList<ClassRepresentation>(container);
        for (Class object : classes) {
            ClassRepresentation representation = getFromList(object, container);
            if (representation == null) {
                fail(object + " not in tree " + container);
            } else {
                copy.remove(representation);
            }
        }
        if (copy.size() > 0) {
            fail("Tree has more elements: " + container);
        }
    }




    private void assertHorizontal(Class left, Class right, Tree<ClassRepresentation> container) {
        assertHorizontal(left, right, container.getAll());
    }

    private void assertLevel(Class one, Class two, List<ClassRepresentation> container) {
        ClassRepresentation l = getFromList(one, container);
        ClassRepresentation r = getFromList(two, container);
        assertTrue("Level: " + l.getLocation() + " != " + r.getLocation(), l.getLocation().y == r.getLocation().y);
    }

    private void assertHorizontal(Class left, Class right, List<ClassRepresentation> container) {
        ClassRepresentation l = getFromList(left, container);
        ClassRepresentation r = getFromList(right, container);
        assertTrue("Horizontal: " + l.getLocation() + " > " + r.getLocation(), l.getLocation().x < r.getLocation().x);
    }

    private void assertVertical(Class top, Class down, Tree<ClassRepresentation> container) {
        assertVertical(top, down, container.getAll());
    }
    private void assertVertical(Class top, Class down, List<ClassRepresentation> container) {
        ClassRepresentation t = getFromList(top, container);
        ClassRepresentation d = getFromList(down, container);
        assertTrue("Vertical: " + t.getLocation() + " > " + d.getLocation(), t.getLocation().y < d.getLocation().y);
    }


    private boolean contains(Class object, List<ClassRepresentation> container) {
        for (ClassRepresentation classRepresentation : container) {
            if (classRepresentation.getClassName().equals(object.getName())) {
                return true;
            }
        }
        return false;
    }

    private Tree<ClassRepresentation> get(Class object, List<Tree<ClassRepresentation>> container) {
        for (Tree<ClassRepresentation> tree : container) {
            if (tree.getHead().getClassName().equals(object.getName())) {
                return tree;
            }
        }
        fail(object + " not found in " + container);
        return null;
    }

    private ClassRepresentation getFromList(Class object, List<ClassRepresentation> container) {
        for (ClassRepresentation classRepresentation : container) {
            if (classRepresentation.getClassName().equals(object.getName())) {
                return classRepresentation;
            }
        }
        return null;
    }

    private Class newClass(String name) {
        return newClass(name, null);
    }

    private Class newClass(String name, Class extend) {
        Class classObject = null;
        try {
             classObject = new Class(name);
             classObject.setExtend(extend);
        } catch (OnlyRefferenceException ex) {
            fail("Only refference: " + ex);
        } catch (NullNotAllowedException ex) {
            fail("Bad class name:" + ex);
        }
        return classObject;
    }

    private PhpUml addToPanel(ZoomPanel panel, Class ... classes) {
        Source s = new Source.Project(new Date(), "Test project");
        for (Class class1 : classes) {
            panel.add(new ClassRepresentation(s, class1));
        }
        return new PhpUml(panel);
    }
}