package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Container for 2D location
 *
 * @author Aurelijus Banelis
 */
public class DoublePoint extends Point2D implements Serializable {

    private double x;
    private double y;

    public DoublePoint(double x, double y) {
        setLocation(x, y);
    }

    public DoublePoint(Point point) {
        setLocation(point);
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public final void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void transalte(double xDifferent, double yDifferent) {
        setLocation(x + xDifferent, y + yDifferent);
    }
}
