package noUtilizadas;

import Clases.Modelo;
import controlador.Escenario;
import java.text.NumberFormat;


public class ModeloCosto {

    private String nombreX;

    private String nombreY;

    private double promedio;

    private Modelo modeloPronosticacion = null;

    private double [] costosValorCentral;

    private double [] costosLimiteInferior;

    private double [] costosLimiteSuperior;

    private double [] costosHistoricos;

    private int tipoDatoPronosticado;

    private boolean factura;


    public ModeloCosto () {
    }

    public String getNombreX () {
        return nombreX;
    }

    public void setNombreX (String val) {
        this.nombreX = val;
    }

    public String getNombreY () {
        return nombreY;
    }

    public void setNombreY (String val) {
        this.nombreY = val;
    }

    public double getPromedio () {
        return promedio;
    }

    public void setPromedio (double val) {
        this.promedio = val;
        this.modeloPronosticacion = null;
    }

    public double calcularPromedioPorcentajeCosto(double [] x, double [] y)
    {
        double suma = 0;
        double n = (double)x.length;

        for (int i=0; i<x.length; i++)
        {
            suma += y[i]/x[i];
        }
        this.promedio = suma/n;
        return this.promedio;
    }

    public double [] obtenerListaPorcentajesCosto(double [] x, double [] y)
    {
        double [] valores = new double[x.length];
        double n = (double)x.length;

        for (int i=0; i<x.length; i++)
        {
            valores[i]= y[i]/x[i];
        }
        return valores;
    }

    public double getPromedioRedondeado(){
        NumberFormat mf = NumberFormat.getInstance();
        mf.setMaximumFractionDigits(2);
        String resultado = mf.format(promedio);
        return Double.parseDouble(resultado);
    }

    public Modelo getModeloPronosticacion(){
        return this.modeloPronosticacion;
    }

    public void setModeloPronosticacion(Modelo val){
        this.modeloPronosticacion = val;
    }

    public void setDatosHistcos(double [] val){
        this.costosHistoricos = val;
    }

    public double [] getDatosHistoricos(){
        return this.costosHistoricos;
    }

    public void setCostosValorCentral(double [] val){
        this.costosValorCentral = val;
    }

    public double [] getCostosValorCentral(){
        return this.costosValorCentral;
    }

    public void setCostosLimiteInferior(double [] val){
        this.costosLimiteInferior = val;
    }

    public double [] getCostosLimiteInferior(){
        return this.costosLimiteInferior;
    }

    public void setCostosLimiteSuperior(double [] val){
        this.costosLimiteSuperior = val;
    }

    public double [] getCostosLimiteSuperior(){
        return this.costosLimiteSuperior;
    }

    public boolean getFactura(){
        return this.factura;
    }

    public void setFactura(boolean val){
        this.factura = val;
    }

    public void setTipoEstimacion(int tipo){
        this.tipoDatoPronosticado = tipo;
    }

    public int getTipoEstimacion(){
        return this.tipoDatoPronosticado;
    }

    public void pronosticarCostos(Escenario escenario){
        if (this.modeloPronosticacion!=null){
            double [] x = this.modeloPronosticacion.obtenerSerieValoresFuturosX(escenario.getNumeroPeriodos());
            this.costosValorCentral = this.modeloPronosticacion.estimarSerieValoresFuturosCalculados(x);
            this.costosLimiteInferior = this.modeloPronosticacion.estimarSerieLimitesInferiores(this.costosValorCentral);
            this.costosLimiteSuperior = this.modeloPronosticacion.estimarSerieLimitesSuperiores(this.costosValorCentral);
        }
        else{
            this.costosValorCentral = new double[escenario.getNumeroPeriodos()];
            for (int i=0; i<escenario.getNumeroPeriodos(); i++){
                //this.costosValorCentral[i] = escenario.getListaIngresos()[i]*this.getPromedioRedondeado();
            }
        }
    }

    public double [] getCostosPronosticados(Escenario escenario){
        this.pronosticarCostos(escenario);
        double [] ret = null;
        if (tipoDatoPronosticado==Modelo.LIMITE_INFERIOR){
            ret = this.costosLimiteInferior;
        }
        else if ((tipoDatoPronosticado==Modelo.VALOR_CENTRAL)||(tipoDatoPronosticado==Modelo.COSTO_PORCENTUAL)){
            ret = this.costosValorCentral;
        }
        else if (tipoDatoPronosticado==Modelo.LIMITE_SUPERIOR){
            ret = this.costosLimiteSuperior;
        }
        return ret;
    }
}

