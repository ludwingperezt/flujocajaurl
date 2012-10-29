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
import Intermedias.Detalles;
import controlador.Escenario;
import controlador.EscenarioModificado;
import controlador.ModeloEscenarios;
import flujocaja.FormularioDatosExactos;
import java.text.NumberFormat;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author HP G42
 */
public class TeoriaEscenarios extends javax.swing.JDialog {
    
    //private Escenario escenarioNormal;
    private EscenarioModificado escenarioPuntual;
    private EscenarioModificado escenarioPesimista;
    private EscenarioModificado escenarioOptimista;

    /** Creates new form TeoriaEscenarios */
    public TeoriaEscenarios(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public void estimarEscenarios(Escenario normal){
        this.escenarioPuntual = new EscenarioModificado(normal);
        this.escenarioPuntual.setTipoEscenario(EscenarioModificado.ESCENARIO_NORMAL);
        this.escenarioPesimista = new EscenarioModificado(normal);
        this.escenarioPesimista.setTipoEscenario(EscenarioModificado.ESCENARIO_PESIMISTA);
        this.escenarioOptimista = new EscenarioModificado(normal);
        this.escenarioOptimista.setTipoEscenario(EscenarioModificado.ESCENARIO_OPTIMISTA);
        this.setVisible(true);
        
    }
    
    private void iniciarEscenarios(){
        this.escenarioPuntual.setTipoEscenario(EscenarioModificado.ESCENARIO_NORMAL);
        this.escenarioPesimista = new EscenarioModificado(escenarioPuntual);
        this.escenarioPesimista.setTipoEscenario(EscenarioModificado.ESCENARIO_PESIMISTA);
        this.escenarioOptimista = new EscenarioModificado(escenarioPuntual);
        this.escenarioOptimista.setTipoEscenario(EscenarioModificado.ESCENARIO_OPTIMISTA);
    }
    
    private void llenarDatos() {
        llenarDatosEscenarioOptimista();
        llenarDatosEscenarioPesimista();
        llenarDetalles();
    }
    
    private void llenarDetalles(){
        this.tmar.setText(this.escenarioPuntual.getTMARFormateada());
        this.inversionInicial.setText(Double.toString(ModeloPorcentual.redondearCifra(this.escenarioPuntual.getInversionInicial())));
        
        DefaultTableModel modelo = new DefaultTableModel();
        DefaultTableModel modeloArbol = new DefaultTableModel();
        
        //this.tmar.setText(null);
        
        modelo.addColumn("Escenario");
        modelo.addColumn("Payback");
        modelo.addColumn("VAN");
        modelo.addColumn("TIR");
        
        modeloArbol.addColumn("Escenario");
        modeloArbol.addColumn("Probabilidad");
        modeloArbol.addColumn("VAN ponderada");
        modeloArbol.addColumn("TIR ponderada");       
        
        tablaResultados.setModel(modelo);
        tablaArbol.setModel(modeloArbol);
        
        modelo.addRow(new Object[]{"Puntual",this.escenarioPuntual.getPayback_string(),ModeloPorcentual.redondearCifra(this.escenarioPuntual.getSumatoriaVAN()),this.escenarioPuntual.getTIR_string(),});
        modelo.addRow(new Object[]{"Optimista",this.escenarioOptimista.getPayback_string(),ModeloPorcentual.redondearCifra(this.escenarioOptimista.getSumatoriaVAN()),this.escenarioOptimista.getTIR_string()});
        modelo.addRow(new Object[]{"Pesimista",this.escenarioPesimista.getPayback_string(),ModeloPorcentual.redondearCifra(this.escenarioPesimista.getSumatoriaVAN()),this.escenarioPesimista.getTIR_string()});
        
        modeloArbol.addRow(new Object[]{"Puntual",ModeloPorcentual.redondearCifra(this.escenarioPuntual.getProbabilidad()*100)+"%",ModeloPorcentual.redondearCifra(this.escenarioPuntual.getVanPonderada()),ModeloPorcentual.redondearCifra(this.escenarioPuntual.getTirPonderada()*100)+"%"});
        modeloArbol.addRow(new Object[]{"Optimista",ModeloPorcentual.redondearCifra(this.escenarioOptimista.getProbabilidad()*100)+"%",ModeloPorcentual.redondearCifra(this.escenarioOptimista.getVanPonderada()),ModeloPorcentual.redondearCifra(this.escenarioOptimista.getTirPonderada()*100)+"%"});
        modeloArbol.addRow(new Object[]{"Pesimista",ModeloPorcentual.redondearCifra(this.escenarioPesimista.getProbabilidad()*100)+"%",ModeloPorcentual.redondearCifra(this.escenarioPesimista.getVanPonderada()),ModeloPorcentual.redondearCifra(this.escenarioPesimista.getTirPonderada()*100)+"%"});
        
        double intervaloTir = (this.escenarioOptimista.getTIR() + this.escenarioPesimista.getTIR());
        double intervaloVan = this.escenarioOptimista.getSumatoriaVAN() + this.escenarioPesimista.getSumatoriaVAN();
        
        double sumaTirPonderada = this.escenarioOptimista.getTirPonderada()+this.escenarioPesimista.getTirPonderada()+this.escenarioPuntual.getTirPonderada();
        double sumaVanPonderada = this.escenarioOptimista.getVanPonderada()+this.escenarioPesimista.getVanPonderada()+this.escenarioPuntual.getVanPonderada();
        double sumaPorcentajes = this.escenarioOptimista.getProbabilidad()+this.escenarioPesimista.getProbabilidad()+this.escenarioPuntual.getProbabilidad();
        String tir = "Err";
        String van = "Err";
        String tirPonderada = "";
        String vanPonderada = "";
        String porcentaje = "";
        try{
            NumberFormat mf = NumberFormat.getInstance();
            mf.setMaximumFractionDigits(2);
            tir = mf.format(intervaloTir*100)+"%";
            van = mf.format(intervaloVan);
            tirPonderada = mf.format(sumaTirPonderada*100)+"%";
            vanPonderada = mf.format(sumaVanPonderada);
            porcentaje = mf.format(sumaPorcentajes*100)+"%";
        }catch(Exception ex){
            
        }
        this.intervaloTIR.setText(tir);
        this.intervaloVAN.setText(van);
        modeloArbol.addRow(new Object[]{"Totales",porcentaje,vanPonderada,tirPonderada});
        
        
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
                    this.insertarFilaTabla(g.getNombreGasto(), (g.getFactura())? 1:0, iterador,modelo);
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
                    this.insertarFilaTabla(g.getNombreGasto(), (g.getFactura())? 1:0, iterador,modelo);
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
        jLabel5 = new javax.swing.JLabel();
        probablilidadOptimista = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        porcentajeDisminucionIngresos = new javax.swing.JTextField();
        porcentajeAumentoCostos = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        probabilidadPesimista = new javax.swing.JTextField();
        calcular = new javax.swing.JButton();
        probablilidadPuntual = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        panelDatosOptimistas = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaDatosOptimistas = new javax.swing.JTable();
        panelPesimista = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaDatosPesimistas = new javax.swing.JTable();
        panelResultados = new javax.swing.JPanel();
        intervaloTIR = new javax.swing.JTextField();
        intervaloVAN = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        panelResultados_1 = new javax.swing.JTabbedPane();
        panelEscenarios = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaResultados = new javax.swing.JTable();
        panelArbol = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tablaArbol = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        inversionInicial = new javax.swing.JTextField();
        tmar = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuArchivo = new javax.swing.JMenu();
        itemAbrirEscenarioOptimista = new javax.swing.JMenuItem();
        Guardar = new javax.swing.JMenuItem();
        menuHerramientas = new javax.swing.JMenu();
        itemDetallesOptimista = new javax.swing.JMenuItem();
        itemDetallesPuntual = new javax.swing.JMenuItem();
        itemDetallesPesimista = new javax.swing.JMenuItem();
        menuImpuestos = new javax.swing.JMenu();
        menuDetalleImpuestosOptimista = new javax.swing.JMenu();
        itemOptimistaIVA = new javax.swing.JMenuItem();
        itemOptimistaISR = new javax.swing.JMenuItem();
        itemOptimistaISO = new javax.swing.JMenuItem();
        menuDetallesImpuestosPuntual = new javax.swing.JMenu();
        itemPuntualIVA = new javax.swing.JMenuItem();
        itemPuntualISR = new javax.swing.JMenuItem();
        itemPuntualISO = new javax.swing.JMenuItem();
        menuDetalleImpuestosPesimista = new javax.swing.JMenu();
        itemPesimistaIVA = new javax.swing.JMenuItem();
        itemPesimistaISR = new javax.swing.JMenuItem();
        itemPesimistaISO = new javax.swing.JMenuItem();

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
        porcentajeDisminucionCostos.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                porcentajeDisminucionCostosFocusLost(evt);
            }
        });

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        probablilidadOptimista.setText(resourceMap.getString("probablilidadOptimista.text")); // NOI18N
        probablilidadOptimista.setName("probablilidadOptimista"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5))
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(porcentajeDisminucionCostos, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(porcentajeAumentoIngresos, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(probablilidadOptimista, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(probablilidadOptimista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addContainerGap())
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
        porcentajeAumentoCostos.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                porcentajeAumentoCostosFocusLost(evt);
            }
        });

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        probabilidadPesimista.setText(resourceMap.getString("probabilidadPesimista.text")); // NOI18N
        probabilidadPesimista.setName("probabilidadPesimista"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(porcentajeAumentoCostos, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(porcentajeDisminucionIngresos, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                    .addComponent(probabilidadPesimista, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(probabilidadPesimista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        calcular.setText(resourceMap.getString("calcular.text")); // NOI18N
        calcular.setName("calcular"); // NOI18N
        calcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcularActionPerformed(evt);
            }
        });

        probablilidadPuntual.setText(resourceMap.getString("probablilidadPuntual.text")); // NOI18N
        probablilidadPuntual.setName("probablilidadPuntual"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        javax.swing.GroupLayout panelCaracteristicasLayout = new javax.swing.GroupLayout(panelCaracteristicas);
        panelCaracteristicas.setLayout(panelCaracteristicasLayout);
        panelCaracteristicasLayout.setHorizontalGroup(
            panelCaracteristicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCaracteristicasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCaracteristicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCaracteristicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(calcular)
                        .addGroup(panelCaracteristicasLayout.createSequentialGroup()
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(10, 10, 10)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelCaracteristicasLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(probablilidadPuntual, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        panelCaracteristicasLayout.setVerticalGroup(
            panelCaracteristicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCaracteristicasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCaracteristicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(probablilidadPuntual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCaracteristicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(calcular)
                .addGap(144, 144, 144))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelDatosOptimistasLayout.setVerticalGroup(
            panelDatosOptimistasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDatosOptimistasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelPesimistaLayout.setVerticalGroup(
            panelPesimistaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPesimistaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelPrincipal.addTab(resourceMap.getString("panelPesimista.TabConstraints.tabTitle"), panelPesimista); // NOI18N

        panelResultados.setName("panelResultados"); // NOI18N

        intervaloTIR.setText(resourceMap.getString("intervaloTIR.text")); // NOI18N
        intervaloTIR.setName("intervaloTIR"); // NOI18N

        intervaloVAN.setText(resourceMap.getString("intervaloVAN.text")); // NOI18N
        intervaloVAN.setName("intervaloVAN"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        panelResultados_1.setName("panelResultados_1"); // NOI18N

        panelEscenarios.setName("panelEscenarios"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        tablaResultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaResultados.setName("tablaResultados"); // NOI18N
        jScrollPane3.setViewportView(tablaResultados);

        javax.swing.GroupLayout panelEscenariosLayout = new javax.swing.GroupLayout(panelEscenarios);
        panelEscenarios.setLayout(panelEscenariosLayout);
        panelEscenariosLayout.setHorizontalGroup(
            panelEscenariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEscenariosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelEscenariosLayout.setVerticalGroup(
            panelEscenariosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEscenariosLayout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelResultados_1.addTab(resourceMap.getString("panelEscenarios.TabConstraints.tabTitle"), panelEscenarios); // NOI18N

        panelArbol.setName("panelArbol"); // NOI18N

        jScrollPane4.setName("jScrollPane4"); // NOI18N

        tablaArbol.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaArbol.setName("tablaArbol"); // NOI18N
        jScrollPane4.setViewportView(tablaArbol);

        javax.swing.GroupLayout panelArbolLayout = new javax.swing.GroupLayout(panelArbol);
        panelArbol.setLayout(panelArbolLayout);
        panelArbolLayout.setHorizontalGroup(
            panelArbolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArbolLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelArbolLayout.setVerticalGroup(
            panelArbolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelArbolLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelResultados_1.addTab(resourceMap.getString("panelArbol.TabConstraints.tabTitle"), panelArbol); // NOI18N

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        inversionInicial.setEditable(false);
        inversionInicial.setText(resourceMap.getString("inversionInicial.text")); // NOI18N
        inversionInicial.setName("inversionInicial"); // NOI18N

        tmar.setEditable(false);
        tmar.setText(resourceMap.getString("tmar.text")); // NOI18N
        tmar.setName("tmar"); // NOI18N

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        javax.swing.GroupLayout panelResultadosLayout = new javax.swing.GroupLayout(panelResultados);
        panelResultados.setLayout(panelResultadosLayout);
        panelResultadosLayout.setHorizontalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelResultados_1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
                    .addGroup(panelResultadosLayout.createSequentialGroup()
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addGap(16, 16, 16)
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(intervaloTIR, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(intervaloVAN, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 218, Short.MAX_VALUE)
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10)
                            .addGroup(panelResultadosLayout.createSequentialGroup()
                                .addGap(45, 45, 45)
                                .addComponent(jLabel11)))
                        .addGap(10, 10, 10)
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(inversionInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tmar, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        panelResultadosLayout.setVerticalGroup(
            panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelResultadosLayout.createSequentialGroup()
                .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelResultadosLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(inversionInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(tmar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelResultadosLayout.createSequentialGroup()
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(intervaloTIR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(intervaloVAN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelResultados_1, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelPrincipal.addTab(resourceMap.getString("panelResultados.TabConstraints.tabTitle"), panelResultados); // NOI18N

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        menuArchivo.setText(resourceMap.getString("menuArchivo.text")); // NOI18N
        menuArchivo.setName("menuArchivo"); // NOI18N

        itemAbrirEscenarioOptimista.setText(resourceMap.getString("itemAbrirEscenarioOptimista.text")); // NOI18N
        itemAbrirEscenarioOptimista.setName("itemAbrirEscenarioOptimista"); // NOI18N
        itemAbrirEscenarioOptimista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemAbrirEscenarioOptimistaActionPerformed(evt);
            }
        });
        menuArchivo.add(itemAbrirEscenarioOptimista);

        Guardar.setText(resourceMap.getString("Guardar.text")); // NOI18N
        Guardar.setName("Guardar"); // NOI18N
        Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GuardarActionPerformed(evt);
            }
        });
        menuArchivo.add(Guardar);

        jMenuBar1.add(menuArchivo);

        menuHerramientas.setText(resourceMap.getString("menuHerramientas.text")); // NOI18N
        menuHerramientas.setName("menuHerramientas"); // NOI18N

        itemDetallesOptimista.setText(resourceMap.getString("itemDetallesOptimista.text")); // NOI18N
        itemDetallesOptimista.setName("itemDetallesOptimista"); // NOI18N
        itemDetallesOptimista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemDetallesOptimistaActionPerformed(evt);
            }
        });
        menuHerramientas.add(itemDetallesOptimista);

        itemDetallesPuntual.setText(resourceMap.getString("itemDetallesPuntual.text")); // NOI18N
        itemDetallesPuntual.setName("itemDetallesPuntual"); // NOI18N
        itemDetallesPuntual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemDetallesPuntualActionPerformed(evt);
            }
        });
        menuHerramientas.add(itemDetallesPuntual);

        itemDetallesPesimista.setText(resourceMap.getString("itemDetallesPesimista.text")); // NOI18N
        itemDetallesPesimista.setName("itemDetallesPesimista"); // NOI18N
        itemDetallesPesimista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemDetallesPesimistaActionPerformed(evt);
            }
        });
        menuHerramientas.add(itemDetallesPesimista);

        jMenuBar1.add(menuHerramientas);

        menuImpuestos.setText(resourceMap.getString("menuImpuestos.text")); // NOI18N
        menuImpuestos.setName("menuImpuestos"); // NOI18N

        menuDetalleImpuestosOptimista.setText(resourceMap.getString("menuDetalleImpuestosOptimista.text")); // NOI18N
        menuDetalleImpuestosOptimista.setName("menuDetalleImpuestosOptimista"); // NOI18N

        itemOptimistaIVA.setText(resourceMap.getString("itemOptimistaIVA.text")); // NOI18N
        itemOptimistaIVA.setName("itemOptimistaIVA"); // NOI18N
        itemOptimistaIVA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemOptimistaIVAActionPerformed(evt);
            }
        });
        menuDetalleImpuestosOptimista.add(itemOptimistaIVA);

        itemOptimistaISR.setText(resourceMap.getString("itemOptimistaISR.text")); // NOI18N
        itemOptimistaISR.setName("itemOptimistaISR"); // NOI18N
        itemOptimistaISR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemOptimistaISRActionPerformed(evt);
            }
        });
        menuDetalleImpuestosOptimista.add(itemOptimistaISR);

        itemOptimistaISO.setText(resourceMap.getString("itemOptimistaISO.text")); // NOI18N
        itemOptimistaISO.setName("itemOptimistaISO"); // NOI18N
        itemOptimistaISO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemOptimistaISOActionPerformed(evt);
            }
        });
        menuDetalleImpuestosOptimista.add(itemOptimistaISO);

        menuImpuestos.add(menuDetalleImpuestosOptimista);

        menuDetallesImpuestosPuntual.setText(resourceMap.getString("menuDetallesImpuestosPuntual.text")); // NOI18N
        menuDetallesImpuestosPuntual.setName("menuDetallesImpuestosPuntual"); // NOI18N

        itemPuntualIVA.setText(resourceMap.getString("itemPuntualIVA.text")); // NOI18N
        itemPuntualIVA.setName("itemPuntualIVA"); // NOI18N
        itemPuntualIVA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemPuntualIVAActionPerformed(evt);
            }
        });
        menuDetallesImpuestosPuntual.add(itemPuntualIVA);

        itemPuntualISR.setText(resourceMap.getString("itemPuntualISR.text")); // NOI18N
        itemPuntualISR.setName("itemPuntualISR"); // NOI18N
        itemPuntualISR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemPuntualISRActionPerformed(evt);
            }
        });
        menuDetallesImpuestosPuntual.add(itemPuntualISR);

        itemPuntualISO.setText(resourceMap.getString("itemPuntualISO.text")); // NOI18N
        itemPuntualISO.setName("itemPuntualISO"); // NOI18N
        itemPuntualISO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemPuntualISOActionPerformed(evt);
            }
        });
        menuDetallesImpuestosPuntual.add(itemPuntualISO);

        menuImpuestos.add(menuDetallesImpuestosPuntual);

        menuDetalleImpuestosPesimista.setText(resourceMap.getString("menuDetalleImpuestosPesimista.text")); // NOI18N
        menuDetalleImpuestosPesimista.setName("menuDetalleImpuestosPesimista"); // NOI18N

        itemPesimistaIVA.setText(resourceMap.getString("itemPesimistaIVA.text")); // NOI18N
        itemPesimistaIVA.setName("itemPesimistaIVA"); // NOI18N
        itemPesimistaIVA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemPesimistaIVAActionPerformed(evt);
            }
        });
        menuDetalleImpuestosPesimista.add(itemPesimistaIVA);

        itemPesimistaISR.setText(resourceMap.getString("itemPesimistaISR.text")); // NOI18N
        itemPesimistaISR.setName("itemPesimistaISR"); // NOI18N
        itemPesimistaISR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemPesimistaISRActionPerformed(evt);
            }
        });
        menuDetalleImpuestosPesimista.add(itemPesimistaISR);

        itemPesimistaISO.setText(resourceMap.getString("itemPesimistaISO.text")); // NOI18N
        itemPesimistaISO.setName("itemPesimistaISO"); // NOI18N
        itemPesimistaISO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemPesimistaISOActionPerformed(evt);
            }
        });
        menuDetalleImpuestosPesimista.add(itemPesimistaISO);

        menuImpuestos.add(menuDetalleImpuestosPesimista);

        jMenuBar1.add(menuImpuestos);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void calcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcularActionPerformed
        // TODO add your handling code here:
        this.iniciarEscenarios();
        
        this.escenarioOptimista.setTasaDisminucionCostos(ModeloPorcentual.formatearPorcentaje(porcentajeDisminucionCostos.getText(), false,false));
        this.escenarioOptimista.setTasaIncrementoIngresos(ModeloPorcentual.formatearPorcentaje(porcentajeAumentoIngresos.getText(), false,false));
        
        this.escenarioPesimista.setTasaDisminucionIngresos(ModeloPorcentual.formatearPorcentaje(porcentajeDisminucionIngresos.getText(), false,false));
        this.escenarioPesimista.setTasaIncrementoCostos(ModeloPorcentual.formatearPorcentaje(porcentajeAumentoCostos.getText(), false,false));
        
        double pPuntual =   ModeloPorcentual.formatearPorcentaje(probablilidadPuntual.getText(),false);
        double pOptimista = ModeloPorcentual.formatearPorcentaje(probablilidadOptimista.getText(),false);
        double pPesimista = ModeloPorcentual.formatearPorcentaje(probabilidadPesimista.getText(),false);
        double sum = pPuntual + pOptimista + pPesimista;
        if (sum==1){
            this.escenarioPuntual.setProbabilidad(pPuntual);
            this.escenarioOptimista.setProbabilidad(pOptimista);
            this.escenarioPesimista.setProbabilidad(pPesimista);

//            this.escenarioOptimista.getModeloIngresos().setListaIngresos(this.escenarioPuntual.getModeloIngresos().getListaIngresos());
//            this.escenarioPesimista.getModeloIngresos().setListaIngresos(this.escenarioPuntual.getModeloIngresos().getListaIngresos());
//            this.escenarioOptimista.getModeloCostos().setCostos(this.escenarioPuntual.getModeloCostos().getCostos());
//            this.escenarioPesimista.getModeloCostos().setCostos(this.escenarioPuntual.getModeloCostos().getCostos());
            
            this.escenarioOptimista.plantearEscenario();
            this.escenarioPesimista.plantearEscenario();
            this.escenarioPuntual.plantearEscenario();
            this.llenarDatos();
        }
        else{
            JOptionPane.showMessageDialog(null, "Las probabilidades de los escenarios no suman 100%", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        
    }//GEN-LAST:event_calcularActionPerformed

    private void porcentajeDisminucionCostosFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_porcentajeDisminucionCostosFocusLost
        // TODO add your handling code here:
        this.probablilidadOptimista.selectAll();
    }//GEN-LAST:event_porcentajeDisminucionCostosFocusLost

    private void porcentajeAumentoCostosFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_porcentajeAumentoCostosFocusLost
        // TODO add your handling code here:
        this.probabilidadPesimista.selectAll();
    }//GEN-LAST:event_porcentajeAumentoCostosFocusLost

    private void itemDetallesOptimistaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemDetallesOptimistaActionPerformed
        // TODO add your handling code here:
        FormularioDatosExactos fde = new FormularioDatosExactos(null, false);
        fde.mostrarDatosExactos((Escenario)escenarioOptimista);
    }//GEN-LAST:event_itemDetallesOptimistaActionPerformed

    private void itemDetallesPuntualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemDetallesPuntualActionPerformed
        // TODO add your handling code here:
        FormularioDatosExactos fde = new FormularioDatosExactos(null, false);
        fde.mostrarDatosExactos((Escenario)escenarioPuntual);
    }//GEN-LAST:event_itemDetallesPuntualActionPerformed

    private void itemDetallesPesimistaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemDetallesPesimistaActionPerformed
        // TODO add your handling code here:
        FormularioDatosExactos fde = new FormularioDatosExactos(null, false);
        fde.mostrarDatosExactos((Escenario)escenarioPesimista);
    }//GEN-LAST:event_itemDetallesPesimistaActionPerformed

    private void itemOptimistaIVAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemOptimistaIVAActionPerformed
        // TODO add your handling code here:
        Detalles d = new Detalles(null, false);
        d.verDetallesCalculoIVA((Escenario)escenarioOptimista);
    }//GEN-LAST:event_itemOptimistaIVAActionPerformed

    private void itemOptimistaISRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemOptimistaISRActionPerformed
        // TODO add your handling code here:
        Detalles d = new Detalles(null, false);
        d.verDetallesCalculoISR((Escenario)escenarioOptimista);
    }//GEN-LAST:event_itemOptimistaISRActionPerformed

    private void itemOptimistaISOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemOptimistaISOActionPerformed
        // TODO add your handling code here:
        Detalles d = new Detalles(null, false);
        d.verDetallesCalculoISO((Escenario)escenarioOptimista);
    }//GEN-LAST:event_itemOptimistaISOActionPerformed

    private void itemPesimistaIVAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemPesimistaIVAActionPerformed
        // TODO add your handling code here:
        Detalles d = new Detalles(null, false);
        d.verDetallesCalculoIVA((Escenario)escenarioPesimista);
    }//GEN-LAST:event_itemPesimistaIVAActionPerformed

    private void itemPesimistaISRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemPesimistaISRActionPerformed
        // TODO add your handling code here:
        Detalles d = new Detalles(null, false);
        d.verDetallesCalculoISR((Escenario)escenarioPesimista);
    }//GEN-LAST:event_itemPesimistaISRActionPerformed

    private void itemPesimistaISOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemPesimistaISOActionPerformed
        // TODO add your handling code here:
        Detalles d = new Detalles(null, false);
        d.verDetallesCalculoISO((Escenario)escenarioPesimista);
    }//GEN-LAST:event_itemPesimistaISOActionPerformed

    private void itemPuntualIVAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemPuntualIVAActionPerformed
        // TODO add your handling code here:
        Detalles d = new Detalles(null, false);
        d.verDetallesCalculoIVA((Escenario)escenarioPuntual);
    }//GEN-LAST:event_itemPuntualIVAActionPerformed

    private void itemPuntualISRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemPuntualISRActionPerformed
        // TODO add your handling code here:
        Detalles d = new Detalles(null, false);
        d.verDetallesCalculoISR((Escenario)escenarioPuntual);
    }//GEN-LAST:event_itemPuntualISRActionPerformed

    private void itemPuntualISOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemPuntualISOActionPerformed
        // TODO add your handling code here:
        Detalles d = new Detalles(null, false);
        d.verDetallesCalculoISO((Escenario)escenarioPuntual);
    }//GEN-LAST:event_itemPuntualISOActionPerformed

    private void itemAbrirEscenarioOptimistaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAbrirEscenarioOptimistaActionPerformed
        // TODO add your handling code here:        
                
        JFileChooser selector = new JFileChooser();
        int resultado = selector.showOpenDialog(null);
        if (resultado==JFileChooser.APPROVE_OPTION){
            ModeloEscenarios me = ModeloEscenarios.deserializarDeXML(selector.getSelectedFile().getAbsolutePath());
            /*
             * setear las opciones primero, antes de recargar los datos
             */
            this.escenarioOptimista = me.getOptimista();
            this.escenarioPesimista = me.getPesimista();
            this.escenarioPuntual = me.getPuntual();
            
            this.probablilidadPuntual.setText(Double.toString(this.escenarioPuntual.getProbabilidad()));
            
            this.probablilidadOptimista.setText(Double.toString(this.escenarioOptimista.getProbabilidad()));
            this.porcentajeAumentoIngresos.setText(Double.toString(this.escenarioOptimista.getTasaIncrementoIngresos()));
            this.porcentajeDisminucionCostos.setText(Double.toString(this.escenarioOptimista.getTasaDisminucionCostos()));
            
            this.probabilidadPesimista.setText(Double.toString(this.escenarioPesimista.getProbabilidad()));
            this.porcentajeDisminucionIngresos.setText(Double.toString(this.escenarioPesimista.getTasaDisminucionIngresos()));
            this.porcentajeAumentoCostos.setText(Double.toString(this.escenarioPesimista.getTasaIncrementoCostos()));
        }       
    }//GEN-LAST:event_itemAbrirEscenarioOptimistaActionPerformed

    private void GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GuardarActionPerformed
        // TODO add your handling code here:
        JFileChooser selector = new JFileChooser();
        int resultado = selector.showSaveDialog(null);
        if (resultado==JFileChooser.APPROVE_OPTION){
            ModeloEscenarios me = new ModeloEscenarios();
            me.setOptimista(escenarioOptimista);
            me.setPesimista(escenarioPesimista);
            me.setPuntual(escenarioPuntual);
            me.serializarAXML(selector.getSelectedFile().getAbsolutePath());
        }    
    }//GEN-LAST:event_GuardarActionPerformed

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
    private javax.swing.JMenuItem Guardar;
    private javax.swing.JButton calcular;
    private javax.swing.JTextField intervaloTIR;
    private javax.swing.JTextField intervaloVAN;
    private javax.swing.JTextField inversionInicial;
    private javax.swing.JMenuItem itemAbrirEscenarioOptimista;
    private javax.swing.JMenuItem itemDetallesOptimista;
    private javax.swing.JMenuItem itemDetallesPesimista;
    private javax.swing.JMenuItem itemDetallesPuntual;
    private javax.swing.JMenuItem itemOptimistaISO;
    private javax.swing.JMenuItem itemOptimistaISR;
    private javax.swing.JMenuItem itemOptimistaIVA;
    private javax.swing.JMenuItem itemPesimistaISO;
    private javax.swing.JMenuItem itemPesimistaISR;
    private javax.swing.JMenuItem itemPesimistaIVA;
    private javax.swing.JMenuItem itemPuntualISO;
    private javax.swing.JMenuItem itemPuntualISR;
    private javax.swing.JMenuItem itemPuntualIVA;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JMenu menuArchivo;
    private javax.swing.JMenu menuDetalleImpuestosOptimista;
    private javax.swing.JMenu menuDetalleImpuestosPesimista;
    private javax.swing.JMenu menuDetallesImpuestosPuntual;
    private javax.swing.JMenu menuHerramientas;
    private javax.swing.JMenu menuImpuestos;
    private javax.swing.JPanel panelArbol;
    private javax.swing.JPanel panelCaracteristicas;
    private javax.swing.JPanel panelDatosOptimistas;
    private javax.swing.JPanel panelEscenarios;
    private javax.swing.JPanel panelPesimista;
    private javax.swing.JTabbedPane panelPrincipal;
    private javax.swing.JPanel panelResultados;
    private javax.swing.JTabbedPane panelResultados_1;
    private javax.swing.JTextField porcentajeAumentoCostos;
    private javax.swing.JTextField porcentajeAumentoIngresos;
    private javax.swing.JTextField porcentajeDisminucionCostos;
    private javax.swing.JTextField porcentajeDisminucionIngresos;
    private javax.swing.JTextField probabilidadPesimista;
    private javax.swing.JTextField probablilidadOptimista;
    private javax.swing.JTextField probablilidadPuntual;
    private javax.swing.JTable tablaArbol;
    private javax.swing.JTable tablaDatosOptimistas;
    private javax.swing.JTable tablaDatosPesimistas;
    private javax.swing.JTable tablaResultados;
    private javax.swing.JTextField tmar;
    // End of variables declaration//GEN-END:variables

    
}
