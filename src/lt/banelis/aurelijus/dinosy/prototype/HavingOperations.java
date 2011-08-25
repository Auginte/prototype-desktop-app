package lt.banelis.aurelijus.dinosy.prototype;

import java.util.List;

/**
 * Representation that has its own operations
 * 
 * @author Aurelijus Banelis
 */
public interface HavingOperations {
    public List<BasicVisualization.Operation> getOperations(final ZoomPanel panel);
}
