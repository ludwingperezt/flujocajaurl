/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Detalles.java
 *
 * Created on 6/10/2012, 07:28:27 PM
 */
package Intermedias;

import Clases.ISO;
import Clases.ISR;
import Clases.ModeloPorcentual;
import controlador.Escenario;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author HP G42
 */
public class Detalles extends javax.swing.JDialog {

    /** Creates new form Detalles */
    public Detalles(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public void verDetallesCalculoIVA(Escenario escenario){
        this.setTitle("Detalle de cálculo de IVA");
        double [] ivaPagar = escenario.getModeloIVA().getIVAPorPagar();
        double [] ivaCobrar = escenario.getModeloIVA().getIVAPorCobrar();
        double [] ivaDebito = escenario.getModeloIVA().getIVADebito();
        double [] ivaCredito = escenario.getModeloIVA().getIVACredito();
        double [] diferencia = escenario.getModeloIVA().getDiferenciaIVADebitoIVACredito();
        double [] egresos = escenario.getModeloIVA().getEgresos();
        int [] anios = escenario.getListaAnios();
        
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("");
        for (int i=0; i<anios.length; i++){
            modelo.addColumn(anios[i]);
        }
        this.jTable1.setModel(modelo);
        insertarFila("Egresos", egresos);
        insertarFila("IVA Debito", ivaDebito);
        insertarFila("IVA Credito", ivaCredito);
        insertarFila("IVA Debito - IVA Credito",diferencia);
        insertarFila("IVA por cobrar (acumulado)", ivaCobrar);
        insertarFila("IVA por pagar", ivaPagar);
        
        this.setVisible(true);
    }
    
    public void verDetallesCalculoISR(Escenario escenario){
        this.setTitle("Detalle de cálculo de ISR");
        ISR isr = escenario.getModeloISR();
        double[] ingresos = escenario.getModeloISR().getIngresos();
        double[] regimen = isr.getRegimen();
        double[] ISRporPagar = isr.getISRporPagar();
        double[] egresos = isr.getEgresos();
        double[] deduciblesSiguientePeriodo = isr.getDeduciblesSiguientePeriodo();
        double[] UAI = isr.getUAI();
        double[] UAIFiscal = isr.getUAIFiscal();
        double[] ISRPorPagarTemporal = isr.getISRPorPagarTemporal();
        double[] porcentajeUtilidad = isr.getPorcentajeUtilidad();
        double[] egresosDeducibles97 = isr.getEgresosDeducibles97();
        int [] anios = escenario.getListaAnios();
        
        DefaultTableModel modelo = new DefaultTableModel();   
        modelo.addColumn("");
        for (int i=0; i<anios.length; i++){
            modelo.addColumn(anios[i]);
        }
        this.jTable1.setModel(modelo);
        
        this.insertarFila("Ingresos", ingresos);
        this.insertarFila("Deducibles", egresos);
        this.insertarFila("UAI", UAI);
        this.insertarFila("% de utilidad", porcentajeUtilidad);
        this.insertarFila("Regimen", regimen);
        this.insertarFila("Deducibles 97%", egresosDeducibles97);
        this.insertarValor("Gastos personales", isr.getGastosPersonalesEscalados(),escenario.getNumeroPeriodos());
        this.insertarFila("Deducibles siguente periodo", deduciblesSiguientePeriodo);
        this.insertarFila("Renta imponible (UAI fiscal)", UAIFiscal);
        this.insertarFila("ISR temporal", ISRPorPagarTemporal);
        this.insertarFila("ISR definitivo", ISRporPagar);
        
        this.setVisible(true);
    }
    
    public void verDetallesCalculoISO(Escenario escenario){
        this.setTitle("Detalle de cálculo de ISO");
        ISO iso = escenario.getModeloISO();
        double[] porcentajeUtilidadBruta = iso.getPorcentajeUtilidadBruta();
        double[] isoActivos = iso.getIsoActivos();
        double[] isoIngresos = iso.getIsoIngresos();
        double[] isoPorPagarTemporal = iso.getIsoPorPagarTemporal();
        double[] isoPorPagarDefinitivo = iso.getIsoPorPagarDefinitivo();
        double[] isoAcumulado = iso.getIsoAcumulado();
        int [] anios = escenario.getListaAnios();
        
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("");
        for (int i=0; i<anios.length; i++){
            modelo.addColumn(anios[i]);
        }
        this.jTable1.setModel(modelo);
        if (iso.getAcreditacion()==ISO.ISO_ACREDITABLE_ISR)
            insertarValor("Afiliación", "ISO acreditado a ISR", escenario.getNumeroPeriodos());
        else
            insertarValor("Afiliación", "ISR acreditado a ISO", escenario.getNumeroPeriodos());
        insertarFila("Ingresos", escenario.getModeloIngresos().getIngresosActuales());
        insertarFila("Utilidad bruta", porcentajeUtilidadBruta);
        insertarFila("ISO ingresos", isoIngresos);
        insertarFila("ISO activos", isoActivos);
        insertarFila("ISO temporal", isoPorPagarTemporal);
        insertarFila("ISR temporal", escenario.getModeloISR().getISRPorPagarTemporal());
        insertarFila("ISO acumulado", isoAcumulado);
        insertarFila("ISO definitivo", isoPorPagarDefinitivo);
        insertarFila("ISR definitivo", escenario.getModeloISR().getISRporPagar());
        
        this.setVisible(true);
    }
    
    private void insertarFila(String nombre,double [] val){
        Object [] fila = new Object[val.length+1];
        fila[0]=nombre;
        for (int i=0; i<val.length; i++){
            fila[i+1] = ModeloPorcentual.redondearCifra(val[i]);
        }
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        modelo.addRow(fila);
    }
    
    private void insertarValor(String nombre,Object val, int periodos){
        Object [] fila = new Object[periodos+1];
        fila[0]=nombre;
        for (int i=0; i<periodos; i++){
            fila[i+1] = val;
        }
        DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();
        modelo.addRow(fila);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 970, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(Detalles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Detalles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Detalles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Detalles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Detalles dialog = new Detalles(new javax.swing.JFrame(), true);
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

}
