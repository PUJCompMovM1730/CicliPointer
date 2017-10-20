package co.edu.javeriana.ciclipointer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.util.BuddhistCalendar;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import entities.Usuario;

public class ProfileActivity extends AppCompatActivity {

    private Button Breccorridos,Bupdate;
    private EditText Pnombre,  Ppeso,Paltura,Prh,Pedad;
    private TextView Pcorreo;
    private String nombre,correo,RH;
    private int peso,edad;
    private double altura;
    private ImageView Ppicture;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private FirebaseUser user = null;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private boolean valor = false;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Breccorridos = (Button) findViewById(R.id.Brecorridos);
        Breccorridos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RecorridosActivity.class);
                startActivity(intent);
            }
        });

        mProgressDialog = new ProgressDialog(this);
        Pnombre = (EditText) findViewById(R.id.PNombreUsuario);
        Pcorreo = (TextView) findViewById(R.id.PCorreo);
        Ppeso = (EditText) findViewById(R.id.PPeso);
        Paltura = (EditText) findViewById(R.id.PAltura);
        Prh = (EditText) findViewById(R.id.PRh);
        Pedad = (EditText) findViewById(R.id.PEdad);
        Ppicture = (ImageView) findViewById(R.id.PImageView);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth =	FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        if(cargarInfo()) {
            nombre = Pedad.getText().toString();
            correo = Pcorreo.getText().toString();
            RH = Prh.getText().toString();
            peso = Integer.parseInt(Ppeso.getText().toString());
            edad = Integer.parseInt(Pedad.getText().toString());
            altura = Double.parseDouble(Paltura.getText().toString());
        }

        Bupdate = (Button) findViewById(R.id.Beditar);
        Bupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateForm()){
                    if(cambios())
                        update();
                    else
                        Toast.makeText(ProfileActivity.this, "No ha hecho cambios", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(ProfileActivity.this, "Datos invalidos", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,InicioActivity.class));
    }

    private boolean cargarInfo(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            mProgressDialog.setTitle("Cargando...");
            mProgressDialog.setMessage("Cargando información");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            Pcorreo.setText(user.getEmail());
            Pnombre.setText(user.getDisplayName());
            myRef.child("users/"+user.getUid()).addListenerForSingleValueEvent(new	ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)	 {
                  /*  for	(DataSnapshot singleSnapshot :	dataSnapshot.getChildren())	{
                        System.out.println("valor es "+singleSnapshot.getValue());*/
                       Usuario myUser =	dataSnapshot.getValue(Usuario.class);
                    //System.out.println("valor es "+myUser.getAltura());
                        Ppeso.setText(myUser.getPeso()+"");
                        Pedad.setText(myUser.getEdad()+"");
                        Paltura.setText(myUser.getAltura()+"");
                        Prh.setText(myUser.getRH());
                        valor = true;
                        mProgressDialog.dismiss();
                    //}
                }
                @Override
                public void onCancelled(DatabaseError databaseError)	{
                    Toast.makeText(ProfileActivity.this, "Error descargando datos "+
                            databaseError.toException(), Toast.LENGTH_SHORT).show();
                    valor = false;
                    mProgressDialog.dismiss();
                }
            });

            mProgressDialog.setTitle("Cargando...");
            mProgressDialog.setMessage("Cargando foto del servidor");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            mStorageRef.child(user.getUid())
                    .getDownloadUrl().addOnSuccessListener(this, new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) { // guardando esta uri no es necesario volver a buscarla en servidor
                    Glide.with(ProfileActivity.this)
                            .load(uri)
                            .fitCenter()
                            .centerCrop()
                            .into(Ppicture);
                    valor = true;
                    mProgressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    valor = false;
                    mProgressDialog.dismiss();
                }
            });
        }
        return valor;
    }

    private boolean validateForm(){
        boolean cam = true;
        // ver datos no vacios
        // ver que altura menor 2.0
        // ver peso menor 100
        // ver edad menor 100
        // ver formato rh, puede ser comparando equsl
        return cam;
    }
    private boolean cambios(){
        boolean cam = true;
        // ver si cambio datos
        return cam;
    }

    private void update(){

    }
}
