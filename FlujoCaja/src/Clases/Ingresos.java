package Clases;

import controlador.Escenario;


public class Ingresos {
    
    public static int INGRESO_MODELO_PRONOSTICACION = 0;
    public static int INGRESO_MANUAL = 1;
    
    public int tipoIngreso;

    private Escenario padre;

    private Modelo modeloIngresos;

    private double[] ingresosHistoricos;
    
    private double[] listaIngresos;
    
    //private Modelos listaModelos;

    public Ingresos () {
    }
    
    public Ingresos(Ingresos base){
        this.tipoIngreso = base.tipoIngreso;
        this.modeloIngresos = new Modelo(base.modeloIngresos);
        this.ingresosHistoricos = base.ingresosHistoricos.clone();
        this.listaIngresos = base.listaIngresos.clone();
    }
    
    public void setTipoIngreso(int val){
        this.tipoIngreso = val;
    }
    
    public int getTipoIngreso(){
        return this.tipoIngreso;
    }

//    public void setListaModelos(Modelos lista){
//        this.listaModelos = lista;
//    }
//    
//    public Modelos getListaModelos(){
//        return this.listaModelos;
//    }
    
    public void setPadre(Escenario val){
        this.padre = val;
    }

    public Escenario getPadre(){
        return this.padre;
    }

    public void setIngresosHistoricos (double[] val) {
        this.ingresosHistoricos = val;
    }
    
    public double[] getIngresosHistoricos () {
        return this.ingresosHistoricos;
    }

    public Modelo getModeloIngresos () {
        return modeloIngresos;
    }

    public void setModeloIngresos (Modelo val) {
        this.modeloIngresos = val;
    }
    
    public double [] getIngresosActuales(){
        return this.listaIngresos;
    }

    public double [] getIngresos(){
        if (this.tipoIngreso==Ingresos.INGRESO_MODELO_PRONOSTICACION){
            this.pronosticarIngresos();
        }
        return this.listaIngresos;
    }

    public void pronosticarIngresos(){
        if (this.tipoIngreso==Ingresos.INGRESO_MODELO_PRONOSTICACION)
            this.listaIngresos = this.modeloIngresos.estimarValores(this.padre.getNumeroPeriodos());
    }

    public void setListaIngresos(double[] datos) {
        this.listaIngresos = datos;
    }
    
}

