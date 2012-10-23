package Clases;

import controlador.Escenario;
import java.util.ArrayList;


public class ISR {

    public static double TASA_PERMITIDA = 0.97;

    public static double REGLA_DECISION = 0.16;

    public static double REGIMEN_GENERAL = 0.05;

    public static double REGIMEN_OPTATIVO = 0.31;

    public static double LIMITE = 0.03;

    public static double GASTOS_PERSONALES = 36000;
    
    private Escenario padre;

    private double[] ingresos;

    private double[] regimen;

    private double[] ISRporPagar;

    private double[] egresos;

    private double[] deduciblesSiguientePeriodo;

    private double[] UAI;

    private double[] UAIFiscal;

    private double[] ISRPorPagarTemporal;

    private double[] porcentajeUtilidad;

    private double[] egresosDeducibles97;

    private double escala; //esta escala

    public ISR () {
    }

    public ISR(ISR base){
        if (base.ingresos!=null)
            this.ingresos = base.ingresos.clone();
        if (base.regimen!=null)
            this.regimen = base.regimen.clone();
        if (base.ISRporPagar!=null)
            this.ISRporPagar = base.ISRporPagar.clone();
        if (base.egresos!=null)
            this.egresos = base.egresos.clone();
        if (base.deduciblesSiguientePeriodo!=null)
            this.deduciblesSiguientePeriodo = base.deduciblesSiguientePeriodo.clone();
        if (base.UAI!=null)
            this.UAI = base.UAI.clone();
        if (base.UAIFiscal!=null)
            this.UAIFiscal = base.UAIFiscal.clone();
        if (base.ISRPorPagarTemporal!=null)
            this.ISRPorPagarTemporal = base.ISRPorPagarTemporal.clone();
        if (base.porcentajeUtilidad!=null)
            this.porcentajeUtilidad = base.porcentajeUtilidad.clone();
        if (base.egresosDeducibles97!=null)
            this.egresosDeducibles97 = base.egresosDeducibles97.clone();
        this.escala = base.escala;
    }
    
    public void setPadre(Escenario val){
        this.padre = val;
    }

    public Escenario getPadre(){
        return this.padre;
    }

    public double[] getISRPorPagarTemporal () {
        return ISRPorPagarTemporal;
    }

    public void setISRPorPagarTemporal (double[] val) {
        this.ISRPorPagarTemporal = val;
    }

    public double[] getISRporPagar () {
        return ISRporPagar;
    }

    public void setISRporPagar (double[] val) {
        this.ISRporPagar = val;
    }

    public double[] getUAI () {
        return UAI;
    }

    public void setUAI (double[] val) {
        this.UAI = val;
    }

    public double[] getUAIFiscal () {
        return UAIFiscal;
    }

    public void setUAIFiscal (double[] val) {
        this.UAIFiscal = val;
    }

    public double[] getDeduciblesSiguientePeriodo () {
        return deduciblesSiguientePeriodo;
    }

    public void setDeduciblesSiguientePeriodo (double[] val) {
        this.deduciblesSiguientePeriodo = val;
    }

    public double[] getEgresos () {
        return egresos;
    }
    
    public double[] getIngresos () {
        return ingresos;
    }

    public void setEgresos (double[] val) {
        this.egresos = val;
    }

    public double[] getEgresosDeducibles97 () {
        return egresosDeducibles97;
    }

    public void setEgresosDeducibles97 (double[] val) {
        this.egresosDeducibles97 = val;
    }

    public double[] getPorcentajeUtilidad () {
        return porcentajeUtilidad;
    }

    public void setPorcentajeUtilidad (double[] val) {
        this.porcentajeUtilidad = val;
    }

    public double[] getRegimen () {
        return regimen;
    }

    public void setRegimen (double[] val) {
        this.regimen = val;
    }

    public double getGastosPersonalesEscalados(){
        return ISR.GASTOS_PERSONALES/this.escala;
    }

    /**
     * Función principal para calcular ISR
     */
    public void calcularISR(){
        int periodos = this.padre.getNumeroPeriodos();
        /////////////////////
        this.escala = this.padre.getEscala();
        /////////////////////
        this.ISRPorPagarTemporal=new double[periodos];
        this.ISRporPagar = new double[periodos];
        this.UAI = new double[periodos];
        this.UAIFiscal = new double[periodos];
        this.deduciblesSiguientePeriodo = new double[periodos];
        this.egresosDeducibles97 = new double[periodos];
        this.porcentajeUtilidad = new double[periodos];
        this.regimen = new double[periodos];
        this.ingresos = this.padre.getModeloIngresos().getIngresos();
        
        calcularDeducibles();
        calcularUAI();
        
        for (int i=0; i<periodos; i++){
            if (regimen[i]==ISR.REGIMEN_GENERAL){
                calcularISRRegimenGeneral(i);
            }
            else{
                regimenOptativo(i,this.padre.getDatosNetos());
            }
        }
    }

    private void calcularISRRegimenGeneral(int i){
        this.ISRPorPagarTemporal[i] = ingresos[i] * ISR.REGIMEN_GENERAL;
        /*
        if (cantidadesNetas){
            this.ISRPorPagarTemporal[i] = ingresos[i] * ISR.REGIMEN_GENERAL;
        }
        else{
            //this.ISRPorPagarTemporal[i] = (ingresos[i]/IVA.AJUSTE) * ISR.REGIMEN_GENERAL;
            this.ISRPorPagarTemporal[i] = ingresos[i] * ISR.REGIMEN_GENERAL;
        }*/
    }
    
    private void calcularDeducibles(){
        if (this.padre.getDatosNetos()){ //si las cantidades son netas
            this.egresos = new double[this.padre.getNumeroPeriodos()];
            double [] costos = this.padre.getModeloCostos().getCostos();
            ArrayList<Gasto> listaGastos = this.padre.getListaGastos();
            ArrayList<Intereses> listaIntereses = this.padre.getListaIntereses();
            for (int i=0; i<egresos.length; i++){
                egresos[i] = costos[i];
                for (Gasto g: listaGastos){
                    double [] montoGastos = g.getListaGastos();
                    if (i<montoGastos.length)
                        egresos[i]+=montoGastos[i];
                }
                for (Intereses n:listaIntereses){
                    if (n.getEntidadSupervisada()){
                        double [] monto = n.getListaCuotasAnuales();
                        if (i<monto.length)
                            egresos[i]+=monto[i];
                    }
                }
            }
        }else{//si las cantidades son brutas
            this.egresos = new double[this.padre.getNumeroPeriodos()];
            
            double [] costosSinFactura = new double[this.egresos.length];
            double [] costos = this.padre.getModeloCostos().getCostos();
            
            ArrayList<Gasto> listaGastos = this.padre.getListaGastos();
            ArrayList<Intereses> listaIntereses = this.padre.getListaIntereses();
            
            //calcular la cantidad neta de egresos con factura (que tengan IVA y luego quitarles el IVA)
            for (int i=0; i<egresos.length; i++){
                egresos[i] = costos[i];
                for (Gasto g: listaGastos){
                    double [] montoGastos = g.getListaGastos();
                    if (g.getFactura()){ //si tiene factura se suma a los egresos con factura                        
                        if (i<montoGastos.length)
                            egresos[i]+=montoGastos[i];
                    }
                    else{//si el gasto no tiene factura se suma a un acumulador que luego se suma
                        if (i<montoGastos.length)
                            costosSinFactura[i]+=montoGastos[i];
                    }                    
                }
                //se suman los intereses al acumulado de gastos sin factura, sólo si son de una entidad supervisada
                for (Intereses n:listaIntereses){
                    if (n.getEntidadSupervisada()){
                        double [] monto = n.getListaCuotasAnuales();
                        if (i<monto.length)
                            costosSinFactura[i]+=monto[i];
                    }
                }
                //se calcula la cantidad neta de egresos con factura y se suma a los gastos sin factura
                egresos[i] = egresos[i]/IVA.AJUSTE;
                egresos[i] += costosSinFactura[i];
            }
        }
    }

    private void calcularUAI(){
        //this.ingresos = this.padre.getModeloIngresos().getIngresos();
        for (int i=0; i<this.egresos.length;i++){
            if (this.padre.getDatosNetos())
                this.UAI[i] = ingresos[i]-egresos[i];
            else{
                this.ingresos[i] = ingresos[i]/IVA.AJUSTE;
                this.UAI[i] = ingresos[i] - egresos[i];
            }
            this.porcentajeUtilidad[i] = this.UAI[i]/ingresos[i];
            
            if (this.porcentajeUtilidad[i]>ISR.REGLA_DECISION)
                this.regimen[i] = ISR.REGIMEN_GENERAL;
            else
                this.regimen[i] = ISR.REGIMEN_OPTATIVO;
        }
    }

    private void regimenOptativo(int indice,boolean cantidadesNetas){
        if ((this.UAI[indice]<=0)||(this.porcentajeUtilidad[indice]<ISR.LIMITE)){ //si la utilidad es negativa o si el porcentaje de utilidad es menor a 3%
            this.egresosDeducibles97[indice] = ingresos[indice] * ISR.TASA_PERMITIDA;
            this.UAIFiscal[indice] = ingresos[indice] - egresosDeducibles97[indice];
            this.deduciblesSiguientePeriodo[indice] = this.egresos[indice] - this.egresosDeducibles97[indice];

            if (this.padre.getEmpresaIndividual())
                this.UAIFiscal[indice] = this.UAIFiscal[indice] - getGastosPersonalesEscalados();//this.UAIFiscal[indice] = this.UAI[indice] - getGastosPersonalesEscalados();

            double isrTemporal = this.UAIFiscal[indice];

            if (indice>0)
                isrTemporal -= this.deduciblesSiguientePeriodo[indice-1];
            
            if (isrTemporal>0)
                ISRPorPagarTemporal[indice] = isrTemporal * ISR.REGIMEN_OPTATIVO;
            else
                ISRPorPagarTemporal[indice] = 0;
        }
        else{// si la utilidad es positiva y además el porcentaje de utilidad es mayor a 3% (caso I)
            this.UAIFiscal[indice] = this.UAI[indice];
            if (this.padre.getEmpresaIndividual()) //validar que si la UAI fiscal es negativa no aplicar los gastos personales
                this.UAIFiscal[indice] = this.UAIFiscal[indice] - getGastosPersonalesEscalados();
            
            double isrTemporal = this.UAIFiscal[indice];

            if (indice>0)
                isrTemporal = isrTemporal - this.deduciblesSiguientePeriodo[indice-1];

            if (isrTemporal>0)
                this.ISRPorPagarTemporal[indice] = isrTemporal * ISR.REGIMEN_OPTATIVO;
            else
                this.ISRPorPagarTemporal[indice] = 0;
        }
    }
}

