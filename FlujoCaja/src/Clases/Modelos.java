/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Clases;

import java.util.ArrayList;

/**
 *
 * @author ludwing
 */

public class Modelos {

    private ArrayList<Modelo> listaModelos;
    private String nombreVariable;
    private double [] historicosX;
    private double [] historicosY;
    
    public Modelos()
    {
        listaModelos = new ArrayList<Modelo>();
    }
    
    public Modelos(Modelos base){
        this.nombreVariable = base.nombreVariable;
        if (base.historicosX!=null)
            this.historicosX = base.historicosX.clone();
        if (base.historicosY!=null)
            this.historicosY = base.historicosY.clone();
        if (base.listaModelos!=null){
            this.listaModelos = new ArrayList<Modelo>();
            for(Modelo m:base.listaModelos){
                this.listaModelos.add(new Modelo(m));
            }
        }
    }
    
    public void setHistoricosX(double [] val){
        this.historicosX = val;
    }
    
    public double [] getHistoricosX(){
        return this.historicosX;
    }
    
    public void setHistoricosY(double [] val){
        this.historicosY = val;
    }
    
    public double [] getHistoricosY(){
        return this.historicosY;
    }
    
    public Modelos(double[]x,double[]y,String nombre)
    {
        this.setNombreVariable(nombre);
        listaModelos = new ArrayList<Modelo>();
        for (int i=0; i<5; i++)
        {
            Modelo temp = new Modelo();
            temp.calcularVarianzas(x, y, i);
            listaModelos.add(temp);
        }
    }
    public void setNombreVariable(String nombre)
    {
        this.nombreVariable = nombre;
    }
    public String getNombreVariable()
    {
        return this.nombreVariable;
    }
    public ArrayList<Modelo> getListaModelos()
    {
        return this.listaModelos;
    }
    public void setListaModelos(ArrayList<Modelo> val){
        this.listaModelos = val;
    }
    
    public Modelo obtenerMejorModelo()
    {
        double SxyMenor = listaModelos.get(0).getSxy();
        double rMayor = listaModelos.get(0).getR();
        int indiceMejorModelo = 0;
        int indiceMayorR = 0;
        
        for (int i=1; i<listaModelos.size(); i++)
        {
            if (listaModelos.get(i).getSxy()<SxyMenor)
            {
                SxyMenor = listaModelos.get(i).getSxy();
                indiceMejorModelo = i;
            }
        }

        return listaModelos.get(indiceMejorModelo);
    }
}
