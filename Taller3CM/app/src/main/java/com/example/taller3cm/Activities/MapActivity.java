package com.example.taller3cm.Activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.example.taller3cm.Other.Localizacion;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final static int LOCATION_PERMESSION_REQUEST = 1;
    private final static int REQUEST_CHECK_SETTINGS = 2;
    public static final String PATH_LOCATIONS = "locationsArray/";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private GoogleMap mMap;
    Geocoder mGeocoder;
    LatLng ubactual;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private FirebaseAuth mAuth;
    ArrayList<Localizacion> locations = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mAuth = FirebaseAuth.getInstance();
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
                    ubactual = new LatLng(my_location.getLatitude(), my_location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(ubactual).title("Ubicación actual: " + geoCoderSearchLatLng(ubactual)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(ubactual));
                }
            }
        };

        //Search addresses
        mGeocoder = new Geocoder(getBaseContext());

        requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION,"Es necesario para el funcionamiento correcto de la APP.",LOCATION_PERMESSION_REQUEST);
        usePermition();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Markers
        loadLocations();
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuprincipal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menuLogout) {
            mAuth.signOut();
            Intent intent = new Intent(MapActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        if(id == R.id.menuDisponible) {

        }
        if(id == R.id.menuListaDisponibles) {
            Intent intent = new Intent(MapActivity.this, AvailableListActivity.class);
            startActivity(intent);
        }

        return true;
    }

    //Leer locaciones
    public void loadLocations(){
        myRef = database.getReference(PATH_LOCATIONS);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapsot : dataSnapshot.getChildren()){
                    Localizacion loc = singleSnapsot.getValue(Localizacion.class);
                    locations.add(loc);
                    Log.i("BANDERA", "Tiene " + loc.getName()+ " " + loc.getLatitude() + loc.getLongitude() );
                    Toast.makeText(MapActivity.this, "ENCONTRÉ", Toast.LENGTH_SHORT).show();
                }
                for(Localizacion loc : locations){
                    LatLng p = new LatLng(loc.getLatitude(), loc.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(p).title(loc.getName()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MapActivity.this, "error" , Toast.LENGTH_SHORT).show();
            }
        });

        Log.i("BANDERA size", "Tiene " + locations.size());
    }

    //Geocoder
    private String geoCoderSearchLatLng(LatLng latlng){
        String address = "";
        try{
            List<Address> res = mGeocoder.getFromLocation(latlng.latitude, latlng.longitude, 2);
            if(res != null && res.size() > 0){
                address = res.get(0).getAddressLine(0);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        return address;
    }

    //Permisos
    private void usePermition() {
        if(ContextCompat.checkSelfPermission(getBaseContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
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
                            resolvable.startResolutionForResult(MapActivity.this ,REQUEST_CHECK_SETTINGS);
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
