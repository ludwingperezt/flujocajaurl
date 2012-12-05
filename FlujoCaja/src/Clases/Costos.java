/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Clases;

import controlador.Escenario;


/**
 *
 * @author ludwing
 */

public class Costos {

    public static int COSTO_PORCENTUAL = 0;
    public static int COSTO_MODELO = 1;
    public static int COSTO_MANUAL = 2;
    public static int COSTO_PORCENTAJES_MANUALES = 3;
    
    private Escenario padre;

    private Modelo modeloPronosticacion = null; //
    
    private ModeloPorcentual modeloPorcentual = null; //
    
    private double [] costos;
    
    private double [] costosHistoricos;

    private boolean factura;
    
    private int tipoCosto; //

    private double [] listaPorcentajes;

    public Costos () {
    }
    
    public Costos (Costos base){
        this.factura = base.factura;
        this.tipoCosto = base.tipoCosto;
        if (base.costos !=null)
            this.costos = base.costos.clone();
        
        if (base.modeloPorcentual!=null)
            this.modeloPorcentual = new ModeloPorcentual(base.getModeloPorcentual());
        if (base.modeloPronosticacion!=null)
            this.modeloPronosticacion = new Modelo(base.getModeloPronosticacion());
        
        if (base.listaPorcentajes !=null)
            this.listaPorcentajes = base.listaPorcentajes.clone();
        
        if (base.costosHistoricos!=null)
            this.costosHistoricos = base.costosHistoricos.clone();
        
    }
    
    public double[] getCostosHistoricos() {
        return costosHistoricos;
    }

    public void setCostosHistoricos(double[] costosHistoricos) {
        this.costosHistoricos = costosHistoricos;
    }
    
    public void establecerCostosHistoricos(){
        if (this.tipoCosto==Costos.COSTO_PORCENTUAL){
            if (this.modeloPorcentual!=null){
                if (this.modeloPorcentual.getY()!=null){
                    this.costosHistoricos = this.modeloPorcentual.getY().clone();
                }
            }
        }
        else if (this.tipoCosto==Costos.COSTO_MODELO){
            if (this.modeloPronosticacion!=null){
                if (this.modeloPronosticacion.getY()!=null){
                    this.costosHistoricos = this.modeloPronosticacion.getY().clone();
                }
            }
        }
    }

    public void setListaPorcentajes(double[] lista){
        this.tipoCosto = Costos.COSTO_PORCENTAJES_MANUALES;
        this.listaPorcentajes = lista;
    }
    
    public double [] getListaPorcentajes(){
        return this.listaPorcentajes;
    }
    
    public void setModeloPorcentual(ModeloPorcentual val){
        this.tipoCosto = Costos.COSTO_PORCENTUAL;
        this.modeloPorcentual = val;
        this.establecerCostosHistoricos();
    }
    
    public ModeloPorcentual getModeloPorcentual(){
        return this.modeloPorcentual;        
    }
     
    public int getTipoCosto(){
        return this.tipoCosto;
    }
    
    public void setTipoCosto(int tipoCosto){
        this.tipoCosto = tipoCosto;
    }
    
    public void setPadre(Escenario val){
        this.padre = val;
    }

    public Escenario getPadre(){
        return this.padre;
    }

    public double getPromedio () {
        return this.modeloPorcentual.getPromedio();
    }

    public void setPromedio (double val) {
        this.modeloPorcentual.setPromedioManual(val);
        this.setTipoCosto(Costos.COSTO_PORCENTUAL);
    }

    public Modelo getModeloPronosticacion(){
        return this.modeloPronosticacion;
    }

    public void setModeloPronosticacion(Modelo val){
        this.modeloPronosticacion = val;
        this.setTipoCosto(Costos.COSTO_MODELO);
        this.establecerCostosHistoricos();
    }
    /*
    public void setDatosHistcos(double [] val){
        this.modeloPorcentual.setY(val);
    }

    public double [] getDatosHistoricos(){
        return this.modeloPorcentual.getY();
    }
    
    public void setIngresosHistoricos(double [] val){
        this.modeloPorcentual.setX(val);
    }
    
    public double [] getIngresosHistoricos(){
        return this.modeloPorcentual.getX();
    }*/

    /**
     * Solo retorna la lista de costos calculados actuales para el valor central, si no hay datos, retorna null o los datos que ya estaban establecidos.
     * @return 
     */
    public double [] getCostosActuales(){
        return this.costos;
    }

    public boolean getFactura(){
        return this.factura;
    }

    public void setFactura(boolean val){
        this.factura = val;
    }
 
    /**
     * Procedimiento para pronosticar el valor de los costos.
     * Ya deberá tener cargados los modelos que necesite con todos los datos necesarios.
     */
    public void pronosticarCostos(){
        if (this.tipoCosto == Costos.COSTO_MODELO){
            this.costos = this.modeloPronosticacion.estimarValores(this.padre.getNumeroPeriodos());
        }
        else if (this.tipoCosto==Costos.COSTO_PORCENTUAL){
            this.costos = this.modeloPorcentual.estimarValores(this.padre.ingresos());
        }
        else if (this.tipoCosto==Costos.COSTO_PORCENTAJES_MANUALES){
            this.costos = this.calcularCostosPorcentajesManuales(this.padre.ingresos());
        }
    }

    /**
     * Funcion que retorna la lista de costos, si no están definidos, los calcula y devuelve la lista.
     * Hace el calculo de los costos implicitamente.
     * @return 
     */
    public double [] getPronosticarCostos(){
        //if (this.costosValorCentral==null)
        this.pronosticarCostos();
        return this.costos;
    }
    /**
     * Setea una lista de costos establecidos manualmente
     * @param lista 
     */
    public void establecerCostosManualmente(double [] lista){
        this.costos = lista;
        this.setTipoCosto(Costos.COSTO_MANUAL);
    }
    /**
     * Funcion que setea una lista de costos establecida manualmente
     * @param lista 
     */
    public void setCostos(double [] lista){
        this.costos = lista;
        this.setTipoCosto(Costos.COSTO_MANUAL);
    }
    public double [] getCostos(){
        return this.costos;
    }

    private double[] calcularCostosPorcentajesManuales(double[] ingresos) {
        this.costos = new double[ingresos.length];
        for (int i=0; i<ingresos.length; i++){
            this.costos[i] = ingresos[i]*this.listaPorcentajes[i];
        }
        return this.costos;
    }
}
