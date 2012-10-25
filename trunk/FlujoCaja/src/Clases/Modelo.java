package Clases;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;


public class Modelo {
    
    public static double CONFIANZA_90 = 1.65;
    public static double CONFIANZA_95 = 1.96;
    public static double CONFIANZA_99 = 2.58;

    public static int VALOR_CENTRAL = 1;
    public static int LIMITE_INFERIOR = 0;
    public static int LIMITE_SUPERIOR = 2;
    public static int COSTO_PORCENTUAL = 3;

    public static int LINEAL = 0;
    public static int POLINOMIAL = 1;
    public static int EXPONENCIAL=2;
    public static int POTENCIAL=3;
    public static int LOGARITMICO=4;

    private static double GRADOS_LIBERTAD = 2;
    private static double GRADOS_LIBERTAD_2 = 2;
    
    
    private int tipo;  //INDICA el modelo de correlación: lineal=0, polinomial=1, exponencial=2, potencial=3 o logarítmico=4

    private double [] x;

    private double [] y;

    private int n;

    private double r;

    private double r2;

    private double a = 0;

    private double b = 0;

    private double c = 0;

    private double Sxy;

    private double porcentajeConfianza;

    private int valorATomarDeEstimacion;
    
    private double [] valorCentral;

    private double [] limiteInferior;
    
    private double [] limiteSuperior;

    public Modelo () {
    }
    
    public Modelo(Modelo base){
        this.tipo = base.tipo;
        if (base.x!=null)
            this.x = base.x.clone();
        if (base.y!=null)
            this.y = base.y.clone();
        this.n = base.n;
        this.r = base.r;
        this.r2 = base.r2;
        this.a = base.a;
        this.b = base.b;
        this.c = base.c;
        this.Sxy = base.Sxy;
        this.porcentajeConfianza = base.porcentajeConfianza;
        this.valorATomarDeEstimacion = base.valorATomarDeEstimacion;
        if (base.valorCentral!=null)
            this.valorCentral = base.valorCentral.clone();
        if (base.limiteInferior!=null)
            this.limiteInferior = base.limiteInferior.clone();
        if (base.limiteSuperior!=null)
            this.limiteSuperior = base.limiteSuperior.clone();
    }
    
    public Modelo (double[]x,double[]y,int tipoCorrelacion) throws Exception {
        this.calcularVarianzas(x, y, tipoCorrelacion);
    }

    public void setValorATomarDeEstimacion(int val){
        this.valorATomarDeEstimacion = val;
    }

    public int getValorATomarDeEstimacion(){
        return this.valorATomarDeEstimacion;
    }

    public double getSxy () {
        return Sxy;
    }

    public void setSxy (double val) {
        this.Sxy = val;
    }

    public int getN () {
        return n;
    }

    public void setN (int val) {
        this.n = val;
    }

    public double getR () {
        return r;
    }

    public void setR (double val) {
        this.r = val;
    }

    public double getR2 () {
        return r2;
    }

    public void setR2 (double val) {
        this.r2 = val;
    }

    public int getTipo () {
        return tipo;
    }

    public void setTipo (int val) {
        this.tipo = val;
    }

    public double [] getX () {
        return x;
    }

    public void setX (double[] val) {
        this.x = val;
    }

    public double [] getY () {
        return y;
    }

    public void setY (double [] val) {
        this.y = val;
    }

    public double getA()
    {
        return a;
    }

    public void setA(double a)
    {
        this.a = a;
    }

    public double getB()
    {
        return b;
    }

    public void setB(double b)
    {
        this.b = b;
    }

    public double getC()
    {
        return c;
    }

    public void setC(double c)
    {
        this.c = c;
    }
    
    public double[] getLimiteInferior() {
        return limiteInferior;
    }

    public void setLimiteInferior(double[] limiteInferior) {
        this.limiteInferior = limiteInferior;
    }

    public double[] getLimiteSuperior() {
        return limiteSuperior;
    }

    public void setLimiteSuperior(double[] limiteSuperior) {
        this.limiteSuperior = limiteSuperior;
    }

    public double getPorcentajeConfianza() {
        return porcentajeConfianza;
    }

    public void setPorcentajeConfianza(double porcentajeConfianza) {
        this.porcentajeConfianza = porcentajeConfianza;
    }

    public double[] getValorCentral() {
        return valorCentral;
    }

    public void setValorCentral(double[] valorCentral) {
        this.valorCentral = valorCentral;
    }

    public void setConfianza(double confianza){
        this.porcentajeConfianza = confianza;
    }

    public double getConfianza(){
        return this.porcentajeConfianza;
    }
    
    public void calcularVarianzas(double[]x,double[]y,int tipoCorrelacion)
    {
        
            this.setX(x);
            this.setY(y);
            this.setTipo(tipoCorrelacion);
            this.setN(this.x.length);
            switch(tipoCorrelacion)
            {
                case 0:
                    this.modeloLineal();
                    break;
                case 1:
                    this.modeloPolinomial();
                    break;
                case 2:
                    this.modeloExponencial();
                    break;
                case 3:
                    this.modeloPotencial();
                    break;
                case 4:
                    this.modeloLogaritmico();
                    break;
            } 
        
    }
    private void modeloLineal()
    {
        double sumaXY = this.sumarYX();
        double sumaX = this.sumarX();
        double sumaY = this.sumarY();
        double sumaX2 = this.sumarX2();
        double sumaY2 = this.sumarY2();
        double sumaCuadradaY = sumaY * sumaY;
        double sumaCuadradaX = sumaX * sumaX;
        double variacionNoExplicada = 0;
        double numero = (double)this.n;

        a = ((numero*sumaXY)-(sumaX*sumaY))/((numero*sumaX2)-sumaCuadradaX);
        b = (sumaY/numero)-(a*(sumaX/numero));       

        //calcular la variación no explicada
        for (int i=0; i<this.n; i++)
        {
            double YCalculada = (a*x[i])+b;
            double varianza = (y[i]-YCalculada);
            variacionNoExplicada += Math.pow(varianza, 2);
        }
        
        this.r = ((numero*sumaXY)-(sumaX*sumaY))/(Math.sqrt(((numero*sumaX2)-sumaCuadradaX)*((numero*sumaY2)-sumaCuadradaY)));
        this.r2 = this.r * this.r;
        this.Sxy = Math.sqrt(variacionNoExplicada/(numero-Modelo.GRADOS_LIBERTAD));
    }
    private double sumarYX()
    {
        double suma = 0;
        for (int i=0; i<this.n; i++)
        {
            suma += (x[i]*y[i]);
        }
        return suma;
    }
    private double sumarYXPromedio()
    {
        double suma = 0;
        for (int i=0; i<this.n; i++)
        {
            suma += (x[i]*y[i]);
        }
        return suma/((double)n);
    }
    private double sumarX()
    {
        double suma = 0;
        for (int i=0; i<this.n; i++)
        {
            suma += x[i];
        }
        return suma;
    }
    private double mediaX()
    {
        return this.sumarX()/((double)n);
    }
    private double sumarY()
    {
        double suma = 0;
        for (int i=0; i<this.n; i++)
        {
            suma += y[i];
        }
        return suma;
    }
    private double mediaY()
    {
        return this.sumarY()/((double)n);
    }
    private double sumarX2()
    {
        double suma = 0;
        for (int i=0; i<this.n; i++)
        {
            suma += (x[i]*x[i]);
        }
        return suma;
    }
    private double sumarY2()
    {
        double suma = 0;
        for (int i=0; i<this.n; i++)
        {
            suma += (y[i]*y[i]);
        }
        return suma;
    }

    private void modeloPolinomial()
    {
        double sumaX = sumarX();
        double sumaX2 = sumarX2();
        double sumaX3 = sumarX3();
        double sumaX4 = sumarX4();
        double sumaX2Y = sumarX2Y();
        double sumaXY = sumarYX();
        double numero = (double)this.n;
        double sumaY = sumarY();
        double variacionNoExplicada = 0;
        double variacionTotal = 0;
        double variacionExplicada = 0;
        double mediaY = mediaY();
        
        double[][] matrixData2 = { {numero,sumaX, sumaX2}, {sumaX,sumaX2,sumaX3}, {sumaX2,sumaX3,sumaX4}}; //se definen los coeficientes de la matriz principal
        RealVector constants = new ArrayRealVector(new double[] { sumaY, sumaXY, sumaX2Y }, false);  //se define el vector del otro lado de la ecuación

        RealMatrix coefficients = new Array2DRowRealMatrix(matrixData2,false);
        DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
        RealVector solution = solver.solve(constants); //vector con la solucion

        a = solution.getEntry(2);
        b = solution.getEntry(1);
        c = solution.getEntry(0);

        for (int i=0; i<this.n; i++)
        {
            double YCalculada = (a * Math.pow(x[i], 2))+(b * x[i])+c;
            double varianza = (y[i]-YCalculada);
            variacionNoExplicada += Math.pow(varianza, 2);
            variacionTotal += Math.pow((y[i]-mediaY), 2);
            variacionExplicada += Math.pow(YCalculada-mediaY, 2);
        }

        this.r = Math.sqrt(variacionExplicada/variacionTotal);
        this.r2 = this.r * this.r;
        this.Sxy = Math.sqrt(variacionNoExplicada/(numero-Modelo.GRADOS_LIBERTAD_2));

    }
    private double sumarX3()
    {
        double suma = 0;
        for (int i=0; i<this.n; i++)
        {
            suma += (x[i]*x[i]*x[i]);
        }
        return suma;
    }
    private double sumarX4()
    {
        double suma = 0;
        for (int i=0; i<this.n; i++)
        {
            suma += (x[i]*x[i]*x[i]*x[i]);
        }
        return suma;
    }
    private double sumarX2Y()
    {
        double suma = 0;
        for (int i=0; i<this.n; i++)
        {
            suma += (x[i]*x[i]*y[i]);
        }
        return suma;
    }


    private void modeloExponencial()
    {
        double mediaXlnY = this.mediaXlnY();
        double promedioX = this.mediaX();
        double mediaLnY = this.mediaLnY();
        double mediaX2 = this.mediaX2();
        double mediaCuadradaX = promedioX * promedioX;
        double mediaLnY2 = this.mediaLnY2();
        double mediaCuadradaLnY = mediaLnY * mediaLnY;
        double variacionNoExplicada = 0;

        b = (mediaXlnY-(promedioX*mediaLnY))/(mediaX2-mediaCuadradaX);
        a = Math.exp(mediaLnY-(b*promedioX));

        r = (mediaXlnY-(promedioX*mediaLnY))/(Math.sqrt((mediaX2-mediaCuadradaX)*(mediaLnY2-mediaCuadradaLnY)));
        r2 = Math.pow(r, 2);

        for (int i=0; i<this.n; i++)
        {
            double YCalculada = a*Math.exp(x[i]*b);
            double varianza = (y[i]-YCalculada);
            variacionNoExplicada += Math.pow(varianza, 2);
        }
        this.Sxy = Math.sqrt(variacionNoExplicada/(((double)this.n)-Modelo.GRADOS_LIBERTAD_2));
    }
    private double sumaXlnY()
    {
        double suma = 0;
        for (int i=0; i<this.n; i++)
        {
            suma += x[i]*Math.log(y[i]);
        }
        return suma;
    }
    private double mediaXlnY()
    {
        return sumaXlnY()/((double)this.n);
    }
    private double sumaLnY()
    {
        double suma = 0;
        for (int i=0; i<this.n; i++)
        {
            suma += Math.log(y[i]);
        }
        return suma;
    }
    private double mediaLnY()
    {
        return sumaLnY()/((double)this.n);
    }
    private double mediaX2()
    {
        return sumarX2()/((double)this.n);
    }
    private double mediaY2()
    {
        return sumarY2()/((double)this.n);
    }
    private double sumaLnY2()
    {
        double suma = 0;
        for (int i=0; i<this.n; i++)
        {
            suma += Math.log(y[i])*Math.log(y[i]);
        }
        return suma;
    }
    private double mediaLnY2()
    {
        return sumaLnY2()/((double)this.n);
    }

    private void modeloPotencial()
    {
        double medialnXlnY = this.mediaLnXLnY();
        double mediaLnX = this.mediaLnX();
        double mediaLnY = this.mediaLnY();
        double mediaLnX2 = this.mediaLnX2();
        double mediaCuadradaLnX = mediaLnX * mediaLnX;
        double mediaLnY2 = this.mediaLnY2();
        double mediaCuadradaLnY = mediaLnY * mediaLnY;
        double variacionNoExplicada = 0;

        b = (medialnXlnY-(mediaLnX*mediaLnY))/(mediaLnX2-mediaCuadradaLnX);
        a = Math.exp(mediaLnY-(b*mediaLnX));

        r = (medialnXlnY-(mediaLnX*mediaLnY))/(Math.sqrt((mediaLnX2-mediaCuadradaLnX)*(mediaLnY2-mediaCuadradaLnY)));
        r2 = Math.pow(r, 2);

        for (int i=0; i<this.n; i++)
        {
            double YCalculada = a*Math.pow(x[i], b);
            double varianza = (y[i]-YCalculada);
            variacionNoExplicada += Math.pow(varianza, 2);
        }
        this.Sxy = Math.sqrt(variacionNoExplicada/(((double)this.n)-Modelo.GRADOS_LIBERTAD_2));
    }
    private double sumaLnXLnY()
    {
        double suma = 0;
        for (int i=0; i<this.n; i++)
        {
            suma += Math.log(x[i])*Math.log(y[i]);
        }
        return suma;
    }
    private double mediaLnXLnY()
    {
        return sumaLnXLnY()/((double)this.n);
    }
    private double sumaLnX()
    {
        double suma = 0;
        for (int i=0; i<this.n; i++)
        {
            suma += Math.log(x[i]);
        }
        return suma;
    }
    private double mediaLnX()
    {
        return sumaLnX()/((double)this.n);
    }
    private double sumaLnX2()
    {
        double suma = 0;
        for (int i=0; i<this.n; i++)
        {
            suma += Math.log(x[i])*Math.log(x[i]);
        }
        return suma;
    }
    private double mediaLnX2()
    {
        return sumaLnX2()/((double)this.n);
    }
    private void modeloLogaritmico()
    {
        double mediaYLnX = this.mediaYLnX();
        double mediaLnX = this.mediaLnX();
        double promedioY = this.mediaY();
        double mediaLnX2 = this.mediaLnX2();
        double mediaCuadradaLnX = mediaLnX * mediaLnX;
        double mediaY2 = this.mediaY2();
        double mediaCuadradaY = promedioY * promedioY;
        double variacionNoExplicada = 0;

        b= (mediaYLnX-(mediaLnX*promedioY))/(mediaLnX2-mediaCuadradaLnX);
        a = promedioY - (b * mediaLnX);

        r = (mediaYLnX-(mediaLnX*promedioY))/(Math.sqrt((mediaLnX2-mediaCuadradaLnX)*(mediaY2-mediaCuadradaY)));
        r2 = Math.pow(r, 2);

        for (int i=0; i<this.n; i++)
        {
            double YCalculada = a + (b*Math.log(x[i]));
            double varianza = (y[i]-YCalculada);
            variacionNoExplicada += Math.pow(varianza, 2);
        }
        this.Sxy = Math.sqrt(variacionNoExplicada/(((double)this.n)-Modelo.GRADOS_LIBERTAD_2));
    }
    private double sumaYLnX()
    {
        double suma = 0;
        for (int i=0; i<this.n; i++)
        {
            suma += y[i]*Math.log(x[i]);
        }
        return suma;
    }
    private double mediaYLnX()
    {
        return sumaYLnX()/((double)this.n);
    }

    public String getNombreModelo() {
        String nombre = "";

        switch(this.tipo)
        {
            case 0:
                nombre = "Lineal";
                break;
            case 1:
                nombre = "Polinomial";
                break;
            case 2:
                nombre = "Exponencial";
                break;
            case 3:
                nombre = "Potencial";
                break;
            case 4:
                nombre = "Logarítmico";
                break;
        }

        return nombre;

    }

    public String getEcuacion() {
        String ecuacion = "";

        switch(this.tipo)
        {
            case 0:
                ecuacion = Double.toString(a)+"X + "+Double.toString(b);
                break;
            case 1:
                ecuacion = Double.toString(a)+"X² + "+Double.toString(b)+"X + "+Double.toString(c);
                break;
            case 2:
                ecuacion = Double.toString(a)+"e^X"+Double.toString(b);
                break;
            case 3:
                ecuacion = Double.toString(a)+"X^"+Double.toString(b);
                break;
            case 4:
                ecuacion = Double.toString(b)+"Ln(X) + "+Double.toString(a);
                break;
        }
        return ecuacion;
    }

    public double estimarValorFuturo(double x) {
        double valor = 0;
        switch(this.tipo)
        {
            case 0:
                valor = (a*x)+b;
                break;
            case 1:
                valor = (a*x*x) +(b*x)+c;
                break;
            case 2:
                valor = a * Math.exp(x*b);
                break;
            case 3:
                valor = a * Math.pow(x, b);
                break;
            case 4:
                valor = (b*Math.log(x))+a;
                break;
        }
        return valor;
    }
    public double estimarLimiteInferior(double xEstimado,double confianza)
    {
        double limite = 0;
        double z;
        if (confianza==99)
        {
            z = 2.58;
        }
        else if (confianza == 95)
        {
            z = 1.96;
        }
        else
        {
            z = 1.65;
        }
        limite = xEstimado - (z * (this.Sxy/Math.sqrt((double)this.n)));
        return limite;
    }
    public double estimarLimiteSuperior(double xEstimado,double confianza)
    {
        double limite = 0;
        double z;
        if (confianza==99)
        {
            z = 2.58;
        }
        else if (confianza == 95)
        {
            z = 1.96;
        }
        else
        {
            z = 1.65;
        }
        limite = xEstimado + (z * (this.Sxy/Math.sqrt((double)this.n)));
        return limite;
    }
    public double[] estimarSerieValoresFuturosCalculados(double [] x)
    {
        double [] yFuturos = new double[x.length];
        for (int i=0; i<x.length; i++)
        {
            yFuturos[i] = this.estimarValorFuturo(x[i]);
        }
        return yFuturos;
    }
    public double[] obtenerSerieValoresFuturosX(int cantidad)
    {
        double semilla = x[x.length-1]+1;
        double [] futuros = new double[cantidad];

        for (int i=0; i<cantidad; i++)
        {
            futuros[i] = semilla + i;
        }
        return futuros;
    }
    public double[] estimarSerieLimitesInferiores(double [] xEstimados,double confianza)
    {
        double [] limitesInferiores = new double[xEstimados.length];
        for (int i=0; i<xEstimados.length; i++)
        {
            limitesInferiores[i] = this.estimarLimiteInferior(xEstimados[i],confianza);
        }
        return limitesInferiores;
    }
    public double[] estimarSerieLimitesSuperiores(double [] xEstimados,double confianza)
    {
        double [] limitesSuperiores = new double[xEstimados.length];
        for (int i=0; i<xEstimados.length; i++)
        {
            limitesSuperiores[i] = this.estimarLimiteSuperior(xEstimados[i],confianza);
        }
        return limitesSuperiores;
    }

    public double[] estimarSerieLimitesInferiores(double [] xEstimados)
    {
        double [] limitesInferiores = new double[xEstimados.length];
        for (int i=0; i<xEstimados.length; i++)
        {
            limitesInferiores[i] = this.estimarLimiteInferior(xEstimados[i],this.porcentajeConfianza);
        }
        return limitesInferiores;
    }
    
    public double[] estimarSerieLimitesSuperiores(double [] xEstimados)
    {
        double [] limitesSuperiores = new double[xEstimados.length];
        for (int i=0; i<xEstimados.length; i++)
        {
            limitesSuperiores[i] = this.estimarLimiteSuperior(xEstimados[i],this.porcentajeConfianza);
        }
        return limitesSuperiores;
    }
    //potencial, logaritmico
    /**
     * Función que utiliza la ecuación encontrada para pronosticar valores.
     * Para poder utilizarla, el objeto ya deberá tener calculada la ecuación, 
     * definido el porcentaje de confianza y establecido qué valor se utilizará:
     * Valor central, límites inferiores o límites superiores
     * @return 
     */
    public double [] estimarValores(int periodos){
        this.valorCentral = this.estimarSerieValoresFuturosCalculados(this.obtenerSerieValoresFuturosX(periodos));
        this.limiteInferior = this.estimarSerieLimitesInferiores(this.valorCentral);
        this.limiteSuperior = this.estimarSerieLimitesSuperiores(this.valorCentral);
        
        if (this.valorATomarDeEstimacion==Modelo.LIMITE_INFERIOR)
            return this.limiteInferior;
        if (this.valorATomarDeEstimacion==Modelo.LIMITE_SUPERIOR)
            return this.limiteSuperior;
        else
            return this.valorCentral;
    }
}

