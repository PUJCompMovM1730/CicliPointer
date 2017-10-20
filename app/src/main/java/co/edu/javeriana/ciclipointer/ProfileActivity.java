package co.edu.javeriana.ciclipointer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.BuddhistCalendar;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    private Button Breccorridos,Bupdate;
    private EditText Pnombre,  Ppeso,Paltura,Prh,Pedad;
    private TextView Pcorreo;
    private String nombre,RH,Anombre,ARH;
    private int peso,edad,Apeso,Aedad;
    private double altura,Aaltura;
    private ImageView Ppicture;
    private boolean camAuth = false, camDatabase = false;
    private UserProfileChangeRequest.Builder upcrb = null;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private FirebaseUser user = null;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private boolean valor = false;
    private ProgressDialog mProgressDialog;

    private final int MY_PERMISSIONS_REQUEST_CAMARA = 1;
    private final int IMAGE_PICKER_REQUEST = 2;
    private ImageButton Galeria,Foto;
    private Uri profileUri = null;

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

    private void cargarInfo(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            mProgressDialog.setTitle("Cargando...");
            mProgressDialog.setMessage("Cargando información");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            Pcorreo.setText(user.getEmail());
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
    }

    private boolean validateForm(){
        boolean cam = true;
        if(valor){
            nombre = Pnombre.getText().toString();
            if	(TextUtils.isEmpty(nombre))	 {
                Pnombre.setError("Required.");
                cam	= false;
            }	else	{
                Pnombre.setError(null);
            RH = Prh.getText().toString();
            }if	(TextUtils.isEmpty(RH))	 {
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

    private boolean cambios(){
        boolean cam = false;
        if(!nombre.equalsIgnoreCase(Anombre)){
            cam = true;
            camAuth = true;
        }
        if(profileUri!=null){
            cam = true;
            camAuth = true;
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

    private void update(){
        if(camAuth){
            upcrb = new UserProfileChangeRequest.Builder();
            upcrb.setDisplayName(nombre);
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
                                profileUri = null;
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                profileUri = null;
                                Toast.makeText(ProfileActivity.this, "Error al subir imagen", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            user = mAuth.getCurrentUser();
            user.updateProfile(upcrb.build())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProfileActivity.this, "Se actualizó información principal", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            profileUri = null;
                            Toast.makeText(ProfileActivity.this, "Error actualizando "+exception.getMessage().toString(),
                                    Toast.LENGTH_SHORT).show();
                            upcrb.setPhotoUri(null);
                        }
                    });

        }if(camDatabase){
            Usuario us = new Usuario();
            us.setAltura(altura);
            us.setEdad(edad);
            us.setPeso(peso);
            us.setRH(RH);
            myRef.child("users/"+mAuth.getCurrentUser().getUid())
                    .setValue(us)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProfileActivity.this, "Se actualizó información secundaria",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(ProfileActivity.this, "Error guardar datos en servidor "+exception.getMessage().toString(),
                            Toast.LENGTH_SHORT).show();
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
