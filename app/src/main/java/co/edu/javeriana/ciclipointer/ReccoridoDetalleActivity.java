package co.edu.javeriana.ciclipointer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ReccoridoDetalleActivity extends AppCompatActivity {

    TextView detalle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reccorido_detalle);

        detalle = (TextView) findViewById(R.id.textDetalle);
        detalle.setText(getIntent().getStringExtra("todo"));

    }
}
