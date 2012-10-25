/*
 * FlujoCajaView.java
 */

package flujocaja;

import Clases.*;
import Intermedias.Detalles;
import Intermedias.SeleccionCostos;
import Intermedias.SeleccionGastos;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.JFrame;
import controlador.Escenario;
import herramientas.TeoriaEscenarios;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modificaciones.ModificarModeloCostos;
import modificaciones.ModificarModeloGastos;
import modificaciones.ModificarModeloIntereses;

/**
 * The application's main frame.
 */
public class FlujoCajaView extends FrameView {

    private Escenario escenarioNormal;
    private Map<Integer,String> mapa;

    public FlujoCajaView(SingleFrameApplication app) {
        super(app);

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
        this.Comenzar();
    }

    private void Comenzar()
    {
        escenarioNormal = new Escenario();
        mapa = new HashMap<Integer, String>();
        menuTMAR.setVisible(false);
        menuVariables.setVisible(false);
        menuImpuestos.setVisible(false);
        panelTabs.setVisible(false);
        menuOperaciones.setVisible(false);
        menuHerramientas.setVisible(false);
    }

    private boolean buscarCuenta(String nombre){
        return mapa.containsValue(nombre);
    }

    
    private void insertarFilaTablaPrincipal(String nombre,boolean factura,double [] valores){
        Object [] fila = new Object[valores.length+2];
        fila[0] = nombre;
        if (factura)
            fila[1] = "S";
        else
            fila[1] = "N";
        //System.arraycopy(valores, 0, fila, 1, valores.length);
        for (int i=0; i<valores.length; i++){
            NumberFormat mf = NumberFormat.getInstance();
            mf.setMaximumFractionDigits(2);
            String resultado = mf.format(valores[i]);
            fila[i+2] = resultado;
        }
        DefaultTableModel modelo = (DefaultTableModel) this.tablaPrincipal.getModel();
        modelo.addRow(fila);
        mapa.put(modelo.getRowCount()-1, nombre);
    }
    //usada para los intereses
    private void insertarInteresTablaPrincipal(String nombre,boolean val,double [] valores){
        Object [] fila = new Object[valores.length+2];
        fila[0] = "Intereses";
        fila[1] = val;
        //System.arraycopy(valores, 0, fila, 1, valores.length);
        for (int i=0; i<valores.length; i++){
            NumberFormat mf = NumberFormat.getInstance();
            mf.setMaximumFractionDigits(2);
            String resultado = mf.format(valores[i]);
            fila[i+2] = resultado;
        }
        DefaultTableModel modelo = (DefaultTableModel) this.tablaPrincipal.getModel();
        modelo.addRow(fila);
        mapa.put(modelo.getRowCount()-1, nombre);
    }

    private void insertarFilaTablaPrincipal(String nombre,double [] valores){
        Object [] fila = new Object[valores.length+2];
        fila[0] = nombre;
        fila[1] = null;
        //System.arraycopy(valores, 0, fila, 1, valores.length);
        for (int i=0; i<valores.length; i++){
            NumberFormat mf = NumberFormat.getInstance();
            mf.setMaximumFractionDigits(2);
            String resultado = mf.format(valores[i]);
            fila[i+2] = resultado;
        }
        DefaultTableModel modelo = (DefaultTableModel) this.tablaPrincipal.getModel();
        modelo.addRow(fila);
        mapa.put(modelo.getRowCount()-1, nombre);
    }
    
    //usada para la tmar
    private void insertarFilaTablaPrincipal(String nombre,String valor){
        DefaultTableModel modelo = (DefaultTableModel) this.tablaPrincipal.getModel();
        int columnCount = modelo.getColumnCount();
        Object [] fila = new Object[this.tablaPrincipal.getColumnCount()+2];
        fila[0] = nombre;
        fila[1] = null;
        //System.arraycopy(valores, 0, fila, 1, valores.length);
        for (int i=2; i<fila.length; i++){
            fila[i] = valor;
        }
        
        modelo.addRow(fila);
        mapa.put(modelo.getRowCount()-1, nombre);
    }
    

    private void setOpcionesDeEscenario(){
        this.escenarioNormal.setEmpresaNueva(opcionEmpresaNueva.isSelected());
        this.escenarioNormal.setPatenteDeComercio(opcionPatente.isSelected());
        this.escenarioNormal.setDatosNetos(opcionDatosNetos.isSelected());
        this.escenarioNormal.setEmpresaIndividual(opcionEmpresaIndividual.isSelected());
        
        if (opcionInversionMasActivos.isSelected())
            this.escenarioNormal.setTipoInversionInicial(Escenario.INVERSION_INICIAL_ACTIVOS_MAS_PRESTAMOS);
        else if (opcionSoloInversion.isSelected())
            this.escenarioNormal.setTipoInversionInicial(Escenario.INVERSION_INICIAL_SOLO_PRESTAMOS);
        else if (opcionSoloActivos.isSelected())
            this.escenarioNormal.setTipoInversionInicial(Escenario.INVERSION_INICIAL_SOLO_ACTIVOS);
        else if (opcionManual.isSelected())
            this.escenarioNormal.setTipoInversionInicial(Escenario.INVERSION_INICIAL_MANUAL);
    }
    
    private void recargarDatos(){
        ///// PARTE DE ESTABLECIMIENTO DE CARACTERISTICAS GENERALES
        this.setOpcionesDeEscenario();
        
        if (this.escenarioNormal.ivaCalculado())
            this.escenarioNormal.setDatosNetos(opcionDatosNetos.isSelected());
        
        if (this.escenarioNormal.isrCalculado()){
            this.escenarioNormal.setEmpresaIndividual(opcionEmpresaIndividual.isSelected());
            
            if (this.escenarioNormal.ISOcalculado()){
                if (opcionISOaISR.isSelected())
                    this.escenarioNormal.getModeloISO().setAcreditacion(ISO.ISO_ACREDITABLE_ISR);
                else
                    this.escenarioNormal.getModeloISO().setAcreditacion(ISO.ISR_ACREDITABLE_ISO);
            }
        }
        ////// FIN DE LA PARTE DE CARACTERISTICAS GENERALES
        
        ////// PARTE DE CÁLCULOS
        this.escenarioNormal.recalcularTodo();
        ////// FIN DE LA PARTE DE CÁLCULOS
        
        ////// PARTE DE MOSTRADO DE DATOS
        mapa.clear();
        
        this.inversionInicial.setText(Double.toString(ModeloPorcentual.redondearCifra(this.escenarioNormal.getInversionInicial())));
        
        double [] iterador;
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Concepto");
        modelo.addColumn("Factura");
        for (int i=0; i<this.escenarioNormal.getListaAnios().length; i++){
            modelo.addColumn(this.escenarioNormal.getListaAnios()[i]);
        }
        this.tablaPrincipal.setModel(modelo);
        
        //ingresos
        if (this.escenarioNormal.ingresosCalculados())
            this.insertarFilaTablaPrincipal("Ingresos", this.escenarioNormal.ingresosActuales());

        //costos
        if (this.escenarioNormal.costosCalculados())
            this.insertarFilaTablaPrincipal("Costos", this.escenarioNormal.getModeloCostos().getFactura(), this.escenarioNormal.costosActuales());

        //uitlidad bruta
        if (this.escenarioNormal.utilidadBrutaCalculada())
            this.insertarFilaTablaPrincipal("Utilidad bruta", this.escenarioNormal.getUtilidadBruta());

        //gastos
        ArrayList<Gasto> listaGastos = this.escenarioNormal.getListaGastos();
        if (listaGastos!=null){
            for(Gasto g : listaGastos){
                iterador = g.getListaGastos();
                if (iterador!=null)
                    this.insertarFilaTablaPrincipal(g.getNombreGasto(), g.getFactura(), iterador);
            }
        }
        //intereses
        ArrayList<Intereses> listaIntereses = this.escenarioNormal.getListaIntereses();
        if (listaIntereses!=null){
            for (Intereses i:listaIntereses){
                iterador = i.getListaCuotasAnuales();
                if (iterador!=null)
                    this.insertarFilaTablaPrincipal("Intereses", false, i.getListaCuotasAnuales());
            }
        }
        //"UAI"
        if (this.escenarioNormal.uaiCalculado())
            this.insertarFilaTablaPrincipal("UAI", this.escenarioNormal.getUtilidadAntesImpuestos());
        
        //"IVA por pagar"
        if (this.escenarioNormal.ivaCalculado()){
            this.insertarFilaTablaPrincipal("IVA por pagar", this.escenarioNormal.IVAporPagar());
        }            
        
        // ISR
        if (this.escenarioNormal.isrCalculado()){
            iterador = this.escenarioNormal.ISRporPagarTemporal();
            if (iterador!=null)
                this.insertarFilaTablaPrincipal("ISR temporal", iterador);
            
        }
        //ISO
        if (this.escenarioNormal.ISOcalculado()){
            iterador = this.escenarioNormal.ISOporPagar();
            if (iterador!=null){
                this.insertarFilaTablaPrincipal("ISR por pagar", this.escenarioNormal.ISRporPagar());
                this.insertarFilaTablaPrincipal("ISO", iterador);
                
            }
        }
        //UTILIDAD NETA
        if (this.escenarioNormal.getUtilidadNeta()!=null)
            this.insertarFilaTablaPrincipal("Utilidad neta", this.escenarioNormal.getUtilidadNeta());
        //FEN
        if (this.escenarioNormal.getFEN()!=null)
            this.insertarFilaTablaPrincipal("FEN", this.escenarioNormal.getFEN());
        //TMAR
        if (this.escenarioNormal.getTmar()!=null)
            this.insertarFilaTablaPrincipal("TMAR",this.escenarioNormal.getTMARFormateada());
        //VAN
        if (this.escenarioNormal.getVAN()!=null){
            this.insertarFilaTablaPrincipal("VAN", this.escenarioNormal.getVAN());
            this.llenarDetalles();
        }
        ////// FIN DE LA PARTE DE MOSTRADO DE DATOS
        
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = FlujoCajaApp.getApplication().getMainFrame();
            aboutBox = new FlujoCajaAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        FlujoCajaApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        panelTabs = new javax.swing.JTabbedPane();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tablaPrincipal = new javax.swing.JTable();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        panelPrincipal = new javax.swing.JTabbedPane();
        panelAcciones = new javax.swing.JPanel();
        botonIngresos = new javax.swing.JButton();
        botonCostos = new javax.swing.JButton();
        botonGastos = new javax.swing.JButton();
        calcularUtilidaBruta = new javax.swing.JButton();
        calcularUAI = new javax.swing.JButton();
        calcularIVA = new javax.swing.JButton();
        calcularISR = new javax.swing.JButton();
        iso = new javax.swing.JButton();
        botonInteres = new javax.swing.JButton();
        utilidadNeta = new javax.swing.JButton();
        flujoEfectivoNeto = new javax.swing.JButton();
        calcularVAN = new javax.swing.JButton();
        calcularTodo = new javax.swing.JButton();
        botonTMAR1 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        panelCaracteristicas = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        opcionDatosNetos = new javax.swing.JRadioButton();
        opcionDatosBrutos = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        opcionEmpresaIndividual = new javax.swing.JRadioButton();
        opcionEmpresaJuridica = new javax.swing.JRadioButton();
        panelEmpresaNueva = new javax.swing.JPanel();
        opcionEmpresaNueva = new javax.swing.JRadioButton();
        opcionEmpresaNoNueva = new javax.swing.JRadioButton();
        panelPatente = new javax.swing.JPanel();
        opcionPatente = new javax.swing.JRadioButton();
        opcionSinPatente = new javax.swing.JRadioButton();
        panelISO = new javax.swing.JPanel();
        opcionISOaISR = new javax.swing.JRadioButton();
        opcionISRaISO = new javax.swing.JRadioButton();
        panelDetalles = new javax.swing.JPanel();
        verDetalles = new javax.swing.JRadioButton();
        noVerDetalles = new javax.swing.JRadioButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        escala = new javax.swing.JTextField();
        activosNuevos = new javax.swing.JTextField();
        activosAnteriores = new javax.swing.JTextField();
        cargarDetalles = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        opcionInversionMasActivos = new javax.swing.JRadioButton();
        opcionSoloInversion = new javax.swing.JRadioButton();
        opcionSoloActivos = new javax.swing.JRadioButton();
        opcionManual = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        inversionInicial = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaTmar = new javax.swing.JTable();
        botonTMAR2 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        tmarPonderada = new javax.swing.JTextField();
        panelSalida = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaResultados = new javax.swing.JTable();
        panelDatosExactos = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablaDatosExactos = new javax.swing.JTable();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        establecerPeriodo = new javax.swing.JMenuItem();
        menuAbrir = new javax.swing.JMenuItem();
        menuGuardar = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        menuOperaciones = new javax.swing.JMenu();
        menuCalcularTodo = new javax.swing.JMenuItem();
        menuCantidadesExactas = new javax.swing.JMenuItem();
        menuRecargar = new javax.swing.JMenuItem();
        menuTMAR = new javax.swing.JMenu();
        calcularTMAR = new javax.swing.JMenuItem();
        menuVariables = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        insertarIngresos = new javax.swing.JMenuItem();
        ingresosManual = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        costosPorcentaje = new javax.swing.JMenuItem();
        costosModeloPronosticacion = new javax.swing.JMenuItem();
        costosManual = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        gastosEscalonados = new javax.swing.JMenuItem();
        gastosCoutaFija = new javax.swing.JMenuItem();
        gastosSegunVariable = new javax.swing.JMenuItem();
        gastosModeloPronosticacion = new javax.swing.JMenuItem();
        gastosDepreciaciones = new javax.swing.JMenuItem();
        gastosManual = new javax.swing.JMenuItem();
        calcularIntereses = new javax.swing.JMenuItem();
        menuImpuestos = new javax.swing.JMenu();
        itemIVA = new javax.swing.JMenuItem();
        itemISR = new javax.swing.JMenuItem();
        itemISO = new javax.swing.JMenuItem();
        menuHerramientas = new javax.swing.JMenu();
        menuEscenarios = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        Pronosticos = new javax.swing.JMenu();
        pronosticarModelo = new javax.swing.JMenuItem();
        modeloPorcentual = new javax.swing.JMenuItem();
        menuExtrasImpuestos = new javax.swing.JMenu();
        extraIVA = new javax.swing.JMenuItem();
        extraISR = new javax.swing.JMenuItem();
        extraISO = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        modificarVariable = new javax.swing.JMenuItem();
        eliminarVariable = new javax.swing.JMenuItem();
        insertarGastoPorcentual = new javax.swing.JMenuItem();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu6 = new javax.swing.JMenu();
        jMenu7 = new javax.swing.JMenu();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        buttonGroup6 = new javax.swing.ButtonGroup();
        buttonGroup7 = new javax.swing.ButtonGroup();

        mainPanel.setName("mainPanel"); // NOI18N

        panelTabs.setName("panelTabs"); // NOI18N

        jPanel8.setName("jPanel8"); // NOI18N

        jScrollPane8.setName("jScrollPane8"); // NOI18N

        tablaPrincipal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaPrincipal.setComponentPopupMenu(jPopupMenu1);
        tablaPrincipal.setName("tablaPrincipal"); // NOI18N
        jScrollPane8.setViewportView(tablaPrincipal);

        filler1.setName("filler1"); // NOI18N

        panelPrincipal.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        panelPrincipal.setName("panelPrincipal"); // NOI18N

        panelAcciones.setName("panelAcciones"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(flujocaja.FlujoCajaApp.class).getContext().getResourceMap(FlujoCajaView.class);
        botonIngresos.setText(resourceMap.getString("botonIngresos.text")); // NOI18N
        botonIngresos.setName("botonIngresos"); // NOI18N
        botonIngresos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonIngresosActionPerformed(evt);
            }
        });

        botonCostos.setText(resourceMap.getString("botonCostos.text")); // NOI18N
        botonCostos.setName("botonCostos"); // NOI18N
        botonCostos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCostosActionPerformed(evt);
            }
        });

        botonGastos.setText(resourceMap.getString("botonGastos.text")); // NOI18N
        botonGastos.setName("botonGastos"); // NOI18N
        botonGastos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonGastosActionPerformed(evt);
            }
        });

        calcularUtilidaBruta.setText(resourceMap.getString("calcularUtilidaBruta.text")); // NOI18N
        calcularUtilidaBruta.setName("calcularUtilidaBruta"); // NOI18N
        calcularUtilidaBruta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcularUtilidaBrutaActionPerformed(evt);
            }
        });

        calcularUAI.setText(resourceMap.getString("calcularUAI.text")); // NOI18N
        calcularUAI.setName("calcularUAI"); // NOI18N
        calcularUAI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcularUAIActionPerformed(evt);
            }
        });

        calcularIVA.setText(resourceMap.getString("calcularIVA.text")); // NOI18N
        calcularIVA.setName("calcularIVA"); // NOI18N
        calcularIVA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcularIVAActionPerformed(evt);
            }
        });

        calcularISR.setText(resourceMap.getString("calcularISR.text")); // NOI18N
        calcularISR.setName("calcularISR"); // NOI18N
        calcularISR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcularISRActionPerformed(evt);
            }
        });

        iso.setText(resourceMap.getString("iso.text")); // NOI18N
        iso.setName("iso"); // NOI18N
        iso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isoActionPerformed(evt);
            }
        });

        botonInteres.setText(resourceMap.getString("botonInteres.text")); // NOI18N
        botonInteres.setName("botonInteres"); // NOI18N
        botonInteres.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonInteresActionPerformed(evt);
            }
        });

        utilidadNeta.setText(resourceMap.getString("utilidadNeta.text")); // NOI18N
        utilidadNeta.setName("utilidadNeta"); // NOI18N
        utilidadNeta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                utilidadNetaActionPerformed(evt);
            }
        });

        flujoEfectivoNeto.setText(resourceMap.getString("flujoEfectivoNeto.text")); // NOI18N
        flujoEfectivoNeto.setName("flujoEfectivoNeto"); // NOI18N
        flujoEfectivoNeto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                flujoEfectivoNetoActionPerformed(evt);
            }
        });

        calcularVAN.setText(resourceMap.getString("calcularVAN.text")); // NOI18N
        calcularVAN.setName("calcularVAN"); // NOI18N
        calcularVAN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcularVANActionPerformed(evt);
            }
        });

        calcularTodo.setText(resourceMap.getString("calcularTodo.text")); // NOI18N
        calcularTodo.setName("calcularTodo"); // NOI18N
        calcularTodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcularTodoActionPerformed(evt);
            }
        });

        botonTMAR1.setText(resourceMap.getString("botonTMAR1.text")); // NOI18N
        botonTMAR1.setName("botonTMAR1"); // NOI18N
        botonTMAR1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonTMAR1ActionPerformed(evt);
            }
        });

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelAccionesLayout = new javax.swing.GroupLayout(panelAcciones);
        panelAcciones.setLayout(panelAccionesLayout);
        panelAccionesLayout.setHorizontalGroup(
            panelAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAccionesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonTMAR1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(calcularVAN, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(utilidadNeta, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonGastos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonInteres, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonIngresos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonCostos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(iso, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(calcularUAI, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(calcularIVA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(calcularISR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(calcularUtilidaBruta, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(flujoEfectivoNeto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(calcularTodo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        panelAccionesLayout.setVerticalGroup(
            panelAccionesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelAccionesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(botonTMAR1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botonIngresos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botonCostos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(calcularUtilidaBruta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botonGastos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botonInteres)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(calcularUAI)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(calcularIVA)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(calcularISR)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(iso)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(utilidadNeta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(flujoEfectivoNeto)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(calcularVAN)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(calcularTodo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        panelPrincipal.addTab(resourceMap.getString("panelAcciones.TabConstraints.tabTitle"), panelAcciones); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(panelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
        );

        panelTabs.addTab(resourceMap.getString("jPanel8.TabConstraints.tabTitle"), jPanel8); // NOI18N

        panelCaracteristicas.setName("panelCaracteristicas"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel1.border.title"))); // NOI18N
        jPanel1.setName("jPanel1"); // NOI18N

        buttonGroup1.add(opcionDatosNetos);
        opcionDatosNetos.setSelected(true);
        opcionDatosNetos.setText(resourceMap.getString("opcionDatosNetos.text")); // NOI18N
        opcionDatosNetos.setName("opcionDatosNetos"); // NOI18N
        opcionDatosNetos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionDatosNetosActionPerformed(evt);
            }
        });

        buttonGroup1.add(opcionDatosBrutos);
        opcionDatosBrutos.setText(resourceMap.getString("opcionDatosBrutos.text")); // NOI18N
        opcionDatosBrutos.setName("opcionDatosBrutos"); // NOI18N
        opcionDatosBrutos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionDatosBrutosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(opcionDatosBrutos)
                    .addComponent(opcionDatosNetos))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(opcionDatosNetos)
                .addGap(3, 3, 3)
                .addComponent(opcionDatosBrutos))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel2.border.title"))); // NOI18N
        jPanel2.setName("jPanel2"); // NOI18N

        buttonGroup2.add(opcionEmpresaIndividual);
        opcionEmpresaIndividual.setSelected(true);
        opcionEmpresaIndividual.setText(resourceMap.getString("opcionEmpresaIndividual.text")); // NOI18N
        opcionEmpresaIndividual.setName("opcionEmpresaIndividual"); // NOI18N
        opcionEmpresaIndividual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionEmpresaIndividualActionPerformed(evt);
            }
        });

        buttonGroup2.add(opcionEmpresaJuridica);
        opcionEmpresaJuridica.setText(resourceMap.getString("opcionEmpresaJuridica.text")); // NOI18N
        opcionEmpresaJuridica.setName("opcionEmpresaJuridica"); // NOI18N
        opcionEmpresaJuridica.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionEmpresaJuridicaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(opcionEmpresaIndividual)
                    .addComponent(opcionEmpresaJuridica))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(opcionEmpresaIndividual)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(opcionEmpresaJuridica))
        );

        panelEmpresaNueva.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("panelEmpresaNueva.border.title"))); // NOI18N
        panelEmpresaNueva.setName("panelEmpresaNueva"); // NOI18N

        buttonGroup3.add(opcionEmpresaNueva);
        opcionEmpresaNueva.setSelected(true);
        opcionEmpresaNueva.setText(resourceMap.getString("opcionEmpresaNueva.text")); // NOI18N
        opcionEmpresaNueva.setName("opcionEmpresaNueva"); // NOI18N
        opcionEmpresaNueva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionEmpresaNuevaActionPerformed(evt);
            }
        });

        buttonGroup3.add(opcionEmpresaNoNueva);
        opcionEmpresaNoNueva.setText(resourceMap.getString("opcionEmpresaNoNueva.text")); // NOI18N
        opcionEmpresaNoNueva.setName("opcionEmpresaNoNueva"); // NOI18N
        opcionEmpresaNoNueva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionEmpresaNoNuevaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelEmpresaNuevaLayout = new javax.swing.GroupLayout(panelEmpresaNueva);
        panelEmpresaNueva.setLayout(panelEmpresaNuevaLayout);
        panelEmpresaNuevaLayout.setHorizontalGroup(
            panelEmpresaNuevaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEmpresaNuevaLayout.createSequentialGroup()
                .addGroup(panelEmpresaNuevaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(opcionEmpresaNueva)
                    .addComponent(opcionEmpresaNoNueva))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        panelEmpresaNuevaLayout.setVerticalGroup(
            panelEmpresaNuevaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelEmpresaNuevaLayout.createSequentialGroup()
                .addComponent(opcionEmpresaNueva)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(opcionEmpresaNoNueva)
                .addContainerGap())
        );

        panelPatente.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("panelPatente.border.title"))); // NOI18N
        panelPatente.setName("panelPatente"); // NOI18N

        buttonGroup4.add(opcionPatente);
        opcionPatente.setSelected(true);
        opcionPatente.setText(resourceMap.getString("opcionPatente.text")); // NOI18N
        opcionPatente.setName("opcionPatente"); // NOI18N
        opcionPatente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionPatenteActionPerformed(evt);
            }
        });

        buttonGroup4.add(opcionSinPatente);
        opcionSinPatente.setText(resourceMap.getString("opcionSinPatente.text")); // NOI18N
        opcionSinPatente.setName("opcionSinPatente"); // NOI18N
        opcionSinPatente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionSinPatenteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPatenteLayout = new javax.swing.GroupLayout(panelPatente);
        panelPatente.setLayout(panelPatenteLayout);
        panelPatenteLayout.setHorizontalGroup(
            panelPatenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPatenteLayout.createSequentialGroup()
                .addGroup(panelPatenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(opcionSinPatente)
                    .addComponent(opcionPatente))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        panelPatenteLayout.setVerticalGroup(
            panelPatenteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPatenteLayout.createSequentialGroup()
                .addComponent(opcionPatente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(opcionSinPatente)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelISO.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("panelISO.border.title"))); // NOI18N
        panelISO.setName("panelISO"); // NOI18N

        buttonGroup5.add(opcionISOaISR);
        opcionISOaISR.setSelected(true);
        opcionISOaISR.setText(resourceMap.getString("opcionISOaISR.text")); // NOI18N
        opcionISOaISR.setName("opcionISOaISR"); // NOI18N
        opcionISOaISR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionISOaISRActionPerformed(evt);
            }
        });

        buttonGroup5.add(opcionISRaISO);
        opcionISRaISO.setText(resourceMap.getString("opcionISRaISO.text")); // NOI18N
        opcionISRaISO.setName("opcionISRaISO"); // NOI18N
        opcionISRaISO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionISRaISOActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelISOLayout = new javax.swing.GroupLayout(panelISO);
        panelISO.setLayout(panelISOLayout);
        panelISOLayout.setHorizontalGroup(
            panelISOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(opcionISRaISO)
            .addComponent(opcionISOaISR)
        );
        panelISOLayout.setVerticalGroup(
            panelISOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelISOLayout.createSequentialGroup()
                .addComponent(opcionISOaISR)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(opcionISRaISO))
        );

        panelDetalles.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("panelDetalles.border.title"))); // NOI18N
        panelDetalles.setName("panelDetalles"); // NOI18N

        buttonGroup6.add(verDetalles);
        verDetalles.setText(resourceMap.getString("verDetalles.text")); // NOI18N
        verDetalles.setName("verDetalles"); // NOI18N

        buttonGroup6.add(noVerDetalles);
        noVerDetalles.setSelected(true);
        noVerDetalles.setText(resourceMap.getString("noVerDetalles.text")); // NOI18N
        noVerDetalles.setName("noVerDetalles"); // NOI18N

        javax.swing.GroupLayout panelDetallesLayout = new javax.swing.GroupLayout(panelDetalles);
        panelDetalles.setLayout(panelDetallesLayout);
        panelDetallesLayout.setHorizontalGroup(
            panelDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetallesLayout.createSequentialGroup()
                .addGroup(panelDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(noVerDetalles)
                    .addComponent(verDetalles))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        panelDetallesLayout.setVerticalGroup(
            panelDetallesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDetallesLayout.createSequentialGroup()
                .addComponent(verDetalles)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(noVerDetalles))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel3.border.title"))); // NOI18N
        jPanel3.setName("jPanel3"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        escala.setText(resourceMap.getString("escala.text")); // NOI18N
        escala.setName("escala"); // NOI18N

        activosNuevos.setText(resourceMap.getString("activosNuevos.text")); // NOI18N
        activosNuevos.setName("activosNuevos"); // NOI18N

        activosAnteriores.setText(resourceMap.getString("activosAnteriores.text")); // NOI18N
        activosAnteriores.setName("activosAnteriores"); // NOI18N

        cargarDetalles.setText(resourceMap.getString("cargarDetalles.text")); // NOI18N
        cargarDetalles.setName("cargarDetalles"); // NOI18N
        cargarDetalles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cargarDetallesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cargarDetalles)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(escala)
                            .addComponent(activosNuevos)
                            .addComponent(activosAnteriores, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(escala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(activosNuevos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(activosAnteriores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cargarDetalles))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel4.border.title"))); // NOI18N
        jPanel4.setName("jPanel4"); // NOI18N

        buttonGroup7.add(opcionInversionMasActivos);
        opcionInversionMasActivos.setSelected(true);
        opcionInversionMasActivos.setText(resourceMap.getString("opcionInversionMasActivos.text")); // NOI18N
        opcionInversionMasActivos.setName("opcionInversionMasActivos"); // NOI18N
        opcionInversionMasActivos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionInversionMasActivosActionPerformed(evt);
            }
        });

        buttonGroup7.add(opcionSoloInversion);
        opcionSoloInversion.setText(resourceMap.getString("opcionSoloInversion.text")); // NOI18N
        opcionSoloInversion.setName("opcionSoloInversion"); // NOI18N
        opcionSoloInversion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionSoloInversionActionPerformed(evt);
            }
        });

        buttonGroup7.add(opcionSoloActivos);
        opcionSoloActivos.setText(resourceMap.getString("opcionSoloActivos.text")); // NOI18N
        opcionSoloActivos.setName("opcionSoloActivos"); // NOI18N
        opcionSoloActivos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionSoloActivosActionPerformed(evt);
            }
        });

        buttonGroup7.add(opcionManual);
        opcionManual.setText(resourceMap.getString("opcionManual.text")); // NOI18N
        opcionManual.setName("opcionManual"); // NOI18N
        opcionManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                opcionManualActionPerformed(evt);
            }
        });

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        inversionInicial.setEditable(false);
        inversionInicial.setText(resourceMap.getString("inversionInicial.text")); // NOI18N
        inversionInicial.setName("inversionInicial"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(opcionInversionMasActivos)
                    .addComponent(opcionSoloInversion)
                    .addComponent(opcionSoloActivos)
                    .addComponent(opcionManual)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inversionInicial)))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(opcionInversionMasActivos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(opcionSoloInversion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(opcionSoloActivos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(opcionManual)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(inversionInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout panelCaracteristicasLayout = new javax.swing.GroupLayout(panelCaracteristicas);
        panelCaracteristicas.setLayout(panelCaracteristicasLayout);
        panelCaracteristicasLayout.setHorizontalGroup(
            panelCaracteristicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCaracteristicasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCaracteristicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelEmpresaNueva, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelPatente, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelCaracteristicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelCaracteristicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(panelISO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 353, Short.MAX_VALUE)
                .addComponent(panelDetalles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelCaracteristicasLayout.setVerticalGroup(
            panelCaracteristicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCaracteristicasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCaracteristicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelISO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelCaracteristicasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelCaracteristicasLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelEmpresaNueva, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelPatente, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelCaracteristicasLayout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(58, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelCaracteristicasLayout.createSequentialGroup()
                .addGap(214, 214, 214)
                .addComponent(panelDetalles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        panelTabs.addTab(resourceMap.getString("panelCaracteristicas.TabConstraints.tabTitle"), panelCaracteristicas); // NOI18N

        jPanel7.setName("jPanel7"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tablaTmar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaTmar.setName("tablaTmar"); // NOI18N
        jScrollPane1.setViewportView(tablaTmar);

        botonTMAR2.setText(resourceMap.getString("botonTMAR2.text")); // NOI18N
        botonTMAR2.setName("botonTMAR2"); // NOI18N
        botonTMAR2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonTMAR2ActionPerformed(evt);
            }
        });

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        tmarPonderada.setEditable(false);
        tmarPonderada.setText(resourceMap.getString("tmarPonderada.text")); // NOI18N
        tmarPonderada.setName("tmarPonderada"); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(botonTMAR2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tmarPonderada, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(711, 711, 711))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 849, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(botonTMAR2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(tmarPonderada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
        );

        panelTabs.addTab(resourceMap.getString("jPanel7.TabConstraints.tabTitle"), jPanel7); // NOI18N

        panelSalida.setName("panelSalida"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        tablaResultados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaResultados.setName("tablaResultados"); // NOI18N
        jScrollPane2.setViewportView(tablaResultados);

        javax.swing.GroupLayout panelSalidaLayout = new javax.swing.GroupLayout(panelSalida);
        panelSalida.setLayout(panelSalidaLayout);
        panelSalidaLayout.setHorizontalGroup(
            panelSalidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSalidaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(387, Short.MAX_VALUE))
        );
        panelSalidaLayout.setVerticalGroup(
            panelSalidaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSalidaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(280, Short.MAX_VALUE))
        );

        panelTabs.addTab(resourceMap.getString("panelSalida.TabConstraints.tabTitle"), panelSalida); // NOI18N

        panelDatosExactos.setName("panelDatosExactos"); // NOI18N

        jScrollPane3.setName("jScrollPane3"); // NOI18N

        tablaDatosExactos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaDatosExactos.setName("tablaDatosExactos"); // NOI18N
        jScrollPane3.setViewportView(tablaDatosExactos);

        javax.swing.GroupLayout panelDatosExactosLayout = new javax.swing.GroupLayout(panelDatosExactos);
        panelDatosExactos.setLayout(panelDatosExactosLayout);
        panelDatosExactosLayout.setHorizontalGroup(
            panelDatosExactosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosExactosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 829, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelDatosExactosLayout.setVerticalGroup(
            panelDatosExactosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosExactosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
                .addContainerGap())
        );

        panelTabs.addTab(resourceMap.getString("panelDatosExactos.TabConstraints.tabTitle"), panelDatosExactos); // NOI18N

        filler2.setName("filler2"); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(panelTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 854, Short.MAX_VALUE)
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(panelTabs, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                .addContainerGap())
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N
        fileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuActionPerformed(evt);
            }
        });

        establecerPeriodo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        establecerPeriodo.setText(resourceMap.getString("establecerPeriodo.text")); // NOI18N
        establecerPeriodo.setName("establecerPeriodo"); // NOI18N
        establecerPeriodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                establecerPeriodoActionPerformed(evt);
            }
        });
        fileMenu.add(establecerPeriodo);

        menuAbrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        menuAbrir.setText(resourceMap.getString("menuAbrir.text")); // NOI18N
        menuAbrir.setName("menuAbrir"); // NOI18N
        menuAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuAbrirActionPerformed(evt);
            }
        });
        fileMenu.add(menuAbrir);

        menuGuardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        menuGuardar.setText(resourceMap.getString("menuGuardar.text")); // NOI18N
        menuGuardar.setName("menuGuardar"); // NOI18N
        menuGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuGuardarActionPerformed(evt);
            }
        });
        fileMenu.add(menuGuardar);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(flujocaja.FlujoCajaApp.class).getContext().getActionMap(FlujoCajaView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setText(resourceMap.getString("exitMenuItem.text")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        menuOperaciones.setText(resourceMap.getString("menuOperaciones.text")); // NOI18N
        menuOperaciones.setName("menuOperaciones"); // NOI18N

        menuCalcularTodo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        menuCalcularTodo.setText(resourceMap.getString("menuCalcularTodo.text")); // NOI18N
        menuCalcularTodo.setName("menuCalcularTodo"); // NOI18N
        menuCalcularTodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCalcularTodoActionPerformed(evt);
            }
        });
        menuOperaciones.add(menuCalcularTodo);

        menuCantidadesExactas.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        menuCantidadesExactas.setText(resourceMap.getString("menuCantidadesExactas.text")); // NOI18N
        menuCantidadesExactas.setName("menuCantidadesExactas"); // NOI18N
        menuCantidadesExactas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCantidadesExactasActionPerformed(evt);
            }
        });
        menuOperaciones.add(menuCantidadesExactas);

        menuRecargar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        menuRecargar.setText(resourceMap.getString("menuRecargar.text")); // NOI18N
        menuRecargar.setName("menuRecargar"); // NOI18N
        menuRecargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuRecargarActionPerformed(evt);
            }
        });
        menuOperaciones.add(menuRecargar);

        menuBar.add(menuOperaciones);

        menuTMAR.setText(resourceMap.getString("menuTMAR.text")); // NOI18N
        menuTMAR.setName("menuTMAR"); // NOI18N

        calcularTMAR.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        calcularTMAR.setText(resourceMap.getString("calcularTMAR.text")); // NOI18N
        calcularTMAR.setName("calcularTMAR"); // NOI18N
        calcularTMAR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcularTMARActionPerformed(evt);
            }
        });
        menuTMAR.add(calcularTMAR);

        menuBar.add(menuTMAR);

        menuVariables.setText(resourceMap.getString("menuVariables.text")); // NOI18N
        menuVariables.setName("menuVariables"); // NOI18N

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        insertarIngresos.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        insertarIngresos.setText(resourceMap.getString("insertarIngresos.text")); // NOI18N
        insertarIngresos.setName("insertarIngresos"); // NOI18N
        insertarIngresos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertarIngresosActionPerformed(evt);
            }
        });
        jMenu1.add(insertarIngresos);

        ingresosManual.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        ingresosManual.setText(resourceMap.getString("ingresosManual.text")); // NOI18N
        ingresosManual.setName("ingresosManual"); // NOI18N
        ingresosManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ingresosManualActionPerformed(evt);
            }
        });
        jMenu1.add(ingresosManual);

        menuVariables.add(jMenu1);

        jMenu4.setText(resourceMap.getString("jMenu4.text")); // NOI18N
        jMenu4.setName("jMenu4"); // NOI18N

        costosPorcentaje.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        costosPorcentaje.setText(resourceMap.getString("costosPorcentaje.text")); // NOI18N
        costosPorcentaje.setName("costosPorcentaje"); // NOI18N
        costosPorcentaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                costosPorcentajeActionPerformed(evt);
            }
        });
        jMenu4.add(costosPorcentaje);

        costosModeloPronosticacion.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        costosModeloPronosticacion.setText(resourceMap.getString("costosModeloPronosticacion.text")); // NOI18N
        costosModeloPronosticacion.setName("costosModeloPronosticacion"); // NOI18N
        costosModeloPronosticacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                costosModeloPronosticacionActionPerformed(evt);
            }
        });
        jMenu4.add(costosModeloPronosticacion);

        costosManual.setText(resourceMap.getString("costosManual.text")); // NOI18N
        costosManual.setName("costosManual"); // NOI18N
        jMenu4.add(costosManual);

        menuVariables.add(jMenu4);

        jMenu5.setText(resourceMap.getString("jMenu5.text")); // NOI18N
        jMenu5.setName("jMenu5"); // NOI18N

        gastosEscalonados.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        gastosEscalonados.setText(resourceMap.getString("gastosEscalonados.text")); // NOI18N
        gastosEscalonados.setName("gastosEscalonados"); // NOI18N
        gastosEscalonados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gastosEscalonadosActionPerformed(evt);
            }
        });
        jMenu5.add(gastosEscalonados);

        gastosCoutaFija.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        gastosCoutaFija.setText(resourceMap.getString("gastosCoutaFija.text")); // NOI18N
        gastosCoutaFija.setName("gastosCoutaFija"); // NOI18N
        gastosCoutaFija.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gastosCoutaFijaActionPerformed(evt);
            }
        });
        jMenu5.add(gastosCoutaFija);

        gastosSegunVariable.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        gastosSegunVariable.setText(resourceMap.getString("gastosSegunVariable.text")); // NOI18N
        gastosSegunVariable.setName("gastosSegunVariable"); // NOI18N
        gastosSegunVariable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gastosSegunVariableActionPerformed(evt);
            }
        });
        jMenu5.add(gastosSegunVariable);

        gastosModeloPronosticacion.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.ALT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        gastosModeloPronosticacion.setText(resourceMap.getString("gastosModeloPronosticacion.text")); // NOI18N
        gastosModeloPronosticacion.setName("gastosModeloPronosticacion"); // NOI18N
        gastosModeloPronosticacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gastosModeloPronosticacionActionPerformed(evt);
            }
        });
        jMenu5.add(gastosModeloPronosticacion);

        gastosDepreciaciones.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        gastosDepreciaciones.setText(resourceMap.getString("gastosDepreciaciones.text")); // NOI18N
        gastosDepreciaciones.setName("gastosDepreciaciones"); // NOI18N
        gastosDepreciaciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gastosDepreciacionesActionPerformed(evt);
            }
        });
        jMenu5.add(gastosDepreciaciones);

        gastosManual.setText(resourceMap.getString("gastosManual.text")); // NOI18N
        gastosManual.setName("gastosManual"); // NOI18N
        jMenu5.add(gastosManual);

        menuVariables.add(jMenu5);

        calcularIntereses.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        calcularIntereses.setText(resourceMap.getString("calcularIntereses.text")); // NOI18N
        calcularIntereses.setName("calcularIntereses"); // NOI18N
        calcularIntereses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcularInteresesActionPerformed(evt);
            }
        });
        menuVariables.add(calcularIntereses);

        menuBar.add(menuVariables);

        menuImpuestos.setText(resourceMap.getString("menuImpuestos.text")); // NOI18N
        menuImpuestos.setName("menuImpuestos"); // NOI18N

        itemIVA.setText(resourceMap.getString("itemIVA.text")); // NOI18N
        itemIVA.setName("itemIVA"); // NOI18N
        itemIVA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemIVAActionPerformed(evt);
            }
        });
        menuImpuestos.add(itemIVA);

        itemISR.setText(resourceMap.getString("itemISR.text")); // NOI18N
        itemISR.setName("itemISR"); // NOI18N
        itemISR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemISRActionPerformed(evt);
            }
        });
        menuImpuestos.add(itemISR);

        itemISO.setText(resourceMap.getString("itemISO.text")); // NOI18N
        itemISO.setName("itemISO"); // NOI18N
        itemISO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemISOActionPerformed(evt);
            }
        });
        menuImpuestos.add(itemISO);

        menuBar.add(menuImpuestos);

        menuHerramientas.setText(resourceMap.getString("menuHerramientas.text")); // NOI18N
        menuHerramientas.setName("menuHerramientas"); // NOI18N

        menuEscenarios.setText(resourceMap.getString("menuEscenarios.text")); // NOI18N
        menuEscenarios.setName("menuEscenarios"); // NOI18N
        menuEscenarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuEscenariosActionPerformed(evt);
            }
        });
        menuHerramientas.add(menuEscenarios);

        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        menuHerramientas.add(jMenuItem1);

        menuBar.add(menuHerramientas);

        Pronosticos.setText(resourceMap.getString("Pronosticos.text")); // NOI18N
        Pronosticos.setName("Pronosticos"); // NOI18N

        pronosticarModelo.setText(resourceMap.getString("pronosticarModelo.text")); // NOI18N
        pronosticarModelo.setName("pronosticarModelo"); // NOI18N
        pronosticarModelo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pronosticarModeloActionPerformed(evt);
            }
        });
        Pronosticos.add(pronosticarModelo);

        modeloPorcentual.setText(resourceMap.getString("modeloPorcentual.text")); // NOI18N
        modeloPorcentual.setName("modeloPorcentual"); // NOI18N
        modeloPorcentual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modeloPorcentualActionPerformed(evt);
            }
        });
        Pronosticos.add(modeloPorcentual);

        menuExtrasImpuestos.setText(resourceMap.getString("menuExtrasImpuestos.text")); // NOI18N
        menuExtrasImpuestos.setName("menuExtrasImpuestos"); // NOI18N

        extraIVA.setText(resourceMap.getString("extraIVA.text")); // NOI18N
        extraIVA.setName("extraIVA"); // NOI18N
        menuExtrasImpuestos.add(extraIVA);

        extraISR.setText(resourceMap.getString("extraISR.text")); // NOI18N
        extraISR.setName("extraISR"); // NOI18N
        menuExtrasImpuestos.add(extraISR);

        extraISO.setText(resourceMap.getString("extraISO.text")); // NOI18N
        extraISO.setName("extraISO"); // NOI18N
        menuExtrasImpuestos.add(extraISO);

        Pronosticos.add(menuExtrasImpuestos);

        menuBar.add(Pronosticos);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setText(resourceMap.getString("aboutMenuItem.text")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 864, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 694, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1))
        );

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        modificarVariable.setText(resourceMap.getString("modificarVariable.text")); // NOI18N
        modificarVariable.setName("modificarVariable"); // NOI18N
        modificarVariable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modificarVariableActionPerformed(evt);
            }
        });
        jPopupMenu1.add(modificarVariable);

        eliminarVariable.setText(resourceMap.getString("eliminarVariable.text")); // NOI18N
        eliminarVariable.setName("eliminarVariable"); // NOI18N
        eliminarVariable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarVariableActionPerformed(evt);
            }
        });
        jPopupMenu1.add(eliminarVariable);

        insertarGastoPorcentual.setText(resourceMap.getString("insertarGastoPorcentual.text")); // NOI18N
        insertarGastoPorcentual.setName("insertarGastoPorcentual"); // NOI18N
        insertarGastoPorcentual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertarGastoPorcentualActionPerformed(evt);
            }
        });
        jPopupMenu1.add(insertarGastoPorcentual);

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        jMenu6.setText(resourceMap.getString("jMenu6.text")); // NOI18N
        jMenu6.setName("jMenu6"); // NOI18N
        jMenuBar1.add(jMenu6);

        jMenu7.setText(resourceMap.getString("jMenu7.text")); // NOI18N
        jMenu7.setName("jMenu7"); // NOI18N
        jMenuBar1.add(jMenu7);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void calcularTMARActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcularTMARActionPerformed
        // TODO add your handling code here:
        this.calculoTMAR();

    }//GEN-LAST:event_calcularTMARActionPerformed

    private void costosPorcentajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_costosPorcentajeActionPerformed
        // TODO add your handling code here:
        this.costosPorPorcentaje();
    }//GEN-LAST:event_costosPorcentajeActionPerformed

    private void calcularInteresesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcularInteresesActionPerformed
        // TODO add your handling code here:
        this.intereses();
    }//GEN-LAST:event_calcularInteresesActionPerformed

    private void itemIVAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemIVAActionPerformed
        // TODO add your handling code here:
        Detalles dt = new Detalles(null, false);
        dt.verDetallesCalculoIVA(escenarioNormal);
    }//GEN-LAST:event_itemIVAActionPerformed

    private void insertarIngresosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertarIngresosActionPerformed
        // TODO add your handling code here:
        this.ingresos();
    }//GEN-LAST:event_insertarIngresosActionPerformed

    private void costosModeloPronosticacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_costosModeloPronosticacionActionPerformed
        // TODO add your handling code here:
        //int opc = JOptionPane.showConfirmDialog(mainPanel, "¿Los costos tienen factura?", "Factura", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        //si opc = 0 entonces los costos tienen factura
        this.costosPorModeloPronostico();
    }//GEN-LAST:event_costosModeloPronosticacionActionPerformed

    private void establecerPeriodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_establecerPeriodoActionPerformed
        // TODO add your handling code here:
        DeterminarPeriodo ventanaPeriodo = new DeterminarPeriodo(null, true);
        ventanaPeriodo.establecerDatos(escenarioNormal);
        if (escenarioNormal!=null){
            this.mostrarModelo();

            this.activosNuevos.setText(Double.toString(this.escenarioNormal.getActivos()));
            this.activosAnteriores.setText(Double.toString(this.escenarioNormal.getActivosAnteriores()));
            this.escala.setText(Double.toString(this.escenarioNormal.getEscala()));
            this.inversionInicial.setText(Double.toString(ModeloPorcentual.redondearCifra(this.escenarioNormal.getInversionInicial())));
        }
        
        
    }//GEN-LAST:event_establecerPeriodoActionPerformed

    private void gastosEscalonadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gastosEscalonadosActionPerformed
        // TODO add your handling code here:
        this.gastoEscalonado();
    }//GEN-LAST:event_gastosEscalonadosActionPerformed

    private void gastosCoutaFijaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gastosCoutaFijaActionPerformed
        // TODO add your handling code here:
        this.gastoCuotaFija();
    }//GEN-LAST:event_gastosCoutaFijaActionPerformed

    private void gastosSegunVariableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gastosSegunVariableActionPerformed
        // TODO add your handling code here:
        this.gastoSegunVariable();        
    }//GEN-LAST:event_gastosSegunVariableActionPerformed

    private void gastosDepreciacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gastosDepreciacionesActionPerformed
        // TODO add your handling code here:
        this.gastosDepreciaciones();
    }//GEN-LAST:event_gastosDepreciacionesActionPerformed

    private void gastosModeloPronosticacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gastosModeloPronosticacionActionPerformed
        // TODO add your handling code here:
        this.gastosModeloPronostico();
    }//GEN-LAST:event_gastosModeloPronosticacionActionPerformed

    private void calcularIVAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcularIVAActionPerformed
        // TODO add your handling code here:
        this.setOpcionesDeEscenario();
        this.calculoIVA();
}//GEN-LAST:event_calcularIVAActionPerformed

    private void calcularUAIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcularUAIActionPerformed
        // TODO add your handling code here:
        this.calculoUAI();
}//GEN-LAST:event_calcularUAIActionPerformed

    private void calcularUtilidaBrutaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcularUtilidaBrutaActionPerformed
        // TODO add your handling code here:
        if (this.buscarCuenta("Utilidad bruta")==false){
            this.insertarFilaTablaPrincipal("Utilidad bruta", this.escenarioNormal.utilidadBruta());
        }
}//GEN-LAST:event_calcularUtilidaBrutaActionPerformed

    private void calcularISRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcularISRActionPerformed
        // TODO add your handling code here: 
        this.setOpcionesDeEscenario();
        this.calculoISR();
        //this.insertarFilaTablaPrincipal("ISR por pagar", modeloISR.getISRporPagar());
    }//GEN-LAST:event_calcularISRActionPerformed

    private void modificarVariableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modificarVariableActionPerformed
        // TODO add your handling code here:
        //recalcular todo
        int index = tablaPrincipal.getSelectedRow();
        String nombre = mapa.get(index);
        if (nombre.equals("Ingresos")){
            //this.ingresos();
            //elegir si se cambia el modelo o los datos manualmente
            ModeloRegresion mr = new ModeloRegresion(null, true);
            Modelo m = mr.modificarModelo(this.escenarioNormal.getModeloIngresos().getModeloIngresos(),nombre);
            if (m!=null)
                this.escenarioNormal.getModeloIngresos().setModeloIngresos(m);
            recargarDatos();
        }
        else if (nombre.equals("Costos")){
            ModificarModeloCostos mmc = new ModificarModeloCostos(null, true);
            mmc.modificar(escenarioNormal);
            recargarDatos();
        }
        else if (this.escenarioNormal.gastoValido(nombre)){
            Gasto temporal = this.escenarioNormal.obtenerGasto(nombre);
            //modificar
            ModificarModeloGastos mmg = new ModificarModeloGastos(null, true);
            mmg.modificarGasto(escenarioNormal, temporal);
            recargarDatos();
        }
        else if (this.escenarioNormal.interesValido(nombre)){
            Intereses temporal = this.escenarioNormal.obtenerInteres(nombre);
            //modificar
            ModificarModeloIntereses mmi = new ModificarModeloIntereses(null, true);
            mmi.cambiarPrestamo(escenarioNormal, temporal);
            recargarDatos();
        }

    }//GEN-LAST:event_modificarVariableActionPerformed

    private void opcionDatosNetosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionDatosNetosActionPerformed
        // TODO add your handling code here:        
        this.recargarDatos();
    }//GEN-LAST:event_opcionDatosNetosActionPerformed

    private void opcionDatosBrutosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionDatosBrutosActionPerformed
        // TODO add your handling code here:
        this.recargarDatos();
    }//GEN-LAST:event_opcionDatosBrutosActionPerformed

    private void opcionEmpresaIndividualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionEmpresaIndividualActionPerformed
        // TODO add your handling code here:
        this.recargarDatos();
    }//GEN-LAST:event_opcionEmpresaIndividualActionPerformed

    private void opcionEmpresaJuridicaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionEmpresaJuridicaActionPerformed
        // TODO add your handling code here:
        this.recargarDatos();
    }//GEN-LAST:event_opcionEmpresaJuridicaActionPerformed

    private void botonCostosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCostosActionPerformed
        // TODO add your handling code here:
        SeleccionCostos sc = new SeleccionCostos(null, true);
        int ipc = sc.seleccionarCostos();
        if (ipc==0)
            this.costosPorPorcentaje();
        else if (ipc == 1)
            this.costosPorModeloPronostico();
        else if (ipc == 2)
            this.costosManual();
    }//GEN-LAST:event_botonCostosActionPerformed

    private void botonGastosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonGastosActionPerformed
        // TODO add your handling code here:
        SeleccionGastos sg = new SeleccionGastos(null, true);
        int opc = sg.seleccionarGasto();
        if (opc==0)
            this.gastoEscalonado();
        else if (opc==1)
            this.gastoCuotaFija();
        else if (opc==2)
            this.gastoSegunVariable();
        else if (opc==3)
            this.gastosModeloPronostico();
        else if (opc==4)
            this.gastosDepreciaciones();
        else if (opc==5)
            this.gastosManuales();
        
    }//GEN-LAST:event_botonGastosActionPerformed

    private void botonIngresosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonIngresosActionPerformed
        // TODO add your handling code here:
        this.ingresos();
    }//GEN-LAST:event_botonIngresosActionPerformed

    private void isoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_isoActionPerformed
        // TODO add your handling code here:
        this.setOpcionesDeEscenario();
        this.calculoISO();
    }//GEN-LAST:event_isoActionPerformed

    private void opcionISRaISOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionISRaISOActionPerformed
        // TODO add your handling code here:
        this.recargarDatos();
    }//GEN-LAST:event_opcionISRaISOActionPerformed

    private void opcionISOaISRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionISOaISRActionPerformed
        // TODO add your handling code here:
        this.recargarDatos();
    }//GEN-LAST:event_opcionISOaISRActionPerformed

    private void opcionSinPatenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionSinPatenteActionPerformed
        // TODO add your handling code here:
        this.recargarDatos();
    }//GEN-LAST:event_opcionSinPatenteActionPerformed

    private void opcionPatenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionPatenteActionPerformed
        // TODO add your handling code here:
        this.recargarDatos();
    }//GEN-LAST:event_opcionPatenteActionPerformed

    private void opcionEmpresaNoNuevaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionEmpresaNoNuevaActionPerformed
        // TODO add your handling code here:
        this.recargarDatos();
    }//GEN-LAST:event_opcionEmpresaNoNuevaActionPerformed

    private void opcionEmpresaNuevaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionEmpresaNuevaActionPerformed
        // TODO add your handling code here:
        this.recargarDatos();
    }//GEN-LAST:event_opcionEmpresaNuevaActionPerformed

    private void botonInteresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonInteresActionPerformed
        // TODO add your handling code here:
        this.intereses();
    }//GEN-LAST:event_botonInteresActionPerformed

    private void itemISRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemISRActionPerformed
        // TODO add your handling code here:
        Detalles dt = new Detalles(null, false);
        dt.verDetallesCalculoISR(escenarioNormal);
    }//GEN-LAST:event_itemISRActionPerformed

    private void itemISOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemISOActionPerformed
        // TODO add your handling code here:
        Detalles dt = new Detalles(null, false);
        dt.verDetallesCalculoISO(escenarioNormal);
    }//GEN-LAST:event_itemISOActionPerformed

    private void utilidadNetaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_utilidadNetaActionPerformed
        // TODO add your handling code here:
        this.insertarFilaTablaPrincipal("Utilidad Neta", this.escenarioNormal.calcularUtilidadNeta());
    }//GEN-LAST:event_utilidadNetaActionPerformed

    private void flujoEfectivoNetoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flujoEfectivoNetoActionPerformed
        // TODO add your handling code here:
        this.insertarFilaTablaPrincipal("FEN", this.escenarioNormal.calcularFEN());
    }//GEN-LAST:event_flujoEfectivoNetoActionPerformed

    private void calcularVANActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcularVANActionPerformed
        // TODO add your handling code here:
        this.insertarFilaTablaPrincipal("VAN", this.escenarioNormal.calcularVAN());
        this.llenarDetalles();
    }//GEN-LAST:event_calcularVANActionPerformed

    private void calcularTodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcularTodoActionPerformed
        // TODO add your handling code here:
        this.setOpcionesDeEscenario();
        this.calcularTodo();
    }//GEN-LAST:event_calcularTodoActionPerformed

    private void fileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_fileMenuActionPerformed

    private void menuCantidadesExactasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCantidadesExactasActionPerformed
        // TODO add your handling code here:
        //this.insertarFilaTablaPrincipal("Ingresos", this.escenarioNormal.ingresosActuales());
         double [] iterador;
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Concepto");
        modelo.addColumn("Factura");
        for (int i=0; i<this.escenarioNormal.getListaAnios().length; i++){
            modelo.addColumn(this.escenarioNormal.getListaAnios()[i]);
        }
        this.tablaDatosExactos.setModel(modelo);
        
        //ingresos
        if (this.escenarioNormal.ingresosCalculados())
            this.insertarFilaTablaDatosExactos("Ingresos", this.escenarioNormal.ingresosActuales());

        //costos
        if (this.escenarioNormal.costosCalculados())
            this.insertarFilaTablaDatosExactos("Costos", this.escenarioNormal.getModeloCostos().getFactura(), this.escenarioNormal.costosActuales());

        //uitlidad bruta
        if (this.escenarioNormal.utilidadBrutaCalculada())
            this.insertarFilaTablaDatosExactos("Utilidad bruta", this.escenarioNormal.getUtilidadBruta());

        //gastos
        ArrayList<Gasto> listaGastos = this.escenarioNormal.getListaGastos();
        if (listaGastos!=null){
            for(Gasto g : listaGastos){
                iterador = g.getListaGastos();
                if (iterador!=null)
                    this.insertarFilaTablaDatosExactos(g.getNombreGasto(), g.getFactura(), iterador);
            }
        }
        //intereses
        ArrayList<Intereses> listaIntereses = this.escenarioNormal.getListaIntereses();
        if (listaIntereses!=null){
            for (Intereses i:listaIntereses){
                iterador = i.getListaCuotasAnuales();
                if (iterador!=null)
                    this.insertarFilaTablaDatosExactos("Intereses", false, i.getListaCuotasAnuales());
            }
        }
        //"UAI"
        if (this.escenarioNormal.uaiCalculado())
            this.insertarFilaTablaDatosExactos("UAI", this.escenarioNormal.getUtilidadAntesImpuestos());
        
        //"IVA por pagar"
        if (this.escenarioNormal.ivaCalculado()){
            this.insertarFilaTablaDatosExactos("IVA por pagar", this.escenarioNormal.IVAporPagar());
        }            
        
        // ISR
        if (this.escenarioNormal.isrCalculado()){
            iterador = this.escenarioNormal.ISRporPagarTemporal();
            if (iterador!=null)
                this.insertarFilaTablaDatosExactos("ISR temporal", iterador);
            
        }
        //ISO
        if (this.escenarioNormal.ISOcalculado()){
            iterador = this.escenarioNormal.ISOporPagar();
            if (iterador!=null){
                this.insertarFilaTablaDatosExactos("ISR por pagar", this.escenarioNormal.ISRporPagar());
                this.insertarFilaTablaDatosExactos("ISO", iterador);
                
            }
        }
        //UTILIDAD NETA
        if (this.escenarioNormal.getUtilidadNeta()!=null)
            this.insertarFilaTablaDatosExactos("Utilidad neta", this.escenarioNormal.getUtilidadNeta());
        //FEN
        if (this.escenarioNormal.getFEN()!=null)
            this.insertarFilaTablaDatosExactos("FEN", this.escenarioNormal.getFEN());
        //TMAR
        this.insertarFilaTablaDatosExactos("TMAR",this.escenarioNormal.getTMARFormateada());
        //VAN
        if (this.escenarioNormal.getVAN()!=null){
            this.insertarFilaTablaDatosExactos("VAN", this.escenarioNormal.getVAN());
        }
        ////// FIN DE LA PARTE DE MOSTRADO DE DATOS
    }//GEN-LAST:event_menuCantidadesExactasActionPerformed

    private void menuCalcularTodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCalcularTodoActionPerformed
        // TODO add your handling code here:
        this.calcularTodo();
    }//GEN-LAST:event_menuCalcularTodoActionPerformed

    private void ingresosManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ingresosManualActionPerformed
        // TODO add your handling code here:
        this.insertarIngresosManualmente();
    }//GEN-LAST:event_ingresosManualActionPerformed

    private void cargarDetallesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cargarDetallesActionPerformed
        // TODO add your handling code here:
        this.escenarioNormal.setActivos(Double.parseDouble(activosNuevos.getText()));
        this.escenarioNormal.setActivosAnteriores(Double.parseDouble(activosAnteriores.getText()));
        this.escenarioNormal.setEscala(Double.parseDouble(escala.getText()));
        this.recargarDatos();
    }//GEN-LAST:event_cargarDetallesActionPerformed

    private void pronosticarModeloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pronosticarModeloActionPerformed
        // TODO add your handling code here:
        ModeloRegresion r = new ModeloRegresion(null, false);
        r.standalone();
    }//GEN-LAST:event_pronosticarModeloActionPerformed

    private void modeloPorcentualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modeloPorcentualActionPerformed
        // TODO add your handling code here:
        EstimacionPorcentual ep = new EstimacionPorcentual(null, false);
        ep.standalone();
    }//GEN-LAST:event_modeloPorcentualActionPerformed

    private void botonTMAR1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonTMAR1ActionPerformed
        // TODO add your handling code here:
        this.calculoTMAR();
    }//GEN-LAST:event_botonTMAR1ActionPerformed

    private void botonTMAR2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonTMAR2ActionPerformed
        // TODO add your handling code here:
        this.calculoTMAR();
    }//GEN-LAST:event_botonTMAR2ActionPerformed

    private void opcionInversionMasActivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionInversionMasActivosActionPerformed
        // TODO add your handling code here:
        this.escenarioNormal.setTipoInversionInicial(Escenario.INVERSION_INICIAL_ACTIVOS_MAS_PRESTAMOS);
        this.recargarDatos();
    }//GEN-LAST:event_opcionInversionMasActivosActionPerformed

    private void opcionSoloInversionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionSoloInversionActionPerformed
        // TODO add your handling code here:
        this.escenarioNormal.setTipoInversionInicial(Escenario.INVERSION_INICIAL_SOLO_PRESTAMOS);
        this.recargarDatos();
    }//GEN-LAST:event_opcionSoloInversionActionPerformed

    private void opcionSoloActivosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionSoloActivosActionPerformed
        // TODO add your handling code here:
        this.escenarioNormal.setTipoInversionInicial(Escenario.INVERSION_INICIAL_SOLO_ACTIVOS);
        this.recargarDatos();
    }//GEN-LAST:event_opcionSoloActivosActionPerformed

    private void opcionManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_opcionManualActionPerformed
        // TODO add your handling code here:
        String inv = JOptionPane.showInputDialog("Ingrese la inversión inicial (si se deja en blanco se tomará como la suma de activos + inversiones)");
        if (inv!=null){
            this.escenarioNormal.setInversionInicial(Double.parseDouble(inv));
            this.recargarDatos();
        }
    }//GEN-LAST:event_opcionManualActionPerformed

    private void eliminarVariableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarVariableActionPerformed
        // TODO add your handling code here:
        int index = tablaPrincipal.getSelectedRow();
        String nombre = mapa.get(index);
        if (nombre.equals("Ingresos")){
            //this.ingresos();
            //elegir si se cambia el modelo o los datos manualmente
            JOptionPane.showMessageDialog(null, "No se puede eliminar el modelo de ingresos", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
        else if (nombre.equals("Costos")){
            JOptionPane.showMessageDialog(null, "No se puede eliminar el modelo de costos", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
        else if (this.escenarioNormal.gastoValido(nombre)){
            int opc = JOptionPane.showConfirmDialog(null, "¿Realmente desea eliminar el gasto "+nombre+" ?","Eliminar gasto",JOptionPane.YES_NO_OPTION);
            if (opc==0){
                this.escenarioNormal.removerGasto(this.escenarioNormal.obtenerGasto(nombre));
                recargarDatos();
                JOptionPane.showMessageDialog(null, "Gasto "+nombre+" eliminado", "Correcto", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        else if (this.escenarioNormal.interesValido(nombre)){
            //modificar
            int opc = JOptionPane.showConfirmDialog(null, "¿Realmente desea eliminar éste préstamo?","Eliminar Préstamo",JOptionPane.YES_NO_OPTION);
            if (opc==0){
                this.escenarioNormal.removerPrestamo(this.escenarioNormal.obtenerInteres(nombre));
                recargarDatos();
                JOptionPane.showMessageDialog(null, "Préstamo eliminado", "Correcto", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_eliminarVariableActionPerformed

    private void menuAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuAbrirActionPerformed
        // TODO add your handling code here:
        JFileChooser selector = new JFileChooser();
        int resultado = selector.showOpenDialog(null);
        if (resultado==JFileChooser.APPROVE_OPTION){
            this.escenarioNormal = Escenario.deserializarDeXML(selector.getSelectedFile().getAbsolutePath());
            /*
             * setear las opciones primero, antes de recargar los datos
             */
            this.cargarOpcionesEscenario();
            this.mapa = new HashMap<Integer, String>();
            this.mostrarModelo();
            this.recargarDatos();
            this.mostrarTMAR();
        }       
    }//GEN-LAST:event_menuAbrirActionPerformed

    private void insertarGastoPorcentualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertarGastoPorcentualActionPerformed
        // TODO add your handling code here:
        int index = tablaPrincipal.getSelectedRow();
        String nombre = mapa.get(index);
        if (nombre.equals("Ingresos")){
            this.gastoSegunVariable(this.escenarioNormal.getNumeroPeriodos(),Gasto.GASTO_SEGUN_INGRESOS);
        }
        else if (nombre.equals("Costos")){  
            this.gastoSegunVariable(this.escenarioNormal.getNumeroPeriodos(),Gasto.GASTO_SEGUN_COSTOS);
        }
        else if (this.escenarioNormal.gastoValido(nombre)){ 
            Gasto t = this.escenarioNormal.obtenerGasto(nombre);
            this.gastoSegunGasto(t);
        }
        else if (this.escenarioNormal.interesValido(nombre)){
            Intereses i = this.escenarioNormal.obtenerInteres(nombre);
            this.gastoSegunInversion(i);
        }
    }//GEN-LAST:event_insertarGastoPorcentualActionPerformed

    private void menuEscenariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuEscenariosActionPerformed
        // TODO add your handling code here:
        TeoriaEscenarios te = new TeoriaEscenarios(null, false);
        te.estimarEscenarios(escenarioNormal);
    }//GEN-LAST:event_menuEscenariosActionPerformed

    private void menuRecargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuRecargarActionPerformed
        // TODO add your handling code here:
        this.recargarDatos();
    }//GEN-LAST:event_menuRecargarActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.recargarDatos();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void menuGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuGuardarActionPerformed
        // TODO add your handling code here:
        JFileChooser selector = new JFileChooser();
        int resultado = selector.showSaveDialog(null);
        if (resultado==JFileChooser.APPROVE_OPTION){
            this.escenarioNormal.serializarAXML(selector.getSelectedFile().getAbsolutePath());
        }    
    }//GEN-LAST:event_menuGuardarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu Pronosticos;
    private javax.swing.JTextField activosAnteriores;
    private javax.swing.JTextField activosNuevos;
    private javax.swing.JButton botonCostos;
    private javax.swing.JButton botonGastos;
    private javax.swing.JButton botonIngresos;
    private javax.swing.JButton botonInteres;
    private javax.swing.JButton botonTMAR1;
    private javax.swing.JButton botonTMAR2;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.ButtonGroup buttonGroup6;
    private javax.swing.ButtonGroup buttonGroup7;
    private javax.swing.JButton calcularISR;
    private javax.swing.JButton calcularIVA;
    private javax.swing.JMenuItem calcularIntereses;
    private javax.swing.JMenuItem calcularTMAR;
    private javax.swing.JButton calcularTodo;
    private javax.swing.JButton calcularUAI;
    private javax.swing.JButton calcularUtilidaBruta;
    private javax.swing.JButton calcularVAN;
    private javax.swing.JButton cargarDetalles;
    private javax.swing.JMenuItem costosManual;
    private javax.swing.JMenuItem costosModeloPronosticacion;
    private javax.swing.JMenuItem costosPorcentaje;
    private javax.swing.JMenuItem eliminarVariable;
    private javax.swing.JTextField escala;
    private javax.swing.JMenuItem establecerPeriodo;
    private javax.swing.JMenuItem extraISO;
    private javax.swing.JMenuItem extraISR;
    private javax.swing.JMenuItem extraIVA;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.JButton flujoEfectivoNeto;
    private javax.swing.JMenuItem gastosCoutaFija;
    private javax.swing.JMenuItem gastosDepreciaciones;
    private javax.swing.JMenuItem gastosEscalonados;
    private javax.swing.JMenuItem gastosManual;
    private javax.swing.JMenuItem gastosModeloPronosticacion;
    private javax.swing.JMenuItem gastosSegunVariable;
    private javax.swing.JMenuItem ingresosManual;
    private javax.swing.JMenuItem insertarGastoPorcentual;
    private javax.swing.JMenuItem insertarIngresos;
    private javax.swing.JTextField inversionInicial;
    private javax.swing.JButton iso;
    private javax.swing.JMenuItem itemISO;
    private javax.swing.JMenuItem itemISR;
    private javax.swing.JMenuItem itemIVA;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuItem menuAbrir;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menuCalcularTodo;
    private javax.swing.JMenuItem menuCantidadesExactas;
    private javax.swing.JMenuItem menuEscenarios;
    private javax.swing.JMenu menuExtrasImpuestos;
    private javax.swing.JMenuItem menuGuardar;
    private javax.swing.JMenu menuHerramientas;
    private javax.swing.JMenu menuImpuestos;
    private javax.swing.JMenu menuOperaciones;
    private javax.swing.JMenuItem menuRecargar;
    private javax.swing.JMenu menuTMAR;
    private javax.swing.JMenu menuVariables;
    private javax.swing.JMenuItem modeloPorcentual;
    private javax.swing.JMenuItem modificarVariable;
    private javax.swing.JRadioButton noVerDetalles;
    private javax.swing.JRadioButton opcionDatosBrutos;
    private javax.swing.JRadioButton opcionDatosNetos;
    private javax.swing.JRadioButton opcionEmpresaIndividual;
    private javax.swing.JRadioButton opcionEmpresaJuridica;
    private javax.swing.JRadioButton opcionEmpresaNoNueva;
    private javax.swing.JRadioButton opcionEmpresaNueva;
    private javax.swing.JRadioButton opcionISOaISR;
    private javax.swing.JRadioButton opcionISRaISO;
    private javax.swing.JRadioButton opcionInversionMasActivos;
    private javax.swing.JRadioButton opcionManual;
    private javax.swing.JRadioButton opcionPatente;
    private javax.swing.JRadioButton opcionSinPatente;
    private javax.swing.JRadioButton opcionSoloActivos;
    private javax.swing.JRadioButton opcionSoloInversion;
    private javax.swing.JPanel panelAcciones;
    private javax.swing.JPanel panelCaracteristicas;
    private javax.swing.JPanel panelDatosExactos;
    private javax.swing.JPanel panelDetalles;
    private javax.swing.JPanel panelEmpresaNueva;
    private javax.swing.JPanel panelISO;
    private javax.swing.JPanel panelPatente;
    private javax.swing.JTabbedPane panelPrincipal;
    private javax.swing.JPanel panelSalida;
    private javax.swing.JTabbedPane panelTabs;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JMenuItem pronosticarModelo;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTable tablaDatosExactos;
    private javax.swing.JTable tablaPrincipal;
    private javax.swing.JTable tablaResultados;
    private javax.swing.JTable tablaTmar;
    private javax.swing.JTextField tmarPonderada;
    private javax.swing.JButton utilidadNeta;
    private javax.swing.JRadioButton verDetalles;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;

    private void calcularTodo(){
        this.calculoUAI();
        this.calculoIVA();
        this.calculoISR();
        this.calculoISO();        
        this.insertarFilaTablaPrincipal("Utilidad Neta", this.escenarioNormal.calcularUtilidadNeta());
        this.insertarFilaTablaPrincipal("FEN", this.escenarioNormal.calcularFEN());
        this.insertarFilaTablaPrincipal("TMAR", this.escenarioNormal.getTMARFormateada());
        this.insertarFilaTablaPrincipal("VAN", this.escenarioNormal.calcularVAN());
        this.llenarDetalles();
    }
    
    private void llenarDetalles(){
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Total de inversion");
        modelo.addColumn("TMAR");
        modelo.addColumn("VAN");
        modelo.addColumn("Payback");
        modelo.addColumn("TIR");
        
        
        modelo.addRow(new Object[]{this.escenarioNormal.getInversionInicial(),this.escenarioNormal.getTMARFormateada(),ModeloPorcentual.redondearCifra(this.escenarioNormal.getSumatoriaVAN()),this.escenarioNormal.getPayback_string(),null});
        tablaResultados.setModel(modelo);
        
        //TIR
        if ((this.escenarioNormal!=null)&&(this.escenarioNormal.getVAN()!=null))
            modelo.setValueAt(this.escenarioNormal.getTIR_string(), 0, 4);
        
        
    }
    
    private void costosPorPorcentaje(){
        EstimacionPorcentual ventanaCostos = new EstimacionPorcentual(null, true);
        ModeloPorcentual mPorcentual = ventanaCostos.estimarPorcentaje("Costos");
        
        if (mPorcentual!=null){
            Costos mCostos = new Costos();
            mCostos.setFactura(true);
            mCostos.setPadre(escenarioNormal);
            mCostos.setModeloPorcentual(mPorcentual);
            escenarioNormal.setModeloCostos(mCostos);

            double [] listaCostos = mCostos.getPronosticarCostos();
            if (listaCostos!=null){
                this.insertarFilaTablaPrincipal("Costos", mCostos.getFactura(),listaCostos);
            }
        }
    }
    
    private void costosPorModeloPronostico(){
        ModeloRegresion ventana = new ModeloRegresion(null, true);
        Modelo modeloR = ventana.determinarModeloOptimo("Costos");
        if (modeloR!=null){
            Costos temporal = new Costos();
            temporal.setPadre(escenarioNormal);
            temporal.setFactura(true);
            temporal.setModeloPronosticacion(modeloR);
            this.escenarioNormal.setModeloCostos(temporal);
            this.insertarFilaTablaPrincipal("Costos",temporal.getFactura(), temporal.getPronosticarCostos());
        }        
    }
    
    private void gastoEscalonado(){
        ModuloGastosEscalonados ventanaGasto = new ModuloGastosEscalonados(null, true);
        Gasto temporal = ventanaGasto.obtenerModeloGasto(this.escenarioNormal.getNumeroPeriodos());
        if (temporal!=null){
            temporal.setPadre(escenarioNormal);
            temporal.calcularGastos();
            this.escenarioNormal.insertarGasto(temporal);
            this.insertarFilaTablaPrincipal(temporal.getNombreGasto(), temporal.getFactura() ,temporal.getListaGastos());
        }
    }
    
    private void gastoCuotaFija(){
        ModuloGastosCoutaFija ventanaGasto = new ModuloGastosCoutaFija(null, true);
        Gasto temporal = ventanaGasto.obtenerModeloGasto(this.escenarioNormal.getNumeroPeriodos());
        if (temporal!=null){
            temporal.setPadre(escenarioNormal);
            temporal.calcularGastos();
            this.insertarFilaTablaPrincipal(temporal.getNombreGasto(), temporal.getFactura() ,temporal.getListaGastos());
            this.escenarioNormal.insertarGasto(temporal);
        }
    }
    
    private void gastoSegunVariable(){
        ModuloGastosSegunVariable ventanaGasto = new ModuloGastosSegunVariable(null, true);
        Gasto temporal = ventanaGasto.obtenerNuevoModeloGasto(this.escenarioNormal.getNumeroPeriodos());
        if (temporal!=null){
            temporal.setPadre(escenarioNormal);
            temporal.calcularGastos();
            this.escenarioNormal.insertarGasto(temporal);
            this.insertarFilaTablaPrincipal(temporal.getNombreGasto(), temporal.getFactura(), temporal.getListaGastos());
        }
    }
    
    private void gastoSegunVariable(int periodos, int tipoVariable){
        ModuloGastosSegunVariable ventanaGasto = new ModuloGastosSegunVariable(null, true);
        Gasto temporal = ventanaGasto.obtenerNuevoModeloGasto(periodos,tipoVariable);
        if (temporal!=null){
            temporal.setPadre(escenarioNormal);
            temporal.calcularGastos();
            this.escenarioNormal.insertarGasto(temporal);
            this.insertarFilaTablaPrincipal(temporal.getNombreGasto(), temporal.getFactura(), temporal.getListaGastos());
        }
    }
    
    private void gastoSegunGasto(Gasto gasto){
        ModuloGastosSegunVariable ventanaGasto = new ModuloGastosSegunVariable(null, true);
        Gasto temporal = ventanaGasto.obtenerNuevoModeloGasto(gasto.getCantidadPeriodos(),Gasto.GASTO_SEGUN_GASTO);
        if (temporal!=null){
            temporal.setGastoBase(gasto);
            temporal.setPadre(escenarioNormal);
            temporal.calcularGastos();
            this.escenarioNormal.insertarGasto(temporal);
            this.insertarFilaTablaPrincipal(temporal.getNombreGasto(), temporal.getFactura(), temporal.getListaGastos());
        }
    }
    
    private void gastoSegunInversion(Intereses intereses){
        ModuloGastosSegunVariable ventanaGasto = new ModuloGastosSegunVariable(null, true);
        Gasto temporal = ventanaGasto.obtenerNuevoModeloGasto(intereses.getListaCuotasAnuales().length,Gasto.GASTO_SEGUN_INVERSION);
        if (temporal!=null){
            temporal.setInteresesBase(intereses);
            temporal.setPadre(escenarioNormal);
            temporal.calcularGastos();
            this.escenarioNormal.insertarGasto(temporal);
            this.insertarFilaTablaPrincipal(temporal.getNombreGasto(), temporal.getFactura(), temporal.getListaGastos());
        }
    }

    private void gastosDepreciaciones() {
        ModuloGastosPorcentuales ventanaGasto = new ModuloGastosPorcentuales(null, true);
        Gasto temporal = ventanaGasto.obtenerModeloGasto(this.escenarioNormal.getNumeroPeriodos());
        if (temporal!=null){
            temporal.setPadre(escenarioNormal);

            temporal.calcularGastos();

            this.insertarFilaTablaPrincipal(temporal.getNombreGasto(),temporal.getFactura(), temporal.getListaGastos());
            this.escenarioNormal.insertarGasto(temporal);
        }
    }

    private void gastosModeloPronostico() {
        ModuloGastosPorModelo ventanaGasto = new ModuloGastosPorModelo(null, true);
        Gasto temporal = ventanaGasto.obtenerModeloGasto(this.escenarioNormal.getNumeroPeriodos());
        if (temporal!=null){
            temporal.setPadre(escenarioNormal);

            ModeloRegresion ventanaRegresion = new ModeloRegresion(null, true);
            Modelo optimo = ventanaRegresion.determinarModeloOptimo("Gasto: "+temporal.getNombreGasto());

            temporal.setModeloGastos(optimo);
            temporal.calcularGastos();

            this.insertarFilaTablaPrincipal(temporal.getNombreGasto(),temporal.getFactura(), temporal.getListaGastos());
            this.escenarioNormal.insertarGasto(temporal);
        }
    }
    
    private void intereses(){
        CalculoIntereses calculadoraIntereses = new CalculoIntereses(null, true);
        Intereses temporal = calculadoraIntereses.calcularIntereses();
        temporal.setPadre(escenarioNormal);
        this.escenarioNormal.insertarIntereses(temporal);
        //this.insertarFilaTablaPrincipal("Intereses", false,temporal.getListaCoutasAnuales());
        this.insertarInteresTablaPrincipal(temporal.getNombre(), true, temporal.getListaCuotasAnuales());
    }

    private void ingresos() {
        ModeloRegresion ventanaEstimacionValoresFuturos = new ModeloRegresion(null, true);
        Modelo temp = ventanaEstimacionValoresFuturos.determinarModeloOptimo("Ingresos");

        if (temp!=null){
            Ingresos mIngresos = new Ingresos();
            mIngresos.setPadre(escenarioNormal);
            mIngresos.setModeloIngresos(temp);
            mIngresos.setIngresosHistoricos(temp.getY());
            escenarioNormal.setModeloIngresos(mIngresos);

            double [] listaIngresos = mIngresos.getCalcularIngresos();

            if (listaIngresos!=null){
                this.insertarFilaTablaPrincipal("Ingresos", listaIngresos);
            }
        }
    }
    
    private void calculoIVA(){
        this.escenarioNormal.setDatosNetos(this.opcionDatosNetos.isSelected());
        if (this.escenarioNormal.getModeloIVA()!=null){
            this.insertarFilaTablaPrincipal("IVA por pagar", this.escenarioNormal.IVAporPagar());
        } else{           
            
            IVA modelo = new IVA();            
            modelo.setPadre(escenarioNormal);
            this.escenarioNormal.setModeloIVA(modelo);
            this.insertarFilaTablaPrincipal("IVA por pagar", this.escenarioNormal.IVAporPagar());
            
        }
        if (verDetalles.isSelected()){
            Detalles dt = new Detalles(null, false);
            dt.verDetallesCalculoIVA(this.escenarioNormal);
        }
    }
    
    private void calculoUAI(){
        if (this.buscarCuenta("UAI")==false){
            this.insertarFilaTablaPrincipal("UAI", this.escenarioNormal.utilidadAntesDeImpuestos());
        }
    }
    
    private void calculoISR(){
        this.escenarioNormal.setDatosNetos(this.opcionDatosNetos.isSelected());
        escenarioNormal.setEmpresaIndividual(opcionEmpresaIndividual.isSelected());
        escenarioNormal.calcularISR(opcionEmpresaIndividual.isSelected());
        //this.insertarFilaTablaPrincipal("Regimen", modeloISR.getRegimen());
        this.insertarFilaTablaPrincipal("ISR temporal", escenarioNormal.getModeloISR().getISRPorPagarTemporal());
        if (verDetalles.isSelected()){
            Detalles dt = new Detalles(null, false);
            dt.verDetallesCalculoISR(this.escenarioNormal);
        }
    }
    
    private void calculoISO(){
        this.escenarioNormal.setDatosNetos(this.opcionDatosNetos.isSelected());
        this.escenarioNormal.setEmpresaNueva(opcionEmpresaNueva.isSelected());
        this.escenarioNormal.setPatenteDeComercio(opcionPatente.isSelected());
        if (this.escenarioNormal.getModeloISO()!=null){
            if (opcionISOaISR.isSelected())
                this.escenarioNormal.getModeloISO().setAcreditacion(ISO.ISO_ACREDITABLE_ISR);
            else
                this.escenarioNormal.getModeloISO().setAcreditacion(ISO.ISR_ACREDITABLE_ISO);
            escenarioNormal.calcularISR(opcionEmpresaIndividual.isSelected());
            this.escenarioNormal.getModeloISO().calcularISO();
            this.insertarFilaTablaPrincipal("ISR por pagar", this.escenarioNormal.getModeloISR().getISRporPagar());
            this.insertarFilaTablaPrincipal("ISO por pagar", this.escenarioNormal.ISOporPagar());
        }
        else{
            ISO modelo = new ISO();
            if (opcionISOaISR.isSelected())
                modelo.setAcreditacion(ISO.ISO_ACREDITABLE_ISR);
            else
                modelo.setAcreditacion(ISO.ISR_ACREDITABLE_ISO);
            this.escenarioNormal.setModeloISO(modelo);
            modelo.setPadre(escenarioNormal);
            escenarioNormal.calcularISR(opcionEmpresaIndividual.isSelected());
            modelo.calcularISO();
            this.insertarFilaTablaPrincipal("ISR por pagar", this.escenarioNormal.getModeloISR().getISRporPagar());
            this.insertarFilaTablaPrincipal("ISO por pagar", this.escenarioNormal.ISOporPagar());

        }
        if (verDetalles.isSelected()){
            Detalles dt = new Detalles(null, false);
            dt.verDetallesCalculoISO(this.escenarioNormal);
        }
    }
    
    private void insertarFilaTablaDatosExactos(String nombre,boolean factura,double [] valores){
        Object [] fila = new Object[valores.length+2];
        fila[0] = nombre;
        if (factura)
            fila[1] = "S";
        else
            fila[1] = "N";
        //System.arraycopy(valores, 0, fila, 1, valores.length);
        for (int i=0; i<valores.length; i++){
            fila[i+2] = valores[i];
        }
        DefaultTableModel modelo = (DefaultTableModel) this.tablaDatosExactos.getModel();
        modelo.addRow(fila);
    }

    private void insertarFilaTablaDatosExactos(String nombre,double [] valores){
        Object [] fila = new Object[valores.length+2];
        fila[0] = nombre;
        fila[1] = null;
        //System.arraycopy(valores, 0, fila, 1, valores.length);
        for (int i=0; i<valores.length; i++){
            fila[i+2] = valores[i];
        }
        DefaultTableModel modelo = (DefaultTableModel) this.tablaDatosExactos.getModel();
        modelo.addRow(fila);
    }
    
    private void insertarFilaTablaDatosExactos(String nombre,String valor){
        DefaultTableModel modelo = (DefaultTableModel) this.tablaDatosExactos.getModel();
        Object [] fila = new Object[this.tablaDatosExactos.getColumnCount()+2];
        fila[0] = nombre;
        fila[1] = null;
        //System.arraycopy(valores, 0, fila, 1, valores.length);
        for (int i=2; i<fila.length; i++){
            fila[i] = valor;
        }
        
        modelo.addRow(fila);
    }
    
    private void insertarInteresTablaDatosExactos(String nombre,boolean val,double [] valores){
        Object [] fila = new Object[valores.length+2];
        fila[0] = "Intereses";
        fila[1] = val;
        //System.arraycopy(valores, 0, fila, 1, valores.length);
        for (int i=0; i<valores.length; i++){
            fila[i+2] = valores[i];
        }
        DefaultTableModel modelo = (DefaultTableModel) this.tablaDatosExactos.getModel();
        modelo.addRow(fila);
    }

    private void costosManual() {
        Manual m = new Manual(null, true);
        double [] costos = m.insertarDatosManuales(this.escenarioNormal.getListaAnios());
        if (costos!=null){        
            Costos temporal = new Costos();
            temporal.setPadre(escenarioNormal);
            temporal.setFactura(true);
            temporal.setCostos(costos);
            this.escenarioNormal.setModeloCostos(temporal);
            this.insertarFilaTablaPrincipal("Costos",temporal.getFactura(), temporal.getPronosticarCostos());
        }
    }

    private void gastosManuales() {
        ModuloGastosSegunVariable mgv = new ModuloGastosSegunVariable(null, true);
        Gasto temporal = mgv.obtenerNuevoModeloGastoManual(this.escenarioNormal.getNumeroPeriodos());
        if (temporal!=null){
            Manual m = new Manual(null, true);
            double [] datos = m.insertarDatosManuales(this.escenarioNormal.getListaAnios());
            if (datos!=null){
                temporal.setGastosManualmente(datos);
                temporal.setPadre(escenarioNormal);
                this.escenarioNormal.insertarGasto(temporal);
                this.insertarFilaTablaPrincipal(temporal.getNombreGasto(),temporal.getFactura(), temporal.getListaGastos());
            }
        }
    }

    private void insertarIngresosManualmente() {
        Manual m = new Manual(null, true);
        double [] datos = m.insertarDatosManuales(this.escenarioNormal.getListaAnios());
        if (datos!=null){
            Ingresos i = new Ingresos();
            i.setTipoIngreso(Ingresos.INGRESO_MANUAL);
            i.setListaIngresosManualmente(datos);
            i.setPadre(escenarioNormal);
            escenarioNormal.setModeloIngresos(i);
            this.insertarFilaTablaPrincipal("Ingresos",i.getCalcularIngresos());
        }
    }

    private void calculoTMAR() {
        IngresarInversionistas frmIngresarInversionistas = new IngresarInversionistas(null, true);
        Inversionistas inv = frmIngresarInversionistas.insertarInversionistas();        
        
        if (inv!=null)
        {
            this.escenarioNormal.setTmar(inv);
            this.mostrarTMAR();
            inv.setPadre(escenarioNormal);
            //this.escenarioNormal.setListaInversionistas(inv);
        }
    }

    private void mostrarModelo() {
        int [] anios = escenarioNormal.getListaAnios();
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Concepto");
        modelo.addColumn("Factura");
        for (int i=0; i<anios.length; i++){
            modelo.addColumn(anios[i]);
        }
        this.tablaPrincipal.setModel(modelo);
        menuTMAR.setVisible(true);
        menuVariables.setVisible(true);
        menuImpuestos.setVisible(true);
        panelTabs.setVisible(true);
        menuOperaciones.setVisible(true);
        menuHerramientas.setVisible(true);
    }

    private void mostrarTMAR() {
        DefaultTableModel modeloTabla = new DefaultTableModel();
            modeloTabla.addColumn("Inversionista");
            modeloTabla.addColumn("% Riesgo");
            modeloTabla.addColumn("% Participación");
            modeloTabla.addColumn("TMAR (%)");
            modeloTabla.addColumn("TMAR ponderada (%)");
            ArrayList<Inversionista> lstInversionistas = this.escenarioNormal.getTmar().getListaInversionistas();

            for (Inversionista i: lstInversionistas){
                Object []fila = new Object[5];

                fila[0]=i.getNombre();
                fila[1]=i.getRiesgo()*100;
                fila[2]=i.getParticipacion()*100;
                fila[3]=i.getTmar()*100;
                fila[4]=i.getTmarPonderada()*100;

                modeloTabla.addRow(fila);

            }
            tablaTmar.setModel(modeloTabla);
            tmarPonderada.setText(this.escenarioNormal.getTMARFormateada());
    }

    private void cargarOpcionesEscenario() {
        if (this.escenarioNormal.getDatosNetos())
                this.opcionDatosNetos.setSelected(true);
            else
                this.opcionDatosBrutos.setSelected(true);
            
            if (this.escenarioNormal.getEmpresaIndividual())
                this.opcionEmpresaIndividual.setSelected(true);
            else
                this.opcionEmpresaJuridica.setSelected(true);
            if (this.escenarioNormal.getEmpresaNueva())
                this.opcionEmpresaNueva.setSelected(true);
            else
                this.opcionEmpresaNoNueva.setSelected(true);
            if (this.escenarioNormal.getPatenteDeComercio())
                this.opcionPatente.setSelected(true);
            else
                this.opcionSinPatente.setSelected(true);
            
            if (this.escenarioNormal.getModeloISO()!=null){
                if (this.escenarioNormal.getModeloISO().getAcreditacion()==ISO.ISO_ACREDITABLE_ISR)
                    this.opcionISOaISR.setSelected(true);
                else
                    this.opcionISRaISO.setSelected(true);
            }
            this.escala.setText(Double.toString(this.escenarioNormal.getEscala()));
            this.activosNuevos.setText(Double.toString(this.escenarioNormal.getActivos()));
            this.activosAnteriores.setText(Double.toString(this.escenarioNormal.getActivosAnteriores()));
            this.inversionInicial.setText(Double.toString(this.escenarioNormal.getInversionInicial()));
            
            int tipoInversionInicial = this.escenarioNormal.getTipoInversionInicial();
            if (tipoInversionInicial==Escenario.INVERSION_INICIAL_ACTIVOS_MAS_PRESTAMOS)
                this.opcionInversionMasActivos.setSelected(true);
            else if (tipoInversionInicial==Escenario.INVERSION_INICIAL_SOLO_PRESTAMOS)
                this.opcionSoloInversion.setSelected(true);
            else if (tipoInversionInicial==Escenario.INVERSION_INICIAL_SOLO_ACTIVOS)
                this.opcionSoloActivos.setSelected(true);
            else if (tipoInversionInicial==Escenario.INVERSION_INICIAL_MANUAL)
                this.opcionManual.setSelected(true);
    }
}
