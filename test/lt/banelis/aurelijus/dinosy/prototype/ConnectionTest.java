package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.Point;
import javax.swing.JLabel;
import org.junit.Test;

public class ConnectionTest {
    @Test
    public void testGetNearPointSimple() {
        JLabel label1 = new JLabel("Test1");
        label1.setLocation(-5, -5);
        label1.setSize(10,10);

        JLabel label2 = new JLabel("Test1");
        label1.setLocation(-5, 40);
        label1.setSize(10,10);

        Point result = new Point();
//        int r = Connection.arrowSize / 2;
//        Connection.getNearPoint(label1, label2, result);
        //FIXME: assertEquals(new Point(0, 5 + r), result);
    }

}