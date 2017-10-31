package entities;

/**
 * Created by Felipe on 30/10/2017.
 */

public class RutaProgramada {
    private double latOrigen;
    private double longOrigen;
    private double latDestino;
    private double longDestino;
    private int ruta;
    private String fecha;
    private String hora;
    private String tipo;
    private String keyGrupal;
    private String keyAnfitrion;

    public RutaProgramada() {
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

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getKeyGrupal() {
        return keyGrupal;
    }

    public void setKeyGrupal(String keyGrupal) {
        this.keyGrupal = keyGrupal;
    }

    public String getKeyAnfitrion() {
        return keyAnfitrion;
    }

    public void setKeyAnfitrion(String keyAnfitrion) {
        this.keyAnfitrion = keyAnfitrion;
    }
}