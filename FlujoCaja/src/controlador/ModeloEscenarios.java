/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HP G42
 */
public class ModeloEscenarios {
    
    private EscenarioModificado puntual;
    private EscenarioModificado pesimista;
    private EscenarioModificado optimista;

    public EscenarioModificado getOptimista() {
        return optimista;
    }

    public void setOptimista(EscenarioModificado optimista) {
        this.optimista = optimista;
    }

    public EscenarioModificado getPesimista() {
        return pesimista;
    }

    public void setPesimista(EscenarioModificado pesimista) {
        this.pesimista = pesimista;
    }

    public EscenarioModificado getPuntual() {
        return puntual;
    }

    public void setPuntual(EscenarioModificado puntual) {
        this.puntual = puntual;
    }
    
    public void serializarAXML(String direccion){
        FileOutputStream outputFile = null;
            try {
                outputFile = new FileOutputStream(direccion);
                // Relates the XML encoder with the output file stream
                XMLEncoder xe = new XMLEncoder(outputFile);
                // Serializes the selected object using an XML encoding
                xe.writeObject(this);
                // Closes the XML encoder
                xe.close();
                outputFile.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ModeloEscenarios.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ModeloEscenarios.class.getName()).log(Level.SEVERE, null, ex);
            }        
    }
    
    public static ModeloEscenarios deserializarDeXML(String direccion){
        ModeloEscenarios escenario = null;
        FileInputStream inputFile = null;
            try {
                inputFile = new FileInputStream(direccion);
                // Relates the XML decoder with the input file stream
                XMLDecoder xd = new XMLDecoder(inputFile);
                // Reads the object from the stream and deserializes it using an XML decoding
                escenario = (ModeloEscenarios) xd.readObject();
                // Closes the XML decoder
                xd.close();
                inputFile.close();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ModeloEscenarios.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(ModeloEscenarios.class.getName()).log(Level.SEVERE, null, ex);
            }
        return escenario;
    }
    
}
