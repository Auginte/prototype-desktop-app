package lt.banelis.aurelijus.dinosy.prototype;

import com.sun.org.apache.bcel.internal.generic.F2D;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Dimension2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import lt.dinosy.datalib.Controller;
import lt.dinosy.datalib.Controller.BadVersionException;
import lt.dinosy.datalib.Data;
import lt.dinosy.datalib.NotUniqueIdsException;
import lt.dinosy.datalib.Representation;
import lt.dinosy.datalib.Representation.Element;
import lt.dinosy.datalib.Source;
import org.xml.sax.SAXException;
import lt.banelis.aurelijus.dinosy.prototype.Connectable.ConnectionState;
import lt.dinosy.datalib.Okular;
import lt.dinosy.datalib.Relation;
import lt.dinosy.datalib.Relation.Association;
import lt.dinosy.datalib.Settings;

/**
 * Basic elements of visualization
 *
 * @author Aurelijus Banelis
 */
public class BasicVisualization {
    static final Key editKey = new Key(Key.Modifier.NONE, KeyEvent.VK_ENTER);
    
    private ZoomPanel panel;
    private Controller storage = new Controller();
    private String savedTo;
    private ZoomableComponent selectionBox;
    private double selectionX = Double.NaN;
    private double selectionY = Double.NaN;
    private Component connectionStart = null;
    private Component connectionEnd = null;
    private ClipboardSourceListener clipboardSourceListener = null;
    private Source lastClipboardSource = null;
    private volatile boolean checkingClipboard = false;
    private long lastChecked = 0;
    
    //FIXME: normal source implemntation
    public Source defaultSource = null;

    private BasicVisualization() {
        Collections.sort(operations, new Comparator<BasicVisualization.Operation>() {
            public int compare(Operation o1, Operation o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    public BasicVisualization(ZoomPanel panel) {
        this();
        this.panel = panel;
    }

    public void initAll() {
        initZooming();
        initSelectable();
        initMouseCloning();
        initDragAndDrop();
        initConnections();
        initClipboard();
    }

    
    /*
     * Selection
     */
    
    public void initSelectable() {
        /* Selecting separate elements */
        panel.addChangeListener(new ZoomPanel.ContentChangeAdapter() {
            @Override
            public void added(Component component) {
                addSelectionListener(component);
            }

            @Override
            public void addedAll() {
                addSelectableToAll();
            }
        });
        addSelectableToAll();

        /* Selection border */
        ZoomableGroup zoomableGroup = new ZoomableGroup() {
            @Override
            public void zoomed(double z) {
                super.zoomed(z);
                selectionX = this.getLocation().getX();
                selectionY = this.getLocation().getY();
            }
        };
        zoomableGroup.setVisible(false);
        selectionBox = panel.addComponent(zoomableGroup);
        panel.addMouseListener(selectBoxListener);
        panel.addMouseMotionListener(selectBoxListener);
    }

    private MouseInputListener selectBoxListener = new MouseInputAdapter() {
        private Border borderSelect = BorderFactory.createLineBorder(Selectable.selectionColor);
        private Border borderDeselect = BorderFactory.createLineBorder(Selectable.deselectionColor);

        @Override
        public void mousePressed(MouseEvent e) {
            if (isModifier(e, KeyEvent.CTRL_DOWN_MASK) || isModifier(e, KeyEvent.SHIFT_DOWN_MASK)) {
                if (panel.getZoomableComponent(selectionBox.getComponent()) == null) {
                    selectionBox = panel.addComponent(selectionBox.getComponent());
                }
                selectionBox.setLocation(e.getX(), e.getY());
                selectionBox.setSize(new Dimension(2, 2));
                selectionBox.getComponent().setVisible(true);
                selectionX = selectionBox.getLocation().getX();
                selectionY = selectionBox.getLocation().getY();
                if (e.isControlDown()) {
                    ((JComponent) selectionBox.getComponent()).setBorder(borderDeselect);
                } else {
                    ((JComponent) selectionBox.getComponent()).setBorder(borderSelect);
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (selectionBox.getComponent().isVisible()) {
                if (Double.isNaN(selectionX) || Double.isNaN(selectionY)) {
                    selectionX = e.getX();
                    selectionY = e.getY();
                }
                double x = selectionX;
                double y = selectionY;
                double w = selectionBox.getSize().getWidth();
                double h = selectionBox.getSize().getHeight();
                if (e.getX() >= x) {
                    w = e.getX() - x;
                } else {
                    w = x - e.getX();
                    x = e.getX();
                }
                if (e.getY() >= y) {
                    h = e.getY() - y;
                } else {
                    h = y - e.getY();
                    y = e.getY();
                }
                selectionBox.setLocation(x, y);
                selectionBox.setSize(w, h);
                for (Component component : panel.getComponents()) {
                    if (component instanceof Selectable && ((Selectable) component).isSelectable()) {
                        double x2 = x + w;
                        double y2 = y + h;
                        int cx = component.getX();                      //Component
                        int cy = component.getY();
                        int cx2 = cx + component.getWidth();
                        int cy2 = cy + component.getHeight();
                        boolean componentStart = in(cx, x, x2) && in(cy, y, y2);   //Component in selection
                        boolean componentEnd = in(cx2, x, x2) && in(cy2, y, y2);
                        boolean selectionStart = in(x, cx, cx2) && in(y, cy, cy2); //Selection in component
                        boolean selectionEnd = in(x2, cx, cx2) && in(y2, cy, cy2);
                        if (componentStart || componentEnd || selectionStart || selectionEnd) {
                            if (e.isControlDown()) {
                                ((Selectable) component).setSelected(false);
                            } else {
                                ((Selectable) component).setSelected(true);
                            }
                        }
                    }
                }
            }
        }

        private boolean in(double what, double d1, double d2) {
            return d1 < what && what < d2;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (selectionBox.getComponent().isVisible()) {
                selectionBox.getComponent().setVisible(false);
            }
        }
    };

    private void addSelectableToAll() {
        for (Component component : panel.getComponents()) {
            addSelectionListener(component);
        }
    }

    private void addSelectionListener(Component component) {
        if (!contains(Selectable.defaultSelelectionListener, component.getMouseListeners())) {
                component.addMouseListener(Selectable.defaultSelelectionListener);
            }
    }

    public void initZooming() {
        panel.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent evt) {
                double amount = 1.1;
                if (evt.getWheelRotation() < 0) {
                    amount = 0.9;
                }
                panel.zoom(amount, evt.getX(), evt.getY());
            }
        });
    }

    
    /*
     * Clonning
     */
    
    private void initMouseCloning() {
        panel.addChangeListener(new ZoomPanel.ContentChangeAdapter() {
            @Override
            public void added(Component component) {
                component.addMouseListener(getMouseClonning());
            }

            @Override
            public void addedAll() {
                for (Component component : panel.getComponents()) {
                    component.addMouseListener(getMouseClonning());
                }
            }
        });
        for (Component component : panel.getComponents()) {
            component.addMouseListener(getMouseClonning());
        }
    }
    
    private MouseListener getMouseClonning() {
        return new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int modifier = KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK;
                if (isModifier(e, modifier) && (e.getComponent() instanceof ZoomableLabel)) {
                    //FIXME: all components (not only ZoomableLabel)
                    try {
                        panel.addComponent(((ZoomableLabel) e.getComponent()).clone());
                        ZoomableComponent component = panel.getZoomableComponent(e.getComponent());
                        component.getMoveAdapter().setBeingDragged(true);
                    } catch (CloneNotSupportedException ex) {
                        Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, "MouseClonning: Zoomable component must be clonnable", ex);
                    }
                }
            }
        };
    }

    private void initDragAndDrop() {
        panel.setTransferHandler(new TransferHandler() {
            private static final String stringListFlavor = "text/uri-list; class=java.lang.String; charset=Unicode";
            @Override
            public boolean canImport(TransferSupport support) {
                return true;
//                for (DataFlavor dataFlavor : support.getDataFlavors()) {
//                    if (dataFlavor.getMimeType().equals(stringListFlavor)) {
//                        return true;
//                    }
//                }
//                return false;
            }

            @Override
            public boolean importData(TransferSupport support) {
                boolean succeeded = true;
                for (DataFlavor dataFlavor : support.getDataFlavors()) {
                    try {
                        if (dataFlavor.getMimeType().equals(stringListFlavor)) {
                            String text = (String) support.getTransferable().getTransferData(new DataFlavor(stringListFlavor));
                            for (String string : text.split("\\n+")) {
                                String file = string.substring("file://".length()).trim();
                                int x = support.getDropLocation().getDropPoint().x;
                                int y = support.getDropLocation().getDropPoint().y;
                                if (isImage(file)) {
                                    addImage(file, x, y);
                                } else {
                                    //TODO: import of other (eg. text) files
                                    addText(file, x, y, 100, 20, false);
                                }
                            }
//                        } else {
//                            if (dataFlavor.getPrimaryType().equals("text")) {
//                                System.out.println("TEXT: " + dataFlavor);
//                                BufferedReader bufferedReader = new BufferedReader(dataFlavor.getReaderForText(support.getTransferable()));
//                                String line = null;
//                                while ((line = bufferedReader.readLine()) != null) {
//                                    System.out.println("\t" + line);
//                                }
//                            } else {
//                                System.out.println(dataFlavor + " <--- " +  dataFlavor.getDefaultRepresentationClass().getSimpleName());
//                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, "Cannot import drag n drop data", ex);
                        succeeded = false;
                    }
                }
                return succeeded;
            }
            
            private boolean isImage(String file) {
                return isExtention(file, "jpg") || isExtention(file, "png") || isExtention(file, "gif") || isExtention(file, "bmp");
            }
            
            private boolean isExtention(String file, String extention) {
                return file.toLowerCase().endsWith("." + extention.toLowerCase());
            }
            
        });
    }
    
    
    /*
     * Clipboard
     */
    
    public static interface ClipboardSourceListener {
        public void checking();
        public void noNew();
        public void newOkular(Source.Okular source);
        public void newFirefox(Source.Internet source);
        public void otherData(String data);
    }

    public void setClipboardSourceListener(ClipboardSourceListener clipboardSourceListener) {
        this.clipboardSourceListener = clipboardSourceListener;
    }

    public ClipboardSourceListener getClipboardSourceListener() {
        return clipboardSourceListener;
    }

    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    
    private void initClipboard() {
        clipboard.addFlavorListener(clipboardListener);
    }
    
    public void forceClipboardCheck() {
        long now = (new Date()).getTime() / 3;
        if (!checkingClipboard && now != lastChecked) {
            clipboardListener.flavorsChanged(null);
        }
        lastChecked = now;
    }
        
    private FlavorListener clipboardListener = new FlavorListener() {
        public void flavorsChanged(FlavorEvent e) {
            /* One instance for performance */
            if (checkingClipboard) {
                return;
            } else {
                checkingClipboard = true;
            }
            if (clipboardSourceListener != null) {
                clipboardSourceListener.checking();
            }
            
            /* Gathering clipboard data */
            final Transferable contents = clipboard.getContents(null);
            int page = 0;
            String url = "";
            Source.Okular.Boundary boundary = new Source.Okular.Boundary(-1, -1, -1, -1);
            String otherData = "";
            DataFlavor okularImage = null;
            for (DataFlavor dataFlavor : contents.getTransferDataFlavors()) {
                try {
                    boolean streamRepresentation = (dataFlavor.getDefaultRepresentationClass() == InputStream.class);
                    if (otherData.length() == 0) {
                        otherData = dataFlavor.getHumanPresentableName();
                    }
                    if (dataFlavor.getMimeType().startsWith("text/x-okular-")) {
                        String data = getFromReader(dataFlavor.getReaderForText(contents));
                        if (dataFlavor.getMimeType().equals("text/x-okular-page; class=java.io.InputStream")) {
                            page = Integer.parseInt(data.trim());
                        } else if (dataFlavor.getMimeType().equals("text/x-okular-url; class=java.io.InputStream")) {
                            url = URLDecoder.decode(data, "UTF-8").trim();
                        } else if (dataFlavor.getMimeType().equals("text/x-okular-selection; class=java.nio.ByteBuffer") || dataFlavor.getMimeType().equals("text/x-okular-selection; class=java.io.InputStream")) {
                            String[] parts = data.split("x|, ", 4);
                            boundary.l = Float.parseFloat(parts[0]);
                            boundary.t = Float.parseFloat(parts[1]);
                            boundary.r = Float.parseFloat(parts[2]);
                            boundary.b = Float.parseFloat(parts[3]);
                        }
                    } else if (dataFlavor.getMimeType().startsWith("image/png") && streamRepresentation) {
                        okularImage = dataFlavor;
                    } else if (dataFlavor.getPrimaryType().equals("text") && streamRepresentation) {
                        otherData = getFromReader(dataFlavor.getReaderForText(contents));
                    }
                } catch (Exception ex) {
                    Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, "Clipboard error", ex);
                }
            }
            
            /* Exporting data to Source format */
            if (page > 0 && url.length() > 0 && okularImage != null && clipboardSourceListener != null) {
                Source.Okular lastSource = null;
                if (lastClipboardSource instanceof Source.Okular) {
                    lastSource = (Source.Okular) lastClipboardSource;
                }
                if (lastSource == null || lastSource.getPage() != page || !lastSource.getSource().equals(url) || !lastSource.getPosition().equals(boundary)) {
                    final DataFlavor finalFlavor = okularImage;
                    final String finalUrl = url;
                    final int finalPage = page;
                    final Source.Okular.Boundary finalBoundary = boundary;
//                    Thread saveClipboard = new Thread() {
//                        @Override
//                        public void run() {
                            File destination = generateClipboardFile(finalUrl);
                            FileOutputStream output = null;
                            try {
                                InputStream imageStream = (InputStream) contents.getTransferData(finalFlavor);
                                output = new FileOutputStream(destination);
                                int nextChar;
                                while ( (nextChar = imageStream.read()) != -1 ) {
                                    output.write(nextChar);
                                }
                                imageStream.close();
                                output.close();
                            } catch (Exception ex) {
                                Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, "Can not save image from clipboard to " + destination, ex);
                            }
                            Source.Okular source = new Source.Okular(new Date(), finalUrl, finalPage, finalBoundary, destination.getPath());
                            clipboardSourceListener.newOkular(source);
                            lastClipboardSource = source;
//                        }
//                    };
//                    saveClipboard.start();
                } else {
                    clipboardSourceListener.noNew();
                }
            } else {
                clipboardSourceListener.otherData(otherData);
            }
            checkingClipboard = false;
        }
    };
        
    private File generateClipboardFile(String url) {
        int slash = url.lastIndexOf(System.getProperty("file.separator"));
        if (slash < 0) {
            slash = 0;
        }
        String fileName = url.substring(slash).trim() + "." + (new Date()).getTime() + ".png";
        return new File(Settings.getDateCacheDirecotry(), fileName);
    }
    
    private static int intGroup(Matcher matcher, int group) {
        return Integer.parseInt(matcher.group(group));
    }
    
    private static String getFromReader(Reader reader) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line).append("\n");
            }
        } catch (Exception ex) {
            Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, "Can not convert reader to string", ex);
        }
        return result.toString();
    }

    public Source getLastClipboardSource() {
        return lastClipboardSource;
    }
    
    
    /*
     * Connections
     */
    
    private void initConnections() {
        panel.addChangeListener(new ZoomPanel.ContentChangeListener() {
            public void added(Component component) {
                addConnectionListener(component);
            }

            public void addedAll() {
                for (Component component : panel.getComponents()) {
                    addConnectionListener(component);
                }
            }

            public void removed(Component component) {
                panel.removeConnections(component);
                panel.repaint();
            }

            public void removedAll() { }
        });
        for (Component component : panel.getComponents()) {
            addConnectionListener(component);
        }
    }
    
    private void addConnectionListener(Component component) {
        MouseAdapter connectionsListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isModifier(e)) {
                    connectionStart = e.getComponent();
                    ((Connectable) e.getComponent()).setConnectinState(ConnectionState.connectionStart);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (isModifier(e) && connectionStart != connectionEnd && connectionStart != null && connectionEnd != null) {
                    addAssociationConnection(connectionStart, connectionEnd);
                    panel.repaint();
                }
                connectionStart = null;
                connectionEnd = null;
                for (Component component1 : panel.getComponents()) {
                    if (component1 instanceof Connectable) {
                        ((Connectable) component1).setConnectinState(ConnectionState.none);
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (isModifier(e) && connectionStart != null) {
                    Connectable connectable = (Connectable) e.getComponent();
                    if (connectable.getConnectionState() != ConnectionState.connectionStart) {
                        //TODO: do we need relation to self
                        connectable.setConnectinState(ConnectionState.connectionCandidate);
                        connectionEnd = e.getComponent();
                    }
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (isModifier(e)) {
                    Connectable connectable = (Connectable) e.getComponent();
                    if (connectable.getConnectionState() != ConnectionState.connectionStart) {
                        connectable.setConnectinState(ConnectionState.none);
                    }
                }
            }
            
            private boolean isModifier(MouseEvent e) {
                int modifier = MouseEvent.CTRL_DOWN_MASK + MouseEvent.ALT_DOWN_MASK;
                return BasicVisualization.isModifier(e, modifier);
            }
        };
        if (component instanceof Connectable) {
            component.addMouseListener(connectionsListener);
            component.addMouseMotionListener(connectionsListener);
        }
    }
    
   
    private void addAssociationConnection(Component from, Component to) {
        if (from instanceof DataRepresentation && to instanceof DataRepresentation) {
            Data dataFrom = ((DataRepresentation) from).getData();
            Data dataTo = ((DataRepresentation) to).getData();
            //TODO: input with suggestions
            String name = JOptionPane.showInputDialog(panel, "Enter association name:");
            Association association = new Association(dataFrom, dataTo, name);
            dataFrom.addRelation(association);
            dataTo.addRelation(association);
            panel.addConnnection(new Connection(from, to, new Arrow.Association(name)));
        }
    }
    
    
    /*
     * Open / Save
     */

    public void loadData(String file) {
        try {
            if (storage.openFile(file)) {
                List<Representation> representations = new LinkedList<Representation>();
                /* Representations */
                for (Data data : storage.getData().values()) {
                    for (Representation representation : data.getRepresentations()) {
                        ZoomableComponent component;
                        if (data instanceof Data.Class) {
                            component = panel.addComponent(new ClassRepresentation((Data.Class) data));
                        } else if (data instanceof Data.Image) {
                            ZoomableImage image = new ZoomableImage((Data.Image) data);
                            component = panel.addComponent(image);
                            image.loadImage();
                        } else {
                            component = panel.addComponent(new ZoomableLabel(data));
                        }
                        representation.setAssigned(component.getComponent());
                        iniciateRepresentation(component, representation);
                        if (representation instanceof Representation.PlaceHolder) {
                            int x = (panel.getWidth() / 2) - (component.getSize().width / 2);
                            int y = (panel.getHeight() / 2) - (component.getSize().height / 2);
                            component.setLocation(x, y);
                            if (!(data instanceof Data.Image)) {
                                iniciateRepresentation(component, representation);
                            }
                        } else {
                            component.zoom(1);
                        }
                        representations.add(representation);
                    }
                }
                
                /* Relations */
                for (Representation representation : representations) {
                    for (Relation relation : representation.getData().getRelations()) {
                        if (relation.getFrom() == representation.getData()) {
                            Component from = (Component) representation.getAssigned();
                            Component to = getComponent(relation.getTo(), representations);
                            assert to != null;
                            if (relation instanceof Association) {
                                String name = ((Association) relation).getName();
                                panel.addConnnection(new Connection(from, to, new Arrow.Association(name)));
                            } else if (relation instanceof Relation.Generalization) {
                                panel.addConnnection(new Connection(from, to, new Arrow.Generalization()));
                            } else {
                                panel.addConnnection(new Connection(from, to));
                            }
                        }
                    }
                }
                
                savedTo = file;
                panel.repaint();
            }

        //TODO: normal exception handling
        } catch (URISyntaxException ex) {
            Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, "URISyntaxException loading data", ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, "XML ParserConfigurationException loading data", ex);
        } catch (SAXException ex) {
            Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, "SAXException loading data", ex);
        } catch (IOException ex) {
            Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, "IOException loading data", ex);
        } catch (BadVersionException ex) {
            Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, "Not compatible file version", ex);
        }
    }

    private Component getComponent(Data data, List<Representation> representations) {
        for (Representation representation : representations) {
            if (representation.getData() == data) {
                return (Component) representation.getAssigned();
            }
        }
        return null;
    }
    
    public void save(List<Component> components, String file) {
        List<Data> data = new LinkedList<Data>();
        Set<Representation> representations = new HashSet<Representation>();
        for (Component component : components) {
            if (component instanceof DataRepresentation) {
                DataRepresentation object = (DataRepresentation) component;
                ZoomableComponent zoomableComponent = panel.getZoomableComponent(component);
                object.updateData(zoomableComponent);
                data.add(object.getData());
                for (Representation representation : object.getData().getRepresentations()) {
                    representations.add(representation);
                }
            }
        }
        try {
            storage.save(data, representations, file);
            savedTo = file;
        } catch (NotUniqueIdsException ex) {
            Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void iniciateRepresentation(ZoomableComponent component, Representation representation) {
        if (representation instanceof Representation.Element) {
            Representation.Element element = (Element) representation;
            component.zoom(element.getZ());
            component.setLocation(element.getPosition());
            component.setSize(element.getSize());
        } else if (!(component.getComponent() instanceof ZoomableImage)) {
            component.setSize(new Dimension(100, 100));
        }
    }

    /**
     * @deprecated use getOperationsPopup()
     */
    public JPopupMenu getPopup() {
        return getOperationsPopup();
    }

    public JPopupMenu getOperationsPopup() {
        JPopupMenu result = new JPopupMenu();
        
        /* Type specific operations */
        if ((panel.getSelected().size() > 0)) {
            HashMap<String, List<Operation>> distinct = new HashMap<String, List<Operation>>();
            for (ZoomableComponent zoomableComponent : panel.getSelected()) {
                if (zoomableComponent.getComponent() instanceof HavingOperations) {
                    HavingOperations ownOperations = (HavingOperations) zoomableComponent.getComponent();
                    for (Operation operation : ownOperations.getOperations(panel)) {
                        if (!distinct.containsKey(operation.getName())) {
                            distinct.put(operation.getName(), new LinkedList<Operation>(Arrays.asList(operation)));
                        } else {
                            distinct.get(operation.getName()).add(operation);
                        }
                    }
                }
            }
            if (distinct.size() > 0) {
                result.add(menuSeparator("Specialized:"));
                List<String> names = new ArrayList<String>(distinct.keySet());
                Collections.sort(names);
                for (String name : names) {
                    final List<Operation> group = distinct.get(name);
                    addMenuItem(group.get(0), result, new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            for (Operation operation : group) {
                                operation.perform();
                            }
                        }
                    });
                }
                result.addSeparator();
            }
        }
        
        /* Common operations for focused elements */
        if (panel.getFocusOwner() != null) {
            result.add(menuSeparator("Focused:"));
            for (Operation operation : operations) {
                if (operation instanceof FocusedOperation) {
                    addMenuItem(operation, result, performOne(operation));
                }
            }
            result.addSeparator();
        }
        
        /* Source information */
        if (panel.getFocusOwner() != null) {
            sourceInformation(result, panel.getFocusOwner().getComponent());
        }
        
        /* Common operations for selected elements */
        if (panel.getSelected().size() > 0) {
            result.add(menuSeparator("Selected:"));
            for (Operation operation : operations) {
                if (operation instanceof SelectionOperation) {
                    addMenuItem(operation, result, performOne(operation));
                }
            }
            result.addSeparator();
        }
        
        /* Adding new elements */
        result.add(menuSeparator("Add new:"));
        for (Operation operation : operations) {
            if (operation instanceof AddOperation) {
                addMenuItem(operation, result, performOne(operation));
            }
        }
        result.addSeparator();
        
        /* All the reset operations (usually panel related) */
        result.add(menuSeparator("Common:"));
        for (Operation operation : operations) {
            if (!(operation instanceof FocusedOperation) && !(operation instanceof SelectionOperation) && !(operation instanceof AddOperation)) {
                addMenuItem(operation, result, performOne(operation));
            }
        }
        return result;
    }
    
    private void sourceInformation(JPopupMenu menu, Component focused) {
        if (focused instanceof DataRepresentation) {
            menu.add(menuSeparator("Source:"));
            final DataRepresentation representation = (DataRepresentation) focused;
            if (representation.getData() instanceof Data.Image) {
                menu.add(((Data.Image) representation.getData()).getData());
            }
            Source source = representation.getData().getSource();
            StringBuilder tooltip = new StringBuilder(source.getDateSting());
            JMenuItem item = new JMenuItem(source.toString());
            if (source instanceof Source.Internet) {
                final Source.Internet internetSource = (Source.Internet) source;
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        runExternal(new String[] {"/usr/bin/firefox", internetSource.getSource()});
                    }
                });
                if (internetSource.getTitle() != null && internetSource.getTitle().length() > 0) {
                    JMenuItem title = new JMenuItem(internetSource.getTitle());
                    if (internetSource.getXpaht() != null && internetSource.getXpaht().length() > 0) {
                        title.setToolTipText(internetSource.getXpaht());
                    }
                    title.setEnabled(false);
                    menu.add(title);
                }
            } else if (source instanceof Source.Okular) {
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Source.Okular source = (Source.Okular) representation.getData().getSource();
                        runExternal(new String[] {"/usr/bin/okular", "-p", String.valueOf(source.getPage()), source.getSource()});
                    }
                });
            } else if (source instanceof Source.Project) {
                sourceInformationProject(item, source);
            } else if (source instanceof Source.Model) {
                String preAddress = "";
                if (source.getParent() instanceof Source.Project) {
                    JMenuItem pojectItem = new JMenuItem(source.getParent().getSource());
                    sourceInformationProject(pojectItem, source.getParent());
                    menu.add(pojectItem);
                    preAddress = ((Source.Project) source.getParent()).getAddress();
                }
                final String address = preAddress + representation.getData().getSource().getSource();
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        runExternal(new String[] {"/usr/bin/geany", address});
                    }
                });
            } else {
                item.setEnabled(false);
            }
            item.setToolTipText(tooltip.toString());
            menu.add(item);
            menu.addSeparator();
        }
    }
    
    private static void sourceInformationProject(JMenuItem item, final Source dataSource) {
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Source.Project source = (Source.Project) dataSource;
                runExternal(new String[] {"/usr/bin/nautilus", source.getAddress()});
            }
        });
    }
    
    private static void runExternal(final String[] commands) {
        Thread editingThread = new Thread() {
            @Override
            public void run() {
                try {
                    Process proc = Runtime.getRuntime().exec(commands);
                    consumeAll(proc.getInputStream());
                    consumeAll(proc.getErrorStream());
                    proc.waitFor();
                } catch (Exception ex) {
                    Logger.getLogger(ZoomableImage.class.getName()).log(Level.SEVERE, "Error running eternal program" + Arrays.toString(commands), ex);
                }
            }
        };
        editingThread.start();        
    }
    
    private static void consumeAll(InputStream stream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line = null;
        try {
            while ( (line = br.readLine()) != null) {
                if ("debug".equals("on")) {
                    System.out.println(line);
                }
            }
        } catch (IOException ex) {}
    }
    
    private static void addMenuItem(Operation operation, JPopupMenu container, ActionListener action) {
        JMenuItem item = new JMenuItem(operation.getName());
        item.setAccelerator(KeyStroke.getKeyStroke(operation.getKeys()[0].code, operation.getKeys()[0].modifier));
        item.addActionListener(action);
        container.add(item);
    }
    
    private static ActionListener performOne(final Operation operation) {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                operation.perform();
            }
        };
    }

    private static JMenuItem menuSeparator(String name) {
        JMenuItem result = new JMenuItem(name);
        result.setEnabled(false);
        return result;
    }
    
    
    /*
     * Key opertations
     */

    public static class Key {
        public int modifier;
        public int code;
        public boolean actOnRelease = false;
        private boolean nowPressed = false;

        public Key(int modifier, int keyCode) {
            this.modifier = modifier;
            this.code = keyCode;
        }

        public Key(Modifier modifier, int keyCode) {
            this(modifier.getModifier(), keyCode);
        }

        public Key(Modifier modifier, int keyCode, boolean actOnRelease) {
            this(modifier.getModifier(), keyCode, actOnRelease);
        }

        public Key(int modifier, int keyCode, boolean actOnRelease) {
            this.modifier = modifier;
            this.code = keyCode;
            this.actOnRelease = actOnRelease;
        }
        
        public boolean isKeyOwner(KeyEvent event) {
            boolean keyCode = (code == event.getKeyCode());
            int hightModifiers = 32752;
            boolean keyModifier = (event.getModifiersEx() & hightModifiers) == (modifier & hightModifiers);
            boolean stateDown = (event.getID() == KeyEvent.KEY_PRESSED);
            boolean stateUp = (event.getID() == KeyEvent.KEY_RELEASED);
            boolean state = (stateDown && !actOnRelease) || (stateUp && actOnRelease);
            if (keyCode && keyModifier && !actOnRelease) {
                nowPressed = !stateUp;
            }
            if (keyCode && keyModifier && state) {
                return true;
            }
            return false;
        }

        /**
         * @return <code>true</code> if key is not released
         */
        public boolean isNowPressed() {
            return nowPressed;
        }

        public void release() {
            nowPressed = false;
        }
        
        @Override
        public String toString() {
            return KeyEvent.getKeyModifiersText(modifier) + " + " + KeyEvent.getKeyText(code);
        }

        public enum Modifier {
            NONE(0),
            CTRL(KeyEvent.CTRL_DOWN_MASK),
            ALT(KeyEvent.ALT_DOWN_MASK),
            SHIFT(KeyEvent.SHIFT_DOWN_MASK),
            CTRL_ALT(KeyEvent.CTRL_DOWN_MASK + KeyEvent.ALT_DOWN_MASK),
            CTRL_SHIFT(KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK),
            CTRL_ALT_SHIFT(KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK + KeyEvent.ALT_DOWN_MASK);

            private int modifier;

            private Modifier(int modifier) {
                this.modifier = modifier;
            }

            public int getModifier() {
                return modifier;
            }
        }
    }

    public KeyListener defaultKeyShortCuts = new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent e) {
            for (Operation operation : operations) {
                checkAllKeys(operation, e);
            }
            for (ZoomableComponent zoomableComponent : panel.getSelected()) {
                checkKeyForSelected(zoomableComponent.getComponent(), panel, e);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            for (Operation operation : operations) {
                checkAllKeys(operation, e);
            }
            for (ZoomableComponent zoomableComponent : panel.getSelected()) {
                checkKeyForSelected(zoomableComponent.getComponent(), panel, e);
            }
        }
    };
    
    private void checkKeyForSelected(Component component, ZoomPanel panel, KeyEvent e) {
        if (component instanceof HavingOperations) {
            HavingOperations specialized = (HavingOperations) component;
            for (Operation operation : specialized.getOperations(panel)) {
                checkAllKeys(operation, e);
            }
        }
    }
    
    private static void checkAllKeys(Operation operation, KeyEvent e) {
        boolean onKeyPressed = (e.getID() == KeyEvent.KEY_PRESSED);
        for (Key key : operation.getKeys()) {
            if (key.isKeyOwner(e) || (onKeyPressed && key.isNowPressed())) {
                operation.perform();
                if (onKeyPressed && key.isNowPressed()) {
                    key.release();
                }
                break;
            }
        }
    }

    /**
     * Adds key default shortcuts to components,
     * checking if there was not already set.
     */
    public void addKeyListener(Component component) {
        if (!contains(defaultKeyShortCuts, component.getKeyListeners())) {
            component.addKeyListener(defaultKeyShortCuts);
        }
    }

    private static boolean contains(Object what, Object[] in) {
        for (Object object : in) {
            if (object.equals(what)) {
                return true;
            }
        }
        return false;
    }

    /*
     * Operations
     */

    /**
     * General operation with zoomable components or panel itself
     */
    public static abstract class Operation {
        private String name;
        private Key[] keys;
        //TODO: icon, description, internalization, groups, context

        public Operation(String name, Key ... keys) {
            this.name = name;
            this.keys = keys;
        }

        public Key[] getKeys() {
            return keys;
        }

        public String getName() {
            return name;
        }

        public abstract void perform();
    }

    /**
     * Operation specialized to ZoomPanel attributes
     */
    public abstract class PanelOperation extends Operation {
        public PanelOperation(String name, Key ... keys) {
            super(name, keys);
        }

        @Override
        public void perform() {
            perform(panel);
            panel.lastFocusOwner = null;
        }

        protected abstract void perform(ZoomPanel panel);
    }

    /**
     * Operations specialized to focused zoomable element in ZoomPanel
     */
    public abstract class FocusedOperation extends Operation {
        public FocusedOperation(String name, Key ... keys) {
            super(name, keys);
        }

        @Override
        public void perform() {
            ZoomableComponent component = panel.getFocusOwner();
            if (component != null) {
                perform(component, panel);
            }
            panel.lastFocusOwner = null;
        }

        protected abstract void perform(ZoomableComponent focused, ZoomPanel panel);
    }

    /**
     * Operations performed on all selected elements in ZoomPanel
     */
    public abstract class SelectionOperation extends Operation {
        public SelectionOperation(String name, Key ... keys) {
            super(name, keys);
        }

        @Override
        public void perform() {
            //TODO: optimize
            List<ZoomableComponent> selected = panel.getSelected();
            if ( (panel.getFocusOwner() != null) && (!selected.contains(panel.getFocusOwner())) ) {
                selected.add(panel.getFocusOwner());
            }
            if (selected.size() > 0) {
                perform(selected, panel);
            }
            panel.lastFocusOwner = null;
        }

        protected abstract void perform(List<ZoomableComponent> selected, ZoomPanel panel);
    }

    /**
     * Operations adding new elements to panel
     */
    public abstract class AddOperation extends PanelOperation {
        public AddOperation(String what, Key... keys) {
            super("Add " + what, keys);
        }
    }
    
    /**
     * List of commonly used functions of ZoomPanel and its elements
     */
    public List<Operation> operations = Arrays.asList(
        new PanelOperation("Zoom in", new Key(Key.Modifier.CTRL, KeyEvent.VK_PLUS), new Key(KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_EQUALS), new Key(KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_E)) {
            @Override
            public void perform(ZoomPanel panel) {
                panel.zoom(1.1);
            }
        },
        new PanelOperation("Zoom out", new Key(Key.Modifier.CTRL, KeyEvent.VK_MINUS), new Key(Key.Modifier.CTRL, KeyEvent.VK_Q)) {
            @Override
            public void perform(ZoomPanel panel) {
                panel.zoom(0.9);
            }
        },
        new PanelOperation("Reset zoom", new Key(Key.Modifier.CTRL, KeyEvent.VK_ASTERISK)) {
            @Override
            public void perform(ZoomPanel panel) {
                panel.reset();
            }
        },
        new PanelOperation("Go left", new Key(Key.Modifier.CTRL, KeyEvent.VK_LEFT), new Key(Key.Modifier.CTRL, KeyEvent.VK_A)) {
            @Override
            public void perform(ZoomPanel panel) {
                panel.translate(10, 0);
            }
        },
        new PanelOperation("Go right", new Key(Key.Modifier.CTRL, KeyEvent.VK_RIGHT), new Key(Key.Modifier.CTRL, KeyEvent.VK_D)) {
            @Override
            public void perform(ZoomPanel panel) {
                panel.translate(-10, 0);
            }
        },
        new PanelOperation("Go up", new Key(Key.Modifier.CTRL, KeyEvent.VK_UP), new Key(Key.Modifier.CTRL, KeyEvent.VK_W)) {
            @Override
            public void perform(ZoomPanel panel) {
                panel.translate(0, 10);
            }
        },
        new PanelOperation("Go down", new Key(Key.Modifier.CTRL, KeyEvent.VK_DOWN), new Key(Key.Modifier.CTRL, KeyEvent.VK_S)) {
            @Override
            public void perform(ZoomPanel panel) {
                panel.translate(0, -10);
            }
        },
        new PanelOperation("Save", new Key(Key.Modifier.CTRL_SHIFT, KeyEvent.VK_S)) {
            @Override
            protected void perform(ZoomPanel panel) {
                if (savedTo != null) {
                    save(Arrays.asList(panel.getComponents()), savedTo);
                } else {
                    saveAs();
                }
            }
        },
        new PanelOperation("Save as ...", new Key(Key.Modifier.CTRL_ALT_SHIFT, KeyEvent.VK_S)) {
            @Override
            protected void perform(ZoomPanel panel) {
                saveAs();
            }
        },
        new PanelOperation("Open", new Key(Key.Modifier.CTRL_ALT_SHIFT, KeyEvent.VK_O)) {
            @Override
            protected void perform(ZoomPanel panel) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (jfc.showOpenDialog(panel.getParent()) == JFileChooser.APPROVE_OPTION) {
                    loadData(jfc.getSelectedFile().getPath());
                    savedTo = jfc.getSelectedFile().getPath();
                }
            }
        },
        new PanelOperation("Select all / none", new Key(Key.Modifier.CTRL_SHIFT, KeyEvent.VK_A)) {
            @Override
            protected void perform(ZoomPanel panel) {
                boolean deselect = false;
                for (Component component : panel.getComponents()) {
                    if (component instanceof Selectable && ((Selectable) component).isSelectable() && ((Selectable) component).isSelected()) {
                        deselect = true;
                        break;
                    }
                }
                for (Component component : panel.getComponents()) {
                    if (component instanceof Selectable && ((Selectable) component).isSelectable()) {
                        if (deselect) {
                            ((Selectable) component).setSelected(false);
                        } else {
                            ((Selectable) component).setSelected(true);
                        }
                    }
                }
            }
        },
        new PanelOperation("Delete all", new Key(Key.Modifier.CTRL_SHIFT, KeyEvent.VK_DELETE, true)) {
            @Override
            public void perform(ZoomPanel panel) {
                panel.removeAll();
                panel.repaint();
            }
        },
        
        new AddOperation("text", new Key(Key.Modifier.CTRL, KeyEvent.VK_SPACE, true)) {
            @Override
            public void perform(ZoomPanel panel) {
                addText("", panel.getWidth() / 2, panel.getHeight() / 2, 100, 20, true);
            }
        },
        new AddOperation("image", new Key(Key.Modifier.CTRL, KeyEvent.VK_I, true)) {
            @Override
            public void perform(ZoomPanel panel) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (jfc.showOpenDialog(panel.getParent()) == JFileChooser.APPROVE_OPTION) {
                    addImage(jfc.getSelectedFile().getPath(), null, null);
                }
            }
        },
        new AddOperation("screenShot", new Key(Key.Modifier.CTRL, KeyEvent.VK_PRINTSCREEN, true)) {
            @Override
            public void perform(ZoomPanel panel) {
                addScreenShot(0, Settings.getDateCacheDirecotry());
            }
        },
        
        new FocusedOperation("Add text near", new Key(Key.Modifier.SHIFT, KeyEvent.VK_D, true)) {
            @Override
            public void perform(ZoomableComponent focused, ZoomPanel panel) {
                addText("", (int) focused.getLocation().getX(), (int) (focused.getLocation().getY() + focused.getSize().getHeight()), 100, (int) focused.getSize().getHeight(), true);
            }
        },
        new SelectionOperation("Zoom with element in", new Key(Key.Modifier.ALT, KeyEvent.VK_PLUS), new Key(Key.Modifier.ALT, KeyEvent.VK_EQUALS), new Key(Key.Modifier.ALT, KeyEvent.VK_E)) {
            @Override
            protected void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
                zoomWithSelected(selected, panel, 1.1);
            }
        },
        new SelectionOperation("Zoom with element out", new Key(Key.Modifier.ALT, KeyEvent.VK_MINUS), new Key(Key.Modifier.ALT, KeyEvent.VK_Q)) {
            @Override
            protected void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
                zoomWithSelected(selected, panel, 0.9);
            }
        },
        new SelectionOperation("Go with elements left", new Key(Key.Modifier.ALT, KeyEvent.VK_LEFT), new Key(Key.Modifier.ALT, KeyEvent.VK_A)) {
            @Override
            public void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
                translateWithSelected(selected, panel, 10, 0);
            }
        },
        new SelectionOperation("Go with elements right", new Key(Key.Modifier.ALT, KeyEvent.VK_RIGHT), new Key(Key.Modifier.ALT, KeyEvent.VK_D)) {
            @Override
            public void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
                translateWithSelected(selected, panel, -10, 0);
            }
        },
        new SelectionOperation("Go with elements up", new Key(Key.Modifier.ALT, KeyEvent.VK_UP), new Key(Key.Modifier.ALT, KeyEvent.VK_W)) {
            @Override
            public void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
                translateWithSelected(selected, panel, 0, 10);
            }
        },
        new SelectionOperation("Go with elements down", new Key(Key.Modifier.ALT, KeyEvent.VK_DOWN), new Key(Key.Modifier.ALT, KeyEvent.VK_S)) {
            @Override
            public void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
                translateWithSelected(selected, panel, 0, -10);
            }
        },
        new SelectionOperation("Delete element", new Key(Key.Modifier.NONE, KeyEvent.VK_DELETE, true)) {
            @Override
            public void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
                for (ZoomableComponent zoomableComponent : selected) {
                    panel.remove(zoomableComponent.getComponent());
                }
                panel.repaint();
            }
        },
        new SelectionOperation("Export to HTML", new Key(Key.Modifier.NONE, KeyEvent.VK_F12, true)) {
            @Override
            public void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                if (jfc.showSaveDialog(panel.getParent()) == JFileChooser.APPROVE_OPTION) {
                    try {
                        exportToHtml(selected, jfc.getSelectedFile().getPath());
                    } catch (IOException ex) {
                        Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, "Error saving file", ex);
                    }
                }
            }
        },
        
        new FocusedOperation("Clone representaion", new Key(Key.Modifier.CTRL_ALT, KeyEvent.VK_C, true)) {
            //FIXME: act on realses not workin!!!!
            @Override
            protected void perform(ZoomableComponent focused, ZoomPanel panel) {
                //FIXME: all components
                if ((focused.getComponent() instanceof Cloneable) && (focused.getComponent() instanceof ZoomableLabel)) {
                    try {
                        ZoomableComponent component = panel.addComponent(((ZoomableLabel) focused.getComponent()).clone());
                        component.setLocation(focused.getLocation().getX() + 1, focused.getLocation().getY() + 1);
                        component.getComponent().requestFocus();
                    } catch (CloneNotSupportedException ex) {
                        Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, "Zoomable object should be deep clonable", ex);
                    }
                }
            }
        },
        new FocusedOperation("Select cloned elements", new Key(Key.Modifier.CTRL_ALT_SHIFT, KeyEvent.VK_C, true)) {
            @Override
            protected void perform(ZoomableComponent focused, ZoomPanel panel) {
                if (focused.getComponent() instanceof DataRepresentation) {
                    Data data = ((DataRepresentation) focused.getComponent()).getData();
                    for (Component component : panel.getComponents()) {
                        if (component instanceof DataRepresentation && component instanceof Selectable) {
                            if (((DataRepresentation) component).getData() == data) {
                                ((Selectable) component).setSelected(true);
                            } else {
                                ((Selectable) component).setSelected(false);
                            }
                        }
                    }
                    for (ZoomableComponent zoomableComponent : panel.getZoomableComponetns()) {
                        if (zoomableComponent.getComponent() instanceof DataRepresentation) {
                            if (((DataRepresentation) zoomableComponent.getComponent()).getData() == data) {
                            }
                        }
                    }
                }
            }
        }
    );

    private void addImage(String path, Integer x, Integer y) {
        if (defaultSource == null) {
            defaultSource = new Source.Event();
        }
        ZoomableImage image = new ZoomableImage(path, defaultSource);
        ZoomableComponent component = panel.addComponent(image);
        image.loadImage();
        if (x == null || y == null) {
            x = (panel.getWidth() / 2) - (component.getSize().width / 2);
            y = (panel.getHeight() / 2) - (component.getSize().height / 2);
        }
        ((JComponent) component.getComponent()).setToolTipText(path);
        component.setLocation(x, y);
    }
    
    private void addText(String text, int x, int y, int widh, int height, boolean edit) {
        if (defaultSource == null) {
            defaultSource = new Source.Event();
        }
        ZoomableLabel label = new ZoomableLabel(new Data.Plain(text, defaultSource));
        if (edit) {
            label.switchEditable();
        }
        ZoomableComponent component = panel.addComponent(label);
        component.setLocation(x, y);
        component.setSize(new Dimension(widh, height));
        label.requestFocusInWindow();
    }
    
    private void addScreenShot(final long delay, final File outputDirectory) {
        Thread captureProgram = new Thread() {
            @Override
            public void run() {
                if (delay > 100) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, "ScreenShot delay interupted", ex);
                    }
                }
                if (outputDirectory.isDirectory()) {
                    outputDirectory.mkdirs();
                }
                String fileName = outputDirectory.getPath() + "/" + new SimpleDateFormat("yyyy-MM-dd'T'HH;mm;ss").format(new Date()) + ".jpg";
                Okular.run(new String[] {"/usr/bin/import", fileName});
                addImage(fileName, panel.getWidth() / 2, panel.getHeight() / 2);
            }
        };
        captureProgram.start();      
    }
    
    private void saveAs() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (jfc.showSaveDialog(panel.getParent()) == JFileChooser.APPROVE_OPTION) {
            List<Component> components = Arrays.asList(panel.getComponents());
            save(components, jfc.getSelectedFile().getPath());
            savedTo = jfc.getSelectedFile().getPath();
        }
    }
    
    public void zoomWithSelected(List<ZoomableComponent> selected, ZoomPanel panel, double zDifference) {
        for (ZoomableComponent zoomableComponent : selected) {
            zoomableComponent.getMoveAdapter().setBeingDragged(true);
        }
        panel.zoom(zDifference);
        for (ZoomableComponent zoomableComponent : selected) {
            zoomableComponent.getMoveAdapter().setBeingDragged(false);
        }
    }

    public void translateWithSelected(List<ZoomableComponent> selected, ZoomPanel panel, double xDifference, double yDifference) {
        for (ZoomableComponent zoomableComponent : selected) {
            if (zoomableComponent.getMoveAdapter() != null) {
                zoomableComponent.getMoveAdapter().setBeingDragged(true);
            }
        }
        panel.translate(xDifference, yDifference);
        for (ZoomableComponent zoomableComponent : selected) {
            if (zoomableComponent.getMoveAdapter() != null) {
                zoomableComponent.getMoveAdapter().setBeingDragged(false);
            }
        }
    }

    private void performOperation(String operationName) {
        for (Operation operation : operations) {
            if (operation.getName().equalsIgnoreCase(operationName)) {
                operation.perform();
                break;
            }
        }
    }
    
    private static String getHtmlStyle(ZoomableComponent component) {
        return "position: absolute; left: " + (int) component.getLocation().getX() +
                "px; top: " + (int) component.getLocation().getY() +
                "px; width: " + (int) component.getSize().width + 
                "px; height: " + (int) component.getSize().height +
                "px;";
    }
    
    private static String stripHtml(String text) {
        return text.replaceAll("<(script|style)[^>]*?>(?:.|\\n)*?</\\s*\\1\\s*>", text);
    }
    
    private void exportToHtml(List<ZoomableComponent> selected, String file) throws IOException {
        /* Converting elements to HTML */
        StringBuilder html = new StringBuilder();
        for (ZoomableComponent zoomableComponent : selected) {
            if (zoomableComponent.getSize().width > 1) {
                if (zoomableComponent.getComponent() instanceof ZoomableLabel) {
                    ZoomableLabel label = (ZoomableLabel) zoomableComponent.getComponent();
                    if (label.getFontSize() > 0) {
                        html.append("<span class=\"zoomable\" style=\"").append(getHtmlStyle(zoomableComponent));
                        html.append(" font-size: ").append(label.getFontSize());
                        html.append("px;\">").append(stripHtml(label.getText())).append("</span>\n");
                    }
                } else if (zoomableComponent.getComponent() instanceof ZoomableImage) {
                    ZoomableImage image = (ZoomableImage) zoomableComponent.getComponent();
                    File cachedImage = new File(image.getCached());
                    File directory = new File(file).getParentFile();
                    File copyTo = new File(directory.getPath() + File.separator + "img" + File.separator + cachedImage.getName());
                    if (cachedImage.exists()) {
                        copyTo.getParentFile().mkdirs();
                        copyFile(cachedImage, copyTo);
                        html.append("<img class=\"zoomable\" style=\"").append(getHtmlStyle(zoomableComponent));
                        html.append("\" src=\"img/").append(cachedImage.getName()).append("\" alt=\"");
                        html.append(image.getData()).append("\"/>\n");
                    }
                }
            }
        }
        
        /* Saving output to file */
        BufferedReader reader = new BufferedReader(new InputStreamReader(BasicVisualization.class.getResourceAsStream("zoomoozTemplate.html")));
        BufferedWriter writter = new BufferedWriter(new FileWriter(file));
        String line;
        String nl = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            if (line.equalsIgnoreCase("[Code]")) {
                writter.write(html.toString() + nl);
            } else {
                writter.write(line + nl);
            }
        }
        reader.close();
        writter.close();
    }
    
    private static void copyFile(File sourceFile, File destFile) throws IOException {
    if(!destFile.exists()) {
        destFile.createNewFile();
    }

    FileChannel source = null;
    FileChannel destination = null;

    try {
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        destination.transferFrom(source, 0, source.size());
    }
    finally {
        if(source != null) {
            source.close();
        }
        if(destination != null) {
            destination.close();
        }
    }
}

    
    /*
     * Utilities
     */

    public static Representation getSelf(DataRepresentation object) {
        for (Representation representation : object.getData().getRepresentations()) {
            if (representation.getAssigned() == object) {
                return representation;
            }
        }
        return null;
    }

    static Representation.Element createRepresentation(Data data, ZoomableComponent component, Object assigned) {
        Dimension2D size = component.getOriginalSize();
        if (component.getZ() == 1) {
            size = component.getSize();
        }
        boolean mainIdea = false;
        if (assigned instanceof Idea) {
            mainIdea = ((Idea) assigned).isMainIdea();
        }
        return new Representation.Element(data, component.getLocation(), component.getZ(), size, mainIdea, assigned);
    }

    static void setRepresentation(Representation.Element representation, ZoomableComponent component) {
        Dimension2D size = component.getOriginalSize();
        if (component.getZ() == 1) {
            size = component.getSize();
        }
        representation.set(component.getLocation(), component.getZ(), size);
    }
    
    private static boolean isModifier(InputEvent e, int modifier) {
        int mask = KeyEvent.BUTTON1_DOWN_MASK - 1;
        modifier = modifier & mask;
        int pressed = e.getModifiersEx() & mask;
        return pressed == modifier;
    }
}
