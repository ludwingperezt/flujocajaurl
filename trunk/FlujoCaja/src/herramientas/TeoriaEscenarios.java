/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TeoriaEscenarios.java
 *
 * Created on 22/10/2012, 10:37:54 PM
 */
package herramientas;

import Clases.Gasto;
import Clases.Intereses;
import Clases.ModeloPorcentual;
import controlador.Escenario;
import controlador.EscenarioModificado;
import java.text.NumberFormat;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author HP G42
 */
public class TeoriaEscenarios extends javax.swing.JDialog {
    
    private Escenario escenarioNormal;
    private EscenarioModificado escenarioPesimista;
    private EscenarioModificado escenarioOptimista;

    /** Creates new form TeoriaEscenarios */
    public TeoriaEscenarios(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public void estimarEscenarios(Escenario normal){
        this.escenarioNormal = normal;
        this.escenarioPesimista = new EscenarioModificado(normal);
        this.escenarioPesimista.setTipoEscenario(EscenarioModificado.ESCENARIO_PESIMISTA);
        this.escenarioOptimista = new EscenarioModificado(normal);
        this.escenarioOptimista.setTipoEscenario(EscenarioModificado.ESCENARIO_OPTIMISTA);
        this.setVisible(true);
        
    }
    
    private void llenarDatos() {
        llenarDatosEscenarioOptimista();
        llenarDatosEscenarioPesimista();
        llenarDetalles();
    }
    
    private void llenarDetalles(){
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Escenario");
        modelo.addColumn("TMAR");
        modelo.addColumn("Inversión inicial");
        modelo.addColumn("Payback");
        modelo.addColumn("VAN");
        modelo.addColumn("TIR");
        
        tablaResultados.setModel(modelo);
        
        modelo.addRow(new Object[]{"Puntual",this.escenarioNormal.getTMARFormateada(),this.escenarioNormal.getInversionInicial(),this.escenarioNormal.getPayback_string(),this.escenarioNormal.getSumatoriaVAN(),this.escenarioNormal.getTIR_string()});
        modelo.addRow(new Object[]{"Puntual",this.escenarioOptimista.getTMARFormateada(),this.escenarioOptimista.getInversionInicial(),this.escenarioOptimista.getPayback_string(),this.escenarioOptimista.getSumatoriaVAN(),this.escenarioOptimista.getTIR_string()});
        modelo.addRow(new Object[]{"Pesimista",this.escenarioPesimista.getTMARFormateada(),this.escenarioPesimista.getInversionInicial(),this.escenarioPesimista.getPayback_string(),this.escenarioPesimista.getSumatoriaVAN(),this.escenarioPesimista.getTIR_string()});
        
        double tirPonderada = (this.escenarioOptimista.getTIR()-this.escenarioPesimista.getTIR())*100;
        String tir = "Err";
        try{
            NumberFormat mf = NumberFormat.getInstance();
            mf.setMaximumFractionDigits(2);
            tir = mf.format(tirPonderada)+"%";
        }catch(Exception ex){
            
        }
        modelo.addRow(new Object[]{null,null,null,null,null,null});
        modelo.addRow(new Object[]{"TIR ponderada",null,null,null,null,tir});
        
        
    }
    private void llenarDatosEscenarioOptimista(){
        
        double [] iterador;
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Concepto");
        modelo.addColumn("Factura");
        for (int i=0; i<this.escenarioOptimista.getListaAnios().length; i++){
            modelo.addColumn(this.escenarioOptimista.getListaAnios()[i]);
        }
        this.tablaDatosOptimistas.setModel(modelo);
        
        //ingresos
        if (this.escenarioOptimista.ingresosCalculados())
            this.insertarFilaTabla("Ingresos",-1, this.escenarioOptimista.ingresosActuales(),modelo);

        //costos
        if (this.escenarioOptimista.costosCalculados())
            this.insertarFilaTabla("Costos",(this.escenarioOptimista.getModeloCostos().getFactura()) ? 1 : 0, this.escenarioOptimista.costosActuales(),modelo);

        //uitlidad bruta
        if (this.escenarioOptimista.utilidadBrutaCalculada())
            this.insertarFilaTabla("Utilidad bruta",-1, this.escenarioOptimista.getUtilidadBruta(),modelo);

        //gastos
        ArrayList<Gasto> listaGastos = this.escenarioOptimista.getListaGastos();
        if (listaGastos!=null){
            for(Gasto g : listaGastos){
                iterador = g.getListaGastos();
                if (iterador!=null)
                    this.insertarFilaTabla(g.getNombre(), (g.getFactura())? 1:0, iterador,modelo);
            }
        }
        //intereses
        ArrayList<Intereses> listaIntereses = this.escenarioOptimista.getListaIntereses();
        if (listaIntereses!=null){
            for (Intereses i:listaIntereses){
                iterador = i.getListaCuotasAnuales();
                if (iterador!=null)
                    this.insertarFilaTabla("Intereses", 0, i.getListaCuotasAnuales(),modelo);
            }
        }
        //"UAI"
        if (this.escenarioOptimista.uaiCalculado())
            this.insertarFilaTabla("UAI", -1,this.escenarioOptimista.getUtilidadAntesImpuestos(),modelo);
        
        //"IVA por pagar"
        if (this.escenarioOptimista.ivaCalculado()){
            this.insertarFilaTabla("IVA por pagar", -1,this.escenarioOptimista.IVAporPagar(),modelo);
        }            
        
        // ISR
        if (this.escenarioOptimista.isrCalculado()){
            iterador = this.escenarioOptimista.ISRporPagarTemporal();
            if (iterador!=null)
                this.insertarFilaTabla("ISR temporal", -1,iterador,modelo);
            
        }
        //ISO
        if (this.escenarioOptimista.ISOcalculado()){
            iterador = this.escenarioOptimista.ISOporPagar();
            if (iterador!=null){
                this.insertarFilaTabla("ISR por pagar", -1,this.escenarioOptimista.ISRporPagar(),modelo);
                this.insertarFilaTabla("ISO", -1,iterador,modelo);
                
            }
        }
        //UTILIDAD NETA
        if (this.escenarioOptimista.getUtilidadNeta()!=null)
            this.insertarFilaTabla("Utilidad neta", -1,this.escenarioOptimista.getUtilidadNeta(),modelo);
        //FEN
        if (this.escenarioOptimista.getFEN()!=null)
            this.insertarFilaTabla("FEN", -1,this.escenarioOptimista.getFEN(),modelo);
        //VAN
        if (this.escenarioOptimista.getVAN()!=null){
            this.insertarFilaTabla("VAN", -1,this.escenarioOptimista.getVAN(),modelo);
            this.llenarDetalles();
        }
        ////// FIN DE LA PARTE DE MOSTRADO DE DATOS
        
    }
    private void llenarDatosEscenarioPesimista(){
        double [] iterador;
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Concepto");
        modelo.addColumn("Factura");
        for (int i=0; i<this.escenarioPesimista.getListaAnios().length; i++){
            modelo.addColumn(this.escenarioPesimista.getListaAnios()[i]);
        }
        this.tablaDatosPesimistas.setModel(modelo);
        
        //ingresos
        if (this.escenarioPesimista.ingresosCalculados())
            this.insertarFilaTabla("Ingresos",-1, this.escenarioPesimista.ingresosActuales(),modelo);

        //costos
        if (this.escenarioPesimista.costosCalculados())
            this.insertarFilaTabla("Costos",(this.escenarioPesimista.getModeloCostos().getFactura()) ? 1 : 0, this.escenarioPesimista.costosActuales(),modelo);

        //uitlidad bruta
        if (this.escenarioPesimista.utilidadBrutaCalculada())
            this.insertarFilaTabla("Utilidad bruta",-1, this.escenarioPesimista.getUtilidadBruta(),modelo);

        //gastos
        ArrayList<Gasto> listaGastos = this.escenarioPesimista.getListaGastos();
        if (listaGastos!=null){
            for(Gasto g : listaGastos){
                iterador = g.getListaGastos();
                if (iterador!=null)
                    this.insertarFilaTabla(g.getNombre(), (g.getFactura())? 1:0, iterador,modelo);
            }
        }
        //intereses
        ArrayList<Intereses> listaIntereses = this.escenarioPesimista.getListaIntereses();
        if (listaIntereses!=null){
            for (Intereses i:listaIntereses){
                iterador = i.getListaCuotasAnuales();
                if (iterador!=null)
                    this.insertarFilaTabla("Intereses", 0, i.getListaCuotasAnuales(),modelo);
            }
        }
        //"UAI"
        if (this.escenarioPesimista.uaiCalculado())
            this.insertarFilaTabla("UAI", -1,this.escenarioPesimista.getUtilidadAntesImpuestos(),modelo);
        
        //"IVA por pagar"
        if (this.escenarioPesimista.ivaCalculado()){
            this.insertarFilaTabla("IVA por pagar", -1,this.escenarioPesimista.IVAporPagar(),modelo);
        }            
        
        // ISR
        if (this.escenarioPesimista.isrCalculado()){
            iterador = this.escenarioPesimista.ISRporPagarTemporal();
            if (iterador!=null)
                this.insertarFilaTabla("ISR temporal", -1,iterador,modelo);
            
        }
        //ISO
        if (this.escenarioPesimista.ISOcalculado()){
            iterador = this.escenarioPesimista.ISOporPagar();
            if (iterador!=null){
                this.insertarFilaTabla("ISR por pagar", -1,this.escenarioPesimista.ISRporPagar(),modelo);
                this.insertarFilaTabla("ISO", -1,iterador,modelo);
                
            }
        }
        //UTILIDAD NETA
        if (this.escenarioPesimista.getUtilidadNeta()!=null)
            this.insertarFilaTabla("Utilidad neta", -1,this.escenarioPesimista.getUtilidadNeta(),modelo);
        //FEN
        if (this.escenarioPesimista.getFEN()!=null)
            this.insertarFilaTabla("FEN", -1,this.escenarioPesimista.getFEN(),modelo);
        //VAN
        if (this.escenarioPesimista.getVAN()!=null){
            this.insertarFilaTabla("VAN", -1,this.escenarioPesimista.getVAN(),modelo);
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
            
        //System.arraycopy(valores, 0, fila, 1, valores.length);
        for (int i=0; i<valores.length; i++){
            NumberFormat mf = NumberFormat.getInstance();
            mf.setMaximumFractionDigits(2);
            String resultado = mf.format(valores[i]);
            fila[i+2] = resultado;
        }
        //DefaultTableModel modelo = (DefaultTableModel) this.tablaPrincipal.getModel();
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

        panelPrincipal = new javax.swing.JTabbedPane();
        panelCaracteristicas = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        porcentajeAumentoIngresos = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        porcentajeDisminucionCostos = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        porcentajeDisminucionIngresos = new javax.swing.JTextField();
        porcentajeAumentoCostos = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        calcular = new javax.swing.JButton();
        panelDatosOptimistas = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaDatosOptimistas = new javax.swing.JTable();
        panelPesimista = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaDatosPesimistas = new javax.swing.JTable();
        panelResultados = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaResultados = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        panelPrincipal.setName("panelPrincipal"); // NOI18N

        panelCaracteristicas.setName("panelCaracteristicas"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(flujocaja.FlujoCajaApp.class).getContext().getResourceMap(TeoriaEscenarios.class);
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        porcentajeAumentoIngresos.setText(resourceMap.getString("porcentajeAumentoIngresos.text")); // NOI18N
        porcentajeAumentoIngresos.setName("porcentajeAumentoIngresos"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        porcentajeDisminucionCostos.setText(resourceMap.getString("porcentajeDisminucionCostos.text")); // NOI18N
        porcentajeDisminucionCostos.setName("porcentajeDisminucionCostos"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(porcentajeDisminucionCostos, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(porcentajeAumentoIngresos, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(porcentajeAumentoIngresos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(porcentajeDisminucionCostos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        porcentajeDisminucionIngresos.setText(resourceMap.getString("porcentajeDisminucionIngresos.text")); // NOI18N
        porcentajeDisminucionIngresos.setName("porcentajeDisminucionIngresos"); // NOI18N

        porcentajeAumentoCostos.setText(resourceMap.getString("porcentajeAumentoCostos.text")); // NOI18N
        porcentajeAumentoCostos.setName("porcentajeAumentoCostos"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(porcentajeAumentoCostos, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(porcentajeDisminucionIngresos, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(porcentajeDisminucionIngresos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(porcentajeAumentoCostos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel3.border.title"))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        calcular.setText(resourceMap.getString("calcular.text")); // NOI18N
        calcular.setName("calcular"); // NOI18N
        calcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcularActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelCaracteristicasLayout = new javax.swing.GroupLayout(panelCaracteristicas);
        panelCaracteristicas.setLayout(panelCaracteristicasLayout);
        panelCaracteristicasLayout.setHorizontalGroup(
            panelCaracteristicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCaracteristicasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCaracteristicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelCaracteristicasLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelCaracteristicasLayout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(calcular)))
                .addContainerGap(142, Short.MAX_VALUE))
        );
        panelCaracteristicasLayout.setVerticalGroup(
            panelCaracteristicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCaracteristicasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCaracteristicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCaracteristicasLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(calcular)
                        .addGap(163, 163, 163))
                    .addGroup(panelCaracteristicasLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(59, Short.MAX_VALUE))))
        );

        panelPrincipal.addTab(resourceMap.getString("panelCaracteristicas.TabConstraints.tabTitle"), panelCaracteristicas); // NOI18N

        panelDatosOptimistas.setName("panelDatosOptimistas"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tablaDatosOptimistas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaDatosOptimistas.setName("tablaDatosOptimistas"); // NOI18N
        jScrollPane1.setViewportView(tablaDatosOptimistas);

        javax.swing.GroupLayout panelDatosOptimistasLayout = new javax.swing.GroupLayout(panelDatosOptimistas);
        panelDatosOptimistas.setLayout(panelDatosOptimistasLayout);
        panelDatosOptimistasLayout.setHorizontalGroup(
            panelDatosOptimistasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosOptimistasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelDatosOptimistasLayout.setVerticalGroup(
            panelDatosOptimistasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosOptimistasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelPrincipal.addTab(resourceMap.getString("panelDatosOptimistas.TabConstraints.tabTitle"), panelDatosOptimistas); // NOI18N

        panelPesimista.setName("panelPesimista"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tablaDatosPesimistas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaDatosPesimistas.setName("tablaDatosPesimistas"); // NOI18N
        jScrollPane2.setViewportView(tablaDatosPesimistas);

        javax.swing.GroupLayout panelPesimistaLayout = new javax.swing.GroupLayout(panelPesimista);
        panelPesimista.setLayout(panelPesimistaLayout);
        panelPesimistaLayout.setHorizontalGroup(
            panelPesimistaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPesimistaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelPesimistaLayout.setVerticalGroup(
            panelPesimistaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPesimistaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelPrincipal.addTab(resourceMap.getString("panelPesimista.TabConstraints.tabTitle"), panelPesimista); // NOI18N

        panelResultados.setName("panelResultados"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        tablaResultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaResultados.setName("tablaResultados"); // NOI18N
        jScrollPane3.setViewportView(tablaResultados);

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelPrincipal.addTab(resourceMap.getString("panelResultados.TabConstraints.tabTitle"), panelResultados); // NOI18N

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N
        jMenuBar1.add(jMenu1);

        jMenu2.setText(resourceMap.getString("jMenu2.text")); // NOI18N
        jMenu2.setName("jMenu2"); // NOI18N
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelPrincipal)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void calcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcularActionPerformed
        // TODO add your handling code here:
        this.escenarioOptimista.setTasaDisminucionCostos(ModeloPorcentual.formatearPorcentaje(porcentajeDisminucionCostos.getText(), false));
        this.escenarioOptimista.setTasaIncrementoIngresos(ModeloPorcentual.formatearPorcentaje(porcentajeAumentoIngresos.getText(), false));
        
        this.escenarioPesimista.setTasaDisminucionIngresos(ModeloPorcentual.formatearPorcentaje(porcentajeDisminucionIngresos.getText(), false));
        this.escenarioPesimista.setTasaIncrementoCostos(ModeloPorcentual.formatearPorcentaje(porcentajeAumentoCostos.getText(), false));
        
        this.escenarioOptimista.plantearEscenario();
        this.escenarioPesimista.plantearEscenario();
        this.llenarDatos();
    }//GEN-LAST:event_calcularActionPerformed

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
            java.util.logging.Logger.getLogger(TeoriaEscenarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TeoriaEscenarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TeoriaEscenarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TeoriaEscenarios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                TeoriaEscenarios dialog = new TeoriaEscenarios(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton calcular;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel panelCaracteristicas;
    private javax.swing.JPanel panelDatosOptimistas;
    private javax.swing.JPanel panelPesimista;
    private javax.swing.JTabbedPane panelPrincipal;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JTextField porcentajeAumentoCostos;
    private javax.swing.JTextField porcentajeAumentoIngresos;
    private javax.swing.JTextField porcentajeDisminucionCostos;
    private javax.swing.JTextField porcentajeDisminucionIngresos;
    private javax.swing.JTable tablaDatosOptimistas;
    private javax.swing.JTable tablaDatosPesimistas;
    private javax.swing.JTable tablaResultados;
    // End of variables declaration//GEN-END:variables

    
}
