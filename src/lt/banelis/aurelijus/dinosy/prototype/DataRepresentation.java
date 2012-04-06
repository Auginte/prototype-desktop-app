package lt.banelis.aurelijus.dinosy.prototype;

import java.io.Externalizable;
import java.io.Serializable;
import lt.dinosy.datalib.Data;
import lt.dinosy.datalib.Representation;

/**
 * Representing data, that could be exported, or imported
 *
 * @author Aurelijus Banelis
 */
public interface DataRepresentation extends Serializable {
    /**
     * Visualize data, adds self as representation of data
     *
     * @param data  Data to be represented
     */
    public void iniciateData(Data data);

    /**
     * @return  Data object which is represented by component
     */
    public Data getData();

    /**
     * Prepare Data object and its representation
     *
     * @param component ZoomableComponent that links to this component
     */
    public void updateData(ZoomableComponent component);

    /**
     * Not to forget to add self as assigned representation
     *
     * @param component ZoomableComponent that links to this component
     * @return  <code>true</code> - was newly inicialized
     *          <code>fale</code> - was already inicialized
     *
     * @see BasicVisualization#getSelf(lt.banelis.aurelijus.dinosy.prototype.DataRepresentation)
     * @see Representation#getAssigned() 
     */
    public boolean inicializeRepresentation(ZoomableComponent component);    
}
