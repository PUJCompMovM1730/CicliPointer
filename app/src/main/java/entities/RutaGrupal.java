package entities;

import java.util.List;

/**
 * Created by Felipe on 31/10/2017.
 */

public class RutaGrupal extends Ruta {

    private String fecha;
    private String hora;
    private String tipo;
    private String tipoGru;
    private List<String> invitados;
    private List<String> confirmados;
    private List<String> programadosConfirmados;

    public RutaGrupal() {
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

    public List<String> getProgramadosConfirmados() {
        return programadosConfirmados;
    }

    public void setProgramadosConfirmados(List<String> programadosConfirmados) {
        this.programadosConfirmados = programadosConfirmados;
    }
}
