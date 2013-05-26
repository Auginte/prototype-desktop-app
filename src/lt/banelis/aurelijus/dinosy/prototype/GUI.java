/*
 * mainTest.java
 *
 * Created on Jun 22, 2011, 12:16:11 AM
 */
package lt.banelis.aurelijus.dinosy.prototype;

import lt.banelis.aurelijus.dinosy.prototype.helpers.VisualizationHelper;
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
import java.awt.image.BufferedImage;
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
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import lt.banelis.aurelijus.dinosy.prototype.helpers.AddingHelper;
import lt.banelis.aurelijus.dinosy.prototype.helpers.StorageHelper;
import lt.banelis.aurelijus.dinosy.prototype.operations.Common;
import lt.banelis.aurelijus.dinosy.prototype.operations.Progress;
import lt.dinosy.datalib.Data;
import lt.dinosy.datalib.Source;
import lt.dinosy.datalib.Source.Book;
import lt.dinosy.datalib.Source.Event;
import lt.dinosy.datalib.Source.Internet;
import lt.dinosy.datalib.Source.Okular;

/**
 * Graphical user interface.
 *
 * @author Aurelijus Banelis
 */
public class GUI extends javax.swing.JFrame {

    private VisualizationHelper visualization;
    private Common commonOperations;
    private StorageHelper storageHelper;
    private AddingHelper addingHelper;
    private JPopupMenu contextMenu;
    private Date sourceTypeDate = null;
    private DefaultComboBoxModel booksSourcesModel = null;
    private boolean booksModelChaning = false;
    private DefaultComboBoxModel booksNamesModel = null;
    private static final int AUTO_SAVE_INTERVAL = 60000;
    private Progress progress;

    /**
     * Creates new form mainTest
     */
    public GUI() {
        initComponents();
        this.progress = new Progress() {
            public void update(double percent, String operaion) {
                setTitle(Math.round(percent * 100) + "%: " + operaion);
            }
        };
        visualization = new VisualizationHelper(zoomPanel1, progress);
        commonOperations = new Common(visualization, zoomPanel1);
        storageHelper = commonOperations.getStorageHelper();
        addingHelper = commonOperations.getAddingHelper();
        visualization.initAll();
        initPopups();
        initSources();
        initKeyShortcuts(getContentPane());
        initMemoryMonitor();
        initAutoSave();
        initExmaples();
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
                if (autosaveCheckbox.isSelected() && storageHelper.getSavedTo() != null && !zoomPanel1.isLoading()) {
                    storageHelper.save(zoomPanel1, progress);
                    setTitle("Autosaved: " + getTime() + " " + storageHelper.getSavedTo());
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

                g.setColor(Color.GRAY);
                g.drawString("Image loading stats", getWidth() - 140, getHeight() - 10);

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
                g.fillRect(0, h += 5, tmw, 5);
                g.setColor(Color.YELLOW);
                g.fillRect(0, h += 5, lw, 5);

                g.setColor(Color.RED);
                h += 15;
                g.drawString("MEM: " + runtime.freeMemory() + "/" + runtime.maxMemory(), 0, h);
                g.drawString("Queue/Comp: " + ImageLoader.loadingQueue.size() + "/" + zoomPanel1.getComponentCount(), 200, h);
                g.drawString("Load/All: " + nLoaded + "/" + nAll, 350, h);
                g.drawString("remove/All: " + nUnloaded + "/" + nAll, 350, h += 15);
                g.drawString("Loader cycle: " + ImageLoader.cycleCount, 0, h);
                g.drawString("AVG WEAK: " + ImageLoader.getAveragePriority() + " " + imageLoader.getWeak(), 200, h);
                h += 5;

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
                }
            }
        }

        public void focusLost(FocusEvent event) {
        }
    };

    private void initPopups() {
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
                subComponent.addKeyListener(commonOperations.getDefaultKeyShortCuts());
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

    private void initExmaples() {
        exampleButton.setVisible(new File(getPathToExample()).exists());
    }

    private String getPathToExample() {
        return "../Examples/projecting.zip";
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JTabbedPane();
        buttonsPanel = new javax.swing.JPanel();
        exampleButton = new javax.swing.JButton();
        memoryMonitor = new javax.swing.JPanel();
        autosaveCheckbox = new javax.swing.JCheckBox();
        jButton2 = new javax.swing.JButton();
        sourcePanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        sourceEventDate = new javax.swing.JTextField();
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
        jButton3 = new javax.swing.JButton();
        topPanel2 = new javax.swing.JPanel();
        zoomPanel1 = new lt.banelis.aurelijus.dinosy.prototype.ZoomPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        topPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        topPanel.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        topPanel.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        buttonsPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        exampleButton.setText("Example");
        exampleButton.setToolTipText("Load DiNOSy project");
        exampleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exampleButtonActionPerformed(evt);
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
            .addGap(0, 70, Short.MAX_VALUE)
        );

        autosaveCheckbox.setSelected(true);
        autosaveCheckbox.setText("Autosave");

        jButton2.setText("Operations");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonsPanelLayout = new javax.swing.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addComponent(exampleButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 972, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(autosaveCheckbox))
            .addComponent(memoryMonitor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exampleButton)
                    .addComponent(autosaveCheckbox)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(memoryMonitor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        topPanel.addTab("Controlls", buttonsPanel);

        sourcePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jLabel3.setText("Source time:");

        sourceEventDate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sourceEventDateKeyTyped(evt);
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
            .addGap(0, 1059, Short.MAX_VALUE)
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
                .addComponent(sourceEventName, javax.swing.GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE)
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
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sourceinternetXpath))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(2, 2, 2)
                        .addComponent(sourceinternetTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 1010, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(sourceinternetUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(sourceinternetXpath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(sourceinternetTitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 14, Short.MAX_VALUE))
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
                    .addComponent(booksNamesCombo, javax.swing.GroupLayout.Alignment.LEADING, 0, 421, Short.MAX_VALUE)
                    .addComponent(sourceBookName, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE))
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
                    .addComponent(selectedBook, javax.swing.GroupLayout.DEFAULT_SIZE, 557, Short.MAX_VALUE))
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

        jButton3.setText("Copy-screen");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout sourcePanelLayout = new javax.swing.GroupLayout(sourcePanel);
        sourcePanel.setLayout(sourcePanelLayout);
        sourcePanelLayout.setHorizontalGroup(
            sourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sourcePanelLayout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(sourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(sourceEventDate, javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE))
                    .addComponent(jButton3))
                .addGap(1, 1, 1))
        );
        sourcePanelLayout.setVerticalGroup(
            sourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sourcePanelLayout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sourceEventDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(sourcePanelLayout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        topPanel.addTab("Source", sourcePanel);

        getContentPane().add(topPanel, java.awt.BorderLayout.PAGE_START);

        topPanel2.setLayout(new java.awt.BorderLayout());
        getContentPane().add(topPanel2, java.awt.BorderLayout.NORTH);

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
            .addGap(0, 1264, Short.MAX_VALUE)
        );
        zoomPanel1Layout.setVerticalGroup(
            zoomPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 793, Short.MAX_VALUE)
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

    private void exampleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exampleButtonActionPerformed
        zoomPanel1.removeAll();
        storageHelper.loadData(getPathToExample(), zoomPanel1);
    }//GEN-LAST:event_exampleButtonActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        visualization.getOperationsPopup().show(jButton2, 0, 0);
    }//GEN-LAST:event_jButton2ActionPerformed

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
            switch (sourceType) {
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
    }

    public void loadProject(String file) {
        storageHelper.loadData(file, zoomPanel1);
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
    VisualizationHelper.ClipboardSourceListener clipboardSourceListener = new VisualizationHelper.ClipboardSourceListener() {
        public void newOkular(Okular source, String file, BufferedImage image) {
            sourceOkularClipboard.setText("<HTML><B>Page:</B> " + source.getPage() + "<BR/>"
                                          + "<B>URL:</B> " + source.getSource() + "<BR/>"
                                          + "<B>Rect:</B> " + source.getPosition().l + "x" + source.getPosition().t + " | " + source.getPosition().r + "x" + source.getPosition().b + "<BR/>"
                                          + "<B>Date:</B> " + source.getDateSting()
                                          + "<HTML>");
            if (sourceOkularAuto.isSelected()) {
                addingHelper.addImage(source, file, image, GUI.this.zoomPanel1);
            }
        }

        public void newFirefox(Internet source) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void otherData(String data) {
            sourceOkularClipboard.setText("<HTML><B>Not okular data in clipboard:</B><BR/>"
                                          + data + "<BR/>"
                                          + "<B>Date</B>:" + Source.parseDate(new Date()) + "</HTML>");
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
        component.setSize(ZoomableComponent.deafaultBounding.width, ZoomableComponent.deafaultBounding.height);
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
                GUI mainTest = new GUI();
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
        return new File(new URI(GUI.class.getResource(name).toString()));
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox autosaveCheckbox;
    private javax.swing.JLabel booksLabel;
    private javax.swing.JComboBox booksNamesCombo;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton exampleButton;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
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
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel memoryMonitor;
    private javax.swing.JTextField selectedBook;
    private javax.swing.JTextField sourceBookIsbn;
    private javax.swing.JTextField sourceBookName;
    private javax.swing.JTextField sourceBookPage;
    private javax.swing.JTextField sourceEventDate;
    private javax.swing.JTextField sourceEventName;
    private javax.swing.JTextField sourceEventPlace;
    private javax.swing.JCheckBox sourceOkularAuto;
    private javax.swing.JLabel sourceOkularClipboard;
    private javax.swing.JPanel sourcePanel;
    private javax.swing.JTextField sourceinternetTitle;
    private javax.swing.JTextField sourceinternetUrl;
    private javax.swing.JTextField sourceinternetXpath;
    private javax.swing.JTabbedPane topPanel;
    private javax.swing.JPanel topPanel2;
    private lt.banelis.aurelijus.dinosy.prototype.ZoomPanel zoomPanel1;
    // End of variables declaration//GEN-END:variables
}
