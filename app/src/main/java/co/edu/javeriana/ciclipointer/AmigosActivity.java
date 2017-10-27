package co.edu.javeriana.ciclipointer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import entities.Usuario;

public class AmigosActivity extends AppCompatActivity {

    private ArrayList<Usuario> arreglo = new ArrayList<>();
    private ArrayList<Usuario> arreglo2= new ArrayList<>();
    private ListView listView;
    private EditText search;
    private TextView texto;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private FirebaseUser user = null;
    private FirebaseDatabase database;
    private DatabaseReference myRef,ref2;
    private String valor = "";
    private ProgressDialog mProgressDialog;
    private UserAdapter adapter,adapter2;
    private RadioButton RBa,RBu;
    private String amigito = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amigos);



        mAuth =	FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        ref2 = database.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        search = (EditText) findViewById(R.id.searchuser);
        mProgressDialog = new ProgressDialog(this);
        RBa = (RadioButton) findViewById(R.id.radioButtonAmigos);
        RBu = (RadioButton) findViewById(R.id.radioButtonUsuarios);

        adapter2 = new UserAdapter(AmigosActivity.this,arreglo2);
        adapter = new UserAdapter(AmigosActivity.this,arreglo);
        listView = (ListView)findViewById(R.id.listAmigos);
        texto = (TextView) findViewById(R.id.textViewTitulo);

    }

    public void onRadioButton(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radioButtonAmigos:
                if (checked){
                    texto.setText("Mis amigos");
                    listView.setAdapter(adapter2);
                    RBu.setChecked(false);
                    search.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                                valor= search.getText().toString().toUpperCase();
                                buscarAmigos();
                                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(search.getWindowToken(), 0);
                                return true;
                            }
                            return false;
                        }
                    });


                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            Intent intent = new Intent(getBaseContext(), UsuarioDetalleActivity.class);
                            intent.putExtra("llave",arreglo2.get(i).getRH());
                            intent.putExtra("nombre",arreglo2.get(i).getNombre());
                            intent.putExtra("correo",arreglo2.get(i).getCorreo());
                            intent.putExtra("solicitar",true);
                            amigito = "si";
                            intent.putExtra("amigo",true);
                            startActivity(intent);

                        }
                    });
                }
                break;
            case R.id.radioButtonUsuarios:
                if (checked){
                    texto.setText("Usuarios");
                    listView.setAdapter(adapter);
                    RBa.setChecked(false);
                    search.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                                valor= search.getText().toString().toUpperCase();
                                buscarUsuario();
                                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.hideSoftInputFromWindow(search.getWindowToken(), 0);
                                return true;
                            }
                            return false;
                        }
                    });



                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            Intent intent = new Intent(getBaseContext(), UsuarioDetalleActivity.class);
                            intent.putExtra("llave",arreglo.get(i).getRH());
                            intent.putExtra("nombre",arreglo.get(i).getNombre());
                            intent.putExtra("correo",arreglo.get(i).getCorreo());
                            intent.putExtra("solicitar",true);
                            amigito = "no";
                            comprobarAmistad(arreglo.get(i).getRH(), intent);

                        }
                    });
                }
                break;
        }
    }

    private void comprobarAmistad(final String llave, final Intent intent) {
        myRef.child("amistades/" + user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean bAmigo = false;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot single : singleSnapshot.getChildren()){
                        if(single.getValue().equals(llave)){
                            bAmigo = true;
                        }
                    }
                }
                intent.putExtra("amigo",bAmigo);
                startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AmigosActivity.this, "ERROR, intentelo de nuevo" + databaseError
                        .getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buscarUsuario() {

        mProgressDialog.setTitle("Cargando...");
        mProgressDialog.setMessage("Buscando usuarios");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        arreglo.clear();
        myRef.child("users/").orderByChild("nombre").startAt(valor).endAt(valor+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for	(DataSnapshot singleSnapshot :	dataSnapshot.getChildren()) {
                    if(!mAuth.getCurrentUser().getUid().equals(singleSnapshot.getKey().toString())) {
                        Usuario myUser = singleSnapshot.getValue(Usuario.class);
                        myUser.setRH(singleSnapshot.getKey().toString());
                        arreglo.add(myUser);
                        listView.setAdapter(adapter);
                        mProgressDialog.dismiss();
                    }
                }
                if(!dataSnapshot.hasChildren()){
                    Toast.makeText(AmigosActivity.this, "No se encuentran usuarios", Toast.LENGTH_SHORT).show();
                    listView.setAdapter(adapter);
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AmigosActivity.this, "ERROR, no se encuentran usuarios", Toast.LENGTH_SHORT).show();
                arreglo.clear();
                listView.setAdapter(adapter);
                mProgressDialog.dismiss();
            }
        });
    }

    private void buscarAmigos() {

        mProgressDialog.setTitle("Cargando...");
        mProgressDialog.setMessage("Cargando amigos");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        arreglo2.clear();
        myRef.child("amistades/" + user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean bAmigo = false;
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot single : singleSnapshot.getChildren()){
                        ref2.child("users/"+single.getValue().toString())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                Usuario user = dataSnapshot.getValue(Usuario.class);
                                user.setRH(dataSnapshot.getKey().toString());
                                if(valor.equals("")){
                                    arreglo2.add(user);
                                    listView.setAdapter(adapter2);
                                }
                                else if(user.getNombre().contains(valor)){
                                    arreglo2.add(user);
                                    listView.setAdapter(adapter2);
                                }
                                mProgressDialog.dismiss();
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(AmigosActivity.this, "Error cargando datos intentelo de nuevo",
                                        Toast.LENGTH_LONG).show();
                                mProgressDialog.dismiss();
                            }
                        });

                    }
                }
                if(!dataSnapshot.hasChildren()){
                    mProgressDialog.dismiss();
                    Toast.makeText(AmigosActivity.this, "No tiene amigos", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AmigosActivity.this, "ERROR, cargando amigos " + databaseError
                        .getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(amigito!=null) {
            if (amigito.equals("si"))
                buscarAmigos();
            else
                buscarUsuario();
        }

    }
}

