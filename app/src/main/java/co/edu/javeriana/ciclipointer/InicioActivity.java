package co.edu.javeriana.ciclipointer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class InicioActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Button bNueva,bViaje,bGrupal;
    private ImageView barraPerfil;
    TextView barraNombre,barraCorreo;

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private FirebaseUser user = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        barraNombre=  navigationView.getHeaderView(0).findViewById(R.id.barraProfileName);
        barraPerfil=  navigationView.getHeaderView(0).findViewById(R.id.barraProfileImage);
        barraCorreo=  navigationView.getHeaderView(0).findViewById(R.id.barraProfileEmail);

        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth =	FirebaseAuth.getInstance();

        cargarInfoBarra();

        bNueva = (Button) findViewById(R.id.buttonNueva);
        bNueva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NuevaActivity.class);
                startActivity(intent);
            }
        });

        bGrupal = (Button) findViewById(R.id.buttonGrupal);
        bGrupal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NuevoGrupalActivity.class);
                startActivity(intent);
            }
        });

        bViaje = (Button) findViewById(R.id.buttonViaje);
        bViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OpcionesViajeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            startActivity(new Intent(this,MainActivity.class));
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public	boolean onCreateOptionsMenu(Menu	menu){
        getMenuInflater().inflate(R.menu.menu,	menu);
        return	true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClicked =	item.getItemId();
        if(itemClicked ==	R.id.menuLogOut){
            mAuth.signOut();
            Intent	intent	=	new	Intent(InicioActivity.this,	MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return	super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_perfil) {
            Intent intent = new Intent(getApplicationContext(),ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_amigos) {
            Intent intent = new Intent(getApplicationContext(),AmigosActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_mis_rutas) {
            Intent intent = new Intent(getApplicationContext(),MisRutasActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_mis_viajes_grupales) {
            Intent intent = new Intent(getApplicationContext(),MisGrupalesActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Descripción: carga la información del
     * usuarios en el header de la barra
     * @param
     * @return
     */
    private void cargarInfoBarra(){
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null) {
            barraCorreo.setText(user.getEmail());
            barraNombre.setText(user.getDisplayName());
            mStorageRef.child(user.getUid())
                    .getDownloadUrl().addOnSuccessListener(this, new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) { // guardando esta uri no es necesario volver a buscarla en servidor
                    Glide.with(InicioActivity.this)
                            .load(uri)
                            .fitCenter()
                            .centerCrop()
                            .into(barraPerfil);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });
        }
    }

}
