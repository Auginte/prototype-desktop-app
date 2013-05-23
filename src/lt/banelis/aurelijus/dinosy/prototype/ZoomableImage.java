package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import static lt.banelis.aurelijus.dinosy.prototype.helpers.RepresentationsHelper.createRepresentation;
import static lt.banelis.aurelijus.dinosy.prototype.helpers.RepresentationsHelper.getSelf;
import static lt.banelis.aurelijus.dinosy.prototype.helpers.RepresentationsHelper.setRepresentation;
import lt.banelis.aurelijus.dinosy.prototype.helpers.RunHelper;
import lt.banelis.aurelijus.dinosy.prototype.operations.Common;
import lt.banelis.aurelijus.dinosy.prototype.operations.HavingOperations;
import lt.banelis.aurelijus.dinosy.prototype.operations.Key;
import lt.banelis.aurelijus.dinosy.prototype.operations.KeyModifier;
import lt.banelis.aurelijus.dinosy.prototype.operations.Operation;
import lt.dinosy.datalib.Data;
import lt.dinosy.datalib.Data.Image;
import lt.dinosy.datalib.Representation;
import lt.dinosy.datalib.Settings;
import lt.dinosy.datalib.Source;

/**
 * Zoomable element, that store images
 *
 * @author Aurelijus Banelis
 */
public class ZoomableImage extends JLabel implements DataRepresentation, Zoomable, Selectable, HavingOperations, Cloneable {

    private Data.Image data;
//    private volatile BufferedImage originalImage = null;
    private double scaleFactor = 1;
    private boolean loadingFromNew = false;
    private boolean selectable = true;
    private boolean selected = false;
    private static String externalProgram = Settings.getInstance().getPaintingProgram();
    private static String externalFileManager = Settings.getInstance().getBrowserProgram();
    private int lastWidth = -1;
    private int lastHeight = -1;
    private transient BufferedImage cachedImage = null;
    private Optimization optimization = Optimization.part;
    private ImageLoader imageLoader = ImageLoader.getInstance();    //TODO: change using contruktor or etc
    private static boolean imageLoaded = false;
    private long lastModified = 0;
    private ZoomPanel panel;

    private enum Optimization {

        time,
        memory,
        part
    }

    public int getPriority() {
        return imageLoader.getPriority(this);
    }

    /*
     * Constructors
     */
    private ZoomableImage() {
        setForeground(Color.cyan);
        initFocusability();
    }

    public ZoomableImage(Data.Image data) {
        this(data, 1);
        loadingFromNew = true;
    }

    public ZoomableImage(Data.Image data, double scaleFactor) {
        this();
        iniciateData(data);
        this.scaleFactor = scaleFactor;
    }

    public ZoomableImage(String file, Source source) {
        this();
        loadingFromNew = true;
        iniciateData(new Data.Image(file, source));
    }

    public ZoomableImage(String file) {
        this(file, new Source.Event());
    }

    public void updateSize() {
        BufferedImage originalImage = imageLoader.getImage(this);
        if (originalImage != null) {
            if (ZoomableImage.this.getParent() instanceof ZoomPanel) {
                ZoomPanel panel = (ZoomPanel) ZoomableImage.this.getParent();
                ZoomableComponent zoomable = panel.getZoomableComponent(this);
                //TODO: by width
                long modified = imageLoader.getModified(this);
                if (loadingFromNew || modified != lastModified) {
                    scaleFactor = zoomable.reinisiateOriginalSize(originalImage.getWidth(), originalImage.getHeight());
                    repaint();
                    loadingFromNew = false;
                } else {
                    this.setSize(zoomable.getSize().width, zoomable.getSize().height);
                }
                lastModified = modified;
            } else {
                this.setSize(originalImage.getWidth(), originalImage.getHeight());
            }
        } else if (loadingFromNew) {
            this.setSize(ZoomableComponent.deafaultBounding.width, ZoomableComponent.deafaultBounding.height);
        }
    }

    /*
     * Representation
     */
    public void loadImage() {
        if (imageLoader.getState(this) != ImageLoader.State.loaded) {
            imageLoader.addImage(this, data.getData());
        }
        imageLoaded = true;
    }

    public String getCached() {
        if (data.getCached() != null) {
            return data.getCached();
        } else {
            return data.getData();
        }
    }

    public void save(BufferedImage image) {
        imageLoader.save(this, image);
    }

    @Override
    public void paint(Graphics g) {
        loadImage();
        BufferedImage originalImage = imageLoader.getImage(this);
        if (originalImage != null) {
            int newW = (int) (originalImage.getWidth() * scaleFactor);
            int newH = (int) (originalImage.getHeight() * scaleFactor);
            if (optimization == Optimization.time) {
                /* Faster but more memory */
                if (cachedImage == null || newW != lastWidth || newH != lastHeight) {
                    cachedImage = new BufferedImage(newW, newH, BufferedImage.TYPE_3BYTE_BGR);
                    Graphics2D graphics2D = cachedImage.createGraphics();
                    graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    graphics2D.drawImage(originalImage, 0, 0, newW, newH, null);
                }
                g.drawImage(cachedImage, 0, 0, null);
            } else if (optimization == Optimization.part) {
                Rectangle clipBounds = g.getClipBounds();
                cachedImage = new BufferedImage(clipBounds.width, clipBounds.height, BufferedImage.TYPE_3BYTE_BGR);
                Graphics2D graphics2D = cachedImage.createGraphics();
                graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                int sx1 = (int) (clipBounds.x / scaleFactor);
                int sy1 = (int) (clipBounds.y / scaleFactor);
                int sx2 = (int) ((clipBounds.width + clipBounds.x) / scaleFactor);
                int sy2 = (int) ((clipBounds.height + clipBounds.y) / scaleFactor);
                graphics2D.drawImage(originalImage, 0, 0, clipBounds.width, clipBounds.height, sx1, sy1, sx2, sy2, null);
                g.drawImage(cachedImage, clipBounds.x, clipBounds.y, null);
            } else {
                /* Slower but less memory */
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(originalImage, 0, 0, newW, newH, null);
            }

        } else {
            ZoomPanel.paintLoading(g, getSize().width, getSize().height);
            g.drawRect(1, 1, this.getWidth() - 2, this.getHeight() - 2);
//            imageLoader.drawPriority(this, g);
//            g.drawString("Loading: " + data.getData(), 0, 10);

        }
//        imageLoader.drawPriority(this, g);
//        setToolTipText(imageLoader.debugTooltip(this));
        paintSelected(g);
        paintFocus(g);
//        if (loadable != null) {
//            loadable.drawPriority(g);
//        }
    }

    public void zoomed(double z) {
        scaleFactor = z;
    }

    public double getZ() {
        return scaleFactor;
    }

    /*
     * Data container
     */
    public final void iniciateData(Data data) {
        this.data = (Image) data;
        updateSize();
    }

    public Data.Image getData() {
        return data;
    }

    public void updateData(ZoomableComponent component) {
        if (!inicializeRepresentation(component)) {
            setRepresentation((Representation.Element) getSelf(this), component);
        }
    }

    public boolean inicializeRepresentation(ZoomableComponent component) {
        if (getSelf(this) == null) {
            getData().addRepresentation(createRepresentation(data, component, this));
            return true;
        } else {
            return false;
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
     * Operations
     */
    private List<Operation> operations = Arrays.asList(
        (Operation) new Operation(getPanel(), "Edit externally", Common.getEditKey()) {
        @Override
        public void perform() {
            if (externalProgram != null) {
                Thread editingThread = new Thread() {
                    @Override
                    public void run() {
                        String externalEdditor = externalProgram;
                        String file = getData().getData();
                        if (file.contains("/sketch-")) {
                            externalEdditor = Settings.getInstance().getSketchingProgram();
                        }
                        try {
                            Process proc = Runtime.getRuntime().exec(new String[]{externalEdditor, file});
                            consumeAll(proc.getInputStream());
                            consumeAll(proc.getErrorStream());
                            proc.waitFor();
                            loadImage();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ZoomableImage.class.getName()).log(Level.SEVERE, "External image editing interupted", ex);
                        } catch (IOException ex) {
                            Logger.getLogger(ZoomableImage.class.getName()).log(Level.SEVERE, "Error launching external image editor: " + externalEdditor, ex);
                        }
                    }
                };
                editingThread.start();
            }
        }
    },
        new Operation(getPanel(), "Show file", new Key(KeyModifier.CTRL_ALT, KeyEvent.VK_F)) {
        @Override
        public void perform() {
            if (externalFileManager != null) {
                final Runnable viewing = RunHelper.runExternal(new String[]{externalFileManager, getData().getData()}, false);
                Thread updating = new Thread() {
                    @Override
                    public void run() {
                        viewing.run();
                        loadImage();
                    }
                };
                updating.start();
            }
        }
    },
        new Operation(getPanel(), "Update", new Key(KeyModifier.CTRL_SHIFT, KeyEvent.VK_U)) {
        @Override
        public void perform() {
            loadImage();
        }
    });

    public List<Operation> getOperations(ZoomPanel panel) {
        this.panel = panel;
        return operations;
    }

    private ZoomPanel getPanel() {
        if (panel == null) {
            panel = Common.getPanel(getParent());
        }
        return panel;
    }

    private static void consumeAll(InputStream stream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                if ("debug".equals("on")) {
                    System.err.println(line);
                }
            }
        } catch (IOException ex) {
        }
    }

    @Override
    public ZoomableImage clone() throws CloneNotSupportedException {
        //FIXME: update after clonning, cloning optimization
        ZoomableImage image = new ZoomableImage(data, scaleFactor);
        imageLoader.addImage(image, imageLoader.getImage(this));
        return image;
    }
}
