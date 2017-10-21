package co.edu.javeriana.ciclipointer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class NuevaActivity extends AppCompatActivity {

    private Button bYa;
    private Button bProgramar;
    private Button bGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva);

        bYa = (Button) findViewById(R.id.buttonUsarMr);
        bProgramar = (Button) findViewById(R.id.buttonEliminarMr); // se debe seleccionar Hora/Fecha de inicio
        bGuardar = (Button) findViewById(R.id.buttonGuardar); // solo aparece al llegar al destino
        // map mostrar usuarios cercanos a 1km y ver como guardar recorrido
    }
}
