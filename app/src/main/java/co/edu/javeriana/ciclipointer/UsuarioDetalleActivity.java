package co.edu.javeriana.ciclipointer;

import android.app.ProgressDialog;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.List;

import entities.Amistad;
import entities.SolicitudAmistad;

public class UsuarioDetalleActivity extends AppCompatActivity {

    private TextView nombreUs,correoUs;
    private ImageView foto;
    private ProgressDialog mProgressDialog;
    private StorageReference mStorageRef;
    private Button agregar,mensaje,eliminar,
                    aceptar,rechazar;
    private DatabaseReference myRef;
    private String llave;
    private FirebaseAuth mAuth;
    private FirebaseUser user = null;
    private ArrayList<String> soli = new ArrayList<String>();
    private boolean re = true, de1 = true, de2 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_detalle);

        nombreUs = (TextView) findViewById(R.id.DnombreUs);
        nombreUs.setText(getIntent().getStringExtra("nombre"));
        correoUs = (TextView) findViewById(R.id.DnombreCorreo);
        correoUs.setText(getIntent().getStringExtra("correo"));
        foto = (ImageView)findViewById(R.id.imageViewPicture2);
        agregar = (Button)findViewById(R.id.Bagregar);
        mensaje = (Button)findViewById(R.id.Bmensaje);
        eliminar = (Button)findViewById(R.id.Beliminar);
        aceptar = (Button)findViewById(R.id.Baceptar);
        rechazar = (Button)findViewById(R.id.Brechazar);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        myRef = FirebaseDatabase.getInstance().getReference();
        llave = getIntent().getStringExtra("llave");
        mAuth =	FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        Boolean solicitar = getIntent().getBooleanExtra("solicitar",false);

        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soli.clear();
                myRef.child("solicitudes/" + llave).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            boolean enviada = false;
                            String key = dataSnapshot.getKey();
                            soli = (ArrayList<String>) singleSnapshot.getValue();
                            for (String id : soli) {
                                if (id.equals(user.getUid())) {
                                    enviada = true;
                                }
                            }
                            if (enviada) {
                                Toast.makeText(UsuarioDetalleActivity.this, "Ya envió solicitud antes", Toast.LENGTH_SHORT).show();
                                agregar.setClickable(false);
                            } else
                                update(key);
                        }
                        if (!dataSnapshot.hasChildren()) {
                            // si no existe
                            crearSolicitud();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(UsuarioDetalleActivity.this, "ERROR enviando solicitud " + databaseError
                                .getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        if(solicitar) {

            aceptar.setVisibility(View.GONE);
            rechazar.setVisibility(View.GONE);
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle("Cargando...");
            mProgressDialog.setMessage("Cargando foto del servidor");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            try {
                mStorageRef.child(llave)
                        .getDownloadUrl().addOnSuccessListener(this, new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(UsuarioDetalleActivity.this)
                                .load(uri)
                                .fitCenter()
                                .centerCrop()
                                .into(foto);
                        mProgressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(UsuarioDetalleActivity.this, "No se encuentra imagen de perfil", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, "Error con imagen: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }

            Boolean friend = getIntent().getBooleanExtra("amigo", false);
            if (friend) {
                agregar.setVisibility(View.GONE);

            } else {
                mensaje.setVisibility((View.GONE));
                eliminar.setVisibility(View.GONE);//se muestra agregar, y se quita mensaje y eliminar
            }



        }else{
            agregar.setVisibility(View.GONE);
            mensaje.setVisibility(View.GONE);
            eliminar.setVisibility(View.GONE);

            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle("Cargando...");
            mProgressDialog.setMessage("Cargando foto del servidor");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            try {
                mStorageRef.child(llave)
                        .getDownloadUrl().addOnSuccessListener(this, new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(UsuarioDetalleActivity.this)
                                .load(uri)
                                .fitCenter()
                                .centerCrop()
                                .into(foto);
                        mProgressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(UsuarioDetalleActivity.this, "No se encuentra imagen de perfil", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, "Error con imagen: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }



        }
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soli.clear();
                myRef.child("amistades/" + llave).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            String key = dataSnapshot.getKey();
                            soli = (ArrayList<String>) singleSnapshot.getValue();
                            updateAmistadAmigo();
                        }
                        if (!dataSnapshot.hasChildren()) {
                            crearAmistadAmigo();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(UsuarioDetalleActivity.this, "ERROR creando amistad " + databaseError
                                .getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }


        });

        rechazar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarSolicitud();
                if(re){
                    Toast.makeText(UsuarioDetalleActivity.this, "Se rechazó", Toast.LENGTH_SHORT).show();
                    aceptar.setVisibility(View.GONE);
                    rechazar.setVisibility(View.GONE);
                    agregar.setVisibility(View.VISIBLE);
                }else{
                    re = true;
                    Toast.makeText(UsuarioDetalleActivity.this, "Error rechazando", Toast.LENGTH_SHORT).show();
                }

            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminarAmistadAmigo();
                eliminarAmistad();
                if(de1 && de2){
                    Toast.makeText(UsuarioDetalleActivity.this, "Se eliminó amistad", Toast.LENGTH_SHORT).show();
                    mensaje.setVisibility(View.GONE);
                    eliminar.setVisibility(View.GONE);
                    agregar.setVisibility(View.VISIBLE);
                }else{
                    de1 = true;
                    de2 = true;
                    Toast.makeText(UsuarioDetalleActivity.this, "Error eliminando amistad", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void update(String key) {
        SolicitudAmistad so = new SolicitudAmistad();
        soli.add(user.getUid());
        so.setSolicitantes(soli);
        myRef.child("solicitudes/"+llave)
                .setValue(so).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(UsuarioDetalleActivity.this, "Error creando solicitud", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                agregar.setClickable(false);
                Toast.makeText(UsuarioDetalleActivity.this, "Se envió solicitud de amistad", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void crearSolicitud(){
        SolicitudAmistad sa = new SolicitudAmistad();
        List<String> solicitantes = new ArrayList<>();
        solicitantes.add(user.getUid());
        sa.setSolicitantes(solicitantes);
        myRef.child("solicitudes/"+llave)
                .setValue(sa).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(UsuarioDetalleActivity.this, "Error creando solicitud", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                agregar.setClickable(false);
                Toast.makeText(UsuarioDetalleActivity.this, "Se envió solicitud de amistad", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void crearAmistadAmigo(){
        Amistad am = new Amistad();
        List<String> amigos = new ArrayList<>();
        amigos.add(user.getUid());
        am.setAmigos(amigos);
        myRef.child("amistades/"+llave)
                .setValue(am).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateAmistadMitad();
            }
        });
    }

    private void crearAmistad(){
        Amistad am = new Amistad();
        List<String> amigos = new ArrayList<>();
        amigos.add(llave);
        am.setAmigos(amigos);
        myRef.child("amistades/"+user.getUid())
                .setValue(am).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                eliminarAmistadAmigo();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UsuarioDetalleActivity.this, "Ahora son amigos", Toast.LENGTH_SHORT).show();
                eliminarSolicitud();
                aceptar.setVisibility(View.GONE);
                rechazar.setVisibility(View.GONE);
                eliminar.setVisibility(View.VISIBLE);
                mensaje.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateAmistadMitad(){
        soli.clear();
        myRef.child("amistades/" + user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    soli = (ArrayList<String>) singleSnapshot.getValue();
                    updateAmistad();
                }
                if (!dataSnapshot.hasChildren()) {
                        crearAmistad();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UsuarioDetalleActivity.this, "ERROR creando amistad" + databaseError
                        .getMessage().toString(), Toast.LENGTH_SHORT).show();
                eliminarAmistadAmigo();
            }
        });
    }


    private void updateAmistadAmigo() {
        Amistad am = new Amistad();
        if(!soli.contains(user.getUid()))
            soli.add(user.getUid());
        am.setAmigos(soli);
        myRef.child("amistades/"+llave)
                .setValue(am).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
               updateAmistadMitad();
            }
        });
    }
    private void updateAmistad() {
        Amistad am = new Amistad();
        if(!soli.contains(llave))
            soli.add(llave);
        am.setAmigos(soli);
        myRef.child("amistades/"+user.getUid())
                .setValue(am).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                eliminarAmistadAmigo();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UsuarioDetalleActivity.this, "Ahora son amigos", Toast.LENGTH_SHORT).show();
                eliminarSolicitud();
                aceptar.setVisibility(View.GONE);
                rechazar.setVisibility(View.GONE);
                eliminar.setVisibility(View.VISIBLE);
                mensaje.setVisibility(View.VISIBLE);
            }
        });
    }

    private void eliminarAmistadAmigo(){
        soli.clear();
        myRef.child("amistades/"+llave)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot single : singleSnapshot.getChildren()){
                        if(!single.getValue().equals(user.getUid())){
                            soli.add(single.getValue().toString());
                        }
                    }
                }
                updateEliminarAmistadAmigo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UsuarioDetalleActivity.this, "ERROR eliminando amigo " + databaseError
                        .getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEliminarAmistadAmigo(){
        myRef.child("amistades/"+ llave+"/amigos")
                .setValue(soli).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UsuarioDetalleActivity.this, "Error eliminando solicitud "+e.getMessage().toString(),
                        Toast.LENGTH_SHORT).show();
                de1 = false;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    private void eliminarAmistad(){
        soli.clear();
        myRef.child("amistades/"+user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                            for (DataSnapshot single : singleSnapshot.getChildren()){
                                if(!single.getValue().equals(llave)){
                                    soli.add(single.getValue().toString());
                                }
                            }
                        }
                        updateEliminarAmistad();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(UsuarioDetalleActivity.this, "ERROR eliminando amigo " + databaseError
                                .getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateEliminarAmistad(){
        myRef.child("amistades/"+ user.getUid()+"/amigos")
                .setValue(soli).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UsuarioDetalleActivity.this, "Error eliminando solicitud "+e.getMessage().toString(),
                        Toast.LENGTH_SHORT).show();
                de2 = false;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    private void eliminarSolicitud(){
         soli.clear();
        myRef.child("solicitudes/" + user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot single : singleSnapshot.getChildren()){
                        if(!single.getValue().equals(llave)){
                           soli.add(single.getValue().toString());
                        }
                    }
                }
                updateSolicitud();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UsuarioDetalleActivity.this, "ERROR eliminando solicitud de amistad " + databaseError
                        .getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSolicitud(){
        myRef.child("solicitudes/"+ user.getUid()+"/solicitantes")
                .setValue(soli).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UsuarioDetalleActivity.this, "Error eliminando solicitud "+e.getMessage().toString(),
                        Toast.LENGTH_SHORT).show();
                re = false;
            }
        });
    }
}
