package entities;

import java.util.List;

/**
 * Created by Felipe on 26/10/2017.
 */

public class SolicitudAmistad {

    private List<String> solicitantes;

    public SolicitudAmistad() {
    }


    public List<String> getSolicitantes() {
        return solicitantes;
    }

    public void setSolicitantes(List<String> solicitantes) {
        this.solicitantes = solicitantes;
    }
}
