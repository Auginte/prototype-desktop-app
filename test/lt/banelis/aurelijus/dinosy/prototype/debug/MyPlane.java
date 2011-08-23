package lt.banelis.aurelijus.dinosy.prototype.debug;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.MenuComponent;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.im.InputContext;
import java.awt.im.InputMethodRequests;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.EventListener;
import java.util.Locale;
import java.util.Set;
import javax.accessibility.AccessibleContext;
import javax.swing.InputVerifier;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import javax.swing.border.Border;
import javax.swing.event.AncestorListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.PanelUI;
import org.junit.Test;

/**
 * This file is used only to analize swing components workflow
 * Use this file, remove test elements, uncoment and copy to src package as
 * runnable class.
 *
 * @author Aurelijus Banelis
 */
public class MyPlane extends JPanel {

    @Test
    public void fakeTest() { }

//    @Override
//    public AccessibleContext getAccessibleContext() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getAccessibleContext();
//    }
//
//    @Override
//    public PanelUI getUI() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getUI();
//    }
//
//    @Override
//    public String getUIClassID() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getUIClassID();
//    }
//
//    @Override
//    protected String paramString() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.paramString();
//    }
//
//    @Override
//    public void setUI(PanelUI ui) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setUI(ui);
//    }
//
//    @Override
//    public void updateUI() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.updateUI();
//    }
//
//    @Override
//    public void addAncestorListener(AncestorListener listener) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.addAncestorListener(listener);
//    }
//
//    @Override
//    public void addNotify() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.addNotify();
//    }
//
//    @Override
//    public synchronized void addVetoableChangeListener(VetoableChangeListener listener) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.addVetoableChangeListener(listener);
//    }
//
//    @Override
//    public void computeVisibleRect(Rectangle visibleRect) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.computeVisibleRect(visibleRect);
//    }
//
//    @Override
//    public boolean contains(int x, int y) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.contains(x, y);
//    }
//
//    @Override
//    public JToolTip createToolTip() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.createToolTip();
//    }
//
//    @Override
//    public void disable() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.disable();
//    }
//
//    @Override
//    public void enable() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.enable();
//    }
//
//    @Override
//    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.firePropertyChange(propertyName, oldValue, newValue);
//    }
//
//    @Override
//    public void firePropertyChange(String propertyName, int oldValue, int newValue) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.firePropertyChange(propertyName, oldValue, newValue);
//    }
//
//    @Override
//    public void firePropertyChange(String propertyName, char oldValue, char newValue) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.firePropertyChange(propertyName, oldValue, newValue);
//    }
//
//    @Override
//    protected void fireVetoableChange(String propertyName, Object oldValue, Object newValue) throws PropertyVetoException { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.fireVetoableChange(propertyName, oldValue, newValue);
//    }
//
//    @Override
//    public ActionListener getActionForKeyStroke(KeyStroke aKeyStroke) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getActionForKeyStroke(aKeyStroke);
//    }
//
//    @Override
//    public float getAlignmentX() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getAlignmentX();
//    }
//
//    @Override
//    public float getAlignmentY() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getAlignmentY();
//    }
//
//    @Override
//    public AncestorListener[] getAncestorListeners() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getAncestorListeners();
//    }
//
//    @Override
//    public boolean getAutoscrolls() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getAutoscrolls();
//    }
//
//    @Override
//    public int getBaseline(int width, int height) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getBaseline(width, height);
//    }
//
//    @Override
//    public BaselineResizeBehavior getBaselineResizeBehavior() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getBaselineResizeBehavior();
//    }
//
//    @Override
//    public Border getBorder() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getBorder();
//    }
//
//    @Override
//    public Rectangle getBounds(Rectangle rv) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getBounds(rv);
//    }
//
//    @Override
//    protected Graphics getComponentGraphics(Graphics g) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getComponentGraphics(g);
//    }
//
//    @Override
//    public JPopupMenu getComponentPopupMenu() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getComponentPopupMenu();
//    }
//
//    @Override
//    public int getConditionForKeyStroke(KeyStroke aKeyStroke) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getConditionForKeyStroke(aKeyStroke);
//    }
//
//    @Override
//    public int getDebugGraphicsOptions() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getDebugGraphicsOptions();
//    }
//
//    @Override
//    public FontMetrics getFontMetrics(Font font) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getFontMetrics(font);
//    }
//
//    @Override
//    public Graphics getGraphics() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getGraphics();
//    }
//
//    @Override
//    public int getHeight() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getHeight();
//    }
//
//    @Override
//    public boolean getInheritsPopupMenu() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getInheritsPopupMenu();
//    }
//
//    @Override
//    public InputVerifier getInputVerifier() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getInputVerifier();
//    }
//
//    @Override
//    public Insets getInsets() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getInsets();
//    }
//
//    @Override
//    public Insets getInsets(Insets insets) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getInsets(insets);
//    }
//
//    @Override
//    public <T extends EventListener> T[] getListeners(Class<T> listenerType) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getListeners(listenerType);
//    }
//
//    @Override
//    public Point getLocation(Point rv) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getLocation(rv);
//    }
//
//    @Override
//    public Dimension getMaximumSize() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getMaximumSize();
//    }
//
//    @Override
//    public Dimension getMinimumSize() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getMinimumSize();
//    }
//
//    @Override
//    public Component getNextFocusableComponent() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getNextFocusableComponent();
//    }
//
//    @Override
//    public Point getPopupLocation(MouseEvent event) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getPopupLocation(event);
//    }
//
//    @Override
//    public Dimension getPreferredSize() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getPreferredSize();
//    }
//
//    @Override
//    public KeyStroke[] getRegisteredKeyStrokes() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getRegisteredKeyStrokes();
//    }
//
//    @Override
//    public JRootPane getRootPane() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getRootPane();
//    }
//
//    @Override
//    public Dimension getSize(Dimension rv) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getSize(rv);
//    }
//
//    @Override
//    public Point getToolTipLocation(MouseEvent event) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getToolTipLocation(event);
//    }
//
//    @Override
//    public String getToolTipText() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getToolTipText();
//    }
//
//    @Override
//    public String getToolTipText(MouseEvent event) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getToolTipText(event);
//    }
//
//    @Override
//    public Container getTopLevelAncestor() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getTopLevelAncestor();
//    }
//
//    @Override
//    public TransferHandler getTransferHandler() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getTransferHandler();
//    }
//
//    @Override
//    public boolean getVerifyInputWhenFocusTarget() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getVerifyInputWhenFocusTarget();
//    }
//
//    @Override
//    public synchronized VetoableChangeListener[] getVetoableChangeListeners() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getVetoableChangeListeners();
//    }
//
//    @Override
//    public Rectangle getVisibleRect() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getVisibleRect();
//    }
//
//    @Override
//    public int getWidth() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getWidth();
//    }
//
//    @Override
//    public int getX() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getX();
//    }
//
//    @Override
//    public int getY() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getY();
//    }
//
//    @Override
//    public void grabFocus() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.grabFocus();
//    }
//
//    @Override
//    public boolean isDoubleBuffered() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isDoubleBuffered();
//    }
//
//    @Override
//    public boolean isManagingFocus() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isManagingFocus();
//    }
//
//    @Override
//    public boolean isOpaque() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isOpaque();
//    }
//
//    @Override
//    public boolean isOptimizedDrawingEnabled() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isOptimizedDrawingEnabled();
//    }
//
//    @Override
//    public boolean isPaintingTile() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isPaintingTile();
//    }
//
//    @Override
//    public boolean isRequestFocusEnabled() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isRequestFocusEnabled();
//    }
//
//    @Override
//    public boolean isValidateRoot() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isValidateRoot();
//    }
//
//    @Override
//    public void paint(Graphics g) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.paint(g);
//    }
//
//    @Override
//    protected void paintBorder(Graphics g) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.paintBorder(g);
//    }
//
//    @Override
//    protected void paintChildren(Graphics g) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.paintChildren(g);
//    }
//
//    @Override
//    protected void paintComponent(Graphics g) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.paintComponent(g);
//    }
//
//    @Override
//    public void paintImmediately(int x, int y, int w, int h) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.paintImmediately(x, y, w, h);
//    }
//
//    @Override
//    public void paintImmediately(Rectangle r) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.paintImmediately(r);
//    }
//
//    @Override
//    public void print(Graphics g) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.print(g);
//    }
//
//    @Override
//    public void printAll(Graphics g) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.printAll(g);
//    }
//
//    @Override
//    protected void printBorder(Graphics g) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.printBorder(g);
//    }
//
//    @Override
//    protected void printChildren(Graphics g) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.printChildren(g);
//    }
//
//    @Override
//    protected void printComponent(Graphics g) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.printComponent(g);
//    }
//
//    @Override
//    protected void processComponentKeyEvent(KeyEvent e) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.processComponentKeyEvent(e);
//    }
//
//    @Override
//    protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.processKeyBinding(ks, e, condition, pressed);
//    }
//
//    @Override
//    protected void processKeyEvent(KeyEvent e) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.processKeyEvent(e);
//    }
//
//    @Override
//    protected void processMouseEvent(MouseEvent e) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.processMouseEvent(e);
//    }
//
//    @Override
//    protected void processMouseMotionEvent(MouseEvent e) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.processMouseMotionEvent(e);
//    }
//
//    @Override
//    public void registerKeyboardAction(ActionListener anAction, String aCommand, KeyStroke aKeyStroke, int aCondition) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.registerKeyboardAction(anAction, aCommand, aKeyStroke, aCondition);
//    }
//
//    @Override
//    public void registerKeyboardAction(ActionListener anAction, KeyStroke aKeyStroke, int aCondition) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.registerKeyboardAction(anAction, aKeyStroke, aCondition);
//    }
//
//    @Override
//    public void removeAncestorListener(AncestorListener listener) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.removeAncestorListener(listener);
//    }
//
//    @Override
//    public void removeNotify() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.removeNotify();
//    }
//
//    @Override
//    public synchronized void removeVetoableChangeListener(VetoableChangeListener listener) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.removeVetoableChangeListener(listener);
//    }
//
//    @Override
//    public void repaint(long tm, int x, int y, int width, int height) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.repaint(tm, x, y, width, height);
//    }
//
//    @Override
//    public void repaint(Rectangle r) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.repaint(r);
//    }
//
//    @Override
//    public boolean requestDefaultFocus() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.requestDefaultFocus();
//    }
//
//    @Override
//    public void requestFocus() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.requestFocus();
//    }
//
//    @Override
//    public boolean requestFocus(boolean temporary) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.requestFocus(temporary);
//    }
//
//    @Override
//    public boolean requestFocusInWindow() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.requestFocusInWindow();
//    }
//
//    @Override
//    protected boolean requestFocusInWindow(boolean temporary) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.requestFocusInWindow(temporary);
//    }
//
//    @Override
//    public void resetKeyboardActions() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.resetKeyboardActions();
//    }
//
//    @Override
//    public void reshape(int x, int y, int w, int h) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.reshape(x, y, w, h);
//    }
//
//    @Override
//    public void revalidate() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.revalidate();
//    }
//
//    @Override
//    public void scrollRectToVisible(Rectangle aRect) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.scrollRectToVisible(aRect);
//    }
//
//    @Override
//    public void setAlignmentX(float alignmentX) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setAlignmentX(alignmentX);
//    }
//
//    @Override
//    public void setAlignmentY(float alignmentY) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setAlignmentY(alignmentY);
//    }
//
//    @Override
//    public void setAutoscrolls(boolean autoscrolls) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setAutoscrolls(autoscrolls);
//    }
//
//    @Override
//    public void setBackground(Color bg) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setBackground(bg);
//    }
//
//    @Override
//    public void setBorder(Border border) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setBorder(border);
//    }
//
//    @Override
//    public void setComponentPopupMenu(JPopupMenu popup) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setComponentPopupMenu(popup);
//    }
//
//    @Override
//    public void setDebugGraphicsOptions(int debugOptions) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setDebugGraphicsOptions(debugOptions);
//    }
//
//    @Override
//    public void setDoubleBuffered(boolean aFlag) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setDoubleBuffered(aFlag);
//    }
//
//    @Override
//    public void setEnabled(boolean enabled) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setEnabled(enabled);
//    }
//
//    @Override
//    public void setFocusTraversalKeys(int id, Set<? extends AWTKeyStroke> keystrokes) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setFocusTraversalKeys(id, keystrokes);
//    }
//
//    @Override
//    public void setFont(Font font) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setFont(font);
//    }
//
//    @Override
//    public void setForeground(Color fg) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setForeground(fg);
//    }
//
//    @Override
//    public void setInheritsPopupMenu(boolean value) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setInheritsPopupMenu(value);
//    }
//
//    @Override
//    public void setInputVerifier(InputVerifier inputVerifier) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setInputVerifier(inputVerifier);
//    }
//
//    @Override
//    public void setMaximumSize(Dimension maximumSize) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setMaximumSize(maximumSize);
//    }
//
//    @Override
//    public void setMinimumSize(Dimension minimumSize) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setMinimumSize(minimumSize);
//    }
//
//    @Override
//    public void setNextFocusableComponent(Component aComponent) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setNextFocusableComponent(aComponent);
//    }
//
//    @Override
//    public void setOpaque(boolean isOpaque) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setOpaque(isOpaque);
//    }
//
//    @Override
//    public void setPreferredSize(Dimension preferredSize) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setPreferredSize(preferredSize);
//    }
//
//    @Override
//    public void setRequestFocusEnabled(boolean requestFocusEnabled) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setRequestFocusEnabled(requestFocusEnabled);
//    }
//
//    @Override
//    public void setToolTipText(String text) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setToolTipText(text);
//    }
//
//    @Override
//    public void setTransferHandler(TransferHandler newHandler) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setTransferHandler(newHandler);
//    }
//
//    @Override
//    protected void setUI(ComponentUI newUI) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setUI(newUI);
//    }
//
//    @Override
//    public void setVerifyInputWhenFocusTarget(boolean verifyInputWhenFocusTarget) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setVerifyInputWhenFocusTarget(verifyInputWhenFocusTarget);
//    }
//
//    @Override
//    public void setVisible(boolean aFlag) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setVisible(aFlag);
//    }
//
//    @Override
//    public void unregisterKeyboardAction(KeyStroke aKeyStroke) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.unregisterKeyboardAction(aKeyStroke);
//    }
//
//    @Override
//    public void update(Graphics g) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.update(g);
//    }
//
//    @Override
//    public Component add(Component comp) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.add(comp);
//    }
//
//    @Override
//    public Component add(String name, Component comp) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.add(name, comp);
//    }
//
//    @Override
//    public Component add(Component comp, int index) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.add(comp, index);
//    }
//
//    @Override
//    public void add(Component comp, Object constraints) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.add(comp, constraints);
//    }
//
//    @Override
//    public void add(Component comp, Object constraints, int index) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.add(comp, constraints, index);
//    }
//
//    @Override
//    public synchronized void addContainerListener(ContainerListener l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.addContainerListener(l);
//    }
//
//    @Override
//    protected void addImpl(Component comp, Object constraints, int index) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.addImpl(comp, constraints, index);
//    }
//
//    @Override
//    public void addPropertyChangeListener(PropertyChangeListener listener) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.addPropertyChangeListener(listener);
//    }
//
//    @Override
//    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.addPropertyChangeListener(propertyName, listener);
//    }
//
//    @Override
//    public void applyComponentOrientation(ComponentOrientation o) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.applyComponentOrientation(o);
//    }
//
//    @Override
//    public boolean areFocusTraversalKeysSet(int id) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.areFocusTraversalKeysSet(id);
//    }
//
//    @Override
//    public int countComponents() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.countComponents();
//    }
//
//    @Override
//    public void deliverEvent(Event e) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.deliverEvent(e);
//    }
//
//    @Override
//    public void doLayout() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.doLayout();
//    }
//
//    @Override
//    public Component findComponentAt(int x, int y) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.findComponentAt(x, y);
//    }
//
//    @Override
//    public Component findComponentAt(Point p) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.findComponentAt(p);
//    }
//
//    @Override
//    public Component getComponent(int n) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getComponent(n);
//    }
//
//    @Override
//    public Component getComponentAt(int x, int y) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getComponentAt(x, y);
//    }
//
//    @Override
//    public Component getComponentAt(Point p) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getComponentAt(p);
//    }
//
//    @Override
//    public int getComponentCount() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getComponentCount();
//    }
//
//    @Override
//    public int getComponentZOrder(Component comp) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getComponentZOrder(comp);
//    }
//
//    @Override
//    public Component[] getComponents() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getComponents();
//    }
//
//    @Override
//    public synchronized ContainerListener[] getContainerListeners() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getContainerListeners();
//    }
//
//    @Override
//    public Set<AWTKeyStroke> getFocusTraversalKeys(int id) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getFocusTraversalKeys(id);
//    }
//
//    @Override
//    public FocusTraversalPolicy getFocusTraversalPolicy() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getFocusTraversalPolicy();
//    }
//
//    @Override
//    public LayoutManager getLayout() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getLayout();
//    }
//
//    @Override
//    public Point getMousePosition(boolean allowChildren) throws HeadlessException { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getMousePosition(allowChildren);
//    }
//
//    @Override
//    public Insets insets() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.insets();
//    }
//
//    @Override
//    public void invalidate() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.invalidate();
//    }
//
//    @Override
//    public boolean isAncestorOf(Component c) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isAncestorOf(c);
//    }
//
//    @Override
//    public boolean isFocusCycleRoot(Container container) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isFocusCycleRoot(container);
//    }
//
//    @Override
//    public boolean isFocusCycleRoot() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isFocusCycleRoot();
//    }
//
//    @Override
//    public boolean isFocusTraversalPolicySet() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isFocusTraversalPolicySet();
//    }
//
//    @Override
//    public void layout() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.layout();
//    }
//
//    @Override
//    public void list(PrintStream out, int indent) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.list(out, indent);
//    }
//
//    @Override
//    public void list(PrintWriter out, int indent) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.list(out, indent);
//    }
//
//    @Override
//    public Component locate(int x, int y) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.locate(x, y);
//    }
//
//    @Override
//    public Dimension minimumSize() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.minimumSize();
//    }
//
//    @Override
//    public void paintComponents(Graphics g) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.paintComponents(g);
//    }
//
//    @Override
//    public Dimension preferredSize() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.preferredSize();
//    }
//
//    @Override
//    public void printComponents(Graphics g) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.printComponents(g);
//    }
//
//    @Override
//    protected void processContainerEvent(ContainerEvent e) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.processContainerEvent(e);
//    }
//
//    @Override
//    protected void processEvent(AWTEvent e) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.processEvent(e);
//    }
//
//    @Override
//    public void remove(int index) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.remove(index);
//    }
//
//    @Override
//    public void remove(Component comp) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.remove(comp);
//    }
//
//    @Override
//    public void removeAll() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.removeAll();
//    }
//
//    @Override
//    public synchronized void removeContainerListener(ContainerListener l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.removeContainerListener(l);
//    }
//
//    @Override
//    public void setComponentZOrder(Component comp, int index) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setComponentZOrder(comp, index);
//    }
//
//    @Override
//    public void setFocusCycleRoot(boolean focusCycleRoot) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setFocusCycleRoot(focusCycleRoot);
//    }
//
//    @Override
//    public void setFocusTraversalPolicy(FocusTraversalPolicy policy) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setFocusTraversalPolicy(policy);
//    }
//
//    @Override
//    public void setLayout(LayoutManager mgr) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setLayout(mgr);
//    }
//
//    @Override
//    public void transferFocusDownCycle() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.transferFocusDownCycle();
//    }
//
//    @Override
//    public void validate() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.validate();
//    }
//
//    @Override
//    protected void validateTree() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.validateTree();
//    }
//
//    @Override
//    public boolean action(Event evt, Object what) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.action(evt, what);
//    }
//
//    @Override
//    public void add(PopupMenu popup) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.add(popup);
//    }
//
//    @Override
//    public synchronized void addComponentListener(ComponentListener l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.addComponentListener(l);
//    }
//
//    @Override
//    public synchronized void addFocusListener(FocusListener l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.addFocusListener(l);
//    }
//
//    @Override
//    public void addHierarchyBoundsListener(HierarchyBoundsListener l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.addHierarchyBoundsListener(l);
//    }
//
//    @Override
//    public void addHierarchyListener(HierarchyListener l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.addHierarchyListener(l);
//    }
//
//    @Override
//    public synchronized void addInputMethodListener(InputMethodListener l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.addInputMethodListener(l);
//    }
//
//    @Override
//    public synchronized void addKeyListener(KeyListener l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.addKeyListener(l);
//    }
//
//    @Override
//    public synchronized void addMouseListener(MouseListener l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.addMouseListener(l);
//    }
//
//    @Override
//    public synchronized void addMouseMotionListener(MouseMotionListener l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.addMouseMotionListener(l);
//    }
//
//    @Override
//    public synchronized void addMouseWheelListener(MouseWheelListener l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.addMouseWheelListener(l);
//    }
//
//    @Override
//    public Rectangle bounds() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.bounds();
//    }
//
//    @Override
//    public int checkImage(Image image, ImageObserver observer) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.checkImage(image, observer);
//    }
//
//    @Override
//    public int checkImage(Image image, int width, int height, ImageObserver observer) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.checkImage(image, width, height, observer);
//    }
//
//    @Override
//    protected AWTEvent coalesceEvents(AWTEvent existingEvent, AWTEvent newEvent) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.coalesceEvents(existingEvent, newEvent);
//    }
//
//    @Override
//    public boolean contains(Point p) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.contains(p);
//    }
//
//    @Override
//    public Image createImage(ImageProducer producer) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.createImage(producer);
//    }
//
//    @Override
//    public Image createImage(int width, int height) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.createImage(width, height);
//    }
//
//    @Override
//    public VolatileImage createVolatileImage(int width, int height) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.createVolatileImage(width, height);
//    }
//
//    @Override
//    public VolatileImage createVolatileImage(int width, int height, ImageCapabilities caps) throws AWTException { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.createVolatileImage(width, height, caps);
//    }
//
//    @Override
//    public void enable(boolean b) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.enable(b);
//    }
//
//    @Override
//    public void enableInputMethods(boolean enable) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.enableInputMethods(enable);
//    }
//
//    @Override
//    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.firePropertyChange(propertyName, oldValue, newValue);
//    }
//
//    @Override
//    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.firePropertyChange(propertyName, oldValue, newValue);
//    }
//
//    @Override
//    public void firePropertyChange(String propertyName, short oldValue, short newValue) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.firePropertyChange(propertyName, oldValue, newValue);
//    }
//
//    @Override
//    public void firePropertyChange(String propertyName, long oldValue, long newValue) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.firePropertyChange(propertyName, oldValue, newValue);
//    }
//
//    @Override
//    public void firePropertyChange(String propertyName, float oldValue, float newValue) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.firePropertyChange(propertyName, oldValue, newValue);
//    }
//
//    @Override
//    public void firePropertyChange(String propertyName, double oldValue, double newValue) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.firePropertyChange(propertyName, oldValue, newValue);
//    }
//
//    @Override
//    public Color getBackground() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getBackground();
//    }
//
//    @Override
//    public Rectangle getBounds() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getBounds();
//    }
//
//    @Override
//    public ColorModel getColorModel() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getColorModel();
//    }
//
//    @Override
//    public synchronized ComponentListener[] getComponentListeners() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getComponentListeners();
//    }
//
//    @Override
//    public ComponentOrientation getComponentOrientation() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getComponentOrientation();
//    }
//
//    @Override
//    public Cursor getCursor() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getCursor();
//    }
//
//    @Override
//    public synchronized DropTarget getDropTarget() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getDropTarget();
//    }
//
//    @Override
//    public Container getFocusCycleRootAncestor() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getFocusCycleRootAncestor();
//    }
//
//    @Override
//    public synchronized FocusListener[] getFocusListeners() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getFocusListeners();
//    }
//
//    @Override
//    public boolean getFocusTraversalKeysEnabled() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getFocusTraversalKeysEnabled();
//    }
//
//    @Override
//    public Font getFont() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getFont();
//    }
//
//    @Override
//    public Color getForeground() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getForeground();
//    }
//
//    @Override
//    public GraphicsConfiguration getGraphicsConfiguration() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getGraphicsConfiguration();
//    }
//
//    @Override
//    public synchronized HierarchyBoundsListener[] getHierarchyBoundsListeners() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getHierarchyBoundsListeners();
//    }
//
//    @Override
//    public synchronized HierarchyListener[] getHierarchyListeners() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getHierarchyListeners();
//    }
//
//    @Override
//    public boolean getIgnoreRepaint() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getIgnoreRepaint();
//    }
//
//    @Override
//    public InputContext getInputContext() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getInputContext();
//    }
//
//    @Override
//    public synchronized InputMethodListener[] getInputMethodListeners() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getInputMethodListeners();
//    }
//
//    @Override
//    public InputMethodRequests getInputMethodRequests() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getInputMethodRequests();
//    }
//
//    @Override
//    public synchronized KeyListener[] getKeyListeners() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getKeyListeners();
//    }
//
//    @Override
//    public Locale getLocale() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getLocale();
//    }
//
//    @Override
//    public Point getLocation() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getLocation();
//    }
//
//    @Override
//    public Point getLocationOnScreen() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getLocationOnScreen();
//    }
//
//    @Override
//    public synchronized MouseListener[] getMouseListeners() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getMouseListeners();
//    }
//
//    @Override
//    public synchronized MouseMotionListener[] getMouseMotionListeners() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getMouseMotionListeners();
//    }
//
//    @Override
//    public Point getMousePosition() throws HeadlessException { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getMousePosition();
//    }
//
//    @Override
//    public synchronized MouseWheelListener[] getMouseWheelListeners() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getMouseWheelListeners();
//    }
//
//    @Override
//    public String getName() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getName();
//    }
//
//    @Override
//    public Container getParent() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getParent();
//    }
//
//    @Override
//    public PropertyChangeListener[] getPropertyChangeListeners() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getPropertyChangeListeners();
//    }
//
//    @Override
//    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getPropertyChangeListeners(propertyName);
//    }
//
//    @Override
//    public Dimension getSize() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getSize();
//    }
//
//    @Override
//    public Toolkit getToolkit() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.getToolkit();
//    }
//
//    @Override
//    public boolean gotFocus(Event evt, Object what) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.gotFocus(evt, what);
//    }
//
//    @Override
//    public boolean handleEvent(Event evt) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.handleEvent(evt);
//    }
//
//    @Override
//    public boolean hasFocus() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.hasFocus();
//    }
//
//    @Override
//    public void hide() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.hide();
//    }
//
//    @Override
//    public boolean imageUpdate(Image img, int infoflags, int x, int y, int w, int h) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.imageUpdate(img, infoflags, x, y, w, h);
//    }
//
//    @Override
//    public boolean inside(int x, int y) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.inside(x, y);
//    }
//
//    @Override
//    public boolean isBackgroundSet() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isBackgroundSet();
//    }
//
//    @Override
//    public boolean isCursorSet() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isCursorSet();
//    }
//
//    @Override
//    public boolean isDisplayable() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isDisplayable();
//    }
//
//    @Override
//    public boolean isEnabled() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isEnabled();
//    }
//
//    @Override
//    public boolean isFocusOwner() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isFocusOwner();
//    }
//
//    @Override
//    public boolean isFocusTraversable() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isFocusTraversable();
//    }
//
//    @Override
//    public boolean isFocusable() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isFocusable();
//    }
//
//    @Override
//    public boolean isFontSet() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isFontSet();
//    }
//
//    @Override
//    public boolean isForegroundSet() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isForegroundSet();
//    }
//
//    @Override
//    public boolean isLightweight() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isLightweight();
//    }
//
//    @Override
//    public boolean isMaximumSizeSet() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isMaximumSizeSet();
//    }
//
//    @Override
//    public boolean isMinimumSizeSet() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isMinimumSizeSet();
//    }
//
//    @Override
//    public boolean isPreferredSizeSet() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isPreferredSizeSet();
//    }
//
//    @Override
//    public boolean isShowing() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isShowing();
//    }
//
//    @Override
//    public boolean isValid() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isValid();
//    }
//
//    @Override
//    public boolean isVisible() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.isVisible();
//    }
//
//    @Override
//    public boolean keyDown(Event evt, int key) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.keyDown(evt, key);
//    }
//
//    @Override
//    public boolean keyUp(Event evt, int key) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.keyUp(evt, key);
//    }
//
//    @Override
//    public void list() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.list();
//    }
//
//    @Override
//    public void list(PrintStream out) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.list(out);
//    }
//
//    @Override
//    public void list(PrintWriter out) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.list(out);
//    }
//
//    @Override
//    public Point location() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.location();
//    }
//
//    @Override
//    public boolean lostFocus(Event evt, Object what) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.lostFocus(evt, what);
//    }
//
//    @Override
//    public boolean mouseDown(Event evt, int x, int y) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.mouseDown(evt, x, y);
//    }
//
//    @Override
//    public boolean mouseDrag(Event evt, int x, int y) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.mouseDrag(evt, x, y);
//    }
//
//    @Override
//    public boolean mouseEnter(Event evt, int x, int y) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.mouseEnter(evt, x, y);
//    }
//
//    @Override
//    public boolean mouseExit(Event evt, int x, int y) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.mouseExit(evt, x, y);
//    }
//
//    @Override
//    public boolean mouseMove(Event evt, int x, int y) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.mouseMove(evt, x, y);
//    }
//
//    @Override
//    public boolean mouseUp(Event evt, int x, int y) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.mouseUp(evt, x, y);
//    }
//
//    @Override
//    public void move(int x, int y) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.move(x, y);
//    }
//
//    @Override
//    public void nextFocus() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.nextFocus();
//    }
//
//    @Override
//    public void paintAll(Graphics g) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.paintAll(g);
//    }
//
//    @Override
//    public boolean postEvent(Event e) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.postEvent(e);
//    }
//
//    @Override
//    public boolean prepareImage(Image image, ImageObserver observer) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.prepareImage(image, observer);
//    }
//
//    @Override
//    public boolean prepareImage(Image image, int width, int height, ImageObserver observer) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.prepareImage(image, width, height, observer);
//    }
//
//    @Override
//    protected void processComponentEvent(ComponentEvent e) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.processComponentEvent(e);
//    }
//
//    @Override
//    protected void processFocusEvent(FocusEvent e) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.processFocusEvent(e);
//    }
//
//    @Override
//    protected void processHierarchyBoundsEvent(HierarchyEvent e) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.processHierarchyBoundsEvent(e);
//    }
//
//    @Override
//    protected void processHierarchyEvent(HierarchyEvent e) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.processHierarchyEvent(e);
//    }
//
//    @Override
//    protected void processInputMethodEvent(InputMethodEvent e) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.processInputMethodEvent(e);
//    }
//
//    @Override
//    protected void processMouseWheelEvent(MouseWheelEvent e) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.processMouseWheelEvent(e);
//    }
//
//    @Override
//    public void remove(MenuComponent popup) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.remove(popup);
//    }
//
//    @Override
//    public synchronized void removeComponentListener(ComponentListener l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.removeComponentListener(l);
//    }
//
//    @Override
//    public synchronized void removeFocusListener(FocusListener l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.removeFocusListener(l);
//    }
//
//    @Override
//    public void removeHierarchyBoundsListener(HierarchyBoundsListener l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.removeHierarchyBoundsListener(l);
//    }
//
//    @Override
//    public void removeHierarchyListener(HierarchyListener l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.removeHierarchyListener(l);
//    }
//
//    @Override
//    public synchronized void removeInputMethodListener(InputMethodListener l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.removeInputMethodListener(l);
//    }
//
//    @Override
//    public synchronized void removeKeyListener(KeyListener l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.removeKeyListener(l);
//    }
//
//    @Override
//    public synchronized void removeMouseListener(MouseListener l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.removeMouseListener(l);
//    }
//
//    @Override
//    public synchronized void removeMouseMotionListener(MouseMotionListener l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.removeMouseMotionListener(l);
//    }
//
//    @Override
//    public synchronized void removeMouseWheelListener(MouseWheelListener l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.removeMouseWheelListener(l);
//    }
//
//    @Override
//    public void removePropertyChangeListener(PropertyChangeListener listener) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.removePropertyChangeListener(listener);
//    }
//
//    @Override
//    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.removePropertyChangeListener(propertyName, listener);
//    }
//
//    @Override
//    public void repaint() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.repaint();
//    }
//
//    @Override
//    public void repaint(long tm) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.repaint(tm);
//    }
//
//    @Override
//    public void repaint(int x, int y, int width, int height) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.repaint(x, y, width, height);
//    }
//
//    @Override
//    public void resize(int width, int height) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.resize(width, height);
//    }
//
//    @Override
//    public void resize(Dimension d) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.resize(d);
//    }
//
//    @Override
//    public void setBounds(int x, int y, int width, int height) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setBounds(x, y, width, height);
//    }
//
//    @Override
//    public void setBounds(Rectangle r) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setBounds(r);
//    }
//
//    @Override
//    public void setComponentOrientation(ComponentOrientation o) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setComponentOrientation(o);
//    }
//
//    @Override
//    public void setCursor(Cursor cursor) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setCursor(cursor);
//    }
//
//    @Override
//    public synchronized void setDropTarget(DropTarget dt) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setDropTarget(dt);
//    }
//
//    @Override
//    public void setFocusTraversalKeysEnabled(boolean focusTraversalKeysEnabled) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setFocusTraversalKeysEnabled(focusTraversalKeysEnabled);
//    }
//
//    @Override
//    public void setFocusable(boolean focusable) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setFocusable(focusable);
//    }
//
//    @Override
//    public void setIgnoreRepaint(boolean ignoreRepaint) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setIgnoreRepaint(ignoreRepaint);
//    }
//
//    @Override
//    public void setLocale(Locale l) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setLocale(l);
//    }
//
//    @Override
//    public void setLocation(int x, int y) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setLocation(x, y);
//    }
//
//    @Override
//    public void setLocation(Point p) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setLocation(p);
//    }
//
//    @Override
//    public void setName(String name) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setName(name);
//    }
//
//    @Override
//    public void setSize(int width, int height) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setSize(width, height);
//    }
//
//    @Override
//    public void setSize(Dimension d) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.setSize(d);
//    }
//
//    @Override
//    public void show() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.show();
//    }
//
//    @Override
//    public void show(boolean b) { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.show(b);
//    }
//
//    @Override
//    public Dimension size() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.size();
//    }
//
//    @Override
//    public String toString() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        return super.toString();
//    }
//
//    @Override
//    public void transferFocus() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.transferFocus();
//    }
//
//    @Override
//    public void transferFocusBackward() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.transferFocusBackward();
//    }
//
//    @Override
//    public void transferFocusUpCycle() { System.out.println((new Throwable()).getStackTrace()[0].getMethodName());
//        super.transferFocusUpCycle();
//    }

}
