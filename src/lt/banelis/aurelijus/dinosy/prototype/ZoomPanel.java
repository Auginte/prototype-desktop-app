package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;

/**
 * Panel with zooming capabilities.
 *
 * @author Aurelijus Banelis
 */
public class ZoomPanel extends JPanel implements Serializable {
    private double z = 1;
    private DoublePoint cameraLocation = new DoublePoint(0, 0);
    private volatile Map<Component, ZoomableComponent> components = new HashMap<Component, ZoomableComponent>();
    private boolean movability = true;
    private int translateEdges = 20;
    private int translateEdgesDelay = 50;
    private EdgeAdapter edgeAdapter = new EdgeAdapter(this, translateEdgesDelay);
    private boolean componentsInicialised = false;
    private List<Connection> connections = new LinkedList<Connection>();
    private boolean grid = true;
    private Color gridColor = new Color(32, 32, 32);
    //TODO: beter integration of popups and focusing
    Component lastFocusOwner = null;
    private volatile boolean loading = false;
    private static int loadingRing = 1;
    private Thread loadingThread = null;
    private static Color loadingColor = new Color(40, 40, 40);
    
    private static Color addColor(Color color, int delta) {
        int c[] = new int[3];
        c[0] = color.getRed() + delta;
        c[1] = color.getGreen() + delta;
        c[2] = color.getBlue() + delta;
        for (int i= 0; i < 3; i++) {
            if (c[i] > 255) {
                c[i] = 255;
            } else if (c[i] < 0) {
                c[i] = 0;
            }
        }
        return new Color(c[0], c[1], c[2]);
    }

    public interface ContentChangeListener {
        public void added(Component component);
        public void addedAll();
        public void removed(Component component);
        public void removedAll();
    };
    public abstract static class ContentChangeAdapter implements ContentChangeListener {
        public void added(Component component) { }
        public void addedAll() { }
        public void removed(Component component) { }
        public void removedAll() { }
    }

    private LinkedList<ContentChangeListener> changeListerners = new LinkedList<ContentChangeListener>();

    public ZoomPanel() {
        //FIXME: remove in production environment
        setForeground(Color.green);
        addMovability();
    }


    /*
     * Initianing Zoomable components
     */
   
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!componentsInicialised) {
            initialiseComponents();
        }
        setLayout(null);
    }
    
    @Override
    protected void paintChildren(Graphics g) {
        if (grid) {
            printGrid(g);
        }
        if (loading) {
            paintLoading(g, getSize().width, getSize().height);
        }
        paintConnections(g);
//        debugPaintProcessor(g);
        debugComponents(g);
        super.paintChildren(g);
    }

    //FIXME: debuging
    private static Runtime runtime = Runtime.getRuntime();
    private void debugPaintProcessor(Graphics g) {
        g.setColor(Color.RED);
        float percent = runtime.freeMemory() / (float) runtime.totalMemory();
        g.fillRect(0, 0, (int) (getWidth() * percent), getHeight() / 8);
        g.setColor(Color.YELLOW);
        int n = 0;
        for (Component component : getComponents()) {
            if (component instanceof ZoomableImage) {
                n++;
            }
        }
        if (n > 0) {
            float h = (getHeight() - getHeight() / 8) / (n+1);
            int i = 0;
            int avg = ImageLoader.getAveragePriority();
            for (Component component : getComponents()) {
                if (component instanceof ZoomableImage) {
                    ZoomableImage zi = (ZoomableImage) component;
                    if (zi.getPriority() < avg / 3) {
                        g.setColor(Color.ORANGE);                        
                    } else {
                        g.setColor(Color.YELLOW);
                    }
                    g.drawString(i + " " + zi.getPriority(), 10, (int) (getHeight() / 8 + i*h));
                    i++;
                }
            }
            g.drawString("AVG:" + ImageLoader.getAveragePriority(), 0, (int) (getHeight() / 8 + i*h));
        }
    }


    @Override
    public void validate() {
        super.validate();
        initialiseComponents();
    }


    /**
     * Delegates zoomable component for each Swing component.
     * Memorize original position and size of components.
     */
    private synchronized void initialiseComponents() {
        if (!componentsInicialised) {
            for (Component component : getComponents()) {
                ZoomableComponent zoomableComponent = components.get(component);
                if (zoomableComponent == null) {
                    addZoomable(component);
                } else if (zoomableComponent.getSize().getWidth() == 0) {
                    zoomableComponent.recalculateOriginal(1);
                    }
                }
            componentsInicialised = true;
            for (ContentChangeListener contentChangeListener : changeListerners) {
                contentChangeListener.addedAll();
            }
        }
    }

    @Override
    public Component add(Component comp) {
        addZoomable(comp);
        return super.add(comp);
    }

    @Override
    public Component add(String name, Component comp) {
        addZoomable(comp);
        return super.add(name, comp);
    }

    @Override
    public void add(Component comp, Object constraints) {
        addZoomable(comp);
        super.add(comp, constraints);
    }

    @Override
    public Component add(Component comp, int index) {
        addZoomable(comp);
        return super.add(comp, index);
    }

    @Override
    public void add(Component comp, Object constraints, int index) {
        addZoomable(comp);
        super.add(comp, constraints, index);
    }

    private ZoomableComponent addZoomable(Component comp, double z) {
        ZoomableComponent component = new ZoomableComponent(comp, z);
        addMovability(component);
        addEdgeAdapter(component.getComponent());
        components.put(comp, component);
        for (ContentChangeListener contentChangeListener : changeListerners) {
            contentChangeListener.added(comp);
        }
        return component;
    }
    
    private ZoomableComponent addZoomable(Component comp) {
        return addZoomable(comp, 1);
    }

    @Override
    public void remove(Component comp) {
        components.remove(comp);
        super.remove(comp);
    }

    @Override
    public void remove(int index) {
        Component component = getComponent(index);
        for (ContentChangeListener contentChangeListener : changeListerners) {
            contentChangeListener.removed(component);
        }
        components.remove(component);
        super.remove(index);
    }

    @Override
    public void removeAll() {
        for (ContentChangeListener contentChangeListener : changeListerners) {
            contentChangeListener.removedAll();
        }
        components.clear();
        connections.clear();
        super.removeAll();
    }

    private void debugComponents(Graphics g) {
        Color oldColor = g.getColor();

//        g.setColor(Color.BLACK);
//        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        g.setColor(Color.GRAY);
        g.drawRect(translateEdges, translateEdges, this.getWidth() - translateEdges*2, this.getHeight() - translateEdges*2);

        g.setColor(oldColor);
    }

    public ZoomableComponent addComponent(Component component, double z) {
        super.add(component);
        setComponentZOrder(component, 0);
        return addZoomable(component, z);
    }
    
    public ZoomableComponent addComponent(Component component) {
        return addComponent(component, 1);
    }

    public Collection<ZoomableComponent> getZoomableComponetns() {
        return Collections.unmodifiableCollection(components.values());
    }

    /*
     * Change listeners
     */

    public void addChangeListener(ContentChangeListener listener) {
        changeListerners.add(listener);
    }

    public void removeChangeListener(ContentChangeListener listener) {
        if (changeListerners.contains(listener)) {
            changeListerners.remove(listener);
        }
    }
    

    /*
     * Using camera
     */

    public synchronized void translate(double xDifference, double yDifference) {
        translate(xDifference, yDifference, false);
    }

    public synchronized void translate(double xDifference, double yDifference, boolean translateDraggable) {
        if (!loading) {
            for (ZoomableComponent zoomableComponent : components.values()) {
                if (translateDraggable || !zoomableComponent.getMoveAdapter().isBeingDragged()) {
                    zoomableComponent.translate(xDifference, yDifference);
                }
            }
            cameraLocation.transalte(-xDifference, -yDifference);
        }
    }

    public Point2D getCameraLocation() {
        return cameraLocation;
    }

    /*
     * Zooming
     */

    public double getZoom() {
        return z;
    }

    public void zoom(double zDifference) {
        zoom(zDifference, this.getWidth() / 2, this.getHeight() / 2);
    }

    public synchronized void zoom(double zDifference, int fromX, int fromY) {
        if (!loading) {
            this.z *= zDifference;
            for (ZoomableComponent zoomableComponent : components.values()) {            
                if (!zoomableComponent.getMoveAdapter().isBeingDragged()) {
                    zoomableComponent.zoom(zDifference, fromX, fromY);
                }
            }
            updateConnectionsSize();
            zoomCamera(zDifference, fromX, fromY);
            this.repaint();
        }
    }

    private void zoomCamera(double zDifference, int translateX, int translateY) {
        int centerX = this.getWidth() / 2;
        int centerY = this.getHeight() / 2;
        double cameraX = cameraLocation.getX();
        double cameraY = cameraLocation.getY();
        double x = (cameraX - translateX) * zDifference + translateX;
        double y = (cameraY - translateY) * zDifference + translateY;
        cameraLocation.setLocation(x, y);
    }

    public void reset() {
        for (ZoomableComponent zoomableComponent : components.values()) {
            zoomableComponent.reset();
        }
        z = 1;
        cameraLocation.setLocation(0, 0);
    }

    protected int getZoomableComponentsCount() {
        return components.size();
    }

    public ZoomableComponent getZoomableComponent(Component component) {
        return components.get(component);
    }

    private static final Dimension minimumSize = new Dimension(25, 25);
    @Override
    public Dimension getPreferredSize() {
        if (super.getPreferredSize().width < minimumSize.getWidth() || super.getPreferredSize().height < minimumSize.getHeight()) {
            super.setPreferredSize(minimumSize);
        }
        return super.getPreferredSize();
    }
    
    /*
     * Moving
     */

    private void addMovability(ZoomableComponent component) {
        if (component.getMoveAdapter() == null) {
            component.setMoveAdapter(new MoveAdapter(this, component));
        }
    }

    private void addMovability() {
        MoveAdapter planeAdapter = null;
        for (MouseListener mouseListener : getMouseListeners()) {
            if (mouseListener instanceof MoveAdapter) {
                planeAdapter = (MoveAdapter) mouseListener;
                break;
            }
        }
        if (planeAdapter == null) {
            planeAdapter = new MoveAdapter(this);
            addMouseListener(planeAdapter);
            addMouseMotionListener(planeAdapter);
        }
        enableTranslateEdges();
    }

    public boolean isMovable() {
        return movability;
    }

    public void setMovable(boolean movable) {
        this.movability = movable;
        for (ZoomableComponent zoomableComponent : components.values()) {
            zoomableComponent.getMoveAdapter().setEnabled(movable);
        }
    }


    /*
     * Edges
     */

    private void enableTranslateEdges() {
        if (translateEdges > 0) {
            addEdgeAdapter(this);
            for (Component component : getComponents()) {
                addEdgeAdapter(component);
            }
        }
    }

    /**
     * Adds adapter if not already exists
     * @todo using sub components
     */
    private void addEdgeAdapter(Component component) {
        for (MouseMotionListener listener : component.getMouseMotionListeners()) {
            if (listener == edgeAdapter) {
                return;
            }
        }
        component.addMouseListener(edgeAdapter);
        component.addMouseMotionListener(edgeAdapter);
    }

    public void setTranslateEdges(int translateEdges) {
        this.translateEdges = translateEdges;
    }

    public void setTranslateEdgesDelay(int translateEdgesDelay) {
        if (translateEdgesDelay > 0) {
            this.translateEdgesDelay = translateEdgesDelay;
        }
    }


    public int getTranslateEdge() {
        return translateEdges;
    }

    public int getTranslateEdgesDelay() {
        return translateEdgesDelay;
    }


    /*
     * Connections
     */

    public void addConnnection(Connection connection) {
        connections.add(connection);
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void removeConnections(Class<? extends Arrow> headType) {
        LinkedList<Connection> toDelete = new LinkedList<Connection>();
        for (Connection connection : connections) {
            if (headType.isInstance(connection.getArrowTo())) {
                toDelete.add(connection);
            }
        }
        removeConnections(toDelete);
    }
    
    public void removeConnections(List<? extends Connection> toDelete) {
        connections.removeAll(toDelete);
    }
    
    //TODO: why concurent modification?
    private synchronized void paintConnections(Graphics g) {
        for (Connection connection : connections) {
            connection.paint(g);
        }
    }

    private void updateConnectionsSize() {
        for (Connection connection : connections) {
            connection.setArrowSize(z * Arrow.defaultSize);
        }
    }


    /*
     * Grid
     */

    public boolean isGrid() {
        return grid;
    }

    public void setGrid(boolean grid) {
        this.grid = grid;
    }

    private void printGrid(Graphics g) {
        final int maxGridSize = 100;
        final int gridStep = maxGridSize / 10;
        int gridSize;
        double zoom = z;
        double lastZoom = z;
        while (zoom < maxGridSize) {
            zoom *= maxGridSize;
        }
        int maxZoom = Math.max(this.getHeight(), this.getWidth());
        while (zoom > maxZoom) {
            zoom /= gridStep;
        }
        gridSize = (int) (zoom / gridStep);

        Color oldColor = g.getColor();

        int initialX = (int) Math.floor(cameraLocation.getX()) % gridSize;
        int initialY = (int) Math.floor(cameraLocation.getY()) % gridSize;
        if (gridSize > 2) {
            g.setColor(gridColor);
            for (int x = -initialX; x  < this.getWidth(); x += gridSize) {
                g.drawLine(x, 0, x, this.getHeight());
            }
            for (int y = -initialY; y  < this.getHeight(); y += gridSize) {
                g.drawLine(0, y, this.getWidth(), y);
            }
        }
        g.setColor(oldColor);
    }


    /*
     * Focusable, selectable elements
     */

    public ZoomableComponent getFocusOwner() {
        for (Component component : components.keySet()) {
            if (component.isFocusOwner()) {
                return components.get(component);
            }
        }
        return components.get(lastFocusOwner);
    }

    public List<ZoomableComponent> getSelected() {
        List<ZoomableComponent> selected = new LinkedList<ZoomableComponent>();
        for (ZoomableComponent zoomableComponent : this.getZoomableComponetns()) {
            if (zoomableComponent.getComponent() instanceof Selectable && ((Selectable) zoomableComponent.getComponent()).isSelectable()) {
                if (((Selectable) zoomableComponent.getComponent()).isSelected()) {
                    selected.add(zoomableComponent);
                }
            }
        }
        if (selected.size() < 1) {
            ZoomableComponent component = this.getFocusOwner();
            if (component != null)
            selected.add(component);
        }
        return selected;
    }
    
    
    /*
     * Concurent modifications and threads
     */

    public void setLoading(boolean loading) {
        if (loading && (loadingThread == null || !loadingThread.isAlive())) {
            loadingThread = new Thread() {
                @Override
                public synchronized void run() {
                    while (ZoomPanel.this.loading) {
                        loadingRing++;
                        if (loadingRing > 36) {
                            loadingRing = 0;
                        }
                        ZoomPanel.this.repaint();
                        try {
                            wait(200);
                        } catch (InterruptedException ex) {}
                    }
                }
            };
            loadingThread.setPriority(Thread.MIN_PRIORITY);
            loadingThread.start();
        } else if (loading == false) {
            repaint();
        }
        this.loading = loading;
    }
    
    
    public static void paintLoading(Graphics g, int width, int height) {
        g.setColor(loadingColor);
        g.fillRect(0, 0, width, height);
        int x = width/2;
        int y = height/2;
        int size = Math.min(width, height) / 4;
        g.setColor(Color.GRAY);
        g.fillArc(x - size/2, y - size/2, size, size, loadingRing*10, 60);
        g.fillArc(x - size/2, y - size/2, size, size, loadingRing*10 + 180, 60);
        g.setColor(loadingColor);
        g.fillOval(x - size/4, y - size/4, size/2, size/2);
    }
}
