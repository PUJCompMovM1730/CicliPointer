package entities;


/**
 * Created by Felipe on 28/10/2017.
 */

public class Recorrido {

    private double km;
    private String origen;
    private String destino;
    private int tiempo;
    private String tipo;
    private String horaInicio;
    private String fechaInicio;

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Recorrido() {
    }

    public double getKm() {
        return km;
    }

    public void setKm(double km) {
        this.km = km;
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

    public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "- Origen: "+origen+
                ". \n - Destino: "+destino+
                ". \n - Tiempo : "+tiempo+" min"+
                ". \n - Tipo: "+tipo+
                ". \n - Km: "+km+
                ". \n - Fecha: "+fechaInicio+
                ". \n - Hora: "+horaInicio+".";
    }
}

