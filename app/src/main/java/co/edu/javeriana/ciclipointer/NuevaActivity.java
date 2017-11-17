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
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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

import entities.Marcador;
import entities.MisRuta;
import entities.Recorrido;
import entities.RecorridoUsuario;
import entities.Ubicacion;
import entities.Usuario;

public class NuevaActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap = null;
    private EditText dirección;
    private TextView distanci, tiempo;

    private LocationRequest mLocationRequest; // prender loc si esta apagada
    private LocationCallback mLocationCallback; // objeto que permite suscripción a localización
    final int REQUEST_CHECK_SETTINGS = 4;
    final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 3;
    public final static double RADIUS_OF_EARTH_KM = 6371;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location location = null;
    private LatLng origen, desti = null;
    private String Norigen = "Centro Comercial Parque La Colina", Ndestino = null;
    private Marker bikeActual, destinoAzul, tienda1, tienda2, tienda3, tienda4, tienda5;
    public double longitudCityBike = -74.052350, latitudCityBike = 4.732924;
    public double longituBabilonia = -74.033026, latitudBabilonia = 4.743359;
    public double longituBeneton = -74.052110, latitudBeneton = 4.731317;
    public double longituCastillo = -74.030582, latitudCastillo = 4.698325;
    public double longituBikers = -74.036585, latitudBikers = 4.719675;
    private View popup = null;
    private boolean first = true, advanceLooking = false;
    private ImageView move = null;
    private Button avanzada = null, volver = null, rutas;
    private Button iniciarRecorrido = null, volverLista = null,
            cancelar = null, guardar = null;
    private PlaceAutocompleteFragment autocompleteFragment = null;
    private DirectionsResult results = null;
    private ListView listRutas = null;
    private List<String> listRutasString = new ArrayList<String>();
    private TextView rutaInfo;
    private Polyline poly;
    private int routeSelected = 0;
    private boolean recorriendo = false,dia = true;

    public static final double lowerLeftLatitude = 4.469636;
    public static final double lowerLeftLongitude = -74.177171;
    public static final double upperRightLatitude = 4.817991;
    public static final double upperRigthLongitude = -74.001390;

    private static final String GOOGLE_KEY_SERVER = "AIzaSyCs6lRlHIp2XrYyJJOCeMAk-Bd-g5ISUBE";

    private DatabaseReference myRef,ref2;
    private FirebaseAuth mAuth;
    private FirebaseUser user = null;
    private Map<String,Marker> usuarios = new HashMap<String,Marker>();
    private Map<String,Boolean> usuariosExistente = new HashMap<String,Boolean>();
    private Map<String,Marker> marcadores = new HashMap<String,Marker>();
    private Map<String,Boolean> marcadoresExistente = new HashMap<String,Boolean>();
    private List<String> borrar = new ArrayList<>();
    private Recorrido re;
    private double kmOirginal = 0, pos = 0,
                    kmDestino = 0;
    private boolean primeraKm = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mLocationRequest = createLocationRequest();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        /* Move boton que permite volver a ubicación actual.*
        / Revisa si tiene permisos, sino los pide.
         */

        mAuth =	FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference();

        distanci = (TextView) findViewById(R.id.textViewDistancia);
        tiempo = (TextView) findViewById(R.id.textViewTiempo);

        move = (ImageView) findViewById(R.id.imageViewMove);
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheck == 0) {
                    if (location != null && mMap != null) {
                        origen = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(origen));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    }
                } else
                    solicitudPermiso();
            }
        });
        // acá el callback tiene la localización actualizada
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                location = locationResult.getLastLocation();
                // Log.i("LOCATION",	"Location	update	in	the	callback:	"	+	location);
                localizarActual();
                calculoDistancia();
                if (recorriendo){
                    enMovimiento();
                }
                updateLocalizacion(); //envía loc a firebase
                getUsersNear();//busca usuarios cercanos y muestra
                getMarcadoresNear();
                borrarMarcadoresExpirados();
            }
        };

        // revisa si tiene permisos, sino los pide al rutas.
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == 0) {
            localizacion();
        } else
            solicitudPermiso();


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
        cancelar = (Button) findViewById(R.id.buttonCancelar);
        rutaInfo = (TextView) findViewById(R.id.rutaInfo);
        rutaInfo.setVisibility(View.GONE);
        volver.setVisibility(View.GONE);
        volverLista.setVisibility(View.GONE);
        iniciarRecorrido.setVisibility(View.GONE);
        cancelar.setVisibility(View.GONE);
        guardar = (Button) findViewById(R.id.buttonGuardar);
        guardar.setVisibility(View.GONE);

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
                Ndestino = null;
                calculoDistancia();
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
                if (desti == null) {
                    Toast.makeText(NuevaActivity.this, "Especifique un destino", Toast.LENGTH_SHORT).show();
                    complete = false;
                }
                if (origen == null) {
                    Toast.makeText(NuevaActivity.this, "Especifique un origen", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(NuevaActivity.this, "No se encuentran rutas", Toast.LENGTH_SHORT).show();
                    } catch (com.google.maps.errors.ApiException e) {
                        e.printStackTrace();
                        Toast.makeText(NuevaActivity.this, "Error con el servidor", Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Toast.makeText(NuevaActivity.this, "Se perdió conexión", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(NuevaActivity.this, "Error", Toast.LENGTH_SHORT).show();
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
                cancelar.setVisibility(View.VISIBLE);
                iniciarRecorrido.setVisibility(View.GONE);
                recorriendo = true;
                distanci.setText("Distancia ruta: " + results.routes[routeSelected].legs[0].distance);
                tiempo.setText("Duración: " + results.routes[routeSelected].legs[0].duration);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(origen));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                iniciarKm();

                Date date = new Date();

                re = new Recorrido();
                String kim = results.routes[routeSelected].legs[0].distance.humanReadable;
                StringTokenizer tok = new StringTokenizer(kim," ");
                //System.err.println("valor es km"+tok.nextToken());
                re.setKm(Double.parseDouble(tok.nextToken()));
                String tim = results.routes[routeSelected].legs[0].duration.humanReadable;
                tok = new StringTokenizer(tim," ");
                re.setTiempo(Integer.parseInt(tok.nextToken()));
               /* System.err.println("valor estiempo"+tok.nextToken());
                System.err.println("valor date hora"+date.getHours()+":"+date.getMinutes()+":"+date.getSeconds());
                System.err.println("valor date fecha"+(date.getYear()+1900)+"/"+
                        (date.getMonth()+1)+"/"+(date.getDay()+22));*/
                re.setTipo("Personal");
                re.setHoraInicio(date.getHours()+":"+date.getMinutes()+":"+date.getSeconds());
                re.setFechaInicio((date.getYear()+1900)+"/"+
                        (date.getMonth()+1)+"/"+(date.getDate()));
                Geocoder mGeocoder = new Geocoder(getBaseContext());
                List<Address> addresses = null;
                try {
                    addresses = mGeocoder.getFromLocation(origen.latitude,origen.longitude,4);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address addressResult = addresses.get(0);
                        //System.err.println("valor es "+addressResult.getAddressLine(0));
                        re.setOrigen(addressResult.getAddressLine(0));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(dirección.getText().toString().equals("")){
                    try {
                        addresses = mGeocoder.getFromLocation(desti.latitude,desti.longitude,4);
                        if (addresses != null && !addresses.isEmpty()) {
                            Address addressResult = addresses.get(0);
                           // System.err.println("valor es "+addressResult.getAddressLine(0));
                            if(addressResult.getAddressLine(0)!=null)
                                re.setDestino(addressResult.getAddressLine(0));
                            else
                                re.setDestino("");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    re.setDestino(dirección.getText().toString());
                }


            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dirección.setVisibility(View.VISIBLE);
                dirección.setText("");
                removePolyline();
                desti = null;
                avanzada.setVisibility(View.VISIBLE);
                rutas.setVisibility(View.VISIBLE);
                cancelar.setVisibility(View.GONE);
                destinoAzul.setVisible(false);
                distanci.setText("");
                tiempo.setText("");
                routeSelected = 0;
                recorriendo = false;
                guardar.setVisibility(View.GONE);
                kmDestino = 0;
                primeraKm = true;
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GuardarRuta();
                dirección.setVisibility(View.VISIBLE);
                dirección.setText("");
                avanzada.setVisibility(View.VISIBLE);
                rutas.setVisibility(View.VISIBLE);
                cancelar.setVisibility(View.GONE);
                guardar.setVisibility(View.GONE);
                destinoAzul.setVisible(false);
                distanci.setText("");
                tiempo.setText("");
                routeSelected = 0;
                recorriendo = false;
                kmDestino = 0;
                primeraKm = true;


            }
        });
    }

    private void GuardarRuta() {
        MisRuta ruta = new MisRuta();
        ruta.setLatDestino(desti.latitude);
        ruta.setLongDestino(desti.longitude);
        ruta.setLatOrigen(origen.latitude);
        ruta.setLongOrigen(origen.longitude);
        ruta.setRuta(routeSelected);
        ruta.setOrigen(re.getOrigen());
        ruta.setDestino(re.getDestino());
        String key = myRef.child("rutas/"+user.getUid()).push().getKey();
        myRef.child("rutas/"+user.getUid()+"/"+key).setValue(ruta).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(NuevaActivity.this, "Se guardo la ruta", Toast.LENGTH_SHORT).show();
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


        origen = new LatLng(4.628479, -74.064908);
        if (date.getHours() >= 6 && date.getHours() < 18) {
            dia = true;
            if (move != null)
                move.setImageResource(R.drawable.movelocation);
            mMap.setMapStyle(MapStyleOptions
                    .loadRawResourceStyle(this, R.raw.style_json));
            bikeActual = mMap.addMarker(new MarkerOptions()
                    .position(origen)
                    .title("Posición origen")
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.bici)));
            bikeActual.setVisible(false);

        } else {
            dia = false;
            if (move != null)
                move.setImageResource(R.drawable.movelocationnight);
            mMap.setMapStyle(MapStyleOptions
                    .loadRawResourceStyle(this, R.raw.style_night_json));
            bikeActual = mMap.addMarker(new MarkerOptions()
                    .position(origen)
                    .title("Posición origen")
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.bicinight)));
            bikeActual.setVisible(false);
        }

        destinoAzul = mMap.addMarker(new MarkerOptions()
                .position(origen)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        destinoAzul.setVisible(false);


        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                if (popup == null) {
                    popup = getLayoutInflater().inflate(R.layout.popupmaps, null);
                }

                TextView tv = (TextView) popup.findViewById(R.id.title);
                tv.setText(marker.getTitle());
                tv = (TextView) popup.findViewById(R.id.snippet);
                tv.setText(marker.getSnippet());
                return popup;
            }
        });
        LatLng t1 = new LatLng(latitudCityBike, longitudCityBike);
        tienda1 = mMap.addMarker(new MarkerOptions()
                .position(t1)
                .title("City Bike ")
                .snippet("Lunes - Sábado: 10:00-19:00")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.citybike)));

        LatLng t2 = new LatLng(latitudBeneton, longituBeneton);
        tienda2 = mMap.addMarker(new MarkerOptions()
                .position(t2)
                .title("Bicicletas Beneton")
                .snippet("Lunes - Sábado: 8:00-19:00 \n" +
                        "Domingo: 8:00-13:00")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.beneton)));

        LatLng t3 = new LatLng(latitudBikers, longituBikers);
        tienda3 = mMap.addMarker(new MarkerOptions()
                .position(t3)
                .title("Speed Bikers")
                .snippet("Lunes - Viernes: 10:30-19:00 \n" +
                        "Sábado: 11:00-18:00 \n" +
                        "Domingo: cerrado")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.bikers)));

        LatLng t4 = new LatLng(latitudBabilonia, longituBabilonia);
        tienda4 = mMap.addMarker(new MarkerOptions()
                .position(t4)
                .title("Bicicleteria Babilonia")
                .snippet("Lunes - Domingo: 8:00-20:00")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.babilonia)));

        LatLng t5 = new LatLng(latitudCastillo, longituCastillo);
        tienda5 = mMap.addMarker(new MarkerOptions()
                .position(t5)
                .title("Bicicletas Castillo")
                .snippet("Lunes - Sábado: 9:00-19:00 \n" +
                        "Domingo: 9:30-14:30")
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.castillo)));


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
               // Toast.makeText(NuevaActivity.this, "CLICK EN " + latLng.toString(), Toast.LENGTH_SHORT).show();
                if (mMap != null) {
                    // System.out.println("destino es "+desti.latitude+ " - "+ desti.longitude);
                    if(!recorriendo) {
                        desti = latLng;
                        destinoAzul.setPosition(desti);
                        destinoAzul.setVisible(true);
                        destinoAzul.setTitle("Destino");
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(desti));
                        calculoDistancia();
                    }

                }
            }
        });
    }



    /**
     * Descripción: actualiza el mapa con la actualización actual.
     * @param
     * @return
     */
        private void localizarActual(){
        if(mMap != null && location!=null){
            origen = new LatLng(location.getLatitude(), location.getLongitude());
            bikeActual.setPosition(origen);
            bikeActual.setVisible(true);
            if(first){
                mMap.moveCamera(CameraUpdateFactory.newLatLng(origen));
                first = false;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    localizacion();
                } else {
                    Toast.makeText(getApplicationContext(),"Permiso denegado localización", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    /**
     * Descripción: pide permiso para acceder
     * a la localización.
     * @param
     * @return
     */
    private void solicitudPermiso (){
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Se necesita el permiso para poder mostrar los contactos!", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_ACCESS_FINE_LOCATION);


        }
    }

    /**
     * Descripción: prende localización
     * si esta apagada
     * @param
     * @return
     */
    private void localizacion(){
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == 0){
            // se pide localización usuario en la configuración
            LocationSettingsRequest.Builder builder	=	new
                    LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
            SettingsClient client	 =	LocationServices.getSettingsClient(NuevaActivity.this);
            Task<LocationSettingsResponse> task	=	client.checkLocationSettings(builder.build());
            task.addOnSuccessListener(NuevaActivity.this,	 new	OnSuccessListener<LocationSettingsResponse>()
            {
                @Override
                public	void	onSuccess(LocationSettingsResponse locationSettingsResponse)	 {
                    startLocationUpdates();	 //Todas las condiciones para	recibir localizaciones
                }
            });

            // paso extra en caso de estar apagado localización
            task.addOnFailureListener(NuevaActivity.this,	 new	OnFailureListener()	 {
                @Override
                public	void	onFailure(@NonNull Exception	 e)	{
                    int statusCode =	((ApiException)	e).getStatusCode();
                    switch	(statusCode)	{
                        case	CommonStatusCodes.RESOLUTION_REQUIRED:
                            //	Location	settings	are	not	satisfied,	but	this	can	be	fixed	by	showing	the	user	a	dialog.
                            try	{//	Show	the	dialog	by	calling	startResolutionForResult(),	and	check	the	result	in	onActivityResult().
                                ResolvableApiException resolvable	 =	(ResolvableApiException)	 e;
                                resolvable.startResolutionForResult(NuevaActivity.this,
                                        REQUEST_CHECK_SETTINGS);// lanza dialogo para encender localización
                            }	catch	(IntentSender.SendIntentException sendEx)	{
                                //	Ignore	the	error.
                            }	break;
                        case	LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //	Location	settings	are	not	satisfied.	No	way	to	fix	the	settings	so	we	won't	show	the	dialog.
                            break;
                    }
                }
            });
        }
    }

    /**
     * Descripción: pide localización de usuario
     * cada 10 segundos.
     * @param
     * @return
     */
    protected	LocationRequest createLocationRequest()	 {
        LocationRequest mLocationRequest =	new	LocationRequest();
        mLocationRequest.setInterval(10000);	 //tasa de	refresco en	milisegundos
        mLocationRequest.setFastestInterval(5000);	 //máxima tasa de	refresco
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return	mLocationRequest;
    }

    /**
     * Descripción: revisa permisos
     * y pide localización.
     * @param
     * @return
     */
    private	void	startLocationUpdates()	 {
        if	(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)	 ==
                PackageManager.PERMISSION_GRANTED)	 {//Verificación de	permiso!!
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback,null);
        }
    }

    /**
     * Descripción: resultado del dialogo que
     * prende localización, ve si esta o no prendida.
     * @param
     * @return
     */
    @Override
    protected	void	onActivityResult(int requestCode,	 int resultCode,	 Intent data)	 {
        switch	(requestCode)	 {
            case	REQUEST_CHECK_SETTINGS:	 {
                if	(resultCode ==	RESULT_OK)	 {
                    startLocationUpdates();	 	//Se	encendió la	localización!!!
                }	else	{
                    Toast.makeText(this,
                            "Sin	acceso a	localización,	hardware	deshabilitado!",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    /**
     * Descripción: calcula la distancia recta
     * a un destino dado.
     * @param
     * @return
     */
    public	double	distance(double	 lat1,	double	long1,	double	lat2,	double	long2)	{
        double	latDistance =Math.toRadians(lat1-lat2);
        double	lngDistance =Math.toRadians(long1 - long2);
        double	a	=	Math.sin(latDistance/2)*Math.sin(latDistance/2)
                +	Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))
                *	Math.sin(lngDistance/2)*Math.sin(lngDistance/2);
        double	c	=	2*Math.atan2(Math.sqrt(a),Math.sqrt(1-a));
        double	result	=RADIUS_OF_EARTH_KM*c;
        return	Math.round(result*100.0)/100.0;
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
                    desti = new LatLng(addressResult.getLatitude(), addressResult.getLongitude());
                    if (mMap != null) {
                        // System.out.println("destino es "+desti.latitude+ " - "+ desti.longitude);
                        Ndestino = addressResult.getFeatureName();
                        destinoAzul.setPosition(desti);
                        destinoAzul.setVisible(true);
                        destinoAzul.setTitle(addressResult.getFeatureName());
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(desti));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                        calculoDistancia();
                        advanceLooking = false;
                    }
                } else {
                    Toast.makeText(NuevaActivity.this, "Dirección no encontrada", Toast.LENGTH_SHORT).show();
                    destinoAzul.setVisible(false);
                    desti = null;
                    Ndestino = null;
                    calculoDistancia();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(NuevaActivity.this, "La dirección esta vacía", Toast.LENGTH_SHORT).show();
            destinoAzul.setVisible(false);
            desti = null;
            Ndestino = null;
            calculoDistancia();
        }
    }

    /**
     * Descripción: escribe la distancia calculada.
     * @param
     * @return
     */
    private void calculoDistancia(){
        if	(origen !=	null && desti!=null && !recorriendo){
            distanci.setText("Distancia es: "+
                    String.valueOf(distance(location.getLatitude(),location.getLongitude(),
                            desti.latitude,desti.longitude))+" km");
        }
        else
            distanci.setText("");
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
                desti = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                if (mMap != null) {
                    Ndestino =  place.getName().toString();
                    destinoAzul.setPosition(desti);
                    destinoAzul.setVisible(true);
                    destinoAzul.setTitle(place.getName().toString());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(desti));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                    calculoDistancia();
                    advanceLooking = true;
                }
                // Log.i("MAP", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(NuevaActivity.this, "Error cargando destino", Toast.LENGTH_SHORT).show();
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
                        llegue(x);
                        if(primeraKm) {
                            kmDestino = x.doubleValue()/1000;
                            primeraKm = false;
                        }
                        actualizarKm(x.doubleValue());

                    } else {
                        Long x = results.routes[0].legs[0].distance.inMeters;
                        distanci.setText("Distancia ruta: " + results.routes[0].legs[0].distance);
                        tiempo.setText("Duración: " + results.routes[0].legs[0].duration);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(origen));
                        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                        if (removePolyline())
                            addPolyline(results, 0);
                        llegue(x);
                        if(primeraKm) {
                            kmDestino = x.doubleValue() / 1000;
                            primeraKm = false;
                        }
                        actualizarKm(x.doubleValue());
                    }
                }
                else {
                    if (removePolyline()) {
                        distanci.setText("Espere un momento");
                        tiempo.setText("");
                    }
                }
            } catch (com.google.maps.errors.ApiException e) {
                recorriendo = false;
                tiempo.setText("");
                distanci.setText("Vuelva a iniciar recorrido");
                e.printStackTrace();
                Toast.makeText(NuevaActivity.this, "Error con el servidor", Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                recorriendo = false;
                tiempo.setText("");
                distanci.setText("Vuelva a iniciar recorrido");
                e.printStackTrace();
                Toast.makeText(NuevaActivity.this, "Se perdió conexión", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                recorriendo = false;
                tiempo.setText("");
                distanci.setText("Vuelva a iniciar recorrido");
                e.printStackTrace();
                Toast.makeText(NuevaActivity.this, "Error en la ruta", Toast.LENGTH_SHORT).show();
            }

        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,InicioActivity.class));
    }

    private void llegue(Long x){
        if(x<=65){
            guardar.setVisibility(View.VISIBLE);
            recorriendo = false;
            tiempo.setText("");
            distanci.setText("¡Has llegado!");
            removePolyline();
            registrarRecorrido();
            kmDestino = 0;
            primeraKm = true;
        }
    }

    private void updateLocalizacion(){
        myRef.child("ubicaciones/" + user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    String key = dataSnapshot.getKey();
                   // System.out.println("valor es key "+key);
                    Ubicacion ubi = dataSnapshot.getValue(Ubicacion.class);
                   // System.out.println("valor es"+ubi.getNombre());
                    updateUbicacion(ubi);
                }
                if (!dataSnapshot.hasChildren()) {
                    crearUbicacion();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NuevaActivity.this, "ERROR subiendo a base de datos" + databaseError
                        .getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void crearUbicacion(){
        Ubicacion ub = new Ubicacion();
        ub.setNombre(user.getDisplayName());
        ub.setLatitud(origen.latitude);
        ub.setLongitud(origen.longitude);
        myRef.child("ubicaciones/"+user.getUid())
        .setValue(ub);

    }

    private void updateUbicacion(Ubicacion ubi){
        myRef.child("ubicaciones/"+user.getUid())
                .setValue(ubi);
    }

    private void getUsersNear(){
        myRef.child("ubicaciones/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                  //  System.err.println("valor es key " + singleSnapshot.getKey());
                  //  System.out.println("valor es " + singleSnapshot.getValue());
                     if(!singleSnapshot.getKey().equals(user.getUid())) {
                        Ubicacion ubi = singleSnapshot.getValue(Ubicacion.class);
                         LatLng pos = new LatLng(ubi.getLatitud(),ubi.getLongitud());
                         double dis = distance(location.getLatitude(),location.getLongitude(),
                                 pos.latitude,pos.longitude);
                         //System.err.println("valor es dis: "+dis);
                         if(dis<=1.0) {
                           //  System.err.println("valor es dentro: "+ubi.getNombre());
                             if (usuarios.containsKey(singleSnapshot.getKey())) {
                                 Marker mark = usuarios.get(singleSnapshot.getKey());
                                 mark.setPosition(pos);
                                 usuarios.put(singleSnapshot.getKey(), mark);
                                 usuariosExistente.put(singleSnapshot.getKey(), true);
                             } else {
                                 if (dia) {
                                     Marker mark = mMap.addMarker(new MarkerOptions()
                                             .position(pos)
                                             .title(ubi.getNombre())
                                             .icon(BitmapDescriptorFactory
                                                     .fromResource(R.drawable.user)));
                                     usuarios.put(singleSnapshot.getKey(), mark);
                                     usuariosExistente.put(singleSnapshot.getKey(), true);
                                 } else {
                                     Marker mark = mMap.addMarker(new MarkerOptions()
                                             .position(pos)
                                             .title(ubi.getNombre())
                                             .icon(BitmapDescriptorFactory
                                                     .fromResource(R.drawable.usernight)));
                                     usuarios.put(singleSnapshot.getKey(), mark);
                                     usuariosExistente.put(singleSnapshot.getKey(), true);
                                 }

                             }
                         }
                    }
                }

                Map<String,Boolean> copia = new HashMap<String, Boolean>(usuariosExistente);
                for(String key:usuariosExistente.keySet()){
                    if(!usuariosExistente.get(key)){
                        //System.err.println("valor eliminado es "+usuarios.get(key).getTitle());
                        usuarios.get(key).remove();
                        usuarios.remove(key);
                        copia.remove(key);

                    }else{
                        copia.put(key,false);
                    }
                }
                usuariosExistente = new HashMap<String, Boolean>(copia);
              //  System.err.println("valor es usuariosExistentes 3"+usuariosExistente.size());
              //  System.err.println("valor es usuarios 3"+usuarios.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NuevaActivity.this, "ERROR cargando usuarios cercanos" + databaseError
                        .getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        myRef.child("ubicaciones/" + user.getUid()).removeValue();
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // revisa si tiene permisos, sino los pide al rutas.
        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == 0) {
            localizacion();
        } else
            solicitudPermiso();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRef.child("ubicaciones/" + user.getUid()).removeValue();
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private void registrarRecorrido(){
        myRef.child("recorridos/" + user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    String key = dataSnapshot.getKey();
                     //System.out.println("valor es key "+key);
                    List<Recorrido> m = new ArrayList<Recorrido>();
                     for(DataSnapshot d:singleSnapshot.getChildren()){// for de cada
                         Recorrido reco = d.getValue(Recorrido.class);
                         m.add(reco);
                         //System.out.println("valor es"+reco.getOrigen());
                     }
                    //acá llenamos lista de recorridos anteriores, y luego update
                    updateRecorrido(m);
                }
                if (!dataSnapshot.hasChildren()) {
                    crearRecorrido();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NuevaActivity.this, "ERROR subiendo a base de datos" + databaseError
                        .getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void crearRecorrido(){
        RecorridoUsuario reUs = new RecorridoUsuario();
        List<Recorrido> recorri = new ArrayList<>();
        recorri.add(re);
        reUs.setRecorridos(recorri);
        myRef.child("recorridos/"+user.getUid())
                .setValue(reUs);

    }

    private void updateRecorrido(List<Recorrido> m){
        m.add(re);
        RecorridoUsuario reUs = new RecorridoUsuario();
        reUs.setRecorridos(m);
        myRef.child("recorridos/"+user.getUid())
                .setValue(reUs);
    }

    private void iniciarKm(){
        myRef.child("users/"+user.getUid()).addListenerForSingleValueEvent(new	ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)	 {
                  /*  for	(DataSnapshot singleSnapshot :	dataSnapshot.getChildren())	{
                        System.out.println("valor es "+singleSnapshot.getValue());*/
                Usuario myUser =	dataSnapshot.getValue(Usuario.class);
                kmOirginal = myUser.getKm();
            }
            @Override
            public void onCancelled(DatabaseError databaseError)	{
                Toast.makeText(NuevaActivity.this, "Error descargando datos de usuario "+
                        databaseError.toException(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarKm(double x) {
        double kmx = x/1000;
        double kmRecorrido = kmDestino-kmx;
        double kmnuevo =  (kmOirginal + kmRecorrido);
        if(kmnuevo==kmOirginal)
            pos = 0;
        else
            kmOirginal = kmnuevo;
        if(kmOirginal<0)
            pos = 0;
        if(pos == 2){
            myRef.child("users/"+user.getUid()).addListenerForSingleValueEvent(new	ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)	 {
                    Usuario myUser =	dataSnapshot.getValue(Usuario.class);
                    myUser.setKm(kmOirginal);
                    updateUser(myUser);
                }
                @Override
                public void onCancelled(DatabaseError databaseError)	{
                }
            });
            pos = 0;
        }else{
            pos++;
        }
    }

    private void updateUser(Usuario us){
        myRef.child("users/"+mAuth.getCurrentUser().getUid())
                .setValue(us);
    }

    private void getMarcadoresNear(){
        myRef.child("marcadores/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    //  System.err.println("valor es key " + singleSnapshot.getKey());
                    //  System.out.println("valor es " + singleSnapshot.getValue());
                    if(!singleSnapshot.getKey().equals(user.getUid())) {
                        Marcador m = singleSnapshot.getValue(Marcador.class);
                        Date d = new Date();
                        d.setMonth(d.getMonth()+1);
                        d.setYear(d.getYear()+1900);
                        StringTokenizer tok = new StringTokenizer(m.getFecha(),"/");
                        int diaa = Integer.parseInt(tok.nextToken());
                        int mes = Integer.parseInt(tok.nextToken());
                        int año = Integer.parseInt(tok.nextToken());
                        Date segunda = new Date(año,mes,diaa);
                        if(d.compareTo(segunda)==1){
                            //System.out.println("valor es: ya expiró");
                            borrar.add(singleSnapshot.getKey());
                        }else{
                              LatLng pos = new LatLng(m.getLatitud(),m.getLongitud());
                            double dis = distance(location.getLatitude(),location.getLongitude(),
                                    pos.latitude,pos.longitude);
                            //System.err.println("valor es dis: "+dis);
                            if(dis<=1.0) {
                                //  System.err.println("valor es dentro: "+ubi.getNombre());
                                if (marcadores.containsKey(singleSnapshot.getKey())) {
                                    Marker mark = marcadores.get(singleSnapshot.getKey());
                                    mark.setPosition(pos);
                                    marcadores.put(singleSnapshot.getKey(), mark);
                                    marcadoresExistente.put(singleSnapshot.getKey(), true);
                                } else {
                                        Marker mark = mMap.addMarker(new MarkerOptions()
                                                .position(pos)
                                                .title(m.getNombre())
                                                .snippet(m.getDescripción())
                                                .icon(BitmapDescriptorFactory
                                                        .fromResource(R.drawable.castillo)));
                                        usuarios.put(singleSnapshot.getKey(), mark);
                                        usuariosExistente.put(singleSnapshot.getKey(), true);
                                }
                            }
                        }
                    }
                }

                Map<String,Boolean> copia = new HashMap<String, Boolean>(marcadoresExistente);
                for(String key:marcadoresExistente.keySet()){
                    if(!marcadoresExistente.get(key)){
                        //System.err.println("valor eliminado es "+usuarios.get(key).getTitle());
                        marcadores.get(key).remove();
                        marcadores.remove(key);
                        copia.remove(key);

                    }else{
                        copia.put(key,false);
                    }
                }
                marcadoresExistente = new HashMap<String, Boolean>(copia);
                //  System.err.println("valor es usuariosExistentes 3"+usuariosExistente.size());
                //  System.err.println("valor es usuarios 3"+usuarios.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(NuevaActivity.this, "ERROR cargando marcadores cercanos" + databaseError
                        .getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void borrarMarcadoresExpirados(){
        for(String k:borrar){
            myRef.child("marcadores/"+k).removeValue();
        }
        borrar.clear();
    }
}
