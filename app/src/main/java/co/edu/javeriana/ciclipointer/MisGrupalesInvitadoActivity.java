package co.edu.javeriana.ciclipointer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import entities.RutaGrupal;
import entities.RutaProgramada;
import entities.Usuario;

public class MisGrupalesInvitadoActivity extends AppCompatActivity {

    private TextView tipo,fecha;
    private List<String> arreglo = new ArrayList<>();
    private ListView listView;
    private Button aceptar,rechazar;
    private DatabaseReference myRef,ref2;
    private FirebaseAuth mAuth;
    private FirebaseUser user = null;
    private RutaGrupal ru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_grupales_invitado);
        tipo = (TextView) findViewById(R.id.textViewTipoInvitado);
        fecha = (TextView) findViewById(R.id.textViewFechaInvitado);


        mAuth =	FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();
        ref2 = FirebaseDatabase.getInstance().getReference();

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arreglo);

        listView = (ListView)findViewById(R.id.listAmigosConfirmadosInvitado);
        listView.setAdapter(adapter);

        aceptar = (Button) findViewById(R.id.buttonAceptar);
        rechazar= (Button) findViewById(R.id.buttonRechazar);

        final String keyGru = getIntent().getStringExtra("grupal");
        final String keyAnfi = getIntent().getStringExtra("anfitrion");


        myRef.child("grupales/"+keyAnfi+"/"+keyGru)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ru = dataSnapshot.getValue(RutaGrupal.class);
                        tipo.setText("Tipo: "+ru.getTipoGru());
                        fecha.setText("Fecha: "+ru.getFecha()+"\nHora: "+ru.getHora());
                        for(String k:ru.getInvitados()){
                            ref2.child("users/"+k).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Usuario us = dataSnapshot.getValue(Usuario.class);
                                    arreglo.add(us.getNombre());
                                    listView.setAdapter(adapter);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MisGrupalesInvitadoActivity.this, "Error cargando datos", Toast.LENGTH_SHORT).show();
                    }
                });

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    eliminarSolicitud();
                    crearProgramado();
                //creamos programado con key
                // agregamos keyus y key prog a lista grupal
            }
        });

        rechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarSolicitud();
                Toast.makeText(MisGrupalesInvitadoActivity.this, "Se ha eliminado", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),InicioActivity.class));
            }
        });
    }

    private void eliminarSolicitud(){
        String key = getIntent().getStringExtra("key");
        myRef.child("solicitudGrupal/"+user.getUid()+"/"+key).removeValue();
    }

    private void crearProgramado(){
        RutaProgramada ruta = new RutaProgramada();
        ruta.setFecha(ru.getFecha());
        ruta.setHora(ru.getHora());
        ruta.setTipo("Programado-Grupal");
        ruta.setLatOrigen(ru.getLatOrigen());
        ruta.setLongOrigen(ru.getLongOrigen());
        ruta.setLatDestino(ru.getLatDestino());
        ruta.setLongDestino(ru.getLongDestino());
        ruta.setRuta(ru.getRuta());
        ruta.setKeyGrupal(getIntent().getStringExtra("grupal"));
        ruta.setKeyAnfitrion(getIntent().getStringExtra("anfitrion"));

        final String key = myRef.child("programados/"+user.getUid()).push().getKey();
        myRef.child("programados/"+user.getUid()+"/"+key)
                .setValue(ruta).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ref2.child("grupales/"+getIntent().getStringExtra("anfitrion")+"/"+getIntent().getStringExtra("grupal"))
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ru = dataSnapshot.getValue(RutaGrupal.class);
                                List<String> confirmados = ru.getConfirmados();
                                List<String> keys = ru.getProgramadosConfirmados();
                                if(confirmados==null && keys==null){
                                   confirmados = new ArrayList<String>();
                                    keys = new ArrayList<String>();
                                    confirmados.add(user.getUid());
                                    keys.add(key);
                                }else{
                                    confirmados.add(user.getUid());
                                    keys.add(key);
                                }
                                ru.setConfirmados(confirmados);
                                ru.setProgramadosConfirmados(keys);
                                actualizarRutaGrupal(ru);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(MisGrupalesInvitadoActivity.this, "Error actualizando datos", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });
    }

    private void actualizarRutaGrupal(RutaGrupal ru) {
        myRef.child("grupales/"+getIntent().getStringExtra("anfitrion")+"/"+getIntent().getStringExtra("grupal"))
                .setValue(ru).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MisGrupalesInvitadoActivity.this, "Se ha confirmado", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),InicioActivity.class));
            }
        });
    }

}
