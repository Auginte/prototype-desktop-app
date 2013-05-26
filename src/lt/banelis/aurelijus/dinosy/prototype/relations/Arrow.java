package lt.banelis.aurelijus.dinosy.prototype.relations;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableLabel;

/**
 * Arrow at connection end
 *
 * @author Aurelijus Banelis
 */
public abstract class Arrow {

    protected Point location = new Point();
    protected double ange;
    private int size;
    public static int defaultSize = 20;
    private double distance;

    public void setAnge(double ange) {
        this.ange = normaliseAnge(ange);
    }

    public void setLocation(Point position) {
        setLocation(position.x, position.y);
    }

    public void setLocation(double x, double y) {
        this.location.setLocation(x, y);
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getAnge() {
        return ange;
    }

    public double getDegrees() {
        return ange * 180 / Math.PI;
    }

    protected int getX(double ange) {
        ange = normaliseAnge(ange);
        return location.x + (int) (Math.cos(ange) * (size / 2));
    }

    protected int getY(double ange) {
        ange = normaliseAnge(ange);
        return location.y + (int) (Math.sin(ange) * (size / 2));
    }

    public Point getLocation() {
        return location;
    }

    public double getDistance() {
        return distance;
    }

    protected Polygon getPolygon(double anges[]) {
        int[] xPoints = new int[anges.length];
        int[] yPoints = new int[anges.length];
        for (int i = 0; i < anges.length; i++) {
            xPoints[i] = getX(anges[i]);
            yPoints[i] = getY(anges[i]);
        }
        return new Polygon(xPoints, yPoints, anges.length);
    }

    public abstract void paint(Graphics g);

    protected void debug(Graphics g) {
        g.fillRect(location.x - 2, location.y - 2, 4, 4);
    }

    /*
     * Common arrows
     */
    public static class None extends Arrow {

        public void paint(Graphics g) {
            g.drawLine(getX(ange), getY(ange), getX(ange + Math.PI), getY(ange + Math.PI));
        }
    };

    public static class Generalization extends Arrow {

        public void paint(Graphics g) {
            g.drawPolygon(getPolygon(new double[]{ange, ange + Math.PI / 2, ange - Math.PI / 2}));
            g.drawLine(location.x, location.y, getX(ange + Math.PI), getY(ange + Math.PI));
        }
    };

    public static class Association extends Arrow {

        private String name;

        public Association(String name) {
            this.name = name;
        }

        public void paint(Graphics g) {
            g.drawLine(getX(ange - Math.PI / 2), getY(ange - Math.PI / 2), getX(ange), getY(ange));
            g.drawLine(getX(ange + Math.PI), getY(ange + Math.PI), getX(ange), getY(ange));
            g.drawLine(getX(ange + Math.PI / 2), getY(ange + Math.PI / 2), getX(ange), getY(ange));
            if (getDistance() > 40) {
                drawTitle(name, g);
            }
        }
    }

    protected void drawTitle(String name, Graphics g) {
        if (name != null) {
            int x = location.x + size;
            int y = location.y + size;
            if (getDegrees() < 180) {
                y = location.y - size;
            }
            //TODO: align right by degrees
            g.setFont(ZoomableLabel.getFont(size, g.getFont()));
            g.drawString(name, x, y);
        }
    }

    /*
     * Helpers
     */
    private static double normaliseAnge(double ange) {
        if (ange > 2 * Math.PI) {
            return ange - 2 * Math.PI;
        } else if (ange < 0) {
            return ange + 2 * Math.PI;
        } else {
            return ange;
        }
    }
}
