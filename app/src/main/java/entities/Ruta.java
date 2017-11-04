package entities;

/**
 * Created by Felipe on 4/11/2017.
 */

public class Ruta {
    private double latOrigen;
    private double longOrigen;
    private double latDestino;
    private double longDestino;
    private int ruta;

    public Ruta() {
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
}
