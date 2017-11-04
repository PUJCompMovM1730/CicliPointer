package entities;

/**
 * Created by Felipe on 31/10/2017.
 */

public class MisRuta extends Ruta{


    private String origen;
    private String destino;

    public MisRuta() {
    }


    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }
}
