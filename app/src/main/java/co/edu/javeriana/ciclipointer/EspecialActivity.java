package co.edu.javeriana.ciclipointer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class EspecialActivity extends AppCompatActivity {

    private Button alto, comelona, joven, romantica;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_especial);

        alto = (Button) findViewById(R.id.buttonAlto);
        comelona = (Button) findViewById(R.id.button3);
        joven = (Button) findViewById(R.id.button5);
        romantica = (Button) findViewById(R.id.button6);

        alto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intet = new Intent(getApplicationContext(),RutaEspecialActivity.class);
                intet.putExtra("tipo","Alto Rendimiento");
                startActivity(intet);
            }
        });

        comelona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intet = new Intent(getApplicationContext(),RutaEspecialActivity.class);
                intet.putExtra("tipo","Comelona Usaquen");
                startActivity(intet);
            }
        });

        joven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intet = new Intent(getApplicationContext(),RutaEspecialActivity.class);
                intet.putExtra("tipo","Jovenes en Acción");
                startActivity(intet);
            }
        });

        romantica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intet = new Intent(getApplicationContext(),RutaEspecialActivity.class);
                intet.putExtra("tipo","Romantica");
                startActivity(intet);
            }
        });
    }
}
