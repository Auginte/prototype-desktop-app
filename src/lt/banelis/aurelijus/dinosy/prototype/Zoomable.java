package lt.banelis.aurelijus.dinosy.prototype;

/**
 * Interface for zoomabel object to handle zoom opeations.
 *
 * @author Aurelijus Banelis
 */
public interface Zoomable {
    /**
     * Executed when parent resized this component
     */
    public void zoomed(double z);
}
