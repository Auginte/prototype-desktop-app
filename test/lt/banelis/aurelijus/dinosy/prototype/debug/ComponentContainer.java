package lt.banelis.aurelijus.dinosy.prototype.debug;

import java.awt.AWTException;
import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.MenuComponent;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;
import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.im.InputContext;
import java.awt.im.InputMethodRequests;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.beans.PropertyChangeListener;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.EventListener;
import java.util.Locale;
import java.util.Set;
import javax.accessibility.AccessibleContext;
import org.junit.Test;

/**
 * This file is used only used to try to delegate AWT component
 * Use this file, remove test elements, uncoment and copy to src package as
 * runnable class.
 *
 * @author Aurelijus Banelis
 */
public class ComponentContainer extends Component {

    @Test
    public void fakeTest() { }


//    private Component component;
//
//    public ComponentContainer(Component component) {
//        this.component = component;
//    }
//
//    public Component getComponent() {
//        return component;
//    }
//
//    @Override
//    public void validate() {
//        component.validate();
//    }
//
//    @Override
//    public void update(Graphics g) {
//        component.update(g);
//    }
//
//    @Override
//    public void transferFocusUpCycle() {
//        component.transferFocusUpCycle();
//    }
//
//    @Override
//    public void transferFocusBackward() {
//        component.transferFocusBackward();
//    }
//
//    @Override
//    public void transferFocus() {
//        component.transferFocus();
//    }
//
//    @Override
//    public void setVisible(boolean b) {
//        component.setVisible(b);
//    }
//
//    @Override
//    public void setSize(Dimension d) {
//        component.setSize(d);
//    }
//
//    @Override
//    public void setSize(int width, int height) {
//        component.setSize(width, height);
//    }
//
//    @Override
//    public void setPreferredSize(Dimension preferredSize) {
//        component.setPreferredSize(preferredSize);
//    }
//
//    @Override
//    public void setName(String name) {
//        component.setName(name);
//    }
//
//    @Override
//    public void setMinimumSize(Dimension minimumSize) {
//        component.setMinimumSize(minimumSize);
//    }
//
//    @Override
//    public void setMaximumSize(Dimension maximumSize) {
//        component.setMaximumSize(maximumSize);
//    }
//
//    @Override
//    public void setLocation(Point p) {
//        component.setLocation(p);
//    }
//
//    @Override
//    public void setLocation(int x, int y) {
//        component.setLocation(x, y);
//    }
//
//    @Override
//    public void setLocale(Locale l) {
//        component.setLocale(l);
//    }
//
//    @Override
//    public void setIgnoreRepaint(boolean ignoreRepaint) {
//        component.setIgnoreRepaint(ignoreRepaint);
//    }
//
//    @Override
//    public void setForeground(Color c) {
//        component.setForeground(c);
//    }
//
//    @Override
//    public void setFont(Font f) {
//        component.setFont(f);
//    }
//
//    @Override
//    public void setFocusable(boolean focusable) {
//        component.setFocusable(focusable);
//    }
//
//    @Override
//    public void setFocusTraversalKeysEnabled(boolean focusTraversalKeysEnabled) {
//        component.setFocusTraversalKeysEnabled(focusTraversalKeysEnabled);
//    }
//
//    @Override
//    public void setFocusTraversalKeys(int id, Set<? extends AWTKeyStroke> keystrokes) {
//        component.setFocusTraversalKeys(id, keystrokes);
//    }
//
//    @Override
//    public void setEnabled(boolean b) {
//        component.setEnabled(b);
//    }
//
//    @Override
//    public synchronized void setDropTarget(DropTarget dt) {
//        component.setDropTarget(dt);
//    }
//
//    @Override
//    public void setCursor(Cursor cursor) {
//        component.setCursor(cursor);
//    }
//
//    @Override
//    public void setComponentOrientation(ComponentOrientation o) {
//        component.setComponentOrientation(o);
//    }
//
//    @Override
//    public void setBounds(Rectangle r) {
//        component.setBounds(r);
//    }
//
//    @Override
//    public void setBounds(int x, int y, int width, int height) {
//        component.setBounds(x, y, width, height);
//    }
//
//    @Override
//    public void setBackground(Color c) {
//        component.setBackground(c);
//    }
//
//    @Override
//    public boolean requestFocusInWindow() {
//        return component.requestFocusInWindow();
//    }
//
//    @Override
//    public void requestFocus() {
//        component.requestFocus();
//    }
//
//    @Override
//    public void repaint(long tm, int x, int y, int width, int height) {
//        component.repaint(tm, x, y, width, height);
//    }
//
//    @Override
//    public void repaint(int x, int y, int width, int height) {
//        component.repaint(x, y, width, height);
//    }
//
//    @Override
//    public void repaint(long tm) {
//        component.repaint(tm);
//    }
//
//    @Override
//    public void repaint() {
//        component.repaint();
//    }
//
//    @Override
//    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
//        component.removePropertyChangeListener(propertyName, listener);
//    }
//
//    @Override
//    public void removePropertyChangeListener(PropertyChangeListener listener) {
//        component.removePropertyChangeListener(listener);
//    }
//
//    @Override
//    public void removeNotify() {
//        component.removeNotify();
//    }
//
//    @Override
//    public synchronized void removeMouseWheelListener(MouseWheelListener l) {
//        component.removeMouseWheelListener(l);
//    }
//
//    @Override
//    public synchronized void removeMouseMotionListener(MouseMotionListener l) {
//        component.removeMouseMotionListener(l);
//    }
//
//    @Override
//    public synchronized void removeMouseListener(MouseListener l) {
//        component.removeMouseListener(l);
//    }
//
//    @Override
//    public synchronized void removeKeyListener(KeyListener l) {
//        component.removeKeyListener(l);
//    }
//
//    @Override
//    public synchronized void removeInputMethodListener(InputMethodListener l) {
//        component.removeInputMethodListener(l);
//    }
//
//    @Override
//    public void removeHierarchyListener(HierarchyListener l) {
//        component.removeHierarchyListener(l);
//    }
//
//    @Override
//    public void removeHierarchyBoundsListener(HierarchyBoundsListener l) {
//        component.removeHierarchyBoundsListener(l);
//    }
//
//    @Override
//    public synchronized void removeFocusListener(FocusListener l) {
//        component.removeFocusListener(l);
//    }
//
//    @Override
//    public synchronized void removeComponentListener(ComponentListener l) {
//        component.removeComponentListener(l);
//    }
//
//    @Override
//    public void remove(MenuComponent popup) {
//        component.remove(popup);
//    }
//
//    @Override
//    public void printAll(Graphics g) {
//        component.printAll(g);
//    }
//
//    @Override
//    public void print(Graphics g) {
//        component.print(g);
//    }
//
//    @Override
//    public boolean prepareImage(Image image, int width, int height, ImageObserver observer) {
//        return component.prepareImage(image, width, height, observer);
//    }
//
//    @Override
//    public boolean prepareImage(Image image, ImageObserver observer) {
//        return component.prepareImage(image, observer);
//    }
//
//    @Override
//    public void paintAll(Graphics g) {
//        component.paintAll(g);
//    }
//
//    @Override
//    public void paint(Graphics g) {
//        component.paint(g);
//    }
//
//    @Override
//    public void list(PrintWriter out, int indent) {
//        component.list(out, indent);
//    }
//
//    @Override
//    public void list(PrintWriter out) {
//        component.list(out);
//    }
//
//    @Override
//    public void list(PrintStream out, int indent) {
//        component.list(out, indent);
//    }
//
//    @Override
//    public void list(PrintStream out) {
//        component.list(out);
//    }
//
//    @Override
//    public void list() {
//        component.list();
//    }
//
//    @Override
//    public boolean isVisible() {
//        return component.isVisible();
//    }
//
//    @Override
//    public boolean isValid() {
//        return component.isValid();
//    }
//
//    @Override
//    public boolean isShowing() {
//        return component.isShowing();
//    }
//
//    @Override
//    public boolean isPreferredSizeSet() {
//        return component.isPreferredSizeSet();
//    }
//
//    @Override
//    public boolean isOpaque() {
//        return component.isOpaque();
//    }
//
//    @Override
//    public boolean isMinimumSizeSet() {
//        return component.isMinimumSizeSet();
//    }
//
//    @Override
//    public boolean isMaximumSizeSet() {
//        return component.isMaximumSizeSet();
//    }
//
//    @Override
//    public boolean isLightweight() {
//        return component.isLightweight();
//    }
//
//    @Override
//    public boolean isForegroundSet() {
//        return component.isForegroundSet();
//    }
//
//    @Override
//    public boolean isFontSet() {
//        return component.isFontSet();
//    }
//
//    @Override
//    public boolean isFocusable() {
//        return component.isFocusable();
//    }
//
//    @Override
//    public boolean isFocusOwner() {
//        return component.isFocusOwner();
//    }
//
//    @Override
//    public boolean isFocusCycleRoot(Container container) {
//        return component.isFocusCycleRoot(container);
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return component.isEnabled();
//    }
//
//    @Override
//    public boolean isDoubleBuffered() {
//        return component.isDoubleBuffered();
//    }
//
//    @Override
//    public boolean isDisplayable() {
//        return component.isDisplayable();
//    }
//
//    @Override
//    public boolean isCursorSet() {
//        return component.isCursorSet();
//    }
//
//    @Override
//    public boolean isBackgroundSet() {
//        return component.isBackgroundSet();
//    }
//
//    @Override
//    public void invalidate() {
//        component.invalidate();
//    }
//
//    @Override
//    public boolean imageUpdate(Image img, int infoflags, int x, int y, int w, int h) {
//        return component.imageUpdate(img, infoflags, x, y, w, h);
//    }
//
//    @Override
//    public boolean hasFocus() {
//        return component.hasFocus();
//    }
//
//    @Override
//    public int getY() {
//        return component.getY();
//    }
//
//    @Override
//    public int getX() {
//        return component.getX();
//    }
//
//    @Override
//    public int getWidth() {
//        return component.getWidth();
//    }
//
//    @Override
//    public Toolkit getToolkit() {
//        return component.getToolkit();
//    }
//
//    @Override
//    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
//        return component.getPropertyChangeListeners(propertyName);
//    }
//
//    @Override
//    public PropertyChangeListener[] getPropertyChangeListeners() {
//        return component.getPropertyChangeListeners();
//    }
//
//    @Override
//    public Dimension getPreferredSize() {
//        return component.getPreferredSize();
//    }
//
//    @Override
//    public Container getParent() {
//        return component.getParent();
//    }
//
//    @Override
//    public String getName() {
//        return component.getName();
//    }
//
//    @Override
//    public synchronized MouseWheelListener[] getMouseWheelListeners() {
//        return component.getMouseWheelListeners();
//    }
//
//    @Override
//    public Point getMousePosition() throws HeadlessException {
//        return component.getMousePosition();
//    }
//
//    @Override
//    public synchronized MouseMotionListener[] getMouseMotionListeners() {
//        return component.getMouseMotionListeners();
//    }
//
//    @Override
//    public synchronized MouseListener[] getMouseListeners() {
//        return component.getMouseListeners();
//    }
//
//    @Override
//    public Dimension getMinimumSize() {
//        return component.getMinimumSize();
//    }
//
//    @Override
//    public Dimension getMaximumSize() {
//        return component.getMaximumSize();
//    }
//
//    @Override
//    public Point getLocationOnScreen() {
//        return component.getLocationOnScreen();
//    }
//
//    @Override
//    public Point getLocation() {
//        return component.getLocation();
//    }
//
//    @Override
//    public Point getLocation(Point rv) {
//        return component.getLocation(rv);
//    }
//
//    @Override
//    public Locale getLocale() {
//        return component.getLocale();
//    }
//
//    @Override
//    public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
//        return component.getListeners(listenerType);
//    }
//
//    @Override
//    public synchronized KeyListener[] getKeyListeners() {
//        return component.getKeyListeners();
//    }
//
//    @Override
//    public InputMethodRequests getInputMethodRequests() {
//        return component.getInputMethodRequests();
//    }
//
//    @Override
//    public synchronized InputMethodListener[] getInputMethodListeners() {
//        return component.getInputMethodListeners();
//    }
//
//    @Override
//    public InputContext getInputContext() {
//        return component.getInputContext();
//    }
//
//    @Override
//    public boolean getIgnoreRepaint() {
//        return component.getIgnoreRepaint();
//    }
//
//    @Override
//    public synchronized HierarchyListener[] getHierarchyListeners() {
//        return component.getHierarchyListeners();
//    }
//
//    @Override
//    public synchronized HierarchyBoundsListener[] getHierarchyBoundsListeners() {
//        return component.getHierarchyBoundsListeners();
//    }
//
//    @Override
//    public int getHeight() {
//        return component.getHeight();
//    }
//
//    @Override
//    public GraphicsConfiguration getGraphicsConfiguration() {
//        return component.getGraphicsConfiguration();
//    }
//
//    @Override
//    public Graphics getGraphics() {
//        return component.getGraphics();
//    }
//
//    @Override
//    public Color getForeground() {
//        return component.getForeground();
//    }
//
//    @Override
//    public FontMetrics getFontMetrics(Font font) {
//        return component.getFontMetrics(font);
//    }
//
//    @Override
//    public Font getFont() {
//        return component.getFont();
//    }
//
//    @Override
//    public boolean getFocusTraversalKeysEnabled() {
//        return component.getFocusTraversalKeysEnabled();
//    }
//
//    @Override
//    public Set<AWTKeyStroke> getFocusTraversalKeys(int id) {
//        return component.getFocusTraversalKeys(id);
//    }
//
//    @Override
//    public synchronized FocusListener[] getFocusListeners() {
//        return component.getFocusListeners();
//    }
//
//    @Override
//    public Container getFocusCycleRootAncestor() {
//        return component.getFocusCycleRootAncestor();
//    }
//
//    @Override
//    public synchronized DropTarget getDropTarget() {
//        return component.getDropTarget();
//    }
//
//    @Override
//    public Cursor getCursor() {
//        return component.getCursor();
//    }
//
//    @Override
//    public ComponentOrientation getComponentOrientation() {
//        return component.getComponentOrientation();
//    }
//
//    @Override
//    public synchronized ComponentListener[] getComponentListeners() {
//        return component.getComponentListeners();
//    }
//
//    @Override
//    public Component getComponentAt(Point p) {
//        return component.getComponentAt(p);
//    }
//
//    @Override
//    public Component getComponentAt(int x, int y) {
//        return component.getComponentAt(x, y);
//    }
//
//    @Override
//    public ColorModel getColorModel() {
//        return component.getColorModel();
//    }
//
//    @Override
//    public Rectangle getBounds(Rectangle rv) {
//        return component.getBounds(rv);
//    }
//
//    @Override
//    public Rectangle getBounds() {
//        return component.getBounds();
//    }
//
//    @Override
//    public BaselineResizeBehavior getBaselineResizeBehavior() {
//        return component.getBaselineResizeBehavior();
//    }
//
//    @Override
//    public int getBaseline(int width, int height) {
//        return component.getBaseline(width, height);
//    }
//
//    @Override
//    public Color getBackground() {
//        return component.getBackground();
//    }
//
//    @Override
//    public float getAlignmentY() {
//        return component.getAlignmentY();
//    }
//
//    @Override
//    public float getAlignmentX() {
//        return component.getAlignmentX();
//    }
//
//    @Override
//    public AccessibleContext getAccessibleContext() {
//        return component.getAccessibleContext();
//    }
//
//    @Override
//    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
//        component.firePropertyChange(propertyName, oldValue, newValue);
//    }
//
//    @Override
//    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
//        component.firePropertyChange(propertyName, oldValue, newValue);
//    }
//
//    @Override
//    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
//        component.firePropertyChange(propertyName, oldValue, newValue);
//    }
//
//    @Override
//    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
//        component.firePropertyChange(propertyName, oldValue, newValue);
//    }
//
//    @Override
//    public void firePropertyChange(String propertyName, char oldValue, char newValue) {
//        component.firePropertyChange(propertyName, oldValue, newValue);
//    }
//
//    @Override
//    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
//        component.firePropertyChange(propertyName, oldValue, newValue);
//    }
//
//    @Override
//    public void enableInputMethods(boolean enable) {
//        component.enableInputMethods(enable);
//    }
//
//    @Override
//    public void doLayout() {
//        component.doLayout();
//    }
//
//    @Override
//    public VolatileImage createVolatileImage(int width, int height, ImageCapabilities caps) throws AWTException {
//        return component.createVolatileImage(width, height, caps);
//    }
//
//    @Override
//    public VolatileImage createVolatileImage(int width, int height) {
//        return component.createVolatileImage(width, height);
//    }
//
//    @Override
//    public Image createImage(int width, int height) {
//        return component.createImage(width, height);
//    }
//
//    @Override
//    public Image createImage(ImageProducer producer) {
//        return component.createImage(producer);
//    }
//
//    @Override
//    public boolean contains(Point p) {
//        return component.contains(p);
//    }
//
//    @Override
//    public boolean contains(int x, int y) {
//        return component.contains(x, y);
//    }
//
//    @Override
//    public int checkImage(Image image, int width, int height, ImageObserver observer) {
//        return component.checkImage(image, width, height, observer);
//    }
//
//    @Override
//    public int checkImage(Image image, ImageObserver observer) {
//        return component.checkImage(image, observer);
//    }
//
//    @Override
//    public boolean areFocusTraversalKeysSet(int id) {
//        return component.areFocusTraversalKeysSet(id);
//    }
//
//    @Override
//    public void applyComponentOrientation(ComponentOrientation orientation) {
//        component.applyComponentOrientation(orientation);
//    }
//
//    @Override
//    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
//        component.addPropertyChangeListener(propertyName, listener);
//    }
//
//    @Override
//    public void addPropertyChangeListener(PropertyChangeListener listener) {
//        component.addPropertyChangeListener(listener);
//    }
//
//    @Override
//    public void addNotify() {
//        component.addNotify();
//    }
//
//    @Override
//    public synchronized void addMouseWheelListener(MouseWheelListener l) {
//        component.addMouseWheelListener(l);
//    }
//
//    @Override
//    public synchronized void addMouseMotionListener(MouseMotionListener l) {
//        component.addMouseMotionListener(l);
//    }
//
//    @Override
//    public synchronized void addMouseListener(MouseListener l) {
//        component.addMouseListener(l);
//    }
//
//    @Override
//    public synchronized void addKeyListener(KeyListener l) {
//        component.addKeyListener(l);
//    }
//
//    @Override
//    public synchronized void addInputMethodListener(InputMethodListener l) {
//        component.addInputMethodListener(l);
//    }
//
//    @Override
//    public void addHierarchyListener(HierarchyListener l) {
//        component.addHierarchyListener(l);
//    }
//
//    @Override
//    public void addHierarchyBoundsListener(HierarchyBoundsListener l) {
//        component.addHierarchyBoundsListener(l);
//    }
//
//    @Override
//    public synchronized void addFocusListener(FocusListener l) {
//        component.addFocusListener(l);
//    }
//
//    @Override
//    public synchronized void addComponentListener(ComponentListener l) {
//        component.addComponentListener(l);
//    }
//
//    @Override
//    public void add(PopupMenu popup) {
//        component.add(popup);
//    }
}
