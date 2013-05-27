package lt.banelis.aurelijus.dinosy.prototype.relations;

/**
 * Simple data structure to store box coordinates.
 *
 * @author Aurelijus Banelis
 */
public class Boundary {

    private int x1;
    private int y1;
    private int x2;
    private int y2;

    public Boundary(int x, int y, int width, int height) {
        this.x1 = x;
        this.y1 = y;
        this.x2 = x + width;
        this.y2 = y + height;
    }

    public void putIntoBoundary(int x, int y, int width, int height) {
        x1 = Math.min(x1, x);
        y1 = Math.min(y1, y);
        x2 = Math.max(x2, x + width);
        y2 = Math.max(y2, y + height);
    }

    public int getX() {
        return x1;
    }

    public int getY() {
        return y1;
    }

    public int getWidth() {
        return x2 - x1;
    }

    public int getHeight() {
        return y2 - y1;
    }
}
