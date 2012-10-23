/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ModuloGastosSegunVariable.java
 *
 * Created on 20/09/2012, 05:29:42 PM
 */

package flujocaja;

import Clases.Gasto;
import Clases.ModeloPorcentual;

/**
 *
 * @author ludwing
 */
public class ModuloGastosSegunVariable extends javax.swing.JDialog {

    private Gasto modeloGasto;
    private int tipoGasto;

    /** Creates new form ModuloGastosSegunVariable */
    public ModuloGastosSegunVariable(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public Gasto modificarModeloGasto(int numeroPeriodos, String nombre, boolean porcentualIngresos, boolean factura, boolean escudoFiscal) {
        this.jTextField1.setText(nombre);
        this.jTextField1.setEditable(false);
        this.periodos.setText(Integer.toString(numeroPeriodos));
        this.porcentualAIngresos.setSelected(porcentualIngresos);
        this.conFactura.setSelected(factura);
        this.escudoFiscal.setSelected(escudoFiscal);
        
        this.setVisible(true);
        
        if (modeloGasto!=null){
            EstimacionPorcentual epc = new EstimacionPorcentual(null, true);
            ModeloPorcentual modelo = epc.estimarPorcentaje("Gasto: "+this.modeloGasto.getNombre());

            if (modelo!=null)
                this.modeloGasto.setModeloPorcentual(modelo);        
            else
                this.modeloGasto = null;
        }
        
        return this.modeloGasto;
    }
    
    public Gasto obtenerNuevoModeloGasto(int numeroPeriodos) {
        this.periodos.setText(Integer.toString(numeroPeriodos));
        //this.periodos.setVisible(false);
        //this.jLabel3.setVisible(false);
        this.setVisible(true);
        
        EstimacionPorcentual epc = new EstimacionPorcentual(null, true);
        ModeloPorcentual modelo = epc.estimarPorcentaje("Gasto: "+this.modeloGasto.getNombre());
        
        if (modelo!=null)
            this.modeloGasto.setModeloPorcentual(modelo);        
        else
            this.modeloGasto = null;
        
        return this.modeloGasto;
    }
    
    public Gasto obtenerNuevoModeloGasto(int numeroPeriodos, int tipoVariable) {
        this.periodos.setText(Integer.toString(numeroPeriodos));        
        this.panelVariable.setVisible(false);
        //this.periodos.setVisible(false);
        //this.jLabel3.setVisible(false);
        this.setVisible(true);
        
        EstimacionPorcentual epc = new EstimacionPorcentual(null, true);
        ModeloPorcentual modelo = epc.estimarPorcentaje("Gasto: "+this.modeloGasto.getNombre());
        
        if (modelo!=null){
            this.modeloGasto.setModeloPorcentual(modelo);       
            this.modeloGasto.setTipoGasto(tipoVariable);
        }
        else
            this.modeloGasto = null;
        
        return this.modeloGasto;
    }
    
    public Gasto obtenerNuevoModeloGastoManual(int numeroPeriodos) {
        this.periodos.setText(Integer.toString(numeroPeriodos));
        //this.periodos.setVisible(false);
        //this.jLabel3.setVisible(false);
        this.panelVariable.setVisible(false);
        this.setVisible(true);        
        return this.modeloGasto;
    }
    
    public Gasto modificarModeloGastoManual(int numeroPeriodos, String nombre, boolean porcentualIngresos, boolean factura, boolean escudoFiscal) {
        this.panelVariable.setVisible(false);
        this.jTextField1.setText(nombre);
        this.jTextField1.setEditable(false);        
        this.periodos.setText(Integer.toString(numeroPeriodos));
        this.porcentualAIngresos.setSelected(porcentualIngresos);
        this.conFactura.setSelected(factura);
        this.escudoFiscal.setSelected(escudoFiscal);        
        this.setVisible(true);
               
        return this.modeloGasto;
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
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        periodos = new javax.swing.JTextField();
        panelVariable = new javax.swing.JPanel();
        porcentualAIngresos = new javax.swing.JRadioButton();
        porcentualACostos = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        conFactura = new javax.swing.JRadioButton();
        sinFactura = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        panelEscudoFiscal = new javax.swing.JPanel();
        escudoFiscal = new javax.swing.JRadioButton();
        noEscudoFiscal = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(flujocaja.FlujoCajaApp.class).getContext().getResourceMap(ModuloGastosSegunVariable.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
        jTextField1.setName("jTextField1"); // NOI18N

        periodos.setText(resourceMap.getString("periodos.text")); // NOI18N
        periodos.setName("periodos"); // NOI18N

        panelVariable.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("panelVariable.border.title"))); // NOI18N
        panelVariable.setName("panelVariable"); // NOI18N

        buttonGroup1.add(porcentualAIngresos);
        porcentualAIngresos.setSelected(true);
        porcentualAIngresos.setText(resourceMap.getString("porcentualAIngresos.text")); // NOI18N
        porcentualAIngresos.setName("porcentualAIngresos"); // NOI18N

        buttonGroup1.add(porcentualACostos);
        porcentualACostos.setText(resourceMap.getString("porcentualACostos.text")); // NOI18N
        porcentualACostos.setName("porcentualACostos"); // NOI18N

        javax.swing.GroupLayout panelVariableLayout = new javax.swing.GroupLayout(panelVariable);
        panelVariable.setLayout(panelVariableLayout);
        panelVariableLayout.setHorizontalGroup(
            panelVariableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVariableLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelVariableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(porcentualAIngresos)
                    .addComponent(porcentualACostos))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelVariableLayout.setVerticalGroup(
            panelVariableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelVariableLayout.createSequentialGroup()
                .addComponent(porcentualAIngresos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(porcentualACostos)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        buttonGroup2.add(conFactura);
        conFactura.setSelected(true);
        conFactura.setText(resourceMap.getString("conFactura.text")); // NOI18N
        conFactura.setName("conFactura"); // NOI18N

        buttonGroup2.add(sinFactura);
        sinFactura.setText(resourceMap.getString("sinFactura.text")); // NOI18N
        sinFactura.setName("sinFactura"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(conFactura)
                    .addComponent(sinFactura))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(conFactura)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sinFactura)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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

        panelEscudoFiscal.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("panelEscudoFiscal.border.title"))); // NOI18N
        panelEscudoFiscal.setName("panelEscudoFiscal"); // NOI18N

        buttonGroup3.add(escudoFiscal);
        escudoFiscal.setText(resourceMap.getString("escudoFiscal.text")); // NOI18N
        escudoFiscal.setName("escudoFiscal"); // NOI18N

        buttonGroup3.add(noEscudoFiscal);
        noEscudoFiscal.setSelected(true);
        noEscudoFiscal.setText(resourceMap.getString("noEscudoFiscal.text")); // NOI18N
        noEscudoFiscal.setName("noEscudoFiscal"); // NOI18N

        javax.swing.GroupLayout panelEscudoFiscalLayout = new javax.swing.GroupLayout(panelEscudoFiscal);
        panelEscudoFiscal.setLayout(panelEscudoFiscalLayout);
        panelEscudoFiscalLayout.setHorizontalGroup(
            panelEscudoFiscalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEscudoFiscalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelEscudoFiscalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(escudoFiscal)
                    .addComponent(noEscudoFiscal))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        panelEscudoFiscalLayout.setVerticalGroup(
            panelEscudoFiscalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEscudoFiscalLayout.createSequentialGroup()
                .addComponent(escudoFiscal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noEscudoFiscal)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelVariable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelEscudoFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(periodos, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(223, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(periodos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelEscudoFiscal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton2)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelVariable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        this.modeloGasto=null;
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.modeloGasto = new Gasto();
        this.modeloGasto.setNombre(jTextField1.getText());
        this.modeloGasto.setCantidadPeriodos(Integer.parseInt(periodos.getText()));
        if (porcentualAIngresos.isSelected())
            this.modeloGasto.setTipoGasto(Gasto.GASTO_SEGUN_INGRESOS);
        else
            this.modeloGasto.setTipoGasto(Gasto.GASTO_SEGUN_COSTOS);
        this.modeloGasto.setFactura(conFactura.isSelected());
        this.modeloGasto.setEscudoFiscal(escudoFiscal.isSelected());
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ModuloGastosSegunVariable dialog = new ModuloGastosSegunVariable(new javax.swing.JFrame(), true);
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
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JRadioButton conFactura;
    private javax.swing.JRadioButton escudoFiscal;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JRadioButton noEscudoFiscal;
    private javax.swing.JPanel panelEscudoFiscal;
    private javax.swing.JPanel panelVariable;
    private javax.swing.JTextField periodos;
    private javax.swing.JRadioButton porcentualACostos;
    private javax.swing.JRadioButton porcentualAIngresos;
    private javax.swing.JRadioButton sinFactura;
    // End of variables declaration//GEN-END:variables

    



}