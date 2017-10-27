package co.edu.javeriana.ciclipointer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ViajeActivity extends AppCompatActivity {

    private Button Bopciones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viaje);

        Bopciones = (Button) findViewById(R.id.buttonOpciones);
        Bopciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ProgramarViajeActivity.class);
                startActivity(intent);
            }
        });
    }
}
