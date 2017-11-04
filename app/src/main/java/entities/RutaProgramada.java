package entities;

/**
 * Created by Felipe on 30/10/2017.
 */

public class RutaProgramada extends Ruta{

    private String fecha;
    private String hora;
    private String tipo;
    private String keyGrupal;
    private String keyAnfitrion;

    public RutaProgramada() {
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
