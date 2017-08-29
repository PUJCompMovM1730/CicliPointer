package co.edu.javeriana.ciclipointer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RutasGruActivity extends AppCompatActivity {

    Button especial,normal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutas_gru);
        especial = (Button) findViewById(R.id.B_R_Especial);
        especial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RutasUsActivity.class);
                startActivity(intent);
            }
        });

        normal = (Button) findViewById(R.id.B_R_Normal);
        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RutasUsActivity.class);
                startActivity(intent);
            }
        });
    }
}
