package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import lt.dinosy.datalib.Firefox;

/**
 * Manages image loading, releasing and updating
 * 
 * @author Aurelijus Banelis
 */
public class ImageLoader {
    private static ImageLoader singleton = null;
    public static boolean usePriority = true;  //FIXME: normal implementation
    private static final int LOADING_UPDATE_INTERVAL = 1000;
    public static long cycleCount = 0;
    public static Stack<Loadable> loadingQueue = new Stack<Loadable>();
    //FIXME: normal lookup
    private static Loadable emptyLoadable = new Loadable("", "", null);
    
    
    public static ImageLoader getInstance() {
        if (singleton == null) {
            singleton = new ImageLoader();
        }
        return singleton;
    }

    public ImageLoader() {
        //TODO: static timer to dynamic timer
    }
    
    
    /**
     *  Use addImage(ZoomableImage object, String file, BufferedImage image)
     */
    public Loadable addImage(ZoomableImage object, String file) {
        for (Loadable loadable : loadingQueue) {
            if (loadable.container == object) {
                return loadable;
            }
        }
        Loadable loadable = new Loadable(file, file, object);
        loadingQueue.add(loadable);
        return loadable; 
    }

    public void addImage(ZoomableImage object, BufferedImage image) {
        Loadable loadable = addImage(object, object.getData().getData());
        loadable.save(image);
    }    
    
    public void remove(ZoomableImage object) {
        System.err.println("NOT implemented yet: remove(ZoomableImage object)");
    }
    
    public void save(final ZoomableImage object, BufferedImage myImage) {
        getLoadable(object).save(myImage);
    }
    
    public void update(String file) {
        System.out.println("NOT IMPLEMENTED: update(String file)");
    }
    
    public void setPriority(ZoomableImage object, int priority) {
        System.out.println("NOT IMPLEMENTED: setPriority(ZoomableImage object, int priority)");
    }
    
    public int getPriority(ZoomableImage object) {
        System.out.println("NOT IMPLEMENTED: getPriority(ZoomableImage object)");
        return 0;
    }
    
    public BufferedImage getImage(ZoomableImage object) {
        return getLoadable(object).image;
    }
    
    public State getState(ZoomableImage object) {
        return getLoadable(object).state;
    }
    
    public boolean isWeak(ZoomableImage object) {
        return getLoadable(object).isWeak();
    }    
   
    public String debugTooltip(ZoomableImage object) {
        return getLoadable(object).debugTooltip();
    }
    
    public String getLoadingText(ZoomableImage object) {
        return "Loading: " + getPriority(object);
    }

    public long getModified(ZoomableImage object) {
        File file = new File(getLoadable(object).path);
        if (file.exists()) {
            return file.lastModified();
        } else {
            return -1;
        }
    }   
    
    private Loadable getLoadable(ZoomableImage object) {
        Loadable result = emptyLoadable;
        for (Loadable loadable : loadingQueue) {
            if (loadable.container == object) {
                return loadable;
            }
        }
        return result;
    }
    
    public void drawPriority(ZoomableImage object, Graphics g) {
        getLoadable(object).drawPriority(g);
    }
    
    public int getLoadedCount() {
        int loaded = 0;
        for (Loadable loadable : loadingQueue) {
            switch (loadable.getState()) {
                case loaded:
                case selected:
                case toSave:
                    loaded++;
            }
        }
        return loaded;
    }
    
    public int getRemoveCount() {
        int toRemove = 0;
        for (Loadable loadable : loadingQueue) {
            if (loadable.getState() == State.removing) {
                toRemove++;
            }
        }
        return toRemove;
    }
    
    public int getAllCount() {
        return loadingQueue.size();
    }
    
    
    public int getWeak() {
        return Loadable.weakPriority();
    }
    
    public static enum State {
        empty,
        loaded,
        selected,
        toSave,
        removing
    }
    
    public void debugPriorities(Graphics g) {
        int min = 0;
        int max = 0;
        boolean first = false;
        int sum = 0;
        int n = 0;
        for (Loadable loadable : loadingQueue) {
            int priority = loadable.getPriority();
            if (first) {
                min = priority;
                max = priority;
            } else if (priority < min) {
                    min = priority;
            } else if (max < priority) {
                max = priority;
            }
            n++;
            sum += priority;
        }
        
        int width = max - min;
        if (!first && n > 1 && width > 0) {
            int gWidth = g.getClipBounds().width;
            int middle = g.getClipBounds().height / 2;
            for (Loadable loadable : loadingQueue) {
                int x = (int) ((loadable.getPriority() - min) / (double) width * gWidth);
                if (loadable.isWeak()) {
                    g.setColor(Color.RED);
                } else if (loadable.needLoading()) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.YELLOW);
                }
                g.drawRect(x, 2, 2, middle - 2);
            }
            
            int xAverage = (int) (((sum/n) - min) / (double) width * gWidth);
            int weak = Loadable.weakPriority();
            int xWeak = (int) (weak / (double) width * gWidth);
            g.setColor(Color.ORANGE);
            g.drawRect(xAverage - middle, 0, middle * 2, middle * 2);
            g.setColor(Color.PINK);
            g.drawRect(xWeak - (middle / 2), 0, middle, middle * 2);

            
            g.setColor(Color.CYAN);
            double average = Math.round(sum/n / 10) * 10;
            g.drawString(min + "", 0, middle * 2);
            g.drawString(max + "", gWidth - (digits(max) * 10), middle * 2);
            g.drawString("A: " + average, xAverage, middle * 2);
            g.drawString("W: " + weak, xWeak, middle * 2);
        }
    }
    
    private static int digits(int number) {
        int digits = 0;
        if (number < 0) {
            digits++;
            number = -number;
        }
        while (number > 0) {
            digits++;
            number /= 10;
        }
        return digits;
    }
    
    /*
     * Image loading optimization
     */
    private volatile static boolean loading;
    private static Runtime runtime = Runtime.getRuntime();
    //FIXME: make private
    
    /**
     * Element with different loading states.
     */
    public static class Loadable {
        public String path;
        public String cached;
        public ZoomableImage container;
        public int priority = 3;
        public BufferedImage image = null;
        private long lastModified = 0;
        
        private static volatile int prioritySum = 0;
        private static volatile int priorityCount = 0;
        
        private State state = State.empty;
        
        public State getState() {
            return state;
        }
        
        public void toSave(BufferedImage image, String file) {
            state = State.toSave;
            save(image);
        }
        
        public void drawPriority(Graphics g) {
            if (usePriority) {
//                updatePriority();
            }
            if (isWeak()) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.GREEN);
            }
            g.drawRect(0, 0, container.getSize().width, container.getSize().height);
            g.fillRect(1, 1, container.getSize().width, container.getSize().height / 8);
            g.setColor(Color.BLUE);
            g.drawString(debugTooltip(), 3, 9);
        }

        public String debugTooltip() {
            return priority + " | " + getAveratePriority() + " w " + isWeak() + " S " + loadingQueue.size();
        }
        
        /**
         * Update priority according to
         * * Selected or new elements get new priority
         * * Not visible elements get negative priority
         * * Bigger objects get bigger priority
         * 
         * @test None visible
         * @test Some visible
         * @test All visible different size
         * @test All visible equal size
        */
        private void updatePriority() {
            float percent = runtime.freeMemory() / (float) runtime.totalMemory();
            Container parent = container.getParent();
            if (container instanceof Selectable) {
                Selectable selectable = (Selectable) container;
                if (selectable.isSelected()) {
                    state = State.selected;
                } else if (state == State.selected) {
                    state = State.loaded;
                }
            }
            prioritySum -= priority;
            int vip = 0;
            if (state == State.toSave || state == State.selected) {
                vip = 5000;
            }
            if (parent != null && container != null && parent.getSize() != null && container.getSize() != null) {
                int x = container.getLocation().x;
                int y = container.getLocation().y;
                int w = container.getSize().width;
                int h = container.getSize().height;
                int cw = parent.getSize().width;
                int ch = parent.getSize().height;
                int right = x + w;
                int bottom = y + h;
                int size = (int) (Math.max(w, h) * percent);
                if (x > cw || y > ch || right < 0 || bottom < 0) {
                    if (size / 1000 > 1000) {
                        size = 1000;
                    }
                    priority = size - 1000;
                    if (right < 0) {
                        priority += right;
                    }
                    if (bottom < 0) {
                        priority += bottom;
                    }
                    if (x > cw) {
                        priority -= x - cw;
                    }
                    if (y > ch) {
                        priority -= y - ch;
                    }
                } else {
                    priority = size * 5;
                    priority -= (cw/2) - (x-w/2);
                    priority -= (ch/2) - (y-h/2);
                }               
            } else {
                priority = -501;
            }
            priority += vip;
            prioritySum += priority;
        }
        
        public int getPriority() {
            return priority;
        }
        
        /**
         * @return <code>true</code> - candidate to unload
         */
        public boolean isWeak() {
            if (state == State.toSave || state == State.selected) {
                return false;
            }           
            return priority < weakPriority();
        }
        
        protected static int weakPriority() {
            int multiplier = 1;
            float percent = runtime.freeMemory() / (float) runtime.totalMemory();
            if (percent < 40) {
                multiplier = 2;
            } else if (percent < 30) {
                multiplier = 4;
            } else if (percent < 20) {
                multiplier = 6;
            }
            //TODO: no cyclic dependency
            int addition = 0;
            if (priorityCount > 100) {
                addition = priorityCount - 100;
            }
            return getAveratePriority() * multiplier + addition;
        }

        public boolean needLoading() {
            return !isWeak() && priority > 0 && getState() != State.loaded;
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
        
        public boolean load() {
            long modified = (new File(path)).lastModified();
            if (modified == lastModified && image != null) {
                return true;
            } else if (image != null) {
                lastModified = modified;
            }

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
                if (file.exists() && runtime.freeMemory() > file.length() * 10) {
                    try {
                        image = ImageIO.read(file);
                        //TODO: check file size and RAM (InputStream) etc.
                        //Exception in thread "Thread-72" java.lang.OutOfMemoryError: Java heap space
                        //	at java.awt.image.DataBufferByte.<init>(DataBufferByte.java:92)
                        //	at java.awt.image.ComponentSampleModel.createDataBuffer(ComponentSampleModel.java:415)
                        //	at java.awt.image.Raster.createWritableRaster(Raster.java:944)
                        //	at javax.imageio.ImageTypeSpecifier.createBufferedImage(ImageTypeSpecifier.java:1186)
                        //	at javax.imageio.ImageReader.getDestination(ImageReader.java:2896)
                        //	at com.sun.imageio.plugins.jpeg.JPEGImageReader.readInternal(JPEGImageReader.java:1016)
                        //	at com.sun.imageio.plugins.jpeg.JPEGImageReader.read(JPEGImageReader.java:984)
                        //	at javax.imageio.ImageIO.read(ImageIO.java:1438)
                        //	at javax.imageio.ImageIO.read(ImageIO.java:1298)
                        //	at lt.banelis.aurelijus.dinosy.prototype.ImageLoader$Loadable.load(ImageLoader.java:258)
                        //	at lt.banelis.aurelijus.dinosy.prototype.ImageLoader$Loadable.access$400(ImageLoader.java:136)
                        //	at lt.banelis.aurelijus.dinosy.prototype.ImageLoader$3.run(ImageLoader.java:329)
                        container.updateSize();
                        state = State.loaded;
                    } catch (OutOfMemoryError e) {
                        image = null;
                        state = State.removing;
                        throw new OutOfMemoryError(file.getAbsolutePath());
                    }
                    return true;
                } else {
                    return false;
                }
            } catch (Exception ex) {
                System.err.println("Error loading image: " + file + " | " + ex);
            }
            return false;
        }
        
        public void unload() {
            image = null;
            state = State.removing;
        }
        
        
        public void save(BufferedImage myImage) {
            image = myImage;
            
            //TODO: saving in queue and lost buffer
            (new SavingThread(myImage, this, path)).start();
        }
    
        
        /** 
         * Writing image buffer to file.
         */
        private static class SavingThread extends Thread {
            private BufferedImage image;
            private Loadable loadable;
            private String path;

            public SavingThread(BufferedImage image, Loadable loadable, String path) {
                this.image = image;
                this.loadable = loadable;
                this.path = path;
                super.setPriority(Thread.MIN_PRIORITY);
            }
            
            
            @Override
            public synchronized void run() {
                try {
                    ImageIO.write(image, "png", new File(path));
                    image = null;
                    loadable.state = ImageLoader.State.loaded;
                } catch (IOException ex) {
                    Logger.getLogger(ZoomableImage.class.getName()).log(Level.SEVERE, "Error saving image " + path, ex);
                }
            }
        }
    }
    
    static {
        javax.swing.Timer timer = new javax.swing.Timer(LOADING_UPDATE_INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (!loading) {
                    loading = true;
                    perfomrLoding();
                }
            }
        });
        timer.start();
    }
    
    public static void updateLoadingStack() {
        //FIXME: remove empty function
    }
    
    private static void perfomrLoding() {
        int totalLoaded = 0;
        int loaded = 0;
        int removed = 0;
        int n1 = 0;
        int n2 = 0;
        /* Udpate priorities */
        for (Loadable loadable : loadingQueue) {
            if (loadable != null) {
                loadable.updatePriority();
            }
            n1++;
        }
        /* Load / unload */
        boolean memoryLeak = false;
        final int loadablePerCycle = 10;
        for (Loadable loadable : loadingQueue) {
            //FIXME: normal image removal: java.lang.NullPointerException
            if (loadable != null) {
                if (loadable.isWeak()) {
                    loadable.unload();
                    removed++;
                } else if (loadable.needLoading() && !memoryLeak) {
                    try {
                        loadable.load();
                        loaded++;
                        if (loaded > loadablePerCycle) {
                            memoryLeak = true;
                        }
                    } catch (OutOfMemoryError ex) {
                        System.err.println("Out of memory: " + ex);
                        memoryLeak = true;
                    }
                } else {
                    totalLoaded++;
                }
            }
            n2++;
        }
        cycleCount++;
        loading = false;
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
}
