package entities;

import java.util.List;

/**
 * Created by Felipe on 28/10/2017.
 */

public class RecorridoUsuario {
    private List<Recorrido> recorridos;

    public RecorridoUsuario() {
    }

    public List<Recorrido> getRecorridos() {
        return recorridos;
    }

    public void setRecorridos(List<Recorrido> recorridos) {
        this.recorridos = recorridos;
    }
}
