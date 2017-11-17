package co.edu.javeriana.ciclipointer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class InicioTurismo extends AppCompatActivity {

    private Button mapa, marcador,salir;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_turismo);

        mAuth =	FirebaseAuth.getInstance();
        mapa = (Button) findViewById(R.id.buttonProTu);
        marcador = (Button) findViewById(R.id.buttonMarTu);
        salir = (Button) findViewById(R.id.buttonProsalir);

        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplication(),ProgramarViajeTurismoActivity.class));
            }
        });

        marcador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplication(),ProgrmarMarcadorActivity.class));
            }
        });

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent	intent	=	new	Intent(InicioTurismo.this,	MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
