package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
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
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
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
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
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
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.TransferHandler;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.border.Border;
import javax.swing.colorchooser.AbstractColorChooserPanel;
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
import lt.dinosy.datalib.Firefox;
import lt.dinosy.datalib.Okular;
import lt.dinosy.datalib.PsiaudoClipboard;
import lt.dinosy.datalib.Relation;
import lt.dinosy.datalib.Relation.Association;
import lt.dinosy.datalib.Settings;
import net.sourceforge.iharder.Base64;

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
    private volatile long lastChecked = 0;
    private DataConnections dataConnections;
    protected static String externalSketching = "/usr/bin/mypaint";
    protected static Color defaultForeground = Color.cyan;
    private Progress progress = emptyProgress;
    
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
        dataConnections = new DataConnections(panel);
    }

    public BasicVisualization(ZoomPanel panel, Progress progress) {
        this(panel);
        this.progress = progress;
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
     * Progress
     */
    
    public static interface Progress {
        public void update(double percent, String operaion);
    }
    private static Progress emptyProgress = new Progress() {
        public void update(double percent, String operaion) { }
    };

    
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
            boolean shiftDown = isModifier(e, KeyEvent.SHIFT_DOWN_MASK);
            boolean ctrlDown = isModifier(e, KeyEvent.CTRL_DOWN_MASK);
            boolean middle = (e.getModifiersEx() & KeyEvent.BUTTON2_DOWN_MASK) == KeyEvent.BUTTON2_DOWN_MASK;
            if (ctrlDown || shiftDown) {
                /* Deselecting old */
                if (shiftDown && !middle) {
                    BasicVisualization.this.selectAll(panel, true);
                }
                
                /* Selecting new */
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
            ZoomableComponent focused = null;
            List<ZoomableComponent> selected = panel.getSelected();
            for (ZoomableComponent component : selected) {
                if (component.getComponent().isFocusOwner()) {
                    focused = component;
                    break;
                }
            }
            if (focused == null && selected.size() > 0) {
                selected.get(0).getComponent().requestFocusInWindow();
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
                if (isModifier(e, modifier) && (e.getComponent() instanceof Cloneable)) {
                    //FIXME: all components (not only ZoomableLabel)
                    BasicVisualization.this.clone(e.getComponent());
                }
            }
        };
    }

    private void clone(Component component) {
        JComponent clone = null;
        try {
            if (component instanceof ZoomableLabel) {
                clone = ((ZoomableLabel) component).clone();
            } else if (component instanceof ZoomableImage) {
                clone = ((ZoomableImage) component).clone();
            }
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, "MouseClonning: Zoomable component must be clonnable", ex);
        }
        if (clone != null) {
            clone.setLocation((int) component.getLocation().getX(), (int) component.getLocation().getY());
            clone.setSize((int) component.getSize().getWidth(), (int) component.getSize().getHeight());
            if ( component instanceof Zoomable) {
                panel.addComponent(clone, ((Zoomable) component).getZ());
            } else {
                panel.addComponent(clone);
            }
            ZoomableComponent zoomableComponent = panel.getZoomableComponent(component);
            zoomableComponent.getMoveAdapter().setBeingDragged(true);
            if (component instanceof DataRepresentation) {
                ((DataRepresentation) clone).updateData(zoomableComponent);
            }
        }
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
                                String file = URLDecoder.decode(string.substring("file://".length()).trim(), "UTF-8");
                                int x = support.getDropLocation().getDropPoint().x;
                                int y = support.getDropLocation().getDropPoint().y;
                                if (isImage(file)) {
                                    addImage(file, x, y);
                                } else {
                                    //TODO: import of other (eg. text) files
                                    addText(file, x, y, 100, 20, false);
                                }
                            }
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
        public void newOkular(Source.Okular source, String file, BufferedImage image);
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
        Thread clipboardThread = new Thread() {
            @Override
            public void run() {
                clipboard.addFlavorListener(clipboardListener);
            }
        };
        clipboardThread.start();
    }
    
    public void forceClipboardCheck() {
        long now = (new Date()).getTime();
        if (!checkingClipboard && now != lastChecked) {
            Timer clipThread = new Timer(0, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    long now = (new Date()).getTime();
                    if (!checkingClipboard && now != lastChecked) {
                        clipboardListener.flavorsChanged(null);
                        lastChecked = now;
                    }
                }
            });
            clipThread.setRepeats(false);
            clipThread.start();
        }
    }
        
    private FlavorListener clipboardListener = new FlavorListener() {
        public void flavorsChanged(FlavorEvent e) {
            Thread clipboardThread = new Thread() {
                @Override
                public void run() {
                    BasicVisualization.this.psiaudoClipboardCheck();
                    
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
                    //TODO: optimise on other side, performance leak with big images
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
                            } else if (otherData.length() < 1 && dataFlavor.getPrimaryType().equals("text") && streamRepresentation) {
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
                            DataFlavor finalFlavor = okularImage;
                            String finalUrl = url;
                            int finalPage = page;
                            Source.Okular.Boundary finalBoundary = boundary;
                            File destination = generateClipboardFile(finalUrl);
                            Source.Okular source = new Source.Okular(new Date(), finalUrl, finalPage, finalBoundary, destination.getPath());                                                       
                            try {
                                //TODO: big images - need threading
                                BufferedImage image = (BufferedImage) clipboard.getData(DataFlavor.imageFlavor);
                                clipboardSourceListener.newOkular(source, destination.getPath(), image);
                                lastClipboardSource = source;
                            } catch (Exception ex) {
                                Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, "Can not extract image from clipboard: " + source, ex);
                            }
                        } else {
                            clipboardSourceListener.noNew();
                        }
                    } else {
                        clipboardSourceListener.otherData(otherData);
                    }
                    checkingClipboard = false;
                }
            };
            clipboardThread.setPriority(Thread.MAX_PRIORITY - 1);
            clipboardThread.start();
        }
    };
        
    private void psiaudoClipboardCheck() {
        List<Map<String, String>> fromClipboard = PsiaudoClipboard.getFromClipboard();
        for (Map<String, String> map : fromClipboard) {
            if (map.containsKey("xpath") && map.containsKey("url") && map.containsKey("title") && map.containsKey("data") && map.containsKey("date") && map.containsKey("saved")) {
                Date date = Source.parseDate(map.get("date"));
                defaultSource = new Source.Internet(date, map.get("url"), map.get("xpath"), map.get("title"), map.get("saved"), null);
                String data = "[]";
                try {
                    data = new String(Base64.decode(map.get("data")));
                } catch (IOException ex) {
                    Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (map.containsKey("type") && map.get("type").equals(Firefox.Type.image.name())) {
                    addImage(data, null, null);
                } else {
                    addText(data, panel.getWidth() / 2, panel.getHeight() / 2, 100, 20, false);        
                }
                if (map.containsKey("saved")) {
                    ZoomableComponent image = addImage(map.get("saved") + ".png", null, null);
                    image.zoom(0.5, panel.getWidth() / 2, panel.getHeight() / 2);
                    image.translate(-30, 15);
                }
            }
        }
        if (fromClipboard.size() > 0) {
            panel.repaint();
        }
    }
    
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
    
    //TODO: error handling
    //TODO: meny objects
    private static class TransferableData implements Serializable {
        private Data data;
        private Representation representation;
        public TransferableData(ZoomableComponent component) {
            if (component.getComponent() instanceof DataRepresentation) {
                DataRepresentation dataRepresentation = (DataRepresentation) component.getComponent(); 
                data = dataRepresentation.getData();
                representation = createRepresentation(dataRepresentation.getData(), component, component.getComponent());
            } else {
                System.err.println("Clipboard: Compnent is not DataReperesentation: " + component.getComponent());
            }
        }
        
        public void paste(ZoomPanel panel) {
            //TODO: implement other types
            if (representation instanceof Representation.Element) {
                ZoomableComponent component = null;
                try {
                    if (data instanceof Data.Image) {
                        Data.Image newData = new Data.Image(data.getData(), data.getSource().clone());
                        ZoomableImage zoomableImage = new ZoomableImage(newData);
                        zoomableImage.loadImage();
                        component = panel.addComponent(zoomableImage);                    
                        zoomableImage.requestFocusInWindow();
                    } else if (data instanceof Data.Plain) {
                        //TODO: copy image
                        Data.Plain newData = new Data.Plain(data.getData(), data.getSource().clone());
                        ZoomableLabel zoomableLabel = new ZoomableLabel(newData);
                        component = panel.addComponent(zoomableLabel);
                        zoomableLabel.requestFocusInWindow();
                    }
                } catch (CloneNotSupportedException ex) {
                    Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, "Source clonning should be supported", ex);
                }
                if (component != null) {
                    if (representation instanceof Representation.Element) {
                        Representation.Element element = (Representation.Element) representation;
                        element.set(translated(element.getPosition(), 10, 10), element.getZ(), element.getSize());
                    }
                    updateRepresentation(component, representation, panel);
                } else {
                    System.err.println("Clipboard: Not valid data: " + data);
                }
            } else {
                System.err.println("Clipboard: Not valid representation: " + representation);
            }
        }
        
        private Point2D translated(Point2D point, int x, int y) {
            return new DoublePoint(point.getX() + x, point.getY() + y);
        }
        
        @Override
        public String toString() {
            return "{" + data.getData() + "}";
        }
    }
    private static final DataFlavor dataFlavor = new DataFlavor(TransferableData.class, "data/dinosy");
    
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
                dataConnections.removeConnections(component);
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
    
    public void loadData(final String file) {
        loadData(file, progress);
    }
    
    public void loadData(final String file, final Progress progress) {
        Thread openning = new Thread() {
            @Override
            public void run() {
                panel.setLoading(true);
                progress.update(0, "Loading XML file");
                try {
                    if (storage.openFile(file)) {
                        progress.update(0.2, "Creating elements");
                        List<Representation> representations = new LinkedList<Representation>();
                        HashMap<Component, Integer> zOrders = new HashMap<Component, Integer>(storage.getData().size());
                        /* Representations */
                        int n = storage.getData().values().size();
                        int i = 0;
                        for (Data data : storage.getData().values()) {
                            for (Representation representation : data.getRepresentations()) {
                                ZoomableComponent component;
                                if (data instanceof Data.Class) {
                                    component = panel.addComponent(new ClassRepresentation((Data.Class) data));
                                } else if (data instanceof Data.Image) {
                                    ZoomableImage image = new ZoomableImage((Data.Image) data);
                                    component = panel.addComponent(image);
                                } else {
                                    component = panel.addComponent(new ZoomableLabel(data));
                                }
                                updateRepresentation(component, representation, panel);
                                if (representation instanceof Representation.Element) {
                                    zOrders.put(component.getComponent(), ((Element) representation).getZIndex());
                                }
                                representations.add(representation);
                            }
                            progress.update(0.2 + (i / (double) n * 0.6), "Creating elements");
                            i++;
                        }
                        progress.update(0.8, "Drawing relations");

                        /* Relations */
                        //TODO: optimize relalions
                        if (storage.hasRelations()) {
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
                        }
                        progress.update(0.9, "Setting Z-Order");
                        
                        /* Z Order */
                        //FIXME: optimized z-Order
//                        int n = panel.getComponentCount();
//                        for (Component component : zOrders.keySet()) {
//                            if (zOrders.get(component) < n) {
//                                panel.setComponentZOrder(component, zOrders.get(component));
//                            }
//                        }
                        
                        setSavedTo(file);                
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
                } finally {
                    panel.setLoading(false);
                    progress.update(1, "Loaded");
                }
            }  
        };
        openning.setPriority(Thread.MAX_PRIORITY - 1);
        openning.start();
    }
    
    private static void updateRepresentation(ZoomableComponent component, Representation representation, ZoomPanel panel) {
        representation.setAssigned(component.getComponent());
        iniciateRepresentation(component, representation);
        if (representation instanceof Representation.PlaceHolder) {
            int x = (panel.getWidth() / 2) - (component.getSize().width / 2);
            int y = (panel.getHeight() / 2) - (component.getSize().height / 2);
            component.setLocation(x, y);
        } else {
            component.zoom(1);
            Element element = (Element) representation;
            if (element.getForeground() != null) {
                component.getComponent().setForeground(element.getForeground());
            }
            if (element.getBackground() != null) {
                if (component.getComponent() instanceof JComponent) {
                    JComponent jComponent = (JComponent) component.getComponent();
                    jComponent.setOpaque(element.getBackground().getAlpha() == 255);
                }
                component.getComponent().setBackground(element.getBackground());
            }
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
        save(components, file, progress);
    }
    
    public void save(List<Component> components, String file, Progress progress) {
        try {
            panel.setLoading(true);
            progress.update(0, "Saving elements");
            List<Data> data = new LinkedList<Data>();
            Set<Representation> representations = new HashSet<Representation>();
            int n = components.size();
            int i = 0;
            for (Component component : components) {
                if (component instanceof DataRepresentation) {
                    DataRepresentation object = (DataRepresentation) component;
                    ZoomableComponent zoomableComponent = panel.getZoomableComponent(component);
                    object.updateData(zoomableComponent);
                    data.add(object.getData());
                    for (Representation representation : object.getData().getRepresentations()) {
                        if (representation instanceof Element) {
                            Element element = (Element) representation;
                            element.setZIndex(panel.getComponentZOrder(component));
                            element.setForeground(component.getForeground(), defaultForeground);
                            if (component instanceof JComponent) {
                                JComponent jComponent = (JComponent) component;
                                element.setBackground(jComponent.getBackground(), jComponent.isOpaque());
                            }
                        }
                        representations.add(representation);
                    }
                }
                progress.update(i / n * 0.8, "Converting to XML");
                i++;
            }
            progress.update(0.8, "Saving to file system");
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
        } finally {
            panel.setLoading(false);
            progress.update(1, "Saved");
        }
    }

    private static void iniciateRepresentation(ZoomableComponent component, Representation representation) {
        if (representation instanceof Representation.Element) {
            Representation.Element element = (Element) representation;
            component.zoom(element.getZ());
            component.setLocation(element.getPosition());
            component.setSize(element.getSize());
        } else if (!(component.getComponent() instanceof ZoomableImage)) {
            component.setSize(new Dimension(600, 600));
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
                int ccnaCource = getCCNA(internetSource.getSource());
                if (ccnaCource > 0) {
                    JMenuItem item2 = new JMenuItem("CCNA" + ccnaCource + ": " + internetSource.getXpaht());
                    menu.add(item2);
                }
            } else if ((source instanceof Source.Book) && source.getSource().endsWith(".pdf")) {
                Source.Book bookSource = (Source.Book) representation.getData().getSource();
                sourceOkular(item, bookSource.getSource(), bookSource.getPage());                                
            } else if (source instanceof Source.Okular) {
                Source.Okular okularSource = (Source.Okular) representation.getData().getSource();
                sourceOkular(item, okularSource.getSource(), okularSource.getPage());
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
    
    private void sourceOkular(JMenuItem item, final String path, final int page) {
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runExternal(new String[] {"/usr/bin/okular", "-p", String.valueOf(page), path });
            }
        });
    }
    
    //FIXME: refactor to plugin or so
    public static int getCCNA(String sourceUrl) {
        int course = 0;
        if (sourceUrl.contains("/CCNA/")) {
            if (sourceUrl.contains("cid=0600000000&")) {
                course = 1;
            } else if (sourceUrl.contains("cid=0900000000&")) {
            course = 2;
            } else if (sourceUrl.contains("cid=1300000000&")) {
            course = 3; 
            } else if (sourceUrl.contains("cid=1400000000&")) {
                course = 4; 
            }
        }
        return course;
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
        runExternal(commands, true);
    }
    
    protected static Runnable runExternal(final String[] commands, boolean start) {
        Runnable runnable = new Runnable() {
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
        if (start) {
            new Thread(runnable).start();
        }
        return runnable;
    }
        
    private static void consumeAll(InputStream stream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        try {
            String line;
            while ( (line = br.readLine()) != null) {
                if ("debug".equals("on")) {
                    System.err.println(line);
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
    
    public void setSavedTo(String savedTo) {
        this.savedTo = savedTo;
    }

    public String getSavedTo() {
        return savedTo;
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
        public synchronized void keyReleased(KeyEvent e) {
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
        
        //TODO: public boolean isActive
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
            super(what, keys);
        }

        @Override
        public final String getName() {
            return "Add " + super.getName();
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
                save();
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
                    setSavedTo(jfc.getSelectedFile().getPath());
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
                BasicVisualization.this.selectAll(panel, deselect);
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
        new AddOperation("sketch", new Key(Key.Modifier.CTRL_ALT, KeyEvent.VK_K, true)) {
            @Override
            protected void perform(ZoomPanel panel) {
                final String sketchFile = Settings.getDateCacheDirecotry() + "/sketch-" + BasicVisualization.getTimeForFile() + ".png";
                final Runnable externalEditing = BasicVisualization.runExternal(new String[] {BasicVisualization.externalSketching, sketchFile}, false);
                Thread adding = new Thread() {
                    @Override
                    public void run() {
                        externalEditing.run();
                        ZoomableComponent component = addImage(sketchFile, null, null);
                    }
                };
                adding.start();
            }
        },
        
        new FocusedOperation("Add text near", new Key(Key.Modifier.SHIFT, KeyEvent.VK_D, true)) {
            @Override
            public void perform(ZoomableComponent focused, ZoomPanel panel) {
                ZoomableLabel label = addText("", (int) focused.getLocation().getX(), (int) (focused.getLocation().getY() + focused.getSize().getHeight()), 100, (int) focused.getSize().getHeight(), true);
                label.setForeground(focused.getComponent().getForeground());
                label.setBackground(focused.getComponent().getBackground());
                if (focused.getComponent() instanceof JComponent) {
                    JComponent jComponent = (JComponent) focused.getComponent();
                    label.setOpaque(jComponent.isOpaque());
                }
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
                    Representation representation = BasicVisualization.getRepresentation(zoomableComponent);
                    if (representation != null) {
                        Data data = ((DataRepresentation) zoomableComponent.getComponent()).getData();
                        data.removeRepresentation(representation);
                    }
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
        new SelectionOperation("Bring selected up", new Key(Key.Modifier.ALT, KeyEvent.VK_PAGE_UP)) {
            @Override
            protected void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
                for (ZoomableComponent zoomableComponent : selected) {
                    panel.setComponentZOrder(zoomableComponent.getComponent(), 0);
                }
                panel.repaint();
            }
        },
        new SelectionOperation("Bring selected down", new Key(Key.Modifier.ALT, KeyEvent.VK_PAGE_DOWN)) {
            @Override
            protected void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
                for (ZoomableComponent zoomableComponent : selected) {
                    panel.setComponentZOrder(zoomableComponent.getComponent(), panel.getComponentCount() - 1);
                }
                panel.repaint();
            }
        },     
        new SelectionOperation("Arrange Liner-X", new Key(Key.Modifier.CTRL, KeyEvent.VK_R, true)) {
            final static int MARGIN = 5;
            @Override
            protected void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
                int addX = 0;
                int firstX = (int) selected.get(0).getLocation().getX();
                for (ZoomableComponent component : selected) {
                    component.setLocation(firstX + addX, component.getLocation().getY());
                    addX+=component.getSize().width + MARGIN;
                }
            }
        },       
        new SelectionOperation("Clone selected", new Key(Key.Modifier.CTRL_ALT, KeyEvent.VK_C, true)) {
            @Override
            protected void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
                for (ZoomableComponent zoomableComponent : selected) {
                    BasicVisualization.this.clone(zoomableComponent.getComponent());
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
                }
            }
        },
        new SelectionOperation("Change foreground", new Key(Key.Modifier.ALT, KeyEvent.VK_F, true)) {
            @Override
            protected void perform(final List<ZoomableComponent> selected, ZoomPanel panel) {
                Thread colorChoosing = new Thread() {
                    @Override
                    public void run() {
                        Color defaultColor = selected.get(0).getComponent().getForeground();
                        Color color = JColorChooser.showDialog(BasicVisualization.this.panel, "Select foreground", defaultColor);
                        if (color != null) {
                            for (ZoomableComponent zoomableComponent : selected) {
                                zoomableComponent.getComponent().setForeground(color);
                            }
                            BasicVisualization.this.panel.repaint();
                            BasicVisualization.this.panel.requestFocusInWindow();
                        }
                    }
                };
                colorChoosing.start();
            }
        },
        new SelectionOperation("Change background", new Key(Key.Modifier.ALT, KeyEvent.VK_B, true)) {
            @Override
            protected void perform(final List<ZoomableComponent> selected, ZoomPanel panel) {
                Color defaultColor = selected.get(0).getComponent().getForeground();
                boolean defaultTransparency = true;
                if (selected.get(0).getComponent() instanceof JComponent) {
                    defaultTransparency = !((JComponent) selected.get(0).getComponent()).isOpaque();
                }
                chooseColor("Select background", defaultColor, defaultTransparency, new ColorAction() {
                    public void colorChosen(Color color, boolean transparent) {
                        for (ZoomableComponent zoomableComponent : selected) {
                            if (zoomableComponent.getComponent() instanceof JComponent) {
                                JComponent component = (JComponent) zoomableComponent.getComponent();
                                component.setOpaque(!transparent);
                            }
                            zoomableComponent.getComponent().setBackground(color);
                        }
                        BasicVisualization.this.panel.repaint();
                    }
                });
            }
        },
        new FocusedOperation("Copy to clipboard", new Key(Key.Modifier.CTRL, KeyEvent.VK_C, true)) {
            @Override
            protected void perform(final ZoomableComponent focused, ZoomPanel panel) {
                clipboard.setContents(new Transferable() {
                    private DataFlavor[] flavors = new DataFlavor[] { dataFlavor };
                    
                    public DataFlavor[] getTransferDataFlavors() {
                        return flavors;
                    }
                    
                    public boolean isDataFlavorSupported(DataFlavor flavor) {
                        return flavor.equals(flavor);
                    }

                    public TransferableData getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                        if (flavor.equals(flavor)) {
                            return new TransferableData(focused);
                        } else {
                            throw new UnsupportedFlavorException(flavor);
                        }
                    }
                }, new ClipboardOwner() {
                    public void lostOwnership(Clipboard clip, Transferable transf) {}
                });
            }
        },
        new PanelOperation("Paste from clipboard", new Key(Key.Modifier.CTRL, KeyEvent.VK_V, true)) {
            @Override
            protected void perform(ZoomPanel panel) {
                try {
                    if (clipboard.isDataFlavorAvailable(dataFlavor)) {
                        TransferableData transferable = (TransferableData) clipboard.getData(dataFlavor);
                        transferable.paste(panel);
                    } else if (clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
                        Image image = (Image) clipboard.getData(DataFlavor.imageFlavor);
                        if (image instanceof BufferedImage) {
                            final String clipboardFile = Settings.getDateCacheDirecotry() + "/pasted-" + BasicVisualization.getTimeForFile() + ".png";
                            if (defaultSource == null) {
                                defaultSource = new Source.Event();
                            }
                            addImage(defaultSource, clipboardFile, (BufferedImage) image);
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(BasicVisualization.class.getName()).log(Level.SEVERE, "Pasting exception", ex);
                }
            }
        }
    );

    public boolean execOperation(String name) {
        for (Operation operation : operations) {
            if (operation.getName().equalsIgnoreCase(name)) {
                operation.perform();
                return true;
            }
        }
        return false;
    }
    
    private void selectAll(ZoomPanel panel, boolean deselect) {
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
    
    private ZoomableComponent addImage(String path, Integer x, Integer y) {
        if (defaultSource == null) {
            defaultSource = new Source.Event();
        }
        ZoomableImage image = new ZoomableImage(path, defaultSource);
        ZoomableComponent component = panel.addComponent(image);
        if (x == null || y == null) {
            setToCenter(component);
        } else {
            component.setLocation(x, y);
        }
        image.setSize(ZoomableComponent.deafaultBounding.width, ZoomableComponent.deafaultBounding.height);
        image.loadImage();
        ((JComponent) component.getComponent()).setToolTipText(path);
        return component;
    }
    
    public void addImage(Source source, String file, BufferedImage image) {
        ZoomableImage component = new ZoomableImage(new Data.Image(file, source));
        ImageLoader.getInstance().addImage(component, file);
        component.save(image);
        component.setSize(image.getWidth(null), image.getHeight(null));
        ZoomableComponent zoomableComponent = panel.addComponent(component);
        setToCenter(zoomableComponent);
    }
    
    private void setToCenter(ZoomableComponent component) {
        int x = (panel.getWidth() / 2) - (component.getSize().width / 2);
        int y = (panel.getHeight() / 2) - (component.getSize().height / 2);
        component.setLocation(x, y);
    }
    
    private ZoomableLabel addText(String text, int x, int y, int widh, int height, boolean edit) {
        if (defaultSource == null) {
            defaultSource = new Source.Event();
        }
        ZoomableLabel label = new ZoomableLabel();
        return addText(new Data.Plain(text, defaultSource), panel, x, y, widh, height, edit);
    }
    
    private ZoomableLabel addText(Data.Plain data, ZoomPanel panel, int x, int y, int widh, int height, boolean edit) {
        ZoomableLabel label = new ZoomableLabel(data);
        if (edit) {
            label.switchEditable();
        }
        ZoomableComponent component = panel.addComponent(label);
        component.setLocation(x, y);
        component.setSize(new Dimension(widh, height));
        label.requestFocusInWindow();
        return label;
    }
    
    //TODO: use robot.
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
                String fileName = outputDirectory.getPath() + "/" + BasicVisualization.getTimeForFile() + ".jpg";
                Okular.run(new String[] {"/usr/bin/import", fileName});
                addImage(fileName, panel.getWidth() / 2, panel.getHeight() / 2);
            }
        };
        captureProgram.start();      
    }
    
    private static String getTimeForFile() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH;mm;ss").format(new Date());
    }
    
    public void save() {
        if (savedTo != null) {
            save(Arrays.asList(panel.getComponents()), savedTo);
        } else {
            saveAs();
        }
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
        String width = "px; width: " + (int) component.getSize().width;
        if (component.getComponent() instanceof ZoomableLabel) {
            width = "";
        }
        return "position: absolute; left: " + (int) component.getLocation().getX() +
                "px; top: " + (int) component.getLocation().getY() +
                width + 
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
                        int fontSize = label.getFontSize();
                        html.append("<span class=\"zoomable\" style=\"").append(getHtmlStyle(zoomableComponent));
                        html.append(" font-size: ").append(fontSize);
                        html.append("px;\">").append(stripHtml(label.getText()));
//                        if (label.getData().getSource() instanceof Source.Internet) {
//                            Source.Internet source = (Source.Internet) label.getData().getSource();
//                            html.append("<a href=\"").append(urlEncode(source.getSource()));
//                            html.append("\">[^]</a>");
//                        }
                        html.append("</span>\n");
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
                        html.append("\" src=\"img/").append(urlEncode(cachedImage.getName())).append("\" alt=\"");
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

    private static String urlEncode(String text) {
        try {
            text = java.net.URLEncoder.encode(text, "UTF-8");
            return text.replace("+", "%2B");
        } catch (UnsupportedEncodingException ex) {
            return text;
        }
    }
    
    /*
     * Color chooser
     */
    
    private class TransparencyPanel extends AbstractColorChooserPanel implements ActionListener {
        private boolean transparent = false;
        private Color defaultColor;
        
        public TransparencyPanel(boolean transparent, Color defaultColor) {
            this.transparent = transparent;
            this.defaultColor = defaultColor;
        }
        
        @Override
        protected void buildChooser() {
            setLayout(new BorderLayout());
            final JCheckBox checkBox = new JCheckBox("Be transparent");
            checkBox.setSelected(transparent);
            checkBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    transparent = checkBox.isSelected();
                    updateDefaultColor();
                }
            });
            add(checkBox);
        }

        @Override
        public void updateChooser() {
            
        }

        @Override
        public String getDisplayName() {
            return "Transparency";
        }

        @Override
        public Icon getSmallDisplayIcon() {
            return null;
        }

        @Override
        public Icon getLargeDisplayIcon() {
            return null;
        }

        public void actionPerformed(ActionEvent arg0) {
            updateDefaultColor();
        }

        private void updateDefaultColor() {
            if (transparent) {
                getColorSelectionModel().setSelectedColor(defaultColor);
            }
        }
        
        public boolean isTransparent() {
            return transparent;
        }
    }
    
    private void chooseColor(String title, final Color initialColor, boolean transparency, final ColorAction action) {
        final JColorChooser colorChooser = new JColorChooser(initialColor);
        final TransparencyPanel transparencyPanel = new TransparencyPanel(transparency, initialColor);
        colorChooser.addChooserPanel(transparencyPanel);
        ActionListener okAction = new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                boolean transparency = transparencyPanel.isTransparent();
                if (colorChooser.getColor() != initialColor && transparency) {
                    transparency = false;
                }
                action.colorChosen(colorChooser.getColor(), transparency);
            }
        };
        JColorChooser.createDialog(panel, title, false, colorChooser, okAction, null).setVisible(true);
    }
    
    private interface ColorAction {
        public void colorChosen(Color color, boolean transparent);
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
        int zIndex = component.getComponent().getParent().getComponentZOrder(component.getComponent());
        Color foreground = component.getComponent().getForeground();
        if (foreground == defaultForeground) {
            foreground = null;
        }
        Color background = component.getComponent().getBackground();
        if (component.getComponent() instanceof JComponent) {
            JComponent jComponent = (JComponent) component.getComponent();
            if (!jComponent.isOpaque()) {
                background = null;
            }
        }
        return new Representation.Element(data, component.getLocation(), component.getZ(), size, zIndex, foreground, background, mainIdea, assigned);
    }

    static Representation.Element getRepresentation(ZoomableComponent component) {
        Representation.Element result = null;
        if (component.getComponent() instanceof DataRepresentation) {
            DataRepresentation dataRepresentation = (DataRepresentation) component.getComponent();
            for (Representation representation : dataRepresentation.getData().getRepresentations()) {
                if (representation.getAssigned() == component.getComponent()) {
                    return (Element) representation;
                }
            }
        }
        return result;
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