package controlador;
import Clases.*;
import Financieras.TIR;
import Financieras.VAN;
import java.util.ArrayList;

public class Escenario {
    
    public static int INVERSION_INICIAL_MANUAL = 0;
    public static int INVERSION_INICIAL_ACTIVOS_MAS_PRESTAMOS = 1;
    public static int INVERSION_INICIAL_SOLO_ACTIVOS = 2;
    public static int INVERSION_INICIAL_SOLO_PRESTAMOS = 3;
    
    protected int [] listaAnios;
    
    protected Inversionistas tmar;

    protected Ingresos modeloIngresos;

    protected Costos modeloCostos;

    protected double [] utilidadBruta;
    
    protected double [] utilidadNeta;
    
    protected double [] escudosFiscales;
    
    protected double [] FEN;
    
    protected double [] VAN;

    protected ArrayList<Gasto> listaGastos;    

    protected ArrayList<Intereses> listaIntereses;
    
    protected double [] utilidadAntesImpuestos;

    protected IVA modeloIVA;

    protected ISR modeloISR;
    
    protected ISO modeloISO;

    protected double escala;
    
    protected double activos;
    
    protected double activosAnteriores;
    
    protected boolean datosNetos;
    
    protected boolean empresaIndividual;
    
    protected boolean empresaNueva;
    
    protected boolean patenteDeComercio;
    
    protected int tipoInversionInicial;
    
    protected double inversionInicial;
    
    public Escenario () {
        this.listaIntereses = new ArrayList<Intereses>();
        this.listaGastos = new ArrayList<Gasto>();
    }
    
    
    public Escenario(Escenario base){
        if (base.listaAnios!=null)
            this.listaAnios = base.listaAnios.clone();
        if (base.tmar!=null)
            this.tmar = new Inversionistas(base.tmar);        
        if (base.modeloIngresos!=null)
            this.modeloIngresos = new Ingresos(base.modeloIngresos);        
        if (base.modeloCostos!=null)
            this.modeloCostos = new Costos(base.modeloCostos);        
        if (base.utilidadBruta!=null)
            this.utilidadBruta = base.utilidadBruta.clone();
        if (base.utilidadNeta!=null)
            this.utilidadNeta = base.utilidadNeta.clone();
        if (base.escudosFiscales!=null)
            this.escudosFiscales = base.escudosFiscales.clone();
        if (base.FEN!=null)
            this.FEN = base.FEN.clone();
        if (base.VAN!=null)
            this.VAN = base.VAN.clone();        
        if (base.utilidadAntesImpuestos!=null)
            this.utilidadAntesImpuestos = base.utilidadAntesImpuestos.clone();
        if (base.modeloIVA!=null)
            this.modeloIVA = new IVA(base.modeloIVA);       
        if (base.modeloISR!=null)
            this.modeloISR = new ISR(base.modeloISR);        
        if (base.modeloISO!=null)
            this.modeloISO = new ISO(base.modeloISO);
        
        this.escala = base.escala;
        this.activos = base.activos;        
        this.activosAnteriores = base.activosAnteriores;
        this.datosNetos = base.datosNetos;
        this.empresaIndividual = base.empresaIndividual;
        this.empresaNueva = base.empresaNueva;
        this.patenteDeComercio = base.patenteDeComercio;
        this.tipoInversionInicial = base.tipoInversionInicial;
        this.inversionInicial = base.inversionInicial;
        //buscar los gastos proporcionales a gastos e intereses y setearle esas propiedades
    }
    /**
     * Esta funcion va justo despues de una creacion de Escenario con constructor copia, sirve para crear los gastos e intereses
     * @param base 
     */
    public void crearGastosEInversiones(Escenario base){
        if (this.tmar!=null)
            this.tmar.setPadre(this);
        if (this.modeloCostos!=null)
            this.modeloCostos.setPadre(this);
        if (this.modeloIngresos!=null)
            this.modeloIngresos.setPadre(this);
        if (this.modeloIVA!=null)
            this.modeloIVA.setPadre(this);
        if (this.modeloISR!=null)
            this.modeloISR.setPadre(this);
        if (this.modeloISO!=null)
            this.modeloISO.setPadre(this);
        if (base.listaGastos!=null){
            this.listaGastos = new ArrayList<Gasto>();
            for (Gasto g:base.listaGastos){
                Gasto t = new Gasto(g);
                t.setPadre(this);
                this.listaGastos.add(t);                
            }
        }
        if (base.listaIntereses!=null){
            this.listaIntereses = new ArrayList<Intereses>();
            for (Intereses i:base.listaIntereses){
                Intereses t = new Intereses(i);
                t.setPadre(this);
                this.listaIntereses.add(t);
            }
        }
        
        if (this.listaGastos!=null){
            for (Gasto g:this.listaGastos){
                if (g.getTipoGasto()==Gasto.GASTO_SEGUN_GASTO){
                //if (g.getGastoBase()!=null){
                    String nombre = g.getGastoBase().getNombre();
                    Gasto t = null;
                    for (Gasto k:this.listaGastos){
                        if (k.getNombre().equals(nombre)){
                            t = k;
                            break;
                        }
                    }
                    g.setGastoBase(t);
                }else if (g.getTipoGasto()==Gasto.GASTO_SEGUN_INVERSION){
                    String nombre = g.getInteresesBase().getNombre();
                    Intereses i = null;
                    for (Intereses n:this.listaIntereses){
                        if (n.getNombre().equals(nombre)){
                            i = n;
                            break;
                        }
                    }
                    g.setInteresesBase(i);
                }
            }
        }
        
    }
    
    public void setTipoInversionInicial(int tipo){
        this.tipoInversionInicial = tipo;
    }
    
    public int getTipoInversionInicial(){
        return this.tipoInversionInicial;
    }
    
    public void setInversionInicial(double inv){
        this.inversionInicial = inv;
        this.tipoInversionInicial = Escenario.INVERSION_INICIAL_MANUAL;
    }
    
    public double getInversionInicial(){
        if (this.tipoInversionInicial==Escenario.INVERSION_INICIAL_ACTIVOS_MAS_PRESTAMOS)
            return this.activos+this.getTotalInversion();
        else if (this.tipoInversionInicial==Escenario.INVERSION_INICIAL_MANUAL)
            return this.inversionInicial;
        else if (this.tipoInversionInicial==Escenario.INVERSION_INICIAL_SOLO_ACTIVOS)
            return this.activos;
        else
            return this.getTotalInversion();
    }
    
    public void setModeloISO(ISO modelo){
        this.modeloISO=modelo;
    }
    
    public ISO getModeloISO(){
        return this.modeloISO;
    }
    
    public void setPatenteDeComercio(boolean val){
        this.patenteDeComercio = val;
    }
    
    public boolean getPatenteDeComercio(){
        return this.patenteDeComercio;
    }
    
    public void setActivos(double val){
        this.activos = val;
    }
    
    public double getActivos(){
        return this.activos;
    }
    
    public void setActivosAnteriores(double val){
        this.activosAnteriores = val;
    }
    
    public double getActivosAnteriores(){
        return this.activosAnteriores;
    }
    
    public void setDatosNetos(boolean val){
        this.datosNetos = val;
    }
    
    public boolean getDatosNetos(){
        return this.datosNetos;
    }
    
    public void setEmpresaIndividual(boolean val){
        this.empresaIndividual = val;
    }
    
    public boolean getEmpresaIndividual(){
        return this.empresaIndividual;
    }
    
    public void setEmpresaNueva(boolean val){
        this.empresaNueva = val;
    }
    
    public boolean getEmpresaNueva(){
        return this.empresaNueva;
    }

    public void setModeloISR(ISR modelo){
        this.modeloISR = modelo;
    }

    public ISR getModeloISR(){
        return this.modeloISR;
    }

    public void setEscala(double val){
        this.escala = val;
    }

    public double getEscala(){
        return this.escala;
    }

    public void setModeloIVA(IVA modelo){
        this.modeloIVA = modelo;
        this.modeloIVA.setPadre(this);
    }

    public IVA getModeloIVA(){
        return this.modeloIVA;
    }

    public int [] getListaAnios () {
        return listaAnios;
    }

    public void setListaAnios (int [] val) {
        this.listaAnios = val;
    }

    public ArrayList<Gasto> getListaGastos () {
        return listaGastos;
    }

    public void setListaGastos (ArrayList<Gasto> val) {
        this.listaGastos = val;
    }

    public ArrayList<Intereses> getListaIntereses () {
        return listaIntereses;
    }

    public void setListaIntereses (ArrayList<Intereses> val) {
        this.listaIntereses = val;
    }

    public Costos getModeloCostos () {
        return modeloCostos;
    }

    public void setModeloCostos (Costos val) {
        this.modeloCostos = val;
        this.modeloCostos.setPadre(this);
    }

    public Ingresos getModeloIngresos () {
        return modeloIngresos;
    }

    public void setModeloIngresos (Ingresos val) {
        this.modeloIngresos = val;
        this.modeloIngresos.setPadre(this);
    }

    public Inversionistas getTmar () {
        return tmar;
    }

    public void setTmar (Inversionistas val) {
        this.tmar = val;
        this.tmar.setPadre(this);
    }

    public double [] getUtilidadAntesImpuestos () {
        return utilidadAntesImpuestos;
    }

    public void setUtilidadAntesImpuestos (double [] val) {
        this.utilidadAntesImpuestos = val;
    }

    public double [] getUtilidadBruta () {
        return utilidadBruta;
    }

    public void setUtilidadBruta (double [] val) {
        this.utilidadBruta = val;
    }

    public void insertarIntereses(Intereses val){
        val.setPadre(this);
        this.listaIntereses.add(val);
    }

    public void insertarGasto(Gasto val){
        val.setPadre(this);
        this.listaGastos.add(val);
    }

    public int getNumeroPeriodos(){
        return this.listaAnios.length;
    }

    /**
     * Obtiene la lista actual de ingresos recalculando los datos
     * @return 
     */
    public double[] ingresos(){
        return this.modeloIngresos.getIngresos();
    }

    /**
     * Obtiene la lista actual de costos recalculando los datos
     * @return 
     */
    public double[] costos(){
        return this.modeloCostos.getCostos();
    }
    
    
    /**
     * Solo obtiene la lista actual de ingresos sin recalcular los datos
     * @return 
     */
    public double[] ingresosActuales(){
        return this.modeloIngresos.getIngresosActuales();
    }
    /**
     * Solo obtiene la lista actual de costos sin recalcular los datos
     * @return 
     */
    public double[] costosActuales(){
        return this.modeloCostos.getCostosActuales();
    }

    public double[] utilidadBruta() {
        this.utilidadBruta = new double[this.getNumeroPeriodos()];
        double [] ingresos = this.modeloIngresos.getIngresos();
        double [] costos = this.modeloCostos.getCostos();
        for (int i=0; i<this.getNumeroPeriodos(); i++){
            utilidadBruta[i]=ingresos[i]-costos[i];
        }
        return this.utilidadBruta;
    }

    public double [] utilidadAntesDeImpuestos(){
        this.utilidadAntesImpuestos = new double[this.getNumeroPeriodos()];

        for (int i=0; i<this.getNumeroPeriodos(); i++){
            this.utilidadAntesImpuestos[i] = this.utilidadBruta[i];
            for (Gasto j: listaGastos){
                double [] gastos = j.getListaGastos();
                if (i<gastos.length)
                    this.utilidadAntesImpuestos[i] -= gastos[i];
            }
            for (Intereses k:listaIntereses){
                double [] interes = k.getListaCuotasAnuales();
                if (i<interes.length)
                    this.utilidadAntesImpuestos[i]-= interes[i];
            }
        }
        return this.utilidadAntesImpuestos;
    }

    public double [] IVAporPagar(){        
        double [] egresos = new double[this.getNumeroPeriodos()];
        for (int i=0; i<this.getNumeroPeriodos(); i++){
            egresos[i]=this.costos()[i];
            for (Gasto j: listaGastos){
                double [] gastos = j.getListaGastos();
                if ((i<gastos.length)&&(j.getFactura()==true)) //si el indice es válido (si se hace el gasto en ese periodo) y si tiene factura
                    egresos[i] += gastos[i];
            }
        }
        this.modeloIVA.calcularIVA(this.modeloIngresos.getIngresos(), egresos);
        return this.modeloIVA.getIVAPorPagar();
    }

    public double [] ISRporPagar(){
        //this.modeloISR.calcularISR();
        return modeloISR.getISRporPagar();
    }
    public double [] ISRporPagarTemporal(){
        //this.modeloISR.calcularISR();
        return modeloISR.getISRPorPagarTemporal();
    }
    /**
     * Función retorna el ISO a pagar
     * @return 
     */
    public double[] ISOporPagar(){
        return this.modeloISO.getIsoPorPagarDefinitivo();
    }

    /**
     * Función para recalcular todos los valores en caso de una modificación o eliminación.
     * Los modelos que sean modificados ya deberán de tener los nuevos datos antes de ejecutar ésta función.
     */
    public void recalcularTodo() {

        //ingresos
        if (this.modeloIngresos!=null)
            this.modeloIngresos.pronosticarIngresos();
        //costos
        if (this.modeloCostos!=null)
            this.modeloCostos.pronosticarCostos();
        //gastos
        for (Gasto g : this.listaGastos){
            g.calcularGastos();            
        }
        //intereses: no se recalcula aquí porque eso se hace en la ventana de modificación
        
        //utilidad bruta
        if (this.utilidadBruta!=null) //si la utilidad bruta ya fue calculada
            this.utilidadBruta();

        if (this.utilidadAntesImpuestos!=null) //si la utilidad antes de impuestos no ha sido calculada
            this.utilidadAntesDeImpuestos();

        if (this.modeloIVA!=null) //si el IVA ya se calculó
            this.IVAporPagar();

        if (this.modeloISR!=null){  //si el ISR ya se calculó
            this.modeloISR.calcularISR();
            if (this.modeloISO!=null) //verifica también el ISO
                this.modeloISO.calcularISO();
        }
        //si la utilidad neta ya fue calculada al menos una vez, se calcula de nuevo
        if (this.utilidadNeta!=null)
            this.calcularUtilidadNeta();
        
        //si los valores de FEN ya fueron calculados al menos una vez, se calcula de nuevo
        if (this.FEN!=null)
            this.calcularFEN();
        
        //si los valores de VAN ya fueron calculados al menos una vez, se calcula de nuevo
        if (this.VAN!=null)
            this.calcularVAN();
        
    }

    /**
     * Función para averiguar si los ingresos ya fueron calculados
     * @return True: si ya existe un modelo de ingresos. False si no
     */
    public boolean ingresosCalculados(){
        return (this.modeloIngresos!=null);
    }
    /**
     * Función para averiguar su los costos ya fueron calculados
     * @return True: si existe un modelo de costos. False si no.
     */
    public boolean costosCalculados(){
        return (this.modeloCostos!=null);
    }
    
    /**
     * Función para averiguar si ya hay un modelo existente para calcular el IVA
     * @return 
     */
    public boolean ivaCalculado(){
        return (this.modeloIVA!=null);
    }
    
    /**
     * Función que determina si ya existe un modelo para calcular ISR
     * @return 
     */
    public boolean isrCalculado(){
        return (this.modeloISR!=null);
    }
    
    public boolean uaiCalculado(){
        return (this.utilidadAntesImpuestos!=null);
    }
    
    public boolean utilidadBrutaCalculada(){
        return (this.utilidadBruta!=null);
    }
    
    public boolean ISOcalculado(){
        return (this.modeloISO!=null);
    }

    public boolean gastoValido(String nombre) {
        boolean bandera = false;
        for (Gasto g:listaGastos){
            if (nombre.equals(g.getNombre())){
                bandera = true;
                break;
            }
        }
        return bandera;
    }

    public Gasto obtenerGasto(String nombre) {
        Gasto temporal = null;
        for(Gasto g:listaGastos){
            if (nombre.equals(g.getNombre())){
                temporal = g;
                break;
            }
        }
        return temporal;
    }
    public void removerGasto(Gasto val){
        listaGastos.remove(val);
    }

    public boolean interesValido(String nombre) {
        boolean bandera = false;
        for (Intereses i:listaIntereses){
            if (nombre.equals(i.getNombre())){
                bandera = true;
                break;
            }
        }
        return bandera;
    }
    public Intereses obtenerInteres(String nombre) {
        Intereses temporal = null;
        for(Intereses i:listaIntereses){
            if (nombre.equals(i.getNombre())){
                temporal = i;
                break;
            }
        }
        return temporal;
    }
    
    //
    /**
     * recalcula o calcula el ISR. Si ya existe el modelo solo hace los cálculos, si no existe el modelo lo crea y luego hace los cálculos
     * @param empresaIndv 
     */
    public void calcularISR(boolean empresaIndv){
        if (this.modeloISR==null){        
            this.modeloISR = new ISR();
            modeloISR.setPadre(this);
            this.setModeloISR(modeloISR);
            this.setEmpresaIndividual(empresaIndv);
        }
        modeloISR.calcularISR();        
    }
    
    public double [] getUtilidadNeta(){
        return this.utilidadNeta;
    }
    
    public void setUtilidadNeta(double [] uNeta){
        this.utilidadNeta = uNeta;
    }
    
    public double [] getEscudosFiscales(){
        return this.escudosFiscales;
    }
    
    /**
     * Devuelve los valores de flujo de efectivo neto, sin realizar operaciones, es decir los valores actuales
     * @return 
     */
    public double [] getFEN(){
        return this.FEN;
    }
    
    /**
     * Devuelve los valores de valor actual neto, sin realizar operaciones, es decir los valores actuales
     * @return 
     */
    public double [] getVAN(){
        return this.VAN;
    }
    
    /**
     * Calcula la utilidad neta (utilidad antes de impuestos - impuestos)
     * Antes deben estar creados todos los modelos y calculados todos los datos
     * @return 
     */
    public double [] calcularUtilidadNeta(){
        this.utilidadNeta = new double[this.getNumeroPeriodos()];
        double [] isrDefinitivo = this.modeloISR.getISRporPagar();
        double [] ivaPorPagar = this.modeloIVA.getIVAPorPagar();
        double [] isoPorPagar = this.modeloISO.getIsoPorPagarDefinitivo();
        for (int i=0; i<this.utilidadNeta.length; i++){
            this.utilidadNeta[i] = this.utilidadAntesImpuestos[i] - ivaPorPagar[i] - isrDefinitivo[i] - isoPorPagar[i];
        }
        return this.utilidadNeta;
    }
    
    /**
     * Calcula y retorna los valores de flujo de efectivo neto.
     * Antes deben estar creados todos los modelos y calculados todos los datos
     * @return 
     */
    public double [] calcularFEN(){
        this.FEN = new double[this.getNumeroPeriodos()];
        this.calcularEscudosFiscales();
        for (int i=0; i<this.utilidadAntesImpuestos.length; i++){
            this.FEN[i] = this.utilidadNeta[i]+this.escudosFiscales[i];
        }
        return this.FEN;
    }
    
    /**
     * Calcula y devuelve los valores del valor actual neto. 
     * Antes deben estar creados todos los modelos y calculados todos los datos
     * @return 
     */
    public double [] calcularVAN(){
        this.VAN = new double[this.getNumeroPeriodos()];
        double sumaTmar = this.tmar.obtenerSumaTmarPonderada();
        
        for (int i=0; i<this.VAN.length; i++){
            this.VAN[i] = this.FEN[i] * Math.pow(1+sumaTmar,-(i+1));
        }
        
        return this.VAN;
    }
    
    protected void calcularEscudosFiscales(){
        this.escudosFiscales = new double[this.getNumeroPeriodos()];
        for (int i=0; i<this.getNumeroPeriodos(); i++){
            for (Gasto g: listaGastos){
                double [] montoGastos = g.getListaGastos();
                if (g.getEscudoFiscal()){ //si es un escudo fiscal
                    if (i<montoGastos.length)
                        escudosFiscales[i]+=montoGastos[i];
                }
            }
        }
    }
    /**
     * Retorna el total de la inversión (sumatoria de todos los montos de prestamos)
     * @return 
     */
    public double getTotalInversion(){
        double total = 0;
        for (Intereses i:listaIntereses){
            total+=i.getMonto();
        }
        return total;
    }
    
    /**
     * Obtiene la sumatoria del total de la inversión (con signo negativo) con los valores de VAN por cada periodo
     * @return 
     */
    public double getSumatoriaVAN(){
        //double total = -this.getTotalInversion();
        double total = -this.getInversionInicial();
        
        for (int i=0; i<this.VAN.length; i++){
            total += this.VAN[i];
        }
        
        return total;
    }
    
    /**
     * Retorna el número de años necesarios para el retorno de la inversión.
     * Si el valor de retorno es -1, la inversión se recupera en más de 10 años.
     * @return 
     */
    public int getPayback(){
        //double total = -this.getTotalInversion();
        double total = -this.getInversionInicial();
        int payback = -1;
        
        for (int i=0; i<this.VAN.length; i++){
            total += this.VAN[i];
            if (total>=0){
                payback = i+1;
                break;
            }
        }
        return payback;
    }
    
    /**
     * Retorna el número de años necesarios para el retorno de la inversión (payback), en una cadena de texto formateada
     * Si el payback está entre 1 y 10 retorna <número de años> + "años" sino retorna el mensaje "Más de 10 años"
     * @return 
     */
    public String getPayback_string(){
        int i = this.getPayback();
        String payback;
        if (i!=-1)
            payback = Integer.toString(i)+" años";
        else
            payback = "+10 años";
        return payback;
    }
    
    /**
     * Obtiene y calcula la TIR en double
     * @return 
     */
    public double getTIR(){
        VAN van = new VAN(this.getNumeroPeriodos());
        van.setInversion(this.getInversionInicial());
        for (int i=0; i<this.VAN.length; i++){
            van.setFlujo(i+1, this.VAN[i]);
        }
        TIR tir = new TIR(van);
        return tir.getTIR();
    }
    
    /**
     * Calcula y obtiene la TIR en formato porcentual como cadena
     * @return 
     */
    public String getTIR_string(){
        String sTir = "Err";
        try{
            double tir = this.getTIR();
            tir = ModeloPorcentual.redondearCifra(tir * 100);
            sTir = Double.toString(tir)+"%";
        }catch(Exception ex){
            sTir = "--";
            System.out.println(ex.toString());
        }        
        return sTir;
    }

    /**
     * Retorna el valor de la sumatoria de TMAR ponderada en formato de porcentaje con dos decimales.
     * @return 
     */
    public String getTMARFormateada() {
        double tmarF = this.tmar.obtenerSumaTmarPonderada();
        tmarF = ModeloPorcentual.redondearCifra(tmarF * 100);
        String ret = Double.toString(tmarF)+"%";
        return ret;
    }

    /**
     * Remueve un prestamo de la lista de prestamos
     * @param val 
     */
    public void removerPrestamo(Intereses val) {
        listaIntereses.remove(val);
    }
}

