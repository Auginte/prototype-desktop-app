package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;

/**
 * Connection between classes or other types of elements
 *
 * @author Aurelijus Banelis
 */
public class Connection {
    private Component from;
    private Component to;
    private Arrow arrowFrom;
    private Arrow arrowTo;
    private Box boxFrom;
    private Box boxTo;;
    private String name;
    private int arrowSize = Arrow.defaultSize;

    public Connection(Component from, Component to) {
        this(from, to, new Arrow.None());
    }

    public Connection(Component from, Component to, Arrow arrowTo) {
        this(from, to, new Arrow.None(), arrowTo);
    }

    public Connection(Component from, Component to, Arrow arrowFrom, Arrow arrowTo) {
        this.from = from;
        this.to = to;
        this.arrowFrom = arrowFrom;
        this.arrowTo = arrowTo;
        arrowFrom.setSize(arrowSize);
        arrowTo.setSize(arrowSize);
        boxFrom = new Box(from);
        boxTo = new Box(to);
    }


    /*
     * Components
     */

    public void setFrom(Component from) {
        this.from = from;
    }

    public void setTo(Component to) {
        this.to = to;
    }

    public Component getFrom() {
        return from;
    }

    public Component getTo() {
        return to;
    }


    /*
     * Arrows
     */

    public void setArrawFrom(Arrow arrawFrom) {
        this.arrowFrom = arrawFrom;
    }

    public void setArrowTo(Arrow arrowTo) {
        this.arrowTo = arrowTo;
    }

    public Arrow getArrawFrom() {
        return arrowFrom;
    }

    public Arrow getArrowTo() {
        return arrowTo;
    }

    public void setArrowSize(double size) {
        arrowSize = (int) size;
        arrowFrom.setSize(arrowSize);
        arrowTo.setSize(arrowSize);
    }

    public int getArrowSize() {
        return arrowSize;
    }


    /*
     * Painting
     */

    public void paint(Graphics g) {
        updateNearPoints();
        paintLine(g);
        arrowFrom.paint(g);
        arrowTo.paint(g);
    }

    private void paintLine(Graphics g) {
        g.drawLine(arrowFrom.getLocation().x, arrowFrom.getLocation().y, arrowTo.getLocation().x, arrowTo.getLocation().y);
    }

    private void updateNearPoints() {
        boxFrom.updateData();
        boxTo.updateData();

        arrowTo.setAnge(boxFrom.getAnge(boxTo));
        arrowFrom.setAnge(boxTo.getAnge(boxFrom));

        if (boxFrom.getCenter().distance(boxTo.getCenter()) < 4) {
            arrowFrom.setLocation(boxFrom.getCenter());
            arrowTo.setLocation(boxTo.getCenter());
        } else {
            boxFrom.updatePosition(arrowFrom);
            boxTo.updatePosition(arrowTo);
        }
    }

    /*
     * Data structure for addtional geometrical component attributes
     */
    private class Box {
        private Component component;
        private int halfWidth;
        private int halfHeight;
        private double radius;
        private double ange;
        private Point center = new Point();

        public Box(Component component) {
            this.component = component;
        }

        public final void updateData() {
            halfWidth = component.getSize().width / 2;
            halfHeight = component.getSize().height / 2;
            radius = Math.sqrt( halfWidth*halfWidth + halfHeight*halfHeight );
            ange = Math.atan2(component.getHeight(), component.getWidth());
            int centerX = component.getLocation().x + halfWidth;
            int cenetrY = component.getLocation().y + halfHeight;
            center.setLocation(centerX, cenetrY);
        }

        public double getRadius() {
            return radius;
        }
        
        public double getAnge() {
            return ange;
        }

        public double getAnge(Box to) {
            int distanceX = to.getCenter().x - center.x;
            int distanceY = to.getCenter().y - center.y;
            return Math.atan2(distanceY, distanceX);
        }

        public double getDegrees() {
            return ange * 180 / Math.PI;
        }

        public Point getCenter() {
            return center;
        }

        public void updatePosition(Arrow arrow) {
            /* H,W,radius - big triangle, h,w,c - small triangle (including halfArrow) */
            float halfArrow = arrowSize / 2;
            if (halfArrow > 0) {
                double arrowAnge = arrow.getAnge() - Math.PI;
                double H = Math.sin(arrowAnge) * (radius + halfArrow);
                double W = Math.cos(arrowAnge) * (radius + halfArrow);
                double h = 0;
                double w = 0;

                /* Quarters */
                double downRight = ange;
                double downLeft = Math.PI - ange;
                if (arrowAnge > downRight && arrowAnge < downLeft) {
                    /* Down */
                    h = halfHeight + halfArrow;
                    w = h * W / H;
                } else if (Math.abs(arrowAnge) < downRight) {
                    /* Right */
                    w = halfWidth + halfArrow;
                    h = H * w / W;
                } else if (Math.abs(arrowAnge) > downLeft) {
                    /* Left */
                    w = -(halfWidth + halfArrow);
                    h = H * w / W;
                } else {
                    /* Up */
                    h = -(halfHeight + halfArrow);
                    w = h * W / H;
                }
                if (Math.abs(w) > Math.abs(W) || Math.abs(h) > Math.abs(H)) {
                    /* Corners correction */
                    w = W;
                    h = H;
                }
                arrow.setLocation(center.x + w, center.y + h);
            } else {
                arrow.setLocation(center.x, center.y);
            }
        }
    }

    /*
     * Other attributes
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}