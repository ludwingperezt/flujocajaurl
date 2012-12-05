/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EstimacionValoresPorcentuales.java
 *
 * Created on 12/10/2012, 10:44:15 PM
 */
package Intermedias;

import Clases.ModeloPorcentual;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.TableModel;

/**
 *
 * @author HP G42
 */
public class EstimacionValoresPorcentuales extends javax.swing.JDialog {

    private ModeloPorcentual modeloPorcentual;
    /** Creates new form EstimacionValoresPorcentuales */
    public EstimacionValoresPorcentuales(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public void estimarValoresPorcentuales(ModeloPorcentual modelo){
        this.setTitle("Estimar valores porcentuales");
        this.modeloPorcentual = modelo;        
        this.setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        estimar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuAcciones = new javax.swing.JMenu();
        definirManualmente = new javax.swing.JMenuItem();
        menuCargarDatos = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(flujocaja.FlujoCajaApp.class).getContext().getResourceMap(EstimacionValoresPorcentuales.class);
        estimar.setText(resourceMap.getString("estimar.text")); // NOI18N
        estimar.setName("estimar"); // NOI18N
        estimar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                estimarActionPerformed(evt);
            }
        });

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jTextField1.setEditable(false);
        jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
        jTextField1.setName("jTextField1"); // NOI18N

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        menuAcciones.setText(resourceMap.getString("menuAcciones.text")); // NOI18N
        menuAcciones.setName("menuAcciones"); // NOI18N

        definirManualmente.setText(resourceMap.getString("definirManualmente.text")); // NOI18N
        definirManualmente.setName("definirManualmente"); // NOI18N
        definirManualmente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                definirManualmenteActionPerformed(evt);
            }
        });
        menuAcciones.add(definirManualmente);

        menuCargarDatos.setText(resourceMap.getString("menuCargarDatos.text")); // NOI18N
        menuCargarDatos.setName("menuCargarDatos"); // NOI18N
        menuCargarDatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCargarDatosActionPerformed(evt);
            }
        });
        menuAcciones.add(menuCargarDatos);

        jMenuBar1.add(menuAcciones);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 688, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(estimar))
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(estimar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuCargarDatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCargarDatosActionPerformed
        // TODO add your handling code here:        
        JFileChooser selector = new JFileChooser();
        int resultado = selector.showOpenDialog(null);
        if (resultado==JFileChooser.APPROVE_OPTION){
            this.cargarDatosHistoricos(selector.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_menuCargarDatosActionPerformed

    private void definirManualmenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_definirManualmenteActionPerformed
        // TODO add your handling code here:
        int t = Integer.parseInt(JOptionPane.showInputDialog("Ingrese la cantidad de periodos a calcular"));
        DefaultTableModel modelo = new DefaultTableModel();
        Object [] fila = new Object[t];
        for (int i=0; i<t; i++){
            modelo.addColumn(i);
        }
        modelo.addRow(fila);
        this.jTable1.setModel(modelo);
    }//GEN-LAST:event_definirManualmenteActionPerformed

    private void estimarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_estimarActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        double [] val = new double[model.getColumnCount()];
        Object [] fila = new Object[model.getColumnCount()];
        
        for (int i=0; i<val.length; i++){
            val[i] = Double.parseDouble(model.getValueAt(0, i).toString());
        }
        
        double [] estimados = modeloPorcentual.estimarValores(val);
        
        for (int i=0; i<estimados.length; i++){
            fila[i] = estimados[i];
        }
        model.addRow(fila);       
    }//GEN-LAST:event_estimarActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(EstimacionValoresPorcentuales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EstimacionValoresPorcentuales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EstimacionValoresPorcentuales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EstimacionValoresPorcentuales.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                EstimacionValoresPorcentuales dialog = new EstimacionValoresPorcentuales(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem definirManualmente;
    private javax.swing.JButton estimar;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JMenu menuAcciones;
    private javax.swing.JMenuItem menuCargarDatos;
    // End of variables declaration//GEN-END:variables

    private void cargarDatosHistoricos(String direccion) {
        try {
            DefaultTableModel modelo = new DefaultTableModel();
            
            FileInputStream origen = new FileInputStream(direccion);
            InputStreamReader conexionArchivo = new InputStreamReader(origen, "ISO-8859-1");
            BufferedReader lectorBuffer = new BufferedReader(conexionArchivo);

            String [] datosHistoricos = lectorBuffer.readLine().split(":");            
            String [] listaDatosHistoricos = datosHistoricos[1].split(",");
            
            for (int i=0; i<listaDatosHistoricos.length; i++){
                modelo.addColumn(i);
            }
            modelo.addRow(listaDatosHistoricos);
            this.jTable1.setModel(modelo);
            
            origen.close();
            conexionArchivo.close();
            lectorBuffer.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(EstimacionValoresPorcentuales.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(EstimacionValoresPorcentuales.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
                Logger.getLogger(EstimacionValoresPorcentuales.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
