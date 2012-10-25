/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author HP G42
 */
@XmlRootElement
public class EscenarioModificado extends Escenario implements Cloneable {
    
    public static int ESCENARIO_NORMAL =0;
    public static int ESCENARIO_PESIMISTA=1;
    public static int ESCENARIO_OPTIMISTA=2;
    
    private double tasaIncrementoIngresos;
    private double tasaIncrementoCostos;
    private double tasaDisminucionIngresos;
    private double tasaDisminucionCostos;
    private int tipoEscenario;
    
    public EscenarioModificado(Escenario base){
        super(base);
        this.crearGastosEInversiones(base);
    }
    
    public EscenarioModificado(EscenarioModificado base){
        super((Escenario)base);
        this.crearGastosEInversiones((Escenario)base);
        
        this.tasaDisminucionCostos = base.tasaDisminucionCostos;
        this.tasaDisminucionIngresos = base.tasaDisminucionIngresos;
        this.tasaIncrementoCostos = base.tasaIncrementoCostos;
        this.tasaIncrementoIngresos = base.tasaIncrementoIngresos;
        this.tipoEscenario = base.tipoEscenario;
    }
    
    public void setTasaIncrementoIngresos(double tasa){
        this.tasaIncrementoIngresos = tasa;
    }
    public double getTasaIncrementoIngresos(){
        return this.tasaIncrementoIngresos;
    }
    public void setTasaIncrementoCostos(double tasa){
        this.tasaIncrementoCostos = tasa;
    }
    public double getTasaIncrementoCostos(){
        return this.tasaIncrementoCostos;
    }
    
    public void setTasaDisminucionIngresos(double tasa){
        this.tasaDisminucionIngresos = tasa;
    }
    public double getTasaDisminucionIngresos(){
        return this.tasaDisminucionIngresos;
    }
    
    public void setTasaDisminucionCostos(double tasa){
        this.tasaDisminucionCostos = tasa;
    }
    public double getTasaDisminucionCostos(){
        return this.tasaDisminucionCostos;
    }
    public void setTipoEscenario(int tipo){
        this.tipoEscenario = tipo;
    }
    public int tipoEscenario(){
        return this.tipoEscenario;
    }

    public void plantearEscenario() {
        if (this.tipoEscenario==EscenarioModificado.ESCENARIO_OPTIMISTA){
            double [] ingresos = this.modeloIngresos.getIngresosActuales();
            double [] costos = this.modeloCostos.getCostosActuales();
            for (int i=0; i<ingresos.length; i++){
                ingresos[i] = ingresos[i] + (ingresos[i] * this.tasaIncrementoIngresos);
                costos[i] = costos[i] - (costos[i]*this.tasaDisminucionCostos);
            }
            this.modeloIngresos.setListaIngresosManualmente(ingresos);
            this.modeloCostos.setCostos(costos);
        }
        else if (this.tipoEscenario==EscenarioModificado.ESCENARIO_PESIMISTA){
            double [] ingresos = this.modeloIngresos.getIngresosActuales();
            double [] costos = this.modeloCostos.getCostosActuales();
            for (int i=0; i<ingresos.length; i++){
                ingresos[i] = ingresos[i] - (ingresos[i] * this.tasaDisminucionIngresos);
                costos[i] = costos[i] + (costos[i]*this.tasaIncrementoCostos);
            }
            this.modeloIngresos.setListaIngresosManualmente(ingresos);
            this.modeloCostos.setCostos(costos);
        }
        this.recalcularTodo();
    }
    
    
    @Override
    public void serializarAXML(String direccion){
        FileOutputStream archivo = null;
        try {
            JAXBContext context = JAXBContext.newInstance(EscenarioModificado.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            archivo = new FileOutputStream(direccion);            
            //guardamos el objeto serializado en un documento XML
            marshaller.marshal(this, archivo);
             
            archivo.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EscenarioModificado.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EscenarioModificado.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(EscenarioModificado.class.getName()).log(Level.SEVERE, null, ex);
        }
 
        
    }
    
    public static EscenarioModificado deserializarDeXML(String direccion){
        EscenarioModificado escenario = null;
        try {
            JAXBContext context = JAXBContext.newInstance(EscenarioModificado.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            //Deserealizamos a partir de un documento XML
            escenario = (EscenarioModificado) unmarshaller.unmarshal(new File(direccion));            
        } catch (JAXBException ex) {
            Logger.getLogger(EscenarioModificado.class.getName()).log(Level.SEVERE, null, ex);
        }
        return escenario;
    }
}
