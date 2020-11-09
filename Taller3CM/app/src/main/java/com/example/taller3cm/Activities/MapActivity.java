package com.example.taller3cm.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taller3cm.Other.Localizacion;
import com.example.taller3cm.Other.Usuario;
import com.example.taller3cm.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private final static int LOCATION_PERMESSION_REQUEST = 1;
    private final static int REQUEST_CHECK_SETTINGS = 2;
    public static final String PATH_LOCATIONS = "locations/";
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private GoogleMap mMap;
    FirebaseDatabase database;
    DatabaseReference myRef;
    LatLng ubactual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //Marcador inicial
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = createLocationRequest();
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location my_location = locationResult.getLastLocation();
                if (my_location != null) {
                    mMap.clear();
                    ubactual = new LatLng(my_location.getLatitude(), my_location.getLongitude());
                    //mMap.addMarker(new MarkerOptions().position(ubactual).title("Ubicación actual: " + geoCoderSearchLatLng(ubactual)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(ubactual));
                }
            }
        };
    }

    //Leer locaciones
    public void loadLocations(){
        myRef = database.getReference(PATH_LOCATIONS);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapsot : dataSnapshot.getChildren()){
                    Localizacion loc = singleSnapsot.getValue(Localizacion.class);
                    String name = loc.getNombre();
                    Toast.makeText(MapActivity.this, "ENCONTRÉ" + name , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MapActivity.this, "error" , Toast.LENGTH_SHORT).show();
            }
        });
    }


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

    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(70000);
        locationRequest.setFastestInterval(70000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

}
