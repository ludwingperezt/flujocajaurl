package Clases;


public class Inversionista {

    //protected static double inflacion = 0.09;

    private String nombre;

    private double participacion;

    private double riesgo;

    private double tmar;

    private double tmarPonderada;
    
    private double tasaInflacion;

    public Inversionista () {
    }
    
    public Inversionista (Inversionista base) {
        this.nombre = base.nombre;
        this.participacion = base.participacion;
        this.riesgo = base.riesgo;
        this.tmar = base.tmar;
        this.tmarPonderada = base.tmarPonderada;
        this.tasaInflacion = base.tasaInflacion;
        //Inversionista.inflacion = base.tasaInflacion;
    }
/*
    public static double getInflacion () {
        return Inversionista.inflacion;
    }

    public static void setInflacion (double val) {
        Inversionista.inflacion = val;
    }
*/
    public String getNombre () {
        return nombre;
    }

    public void setNombre (String val) {
        this.nombre = val;
    }

    public double getParticipacion () {
        return participacion;
    }

    public void setParticipacion (double val) {
        this.participacion = val;
    }

    public double getRiesgo () {
        return riesgo;
    }

    public void setRiesgo (double val) {
        this.riesgo = val;
    }

    public double getTmar () {
        return tmar;
    }

    public void setTmar (double val) {
        this.tmar = val;
    }

    public double getTmarPonderada () {
        return tmarPonderada;
    }

    public void setTmarPonderada (double val) {
        this.tmarPonderada = val;
    }

    public void calcularDatos(String nombre, double participacion, double riesgo)
    {
        this.setNombre(nombre);
        this.setParticipacion(participacion);
        this.setRiesgo(riesgo);
        this.tmar = riesgo + this.tasaInflacion + (riesgo*this.tasaInflacion);
        this.tmarPonderada = tmar * participacion;
    }

    public void setTasaInflacion(double inflacion) {
        this.tasaInflacion = inflacion;
    }
    
    public double getTasaInflacion(){
        return this.tasaInflacion;
    }
   
}

