package co.edu.javeriana.ciclipointer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import entities.Recorrido;
import entities.RecorridoUsuario;

public class RecorridosActivity extends AppCompatActivity {

    private List<String> arreglo = new ArrayList<>();
    private ListView listView;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user = null;
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorridos);

        mAuth =	FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();

        llenarLista();
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, arreglo);

        listView = (ListView)findViewById(R.id.LRecorridos);
        listView.setAdapter(adapter);
    }

    private void llenarLista(){
        myRef.child("recorridos/" + user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    String key = dataSnapshot.getKey();
                    //System.out.println("valor es key "+key);
                    RecorridoUsuario ru = dataSnapshot.getValue(RecorridoUsuario.class);
                    for(Recorrido r: ru.getRecorridos()){
                        arreglo.add(r.toString());
                    }
                }
                if (!dataSnapshot.hasChildren()) {
                    Toast.makeText(RecorridosActivity.this, "No tiene recorridos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RecorridosActivity.this, "ERROR descargando de la base de datos" + databaseError
                        .getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
