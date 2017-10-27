package co.edu.javeriana.ciclipointer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ViajeProgramadoActivity extends AppCompatActivity {

    private Button bYa;
    private Button bProgramar;
    private TextView Opcion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viaje_programado);

        // mapa viaje programado
        Opcion = (TextView) findViewById(R.id.textViewMruta);
        Opcion.setText(getIntent().getStringExtra("opción"));
        bYa = (Button) findViewById(R.id.buttonUsarMr);
        bProgramar = (Button) findViewById(R.id.buttonEliminarMr); // se debe seleccionar Hora/Fecha de inicio
    }
}
