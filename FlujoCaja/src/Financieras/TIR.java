/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Financieras;

/**
 *
 * @author Jose Carlos Palma
 */
public class TIR {

    private VAN van;

    public TIR(VAN van) {
        this.van = van;
    }

    /**
     *
     * @return
     */
    public VAN getVan() {
        return this.van;
    }

    /**
     *
     * @param van
     */
    public void setVan(VAN van) {
        this.van = van;
    }

    /**
     * Calcula el error relativo entre:<br>
     * <CODE>
     * | TIR<sub>n+1</sub> + TIR<sub>n</sub> |<br>
     * ------------------<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| TIR<sub>n+1</sub> |
     * </CODE>
     *
     * @param tir1 es TIR<sub>n+1</sub>
     * @param tir2 es TIR<sub>n</sub>
     * @return
     */
    public double getError(double tir1, double tir2) {
        return (tir1 - tir2) / tir1;
    }
    
    /**
     * Calcula la TIR utilizando el metodo Newton-Raphson
     * @return la tasa interna de retorno.
     */
    public double getTIR() {
        double tir1 = 0.0;
        
        while(true) {
            double tir2 = tir1;
            double van1 = van.getVan(tir2);
            double van2 = van.getVanPrima(tir2);
            
            tir1 = tir2 - van1 / van2;
            
            //System.out.printf("%1$+E15  %2$+E15  %3$+E15  %4$+E15  %5$+E18\n", tir2, van1, van2, tir1, er);
            
            if ( Math.round(tir1 * 10000) == Math.round(tir2 *10000)) break;
            
        }
        
        
        return tir1;
    }
}
