package co.edu.javeriana.ciclipointer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RutaDetalleUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruta_detalle_us);

        // si es desde mis rutas se comparte, si se compartio ver clasificacion
        // si es desde rutas sugerias se ppuede clasificar
        // también poner hora de salida 
    }
}
