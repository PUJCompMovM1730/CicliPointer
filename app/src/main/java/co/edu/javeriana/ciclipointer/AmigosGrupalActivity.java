package co.edu.javeriana.ciclipointer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AmigosGrupalActivity extends AppCompatActivity {

    private List<String> arreglo = new ArrayList<>();
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amigos_grupal);

        llenarLista();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, arreglo);

        listView = (ListView)findViewById(R.id.list_amigos_grupal);
        listView.setAdapter(adapter);
        listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = (String) adapterView.getItemAtPosition(i);
                Toast.makeText(getApplicationContext(),item,Toast.LENGTH_LONG).show();
            }
        });

    }

    private void llenarLista(){
        for(int i = 0; i < 30 ; i++){
            arreglo.add("Amigo"+i);
        }
    }

    public void showSelected(View view) {
        SparseBooleanArray seleccionados = listView.getCheckedItemPositions();
        if(seleccionados==null || seleccionados.size()==0){
            Toast.makeText(this, "No hay elementos seleccionados", Toast.LENGTH_SHORT).show();
        }else{
            //si selecciono almenos uno, aca se guarda todo
            StringBuilder resultado=new StringBuilder();
            resultado.append("Se seleccionaron los siguientes elementos:\n");
            final int size=seleccionados.size();
            for (int i=0; i<size; i++) {
                //Si valueAt(i) es true, es que estaba seleccionado
                if (seleccionados.valueAt(i)) {
                    resultado.append("El elemento "+seleccionados.keyAt(i)+" estaba seleccionado\n");
                }
            }
            Toast.makeText(this,resultado.toString(),Toast.LENGTH_LONG).show();
        }
    }

    public void cancelar(View view){
        Intent intent = new Intent(this,InicioActivity.class);
        startActivity(intent);
    }
}
