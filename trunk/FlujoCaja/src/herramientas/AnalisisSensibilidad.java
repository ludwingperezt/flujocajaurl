/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AnalisisSensibilidad.java
 *
 * Created on 5/11/2012, 11:32:24 PM
 */
package herramientas;

import Clases.Gasto;
import Clases.Intereses;
import Clases.ModeloPorcentual;
import controlador.Escenario;
import java.text.NumberFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author HP G42
 */
public class AnalisisSensibilidad extends javax.swing.JDialog {

    private double [] ingresos;
    private Escenario escenario;
    private int porcentaje;
    private double sensibilidad;
    
    /** Creates new form AnalisisSensibilidad */
    public AnalisisSensibilidad(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public void analisisSensibilidad(Escenario padre){
        this.escenario = new Escenario(padre);
        this.escenario.crearGastosEInversiones(padre);
        this.ingresos = padre.ingresosActuales();        
        double tir = padre.getTIR();
        if (tir>0){
            this.calcularSensibilidad();    
            this.setVisible(true);
        }
        else{
            JOptionPane.showMessageDialog(null, "El proyecto no es rentable, la TIR inicial es negativa", "No aplica", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
        
    }
    
    private void calcularSensibilidad(){
        for (int i = 100; i >= 0; i--){
            this.porcentaje = i+1;
            this.escenario.getModeloIngresos().setListaIngresosManualmente(this.calcularIngresos(i));
            this.escenario.recalcularTodo();
            double tir = this.escenario.getTIR();
            if (tir<0){                
                this.escenario.getModeloIngresos().setListaIngresosManualmente(this.calcularIngresos(this.porcentaje));
                this.escenario.recalcularTodo();
                this.sensibilidad = 1 - (((double)this.porcentaje)/100);
                this.llenarDatos();
                break;
            }
        }
    }
    
    private double [] calcularIngresos(int porcentaje){
        double porcent = ((double)porcentaje)/100;
        double [] ingresosCalculados = new double[this.ingresos.length];
        for (int i=0; i<ingresosCalculados.length; i++){
            ingresosCalculados[i] = this.ingresos[i]*porcent;
        }
        return ingresosCalculados;
    }

    
    private void llenarDatos(){
        
        double [] iterador;
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Concepto");
        modelo.addColumn("Factura");
        for (int i=0; i<this.escenario.getListaAnios().length; i++){
            modelo.addColumn(this.escenario.getListaAnios()[i]);
        }
        this.tablaDatos.setModel(modelo);
        
        //ingresos
        if (this.escenario.ingresosCalculados())
            this.insertarFilaTabla("Ingresos",-1, this.escenario.ingresosActuales(),modelo);

        //costos
        if (this.escenario.costosCalculados())
            this.insertarFilaTabla("Costos",(this.escenario.getModeloCostos().getFactura()) ? 1 : 0, this.escenario.costosActuales(),modelo);

        //uitlidad bruta
        if (this.escenario.utilidadBrutaCalculada())
            this.insertarFilaTabla("Utilidad bruta",-1, this.escenario.getUtilidadBruta(),modelo);

        //gastos
        ArrayList<Gasto> listaGastos = this.escenario.getListaGastos();
        if (listaGastos!=null){
            for(Gasto g : listaGastos){
                iterador = g.getListaGastos();
                if (iterador!=null)
                    this.insertarFilaTabla(g.getNombreGasto(), (g.getFactura())? 1:0, iterador,modelo);
            }
        }
        //intereses
        ArrayList<Intereses> listaIntereses = this.escenario.getListaIntereses();
        if (listaIntereses!=null){
            for (Intereses i:listaIntereses){
                iterador = i.getListaCuotasAnuales();
                if (iterador!=null)
                    this.insertarFilaTabla("Intereses", 0, i.getListaCuotasAnuales(),modelo);
            }
        }
        //"UAI"
        if (this.escenario.uaiCalculado())
            this.insertarFilaTabla("UAI", -1,this.escenario.getUtilidadAntesImpuestos(),modelo);
        
        //"IVA por pagar"
        if (this.escenario.ivaCalculado()){
            this.insertarFilaTabla("IVA por pagar", -1,this.escenario.IVAporPagar(),modelo);
        }            
        
        // ISR
        if (this.escenario.isrCalculado()){
            iterador = this.escenario.ISRporPagarTemporal();
            if (iterador!=null)
                this.insertarFilaTabla("ISR temporal", -1,iterador,modelo);
            
        }
        //ISO
        if (this.escenario.ISOcalculado()){
            iterador = this.escenario.ISOporPagar();
            if (iterador!=null){
                this.insertarFilaTabla("ISR por pagar", -1,this.escenario.ISRporPagar(),modelo);
                this.insertarFilaTabla("ISO", -1,iterador,modelo);
                
            }
        }
        //UTILIDAD NETA
        if (this.escenario.getUtilidadNeta()!=null)
            this.insertarFilaTabla("Utilidad neta", -1,this.escenario.getUtilidadNeta(),modelo);
        //FEN
        if (this.escenario.getFEN()!=null)
            this.insertarFilaTabla("FEN", -1,this.escenario.getFEN(),modelo);
        //VAN
        if (this.escenario.getVAN()!=null){
            this.insertarFilaTabla("VAN", -1,this.escenario.getVAN(),modelo);
            this.llenarDetalles();
        }
        ////// FIN DE LA PARTE DE MOSTRADO DE DATOS
        
    }
    
    private void insertarFilaTabla(String nombre,int factura,double [] valores,DefaultTableModel modelo){
        Object [] fila = new Object[valores.length+2];
        fila[0] = nombre;
        if (factura==1)
            fila[1] = "S";
        else if (factura==0)
            fila[1] = "N";
        else
            fila[1] = null;
            
        for (int i=0; i<valores.length; i++){
            NumberFormat mf = NumberFormat.getInstance();
            mf.setMaximumFractionDigits(2);
            String resultado = mf.format(valores[i]);
            fila[i+2] = resultado;
        }
        modelo.addRow(fila);
    }
    
    
    private void llenarDetalles(){
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Total de inversion");
        modelo.addColumn("TMAR");
        modelo.addColumn("VAN");
        modelo.addColumn("Payback");
        modelo.addColumn("TIR");
        modelo.addColumn("Sensibilidad");
        
        
        modelo.addRow(new Object[]{this.escenario.getInversionInicial(),this.escenario.getTMARFormateada(),ModeloPorcentual.redondearCifra(this.escenario.getSumatoriaVAN()),this.escenario.getPayback_string(),null,Double.toString(ModeloPorcentual.redondearCifra(sensibilidad*100))+"%"});
        tablaResultados.setModel(modelo);
        
        //TIR
        if ((this.escenario!=null)&&(this.escenario.getVAN()!=null))
            modelo.setValueAt(this.escenario.getTIR_string(), 0, 4);
        
        
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        panelSalida = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaResultados = new javax.swing.JTable();
        panelDatos = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaDatos = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        panelSalida.setName("panelSalida"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tablaResultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaResultados.setName("tablaResultados"); // NOI18N
        jScrollPane1.setViewportView(tablaResultados);

        javax.swing.GroupLayout panelSalidaLayout = new javax.swing.GroupLayout(panelSalida);
        panelSalida.setLayout(panelSalidaLayout);
        panelSalidaLayout.setHorizontalGroup(
            panelSalidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSalidaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelSalidaLayout.setVerticalGroup(
            panelSalidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSalidaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(flujocaja.FlujoCajaApp.class).getContext().getResourceMap(AnalisisSensibilidad.class);
        jTabbedPane1.addTab(resourceMap.getString("panelSalida.TabConstraints.tabTitle"), panelSalida); // NOI18N

        panelDatos.setName("panelDatos"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tablaDatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaDatos.setName("tablaDatos"); // NOI18N
        jScrollPane2.setViewportView(tablaDatos);

        javax.swing.GroupLayout panelDatosLayout = new javax.swing.GroupLayout(panelDatos);
        panelDatos.setLayout(panelDatosLayout);
        panelDatosLayout.setHorizontalGroup(
            panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelDatosLayout.setVerticalGroup(
            panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("panelDatos.TabConstraints.tabTitle"), panelDatos); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 597, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE)
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
            java.util.logging.Logger.getLogger(AnalisisSensibilidad.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AnalisisSensibilidad.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AnalisisSensibilidad.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AnalisisSensibilidad.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                AnalisisSensibilidad dialog = new AnalisisSensibilidad(new javax.swing.JFrame(), true);
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel panelDatos;
    private javax.swing.JPanel panelSalida;
    private javax.swing.JTable tablaDatos;
    private javax.swing.JTable tablaResultados;
    // End of variables declaration//GEN-END:variables
}
