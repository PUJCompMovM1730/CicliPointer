package co.edu.javeriana.ciclipointer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProfileActivity extends AppCompatActivity {

    private Button Breccorridos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Breccorridos = (Button) findViewById(R.id.Brecorridos);
        Breccorridos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RecorridosActivity.class);
                startActivity(intent); // FALTAN DATOS PERFIL, PESO, ALTURA, EDAD
            }
        });
    }
}
