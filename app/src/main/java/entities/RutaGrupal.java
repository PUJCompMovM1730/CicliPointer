package entities;

import java.util.List;

/**
 * Created by Felipe on 31/10/2017.
 */

public class RutaGrupal {
    private double latOrigen;
    private double longOrigen;
    private double latDestino;
    private double longDestino;
    private int ruta;
    private String fecha;
    private String hora;
    private String tipo;
    private String tipoGru;
    private List<String> invitados;
    private List<String> confirmados;

    public RutaGrupal() {
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

    public String getTipoGru() {
        return tipoGru;
    }

    public void setTipoGru(String tipoGru) {
        this.tipoGru = tipoGru;
    }

    public List<String> getInvitados() {
        return invitados;
    }

    public void setInvitados(List<String> invitados) {
        this.invitados = invitados;
    }

    public List<String> getConfirmados() {
        return confirmados;
    }

    public void setConfirmados(List<String> confirmados) {
        this.confirmados = confirmados;
    }
}
