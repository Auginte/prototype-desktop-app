/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ArrangeGUITest.java
 *
 * Created on Jul 24, 2011, 9:55:58 AM
 */

package lt.banelis.aurelijus.dinosy.prototype.debug;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.LinkedList;
import lt.banelis.aurelijus.dinosy.prototype.ClassRepresentation;
import lt.banelis.aurelijus.dinosy.prototype.ClassRepresentation;
import lt.banelis.aurelijus.dinosy.prototype.ClassRepresentation;
import lt.banelis.aurelijus.dinosy.prototype.PhpUml;
import lt.banelis.aurelijus.dinosy.prototype.PhpUml;
import lt.banelis.aurelijus.dinosy.prototype.PhpUml;
import lt.banelis.aurelijus.dinosy.prototype.ZoomPanel;
import lt.banelis.aurelijus.dinosy.prototype.ZoomPanel;
import lt.banelis.aurelijus.dinosy.prototype.ZoomPanel;
import lt.banelis.aurelijus.dinosy.prototype.mainTest;
import lt.banelis.aurelijus.dinosy.prototype.mainTest;
import lt.banelis.aurelijus.dinosy.prototype.mainTest;
import lt.banelis.parser.OnlyRefferenceException;
import lt.banelis.parser.Class;
import lt.banelis.parser.NullNotAllowedException;
import lt.dinosy.datalib.Source;
import org.junit.Test;


/**
 * This file is used to debug visually arrangning algorythms
 * Use this file, remove test elements, uncoment and copy to src package as
 * runnable class.
 *
 * @author Aurelijus Banelis
 */
public class ArrangeGUITest extends javax.swing.JFrame {

//    /** Creates new form ArrangeGUITest */
//    public ArrangeGUITest() {
//        initComponents();
//        testAll();
//    }

//##################### To debug boxes ############################
//            for (ZoomableComponent zoomableComponent : components.values()) {
//            if (zoomableComponent.getComponent() instanceof ClassRepresentation) {
//                ClassRepresentation cr = (ClassRepresentation) zoomableComponent.getComponent();
//                if (cr.cb != null) {
//                    g.setColor(debugCR);
//                    g.fillRect(cr.getX() - cr.cb.FIXME_LEFT - 1, cr.getY() - 1, cr.cb.width + 2, cr.cb.height + 2);
//                }
//            }
//        }

    @Test
    public void fakeTest() { }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButtonOriginal = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButtonClear = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        zoomPanel1 = new lt.banelis.aurelijus.dinosy.prototype.ZoomPanel();
        jPanel3 = new javax.swing.JPanel();
        zoomPanel2 = new lt.banelis.aurelijus.dinosy.prototype.ZoomPanel();
        zoomPanel3 = new lt.banelis.aurelijus.dinosy.prototype.ZoomPanel();
        zoomPanel4 = new lt.banelis.aurelijus.dinosy.prototype.ZoomPanel();
        zoomPanel5 = new lt.banelis.aurelijus.dinosy.prototype.ZoomPanel();
        jPanel4 = new javax.swing.JPanel();
        zoomPanel6 = new lt.banelis.aurelijus.dinosy.prototype.ZoomPanel();
        zoomPanel7 = new lt.banelis.aurelijus.dinosy.prototype.ZoomPanel();
        zoomPanel8 = new lt.banelis.aurelijus.dinosy.prototype.ZoomPanel();
        zoomPanel9 = new lt.banelis.aurelijus.dinosy.prototype.ZoomPanel();
        jPanel5 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Tests"));

        jButton1.setText("Test1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("jButton2");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("jButton3");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("jButton4");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("jButton5");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("jButton6");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("jButton7");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("jButton8");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("jButton9");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButtonOriginal.setText("Original");
        jButtonOriginal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOriginalActionPerformed(evt);
            }
        });

        jButton10.setText("All");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButtonClear.setText("Clear All");
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(jButton5)
                    .addComponent(jButton6)
                    .addComponent(jButton7)
                    .addComponent(jButton8)
                    .addComponent(jButton9)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jButton10, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 72, Short.MAX_VALUE)
                        .addComponent(jButtonOriginal, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap(28, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButtonClear)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonClear, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonOriginal))
        );

        javax.swing.GroupLayout zoomPanel1Layout = new javax.swing.GroupLayout(zoomPanel1);
        zoomPanel1.setLayout(zoomPanel1Layout);
        zoomPanel1Layout.setHorizontalGroup(
            zoomPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 660, Short.MAX_VALUE)
        );
        zoomPanel1Layout.setVerticalGroup(
            zoomPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 622, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(zoomPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(zoomPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Test1", jPanel2);

        javax.swing.GroupLayout zoomPanel2Layout = new javax.swing.GroupLayout(zoomPanel2);
        zoomPanel2.setLayout(zoomPanel2Layout);
        zoomPanel2Layout.setHorizontalGroup(
            zoomPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 332, Short.MAX_VALUE)
        );
        zoomPanel2Layout.setVerticalGroup(
            zoomPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 310, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout zoomPanel3Layout = new javax.swing.GroupLayout(zoomPanel3);
        zoomPanel3.setLayout(zoomPanel3Layout);
        zoomPanel3Layout.setHorizontalGroup(
            zoomPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 322, Short.MAX_VALUE)
        );
        zoomPanel3Layout.setVerticalGroup(
            zoomPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 310, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout zoomPanel4Layout = new javax.swing.GroupLayout(zoomPanel4);
        zoomPanel4.setLayout(zoomPanel4Layout);
        zoomPanel4Layout.setHorizontalGroup(
            zoomPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 332, Short.MAX_VALUE)
        );
        zoomPanel4Layout.setVerticalGroup(
            zoomPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 306, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout zoomPanel5Layout = new javax.swing.GroupLayout(zoomPanel5);
        zoomPanel5.setLayout(zoomPanel5Layout);
        zoomPanel5Layout.setHorizontalGroup(
            zoomPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 322, Short.MAX_VALUE)
        );
        zoomPanel5Layout.setVerticalGroup(
            zoomPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 306, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(zoomPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(zoomPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(zoomPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(zoomPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(zoomPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(zoomPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(zoomPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(zoomPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("2-5", jPanel3);

        javax.swing.GroupLayout zoomPanel6Layout = new javax.swing.GroupLayout(zoomPanel6);
        zoomPanel6.setLayout(zoomPanel6Layout);
        zoomPanel6Layout.setHorizontalGroup(
            zoomPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 327, Short.MAX_VALUE)
        );
        zoomPanel6Layout.setVerticalGroup(
            zoomPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 321, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout zoomPanel7Layout = new javax.swing.GroupLayout(zoomPanel7);
        zoomPanel7.setLayout(zoomPanel7Layout);
        zoomPanel7Layout.setHorizontalGroup(
            zoomPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 327, Short.MAX_VALUE)
        );
        zoomPanel7Layout.setVerticalGroup(
            zoomPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 321, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout zoomPanel8Layout = new javax.swing.GroupLayout(zoomPanel8);
        zoomPanel8.setLayout(zoomPanel8Layout);
        zoomPanel8Layout.setHorizontalGroup(
            zoomPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 327, Short.MAX_VALUE)
        );
        zoomPanel8Layout.setVerticalGroup(
            zoomPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 295, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout zoomPanel9Layout = new javax.swing.GroupLayout(zoomPanel9);
        zoomPanel9.setLayout(zoomPanel9Layout);
        zoomPanel9Layout.setHorizontalGroup(
            zoomPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 327, Short.MAX_VALUE)
        );
        zoomPanel9Layout.setVerticalGroup(
            zoomPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 295, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(zoomPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(zoomPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(zoomPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(zoomPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(zoomPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(zoomPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(zoomPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(zoomPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("6-9", jPanel4);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 660, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 622, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Paint box", jPanel5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOriginalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOriginalActionPerformed
        mainTest test = new mainTest();
        test.setVisible(true);
    }//GEN-LAST:event_jButtonOriginalActionPerformed

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        clearAll();
    }//GEN-LAST:event_jButtonClearActionPerformed

    private void clearAll() {
        zoomPanel1.removeAll();
        zoomPanel2.removeAll();
        zoomPanel3.removeAll();
        zoomPanel4.removeAll();
        zoomPanel5.removeAll();
        zoomPanel6.removeAll();
        zoomPanel7.removeAll();
        zoomPanel8.removeAll();
        zoomPanel9.removeAll();

        jPanel3.setLayout(new GridLayout(2, 2));
        jPanel4.setLayout(new GridLayout(2, 2));
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        /*
         * A B C D
         *
         *     E
         *  F     G
         * H J   K L
         *
         *     M
         *   M1
         * M11 M12
         *     M121
         */
        Class A = newClass("A");
        Class B = newClass("B");
        Class C = newClass("C");
        Class D = newClass("D");

        Class E = newClass("E");
        Class F = newClass("F", E);
        Class G = newClass("G", E);
        Class H = newClass("H", F);
        Class J = newClass("J", F);
        Class K = newClass("H", G);
        Class L = newClass("J", G);

        Class M = newClass("M");
        Class M1 = newClass("M1", M);
        Class M11 = newClass("M11", M1);
        Class M12 = newClass("M12", M1);
        Class M121 = newClass("M121", M12);

        PhpUml phpUml = addToPanel(zoomPanel1, A, B, C, D, E, F, G, H, J, K, L, M, M1, M11, M12, M121);
        phpUml.generalizationGrid.arrange();
        zoomPanel1.setToolTipText("1");
        phpUml.addGeneralizations();
    }//GEN-LAST:event_jButton1ActionPerformed


    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        /*
         * A B C
         *   D
         *   E
         */
        Class A = newClass("A");
        Class B = newClass("B");
        Class C = newClass("C");

        Class D = newClass("D");
        Class E = newClass("E", D);

        PhpUml phpUml = addToPanel(zoomPanel2, A, B, C, D, E);
        phpUml.generalizationGrid.arrange();
        zoomPanel2.setToolTipText("2");
        phpUml.addGeneralizations();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        /*
         *   A
         * B   C
         */
        Class A = newClass("A");
        Class B = newClass("B", A);
        Class C = newClass("C", A);

        PhpUml phpUml = addToPanel(zoomPanel3, A, B, C);
        phpUml.generalizationGrid.arrange();
        zoomPanel3.setToolTipText("3");
        phpUml.addGeneralizations();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        /*
         *       A
         * B C D E F G H J
         */
        Class A = newClass("A");
        Class B = newClass("B", A);
        Class C = newClass("C", A);
        Class D = newClass("D", A);
        Class E = newClass("E", A);
        Class F = newClass("F", A);
        Class G = newClass("G", A);
        Class H = newClass("H", A);

        PhpUml phpUml = addToPanel(zoomPanel4, A, B, C, D, E, F, G, H);
        phpUml.generalizationGrid.arrange();
        zoomPanel4.setToolTipText("4");
        phpUml.addGeneralizations();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        /*
         *   A
         * B   E
         * C   F
         *     G
         */
        Class A = newClass("A");

        Class B = newClass("B", A);
        Class C = newClass("C", B);

        Class E = newClass("E", A);
        Class F = newClass("F", E);
        Class G = newClass("B", F);

        PhpUml phpUml = addToPanel(zoomPanel5, A, B, C, E, F, G);
        phpUml.generalizationGrid.arrange();
        zoomPanel5.setToolTipText("5");
        phpUml.addGeneralizations();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        /*
         * A
         * B
         *
         *    C
         *  D   E
         */
        Class A = newClass("A");
        Class B = newClass("B", A);

        Class C = newClass("C");
        Class D = newClass("D", C);
        Class E = newClass("E", C);

        PhpUml phpUml = addToPanel(zoomPanel6, A, B, C, D, E);
        phpUml.generalizationGrid.arrange();
        zoomPanel6.setToolTipText("6");
        phpUml.addGeneralizations();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        /*
         * A
         *
         * B
         * C
         *
         * D
         * E
         * F
         *
         *  G
         *  H
         *  I
         *  J
         * K L
         */
        Class A = newClass("A");

        Class B = newClass("B");
        Class C = newClass("C", B);

        Class D = newClass("D");
        Class E = newClass("E", D);
        Class F = newClass("F", E);

        Class G = newClass("G");
        Class H = newClass("H", G);
        Class I = newClass("I", H);
        Class J = newClass("J", I);
        Class K = newClass("K", J);
        Class L = newClass("L", J);

        PhpUml phpUml = addToPanel(zoomPanel7, A, B, C, D, E, F, G, H, I, J, K, L);
        phpUml.generalizationGrid.arrange();
        zoomPanel7.setToolTipText("7");
        phpUml.addGeneralizations();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        /*
         * A
         * B C
         *   D E
         *     F G
         *       H
         */
        Class A = newClass("A");
        Class B = newClass("B", A);
        Class C = newClass("C", A);
        Class D = newClass("DDDDDDDDDDD", C);
        Class E = newClass("E", C);
        Class F = newClass("FFF", E);
        Class G = newClass("G", E);
        Class H = newClass("H", G);

        PhpUml phpUml = addToPanel(zoomPanel8, A, B, C, D, E, F, G, H);
        phpUml.generalizationGrid.arrange();
        zoomPanel8.setToolTipText("8");
        phpUml.addGeneralizations();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        /*
         * A A2
         *
         *
         * B
         * C
         *
         *  D
         * E F
         *
         *  G
         * H N
         * J O Q
         * K P V
         * L R Z
         * M
         */
        Class A = newClass("A");
        Class A2 = newClass("A2");

        Class B = newClass("B");
        Class C = newClass("C", B);

        Class D = newClass("D");
        Class E = newClass("E", D);
        Class F = newClass("F", D);

        Class G = newClass("G");

        Class H = newClass("H", G);
        Class J = newClass("J", H);
        Class K = newClass("K", J);
        Class L = newClass("L", K);
        Class M = newClass("M", L);

        Class N = newClass("N", G);
        Class O = newClass("O", N);
        Class P = newClass("P", O);
        Class R = newClass("R", P);

        Class Q = newClass("Q", N);
        Class V = newClass("V", Q);
        Class Z = newClass("Z", V);

        PhpUml phpUml = addToPanel(zoomPanel9, A, A2, B, C, D, E, F, G, H, J, K, L, M, N, O, P, R, Q, V, Z);
        phpUml.generalizationGrid.arrange();
        zoomPanel9.setToolTipText("9");
        phpUml.addGeneralizations();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        testAll();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void testAll() {
        java.awt.event.ActionEvent evt = new ActionEvent(this, 1, "");
        clearAll();
        jButton1ActionPerformed(evt);
        jButton2ActionPerformed(evt);
        jButton3ActionPerformed(evt);
        jButton4ActionPerformed(evt);
        jButton5ActionPerformed(evt);
        jButton6ActionPerformed(evt);
        jButton7ActionPerformed(evt);
        jButton8ActionPerformed(evt);
        jButton9ActionPerformed(evt);
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ArrangeGUITest().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonOriginal;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JTabbedPane jTabbedPane1;
    private lt.banelis.aurelijus.dinosy.prototype.ZoomPanel zoomPanel1;
    private lt.banelis.aurelijus.dinosy.prototype.ZoomPanel zoomPanel2;
    private lt.banelis.aurelijus.dinosy.prototype.ZoomPanel zoomPanel3;
    private lt.banelis.aurelijus.dinosy.prototype.ZoomPanel zoomPanel4;
    private lt.banelis.aurelijus.dinosy.prototype.ZoomPanel zoomPanel5;
    private lt.banelis.aurelijus.dinosy.prototype.ZoomPanel zoomPanel6;
    private lt.banelis.aurelijus.dinosy.prototype.ZoomPanel zoomPanel7;
    private lt.banelis.aurelijus.dinosy.prototype.ZoomPanel zoomPanel8;
    private lt.banelis.aurelijus.dinosy.prototype.ZoomPanel zoomPanel9;
    // End of variables declaration//GEN-END:variables

    public Class newClass(String name) {
        return newClass(name, null);
    }

    public Class newClass(String name, Class extend) {
        Class classObject = null;
        try {
             classObject = new Class(name);
             classObject.setExtend(extend);
        } catch (OnlyRefferenceException ex) {
            System.err.println("OnlyRefferenceException: " +  ex);
        } catch (NullNotAllowedException ex) {
            System.err.println("NullNotAllowedException: " +  ex);
        }
        return classObject;
    }

    public PhpUml addToPanel(ZoomPanel panel, Class ... classes) {
        Source s = new Source.Project(new Date(), "GUI test project");
        for (Class class1 : classes) {
            panel.add(new ClassRepresentation(s, class1));
        }
        return new PhpUml(panel);
    }
}
