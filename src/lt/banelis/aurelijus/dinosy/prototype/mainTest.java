/*
 * mainTest.java
 *
 * Created on Jun 22, 2011, 12:16:11 AM
 */

package lt.banelis.aurelijus.dinosy.prototype;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPopupMenu;
import lt.banelis.parser.Class;
import lt.dinosy.datalib.Firefox;

/**
 *
 * @author Aurelijus Banelis
 */
public class mainTest extends javax.swing.JFrame {
    private PhpUml phpUml;
    private BasicVisualization visualization;
    private JPopupMenu contextMenu;

    /** Creates new form mainTest */
    public mainTest() {
        initComponents();
        phpUml = new PhpUml(zoomPanel1);
        visualization = new BasicVisualization(zoomPanel1);
        visualization.initAll();
        initPopups();
        initKeyShortcuts(getContentPane());
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
            }
            @Override
            public void addedAll() {
                for (Component component : zoomPanel1.getComponents()) {
                    component.addMouseListener(contextMenuListener);
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

        zoomPanel1 = new lt.banelis.aurelijus.dinosy.prototype.ZoomPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        zoomableLabel1 = new lt.banelis.aurelijus.dinosy.prototype.ZoomableLabel();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jButton8 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        zoomPanel1.setBackground(java.awt.Color.black);
        zoomPanel1.setFocusCycleRoot(true);
        zoomPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                zoomPanel1MouseClicked(evt);
            }
        });

        jButton1.setText("Translate");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Reset");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("+");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("-");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jTextField1.setText("jTextField1");

        jButton5.setText("Daug elementu");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        zoomableLabel1.setText("Labas");

        jButton6.setText("Ištrinti visus");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout zoomPanel1Layout = new javax.swing.GroupLayout(zoomPanel1);
        zoomPanel1.setLayout(zoomPanel1Layout);
        zoomPanel1Layout.setHorizontalGroup(
            zoomPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(zoomPanel1Layout.createSequentialGroup()
                .addGroup(zoomPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(zoomPanel1Layout.createSequentialGroup()
                        .addGap(297, 297, 297)
                        .addComponent(jButton2))
                    .addGroup(zoomPanel1Layout.createSequentialGroup()
                        .addGap(138, 138, 138)
                        .addComponent(jButton4)))
                .addGap(464, 464, 464)
                .addGroup(zoomPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton6)
                    .addGroup(zoomPanel1Layout.createSequentialGroup()
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(zoomPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(zoomPanel1Layout.createSequentialGroup()
                                .addGap(134, 134, 134)
                                .addComponent(jButton1))
                            .addGroup(zoomPanel1Layout.createSequentialGroup()
                                .addGap(117, 117, 117)
                                .addComponent(jButton3)))))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, zoomPanel1Layout.createSequentialGroup()
                .addGap(123, 123, 123)
                .addComponent(zoomableLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 549, Short.MAX_VALUE)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(384, 384, 384))
        );
        zoomPanel1Layout.setVerticalGroup(
            zoomPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, zoomPanel1Layout.createSequentialGroup()
                .addGroup(zoomPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(zoomPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton4))
                    .addGroup(zoomPanel1Layout.createSequentialGroup()
                        .addGap(158, 158, 158)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(zoomPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addComponent(jButton3)))
                .addGroup(zoomPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, zoomPanel1Layout.createSequentialGroup()
                        .addGap(440, 440, 440)
                        .addComponent(zoomableLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(82, 82, 82))
                    .addGroup(zoomPanel1Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jButton7.setText("Load PHP project");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jProgressBar1.setStringPainted(true);
        jProgressBar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jProgressBar1MouseClicked(evt);
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

        jButton9.setText("Test DP");
        jButton9.setToolTipText("Load DiNOSy project");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setText("Load");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText("Save");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("Test Web");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setText("Test DF");
        jButton13.setToolTipText("Load DataLib file");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(zoomPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1282, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton8)
                        .addComponent(jButton9)
                        .addComponent(jButton12)
                        .addComponent(jButton13))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton10)
                        .addComponent(jButton11)
                        .addComponent(jButton7)
                        .addComponent(jCheckBox1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(zoomPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 862, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        translate(jButton1);
        zoomPanel1.translate(-5, 10);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        zoomPanel1.reset();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        zoomPanel1.zoom(0.5);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        zoomPanel1.zoom(2);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void zoomPanel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_zoomPanel1MouseClicked
        if (evt.getClickCount() > 1) {
            ZoomableLabel label = new ZoomableLabel("");
            label.switchEditable();
            ZoomableComponent component = zoomPanel1.addComponent(label);
            component.setLocation(evt.getX(), evt.getY());
            component.setSize(new Dimension(100, 20));
            label.requestFocusInWindow();
        } else if (evt.getButton() == MouseEvent.BUTTON3) {
            visualization.getOperationsPopup().show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_zoomPanel1MouseClicked

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        int n = 20000;
        if (n > 10000) {
            System.out.println("Loading...");
        }
        int slice = n / 100;
        int h = 0;
        for (int i= 0; i < n; i++) {
            ZoomableLabel l = new ZoomableLabel("Test " + i);
            if (i % 10 == 0) {
                h++;
            }
            l.setLocation((i / 40) * 120, (i % 40) * 100);
            l.setSize(100, 30);
            l.setForeground(Color.red);
            zoomPanel1.add(l);
            if (n > 10000 && i % slice == 0) {
                System.out.println((i / slice) + "%");
            }
        }
        if (n > 10000) {
            System.out.println("Loaded");
        }
        zoomPanel1.repaint();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        List<Component> toDelete = new LinkedList<Component>();
        for (Component component : zoomPanel1.getComponents()) {
            if (component instanceof ZoomableLabel || component instanceof ClassRepresentation) {
                toDelete.add(component);
            }
        }
        for (Component component : toDelete) {
            zoomPanel1.remove(component);
        }
        zoomPanel1.repaint();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        
    }//GEN-LAST:event_formKeyPressed

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

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        phpUml.clear();
        phpUml.loadPhpProject("/home/aurelijus/TEST-project/", jProgressBar1);
    }//GEN-LAST:event_jButton8ActionPerformed

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

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        if (jCheckBox1.isSelected()) {
            phpUml.addGeneralizations();
        } else {
            zoomPanel1.removeConnections(Arrow.Generalization.class);
            zoomPanel1.repaint();
        }
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        zoomPanel1.removeAll();
        visualization.loadData("/home/aurelijus/Documents/DiNoSy/Prototipas/docs/projecting.xml");
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            visualization.loadData(jfc.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (jfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            List<Component> components = Arrays.asList(zoomPanel1.getComponents());
            visualization.save(components, jfc.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        visualization.loadData(Firefox.webDataFile);
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        visualization.loadData("/home/aurelijus/Documents/DiNoSy/DataLib/src/lt/dinosy/datalib/testdocument.xml");
    }//GEN-LAST:event_jButton13ActionPerformed

    private void translate(JComponent component) {
        component.setLocation(component.getLocation().x + 1, component.getLocation().y + 5);
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new mainTest().setVisible(true);
            }
        });
    }

    static File openResource(String name) throws URISyntaxException {
        return new File(new URI(mainTest.class.getResource(name).toString()));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JTextField jTextField1;
    private lt.banelis.aurelijus.dinosy.prototype.ZoomPanel zoomPanel1;
    private lt.banelis.aurelijus.dinosy.prototype.ZoomableLabel zoomableLabel1;
    // End of variables declaration//GEN-END:variables

}
