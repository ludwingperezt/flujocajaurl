/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;


/**
 *
 * @author HP G42
 */

public class EscenarioModificado extends Escenario implements Cloneable {
    
    public static int ESCENARIO_NORMAL =0;
    public static int ESCENARIO_PESIMISTA=1;
    public static int ESCENARIO_OPTIMISTA=2;
    
    private double tasaIncrementoIngresos;
    private double tasaIncrementoCostos;
    private double tasaDisminucionIngresos;
    private double tasaDisminucionCostos;
    private int tipoEscenario;
    private double probabilidad;
    private double tirPonderada;

    public double getTirPonderada() {
        return tirPonderada;
    }

    public void setTirPonderada(double tirPonderada) {
        this.tirPonderada = tirPonderada;
    }

    public double getVanPonderada() {
        return vanPonderada;
    }

    public void setVanPonderada(double vanPonderada) {
        this.vanPonderada = vanPonderada;
    }
    private double vanPonderada;

    
    
    public EscenarioModificado(Escenario base){
        super(base);
        this.crearGastosEInversiones(base);
    }
    
    public double getProbabilidad() {
        return probabilidad;
    }

    public void setProbabilidad(double probabilidad) {
        this.probabilidad = probabilidad;
    }
    
    public EscenarioModificado(EscenarioModificado base){
        super((Escenario)base);
        this.crearGastosEInversiones((Escenario)base);
        
        this.tasaDisminucionCostos = base.tasaDisminucionCostos;
        this.tasaDisminucionIngresos = base.tasaDisminucionIngresos;
        this.tasaIncrementoCostos = base.tasaIncrementoCostos;
        this.tasaIncrementoIngresos = base.tasaIncrementoIngresos;
        this.tipoEscenario = base.tipoEscenario;
    }
    
    public void setTasaIncrementoIngresos(double tasa){
        this.tasaIncrementoIngresos = tasa;
    }
    public double getTasaIncrementoIngresos(){
        return this.tasaIncrementoIngresos;
    }
    public void setTasaIncrementoCostos(double tasa){
        this.tasaIncrementoCostos = tasa;
    }
    public double getTasaIncrementoCostos(){
        return this.tasaIncrementoCostos;
    }
    
    public void setTasaDisminucionIngresos(double tasa){
        this.tasaDisminucionIngresos = tasa;
    }
    public double getTasaDisminucionIngresos(){
        return this.tasaDisminucionIngresos;
    }
    
    public void setTasaDisminucionCostos(double tasa){
        this.tasaDisminucionCostos = tasa;
    }
    public double getTasaDisminucionCostos(){
        return this.tasaDisminucionCostos;
    }
    public void setTipoEscenario(int tipo){
        this.tipoEscenario = tipo;
    }
    public int tipoEscenario(){
        return this.tipoEscenario;
    }

    public void plantearEscenario() {
        if (this.tipoEscenario!=ESCENARIO_NORMAL){
            double [] ingresos = this.modeloIngresos.getIngresosActuales();
            double [] costos = this.modeloCostos.getCostosActuales();
            if (this.tipoEscenario==EscenarioModificado.ESCENARIO_OPTIMISTA){
                for (int i=0; i<ingresos.length; i++){
                    ingresos[i] = ingresos[i] + (ingresos[i] * this.tasaIncrementoIngresos);
                    costos[i] = costos[i] - (costos[i]*this.tasaDisminucionCostos);
                }

            }
            else if (this.tipoEscenario==EscenarioModificado.ESCENARIO_PESIMISTA){

                for (int i=0; i<ingresos.length; i++){
                    ingresos[i] = ingresos[i] - (ingresos[i] * this.tasaDisminucionIngresos);
                    costos[i] = costos[i] + (costos[i]*this.tasaIncrementoCostos);
                }
            }
            this.modeloIngresos.setListaIngresosManualmente(ingresos);
            this.modeloCostos.setCostos(costos);
            this.recalcularTodo();
        }
        this.tirPonderada = (this.getTIR()) * this.probabilidad;
        this.vanPonderada = this.getSumatoriaVAN() * this.probabilidad;        
    }
}
