package co.edu.javeriana.ciclipointer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import entities.MisRuta;

public class MisRutasActivity extends AppCompatActivity {

    private List<String> arreglo = new ArrayList<>();
    private List<MisRuta> rutas = new ArrayList<>();
    private ListView listView;
    private FirebaseAuth mAuth;
    private FirebaseUser user = null;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_rutas);


         adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arreglo);

        listView = (ListView)findViewById(R.id.listMisRutas);
        listView.setAdapter(adapter);

        mAuth =	FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        llenarLista();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getBaseContext(), NuevoProgramadoActivity.class);
                intent.putExtra("latOrigen",rutas.get(i).getLatOrigen());
                intent.putExtra("longOrigen",rutas.get(i).getLongOrigen());
                intent.putExtra("latDesti",rutas.get(i).getLatDestino());
                intent.putExtra("longDesti",rutas.get(i).getLongDestino());
                intent.putExtra("ruta",rutas.get(i).getRuta());
                intent.putExtra("tipo","Persona");
                startActivity(intent);
            }
        });
    }

    private void llenarLista(){
        myRef.child("rutas/"+user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot s: dataSnapshot.getChildren()){
                    MisRuta m = s.getValue(MisRuta.class);
                    rutas.add(m);
                    String valor = "Origen: "+m.getOrigen()+".\n "+
                            "Destino: "+m.getDestino()+".";
                    arreglo.add(valor);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
