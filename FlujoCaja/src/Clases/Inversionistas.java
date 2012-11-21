/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Clases;

import controlador.Escenario;
import java.util.ArrayList;


/**
 *
 * @author ludwing
 */

public class Inversionistas {

    private Escenario padre;

    private ArrayList<Inversionista> listaInversionistas;

    public Inversionistas()
    {
        listaInversionistas = new ArrayList<Inversionista>();
    }
    
    public Inversionistas(Inversionistas base){
        if (base.listaInversionistas!=null){
            this.listaInversionistas = new ArrayList<Inversionista>();
            for (Inversionista i: base.listaInversionistas){
                this.listaInversionistas.add(new Inversionista(i));
            }
        }
    }
    public void setPadre(Escenario val){
        this.padre = val;
    }

    public Escenario getPadre(){
        return this.padre;
    }
    public ArrayList<Inversionista> getListaInversionistas()
    {
        return this.listaInversionistas;
    }
    public void setListaListaInversionistas(ArrayList<Inversionista> val){
        this.listaInversionistas = val;
    }
    public Inversionista getInversionista(int i)
    {
        return this.listaInversionistas.get(i);
    }
    public int getCantidadInversionistas()
    {
        return this.listaInversionistas.size();
    }
    /*public void insertarInversionista(String nombre, double participacion, double riesgo)
    {
        Inversionista temp = new Inversionista();
        temp.calcularDatos(nombre, participacion/100, riesgo/100);
        this.listaInversionistas.add(temp);
    }*/
    public double obtenerSumaTmarPonderada()
    {
        if (this.getCantidadInversionistas()==1)
        {
            return listaInversionistas.get(0).getTmarPonderada();
        }
        else
        {
            double suma = 0;
            for (Inversionista i:listaInversionistas)
            {
                suma+=i.getTmarPonderada();
            }
            return suma;
        }
    }

    public void vaciarListaInversionistas() {
        this.listaInversionistas.clear();
    }

    public void insertarInversionista(String nombre, double participacion, double riesgo, double inflacion) {
        Inversionista temp = new Inversionista();
        temp.setTasaInflacion(inflacion);
        temp.calcularDatos(nombre, participacion/100, riesgo/100);
        this.listaInversionistas.add(temp);
    }
}
