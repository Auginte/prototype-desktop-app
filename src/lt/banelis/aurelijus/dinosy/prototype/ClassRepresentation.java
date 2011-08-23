package lt.banelis.aurelijus.dinosy.prototype;


import javax.swing.border.Border;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import lt.banelis.parser.Class;
import lt.banelis.parser.Function;
import lt.banelis.parser.NullNotAllowedException;
import lt.banelis.parser.Parameter;
import lt.banelis.parser.ParserException;
import lt.banelis.parser.Variable;
import lt.banelis.parser.VisibilityTypes;
import lt.banelis.parser.php.PhpFile;
import lt.banelis.parser.php.PhpProject;
import lt.dinosy.datalib.Data;
import lt.dinosy.datalib.Representation;
import lt.dinosy.datalib.Source;
import static lt.banelis.aurelijus.dinosy.prototype.BasicVisualization.getSelf;

/**
 * Representing PHP class in zoomable panel
 *
 * @author Aurelijus Banelis
 */
public class ClassRepresentation extends JPanel implements DataRepresentation, Cloneable, Selectable {
    private Data.Class data;
    private String maxText = "";
    private int maxLength = 0;
    private boolean resized = false;
    private Class classObject = null;
    private Color defaultBacground = new Color(16,16,16);
    private JPopupMenu contextMenu;
    private boolean clonning = false;
    private ZoomableLabel caption;
    private Border oldBorder;
    private boolean selectable = true;
    private boolean selected = false;

    public ClassRepresentation(Source source, lt.banelis.parser.Class classObject) {
        iniciateData(source, classObject);
        setBackground(defaultBacground);
        this.classObject = classObject;
        initClonning();
        initFocusability();
        setForeground(Color.CYAN);
    }

    public ClassRepresentation(Data.Class data) {
        iniciateData(data);
        setBackground(defaultBacground);
        initClonning();
        initFocusability();
        setForeground(Color.CYAN);
    }


    /*
     * Plain to data representation
     */

    private void iniciateData(Source source, lt.banelis.parser.Class classObject) {
        List<String> attributes = new LinkedList<String>();
        List<String> methods = new LinkedList<String>();
        //TODO: all visibility
        for (Variable variable : classObject.getVariables(VisibilityTypes.visiblePublic)) {
            attributes.add(attribte(variable));
        }
        for (Variable variable : classObject.getVariables(VisibilityTypes.visibleProtected)) {
            attributes.add(attribte(variable));
        }
        for (Variable variable : classObject.getVariables(VisibilityTypes.visibleInPackage)) {
            attributes.add(attribte(variable));
        }
        for (Variable variable : classObject.getVariables(VisibilityTypes.visiblePrivate)) {
            attributes.add(attribte(variable));
        }
        for (Function function : classObject.getFunctions(VisibilityTypes.visiblePublic)) {
            methods.add(method(function));
        }
        for (Function function : classObject.getFunctions(VisibilityTypes.visibleProtected)) {
            methods.add(method(function));
        }
        for (Function function : classObject.getFunctions(VisibilityTypes.visibleInPackage)) {
            methods.add(method(function));
        }
        for (Function function : classObject.getFunctions(VisibilityTypes.visiblePrivate)) {
            methods.add(method(function));
        }
        List<String> extend = new LinkedList<String>();
        if (classObject.whatExtends() != null) {
            extend.add(classObject.whatExtends().getName());
        }
        List<String> implement = new LinkedList<String>();
        for (Class class1 : classObject.whatImplements()) {
            implement.add(class1.getName());
        }
        data = new Data.Class(source, classObject.getName(), extend, implement, attributes, methods);
        inicializiseComponent();
    }


    static {
        EnumMap<VisibilityTypes, String> signs = new EnumMap<VisibilityTypes, String>(VisibilityTypes.class);
        signs.put(VisibilityTypes.visiblePublic, "+");
        signs.put(VisibilityTypes.visibleProtected, "#");
        signs.put(VisibilityTypes.visibleInPackage, "~");
        signs.put(VisibilityTypes.visiblePrivate, "-");
        visibilitySigns = signs;
    }
    private static final EnumMap<VisibilityTypes, String> visibilitySigns;

    private String attribte(Variable variable) {
        StringBuilder result = new StringBuilder(visibilitySigns.get(variable.getVisibility().getVisibilityType()));
        result.append(" ").append(variable.getName());
        if (variable.getType() != null) {
            result.append(": ").append(variable.getType());
        }
        if (variable.getValue() != null) {
            result.append(" = ").append(variable.getValue());
        }
        return result.toString();
    }

    private String method(Function function) {
        StringBuilder result = new StringBuilder(visibilitySigns.get(function.getVisibility().getVisibilityType()));
        result.append(" ").append(function.getName());
        int i = 0;
        result.append("(");
        final int n = function.getParameters().size();
        for (Parameter parameter : function.getParameters()) {
            result.append(parameter.getName());
            if (parameter.getType().length() > 0) {
                result.append(": ").append(parameter.getType());
            }
            if (parameter.getDefaultValue().length() > 0) {
                result.append("=").append(parameter.getDefaultValue());
            }
            if (i < n -1) {
                result.append(", ");
            }
            i++;
        }
        result.append(")");
        if (function.whatReturns() != null && function.whatReturns().getType() != null) {
            result.append(": ").append(function.whatReturns().getType());
        }
        return result.toString();
    }


    /*
     * Data to graphical Representation
     */

    private void inicializiseComponent() {
        this.add(caption());
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        for (String string : data.getAttributes()) {
            ZoomableLabel attribute = new ZoomableLabel(string);
            attribute.setForeground(Color.GRAY);
            this.add(attribute);
        }
        for (String string : data.getMethods()) {
            this.add(new ZoomableLabel(string));
        }
        configureLabels();
        initialSize();
    }

    private ZoomableLabel caption() {
        caption = new ZoomableLabel(data.getData());
        caption.setBackground(Color.GREEN);
        caption.setBorder(BorderFactory.createLineBorder(Color.blue));
        return caption;
    }

    public String whatExtends() {
        if (data.getExtending().size() > 0) {
            return data.getExtending().get(0);
        } else{
            return null;
        }
    }

    public List<String> whatImplements() {
        return data.getImplementing();
    }

    private void configureLabels() {
        for (Component component : getComponents()) {
            if (component instanceof ZoomableLabel) {
                ZoomableLabel zoomableLabel = (ZoomableLabel) component;
                zoomableLabel.setEditable(false);
            }
        }
    }


    /*
     * Size
     */

    private void initialSize() {
        int height = getComponent(0).getPreferredSize().height;
        int maxWidth = 0;
        int n = 0;
        for (Component component : getComponents()) {
            if (component instanceof ZoomableLabel) {
                int width =  ZoomableLabel.getWidth(height, ((ZoomableLabel) component).getText());
                if (width > maxWidth) {
                    maxWidth = width;
                }
                component.setLocation(0, n * height);
                n++;
            }
        }
        height *= n;
        super.setSize(maxWidth, height);
        for (Component component : getComponents()) {
            setSize(maxWidth, height);
        }
    }

    @Override
    public void setSize(int width, int height) {
        int newHeight = height / getComponentCount();
        int n = 0;

        double maxWith = 0;
        if (this.getHeight() > 0) {
            maxWith = this.getWidth() * height / this.getHeight() / 2;
        }
        for (Component component : getComponents()) {
            component.setLocation(0, newHeight * n);
            String text = ((ZoomableLabel) component).getText();
            int labelWidth = ZoomableLabel.getWidth(newHeight, text);
            if (labelWidth > maxWith) {
                maxWith = labelWidth;
            }
            component.setSize(labelWidth, newHeight);
            n++;
        }
        super.setSize((int) maxWith, height);
    }


    /*
     * Movable
     */

    @Override
    public synchronized void addMouseListener(MouseListener l) {
        super.addMouseListener(l);
        for (Component component : getComponents()) {
            component.addMouseListener(l);
        }
    }

    @Override
    public synchronized void addMouseMotionListener(MouseMotionListener l) {
        super.addMouseMotionListener(l);
        for (Component component : getComponents()) {
            component.addMouseMotionListener(l);
        }
    }


    /*
     * Data container
     */

    public final void iniciateData(Data data) {
        this.data = (Data.Class) data;
        this.removeAll();
        inicializiseComponent();
    }

    public Data.Class getData() {
        return data;
    }

    public String getClassName() {
        return data.getData();
    }

    public void updateData(ZoomableComponent component) {
        if (!inicializeRepresentation(component)) {
            BasicVisualization.setRepresentation((Representation.Element) getSelf(this), component);
        }
    }

    public boolean inicializeRepresentation(ZoomableComponent component) {
        if (getSelf(this) == null) {
            getData().addRepresentation(BasicVisualization.createRepresentation(data, component, this));
            return true;
        } else {
            return false;
        }
    }


    /*
     * Class object
     */

    public Class getClassObject() {
        if (classObject == null && getData().getSource() instanceof Source.Model) {
            Source.Model source = (Source.Model) getData().getSource();
            File file = new File(source.getSource());
            if (file.isFile()) {
                classObject = getFromFile(file);
            } else if (getData().getSource().getParent() instanceof Source.Project) {
                String projectPath = ((Source.Project) getData().getSource().getParent()).getAddress();
                file = new File(projectPath + getData().getSource().getSource());
                if (file.isFile()) {
                    classObject = getFromFile(file);
                } else if ((new File(projectPath).isDirectory())) {
                    PhpProject project = PhpProject.get(projectPath);
                    classObject = project.getClass(getData().getData());
                }
            }
        }
        return classObject;
    }

    private Class getFromFile(File file) {
        /* Source model has full path */
        PhpFile phpFile = null;
        try {
            phpFile = new PhpFile(file.getPath());
            phpFile.parse();
        } catch (NullNotAllowedException ex) {
            phpFile = null;
        } catch (ParserException ex) {
            phpFile = null;
        }
        if (phpFile != null) {
            return phpFile.getClass(getData().getData());
        } else {
            return null;
        }
    }


    /*
     * Clonning
     */

    public void setClonning(boolean clonning) {
        this.clonning = clonning;
    }

    public boolean isClonning() {
        return clonning;
    }

    private void initClonning() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (clonning && e.getButton() == MouseEvent.BUTTON3) {
                    if (contextMenu == null) {
                        initContextMenu();
                    }
                    contextMenu.show(ClassRepresentation.this, e.getX(), e.getY());
                }
            }
        });

    }

    private void initContextMenu() {
        contextMenu = new JPopupMenu();
        JMenuItem clone = new JMenuItem("Clone");
        clone.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    getParent().add(ClassRepresentation.this.clone());
                } catch (CloneNotSupportedException ex) {
                    Logger.getLogger(ClassRepresentation.class.getName()).log(Level.SEVERE, "ClassRepresentation should be clonned", ex);
                }
            }
        });
        contextMenu.add(clone);

        if (getData().getSource() instanceof Source.Model) {
            contextMenu.add(new JMenuItem(getData().getSource().getSource()));
        }
    }


    @Override
    protected ClassRepresentation clone() throws CloneNotSupportedException {
        ClassRepresentation clone = new ClassRepresentation(getData());
        clone.setLocation(getLocation().x, getLocation().y);
        clone.setSize(getWidth(), getHeight());
        clone.setClonning(clonning);
        return clone;
    }


    /*
     * Focusability
     */

    //TODO: implement using extend or sth
    private void initFocusability() {
        setFocusable(true);
        MouseListener focusClick = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }

        };
        addMouseListener(focusClick);
        for (Component component : getComponents()) {
            component.setFocusable(false);
            if (component instanceof Selectable) {
                ((Selectable) component).setSelectable(false);
            }
        }
        addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                oldBorder = caption.getBorder();
                caption.setBorder(BorderFactory.createLineBorder(getForeground()));
                repaint();
            }

            public void focusLost(FocusEvent e) {
                repaint();
                caption.setBorder(oldBorder);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintSelected(g);
        paintFocus(g);
    }

    private void paintFocus(Graphics g) {
        if (isFocusOwner()) {
            g.drawRect(0, 0, this.getSize().width - 1, this.getSize().height - 1);
        }
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
     * Misc
     */
    
    @Override
    public String toString() {
        return "{" + data.getData() + " at " + getLocation().x + "x" + getLocation().y + " size " + getSize().width + "x" + getSize().height + "}";
    }
}