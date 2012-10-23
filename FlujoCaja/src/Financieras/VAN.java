/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Financieras;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

/**
 * 
 * @author Jose Carlos Palma
 * @version v1.0.0
 */
public class VAN {
    
    /**
     * Almacena la inversion (primer elemento del arreglo) y el listado de 
     * flujos de cajas por cada periodo, en donde el indice del arreglo
     * representa el periodo del flujo.
     */
    private double flujos[];
    
    /**
     * Constante que representa en el arreglo el valor de la INVERSION.
     */
    private static final int INVERSION = 0;

    /**
     *
     * @param tiempo
     */
    public VAN(int tiempo) {
        this.flujos = new double[tiempo + 1];
    }

    /**
     * Establece el valor de la inversion inicial Nota: tambien se puede
     * establecer con el metod setVan(periodo, valor); en donde periodo es igual
     * a cero.
     *
     * @param inversion el valor de la inversion inicial (positivo)
     *
     */
    public void setInversion(double inversion) {
        this.flujos[INVERSION] = abs(inversion);
    }

    /**
     * Devuelve el valor de la inversion.
     *
     * @return un el valor de la inversion.
     */
    public double getInversion() {
        return this.flujos[INVERSION];
    }

    /**
     * Establece el flujo del flujo en un determinado periodo.
     *
     * @param periodo es el numero del periodo. en caso de ser igual a cero, se
     * considera que se esta estableciendo la inversion.
     * @param flujo es el flujo del flujo o la inversion inicial.
     */
    public void setFlujo(int periodo, double flujo) {
        if (periodo < 0 || periodo >= this.flujos.length) {
            throw new IllegalArgumentException("periodo fuera de rango, periodo = [0," + this.flujos.length + "]");
        }
        this.flujos[periodo] = flujo;

    }

    /**
     * Estable un conjunto de flujos, iniciando desde el periodo 1.
     *
     * @param flujos lista de flujos
     */
    public void setFlujos(double... flujos) {
        setFlujos(1, flujos);
    }

    /**
     * Estable un conjunto de flujos, iniciando desde un periodo establecido.
     *
     * @param periodo Es el periodo donde se quiere iniciar.
     * @param flujos lista de flujos
     */
    public void setFlujos(int periodo, double... flujos) {
        if (periodo < 1) {
            periodo = 1;
        }
        for (int i = periodo, j = 0; i < this.flujos.length && j < flujos.length; i++, j++) {
            this.flujos[i] = flujos[j];
        }
    }

    /**
     * Devuelve el flujo para un periodo.
     *
     * @param periodo es el numero del periodo
     * @return el valor del flujo
     */
    public double getFlujo(int periodo) {
        if (periodo < 0 || periodo >= this.flujos.length) {
            throw new IllegalArgumentException("periodo fuera de rango, periodo = [0," + this.flujos.length + "]");
        }
        return this.flujos[periodo];
    }

    /**
     * Calcula Valor Actual Neto (VAN) o Valor Presente Neto (VPN) para una tasa
     * de interes especifica.
     * <PRE>
     * Ejemplos de tasa:
     *   Costo del dinero a largo plazo
     *   Costo del capital
     *   Costo de oportunidad
     *   Tasa de rentabilidad
     * </PRE>
     * @param tasa es la tasa de interes
     * @return VAN
     */
    public double getVan(double tasa) {
        double retVal = -this.flujos[INVERSION];
        //System.out.println("-E0 = " + retVal);
        for (int i = 1; i < this.flujos.length; i++) {
            //System.out.println("VAi = " + (this.flujos[i] / pow(1.0 + tasa, i)));
            retVal += (this.flujos[i] / pow(1.0 + tasa, i));
        }

        return retVal;
    }
    
    /**
     * Calcula la primera derivada del Valor Actual Neto (VAN) o 
     * Valor Presente Neto (VPN) para una tasa de interes especifica.
     * <PRE>
     * Ejemplos de tasa:
     *   Costo del dinero a largo plazo
     *   Costo del capital
     *   Costo de oportunidad
     *   Tasa de rentabilidad
     * </PRE>
     * @param tasa es la tasa de interes
     * @return VAN'
     */
    public double getVanPrima(double tasa){
        double retVal = 0;
        for(int i = 1; i < this.flujos.length; i ++){
            retVal += (-i * this.flujos[i] / pow(1.0 + tasa, i+1.0));
        }
        return retVal;
    }
    
    /**
     * Imprime la formuna del VAN.
     */
    public void print() {
        StringBuilder sb = new StringBuilder("VAN: ");
        sb.append(this.flujos[INVERSION]);
        for (int i = 1; i < this.flujos.length; i++) {
            sb.append(" + (").append(this.flujos[i]).append("/(1+I)^").append(i).append(")");
        }
        System.out.println(sb);
    }
}
