package lt.banelis.aurelijus.dinosy.prototype.operations;

/**
 * Simple class to track long operations.
 *
 * @author Aurelijus Banelis
 */
public interface Progress {

    public void update(double percent, String operaion);
}
