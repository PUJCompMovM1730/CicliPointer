package co.edu.javeriana.ciclipointer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;

import entities.Usuario;

public class RegistroActivity extends AppCompatActivity {

    private Button registrarme;
    private EditText nombre, contraseña2,contraseña,correo;
    private FirebaseAuth mAuth ;

    private CheckBox turismo;
    private final int MY_PERMISSIONS_REQUEST_CAMARA = 1;
    private final int IMAGE_PICKER_REQUEST = 2;
    private Button Galeria,Foto;
    private ImageView Igaleria;
    private Uri profileUri = null;
    private StorageReference mStorageRef;
    private UserProfileChangeRequest.Builder upcrb = null;
    private ProgressDialog mProgressDialog;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private boolean dbBoolean = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        turismo = (CheckBox) findViewById(R.id.checkBox);
        registrarme = (Button) findViewById(R.id.Bregistro2);
        nombre = (EditText)findViewById(R.id.NombreUsuario);
        contraseña2 = (EditText)findViewById(R.id.Password2);
        contraseña = (EditText)findViewById(R.id.Password);
        correo = (EditText)findViewById(R.id.Correo);
        mProgressDialog = new ProgressDialog(this);



        registrarme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contra()){
                    if(validateForm()){
                        registrar();
                    }
                    else
                        Toast.makeText(RegistroActivity.this, "Parámetros incorrectos", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(RegistroActivity.this, "Contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            }
        });

        Igaleria = (ImageView) findViewById(R.id.imageView);
        Galeria = (Button) findViewById(R.id.Bgaleria);
        Foto = (Button) findViewById(R.id.Bcamara);
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

    /**
     * Descripción: comprueba que contraseñas sean iguales
     * @param
     * @return true si son iguales
     */
    private boolean contra(){
        if(contraseña.getText().toString().equals(contraseña2.getText().toString())){
            return true;
        }
        return false;
    }

    /**
     * Descripción: valida los datos ingresados por el usuario.
     * @param
     * @return true si cumple parámetros.
     */
    private	boolean validateForm()	{
        boolean valid	=	true;
        String	email	=	correo.getText().toString();
        if	(TextUtils.isEmpty(email))	{
            correo.setError("Required.");
            valid	=	false;
        }else{
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                valid	=	false;
                correo.setError("Format required.");
            }else
                correo.setError(null);
        }
        String	password	=	contraseña.getText().toString();
        if	(TextUtils.isEmpty(password))	 {
            contraseña.setError("Required.");
            valid	=	false;
        }	else	{
            if(contraseña.length()<8){
                contraseña.setError("Min 8 caracteres");
            }else
                contraseña.setError(null);
        }
        String	name	=	nombre.getText().toString();
        if	(TextUtils.isEmpty(name))	 {
            nombre.setError("Required.");
            valid	=	false;
        }	else	{
            nombre.setError(null);
        }
        String	nick	=	contraseña2.getText().toString();
        if	(TextUtils.isEmpty(nick))	 {
            contraseña2.setError("Required.");
            valid	=	false;
        }	else	{
            contraseña2.setError(null);
        }
        return	valid;
    }

    /**
     * Descripción: registra un usuario en firebase
     * guarda la imagen en storage.
     * @param
     * @return true si registra correctamente.
     */
    private void registrar(){
        String c = correo.getText().toString();
        String p = contraseña.getText().toString();
        mAuth.createUserWithEmailAndPassword(c,p)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)	{
                        if(task.isSuccessful()){
                            // Log.d(TAG,	"createUserWithEmail:onComplete:"	+	task.isSuccessful());
                            FirebaseUser user	=	mAuth.getCurrentUser();
                            if(user!=null) {    //Update	user	Info
                                upcrb = new UserProfileChangeRequest.Builder();
                                upcrb.setDisplayName(nombre.getText().toString().toUpperCase());
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
                                                    upcrb.setPhotoUri(profileUri);
                                                    Toast.makeText(RegistroActivity.this, "Correcto subir imagen", Toast.LENGTH_SHORT).show();
                                                    avanzar(upcrb);
                                                    mProgressDialog.dismiss();
                                                    profileUri = null;
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    profileUri = null;
                                                    upcrb.setPhotoUri(profileUri);
                                                    Toast.makeText(RegistroActivity.this, "Error al subir imagen", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(RegistroActivity.this, "Seleccione una imagen de perfil", Toast.LENGTH_SHORT).show();
                                    avanzar(upcrb);
                                }


                            }
                        }
                        if	(!task.isSuccessful())	 {
                            String error = task.getException().getMessage().toString();
                            if(error.equals("The email address is already in use by another account.")){
                                Toast.makeText(RegistroActivity.this, "El correo ya está registrado por otro usuario" , Toast.LENGTH_LONG).show();
                            }else
                            System.err.println("error: "+error);

                        }
                    }
                });
    }

    private void avanzar(final UserProfileChangeRequest.Builder upcrb) {
        FirebaseUser user	=	mAuth.getCurrentUser();
        user.updateProfile(upcrb.build()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (guardarDataBase()) {
                    if(turismo.isChecked()){
                        dbBoolean = true;
                        startActivity(new Intent(RegistroActivity.this, InicioTurismo.class));
                    }else{
                        dbBoolean = true;
                        startActivity(new Intent(RegistroActivity.this, InicioActivity.class));
                    }

                }else{
                    Toast.makeText(RegistroActivity.this, "Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistroActivity.this, "Vuelva a intentarlo", Toast.LENGTH_SHORT).show();
                upcrb.setPhotoUri(null);
            }
        });
    }

    /**
     * Descripción: registra los datos básicos en
     * database
     * @param
     * @return true si registra correctamente.
     */
    private boolean guardarDataBase()
    {
        Usuario us = new Usuario();
        us.setAltura(0);
        us.setEdad(0);
        us.setPeso(0);
        us.setRH("");
        us.setKm(0);
        us.setNombre(nombre.getText().toString().toUpperCase());
        us.setCorreo(correo.getText().toString());
        if(turismo.isChecked()){
            us.setTipo("turismo");
        }
        myRef.child("users/"+mAuth.getCurrentUser().getUid())
                .setValue(us).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(RegistroActivity.this, "Error guardar datos en servidor", Toast.LENGTH_SHORT).show();
                dbBoolean = false;
            }
        });

        return dbBoolean;
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
                        Igaleria.setImageBitmap(selectedImage);
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
                    Igaleria.setImageBitmap(imageBitmap);
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
