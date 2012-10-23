package Clases;

import controlador.Escenario;


public class Gasto {

    public static int GASTO_ESCALONADO = 0;
    public static int GASTO_FIJO = 1;
    public static int GASTO_SEGUN_COSTOS = 2;
    public static int GASTO_SEGUN_INGRESOS = 3;
    public static int GASTO_SEGUN_MODELO = 4;
    public static int GASTO_DEPRECIACION = 5;
    public static int GASTO_MANUAL = 6;
    public static int GASTO_SEGUN_GASTO = 7;
    public static int GASTO_SEGUN_INVERSION = 8;
    
    private Escenario padre;

    private double base;
    private double tasaIncremento;
    private int anioBase;
    
    /**
     * Constructor copia de objetos tipo Gasto.
     * Si el gasto es segun otro gasto o intereses, deberá buscarse manualmente ese gasto o interés y asignarlo manualmente.
     * El objeto padre deberá setearse manualemente
     * @param base 
     */
    public Gasto(Gasto base){
        this.base = base.base;
        this.tasaIncremento = base.tasaIncremento;
        this.anioBase = base.anioBase;
        this.cantidadPeriodos = base.cantidadPeriodos;
        this.tipoGasto = base.tipoGasto;
        this.nombreGasto = base.nombreGasto;
        this.factura = base.factura;
        this.escudoFiscal = base.escudoFiscal;
        this.listaGastos = base.listaGastos.clone();
        if (base.modeloGastos!=null)
            this.modeloGastos = new Modelo(base.modeloGastos);
        if (base.modeloPorcentual!=null)
            this.modeloPorcentual = new ModeloPorcentual(base.modeloPorcentual);
        if (base.gastoBase!=null){
            this.gastoBase = new Gasto();
            this.gastoBase.setNombre(base.gastoBase.nombreGasto);
        }
        if (base.interesesBase!=null){
            this.interesesBase = new Intereses();
            this.interesesBase.setNombre(base.interesesBase.getNombre());
        }
    }

    private int cantidadPeriodos;

    private int tipoGasto;

    private String nombreGasto;

    private boolean factura;
    private boolean escudoFiscal;

    private double [] listaGastos;

    private Modelo modeloGastos = null;
    private ModeloPorcentual modeloPorcentual = null;
    private Gasto gastoBase = null;
    private Intereses interesesBase = null;
    
    //private Modelos listaModelos;


    public Gasto () {
        //this.nombreGasto = "Gasto"+Integer.toString(ID);
        //Gasto.ID++;
    }

    public void setGastoBase(Gasto val){
        this.gastoBase = val;
    }
    
    public Gasto getGastoBase(){
        return this.gastoBase;
    }
    
    public void setInteresesBase(Intereses val){
        this.interesesBase = val;
    }
    
    public Intereses getInteresesBase(){
        return this.interesesBase;
    }
    
    public void setEscudoFiscal(boolean val){
        this.escudoFiscal = val;
    }
    
    public boolean getEscudoFiscal(){
        return this.escudoFiscal;
    }
    
    public void setModeloPorcentual(ModeloPorcentual modelo){
        this.modeloPorcentual = modelo;
    }
    public ModeloPorcentual getModeloPorcentual(){
        return this.modeloPorcentual;
    }
    
//    public void setListaModelos(Modelos lista){
//        this.listaModelos = lista;
//    }
//    
//    public Modelos getListaModelos(){
//        return this.listaModelos;
//    }
    
    public void setPadre(Escenario val){
        this.padre = val;
    }

    public Escenario getPadre(){
        return this.padre;
    }

    public int getAnioBase () {
        return anioBase;
    }

    public void setAnioBase (int val) {
        this.anioBase = val;
    }

    public double getBase () {
        return base;
    }

    public void setBase (double val) {
        this.base = val;
    }

    public int getCantidadPeriodos () {
        return cantidadPeriodos;
    }

    public void setCantidadPeriodos (int val) {
        this.cantidadPeriodos = val;
    }

    public double getTasaIncremento () {
        return tasaIncremento;
    }

    public void setTasaIncremento (double val) {
        this.tasaIncremento = val;
    }

    public int getTipoGasto () {
        return tipoGasto;
    }

    public void setTipoGasto (int val) {
        this.tipoGasto = val;
        if (val!=Gasto.GASTO_SEGUN_MODELO)
            this.modeloGastos = null;
    }

    /**
     * Lo mismo que setBase. Establece el monto inicial del pago
     * @param cuota
     */
    public void setCouta(double cuota)
    {
        this.base = cuota;
    }

    /**
     * Lo mismo que getBase. Obtiene el monto inicial del pago
     * @return
     */
    public double getCuota()
    {
        return this.base;
    }

    public double[] getListaGastos()
    {
        return this.listaGastos;
    }

    public void setNombre(String val)
    {
        this.nombreGasto = val;
    }

    public String getNombre()
    {
        return this.nombreGasto;
    }

    public void setFactura(boolean val)
    {
        this.factura = val;
    }

    public boolean getFactura()
    {
        return this.factura;
    }

    private void calcularGastosEscalonados()
    {
        int [] listaAnios = this.padre.getListaAnios();
        this.listaGastos = new double[this.cantidadPeriodos];
        for (int i=0; i<this.cantidadPeriodos; i++)
        {
            listaGastos[i] = this.base * Math.pow((1+this.tasaIncremento),((double)(listaAnios[i]-this.anioBase)));
        }
    }

    private void calcularGastosProporcionalesACostos()
    {
        double [] listaCostos = this.padre.costosActuales();
        this.listaGastos = new double[this.cantidadPeriodos];
        double [] temp = this.modeloPorcentual.estimarValores(listaCostos);
        for (int i=0; i<this.cantidadPeriodos; i++){
            listaGastos[i] = temp[i];
        }
    }

    private void calcularGastosProporcionalesAIngresos()
    {
        double [] listaIngresos = this.padre.ingresosActuales();
        this.listaGastos = new double[this.cantidadPeriodos];
        double [] temp  = this.modeloPorcentual.estimarValores(listaIngresos);
        for (int i=0; i<this.cantidadPeriodos; i++){
            listaGastos[i] = temp[i];
        }
    }

    private void calcularGastosCuotaFija()
    {
        listaGastos = new double[this.cantidadPeriodos];
        for (int i=0; i<this.cantidadPeriodos; i++)
        {
            listaGastos[i] = this.getCuota();
        }
    }

    public void setGastosManualmente(double [] gastos){
        this.listaGastos = gastos;
        this.tipoGasto = Gasto.GASTO_MANUAL;
    }
    /**
     *
     * @param tipoDatosPronosticados: es un indicador para saber que valores se van a utilizar, si el valor pronosticado en sí, su limite inferior o su limite superior
     * @param periodos
     */
    private void calcularGastosSegunModeloPronosticacion(){
        this.listaGastos = this.modeloGastos.estimarValores(this.cantidadPeriodos);
    }

    private void calcularGastoComoDepreciacion(){
        this.listaGastos = new double[this.cantidadPeriodos];
        for (int i=0; i<this.cantidadPeriodos; i++){
            listaGastos[i] = this.base * this.tasaIncremento;
        }
    }
    
    private void calcularGastoSegunGasto(){
//        double [] lista = this.gastoBase.listaGastos;
//        this.listaGastos = new double[this.cantidadPeriodos];
//        double [] temp = this.modeloPorcentual.estimarValores(lista);
//        for (int i=0; i<this.cantidadPeriodos; i++){
//            listaGastos[i] = temp[i];
//        }
        this.listaGastos = this.modeloPorcentual.estimarValores(this.gastoBase.listaGastos);
    }
    private void calcularGastoSegunInversion(){
//        double [] lista = this.interesesBase.getListaCuotasAnuales();
//        this.listaGastos = new double[this.cantidadPeriodos];
//        double [] temp = this.modeloPorcentual.estimarValores(lista);
//        for (int i=0; i<this.cantidadPeriodos; i++){
//            listaGastos[i] = temp[i];
//        }        
        this.listaGastos = this.modeloPorcentual.estimarValores(this.interesesBase.getListaCuotasAnuales());
    }
    /**
     * Función que calcula los gastos y luego los retorna.
     * @return 
     */
    private double [] calcularListaGastos(){
        if (this.tipoGasto==Gasto.GASTO_DEPRECIACION){
            this.calcularGastoComoDepreciacion();
        }else if (this.tipoGasto == Gasto.GASTO_ESCALONADO){
            this.calcularGastosEscalonados();
        }else if (this.tipoGasto == Gasto.GASTO_FIJO){
            this.calcularGastosCuotaFija();
        }else if (this.tipoGasto == Gasto.GASTO_SEGUN_COSTOS){
            this.calcularGastosProporcionalesACostos();
        }else if (this.tipoGasto == Gasto.GASTO_SEGUN_INGRESOS){
            this.calcularGastosProporcionalesAIngresos();
        }else if (this.tipoGasto == Gasto.GASTO_SEGUN_MODELO){
            this.calcularGastosSegunModeloPronosticacion();
        }else if (this.tipoGasto == Gasto.GASTO_SEGUN_GASTO){
            this.calcularGastoSegunGasto();
        }else if (this.tipoGasto == Gasto.GASTO_SEGUN_INVERSION){
            this.calcularGastoSegunInversion();
        }else if (this.tipoGasto == Gasto.GASTO_MANUAL){
            //g.getListaGastos();
        }
        return this.listaGastos;
    }
    public void calcularGastos(){
        if (this.tipoGasto==Gasto.GASTO_DEPRECIACION){
            this.calcularGastoComoDepreciacion();
        }else if (this.tipoGasto == Gasto.GASTO_ESCALONADO){
            this.calcularGastosEscalonados();
        }else if (this.tipoGasto == Gasto.GASTO_FIJO){
            this.calcularGastosCuotaFija();
        }else if (this.tipoGasto == Gasto.GASTO_SEGUN_COSTOS){
            this.calcularGastosProporcionalesACostos();
        }else if (this.tipoGasto == Gasto.GASTO_SEGUN_INGRESOS){
            this.calcularGastosProporcionalesAIngresos();
        }else if (this.tipoGasto == Gasto.GASTO_SEGUN_MODELO){
            this.calcularGastosSegunModeloPronosticacion();
        }else if (this.tipoGasto == Gasto.GASTO_SEGUN_GASTO){
            this.calcularGastoSegunGasto();
        }else if (this.tipoGasto == Gasto.GASTO_SEGUN_INVERSION){
            this.calcularGastoSegunInversion();
        }else if (this.tipoGasto == Gasto.GASTO_MANUAL){
            //g.getListaGastos();
        }
    }
    /////////////////////////
     public Modelo getModeloGastos () {
        return modeloGastos;
    }

    public void setModeloGastos (Modelo val) {
        this.tipoGasto = Gasto.GASTO_SEGUN_MODELO;
        this.modeloGastos = val;
    }

}