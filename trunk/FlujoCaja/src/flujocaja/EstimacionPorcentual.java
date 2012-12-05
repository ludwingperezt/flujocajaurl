/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EstimacionPorcentualCostos.java
 *
 * Created on 4/09/2012, 01:15:23 AM
 */

package flujocaja;

import Clases.ModeloPorcentual;
import Intermedias.EstimacionValoresPorcentuales;
import controlador.Escenario;
import java.awt.event.KeyEvent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ludwing
 */
public class EstimacionPorcentual extends javax.swing.JDialog {

    //private ModeloCosto estimador;
    //private Costos mCostos;
    private ModeloPorcentual modeloPorcentual;
    private boolean standalone = false;

    /** Creates new form EstimacionPorcentualCostos */
    public EstimacionPorcentual(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.estimar.setVisible(false);
    }

    public ModeloPorcentual estimarPorcentaje(String titulo){
        this.setTitle(titulo);
        this.setVisible(true);
        return this.modeloPorcentual;
    }
    
    public ModeloPorcentual modificarPorcentaje(ModeloPorcentual modeloAnterior,String titulo) {
        this.setTitle(titulo);
        this.modeloPorcentual = modeloAnterior;
        txtPromedio.setText(Double.toString(modeloAnterior.getPromedio()*100)+"%");
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("X");
        modelo.addColumn("Y");
        modelo.addColumn("%");
        double [] listaIngresosHistoricos = modeloAnterior.getX();
        double [] listaCostosHistoricos = modeloAnterior.getY();
        double [] porcentajes = modeloAnterior.getListaPorcentajes();
        if (listaIngresosHistoricos!=null){
            for (int i=0; i<listaIngresosHistoricos.length; i++){
                Object [] fila = new Object[3];
                fila[0] = listaIngresosHistoricos[i];
                fila[1] = listaCostosHistoricos[i];
                fila[2] = porcentajes[i];
                modelo.addRow(fila);
            }
        }
        jTable1.setModel(modelo);
        this.setVisible(true);
        return this.modeloPorcentual;
    }

    private void hacerCalculos(){
        try
        {
            TableModel modelo = jTable1.getModel();
            double [] x = new double[modelo.getRowCount()];
            double [] y = new double[modelo.getRowCount()];
            double [] porcentajes;

            double promedio;
            this.modeloPorcentual = new ModeloPorcentual();

            for (int i=0; i<modelo.getRowCount(); i++)
            {
                x[i] = new Double(modelo.getValueAt(i, 0).toString());
                y[i] = new Double(modelo.getValueAt(i, 1).toString());
            }

            modeloPorcentual.setY(y);
            modeloPorcentual.setX(x);
            promedio = modeloPorcentual.calcularPorcentaje(this.redondear.isSelected());
            porcentajes = modeloPorcentual.getListaPorcentajes();

            for (int i=0; i<modelo.getRowCount(); i++)
            {
                String porciento = Double.toString(porcentajes[i]*100)+"%";
                modelo.setValueAt(porciento, i, 2);
            }

            String resultado = Double.toString(promedio*100);
            
            txtPromedio.setText(resultado+"%");

        }
        catch (NullPointerException ex)
        {
            JOptionPane.showMessageDialog(rootPane, "No pueden quedar celdas vacías", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cargarDatosHistoricos(String direccion) {
        try {
            FileInputStream origen = new FileInputStream(direccion);
            InputStreamReader conexionArchivo = new InputStreamReader(origen, "ISO-8859-1");
            BufferedReader lectorBuffer = new BufferedReader(conexionArchivo);

            String [] ingresos = lectorBuffer.readLine().split(":");
            String [] costos = lectorBuffer.readLine().split(":");

            String [] listaIngresosHistoricos = ingresos[1].split(",");
            String [] listaCostosHistoricos = costos[1].split(",");

            DefaultTableModel modelo = new DefaultTableModel();
            modelo.addColumn("X");
            modelo.addColumn("Y");
            modelo.addColumn("%");

            for (int i=0; i<listaIngresosHistoricos.length; i++){
                Object [] fila = new Object[3];
                fila[0] = listaIngresosHistoricos[i];
                fila[1] = listaCostosHistoricos[i];
                modelo.addRow(fila);
            }

            jTable1.setModel(modelo);

            origen.close();
            conexionArchivo.close();
            lectorBuffer.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Escenario.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Escenario.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
                Logger.getLogger(Escenario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void standalone(){
        this.setTitle("Estimación porcentual");
        this.standalone = true;
        this.estimar.setVisible(true);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        txtPromedio = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        redondear = new javax.swing.JRadioButton();
        noRedondear = new javax.swing.JRadioButton();
        estimar = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        ingresoManual = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N
        setResizable(false);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(flujocaja.FlujoCajaApp.class).getContext().getResourceMap(EstimacionPorcentual.class);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "X", "Y", "%"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class, java.lang.Double.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setName("jTable1"); // NOI18N
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3))
            .addComponent(jScrollPane1, 0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE))
        );

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        txtPromedio.setText(resourceMap.getString("txtPromedio.text")); // NOI18N
        txtPromedio.setName("txtPromedio"); // NOI18N

        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText(resourceMap.getString("jButton5.text")); // NOI18N
        jButton5.setName("jButton5"); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        buttonGroup1.add(redondear);
        redondear.setSelected(true);
        redondear.setText(resourceMap.getString("redondear.text")); // NOI18N
        redondear.setName("redondear"); // NOI18N

        buttonGroup1.add(noRedondear);
        noRedondear.setText(resourceMap.getString("noRedondear.text")); // NOI18N
        noRedondear.setName("noRedondear"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(redondear)
                    .addComponent(noRedondear))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(redondear)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noRedondear))
        );

        estimar.setText(resourceMap.getString("estimar.text")); // NOI18N
        estimar.setName("estimar"); // NOI18N
        estimar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                estimarActionPerformed(evt);
            }
        });

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N
        jMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu1ActionPerformed(evt);
            }
        });

        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        ingresoManual.setText(resourceMap.getString("ingresoManual.text")); // NOI18N
        ingresoManual.setName("ingresoManual"); // NOI18N
        ingresoManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ingresoManualActionPerformed(evt);
            }
        });
        jMenu1.add(ingresoManual);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(382, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 94, Short.MAX_VALUE)
                                .addComponent(jButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton5)
                                .addContainerGap())
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(estimar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtPromedio, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE))
                                .addGap(59, 59, 59))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(85, 85, 85)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPromedio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(estimar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 187, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton5)
                            .addComponent(jButton4)))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        hacerCalculos();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel)jTable1.getModel();
        model.addRow(new Object[]{null,null,null});
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        int[] seleccionadas = jTable1.getSelectedRows();
        DefaultTableModel model = (DefaultTableModel)jTable1.getModel();

        for (int i=seleccionadas.length-1;i>=0;i--)
        {
            model.removeRow(seleccionadas[i]);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        this.modeloPorcentual = null;
        this.dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode()==KeyEvent.VK_TAB)
        {
            if ((jTable1.getSelectedRow()==(jTable1.getModel().getRowCount()-1))&&(jTable1.getSelectedColumn()==(jTable1.getColumnCount()-1)))
            {
                DefaultTableModel model = (DefaultTableModel)jTable1.getModel();
                model.addRow(new Object[]{null,null,null});
            }
        }
        if (evt.getKeyCode()==KeyEvent.VK_DELETE)
        {
            int[] seleccionadas = jTable1.getSelectedRows();
            DefaultTableModel model = (DefaultTableModel)jTable1.getModel();

            for (int i=seleccionadas.length-1;i>=0;i--)
            {
                model.removeRow(seleccionadas[i]);
            }
        }
    }//GEN-LAST:event_jTable1KeyPressed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        JFileChooser selector = new JFileChooser();
        int resultado = selector.showOpenDialog(null);
        if (resultado==JFileChooser.APPROVE_OPTION){
            this.cargarDatosHistoricos(selector.getSelectedFile().getAbsolutePath());
            hacerCalculos();
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void ingresoManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ingresoManualActionPerformed
        // TODO add your handling code here:
        if (this.modeloPorcentual==null)
            this.modeloPorcentual = new ModeloPorcentual();
        String porcentaje = JOptionPane.showInputDialog(null, "Ingrese el nuevo porcentaje", "Porcentaje manual", JOptionPane.QUESTION_MESSAGE);
        this.modeloPorcentual.setPromedioManual(ModeloPorcentual.formatearPorcentaje(porcentaje,this.redondear.isSelected()));
        if (standalone)
            this.txtPromedio.setText(porcentaje);
        else
            this.dispose();
    }//GEN-LAST:event_ingresoManualActionPerformed

    private void estimarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_estimarActionPerformed
        // TODO add your handling code here:
        EstimacionValoresPorcentuales evp = new EstimacionValoresPorcentuales(null, false);
        evp.estimarValoresPorcentuales(modeloPorcentual);
    }//GEN-LAST:event_estimarActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                EstimacionPorcentual dialog = new EstimacionPorcentual(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton estimar;
    private javax.swing.JMenuItem ingresoManual;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JRadioButton noRedondear;
    private javax.swing.JRadioButton redondear;
    private javax.swing.JTextField txtPromedio;
    // End of variables declaration//GEN-END:variables

    
}
