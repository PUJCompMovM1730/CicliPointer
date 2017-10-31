package co.edu.javeriana.ciclipointer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import entities.Recorrido;
import entities.RecorridoUsuario;
import entities.RutaProgramada;
import entities.Ubicacion;
import entities.Usuario;

public class ProgramadoActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap = null;
    private EditText dirección;
    private TextView distanci, tiempo;
    private LatLng origen = null, desti = null;

    private Marker bikeActual, destinoAzul;

    private boolean advanceLooking = false;
    private Button avanzada = null, volver = null, rutas;
    private Button iniciarRecorrido = null, volverLista = null;
    private PlaceAutocompleteFragment autocompleteFragment = null;
    private DirectionsResult results = null;
    private ListView listRutas = null;
    private List<String> listRutasString = new ArrayList<String>();
    private TextView rutaInfo;
    private Polyline poly;
    private int routeSelected = 0;

    public static final double lowerLeftLatitude = 4.469636;
    public static final double lowerLeftLongitude = -74.177171;
    public static final double upperRightLatitude = 4.817991;
    public static final double upperRigthLongitude = -74.001390;

    private static final String GOOGLE_KEY_SERVER = "AIzaSyCs6lRlHIp2XrYyJJOCeMAk-Bd-g5ISUBE";

    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programado);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth =	FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();

        distanci = (TextView) findViewById(R.id.textViewDistancia);
        tiempo = (TextView) findViewById(R.id.textViewTiempo);



        // Escucha enter al terminar de escribir destino
        dirección = (EditText) findViewById(R.id.texto);
        dirección.setImeActionLabel("Custom text", KeyEvent.KEYCODE_ENTER);
        dirección.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    buscarDireccion();
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(dirección.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        busquedaAvanzada();

        avanzada = (Button) findViewById(R.id.buttonHelp);
        volver = (Button) findViewById(R.id.buttonBack);
        rutas = (Button) findViewById(R.id.buttonRutas);
        iniciarRecorrido = (Button) findViewById(R.id.buttonIniciar);
        volverLista = (Button) findViewById(R.id.buttonBackList);
        rutaInfo = (TextView) findViewById(R.id.rutaInfo);
        rutaInfo.setVisibility(View.GONE);
        volver.setVisibility(View.GONE);
        volverLista.setVisibility(View.GONE);
        iniciarRecorrido.setVisibility(View.GONE);


        // si el usuario desea buscar de forma avanzada
        avanzada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dirección.setVisibility(View.GONE);
                avanzada.setVisibility(View.GONE);
                autocompleteFragment.getView().setVisibility(View.VISIBLE);
                volver.setVisibility(View.VISIBLE);
            }
        });

        // si usuario desea volver a busqeuda normal
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dirección.setVisibility(View.VISIBLE);
                avanzada.setVisibility(View.VISIBLE);
                autocompleteFragment.getView().setVisibility(View.GONE);
                volver.setVisibility(View.GONE);
                dirección.setText("");
                destinoAzul.setVisible(false);
                desti = null;
            }
        });

        listRutas = (ListView) findViewById(R.id.listRutas);
        listRutas.setVisibility(View.GONE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, listRutasString);
        listRutas.setAdapter(adapter);
        listRutas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                routeSelected = i;
                if (removePolyline())
                    addPolyline(results, i);
                iniciarRecorrido.setVisibility(View.VISIBLE);
            }
        });
        // inicia recorrido con origen y destino dado
        rutas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean complete = true;

                if (origen == null) {
                    Toast.makeText(ProgramadoActivity.this, "Especifique un origen", Toast.LENGTH_SHORT).show();
                    complete = false;
                }
                if (desti == null) {
                    Toast.makeText(ProgramadoActivity.this, "Especifique un destino", Toast.LENGTH_SHORT).show();
                    complete = false;
                }
                if (complete) {
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
                        listRutasString.clear();
                        if (results.routes.length > 0) {
                            for (int i = 0; i < results.routes.length; i++) {
                              /*  System.out.println("esto: "+i+" - "+results.routes[i].legs[0].startAddress);
                                System.out.println("esto: "+i+" - "+results.routes[i].legs[0].distance);
                                System.out.println("esto: "+i+" - "+results.routes[i].legs[0].endAddress);
                                System.out.println("esto: "+i+" - "+results.routes[i].legs[0].arrivalTime);
                                System.out.println("esto: "+i+" - "+results.routes[i].legs[0].duration);*/

                                String valor = (i + 1) + ". Distancia a recorrer: " + results.routes[i].legs[0].distance +
                                        " Duración: " + results.routes[i].legs[0].duration;
                                listRutasString.add(valor);
                            }
                            listRutas.setVisibility(View.VISIBLE);
                            rutaInfo.setVisibility(View.VISIBLE);
                            volverLista.setVisibility(View.VISIBLE);
                            rutas.setVisibility(View.GONE);
                            dirección.setVisibility(View.GONE);
                            avanzada.setVisibility(View.GONE);
                            autocompleteFragment.getView().setVisibility(View.GONE);
                            volver.setVisibility(View.GONE);
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(desti));
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                        } else
                            Toast.makeText(ProgramadoActivity.this, "No se encuentran rutas", Toast.LENGTH_SHORT).show();
                    } catch (com.google.maps.errors.ApiException e) {
                        e.printStackTrace();
                        Toast.makeText(ProgramadoActivity.this, "Error con el servidor", Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Toast.makeText(ProgramadoActivity.this, "Se perdió conexión", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ProgramadoActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        volverLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listRutas.setVisibility(View.GONE);
                rutaInfo.setVisibility(View.GONE);
                rutas.setVisibility(View.VISIBLE);
                volverLista.setVisibility(View.GONE);
                iniciarRecorrido.setVisibility(View.GONE);
                removePolyline();
                routeSelected = 0;
                if (!advanceLooking) {
                    dirección.setVisibility(View.VISIBLE);
                    avanzada.setVisibility(View.VISIBLE);
                } else {
                    autocompleteFragment.getView().setVisibility(View.VISIBLE);
                    volver.setVisibility(View.VISIBLE);
                }
            }
        });

        iniciarRecorrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                volverLista.setVisibility(View.GONE);
                listRutas.setVisibility(View.GONE);
                rutaInfo.setVisibility(View.GONE);
                iniciarRecorrido.setVisibility(View.GONE);
                distanci.setText("Distancia ruta: " + results.routes[routeSelected].legs[0].distance);
                tiempo.setText("Duración: " + results.routes[routeSelected].legs[0].duration);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(origen));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                if(origen!=null && desti!=null){

                    RutaProgramada ru = new RutaProgramada();
                    ru.setFecha(getIntent().getStringExtra("fecha"));
                    ru.setHora(getIntent().getStringExtra("hora"));
                    ru.setTipo(getIntent().getStringExtra("tipo"));
                    ru.setLatOrigen(origen.latitude);
                    ru.setLongOrigen(origen.longitude);
                    ru.setLatDestino(desti.latitude);
                    ru.setLongDestino(desti.longitude);
                    ru.setRuta(routeSelected);
                    crearRutaProgramada(ru);

                }


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
        Date date = new Date();
        // Add a marker in Sydney and move the camera
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.getUiSettings().setZoomControlsEnabled(true);


        LatLng o = new LatLng(4.628479, -74.064908);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(o));
        if (date.getHours() >= 6 && date.getHours() < 18) {
            mMap.setMapStyle(MapStyleOptions
                    .loadRawResourceStyle(this, R.raw.style_json));
            bikeActual = mMap.addMarker(new MarkerOptions()
                    .position(o)
                    .title("Posición origen")
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.bici)));
            bikeActual.setVisible(false);

        } else {
            mMap.setMapStyle(MapStyleOptions
                    .loadRawResourceStyle(this, R.raw.style_night_json));
            bikeActual = mMap.addMarker(new MarkerOptions()
                    .position(o)
                    .title("Posición origen")
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.bicinight)));
            bikeActual.setVisible(false);
        }

        destinoAzul = mMap.addMarker(new MarkerOptions()
                .position(o)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        destinoAzul.setVisible(false);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Toast.makeText(NuevaActivity.this, "CLICK EN " + latLng.toString(), Toast.LENGTH_SHORT).show();
                if (mMap != null) {
                    if(origen==null){
                        origen = latLng;
                        bikeActual.setPosition(origen);
                        bikeActual.setVisible(true);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(origen));
                    }else if(desti==null){
                        desti = latLng;
                        destinoAzul.setPosition(desti);
                        destinoAzul.setVisible(true);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(desti));
                    }
                }
            }
        });
    }




    /**
     * Descripción: busca las coordenadas
     * de un lugar dado un nombre, y ubica
     * el mapa en el destino.
     * @param
     * @return
     */
    private void buscarDireccion(){
        Geocoder mGeocoder = new Geocoder(getBaseContext());
        String addressString = dirección.getText().toString();
        if (!addressString.isEmpty()) {
            try {
                List<Address> addresses = mGeocoder.getFromLocationName(
                        addressString, 4,
                        lowerLeftLatitude,
                        lowerLeftLongitude,
                        upperRightLatitude,
                        upperRigthLongitude);
                if (addresses != null && !addresses.isEmpty()) {
                    Address addressResult = addresses.get(0);
                    if (mMap != null) {
                        if(origen==null){
                            origen = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                            bikeActual.setPosition(origen);
                            bikeActual.setVisible(true);
                            bikeActual.setTitle(addressResult.getFeatureName());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(origen));
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                            advanceLooking = false;
                        }else if(desti==null) {
                            desti = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                            destinoAzul.setPosition(desti);
                            destinoAzul.setVisible(true);
                            destinoAzul.setTitle(addressResult.getFeatureName());
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(desti));
                            mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                            advanceLooking = false;
                        }
                    }
                } else {
                    Toast.makeText(ProgramadoActivity.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();
                    destinoAzul.setVisible(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(ProgramadoActivity.this, "La dirección esta vacía", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Descripción: opción de busqueda con
     * placeautocompletefragment de google,
     * y ubica el mapa en el destino.
     * @param
     * @return
     */
    private void busquedaAvanzada(){
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.getView().setVisibility(View.GONE);
        autocompleteFragment.setBoundsBias(new LatLngBounds(
                new LatLng(lowerLeftLongitude, lowerLeftLatitude),
                new LatLng(upperRigthLongitude, upperRightLatitude)));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if (mMap != null) {
                    if(origen == null){
                        origen = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                        bikeActual.setPosition(origen);
                        bikeActual.setVisible(true);
                        bikeActual.setTitle(place.getName().toString());
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(origen));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                        advanceLooking = true;
                    }else if(desti == null) {
                        desti = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                        destinoAzul.setPosition(desti);
                        destinoAzul.setVisible(true);
                        destinoAzul.setTitle(place.getName().toString());
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(desti));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                        advanceLooking = true;
                    }
                }
                // Log.i("MAP", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(ProgramadoActivity.this, "Error cargando destino", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /*
           Crea contexto con time outs
        */
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


    private void crearRutaProgramada(RutaProgramada ru){
        String key = myRef.child("programados/"+user.getUid()).push().getKey();
        myRef.child("programados/"+user.getUid()+"/"+key)
                .setValue(ru).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ProgramadoActivity.this, "Guardando futuro recorrido", Toast.LENGTH_LONG).show();
                Intent in = new Intent(getApplicationContext(),InicioActivity.class);
                startActivity(in);
            }
        });
    }
}
