package lt.banelis.aurelijus.dinosy.prototype.relations;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Functionality for connecting elements with zoomable panel connections
 * 
 * @see     Connection
 * @author  Aurelijus Banelis
 */
public interface Connectable {
    public ConnectionState getConnectionState();
    public void setConnectinState(ConnectionState state);
    public void paintConnectionState(Graphics g);
    
    public final Color connectionStart = new Color(32, 128, 32);
    public final Color connectionCandidate = new Color(128, 255, 128);
    
    public enum ConnectionState {
        none,
        connected,
        connectionStart,
        connectionCandidate
    }
}
