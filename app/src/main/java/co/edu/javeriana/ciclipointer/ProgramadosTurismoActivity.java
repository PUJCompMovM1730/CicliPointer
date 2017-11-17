package co.edu.javeriana.ciclipointer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import entities.RutaProgramada;

public class ProgramadosTurismoActivity extends AppCompatActivity {

    private List<String> arreglo = new ArrayList<>();
    private List<RutaProgramada> rutas = new ArrayList<>();
    private List<String> keys = new ArrayList<>();
    private ListView listView;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user = null;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programados_turismo);
        mAuth =	FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();

        llenarLista();
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, arreglo);

        listView = (ListView)findViewById(R.id.LProgramados);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RutaProgramada ru = rutas.get(i);
                Intent intent = new Intent(ProgramadosTurismoActivity.this, ProgramadosTurismoDetalleActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("latOrigen",ru.getLatOrigen());
                intent.putExtra("longOrigen",ru.getLongOrigen());
                intent.putExtra("latDesti",ru.getLatDestino());
                intent.putExtra("longDesti",ru.getLongDestino());
                intent.putExtra("ruta",ru.getRuta());
                intent.putExtra("tipo",ru.getTipo());
                intent.putExtra("fecha",ru.getFecha());
                intent.putExtra("hora",ru.getHora());
                startActivity(intent);
            }
        });
    }

    private void llenarLista(){
        myRef.child("turismo programadas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    RutaProgramada ru = singleSnapshot.getValue(RutaProgramada.class);
                    rutas.add(ru);
                    String valor = " Tipo: "+ru.getTipo()+".\n " +
                            "Fecha: "+ru.getFecha()+".\n " +
                            "Hora: "+ru.getHora()+".\n " +
                            "(Presione para más información).";
                    arreglo.add(valor);
                    keys.add(singleSnapshot.getKey());
                }
                listView.setAdapter(adapter);
                if (!dataSnapshot.hasChildren()) {
                    Toast.makeText(ProgramadosTurismoActivity.this, "No existen viajes de turismo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProgramadosTurismoActivity.this, "ERROR descargando de la base de datos" + databaseError
                        .getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
