package com.example.taller3cm.Activities;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taller3cm.Other.UserAdapter;
import com.example.taller3cm.Other.Usuario;
import com.example.taller3cm.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class UserMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private final static int LOCATION_PERMESSION_REQUEST = 1;
    private final static int REQUEST_CHECK_SETTINGS = 2;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private GoogleMap mMap;
    public static final String USERS = "users/";
    DatabaseReference myRef;
    FirebaseDatabase database;
    TextView txtDis;
    LatLng ubactual, ubSeguir;
    String distancia, nameSeguir;
    String idSeguir = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);
        txtDis = findViewById(R.id.txtDis);
        database = FirebaseDatabase.getInstance();

        //Marcador inicial
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest();
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location my_location = locationResult.getLastLocation();
                if(my_location!=null) {
                    mMap.clear();
                    ubactual = new LatLng(my_location.getLatitude(), my_location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(ubactual).title("Ubicación actual!"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubactual,15));
                }
            }
        };

        requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION,"Es necesario para el funcionamiento correcto de la APP.",LOCATION_PERMESSION_REQUEST);
        usePermition();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        loadPosition();
    }

    //Leer posicion del usuario a seguir
    public void loadPosition(){
        myRef = database.getReference(USERS);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot single: dataSnapshot.getChildren()){
                    Usuario user = single.getValue(Usuario.class);
                    user.setId(single.getKey());
                    idSeguir = getIntent().getStringExtra("idSeguir");
                    Log.i("LLEGO", "tengo este id " + idSeguir);
                   if(idSeguir.equals(user.getId())){
                        Log.i("ID seguimiento", user.getId());
                        ubSeguir = new LatLng(user.getLatitud(), user.getLongitud());
                        Log.i("LOAD LOCATION", "Tengo esta ubcacion a seguir: "+ ubSeguir);
                        nameSeguir = user.getNombre().concat(" ").concat(user.getApellido());
                        //Poner marker
                        mMap.clear();
                        if(ubactual != null){
                            mMap.addMarker(new MarkerOptions().position(ubactual).title("Ubicación actual!"));
                        }
                        mMap.addMarker(new MarkerOptions().position(ubSeguir).title(nameSeguir));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubSeguir,15));
                        Log.i("TRACKING MARKER", "Puse marker");

                        //Mostrar distancia
                        distancia = String.valueOf(calculateDistance(ubactual.latitude, ubSeguir.latitude, ubactual.longitude, ubSeguir.longitude));
                        txtDis.setText("Distancia a " + nameSeguir +": " + distancia + " Km");
                        Log.i("DISTANCIA", "mi distancia al otro usuario es: " + distancia);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("DB Realtime", "Error");
            }
        });

    }

    //Calcular distancia
    public static double calculateDistance (double lat1, double lat2, double lon1, double lon2) {
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return Math.round(distance*100.0)/100.0;
    }

    //Permisos
    private void usePermition() {
        if(ContextCompat.checkSelfPermission(getBaseContext(),Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            checkSettings();
        }
    }

    private void checkSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        SettingsClient client =  LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();
            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int statusCode = ((ApiException) e).getStatusCode();
                switch (statusCode){
                    case CommonStatusCodes.RESOLUTION_REQUIRED: {
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) e;
                            resolvable.startResolutionForResult(UserMapActivity.this ,REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException sendEx) {
                        }
                        break;
                    }
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:{
                        Toast.makeText(getBaseContext(),"No se pudo completar la operación.",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void startLocationUpdates() {
        if(ContextCompat.checkSelfPermission(getBaseContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,mLocationCallback,null);
        }
    }

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(70000);
        locationRequest.setFastestInterval(70000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void requestPermission(Activity context, String permiso, String justificacion, int idCode) {
        if(ContextCompat.checkSelfPermission(context,permiso) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(context, permiso)) {
                //Show an explanation to user asynchronously
            }
            //request permission
            ActivityCompat.requestPermissions(context,new String[]{permiso},idCode);
        }
    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
}
