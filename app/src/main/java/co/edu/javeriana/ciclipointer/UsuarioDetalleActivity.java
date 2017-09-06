package co.edu.javeriana.ciclipointer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class UsuarioDetalleActivity extends AppCompatActivity {

    private TextView nombreUs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_detalle);

        nombreUs = (TextView) findViewById(R.id.DnombreUs);
        nombreUs.setText(getIntent().getStringExtra("amigo"));
    }
}
