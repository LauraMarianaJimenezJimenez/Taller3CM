package com.example.taller3cm.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;


import com.example.taller3cm.Other.UserAdapter;
import com.example.taller3cm.R;

public class AvailableListActivity extends AppCompatActivity {

    String[] columnas;
    Cursor cursor;
    UserAdapter adaptador;
    ListView listaDisponibles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_list);
    }
}