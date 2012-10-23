/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

/**
 *
 * @author HP G42
 */
public class EscenarioModificado extends Escenario {
    
    public static int ESCENARIO_NORMAL =0;
    public static int ESCENARIO_PESIMISTA=1;
    public static int ESCENARIO_OPTIMISTA=2;
    
    private double tasaIncrementoIngresos;
    private double tasaIncrementoCostos;
    private double tasaDisminucionIngresos;
    private double tasaDisminucionCostos;
    private int tipoEscenario;
    
    public EscenarioModificado(Escenario base){
        super(base);
        this.crearGastosEInversiones(base);
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
        if (this.tipoEscenario==EscenarioModificado.ESCENARIO_OPTIMISTA){
            double [] ingresos = this.modeloIngresos.getIngresosActuales();
            double [] costos = this.modeloCostos.getCostosActuales();
            for (int i=0; i<ingresos.length; i++){
                ingresos[i] = ingresos[i] + (ingresos[i] * this.tasaIncrementoIngresos);
                costos[i] = costos[i] - (costos[i]*this.tasaDisminucionCostos);
            }
            this.modeloIngresos.setListaIngresos(ingresos);
            this.modeloCostos.setCostos(costos);
        }
        else if (this.tipoEscenario==EscenarioModificado.ESCENARIO_PESIMISTA){
            double [] ingresos = this.modeloIngresos.getIngresosActuales();
            double [] costos = this.modeloCostos.getCostosActuales();
            for (int i=0; i<ingresos.length; i++){
                ingresos[i] = ingresos[i] - (ingresos[i] * this.tasaDisminucionIngresos);
                costos[i] = costos[i] + (costos[i]*this.tasaIncrementoCostos);
            }
            this.modeloIngresos.setListaIngresos(ingresos);
            this.modeloCostos.setCostos(costos);
        }
        this.recalcularTodo();
    }
    
}
