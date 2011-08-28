package lt.banelis.aurelijus.dinosy.prototype;

import java.util.List;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import lt.banelis.aurelijus.dinosy.prototype.BasicVisualization.Operation;
import lt.dinosy.datalib.Data;
import lt.dinosy.datalib.Representation;
import lt.dinosy.datalib.Source;
import static lt.banelis.aurelijus.dinosy.prototype.BasicVisualization.getSelf;

/**
 *
 * @author Aurelijus Banelis
 */
public class ZoomableLabel extends JPanel implements DataRepresentation, Editable, Selectable, Cloneable, Idea, HavingOperations {
    private transient JTextField editField = new JTextField("Some text");
    private Font resized = null;
    private int lastFontHeight = -1;
    private static JLabel sizeLable = new JLabel();
    //FIXME: bold, italic
    private static JLabel label1000 = null;
    private static JLabel label200 = null;
    private static Map<Integer, Font> cachedFonts = new HashMap<Integer, Font>();
    private boolean inicialised = false;
    private boolean editable = true;
    private Data.Plain data;
    private boolean selectable = true;
    private boolean selected = false;
    private boolean mainIdea = false;
    private boolean needIlustration = false;

    public ZoomableLabel() {
        this("Some text");
    }

    public ZoomableLabel(Data data) {
        this((String) data.getData());
        initFocusability();
        iniciateData(data);
    }

    /**
     * Use only when no Data object is created
     */
    ZoomableLabel(String text) {
        super.setPreferredSize(new Dimension(80, 30));
        iniciateData(new Data.Plain(text, new Source.Event()));
        initFocusability();
        constructLabel();
        prepareEditable();
    }


    /*
     * Label
     */

    private void constructLabel() {
        setOpaque(false);
        //FIXME: remove in production version
        setForeground(Color.cyan);
    }

    public final void setText(String text) {
        data.setData(text);
        updateWidth();
    }

    public String getText() {
        return data.getData();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!inicialised) {
            updateFont();
            updateWidth();
            inicialised = true;
        }
        paintIdeaSpecific(g);
        paintSelected(g);
        paintFocus(g);
        g.drawString(getText(), 0, (int) (getHeight() * 0.8));
    }
    

    /*
     * Editable
     */

    private void prepareEditable() {
        this.addMouseListener(editClick);
        editField.addMouseListener(editClick);
        editField.addKeyListener(editEnter);
    }

    private void constructEditable() {
        setLayout(new BorderLayout());
        editField.setVisible(true);
        add(editField, BorderLayout.CENTER);
    }

    private void destcuctEditable() {
        //TODO other inner elements
        //TODO: optimise
        this.removeAll();
    }

    private transient MouseListener editClick = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
          if (editable && e.getClickCount() > 1 && e.getButton() == MouseEvent.BUTTON1 && e.getID() == MouseEvent.MOUSE_CLICKED) {
                switchEditable();
            }
        }
    };

    private transient KeyListener editEnter = new KeyAdapter() {
        @Override
        public void keyTyped(KeyEvent e) {
            if (editable) {
                if (e.getKeyChar() == '\n') {
                    switchEditable();
                } else if (editField.getText().length() > 0) {
                    setText(editField.getText() + e.getKeyChar());
                    updateWidth();
                    ZoomableLabel.this.repaint();
                }
            }
        }
    };

    public void switchEditable() {
        if (isEditMode()) {
            setText(editField.getText());
            destcuctEditable();
            this.requestFocusInWindow();
        } else {
            constructEditable();
            editField.setText(getText());
            this.requestFocusInWindow();
        }
        repaint();
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isEditable() {
        return editable;
    }


    public boolean isEditMode() {
        return (this.getComponentCount() > 0);
    }

    @Override
    public boolean requestFocusInWindow() {
        if (isEditMode()) {
            return editField.requestFocusInWindow();
        } else {
            return super.requestFocusInWindow();
        }
    }



    /*
     * Zooming
     */

    @Override
    public void setSize(int width, int height) {
        setHeight(height);
        updateFont();
    }

    private void updateWidth() {
        setHeight(getHeight());
    }

    private void updateWidth(int width) {
        super.setSize(width, getHeight());
        editField.setSize(this.getSize());
    }

    private void setHeight(int height) {
        if (getText().length() > 0) {
            //TODO: more precise and still fast getting with
            super.setSize((int) (getWidth(height, getText()) * 0.8), height);
            editField.setSize(this.getSize());
        } else {
            super.setSize(6, height);
        }
    }

    public static int getWidth(int height, String text) {
        if (height < 1) {
            return 0;
        } else if (height < 500) {
            if (label200 == null) {
                label200 = new JLabel(text);
                label200.setFont(sizeLable.getFont().deriveFont(200.f));
            } else {
                label200.setText(text);
            }
            return (int) (label200.getPreferredSize().width * (height / 200.0));
        } else {
            if (label1000 == null) {
                label1000 = new JLabel(text);
                label1000.setFont(sizeLable.getFont().deriveFont(1000.f));
            } else {
                label1000.setText(text);
            }
        }
        return (int) (label1000.getPreferredSize().width * (height / 1000.0));
    }

    private void updateFont() {
        int height = (int) (getHeight() * 0.8);
        if (resized == null || lastFontHeight != height) {
            if (cachedFonts.containsKey(height)) {
                resized = cachedFonts.get(height);
            } else {
                resized = getFont().deriveFont((float) height);
                cachedFonts.put(height, resized);
            }
            setFont(resized);
            editField.setFont(resized);
            lastFontHeight = height;
        }
    }

    
    /*
     * Data container
     */

    public final void iniciateData(Data data) {
        this.data = (Data.Plain) data;
    }

    public Data getData() {
        return data;
    }

    public void updateData(ZoomableComponent component) {
        if (data == null) {
            data = new Data.Plain(getText(), new Source.Event());
            inicializeRepresentation(component);
        } else if (!inicializeRepresentation(component)) {
            BasicVisualization.setRepresentation((Representation.Element) getSelf(this), component);
        }
    }

    public boolean inicializeRepresentation(ZoomableComponent component) {
        if (getSelf(this) == null) {
            Representation representation = BasicVisualization.createRepresentation(data, component, this);
            getData().addRepresentation(representation);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        if (data != null) {
            return (String) data.getData() + " : " + super.toString();
        } else {
            return super.toString();
        }
    }

    /*
     * Focusability
     */

    //TODO: implement using extend or sth
    private void initFocusability() {
        setFocusable(true);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                    requestFocusInWindow();
                }

        });
        addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                repaint();
            }

            public void focusLost(FocusEvent e) {
                repaint();
            }
        });
    }

    private void paintFocus(Graphics g) {
        if (isFocusOwner()) {
            g.drawRect(0, 0, this.getSize().width - 1, this.getSize().height - 1);
        }
    }


    /*
     * Clone
     */

    @Override
    protected ZoomableLabel clone() throws CloneNotSupportedException {
        //FIXME: update after clonning
        ZoomableLabel clone = new ZoomableLabel(getData());
        clone.setLocation((int) getLocation().getX(), (int) getLocation().getY());
        clone.setSize((int) getSize().getWidth(), (int) getSize().getHeight());
        return clone;
    }

    
    /*
     * Selectable
     */

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public boolean isSelected() {
        return selected && selectable;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    public void paintSelected(Graphics g) {
        if (isSelected()) {
            Color oldColor = g.getColor();
            g.setColor(Selectable.selectionColor);
            g.drawRect(1, 1, this.getWidth() - 3, this.getHeight() - 3);
            g.setColor(oldColor);
        }
    }


    
    /*
     * Idea representation
     */    
    
    public boolean isMainIdea() {
        return mainIdea;
    }

    public void setMainIdea(boolean isMain) {
        this.mainIdea = isMain;
    }
    
    public boolean isNeedIlustracion() {
        return needIlustration;
    }

    public void setNeedIlustration(boolean needIlustration) {
        this.needIlustration = needIlustration;
    }
    
    private void paintIdeaSpecific(Graphics g) {
        Color oldColor = g.getColor();
        int arc = Math.min(this.getWidth(), this.getHeight()) / 4;
        if (isMainIdea()) {
            g.setColor(new Color(32, 128, 32));
            g.fillRoundRect(0, 0, this.getWidth()-1, this.getHeight()-1, arc, arc);
        }
        if (isNeedIlustracion()) {
            g.setColor(new Color(255, 32, 32));
            g.drawRoundRect(0, 0, this.getWidth()-1, this.getHeight()-1, arc, arc);
        }
        g.setColor(oldColor);
    }
    
    public List<BasicVisualization.Operation> getOperations(final ZoomPanel panel) {
        List<BasicVisualization.Operation> operations = Arrays.asList(
            new BasicVisualization.Operation("Edit element", BasicVisualization.editKey) {
                @Override
                public void perform() {
                    if (!ZoomableLabel.this.isEditMode()) {
                        ZoomableLabel.this.switchEditable();
                    }
                }
            },
           new BasicVisualization.Operation("Toggle need ilustration", new BasicVisualization.Key(BasicVisualization.Key.Modifier.CTRL_SHIFT, KeyEvent.VK_I)) {
                @Override
                public void perform() {
                    ZoomableLabel.this.setNeedIlustration(!ZoomableLabel.this.isNeedIlustracion());
                    ZoomableLabel.this.repaint();
                }
            },
            new BasicVisualization.Operation("Toggle main idea", new BasicVisualization.Key(BasicVisualization.Key.Modifier.CTRL_SHIFT, KeyEvent.VK_M)) {
                @Override
                public void perform() {
                    ZoomableLabel.this.setMainIdea(!ZoomableLabel.this.isMainIdea());
                    ZoomableLabel.this.repaint();
                }
            }   
        );
        return operations;
    }

}