/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SimulacionAlterno.java
 *
 * Created on 20/11/2012, 11:08:58 PM
 */
package herramientas;

import Clases.Gasto;
import Clases.Intereses;
import Clases.ModeloPorcentual;
import controlador.Escenario;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author HP G42
 */
public class SimulacionAlterno extends javax.swing.JDialog {
    
    private Escenario escenario;
    private Escenario antiguo;
    private Escenario original;
    private Map<Integer,String> mapa;

    /** Creates new form SimulacionAlterno */
    public SimulacionAlterno(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        mapa = new HashMap<Integer, String>();
    }
    
    public void analisisSimulacion(Escenario base){
        this.setTitle("Simulación");
        this.escenario = new Escenario(base);
        this.escenario.crearGastosEInversiones(base);
        
        this.escenario.funcionOcultaCambioManualGastos();
        
        this.antiguo = new Escenario(base);
        this.antiguo.crearGastosEInversiones(base);
        
        this.llenarDatos();
        this.setVisible(true);
    }
    
    private void llenarDatos(){
        this.escenario.recalcularTodo();
        
        mapa = new HashMap<Integer, String>();
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
                    this.insertarInteresTablaPrincipal(i.getNombre(), i.getListaCuotasAnuales(),modelo);
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
        mapa.put(modelo.getRowCount()-1, nombre);
    }
    
    private void insertarInteresTablaPrincipal(String nombre,double [] valores,DefaultTableModel modelo){
        Object [] fila = new Object[valores.length+2];
        fila[0] = "Intereses";
        fila[1] = "N";
        //System.arraycopy(valores, 0, fila, 1, valores.length);
        for (int i=0; i<valores.length; i++){
            NumberFormat mf = NumberFormat.getInstance();
            mf.setMaximumFractionDigits(2);
            String resultado = mf.format(valores[i]);
            fila[i+2] = resultado;
        }
        modelo.addRow(fila);
        mapa.put(modelo.getRowCount()-1, nombre);
    }
    
    private void llenarDetalles(){
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Total de inversion");
        modelo.addColumn("TMAR");
        modelo.addColumn("VAN");
        modelo.addColumn("Payback");
        modelo.addColumn("TIR");
                
        modelo.addRow(new Object[]{this.escenario.getInversionInicial(),this.escenario.getTMARFormateada(),ModeloPorcentual.redondearCifra(this.escenario.getSumatoriaVAN()),this.escenario.getPayback_string(),null});
        tablaResultados.setModel(modelo);
        
        //TIR
        if ((this.escenario!=null)&&(this.escenario.getVAN()!=null))
            modelo.setValueAt(this.escenario.getTIR_string(), 0, 4);
        
    }

    private double [] recalcular(double[]anteriores,double porcentaje,double suma){
        double [] ret = new double[anteriores.length];
        
        for (int i=0; i<anteriores.length; i++){
            ret[i] = anteriores[i]+(anteriores[i]*(porcentaje*suma));
        }
        
        return ret;
    }
    
    private void establecerNuevasCantidades(double suma){
        int index = tablaDatos.getSelectedRow();
        String nombre = mapa.get(index);
        if (nombre.equals("Ingresos")){
            String resultado = JOptionPane.showInputDialog(null, "Ingrese el porcentaje a sumar", "Modificar", JOptionPane.QUESTION_MESSAGE);
            try{
                double val = ModeloPorcentual.formatearPorcentaje(resultado,false);
                this.escenario.getModeloIngresos().setListaIngresosManualmente(this.recalcular(this.antiguo.ingresosActuales(), val, suma));
                llenarDatos();
            }
            catch(Exception ex){
                Logger.getLogger(AnalisisSimulacion.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
        else if (nombre.equals("Costos")){
            String resultado = JOptionPane.showInputDialog(null, "Ingrese el porcentaje a sumar", "Modificar", JOptionPane.QUESTION_MESSAGE);
            try{
                double val = ModeloPorcentual.formatearPorcentaje(resultado,false);
                this.escenario.getModeloCostos().establecerCostosManualmente(this.recalcular(this.antiguo.costosActuales(), val, suma));
                llenarDatos();
            }
            catch(Exception ex){
                Logger.getLogger(AnalisisSimulacion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (this.escenario.gastoValido(nombre)){
            Gasto g = this.antiguo.obtenerGasto(nombre);
            Gasto gn = this.escenario.obtenerGasto(nombre);
            String resultado = JOptionPane.showInputDialog(null, "Ingrese el porcentaje a sumar", "Modificar", JOptionPane.QUESTION_MESSAGE);
            try{
                double val = ModeloPorcentual.formatearPorcentaje(resultado,false);
                gn.setGastosManualmente(this.recalcular(g.getListaGastos(), val, suma));
                llenarDatos();
            }
            catch(Exception ex){
                Logger.getLogger(AnalisisSimulacion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        menuSumar = new javax.swing.JMenuItem();
        menuRestar = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaDatos = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaResultados = new javax.swing.JTable();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(flujocaja.FlujoCajaApp.class).getContext().getResourceMap(SimulacionAlterno.class);
        menuSumar.setText(resourceMap.getString("menuSumar.text")); // NOI18N
        menuSumar.setName("menuSumar"); // NOI18N
        menuSumar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuSumarActionPerformed(evt);
            }
        });
        jPopupMenu1.add(menuSumar);

        menuRestar.setText(resourceMap.getString("menuRestar.text")); // NOI18N
        menuRestar.setName("menuRestar"); // NOI18N
        menuRestar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRestarActionPerformed(evt);
            }
        });
        jPopupMenu1.add(menuRestar);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tablaDatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaDatos.setComponentPopupMenu(jPopupMenu1);
        tablaDatos.setName("tablaDatos"); // NOI18N
        jScrollPane1.setViewportView(tablaDatos);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 544, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tablaResultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaResultados.setName("tablaResultados"); // NOI18N
        jScrollPane2.setViewportView(tablaResultados);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 544, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(resourceMap.getString("jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 589, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 375, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuSumarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuSumarActionPerformed
        // TODO add your handling code here:
        this.establecerNuevasCantidades(1);
    }//GEN-LAST:event_menuSumarActionPerformed

    private void menuRestarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRestarActionPerformed
        // TODO add your handling code here:
        this.establecerNuevasCantidades(-1);
    }//GEN-LAST:event_menuRestarActionPerformed

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
            java.util.logging.Logger.getLogger(SimulacionAlterno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SimulacionAlterno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SimulacionAlterno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SimulacionAlterno.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                SimulacionAlterno dialog = new SimulacionAlterno(new javax.swing.JFrame(), true);
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JMenuItem menuRestar;
    private javax.swing.JMenuItem menuSumar;
    private javax.swing.JTable tablaDatos;
    private javax.swing.JTable tablaResultados;
    // End of variables declaration//GEN-END:variables
}
