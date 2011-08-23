package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;
import lt.dinosy.datalib.Source;
import lt.dinosy.datalib.Representation;
import lt.dinosy.datalib.Data;
import lt.dinosy.datalib.Data.Image;
import lt.dinosy.datalib.Firefox;
import static lt.banelis.aurelijus.dinosy.prototype.BasicVisualization.getSelf;

/**
 * Zoobale element ot store images
 *
 * @author Aurelijus Banelis
 */
public class ZoomableImage extends JLabel implements DataRepresentation, Zoomable, Selectable {
    private Data.Image data;
    private volatile BufferedImage originalImage = null;
    private double scaleFactor = 1;
    private Thread loading;
    private boolean loadingFromNew = false;
    private boolean selectable = true;
    private boolean selected = false;

    private ZoomableImage() {
        setForeground(Color.cyan);
        initFocusability();
    }

    public ZoomableImage(Data.Image data) {
        this();
        iniciateData(data);
    }

    public ZoomableImage(String file, Source source) {
        this();
        loadingFromNew = true;
        iniciateData(new Data.Image(file, source));
    }

    public ZoomableImage(String file) {
        this(file, new Source.Event());
    }

    private void updateSize() {
        if (originalImage != null) {
            this.setSize(originalImage.getWidth(), originalImage.getHeight());
            if (ZoomableImage.this.getParent() instanceof ZoomPanel) {
                ZoomPanel panel = (ZoomPanel) ZoomableImage.this.getParent();
                if (loadingFromNew) {
                    panel.getZoomableComponent(this).reinisiateOriginalSize();
                }
            }
        } else if (loadingFromNew) {
            this.setSize(100, 100);
        }
    }

    /*
     * Representation
     */

    public void loadImage() {
        loading = new Thread() {
            @Override
            public void run() {
                try {
                    File file = null;
                    if (data.getData().startsWith("http") && data.getCached() != null) {
                        //FIXME: use configuration (getters)
                        file = new File(Firefox.webDataCache + "/" + data.getCached());
                    } else {
                        file = new File(data.getData());
                    }
                    if (file.exists()) {
                        originalImage = ImageIO.read(file);
                    }
                    updateSize();
                } catch (IOException ex) {
                    System.err.println("EX " + ex);
                }
            }
        };
        loading.start();
    }

    @Override
    public void paint(Graphics g) {
        if (originalImage != null) {
            Graphics2D g2 = (Graphics2D)g;
            int newW = (int)(originalImage.getWidth() * scaleFactor);
            int newH = (int)(originalImage.getHeight() * scaleFactor);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(originalImage, 0, 0, newW, newH, null);
        } else {
            g.drawRect(1, 1, this.getWidth() - 2, this.getHeight() - 2);
            g.drawString("Loading: " + data.getData(), 0, 10);
        }
        paintSelected(g);
        paintFocus(g);
    }

    public void zoomed(double z) {
        scaleFactor = z;
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

}
