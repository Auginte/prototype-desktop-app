package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
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
import org.xml.sax.SAXException;

/**
 * Basic elements of visualization
 *
 * @author Aurelijus Banelis
 */
public class BasicVisualization {
    private ZoomPanel panel;
    private Controller storage = new Controller();
    private JPopupMenu contextMenu;
    private String savedTo;
    private ZoomableComponent selectionBox;
    private double selectionX = Double.NaN;
    private double selectionY = Double.NaN;


    public BasicVisualization(ZoomPanel panel) {
        this.panel = panel;
    }

    public void initAll() {
        initZooming();
        initSelectable();
    }

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
            if (e.isShiftDown() || e.isControlDown()) {
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
        panel.add(getPopup());
    }


    /*
     * Open / Save
     */

    public void loadData(String file) {
        try {
            if (storage.openFile(file)) {
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
                    }
                }
                savedTo = file;
                panel.repaint();
            }
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

    public JPopupMenu getPopup() {
        if (contextMenu == null) {
            contextMenu = new JPopupMenu("Simple operations");

            JMenuItem open = new JMenuItem("Open");
            open.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser jfc = new JFileChooser();
                    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    if (jfc.showOpenDialog(panel.getParent()) == JFileChooser.APPROVE_OPTION) {
                        loadData(jfc.getSelectedFile().getPath());
                    }
                }
            });
            contextMenu.add(open);

            JMenuItem save = new JMenuItem("Save");
            save.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser jfc = new JFileChooser();
                    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    if (jfc.showSaveDialog(panel.getParent()) == JFileChooser.APPROVE_OPTION) {
                        List<Component> components = Arrays.asList(panel.getComponents());
                        save(components, jfc.getSelectedFile().getPath());
                    }
                }
            });
            contextMenu.add(save);

            contextMenu.addSeparator();

            JMenuItem addImage = new JMenuItem("Add image");
            addImage.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser jfc = new JFileChooser();
                    jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    if (jfc.showOpenDialog(panel.getParent()) == JFileChooser.APPROVE_OPTION) {
                        ZoomableImage image = new ZoomableImage(jfc.getSelectedFile().getPath());
                        ZoomableComponent component = panel.addComponent(image);
                        image.loadImage();
                        int x = (panel.getWidth() / 2) - (component.getSize().width / 2);
                        int y = (panel.getHeight() / 2) - (component.getSize().height / 2);
                        component.setLocation(x, y);
                    }
                }
            });
            contextMenu.add(addImage);

            JMenuItem newText = new JMenuItem("New text");
            newText.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ZoomableLabel label = new ZoomableLabel("");
                    label.switchEditable();
                    ZoomableComponent component = panel.addComponent(label);
                    component.setLocation(panel.getWidth() / 2 - 40, panel.getHeight() / 2 - 10);
                    component.setSize(new Dimension(80, 20));
                    label.requestFocusInWindow();
                }
            });

            contextMenu.add(newText);
        }
        return contextMenu;
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

        public boolean isNowPressed() {
            return nowPressed;
        }

        @Override
        public String toString() {
            return KeyEvent.getKeyModifiersText(modifier) + " + " + KeyEvent.getKeyText(code);
        }

        public enum Modifier {
            NONE(0),
            CTRL(KeyEvent.CTRL_DOWN_MASK),
            CTRL_ALT(KeyEvent.CTRL_DOWN_MASK + KeyEvent.ALT_DOWN_MASK),
            CTRL_SHIFT(KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK);

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
                for (Key key : operation.getKeys()) {
                    if (key.isKeyOwner(e) || key.isNowPressed()) {
                        operation.perform();
                        break;
                    }
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            for (Operation operation : operations) {
                for (Key key : operation.getKeys()) {
                    if (key.isKeyOwner(e)) {
                        operation.perform();
                        break;
                    }
                }
            }
        }
    };


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
        }

        protected abstract void perform(ZoomableComponent focused, ZoomPanel panel);
    }

    public abstract class SelectionOperation extends Operation {
        public SelectionOperation(String name, Key ... keys) {
            super(name, keys);
        }

        @Override
        public void perform() {
            //TODO: optimize
            List<ZoomableComponent> selected = panel.getSelected();
            if (selected.size() > 0) {
                perform(selected, panel);
            }
        }

        protected abstract void perform(List<ZoomableComponent> selected, ZoomPanel panel);
    }

    /**
     * List of commonly used functions of ZoomPanel and its elements
     */
    public List<Operation> operations = Arrays.asList(
        new PanelOperation("Add text", new Key(KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_SPACE, true)) {
            @Override
            public void perform(ZoomPanel panel) {
                ZoomableLabel label = new ZoomableLabel("");
                label.switchEditable();
                ZoomableComponent component = panel.addComponent(label);
                component.setLocation(panel.getWidth() / 2, panel.getHeight() / 2);
                component.setSize(new Dimension(100, 20));
                label.requestFocusInWindow();
            }
        },
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

        new FocusedOperation("Add text near", new Key(Key.Modifier.CTRL_SHIFT, KeyEvent.VK_D, true)) {
            @Override
            public void perform(ZoomableComponent focused, ZoomPanel panel) {
                ZoomableLabel label = new ZoomableLabel("");
                label.switchEditable();
                ZoomableComponent component = panel.addComponent(label);
                component.setLocation(focused.getLocation().getX(), focused.getLocation().getY() + focused.getSize().getHeight());
                component.setSize(new DoubleDimension(100, focused.getSize().getHeight()));
                label.requestFocusInWindow();
            }
        },
        new SelectionOperation("Zoom with element in", new Key(Key.Modifier.CTRL_ALT, KeyEvent.VK_PLUS), new Key(Key.Modifier.CTRL_ALT, KeyEvent.VK_EQUALS), new Key(Key.Modifier.CTRL_ALT, KeyEvent.VK_E)) {
            @Override
            protected void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
                zoomWithSelected(selected, panel, 1.1);
            }
        },
        new SelectionOperation("Zoom with element out", new Key(Key.Modifier.CTRL_ALT, KeyEvent.VK_MINUS), new Key(Key.Modifier.CTRL_ALT, KeyEvent.VK_Q)) {
            @Override
            protected void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
                zoomWithSelected(selected, panel, 0.9);
            }
        },
        new SelectionOperation("Go left", new Key(Key.Modifier.CTRL_ALT, KeyEvent.VK_LEFT), new Key(Key.Modifier.CTRL_ALT, KeyEvent.VK_A)) {
            @Override
            public void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
                translateWithSelected(selected, panel, 10, 0);
            }
        },
        new SelectionOperation("Go right", new Key(Key.Modifier.CTRL_ALT, KeyEvent.VK_RIGHT), new Key(Key.Modifier.CTRL_ALT, KeyEvent.VK_D)) {
            @Override
            public void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
                translateWithSelected(selected, panel, -10, 0);
            }
        },
        new SelectionOperation("Go up", new Key(Key.Modifier.CTRL_ALT, KeyEvent.VK_UP), new Key(Key.Modifier.CTRL_ALT, KeyEvent.VK_W)) {
            @Override
            public void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
                translateWithSelected(selected, panel, 0, 10);
            }
        },
        new SelectionOperation("Go down", new Key(Key.Modifier.CTRL_ALT, KeyEvent.VK_DOWN), new Key(Key.Modifier.CTRL_ALT, KeyEvent.VK_S)) {
            @Override
            public void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
                translateWithSelected(selected, panel, 0, -10);
            }
        },
        new FocusedOperation("Edit element", new Key(Key.Modifier.NONE, KeyEvent.VK_ENTER)) {
            @Override
            protected void perform(ZoomableComponent focused, ZoomPanel panel) {
                if (focused.getComponent() instanceof Editable) {
                    Editable component = (Editable) focused.getComponent();
                    //TODO: to all component enter to go in and out?
                    if (!component.isEditMode()) {
                        component.switchEditable();
                    }
                }
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
        }
    );

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
            zoomableComponent.getMoveAdapter().setBeingDragged(true);
        }
        panel.translate(xDifference, yDifference);
        for (ZoomableComponent zoomableComponent : selected) {
            zoomableComponent.getMoveAdapter().setBeingDragged(false);
        }
    }

    /**
     * Adds key default shortucs to components,
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
        return new Representation.Element(data, component.getLocation(), component.getZ(), size, assigned);
    }

    static void setRepresentation(Representation.Element representation, ZoomableComponent component) {
        Dimension2D size = component.getOriginalSize();
        if (component.getZ() == 1) {
            size = component.getSize();
        }
        representation.set(component.getLocation(), component.getZ(), size);
    }
}
