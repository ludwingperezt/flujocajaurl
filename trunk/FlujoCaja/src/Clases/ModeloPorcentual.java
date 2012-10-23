package Clases;

import java.text.NumberFormat;


public class ModeloPorcentual {

    private double[] x;

    private double[] y;

    private double[] listaPorcentajes;

    private double promedio;

    private boolean ingresoManual; //identifica si el porcentaje que tiene el objeto fue calculado o se estableció de forma arbitraria de manera manual

    public ModeloPorcentual () {
    }
    
    public ModeloPorcentual(ModeloPorcentual base){
        if (base.x!=null)
            this.x = base.x.clone();
        if (base.y!=null)
            this.y = base.y.clone();
        if (base.listaPorcentajes!=null)
            this.listaPorcentajes = base.listaPorcentajes.clone();
        this.promedio = base.promedio;
        this.ingresoManual = base.ingresoManual;
    }

    public boolean getIngresoManual () {
        return ingresoManual;
    }

    public void setIngresoManual (boolean val) {
        this.ingresoManual = val;
    }

    public double[] getListaPorcentajes () {
        return listaPorcentajes;
    }

    public void setListaPorcentajes (double[] val) {
        this.listaPorcentajes = val;
    }

    public double getPromedio () {
        return promedio;
    }

    /**
     * Ésta función se debe utilizar para establecer de manera manual el porcentaje.
     * Deberá redondearse la cifra a dos decimales antes de utizar ésta función.
     * En caso de ser un número que represente el porcentaje (por ejemplo 89), antes de utilizar esta función se deberá dividir dicho número dentro de 100
     * Si se utiliza ésta función para establecer un porcentaje manual, se establecerá la propiedad ingresoManual = true
     * @param val 
     */
    public void setPromedio (double val) {
        this.promedio = val;
        this.ingresoManual = true;
    }

    public double[] getX () {
        return x;
    }

    public void setX (double[] val) {
        this.x = val;
    }

    public double[] getY () {
        return y;
    }

    public void setY (double[] val) {
        this.y = val;
    }
    /**
     * Función para estimar valores según el porcentaje establecido y la base que se envía como parámetro
     * @param valores
     * @return 
     */
    public double[] estimarValores(double [] valores){
        double [] estimados = new double[valores.length];
        for (int i=0; i<valores.length; i++){
            estimados[i]=valores[i]*this.promedio;
        }        
        return estimados;
    }
    /**
     * Se calcula el porcentaje promedio según los datos históricos 
     * (solo funciona si no es ingreso manual, es decir si existen datos historicos)
     * Al utilizar ésta función se establece la propiedad ingresoManual = false
     * Deberán estar seteados los datos históricos (X y Y)
     * @return 
     */
    public double calcularPorcentaje(boolean redondear)
    {
        double suma = 0;
        double n = (double)x.length;
        this.listaPorcentajes = new double[x.length];
        
        for (int i=0; i<x.length; i++)
        {
            if (redondear)
                this.listaPorcentajes[i]=ModeloPorcentual.redondearCifra(y[i]/x[i]);
            else
                this.listaPorcentajes[i]=y[i]/x[i];
            suma += this.listaPorcentajes[i] ;
        }
        if (redondear)
            this.promedio = ModeloPorcentual.redondearCifra(suma/n);
        else
            this.promedio = suma/n;
        this.ingresoManual = false;
        return this.promedio;
    }
    /**
     * Función que redondea cifras a dos posiciones decimales
     * @param val
     * @return 
     */
    public static double redondearCifra(double val){
        NumberFormat mf = NumberFormat.getInstance();
        mf.setMaximumFractionDigits(2);
        String resultado = mf.format(val);
        resultado = resultado.replaceAll(",", "");
        return Double.parseDouble(resultado);
    }
    /**
     * Función que formatea una cadena que representa un porcentaje a un double de porcentaje (entre 0 y 1) redondeado a 2 cifras decimales.
     * La cadena puede o no tener el simbolo %
     * La cadena deberá estar en representación natural (entre 0 y 100) porque será dividida dentro de 100
     * @param val
     * @return 
     */
    public static double formatearPorcentaje(String val, boolean redondear){
        String cadena = val.replaceFirst("%", "");
        cadena = val.replaceAll(",", "");
        double ret = Double.parseDouble(cadena);
        ret = ret/100;        
        if (redondear)
            return ModeloPorcentual.redondearCifra(ret);
        else
            return ret;
    }

}

