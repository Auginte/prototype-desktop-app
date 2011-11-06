package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.Rectangle;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.swing.JLabel;
import lt.banelis.aurelijus.dinosy.prototype.BasicVisualization.Operation;
import lt.dinosy.datalib.Source;
import lt.dinosy.datalib.Representation;
import lt.dinosy.datalib.Data;
import lt.dinosy.datalib.Data.Image;
import lt.dinosy.datalib.Firefox;
import static lt.banelis.aurelijus.dinosy.prototype.BasicVisualization.getSelf;

/**
 * Zoomable element, that  store images
 *
 * @author Aurelijus Banelis
 */
public class ZoomableImage extends JLabel implements DataRepresentation, Zoomable, Selectable, HavingOperations {
    private Data.Image data;
    private volatile BufferedImage originalImage = null;
    private double scaleFactor = 1;
    private Thread loading;
    private boolean loadingFromNew = false;
    private boolean selectable = true;
    private boolean selected = false;
    private static String externalProgram = "/usr/bin/pinta";
    private int lastWidth = -1;
    private int lastHeight = -1;
    private transient BufferedImage cachedImage = null;
    private Optimization optimization = Optimization.part;
    
    private enum Optimization {
        time,
        memory,
        part
    }
    
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
                ZoomableComponent zoomable = panel.getZoomableComponent(this);
                if (loadingFromNew) {
                    zoomable.reinisiateOriginalSize();
                } else {
                    this.setSize(zoomable.getSize().width, zoomable.getSize().height);
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
                File file = null;
                try {
                    if (data.getData().startsWith("http") && data.getCached() != null) {
                        //FIXME: use configuration (getters)
                        file = new File(Firefox.webDataCache + "/" + data.getCached());
                    } else if (data.getData().startsWith("http")) {
                        String downloaded = Firefox.webDataCache + "/" + data.getCached();
                        Firefox.download(data.getData(), downloaded);
                        file = new File(downloaded);
                    } else {
                        file = new File(data.getData());
                    }
                    if (file.exists()) {
                        originalImage = ImageIO.read(file);
                    }
                    updateSize();
                } catch (IOException ex) {
                    System.err.println("Error loading image: " + file);
                }
            }
        };
        loading.start();
    }

    public String getCached() {
        if (data.getCached() != null) {
            return data.getCached();
        } else {
            return data.getData();
        }
    }
    
   
    @Override
    public void paint(Graphics g) {
        if (originalImage != null) {
            int newW = (int)(originalImage.getWidth() * scaleFactor);
            int newH = (int)(originalImage.getHeight() * scaleFactor);
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

    public List<Operation> getOperations(ZoomPanel panel) {
        return Arrays.asList((Operation) new Operation("Edit externally", BasicVisualization.editKey) {
            @Override
            public void perform() {
                Thread editingThread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            Process proc = Runtime.getRuntime().exec(new String[] {externalProgram, getData().getData()});
                            consumeAll(proc.getInputStream());
                            consumeAll(proc.getErrorStream());
                            proc.waitFor();
                            loadImage();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(ZoomableImage.class.getName()).log(Level.SEVERE, "External image editing interupted", ex);
                        } catch (IOException ex) {
                            Logger.getLogger(ZoomableImage.class.getName()).log(Level.SEVERE, "Error launching external image editor: " + externalProgram, ex);
                        }
                    }
                };
                editingThread.start();
            }
        },
        new Operation("Update", new BasicVisualization.Key(BasicVisualization.Key.Modifier.CTRL_SHIFT, KeyEvent.VK_U)) {
            @Override
            public void perform() {
                loadImage();
            }
        });
    }
   
    private static void consumeAll(InputStream stream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line = null;
        try {
            while ( (line = br.readLine()) != null) {
                if ("debug".equals("on")) {
                    System.out.println(line);
                }
            }
        } catch (IOException ex) {}
    }
}
