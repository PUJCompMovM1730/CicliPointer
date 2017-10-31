package co.edu.javeriana.ciclipointer;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import entities.InvitacionGrupal;
import entities.RutaGrupal;

public class MapInfoActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap = null;
    private TextView distanci, tiempo;


    private LatLng origen = null, desti = null;
    private Marker bikeActual, destinoAzul;

    private Button cancelar = null;
    private Polyline poly;
    private int routeSelected = 0;


    private static final String GOOGLE_KEY_SERVER = "AIzaSyCs6lRlHIp2XrYyJJOCeMAk-Bd-g5ISUBE";

    private DatabaseReference myRef,ref2;
    private FirebaseAuth mAuth;
    private FirebaseUser user = null;
    private Map<String,Marker> usuarios = new HashMap<String,Marker>();
    private Map<String,Boolean> usuariosExistente = new HashMap<String,Boolean>();
    private DirectionsResult results = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_info);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mAuth =	FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();



        distanci = (TextView) findViewById(R.id.textViewDistancia);
        tiempo = (TextView) findViewById(R.id.textViewTiempo);



        cancelar = (Button) findViewById(R.id.buttonCancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MisGrupalesInvitadoActivity.class);
                intent.putExtra("anfitrion",getIntent().getStringExtra("anfitrion"));
                intent.putExtra("grupal",getIntent().getStringExtra("grupal"));
                intent.putExtra("key",getIntent().getStringExtra("key"));
                startActivity(intent);
            }
        });


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final Date date = new Date();
        // Add a marker in Sydney and move the camera
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.getUiSettings().setZoomControlsEnabled(true);

        InvitacionGrupal invitacion = new InvitacionGrupal();
        invitacion.setViajeGrupal(getIntent().getStringExtra("grupal"));
        invitacion.setAnfritrion(getIntent().getStringExtra("anfitrion"));
        myRef.child("grupales/"+invitacion.getAnfritrion()+"/"+invitacion.getViajeGrupal())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        RutaGrupal ru = dataSnapshot.getValue(RutaGrupal.class);
                        origen = new LatLng(ru.getLatOrigen(),ru.getLongOrigen());
                        desti = new LatLng(ru.getLatDestino(),ru.getLongDestino());
                        routeSelected = ru.getRuta();
                        if (date.getHours() >= 6 && date.getHours() < 18) {
                            mMap.setMapStyle(MapStyleOptions
                                    .loadRawResourceStyle(MapInfoActivity.this, R.raw.style_json));
                            bikeActual = mMap.addMarker(new MarkerOptions()
                                    .position(origen)
                                    .title("Posición origen")
                                    .icon(BitmapDescriptorFactory
                                            .fromResource(R.drawable.bici)));
                            // bikeActual.setVisible(false);

                        } else {
                            mMap.setMapStyle(MapStyleOptions
                                    .loadRawResourceStyle(MapInfoActivity.this, R.raw.style_night_json));
                            bikeActual = mMap.addMarker(new MarkerOptions()
                                    .position(origen)
                                    .title("Posición origen")
                                    .icon(BitmapDescriptorFactory
                                            .fromResource(R.drawable.bicinight)));
                            // bikeActual.setVisible(false);
                        }

                        destinoAzul = mMap.addMarker(new MarkerOptions()
                                .position(desti)
                                .icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                        // destinoAzul.setVisible(false);


                        enMovimiento();
                        distanci.setText("Distancia ruta: " + results.routes[routeSelected].legs[0].distance);
                        tiempo.setText("Duración: " + results.routes[routeSelected].legs[0].duration);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(origen));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MapInfoActivity.this, "Error cargando origen y destino", Toast.LENGTH_SHORT).show();
                    }
                });



    }


    /**
     * Descripción: crea contexto que pedirá
     * la ruta de un lugar.
     * @param
     * @return
     */
    private GeoApiContext getGeoContext(){
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3).setApiKey(GOOGLE_KEY_SERVER).setConnectTimeout(1,
                TimeUnit.SECONDS).setReadTimeout(1, TimeUnit.SECONDS).setWriteTimeout(1, TimeUnit.SECONDS);
    }

    /**
     * Descripción: dibujta la ruta dada
     * en el mapa según la opción seleccionada
     * por el usuario.
     * @param results, r
     * @return
     */
    private void addPolyline(DirectionsResult results, int r) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[r].overviewPolyline.getEncodedPath());
        poly = mMap.addPolyline(new PolylineOptions().addAll(decodedPath)
                .width(2)
                .color(Color.RED));

    }

    /**
     * Descripción: elimina la ruta dibujada
     * en el mapa.
     * @param
     * @return
     */
    private boolean removePolyline(){
        try{
            if(poly!=null){
                poly.remove();
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * Descripción: actualiza polyline
     * según la actualización de la localización
     * de un usuario.
     * @param
     * @return
     */
    private void enMovimiento(){
        boolean complete = true;
        if(desti == null || origen == null){
            complete = false;
        }
        if(complete) {
            DateTime now = new DateTime();
            try {
                results = DirectionsApi.newRequest(getGeoContext())
                        .mode(TravelMode.DRIVING)// preguntar si hacer dos solictudes con
                        // biclycling y si vacia con driving o solo driving
                        .origin(new com.google.maps.model.LatLng(origen.latitude, origen.longitude))
                        .destination(new com.google.maps.model.LatLng(desti.latitude, desti.longitude))
                        .alternatives(true)
                        .departureTime(now)
                        .await();
                if (results.routes.length > 0) {
                    if (results.routes.length > routeSelected) {
                        Long x = results.routes[routeSelected].legs[0].distance.inMeters;
                        distanci.setText("Distancia ruta: " + results.routes[routeSelected].legs[0].distance);
                        tiempo.setText("Duración: " + results.routes[routeSelected].legs[0].duration);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(origen));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                        if (removePolyline())
                            addPolyline(results, routeSelected);

                    } else {
                        Long x = results.routes[0].legs[0].distance.inMeters;
                        distanci.setText("Distancia ruta: " + results.routes[0].legs[0].distance);
                        tiempo.setText("Duración: " + results.routes[0].legs[0].duration);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(origen));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                        if (removePolyline())
                            addPolyline(results, 0);
                    }
                }
                else {
                    if (removePolyline()) {
                        distanci.setText("Espere un momento");
                        tiempo.setText("");
                    }
                }
            } catch (com.google.maps.errors.ApiException e) {
                tiempo.setText("");
                distanci.setText("Vuelva a iniciar recorrido");
                e.printStackTrace();
                Toast.makeText(MapInfoActivity.this, "Error con el servidor", Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                tiempo.setText("");
                distanci.setText("Vuelva a iniciar recorrido");
                e.printStackTrace();
                Toast.makeText(MapInfoActivity.this, "Se perdió conexión", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                tiempo.setText("");
                distanci.setText("Vuelva a iniciar recorrido");
                e.printStackTrace();
                Toast.makeText(MapInfoActivity.this, "Error en la ruta", Toast.LENGTH_SHORT).show();
            }

        }
    }




}
