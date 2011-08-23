package lt.banelis.aurelijus.dinosy.prototype;

import javax.swing.JLabel;
import org.junit.Test;

public class ZoomableLabelTest {

    @Test
    public void testSomeMethod() {
        //8.49 8.284
        //6.26 8.17
        //182.6
        //115.85
        //195.365
        int n = 3000000;
        int m = 40;
        int sumDiff = 0;
        for (int i= 0; i < n; i++) {
            ZoomableLabel.getWidth(i, "Tekstas");
        }
    }

}