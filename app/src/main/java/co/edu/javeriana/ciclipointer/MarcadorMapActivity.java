package co.edu.javeriana.ciclipointer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import entities.Marcador;
import entities.RutaProgramada;

public class MarcadorMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap = null;
    private EditText dirección;
    private TextView distanci, tiempo;
    private LatLng origen = null;

    private Marker bikeActual, destinoAzul;

    private boolean advanceLooking = false;
    private Button avanzada = null, volver = null;
    private Button iniciarRecorrido = null;
    private PlaceAutocompleteFragment autocompleteFragment = null;
    private DirectionsResult results = null;
    private Polyline poly;

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
        setContentView(R.layout.activity_marcador_map);
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
        iniciarRecorrido = (Button) findViewById(R.id.buttonIniciar);
        volver.setVisibility(View.GONE);
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
            }
        });




        iniciarRecorrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                iniciarRecorrido.setVisibility(View.GONE);
                // acá se guarda en bd y vuelve
                mMap.moveCamera(CameraUpdateFactory.newLatLng(origen));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                if(origen!=null ){
                    Marcador m = new Marcador();
                    m.setNombre(getIntent().getStringExtra("nombre"));
                    m.setDescripción(getIntent().getStringExtra("descripcion"));
                    m.setFecha(getIntent().getStringExtra("fecha"));
                    m.setLatitud(origen.latitude);
                    m.setLongitud(origen.longitude);
                    String key = myRef.child("marcadores").push().getKey();
                    myRef.child("marcadores/"+key)
                            .setValue(m).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MarcadorMapActivity.this, "Se guardó marcador", Toast.LENGTH_LONG).show();
                            Intent in = new Intent(getApplicationContext(),InicioTurismo.class);
                            startActivity(in);
                        }
                    });

                }else{
                    Toast.makeText(MarcadorMapActivity.this, "Seleccione un destino", Toast.LENGTH_SHORT).show();
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

                        origen = latLng;
                        bikeActual.setPosition(origen);
                        bikeActual.setVisible(true);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(origen));
                        iniciarRecorrido.setVisibility(View.VISIBLE);

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
                        }
                    }
                } else {
                    Toast.makeText(MarcadorMapActivity.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();
                    destinoAzul.setVisible(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(MarcadorMapActivity.this, "La dirección esta vacía", Toast.LENGTH_SHORT).show();
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
                    }
                }
                // Log.i("MAP", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(MarcadorMapActivity.this, "Error cargando destino", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
