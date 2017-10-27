package co.edu.javeriana.ciclipointer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ProgramarViajeActivity extends AppCompatActivity {

    private List<String> arreglo = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programar_viaje);

        // cambiar, que programe un viaje
        llenarLista();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arreglo);

        listView = (ListView)findViewById(R.id.listOpcionesViaje);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getBaseContext(), ViajeProgramadoActivity.class);
                intent.putExtra("opción",arreglo.get(i));
                startActivity(intent);
            }
        });
    }

    private void llenarLista(){
        for(int i = 0; i < 30 ; i++){
            arreglo.add("Opción "+i);
        }
    }
}
