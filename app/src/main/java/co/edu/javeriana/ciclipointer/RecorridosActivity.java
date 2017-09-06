package co.edu.javeriana.ciclipointer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class RecorridosActivity extends AppCompatActivity {

    private List<String> arreglo = new ArrayList<>();
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorridos);

        llenarLista();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arreglo);

        listView = (ListView)findViewById(R.id.LRecorridos);
        listView.setAdapter(adapter);
      /*  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), ReccoridoDetalleActivity.class);
                intent.putExtra("todo",arreglo.get(position));
                startActivity(intent);
            }
        });*/
    }

    private void llenarLista(){
        for(int i = 0; i < 30 ; i++){
            arreglo.add("Recorrido número "+i+ " el detalle: Punto incio - fin; tiempo transcurrido; " +
                    "fecha; kilómetros; informe de clima ");
        }
    }
}
