package lt.banelis.aurelijus.dinosy.prototype.helpers;

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
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
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
import lt.banelis.aurelijus.dinosy.prototype.DataRepresentation;
import lt.banelis.aurelijus.dinosy.prototype.DoublePoint;
import lt.banelis.aurelijus.dinosy.prototype.Selectable;
import lt.banelis.aurelijus.dinosy.prototype.ZoomPanel;
import lt.banelis.aurelijus.dinosy.prototype.Zoomable;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableComponent;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableGroup;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableImage;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableLabel;
import lt.banelis.aurelijus.dinosy.prototype.operations.AddOperation;
import lt.banelis.aurelijus.dinosy.prototype.operations.Common;
import lt.banelis.aurelijus.dinosy.prototype.operations.FocusedOperation;
import lt.banelis.aurelijus.dinosy.prototype.operations.HavingOperations;
import lt.banelis.aurelijus.dinosy.prototype.operations.Operation;
import lt.banelis.aurelijus.dinosy.prototype.operations.Progress;
import lt.banelis.aurelijus.dinosy.prototype.operations.SelectionOperation;
import lt.banelis.aurelijus.dinosy.prototype.relations.Arrow;
import lt.banelis.aurelijus.dinosy.prototype.relations.Connectable;
import lt.banelis.aurelijus.dinosy.prototype.relations.Connectable.ConnectionState;
import lt.banelis.aurelijus.dinosy.prototype.relations.Connection;
import lt.banelis.aurelijus.dinosy.prototype.relations.DataConnections;
import lt.dinosy.datalib.Data;
import lt.dinosy.datalib.Firefox;
import lt.dinosy.datalib.PsiaudoClipboard;
import lt.dinosy.datalib.Relation.Association;
import lt.dinosy.datalib.Representation;
import lt.dinosy.datalib.Settings;
import lt.dinosy.datalib.Source;
import net.sourceforge.iharder.Base64;

/**
 * Basic elements of visualization
 *
 * @author Aurelijus Banelis
 */
public class VisualizationHelper {

    private ZoomPanel panel;
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
    private Common commonOperations;
    private RunHelper runHelper;
    private AddingHelper addingHelper;
    private StorageHelper storageHelper;
    private List<Operation> operations;
    private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    //FIXME: normal source implemntation
    public Source defaultSource = null;

    /*
     * Initiation
     */
    public VisualizationHelper(ZoomPanel panel) {
        this.panel = panel;
        dataConnections = new DataConnections(panel);
    }

    public VisualizationHelper(ZoomPanel panel, Progress progress) {
        this(panel);
        //TODO: better way of passing progress
        commonOperations = new Common(this, panel);
        runHelper = commonOperations.getRunHelper();
        addingHelper = commonOperations.getAddingHelper();
        storageHelper = commonOperations.getStorageHelper();
        storageHelper.setProgress(progress);
        commonOperations.sort();
        operations = commonOperations.getOperations(panel);
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
     * Getters
     */
    public StorageHelper getStorageHelper() {
        return storageHelper;
    }

    public ZoomPanel getPanel() {
        return panel;
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
            boolean shiftDown = isModifier(e, KeyEvent.SHIFT_DOWN_MASK);
            boolean ctrlDown = isModifier(e, KeyEvent.CTRL_DOWN_MASK);
            boolean middle = (e.getModifiersEx() & KeyEvent.BUTTON2_DOWN_MASK) == KeyEvent.BUTTON2_DOWN_MASK;
            if (ctrlDown || shiftDown) {
                /* Deselecting old */
                if (shiftDown && !middle) {
                    VisualizationHelper.this.selectAll(panel, true);
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
                    VisualizationHelper.this.clone(e.getComponent());
                }
            }
        };
    }

    public void clone(Component component) {
        JComponent clone = null;
        try {
            if (component instanceof ZoomableLabel) {
                clone = ((ZoomableLabel) component).clone();
            } else if (component instanceof ZoomableImage) {
                clone = ((ZoomableImage) component).clone();
            }
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(VisualizationHelper.class.getName()).log(Level.SEVERE, "MouseClonning: Zoomable component must be clonnable", ex);
        }
        if (clone != null) {
            clone.setLocation((int) component.getLocation().getX(), (int) component.getLocation().getY());
            clone.setSize((int) component.getSize().getWidth(), (int) component.getSize().getHeight());
            if (component instanceof Zoomable) {
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
                                    addingHelper.addImage(file, panel, x, y);
                                } else {
                                    //TODO: import of other (eg. text) files
                                    addingHelper.addText(file, panel, x, y, 100, 20, false);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(VisualizationHelper.class.getName()).log(Level.SEVERE, "Cannot import drag n drop data", ex);
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
    public void copyToClipboard(final ZoomableComponent focused) {
        clipboard.setContents(new Transferable() {
            private DataFlavor[] flavors = new DataFlavor[]{dataFlavor};

            public DataFlavor[] getTransferDataFlavors() {
                return flavors;
            }

            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return flavor.equals(flavor);
            }

            public VisualizationHelper.TransferableData getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                if (flavor.equals(flavor)) {
                    return new VisualizationHelper.TransferableData(focused);
                } else {
                    throw new UnsupportedFlavorException(flavor);
                }
            }
        }, new ClipboardOwner() {
            public void lostOwnership(Clipboard clip, Transferable transf) {
            }
        });
    }

    public void pasteFromClipboad(ZoomPanel panel) {
        try {
            if (clipboard.isDataFlavorAvailable(dataFlavor)) {
                VisualizationHelper.TransferableData transferable = (VisualizationHelper.TransferableData) clipboard.getData(dataFlavor);
                transferable.paste(panel);
            } else if (clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
                Image image = (Image) clipboard.getData(DataFlavor.imageFlavor);
                if (image instanceof BufferedImage) {
                    String directory = Settings.getInstance().getDateCacheDirecotry().getPath();
                    final String clipboardFile = directory + "/pasted-" + storageHelper.getTimeForFile() + ".png";
                    if (defaultSource == null) {
                        defaultSource = new Source.Event();
                    }
                    addingHelper.addImage(defaultSource, clipboardFile, (BufferedImage) image, panel);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(VisualizationHelper.class.getName()).log(Level.SEVERE, "Pasting exception", ex);
        }
    }

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
                    VisualizationHelper.this.psiaudoClipboardCheck();

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
                            Logger.getLogger(VisualizationHelper.class.getName()).log(Level.SEVERE, "Clipboard error", ex);
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
                                Logger.getLogger(VisualizationHelper.class.getName()).log(Level.SEVERE, "Can not extract image from clipboard: " + source, ex);
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
                    Logger.getLogger(VisualizationHelper.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (map.containsKey("type") && map.get("type").equals(Firefox.Type.image.name())) {
                    addingHelper.addImage(data, panel, null, null);
                } else {
                    addingHelper.addText(data, panel, panel.getWidth() / 2, panel.getHeight() / 2, 100, 20, false);
                }
                if (map.containsKey("saved")) {
                    ZoomableComponent image = addingHelper.addImage(map.get("saved") + ".png", panel, null, null);
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
        return new File(Settings.getInstance().getDateCacheDirecotry().getPath(), fileName);
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
            Logger.getLogger(VisualizationHelper.class.getName()).log(Level.SEVERE, "Can not convert reader to string", ex);
        }
        return result.toString();
    }

    public Source getLastClipboardSource() {
        return lastClipboardSource;
    }

    //TODO: error handling
    //TODO: many objects
    private static class TransferableData implements Serializable {

        private Data data;
        private Representation representation;

        public TransferableData(ZoomableComponent component) {
            if (component.getComponent() instanceof DataRepresentation) {
                DataRepresentation dataRepresentation = (DataRepresentation) component.getComponent();
                data = dataRepresentation.getData();
                representation = RepresentationsHelper.createRepresentation(dataRepresentation.getData(), component, component.getComponent());
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
                    Logger.getLogger(VisualizationHelper.class.getName()).log(Level.SEVERE, "Source clonning should be supported", ex);
                }
                if (component != null) {
                    if (representation instanceof Representation.Element) {
                        Representation.Element element = (Representation.Element) representation;
                        element.set(translated(element.getPosition(), 10, 10), element.getZ(), element.getSize());
                    }
                    RepresentationsHelper.updateRepresentation(component, representation, panel);
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

            public void removedAll() {
            }
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
                return VisualizationHelper.isModifier(e, modifier);
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
     * Operations
     */
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
                final String filePath = ((Data.Image) representation.getData()).getData();
                JMenuItem item = new JMenuItem(filePath);
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        runHelper.openImageProgram(filePath);
                    }
                });
                menu.add(item);
            }
            Source source = representation.getData().getSource();
            StringBuilder tooltip = new StringBuilder(source.getDateSting());
            JMenuItem item = new JMenuItem(source.toString());
            if (source instanceof Source.Internet) {
                final Source.Internet internetSource = (Source.Internet) source;
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        runHelper.openWebpage(internetSource.getSource());
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
                        runHelper.openEditProgram(address);
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
                runHelper.openPdfProgram(path, page);
            }
        });
    }

    private void sourceInformationProject(JMenuItem item, final Source dataSource) {
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Source.Project source = (Source.Project) dataSource;
                runHelper.openInFileBrowser(source.getAddress());
            }
        });
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

    public void addKeyListener(Component component) {
        if (!contains(commonOperations.getDefaultKeyShortCuts(), component.getKeyListeners())) {
            component.addKeyListener(commonOperations.getDefaultKeyShortCuts());
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

    /**
     *
     */
    public boolean execOperation(String name) {
        for (Operation operation : operations) {
            if (operation.getName().equalsIgnoreCase(name)) {
                operation.perform();
                return true;
            }
        }
        return false;
    }

    public void selectAll(ZoomPanel panel, boolean deselect) {
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
    private static boolean isModifier(InputEvent e, int modifier) {
        int mask = KeyEvent.BUTTON1_DOWN_MASK - 1;
        modifier = modifier & mask;
        int pressed = e.getModifiersEx() & mask;
        return pressed == modifier;
    }
}
