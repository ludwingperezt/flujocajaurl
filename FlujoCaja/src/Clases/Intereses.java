package Clases;

import controlador.Escenario;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class Intereses {

    private static int ID = 0;

    public static int getID() {
        return ID;
    }

    public static void setID(int ID) {
        Intereses.ID = ID;
    }
    
    private double monto; //

    private double tasaInteres; //

    private double plazo; //

    private double capitalizaciones; //

    private double numeroPagosAnuales; //

    private double factorI; //    

    private double renta; //

    private double amortizacion; //

    private double [] resumenInteresesAnualesCuotaNivelada; //

    private double [] resumenInteresesAnualesCuotaNoNivelada; //

    private boolean cuotaNivelada; //
   
    private boolean entidadSupervisada; //

    private int pagos; //
    private Escenario padre; //
    private String nombre; //

    public Intereses () {
        this.nombre = "Prestamo"+Integer.toString(ID);
        Intereses.ID++;
    }
    
    public Intereses(Intereses base){
        this.monto = base.monto;
        this.tasaInteres = base.tasaInteres;
        this.plazo = base.plazo;
        this.capitalizaciones = base.capitalizaciones;
        this.numeroPagosAnuales = base.numeroPagosAnuales;
        this.factorI = base.factorI;
        this.renta = base.renta;
        this.amortizacion = base.amortizacion;
        if (base.resumenInteresesAnualesCuotaNivelada!=null)
            this.resumenInteresesAnualesCuotaNivelada = base.resumenInteresesAnualesCuotaNivelada.clone();
        if (base.resumenInteresesAnualesCuotaNoNivelada!=null)
            this.resumenInteresesAnualesCuotaNoNivelada = base.resumenInteresesAnualesCuotaNoNivelada.clone();
        this.cuotaNivelada = base.cuotaNivelada;
        this.entidadSupervisada = base.entidadSupervisada;
        this.pagos = base.pagos;
        this.nombre = base.nombre;
//        this.nombre = "Prestamo"+Integer.toString(ID);
//        Intereses.ID++;
    }
    
    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public String getNombre(){
        return this.nombre;
    }

    public boolean getEntidadSupervisada(){
        return this.entidadSupervisada;
    }
    public void setEntidadSupervisada(boolean val){
        this.entidadSupervisada = val;
    }
    
    public void setCuotaNivelada(boolean cuotaNivelada) {
        this.cuotaNivelada = cuotaNivelada;
    }

    public boolean getCuotaNivelada() {
        return cuotaNivelada;
    }

    public void setPadre(Escenario modelo) {
        this.padre = modelo;
    }
    public Escenario getPadre(){
        return this.padre;
    }
    
    public void setPagos(int val){
        this.pagos = val;
    }
    
    public int getPagos(){
        return this.pagos;
    }
    
    public void setResumenInteresesAnualesCuotaNivelada(double[] val) {
        this.resumenInteresesAnualesCuotaNivelada = val;
    }

    public void setResumenInteresesAnualesCuotaNoNivelada(double[] val) {
        this.resumenInteresesAnualesCuotaNoNivelada = val;
    }

    public double[] getResumenInteresesAnualesCuotaNivelada() {
        return resumenInteresesAnualesCuotaNivelada;
    }

    public double[] getResumenInteresesAnualesCuotaNoNivelada() {
        return resumenInteresesAnualesCuotaNoNivelada;
    }

    public void setFactorI(double factorI) {
        this.factorI = factorI;
    }

    public double getFactorI() {
        return factorI;
    }
    
    /**
     * Indica si se utilizará cuota nivelada o no nivelada
     * @param val True: para indicar que es cuota nivelada. False si es cuota no nivelada
     */
    public void setTipoCuota(boolean val){
        this.cuotaNivelada = val;
    }
    /**
     * Indica que tipo de cuota se está utilizando: nivelada o no nivelada
     * @return True: si la cuota es nivelada, False si es cuota no nivelada
     */
    public boolean getTipoCuota(){
        return this.cuotaNivelada;
    }
    
    public int getCapitalizacionesInt () {
        return (int)capitalizaciones;
    }

    public void setCapitalizacionesInt (int val) {
        this.capitalizaciones = (double)val;
    }

    public double getCapitalizaciones(){
        return this.capitalizaciones;
    }
    
    public void setCapitalizaciones(double val){
        this.capitalizaciones = val;
    }
    
    public double getMonto () {
        return monto;
    }

    public void setMonto (double val) {
        this.monto = val;
    }

    public int getNumeroPagosAnualesInt () {
        return (int)numeroPagosAnuales;
    }

    public void setNumeroPagosAnualesInt (int val) {
        this.numeroPagosAnuales = (double)val;
    }

    public double getNumeroPagosAnuales(){
        return this.numeroPagosAnuales;
    }
    
    public void setNumeroPagosAnuales(double val){
        this.numeroPagosAnuales = val;
    }
    
    public int getPlazoInt () {
        return (int)plazo;
    }

    public void setPlazoInt (int val) {
        this.plazo = (double)val;
    }
    
    public double getPlazo(){
        return this.plazo;
    }
    
    public void setPlazo(double val){
        this.plazo = val;
    }

    public double getTasaInteres () {
        return tasaInteres;
    }

    public void setTasaInteres (double val) {
        this.tasaInteres = val;
    }

    public double getRenta()
    {
        return this.renta;
    }
    
    public void setRenta(double val){
        this.renta = val;
    }

    public double getAmortizacion()
    {
        return this.amortizacion;
    }
    
    public void setAmortizacion(double val) {
        this.amortizacion = val;
    }

    public double [] getPagosAnualesInteresesCuotaNoNivelada()
    {
        this.calcularCuotas();
        return this.resumenInteresesAnualesCuotaNoNivelada;
    }

    public double [] getPagosAnualesInteresesCuotaNivelada()
    {
        this.calcularCuotas();
        return this.resumenInteresesAnualesCuotaNivelada;
    }

    public double [] getTablaInteresesCuotaNoNivelada()
    {
        double saldo = this.monto;
        double [] tablaIntereses = new double[pagos];
        
        for (int i=0; i<this.pagos; i++)
        {
            tablaIntereses[i] = saldo * this.factorI;
            saldo -= this.amortizacion;
        }
        return tablaIntereses;
    }

    public double [] getTablaRentaCuotaNoNivelada()
    {
        double saldo = this.monto;
        double [] tablaRenta = new double[pagos];

        for (int i=0; i<this.pagos; i++)
        {
            tablaRenta[i] = (saldo * this.factorI)+this.amortizacion;
            saldo -= this.amortizacion;
        }
        return tablaRenta;
    }

    public double [] getTablaCapitalAcumuladoCuotaNoNivelada()
    {
        double saldo = this.monto;
        double [] tablaCapital = new double[pagos];

        for (int i=0; i<this.pagos; i++)
        {
            saldo -= this.amortizacion;
            if (saldo<0)
                tablaCapital[i] = 0;
            else
                tablaCapital[i] = saldo;
        }
        return tablaCapital;
    }

    public double [] getTablaInteresesCuotaNivelada()
    {
        double saldo = this.monto;
        double [] tablaIntereses = new double[pagos];

        for (int i=0; i<this.pagos; i++)
        {
            tablaIntereses[i] = saldo * this.factorI;
            saldo -= (this.renta-tablaIntereses[i]);
        }
        return tablaIntereses;
    }

    public double [] getTablaAmortizacionCuotaNivelada()
    {
        double saldo = this.monto;
        double [] tablaAmortizacion = new double[pagos];

        for (int i=0; i<this.pagos; i++)
        {
            tablaAmortizacion[i] = this.renta-(saldo * this.factorI);
            saldo -= (tablaAmortizacion[i]);
        }
        return tablaAmortizacion;
    }

    public double [] getTablaCapitalAcumuladoCuotaNivelada()
    {
        double saldo = this.monto;
        double [] tablaCapital = new double[pagos];

        for (int i=0; i<this.pagos; i++)
        {
            saldo -= (this.renta-(saldo * this.factorI));
            if (saldo<0)
                tablaCapital[i] = 0;
            else
                tablaCapital[i] = saldo;
        }
        return tablaCapital;
    }

    public void calcularCuotas() {
        calcularCuotasNoNiveladas();
        calcularCuotasNiveladas();
    }

    private void calcularCuotasNoNiveladas() {
        double saldo = this.monto;
        double interesAcumulado = 0;
        int cantidadPagos = (int)numeroPagosAnuales;
        int indice = 0;

        calcularFactorI();

        this.resumenInteresesAnualesCuotaNoNivelada = new double[(int)plazo];
        this.amortizacion = monto / (numeroPagosAnuales * plazo);
        this.pagos = (int)((int)plazo * (int)numeroPagosAnuales);
        
        for (int i=0; i<this.pagos; i++)
        {
            interesAcumulado += saldo * this.factorI;
            saldo -= this.amortizacion;
            
            if (((i+1) % cantidadPagos) == 0)
            {
                this.resumenInteresesAnualesCuotaNoNivelada[indice] = interesAcumulado;
                interesAcumulado = 0;
                indice ++;
            }
        }
    }

    private void calcularCuotasNiveladas() {
        double saldo = this.monto;
        double interesAcumulado = 0;
        double cuotaInteres;
        int cantidadPagos = (int)numeroPagosAnuales;
        int indice = 0;

        calcularFactorI();
        this.resumenInteresesAnualesCuotaNivelada = new double[(int)plazo];
        this.renta = (monto * (Math.pow((1+(tasaInteres/capitalizaciones)), (capitalizaciones/numeroPagosAnuales))-1))/(1-Math.pow((1+(tasaInteres/capitalizaciones)), (-1 * capitalizaciones*plazo)));
        this.pagos = (int)((int)plazo * (int)numeroPagosAnuales);

        for (int i=0; i<this.pagos; i++)
        {
            cuotaInteres = saldo * this.factorI;
            interesAcumulado += cuotaInteres;
            saldo -= (this.renta-cuotaInteres);
            
            if (((i+1) % cantidadPagos) == 0)
            {
                this.resumenInteresesAnualesCuotaNivelada[indice] = interesAcumulado;
                interesAcumulado = 0;
                indice++;
            }
        }

    }

    private void calcularFactorI() {
        this.factorI = Math.pow((1+(tasaInteres/capitalizaciones)), (capitalizaciones/numeroPagosAnuales))- 1;
    }
    

    /**
     * Este método retorna la lista de cuotas a pagar por intereses seleccionando automaticamente que tipo de cuota se eligió para el préstamo
     * (couta nivelada o no nivelada)
     * @return lista de los montos anuales de intereses.
     */
    public double[] getListaCuotasAnuales() {
        if (cuotaNivelada)
            return this.resumenInteresesAnualesCuotaNivelada;
        else
            return this.resumenInteresesAnualesCuotaNoNivelada;
    }
    private double[] calcularCuotasAnuales() {
        this.calcularCuotas();
        if (cuotaNivelada)
            return this.resumenInteresesAnualesCuotaNivelada;
        else
            return this.resumenInteresesAnualesCuotaNoNivelada;
    }

    


}

