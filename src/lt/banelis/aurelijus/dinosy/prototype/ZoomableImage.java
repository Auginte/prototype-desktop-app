package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
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
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
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
public class ZoomableImage extends JLabel implements DataRepresentation, Zoomable, Selectable, HavingOperations, Cloneable {
    private Data.Image data;
    private volatile BufferedImage originalImage = null;
    private double scaleFactor = 1;
    private boolean loadingFromNew = false;
    private boolean selectable = true;
    private boolean selected = false;
    private static String externalProgram = "/usr/bin/pinta";
    private static String externalFileManager = "/usr/bin/nautilus";
    private int lastWidth = -1;
    private int lastHeight = -1;
    private transient BufferedImage cachedImage = null;
    private Optimization optimization = Optimization.part;
    private Loadable loadable;
    private static final int LOADING_UPDATE_INTERVAL = 500;
    
    private enum Optimization {
        time,
        memory,
        part
    }
    
    /*
     * Image loading optimization
     */
    private static Thread loading;
    private static Runtime runtime = Runtime.getRuntime();
    private static Stack<Loadable> loadingQueue = new Stack<Loadable>();
    private static class Loadable {
        public String path;
        public String cached;
        public ZoomableImage container;
        public boolean loaded = false;
        public int priority = 0;
        
        private static volatile int prioritySum = 0;
        private static volatile int priorityCount = 0;
        
        public void drawPriority(Graphics g) {
            updatePriority();
            if (isCritical()) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.GREEN);
            }
            g.drawRect(0, 0, container.getSize().width, container.getSize().height);
            g.fillRect(1, 1, container.getSize().width, container.getSize().height / 8);
            
        }
        
        private void updatePriority() {
            float percent = runtime.freeMemory() / (float) runtime.totalMemory();
            Container parent = container.getParent();
            int x = container.getLocation().x;
            int y = container.getLocation().y;
            int w = container.getSize().width;
            int h = container.getSize().height;
            int cw = parent.getSize().width;
            int ch = parent.getSize().height;
            prioritySum -= priority;
            priority = (int) (Math.max(w, h) * percent * 50);
            if (x + w < 0) {
                priority = 0;
            }
            if (y + h < 0) {
                priority = 0;
            }
            if (x > cw) {
                priority = 0;
            }
            if (y > ch) {
                priority = 0;
            }
            prioritySum += priority;
            checkCritical();
        }
        
        private void checkCritical() {
            if (isCritical()) {
                loaded = false;
                container.originalImage = null;
            } else if (!loaded) {
                ZoomableImage.updateLoadingStack();
            }
        }
        
        public boolean isCritical() {
            int average = getAveratePriority();
            int divider = 1;
            float percent = runtime.freeMemory() / (float) runtime.totalMemory();
            if (percent < 50) {
                divider = 2;
            } else if (percent < 40) {
                divider = 3;
            } else if (percent < 30) {
                divider = 4;
            } else if (percent < 25) {
                divider = 6;
            } else if (percent < 20) {
                divider = 8;
            }
            return priority < average / divider;
        }

        public static int getAveratePriority() {
            return (int) (prioritySum / (float) priorityCount);
        }
        
        public Loadable(String path, String cached, ZoomableImage container) {
            priorityCount++;
            this.path = path;
            this.cached = cached;
            this.container = container;
        }
    }
    
    public int getPriority() {
        if (loadable != null) {
            return loadable.priority;
        } else {
            return 1;
        }
    }
    
    public static int getAveragePriority() {
        return Loadable.getAveratePriority();
    }
    
    /**
     * Thread free stack
     */
    public static class Stack<T> implements Iterable<T> {
        private T element;
        private volatile Stack<T> next = null;
        
        public Stack() {
            this.element = null;
        }
        
        public Stack(T element) {
            this.element = element;
        }
        
        public synchronized void add(T element) {
            if (element != null && this.element != null) {
                Stack<T> stack = new Stack<T>(element);
                getLast().setNext(stack);
            } else if (this.element == null) {
                this.element = element;
            }
        }
        
        protected synchronized Stack<T> getNext() {
            return next;
        }
        
        protected synchronized void setNext(Stack<T> stack) {
            next = stack;
        }
        
        protected T getElement() {
            return element;
        }

        private synchronized Stack<T> getLast() {
            Stack<T> last = this;
            while (last.getNext() != null) {
                last = last.getNext();
            }
            return last;
        }
        
        public synchronized int size() {
            Stack<T> last = this;
            int size = 1;
            while (last.getNext() != null) {
                last = last.getNext();
                size++;
            }
            return size;
        }
        
        public synchronized void clear() {
            next = null;
            element = null;
        }
        
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private Stack<T> last = Stack.this;
                
                public boolean hasNext() {
                    return last != null && last.getElement() != null;
                }

                public T next() {
                    T element = last.getElement();
                    last = last.getNext();
                    return element;
                }

                public void remove() {
                    throw new UnsupportedOperationException("Removing elements not supported yet.");
                }
            };
        }
    }
    
    
    /*
     * Constructors
     */
    
    private ZoomableImage() {
        setForeground(Color.cyan);
        initFocusability();
    }

    public ZoomableImage(Data.Image data) {
        this();
        iniciateData(data);
    }

    public ZoomableImage(Data.Image data, BufferedImage originalImage, double scaleFactor) {
        this();
        this.data = data;
        this.originalImage = originalImage;
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

    private void updateSize() {
        if (originalImage != null) {
            if (ZoomableImage.this.getParent() instanceof ZoomPanel) {
                ZoomPanel panel = (ZoomPanel) ZoomableImage.this.getParent();
                ZoomableComponent zoomable = panel.getZoomableComponent(this);
                if (loadingFromNew) {
                    scaleFactor = zoomable.reinisiateOriginalSize(originalImage.getWidth(), originalImage.getHeight());
                    repaint();
                } else {
                    this.setSize(zoomable.getSize().width, zoomable.getSize().height);
                }
            } else {
                this.setSize(originalImage.getWidth(), originalImage.getHeight());
            }
        } else if (loadingFromNew) {
            this.setSize(100, 100);
        }
    }

    /*
     * Representation
     */

    public void loadImage() {
        ZoomableImage.loadImage(data.getData(), data.getCached(), this);
    }

    private static void loadImage(final String path, final String cached, final ZoomableImage container) {
        container.loadable = new Loadable(path, cached, container);
        loadingQueue.add(container.loadable);
        updateLoadingStack();
    }
    
    private static boolean loadingRepeater = false;
    
    private static void updateLoadingStack() {
        if (!loadingRepeater) {
            javax.swing.Timer timer = new javax.swing.Timer(LOADING_UPDATE_INTERVAL, new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    if (!loading.isAlive()) {
                        updateLoadingStack();
                    }
                }
            });
            timer.start();
            loadingRepeater = true;
        }
        if (loading == null || !loading.isAlive()) {
            loading = new Thread() {
                @Override
                public synchronized void run() {
                    int countAfter = -1;
                    int countBefore = -2;
                    while (countAfter != countBefore) {
                        countBefore = loadingQueue.size();
                        for (Loadable loadable : loadingQueue) {
                            float percent = runtime.freeMemory() / (float) runtime.totalMemory();
                            boolean toLoad = true;
                            if (!loadable.loaded && percent > 30) {
                                toLoad = true;
                            } else if (!loadable.loaded) {
                                loadable.updatePriority();
                                toLoad = loadable.priority > Loadable.getAveratePriority();
                            } else {
                                toLoad = false;
                            }
                            if (toLoad) {
                                loadable.loaded = load(loadable.path, loadable.cached, loadable.container);
                            } else if (percent < 40) {
                                loadable.checkCritical();
                            }
                        }
                        countAfter = loadingQueue.size();
//                        if (countBefore == countAfter) {
//                            resetQueue();
//                        }
                    }
                }
                
                private void resetQueue() {
                    loadingQueue.clear();
                }
                
                private boolean load(String path, String cached, ZoomableImage container) {
                    File file = null;
                    try {
                        if (path.startsWith("http") && cached != null) {
                            file = new File(Firefox.webDataCache + "/" + cached);
                        } else if (path.startsWith("http")) {
                            String downloaded = Firefox.webDataCache + "/" + cached;
                            Firefox.download(path, downloaded);
                            file = new File(downloaded);
                        } else {
                            file = new File(path);
                        }
                        if (file.exists()) {
                            container.setImage(ImageIO.read(file));
                            container.updateSize();
                            return true;
                        } else {
                            return false;
                        }
                    } catch (IOException ex) {
                        System.err.println("Error loading image: " + file);
                    }
                    return false;
                }
            };
            loading.setPriority(Thread.MIN_PRIORITY);
            loading.start();
        }
    }
    
    protected void setImage(BufferedImage image) {
        this.originalImage = image;
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
                        String externalEdditor = externalProgram;
                        String file = getData().getData();
                        if (file.contains("/sketch-")) {
                            externalEdditor = BasicVisualization.externalSketching;
                        }
                        try {
                            Process proc = Runtime.getRuntime().exec(new String[] {externalEdditor, file});
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
        },
        new Operation("Show file", new BasicVisualization.Key(BasicVisualization.Key.Modifier.CTRL_ALT, KeyEvent.VK_F)) {
            @Override
            public void perform() {
                final Runnable viewing = BasicVisualization.runExternal(new String[] {ZoomableImage.externalFileManager, getData().getData()}, false);
                Thread updating = new Thread() {
                    @Override
                    public void run() {
                       viewing.run();
                       loadImage();
                    }
                };
                updating.start();
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
                    System.err.println(line);
                }
            }
        } catch (IOException ex) {}
    }
    
    @Override
    protected ZoomableImage clone() throws CloneNotSupportedException {
        //FIXME: update after clonning
        return new ZoomableImage(data, originalImage, scaleFactor);
    }
}
