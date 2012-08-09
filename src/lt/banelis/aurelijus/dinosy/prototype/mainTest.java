/*
 * mainTest.java
 *
 * Created on Jun 22, 2011, 12:16:11 AM
 */

package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import lt.banelis.aurelijus.dinosy.prototype.BasicVisualization.OkularThread;
import lt.banelis.parser.Class;
import lt.dinosy.datalib.Data;
import lt.dinosy.datalib.Source;
import lt.dinosy.datalib.Source.Book;
import lt.dinosy.datalib.Source.Event;
import lt.dinosy.datalib.Source.Internet;
import lt.dinosy.datalib.Source.Okular;

/**
 *
 * @author Aurelijus Banelis
 */
public class mainTest extends javax.swing.JFrame {
    private PhpUml phpUml;
    private BasicVisualization visualization;
    private JPopupMenu contextMenu;
    private Date sourceTypeDate = null;
    private DefaultComboBoxModel booksSourcesModel = null;
    private boolean booksModelChaning = false;
    private DefaultComboBoxModel booksNamesModel = null;
    private static final int AUTO_SAVE_INTERVAL = 60000;
    
    BasicVisualization.Progress progress = new BasicVisualization.Progress() {
        public void update(double percent, String operaion) {
            setTitle(Math.round(percent*100) + "%: " + operaion);
        }
    };
    
    /** Creates new form mainTest */
    public mainTest() {
        initComponents();
        phpUml = new PhpUml(zoomPanel1);
        visualization = new BasicVisualization(zoomPanel1, progress);
        visualization.initAll();
        initPopups();
        initSources();
        initKeyShortcuts(getContentPane());
        initMemoryMonitor();
        initAutoSave();
    }
    
    private MouseListener contextMenuListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent evt) {
            if (evt.getButton() == MouseEvent.BUTTON3) {
                zoomPanel1.lastFocusOwner = evt.getComponent();
                visualization.getOperationsPopup().show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }
    };
    
    private void initAutoSave() {
        javax.swing.Timer timer = new javax.swing.Timer(AUTO_SAVE_INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (autosaveCheckbox.isSelected() && visualization.getSavedTo() != null && !zoomPanel1.isLoading()) {
                    visualization.save();
                    setTitle("Autosaved: " + getTime());
                }
            }
        });
        timer.start();
    }
    
    private static String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(new Date());
    }
    
    private void initMemoryMonitor() {
        memoryMonitor.setLayout(new BorderLayout());
        final Runtime runtime = Runtime.getRuntime();
        final JPanel updateArea = new JPanel() {
            private int i;
            private final Color memoryColor = new Color(255, 88, 88);
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);
                
                g.setColor(memoryColor);
                int h = 0;
                int mw = (int) (g.getClipBounds().width * runtime.freeMemory() / runtime.maxMemory());
                int tmw = (int) (g.getClipBounds().width * runtime.freeMemory() / runtime.totalMemory());
//                setToolTipText("Free/MAX free/total loaded");
                ImageLoader imageLoader = ImageLoader.getInstance();
                int nLoaded = imageLoader.getLoadedCount();
                int nUnloaded = imageLoader.getRemoveCount();
                int nAll = imageLoader.getAllCount();
                int lw = (int) (g.getClipBounds().width * nLoaded / nAll);
                
                g.fillRect(0, 0, mw, 5);
                g.fillRect(0, h+=5, tmw, 5);
                g.setColor(Color.YELLOW);
                g.fillRect(0, h+=5, lw, 5);
                
                g.setColor(Color.RED);
                h += 15;
                g.drawString("MEM: " + runtime.freeMemory() + "/" + runtime.maxMemory(), 0, h);
                g.drawString("Queue/Comp: " + ImageLoader.loadingQueue.size() + "/" + zoomPanel1.getComponentCount(), 200, h);
                g.drawString("Load/All: " + nLoaded + "/" + nAll, 350, h);
                g.drawString("remove/All: " + nUnloaded + "/" + nAll, 350, h+=15);
                g.drawString("Loader cycle: " + ImageLoader.cycleCount, 0, h);
                g.drawString("AVG WEAK: " + ImageLoader.getAveragePriority() + " " + imageLoader.getWeak(), 200, h);
                h+=5;
                
                g.translate(0, h);
                g.setClip(0, 0, g.getClipBounds().width, g.getClipBounds().height - h);
                imageLoader.debugPriorities(g);
            }
        };
        updateArea.setPreferredSize(memoryMonitor.getSize());
        memoryMonitor.add(updateArea, BorderLayout.CENTER);
        javax.swing.Timer timer = new javax.swing.Timer(200, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateArea.repaint();
            }
        }); 
        timer.start();
    }
    
    private FocusListener focusListener = new FocusListener() {
        public void focusGained(FocusEvent event) {
            selectedBook.setText("");
            if (event.getComponent() instanceof DataRepresentation) {
                Source source = ((DataRepresentation) event.getComponent()).getData().getSource();
                if (source instanceof Source.Book) {
                    Source.Book book = (Source.Book) source;
                    selectedBook.setText(book.getSource() + ": " + book.getPage());
                } else if (source instanceof Source.Internet) {
                    Source.Internet internet = (Source.Internet) source;
                    String chaptersPattern = "(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)\\.?";
                    Matcher mather = Pattern.compile(chaptersPattern).matcher(internet.getXpaht());
                    if (mather.find()) {
                        ccna1.setText(mather.group(1));
                        ccna2.setText(mather.group(2));
                        ccna3.setText(mather.group(3));
                        ccna4.setText(mather.group(4));
                        sourceinternetUrl.setText(internet.getSource());
                        int cource = BasicVisualization.getCCNA(internet.getSource());
                        if (cource > 0 && cource <= ciscoCourse.getItemCount()) {
                            ciscoCourse.setSelectedIndex(cource - 1);
                        }
                        updateCiscoSource();
                    } else {
                        ccna1.setText("");
                        ccna2.setText("");
                        ccna3.setText("");
                        ccna4.setText("");
                    }
                }
            }
        }

        public void focusLost(FocusEvent event) { }
    };
        
    private void initPopups() {
        contextMenu = phpUml.getPopup();
        contextMenu.addSeparator();
        for (Component component : zoomPanel1.getComponents()) {
            component.addMouseListener(contextMenuListener);
        }
        zoomPanel1.addChangeListener(new ZoomPanel.ContentChangeAdapter() {
            @Override
            public void added(Component component) {
                component.addMouseListener(contextMenuListener);
                updateBooksSources();
                component.addFocusListener(focusListener);
            }
            @Override
            public void addedAll() {
                for (Component component : zoomPanel1.getComponents()) {
                    component.addMouseListener(contextMenuListener);
                    component.addFocusListener(focusListener);
                }
            }
        });
    }

    
    private void initKeyShortcuts(Container component) {
        for (Component subComponent : component.getComponents()) {
            if (subComponent.isFocusable()) {
                subComponent.addKeyListener(visualization.defaultKeyShortCuts);
                if (subComponent instanceof Container) {
                    initKeyShortcuts((Container) subComponent);
                }
            }
        }
        zoomPanel1.addChangeListener(new ZoomPanel.ContentChangeAdapter() {
            @Override
            public void added(Component component) {
                visualization.addKeyListener(component);
            }
        });
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        sourcePanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        sourceEventDate = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        sourceEventName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        sourceEventPlace = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        sourceinternetUrl = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        sourceinternetXpath = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        sourceinternetTitle = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        ciscoCourse = new javax.swing.JComboBox();
        ccna1 = new javax.swing.JTextField();
        ccna2 = new javax.swing.JTextField();
        ccna3 = new javax.swing.JTextField();
        ccna4 = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        sourceBookName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        sourceBookPage = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        sourceBookIsbn = new javax.swing.JTextField();
        booksNamesCombo = new javax.swing.JComboBox();
        booksLabel = new javax.swing.JLabel();
        selectedBook = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        sourceOkularAuto = new javax.swing.JCheckBox();
        sourceOkularClipboard = new javax.swing.JLabel();
        sourceLastUpdated = new javax.swing.JLabel();
        buttonsPanel = new javax.swing.JPanel();
        jButton9 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton12 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        memoryMonitor = new javax.swing.JPanel();
        autosaveCheckbox = new javax.swing.JCheckBox();
        zoomPanel1 = new lt.banelis.aurelijus.dinosy.prototype.ZoomPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        topPanel.setLayout(new java.awt.BorderLayout());

        sourcePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Source"));

        jLabel3.setText("Source time:");

        sourceEventDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sourceEventDateKeyTyped(evt);
            }
        });

        jButton1.setText("Update source");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTabbedPane1.setBorder(null);
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 578, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 76, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("None", jPanel1);

        jLabel1.setText("Name:");

        sourceEventName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sourceEventNameKeyTyped(evt);
            }
        });

        jLabel2.setText("Place:");

        sourceEventPlace.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sourceEventPlaceKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(4, 4, 4)
                .addComponent(sourceEventName, javax.swing.GroupLayout.DEFAULT_SIZE, 275, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sourceEventPlace, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(sourceEventName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sourceEventPlace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Event", jPanel2);

        jLabel7.setText("URL:");

        sourceinternetUrl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                sourceinternetUrlKeyReleased(evt);
            }
        });

        jLabel8.setText("XPath:");

        sourceinternetXpath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sourceinternetXpathActionPerformed(evt);
            }
        });
        sourceinternetXpath.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                sourceinternetXpathKeyReleased(evt);
            }
        });

        jLabel9.setText("Title:");

        sourceinternetTitle.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sourceinternetTitleKeyTyped(evt);
            }
        });

        jButton2.setText("Cisco");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        ciscoCourse.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "CCNA 1", "CCNA 2", "CCNA 3", "CCNA 4" }));
        ciscoCourse.setSelectedIndex(3);

        ccna1.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                ccna1MouseWheelMoved(evt);
            }
        });
        ccna1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ccna1ActionPerformed(evt);
            }
        });

        ccna2.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                ccna2MouseWheelMoved(evt);
            }
        });

        ccna3.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                ccna3MouseWheelMoved(evt);
            }
        });

        ccna4.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                ccna4MouseWheelMoved(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sourceinternetUrl, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ciscoCourse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(sourceinternetXpath, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addGap(2, 2, 2)
                        .addComponent(sourceinternetTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(ccna1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ccna2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ccna3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ccna4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(ciscoCourse, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ccna1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ccna2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ccna3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ccna4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(sourceinternetUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(sourceinternetXpath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(sourceinternetTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Internet", jPanel3);

        jLabel4.setText("Name:");

        sourceBookName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                sourceBookNameKeyReleased(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Ubuntu", 1, 15)); // NOI18N
        jLabel5.setText("Page:");

        sourceBookPage.setText("1");
        sourceBookPage.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                sourceBookPageMouseWheelMoved(evt);
            }
        });
        sourceBookPage.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                sourceBookPageKeyReleased(evt);
            }
        });

        jLabel6.setText("ISBN:");

        sourceBookIsbn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sourceBookIsbnKeyTyped(evt);
            }
        });

        booksNamesCombo.setEditable(true);
        booksNamesCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                booksNamesComboItemStateChanged(evt);
            }
        });

        booksLabel.setText("Used:");

        selectedBook.setEditable(false);
        selectedBook.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(booksLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(booksNamesCombo, javax.swing.GroupLayout.Alignment.LEADING, 0, 207, Short.MAX_VALUE)
                    .addComponent(sourceBookName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sourceBookPage, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sourceBookIsbn, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(selectedBook, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(booksNamesCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(booksLabel)
                    .addComponent(selectedBook, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(sourceBookName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sourceBookIsbn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(sourceBookPage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Book", jPanel5);

        jPanel4.setLayout(new java.awt.BorderLayout());

        sourceOkularAuto.setSelected(true);
        sourceOkularAuto.setText("<HTML>Automatically<BR/> add from<BR/> clipboard</HTML>");
        sourceOkularAuto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sourceOkularAutoActionPerformed(evt);
            }
        });
        jPanel4.add(sourceOkularAuto, java.awt.BorderLayout.LINE_START);

        sourceOkularClipboard.setFont(new java.awt.Font("Ubuntu", 0, 9)); // NOI18N
        sourceOkularClipboard.setText("No data in clipboard");
        sourceOkularClipboard.setToolTipText("Move mouse over to force to check clipboard");
        sourceOkularClipboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sourceOkularClipboardMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                sourceOkularClipboardMouseEntered(evt);
            }
        });
        jPanel4.add(sourceOkularClipboard, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Clipboard", jPanel4);

        sourceLastUpdated.setFont(new java.awt.Font("Ubuntu", 0, 10)); // NOI18N
        sourceLastUpdated.setText("Not updated yet");

        javax.swing.GroupLayout sourcePanelLayout = new javax.swing.GroupLayout(sourcePanel);
        sourcePanel.setLayout(sourcePanelLayout);
        sourcePanelLayout.setHorizontalGroup(
            sourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sourcePanelLayout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 582, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, Short.MAX_VALUE)
                    .addComponent(sourceLastUpdated, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(sourceEventDate, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE))))
        );
        sourcePanelLayout.setVerticalGroup(
            sourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sourcePanelLayout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sourceEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sourceLastUpdated)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1))
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        topPanel.add(sourcePanel, java.awt.BorderLayout.WEST);

        buttonsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Controll section"));

        jButton9.setText("Test DP");
        jButton9.setToolTipText("Load DiNOSy project");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jProgressBar1.setStringPainted(true);
        jProgressBar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jProgressBar1MouseClicked(evt);
            }
        });

        jButton7.setText("Load PHP project");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Test PHP");
        jButton8.setToolTipText("Load test PHP project");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jCheckBox1.setText("Generalizations");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jButton12.setText("Test CISCO");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton3.setText("Copy-screen");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        memoryMonitor.setBackground(new java.awt.Color(255, 217, 180));

        javax.swing.GroupLayout memoryMonitorLayout = new javax.swing.GroupLayout(memoryMonitor);
        memoryMonitor.setLayout(memoryMonitorLayout);
        memoryMonitorLayout.setHorizontalGroup(
            memoryMonitorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        memoryMonitorLayout.setVerticalGroup(
            memoryMonitorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 61, Short.MAX_VALUE)
        );

        autosaveCheckbox.setSelected(true);
        autosaveCheckbox.setText("Autosave");

        javax.swing.GroupLayout buttonsPanelLayout = new javax.swing.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(memoryMonitor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(buttonsPanelLayout.createSequentialGroup()
                        .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(buttonsPanelLayout.createSequentialGroup()
                                .addComponent(jButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton9))
                            .addComponent(jCheckBox1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(buttonsPanelLayout.createSequentialGroup()
                                .addComponent(jButton12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton7))
                            .addGroup(buttonsPanelLayout.createSequentialGroup()
                                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(autosaveCheckbox)))))
                .addGap(229, 229, 229))
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton9)
                    .addComponent(jButton12)
                    .addComponent(jButton8)
                    .addComponent(jButton7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox1)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(autosaveCheckbox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(memoryMonitor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        topPanel.add(buttonsPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(topPanel, java.awt.BorderLayout.NORTH);

        zoomPanel1.setBackground(java.awt.Color.black);
        zoomPanel1.setFocusCycleRoot(true);
        zoomPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                zoomPanel1MouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                zoomPanel1MouseEntered(evt);
            }
        });

        javax.swing.GroupLayout zoomPanel1Layout = new javax.swing.GroupLayout(zoomPanel1);
        zoomPanel1.setLayout(zoomPanel1Layout);
        zoomPanel1Layout.setHorizontalGroup(
            zoomPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1394, Short.MAX_VALUE)
        );
        zoomPanel1Layout.setVerticalGroup(
            zoomPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 761, Short.MAX_VALUE)
        );

        getContentPane().add(zoomPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void zoomPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_zoomPanel1MouseClicked
        if (evt.getClickCount() > 1) {
            if (visualization.defaultSource == null) {
                visualization.defaultSource = new Source.Event();
            }
            ZoomableLabel label = new ZoomableLabel(new Data.Plain("", visualization.defaultSource));
            label.switchEditable();
            ZoomableComponent component = zoomPanel1.addComponent(label);
            component.setLocation(evt.getX(), evt.getY());
            component.setSize(new Dimension(100, 20));
            label.requestFocusInWindow();
        } else if (evt.getButton() == MouseEvent.BUTTON3) {
            visualization.getOperationsPopup().show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_zoomPanel1MouseClicked

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        
    }//GEN-LAST:event_formKeyPressed

    private ClassRepresentation getRepresentation(Class classObject) {
        if (classObject == null) {
            return null;
        }
        for (Component component : zoomPanel1.getComponents()) {
            if (component instanceof ClassRepresentation) {
                ClassRepresentation cr = (ClassRepresentation) component;
                if (cr.getClassName().equals(classObject.getName())) {
                    return cr;
                }
            }
        }
        return null;
    }

private void sourceEventDateKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sourceEventDateKeyTyped
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            sourceTypeDate = format.parse(sourceEventDate.getText());
            sourceEventDate.setBackground(Color.white);
        } catch (ParseException ex) {
            sourceTypeDate = null;
            sourceEventDate.setBackground(Color.red);
        }
}//GEN-LAST:event_sourceEventDateKeyTyped

private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
    updateParentSource();
}//GEN-LAST:event_jTabbedPane1StateChanged

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    
}//GEN-LAST:event_jButton1ActionPerformed

private void sourceOkularAutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sourceOkularAutoActionPerformed
    
}//GEN-LAST:event_sourceOkularAutoActionPerformed

private void sourceOkularClipboardMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sourceOkularClipboardMouseEntered
    visualization.forceClipboardCheck();
}//GEN-LAST:event_sourceOkularClipboardMouseEntered

private void sourceOkularClipboardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sourceOkularClipboardMouseClicked
    if (evt.getClickCount() > 1 && visualization.getLastClipboardSource() instanceof Okular) {
        Okular source = (Okular) visualization.getLastClipboardSource();
        addImage(source.getCachedImage(), source, true);
    }
}//GEN-LAST:event_sourceOkularClipboardMouseClicked

private void zoomPanel1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_zoomPanel1MouseEntered
    updateParentSource();
}//GEN-LAST:event_zoomPanel1MouseEntered

    private void sourceinternetTitleKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sourceinternetTitleKeyTyped
        updateParentSource();
    }//GEN-LAST:event_sourceinternetTitleKeyTyped

    private void sourceEventNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sourceEventNameKeyTyped
        updateParentSource();
    }//GEN-LAST:event_sourceEventNameKeyTyped

    private void sourceEventPlaceKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sourceEventPlaceKeyTyped
        updateParentSource();
    }//GEN-LAST:event_sourceEventPlaceKeyTyped

    private void sourceBookIsbnKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sourceBookIsbnKeyTyped
        updateParentSource();
    }//GEN-LAST:event_sourceBookIsbnKeyTyped

private void sourceinternetXpathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sourceinternetXpathActionPerformed
    updateParentSource();
}//GEN-LAST:event_sourceinternetXpathActionPerformed

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    String courseCode = "";
    switch (ciscoCourse.getSelectedIndex()) {
        case 0:
            courseCode = "0600000000";
        break;
        case 1:
            courseCode = "0900000000";
        break;
        case 2:
            courseCode = "1300000000";
        break;
        default:
            courseCode = "1400000000";
    }
    sourceinternetUrl.setText("https://liepa.mif.vu.lt/CCNA/Exploration4English/theme/cheetah.html?cid=" + courseCode + "&l1=en&l2=none&chapter=");
    sourceinternetUrl.requestFocusInWindow();
}//GEN-LAST:event_jButton2ActionPerformed

private void sourceinternetXpathKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sourceinternetXpathKeyReleased
    updateParentSource();
}//GEN-LAST:event_sourceinternetXpathKeyReleased

private void sourceinternetUrlKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sourceinternetUrlKeyReleased
    updateParentSource();
}//GEN-LAST:event_sourceinternetUrlKeyReleased

private void booksNamesComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_booksNamesComboItemStateChanged
    if (!booksModelChaning && booksNamesModel != null && booksNamesModel.getSize() > 0 && booksNamesModel.getSelectedItem() != null) {
        sourceBookName.setText(booksNamesModel.getSelectedItem().toString());
        updateParentSource();
    }
}//GEN-LAST:event_booksNamesComboItemStateChanged

private void sourceBookNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sourceBookNameKeyReleased
    updateParentSource();
}//GEN-LAST:event_sourceBookNameKeyReleased

private void sourceBookPageKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sourceBookPageKeyReleased
    try {
        Integer.parseInt(sourceBookPage.getText());
        sourceBookPage.setBackground(Color.white);
        updateParentSource();
    } catch (NumberFormatException ex) {
        sourceBookPage.setBackground(Color.red);
    }
}//GEN-LAST:event_sourceBookPageKeyReleased

private void sourceBookPageMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_sourceBookPageMouseWheelMoved
    try {
        int value = Integer.parseInt(sourceBookPage.getText());
        value += -evt.getWheelRotation() * evt.getScrollAmount() / 3;
        setBookPage(value);
        updateParentSource();
        
    } catch (NumberFormatException ex) {
        sourceBookPage.setBackground(Color.red);
    }   
}//GEN-LAST:event_sourceBookPageMouseWheelMoved

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        visualization.execOperation("Add screenShot");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        visualization.loadData("/home/aurelijus/Dropbox/Dinosy/cisco.xml");
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        if (jCheckBox1.isSelected()) {
            phpUml.addGeneralizations();
        } else {
            zoomPanel1.removeConnections(Arrow.Generalization.class);
            zoomPanel1.repaint();
        }
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        phpUml.clear();
        phpUml.loadPhpProject("/home/aurelijus/TEST-project/", jProgressBar1);
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        phpUml.clear();
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            phpUml.loadPhpProject(fc.getSelectedFile().getPath(), jProgressBar1, new TimerTask() {
                @Override
                public void run() {
                    jProgressBar1.setString("Arranging");
                    phpUml.generalizationGrid.arrange();
                    jProgressBar1.setString("Ready");

                }
            });
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jProgressBar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jProgressBar1MouseClicked
        if (evt.getClickCount() > 1) {
            PhpUml.cancel();
        }
    }//GEN-LAST:event_jProgressBar1MouseClicked

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        zoomPanel1.removeAll();
        visualization.loadData("/home/aurelijus/Dropbox/Dinosy/projecting.xml");
    }//GEN-LAST:event_jButton9ActionPerformed

    private void ccna1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ccna1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ccna1ActionPerformed

    private void ccna1MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_ccna1MouseWheelMoved
        scroll(ccna1, -evt.getWheelRotation());
        updateCiscoSource();
    }//GEN-LAST:event_ccna1MouseWheelMoved

    private void ccna2MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_ccna2MouseWheelMoved
        scroll(ccna2, -evt.getWheelRotation());
        updateCiscoSource();
    }//GEN-LAST:event_ccna2MouseWheelMoved

    private void ccna3MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_ccna3MouseWheelMoved
        scroll(ccna3, -evt.getWheelRotation());
        updateCiscoSource();
    }//GEN-LAST:event_ccna3MouseWheelMoved

    private void ccna4MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_ccna4MouseWheelMoved
        scroll(ccna4, -evt.getWheelRotation());
        updateCiscoSource();
    }//GEN-LAST:event_ccna4MouseWheelMoved

    private static void scroll(JTextField field, int addition) {
        try {
            int current = Integer.valueOf(field.getText());
            if (current + addition > 0) {
                field.setText(Integer.toString(current + addition));
            }
        } catch (NumberFormatException ex) {
            field.setText("0");
        }
    }
    
    private void setBookPage(int value) {
        if (value < 1) {
            value = 1;
        }
        sourceBookPage.setBackground(Color.white);
        sourceBookPage.setText(Integer.toString(value));
    }
    
    private void updateCiscoSource() {
        sourceinternetXpath.setText(ccna1.getText() + "." + ccna2.getText() + "." + ccna3.getText() + "." + ccna4.getText());
    }

    private void initSources() {
        visualization.setClipboardSourceListener(clipboardSourceListener);
    }

    enum SourceType {
        None,
        Event,
        Internet,
//        Firefox,
        Book,
        Okular
        //TODO: Firefox and Okular integration
    }

    private void updateParentSource() {
        if (visualization != null) {
            SourceType sourceType = SourceType.values()[jTabbedPane1.getSelectedIndex()];
            assert sourceType.name().equals(jTabbedPane1.getTitleAt(jTabbedPane1.getSelectedIndex()));
            if (sourceTypeDate == null) {
                sourceTypeDate = new Date();
            }
            switch(sourceType) {
                case Event:
                    visualization.defaultSource = new Event(sourceTypeDate, sourceEventName.getText(), sourceEventPlace.getText());
                    break;
                case Internet:
                    visualization.defaultSource = new Internet(sourceTypeDate, sourceinternetUrl.getText(), sourceinternetXpath.getText(), sourceinternetTitle.getText(), null, null);
                    break;
                case Book:
                    visualization.defaultSource = new Book(sourceTypeDate, sourceBookName.getText(), Integer.parseInt(sourceBookPage.getText()), sourceBookIsbn.getText(), null);
                    break;
                default:
                    visualization.defaultSource = null;
            }
        }
        sourceLastUpdated.setText("Updated: " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
    }
    
    public void loadProject(String file) {
        visualization.loadData(file);
    }
    
    public Set<Source> getUniqueSources(java.lang.Class<? extends Source> restriction) {
        Set<Source> sources = new HashSet<Source>(zoomPanel1.getComponentCount() / 5);
        for (Component component : zoomPanel1.getComponents()) {
            if (component instanceof DataRepresentation) {
                Source source = ((DataRepresentation) component).getData().getSource();
                if (restriction == null || restriction.isInstance(source)) {
                    sources.add(((DataRepresentation) component).getData().getSource());
                }
            }
        }
        return sources;
    }
    
    private void updateBooksSources() {
//        /* Sources */
//        if (booksSourcesModel == null) {
//            booksSourcesModel = new DefaultComboBoxModel();
//            booksCombo.setModel(booksSourcesModel);
//        }
//        List<Source> sortedSources = new ArrayList<Source>(getUniqueSources(Source.Book.class));
//        Collections.sort(sortedSources, new Comparator<Source>() {
//            public int compare(Source s1, Source s2) {
//                return s1.getSource().compareTo(s2.getSource());
//            }
//        });
//        booksSourcesModel.removeAllElements();
//        for (Source source : sortedSources) {
//            Source.Book book = (Source.Book) source;
//            booksNamesCombo.addItem(book);
//        }

        /* Names */
        booksModelChaning = true;
        if (booksNamesModel == null) {
            booksNamesModel = new DefaultComboBoxModel();
            booksNamesCombo.setModel(booksNamesModel);
        }
        booksNamesModel.removeAllElements();
        HashSet<String> names = new HashSet<String>();
        for (Source source : getUniqueSources(Source.Book.class)) {
            Source.Book book = (Source.Book) source;
            if (book.getSource().length() > 0) {
                names.add(book.getSource());
            }
        }
        List<String> sortedNames = new ArrayList<String>(names);
        Collections.sort(sortedNames);
        for (String name : sortedNames) {
            booksNamesModel.addElement(name);
        }
        booksLabel.setText("Used (" + booksNamesModel.getSize() + "):");
        booksModelChaning = false;
    }
    
    BasicVisualization.ClipboardSourceListener clipboardSourceListener = new BasicVisualization.ClipboardSourceListener() {
        public void newOkular(Okular source, OkularThread clipboardToHdd) {
            sourceOkularClipboard.setText("<HTML><B>Page:</B> " + source.getPage() + "<BR/>" +
                    "<B>URL:</B> " + source.getSource() + "<BR/>" +
                    "<B>Rect:</B> " + source.getPosition().l + "x" + source.getPosition().t + " | " + source.getPosition().r + "x" + source.getPosition().b + "<BR/>" +
                    "<B>Date:</B> " + source.getDateSting() +
                    "<HTML>");
            if (sourceOkularAuto.isSelected()) {
                ZoomableImage image = addImage(source.getCachedImage(), source, false);
                clipboardToHdd.setImage(image);
                clipboardToHdd.setPriority(Thread.MIN_PRIORITY);
                clipboardToHdd.start();
            }
        }

        public void newFirefox(Internet source) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void otherData(String data) {
            sourceOkularClipboard.setText("<HTML><B>Not okular data in clipboard:</B><BR/>" +
                    data + "<BR/>" +
                    "<B>Date</B>:" + Source.parseDate(new Date()) + "</HTML>");
        }

        public void checking() {
            sourceOkularClipboard.setText("Checking clipboard...");
        }

        public void noNew() {
            if (visualization.getLastClipboardSource() instanceof Source.Okular) {
                sourceOkularClipboard.setText("Nothing new in clipboard: " + visualization.getLastClipboardSource().getSource());
            }
        }
    };
    
    private ZoomableImage addImage(String file, Source source, boolean loadImage) {
        ZoomableImage image = new ZoomableImage(file, source);
        ZoomableComponent component = zoomPanel1.addComponent(image);
        component.setLocation(zoomPanel1.getWidth() / 2, zoomPanel1.getHeight() / 2);
        component.setSize(600, 600);
        image.setSize(component.getSize().width, component.getSize().height);
        if (loadImage) {
            image.loadImage();
        }
        return image;
    }
    
    /**
    * @param args the command line arguments
    */
    public static void main(final String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            private Logger l = Logger.getLogger("");
            public void run() {
                try {
                    FileHandler handler = new FileHandler("log.xml");
                    l.addHandler(handler);
                    l.setLevel(Level.WARNING);
                } catch (Exception ex) {
                    System.err.println("Logging exception: " + ex);
                }
                Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    public void uncaughtException(Thread t, Throwable e) {
                        l.log(Level.WARNING, "EXCEPTION: {0} in {1}", new Object[]{e.getMessage(), t.toString()});
                        System.err.println();
                        e.printStackTrace(System.err);
                    }
                });
                mainTest mainTest = new mainTest();
                mainTest.setVisible(true);
                mainTest.repaint();
                if (args.length >= 1) {
                    if ((new File(args[0])).exists()) {
                        mainTest.loadProject(args[0]);
                    }
                }
            }
        });
    }

    static File openResource(String name) throws URISyntaxException {
        return new File(new URI(mainTest.class.getResource(name).toString()));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox autosaveCheckbox;
    private javax.swing.JLabel booksLabel;
    private javax.swing.JComboBox booksNamesCombo;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JTextField ccna1;
    private javax.swing.JTextField ccna2;
    private javax.swing.JTextField ccna3;
    private javax.swing.JTextField ccna4;
    private javax.swing.JComboBox ciscoCourse;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel memoryMonitor;
    private javax.swing.JTextField selectedBook;
    private javax.swing.JTextField sourceBookIsbn;
    private javax.swing.JTextField sourceBookName;
    private javax.swing.JTextField sourceBookPage;
    private javax.swing.JTextField sourceEventDate;
    private javax.swing.JTextField sourceEventName;
    private javax.swing.JTextField sourceEventPlace;
    private javax.swing.JLabel sourceLastUpdated;
    private javax.swing.JCheckBox sourceOkularAuto;
    private javax.swing.JLabel sourceOkularClipboard;
    private javax.swing.JPanel sourcePanel;
    private javax.swing.JTextField sourceinternetTitle;
    private javax.swing.JTextField sourceinternetUrl;
    private javax.swing.JTextField sourceinternetXpath;
    private javax.swing.JPanel topPanel;
    private lt.banelis.aurelijus.dinosy.prototype.ZoomPanel zoomPanel1;
    // End of variables declaration//GEN-END:variables

}
