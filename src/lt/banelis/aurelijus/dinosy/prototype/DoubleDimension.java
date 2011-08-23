package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.io.Serializable;

/**
 * Containter for 2D size
 *
 * @author Aurelijus Banelis
 */
class DoubleDimension extends Dimension2D implements Serializable {
    private double width;
    private double height;

    public DoubleDimension(double width, double height) {
        setSize(width, height);
    }

    public DoubleDimension(Dimension dimention) {
        setSize(dimention);
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public final void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }
}