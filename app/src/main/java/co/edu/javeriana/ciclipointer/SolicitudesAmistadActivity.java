package co.edu.javeriana.ciclipointer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

import entities.SolicitudAmistad;
import entities.Usuario;

public class SolicitudesAmistadActivity extends AppCompatActivity {

    private ArrayList<Usuario> arreglo = new ArrayList<>();
    private ListView listView;
    private FirebaseAuth mAuth;
    private FirebaseUser user = null;
    private FirebaseDatabase database;
    private DatabaseReference myRef,ref2;
    private ProgressDialog mProgressDialog;
    private ArrayList<String> soli = new ArrayList<String>();
    private boolean exi = true;

    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitudes_amistad);

        mAuth =	FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        ref2=database.getReference();
        listView = (ListView) findViewById(R.id.listSolicitudes);

        mProgressDialog = new ProgressDialog(this);

        adapter = new UserAdapter(SolicitudesAmistadActivity.this,arreglo);

        cargarDatos();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getBaseContext(), UsuarioDetalleActivity.class);
                intent.putExtra("llave",arreglo.get(i).getRH());
                intent.putExtra("nombre",arreglo.get(i).getNombre());
                intent.putExtra("correo",arreglo.get(i).getCorreo());
                intent.putExtra("solicitar",false);
                startActivity(intent);
            }
        });
    }



    private void cargarDatos(){
        mProgressDialog.setTitle("Cargando...");
        mProgressDialog.setMessage("Cargando foto del servidor");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        arreglo.clear();
        listView.setAdapter(adapter);
        soli.clear();
        myRef.child("solicitudes/"+user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                exi = true;
                for	(DataSnapshot singleSnapshot :	dataSnapshot.getChildren()) {
                    soli = (ArrayList<String>) singleSnapshot.getValue();
                    for(String id:soli){
                          ref2.child("users/"+id).addListenerForSingleValueEvent(new ValueEventListener() {
                              @Override
                              public void onDataChange(DataSnapshot dataSnapshot) {

                                  Usuario user = dataSnapshot.getValue(Usuario.class);
                                  user.setRH(dataSnapshot.getKey().toString());
                                  boolean esta = false;
                                  for(Usuario v:arreglo){
                                      if(v.getCorreo().equals(user.getCorreo()))
                                          esta = true;
                                  }
                                  if(!esta) {
                                      arreglo.add(user);
                                      listView.setAdapter(adapter);
                                  }
                              }

                              @Override
                              public void onCancelled(DatabaseError databaseError) {
                                  Toast.makeText(SolicitudesAmistadActivity.this, "Error cargando datos intentelo de nuevo",
                                          Toast.LENGTH_LONG).show();
                              }
                          });
                    }

                }
                mProgressDialog.dismiss();
                if(!dataSnapshot.hasChildren()){
                    Toast.makeText(SolicitudesAmistadActivity.this, "No tiene solicitudes", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SolicitudesAmistadActivity.this, "ERROR enviando solicitud "+databaseError
                        .getMessage().toString(), Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth =	FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        ref2=database.getReference();
        cargarDatos();
    }
}
