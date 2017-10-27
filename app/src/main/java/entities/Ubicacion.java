package entities;

/**
 * Created by Felipe on 26/10/2017.
 */

public class Ubicacion {

    private Double latitud;
    private Double longitud;
    private String nombre;

    public Ubicacion() {
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
