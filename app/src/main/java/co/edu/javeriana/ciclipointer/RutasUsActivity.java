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

public class RutasUsActivity extends AppCompatActivity {

    List<String> arreglo = new ArrayList<>();
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutas_us);

        llenarLista();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arreglo);

        listView = (ListView)findViewById(R.id.lista_misRutas);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getBaseContext(), RutaDetalleUsActivity.class);
                intent.putExtra("amigo",arreglo.get(position));
                startActivity(intent);
            }
        });
    }
// si viene directo muestra sus rutas, si viene de grupales muertra rutas compartidas
    private void llenarLista(){
        for(int i = 0; i < 30 ; i++){
            arreglo.add("Usuario dueño - Rutas "+i+" - Inicio desde Banderas - Destino Javeriana");
        }
    }
}
