package co.edu.javeriana.ciclipointer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NuevoGrupalDetalleActivity extends AppCompatActivity {

    private TextView Opcion;
    private Button bInvitarAmigos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_grupal_detalle);

        Opcion = (TextView) findViewById(R.id.textViewMrutaGrupal);
        Opcion.setText(getIntent().getStringExtra("rutaGrupal"));
        bInvitarAmigos = (Button) findViewById(R.id.buttonInvitarAmigos);
        bInvitarAmigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AmigosGrupalActivity.class);
                startActivity(intent);
            }
        });
    }
}
