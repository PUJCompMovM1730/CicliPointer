package co.edu.javeriana.ciclipointer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.BuddhistCalendar;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;

import entities.Usuario;

public class ProfileActivity extends AppCompatActivity {

    private Button Breccorridos,Bupdate, button;
    private EditText Pnombre,  Ppeso,Paltura,Prh,Pedad,Pcorreo,
    Pcontraseña, Pcontraseña2,pass;
    private TextView Pkm;
    private String nombre,RH,Anombre,ARH,correo,Acorreo,
    contraseña,contraseña2,password;
    private int peso,edad,Apeso,Aedad;
    private double altura,Aaltura;
    private ImageView Ppicture;
    private boolean camAuth = false, camDatabase = false,
                    camPicture = false, camCorreo = false,
                    camContra = false;
    private UserProfileChangeRequest.Builder upcrb = null;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private FirebaseUser user = null;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private boolean valor = false;
    private ProgressDialog mProgressDialog;
    private AlertDialog dialog;
    private Uri uri_anterior;


    private final int MY_PERMISSIONS_REQUEST_CAMARA = 1;
    private final int IMAGE_PICKER_REQUEST = 2;
    private ImageButton Galeria,Foto;
    private Uri profileUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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
        Pkm = (TextView) findViewById(R.id.textViewkm);
        Pcorreo = (EditText) findViewById(R.id.PCorreo);
        Ppeso = (EditText) findViewById(R.id.PPeso);
        Paltura = (EditText) findViewById(R.id.PAltura);
        Prh = (EditText) findViewById(R.id.PRh);
        Pedad = (EditText) findViewById(R.id.PEdad);
        Ppicture = (ImageView) findViewById(R.id.PImageView);
        Pcontraseña = (EditText) findViewById(R.id.Pcontraseña);
        Pcontraseña2 = (EditText) findViewById(R.id.Pcontraseña2);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth =	FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        cargarInfo();

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

        Galeria = (ImageButton) findViewById(R.id.imageButtonGallery);
        Foto = (ImageButton) findViewById(R.id.imageButtonCamera);
        Galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE);
                if(permissionCheck == 0){
                    galeriaSelection();
                }else
                    solicitudPermisoGaleria();
            }
        });

        Foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.CAMERA);
                if(permissionCheck == 0){
                    takePicture();
                }else
                    solicitudPermisoCamara();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,InicioActivity.class));
    }

    /**
     * Descripción: carga información del usuario
     * y la muestra.
     * @param
     * @return
     */
    private void cargarInfo(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            mProgressDialog.setTitle("Cargando...");
            mProgressDialog.setMessage("Cargando información");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            Pcorreo.setText(user.getEmail());
            Acorreo = user.getEmail();
            Anombre = user.getDisplayName();
            Pnombre.setText(Anombre);
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
                        Pkm.setText(String.format("%.2f", myUser.getKm()));
                        Apeso = myUser.getPeso();
                        Aaltura = myUser.getAltura();
                        ARH = myUser.getRH();
                        Aedad = myUser.getEdad();
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
            if(user.getPhotoUrl()!=null){
                mStorageRef.child(user.getUid())
                        .getDownloadUrl().addOnSuccessListener(this, new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
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
                        Toast.makeText(ProfileActivity.this, "Seleccione una foto de perfil, " +
                                "de lo contrario no podrá hacer cambios", Toast.LENGTH_LONG).show();
                    }
                });
            }else
                Toast.makeText(this, "No tiene foto", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Descripción: valida los datos ingresados
     * por el usuario para su posterior actualización.
     * @param
     * @return true si todos cumple formato.
     */
    private boolean validateForm(){
        boolean cam = true;
        if(valor){

            contraseña = Pcontraseña.getText().toString();
            contraseña2 = Pcontraseña2.getText().toString();
            if(!TextUtils.isEmpty(contraseña)||!TextUtils.isEmpty(contraseña2)){
                if(!TextUtils.isEmpty(contraseña2)&&!TextUtils.isEmpty(contraseña)){
                    if(contraseña.equals(contraseña2)){
                        if(contraseña.length()<8){
                            Pcontraseña.setError("Min 8 caracteres");
                            Pcontraseña2.setError("Min 8 caracteres");
                            cam = false;
                        }else{
                            camContra = true;
                        }
                    }else{
                        Pcontraseña2.setError("No coinciden");
                        Pcontraseña.setError("No coinciden");
                        cam = false;
                    }
                }else{
                    cam = false;
                    if(TextUtils.isEmpty(contraseña))
                        Pcontraseña.setError("Required.");
                    else
                        Pcontraseña2.setError("Required.");
                }
            }
            correo = Pcorreo.getText().toString();
            if	(TextUtils.isEmpty(correo))	{
                Pcorreo.setError("Required.");
                cam	= false;
            }else{
                if(!Patterns.EMAIL_ADDRESS.matcher(correo).matches()){
                    cam	=	false;
                    Pcorreo.setError("Format required.");
                }else
                    Pcorreo.setError(null);
            }
            nombre = Pnombre.getText().toString();
            if	(TextUtils.isEmpty(nombre))	 {
                Pnombre.setError("Required.");
                cam	= false;
            }	else	{
                Pnombre.setError(null);
            }
            RH = Prh.getText().toString();
            if	(TextUtils.isEmpty(RH))	 {
                Prh.setError("Required.");
                cam	= false;
            }	else	{
                if(RH.equalsIgnoreCase("A+")||RH.equalsIgnoreCase("A-")||
                        RH.equalsIgnoreCase("O+")||RH.equalsIgnoreCase("O-")||
                        RH.equalsIgnoreCase("B+")||RH.equalsIgnoreCase("B-")||
                        RH.equalsIgnoreCase("AB+")||RH.equalsIgnoreCase("AB-")){
                    Prh.setError(null);
                }else{
                    Prh.setError("Ej:A+");
                    cam = false;
                }
            }
            peso = Integer.parseInt(Ppeso.getText().toString());
            if	(TextUtils.isEmpty(peso+""))	 {
                Ppeso.setError("Required.");
                cam	= false;
            }	else {
                if(peso<=100&&peso>=30){
                    Ppeso.setError(null);
                }else{
                    cam = false;
                    Ppeso.setError("min 30;max 100");
                }
            }
            edad = Integer.parseInt(Pedad.getText().toString());
            if	(TextUtils.isEmpty(edad+""))	 {
                Pedad.setError("Required.");
                cam	= false;
            }	else {
                if(edad<=100&&edad>=8){
                    Pedad.setError(null);
                }else{
                    cam = false;
                    Pedad.setError("min 8;max 100");
                }
            }
            if(Paltura.getText().toString().contains(".")){
                altura = Double.parseDouble(Paltura.getText().toString());
                if	(TextUtils.isEmpty(altura+""))	 {
                    Paltura.setError("Required.");
                    cam	= false;
                }	else {
                    if(altura<=2.0&&altura>=1.3){
                        Paltura.setError(null);
                    }else{
                        cam = false;
                        Paltura.setError("min 1.3;max 2.0");
                    }
                }
            }else{
                Paltura.setError("Debe tener punto");
                cam = false;
            }
        }else{
            return false;
        }
        return cam;
    }

    /**
     * Descripción: verifica que el usuario haya hecho
     * cambios en los datos.
     * @param
     * @return false si el usuario no hizo cambios.
     */
    private boolean cambios(){
        boolean cam = false;
        if(camContra)
            cam = true;
        if(!correo.equalsIgnoreCase(Acorreo)){
            cam = true;
            camCorreo = true;
        }
        if(!nombre.equalsIgnoreCase(Anombre)){
            nombre = nombre.toUpperCase();
            cam = true;
            camAuth = true;
        }
        if(profileUri!=null){
            cam = true;
            camPicture = true;
        }
        if(!RH.equalsIgnoreCase(ARH)){
            cam = true;
            camDatabase = true;
        }
        if(peso !=Apeso){
            cam = true;
            camDatabase = true;
        }
        if(edad != Aedad){
            cam = true;
            camDatabase = true;
        }
        if(altura !=Aaltura){
            cam = true;
            camDatabase = true;
        }
        return cam;
    }

    /**
     * Descripción: actualiza la información
     * según los cambios del usuario.
     * @param
     * @return
     */
    private void update(){
        if(camPicture){
            if (profileUri != null) {
                mProgressDialog.setTitle("Subiendo...");
                mProgressDialog.setMessage("Subiendo foto al servidor");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                final Uri file = profileUri;
                StorageReference riversRef = mStorageRef.child(mAuth.getCurrentUser().getUid());
                riversRef.putFile(file)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(ProfileActivity.this, "Correcto subir imagen", Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();

                                camPicture = false;
                                if(camAuth){
                                    upcrb = new UserProfileChangeRequest.Builder();
                                    upcrb.setDisplayName(nombre);
                                    user = mAuth.getCurrentUser();
                                    uri_anterior = user.getPhotoUrl();
                                    upcrb.setPhotoUri(profileUri);
                                    user.updateProfile(upcrb.build())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(ProfileActivity.this, "Se actualizó nombre", Toast.LENGTH_SHORT).show();
                                                    Anombre = nombre;

                                                    Usuario us = new Usuario();
                                                    us.setAltura(altura);
                                                    us.setEdad(edad);
                                                    us.setPeso(peso);
                                                    us.setRH(RH);
                                                    if(camCorreo) {
                                                        us.setCorreo(correo);
                                                    }else{
                                                        us.setCorreo(Acorreo);
                                                    }
                                                    if(camAuth) {
                                                        us.setNombre(nombre);
                                                    }else{
                                                        us.setNombre(Anombre);
                                                    }
                                                    myRef.child("users/"+mAuth.getCurrentUser().getUid())
                                                            .setValue(us)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(ProfileActivity.this, "Se actualizó información física",
                                                                            Toast.LENGTH_SHORT).show();
                                                                    Aaltura = altura;
                                                                    Aedad = edad;
                                                                    Apeso = peso;
                                                                    ARH = RH;
                                                                    camDatabase = false;
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception exception) {
                                                                    Toast.makeText(ProfileActivity.this, "Error guardar información física "+exception.getMessage().toString(),
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                    camAuth = false;
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    Toast.makeText(ProfileActivity.this, "Error actualizando nombre "+exception.getMessage().toString(),
                                                            Toast.LENGTH_SHORT).show();
                                                    upcrb.setPhotoUri(uri_anterior);

                                                }
                                            });
                                }else{
                                    upcrb = new UserProfileChangeRequest.Builder();
                                    user = mAuth.getCurrentUser();
                                    uri_anterior = user.getPhotoUrl();
                                    upcrb.setPhotoUri(profileUri);
                                    user.updateProfile(upcrb.build())
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    Toast.makeText(ProfileActivity.this, "Error actualizando imagen"+exception.getMessage().toString(),
                                                            Toast.LENGTH_SHORT).show();
                                                    upcrb.setPhotoUri(uri_anterior);
                                                }
                                            });
                                }
                                profileUri = null;
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                profileUri = null;
                                camPicture = false;
                                Toast.makeText(ProfileActivity.this, "Error al subir imagen, vuelva a cargarla", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }if(camAuth){
            upcrb = new UserProfileChangeRequest.Builder();
            upcrb.setDisplayName(nombre);
            user = mAuth.getCurrentUser();
            user.updateProfile(upcrb.build())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProfileActivity.this, "Se actualizó nombre", Toast.LENGTH_SHORT).show();
                            Anombre = nombre;
                            Usuario us = new Usuario();
                            us.setAltura(altura);
                            us.setEdad(edad);
                            us.setPeso(peso);
                            us.setRH(RH);
                            if(camCorreo) {
                                us.setCorreo(correo);
                            }else{
                                us.setCorreo(Acorreo);
                            }
                            if(camAuth) {
                                us.setNombre(nombre);
                            }else{
                                us.setNombre(Anombre);
                            }
                            myRef.child("users/"+mAuth.getCurrentUser().getUid())
                                    .setValue(us)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ProfileActivity.this, "Se actualizó información física",
                                                    Toast.LENGTH_SHORT).show();
                                            Aaltura = altura;
                                            Aedad = edad;
                                            Apeso = peso;
                                            ARH = RH;
                                            camDatabase = false;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            Toast.makeText(ProfileActivity.this, "Error guardar información física "+exception.getMessage().toString(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            camAuth = false;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(ProfileActivity.this, "Error actualizando nombre "+exception.getMessage().toString(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }if(camCorreo){
            user = mAuth.getCurrentUser();
            user.updateEmail(correo).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ProfileActivity.this, "Se actualizó correo", Toast.LENGTH_SHORT).show();
                    Acorreo = correo;

                    Usuario us = new Usuario();
                    us.setAltura(altura);
                    us.setEdad(edad);
                    us.setPeso(peso);
                    us.setRH(RH);
                    if(camCorreo) {
                        us.setCorreo(correo);
                    }else{
                        us.setCorreo(Acorreo);
                    }
                    if(camAuth) {
                        us.setNombre(nombre);
                    }else{
                        us.setNombre(Anombre);
                    }
                    myRef.child("users/"+mAuth.getCurrentUser().getUid())
                            .setValue(us)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ProfileActivity.this, "Se actualizó información física",
                                            Toast.LENGTH_SHORT).show();
                                    Aaltura = altura;
                                    Aedad = edad;
                                    Apeso = peso;
                                    ARH = RH;
                                    camDatabase = false;
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Toast.makeText(ProfileActivity.this, "Error guardar información física "+exception.getMessage().toString(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                    camCorreo = false;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String error = e.getMessage().toString();
                    if(error.equals("The email address is already in use by another account.")){
                        Toast.makeText(ProfileActivity.this, "El correo ya está en uso", Toast.LENGTH_LONG).show();
                    }else if(error.equals("This operation is sensitive and requires recent authentication. " +
                            "Log in again before retrying this request.")){
                        Toast.makeText(ProfileActivity.this, "Debe autenticarse de nuevo para hacer " +
                                "el cambio de correo", Toast.LENGTH_LONG).show();
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProfileActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.dialog_reauth,null);
                        mBuilder.setView(mView);
                        dialog = mBuilder.create();
                        dialog.show();
                        pass = (EditText) mView.findViewById(R.id.PcontraActual);
                        button = (Button) mView.findViewById(R.id.buttonActual);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                password = pass.getText().toString();
                                if(TextUtils.isEmpty(password)){
                                    pass.setError("Required.");
                                }else {
                                    pass.setError(null);
                                    dialog.dismiss();
                                    AuthCredential credential = EmailAuthProvider.getCredential(Acorreo,password);
                                    user.reauthenticate(credential)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    user.updateEmail(correo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(ProfileActivity.this, "Se actualizó correo", Toast.LENGTH_SHORT).show();
                                                            Acorreo = correo;
                                                            Usuario us = new Usuario();
                                                            us.setAltura(altura);
                                                            us.setEdad(edad);
                                                            us.setPeso(peso);
                                                            us.setRH(RH);
                                                            if(camCorreo) {
                                                                us.setCorreo(correo);
                                                            }else{
                                                                us.setCorreo(Acorreo);
                                                            }
                                                            if(camAuth) {
                                                                us.setNombre(nombre);
                                                            }else{
                                                                us.setNombre(Anombre);
                                                            }
                                                            myRef.child("users/"+mAuth.getCurrentUser().getUid())
                                                                    .setValue(us)
                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(ProfileActivity.this, "Se actualizó información física",
                                                                                    Toast.LENGTH_SHORT).show();
                                                                            Aaltura = altura;
                                                                            Aedad = edad;
                                                                            Apeso = peso;
                                                                            ARH = RH;
                                                                            camDatabase = false;
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception exception) {
                                                                            Toast.makeText(ProfileActivity.this, "Error guardar información física "+exception.getMessage().toString(),
                                                                                    Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    });
                                                            camCorreo = false;
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(ProfileActivity.this, "Intente de nuevo actualizar correo", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ProfileActivity.this, "Error autenticación "+e.getMessage().toString(),
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        });

                    }else{
                        Toast.makeText(ProfileActivity.this, "Error correo: "+e.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }

                }
            });
        }if(camDatabase){
            Usuario us = new Usuario();
            us.setAltura(altura);
            us.setEdad(edad);
            us.setPeso(peso);
            us.setRH(RH);
            if(camCorreo) {
                us.setCorreo(correo);
                System.out.println("cambio correo");
            }else{
                us.setCorreo(Acorreo);
            }
            if(camAuth) {
                us.setNombre(nombre);
                System.out.println("cambio nombre true 3"+nombre);
                System.out.println("cambio nombre");
            }else{
                System.out.println("cambio nombre false 3");
                us.setNombre(Anombre);
            }
            myRef.child("users/"+mAuth.getCurrentUser().getUid())
                    .setValue(us)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProfileActivity.this, "Se actualizó información física",
                                    Toast.LENGTH_SHORT).show();
                            Aaltura = altura;
                            Aedad = edad;
                            Apeso = peso;
                            ARH = RH;
                            camDatabase = false;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(ProfileActivity.this, "Error guardar información física "+exception.getMessage().toString(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }if(camContra){
            user = mAuth.getCurrentUser();
            user.updatePassword(contraseña).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ProfileActivity.this, "Se actualizó contraseña", Toast.LENGTH_SHORT).show();
                    contraseña = "";
                    contraseña2 = "";
                    Pcontraseña2.setError(null);
                    Pcontraseña.setError(null);
                    Pcontraseña.setText("");
                    Pcontraseña2.setText("");
                    camContra = false;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String error = e.getMessage().toString();
                    if(error.equals("This operation is sensitive and requires recent authentication. " +
                            "Log in again before retrying this request.")){
                        Toast.makeText(ProfileActivity.this, "Debe autenticarse para hacer " +
                                "el cambio de la contraseña", Toast.LENGTH_LONG).show();
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProfileActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.dialog_reauth,null);
                        mBuilder.setView(mView);
                        dialog = mBuilder.create();
                        dialog.show();
                        pass = (EditText) mView.findViewById(R.id.PcontraActual);
                        button = (Button) mView.findViewById(R.id.buttonActual);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                password = pass.getText().toString();
                                if(TextUtils.isEmpty(password)){
                                    pass.setError("Required.");
                                }else {
                                    pass.setError(null);
                                    dialog.dismiss();
                                    if(!password.equals(contraseña)){
                                        AuthCredential credential = EmailAuthProvider.getCredential(Acorreo,password);
                                        user.reauthenticate(credential)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        user.updatePassword(contraseña).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(ProfileActivity.this, "Se actualizó contraseña",
                                                                        Toast.LENGTH_SHORT).show();
                                                                contraseña = "";
                                                                contraseña2 = "";
                                                                Pcontraseña2.setError(null);
                                                                Pcontraseña.setError(null);
                                                                Pcontraseña.setText("");
                                                                Pcontraseña2.setText("");
                                                                camContra = false;
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(ProfileActivity.this, "Intente de nuevo actualizar contraseña",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ProfileActivity.this, "Error autenticación "+e.getMessage().toString(),
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }else{
                                        Toast.makeText(ProfileActivity.this, "Contraseña no debe ser igual a la anterior",
                                                Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                        });

                    }else{
                        Toast.makeText(ProfileActivity.this, "Error contraseña: "+e.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }

    /**
     * Descripción: pide permisos para acceder a la cámara.
     * @param
     * @return
     */
    private void solicitudPermisoCamara (){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.CAMERA)) {
                Toast.makeText(this, "Se necesita el permiso para poder mostrar la imagen!", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMARA);


        }
    }

    /**
     * Descripción: pide permisos para acceder a la galería.
     * @param
     * @return
     */
    private void solicitudPermisoGaleria (){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Se necesita el permiso para poder mostrar la imagen!", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    IMAGE_PICKER_REQUEST);


        }
    }


    /**
     * Descripción: accede a tomar una foto o
     * seleciconar una imagen de la galeria
     * al tener los permisos.
     * @param
     * @return
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMARA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(getApplicationContext(),"Permiso denegado camara", Toast.LENGTH_LONG).show();
                }
                return;
            }case IMAGE_PICKER_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    galeriaSelection();
                } else {
                    Toast.makeText(getApplicationContext(),"Permiso denegado galeria", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


    /**
     * Descripción: guarda la uri de la imagen
     * obtenida.
     * @param  data
     * @return
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case IMAGE_PICKER_REQUEST:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = data.getData();
                        profileUri = imageUri;
                        //System.out.println("uri es "+imageUri);
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        Ppicture.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case MY_PERMISSIONS_REQUEST_CAMARA:
                if(resultCode == RESULT_OK){
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    profileUri = data.getData();
                    Ppicture.setImageBitmap(imageBitmap);
                    // como obtener URI
                }
                break;
        }
    }

    /**
     * Descripción: selecciona la foto
     * @param
     * @return
     */
    private void galeriaSelection(){
        Intent pickImage = new Intent(Intent.ACTION_PICK);
        pickImage.setType("image/*");
        startActivityForResult(pickImage, IMAGE_PICKER_REQUEST);
    }

    /**
     * Descripción: toma la foto
     * @param
     * @return
     */
    private void takePicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, MY_PERMISSIONS_REQUEST_CAMARA);
        }
    }
}
