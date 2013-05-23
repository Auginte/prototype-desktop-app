package lt.banelis.aurelijus.dinosy.prototype.relations;

import lt.banelis.aurelijus.dinosy.prototype.relations.Connection;
import java.awt.Component;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import lt.banelis.aurelijus.dinosy.prototype.DataRepresentation;
import lt.banelis.aurelijus.dinosy.prototype.ZoomPanel;
import lt.dinosy.datalib.Data;
import lt.dinosy.datalib.Relation;

/**
 *
 * @author Aurelijus Banelis
 */
public class DataConnections {
    private ZoomPanel panel;

    public DataConnections(ZoomPanel panel) {
        this.panel = panel;
    }
    
    public void removeConnections(Component component) {
        Set<Connection> toRemove = new HashSet<Connection>();
        Data data = ((DataRepresentation) component).getData();
        for (Connection connection : panel.getConnections()) {
            if (connection.getFrom() == component) {
                toRemove.add(connection);
                removeRelations((DataRepresentation) connection.getTo(), data);
            } else if (connection.getTo() == component) {
                toRemove.add(connection);
                removeRelations((DataRepresentation) connection.getFrom(), data);
            }
        }
        panel.getConnections().removeAll(toRemove);
    }
    
    private void removeRelations(DataRepresentation container, Data linkTo) {
        List<Relation> toRemove = new LinkedList<Relation>();
        for (Relation relation : container.getData().getRelations()) {
            if (relation.getFrom() == linkTo || relation.getTo() == linkTo) {
                toRemove.add(relation);
            }
        }
        container.getData().getRelations().removeAll(toRemove);
    }
    
    private int countRepresentations(Data data) {
        int result = 0;
        for (Component component : panel.getComponents()) {
            if (component instanceof DataRepresentation) {
                if (((DataRepresentation) component).getData() == data) {
                    result++;
                }
            }
        }
        return result;
    }
}
