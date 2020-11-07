package com.example.taller3cm.Other;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.taller3cm.R;

public class UserAdapter extends CursorAdapter {

    private static final int CONTACT_ID_INDEX = 0;
    private static final int DISPLAY_NAME_INDEX = 1;

    public UserAdapter(Context context, Cursor c, int flags) {super(context, c, flags);}

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup){
        return LayoutInflater.from(context).inflate(R.layout.user_adapter, viewGroup, false);
    }

    /*
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView txtidContacto = (TextView) view.findViewById(R.id.idContacto);
        TextView txtnombreContacto = (TextView) view.findViewById(R.id.nombreContacto);
        int idnum = cursor.getInt(CONTACT_ID_INDEX);
        String nombre = cursor.getString(DISPLAY_NAME_INDEX);
        txtidContacto.setText(String.valueOf(idnum));
        txtnombreContacto.setText(nombre);
    }
    */
}
