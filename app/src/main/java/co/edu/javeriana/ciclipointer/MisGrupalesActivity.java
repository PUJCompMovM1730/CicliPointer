package co.edu.javeriana.ciclipointer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
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

import entities.InvitacionGrupal;
import entities.RutaGrupal;
import entities.RutaProgramada;

public class MisGrupalesActivity extends AppCompatActivity {

    private List<String> arreglo = new ArrayList<>();
    private List<String> keys= new ArrayList<>();
    private List<InvitacionGrupal>invitaciones = new ArrayList<>();
    private ListView listView;
    private FirebaseAuth mAuth;
    private FirebaseUser user = null;
    private FirebaseDatabase database;
    private DatabaseReference myRef,ref2;
    private ArrayAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_grupales);
        listView = (ListView)findViewById(R.id.list_mis_grupales);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, arreglo);

        mAuth =	FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        ref2 = database.getReference();

        myRef.child("solicitudGrupal/" + user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot s: dataSnapshot.getChildren()){
                    final InvitacionGrupal in = s.getValue(InvitacionGrupal.class);
                    invitaciones.add(in);
                    keys.add(s.getKey());
                    ref2.child("grupales/"+in.getAnfritrion()+"/"+in.getViajeGrupal())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            RutaGrupal ru = dataSnapshot.getValue(RutaGrupal.class);
                            String valor = "Anfitrión: "+in.getNombre()+".\n " +
                                    "Fecha: "+ru.getFecha()+".\n " +
                                    "Hora: "+ru.getHora()+".\n "+
                                    "Tipo: "+ru.getTipoGru()+".\n "+
                                    "(Presione para más información).";
                            arreglo.add(valor);
                            listView.setAdapter(adapter);

                            if(!dataSnapshot.hasChildren()){
                                Toast.makeText(MisGrupalesActivity.this, "No tiene solicitudes", Toast.LENGTH_SHORT).show();
                            }

                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(MisGrupalesActivity.this, "Error cargando invitación", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MisGrupalesActivity.this, "Error cargando información", Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),MapInfoActivity.class);
                intent.putExtra("anfitrion",invitaciones.get(i).getAnfritrion());
                intent.putExtra("grupal",invitaciones.get(i).getViajeGrupal());
                intent.putExtra("key",keys.get(i));
                startActivity(intent);
            }
        });
    }


}
