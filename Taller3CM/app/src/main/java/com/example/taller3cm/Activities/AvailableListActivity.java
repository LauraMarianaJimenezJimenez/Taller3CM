package com.example.taller3cm.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;


import com.example.taller3cm.Other.UserAdapter;
import com.example.taller3cm.Other.Usuario;
import com.example.taller3cm.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AvailableListActivity extends AppCompatActivity {

    public static final String USERS = "users/";
    DatabaseReference myRef;
    FirebaseDatabase database;
    ListView listaDisponibles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_list);

        listaDisponibles = findViewById(R.id.lstDisponibles);


    }

    /*
    public void loadUsers() {
        myRef = database.getReference(USERS);
        myRef. addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Usuario myUser = singleSnapshot.getValue(Usuario.class);
                    Log.i(TAG, "Encontró usuario: " + myUser.getName());
                    String name = myUser.getName();
                    int age = myUser.getAge();
                    Toast.makeText(MapHomeActivity.this, name + ":" + age, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "error en la consulta", databaseError.toException());
            }
        });
    }*/
}