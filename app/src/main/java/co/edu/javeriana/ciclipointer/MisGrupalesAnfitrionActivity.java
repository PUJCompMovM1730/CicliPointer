package co.edu.javeriana.ciclipointer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MisGrupalesAnfitrionActivity extends AppCompatActivity {

    private TextView Opcion;
    private List<String> arreglo = new ArrayList<>();
    private ListView listView;
    private Button VerMapa;
// PENSAR SI DEJAR EDITAR (SI MODIFICA DEBE NOTIFICAR AMIGOS INVITADOS)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_grupales_anfitrion);

        Opcion = (TextView) findViewById(R.id.textViewInfoAnfitrion);
        final String info = getIntent().getStringExtra("info");
        Opcion.setText(info);
        llenarLista();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arreglo);

        listView = (ListView)findViewById(R.id.listAmigosConfirmados);
        listView.setAdapter(adapter);
        VerMapa = (Button) findViewById(R.id.buttonVerMap);
        VerMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MapInfoActivity.class);
                intent.putExtra("infoo",info);
                startActivity(intent);
            }
        });

    }

    private void llenarLista(){
        for(int i = 0; i < 30 ; i++){
            arreglo.add("Nombre amigo "+i);
        }
    }

    public void volver(View view){
        Intent intent = new Intent(this,InicioActivity.class);
        startActivity(intent);
    }
}
