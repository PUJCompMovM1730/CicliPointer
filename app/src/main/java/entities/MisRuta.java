package entities;

/**
 * Created by Felipe on 31/10/2017.
 */

public class MisRuta {
    private double latOrigen;
    private double longOrigen;
    private double latDestino;
    private double longDestino;
    private int ruta;

    private String origen;
    private String destino;

    public MisRuta() {
    }

    public double getLatOrigen() {
        return latOrigen;
    }

    public void setLatOrigen(double latOrigen) {
        this.latOrigen = latOrigen;
    }

    public double getLongOrigen() {
        return longOrigen;
    }

    public void setLongOrigen(double longOrigen) {
        this.longOrigen = longOrigen;
    }

    public double getLatDestino() {
        return latDestino;
    }

    public void setLatDestino(double latDestino) {
        this.latDestino = latDestino;
    }

    public double getLongDestino() {
        return longDestino;
    }

    public void setLongDestino(double longDestino) {
        this.longDestino = longDestino;
    }

    public int getRuta() {
        return ruta;
    }

    public void setRuta(int ruta) {
        this.ruta = ruta;
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
