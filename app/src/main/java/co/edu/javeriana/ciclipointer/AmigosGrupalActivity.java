package co.edu.javeriana.ciclipointer;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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
import entities.Usuario;

public class AmigosGrupalActivity extends AppCompatActivity {

    private ArrayList<Usuario> arreglo = new ArrayList<>();
    private ListView listView;
    private FirebaseAuth mAuth;
    private FirebaseUser user = null;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private UserAdapter adapter;
    private String keyPri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amigos_grupal);

        mAuth =	FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        adapter = new UserAdapter(AmigosGrupalActivity.this,arreglo);


        listView = (ListView)findViewById(R.id.list_amigos_grupal);
        buscarAmigos();
        listView.setAdapter(adapter);

        listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Usuario item = (Usuario) adapterView.getItemAtPosition(i);
                Toast.makeText(getApplicationContext(),item.getNombre(),Toast.LENGTH_LONG).show();
                listView.setItemChecked(i,true);
                TextView tvName = (TextView) view.findViewById(R.id.nombre);
                tvName.setTextColor(Color.RED);
            }
        });


    }



    public void showSelected(View view) {
        SparseBooleanArray seleccionados = listView.getCheckedItemPositions();
        if(seleccionados==null || seleccionados.size()==0){
            Toast.makeText(this, "No hay elementos seleccionados", Toast.LENGTH_SHORT).show();
        }else{
            //si selecciono almenos uno, aca se guarda todo
            crearRutaGrupal(seleccionados);
            //StringBuilder resultado=new StringBuilder();
            //resultado.append("Se seleccionaron los siguientes elementos:\n");
        }
    }



    public void cancelar(View view){
        Intent intent = new Intent(this,InicioActivity.class);
        startActivity(intent);
    }


    private void crearRutaGrupal(final SparseBooleanArray seleccionados){
        RutaGrupal ru = new RutaGrupal();
        ru.setRuta(getIntent().getIntExtra("ruta",0));
        ru.setFecha(getIntent().getStringExtra("fecha"));
        ru.setHora(getIntent().getStringExtra("hora"));
        ru.setTipo(getIntent().getStringExtra("tipo"));
        ru.setTipoGru(getIntent().getStringExtra("tipoGrup"));
        ru.setLatOrigen(getIntent().getDoubleExtra("latOri",0));
        ru.setLongOrigen(getIntent().getDoubleExtra("longOri",0));
        ru.setLatDestino(getIntent().getDoubleExtra("latDesti",0));
        ru.setLongDestino(getIntent().getDoubleExtra("longDesti",0));
        List<String>us = new ArrayList<>();
        final int size=seleccionados.size();
        for (int i=0; i<size; i++){
            if (seleccionados.valueAt(i)){
                us.add(arreglo.get(seleccionados.keyAt(i)).getRH().toString());
            }
        }
        ru.setInvitados(us);
        ru.setConfirmados(new ArrayList<String>());
        keyPri = myRef.child("grupales/"+user.getUid()).push().getKey();
        myRef.child("grupales/"+user.getUid()+"/"+keyPri)
                .setValue(ru).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                RutaProgramada ru = new RutaProgramada();
                ru.setFecha(getIntent().getStringExtra("fecha"));
                ru.setHora(getIntent().getStringExtra("hora"));
                ru.setTipo(getIntent().getStringExtra("tipo"));
                ru.setLatOrigen(getIntent().getDoubleExtra("latOri",0));
                ru.setLongOrigen(getIntent().getDoubleExtra("longOri",0));
                ru.setLatDestino(getIntent().getDoubleExtra("latDesti",0));
                ru.setLongDestino(getIntent().getDoubleExtra("longDesti",0));
                ru.setRuta(getIntent().getIntExtra("ruta",0));
                ru.setKeyGrupal(keyPri);
                ru.setKeyAnfitrion(user.getUid());
                crearRutaProgramada(ru,seleccionados);
            }
        });
    }

    private void crearRutaProgramada(RutaProgramada ru, final SparseBooleanArray  seleccionados){
        String key = myRef.child("programados/"+user.getUid()).push().getKey();
        myRef.child("programados/"+user.getUid()+"/"+key)
                .setValue(ru).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                final int size=seleccionados.size();
                for (int i=0; i<size; i++){
                    if (seleccionados.valueAt(i)){
                        crearSolicitud(arreglo.get(seleccionados.keyAt(i)));
                    }
                }
                Toast.makeText(AmigosGrupalActivity.this, "Se ha guardado el recorrido", Toast.LENGTH_LONG).show();
                Intent in = new Intent(getApplicationContext(),InicioActivity.class);
                startActivity(in);
            }
        });
    }

    private void crearSolicitud(Usuario usuario) {
        InvitacionGrupal in = new InvitacionGrupal();
        in.setAnfritrion(user.getUid());
        in.setViajeGrupal(keyPri);
        in.setNombre(user.getDisplayName().toUpperCase());
        String key = myRef.child("solicitudGrupal/" + usuario.getRH()).push().getKey();
        myRef.child("solicitudGrupal/" + usuario.getRH()+"/"+key).setValue(in);
    }

    private void buscarAmigos() {

        myRef.child("amistades/" + user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot single : singleSnapshot.getChildren()){
                        myRef.child("users/"+single.getValue().toString())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        Usuario user = dataSnapshot.getValue(Usuario.class);
                                        user.setRH(dataSnapshot.getKey().toString());
                                            arreglo.add(user);
                                            listView.setAdapter(adapter);
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(AmigosGrupalActivity.this, "Error cargando datos intentelo de nuevo",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });

                    }
                }
                if(!dataSnapshot.hasChildren()){
                    Toast.makeText(AmigosGrupalActivity.this, "No tiene amigos", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AmigosGrupalActivity.this, "ERROR, cargando amigos " + databaseError
                        .getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
