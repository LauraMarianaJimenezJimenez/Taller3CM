package com.example.taller3cm.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;


import com.example.taller3cm.Other.UserAdapter;
import com.example.taller3cm.Other.Usuario;
import com.example.taller3cm.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class AvailableListActivity extends AppCompatActivity {

    public static final String USERS = "users/";
    DatabaseReference myRef;
    FirebaseDatabase database;
    ListView listaDisponibles;
    ValueEventListener usuario;
    private FirebaseAuth mAuth;
    ArrayList<Usuario> usuarios = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_list);

        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        listaDisponibles = findViewById(R.id.lstDisponibles);
        loadUsers();
    }



    public void loadUsers(){
        myRef = database.getReference(USERS);
        usuario = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuarios.clear();
                for(DataSnapshot single: dataSnapshot.getChildren()){
                    Usuario user = single.getValue(Usuario.class);
                    Log.i("user.getId", user.getId());
                    Log.i("mAuth.getUid", mAuth.getUid());
                    if(!(user.getId().equals(mAuth.getUid())))
                    {
                        if (user.isDisponible())
                        {
                            usuarios.add(user);
                        }
                    }

                }

                UserAdapter adapter = new UserAdapter(getBaseContext(), usuarios);
                listaDisponibles.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}