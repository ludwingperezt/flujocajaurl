/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ModificarModeloGastos.java
 *
 * Created on 30/09/2012, 07:05:44 PM
 */

package modificaciones;

import Clases.Gasto;
import Clases.Modelo;
import Clases.ModeloPorcentual;
import Intermedias.SeleccionGastos;
import Intermedias.SeleccionVariable;
import controlador.Escenario;
import javax.swing.table.DefaultTableModel;
import flujocaja.*;

/**
 *
 * @author ludwing
 */
public class ModificarModeloGastos extends javax.swing.JDialog {
    
    private Escenario escenarioNormal;
    private Gasto modificado;
    private int tipoGasto;

    /** Creates new form ModificarModeloGastos */
    public ModificarModeloGastos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    public void modificarGasto(Escenario escenarioNormal, Gasto modificado){
        this.setTitle("Modificar gasto: "+modificado.getNombreGasto());
        this.escenarioNormal = escenarioNormal;
        this.modificado = modificado;
        cargarDatos();
        this.setVisible(true);
    }
    
    private void cargarDatos(){
        this.nombre.setText(modificado.getNombreGasto());
        if (this.modificado.getFactura())
            factura.setSelected(true);
        else
            sinFactura.setSelected(true);
        
        if (this.modificado.getEscudoFiscal())
            escudoFiscal.setSelected(true);
        else
            noEscudoFiscal.setSelected(true);
        
        cargarTablaDatos();
                
        tipoGasto = modificado.getTipoGasto();
        
        if (tipoGasto == Gasto.GASTO_ESCALONADO){
            panelGastoDepreciacion.setVisible(false);
            panelGastoFijo.setVisible(false);
            panelGastoVariable.setVisible(false);
            panelGastoModelo.setVisible(false);
            panelPorcentajesManuales.setVisible(false);
            escalonadoBase.setText(Double.toString(modificado.getBase()));
            escalonadoAnioBase.setText(Integer.toString(modificado.getAnioBase()));
            escalonadoTasaIncremento.setText(Double.toString(modificado.getTasaIncremento()*100));
            escalonadoPeriodos.setText(Integer.toString(modificado.getCantidadPeriodos()));
        }else if (tipoGasto == Gasto.GASTO_FIJO){
            panelGastoDepreciacion.setVisible(false);
            panelGastoVariable.setVisible(false);
            panelGastoModelo.setVisible(false);
            panelGastoEscalonado.setVisible(false);
            panelPorcentajesManuales.setVisible(false);
            fijoCuota.setText(Double.toString(modificado.getBase()));
            fijoPeriodos.setText(Integer.toString(modificado.getCantidadPeriodos()));
        }else if (tipoGasto == Gasto.GASTO_SEGUN_COSTOS){
            panelGastoDepreciacion.setVisible(false);
            panelGastoModelo.setVisible(false);
            panelGastoEscalonado.setVisible(false);
            panelGastoFijo.setVisible(false);
            panelPorcentajesManuales.setVisible(false);
            porcentajeAnterior.setText(Double.toHexString(modificado.getModeloPorcentual().getPromedio()*100)+"%");
            variablePeriodos.setText(Integer.toString(modificado.getCantidadPeriodos()));
            costos.setSelected(true);
        }else if (tipoGasto == Gasto.GASTO_SEGUN_INGRESOS){
            panelGastoDepreciacion.setVisible(false);
            panelGastoModelo.setVisible(false);
            panelGastoEscalonado.setVisible(false);
            panelGastoFijo.setVisible(false);
            panelPorcentajesManuales.setVisible(false);
            porcentajeAnterior.setText(Double.toHexString(modificado.getModeloPorcentual().getPromedio()*100)+"%");
            variablePeriodos.setText(Integer.toString(modificado.getCantidadPeriodos()));
            ingresos.setSelected(true);
        }else if (tipoGasto == Gasto.GASTO_SEGUN_MODELO){
            panelGastoDepreciacion.setVisible(false);
            panelGastoEscalonado.setVisible(false);
            panelGastoFijo.setVisible(false);
            panelGastoVariable.setVisible(false);
            panelPorcentajesManuales.setVisible(false);
            modeloPeriodos.setText(Integer.toString(modificado.getCantidadPeriodos()));
        }else if (tipoGasto == Gasto.GASTO_DEPRECIACION){
            panelGastoEscalonado.setVisible(false);
            panelGastoFijo.setVisible(false);
            panelGastoVariable.setVisible(false);
            panelGastoModelo.setVisible(false);
            panelPorcentajesManuales.setVisible(false);
            depreciacionInicial.setText(Double.toString(modificado.getBase()));
            depreciacionPeriodos.setText(Integer.toString(modificado.getCantidadPeriodos()));
            depreciacionPorcentaje.setText(Double.toString(modificado.getTasaIncremento()*100));
            
        }else if (tipoGasto == Gasto.GASTO_MANUAL){
            panelGastoEscalonado.setVisible(false);
            panelGastoFijo.setVisible(false);
            panelGastoVariable.setVisible(false);
            panelGastoModelo.setVisible(false);
            panelGastoDepreciacion.setVisible(false);
            panelPorcentajesManuales.setVisible(false);
        }else if (tipoGasto == Gasto.GASTO_SEGUN_COSTOS_PORCENTAJES_MANUALES){//||())||)||){
            panelGastoEscalonado.setVisible(false);
            panelGastoFijo.setVisible(false);
            panelGastoVariable.setVisible(false);
            panelGastoModelo.setVisible(false);
            panelGastoDepreciacion.setVisible(false);
            this.jTextField1.setText("Porcentual a costos");
            this.cargarPorcentajes();
        }else if (tipoGasto == Gasto.GASTO_SEGUN_INGRESOS_PORCENTAJES_MANUALES){
            panelGastoEscalonado.setVisible(false);
            panelGastoFijo.setVisible(false);
            panelGastoVariable.setVisible(false);
            panelGastoModelo.setVisible(false);
            panelGastoDepreciacion.setVisible(false);
            this.jTextField1.setText("Porcentual a ingresos");
            this.cargarPorcentajes();
        }else if (tipoGasto == Gasto.GASTO_SEGUN_INVERSION_PORCENTAJES_MANUALES){
            panelGastoEscalonado.setVisible(false);
            panelGastoFijo.setVisible(false);
            panelGastoVariable.setVisible(false);
            panelGastoModelo.setVisible(false);
            panelGastoDepreciacion.setVisible(false);
            this.jTextField1.setText("Porcentual a inversión");
            this.cargarPorcentajes();
        }else if (tipoGasto == Gasto.GASTO_SEGUN_GASTO_PORCENTAJES_MANUALES){
            panelGastoEscalonado.setVisible(false);
            panelGastoFijo.setVisible(false);
            panelGastoVariable.setVisible(false);
            panelGastoModelo.setVisible(false);
            panelGastoDepreciacion.setVisible(false);
            this.jTextField1.setText("Porcentual a "+modificado.getGastoBase().getNombreGasto());
            this.cargarPorcentajes();
        }
    }
    private void cargarPorcentajes(){
        DefaultTableModel m = new DefaultTableModel();
        Object [] fila = new Object[this.escenarioNormal.getListaAnios().length];
        for (int i=0; i<this.escenarioNormal.getListaAnios().length; i++){
            m.addColumn(this.escenarioNormal.getListaAnios()[i]);
            fila[i] = Double.toString(ModeloPorcentual.redondearCifra(this.modificado.getModeloPorcentual().getPorcentajesManuales()[i]*100))+"%";
        }
        m.addRow(fila);
        this.jTable1.setModel(m);
    }
    private void cargarTablaDatos(){
        DefaultTableModel modelo = new DefaultTableModel();
        double [] lista = modificado.getListaGastos();
        int [] listaAnios = this.escenarioNormal.getListaAnios();
        Object [] fila = new Object[lista.length];
        
        for (int i=0; i<listaAnios.length; i++){
            modelo.addColumn(listaAnios[i]);
        }        
        for (int i=0; i<modificado.getCantidadPeriodos(); i++){
            fila[i]=lista[i];
        }
        modelo.addRow(fila);
        tablaDatos.setModel(modelo);
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
        panel = new javax.swing.JTabbedPane();
        panelDatos = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaDatos = new javax.swing.JTable();
        editar = new javax.swing.JButton();
        panelGastoEscalonado = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        escalonadoBase = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        escalonadoAnioBase = new javax.swing.JTextField();
        escalonadoPeriodos = new javax.swing.JTextField();
        escalonadoTasaIncremento = new javax.swing.JTextField();
        panelGastoFijo = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        fijoCuota = new javax.swing.JTextField();
        fijoPeriodos = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        panelGastoVariable = new javax.swing.JPanel();
        variablePeriodos = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        ingresos = new javax.swing.JRadioButton();
        costos = new javax.swing.JRadioButton();
        variableCambiarAModelo = new javax.swing.JButton();
        variableCambiarPorcentaje = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        porcentajeAnterior = new javax.swing.JTextField();
        panelGastoModelo = new javax.swing.JPanel();
        modeloPeriodos = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        modeloCambiarAPorcentaje = new javax.swing.JButton();
        modeloCambiarModelo = new javax.swing.JButton();
        panelGastoDepreciacion = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        depreciacionPeriodos = new javax.swing.JTextField();
        depreciacionPorcentaje = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        depreciacionInicial = new javax.swing.JTextField();
        panelPorcentajesManuales = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        botonCambiarPorcentajes = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        factura = new javax.swing.JRadioButton();
        sinFactura = new javax.swing.JRadioButton();
        nombre = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        aceptarManual = new javax.swing.JButton();
        cambiarTipoGasto3 = new javax.swing.JButton();
        panelEscudoFiscal = new javax.swing.JPanel();
        escudoFiscal = new javax.swing.JRadioButton();
        noEscudoFiscal = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Form"); // NOI18N

        panel.setName("panel"); // NOI18N

        panelDatos.setName("panelDatos"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tablaDatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaDatos.setName("tablaDatos"); // NOI18N
        jScrollPane1.setViewportView(tablaDatos);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(flujocaja.FlujoCajaApp.class).getContext().getResourceMap(ModificarModeloGastos.class);
        editar.setText(resourceMap.getString("editar.text")); // NOI18N
        editar.setName("editar"); // NOI18N
        editar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelDatosLayout = new javax.swing.GroupLayout(panelDatos);
        panelDatos.setLayout(panelDatosLayout);
        panelDatosLayout.setHorizontalGroup(
            panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 703, Short.MAX_VALUE)
                    .addComponent(editar))
                .addContainerGap())
        );
        panelDatosLayout.setVerticalGroup(
            panelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDatosLayout.createSequentialGroup()
                .addComponent(editar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)
                .addContainerGap())
        );

        panel.addTab(resourceMap.getString("panelDatos.TabConstraints.tabTitle"), panelDatos); // NOI18N

        panelGastoEscalonado.setName("panelGastoEscalonado"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        escalonadoBase.setName("escalonadoBase"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        escalonadoAnioBase.setName("escalonadoAnioBase"); // NOI18N

        escalonadoPeriodos.setName("escalonadoPeriodos"); // NOI18N

        escalonadoTasaIncremento.setName("escalonadoTasaIncremento"); // NOI18N

        javax.swing.GroupLayout panelGastoEscalonadoLayout = new javax.swing.GroupLayout(panelGastoEscalonado);
        panelGastoEscalonado.setLayout(panelGastoEscalonadoLayout);
        panelGastoEscalonadoLayout.setHorizontalGroup(
            panelGastoEscalonadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGastoEscalonadoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGastoEscalonadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(panelGastoEscalonadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(escalonadoAnioBase, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(escalonadoBase, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(escalonadoTasaIncremento, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(escalonadoPeriodos, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(473, Short.MAX_VALUE))
        );
        panelGastoEscalonadoLayout.setVerticalGroup(
            panelGastoEscalonadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGastoEscalonadoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGastoEscalonadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(escalonadoBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelGastoEscalonadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(escalonadoAnioBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelGastoEscalonadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(escalonadoTasaIncremento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelGastoEscalonadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(escalonadoPeriodos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap(152, Short.MAX_VALUE))
        );

        panel.addTab(resourceMap.getString("panelGastoEscalonado.TabConstraints.tabTitle"), panelGastoEscalonado); // NOI18N

        panelGastoFijo.setName("panelGastoFijo"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        fijoCuota.setName("fijoCuota"); // NOI18N

        fijoPeriodos.setName("fijoPeriodos"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        javax.swing.GroupLayout panelGastoFijoLayout = new javax.swing.GroupLayout(panelGastoFijo);
        panelGastoFijo.setLayout(panelGastoFijoLayout);
        panelGastoFijoLayout.setHorizontalGroup(
            panelGastoFijoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGastoFijoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGastoFijoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGastoFijoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fijoPeriodos, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fijoCuota, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(540, Short.MAX_VALUE))
        );
        panelGastoFijoLayout.setVerticalGroup(
            panelGastoFijoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGastoFijoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGastoFijoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fijoCuota, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGastoFijoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fijoPeriodos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addContainerGap(219, Short.MAX_VALUE))
        );

        panel.addTab(resourceMap.getString("panelGastoFijo.TabConstraints.tabTitle"), panelGastoFijo); // NOI18N

        panelGastoVariable.setName("panelGastoVariable"); // NOI18N

        variablePeriodos.setName("variablePeriodos"); // NOI18N

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel5.border.title"))); // NOI18N
        jPanel5.setName("jPanel5"); // NOI18N

        buttonGroup2.add(ingresos);
        ingresos.setSelected(true);
        ingresos.setText(resourceMap.getString("ingresos.text")); // NOI18N
        ingresos.setName("ingresos"); // NOI18N

        buttonGroup2.add(costos);
        costos.setText(resourceMap.getString("costos.text")); // NOI18N
        costos.setName("costos"); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ingresos)
                    .addComponent(costos))
                .addContainerGap(103, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(ingresos)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(costos)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        variableCambiarAModelo.setText(resourceMap.getString("variableCambiarAModelo.text")); // NOI18N
        variableCambiarAModelo.setName("variableCambiarAModelo"); // NOI18N
        variableCambiarAModelo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                variableCambiarAModeloActionPerformed(evt);
            }
        });

        variableCambiarPorcentaje.setText(resourceMap.getString("variableCambiarPorcentaje.text")); // NOI18N
        variableCambiarPorcentaje.setName("variableCambiarPorcentaje"); // NOI18N
        variableCambiarPorcentaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                variableCambiarPorcentajeActionPerformed(evt);
            }
        });

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        porcentajeAnterior.setEditable(false);
        porcentajeAnterior.setText(resourceMap.getString("porcentajeAnterior.text")); // NOI18N
        porcentajeAnterior.setName("porcentajeAnterior"); // NOI18N

        javax.swing.GroupLayout panelGastoVariableLayout = new javax.swing.GroupLayout(panelGastoVariable);
        panelGastoVariable.setLayout(panelGastoVariableLayout);
        panelGastoVariableLayout.setHorizontalGroup(
            panelGastoVariableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGastoVariableLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGastoVariableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelGastoVariableLayout.createSequentialGroup()
                        .addGroup(panelGastoVariableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelGastoVariableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(porcentajeAnterior)
                            .addComponent(variablePeriodos, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE))
                        .addContainerGap(529, Short.MAX_VALUE))
                    .addGroup(panelGastoVariableLayout.createSequentialGroup()
                        .addGroup(panelGastoVariableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelGastoVariableLayout.createSequentialGroup()
                                .addComponent(variableCambiarPorcentaje)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(variableCambiarAModelo))
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(463, 463, 463))))
        );
        panelGastoVariableLayout.setVerticalGroup(
            panelGastoVariableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGastoVariableLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGastoVariableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(variablePeriodos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelGastoVariableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(porcentajeAnterior, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGastoVariableLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(variableCambiarPorcentaje)
                    .addComponent(variableCambiarAModelo))
                .addContainerGap(87, Short.MAX_VALUE))
        );

        panel.addTab(resourceMap.getString("panelGastoVariable.TabConstraints.tabTitle"), panelGastoVariable); // NOI18N

        panelGastoModelo.setName("panelGastoModelo"); // NOI18N

        modeloPeriodos.setName("modeloPeriodos"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        modeloCambiarAPorcentaje.setText(resourceMap.getString("modeloCambiarAPorcentaje.text")); // NOI18N
        modeloCambiarAPorcentaje.setName("modeloCambiarAPorcentaje"); // NOI18N
        modeloCambiarAPorcentaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modeloCambiarAPorcentajeActionPerformed(evt);
            }
        });

        modeloCambiarModelo.setText(resourceMap.getString("modeloCambiarModelo.text")); // NOI18N
        modeloCambiarModelo.setName("modeloCambiarModelo"); // NOI18N
        modeloCambiarModelo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modeloCambiarModeloActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelGastoModeloLayout = new javax.swing.GroupLayout(panelGastoModelo);
        panelGastoModelo.setLayout(panelGastoModeloLayout);
        panelGastoModeloLayout.setHorizontalGroup(
            panelGastoModeloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGastoModeloLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGastoModeloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelGastoModeloLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(modeloPeriodos, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelGastoModeloLayout.createSequentialGroup()
                        .addComponent(modeloCambiarModelo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(modeloCambiarAPorcentaje)))
                .addContainerGap(463, Short.MAX_VALUE))
        );
        panelGastoModeloLayout.setVerticalGroup(
            panelGastoModeloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGastoModeloLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGastoModeloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(modeloPeriodos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelGastoModeloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(modeloCambiarModelo)
                    .addComponent(modeloCambiarAPorcentaje))
                .addContainerGap(211, Short.MAX_VALUE))
        );

        panel.addTab(resourceMap.getString("panelGastoModelo.TabConstraints.tabTitle"), panelGastoModelo); // NOI18N

        panelGastoDepreciacion.setName("panelGastoDepreciacion"); // NOI18N

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        depreciacionPeriodos.setName("depreciacionPeriodos"); // NOI18N

        depreciacionPorcentaje.setName("depreciacionPorcentaje"); // NOI18N

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        depreciacionInicial.setName("depreciacionInicial"); // NOI18N

        javax.swing.GroupLayout panelGastoDepreciacionLayout = new javax.swing.GroupLayout(panelGastoDepreciacion);
        panelGastoDepreciacion.setLayout(panelGastoDepreciacionLayout);
        panelGastoDepreciacionLayout.setHorizontalGroup(
            panelGastoDepreciacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGastoDepreciacionLayout.createSequentialGroup()
                .addGroup(panelGastoDepreciacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelGastoDepreciacionLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(depreciacionInicial, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelGastoDepreciacionLayout.createSequentialGroup()
                        .addGroup(panelGastoDepreciacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panelGastoDepreciacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(depreciacionPeriodos, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(depreciacionPorcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(495, Short.MAX_VALUE))
        );
        panelGastoDepreciacionLayout.setVerticalGroup(
            panelGastoDepreciacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGastoDepreciacionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGastoDepreciacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(depreciacionInicial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGastoDepreciacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(depreciacionPorcentaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGastoDepreciacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(depreciacionPeriodos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addContainerGap(193, Short.MAX_VALUE))
        );

        panel.addTab(resourceMap.getString("panelGastoDepreciacion.TabConstraints.tabTitle"), panelGastoDepreciacion); // NOI18N

        panelPorcentajesManuales.setName("panelPorcentajesManuales"); // NOI18N

        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
        jTextField1.setName("jTextField1"); // NOI18N

        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane2.setViewportView(jTable1);

        botonCambiarPorcentajes.setText(resourceMap.getString("botonCambiarPorcentajes.text")); // NOI18N
        botonCambiarPorcentajes.setName("botonCambiarPorcentajes"); // NOI18N
        botonCambiarPorcentajes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCambiarPorcentajesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelPorcentajesManualesLayout = new javax.swing.GroupLayout(panelPorcentajesManuales);
        panelPorcentajesManuales.setLayout(panelPorcentajesManualesLayout);
        panelPorcentajesManualesLayout.setHorizontalGroup(
            panelPorcentajesManualesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPorcentajesManualesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPorcentajesManualesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 703, Short.MAX_VALUE)
                    .addGroup(panelPorcentajesManualesLayout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel15)
                    .addComponent(botonCambiarPorcentajes, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        panelPorcentajesManualesLayout.setVerticalGroup(
            panelPorcentajesManualesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPorcentajesManualesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelPorcentajesManualesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(botonCambiarPorcentajes))
        );

        panel.addTab(resourceMap.getString("panelPorcentajesManuales.TabConstraints.tabTitle"), panelPorcentajesManuales); // NOI18N

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("jPanel4.border.title"))); // NOI18N
        jPanel4.setName("jPanel4"); // NOI18N

        buttonGroup1.add(factura);
        factura.setSelected(true);
        factura.setText(resourceMap.getString("factura.text")); // NOI18N
        factura.setName("factura"); // NOI18N

        buttonGroup1.add(sinFactura);
        sinFactura.setText(resourceMap.getString("sinFactura.text")); // NOI18N
        sinFactura.setName("sinFactura"); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(factura)
                    .addComponent(sinFactura))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(factura)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sinFactura)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        nombre.setBackground(resourceMap.getColor("nombre.background")); // NOI18N
        nombre.setEditable(false);
        nombre.setName("nombre"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        aceptarManual.setText(resourceMap.getString("aceptarManual.text")); // NOI18N
        aceptarManual.setName("aceptarManual"); // NOI18N
        aceptarManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aceptarManualActionPerformed(evt);
            }
        });

        cambiarTipoGasto3.setText(resourceMap.getString("cambiarTipoGasto3.text")); // NOI18N
        cambiarTipoGasto3.setName("cambiarTipoGasto3"); // NOI18N
        cambiarTipoGasto3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cambiarTipoGasto3ActionPerformed(evt);
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 728, Short.MAX_VALUE)
                    .addComponent(aceptarManual)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(panelEscudoFiscal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 377, Short.MAX_VALUE)
                        .addComponent(cambiarTipoGasto3)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(panelEscudoFiscal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(cambiarTipoGasto3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, 304, Short.MAX_VALUE)
                .addGap(11, 11, 11)
                .addComponent(aceptarManual))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cambiarTipoGasto3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cambiarTipoGasto3ActionPerformed
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
        this.dispose();
    }//GEN-LAST:event_cambiarTipoGasto3ActionPerformed

    private void editarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editarActionPerformed
        // TODO add your handling code here:
        modificado.setFactura(factura.isSelected());
        modificado.setEscudoFiscal(escudoFiscal.isSelected());
        tipoGasto = Gasto.GASTO_MANUAL;
        Manual m = new Manual(null, true);
        double [] datos = m.editarDatosManuales(this.escenarioNormal.getListaAnios(),modificado.getListaGastos());
        if (datos!=null){            
            modificado.setTipoGasto(Gasto.GASTO_MANUAL);
            modificado.setGastosManualmente(datos);
            this.dispose();
        }
        
    }//GEN-LAST:event_editarActionPerformed

    private void aceptarManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aceptarManualActionPerformed
        // TODO add your handling code here:
        modificado.setFactura(factura.isSelected());
        modificado.setEscudoFiscal(escudoFiscal.isSelected());
        
        if (tipoGasto == Gasto.GASTO_ESCALONADO){

            modificado.setBase(Double.parseDouble(escalonadoBase.getText()));
            modificado.setAnioBase(Integer.parseInt(escalonadoAnioBase.getText()));
            modificado.setTasaIncremento(Double.parseDouble(escalonadoTasaIncremento.getText())/100);
            modificado.setCantidadPeriodos(Integer.parseInt(escalonadoPeriodos.getText()));
            
        }else if (tipoGasto == Gasto.GASTO_FIJO){

            modificado.setBase(Double.parseDouble(fijoCuota.getText()));
            modificado.setCantidadPeriodos(Integer.parseInt(fijoPeriodos.getText()));
            
        }else if ((tipoGasto == Gasto.GASTO_SEGUN_COSTOS)||(tipoGasto == Gasto.GASTO_SEGUN_INGRESOS)){
           
            modificado.setCantidadPeriodos(Integer.parseInt(variablePeriodos.getText()));
            if (ingresos.isSelected())
                modificado.setTipoGasto(Gasto.GASTO_SEGUN_INGRESOS);
            else
                modificado.setTipoGasto(Gasto.GASTO_SEGUN_COSTOS);
            
        }else if (tipoGasto == Gasto.GASTO_SEGUN_MODELO){
            modificado.setCantidadPeriodos(Integer.parseInt(modeloPeriodos.getText()));
        }else if (tipoGasto == Gasto.GASTO_DEPRECIACION){
            modificado.setBase(Double.parseDouble(depreciacionInicial.getText()));
            modificado.setTasaIncremento(Double.parseDouble(depreciacionPorcentaje.getText())/100);
            modificado.setCantidadPeriodos(Integer.parseInt(depreciacionPeriodos.getText()));
        }
        this.dispose();
    }//GEN-LAST:event_aceptarManualActionPerformed

    private void variableCambiarPorcentajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_variableCambiarPorcentajeActionPerformed
        // TODO add your handling code here:
        EstimacionPorcentual epc = new EstimacionPorcentual(null, true);
        ModeloPorcentual mp = epc.modificarPorcentaje(modificado.getModeloPorcentual(),"Gasto: "+modificado.getNombreGasto());
        if (mp!=null){
            modificado.setCantidadPeriodos(Integer.parseInt(variablePeriodos.getText()));
            modificado.setModeloPorcentual(mp);
            modificado.setFactura(factura.isSelected());
            modificado.setEscudoFiscal(escudoFiscal.isSelected());
            if (ingresos.isSelected())
                modificado.setTipoGasto(Gasto.GASTO_SEGUN_INGRESOS);
            else
                modificado.setTipoGasto(Gasto.GASTO_SEGUN_COSTOS);
            this.dispose();
        }
        
    }//GEN-LAST:event_variableCambiarPorcentajeActionPerformed

    private void variableCambiarAModeloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_variableCambiarAModeloActionPerformed
        // TODO add your handling code here:
        ModeloRegresion ventana = new ModeloRegresion(null, true);
        Modelo modeloR = ventana.determinarModeloOptimo("Gasto: "+modificado.getNombreGasto());
        if (modeloR!=null){
            modificado.setCantidadPeriodos(Integer.parseInt(variablePeriodos.getText()));
            modificado.setFactura(factura.isSelected());
            modificado.setEscudoFiscal(escudoFiscal.isSelected());
            modificado.setModeloGastos(modeloR);
            modificado.setTipoGasto(Gasto.GASTO_SEGUN_MODELO);
            this.dispose();
        }
        
    }//GEN-LAST:event_variableCambiarAModeloActionPerformed

    private void modeloCambiarModeloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modeloCambiarModeloActionPerformed
        // TODO add your handling code here:
        ModeloRegresion ventana = new ModeloRegresion(null, true);
        Modelo modeloR = ventana.modificarModelo(modificado.getModeloGastos(),"Gasto: "+modificado.getNombreGasto());
        if (modeloR!=null){
            modificado.setModeloGastos(modeloR);
            modificado.setCantidadPeriodos(Integer.parseInt(modeloPeriodos.getText()));
            modificado.setFactura(factura.isSelected());
            modificado.setEscudoFiscal(escudoFiscal.isSelected());
            modificado.setTipoGasto(Gasto.GASTO_SEGUN_MODELO);
            this.dispose();
        }
        
    }//GEN-LAST:event_modeloCambiarModeloActionPerformed

    private void modeloCambiarAPorcentajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modeloCambiarAPorcentajeActionPerformed
        // TODO add your handling code here:
        EstimacionPorcentual epc = new EstimacionPorcentual(null, true);
        ModeloPorcentual mp = epc.estimarPorcentaje("Gasto: "+modificado.getNombreGasto());
        if (mp!=null){
            modificado.setModeloPorcentual(mp);
            modificado.setFactura(factura.isSelected());
            modificado.setEscudoFiscal(escudoFiscal.isSelected());
            modificado.setCantidadPeriodos(Integer.parseInt(modeloPeriodos.getText()));
            SeleccionVariable sv = new SeleccionVariable(null, true);
            if (sv.porcentualAIngresos())
                modificado.setTipoGasto(Gasto.GASTO_SEGUN_INGRESOS);
            else
                modificado.setTipoGasto(Gasto.GASTO_SEGUN_COSTOS);
            this.dispose();
        }
        
    }//GEN-LAST:event_modeloCambiarAPorcentajeActionPerformed

    private void botonCambiarPorcentajesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCambiarPorcentajesActionPerformed
        // TODO add your handling code here:
        modificado.setFactura(factura.isSelected());
        modificado.setEscudoFiscal(escudoFiscal.isSelected());
        this.editarPorcentajesManualmente();
    }//GEN-LAST:event_botonCambiarPorcentajesActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ModificarModeloGastos dialog = new ModificarModeloGastos(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton aceptarManual;
    private javax.swing.JButton botonCambiarPorcentajes;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JButton cambiarTipoGasto3;
    private javax.swing.JRadioButton costos;
    private javax.swing.JTextField depreciacionInicial;
    private javax.swing.JTextField depreciacionPeriodos;
    private javax.swing.JTextField depreciacionPorcentaje;
    private javax.swing.JButton editar;
    private javax.swing.JTextField escalonadoAnioBase;
    private javax.swing.JTextField escalonadoBase;
    private javax.swing.JTextField escalonadoPeriodos;
    private javax.swing.JTextField escalonadoTasaIncremento;
    private javax.swing.JRadioButton escudoFiscal;
    private javax.swing.JRadioButton factura;
    private javax.swing.JTextField fijoCuota;
    private javax.swing.JTextField fijoPeriodos;
    private javax.swing.JRadioButton ingresos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton modeloCambiarAPorcentaje;
    private javax.swing.JButton modeloCambiarModelo;
    private javax.swing.JTextField modeloPeriodos;
    private javax.swing.JRadioButton noEscudoFiscal;
    private javax.swing.JTextField nombre;
    private javax.swing.JTabbedPane panel;
    private javax.swing.JPanel panelDatos;
    private javax.swing.JPanel panelEscudoFiscal;
    private javax.swing.JPanel panelGastoDepreciacion;
    private javax.swing.JPanel panelGastoEscalonado;
    private javax.swing.JPanel panelGastoFijo;
    private javax.swing.JPanel panelGastoModelo;
    private javax.swing.JPanel panelGastoVariable;
    private javax.swing.JPanel panelPorcentajesManuales;
    private javax.swing.JTextField porcentajeAnterior;
    private javax.swing.JRadioButton sinFactura;
    private javax.swing.JTable tablaDatos;
    private javax.swing.JButton variableCambiarAModelo;
    private javax.swing.JButton variableCambiarPorcentaje;
    private javax.swing.JTextField variablePeriodos;
    // End of variables declaration//GEN-END:variables

    private void gastoEscalonado(){
        ModuloGastosEscalonados ventanaGasto = new ModuloGastosEscalonados(null, true);
        Gasto temporal = ventanaGasto.obtenerModeloGasto(this.escenarioNormal.getNumeroPeriodos(),modificado.getNombreGasto());
        if (temporal!=null){
            temporal.setPadre(escenarioNormal);
            temporal.calcularGastos();
            temporal.setNombreGasto(modificado.getNombreGasto());
            this.escenarioNormal.removerGasto(modificado);
            this.escenarioNormal.insertarGasto(temporal);
            //this.insertarFilaTablaPrincipal(temporal.getNombre(), temporal.getFactura() ,temporal.getListaGastos());
        }
    }
    
    private void gastoCuotaFija(){
        ModuloGastosCoutaFija ventanaGasto = new ModuloGastosCoutaFija(null, true);
        Gasto temporal = ventanaGasto.obtenerModeloGasto(this.escenarioNormal.getNumeroPeriodos(),modificado.getNombreGasto());
        if (temporal!=null){
            temporal.setPadre(escenarioNormal);
            temporal.calcularGastos();
            //this.insertarFilaTablaPrincipal(temporal.getNombre(), temporal.getFactura() ,temporal.getListaGastos());
            temporal.setNombreGasto(modificado.getNombreGasto());
            this.escenarioNormal.removerGasto(modificado);
            this.escenarioNormal.insertarGasto(temporal);
        }
    }
    
    private void gastoSegunVariable(){
        ModuloGastosSegunVariable ventanaGasto = new ModuloGastosSegunVariable(null, true);
        Gasto temporal;
        if (modificado.getTipoGasto()==Gasto.GASTO_SEGUN_INGRESOS)
            temporal = ventanaGasto.modificarModeloGasto(this.escenarioNormal.getNumeroPeriodos(),modificado.getNombreGasto(),true,modificado.getFactura(),modificado.getEscudoFiscal());
        else
            temporal = ventanaGasto.modificarModeloGasto(this.escenarioNormal.getNumeroPeriodos(),modificado.getNombreGasto(),false,modificado.getFactura(),modificado.getEscudoFiscal());
        if (temporal!=null){
            temporal.setPadre(escenarioNormal);
            temporal.calcularGastos();
            temporal.setNombreGasto(modificado.getNombreGasto());
            this.escenarioNormal.removerGasto(modificado);
            this.escenarioNormal.insertarGasto(temporal);
            //this.insertarFilaTablaPrincipal(temporal.getNombre(), temporal.getFactura(), temporal.getListaGastos());
        }
    }

    private void gastosDepreciaciones() {
        ModuloGastosPorcentuales ventanaGasto = new ModuloGastosPorcentuales(null, true);
        Gasto temporal = ventanaGasto.obtenerModeloGasto(this.escenarioNormal.getNumeroPeriodos(),modificado.getNombreGasto());
        if (temporal!=null){
            temporal.setPadre(escenarioNormal);

            temporal.calcularGastos();

            //this.insertarFilaTablaPrincipal(temporal.getNombre(),temporal.getFactura(), temporal.getListaGastos());
            temporal.setNombreGasto(modificado.getNombreGasto());
            this.escenarioNormal.removerGasto(modificado);
            this.escenarioNormal.insertarGasto(temporal);
        }
    }

    private void gastosModeloPronostico() {
        ModuloGastosPorModelo ventanaGasto = new ModuloGastosPorModelo(null, true);
        Gasto temporal = ventanaGasto.obtenerModeloGasto(this.escenarioNormal.getNumeroPeriodos(),modificado.getNombreGasto());
        if (temporal!=null){
            temporal.setPadre(escenarioNormal);

            ModeloRegresion ventanaRegresion = new ModeloRegresion(null, true);
            Modelo optimo = ventanaRegresion.determinarModeloOptimo("Gasto: "+modificado.getNombreGasto());

            temporal.setModeloGastos(optimo);
            temporal.calcularGastos();

            //this.insertarFilaTablaPrincipal(temporal.getNombre(),temporal.getFactura(), temporal.getListaGastos());
            temporal.setNombreGasto(modificado.getNombreGasto());
            this.escenarioNormal.removerGasto(modificado);
            this.escenarioNormal.insertarGasto(temporal);
        }
    }
    
    private void gastosManuales() {
        ModuloGastosSegunVariable ventanaGasto = new ModuloGastosSegunVariable(null, true);
        Gasto temporal;
        
        if (modificado.getTipoGasto()==Gasto.GASTO_SEGUN_INGRESOS)
            temporal = ventanaGasto.modificarModeloGastoManual(this.escenarioNormal.getNumeroPeriodos(),modificado.getNombreGasto(),true,modificado.getFactura(),modificado.getEscudoFiscal());
        else
            temporal = ventanaGasto.modificarModeloGastoManual(this.escenarioNormal.getNumeroPeriodos(),modificado.getNombreGasto(),false,modificado.getFactura(),modificado.getEscudoFiscal());
        
        if (temporal!=null){
            Manual m = new Manual(null, true);
            double [] datos = m.insertarDatosManuales(this.escenarioNormal.getListaAnios());
            if (datos!=null){
                temporal.setGastosManualmente(datos);
                temporal.setPadre(escenarioNormal);
                temporal.setNombreGasto(modificado.getNombreGasto());
                this.escenarioNormal.removerGasto(modificado);
                this.escenarioNormal.insertarGasto(temporal);
            }
        }
    }
    
    private void editarPorcentajesManualmente(){
        Manual m = new Manual(null, true);
        double [] lista = m.editarPorcentajesManuales(this.escenarioNormal.getListaAnios(),modificado.getModeloPorcentual().getPorcentajesManuales());
        if (lista!=null){
            this.modificado.getModeloPorcentual().setPorcentajesManuales(lista);
            this.dispose();
        }
    }
}
