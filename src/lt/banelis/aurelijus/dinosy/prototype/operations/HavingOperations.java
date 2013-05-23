package lt.banelis.aurelijus.dinosy.prototype.operations;

import java.util.List;
import lt.banelis.aurelijus.dinosy.prototype.ZoomPanel;

/**
 * Representation that has its own operations
 *
 * @author Aurelijus Banelis
 */
public interface HavingOperations {

    public List<Operation> getOperations(final ZoomPanel panel);
}
