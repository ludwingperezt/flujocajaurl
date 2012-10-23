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
public class IVA {

    private Escenario padre;
    private double [] IVACredito;
    private double [] IVADebito;
    private double [] IVAPorPagar;
    private double [] IVAPorCobrar;
    private double [] egresos;
    private double [] diferenciaIVADebitoIVACredito;
    
    
    //private int regimen;
    //private boolean cantidadesNetas;
    
    private static double TASA_IMPOSITIVA = 0.12;
    public static double AJUSTE = 1.12;
    public static int PEQUENIO_CONTRIBUYENTE = 0;
    public static int REGIMEN_SIMPLIFICADO = 1;
    public static int CONTRIBUYENTE_NORMAL = 2;

    public IVA()
    {
    }

    public IVA(IVA base){
        this.IVACredito = base.IVACredito.clone();
        this.IVADebito = base.IVADebito.clone();
        this.IVAPorCobrar = base.IVAPorCobrar.clone();
        this.IVAPorPagar = base.IVAPorPagar.clone();
        this.egresos = base.egresos.clone();
        this.diferenciaIVADebitoIVACredito = base.diferenciaIVADebitoIVACredito.clone();
    }
    
    public void setEgresos(double [] egresos){
        this.egresos = egresos;
    }
    
    public double [] getEgresos(){
        return this.egresos;
    }
            
    public double[] getIVACredito()
    {
        return this.IVACredito;
    }
    public double[] getIVADebito()
    {
        return this.IVADebito;
    }
    public double[] getIVAPorCobrar()
    {
        return this.IVAPorCobrar;
    }
    public double[] getIVAPorPagar()
    {
        return this.IVAPorPagar;
    }

    public double[] getDiferenciaIVADebitoIVACredito(){
        return this.diferenciaIVADebitoIVACredito;
    }
    
    public void calcularIVA(double [] ingresos, double [] egresos)
    {
        this.egresos = egresos;
        this.calcularIVACredito(egresos, this.padre.getDatosNetos());
        this.calcularIVADebito(ingresos, this.padre.getDatosNetos());
        this.ajustarIVA();
    }

    private void calcularIVADebito(double [] ingresos, boolean cantidadesNetas)
    {
        this.IVADebito = new double[ingresos.length];
        for (int i=0; i<ingresos.length; i++)
        {
            if (cantidadesNetas)
                IVADebito[i] = ingresos[i]*IVA.TASA_IMPOSITIVA;
            else
                IVADebito[i] = (ingresos[i]/IVA.AJUSTE)*IVA.TASA_IMPOSITIVA;
        }
    }
    private void calcularIVACredito(double [] egresos, boolean cantidadesNetas)
    {
        this.IVACredito = new double[egresos.length];
        for (int i=0; i<egresos.length; i++)
        {
            if (cantidadesNetas)
                IVACredito[i] = egresos[i]*IVA.TASA_IMPOSITIVA;
            else
                IVACredito[i] = (egresos[i]/IVA.AJUSTE)*IVA.TASA_IMPOSITIVA;
        }
    }
    private void ajustarIVA(){
        inicializar();        
        for (int i=0; i<IVADebito.length; i++){
            if (IVADebito[i]>IVACredito[i]){ //si hay iva por pagar positivo
                diferenciaIVADebitoIVACredito[i] = IVADebito[i] - IVACredito[i];
                if (i==0){ //si es el primer periodo, se deja el iva por pagar tal como está
                    IVAPorPagar[i] = diferenciaIVADebitoIVACredito[i];
                }
                else{ //si no es el primer periodo
                    double temporal = diferenciaIVADebitoIVACredito[i] + IVAPorCobrar[i-1]; //se suma al iva por pagar, el iva por cobrar (iva a favor) que esté acumulado
                    if (temporal>0){ //si la suma del iva a favor y del iva por pagar es positva (o sea, es mayor el iva por pagar que el iva por cobrar acumulado)
                        IVAPorPagar[i] = temporal; //el iva por pagar se deja como fue calculado y se pone el acumulado de iva por cobrar a 0
                        IVAPorCobrar[i] = 0;
                    }
                    else{ //si la suma del iva por pagar y el iva por cobrar (iva a favor) es negativa o es cero
                        IVAPorCobrar[i] = IVAPorCobrar[i-1]+temporal; //se suma al IVA por cobrar anterior, el nuevo IVA por cobrar y ese es el acumulado; el iva por pagar se establece a cero.
                        IVAPorPagar[i] = 0;
                    }
                }
            }
            else{
                if (i==0) //si es el primer periodo se deja el iva por cobrar así como sale la resta, de lo contrario se acumula
                    IVAPorCobrar[i] = IVADebito[i] - IVACredito[i];
                else
                    IVAPorCobrar[i] = IVADebito[i] - IVACredito[i] + IVAPorCobrar[i-1];

                IVAPorPagar[i] = 0;
            }
        }
    }
    
    private void inicializar()
    {
        this.IVAPorCobrar = new double[IVACredito.length];
        this.IVAPorPagar = new double[IVACredito.length];
        this.diferenciaIVADebitoIVACredito = new double[IVACredito.length];
    }

    public void setPadre(Escenario modelo) {
        this.padre = modelo;
    }
    
    public Escenario getPadre(){
        return this.padre;
    }
    
    
    public void standalone(double [] ingresos, double [] egresos, boolean datosNetos)
    {
        this.egresos = egresos;
        this.calcularIVACredito(egresos, datosNetos);
        this.calcularIVADebito(ingresos, datosNetos);
        this.ajustarIVA();
    }

}
