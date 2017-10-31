package co.edu.javeriana.ciclipointer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
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

public class MisGrupalesDetalleActivity extends AppCompatActivity {

    private List<String> arreglo = new ArrayList<>();
    private List<String> arreglo2 = new ArrayList<>();
    private List<String> keys = new ArrayList<>();
    private RadioButton RBi,RBc;
    private FirebaseAuth mAuth;
    private FirebaseUser user = null;
    private FirebaseDatabase database;
    private DatabaseReference myRef,ref2;
    private ArrayAdapter<String> adapter,adapter2;
    private ListView listView;
    private RutaGrupal ru;
    private TextView tipo,fecha,anfi;
    private Button eliminar;
    private List<String> eli = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_grupales_detalle);

        mAuth =	FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        ref2 = database.getReference();

        RBi = (RadioButton) findViewById(R.id.radioButtonInvitados);
        RBc = (RadioButton) findViewById(R.id.radioButtonConfirmados);
        listView = (ListView)findViewById(R.id.listAmigosConfirmadosInvitado);
        tipo = (TextView) findViewById(R.id.textViewTipoInvitado);
        fecha = (TextView) findViewById(R.id.textViewFechaInvitado);
        anfi = (TextView) findViewById(R.id.textViewAnfi);
        eliminar = (Button) findViewById(R.id.buttonEliminar);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arreglo);

        adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, arreglo2);

        cargarInfo();

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getIntent().getStringExtra("anfitrion").equals(user.getUid())){
                    eliminarProgramada();
                    eliminarGrupal();
                    eliminarSolGrupal();
                    eliminarProgramados();
                    startActivity(new Intent(getApplicationContext(),InicioActivity.class));
                }else{
                    eliminarProgramada2();
                }
            }
        });

    }



    private void eliminarProgramada2(){
        myRef.child("programados/"+user.getUid()+"/"+getIntent().getStringExtra("key")).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        List<String> c = new ArrayList<String>();
                        List<String> k = new ArrayList<String>();
                        for(int i = 0; i <ru.getConfirmados().size();i++){
                            if(!ru.getConfirmados().get(i).equals(user.getUid())){
                                c.add(ru.getConfirmados().get(i));
                                k.add(ru.getProgramadosConfirmados().get(i));
                            }
                        }
                        if(c!=null&&c!=null){
                            ru.setProgramadosConfirmados(k);
                            ru.setConfirmados(c);
                            ref2.child("grupales/"+getIntent().getStringExtra("anfitrion")+"/"+getIntent().getStringExtra("grupal"))
                                    .setValue(ru).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MisGrupalesDetalleActivity.this, "Se cancelo recorrido.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(),InicioActivity.class));
                                }
                            });
                        }


                    }
                });
    }

    private void eliminarProgramada(){
        myRef.child("programados/"+user.getUid()+"/"+getIntent().getStringExtra("key")).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
    }

    private void eliminarGrupal(){
        myRef.child("grupales/"+getIntent().getStringExtra("anfitrion")+"/"+getIntent().getStringExtra("grupal"))
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });
    }

    private void eliminarProgramados() {
        for(final String s:ru.getConfirmados()){
            eli.clear();
            myRef.child("programados/"+s).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot single:dataSnapshot.getChildren()){
                        RutaProgramada in = single.getValue(RutaProgramada.class);
                        if(in.getKeyGrupal()!=null){
                            if(in.getKeyGrupal().equals(getIntent().getStringExtra("grupal")))
                                eli.add(single.getKey());
                        }
                    }
                    eliminandoPro(s);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void eliminarSolGrupal(){
        for(final String s:ru.getInvitados()){
            eli.clear();
            myRef.child("solicitudGrupal/"+s).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot single:dataSnapshot.getChildren()){
                        InvitacionGrupal in = single.getValue(InvitacionGrupal.class);
                        if(in.getAnfritrion().equals(user.getUid())){
                            eli.add(single.getKey());
                        }
                    }
                    eliminando(s);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void eliminando(String s) {
        for(String v:eli) {
            ref2.child("solicitudGrupal/" + s + "/"+v).removeValue();
        }
    }

    private void eliminandoPro(String s) {
        for(String v:eli) {
            ref2.child("programados/" + s + "/"+v).removeValue();
        }
    }
    public void onRadioButton(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radioButtonInvitados:
                if (checked){
                    listView.setAdapter(adapter2);
                    RBc.setChecked(false);
                    if(arreglo2.isEmpty()){
                        Toast.makeText(this, "No hay invitados", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case R.id.radioButtonConfirmados:
                if (checked){
                    listView.setAdapter(adapter);
                    RBi.setChecked(false);
                    if(arreglo.isEmpty()){
                        Toast.makeText(this, "No hay confirmados", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
        }
    }

    private void cargarInfo(){
        myRef.child("grupales/"+getIntent().getStringExtra("anfitrion")+"/"+getIntent().getStringExtra("grupal"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ru = dataSnapshot.getValue(RutaGrupal.class);
                        tipo.setText("Tipo: "+ru.getTipoGru());
                        fecha.setText("Fecha: "+ru.getFecha()+"\nHora: "+ru.getHora());
                        if(getIntent().getStringExtra("anfitrion").equals(user.getUid()))
                            anfi.setText("Anfitrión: "+user.getDisplayName().toUpperCase());
                        else{
                            ref2.child("users/"+getIntent().getStringExtra("anfitrion"))
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Usuario usu = dataSnapshot.getValue(Usuario.class);
                                    anfi.setText("Anfitrión: "+usu.getNombre());
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        if(ru.getInvitados()!=null){
                            for(String k:ru.getInvitados()){
                                ref2.child("users/"+k).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Usuario us = dataSnapshot.getValue(Usuario.class);
                                        arreglo2.add(us.getNombre());
                                       // listView.setAdapter(adapter2);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                        if(ru.getConfirmados()!=null && ru.getProgramadosConfirmados()!=null){
                            for(String k:ru.getConfirmados()){
                                ref2.child("users/"+k).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Usuario us = dataSnapshot.getValue(Usuario.class);
                                        arreglo.add(us.getNombre());
                                        // listView.setAdapter(adapter);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            keys = ru.getProgramadosConfirmados();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MisGrupalesDetalleActivity.this, "Error cargando datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
