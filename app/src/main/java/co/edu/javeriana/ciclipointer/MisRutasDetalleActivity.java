package co.edu.javeriana.ciclipointer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MisRutasDetalleActivity extends AppCompatActivity {

    private Button bUsar;
    private Button bEliminar;
    private TextView Ruta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_rutas_detalle);

        Ruta = (TextView) findViewById(R.id.textViewMruta);
        Ruta.setText(getIntent().getStringExtra("ruta"));
        bUsar = (Button) findViewById(R.id.buttonUsarMr); // al usar puede empezar ya o programarla
        bEliminar = (Button) findViewById(R.id.buttonEliminarMr);
    }
}
