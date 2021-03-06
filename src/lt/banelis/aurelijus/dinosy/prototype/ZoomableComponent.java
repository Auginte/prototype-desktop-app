package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * Component, that adds zoom attribute to usual AWT Component.
 * 
 * @author Aurelijus Banelis
 */
public class ZoomableComponent implements Serializable {
    private transient Component component;
    private Point originalLocation;
    private Dimension originalSize;
    private DoublePoint location;
    private DoubleDimension size;
    private Dimension derivedSize;
    private double z;
    private MoveAdapter moveAdapter = null;

   /** Initial sizes for images.
    * 
    * @todo size calculation convention
    *  1) Size and position specified using constructor.
    *      Used when loading from saved files.
    *  2) Using default size/position
    *      a) Updating size inside bounding box
    *          When picture is updated externally
    *      b) Update size using zoom functions
    */
    public static Dimension deafaultBounding = new Dimension(300, 300);
   

    public ZoomableComponent(Component component, double z) {
        this.component = component;
        this.z = z;
        recalculateOriginal(z);
    }

    public ZoomableComponent(Component component) {
        this(component, 1);
    }

    public final void recalculateOriginal(double z) {
        this.originalLocation = component.getLocation();
        this.originalSize = new Dimension((int) (component.getSize().getWidth() / z), (int) (component.getSize().getHeight() / z));
        this.location = new DoublePoint(originalLocation);
        this.size = new DoubleDimension(originalSize);
        this.derivedSize = component.getSize();
    }
    
    /*
     * Setters
     */

    public void setLocation(Point2D location) {
        setLocation(location.getX(), location.getY());

    }

    public void setLocation(double x, double y) {
        location.setLocation(x, y);
        getComponent().setLocation((int) x, (int) y);
    }

    public void translate(double xDifference, double yDifference) {
        location.transalte(xDifference, yDifference);
        getComponent().setLocation((int) location.getX(), (int) location.getY());
    }

    public void setSize(Dimension2D size) {
        setSize(size.getWidth(), size.getHeight());
    }

    public void setSize(double width, double height) {
        size.setSize(width, height);
        getComponent().setSize((int) width, (int) height);
        this.derivedSize.setSize((int) width, (int) height);
    }

    public void zoom(double z) {
        zoom(z, 0, 0);
    }

    public void zoom(double zDifferecnce, double translateX, double translateY) {
        double x = (location.getX() - translateX) * zDifferecnce + translateX;
        double y = (location.getY() - translateY) * zDifferecnce + translateY;
        z *= zDifferecnce;
        setLocation(x, y);
        setSize(getOriginalSize().width * z, getOriginalSize().height * z);
        if (getComponent() instanceof Zoomable) {
            ((Zoomable) getComponent()).zoomed(z);
        }
    }
    
    /**
     * Resets location and size to original
     */
    public void reset() {
        setLocation(getOriginalLocation());
        setSize(getOriginalSize());
        z = 1;
    }

    public double reinisiateOriginalSize(int width, int height) {
        originalSize.setSize(width, height);
        if (getSize().width / width < getSize().height / height) {
            z = getSize().width / (double) width;
        } else {
            z = getSize().height / (double) height;
        }
        setSize(width * z, height * z);
        return z;
    }
    
    public void reinisiateOriginalSize2(double z) {
        this.z = z;
        setSize(getComponent().getWidth(), getComponent().getHeight());
        originalSize.setSize(getComponent().getWidth() / z, getComponent().getHeight() / z);
    }

    /*
     * Getters
     */

    public Component getComponent() {
        return component;
    }

    public Point2D getPreciseLocation() {
        return location;
    }

    public Point getOriginalLocation() {
        return originalLocation;
    }

    public Point2D getLocation() {
        return getPreciseLocation();
    }

    public Dimension getOriginalSize() {
        //TODO: better size calculation
        if (originalSize.width == 0 && originalSize.height == 0) {
            originalSize.setSize(component.getSize());
        }
        return originalSize;
    }

    public Dimension2D getPreciseSize() {
        return size;
    }

    public Dimension getSize(Dimension rv) {
        rv.setSize(size.getWidth(), size.getHeight());
        return rv;
    }

    public Dimension getSize() {
        return derivedSize;
    }

    public double getZ() {
        return z;
    }


    /*
     * Movability
     */

    public void setMoveAdapter(MoveAdapter moveAdapter) {
        this.moveAdapter = moveAdapter;
        if (moveAdapter != null) {
            this.component.addMouseListener(moveAdapter);
            this.component.addMouseMotionListener(moveAdapter);
        }
    }

    public MoveAdapter getMoveAdapter() {
        return moveAdapter;
    }

    /*
     * Utilities
     */

    @Override
    public String toString() {
        return "{POS: " + location.getX() + "x" + location.getY() + "x" + z +
               " SIZE: " + size.getWidth() + "x" + size.getHeight() +
               " Original: " + originalLocation.x + "x" + originalLocation.y +
               " " + originalSize.width + "x" + originalSize.height + "}";
    }
}