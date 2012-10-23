package Clases;

import controlador.Escenario;


public class ISO {

    public static int ISO_ACREDITABLE_ISR = 0;
    public static int ISR_ACREDITABLE_ISO = 1;
    public static double LIMITE = 0.04;
    public static double TASA_IMPOSITIVA = 0.01;
    
    private int regimen;

    private double[] porcentajeUtilidadBruta;

    private double[] isoIngresos;

    private double[] isoActivos;

    private double[] isoPorPagarTemporal;

    private double[] isoPorPagarDefinitivo;

    private double[] isoAcumulado;

    private Escenario padre;

    public ISO () {
    }
    /**
     * Constructor copia
     * @param base 
     */
    public ISO(ISO base){
        this.regimen = base.regimen;
        this.porcentajeUtilidadBruta = base.porcentajeUtilidadBruta.clone();
        this.isoIngresos = base.isoIngresos.clone();
        this.isoActivos = base.isoActivos.clone();
        this.isoPorPagarTemporal = base.isoPorPagarTemporal.clone();
        this.isoPorPagarDefinitivo = base.isoPorPagarDefinitivo.clone();
        this.isoAcumulado = base.isoAcumulado.clone();
    }

    public double[] getIsoActivos () {
        return isoActivos;
    }

    public void setIsoActivos (double[] val) {
        this.isoActivos = val;
    }

    public double[] getIsoAcumulado () {
        return isoAcumulado;
    }

    public void setIsoAcumulado (double[] val) {
        this.isoAcumulado = val;
    }

    public double[] getIsoIngresos () {
        return isoIngresos;
    }

    public void setIsoIngresos (double[] val) {
        this.isoIngresos = val;
    }

    public double[] getIsoPorPagarDefinitivo () {
        return isoPorPagarDefinitivo;
    }

    public void setIsoPorPagarDefinitivo (double[] val) {
        this.isoPorPagarDefinitivo = val;
    }

    public double[] getIsoPorPagarTemporal () {
        return isoPorPagarTemporal;
    }

    public void setIsoPorPagarTemporal (double[] val) {
        this.isoPorPagarTemporal = val;
    }

    public Escenario getPadre () {
        return padre;
    }

    public void setPadre (Escenario val) {
        this.padre = val;
    }

    public double[] getPorcentajeUtilidadBruta () {
        return porcentajeUtilidadBruta;
    }

    public void setPorcentajeUtilidadBruta (double[] val) {
        this.porcentajeUtilidadBruta = val;
    }

    public int getRegimen () {
        return regimen;
    }

    public void setRegimen (int val) {
        this.regimen = val;
    }
    
    public int getAcreditacion () {
        return regimen;
    }

    public void setAcreditacion (int val) {
        this.regimen = val;
    }
    
    /**
     * Función principal para calcular el ISO.
     * Para utilizarla, éste objeto (ISO) deberá tener establecidos ya, el Escenario (padre), 
     * calculado el ISR y la utilidad bruta
     */
    public void calcularISO(){
        if (this.padre.getPatenteDeComercio()){
            double [] ingresos = this.padre.ingresosActuales();
            double [] ub = this.padre.getUtilidadBruta();
            this.isoActivos = new double[this.padre.getNumeroPeriodos()];
            this.isoIngresos = new double[this.padre.getNumeroPeriodos()];
            this.isoAcumulado = new double[this.padre.getNumeroPeriodos()];
            this.isoPorPagarDefinitivo = new double[this.padre.getNumeroPeriodos()];
            this.isoPorPagarTemporal = new double[this.padre.getNumeroPeriodos()];
            
            this.porcentajeUtilidadBruta = new double[ub.length];

            for (int i=0; i<ub.length;i++){
                this.porcentajeUtilidadBruta[i] = ub[i]/ingresos[i];
                calcularCantidadesISO(this.porcentajeUtilidadBruta[i],i);
            }
            this.acreditar();
        }
        else{
            this.isoPorPagarDefinitivo = new double[this.padre.getNumeroPeriodos()];
            this.isoPorPagarTemporal = new double[this.padre.getNumeroPeriodos()];
            //ISR definitivo = ISR temporal
            porcentajeUtilidadBruta = new double[this.padre.getNumeroPeriodos()];
            isoIngresos = new double[this.padre.getNumeroPeriodos()];
            isoActivos = new double[this.padre.getNumeroPeriodos()];
            isoAcumulado = new double[this.padre.getNumeroPeriodos()];
            
            this.padre.getModeloISR().setISRporPagar(this.padre.getModeloISR().getISRPorPagarTemporal());
        }
        
    }
    private void calcularCantidadesISO(double utilidadBruta,int i){
        ISR modelo = this.padre.getModeloISR();
        if ((utilidadBruta>=ISO.LIMITE)&&(modelo.getRegimen()[i]==ISR.REGIMEN_OPTATIVO)){ //si la utilidad bruta es 4% o mayor o si está en el régimen del 31%
            
            if ((!this.padre.getEmpresaNueva()) && (i==0)){
                this.isoActivos[i]=this.padre.getActivosAnteriores() * ISO.TASA_IMPOSITIVA;
                double [] historicos = this.padre.getModeloIngresos().getIngresosHistoricos();
                this.isoIngresos[i]=this.isoDeIngresos(historicos[historicos.length-1]);
            }else if ((this.padre.getEmpresaNueva()) && (i==0)){
                this.isoActivos[i]=0;
                this.isoIngresos[i]=0;
            }else if (i>0){
                this.isoActivos[i]=this.padre.getActivos() * ISO.TASA_IMPOSITIVA;
                double [] ingresos = this.padre.ingresos();
                this.isoIngresos[i]=this.isoDeIngresos(ingresos[i-1]);
            }            
            
            if (this.isoIngresos[i]>=this.isoActivos[i])
                this.isoPorPagarTemporal[i]=this.isoIngresos[i];
            else
                this.isoPorPagarTemporal[i]=this.isoActivos[i];
        }
    }
    
    private double isoDeIngresos(double ingresos){
        double retorno = 0;
        if (this.padre.getDatosNetos())
            retorno = ingresos * ISO.TASA_IMPOSITIVA;
        else
            retorno = (ingresos/IVA.AJUSTE) * ISO.TASA_IMPOSITIVA;
        return retorno;
    }
    
    private void acreditar(){
        double [] isrTemporal = this.padre.getModeloISR().getISRPorPagarTemporal();
        if (this.regimen == ISO.ISO_ACREDITABLE_ISR)
            this.ISOAcreditadoISR(isrTemporal);
        else
            this.ISRAcreditadoISO(isrTemporal);
    }
    
    private void ISOAcreditadoISR(double [] isrTemporal){
        this.isoPorPagarDefinitivo = this.isoPorPagarTemporal;
        double [] isrDefinitivo = new double[isrTemporal.length];
        this.isoAcumulado[0]=this.isoPorPagarTemporal[0];
        isrDefinitivo[0]=isrTemporal[0];
        
        for (int i=1; i<this.isoPorPagarTemporal.length; i++){
            if (isoAcumulado[i-1]>isrTemporal[i]){
                isrDefinitivo[i]=0;
                /////////////////////////////////////////////// ¿Qué hago aquí si es ISR régimen general?
                isoPorPagarDefinitivo[i]=isoPorPagarTemporal[i];
                ///////////////////////////////////////////////
                isoAcumulado[i]=isoAcumulado[i-1]-isrTemporal[i]+isoPorPagarTemporal[i];
            }
            else{
                isrDefinitivo[i]=isrTemporal[i]-isoAcumulado[i-1];
                isoAcumulado[i]=isoPorPagarDefinitivo[i];
            }
        }
        this.padre.getModeloISR().setISRporPagar(isrDefinitivo);
    }
    
    private void ISRAcreditadoISO(double [] isrTemporal){
        for (int i=0; i<this.isoPorPagarTemporal.length;i++){
            if (this.isoPorPagarTemporal[i]>isrTemporal[i])
                this.isoPorPagarDefinitivo[i]=this.isoPorPagarTemporal[i]-isrTemporal[i];
            else
                this.isoPorPagarDefinitivo[i]=0;
        }
        this.padre.getModeloISR().setISRporPagar(isrTemporal);
    }
    
    /*
    private void calcular(double utilidadBruta,int i,double ingresos){
        ISR modelo = this.padre.getModeloISR();
        if ((utilidadBruta>=ISO.LIMITE)&&(modelo.getRegimen()[i]==ISR.REGIMEN_OPTATIVO)){ //si la utilidad bruta es 4% o mayor o si 
            
            this.isoActivos[i]=this.padre.getActivos() * ISO.TASA_IMPOSITIVA;
                        
            
            if ((this.padre.getEmpresaNueva()) && (i==0)){
                double [] historicos = this.padre.getModeloIngresos().getIngresosHistoricos();
                this.isoIngresos[i]=this.isoDeIngresos(historicos[historicos.length-1]);
            }else
                this.isoIngresos[i]=this.isoDeIngresos(ingresos);
            
            
            if (this.isoIngresos[i]>=this.isoActivos[i])
                this.isoPorPagarTemporal[i]=this.isoIngresos[i];
            else
                this.isoPorPagarTemporal[i]=this.isoActivos[i];
        }
    }
    */

}

