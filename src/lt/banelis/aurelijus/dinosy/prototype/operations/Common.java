package lt.banelis.aurelijus.dinosy.prototype.operations;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import lt.banelis.aurelijus.dinosy.prototype.helpers.VisualizationHelper;
import lt.banelis.aurelijus.dinosy.prototype.DataRepresentation;
import lt.banelis.aurelijus.dinosy.prototype.Selectable;
import lt.banelis.aurelijus.dinosy.prototype.ZoomPanel;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableComponent;
import lt.banelis.aurelijus.dinosy.prototype.ZoomableLabel;
import lt.banelis.aurelijus.dinosy.prototype.helpers.AddingHelper;
import lt.banelis.aurelijus.dinosy.prototype.helpers.ColorsHelper;
import lt.banelis.aurelijus.dinosy.prototype.helpers.RepresentationsHelper;
import lt.banelis.aurelijus.dinosy.prototype.helpers.RunHelper;
import lt.banelis.aurelijus.dinosy.prototype.helpers.SourceHelper;
import lt.banelis.aurelijus.dinosy.prototype.helpers.StorageHelper;
import lt.dinosy.datalib.Data;
import lt.dinosy.datalib.Representation;
import lt.dinosy.datalib.Settings;

/**
 * Class containing all common operations.
 *
 * @author Aurelijus Banelis
 */
public class Common implements HavingOperations {

    private VisualizationHelper visualization;
    private ZoomPanel panel;
    private StorageHelper storageHelper = new StorageHelper();
    private SourceHelper sourceHelper = new SourceHelper();
    private RunHelper runHelper = new RunHelper();
    private ColorsHelper colorsHelper = new ColorsHelper();
    private AddingHelper addingHelper = new AddingHelper(storageHelper, sourceHelper, runHelper);
    private static Key editKey = new Key(KeyModifier.NONE, KeyEvent.VK_ENTER);
    private KeyListener defaultKeyShortCuts;

    public Common(VisualizationHelper visualization, ZoomPanel panel) {
        //TODO: remove bidirectional dependency
        this.visualization = visualization;
        this.panel = panel;
        updateOperations(panel);
    }

    public StorageHelper getStorageHelper() {
        return storageHelper;
    }

    public RunHelper getRunHelper() {
        return runHelper;
    }

    public AddingHelper getAddingHelper() {
        return addingHelper;
    }

    public void performOperation(String operationName) {
        for (Operation operation : operations) {
            if (operation.getName().equalsIgnoreCase(operationName)) {
                operation.perform();
                break;
            }
        }
    }

    //TODO: beter panel integration
    public List<Operation> getOperations(ZoomPanel panel) {
        if (panel != null) {
            for (Operation operation : operations) {
                operation.setPanel(panel);
            }
        }
        return operations;
    }

    public final void updateOperations(ZoomPanel panel) {
        for (Operation operation : operations) {
            operation.setPanel(panel);
        }
    }

    public static Key getEditKey() {
        return editKey;
    }

    public static ZoomPanel getPanel(Component component) {
        if (component instanceof ZoomPanel) {
            return (ZoomPanel) component;
        } else {
            return null;
        }
    }

    public KeyListener getDefaultKeyShortCuts() {
        if (defaultKeyShortCuts == null) {
            defaultKeyShortCuts = Key.getDefaultKeyShortCuts(operations, panel);
        }
        return defaultKeyShortCuts;
    }

    public void sort() {
        Collections.sort(operations, new Comparator<Operation>() {
            public int compare(Operation o1, Operation o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    /*
     * utitlites
     */
    /**
     * Casts object to type provided or return null.
     */
    @SuppressWarnings("unchecked")
    private static <T> T cast(Object object, Class<T> type) {
        if (type.isInstance(object)) {
            return (T) object;
        } else {
            return null;
        }
    }
    /*
     * List of commonly used functions of ZoomPanel and its elements
     */
    private List<Operation> operations = Arrays.asList(
        new PanelOperation(panel, "Zoom in", new Key(KeyModifier.CTRL, KeyEvent.VK_PLUS), new Key(KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_EQUALS), new Key(KeyEvent.CTRL_DOWN_MASK, KeyEvent.VK_E)) {
        @Override
        public void perform(ZoomPanel panel) {
            panel.zoom(1.1);
        }
    },
        new PanelOperation(panel, "Zoom out", new Key(KeyModifier.CTRL, KeyEvent.VK_MINUS), new Key(KeyModifier.CTRL, KeyEvent.VK_Q)) {
        @Override
        public void perform(ZoomPanel panel) {
            panel.zoom(0.9);
        }
    },
        new PanelOperation(panel, "Go left", new Key(KeyModifier.CTRL, KeyEvent.VK_LEFT), new Key(KeyModifier.CTRL, KeyEvent.VK_A)) {
        @Override
        public void perform(ZoomPanel panel) {
            panel.translate(10, 0);
        }
    },
        new PanelOperation(panel, "Go right", new Key(KeyModifier.CTRL, KeyEvent.VK_RIGHT), new Key(KeyModifier.CTRL, KeyEvent.VK_D)) {
        @Override
        public void perform(ZoomPanel panel) {
            panel.translate(-10, 0);
        }
    },
        new PanelOperation(panel, "Go up", new Key(KeyModifier.CTRL, KeyEvent.VK_UP), new Key(KeyModifier.CTRL, KeyEvent.VK_W)) {
        @Override
        public void perform(ZoomPanel panel) {
            panel.translate(0, 10);
        }
    },
        new PanelOperation(panel, "Go down", new Key(KeyModifier.CTRL, KeyEvent.VK_DOWN), new Key(KeyModifier.CTRL, KeyEvent.VK_S)) {
        @Override
        public void perform(ZoomPanel panel) {
            panel.translate(0, -10);
        }
    },
        new PanelOperation(panel, "Save", new Key(KeyModifier.CTRL_SHIFT, KeyEvent.VK_S)) {
        @Override
        protected void perform(ZoomPanel panel) {
            storageHelper.save(panel, storageHelper.getProgress());
        }
    },
        new PanelOperation(panel, "Save as ...", new Key(KeyModifier.CTRL_ALT_SHIFT, KeyEvent.VK_S)) {
        @Override
        protected void perform(ZoomPanel panel) {
            storageHelper.saveAs(panel);
        }
    },
        new PanelOperation(panel, "Open", new Key(KeyModifier.CTRL_ALT_SHIFT, KeyEvent.VK_O)) {
        @Override
        protected void perform(ZoomPanel panel) {
            storageHelper.open(panel);
        }
    },
        new PanelOperation(panel, "Select all / none", new Key(KeyModifier.CTRL_SHIFT, KeyEvent.VK_A)) {
        @Override
        protected void perform(ZoomPanel panel) {
            boolean deselect = false;
            for (Component component : panel.getComponents()) {
                if (component instanceof Selectable && ((Selectable) component).isSelectable() && ((Selectable) component).isSelected()) {
                    deselect = true;
                    break;
                }
            }
            visualization.selectAll(panel, deselect);
        }
    },
        new PanelOperation(panel, "Delete all", new Key(KeyModifier.CTRL_SHIFT, KeyEvent.VK_DELETE, true)) {
        @Override
        public void perform(ZoomPanel panel) {
            panel.removeAll();
            panel.repaint();
        }
    },
        new AddOperation(panel, "text", new Key(KeyModifier.CTRL, KeyEvent.VK_SPACE, true)) {
        @Override
        public void perform(ZoomPanel panel) {
            addingHelper.addText("", panel, panel.getWidth() / 2, panel.getHeight() / 2, 100, 20, true);
        }
    },
        new AddOperation(panel, "image", new Key(KeyModifier.CTRL, KeyEvent.VK_I, true)) {
        @Override
        public void perform(ZoomPanel panel) {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (jfc.showOpenDialog(panel.getParent()) == JFileChooser.APPROVE_OPTION) {
                addingHelper.addImage(jfc.getSelectedFile().getPath(), panel, null, null);
            }
        }
    },
        new AddOperation(panel, "screenShot", new Key(KeyModifier.CTRL, KeyEvent.VK_PRINTSCREEN, true)) {
        @Override
        public void perform(ZoomPanel panel) {
            addingHelper.addScreenShot(panel, 0, Settings.getInstance().getDateCacheDirecotry());
        }
    },
        new AddOperation(panel, "sketch", new Key(KeyModifier.CTRL_ALT, KeyEvent.VK_K, true)) {
        @Override
        protected void perform(ZoomPanel panel) {
        }
    },
        new FocusedOperation(panel, "Add text near", new Key(KeyModifier.SHIFT, KeyEvent.VK_D, true)) {
        @Override
        public void perform(ZoomableComponent focused, ZoomPanel panel) {
            ZoomableLabel label = addingHelper.addText("", panel, (int) focused.getLocation().getX(), (int) (focused.getLocation().getY() + focused.getSize().getHeight()), 100, (int) focused.getSize().getHeight(), true);
            label.setForeground(focused.getComponent().getForeground());
            label.setBackground(focused.getComponent().getBackground());
            if (focused.getComponent() instanceof JComponent) {
                JComponent jComponent = (JComponent) focused.getComponent();
                label.setOpaque(jComponent.isOpaque());
            }
        }
    },
        new SelectionOperation(panel, "Zoom with element in", new Key(KeyModifier.ALT, KeyEvent.VK_PLUS), new Key(KeyModifier.ALT, KeyEvent.VK_EQUALS), new Key(KeyModifier.ALT, KeyEvent.VK_E)) {
        @Override
        protected void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
            visualization.zoomWithSelected(selected, panel, 1.1);
        }
    },
        new SelectionOperation(panel, "Zoom with element out", new Key(KeyModifier.ALT, KeyEvent.VK_MINUS), new Key(KeyModifier.ALT, KeyEvent.VK_Q)) {
        @Override
        protected void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
            visualization.zoomWithSelected(selected, panel, 0.9);
        }
    },
        new SelectionOperation(panel, "Go with elements left", new Key(KeyModifier.ALT, KeyEvent.VK_LEFT), new Key(KeyModifier.ALT, KeyEvent.VK_A)) {
        @Override
        public void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
            visualization.translateWithSelected(selected, panel, 10, 0);
        }
    },
        new SelectionOperation(panel, "Go with elements right", new Key(KeyModifier.ALT, KeyEvent.VK_RIGHT), new Key(KeyModifier.ALT, KeyEvent.VK_D)) {
        @Override
        public void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
            visualization.translateWithSelected(selected, panel, -10, 0);
        }
    },
        new SelectionOperation(panel, "Go with elements up", new Key(KeyModifier.ALT, KeyEvent.VK_UP), new Key(KeyModifier.ALT, KeyEvent.VK_W)) {
        @Override
        public void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
            visualization.translateWithSelected(selected, panel, 0, 10);
        }
    },
        new SelectionOperation(panel, "Go with elements down", new Key(KeyModifier.ALT, KeyEvent.VK_DOWN), new Key(KeyModifier.ALT, KeyEvent.VK_S)) {
        @Override
        public void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
            visualization.translateWithSelected(selected, panel, 0, -10);
        }
    },
        new SelectionOperation(panel, "Delete element", new Key(KeyModifier.NONE, KeyEvent.VK_DELETE, true)) {
        @Override
        public void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
            for (ZoomableComponent zoomableComponent : selected) {
                Representation representation = RepresentationsHelper.getRepresentation(zoomableComponent);
                if (representation != null) {
                    Data data = ((DataRepresentation) zoomableComponent.getComponent()).getData();
                    data.removeRepresentation(representation);
                }
                panel.remove(zoomableComponent.getComponent());
            }
            panel.repaint();
        }
    },
        new SelectionOperation(panel, "Export to HTML", new Key(KeyModifier.NONE, KeyEvent.VK_F12, true)) {
        @Override
        public void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (jfc.showSaveDialog(panel.getParent()) == JFileChooser.APPROVE_OPTION) {
                try {
                    storageHelper.exportToHtml(selected, jfc.getSelectedFile().getPath());
                } catch (IOException ex) {
                    Logger.getLogger(VisualizationHelper.class.getName()).log(Level.SEVERE, "Error saving file", ex);
                }
            }
        }
    },
        new SelectionOperation(panel, "Bring selected up", new Key(KeyModifier.ALT, KeyEvent.VK_PAGE_UP)) {
        @Override
        protected void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
            for (ZoomableComponent zoomableComponent : selected) {
                panel.setComponentZOrder(zoomableComponent.getComponent(), 0);
            }
            panel.repaint();
        }
    },
        new SelectionOperation(panel, "Bring selected down", new Key(KeyModifier.ALT, KeyEvent.VK_PAGE_DOWN)) {
        @Override
        protected void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
            for (ZoomableComponent zoomableComponent : selected) {
                panel.setComponentZOrder(zoomableComponent.getComponent(), panel.getComponentCount() - 1);
            }
            panel.repaint();
        }
    },
        new SelectionOperation(panel, "Arrange Liner-X", new Key(KeyModifier.CTRL, KeyEvent.VK_R, true)) {
        final static int MARGIN = 5;

        @Override
        protected void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
            int addX = 0;
            int firstX = (int) selected.get(0).getLocation().getX();
            for (ZoomableComponent component : selected) {
                component.setLocation(firstX + addX, component.getLocation().getY());
                addX += component.getSize().width + MARGIN;
            }
        }
    },
        new SelectionOperation(panel, "Clone selected", new Key(KeyModifier.CTRL_ALT, KeyEvent.VK_C, true)) {
        @Override
        protected void perform(List<ZoomableComponent> selected, ZoomPanel panel) {
            for (ZoomableComponent zoomableComponent : selected) {
                visualization.clone(zoomableComponent.getComponent());
            }
        }
    },
        new FocusedOperation(panel, "Select cloned elements", new Key(KeyModifier.CTRL_ALT_SHIFT, KeyEvent.VK_C, true)) {
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
        new SelectionOperation(panel, "Change foreground", new Key(KeyModifier.ALT, KeyEvent.VK_F, true)) {
        @Override
        protected void perform(final List<ZoomableComponent> selected, final ZoomPanel panel) {
            Thread colorChoosing = new Thread() {
                @Override
                public void run() {
                    colorsHelper.changeColor(selected, panel);
                }
            };
            colorChoosing.start();
        }
    },
        new SelectionOperation(panel, "Change background", new Key(KeyModifier.ALT, KeyEvent.VK_B, true)) {
        @Override
        protected void perform(final List<ZoomableComponent> selected, final ZoomPanel panel) {
            Thread colorChoosing = new Thread() {
                @Override
                public void run() {
                    colorsHelper.changeBackgroundColor(selected, panel);
                }
            };
            colorChoosing.start();
        }
    },
        new FocusedOperation(panel, "Copy to clipboard", new Key(KeyModifier.CTRL, KeyEvent.VK_C, true)) {
        @Override
        protected void perform(ZoomableComponent focused, ZoomPanel panel) {
            visualization.copyToClipboard(focused);
        }
    },
        new PanelOperation(panel, "Paste from clipboard", new Key(KeyModifier.CTRL, KeyEvent.VK_V, true)) {
        @Override
        protected void perform(ZoomPanel panel) {
            visualization.pasteFromClipboad(panel);
        }
    },
        new VisualGroupOperation(panel, "Select group", new Key(KeyModifier.CTRL, KeyEvent.VK_G, true)) {
        @Override
        protected void perform(List<Component> component, ZoomPanel panel) {
            boolean wasUnselected = false;
            for (Component element : component) {
                Selectable selectable = cast(element, Selectable.class);
                if (selectable != null && selectable.isSelectable()) {
                    if (!selectable.isSelected()) {
                        wasUnselected = true;
                    }
                    selectable.setSelected(true);
                }
            }
            if (!wasUnselected) {
                for (Component element : component) {
                    Selectable selectable = cast(element, Selectable.class);
                    if (selectable != null && selectable.isSelectable()) {
                        selectable.setSelected(false);
                    }
                }
            }
        }
    });
}
